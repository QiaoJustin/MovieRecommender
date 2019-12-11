package com.practice.server.model.request;

/**
 * @Description 电影类别查找
 * @Author fuchen
 * @Date 2019/12/11 17:10
 * Version 1.0
 */
public class GetGenresMovieRequest {

    private String genres;

    private int num;

    public GetGenresMovieRequest(String genres, int num) {
        this.genres = genres;
        this.num = num;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
