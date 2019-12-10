package com.practice.server.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description 电影类
 * @Author fuchen
 * @Date 2019/12/10 19:57
 * Version 1.0
 */
public class Movie {

    @JsonIgnore
    private int _id;

    private int mid;

    private String movieName;

    private String genres;

    private double score;

    public Movie(int mid, String movieName, String genres, double score) {
        this.mid = mid;
        this.movieName = movieName;
        this.genres = genres;
        this.score = score;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
