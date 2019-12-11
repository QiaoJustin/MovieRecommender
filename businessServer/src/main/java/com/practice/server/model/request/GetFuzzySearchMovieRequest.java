package com.practice.server.model.request;

/**
 * @Description 根据电影名称或者描述进行模糊检索
 * @Author fuchen
 * @Date 2019/12/11 11:34
 * Version 1.0
 */
public class GetFuzzySearchMovieRequest {

    private String query;

    private int num;

    public GetFuzzySearchMovieRequest(String query, int num) {
        this.query = query;
        this.num = num;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
