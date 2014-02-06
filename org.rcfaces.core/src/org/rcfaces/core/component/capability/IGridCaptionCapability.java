/*
 * $Id: IGridCaptionCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

public interface IGridCaptionCapability extends ICaptionCapability {

    /**
     * 
     * @return the grid summary
     */
    String getSummary();

    /**
     * 
     * @param caption
     *            Grid summary
     */
    void setSummary(String summary);
}
