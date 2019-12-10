package com.practice.server.model.request;

/**
 * @Description 用户的登录请求封装
 * @Author fuchen
 * @Date 2019/12/10 13:48
 * Version 1.0
 */
public class LoginUserRequest {

    private String username;

    private String password;

    public LoginUserRequest(String username, String password) {
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
