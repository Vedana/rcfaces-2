/*
 * $Id: AbstractHtmlBorderRenderer.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.border;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;

import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.border.AbstractBorderRenderer;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.INamespaceConfiguration;
import org.rcfaces.renderkit.html.internal.ns.INamespaceContributor;
import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
@XhtmlNSAttributes({ "className" })
public abstract class AbstractHtmlBorderRenderer extends AbstractBorderRenderer
        implements IHtmlBorderRenderer, INamespaceContributor {

    public static final String TD_TEXT = "_ctext";

    public static final String TD_IMAGE = "_cimage";

    public static final String SOUTH_FACET = "_sfacet";

    public static final String NORTH_FACET = "_nfacet";

    protected static final String DISABLED_SUFFIX = "_disabled";

    protected static final String SELECTED_SUFFIX = "_selected";

    protected static final String BORDER_NW = "_1";

    protected static final String BORDER_N = "_2";

    protected static final String BORDER_NE = "_3";

    protected static final String BORDER_E = "_4";

    protected static final String BORDER_SE = "_5";

    protected static final String BORDER_S = "_6";

    protected static final String BORDER_SW = "_7";

    protected static final String BORDER_W = "_8";

    protected static final String BORDER_BLANK_IMAGEURL = "border/blank.gif";

    protected static final String BORDER_BLANK_IMAGEURL_PROPERTY = "camelia.borderWriter.blank.imageURL";

    protected static final String MARKER_IMAGEURL = "blank.gif";

    private static final String BORDER_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "border";

    protected String width;

    protected String height;

    protected int horizontalSpan;

    protected int verticalSpan;

    protected boolean writeTopBorder = false;

    protected int skipVerticalRow = 0;

    protected String blankImageURL;

    private boolean selected;

    private boolean disabled;

    protected boolean noTable = false;

    protected ICssStyleClasses cssStyleClasses;

    public void initialize(IHtmlWriter writer,
            ICssStyleClasses cssStyleClasses, String width, String height,
            int horizontalSpan, int verticalSpan, boolean disabled,
            boolean selected) throws WriterException {
        this.width = width;
        this.height = height;
        this.horizontalSpan = horizontalSpan;
        this.verticalSpan = verticalSpan;
        this.selected = selected;
        this.disabled = disabled;
        this.cssStyleClasses = cssStyleClasses;
    }

    public IHtmlWriter startRow(IHtmlWriter writer) throws WriterException {
        if (noTable) {
            return writer;
        }

        verifyTopBorder(writer);

        writer.startElement(IHtmlWriter.TR);
        if (hasBorder() == false) {
            return writer;
        }

        if (skipVerticalRow > 0) {
            return writer;
        }

        return writeCellBorderWest(writer);
    }

    protected final IHtmlWriter verifyTopBorder(IHtmlWriter writer)
            throws WriterException {
        if (this.writeTopBorder == false) {
            return writer;
        }

        this.writeTopBorder = false;

        return writeTopBorder(writer);
    }

    protected IHtmlWriter writeTopBorder(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TR);
        writer.writeHeight(getNorthBorderHeight());

        writeCellBorderNorthWest(writer);

        writeCellBorderNorth(writer);

        writeCellBorderNorthEast(writer);

        writer.endElement(IHtmlWriter.TR);

        return writer;
    }

    public IHtmlWriter endRow(IHtmlWriter writer) throws WriterException {
        if (noTable) {
            return writer;
        }

        if (hasBorder()) {
            if (skipVerticalRow < 1) {
                writeCellBorderEast(writer);

            } else {
                skipVerticalRow--;
            }
        }

        writer.endElement(IHtmlWriter.TR);

        return writer;
    }

    public IHtmlWriter startComposite(IHtmlWriter writer)
            throws WriterException {
        if (noTable) {
            return writer;
        }

        writer.startElement(IHtmlWriter.TABLE);
        writer.writeId(writer.getComponentRenderContext()
                .getComponentClientId() + BORDER_ID_SUFFIX);

        String className = getClassName();
        if (className != null) {
            String tableClassName = getTableClassName(writer, disabled,
                    selected);

            writer.writeClass(tableClassName);

            if (tableClassName != className) {
                writer.writeAttributeNS("className", className);
            }
        }
        writer.writeCellPadding(0);
        writer.writeCellSpacing(0);
        writer.writeRole(IAccessibilityRoles.PRESENTATION);

        if (width != null || height != null) {
            ICssWriter cssWriter = writer.writeStyle(64);

            if (width != null) {
                if (onlyDigit(width)) {
                    width += "px";
                }
                cssWriter.writeWidth(width);
            }
            if (height != null) {
                if (onlyDigit(height)) {
                    height += "px";
                }
                cssWriter.writeHeight(height);
            }
        }

        this.writeTopBorder = hasBorder();

        return writer;
    }

    private static boolean onlyDigit(String txt) {
        char cs[] = txt.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];

            if (Character.isDigit(c) == false) {
                return false;
            }
        }

        return true;
    }

    protected String getBlankImageURL(IHtmlWriter writer) {
        if (blankImageURL != null) {
            return blankImageURL;
        }

        blankImageURL = computeBlankImageURL(writer);

        return blankImageURL;
    }

    protected String computeBlankImageURL(IHtmlWriter writer) {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext) componentRenderContext
                .getRenderContext();

        return htmlRenderContext.getHtmlProcessContext().getStyleSheetURI(
                BORDER_BLANK_IMAGEURL, true);
    }

    protected IHtmlWriter writeCellBorderNorthWest(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeWidth(getWestBorderWidth());
        writer.writeClass(getWestBorderClassName(writer));

        writer.startElement(IHtmlWriter.IMG);
        writer.writeWidth(getWestBorderWidth());
        writer.writeHeight(getNorthBorderHeight());
        writer.writeSrc(getBlankImageURL(writer));

        writeAlternateImage(writer);

        writer.endElement(IHtmlWriter.IMG);

        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected void writeAlternateImage(IHtmlWriter writer) {

    }

    protected String getWestBorderClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_NW;
    }

    public int getWestBorderWidth() {
        return 4;
    }

    public int getEastBorderWidth() {
        return 4;
    }

    public int getNorthBorderHeight() {
        return 4;
    }

    public int getSouthBorderHeight() {
        return 4;
    }

    protected IHtmlWriter writeCellBorderNorthEast(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeWidth(getEastBorderWidth());
        writer.writeClass(getEastBorderClassName(writer));

        writer.startElement(IHtmlWriter.IMG);
        writer.writeWidth(getEastBorderWidth());
        writer.writeHeight(getNorthBorderHeight());
        writer.writeSrc(getBlankImageURL(writer));
        writeAlternateImage(writer);

        writer.endElement(IHtmlWriter.IMG);

        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getEastBorderClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_NE;
    }

    protected IHtmlWriter writeCellBorderSouthWest(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getSouthBorderClassName(writer));
        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getSouthBorderClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_SW;
    }

    protected IHtmlWriter writeCellBorderSouthEast(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getSouthEastBorderClassName(writer));
        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getSouthEastBorderClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_SE;
    }

    protected IHtmlWriter writeCellBorderWest(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getBorderWestClassName(writer));

        if (verticalSpan > 1) {
            writer.writeRowSpan(verticalSpan);
        }
        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getBorderWestClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_W;
    }

    protected IHtmlWriter writeCellBorderEast(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getBorderEastClassName(writer));

        if (verticalSpan > 1) {
            writer.writeRowSpan(verticalSpan);
            skipVerticalRow = verticalSpan - 1;
        }

        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getBorderEastClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_E;
    }

    protected IHtmlWriter writeCellBorderSouth(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getBorderSouthClassName(writer));

        if (horizontalSpan > 1) {
            writer.writeColSpan(horizontalSpan);
        }

        writeSouthFacet(writer);

        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected void writeSouthFacet(IHtmlWriter writer) throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        UIComponent southFacet = component.getFacet("south");
        if (southFacet == null || southFacet.isRendered() == false) {
            return;
        }

        writer.startElement(IHtmlWriter.DIV);
        writer.writeClass(getSouthFacetClassName(writer));

        ComponentTools.encodeRecursive(writer.getComponentRenderContext()
                .getFacesContext(), southFacet);

        writer.endElement(IHtmlWriter.DIV);
    }

    protected void writeNorthFacet(IHtmlWriter writer) throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        UIComponent southFacet = component.getFacet("north");
        if (southFacet == null || southFacet.isRendered() == false) {
            return;
        }

        writer.startElement(IHtmlWriter.DIV);
        writer.writeClass(getNorthFacetClassName(writer));

        ComponentTools.encodeRecursive(writer.getComponentRenderContext()
                .getFacesContext(), southFacet);

        writer.endElement(IHtmlWriter.DIV);
    }

    protected String getBorderSouthClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_S;
    }

    protected String getSouthFacetClassName(IHtmlWriter writer) {
        return getClassName() + SOUTH_FACET;
    }

    protected String getNorthFacetClassName(IHtmlWriter writer) {
        return getClassName() + NORTH_FACET;
    }

    protected IHtmlWriter writeCellBorderNorth(IHtmlWriter writer)
            throws WriterException {
        writer.startElement(IHtmlWriter.TD);
        writer.writeClass(getBorderNorthClassName(writer));
        if (horizontalSpan > 1) {
            writer.writeColSpan(horizontalSpan);
        }
        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected String getBorderNorthClassName(IHtmlWriter writer) {
        return getClassName() + BORDER_N;
    }

    public IHtmlWriter endComposite(IHtmlWriter writer) throws WriterException {
        if (noTable) {
            return writer;
        }

        if (hasBorder()) {
            verifyTopBorder(writer);

            writer.startElement(IHtmlWriter.TR);
            writer.writeHeight(getSouthBorderHeight());

            writeCellBorderSouthWest(writer);

            writeCellBorderSouth(writer);

            writeCellBorderSouthEast(writer);

            writer.endElement(IHtmlWriter.TR);
        }

        writer.endElement(IHtmlWriter.TABLE);

        return writer;
    }

    public IHtmlWriter startChild(IHtmlWriter writer, String classSuffix)
            throws WriterException {
        return startChild(writer, classSuffix, null, null, null, null, 1, 1);
    }

    public IHtmlWriter startChild(IHtmlWriter writer, String classSuffix,
            String halign, String valign, String width, String height,
            int colspan, int rowspan) throws WriterException {
        if (noTable) {
            return writer;
        }

        writer.startElement(IHtmlWriter.TD);

        StringAppender className = new StringAppender(128);

        String borderClassName = getClassName();
        if (borderClassName != null) {
            className.append(borderClassName);
            if (classSuffix != null) {
                className.append(classSuffix);
            }
        }

        String componentClassName = cssStyleClasses
                .getSuffixedMainStyleClass(classSuffix);

        if (componentClassName != null) {
            if (className.length() > 0) {
                className.append(' ');
            }
            className.append(componentClassName);
        }

        if (className.length() > 0) {
            writer.writeClass(className.toString());
        }

        if (halign != null) {
            writer.writeAlign(halign);
        }
        if (valign != null) {
            writer.writeVAlign(valign);
        }
        if (width != null) {
            writer.writeWidth(width);
        }
        if (height != null) {
            writer.writeHeight(height);
        }
        if (colspan > 1) {
            writer.writeColSpan(colspan);
        }
        if (rowspan > 1) {
            writer.writeRowSpan(rowspan);
        }

        return writer;
    }

    public IHtmlWriter endChild(IHtmlWriter writer) throws WriterException {
        if (noTable) {
            return writer;
        }

        writer.endElement(IHtmlWriter.TD);

        return writer;
    }

    protected abstract boolean hasBorder();

    protected abstract String getClassName();

    protected String getTableClassName(IHtmlWriter htmlWriter,
            boolean disabled, boolean selected) {
        String tableClassName = getClassName();

        if (disabled) {
            tableClassName += DISABLED_SUFFIX;

        } else if (selected) {
            tableClassName += SELECTED_SUFFIX;
        }

        return tableClassName;
    }

    public void writeComboImage(IHtmlWriter writer, String componentClassName)
            throws WriterException {

        writer.startElement(IHtmlWriter.IMG);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();
        IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext) componentRenderContext
                .getRenderContext();

        String imageURL = htmlRenderContext.getHtmlProcessContext()
                .getStyleSheetURI(MARKER_IMAGEURL, true);
        writer.writeClass(getMarkerClassName(writer, componentClassName));
        writer.writeSrc(imageURL);
        writer.writeWidth(5);
        writer.writeHeight(3);
        writer.writeVAlign(getComboImageVerticalAlign(writer));

        writer.endElement(IHtmlWriter.IMG);
    }

    private String getMarkerClassName(IHtmlWriter writer,
            String componentClassName) {
        return componentClassName + "_marker";
    }

    protected String getComboImageVerticalAlign(IHtmlWriter writer) {
        return "center";
    }

    public void declare(INamespaceConfiguration nameSpaceProperties) {

        nameSpaceProperties.addAttributes(null, new String[] { "className" });

    }

}