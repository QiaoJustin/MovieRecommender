package com.practice.server.model.request;

/**
 * @Description 相似电影推荐矩阵
 * @Author fuchen
 * @Date 2019/12/11 13:55
 * Version 1.0
 */
public class GetItemCFMovieRequest {

    private int mid;

    private int num;

    public GetItemCFMovieRequest(int mid, int num) {
        this.mid = mid;
        this.num = num;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
