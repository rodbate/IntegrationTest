package com.github.rodbate.eh3;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
public class Main {

    public static void main(String[] args) {

        CacheManager cacheManager
            = CacheManagerBuilder.newCacheManagerBuilder()
                                 .withCache("preConfigured",
                                     CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)))
                                 .build(true);
        //cacheManager.init();

        Cache<Long, String> preConfigured =
            cacheManager.getCache("preConfigured", Long.class, String.class);

        Cache<Long, String> myCache = cacheManager.createCache("myCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB)).build());

        myCache.put(1L, "da one WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW !");
        String value = myCache.get(1L);

        //cacheManager.removeCache("preConfigured");

        //cacheManager.close();
    }
}
