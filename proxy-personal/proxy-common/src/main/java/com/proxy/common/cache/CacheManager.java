package com.proxy.common.cache;

/**
 * 缓存管理器

 */
public interface CacheManager<K, V> {

    /**
     * 获取缓存容器
     */
    Cache getCache(String name);

}
