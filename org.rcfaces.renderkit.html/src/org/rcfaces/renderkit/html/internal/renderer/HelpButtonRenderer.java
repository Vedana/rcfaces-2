/*
 * $Id: HelpButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.HelpButtonComponent;
import org.rcfaces.core.component.capability.IForCapability;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "for" })
public class HelpButtonRenderer extends ImageButtonRenderer {

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.HELP_BUTTON;
    }

    public void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(writer);
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new HelpButtonDecorator((IImageButtonFamilly) component);
    }

    protected int getDefaultHelpImageWidth() {
        return 15;
    }

    protected int getDefaultHelpImageHeight() {
        return 15;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
     */
    protected class HelpButtonDecorator extends ImageButtonDecorator {

        public HelpButtonDecorator(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected void encodeAttributes(FacesContext facesContext)
                throws WriterException {
            HelpButtonComponent helpButtonComponent = (HelpButtonComponent) imageButtonFamilly;

            String forId = ((IForCapability) imageButtonFamilly).getFor();

            if (forId != null) {
                IComponentRenderContext componentRenderContext = writer
                        .getComponentRenderContext();

                IRenderContext renderContext = componentRenderContext
                        .getRenderContext();

                String forClientId = renderContext
                        .computeBrotherComponentClientId(helpButtonComponent,
                                forId);

                if (forClientId != null) {
                    writer.writeAttributeNS("for", forClientId);
                }
            }
        }

        protected void writeImageSrc(IHtmlWriter writer, String imageSrc)
                throws WriterException {
            if (imageSrc != null) {
                writer.writeSrc(imageSrc);
                return;
            }

            int width = getDefaultHelpImageWidth();
            if (width > 0) {
                writer.writeWidth(width);
            }

            int height = getDefaultHelpImageHeight();
            if (height > 0) {
                writer.writeHeight(height);
            }
        }
    }
}
