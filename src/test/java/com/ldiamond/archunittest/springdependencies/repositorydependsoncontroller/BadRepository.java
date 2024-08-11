package com.ldiamond.archunittest.springdependencies.repositorydependsoncontroller;

import org.springframework.stereotype.Repository;

@Repository
public class BadRepository {
    public void doSomething() {BadController.badIdea(); }
}
