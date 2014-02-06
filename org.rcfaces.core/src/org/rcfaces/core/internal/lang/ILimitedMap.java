/*
 * $Id: ILimitedMap.java,v 1.1 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.Serializable;

public interface ILimitedMap<K, V extends Serializable> {

    V get(K key);

    void remove(K key);

    void put(K key, V serializable);
}