package com.practice.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import com.practice.server.model.core.User;
import com.practice.server.model.request.LoginUserRequest;
import com.practice.server.model.request.RegisterUserRequest;
import com.practice.server.model.request.UpdateUserGenresRequest;
import com.practice.server.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description 对于用户处理业务服务的服务类
 * @Author fuchen
 * @Date 2019/12/10 11:26
 * Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;

    /**
     * 用于获取 User 表连接
     */
    private MongoCollection<Document> getUserCollection() {
        if (null == userCollection)
            this.userCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_USER_COLLECTION);
        return this.userCollection;
    }

    /**
     * 将 User 转换成 Document
     */
    private Document userToDocument(User user) {
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(user));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将 Document 转换成 User
     */
    private User documentToUser(Document document) {
        try {
            User user = objectMapper.readValue(JSON.serialize(document), User.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于提供注册用户的服务
     * @param request
     */
    public boolean registerUser(RegisterUserRequest request) {
        // 判断是否有相同的用户名已经注册
        if (getUserCollection().find(new Document("username", request.getUsername())).first() != null)
            return false;
        // 创建一个用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst(true);
        // 插入一个用户
        Document document = userToDocument(user);
        if (null == document)
            return false;
        getUserCollection().insertOne(document);
        return true;
    }

    /**
     * 用于提供用户登录的服务
     * @param request
     */
    public boolean loginUser(LoginUserRequest request) {
        // 需要找到这个用户
        Document document = getUserCollection().find(new Document("username", request.getUsername())).first();
        if (null == document)
            return false;
        // 密码验证
        User user = documentToUser(document);
        if (null == user)
            return false;
        return user.getPassword().compareTo(request.getPassword()) == 0;
    }

    /**
     * 用于更新用户第一次登录选择的电影类别
     * @param request
     */
    public void updateUserGenres(UpdateUserGenresRequest request) {
        getUserCollection().updateOne(new Document("username", request.getUsername()), new Document().append("$set", new Document("$genres", request.getGenres())));
        getUserCollection().updateOne(new Document("username", request.getUsername()), new Document().append("$set", new Document("$first", false)));
    }

    /**
     * 根据用户名查找 user
     * @param username
     */
    public User findUserByUsername(String username) {
        Document document = getUserCollection().find(new Document("username", username)).first();
        if (null == document || document.isEmpty())
            return null;
        return documentToUser(document);
    }

}
