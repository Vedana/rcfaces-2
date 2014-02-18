/*
 * $Id: ImageRenderer.java,v 1.4 2013/11/15 10:02:18 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.renderer;

import java.awt.geom.AffineTransform;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.svg.component.ImageComponent;
import org.rcfaces.renderkit.svg.internal.image.SVGImageGeneratedInformation;
import org.rcfaces.renderkit.svg.internal.image.SVGImageGenerationInformation;
import org.rcfaces.renderkit.svg.internal.image.ShapeValue;
import org.rcfaces.renderkit.svg.item.INodeItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/15 10:02:18 $
 */
public class ImageRenderer extends AbstractCssRenderer {
    private static final String FILTRED_CONTENT_PROPERTY = "org.rcfaces.svg.FILTRED_IMAGE";

    @Override
    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = (IHtmlComponentRenderContext) writer
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        ImageComponent image = (ImageComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlElements.IMG);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);
        writeImageAttributes(htmlWriter);

        int imageWidth = image.getImageWidth(facesContext);
        int imageHeight = image.getImageHeight(facesContext);

        SVGImageGenerationInformation imageGenerationInformation = null;
        SVGImageGeneratedInformation generatedImageInformation = null;

        IImageAccessors imageAccessors = (IImageAccessors) image
                .getImageAccessors(facesContext);
        String url = null;
        IContentAccessor contentAccessor = imageAccessors.getImageAccessor();
        if (contentAccessor != null) {
            imageGenerationInformation = new SVGImageGenerationInformation();

            double curveFlatness = image.getCurveFlatness(facesContext);
            if (curveFlatness > 0.0) {
                imageGenerationInformation.setCurveFlatness(curveFlatness);
            }

            double distanceTolerance = image.getDistanceTolerance(facesContext);
            if (distanceTolerance > 0.0) {
                imageGenerationInformation
                        .setDistanceTolerance(distanceTolerance);
            }

            imageGenerationInformation.setComponent(componentRenderContext);

            IFilterProperties filterProperties = image.getFilterProperties();
            imageGenerationInformation.setFilterProperties(filterProperties);

            if (imageWidth > 0) {
                imageGenerationInformation.setImageWidth(imageWidth);
            }

            if (imageHeight > 0) {
                imageGenerationInformation.setImageHeight(imageHeight);
            }

            double pixelUnit = image.getPixelUnitToMillimeter(facesContext);
            if (pixelUnit > 0) {
                imageGenerationInformation
                        .setPixelUnitToMillimeter((float) pixelUnit);
            }

            String defaultFontName = image.getFontName(facesContext);
            if (defaultFontName != null) {
                imageGenerationInformation
                        .setDefaultFontFamily(defaultFontName);
            }

            generatedImageInformation = new SVGImageGeneratedInformation();

            NodesCollectorDecorator nodesCollectorDecorator = ((NodesCollectorDecorator) getComponentDecorator(componentRenderContext));

            INodeItem nodeItems[] = nodesCollectorDecorator.listNodes();
            if (nodeItems != null && nodeItems.length > 0) {
                imageGenerationInformation.setNodes(nodeItems);
            }

            if (nodesCollectorDecorator.isItemSelectable()) {
                imageGenerationInformation.setProcessAtRequest(false);
            }

            url = contentAccessor.resolveURL(facesContext,
                    generatedImageInformation, imageGenerationInformation);

            if (generatedImageInformation.isFiltredModel()) {
                componentRenderContext.setAttribute(FILTRED_CONTENT_PROPERTY,
                        Boolean.TRUE);

                htmlWriter.writeAttribute("v:filtred", true);

                if (filterProperties != null
                        && filterProperties.isEmpty() == false) {
                    String filterExpression = HtmlTools.encodeFilterExpression(
                            filterProperties, componentRenderContext
                                    .getRenderContext().getProcessContext(),
                            componentRenderContext.getComponent());
                    htmlWriter.writeAttribute("v:filterExpression",
                            filterExpression);
                }
            }
        }

        if (url == null) {
            url = componentRenderContext.getHtmlRenderContext()
                    .getHtmlProcessContext()
                    .getStyleSheetURI(BLANK_IMAGE_URL, true);

            htmlWriter.writeAttribute("v:blank", true);
        }
        htmlWriter.writeSrc(url);

        ShapeValue values[] = null;

        if (generatedImageInformation != null) {
            int w = generatedImageInformation.getImageWidth();
            if (w > 0) {
                imageWidth = w;
            }
            int h = generatedImageInformation.getImageHeight();
            if (h > 0) {
                imageHeight = h;
            }

            if (isItemSelectable()) {
                values = generatedImageInformation.getShapeValues();

                if (values != null && values.length == 0) {
                    values = null;
                }
            }
        }

        if (imageWidth > 0) {
            htmlWriter.writeWidth(imageWidth);
        }
        if (imageHeight > 0) {
            htmlWriter.writeHeight(imageHeight);
        }

        String mapName = null;

        if (values != null) {
            mapName = htmlWriter.getComponentRenderContext()
                    .getComponentClientId() + "::map";

            htmlWriter.writeAttribute("usemap", "#" + mapName);
        }

        htmlWriter.endElement(IHtmlElements.IMG);

        if (values != null) {
            htmlWriter.startElement(IHtmlElements.MAP);
            htmlWriter.writeName(mapName);
            htmlWriter.writeln();

            AffineTransform transform = null;
            if (generatedImageInformation != null) {
                transform = generatedImageInformation.getGlobalTransform();
            }

            if (transform == null) {
                transform = new AffineTransform();
            }

            for (int i = 0; i < values.length; i++) {
                ShapeValue value = values[i];

                String itemConvertedValue = null;

                Object itemValue = value.getValue();
                if (itemValue != null) {
                    itemConvertedValue = convertValue(facesContext, image,
                            itemValue);
                }

                String coords[] = value.computeOutline(transform, 1);
                for (int j = 0; j < coords.length; j++) {
                    String c = coords[j];
                    if (c.length() < 1) {
                        continue;
                    }

                    htmlWriter.startElement(IHtmlElements.AREA);
                    htmlWriter.writeId(htmlWriter.getComponentRenderContext()
                            .getComponentClientId() + "::map" + i);

                    htmlWriter.writeAttribute("href", "#");

                    String description = value.getDescription();
                    if (description != null) {
                        htmlWriter.writeTitle(description);
                    }

                    String alternateText = value.getAlternateText();
                    if (alternateText != null) {
                        htmlWriter.writeAlt(alternateText);
                    }

                    if (j == 0) {
                        if (itemConvertedValue != null) {
                            htmlWriter.writeAttribute("v:value",
                                    itemConvertedValue);
                        }

                        String label = value.getLabel();
                        if (label != null) {
                            htmlWriter.writeAttribute("v:label", label);
                        }

                        Map<String, String> clientDatas = value
                                .getClientDatas();
                        if (clientDatas != null
                                && clientDatas.isEmpty() == false) {
                            HtmlTools.writeClientData(htmlWriter, clientDatas);
                        }
                    }

                    htmlWriter.writeAttribute("shape", "poly");

                    htmlWriter.writeAttribute("coords", c);

                    htmlWriter.endElement(IHtmlElements.AREA);

                    htmlWriter.writeln();
                }
            }

            htmlWriter.endElement(IHtmlElements.MAP);
        }

        super.encodeEnd(htmlWriter);
    }

    protected void writeImageAttributes(IHtmlWriter htmlWriter) {
    }

    @Override
    public void addRequiredJavaScriptClassNames(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(htmlWriter,
                javaScriptRenderContext);

        if (htmlWriter.getComponentRenderContext().containsAttribute(
                FILTRED_CONTENT_PROPERTY)) {

            // On prend .COMBO en dure, car le filter n'est pas defini pour les
            // classes qui en héritent !
            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.IMAGE, "filter");
        }
    }

    @Override
    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new NodesCollectorDecorator(component, isItemSelectable());
    }

    protected boolean isItemSelectable() {
        return false;
    }

    @Override
    protected final boolean hasComponenDecoratorSupport() {
        return true;
    }

    @Override
    public final boolean getRendersChildren() {
        return true;
    }

    @Override
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE;
    }
}