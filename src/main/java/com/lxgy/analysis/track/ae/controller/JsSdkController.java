package com.lxgy.analysis.track.ae.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Gryant
 */
@Controller
@RequestMapping("/js_sdk")
public class JsSdkController {

    @RequestMapping("/index")
    public String index() {
        return "sdk/index";
    }

    @RequestMapping("/page_one")
    public String page_one() {

        return "sdk/page_one";
    }

    @RequestMapping("/page_two")
    public String page_two() {

        return "sdk/page_two";
    }

    @RequestMapping("/page_three")
    public String page_three() {

        return "sdk/page_three";
    }

}
