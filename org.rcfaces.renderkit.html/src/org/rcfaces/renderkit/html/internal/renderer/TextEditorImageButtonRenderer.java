/*
 * $Id: TextEditorImageButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TextEditorImageButtonComponent;
import org.rcfaces.core.component.capability.ITextEditorButtonType;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "type", "for" })
public class TextEditorImageButtonRenderer extends ImageButtonRenderer {

    private static final int TEXT_EDITOR_IMAGE_BUTTON_WIDTH = 16;

    private static final int TEXT_EDITOR_IMAGE_BUTTON_HEIGHT = 16;

    private static final Map<String, String> IMAGE_URL_BY_TYPE = new HashMap<String, String>(
            24);
    static {
        IMAGE_URL_BY_TYPE
                .put(ITextEditorButtonType.BOLD, "textEditor/bold.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.ITALIC,
                "textEditor/italic.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.UNDERLINE,
                "textEditor/underline.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.STRIKE,
                "textEditor/strike.gif");

        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.SUB_SCRIPT,
                "textEditor/subScript.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.SUPER_SCRIPT,
                "textEditor/superScript.gif");

        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.JUSTIFY_LEFT,
                "textEditor/left.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.JUSTIFY_RIGHT,
                "textEditor/right.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.JUSTIFY_CENTER,
                "textEditor/center.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.JUSTIFY_FULL,
                "textEditor/justify.gif");

        IMAGE_URL_BY_TYPE
                .put(ITextEditorButtonType.UNDO, "textEditor/undo.gif");
        IMAGE_URL_BY_TYPE
                .put(ITextEditorButtonType.REDO, "textEditor/redo.gif");

        IMAGE_URL_BY_TYPE
                .put(ITextEditorButtonType.COPY, "textEditor/copy.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.CUT, "textEditor/cut.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.PASTE,
                "textEditor/paste.gif");

        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.INDENT,
                "textEditor/indent.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.OUTDENT,
                "textEditor/outdent.gif");

        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.ORDEREDLIST,
                "textEditor/orderedList.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.UNORDEREDLIST,
                "textEditor/unorderedList.gif");

        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.DECREASE_FONT_SIZE,
                "textEditor/decreasefontsize.gif");
        IMAGE_URL_BY_TYPE.put(ITextEditorButtonType.INCREASE_FONT_SIZE,
                "textEditor/increasefontsize.gif");
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TEXT_EDITOR_IMAGE_BUTTON;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        // IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        FacesContext facesContext = componentRenderContext.getFacesContext();
        TextEditorImageButtonComponent textEditorImageButtonComponent = (TextEditorImageButtonComponent) componentRenderContext
                .getComponent();

        textEditorImageButtonComponent.setDisabled(true);

        String type = textEditorImageButtonComponent.getType(facesContext);
        if (type == null) {
            throw new FacesException(
                    "Type attribute is required for an ImageTextEditorButton.");
        }

        type = type.toLowerCase().trim();

        if (IMAGE_URL_BY_TYPE.containsKey(type) == false) {
            throw new FacesException("Unsupported type '" + type
                    + "' for an ImageTextEditorButton.");
        }

        ((IHtmlWriter) writer).getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(writer);
    }

    protected int getTextEditorImageWidth(IHtmlWriter htmlWriter) {
        return TEXT_EDITOR_IMAGE_BUTTON_WIDTH;
    }

    protected int getTextEditorImageHeight(IHtmlWriter htmlWriter) {
        return TEXT_EDITOR_IMAGE_BUTTON_HEIGHT;
    }

    protected IContentAccessor getTextEditorImageAccessor(IHtmlWriter htmlWriter) {

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        String type = ((TextEditorImageButtonComponent) htmlWriter
                .getComponentRenderContext().getComponent())
                .getType(htmlRenderContext.getFacesContext());

        String imageURL = IMAGE_URL_BY_TYPE.get(type.toLowerCase());
        if (imageURL == null) {
            return null;
        }

        IContentAccessor contentAccessor = htmlRenderContext
                .getHtmlProcessContext().getStyleSheetContentAccessor(imageURL,
                        IContentFamily.IMAGE);

        return contentAccessor;
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new TextEditorImageButtonDecorator(
                (IImageButtonFamilly) component);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
     */
    protected class TextEditorImageButtonDecorator extends ImageButtonDecorator {

        private IContentAccessor imageAccessor;

        public TextEditorImageButtonDecorator(
                IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected void encodeAttributes(FacesContext facesContext)
                throws WriterException {
            super.encodeAttributes(facesContext);

            TextEditorImageButtonComponent button = (TextEditorImageButtonComponent) imageButtonFamilly;

            String type = button.getType(facesContext);
            if (type != null) {
                writer.writeAttributeNS("type", type);
            }

            String forProperty = button.getFor(facesContext);
            if (forProperty != null) {
                writer.writeAttributeNS("for", forProperty);
            }
        }

        protected IContentAccessor getImageAccessor(IHtmlWriter htmlWriter) {
            if (imageAccessor != null) {
                return imageAccessor;
            }

            imageAccessor = super.getImageAccessor(htmlWriter);
            if (imageAccessor != null) {
                return imageAccessor;
            }

            imageAccessor = getTextEditorImageAccessor(htmlWriter);

            imageButtonFamilly
                    .setImageWidth(getTextEditorImageWidth(htmlWriter));
            imageButtonFamilly
                    .setImageHeight(getTextEditorImageHeight(htmlWriter));

            return imageAccessor;
        }

        protected IContentAccessor getTextEditorImageAccessor(
                IHtmlWriter htmlWriter) {
            return TextEditorImageButtonRenderer.this
                    .getTextEditorImageAccessor(htmlWriter);
        }

        protected int getTextEditorImageHeight(IHtmlWriter htmlWriter) {
            return TextEditorImageButtonRenderer.this
                    .getTextEditorImageHeight(htmlWriter);
        }

        protected int getTextEditorImageWidth(IHtmlWriter htmlWriter) {
            return TextEditorImageButtonRenderer.this
                    .getTextEditorImageWidth(htmlWriter);
        }

        protected boolean useImageFilterIfNecessery() {
            return true;
        }
    }

}
