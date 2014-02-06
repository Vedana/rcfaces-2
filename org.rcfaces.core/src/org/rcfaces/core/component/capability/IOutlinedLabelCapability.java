/*
 * $Id: IOutlinedLabelCapability.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A label which will be outlined
 * 
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public interface IOutlinedLabelCapability {

    public enum Method {
        IgnoreCase, IgnoreAccents, Multiple, StartsWith, WordOnly, FullText, Server
    }

    /**
     * Returns the label which is outlined
     * 
     * @return The label
     */
    String getOutlinedLabel();

    /**
     * Sets the label which will be outlined
     * 
     * @param label
     *            Label which is outlined
     */
    void setOutlinedLabel(String label);

    String getOutlinedLabelMethod();

    void setOutlinedLabelMethod(String method);
}
