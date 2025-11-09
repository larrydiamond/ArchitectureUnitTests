package com.ldiamond.archunittest.springcache.good;

public class GoodCacheCaller {
    public String callFindAuthorById(GoodCacheUsage cacheUsage, Long id) {
        return cacheUsage.findAuthorById(id);
    }
}
