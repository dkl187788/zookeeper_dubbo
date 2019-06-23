package com.dukl.learn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by adu on 2019/6/22.
 */
@RestController
@RequestMapping("/")
public class IndexController {
    @RequestMapping("")
    @ResponseBody
    public String index() {
        return "SUCCESS";
    }
}
