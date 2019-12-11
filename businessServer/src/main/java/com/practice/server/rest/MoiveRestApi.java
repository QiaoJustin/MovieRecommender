package com.practice.server.rest;

import com.practice.server.model.core.Movie;
import com.practice.server.model.core.Rating;
import com.practice.server.model.core.Tag;
import com.practice.server.model.core.User;
import com.practice.server.model.recom.Recommendation;
import com.practice.server.model.request.*;
import com.practice.server.service.*;
import com.practice.server.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private TagService tagService;

    @Autowired
    private RatingService ratingService;

    private Logger logger = LoggerFactory.getLogger(MoiveRestApi.class);


    // ************ 首页功能 ***************

    /**
     * 提供获取实时推荐信息的接口 【混合推荐】 需要考虑冷启动问题
     * 访问 url：/rest/movies/stream?username=abn&num=100
     * 返回：{success: true, movies:[]}
     *
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
        model.addAttribute("movies", result);
        return model;
    }

    /**
     * 提供获取离线推荐信息的接口
     *
     * @param username
     * @param model
     */
    @RequestMapping(path = "/offline", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfflineRecommendations(@RequestParam("username") String username, @RequestParam("num") int num, Model model) {
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
        model.addAttribute("movies", result);
        return model;
    }

    /**
     * 提供获取热门推荐信息的接口
     *
     * @param num
     * @param model
     */
    @RequestMapping(path = "/hot", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getHotRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getHotRecommendations(new GetHotRecommendationRequest(num)));
        return model;
    }

    /**
     * 提供获取优质电影的信息的接口
     *
     * @param model
     */
    @RequestMapping(path = "/rate", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRateMoreRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getRateMoreMovies(new GetRateMoreMovieRequest(num)));
        return model;
    }

    /**
     * 获取最新电影的信息的接口【该方法还需要进一步完善业务逻辑】
     *
     * @param num
     * @param model
     */
    @RequestMapping(path = "/new", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getNewRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getNewMovies(new GetNewMovieRequest(num)));
        return model;
    }


    // ************ 模糊检索 ***************

    /**
     * 提供基于名称或者描述的模糊检索功能
     *
     * @param query
     * @param model
     */
    @RequestMapping(path = "/query", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getFuzzySearchMovies(@RequestParam("query") String query, @RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getFuzzyMovies(new GetFuzzySearchMovieRequest(query, num)));
        return model;
    }

    // ************ 电影的详细页面 ***************

    /**
     * 获取单部电影信息
     *
     * @param mid
     * @param model
     */
    @RequestMapping(path = "/info/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieInfo(@PathVariable("mid") int mid, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movie", movieService.findMovieInfo(mid));
        return model;
    }

    /**
     * 需要提供给电影打标签的功能
     *
     * @param mid
     * @param tagname
     * @param model
     */
    @RequestMapping(path = "/addtag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public void addTagToMovie(@PathVariable("mid") int mid, @RequestParam("username") String username, @RequestParam("tagname") String tagname, Model model) {
        User user = userService.findUserByUsername(username);
        Tag tag = new Tag(user.getUid(), mid, Double.valueOf(tagname), System.currentTimeMillis() / 1000);
        tagService.addTagToMovie(tag);
    }

    /**
     * 获取单部电影的所有标签信息
     *
     * @param mid
     * @param model
     */
    @RequestMapping(path = "/tag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieTags(@PathVariable("mid") int mid, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("tag", tagService.getMovieTags(mid));
        return model;
    }

    /**
     * 需要能够获取电影相似的电影推荐
     *
     * @param mid
     * @param model
     */
    @RequestMapping(path = "/same/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getSimMoviesRecommendation(@PathVariable("mid") int mid, @RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movie", recommenderService.getHybridRecommendations(new GetHybridRecommendationRequest(0.5, mid, num)));
        return model;
    }

    /**
     * 需要提供给电影打分的功能
     *
     * @param username
     * @param score
     * @param model
     */
    @RequestMapping(path = "/rate/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public void rateMovie(@RequestParam("username") String username, @PathVariable("mid") int mid, @RequestParam("score") Double score, Model model) {
        User user = userService.findUserByUsername(username);
        Rating rating = new Rating(user.getUid(), mid, score, System.currentTimeMillis() / 1000);
        ratingService.rateToMovie(rating);

        // 输出埋点日志
        logger.info(Constant.USER_RATING_LOG_PREFIX + rating.getUid() + "|" + rating.getMid() + "|" + rating.getScore() + "|" + rating.getTimestamp());
    }

    // ************ 电影的类别页面 ***************

    // 需要提供电影类别的查找
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getGenresMovies(@RequestParam("genres") String genres, @RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("tag", recommenderService.getGenresMovies(new GetGenresMovieRequest(genres, num)));
        return model;
    }

    // ************ 用户空间页面 ***************

    // 需要提供用户的所有电影评分记录
    public Model getUserRatings(String username, Model model) {
        return null;
    }

    // 需要能够获取图标数据
    public Model getUserChart(String username, Model model) {
        return null;
    }

}