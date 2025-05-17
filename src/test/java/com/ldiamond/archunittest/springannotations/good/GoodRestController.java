package com.ldiamond.archunittest.springannotations.good;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodRestController {
    @GetMapping("/passmytest")
    public String passmytest(String something) {
        return "blah";
    }
}
