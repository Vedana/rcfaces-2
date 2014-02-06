/*
 * $Id: LimitedMap.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class LimitedMap<K, V extends Serializable> implements ILimitedMap<K, V> {

    private static final Log LOG = LogFactory.getLog(LimitedMap.class);

    // Il faut un cache des erreurs .... et des autres !

    private final Map<K, Cache<K, V>> caches;

    private final Map<K, Cache<K, V>> weakCache;

    private final int maxCacheSize;

    private List<Cache<K, V>> cacheList = new LinkedList<Cache<K, V>>();

    public LimitedMap(int maxCacheSize, boolean enableWeakRefs) {
        this.maxCacheSize = maxCacheSize;

        caches = new HashMap<K, Cache<K, V>>(maxCacheSize + 2);

        LOG.debug("Set max cache size to " + maxCacheSize + ".");

        if (Constants.BASIC_CONTENT_WEAK_CACHE_ENABLED) {
            weakCache = new WeakHashMap<K, Cache<K, V>>(maxCacheSize);
            LOG.debug("Create a weak map initialized with size " + maxCacheSize
                    + ".");

        } else {
            weakCache = null;
        }

    }

    public synchronized V get(K key) {
        Cache<K, V> cache = caches.get(key);
        if (cache == null && weakCache != null) {
            cache = weakCache.get(key);
        }

        if (cache == null) {
            return null;
        }

        if (cacheList.get(0) != cache) {
            cacheList.remove(cache);
            cacheList.add(0, cache);
        }

        return cache.serializable;
    }

    public synchronized void remove(K key) {
        Cache<K, V> cache = caches.remove(key);
        if (cache == null) {
            return;
        }
        cacheList.remove(cache);
    }

    public synchronized void put(K key, V serializable) {
        Cache<K, V> cache = caches.get(key);
        if (cache != null) {
            cache.serializable = serializable;

            cacheList.remove(cache);

        } else {
            cache = new Cache<K, V>(key, serializable);
            caches.put(key, cache);
        }

        cacheList.add(0, cache);

        int cacheSize = caches.size();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Register key='" + key + "' cacheSize=" + cacheSize + ".");
        }

        if (cacheSize > maxCacheSize) {
            Cache<K, V> oldest = cacheList.remove(cacheSize - 1);

            caches.remove(oldest.key);

            if (weakCache != null) {
                weakCache.put(oldest.key, oldest);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove the oldest cached.");
            }
        }
    }

    private String computeKey(String cmd, String url) {
        return cmd + "\uffff" + url;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
     */
    private static class Cache<K, V extends Serializable> {

        final K key;

        V serializable;

        public Cache(K key, V serializable) {
            this.key = key;
            this.serializable = serializable;
        }
    }
}
