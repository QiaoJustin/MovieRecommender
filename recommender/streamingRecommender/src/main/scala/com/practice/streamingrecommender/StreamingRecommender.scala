package com.practice.streamingrecommender

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingRecommender {

    // 入口方法
    def main(args: Array[String]): Unit = {

        val config = Map(
            "spark.cores" -> "local[*]",
            "mongo.uri" -> "mongodb://linux:27017/recommender",
            "mongo.db" -> "recommender",
            "kafka.topic" -> "recommender"
        )
        // 创建一个 SparkConf配置
        val sparkConf = new SparkConf().setAppName("StreamingRecommender").setMaster(config("spark.cores"))
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        val sc = spark.sparkContext
        val ssc = new StreamingContext(sc, Seconds(2))

        // 创建 Spark 的对象
        val kafkaPara = Map(
            "bootstrap.servers" -> "linux:9092",
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "group.id" -> "recommender",
            "auto.offset" -> "latest"
        )

        // 创建到 kafka 的连接
        val kafkaStream = KafkaUtils.createDirectStream[String, String](
            ssc,
            LocationStrategies.PreferConsistent,
            ConsumerStrategies.Subscribe[String, String](
                Array(config("kafka.topic")),
                kafkaPara)
        )

        // UID|MID|SCORE|TIMESTAMP
        // 产生评分流
        val ratingStream = kafkaStream.map{ case msg =>
            val attr = msg.value().split("\\|")
            (attr(0).toInt,attr(1).toInt,attr(2).toDouble, attr(3).toInt)
        }

        ratingStream.foreachRDD{
            rdd => rdd.map{
                case (uid,mid,score,timestamp) =>
                    println(">>>>>>>>>>>>>>")
            }.count()
        }

        // 启动 Streaming 程序
        ssc.start()
        ssc.awaitTermination()
    }

}
