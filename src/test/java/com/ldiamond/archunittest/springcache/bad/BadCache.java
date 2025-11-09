package com.ldiamond.archunittest.springcache.bad;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "authors")
public class BadCache {

    @Cacheable
    public String findAuthorById(Long id) {
        return new String("Jane Doe");
    }

    public String findAnotherAuthorById(Long id) {
        // This internal call will bypass the cache
        return findAuthorById(id);
    }
}
