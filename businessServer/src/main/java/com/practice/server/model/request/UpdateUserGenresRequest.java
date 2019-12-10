package com.practice.server.model.request;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用户第一次登录所选择的电影类别
 * @Author fuchen
 * @Date 2019/12/10 13:59
 * Version 1.0
 */
public class UpdateUserGenresRequest {

    private String username;

    private List<String> genres = new ArrayList<>();

    public UpdateUserGenresRequest(String username, List<String> genres) {
        this.username = username;
        this.genres = genres;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
