package com.practice.server.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description 电影标签
 * @Author fuchen
 * @Date 2019/12/11 12:49
 * Version 1.0
 */
public class Tag {

    @JsonIgnore
    private int _id;

    private int uid;

    private int mid;

    private double score;

    private long timestamp;

    public Tag(int uid, int mid, double score, long timestamp) {
        this.uid = uid;
        this.mid = mid;
        this.score = score;
        this.timestamp = timestamp;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
