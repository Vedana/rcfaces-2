/*
 * $Id: AbstractCssRenderer.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IBackgroundImageCapability;
import org.rcfaces.core.component.capability.IFontCapability;
import org.rcfaces.core.component.capability.IForegroundBackgroundColorCapability;
import org.rcfaces.core.component.capability.IHeightCapability;
import org.rcfaces.core.component.capability.IHiddenModeCapability;
import org.rcfaces.core.component.capability.ILayoutPositionCapability;
import org.rcfaces.core.component.capability.IMarginCapability;
import org.rcfaces.core.component.capability.IPositionCapability;
import org.rcfaces.core.component.capability.ISeverityStyleClassCapability;
import org.rcfaces.core.component.capability.ISizeCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.component.capability.ITextAlignmentCapability;
import org.rcfaces.core.component.capability.IVisibilityCapability;
import org.rcfaces.core.component.capability.IWidthCapability;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.ns.INamespaceConfiguration;
import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
public abstract class AbstractCssRenderer extends AbstractJavaScriptRenderer
        implements ICssRenderer {

    private static final Log LOG = LogFactory.getLog(AbstractCssRenderer.class);

    private static final int DEFAULT_RENDERED_HIDDEN_MODE = IHiddenModeCapability.IGNORE_HIDDEN_MODE;

    private static final String DEFAULT_MARGIN_UNIT = "px";

    private static final String CSS_STYLE_CLASSES_PROPERTY_NAME = "org.rcfaces.html.CSS_STYLE_CLASSES";

    public static final String BLANK_IMAGE_URL = "blank.gif";

    protected static final int CSS_ALL_MASK = 0xffff;

    protected static final int CSS_FONT_MASK = 1;

    protected static final int CSS_SIZE_MASK = 2;

    protected static final int SEVERITY_CLASSES_MASK = 4;

    protected static final int INIT_LAYOUT_MASK = 4;

    /**
     * @param htmlWriter
     */
    public String getComponentStyleClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName();
    }

    public String getMainStyleClassName() {
        return getJavaScriptClassName();
    }

    public final ICssStyleClasses getCssStyleClasses(IHtmlWriter htmlWriter) {

        ICssStyleClasses cssStyleClasses = (ICssStyleClasses) htmlWriter
                .getComponentRenderContext().getAttribute(
                        CSS_STYLE_CLASSES_PROPERTY_NAME);
        if (cssStyleClasses != null) {
            return cssStyleClasses;
        }

        cssStyleClasses = createStyleClasses(htmlWriter);

        htmlWriter.getComponentRenderContext().setAttribute(
                CSS_STYLE_CLASSES_PROPERTY_NAME, cssStyleClasses);

        return cssStyleClasses;
    }

    protected ICssStyleClasses createStyleClasses(IHtmlWriter htmlWriter) {
        return new CssStyleClasses(getMainStyleClassName(),
                getComponentStyleClassName(htmlWriter));
    }

    protected IHtmlWriter writeStyleClass(IHtmlWriter writer,
            ICssStyleClasses cssStyleClasses) throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        if (component instanceof IStyleClassCapability) {
            IStyleClassCapability styleClassCapability = (IStyleClassCapability) component;

            String styleClass = styleClassCapability.getStyleClass();
            if (styleClass != null) {
                cssStyleClasses.addStyleClass(styleClass);
            }

            String sc = cssStyleClasses.constructUserStyleClasses();
            if (sc != null && sc.length() > 0) {
                writer.writeAttributeNS("styleClass", sc);
            }
        }

        String cssClass = cssStyleClasses.constructClassName();
        if (cssClass.length() > 0) {
            writer.writeClass(cssClass);
        }

        return writer;
    }

    /*
     * public final String getStyleClassName(UIComponent component) { return
     * getComponentStyleClassName(); }
     */

    /*
     * protected String computeComponentStyleClass(UIComponent component, String
     * classSuffix) { if (component instanceof IDisabledCapability) { if
     * (((IDisabledCapability) component).isDisabled()) { classSuffix =
     * "_disabled"; } }
     * 
     * if (classSuffix == null) { if (component instanceof IReadOnlyCapability)
     * { if (((IReadOnlyCapability) component).isReadOnly()) { classSuffix =
     * "_readOnly"; } } }
     * 
     * if (component instanceof IRequiredCapability) { if
     * (((IRequiredCapability) component).isRequired()) { classSuffix +=
     * "_required"; } }
     * 
     * return classSuffix; }
     */

    protected final IHtmlWriter writeCssAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        return writeCssAttributes(htmlWriter, getCssStyleClasses(htmlWriter),
                CSS_ALL_MASK);
    }

    protected final IHtmlWriter writeCssAttributes(IHtmlWriter htmlWriter,
            ICssStyleClasses cssStyleClasses, int attributesMask)
            throws WriterException {
        UIComponent component = htmlWriter.getComponentRenderContext()
                .getComponent();

        writeStyleClass(htmlWriter, cssStyleClasses);

        if ((attributesMask & SEVERITY_CLASSES_MASK) != 0) {
            if (component instanceof ISeverityStyleClassCapability) {
                writeSeverityStyleClasses(htmlWriter,
                        (ISeverityStyleClassCapability) component);
            }
        }

        ICssWriter cssWriter = htmlWriter.writeStyle();

        int hiddenMode = DEFAULT_RENDERED_HIDDEN_MODE;

        if (component instanceof IVisibilityCapability) {
            IVisibilityCapability visibilityCapability = (IVisibilityCapability) component;

            cssWriter.writeVisibility(visibilityCapability);

            if (visibilityCapability instanceof IHiddenModeCapability) {
                IHiddenModeCapability hiddenModeCapability = (IHiddenModeCapability) visibilityCapability;

                hiddenMode = hiddenModeCapability.getHiddenMode();
                if (hiddenMode == 0) {
                    hiddenMode = DEFAULT_RENDERED_HIDDEN_MODE;
                }
            }
        }

        if ((attributesMask & CSS_FONT_MASK) != 0) {
            if (component instanceof IFontCapability) {
                cssWriter.writeFont((IFontCapability) component);
            }
        }

        if (component instanceof ITextAlignmentCapability) {
            cssWriter.writeTextAlignment((ITextAlignmentCapability) component);
        }

        writeComponentPosition(htmlWriter, cssWriter, component, attributesMask);

        if (component instanceof IMarginCapability) {
            cssWriter.writeMargin((IMarginCapability) component);
        }

        IForegroundBackgroundColorCapability foregroundBackgroundColorCapability = null;
        if (component instanceof IForegroundBackgroundColorCapability) {
            foregroundBackgroundColorCapability = (IForegroundBackgroundColorCapability) component;

            cssWriter.writeForeground(foregroundBackgroundColorCapability);
        }

        IBackgroundImageCapability backgroundImageCapability = null;
        if (component instanceof IBackgroundImageCapability) {
            backgroundImageCapability = (IBackgroundImageCapability) component;
        }

        if (backgroundImageCapability != null
                || foregroundBackgroundColorCapability != null) {
            cssWriter.writeBackground(foregroundBackgroundColorCapability,
                    backgroundImageCapability);
        }

        writeCustomCss(htmlWriter, cssWriter);

        if (hiddenMode != DEFAULT_RENDERED_HIDDEN_MODE) {
            htmlWriter.writeAttributeNS("hiddenMode", hiddenMode);
        }

        return htmlWriter;
    }

    protected void writeComponentPosition(IHtmlWriter htmlWriter,
            ICssWriter cssWriter, UIComponent component, int attributesMask)
            throws WriterException {

        boolean positionSetted = false;
        if (component instanceof IPositionCapability) {
            IPositionCapability positionCapability = (IPositionCapability) component;

            if (positionCapability.getX() != null
                    || positionCapability.getY() != null) {
                cssWriter.writePosition((IPositionCapability) component);
                positionSetted = true;
            }
        }

        if (positionSetted == false
                && (component instanceof ILayoutPositionCapability)) {
            ILayoutPositionCapability layoutPositionCapability = (ILayoutPositionCapability) component;

            writeLayoutPosition(htmlWriter, cssWriter, layoutPositionCapability);
        }

        if ((attributesMask & CSS_SIZE_MASK) != 0) {
            if (component instanceof ISizeCapability) {
                cssWriter.writeSize((ISizeCapability) component);
            }
        }
    }

    protected boolean needInitLayout() {
        return false;
    }

    protected void writeLayoutPosition(IHtmlWriter writer,
            ICssWriter cssWriter,
            ILayoutPositionCapability layoutPositionCapability)
            throws WriterException {

        String width = null;
        if (layoutPositionCapability instanceof IWidthCapability) {
            width = ((IWidthCapability) layoutPositionCapability).getWidth();
        }

        String height = null;
        if (layoutPositionCapability instanceof IHeightCapability) {
            height = ((IHeightCapability) layoutPositionCapability).getHeight();
        }

        int needLayout = 0;
        boolean positionned = false;

        Number verticalCenter = layoutPositionCapability.getVerticalCenter();
        if (verticalCenter != null) {
            needLayout |= 0x01;
            positionned = true;

            writer.writeAttribute("v:verticalCenter",
                    verticalCenter.longValue());

            if (height != null) {
                writer.writeAttribute("v:height", height);
            }

        } else {
            Number top = layoutPositionCapability.getTop();
            if (top != null) {
                positionned = true;
                writer.writeAttribute("v:top", top.intValue());

                cssWriter.writeTopPx(top.intValue());
            }

            Number bottom = layoutPositionCapability.getBottom();
            if (bottom != null && (top == null || height == null)) {
                positionned = true;
                writer.writeAttribute("v:bottom", bottom.intValue());

                if (top == null) {
                    cssWriter.writeBottomPx(bottom.intValue());
                } else {
                    needLayout |= 0x10;
                }
            } else if (height != null) {
                writer.writeAttribute("v:height", height);
            }
        }

        Number horizontalCenter = layoutPositionCapability
                .getHorizontalCenter();
        if (horizontalCenter != null) {
            needLayout |= 0x02;
            positionned = true;

            writer.writeAttribute("v:horizontalCenter",
                    horizontalCenter.longValue());

            if (width != null) {
                writer.writeAttribute("v:width", width);
            }

        } else {
            Number left = layoutPositionCapability.getLeft();
            if (left != null) {
                positionned = true;
                writer.writeAttribute("v:left", left.intValue());

                cssWriter.writeLeftPx(left.intValue());
            }

            Number right = layoutPositionCapability.getRight();
            if (right != null && (left == null || width == null)) {
                positionned = true;
                writer.writeAttribute("v:right", right.intValue());

                if (left == null) {
                    cssWriter.writeRightPx(right.intValue());

                } else {
                    // La taille doit être calculée !
                    needLayout |= 0x20;
                }
            } else if (width != null) {
                writer.writeAttribute("v:width", width);
            }
        }

        if (positionned) {
            cssWriter.writePosition(ICssWriter.ABSOLUTE);
        }

        if (needLayout > 0) {
            if (needInitLayout()) {
                needLayout |= 0x100;
            }

            writer.writeAttribute("v:layout", needLayout);

            writer.getJavaScriptEnableMode().enableOnLayout();
        }

    }

    protected void writeCustomCss(IHtmlWriter writer, ICssWriter cssWriter) {
    }

    public static final String getSize(String size) {
        if (size == null) {
            return size;
        }

        int len = size.length();
        if (len < 1) {
            return size;
        }

        if (Character.isDigit(size.charAt(len - 1)) == false) {
            return size;
        }

        return size + "px";
    }

    public static final String computeSizeInPixel(String size, int parentWidth,
            int delta) {
        int ssize = computeSize(size, parentWidth, delta);
        if (ssize < 0) {
            return null;
        }

        return ssize + "px";
    }

    public static final int computeSize(String size, int parentWidth, int delta) {
        int v = getPixelSize(size, parentWidth);
        if (v < 0) {
            return v;
        }

        return v + delta;
    }

    public static final int getPixelSize(String size, int parentWidth) {
        if (size == null) {
            return -1;
        }

        size = size.trim();

        int len = size.length();
        if (len < 1) {
            return -1;
        }

        boolean unitPX = false;
        for (; Character.isDigit(size.charAt(len - 1)) == false;) {
            if (size.toLowerCase().endsWith("px") == false) {
                return -1;
            }

            if (unitPX) {
                throw new FacesException("More one unit for size '" + size
                        + "' ?");
            }

            unitPX = true;

            size = size.substring(0, size.length() - 2).trim();

            len = size.length();
            if (len < 1) {
                throw new FacesException("Bad format of size '" + size
                        + "' ? (only unit)");
            }
        }

        try {
            return Double.valueOf(size).intValue();
        } catch (NumberFormatException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can not parse int '" + size + "'.", ex);
            }
            return -1;
        }
    }

    @SuppressWarnings("unused")
    protected static String normalizeMarginValue(String value) {
        value = value.trim();

        if (Constants.NORMALIZE_STYLE_MARGINS == false) {
            if (value.length() < 1) {
                return null;
            }

            return value;
        }

        char chs[] = value.toCharArray();

        int i = 0;
        // Recherche le debut !
        for (; i < chs.length; i++) {
            char c = chs[i];
            if (Character.isDigit(c) || c == '-' || c == '.') {
                break;
            }
        }
        if (i == chs.length) {
            return null;
        }

        boolean negative = false;
        char c = chs[i];
        if (c == '-') {
            i++;
            negative = true;
        }

        double v = 0;
        int decimal = -1;
        // Recherche la fin
        for (; i < chs.length; i++) {
            c = chs[i];

            if (c == '.') {
                if (decimal >= 0) {
                    throw new FacesException("Bad margin unit '" + value + "'.");
                }
                decimal = 0;
                continue;
            }

            if (Character.isDigit(c) == false) {
                break;
            }

            if (decimal >= 0) {
                decimal++;
            }

            v = v * 10 + (c - '0');
        }

        if (decimal > 0) {
            v /= Math.pow(10, decimal);
        }

        // Passe des espaces ....
        for (; i < chs.length; i++) {
            c = chs[i];
            if (Character.isWhitespace(c) == false) {
                break;
            }
        }

        if (v == 0.0) {
            if (i < chs.length) {
                c = chs[i];

                if (Character.isLetter(c) == false && c != '%') {
                    throw new FacesException("Bad unit for margin '" + value
                            + "'.");
                }
            }

            String margin = "0";
            if (LOG.isDebugEnabled()) {
                LOG.debug("Normalize margins original='" + value
                        + "' normalized='" + margin + "'.");
            }

            return margin;
        }

        if (negative) {
            v = -v;
        }

        // La fin !
        String unit;
        if (i == chs.length) {
            // Pas d'unité, on en ajoute une !
            unit = DEFAULT_MARGIN_UNIT;

        } else {
            unit = value.substring(i).trim();
            if (unit.length() < 1) {
                unit = DEFAULT_MARGIN_UNIT;
            }
        }

        String margin;
        if (decimal < 1) {
            margin = Math.floor(v) + DEFAULT_MARGIN_UNIT;
        } else {
            margin = v + DEFAULT_MARGIN_UNIT;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Normalize margins original='" + value + "' normalized='"
                    + margin + "'.");
        }

        return margin;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return true;
    }

    protected final String computeBlankImageURL(IHtmlWriter writer) {

        IHtmlRenderContext htmlRenderContext = writer
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        return htmlRenderContext.getHtmlProcessContext().getStyleSheetURI(
                AbstractCssRenderer.BLANK_IMAGE_URL, true);
    }

    public void declare(INamespaceConfiguration nameSpaceProperties) {
        super.declare(nameSpaceProperties);

        nameSpaceProperties.addAttributes(null, new String[] { "styleClass",
                "hiddenMode" });
    }
}