package com.practice.server.model.request;

/**
 * @Description 获取电影类别的 TOP 电影
 * @Author fuchen
 * @Date 2019/12/11 9:27
 * Version 1.0
 */
public class GetGenresTopMoviesRequest {

    private String genres;

    private int num;

    public GetGenresTopMoviesRequest(String genres, int num) {
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
