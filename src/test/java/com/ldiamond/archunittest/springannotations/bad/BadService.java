package com.ldiamond.archunittest.springannotations.bad;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class BadService {
    @GetMapping("/passmytest")
    public String passmytest(String something) {
        return "blah";
    }
}
