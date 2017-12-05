package com.github.rodbate.eh3;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
/**
 * @author rodbate
 * @since 2017/3/28
 */
public class DiskCache {

    public static void main(String[] args) {

        PersistentCacheManager manager =
            CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence("D://data.txt"))
                               .withCache("disk", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB, true))).build(true);

        Cache<String, String> disk = manager.getCache("disk", String.class, String.class);

        for (int i = 0; i < 10; i++) {
            System.out.println(disk.get("key : " + i));
        }

        manager.close();
    }

}
