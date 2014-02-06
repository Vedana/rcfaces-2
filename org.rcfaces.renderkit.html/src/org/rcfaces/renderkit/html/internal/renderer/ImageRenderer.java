/*
 * $Id: ImageRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ImageComponent;
import org.rcfaces.core.image.GeneratedImageInformation;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "filtred", "filterExpression", "blank" })
public class ImageRenderer extends AbstractCssRenderer {

    private static final String FILTRED_CONTENT_PROPERTY = "camelia.image.filtredContent";

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = (IHtmlComponentRenderContext) writer
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        ImageComponent image = (ImageComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.IMG);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);
        writeFirstTooltipClientId(htmlWriter);

        GeneratedImageInformation generatedImageInformation = null;
        IImageAccessors imageAccessors = (IImageAccessors) image
                .getImageAccessors(facesContext);
        String url = null;
        IContentAccessor contentAccessor = imageAccessors.getImageAccessor();
        if (contentAccessor != null) {
            generatedImageInformation = new GeneratedImageInformation();

            BasicGenerationResourceInformation generationInformation = new BasicGenerationResourceInformation(
                    componentRenderContext);

            IFilterProperties filterProperties = image.getFilterProperties();
            generationInformation.setFilterProperties(filterProperties);

            url = contentAccessor.resolveURL(facesContext,
                    generatedImageInformation, generationInformation);

            if (generatedImageInformation.isFiltredModel()) {
                componentRenderContext.setAttribute(FILTRED_CONTENT_PROPERTY,
                        Boolean.TRUE);

                htmlWriter.writeAttributeNS("filtred", true);

                if (filterProperties != null
                        && filterProperties.isEmpty() == false) {
                    String filterExpression = HtmlTools.encodeFilterExpression(
                            filterProperties, componentRenderContext
                                    .getRenderContext().getProcessContext(),
                            componentRenderContext.getComponent());
                    htmlWriter.writeAttributeNS("filterExpression",
                            filterExpression);
                }
            }
        }

        if (url == null) {
            url = componentRenderContext.getHtmlRenderContext()
                    .getHtmlProcessContext()
                    .getStyleSheetURI(BLANK_IMAGE_URL, true);

            htmlWriter.writeAttributeNS("blank", true);
        }
        htmlWriter.writeSrc(url);

        int imageWidth = image.getImageWidth(facesContext);
        int imageHeight = image.getImageHeight(facesContext);

        if (imageWidth < 0 && imageHeight < 0
                && generatedImageInformation != null) {
            imageWidth = generatedImageInformation.getImageWidth();
            imageHeight = generatedImageInformation.getImageHeight();
        }

        if (imageWidth > 0) {
            htmlWriter.writeWidth(imageWidth);
        }
        if (imageHeight > 0) {
            htmlWriter.writeHeight(imageHeight);
        }

        String alternateText = image.getAlternateText(facesContext);
        if (alternateText != null) {
            htmlWriter.writeAlt(alternateText);
        }

        htmlWriter.endElement(IHtmlWriter.IMG);

        super.encodeEnd(htmlWriter);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(htmlWriter,
                javaScriptRenderContext);

        if (htmlWriter.getComponentRenderContext().containsAttribute(
                FILTRED_CONTENT_PROPERTY)) {

            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.FILTRED_COMPONENT, "filter");
        }
    }
}