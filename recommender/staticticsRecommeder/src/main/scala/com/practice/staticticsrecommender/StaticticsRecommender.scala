package com.practice.staticticsrecommender

import java.text.SimpleDateFormat
import java.util.Date
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/** movieId,title,genres
  * 1、Movies 数据集：通过,分割
  * 9,                    电影的ID
  * Sudden Death (1995),  电影名称（年份）
  * Action                电影类型
  */
case class Movie(val mid: Int, val name: String, val genres: String)

/** userId,movieId,rating,timestamp
  * 2、Ratings 数据集，用户对于电影的评分数据集，用,分割
  * 1,                    用户的ID
  * 307,                  电影的ID
  * 3.5,                  电影评分
  * 1256677221            用户对于电影评分的时间
  */
case class Rating(val uid: Int, val mid: Int, val score: Double, val timestamp: Int)

/**
  * MongoDB 的连接配置
  *
  * @param uri MongoDB 的连接
  * @param db  MongoDB 要操作的数据库
  */
case class MongoConfig(val uri: String, val db: String)

/**
  * 推荐对象
  * @param rid 推荐的Movie的mid
  * @param r Moive 的评分
  */
case class Recommendation(rid:Int, r:Double)

/**
  * 电影类别的推荐
  * @param genres   电影的类别
  * @param recs     top10的电影的集合
  */
case class GenresRecommendation(genres:String, recs:Seq[Recommendation])

object StaticticsRecommender {

    val MONGODB_MOVIE_COLLECTION = "Movie"
    val MONGODB_RATING_COLLECTION = "Rating"

    // 统计表的名称
    val RATE_MORE_MOVIES = "RateMoreMovies"
    val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
    val AVERAGE_MOVIES = "AverageMovies"
    val GENRES_TOP_MOVIES = "GenresTopMovies"

    def main(args: Array[String]): Unit = {

        val config = Map(
            "spark.cores" -> "local[*]",
            "mongo.uri" -> "mongodb://linux:27017/recommender",
            "mongo.db" -> "recommender"
        )

        // 需要创建一个 SparkConf 配置
        val sparkConf = new SparkConf().setAppName("StaticticsRecommender").setMaster(config.get("spark.cores").get)
        // 创建 SparkSession
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

        // 导入隐式
        import spark.implicits._
        // 将movies,ratins 数据加载进来
        val movieDF = spark
          .read
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_MOVIE_COLLECTION)
          .format("com.mongodb.spark.sql")
          .load()
          .as[Movie]
          .toDF()

        val ratingDF = spark
          .read
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_RATING_COLLECTION)
          .format("com.mongodb.spark.sql")
          .load()
          .as[Rating]
          .toDF()

        // 创建一张名为 ratings 的表
        ratingDF.createTempView("ratings")

        // 1、统计所有历史数据中每部电影的评分数
        // 数据结构：mid,count
        val rateMoreMoviesDF = spark.sql("select mid, count(mid) as count from ratings group by mid")
        rateMoreMoviesDF
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", RATE_MORE_MOVIES)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 2、统计以月为单位的每部电影评分数
        // 数据结构：mid,count,timstamp
        // 创建一个时间格式化
        val simpleDateFormat = new SimpleDateFormat("yyyyMM")
        // 注册一个UDF函数，用户将 timestamp 转换成年月
        spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)
        // 将原来 ratings 数据集中的时间转换为年月的格式
        val ratingOfYearhMouth = spark.sql("select mid, score, changeDate(timestamp) as yearhmouth from ratings")
        // 将新的数据集注册为一张表
        ratingOfYearhMouth.createOrReplaceTempView("ratingOfMouth")
        val rateMovieRecentlyMovies = spark.sql("select mid, count(mid) as count, yearhmouth from ratingOfMouth group by yearhmouth, mid")
        rateMovieRecentlyMovies
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", RATE_MORE_RECENTLY_MOVIES)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 3、统计每部电影的平均得分
        val averageMovieDF = spark.sql("select mid, avg(score) as avg from ratings group by mid")
        averageMovieDF
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", AVERAGE_MOVIES)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 4、计算不同类别电影的 top10
        // 不需要 left join，应为只需要有评分的电影
        val movieWithScore = movieDF.join(averageMovieDF, Seq("mid", "mid"))

        // 所有的电影类别
        val genres = List(
            "Action",
            "Adventure",
            "Animation",
            "Comedy",
            "Crime",
            "Documentary",
            "Drama",
            "Family",
            "Romance",
            "Sci-Fi",
            "Thriller",
            "War",
            "Western",
            "Fantasy",
            "Mystery",
            "Children",
            "Horror")
        // 将电影类别转换成 RDD
        val genresRDD = spark.sparkContext.makeRDD(genres)
        // 过滤掉电影的类别不匹配的电影
        val genrenTopMovies = genresRDD.cartesian(movieWithScore.rdd)   // 将电影类别和电影数据做笛卡儿积
          .filter{
              case (genres, row) => row.getAs[String]("genres")
                .toLowerCase.contains(genres.toLowerCase)
          }
          .map{
              // 将整个数据集的数据量减小，生成的RDD[String,Iter[mid,avg]]
              case (genres,row) => {
                  (genres,(row.getAs[Int]("mid"),row.getAs[Double]("avg")))
              }
          }.groupByKey()    // 将genres数据集中的相同的聚集
          .map{
            // 通过评分的大小进行数据的排序，然后将数据映射为对象
              case (genres, item) => GenresRecommendation(genres, item.toList.sortWith(_._2 > _._2).take(10).map(item => Recommendation(item._1, item._2)))
            }
          .toDF()

        // 输出数据到 MongoDB
        genrenTopMovies
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", GENRES_TOP_MOVIES)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 关闭 Spark
        spark.close()
    }
}