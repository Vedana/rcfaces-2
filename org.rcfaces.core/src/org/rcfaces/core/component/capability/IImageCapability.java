/*
 * $Id: IImageCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import org.rcfaces.core.component.familly.IContentAccessors;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IImageCapability {

    /**
     * Returns the URL of the image.
     */
    String getImageURL();

    /**
     * Specify the URL of the image.
     */
    void setImageURL(String url);

    /**
     * Returns ImageAccessors associated to the url. (or java.awt.Image binding)
     * 
     * @return IImageAccessors object.
     */
    IContentAccessors getImageAccessors();
}