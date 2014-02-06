/*
 * $Id: RadioButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.RadioButtonComponent;
import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;
import org.rcfaces.core.component.capability.IRadioValueCapability;
import org.rcfaces.core.component.capability.ISelectedCapability;
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
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRequestContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.ISubInputClientIdRenderer;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "required", "container" })
public class RadioButtonRenderer extends AbstractInputRenderer implements
        ISubInputClientIdRenderer {

    protected static final String INPUT_STYLECLASS_SUFFIX = "_input";

    protected static final String TEXT_STYLECLASS_SUFFIX = "_text";

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

        RadioButtonComponent radioButton = (RadioButtonComponent) componentRenderContext
                .getComponent();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        htmlWriter.startElement(IHtmlWriter.SPAN);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);
        writeValidatorParameters(htmlWriter, radioButton);

        if (radioButton.isRequired()) {
            htmlWriter.writeAttributeNS("required", true);

            htmlWriter.writeAriaRequired(true);

            htmlWriter.getJavaScriptEnableMode().enableOnSubmit();
        }

        String buttonId = componentRenderContext.getComponentClientId();

        String labelClassName = getLabelClassName(htmlWriter);

        String inputClassName = getInputClassName(htmlWriter);

        int horizontalTextPosition = radioButton.getTextPosition(facesContext);
        if (horizontalTextPosition == 0) {
            horizontalTextPosition = IHorizontalTextPositionCapability.DEFAULT_POSITION;
        }

        if (horizontalTextPosition == IHorizontalTextPositionCapability.LEFT_POSITION) {
            writeLabel(htmlWriter, radioButton, labelClassName, buttonId);

            writeInput(htmlWriter, radioButton, inputClassName, buttonId);

        } else {
            writeInput(htmlWriter, radioButton, inputClassName, buttonId);

            writeLabel(htmlWriter, radioButton, labelClassName, buttonId);
        }

        htmlWriter.endElement(IHtmlWriter.SPAN);

        htmlWriter.getJavaScriptEnableMode().enableOnFocus();

        if (radioButton.isRequired()) {
            htmlWriter.getJavaScriptEnableMode().enableOnSubmit();
        }
    }

    protected String getInputClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + INPUT_STYLECLASS_SUFFIX;
    }

    protected String getLabelClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + TEXT_STYLECLASS_SUFFIX;
    }

    protected IHtmlWriter writeInput(IHtmlWriter htmlWriter,
            RadioButtonComponent radioButtonComponent, String className,
            String componentClientId) throws WriterException {

        String inputId = componentClientId + INPUT_ID_SUFFIX;

        htmlWriter.startElement(IHtmlWriter.INPUT);
        htmlWriter.writeId(inputId);
        writeInputAttributes(htmlWriter, inputId);
        writeChecked(htmlWriter, radioButtonComponent);

        htmlWriter.addSubFocusableComponent(inputId);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();
        String svalue = getValueAsText(facesContext, radioButtonComponent);
        if (svalue != null) {
            htmlWriter.writeValue(svalue);
        }

        htmlWriter.writeClass(className);

        /*
         * On se base sur le separator ! if (htmlWriter.isJavaScriptEnabled() ==
         * false) { // Pour le FOCUS, pour retrouver le composant parent !
         * htmlWriter.writeAttributeNS("container", componentClientId); }
         */

        String accessKey = radioButtonComponent.getAccessKey(facesContext);
        if (accessKey != null) {
            htmlWriter.writeAccessKey(accessKey);
        }

        writeTabIndex(htmlWriter, radioButtonComponent);

        htmlWriter.endElement(IHtmlWriter.INPUT);

        // if (Constants.KEEP_DISABLED_STATE) {
        if (htmlWriter.getJavaScriptEnableMode().isOnInitEnabled() == false) {
            if (radioButtonComponent.isDisabled(facesContext)
                    && isChecked(htmlWriter, radioButtonComponent)) {
                htmlWriter.startElement(IHtmlWriter.INPUT);
                htmlWriter.writeType(IHtmlWriter.HIDDEN_INPUT_TYPE);
                htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);

                String name = htmlWriter.getComponentRenderContext()
                        .getComponentClientId() + "::value";
                htmlWriter.writeName(name);

                htmlWriter.writeValue(svalue);

                htmlWriter.endElement(IHtmlWriter.INPUT);
            }
            // }
        }

        return htmlWriter;
    }

    protected IHtmlWriter writeUserInputAttributes(IHtmlWriter writer,
            UIComponent component) throws WriterException {
        return writer;
    }

    protected IHtmlWriter writeLabel(IHtmlWriter htmlWriter,
            RadioButtonComponent button, String className,
            String componentClientId) throws WriterException {
        htmlWriter.startElement(IHtmlWriter.LABEL);

        htmlWriter.writeId(componentClientId + TEXT_ID_SUFFIX);
        htmlWriter.writeClass(className);
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

    protected void decode(IRequestContext context, UIComponent element,
            IComponentData componentData) {

        RadioButtonComponent radioButton = (RadioButtonComponent) element;

        super.decode(context, element, componentData);

        parseSelectedProperty((IHtmlRequestContext) context, radioButton,
                componentData);
    }

    protected void parseSelectedProperty(IHtmlRequestContext requestContext,
            RadioButtonComponent radioButton, IComponentData clientData) {

        String gb = radioButton.getGroupName();
        if (gb == null) {
            return;
        }

        FacesContext facesContext = requestContext.getFacesContext();

        gb = HtmlTools.computeGroupName(requestContext.getHtmlProcessContext(),
                radioButton, gb);

        Object submittedValue = null;
        boolean selected = false;

        String svalue = clientData.getStringProperty("selected");
        if (svalue == null) {
            svalue = clientData.getParameter(gb);

            if (svalue == null) {
                // On est peut etre en CollectionMode
                String name = radioButton.getClientId(facesContext) + "::value";
                svalue = clientData.getParameter(name);
            }

            if (svalue == null) {
                // Pas de changement d'états.
                return;
            }
        }

        // selection
        if (svalue != null) {
            submittedValue = getConvertedValue(facesContext, radioButton,
                    svalue);
        }

        // Submitted value contient la valeur pour LE GROUPE

        Object radioValue = radioButton.getRadioValue(facesContext);
        if (radioValue != null) {
            selected = (radioValue.equals(submittedValue)); // C'est la
            // value de
            // notre bouton

        } else if (svalue != null) {
            String value = getValueAsText(facesContext, radioButton);

            selected = (svalue.equals(value));
        }

        if (radioButton.isValueLocked(facesContext) == false) {
            radioButton.setSubmittedExternalValue(submittedValue);
        }

        if (radioButton.isSelected(facesContext) != selected) {
            radioButton.setSelected(selected);

            radioButton.queueEvent(new PropertyChangeEvent(radioButton,
                    Properties.SELECTED, Boolean.valueOf(selected == false),
                    Boolean.valueOf(selected)));
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.RADIO_BUTTON;
    }

    protected String getInputType(UIComponent component) {
        return IHtmlWriter.RADIO_INPUT_TYPE;
    }

    protected String getInputName(
            IHtmlComponentRenderContext componentRenderContext, String id) {
        RadioButtonComponent radioButtonComponent = (RadioButtonComponent) componentRenderContext
                .getComponent();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        String groupName = radioButtonComponent.getGroupName(facesContext);
        if (groupName == null) {
            throw new FacesException("Group name of radio '" + id
                    + "' is NULL !");
        }

        groupName = HtmlTools.computeGroupName(componentRenderContext
                .getHtmlRenderContext().getHtmlProcessContext(),
                radioButtonComponent, groupName);

        return groupName;
    }

    protected String getValueAsText(FacesContext facesContext,
            RadioButtonComponent component) {
        Object value = component.getRadioValue();

        String v = convertValue(facesContext, component, value);
        if (v != null) {
            return v;
        }

        return getUndefinedValue(facesContext, component);
    }

    private String getUndefinedValue(FacesContext facesContext,
            RadioButtonComponent component) {
        return component.getClientId(facesContext);
    }

    protected boolean isChecked(IHtmlWriter writer,
            IRadioValueCapability radioValueCapability) {

        Object radioValue = radioValueCapability.getRadioValue();
        if (radioValue == null) {
            return ((ISelectedCapability) radioValueCapability).isSelected();
        }

        ValueHolder valueHolder = (ValueHolder) radioValueCapability;

        Object currentValue = valueHolder.getValue();
        return radioValue.equals(currentValue);
    }

    protected IHtmlWriter writeChecked(IHtmlWriter writer,
            ISelectedCapability selectedCapability) throws WriterException {
        if (isChecked(writer, (IRadioValueCapability) selectedCapability)) {
            writer.writeChecked();
        }

        return writer;
    }

    public String computeSubInputClientId(IRenderContext renderContext,
            UIComponent component, String clientId) {
        return clientId + INPUT_ID_SUFFIX;
    }
}