package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.internal.converter.ToolTipPositionConverter;
import org.rcfaces.core.internal.renderkit.IAsyncRenderer;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * 
 * @author jbmeslin@vedana.com
 * @author olivier.oeuillot@vedana.com
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:19:48 $
 * 
 */
public class ToolTipRenderer extends AbstractCssRenderer implements
        IAsyncRenderer {

    public void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeComponentAttributes(htmlWriter);

        htmlWriter.writeAttribute("role", "description");
        
        htmlWriter.writeAttribute("aria-relevant", "additions all");
        htmlWriter.writeAttribute("aria-atomic", true);
                
        htmlWriter.writeAttribute("aria-live", "polite");
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

        ToolTipComponent tooltipComponent = (ToolTipComponent) componentRenderContext
                .getComponent();

        String position = tooltipComponent.getPosition(facesContext);
        if (position != null && position.length() > 0) {
        	 int positionKey;
             try {
                 positionKey = Integer.parseInt(position);

             } catch (NumberFormatException ex) {
                 positionKey = ((Integer) ToolTipPositionConverter.SINGLETON
                         .getAsObject(facesContext, tooltipComponent, position))
                         .intValue();
             }

             htmlWriter.writeAttribute("v:position", positionKey);
        }

        String toolTipId = tooltipComponent.getToolTipId(facesContext);
        if (toolTipId != null && toolTipId.length() > 0) {
            htmlWriter.writeAttribute("v:toolTipId", toolTipId);
        }

        int asyncRender = IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE;

        if (htmlRenderContext.isAsyncRenderEnable()) {
            asyncRender = htmlRenderContext
                    .getAsyncRenderMode(tooltipComponent);

            if (asyncRender != IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                htmlWriter.writeAttribute("v:asyncRender", true);

                htmlRenderContext.pushInteractiveRenderComponent(htmlWriter,
                        null);
            }
        }

        setAsyncRenderer(htmlWriter, tooltipComponent, asyncRender);

        if (htmlRenderContext
                .containsAttribute(ToolTipManagerRenderer.TOOLTIP_MANAGER_DEFINED_PROPERTY) == false) {
            htmlWriter.enableJavaScript();
        }
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;
        // ToolTipComponent tooltipComponent = (ToolTipComponent)
        // htmlWriter.getComponentRenderContext().getComponent();

        htmlWriter.endElement(IHtmlWriter.DIV);

        super.encodeEnd(writer);
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        jsWriter.setIgnoreComponentInitialization();

        IHtmlRenderContext htmlRenderContext = jsWriter.getHtmlRenderContext();

        if (htmlRenderContext
                .containsAttribute(ToolTipManagerRenderer.TOOLTIP_MANAGER_DEFINED_PROPERTY) == false) {
            // Nous sommes en LAZY

            htmlRenderContext.setAttribute(
                    ToolTipManagerRenderer.TOOLTIP_MANAGER_DEFINED_PROPERTY,
                    Boolean.TRUE);
            jsWriter.writeCall("f_toolTipManager", "Get").writeln(");");
        }

    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(writer, javaScriptRenderContext);

        javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.TOOLTIP,
                "f_toolTipManager");
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TOOLTIP;
    }

    public static void render(IHtmlWriter htmlWriter, ToolTipComponent component)
            throws WriterException {
        FacesContext facesContext = htmlWriter.getHtmlComponentRenderContext()
                .getFacesContext();

        ComponentTools.encodeRecursive(facesContext, component);
    }
}
