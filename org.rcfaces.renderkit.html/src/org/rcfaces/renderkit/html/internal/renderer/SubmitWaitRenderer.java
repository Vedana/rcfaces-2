/*
 * $Id: SubmitWaitRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.SubmitWaitComponent;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;

/**
 * Renderer du composant <u:submitWait>
 * 
 * @author flefevere (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "backgroundMode", "imageURL", "width", "height", "text" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class SubmitWaitRenderer extends AbstractJavaScriptRenderer {

    private static final Log LOG = LogFactory.getLog(SubmitWaitRenderer.class);

    private static final String DEFAULT_SUBMIT_WAIT_IMAGE_URL = "waiting/submitWait.gif";

    private static final int DEFAULT_SUBMIT_WAIT_IMAGE_WIDTH = 32;

    private static final int DEFAULT_SUBMIT_WAIT_IMAGE_HEIGHT = 32;

    protected void encodeEnd(IComponentWriter _writer) throws WriterException {
        IComponentRenderContext componentRenderContext = _writer
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        SubmitWaitComponent submitWaitComponent = (SubmitWaitComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) _writer;

        /*
         * String backgroundMode = component.getBackgroundMode(facesContext); if
         * (backgroundMode != null) {
         * htmlWriter.writeAttributeNS("backgroundMode", backgroundMode); }
         */

        if (htmlWriter.getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {

            htmlWriter.startElementNS(LAZY_INIT_TAG);
            writeHtmlAttributes(htmlWriter);
            writeJavaScriptAttributes(htmlWriter);

            htmlWriter.getJavaScriptEnableMode().enableOnSubmit();

            String width = submitWaitComponent.getWidth(facesContext);
            String height = submitWaitComponent.getHeight(facesContext);

            IContentAccessor imageAccessor = null;

            if (submitWaitComponent.isImageURLSetted()) {
                IContentAccessors contentAccessors = submitWaitComponent
                        .getImageAccessors(facesContext);

                if (contentAccessors instanceof IImageAccessors) {
                    IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;
                    imageAccessor = imageAccessors.getImageAccessor();
                }
            }

            IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                    .getHtmlComponentRenderContext();

            if (imageAccessor == null) {
                imageAccessor = getDefaultImageAccessor(htmlComponentRenderContext);
                if (imageAccessor != null) {
                    width = String
                            .valueOf(getDefaultImageWidth(htmlComponentRenderContext));
                    height = String
                            .valueOf(getDefaultImageHeight(htmlComponentRenderContext));
                }
            }

            if (imageAccessor != null) {
                String imageSrc = imageAccessor.resolveURL(facesContext, null,
                        null);
                if (imageSrc != null) {
                    htmlWriter.writeURIAttributeNS("imageURL", imageSrc);
                }
            }

            if (width != null) {
                htmlWriter.writeAttributeNS("width", width);
            }

            if (height != null) {
                htmlWriter.writeAttributeNS("height", height);
            }

            String text = submitWaitComponent.getText(facesContext);
            if (text == null) {
                text = getDefaultText(htmlComponentRenderContext);
            }
            if (text != null) {
                htmlWriter.writeAttributeNS("text", text);
            }

            htmlWriter.endElementNS(LAZY_INIT_TAG);

            declareLazyJavaScriptRenderer(htmlWriter);

        } else {
            htmlWriter.enableJavaScript();
        }

        super.encodeEnd(htmlWriter);
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        if (jsWriter.getJavaScriptRenderContext().isCollectorMode() == false) {
            return;
        }

        jsWriter.setIgnoreComponentInitialization();

        SubmitWaitComponent submitWaitComponent = (SubmitWaitComponent) jsWriter
                .getComponentRenderContext().getComponent();

        IContentAccessor imageAccessor = null;

        FacesContext facesContext = jsWriter.getFacesContext();

        String width = submitWaitComponent.getWidth(facesContext);
        String height = submitWaitComponent.getHeight(facesContext);

        if (submitWaitComponent.isImageURLSetted()) {
            IContentAccessors contentAccessors = submitWaitComponent
                    .getImageAccessors(facesContext);

            if (contentAccessors instanceof IImageAccessors) {
                IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;
                imageAccessor = imageAccessors.getImageAccessor();
            }
        }

        IHtmlComponentRenderContext htmlComponentRenderContext = jsWriter
                .getHtmlComponentRenderContext();

        if (imageAccessor == null) {
            imageAccessor = getDefaultImageAccessor(htmlComponentRenderContext);
            if (imageAccessor != null) {
                width = String
                        .valueOf(getDefaultImageWidth(htmlComponentRenderContext));
                height = String
                        .valueOf(getDefaultImageHeight(htmlComponentRenderContext));
            }
        }

        String imageSrc = null;
        if (imageAccessor != null) {
            imageSrc = imageAccessor.resolveURL(facesContext, null, null);
        }

        String text = submitWaitComponent.getText(facesContext);
        if (text == null) {
            text = getDefaultText(htmlComponentRenderContext);
        }

        String backgroundMode = submitWaitComponent
                .getBackgroundMode(facesContext);
        if (backgroundMode == null) {
            backgroundMode = getDefaultBackgroundMode(htmlComponentRenderContext);
        }

        boolean constructorParameters = (imageSrc != null && width != null && height != null);

        if (constructorParameters == false) {
            String varName = jsWriter.getJavaScriptRenderContext()
                    .allocateVarName();
            jsWriter.setComponentVarName(varName);

            jsWriter.write(varName).write('=');
        }

        jsWriter.writeCall(getJavaScriptClassName(), "f_newInstance");

        if (constructorParameters) {
            jsWriter.writeString(imageSrc).write(',').writeString(text)
                    .write(',').write(width).write(',').write(height)
                    .write(',').writeBoolean(true);
            if (backgroundMode != null) {
                jsWriter.write(',').writeString(backgroundMode);
            }

            jsWriter.writeln(");");
        } else {
            jsWriter.writeln(");");

            if (imageSrc != null) {
                jsWriter.writeMethodCall("f_setImageURL").writeString(imageSrc)
                        .writeln(");");
            }
            if (text != null) {
                jsWriter.writeMethodCall("f_setText").writeString(text)
                        .writeln(");");
            }
            if (width != null) {
                jsWriter.writeMethodCall("f_setWidth").write(width)
                        .writeln(");");
            }

            if (height != null) {
                jsWriter.writeMethodCall("f_setHeight").write(height)
                        .writeln(");");
            }
            if (backgroundMode != null) {
                jsWriter.writeMethodCall("f_setBackgroundMode")
                        .writeString(backgroundMode).writeln(");");
            }

            jsWriter.writeMethodCall("f_installShowOnSubmit").writeln(");");
        }
    }

    protected String getDefaultBackgroundMode(
            IHtmlComponentRenderContext htmlComponentRenderContext) {
        return null;
    }

    protected String getDefaultText(IHtmlComponentRenderContext htmlWriter) {
        return null;
    }

    protected IContentAccessor getDefaultImageAccessor(
            IHtmlComponentRenderContext componentRenderContext) {
        return componentRenderContext
                .getHtmlRenderContext()
                .getHtmlProcessContext()
                .getStyleSheetContentAccessor(DEFAULT_SUBMIT_WAIT_IMAGE_URL,
                        null);

    }

    protected int getDefaultImageWidth(
            IHtmlComponentRenderContext componentRenderContext) {
        return DEFAULT_SUBMIT_WAIT_IMAGE_WIDTH;
    }

    protected int getDefaultImageHeight(
            IHtmlComponentRenderContext componentRenderContext) {
        return DEFAULT_SUBMIT_WAIT_IMAGE_HEIGHT;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.WAITING_SHELL;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }

}
