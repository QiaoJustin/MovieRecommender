package com.practice.server.model.request;

/**
 * @Description 获取优质电影
 * @Author fuchen
 * @Date 2019/12/11 10:59
 * Version 1.0
 */
public class GetRateMoreMovieRequest {

    private int num;

    public GetRateMoreMovieRequest(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
