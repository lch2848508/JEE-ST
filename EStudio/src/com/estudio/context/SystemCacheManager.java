package com.estudio.context;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;

public final class SystemCacheManager {
    private CacheManager cacheManager = null;
    private Cache cache4FormDataSetRecord = null;
    private Cache cache4Design = null;
    private Cache cache4WebClient = null;
    private Cache cache4WebGIS = null;

    /**
     * 获取缓存
     * 
     * @param key
     * @return
     */
    public Object getDataSourceResultSet(final String key) {
        final Element e = cache4FormDataSetRecord.get(key);
        return e != null ? e.getObjectValue() : null;
    }

    /**
     * 缓存对象
     * 
     * @param key
     * @param value
     */
    public void putDataSourceResultSet(final String key, final Object value) {
        cache4FormDataSetRecord.put(new Element(key, value));
    }

    /**
     * 获取设计器缓存
     * 
     * @param key
     * @return
     */
    public Object getDesignObject(final String key) {
        if (cache4Design != null) {
            final Element e = cache4Design.get(key);
            return e != null ? e.getObjectValue() : null;
        }
        return null;
    }

    /**
     * 缓存设计器缓存
     * 
     * @param key
     * @param value
     */
    public void putDesignObject(final String key, final Object value) {
        if (cache4Design != null)
            cache4Design.put(new Element(key, value));
    }

    /**
     * 删除缓存
     * 
     * @param key
     */
    public void removeDesignObject(final String key) {
        if (cache4Design != null)
            cache4Design.remove(key);
    }

    /**
     * 删除以KeyPrefix开头的所有对象
     * 
     * @param keyPrefix
     */
    public void removeDesignObjectByPrefix(final String keyPrefix) {

        final List<String> keys = new ArrayList<String>();
        for (final Object key : cache4Design.getKeys())
            if (StringUtils.startsWith((String) key, keyPrefix))
                keys.add((String) key);
        for (final String k : keys)
            cache4Design.remove(k);

    }

    /**
     * 获取设计器缓存
     * 
     * @param key
     * @return
     */
    public Object getWebClientObject(final String key) {
        final Element e = cache4WebClient.get(key);
        return e != null ? e.getObjectValue() : null;
    }

    /**
     * 缓存设计器缓存
     * 
     * @param key
     * @param value
     */
    public void putWebClientObject(final String key, final Object value) {
        cache4WebClient.put(new Element(key, value));
    }

    /**
     * 删除缓存
     * 
     * @param key
     */
    public void removeWebClientObject(final String key) {
        cache4WebClient.remove(key);
    }

    /**
     * 删除以KeyPrefix开头的所有对象
     * 
     * @param keyPrefix
     */
    public void removeWebClientByPrefix(final String keyPrefix) {

        final List<String> keys = new ArrayList<String>();
        for (final Object key : cache4WebClient.getKeys())
            if (StringUtils.startsWith((String) key, keyPrefix))
                keys.add((String) key);
        for (final String k : keys)
            cache4WebClient.remove(k);
    }

    /**
     * 初始化缓存对象
     * 
     * @param cacheConfigFileName
     */
    public void init(final String cacheConfigFileName) {
        cacheManager = new CacheManager(cacheConfigFileName);
        cache4FormDataSetRecord = cacheManager.getCache("FormDataSetRecordCache");
        cache4Design = cacheManager.getCache("DesignService");
        cache4WebClient = cacheManager.getCache("WebClient");
        cache4WebGIS = cacheManager.getCache("webgiscache");
    }

    public Object getWebGISItem(String key) {
        Element element = cache4WebGIS.get(key);
        Object result = element == null ? null : element.getObjectValue();
        return result;
    }

    public void putWebGISItem(String key, Object value) {
        cache4WebGIS.put(new Element(key, value));
        cache4WebGIS.flush();
    }

    public void putWebGISItem(String key, Object value, int liveSecond) {
        Element cacheElement = new Element(key, value);
        cacheElement.setEternal(false);
        cacheElement.setTimeToIdle(liveSecond);
        cacheElement.setTimeToLive(liveSecond);
        cache4WebGIS.put(cacheElement);
        cache4WebGIS.flush();
    }

    private SystemCacheManager() {

    }

    private final static SystemCacheManager INSTANCE = new SystemCacheManager();

    public static SystemCacheManager getInstance() {
        return INSTANCE;
    }

}
