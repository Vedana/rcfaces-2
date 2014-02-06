/*
 * $Id: IIndexedImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.image;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public interface IIndexedImageOperation extends IImageOperation {

    int INDEX_COLOR_MODEL_NOT_SUPPORTED = 0;

    int INDEX_COLOR_MODEL_SUPPORTED = 1;

    int INDEX_COLOR_MODEL_COLORS_MAP = 2;

    int indexedColorModelSupport();

    IndexColorModel filter(Map<String, Object> requestParameter,
            IndexColorModel source, BufferedImage sourceImage);

}
