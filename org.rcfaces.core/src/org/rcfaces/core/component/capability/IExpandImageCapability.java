/*
 * $Id: IExpandImageCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * An url string pointing to an image used for the expanded state.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IExpandImageCapability extends IStatesImageCapability {

    /**
     * Returns the url string pointing to an image used for the expanded state.
     * 
     * @return url
     */
    String getExpandedImageURL();

    /**
     * Sets the url string pointing to an image used for the expanded state.
     * 
     * @param url
     *            url for image
     */
    void setExpandedImageURL(String url);

}
