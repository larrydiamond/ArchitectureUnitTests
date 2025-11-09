package com.ldiamond.archunittest.springcache.good;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "authors")
public class GoodCacheUsage {

    @Cacheable
    public String findAuthorById(Long id) {
        return new String("Jane Doe");
    }
}
