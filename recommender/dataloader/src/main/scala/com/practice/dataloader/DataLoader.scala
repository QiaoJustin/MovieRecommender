package com.practice.dataloader

import java.net.InetAddress

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

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

/** userId,movieId,tag,timestamp
  * 3、Tags 数据集，用户对于电影的标答数据集，用,分割
  * 14,                   用户的ID
  * 110,                  电影的ID
  * epic,                 标签的具体内容
  * 1443148538            用户对于电影打标签的时间
  */
case class Tag(val uid: Int, val mid: Int, val tag: String, val timestamp: Int)

/**
  * MongoDB 的连接配置
  *
  * @param uri MongoDB 的连接
  * @param db  MongoDB 要操作的数据库
  */
case class MongoConfig(val uri: String, val db: String)

/**
  * ElasticSearch 的连接配置
  *
  * @param httpHosts      http 的连接列表，以,分割
  * @param transportHosts transport 的连接列表，以,分割
  * @param index          需要操作的索引
  * @param clustername    ES 集群的名称
  */
case class ESConfig(val httpHosts: String, val transportHosts: String, val index: String, val clustername: String)

// 数据的加载服务
object DataLoader {
    val MOVIE_DATA_PATH = "D:\\all-workspaces\\IdeaProjects\\MovieRecommedSystem\\recommender\\dataloader\\src\\main\\resources\\movies.csv"
    val RATING_DATA_PATH = "D:\\all-workspaces\\IdeaProjects\\MovieRecommedSystem\\recommender\\dataloader\\src\\main\\resources\\ratings.csv"
    val TAG_DATA_PATH = "D:\\all-workspaces\\IdeaProjects\\MovieRecommedSystem\\recommender\\dataloader\\src\\main\\resources\\tags.csv"

    val MONGODB_MOVIE_COLLECTION = "Movie"
    val MONGODB_RATING_COLLECTION = "Rating"
    val MONGODB_TAG_COLLECTION = "Tag"

    val ES_MOVIE_INDEX = "Movie"

    // 程序的入口
    def main(args: Array[String]): Unit = {
        val config = Map(
            "spark.cores" -> "local[*]",
            "mongo.uri" -> "mongodb://linux:27017/recommender",
            "mongo.db" -> "recommender",
            "es.httpHosts" -> "linux:9200",
            "es.transportHosts" -> "linux:9300",
            "es.index" -> "recommender",
            "es.cluster.name" -> "es-cluster"
        )

        // 需要创建一个 SparkConf 配置
        val sparkConf = new SparkConf().setAppName("DataLoader").setMaster(config.get("spark.cores").get)

        // 创建一个 SparkSession
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        import spark.implicits._

        // 将 moives、ratins、tags 数据集加载进来
        val movieRDD = spark.sparkContext.textFile(MOVIE_DATA_PATH)
        // 将 movieRDD 转换成 DataFrame
        val movieDF = movieRDD.map(item => {
            val attr = item.split(",")
            Movie(attr(0).toInt, attr(1).trim, attr(2).trim)
        }).toDF()

        val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
        // 将 ratingRDD 转换成 DataFrame
        val ratingDF = ratingRDD.map(item => {
            val attr = item.split(",")
            Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
        }).toDF()

        val tagRDD = spark.sparkContext.textFile(TAG_DATA_PATH)
        // 将 tagRDD 转换成 DataFrame
        val tagDF = tagRDD.map(item => {
            val attr = item.split(",")
            Tag(attr(0).toInt, attr(1).toInt, attr(2).trim, attr(3).toInt)
        }).toDF()

        implicit val mongoConfig = MongoConfig(config.get("mongo.uri").get, config.get("mongo.db").get)

        // 需要将数据加载 MongoDB 中
        storeDataInMongoDB(movieDF,ratingDF,ratingDF)

        // 首先将 tags 数据集进行处理，处理后的形式为：mid：tag1|tag2|tag3|......
        import org.apache.spark.sql.functions._

        // newTag 的数据格式为：mid,tag1|tag2|tag3|tag4|...
/*        val newTag = tagDF.groupBy($"mid").agg(concat_ws("|", collect_set($"tag")).as("tags")).select("mid", "tags")

        // 需要将处理后的 tag 数据和 movie 数据融合，产生新的 movie 数据
        val movieWithTag = movieDF.join(newTag, Seq("mid", "mid"), "left")

        // 声明了一个 ES 配置的一些隐式参数
        implicit val esConfig = ESConfig(
            config.get("es.httpHosts").get,
            config.get("es.transportHosts").get,
            config.get("es.index").get,
            config.get("es.cluster.name").get)*/

        // 需要新的 movie 数据加载 ES 中
        // storeDataInES(movieWithTag)

        // 关闭 Spark
        spark.stop()
    }

    // 将数据保存到 MongoDB 中的方法
    def storeDataInMongoDB(movieDF: DataFrame, ratingDF: DataFrame, tagDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {
        // 新建一个到 MongoDB 的连接
        val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

        // 如果 MongoDB 中有对应的数据库，那么应该删除
        mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).dropCollection()
        mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).dropCollection()
        mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).dropCollection()

        // 将当前数据 movies、ratings、tags 写入 MongoDB
        movieDF
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_MOVIE_COLLECTION)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        ratingDF
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_RATING_COLLECTION)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        tagDF
          .write
          .option("uri", mongoConfig.uri)
          .option("collection", MONGODB_TAG_COLLECTION)
          .mode("overwrite")
          .format("com.mongodb.spark.sql")
          .save()

        // 对数据表建索引
        mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
        mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
        mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
        mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
        mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("mid" -> 1))

        // 关闭 MongoDB 的连接
        mongoClient.close()
    }

    // 将数据保存到 ES 中的方法
    def storeDataInES(movieDF: DataFrame)(implicit eSConfig: ESConfig): Unit = {

        // 新建一个配置
        val settings: Settings = Settings.builder().put("cluster.name", eSConfig.clustername).build()

        // 新建一个 ES 客户端
        val esCclient = new PreBuiltTransportClient(settings)

        // 需要将 TransportHosts 添加到 esCclient 中
        val REGEX_HOST_PORT = "(.+):(\\d+)".r
        eSConfig.transportHosts.split(",").foreach {
            case REGEX_HOST_PORT(host: String, port: String) => {
                esCclient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port.toInt))
            }
        }

        // 需要清除掉 ES 中遗留的数据
        if (esCclient.admin().indices().exists(new IndicesExistsRequest(eSConfig.index)).actionGet() isExists) {
            esCclient.admin().indices().delete(new DeleteIndexRequest(eSConfig.index))
        }
        esCclient.admin().indices().create(new CreateIndexRequest(eSConfig.index))

        // 将数据写入 ES 中
        movieDF
          .write
          .option("es.nodes", eSConfig.httpHosts)
          .option("es.http.timeout", "100m")
          .option("es.mapping.id", "mid")
          .format("org.elasticsearch.spark.sql")
          .mode("overwrite")
          .save(eSConfig.index + "/" + ES_MOVIE_INDEX)
    }
}