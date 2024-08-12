package com.ldiamond.archunittest.springdependencies.repositorydependsonservice;

import org.springframework.stereotype.Service;

@Service
public class BadService {
    BadRepository badRepository;

    BadService (final BadRepository badRepository) {
        this.badRepository = badRepository;
    }

    public void doSomething() {
        badRepository.doSomething();
    }

    public static void badIdea() {}
}
