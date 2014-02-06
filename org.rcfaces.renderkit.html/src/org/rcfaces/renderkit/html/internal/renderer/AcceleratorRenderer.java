/*
 * $Id: AcceleratorRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.AcceleratorComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.KeyTools;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "for", "forItemValue", "character", "virtualKey",
        "keyFlags", "ignoreEditableComponent" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class AcceleratorRenderer extends AbstractJavaScriptRenderer {

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        AcceleratorComponent acceleratorComponent = (AcceleratorComponent) componentRenderContext
                .getComponent();

        String keyBinding = acceleratorComponent.getKeyBinding(facesContext);
        if (keyBinding == null || keyBinding.length() == 0) {
            return;
        }

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        if (htmlComponentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {

            htmlWriter.startElementNS(LAZY_INIT_TAG);
            writeHtmlAttributes(htmlWriter);
            writeJavaScriptAttributes(htmlWriter);

            String forComponent = acceleratorComponent.getFor(facesContext);
            if (forComponent != null) {
                htmlWriter.writeAttributeNS("for", forComponent);
            }

            String forItemValue = acceleratorComponent
                    .getForItemValue(facesContext);
            if (forItemValue != null) {
                htmlWriter.writeAttributeNS("forItemValue", forItemValue);
            }

            KeyTools.State state = KeyTools.parseKeyBinding(keyBinding);

            if (state.character > 0) {
                htmlWriter.writeAttributeNS("character",
                        String.valueOf(state.character));
            }

            if (state.virtualKey != null) {
                htmlWriter.writeAttributeNS("virtualKey",
                        state.virtualKey.intValue());
            }

            if (state.keyFlags > 0) {
                htmlWriter.writeAttributeNS("keyFlags", state.keyFlags);
            }

            if (acceleratorComponent.isIgnoreEditableComponent(facesContext)) {
                htmlWriter.writeAttributeNS("ignoreEditableComponent", true);
            }

            htmlWriter.endElementNS(LAZY_INIT_TAG);

            declareLazyJavaScriptRenderer(htmlWriter);
            htmlWriter.getJavaScriptEnableMode().enableOnInit();

        } else {
            htmlWriter.enableJavaScript();
        }

        super.encodeEnd(htmlWriter);
    }

    @Override
    protected boolean encodeEventsInAttributes(IHtmlWriter writer) {
        if (writer.getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {
            return true;
        }

        return false;
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        if (jsWriter.getJavaScriptRenderContext().isCollectorMode() == false) {
            return;
        }

        FacesContext facesContext = jsWriter.getFacesContext();

        IComponentRenderContext componentRenderContext = jsWriter
                .getComponentRenderContext();

        AcceleratorComponent acceleratorComponent = (AcceleratorComponent) componentRenderContext
                .getComponent();

        String keyBinding = acceleratorComponent.getKeyBinding(facesContext);

        jsWriter.setIgnoreComponentInitialization();

        String varName = jsWriter.getJavaScriptRenderContext()
                .allocateVarName();
        jsWriter.setComponentVarName(varName);

        jsWriter.write(varName).write('=')
                .writeCall(getJavaScriptClassName(), "f_newInstance");

        int param = 0;

        KeyTools.State state = KeyTools.parseKeyBinding(keyBinding);
        if (state.character > 0) {
            jsWriter.writeString(String.valueOf(state.character));
        } else {
            jsWriter.writeNull();
        }

        if (state.virtualKey != null) {
            for (; param > 0; param--) {
                jsWriter.write(',').writeNull();
            }
            jsWriter.write(",[").writeInt(state.virtualKey.intValue())
                    .write(']');

        } else {
            param++;
        }

        if (state.keyFlags > 0) {
            for (; param > 0; param--) {
                jsWriter.write(',').writeNull();
            }
            jsWriter.write(',').writeInt(state.keyFlags);

        } else {
            param++;
        }

        String forComponent = acceleratorComponent.getFor(facesContext);
        if (forComponent != null) {
            for (; param > 0; param--) {
                jsWriter.write(',').writeNull();
            }

            // Il faut calculer le for, car l'accelerator n'est plus rattaché à
            // un composant DOM !

            IRenderContext renderContext = componentRenderContext
                    .getRenderContext();

            String forComponentClientId = renderContext
                    .computeBrotherComponentClientId(acceleratorComponent,
                            forComponent);

            jsWriter.write(',').writeString(forComponentClientId);

        } else {
            param++;
        }

        String forItemValue = acceleratorComponent
                .getForItemValue(facesContext);
        if (forItemValue != null) {
            for (; param > 0; param--) {
                jsWriter.write(',').writeNull();
            }
            jsWriter.write(',').writeString(forItemValue);

        } else {
            param++;
        }

        if (acceleratorComponent.isIgnoreEditableComponent(facesContext)) {
            for (; param > 0; param--) {
                jsWriter.write(',').writeNull();
            }
            jsWriter.write(',').writeBoolean(true);

        } else {
            param++;
        }

        jsWriter.writeln(");");
    }

    /*
     * protected IWriter writeIdAttribute(IWriter htmlWriter) throws
     * WriterException { Pas ca car il nous faut un ID ! (en cas de premier
     * composant a initialiser ! if
     * (ComponentTools.isAnonymousComponentId(htmlWriter
     * .getComponentRenderContext().getComponentId())) { return htmlWriter; }
     * 
     * return super.writeIdAttribute(htmlWriter); }
     */

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.ACCELERATOR;
    }

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getKeyPressEventName();
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}
