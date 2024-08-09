package com.ldiamond.archunittest.springdependencies.servicedependsoncontroller;

import org.springframework.stereotype.Service;

@Service
public class BadService {
    public void doSomething() { BadController.badIdea(); }
}
