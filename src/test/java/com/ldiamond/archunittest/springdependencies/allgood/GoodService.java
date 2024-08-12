package com.ldiamond.archunittest.springdependencies.allgood;

import org.springframework.stereotype.Service;

@Service
public class GoodService {
    GoodRepository goodRepository;

    GoodService(final GoodRepository goodRepository) {
        this.goodRepository = goodRepository;
    }

    public void doSomething() {
        goodRepository.doSomething();
    }
}
