package com.ldiamond.archunittest.springdependencies.allgood;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodController {
    GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    @GetMapping("/passmytest")
    public String passmytest(String something) {
        goodService.doSomething();
        return "blah";
    }
}


