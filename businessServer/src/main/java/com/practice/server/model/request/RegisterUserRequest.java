package com.practice.server.model.request;

/**
 * @Description 新建用户的请求封装
 * @Author fuchen
 * @Date 2019/12/10 11:43
 * Version 1.0
 */
public class RegisterUserRequest {

    private String username;

    private String password;

    public RegisterUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
