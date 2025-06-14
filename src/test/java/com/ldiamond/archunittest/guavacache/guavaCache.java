package com.ldiamond.archunittest.guavacache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class guavaCache {
    void createCache() {
        CacheLoader<String, String> loader = new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                return key.toUpperCase();
            }
        };
        LoadingCache<String, String> cache = CacheBuilder.newBuilder().maximumSize(3).build(loader);
    }
}
