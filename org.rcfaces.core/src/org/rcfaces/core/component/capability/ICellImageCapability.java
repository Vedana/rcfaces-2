/*
 * $Id: ICellImageCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ICellImageCapability {

    /**
     * Returns an url string pointing to the default image.
     * 
     * @return image url
     */
    String getDefaultCellImageURL();

    /**
     * Sets an url string pointing to the default image.
     * 
     * @param defaultCellImageURL
     *            image url
     */
    void setDefaultCellImageURL(String defaultCellImageURL);

    /**
     * Returns an url string pointing to the image.
     * 
     * @return image url
     */
    String getCellImageURL();

    /**
     * Sets an url string pointing to the image.
     * 
     * @param cellImageURL
     *            image url for the cell
     */
    void setCellImageURL(String cellImageURL);
}
