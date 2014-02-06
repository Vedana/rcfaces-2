/*
 * $Id: ResizeOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
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
public class ResizeOperation extends AbstractScaleOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private float size;

    public void configure(Map configuration) {
        super.configure(configuration);

        String size = (String) configuration.get(getSizePropertyName());
        if (size != null) {
            this.size = Float.parseFloat(size);
        }
    }

    protected String getSizePropertyName() {
        return "size";
    }

    protected String getDefaultPropertyName() {
        return getSizePropertyName();
    }

    protected float[] computeScales(BufferedImage sourceImage, Map configuration) {

        if (sourceImage == null) {
            return new float[] { 1.0f, 1.0f };
        }

        float size = this.size;

        String ssize = (String) configuration.get(getSizePropertyName());
        if (ssize == null) {
            ssize = (String) configuration.get("#0");
        }
        if (ssize != null) {
            size = Float.parseFloat(ssize);
        }

        float w = size / sourceImage.getWidth();
        float h = size / sourceImage.getHeight();

        if (w > h) {
            w = h;
        }

        return new float[] { w, w };
    }

}
