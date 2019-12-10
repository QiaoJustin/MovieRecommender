package com.practice.server.service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.practice.server.model.recom.Recommendation;
import com.practice.server.model.request.GetContentBasedRecommendationRequest;
import com.practice.server.model.request.GetHybridRecommendationRequest;
import com.practice.server.model.request.GetStreamRecsRequest;
import com.practice.server.model.request.GetUserCFRequest;
import com.practice.server.utils.Constant;
import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 用于推荐服务
 * @Author fuchen
 * @Date 2019/12/10 14:41
 * Version 1.0
 */
@Service
public class RecommenderService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private TransportClient esClient;

    /**
     * 获取混合推荐结果【用在当前电影的相似中】
     * @param request
     * @return
     */
    public List<Recommendation> getHybridRecommendations(GetHybridRecommendationRequest request) {

        // 获得实时推荐结果
        // List<Recommendation> streamRecs = getStreamRecsMovies(new GetStreamRecsRequest(request.getUid(), request.getNum()));

        // 获得 ALS 离线推荐结果
        // List<Recommendation> userRecs = getUserCFMovies(new GetUserCFRequest(request.getUid(), request.getNum()));

        // 获得基于内容推荐结果


        // 返回结果

        return null;
    }

    /**
     * 获取基于内容的推荐结果
     *
     * @param request
     * @return
     */
    public List<Recommendation> getContentBasedRecommendation(GetContentBasedRecommendationRequest request) {

        MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(
                new MoreLikeThisQueryBuilder.Item[]{
                        new MoreLikeThisQueryBuilder.Item(Constant.ES_INDEX, Constant.ES_TYPE, String.valueOf(request.getMid()))
                });
        SearchResponse response = esClient.prepareSearch(Constant.ES_INDEX).setQuery(queryBuilder).setSize(request.getSum()).execute().actionGet();
        return parseESResponse(response);
    }

    /**
     * 用于解析 ElasticSearch 的查询响应
     * @param response
     * @return
     */
    private List<Recommendation> parseESResponse(SearchResponse response) {
        List<Recommendation> recommendations = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> hitContents = hit.getSourceAsMap();
            recommendations.add(new Recommendation((int)hitContents.get("mid"), 0D));
        }
        return recommendations;
    }

    /**
     * 用于获取 ALS 算法中用户推荐矩阵
     *
     * @param request
     * @return
     */
    public List<Recommendation> getUserCFMovies(GetUserCFRequest request) {
        MongoCollection<Document> userCFCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_USER_COLLECTION);
        Document document = userCFCollection.find(new Document("uid", request.getUid())).first();
        return parseDocument(document, request.getSum());
    }

    /**
     * 用于解析 Document
     * @param document
     * @param sum
     * @return
     */
    private List<Recommendation> parseDocument(Document document, int sum) {
        List<Recommendation> result = new ArrayList<>();
        if (null == document || document.isEmpty())
            return result;
        List<Document> documents = document.get("recs", ArrayList.class);
        for (Document item : documents) {
            result.add(new Recommendation(item.getInteger("rid"), item.getDouble("r")));
        }
        return result.subList(0, result.size() > sum ? sum : result.size());
    }

    /**
     * 获取当前用户的实时推荐
     *
     * @param request
     */
    public List<Recommendation> getStreamRecsMovies(GetStreamRecsRequest request) {
        MongoCollection<Document> streamRecsCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_STREAM_RECS_COLLECTION);
        Document document = streamRecsCollection.find(new Document("uid", request.getUid())).first();

        List<Recommendation> result = new ArrayList<>();
        if (null == document || document.isEmpty())
            return result;

        for (String item : document.getString("recs").split("\\|")) {
            String[] para = item.split(":");
            result.add(new Recommendation(Integer.parseInt(para[0]), Double.parseDouble(para[1])));
        }
        return result.subList(0, result.size() > request.getNum() ? request.getNum() : result.size());
    }

}
