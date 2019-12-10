package com.practice.server.model.request;

/**
 * @Description 混合推荐
 * @Author fuchen
 * @Date 2019/12/10 14:45
 * Version 1.0
 */
public class GetHybridRecommendationRequest {

    // 实时推荐结果的占比
    private double streamShare;

    // 基于 ALS 的离线推荐结果的占比
    private double alsShare;

    // 基于 ES 的内容结果的占比
    private double contentShare;

    private int uid;

    private int num;

    public GetHybridRecommendationRequest(double streamShare, double alsShare, double contentShare, int uid, int num) {
        this.streamShare = streamShare;
        this.alsShare = alsShare;
        this.contentShare = contentShare;
        this.uid = uid;
        this.num = num;
    }

    public double getStreamShare() {
        return streamShare;
    }

    public void setStreamShare(double streamShare) {
        this.streamShare = streamShare;
    }

    public double getAlsShare() {
        return alsShare;
    }

    public void setAlsShare(double alsShare) {
        this.alsShare = alsShare;
    }

    public double getContentShare() {
        return contentShare;
    }

    public void setContentShare(double contentShare) {
        this.contentShare = contentShare;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
