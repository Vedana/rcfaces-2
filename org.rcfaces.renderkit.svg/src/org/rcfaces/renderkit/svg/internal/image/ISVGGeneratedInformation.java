/*
 * $Id: ISVGGeneratedInformation.java,v 1.1 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:39 $
 */
public interface ISVGGeneratedInformation extends IGeneratedResourceInformation {

    String WIDTH_PROPERTY = "org.rfcaces.core.model.WIDTH";

    String HEIGHT_PROPERTY = "org.rfcaces.core.model.HEIGHT";

    void setHeight(int height);

    void setWidth(int width);
}
