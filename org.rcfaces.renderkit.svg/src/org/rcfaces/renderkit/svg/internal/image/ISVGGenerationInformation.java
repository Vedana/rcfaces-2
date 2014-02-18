/*
 * $Id: ISVGGenerationInformation.java,v 1.1 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:39 $
 */
public interface ISVGGenerationInformation extends
        IGenerationResourceInformation {

    String WIDTH_PROPERTY = "org.rfcaces.core.model.WIDTH";

    String HEIGHT_PROPERTY = "org.rfcaces.core.model.HEIGHT";

    void setHeight(int imageHeight);

    int getHeight();

    void setWidth(int imageWidth);

    int getWidth();
}
