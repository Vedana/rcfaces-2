/*
 * $Id: ViewDialogRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ViewDialogComponent;
import org.rcfaces.core.internal.codec.URLFormCodec;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "width", "height", "title", "closable", "viewURL",
        "parameter", "shellDecorator", "visible", "dialogPriority" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class ViewDialogRenderer extends AbstractJavaScriptRenderer {

    public ViewDialogRenderer() {
        super();
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();
        ViewDialogComponent viewDialogComponent = (ViewDialogComponent) componentRenderContext
                .getComponent();

        boolean designMode = componentRenderContext.getRenderContext()
                .getProcessContext().isDesignerMode();

        if (viewDialogComponent.isVisible(facesContext) && designMode == false) {
            htmlWriter.getJavaScriptEnableMode().enableOnInit();
        }

        htmlWriter.startElementNS(LAZY_INIT_TAG);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);

        String width = viewDialogComponent.getWidth(facesContext);
        if (width != null) {
            htmlWriter.writeAttributeNS("width", width);
        }

        String height = viewDialogComponent.getHeight(facesContext);
        if (height != null) {
            htmlWriter.writeAttributeNS("height", height);
        }

        String text = viewDialogComponent.getText(facesContext);
        if (text != null) {
            htmlWriter.writeAttributeNS("title", text);
        }

        boolean closable = viewDialogComponent.isClosable(facesContext);
        if (closable == true) {
            htmlWriter.writeAttributeNS("closable", closable);
        }

        String returnFocusClientId = viewDialogComponent
                .getReturnFocusClientId(facesContext);
        if (returnFocusClientId != null) {
            String forId = componentRenderContext.getRenderContext()
                    .computeBrotherComponentClientId(viewDialogComponent,
                            returnFocusClientId);
            if (forId != null) {
                htmlWriter.writeAttributeNS("returnFocusClientId", forId);
            }
        }

        String src = viewDialogComponent.getViewURL(facesContext);
        if (src != null) {
            IContentAccessor contentAccessor = ContentAccessorFactory
                    .createFromWebResource(facesContext, src,
                            IContentFamily.JSP);

            src = contentAccessor.resolveURL(facesContext, null, null);
            if (src != null) {
                htmlWriter.writeAttributeNS("viewURL", src);
            }
        }

        writeClientData(htmlWriter, viewDialogComponent);

        // TODO A refaire en décorator !
        List children = viewDialogComponent.getChildren();
        Map<String, String> values = null;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof UISelectItem) {
                UISelectItem selectItem = (UISelectItem) children.get(i);

                Object value = selectItem.getItemValue();
                if (value == null) {
                    continue;
                }

                if (values == null) {
                    values = new HashMap<String, String>(8);
                }
                values.put(selectItem.getItemLabel(), String.valueOf(value));
            }
        }

        if (values != null) {
            StringAppender datas = new StringAppender(values.size() * 64);
            for (Map.Entry<String, String> entry : values.entrySet()) {

                String key = entry.getKey();
                if (key == null || key.length() < 1) {
                    continue;
                }

                String value = entry.getValue();
                if (value == null) {
                    continue;
                }

                if (datas.length() > 0) {
                    datas.append(',');
                }

                appendData(datas, key, value);
            }

            if (datas.length() > 0) {
                htmlWriter.writeAttributeNS("parameter", datas.toString());
            }
        }

        String shellDecorator = viewDialogComponent
                .getShellDecoratorName(facesContext);
        if (shellDecorator != null) {
            htmlWriter.writeAttributeNS("shellDecorator", shellDecorator);
        }

        if (viewDialogComponent.isVisible(facesContext) == false) {
            htmlWriter.writeAttributeNS("visible", false);
        }

        if (viewDialogComponent.isDialogPrioritySetted()) {
            htmlWriter.writeAttributeNS("dialogPriority",
                    viewDialogComponent.getDialogPriority(facesContext));
        }
        htmlWriter.endElementNS(LAZY_INIT_TAG);

        declareLazyJavaScriptRenderer(htmlWriter);

        super.encodeEnd(htmlWriter);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.VIEW_DIALOG;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }

    private void appendData(StringAppender datas, String key, String value) {
        URLFormCodec.encode(datas, key);
        datas.append('=');
        URLFormCodec.encode(datas, value);
    }
}
