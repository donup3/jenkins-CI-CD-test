package com.schedulsharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    //2
    @GetMapping
    public String hello() {
        return "hello jenkins~!!! Success!!!!!";
    }
}
