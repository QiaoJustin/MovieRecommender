package com.practice.server.rest;

import com.practice.server.model.core.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description 用于处理 User 相关的动作
 * @Author fuchen
 * @Date 2019/12/6 13:32
 * Version 1.0
 */
@Controller
@RequestMapping("/rest/users")
public class UserRestApi {

    /**
     * 需要提供用户注册功能
     * url: /rest/users/register
     * @param username 用户名
     * @param password 密码
     * @param model
     * @return Model
     */
    @RequestMapping(path = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {

        return null;
    }

    /**
     * 需要提供用户登录功能
     *
     * @param username 用户名
     * @param password 密码
     * @param model
     */
    @RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public User loginUser(String username, String password, Model model) {
        return null;
    }

    /**
     * 需要能够添加用户偏爱的影片类别
     *
     * @param username
     * @param genres
     * @param model
     */
    @RequestMapping(path = "/addGenres", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Model addGenres(String username, String genres, Model model) {
        return null;
    }

}
