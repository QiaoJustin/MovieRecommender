package com.practice.server.model.recom;

/**
 * @Description 推荐的单个电影的封装
 * @Author fuchen
 * @Date 2019/12/10 14:42
 * Version 1.0
 */
public class Recommendation {

    private int mid;

    private Double score;

    public Recommendation(int mid, Double score) {
        this.mid = mid;
        this.score = score;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
