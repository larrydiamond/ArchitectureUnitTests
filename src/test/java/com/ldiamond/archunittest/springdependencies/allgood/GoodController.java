package com.ldiamond.archunittest.springdependencies.allgood;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodController {
    GoodService goodService;
    GoodRepository goodRepository;

    public GoodController(GoodService goodService, GoodRepository goodRepository) {
        this.goodService = goodService;
        this.goodRepository = goodRepository;
    }

    @GetMapping("/passmytest")
    public String passmytest(String something) {
        goodService.doSomething();
        return "blah";
    }
}


