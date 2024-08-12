package com.ldiamond.archunittest.springdependencies.repositorydependsonservice;

import org.springframework.stereotype.Repository;

@Repository
public class BadRepository {
    public void doSomething() {BadService.badIdea(); }
}

