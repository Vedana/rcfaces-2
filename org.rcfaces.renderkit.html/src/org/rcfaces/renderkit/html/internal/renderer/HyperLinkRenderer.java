/*
 * $Id: HyperLinkRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.HyperLinkComponent;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "value" })
public class HyperLinkRenderer extends AbstractCssRenderer {

    public void encodeEnd(IComponentWriter writer) throws WriterException {
        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();

        HyperLinkComponent component = (HyperLinkComponent) writer
                .getComponentRenderContext().getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        boolean disabled = component.isDisabled(facesContext);
        boolean readOnly = component.isReadOnly(facesContext);
        if (disabled) {
            getCssStyleClasses(htmlWriter).addSuffix("_disabled");

        } else if (readOnly) {
            getCssStyleClasses(htmlWriter).addSuffix("_readOnly");
        }

        htmlWriter.startElement(IHtmlWriter.A);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        if (disabled) {
            htmlWriter.writeDisabled();
        }
        writeFirstTooltipClientId(htmlWriter);

        // Il faut le laisser pour le lazy FOCUS
        htmlWriter.writeHRef_JavascriptVoid0();

        Object value = getValue(component);
        if (value != null) {
            String convertedValue = convertValue(facesContext, component, value);

            if (convertedValue != null) {
                htmlWriter.writeAttributeNS("value", convertedValue);
            }
        }

        String text = component.getText(facesContext);
        if (text != null) {
            if (text != null) {
                text = ParamUtils.formatMessage(component, text);
            }
            htmlWriter.writeText(text);
        }

        htmlWriter.endElement(IHtmlWriter.A);

        htmlWriter.addSubFocusableComponent(htmlWriter
                .getComponentRenderContext().getComponentClientId());
        htmlWriter.getJavaScriptEnableMode().enableOnFocus();

        super.encodeEnd(writer);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * getJavaScriptClassName()
     */
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.HYPER_LINK;
    }

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }

    protected boolean useHtmlAccessKeyAttribute() {
        return true;
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        HyperLinkComponent hyperlinkComponent = (HyperLinkComponent) component;

        String text = componentData.getStringProperty("text");
        if (text != null) {
            String old = hyperlinkComponent.getText(facesContext);
            if (text.equals(old) == false) {
                hyperlinkComponent.setText(text);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.TEXT, old, text));
            }
        }

        Object value = componentData.getProperty("value");
        if (value != null) {
            value = getConvertedValue(facesContext, hyperlinkComponent, value);

            if (value != null) {
                Object old = hyperlinkComponent.getValue();
                if (value.equals(old) == false) {
                    hyperlinkComponent.setValue(value);

                    hyperlinkComponent.queueEvent(new PropertyChangeEvent(
                            hyperlinkComponent, Properties.VALUE, old, value));
                }
            }
        }
    }
}
