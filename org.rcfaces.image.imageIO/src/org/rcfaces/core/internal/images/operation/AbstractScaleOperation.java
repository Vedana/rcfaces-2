/*
 * $Id: AbstractScaleOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RasterOp;
import java.util.Map;

import org.rcfaces.core.image.AbstractBufferedImageOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public abstract class AbstractScaleOperation extends
        AbstractBufferedImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    protected RasterOp getImageOperation(BufferedImage sourceImage,
            Map configuration) {
        float scales[] = computeScales(sourceImage, configuration);

        if (scales == null) {
            return null;
        }

        if (scales[0] == 1.0f && scales[1] == 1.0f) {
            return null;
        }
        if (scales[0] == 0.0f && scales[1] == 0.0f) {
            return null;
        }

        AffineTransform tx = new AffineTransform();
        tx.scale(scales[0], scales[1]);

        return new AffineTransformOp(tx, getInterpolationType());
    }

    protected int getInterpolationType() {
        return AffineTransformOp.TYPE_BILINEAR;
    }

    protected abstract float[] computeScales(BufferedImage sourceImage,
            Map configuration);
}
