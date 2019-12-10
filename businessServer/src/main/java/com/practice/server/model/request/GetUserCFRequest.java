package com.practice.server.model.request;

/**
 * @Description 获取 ALS 算法下的用户推荐矩阵
 * @Author fuchen
 * @Date 2019/12/10 15:50
 * Version 1.0
 */
public class GetUserCFRequest {

    private int uid;

    private int sum;

    public GetUserCFRequest(int uid, int sum) {
        this.uid = uid;
        this.sum = sum;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
