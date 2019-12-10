package com.practice.server.utils;

/**
 * @Description 定义整个业务系统的常量
 * @Author fuchen
 * @Date 2019/12/10 8:45
 * Version 1.0
 */
public class Constant {

    //************** MongoDB 中的表名 **************

    /** MongoDB 数据库名 */
    public static final String MONGO_DATABASE = "recommender";

    /** 电影表名 */
    public static final String MONGO_MOVIE_COLLECTION = "Movie";

    /** 电影评分表名 */
    public static final String MONGO_RATING_COLLECTION = "Rating";

    /** 电影标签的表名 */
    public static final String MONGO_TAG_COLLECTION = "Tag";

    /** 用户表 */
    public static final String MONGO_USER_COLLECTION = "User";

    /** 电影的平均评分表 */
    public static final String MONGO_AVERAGE_MOVIES = "AverageMovies";

    /** 电影类别 Top 10 表 */
    public static final String MONGO_GENRES_TOP_MOVIES = "GenresTopMovies";

    /** 优质电影表 */
    public static final String MONGO_RATE_MORE_MOVIES = "RateMoreMovies";

    /** 最热电影表 */
    public static final String MONGO_RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies";

    /** 用户的推荐矩阵 */
    public static final String MONGO_USER_RECS_COLLECTION = "UserRecs";

    /** 电影的相似度矩阵 */
    public static final String MONGO_MOVIE_RECS_COLLECTION = "MovieRecs";

    /** 实时推荐电影表 */
    public static final String MONGO_STREAM_RECS_COLLECTION = "StreamRecs";


    //************** ES 中的表名 **************

    /** 使用的 Index */
    public static final String ES_INDEX = "recommender";

    /** 使用的 Type */
    public static final String ES_TYPE = "Moive";


    //************** Redis **************
    public static final int USER_RATING_QUEUE_SIZE = 20;


    //************** Log **************
    public static final String USER_RATING_LOG_PREFIX = "USER_RATING_LOG_PREFIX:";

}
