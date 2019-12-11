package com.practice.server.model.request;

/**
 * @Description 获取最新电影
 * @Author fuchen
 * @Date 2019/12/11 10:59
 * Version 1.0
 */
public class GetNewMovieRequest {

    private int num;

    public GetNewMovieRequest(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
