package com.practice.server.model.request;

/**
 * @Description 获取当前最热的电影
 * @Author fuchen
 * @Date 2019/12/11 10:23
 * Version 1.0
 */
public class GetHotRecommendationRequest {

    private int sum;

    public GetHotRecommendationRequest(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
