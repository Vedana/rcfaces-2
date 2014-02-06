/*
 * $Id: ImageButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.ITextCapability;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.AbstractImageButtonFamillyDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "value", "disabled", "readOnly" })
public class ImageButtonRenderer extends AbstractCssRenderer {

    public static final String IMAGE_BUTTON_WRITER = "camelia.writer.ImageButton";

    private static final String INTERNAL_VALUE_ATTRIBUTE = "camelia.internalValue";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_BUTTON;
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        if (hasComponenDecoratorSupport() == false) {
            encodeComponent(htmlWriter);
        }

        // Il faut activer le Javascript
        // car l'attribut SELECTED doit être envoyé a chaque requete du client
        // vers le serveur !
        // TODO ((IHtmlWriter) writer).getJavaScriptEnableMode().enableOnInit();

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        if (componentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode()) {
            if (htmlWriter.getJavaScriptEnableMode().isOnInitEnabled() == false) {

                String value = (String) componentRenderContext
                        .getAttribute(INTERNAL_VALUE_ATTRIBUTE);

                if (value != null) {
                    htmlWriter.startElement(IHtmlWriter.INPUT);
                    htmlWriter.writeType(IHtmlWriter.HIDDEN_INPUT_TYPE);
                    htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);

                    String name = componentRenderContext.getComponentClientId()
                            + "::value";
                    htmlWriter.writeName(name);

                    htmlWriter.writeValue(value);

                    htmlWriter.endElement(IHtmlWriter.INPUT);
                }
            }
        }

        htmlWriter.getJavaScriptEnableMode().enableOnOver();

        super.encodeEnd(writer);
    }

    protected void encodeComponent(IHtmlWriter writer) throws WriterException {
        throw new WriterException("Render is not implemented !", null, writer
                .getComponentRenderContext().getComponent());
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new ImageButtonDecorator((IImageButtonFamilly) component);
    }

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        if (component instanceof ITextCapability) {
            ITextCapability textCapability = (ITextCapability) component;
            String text = componentData.getStringProperty("text");
            if (text != null) {
                String old = textCapability.getText();
                if (text.equals(old) == false) {
                    textCapability.setText(text);

                    component.queueEvent(new PropertyChangeEvent(component,
                            Properties.TEXT, old, text));
                }
            }
        }
    }

    protected String getWAIRole() {
        return null; // IAccessibilityRoles.PRESENTATION;
    }

    protected IHtmlWriter writeUserInputAttributes(IHtmlWriter writer,
            UIComponent component) throws WriterException {
        return writer;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
     */
    protected class ImageButtonDecorator extends
            AbstractImageButtonFamillyDecorator {

        public ImageButtonDecorator(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected String getMainStyleClassName() {
            return ImageButtonRenderer.this.getMainStyleClassName();
        }

        protected void writeAttributes(ICssStyleClasses cssStyleClasses)
                throws WriterException {

            writeHtmlAttributes(writer);
            writeJavaScriptAttributes(writer);
            writeCssAttributes(writer, cssStyleClasses, ~CSS_FONT_MASK);
            writeFirstTooltipClientId(writer);

            FacesContext facesContext = writer.getComponentRenderContext()
                    .getFacesContext();
            encodeAttributes(facesContext);
        }

        protected void encodeAttributes(FacesContext facesContext)
                throws WriterException {
            String value = getInputValue(false);
            if (value != null) {
                writer.writeAttributeNS("value", value);

                writer.getComponentRenderContext().setAttribute(
                        INTERNAL_VALUE_ATTRIBUTE, value);
            }

            if (imageButtonFamilly.isDisabled(facesContext)) {
                writer.writeAttributeNS("disabled", true);
            }

            if (imageButtonFamilly.isReadOnly(facesContext)) {
                writer.writeAttributeNS("readOnly", true);
            }
        }
    }
}