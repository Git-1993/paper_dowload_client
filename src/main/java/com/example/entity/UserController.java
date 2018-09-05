package com.example.entity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/7/3.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/user")
    public void test() {

    }
}
