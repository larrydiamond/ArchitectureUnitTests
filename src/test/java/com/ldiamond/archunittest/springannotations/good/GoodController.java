package com.ldiamond.archunittest.springannotations.good;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodController {
    @GetMapping("/passmytest")
    public String passmytest(String something) {
        return "blah";
    }
}
