/*
 * $Id: IImageSizeCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IImageSizeCapability {

    /**
     * Returns an int value specifying the width (in pixels) to use for the
     * image shown.
     * 
     * @return image width in pixels
     */
    int getImageWidth();

    /**
     * Sets an int value specifying the width (in pixels) to use for the image
     * shown.
     * 
     * @param width
     *            image width in pixels
     */
    void setImageWidth(int width);

    /**
     * Returns an int value specifying the height (in pixels) to use for the
     * image shown.
     * 
     * @return image height in pixels
     */
    int getImageHeight();

    /**
     * Sets an int value specifying the height (in pixels) to use for the image
     * shown.
     * 
     * @param height
     *            image height in pixels
     */
    void setImageHeight(int height);
}
