package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.MessageDialogComponent;
import org.rcfaces.core.component.capability.IHiddenModeCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.AbstractSelectItemsRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.MessageDialogDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;

/**
 * 
 * @author Fred Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "title", "text", "defaultValue", "width", "height",
        "waiRole", "dialogPriority", "styleClass", "lookId", "imageURL" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class MessageDialogRenderer extends AbstractSelectItemsRenderer {

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {
        return createMessageBoxDecorator(facesContext, component);
    }

    protected IComponentDecorator createMessageBoxDecorator(
            FacesContext facesContext, UIComponent component) {

        // if (((MessageDialogComponent) component).isVisible(facesContext)) {
        return new MessageDialogDecorator(component);
        // }
        // return null;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.MESSAGE_DIALOG;
    }

    /**
     * 
     * @return String the base css name for all the classes
     */
    protected String getCssClassBase(MessageDialogComponent component,
            FacesContext facesContext) {
        return null;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();
        MessageDialogComponent component = (MessageDialogComponent) componentRenderContext
                .getComponent();

        // Pas visible : rien à faire !
        if (!component.isVisible(facesContext)
                && component.getHiddenMode(facesContext) == IHiddenModeCapability.SERVER_HIDDEN_MODE) {
            super.encodeEnd(writer);
            return;
        }

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        if (component.isVisible(facesContext)) {
            htmlWriter.getJavaScriptEnableMode().enableOnInit();
        }

        htmlWriter.startElementNS(LAZY_INIT_TAG);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);

        String chaine = component.getTitle(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("title", chaine);
        }
        chaine = component.getText(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("text", chaine);
        }
        chaine = component.getDefaultValue(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("defaultValue", chaine);
        }
        chaine = component.getWidth(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("width", chaine);
        }
        chaine = component.getHeight(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("height", chaine);
        }
        chaine = component.getWaiRole(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("waiRole", chaine);
        }
        if (component.isDialogPrioritySetted()) {
            htmlWriter.writeAttributeNS("dialogPriority",
                    component.getDialogPriority(facesContext));
        }
        chaine = component.getStyleClass(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("styleClass", chaine);
        }
        chaine = component.getLookId(facesContext);
        if (chaine != null) {
            htmlWriter.writeAttributeNS("lookId", chaine);
        }
        if (component.isImageURLSetted()) {
            IContentAccessors contentAccessors = component
                    .getImageAccessors(facesContext);
            if (contentAccessors instanceof IImageAccessors) {
                IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;
                IContentAccessor imageAccessor = imageAccessors
                        .getImageAccessor();
                if (imageAccessor != null) {
                    String imageSrc = imageAccessor.resolveURL(facesContext,
                            null, null);
                    if (imageSrc != null) {
                        htmlWriter.writeURIAttributeNS("imageURL", imageSrc);
                    }
                }
            }
        }
        htmlWriter.endElementNS(LAZY_INIT_TAG);

        declareLazyJavaScriptRenderer(htmlWriter);

        super.encodeEnd(htmlWriter);

    }

}
