package com.practice.server.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用户类
 * @Author fuchen
 * @Date 2019/12/7 18:37
 * Version 1.0
 */
public class User {

    @JsonIgnore
    private int _id;

    private int uid;

    private String username;

    private String password;

    /** 用户记录用户是第一次登录 */
    private boolean first;

    /** 用于保存电影的的类别 */
    private List<String> genres = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.uid = username.hashCode();
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

    public int getUid() {
        return uid;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }
}
