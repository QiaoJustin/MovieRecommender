package com.practice.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import com.practice.server.model.core.Movie;
import com.practice.server.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用于电影服务
 * @Author fuchen
 * @Date 2019/12/10 20:16
 * Version 1.0
 */
@Service
public class MovieService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> movieCollection;

    private MongoCollection<Document> getMovieCollection() {
        if (null == movieCollection)
            this.movieCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_MOVIE_COLLECTION);
        return this.movieCollection;
    }

    private Movie documentToMovie(Document document) {
        try {
            Movie movie = objectMapper.readValue(JSON.serialize(document), Movie.class);
            Document score = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_AVERAGE_MOVIES).find(Filters.eq("mid", movie.getMid())).first();
            if (null == score || score.isEmpty())
                movie.setScore(0D);
            else
                movie.setScore(score.getDouble("avg"));
            return movie;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document movieToDocument(Movie movie) {
        try {
            return Document.parse(objectMapper.writeValueAsString(movie));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Movie> getMoviesByMids(List<Integer> ids) {
        List<Movie> result = new ArrayList<>();
        FindIterable<Document> documents = getMovieCollection().find(Filters.in("mid", ids));
        for (Document item : documents) {
            result.add(documentToMovie(item));
        }
        return result;
    }

    /**
     * 单部电影信息
     *
     * @param mid
     */
    public Movie findMovieInfo(int mid) {
        Document movieDocument = getMovieCollection().find(new Document("mid", mid)).first();
        if (null == movieDocument || movieDocument.isEmpty())
            return null;
        return documentToMovie(movieDocument);
    }

}
