/*
 * $Id: SetSizeOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class SetSizeOperation extends AbstractScaleOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private float sizeWidth;

    private float sizeHeight;

    public void configure(Map configuration) {
        super.configure(configuration);

        String sizeWidth = (String) configuration.get(getWidthPropertyName());
        String sizeHeight = (String) configuration.get(getHeightPropertyName());
        if (sizeWidth != null && sizeHeight != null) {
            this.sizeWidth = Float.parseFloat(sizeWidth);
            this.sizeHeight = Float.parseFloat(sizeHeight);
        }
    }

    private String getHeightPropertyName() {
        return "height";
    }

    private String getWidthPropertyName() {
        return "width";
    }

    protected float[] computeScales(BufferedImage sourceImage, Map configuration) {

        if (sourceImage == null) {
            return null;
        }

        float sizeWidth = this.sizeWidth;
        float sizeHeight = this.sizeHeight;

        String ssizeWidth = (String) configuration.get(getWidthPropertyName());
        if (ssizeWidth == null) {
            ssizeWidth = (String) configuration.get("#0");
        }
        String ssizeHeight = (String) configuration
                .get(getHeightPropertyName());
        if (ssizeHeight == null) {
            ssizeHeight = (String) configuration.get("#1");
        }
        if (ssizeWidth != null && ssizeHeight != null) {
            sizeWidth = Float.parseFloat(ssizeWidth);
            sizeHeight = Float.parseFloat(ssizeHeight);
        }

        float w = sizeWidth / sourceImage.getWidth();
        float h = sizeHeight / sourceImage.getHeight();

        return new float[] { w, h };
    }
}
