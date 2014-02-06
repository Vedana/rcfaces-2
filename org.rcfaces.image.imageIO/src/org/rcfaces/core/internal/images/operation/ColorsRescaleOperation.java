/*
 * $Id: ColorsRescaleOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RasterOp;
import java.awt.image.RescaleOp;
import java.util.Map;

import javax.faces.FacesException;

import org.rcfaces.core.image.AbstractBufferedIndexedImageOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ColorsRescaleOperation extends
        AbstractBufferedIndexedImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final float DEFAULT_SCALE = 1;

    private static final float DEFAULT_OFFSET = 0;

    protected RasterOp getImageOperation(BufferedImage sourceImage,
            Map configuration) {
        float scale;
        String sScale = (String) configuration.get(getScalePropertyName());
        if (sScale != null && sScale.length() > 0) {
            try {
                scale = Float.parseFloat(sScale);

            } catch (NumberFormatException ex) {
                throw new FacesException("Invalid value of property '"
                        + getScalePropertyName() + "' (" + sScale + ").", ex);
            }
        } else {
            scale = getDefaultScale();
        }

        float offset;
        String sOffset = (String) configuration.get(getOffsetPropertyName());
        if (sOffset != null && sOffset.length() > 0) {
            try {
                offset = Float.parseFloat(sOffset);

            } catch (NumberFormatException ex) {
                throw new FacesException("Invalid value of property '"
                        + getOffsetPropertyName() + "' (" + sScale + ").", ex);
            }
        } else {
            offset = getDefaultOffset();
        }

        String param1 = (String) configuration.get("#0");
        if (param1 != null) {
            float valueDef;
            try {
                valueDef = Float.parseFloat(param1);

            } catch (NumberFormatException ex) {
                throw new FacesException(
                        "Invalid value for default property value='" + param1
                                + "'.", ex);
            }

            String defaultProperty = getDefaultPropertyName();
            if (defaultProperty.equals(getOffsetPropertyName())) {
                offset = valueDef;

            } else if (defaultProperty.equals(getScalePropertyName())) {
                scale = valueDef;
            }
        }

        float scales4[] = new float[4];
        float offsets4[] = new float[4];

        for (int i = 0; i < scales4.length; i++) {
            scales4[i] = scale;
            offsets4[i] = offset * 255;
        }

        float scales3[] = new float[3];
        float offsets3[] = new float[3];

        for (int i = 0; i < scales3.length; i++) {
            scales3[i] = scale;
            offsets3[i] = offset * 255;
        }

        /*
         * RenderingHints brightHint = new RenderingHints(
         * RenderingHints.KEY_INTERPOLATION,
         * RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         * brightHint.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
         * RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
         * brightHint.put(RenderingHints.KEY_COLOR_RENDERING,
         * RenderingHints.VALUE_COLOR_RENDER_QUALITY);
         * brightHint.put(RenderingHints.KEY_RENDERING,
         * RenderingHints.VALUE_RENDER_QUALITY);
         */

        RasterOp rescale3 = new RescaleOp(scales3, offsets3, null);

        RasterOp rescale4 = new RescaleOp(scales4, offsets4, null);

        return new RescaleRasterOp(rescale4, rescale3, scale, offset);
    }

    public int indexedColorModelSupport() {
        return INDEX_COLOR_MODEL_COLORS_MAP;
    }

    protected float getDefaultOffset() {
        return DEFAULT_OFFSET;
    }

    protected float getDefaultScale() {
        return DEFAULT_SCALE;
    }

    protected String getOffsetPropertyName() {
        return "offset";
    }

    protected String getScalePropertyName() {
        return "scale";
    }

    protected String getDefaultPropertyName() {
        return null;
    }

    protected void filter0(RasterOp imageOperation, BufferedImage source,
            BufferedImage destination) {
        int numComponents = source.getColorModel().getNumComponents();

        if (numComponents == 3) {
            imageOperation = ((RescaleRasterOp) imageOperation)
                    .get3Components();

        } else {
            imageOperation = ((RescaleRasterOp) imageOperation)
                    .get4Components();
        }

        super.filter0(imageOperation, source, destination);
    }

    public IndexColorModel filter(RasterOp imageOperation,
            Map requestParameter, IndexColorModel source) {
        int mapSize = source.getMapSize();

        int colorMap[] = new int[mapSize];
        source.getRGBs(colorMap);

        int transparentPixel = source.getTransparentPixel();

        float scale = ((RescaleRasterOp) imageOperation).scale;
        float offset = ((RescaleRasterOp) imageOperation).offset;

        for (int i = 0; i < colorMap.length; i++) {
            if (i == transparentPixel) {
                continue;
            }

            int c = colorMap[i];

            float r = ((c >> 16) & 255) / 255.0f;
            float g = ((c >> 8) & 255) / 255.0f;
            float b = (c & 255) / 255.0f;

            int rint = (int) ((r * scale + offset) * 255);
            if (rint < 0) {
                rint = 0;
            } else if (rint > 255) {
                rint = 255;
            }

            int gint = (int) ((g * scale + offset) * 255);
            if (gint < 0) {
                gint = 0;
            } else if (gint > 255) {
                gint = 255;
            }

            int bint = (int) ((b * scale + offset) * 255);
            if (bint < 0) {
                bint = 0;
            } else if (bint > 255) {
                bint = 255;
            }

            colorMap[i] = (rint << 16) | (gint << 8) | bint | (c & 0xff000000);
        }

        return new IndexColorModel(8, colorMap.length, colorMap, 0, source
                .hasAlpha(), source.getTransparentPixel(), DataBuffer.TYPE_BYTE);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
     */
    protected static class RescaleRasterOp extends ParametredRasterOp {
        private static final String REVISION = "$Revision: 1.2 $";

        private final RasterOp rescale3;

        private final float scale;

        private final float offset;

        public RescaleRasterOp(RasterOp parent, RasterOp rescale3, float scale,
                float offset) {
            super(parent);

            this.scale = scale;
            this.offset = offset;
            this.rescale3 = rescale3;
        }

        public RasterOp get3Components() {
            return rescale3;
        }

        public RasterOp get4Components() {
            return parent;
        }
    }
}
