/*
 * $Id: IComponentEngineAccessor.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import org.rcfaces.core.internal.capability.IComponentEngine;

interface IComponentEngineAccessor {
    /**
     * INTERNAL USE ONLY
     */
    IComponentEngine getComponentEngine();

    /**
     * INTERNAL USE ONLY
     */
    void cloneComponentEngine();

}
