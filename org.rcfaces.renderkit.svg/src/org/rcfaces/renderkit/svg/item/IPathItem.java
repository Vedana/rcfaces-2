/*
 * $Id: IPathItem.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.item;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public interface IPathItem extends INodeItem {

    String getClip();

    String getClipPath();

    String getClipRule();

    String getColor();

    String getDisplay();

    String getFill();

    String getFillRule();

    String getFillOpacity();

    String getFontFamily();

    String getFontSize();

    String getFontSizeAdjust();

    String getFontStretch();

    String getFontStyle();

    String getFontVariant();

    String getFontWeight();

    String getOpacity();

    String getOverflow();

    String getStopColor();

    String getStopOpacity();

    String getStroke();

    String getStrokeDashArray();

    String getStrokeDashOffset();

    String getStrokeLineCap();

    String getStrokeLineJoin();

    String getStrokeMiterLimit();

    String getStrokeOpacity();

    String getStrokeWidth();

    String getTextAlign();

    String getTextAnchor();

    String getTextDecoration();

    String getTextRendering();

    String getVisibility();

    String getWordSpacing();

    String getWritingMode();
}
