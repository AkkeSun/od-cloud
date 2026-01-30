package com.odcloud.adapter.in.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PolicyController {

    @GetMapping("/policy")
    public String policy() {
        return "policy";
    }
}
