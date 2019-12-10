package com.practice.server.rest;

import com.practice.server.model.request.LoginUserRequest;
import com.practice.server.model.request.RegisterUserRequest;
import com.practice.server.model.request.UpdateUserGenresRequest;
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

/**
 * @Description 用于处理 User 相关的动作
 * @Author fuchen
 * @Date 2019/12/6 13:32
 * Version 1.0
 */
@Controller
@RequestMapping("/rest/users")
public class UserRestApi {

    @Autowired
    private UserService userService;

    /**
     * 需要提供用户注册功能
     * 访问 url: /rest/users/register?username=abc&password=123
     * 返回 ：{success: true}
     * @param username 用户名
     * @param password 密码
     * @param model
     * @return Model
     */
    @RequestMapping(path = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        model.addAttribute("success", userService.registerUser(new RegisterUserRequest(username, password)));
        return model;
    }

    /**
     * 需要提供用户登录功能
     * 访问 url : /rest/users/login?username=abc&password=123
     * 返回 ： {success: true}
     * @param username 用户名
     * @param password 密码
     * @param model
     */
    @RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model loginUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        model.addAttribute("success", userService.loginUser(new LoginUserRequest(username, password)));
        return model;
    }

    /**
     * 需要能够添加用户偏爱的影片类别
     * 访问 url： /rest/users/genres?username=abc&genres=a|b|c|d|e
     * @param username
     * @param genres
     * @param model
     */
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public void addGenres(@RequestParam("username") String username, @RequestParam("genres") String genres, Model model) {
        List<String> genresList = new ArrayList<>();
        String[] genre = genres.split("\\|");
        for (String gen : genre)
            genresList.add(gen);
        userService.updateUserGenres(new UpdateUserGenresRequest(username,genresList));
    }

}
