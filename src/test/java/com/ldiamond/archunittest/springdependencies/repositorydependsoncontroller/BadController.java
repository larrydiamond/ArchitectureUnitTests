package com.ldiamond.archunittest.springdependencies.repositorydependsoncontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BadController {
    BadRepository badRepository;

    public BadController(BadRepository badRepository) {
        this.badRepository = badRepository;
    }

    @GetMapping("/passmytest")
    public String passmytest(String something) {
        badRepository.doSomething();
        return "blah";
    }

    public static void badIdea() { ; }
}
