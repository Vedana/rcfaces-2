/*
 * $Id: SVGImageGeneratedInformation.java,v 1.2 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import java.awt.geom.AffineTransform;

import org.rcfaces.core.image.GeneratedImageInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:39 $
 */
public class SVGImageGeneratedInformation extends GeneratedImageInformation {
    private static final String SHAPE_VALUES_PROPERTY = "org.rcfaces.svg.SHAPE_VALUES";

    private static final String GLOBAL_TRANSFORM_PROPERTY = "org.rcfaces.svg.GLOBAL_TRANSFORM";

    public void setShapeValues(ShapeValue shapeValues[]) {
        setAttribute(SHAPE_VALUES_PROPERTY, shapeValues);
    }

    public ShapeValue[] getShapeValues() {
        return (ShapeValue[]) getAttribute(SHAPE_VALUES_PROPERTY);
    }

    public void setGlobalTransform(AffineTransform transform) {
        setAttribute(GLOBAL_TRANSFORM_PROPERTY, transform);
    }

    public AffineTransform getGlobalTransform() {
        return (AffineTransform) getAttribute(GLOBAL_TRANSFORM_PROPERTY);
    }

}
