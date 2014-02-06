/*
 * $Id: BoxRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.BoxComponent;
import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.MenuComponent;
import org.rcfaces.core.component.capability.IAsyncDecodeModeCapability;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.component.iterator.IMenuIterator;
import org.rcfaces.core.internal.renderkit.IAsyncRenderer;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.designer.IDesignerEngine;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.SubMenuDecorator;
import org.rcfaces.renderkit.html.internal.ns.INamespaceConfiguration;
import org.rcfaces.renderkit.html.internal.util.TextTypeTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
public class BoxRenderer extends AbstractCssRenderer implements IAsyncRenderer {
    private static final String HTML_TYPE_PROPERTY = "org.rcfaces.renderkit.html.box.COMPONENT_TYPE";

    public final void encodeBegin(IComponentWriter writer)
            throws WriterException {
        // Final a cause du designer

        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        String type = getMainTagName(htmlWriter);
        if (type == null) {
            type = IHtmlWriter.DIV;
        }

        htmlWriter.getComponentRenderContext().setAttribute(HTML_TYPE_PROPERTY,
                type);

        htmlWriter.startElement(type);

        writeComponentAttributes(htmlWriter);

        designerBeginChildren(htmlWriter, IDesignerEngine.MAIN_BODY);
    }

    protected String getMainTagName(IHtmlWriter htmlWriter) {
        return TextTypeTools.getType(htmlWriter);
    }

    protected void writeComponentAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        IHtmlRenderContext htmlRenderContext = componentRenderContext
                .getHtmlRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        BoxComponent boxComponent = (BoxComponent) componentRenderContext
                .getComponent();

        String overStyleClass = boxComponent.getOverStyleClass(facesContext);
        if (overStyleClass != null) {
            htmlWriter.writeAttributeNS("overStyleClass", overStyleClass);

            htmlWriter.getJavaScriptEnableMode().enableOnOver();
        }

        int asyncRender = IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE;

        boolean hidden = Boolean.FALSE.equals(boxComponent.getVisibleState());

        if (hidden) {
            if (htmlRenderContext.isAsyncRenderEnable()) {
                asyncRender = htmlRenderContext
                        .getAsyncRenderMode(boxComponent);

                if (asyncRender != IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                    htmlWriter.writeAttributeNS("asyncRender", true);

                    if (boxComponent.getAsyncDecodeMode(componentRenderContext
                            .getFacesContext()) == IAsyncDecodeModeCapability.PARTIAL_ASYNC_DECODE_MODE) {
                        htmlWriter.writeAttributeNS("asyncDecode", true);
                    }

                    htmlRenderContext.pushInteractiveRenderComponent(
                            htmlWriter, null);
                }
            }
        }

        setAsyncRenderer(htmlWriter, boxComponent, asyncRender);
    }

    protected final void encodeEnd(IComponentWriter writer)
            throws WriterException {
        // Final a cause du designer

        designerEndChildren(writer, IDesignerEngine.MAIN_BODY);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;
        BoxComponent boxComponent = (BoxComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        String type = (String) htmlWriter.getComponentRenderContext()
                .getAttribute(HTML_TYPE_PROPERTY);

        htmlWriter.endElement(type);

        IMenuIterator menuIterator = boxComponent.listMenus();
        if (menuIterator.hasNext()) {
            htmlWriter.getJavaScriptEnableMode().enableOnInit();
        }
        super.encodeEnd(writer);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.BOX;
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        IComponentDecorator decorator = null;

        BoxComponent boxComponent = (BoxComponent) component;

        IMenuIterator menuIterator = boxComponent.listMenus();
        for (; menuIterator.hasNext();) {
            MenuComponent menuComponent = menuIterator.next();

            IComponentDecorator menuDecorator = new SubMenuDecorator(
                    menuComponent, menuComponent.getMenuId(), null,
                    menuComponent.isRemoveAllWhenShown(facesContext),
                    getItemImageWidth(menuComponent),
                    getItemImageHeight(menuComponent));

            if (decorator == null) {
                decorator = menuDecorator;
                continue;
            }

            menuDecorator.addChildDecorator(decorator);
            decorator = menuDecorator;
        }

        return decorator;
    }

    protected int getItemImageWidth(IMenuComponent menuComponent) {
        return -1;
    }

    protected int getItemImageHeight(IMenuComponent menuComponent) {
        return -1;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(writer, javaScriptRenderContext);

        // FacesContext facesContext =
        // writer.getComponentRenderContext().getFacesContext();

        BoxComponent boxComponent = (BoxComponent) writer
                .getComponentRenderContext().getComponent();
        IMenuIterator menuIterator = boxComponent.listMenus();

        if (menuIterator.hasNext()) {
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.BOX,
                    "menu");
        }
    }

    public void declare(INamespaceConfiguration nameSpaceProperties) {
        super.declare(nameSpaceProperties);

        nameSpaceProperties.addAttributes(null, new String[] {
                "overStyleClass", "asyncRender", "asyncDecode" });
    }
}