/*
 * $Id: LimitedMap2.java,v 1.1 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.util.LinkedListEntry;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:59 $
 */
public class LimitedMap2<K, V extends Serializable> implements
        ILimitedMap<K, V> {

    private static final Log LOG = LogFactory.getLog(LimitedMap2.class);

    // Il faut un cache des erreurs .... et des autres !

    private final Map<K, Cache<K, V>> caches;

    private final boolean weakCacheEnabled;

    private SoftReference<Map<K, Cache<K, V>>> weakCacheSoftReference;

    private final int maxCacheSize;

    private final LinkedListEntry<Cache<K, V>> cachesOrder = new LinkedListEntry<Cache<K, V>>() {

        private static final long serialVersionUID = 6965443540296248833L;

        @Override
        protected Entry<Cache<K, V>> createEntry(Cache<K, V> element,
                Entry<Cache<K, V>> next, Entry<Cache<K, V>> previous) {

            if (element == null) {
                element = new Cache<K, V>(null, null);
            }

            element.next = next;
            element.previous = previous;

            return element;
        }

    };

    public LimitedMap2(int maxCacheSize, boolean weakCacheEnabled) {
        this.maxCacheSize = maxCacheSize;
        this.weakCacheEnabled = weakCacheEnabled;

        caches = new HashMap<K, Cache<K, V>>(maxCacheSize + 2);

        LOG.debug("Set max cache size to " + maxCacheSize + ".");

    }

    public synchronized V get(K key) {
        Cache<K, V> cache = caches.get(key);
        if (cache != null) {
            // On la remet en tete !

            if (cachesOrder.getFirst() != cache) {
                cachesOrder.remove(cache);
                cachesOrder.addFirst(cache);
            }

            return cache.serializable;
        }

        if (weakCacheSoftReference != null) {
            Map<K, Cache<K, V>> weakCache = weakCacheSoftReference.get();
            if (weakCache != null) {
                cache = weakCache.get(key);
                if (cache != null) {
                    return cache.serializable;
                }
            }
        }

        return null;
    }

    public synchronized void remove(K key) {

        if (weakCacheSoftReference != null) {
            Map<K, Cache<K, V>> weakCache = weakCacheSoftReference.get();
            if (weakCache != null) {
                weakCache.remove(key);
            }
        }

        Cache<K, V> cache = caches.remove(key);
        if (cache == null) {
            return;
        }

        cachesOrder.remove(cache);
    }

    public synchronized void put(K key, V serializable) {
        Cache<K, V> cache = caches.get(key);

        if (cache != null) {
            if (cache.serializable != serializable) {
                cache.serializable = serializable;
            }

            if (cachesOrder.getFirst() != cache) {
                cachesOrder.remove(cache);
                cachesOrder.addFirst(cache);
            }
            return;
        }

        cache = new Cache<K, V>(key, serializable);
        caches.put(key, cache);
        cachesOrder.addFirst(cache);

        // /

        int cacheSize = caches.size();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Register key='" + key + "' cacheSize=" + cacheSize + ".");
        }

        if (cacheSize > maxCacheSize) {
            Cache<K, V> oldest = cachesOrder.removeLast();

            caches.remove(oldest.key);

            if (weakCacheEnabled && Constants.LIMITED_MAP_WEAK_CACHE_SIZE > 0) {
                Map<K, Cache<K, V>> weakCache = null;
                if (weakCacheSoftReference != null) {
                    weakCache = weakCacheSoftReference.get();
                }
                if (weakCache == null) {
                    weakCache = new WeakHashMap<K, Cache<K, V>>(
                            Constants.LIMITED_MAP_WEAK_CACHE_SIZE);

                    weakCacheSoftReference = new SoftReference<Map<K, Cache<K, V>>>(
                            weakCache);
                }

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
     * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:59 $
     */
    private static class Cache<K, V extends Serializable> extends
            LinkedListEntry.Entry<Cache<K, V>> {

        final K key;

        V serializable;

        public Cache(K key, V serializable) {
            super(null, null, null);

            this.element = this;

            this.key = key;
            this.serializable = serializable;
        }
    }
}
