package com.practice.server.rest;

import com.practice.server.model.core.Movie;
import com.practice.server.model.core.User;
import com.practice.server.model.recom.Recommendation;
import com.practice.server.model.request.*;
import com.practice.server.service.MovieService;
import com.practice.server.service.RecommenderService;
import com.practice.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description 用于处理电影相关的功能
 * @Author fuchen
 * @Date 2019/12/7 17:13
 * Version 1.0
 */
@Controller
@RequestMapping("/rest/movies")
public class MoiveRestApi {

    @Autowired
    private RecommenderService recommenderService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    // ************ 首页功能 ***************

    /** 提供获取实时推荐信息的接口 【混合推荐】 需要考虑冷启动问题
     *  访问 url：/rest/movies/stream?username=abn&num=100
     *  返回：{success: true, movies:[]}
     * @param username
     * @param num
     * @param model
     */
    @RequestMapping(path = "/stream", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRealRecommendations(@RequestParam("username") String username, @RequestParam("num") int num, Model model) {
        User user = userService.findUserByUsername(username);
        List<Recommendation> recommendations = recommenderService.getStreamRecsMovies(new GetStreamRecsRequest(user.getUid(), num));
        if (recommendations.size() == 0) {
            Random random = new Random();
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(random.nextInt(user.getGenres().size())), num));
        }
        List<Integer> ids = new ArrayList<>();
        for (Recommendation recom : recommendations) {
            ids.add(recom.getMid());
        }
        List<Movie> result = movieService.getMoviesByMids(ids);
        model.addAttribute("success", true);
        model.addAttribute("movies",result);
        return model;
    }

    /**
     * 提供获取离线推荐信息的接口
     * @param username
     * @param model
     */
    @RequestMapping(path = "/offline", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfflineRecommendations(@RequestParam("username")String username, @RequestParam("num") int num, Model model) {
        User user = userService.findUserByUsername(username);
        List<Recommendation> recommendations = recommenderService.getUserCFMovies(new GetUserCFRequest(user.getUid(), num));
        if (recommendations.size() == 0) {
            Random random = new Random();
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(random.nextInt(user.getGenres().size())), num));
        }
        List<Integer> ids = new ArrayList<>();
        for (Recommendation recom : recommendations) {
            ids.add(recom.getMid());
        }
        List<Movie> result = movieService.getMoviesByMids(ids);
        model.addAttribute("success", true);
        model.addAttribute("movies",result);
        return model;
    }

    /**
     * 提供获取热门推荐信息的接口
     * @param num
     * @param model
     */
    @RequestMapping(path = "/hot", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getHotRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies",recommenderService.getHotRecommendations(new GetHotRecommendationRequest(num)));
        return model;
    }

    /**
     * 提供获取优质电影的信息的接口
     * @param model
     */
    @RequestMapping(path = "/rate", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRateMoreRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies",recommenderService.getRateMoreMovies(new GetRateMoreMovieRequest(num)));
        return model;
    }

    // 获取最新电影的信息的接口
    public Model getNewRecommendations(Model model) {
        return null;
    }


    // ************ 模糊检索 ***************

    // 提供基于名称或者描述的模糊检索功能
    public Model getFuzzySearchMovies(String query, Model model) {
        return null;
    }

    // ************ 电影的详细页面 ***************

    // 获取单部电影信息
    public Model getMovieInfo(int mid, Model model) {
        return null;
    }

    // 需要提供给电影打标签的功能
    public Model addTagToMovie(int mid, String tagName, Model model) {
        return null;
    }

    // 获取单部电影的所有标签信息
    public Model getMovieTags(int mid, Model model) {
        return null;
    }

    // 需要能够获取电影相似的电影推荐
    public Model getSimMoviesRecommendation(int mid, Model model){
        return null;
    }

    // 需要提供给电影打分的功能
    public Model rateMovies(int mid, Double score, Model model) {
        return null;
    }

    // ************ 电影的类别页面 ***************

    // 需要提供电影类别的查找
    public Model getGenresMovies(String genres, Model model) {
        return null;
    }

    // ************ 用户空间页面 ***************

    // 需要提供用户的所有电影评分记录
    public Model getUserRatings(String username, Model model){
        return null;
    }

    // 需要能够获取图标数据
    public Model getUserChart(String username, Model model) {
        return null;
    }

}