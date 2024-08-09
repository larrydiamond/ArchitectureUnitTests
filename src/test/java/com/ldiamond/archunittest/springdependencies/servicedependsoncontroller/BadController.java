package com.ldiamond.archunittest.springdependencies.servicedependsoncontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BadController {
    BadService badService;

    public BadController(BadService badService) {
        this.badService = badService;
    }

    @GetMapping("/passmytest")
    public String passmytest(String something) {
        badService.doSomething();
        return "blah";
    }

    public static void badIdea () { ; }
}


