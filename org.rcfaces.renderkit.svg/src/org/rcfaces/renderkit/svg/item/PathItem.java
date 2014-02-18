/*
 * $Id: PathItem.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.item;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.ItemTools;
import org.rcfaces.renderkit.svg.component.PathComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class PathItem extends NodeItem implements IPathItem {
    private static final long serialVersionUID = -2897540950411472654L;

    private String clip;

    private String clipPath;

    private String clipRule;

    private String color;

    private String display;

    private String fill;

    private String fillRule;

    private String fillOpacity;

    private String fontFamily;

    private String fontSize;

    private String fontSizeAdjust;

    private String fontStretch;

    private String fontStyle;

    private String fontVariant;

    private String fontWeight;

    private String opacity;

    private String overflow;

    private String stopColor;

    private String stopOpacity;

    private String stroke;

    private String strokeDashArray;

    private String strokeDashOffset;

    private String strokeLineCap;

    private String strokeLineJoin;

    private String strokeMiterLimit;

    private String strokeOpacity;

    private String strokeWidth;

    private String textAlign;

    private String textAnchor;

    private String textDecoration;

    private String textRendering;

    private String visibility;

    private String wordSpacing;

    private String writingMode;

    public PathItem() {
    }

    public PathItem(IPathItem pathItem) {
        super(pathItem, null);

        ItemTools.copyProperties(this, IPathItem.class, pathItem);
    }

    public PathItem(PathComponent pathComponent) {
        super(pathComponent);

        ItemTools.copyProperties(this, PathComponent.class, pathComponent);
    }

    public final String getClip() {
        return clip;
    }

    public final void setClip(String clip) {
        this.clip = clip;
    }

    public final String getClipPath() {
        return clipPath;
    }

    public final void setClipPath(String clipPath) {
        this.clipPath = clipPath;
    }

    public final String getClipRule() {
        return clipRule;
    }

    public final void setClipRule(String clipRule) {
        this.clipRule = clipRule;
    }

    public final String getColor() {
        return color;
    }

    public final void setColor(String color) {
        this.color = color;
    }

    public final String getDisplay() {
        return display;
    }

    public final void setDisplay(String display) {
        this.display = display;
    }

    public final String getFill() {
        return fill;
    }

    public final void setFill(String fill) {
        this.fill = fill;
    }

    public final String getFillRule() {
        return fillRule;
    }

    public final void setFillRule(String fillRule) {
        this.fillRule = fillRule;
    }

    public final String getFillOpacity() {
        return fillOpacity;
    }

    public final void setFillOpacity(String fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public final String getFontFamily() {
        return fontFamily;
    }

    public final void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public final String getFontSize() {
        return fontSize;
    }

    public final void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public final String getFontSizeAdjust() {
        return fontSizeAdjust;
    }

    public final void setFontSizeAdjust(String fontSizeAdjust) {
        this.fontSizeAdjust = fontSizeAdjust;
    }

    public final String getFontStretch() {
        return fontStretch;
    }

    public final void setFontStretch(String fontStretch) {
        this.fontStretch = fontStretch;
    }

    public final String getFontStyle() {
        return fontStyle;
    }

    public final void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public final String getFontVariant() {
        return fontVariant;
    }

    public final void setFontVariant(String fontVariant) {
        this.fontVariant = fontVariant;
    }

    public final String getFontWeight() {
        return fontWeight;
    }

    public final void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public final String getOpacity() {
        return opacity;
    }

    public final void setOpacity(String opacity) {
        this.opacity = opacity;
    }

    public final String getOverflow() {
        return overflow;
    }

    public final void setOverflow(String overflow) {
        this.overflow = overflow;
    }

    public final String getStopColor() {
        return stopColor;
    }

    public final void setStopColor(String stopColor) {
        this.stopColor = stopColor;
    }

    public final String getStopOpacity() {
        return stopOpacity;
    }

    public final void setStopOpacity(String stopOpacity) {
        this.stopOpacity = stopOpacity;
    }

    public final String getTextAnchor() {
        return textAnchor;
    }

    public final void setTextAnchor(String textAnchor) {
        this.textAnchor = textAnchor;
    }

    public final String getTextDecoration() {
        return textDecoration;
    }

    public final void setTextDecoration(String textDecoration) {
        this.textDecoration = textDecoration;
    }

    public final String getTextRendering() {
        return textRendering;
    }

    public final void setTextRendering(String textRendering) {
        this.textRendering = textRendering;
    }

    public final String getVisibility() {
        return visibility;
    }

    public final void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public final String getWordSpacing() {
        return wordSpacing;
    }

    public final void setWordSpacing(String wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    public final String getWritingMode() {
        return writingMode;
    }

    public final void setWritingMode(String writingMode) {
        this.writingMode = writingMode;
    }

    public final String getStroke() {
        return stroke;
    }

    public final void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public final String getStrokeDashArray() {
        return strokeDashArray;
    }

    public final void setStrokeDashArray(String strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
    }

    public final String getStrokeDashOffset() {
        return strokeDashOffset;
    }

    public final void setStrokeDashOffset(String strokeDashOffset) {
        this.strokeDashOffset = strokeDashOffset;
    }

    public final String getStrokeLineCap() {
        return strokeLineCap;
    }

    public final void setStrokeLineCap(String strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
    }

    public final String getStrokeLineJoin() {
        return strokeLineJoin;
    }

    public final void setStrokeLineJoin(String strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
    }

    public final String getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    public final void setStrokeMiterLimit(String strokeMiterLimit) {
        this.strokeMiterLimit = strokeMiterLimit;
    }

    public final String getStrokeOpacity() {
        return strokeOpacity;
    }

    public final void setStrokeOpacity(String strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public final String getStrokeWidth() {
        return strokeWidth;
    }

    public final void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public final String getTextAlign() {
        return textAlign;
    }

    public final void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    @Override
    public void restoreState(FacesContext facesContext, Object state) {
        Object states[] = (Object[]) state;

        super.restoreState(facesContext, states[0]);

        ItemTools.restoreState(facesContext, this, this.getClass()
                .getSuperclass(), states[1]);
    }

    @Override
    public Object saveState(FacesContext facesContext) {
        Object state[] = new Object[2];

        state[0] = super.saveState(facesContext);

        state[1] = ItemTools.saveState(facesContext, this, this.getClass()
                .getSuperclass());

        return state;
    }

    @Override
    public void participeKey(StringAppender sa) {
        super.participeKey(sa);

        ItemTools.participeKey(sa, this, PathItem.class);
    }
}
