package com.ldiamond.archunittest.springproxiedselfcall.badcacheevict;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class BadCacheEvictSelfCall {

    @CacheEvict("authors")
    public void evictAuthor(Long id) {
        // Intentionally empty: the rule only checks who calls this method
    }

    public void replaceAuthor(Long id) {
        // This internal call will bypass the cache proxy and the entry will not be evicted
        evictAuthor(id);
    }
}
