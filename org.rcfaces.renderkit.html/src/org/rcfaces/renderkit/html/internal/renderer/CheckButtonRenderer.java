/*
 * $Id: CheckButtonRenderer.java,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.CheckButtonComponent;
import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;
import org.rcfaces.core.component.capability.IRequiredCapability;
import org.rcfaces.core.component.capability.ITabIndexCapability;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.AbstractInputRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.ISubInputClientIdRenderer;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
@XhtmlNSAttributes({ "required", "tabIndex", "container" })
public class CheckButtonRenderer extends AbstractInputRenderer implements
        ISubInputClientIdRenderer {

    protected static final String DEFAULT_VALUE = "CHECKED";

    public static final String INPUT_STYLECLASS_SUFFIX = "_input";

    public static final String TEXT_STYLECLASS_SUFFIX = "_text";

    public static final String TEXT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "text";

    public static final String INPUT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "input";

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        CheckButtonComponent button = (CheckButtonComponent) componentRenderContext
                .getComponent();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        htmlWriter.startElement(IHtmlWriter.SPAN);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        /*
         * if (button.isDisabled(facesContext)) {
         * htmlWriter.writeAttribute("DISABLED"); }
         */

        if (button instanceof IRequiredCapability) {
            IRequiredCapability requiredCapability = (IRequiredCapability) button;

            if (requiredCapability.isRequired()) {
                htmlWriter.writeAttributeNS("required", true);

                htmlWriter.getJavaScriptEnableMode().enableOnSubmit();
            }
        }

        String buttonId = componentRenderContext.getComponentClientId();

        String className = getMainStyleClassName();

        int horizontalTextPosition = button.getTextPosition(facesContext);
        if (horizontalTextPosition == 0) {
            horizontalTextPosition = IHorizontalTextPositionCapability.DEFAULT_POSITION;
        }

        if (horizontalTextPosition == IHorizontalTextPositionCapability.LEFT_POSITION) {
            writeLabel(htmlWriter, button, className, buttonId);

            writeInput(htmlWriter, button, className, buttonId);

        } else {
            writeInput(htmlWriter, button, className, buttonId);

            writeLabel(htmlWriter, button, className, buttonId);
        }

        htmlWriter.endElement(IHtmlWriter.SPAN);

        htmlWriter.getJavaScriptEnableMode().enableOnFocus();
    }

    @Override
    protected IHtmlWriter writeTabIndex(IHtmlWriter htmlWriter,
            ITabIndexCapability tabIndexCapability) throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        CheckButtonComponent button = (CheckButtonComponent) componentRenderContext
                .getComponent();

        Integer index = tabIndexCapability.getTabIndex();
        if (index == null) {
            return htmlWriter;
        }

        int idx = index.intValue();

        if (button.isDisabled() == false) {
            // htmlWriter.writeTabIndex(idx);
        } else {
            htmlWriter.writeTabIndex(-1);
            htmlWriter.writeAttributeNS("tabIndex", idx);
        }

        return htmlWriter;
    }

    protected void writeInput(IHtmlWriter htmlWriter,
            CheckButtonComponent button, String className,
            String componentClientId) throws WriterException {

        String inputId = componentClientId + INPUT_ID_SUFFIX;

        htmlWriter.startElement(IHtmlWriter.INPUT);
        htmlWriter.writeId(inputId);
        writeInputAttributes(htmlWriter, inputId);

        Integer index = ((ITabIndexCapability) button).getTabIndex();

        if (button.isDisabled() == false && index != null) {
            int idx = index.intValue();
            htmlWriter.writeTabIndex(idx);
        }

        writeChecked(htmlWriter, button);

        htmlWriter.addSubFocusableComponent(inputId);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();
        String value = getValue(facesContext, button);
        if (value != null) {
            htmlWriter.writeValue(value);
        }

        htmlWriter.writeClass(className + INPUT_STYLECLASS_SUFFIX);

        /*
         * On se base sur le separator ! if (htmlWriter.isJavaScriptEnabled() ==
         * false) { // Pour le FOCUS, pour retrouver le composant parent !
         * htmlWriter.writeAttributeNS("container", componentClientId); }
         */

        String accessKey = button.getAccessKey(facesContext);
        if (accessKey != null) {
            htmlWriter.writeAccessKey(accessKey);
        }
        writeAlternateText(htmlWriter, button);

        htmlWriter.endElement(IHtmlWriter.INPUT);

        // if (Constants.KEEP_DISABLED_STATE) {
        if (htmlWriter.getJavaScriptEnableMode().isOnInitEnabled() == false) {
            if (button.isDisabled(facesContext)
                    && button.isSelected(facesContext)) {
                htmlWriter.startElement(IHtmlWriter.INPUT);
                htmlWriter.writeType(IHtmlWriter.HIDDEN_INPUT_TYPE);
                htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);

                String name = htmlWriter.getComponentRenderContext()
                        .getComponentClientId() + "::value";
                htmlWriter.writeName(name);

                htmlWriter.writeValue("CHECKED");

                htmlWriter.endElement(IHtmlWriter.INPUT);
            }
        }
        // }
    }

    protected IHtmlWriter writeLabel(IHtmlWriter htmlWriter,
            CheckButtonComponent button, String className,
            String componentClientId) throws WriterException {
        htmlWriter.startElement(IHtmlWriter.LABEL);
        designerEditableZone(htmlWriter, "text");

        htmlWriter.writeId(componentClientId + TEXT_ID_SUFFIX);
        htmlWriter.writeClass(className + TEXT_STYLECLASS_SUFFIX);
        writeTextDirection(htmlWriter, button);

        String inputId = componentClientId + INPUT_ID_SUFFIX;
        htmlWriter.writeFor(inputId);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        String text = button.getText(facesContext);
        if (text != null) {
            text = ParamUtils.formatMessage(button, text);
        }

        HtmlTools.writeSpanAccessKey(htmlWriter, button, text, true);

        htmlWriter.endElement(IHtmlWriter.LABEL);

        return htmlWriter;
    }

    protected String getValue(FacesContext facesContext,
            CheckButtonComponent component) {
        Object value = component.getValue();

        String svalue = convertValue(facesContext, component, value);

        if (svalue != null) {
            return svalue;
        }

        return DEFAULT_VALUE;
    }

    protected String getInputType(UIComponent component) {
        return IHtmlWriter.CHECKBOX_INPUT_TYPE;
    }

    protected void decode(IRequestContext context, UIComponent element,
            IComponentData componentData) {

        FacesContext facesContext = context.getFacesContext();

        CheckButtonComponent button = (CheckButtonComponent) element;

        super.decode(context, element, componentData);

        parseSelectedProperty(facesContext, button, componentData);
    }

    protected void parseSelectedProperty(FacesContext facesContext,
            CheckButtonComponent button, IComponentData clientData) {

        boolean selected = false;

        String selectedProperty = clientData.getStringProperty("selected");
        if (selectedProperty != null) {
            selected = (selectedProperty.length() > 0);

        } else {
            String values[] = clientData.getComponentParameters();

            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    selected = (values[i].length() > 0);

                    if (selected) {
                        break;
                    }
                }

            } else {
                // Pas d'état du composant ! (A cause du DISABLED)

                String name = button.getClientId(facesContext) + "::value";
                if (clientData.getParameter(name) != null) {
                    selected = true;
                }
            }
        }

        if (button.isSelected(facesContext) != selected
                && button.isValueLocked(facesContext) == false) {
            button.setSubmittedValue(Boolean.valueOf(selected));

            button.queueEvent(new PropertyChangeEvent(button,
                    Properties.SELECTED, Boolean.valueOf(selected == false),
                    Boolean.valueOf(selected)));
        }

    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.CHECK_BUTTON;
    }

    public String computeSubInputClientId(IRenderContext renderContext,
            UIComponent component, String clientId) {
        return clientId + INPUT_ID_SUFFIX;
    }

}