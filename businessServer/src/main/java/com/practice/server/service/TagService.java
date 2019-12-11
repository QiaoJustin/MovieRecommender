package com.practice.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import com.practice.server.model.core.Tag;
import com.practice.server.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 电影标签服务类
 * @Author fuchen
 * @Date 2019/12/11 12:55
 * Version 1.0
 */
@Service
public class TagService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Tag documentToTag(Document document){
        try {
            Tag tag = objectMapper.readValue(JSON.serialize(document), Tag.class);
            return tag;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document tagToDocument(Tag tag) {
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(tag));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取电影标签
     * @param mid
     */
    public List<Tag> getMovieTags(int mid){
        MongoCollection<Document> tagCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_TAG_COLLECTION);
        FindIterable<Document> documents = tagCollection.find(Filters.eq("mid", mid));
        List<Tag> tags = new ArrayList<>();
        for (Document item : documents) {
            tags.add(documentToTag(item));
        }
        return tags;
    }

    /**
     * 给电影打标签
     * @param tag
     */
    public void addTagToMovie(Tag tag){
        MongoCollection<Document> tagCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_TAG_COLLECTION);
        tagCollection.insertOne(tagToDocument(tag));
    }

}
