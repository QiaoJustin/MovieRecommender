package com.practice.server.model.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用户类
 * @Author fuchen
 * @Date 2019/12/7 18:37
 * Version 1.0
 */
public class User {

    private String username;

    private String password;

    private List<String> genres = new ArrayList<>();

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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
