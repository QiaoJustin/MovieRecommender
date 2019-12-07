package com.practice.business.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
     *  需要提供用户注册功能
     * @param username      用户名
     * @param password      密码
     * @param model
     * @return Model
     */
    /*public Model registerUser(String username, String password, Model model){
        return null;
    }*/

    /**
     * 需要提供用户登录功能
     * @param username      用户名
     * @param password      密码
     * @param model
     */
    /*public Model loginUser(String username, String password, Model model) {
        return null;
    }*/

    /**
     * 需要能够添加用户偏爱的影片类别
     * @param username
     * @param genres
     * @param model
     */
    /*public Model addGenres(String username, String genres, Model model) {
        return null;
    }*/

}
