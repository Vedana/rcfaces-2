/*
 * $Id: AbstractBufferedImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public abstract class AbstractBufferedImageOperation extends
        AbstractImageOperation implements IImageIOOperation {

    private static final Log LOG = LogFactory
            .getLog(AbstractBufferedImageOperation.class);

    /* packaged */RasterOp imageOperation;

    @Override
    public void configure(Map configuration) {
        this.imageOperation = getImageOperation(null, configuration);
    }

    protected abstract RasterOp getImageOperation(BufferedImage sourceImage,
            Map configuration);

    public BufferedImage filter(Map requestParameter, BufferedImage source,
            BufferedImage destination) {

        RasterOp imageOperation;

        if (requestParameter.isEmpty() == false
                && ignoreRequestParameter() == false) {
            imageOperation = getImageOperation(source, requestParameter);

        } else {
            imageOperation = this.imageOperation;
        }

        if (imageOperation == null) {
            return source;
        }

        int type = source.getType();
        if (type == BufferedImage.TYPE_BYTE_BINARY
                || type == BufferedImage.TYPE_BYTE_INDEXED) {
            type = BufferedImage.TYPE_INT_ARGB;
        }

        Rectangle2D dest = imageOperation.getBounds2D(source.getRaster());

        if (destination == null || destination.getType() != type
                || dest.getWidth() != source.getWidth()
                || dest.getHeight() != source.getHeight()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Convert image to type '" + type + "'.");
            }

            destination = new BufferedImage((int) dest.getWidth(),
                    (int) dest.getHeight(), type);
        }

        filter0(imageOperation, source, destination);

        return destination;
    }

    protected void filter0(RasterOp imageOperation, BufferedImage source,
            BufferedImage destination) {

        imageOperation.filter(source.getRaster(), destination.getRaster());
    }

    protected boolean ignoreRequestParameter() {
        return false;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
     */
    protected static class ParametredRasterOp implements RasterOp {
        protected final RasterOp parent;

        protected ParametredRasterOp(RasterOp parent) {
            this.parent = parent;
        }

        public WritableRaster createCompatibleDestRaster(Raster src) {
            return parent.createCompatibleDestRaster(src);
        }

        public WritableRaster filter(Raster src, WritableRaster dest) {
            return parent.filter(src, dest);
        }

        public Rectangle2D getBounds2D(Raster src) {
            return parent.getBounds2D(src);
        }

        public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
            return parent.getPoint2D(srcPt, dstPt);
        }

        public RenderingHints getRenderingHints() {
            return parent.getRenderingHints();
        }

    }
}
