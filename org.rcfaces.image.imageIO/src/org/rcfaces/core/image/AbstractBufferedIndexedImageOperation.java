/*
 * $Id: AbstractBufferedIndexedImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.image;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RasterOp;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public abstract class AbstractBufferedIndexedImageOperation extends
        AbstractBufferedImageOperation implements IIndexedImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final Log LOG = LogFactory
            .getLog(AbstractBufferedIndexedImageOperation.class);

    public IndexColorModel filter(Map<String, Object> requestParameter, IndexColorModel source,
            BufferedImage sourceImage) {
        RasterOp imageOperation;

        if (requestParameter.isEmpty() == false
                && ignoreRequestParameter() == false) {
            imageOperation = getImageOperation(sourceImage, requestParameter);

        } else {
            imageOperation = this.imageOperation;
        }

        if (imageOperation == null) {
            return source;
        }

        return filter(imageOperation, requestParameter, source);
    }

    protected abstract IndexColorModel filter(RasterOp imageOperation,
            Map requestParameter, IndexColorModel source);
}
