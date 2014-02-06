/*
 * $Id: IBackgroundImageCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * Everything concerning background image.
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IBackgroundImageCapability {

    /**
     * Returns an url string pointing to the background image.
     * 
     * @return image url
     */
    String getBackgroundImageURL();

    /**
     * Sets an url string pointing to the background image.
     * 
     * @param backgroundImageURL
     *            image url
     */
    void setBackgroundImageURL(String backgroundImageURL);

    /**
     * Returns a string indicating the horizontal positionning for the
     * background image.
     * 
     * @return horizontal position
     */
    String getBackgroundImageHorizontalPosition();

    /**
     * Sets a string indicating the horizontal positionning for the background
     * image.
     * 
     * @param horizontalPosition
     *            horizontal position
     */
    void setBackgroundImageHorizontalPosition(String horizontalPosition);

    /**
     * Returns a string indicating the vertical positionning for the background
     * image.
     * 
     * @return vertical position
     */
    String getBackgroundImageVerticalPosition();

    /**
     * Sets a string indicating the vertical positionning for the background
     * image.
     * 
     * @param verticalPosition
     *            position
     */
    void setBackgroundImageVerticalPosition(String verticalPosition);

    /**
     * Returns a boolean value indicating wether the background image should be
     * repeated horizontally or not.
     * 
     * @return repeat
     */
    boolean isBackgroundImageHorizontalRepeat();

    /**
     * Sets a boolean value indicating wether the background image should be
     * repeated horizontally or not.
     * 
     * @param repeat
     *            boolean
     */
    void setBackgroundImageHorizontalRepeat(boolean repeat);

    /**
     * Returns a boolean value indicating wether the background image should be
     * repeated vertically or not.
     * 
     * @return repeat
     */
    boolean isBackgroundImageVerticalRepeat();

    /**
     * Sets a boolean value indicating wether the background image should be
     * repeated vertically or not.
     * 
     * @param repeat
     *            boolean
     */
    void setBackgroundImageVerticalRepeat(boolean repeat);
}