/*
 * $Id: ImagePagerButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.text.MessageFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ImagePagerButtonComponent;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
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
@XhtmlNSAttributes({ "type", "for", "hideIfDisabled" })
public class ImagePagerButtonRenderer extends ImageButtonRenderer {

    private static final Log LOG = LogFactory
            .getLog(ImagePagerButtonRenderer.class);

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_PAGER_BUTTON;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();
        ImagePagerButtonComponent imagePagerButtonComponent = (ImagePagerButtonComponent) componentRenderContext
                .getComponent();

        imagePagerButtonComponent.setDisabled(true);
        if (imagePagerButtonComponent.isHideIfDisabled(facesContext)) {
            imagePagerButtonComponent.setVisible(false);
        }

        if (imagePagerButtonComponent.getAlternateText(facesContext) == null) {
            String type = imagePagerButtonComponent.getType(facesContext);

            if (type != null) {
                type = type.trim().toLowerCase();
            }

            if (type != null && type.length() > 0) {
                String key = null;
                Object arguments[] = null;
                if (Character.isDigit(type.charAt(0))) {
                    try {
                        int page = Integer.parseInt(type);

                        key = "f_imagePagerButton.INDEX";
                        arguments = new Integer[] { new Integer(page) };

                    } catch (NumberFormatException ex) {
                        LOG.debug(ex);
                    }
                }

                if (key == null) {
                    if ("prev".equals(type)) {
                        type = "PREVIOUS";
                    }

                    key = "f_imagePagerButton." + type.toUpperCase();
                }

                if (key != null) {
                    String alt = getResourceBundleValue((IHtmlWriter) writer,
                            key);
                    if (alt != null) {
                        if (arguments != null) {
                            alt = MessageFormat.format(alt, arguments);
                        }

                        imagePagerButtonComponent.setAlternateText(alt);
                    }
                }
            }
        }

        // Il faut intialiser le bouton au début pour retirer le disabled si
        // necessaire !
        ((IHtmlWriter) writer).getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(writer);
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new PagerImageButtonDecorator((IImageButtonFamilly) component);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
     */
    protected class PagerImageButtonDecorator extends ImageButtonDecorator {

        public PagerImageButtonDecorator(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected void encodeAttributes(FacesContext facesContext)
                throws WriterException {
            super.encodeAttributes(facesContext);

            ImagePagerButtonComponent button = (ImagePagerButtonComponent) imageButtonFamilly;

            String type = button.getType(facesContext);
            if (type != null) {
                writer.writeAttributeNS("type", type);
            }

            String forProperty = button.getFor(facesContext);
            if (forProperty != null) {
                writer.writeAttributeNS("for", forProperty);
            }

            boolean hideIfDisabled = button.isHideIfDisabled(facesContext);
            if (hideIfDisabled) {
                writer.writeAttributeNS("hideIfDisabled", true);
            }
        }
    }
}