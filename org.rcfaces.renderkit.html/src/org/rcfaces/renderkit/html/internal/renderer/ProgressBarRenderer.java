/*
 * $Id: ProgressBarRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ProgressBarComponent;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "min", "max", "value" })
public class ProgressBarRenderer extends AbstractCssRenderer {

    private static final int BORDER_LEFT_WIDTH = 4;

    private static final int BORDER_RIGHT_WIDTH = 4;

    private static final int BORDER_HEIGHT = 16;

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.PROGRESS_BAR;
    }

    // On le met sur le end, car des clientsDatas ... et autres peuvent survenir
    // ...
    public void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeComponent(htmlWriter);

        super.encodeEnd(htmlWriter);
    }

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        ProgressBarComponent progressBar = (ProgressBarComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        writeProgressBarAttributes(htmlWriter, progressBar);

        htmlWriter.endElement(IHtmlWriter.DIV);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        /*
         * 
         * IComponentRenderContext componentRenderContext = htmlWriter
         * .getComponentRenderContext();
         * 
         * IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext)
         * componentRenderContext .getRenderContext();
         * 
         * String blankImageURL = htmlRenderContext.getHtmlProcessContext()
         * .getStyleSheetURI(BLANK_IMAGE_URL, true);
         * 
         * htmlWriter.startElement(IHtmlWriter.TABLE);
         * 
         * htmlWriter.writeCellPadding(0); htmlWriter.writeCellSpacing(0);
         * 
         * htmlWriter.startElement(IHtmlWriter.COL);
         * htmlWriter.writeWidth(BORDER_LEFT_WIDTH);
         * htmlWriter.endElement(IHtmlWriter.COL);
         * 
         * htmlWriter.startElement(IHtmlWriter.COL); htmlWriter.writeWidth("*");
         * htmlWriter.endElement(IHtmlWriter.COL);
         * 
         * htmlWriter.startElement(IHtmlWriter.COL);
         * htmlWriter.writeWidth(BORDER_RIGHT_WIDTH);
         * htmlWriter.endElement(IHtmlWriter.COL);
         * 
         * htmlWriter.writeln();
         * 
         * htmlWriter.startElement(IHtmlWriter.TBODY);
         * 
         * htmlWriter.startElement(IHtmlWriter.TR);
         * 
         * htmlWriter.startElement(IHtmlWriter.TD);
         * htmlWriter.writeAttribute("class", getLeftCellClassName(htmlWriter));
         * 
         * htmlWriter.startElement(IHtmlWriter.IMG);
         * htmlWriter.writeWidth(BORDER_LEFT_WIDTH);
         * htmlWriter.writeHeight(BORDER_HEIGHT);
         * htmlWriter.writeSrc(blankImageURL);
         * htmlWriter.endElement(IHtmlWriter.IMG);
         * 
         * htmlWriter.endElement(IHtmlWriter.TD);
         * 
         * htmlWriter.startElement(IHtmlWriter.TD);
         * htmlWriter.writeClass(getMidCellClassName(htmlWriter));
         * htmlWriter.writeText(ISgmlWriter.NBSP);
         * htmlWriter.endElement(IHtmlWriter.TD);
         * 
         * htmlWriter.startElement(IHtmlWriter.TD);
         * htmlWriter.writeClass(getRightCellClassName(htmlWriter));
         * 
         * htmlWriter.startElement(IHtmlWriter.IMG);
         * htmlWriter.writeWidth(BORDER_RIGHT_WIDTH);
         * htmlWriter.writeHeight(BORDER_HEIGHT);
         * htmlWriter.writeSrc(blankImageURL);
         * htmlWriter.endElement(IHtmlWriter.IMG);
         * 
         * htmlWriter.endElement(IHtmlWriter.TD);
         * 
         * htmlWriter.endElement(IHtmlWriter.TR);
         * 
         * htmlWriter.endElement(IHtmlWriter.TBODY);
         * 
         * htmlWriter.endElement(IHtmlWriter.TABLE);
         */
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.PROGRESS_BAR;
    }

    /*
     * protected String getLeftCellClassName(IHtmlWriter htmlWriter) { return
     * getMainStyleClassName() + "_left"; }
     * 
     * protected String getRightCellClassName(IHtmlWriter htmlWriter) { return
     * getMainStyleClassName() + "_right"; }
     * 
     * protected String getMidCellClassName(IHtmlWriter htmlWriter) { return
     * getMainStyleClassName() + "_mid"; }
     */
    protected void writeProgressBarAttributes(IHtmlWriter htmlWriter,
            ProgressBarComponent progressBar) throws WriterException {

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        double minValue = progressBar.getMinimum(facesContext);
        if (minValue != 0.0) {
            htmlWriter.writeAttributeNS("min", Double.toString(minValue));
        }

        double maxValue = progressBar.getMaximum(facesContext);
        if (maxValue != 0.0) {
            htmlWriter.writeAttributeNS("max", Double.toString(maxValue));
        }

        Object value = progressBar.getValue();
        if (value instanceof String) {
            String svalue = (String) value;

            if (svalue.length() > 0 && Character.isDigit(svalue.charAt(0))) {
                value = new Double(svalue);
            }
        }
        if (value instanceof Number) {
            Number num = (Number) value;

            if (num.doubleValue() != 0.0) {
                htmlWriter.writeAttributeNS("value", num.toString());
            }
        }
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        ProgressBarComponent progressBar = (ProgressBarComponent) component;

        Number value = componentData.getNumberProperty("value");
        if (value != null) {
            progressBar.setSubmittedExternalValue(value);
        }

        Number min = componentData.getNumberProperty("min");
        if (min != null) {
            double old = progressBar.getMinimum(facesContext);

            if (min.doubleValue() != old) {
                progressBar.setMinimum(min.doubleValue());

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.MINIMUM, new Double(old), min));
            }
        }

        Number max = componentData.getNumberProperty("max");
        if (max != null) {
            double old = progressBar.getMaximum(facesContext);

            if (max.doubleValue() != old) {
                progressBar.setMaximum(max.doubleValue());

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.MAXIMUM, new Double(old), max));
            }
        }
    }
}