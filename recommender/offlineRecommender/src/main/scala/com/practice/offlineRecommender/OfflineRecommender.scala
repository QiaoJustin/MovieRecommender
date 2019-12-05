package com.practice.offlineRecommender

import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession

/**
  * 1、Movies 数据集：通过,分割
  * 9,                    电影的ID
  * Sudden Death (1995),  电影名称（年份）
  * Action                电影类型
  */
case class Movie(val mid: Int, val name: String, val genres: String)

/**
  * 2、Ratings 数据集，用户对于电影的评分数据集，用,分割
  * 1,                    用户的ID
  * 307,                  电影的ID
  * 3.5,                  电影评分
  * 1256677221            用户对于电影评分的时间
  */
case class MovieRating(val uid: Int, val mid: Int, val score: Double, val timestamp: String)

/**
  * MongoDB 的连接配置
  *
  * @param uri MongoDB 的连接
  * @param db  MongoDB 要操作的数据库
  */
case class MongoConfig(val uri: String, val db: String)

/**
  * 推荐
  * @param rid
  * @param r
  */
case class Recommendation(rid:Int, r:Double)

/**
  * 用户的推荐
  * @param uid
  * @param recs
  */
case class UserRecs(uid:Int,recs:Seq[Recommendation])

/**
  * 电影的相似度
  * @param uid
  * @param recs
  */
case class MovieRecs(uid:Int, recs:Seq[Recommendation])

object OfflineRecommender {

    val MONGODB_MOVIE_COLLECTION = "Movie"
    val MONGODB_RATING_COLLECTION = "Rating"
    val USER_MAX_RECOMMENDATION = 20
    val USER_RECS = "UserRecs"

    def main(args: Array[String]): Unit = {
        val config = Map(
            "spark.cores" -> "local[*]",
            "mongo.uri" -> "mongodb://linux:27017/recommender",
            "mongo.db" -> "recommender"
        )
        // 创建一个 SparkConf 配置
        val sparkConf = new SparkConf()
          .setAppName("OfflineRecommender")
          .setMaster(config("spark.cores"))
          .set("spark.executor.memory", "6G")
          .set("spark.driver.memory", "3G")

        // 基于 SparkSession 创建一个 Sparksession
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()

        // 创建一个 MongoDBConfig
        val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.uri"))

        import spark.implicits._
        // 读取 MongoDB 的业务数据
        val ratingRDD = spark
          .read
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_RATING_COLLECTION)
          .format("com.mongodb.spark.sql")
          .load()
          .as[MovieRating]
          .rdd
          .map(rating => (rating.uid, rating.mid, rating.score))

        // 用户的数据集
        val userRDD = ratingRDD.map(_._1).distinct()

        // 电影的数据集
        val movieRDD = spark
          .read
          .option("uri",mongoConfig.uri)
          .option("collection", MONGODB_MOVIE_COLLECTION)
          .format("com.mongodb.spark.sql")
          .load()
          .as[Movie]
          .rdd
          .map(_.mid)

        // 创建训练数据集
        val trainData = ratingRDD.map(x => Rating(x._1,x._2,x._3))

        val (rank, iterations,lambda) = (50, 10, 0.01)

        // 训练 ALS 模型
        val model = ALS.train(trainData,rank,iterations,lambda)

        // 计算用户推荐矩阵
        // 需要构造 usersproducts RDD[Int,Int]
        val userMovies = userRDD.cartesian(movieRDD)

        val preRatings = model.predict(userMovies)

        val userRecs = preRatings.map(rating => (rating.user, (rating.product, rating.rating))).groupByKey().map {
            case (uid, recs) => UserRecs(
                uid, recs.toList.sortWith(_._2 > _._2)
                  .take(USER_MAX_RECOMMENDATION)
                  .map(x => Recommendation(x._1, x._2))
            )
        }.toDF()

        userRecs
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", USER_RECS)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 计算电影相似度矩阵

        // 关闭 spark
        spark.close()
    }

}
