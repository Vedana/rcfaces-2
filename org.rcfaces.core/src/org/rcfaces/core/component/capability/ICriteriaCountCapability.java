/*
 * $Id: ICriteriaCountCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * Enable/Disabled state.
 * 
 * @author jbmeslin@vedana.com
 */
public interface ICriteriaCountCapability {

    /**
     * 
     */
    boolean isFullCriteriaCount();

    /**
     *
     */
    void setFullCriteriaCount(boolean fullCriteriaCount);
}
