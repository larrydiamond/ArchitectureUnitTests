package com.ldiamond.archunittest.springdependencies.repositorydependsonservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BadController {
    BadService badService;
    BadRepository badRepository;

    public BadController(final BadService BadService, BadRepository badRepository) {
        this.badService = badService;
        this.badRepository = badRepository;
    }

    @GetMapping("/passmytest")
    public String passmytest(String something) {
        badRepository.doSomething();
        return "blah";
    }
}
