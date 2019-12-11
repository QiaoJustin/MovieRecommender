package com.practice.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import com.practice.server.model.core.Rating;
import com.practice.server.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * @Description 电影评分服务
 * @Author fuchen
 * @Date 2019/12/11 14:23
 * Version 1.0
 */
@Service
public class RatingService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jedis jedis;

    private Document ratingToDocument(Rating rating){
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(rating));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Rating documentToRating(Document document) {
        try {
            Rating rating = objectMapper.readValue(JSON.serialize(document), Rating.class);
            return rating;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void rateToMovie(Rating rating) {
        MongoCollection<Document> ratingCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_RATING_COLLECTION);
        ratingCollection.insertOne(ratingToDocument(rating));
        // 更新 redis
        updateJedis(rating);
    }

    private void updateJedis(Rating rating){
        if (jedis.llen("uid:" + rating.getUid()) >= Constant.USER_RATING_QUEUE_SIZE) {
            jedis.rpop("uid:" + rating.getUid());
        }
        jedis.lpush("uid:" + rating.getUid(), rating.getMid() + ":" + rating.getScore());
    }

}
