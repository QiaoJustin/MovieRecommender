package com.practice.server.service;

import com.mongodb.MongoClient;
import com.practice.server.model.core.User;
import com.practice.server.model.request.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean registerUser(RegisterUserRequest request) {
        // 创建一个用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst(true);

        // 插入一个用户

        return false;
    }

}
