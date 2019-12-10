package com.practice.server.model.request;

/**
 * @Description 实时推荐请求
 * @Author fuchen
 * @Date 2019/12/10 14:52
 * Version 1.0
 */
public class GetStreamRecsRequest {

    private int uid;

    private int num;

    public GetStreamRecsRequest(int uid, int num) {
        this.uid = uid;
        this.num = num;
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
