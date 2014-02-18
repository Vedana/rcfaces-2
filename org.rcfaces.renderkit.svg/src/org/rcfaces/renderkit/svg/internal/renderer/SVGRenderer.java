/*
 * $Id: SVGRenderer.java,v 1.3 2013/11/15 10:02:18 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.renderer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.decorator.CssFilesCollectorDecorator;
import org.rcfaces.renderkit.html.internal.decorator.FilesCollectorDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;
import org.rcfaces.renderkit.svg.component.SVGComponent;
import org.rcfaces.renderkit.svg.internal.component.ISVGAccessors;
import org.rcfaces.renderkit.svg.internal.image.SVGGeneratedInformation;
import org.rcfaces.renderkit.svg.internal.image.SVGGenerationInformation;
import org.rcfaces.renderkit.svg.internal.util.ISVGContentFamily;
import org.rcfaces.renderkit.svg.internal.util.SVGContentAccessorFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/15 10:02:18 $
 */
public class SVGRenderer extends AbstractCssRenderer {
    private static final String FILTRED_CONTENT_PROPERTY = "org.rcfaces.svg.FILTRED";

    private static final String STYLE_FILES_PROPERTY = "org.rcfaces.svg.STYLE_FILES";

    @Override
    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;
        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        SVGComponent svgComponent = (SVGComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlElements.OBJECT);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String width = svgComponent.getWidth(facesContext);
        if (width == null || width.length() < 1) {
            throw new FacesException("Width must be setted for SVG component");
        }

        int svgWidth = computeSize(width, -1, 0);

        String height = svgComponent.getHeight(facesContext);
        if (height == null || height.length() < 1) {
            throw new FacesException("Height must be setted for SVG component");
        }

        int svgHeight = computeSize(height, -1, 0);

        String svgURL = svgComponent.getSvgURL(facesContext);
        if (svgURL == null || svgURL.length() < 1) {
            throw new FacesException("SVG URL must be setted for SVG component");
        }

        ISVGAccessors contentAccessors = SVGContentAccessorFactory
                .createSingleSVGWebResource(facesContext, svgURL,
                        ISVGContentFamily.SVG);

        SVGGenerationInformation generationInformation = null;
        SVGGeneratedInformation generatedInformation = null;

        IContentAccessor contentAccessor = contentAccessors.getSVGAccessor();
        if (contentAccessor != null) {
            generationInformation = new SVGGenerationInformation();

            generationInformation.setComponent(componentRenderContext);

            IFilterProperties filterProperties = svgComponent
                    .getFilterProperties();
            generationInformation.setFilterProperties(filterProperties);

            if (svgWidth > 0) {
                generationInformation.setWidth(svgWidth);
            }

            if (svgHeight > 0) {
                generationInformation.setHeight(svgHeight);
            }

            generatedInformation = new SVGGeneratedInformation();

            svgURL = contentAccessor.resolveURL(facesContext,
                    generatedInformation, generationInformation);

            if (generatedInformation.isFiltredModel()) {
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

        if (generatedInformation != null) {
            int w = generatedInformation.getWidth();
            if (w > 0) {
                svgWidth = w;
            }
            int h = generatedInformation.getHeight();
            if (h > 0) {
                svgHeight = h;
            }
        }

        htmlWriter.writeAttribute("type", "image/svg+xml");

        if (svgURL != null) {
            htmlWriter.writeAttribute("data", svgURL);
        }

        if (svgWidth > 0) {
            htmlWriter.writeWidth(svgWidth);
        }
        if (svgHeight > 0) {
            htmlWriter.writeHeight(svgHeight);
        }

        htmlWriter.endElement(IHtmlElements.OBJECT);

        FilesCollectorDecorator filesCollectorDecorator = (FilesCollectorDecorator) getComponentDecorator(componentRenderContext);
        if (filesCollectorDecorator != null) {
            FileItemSource[] sources = filesCollectorDecorator.listSources();
            if (sources != null && sources.length > 0) {
                componentRenderContext.setAttribute(STYLE_FILES_PROPERTY,
                        sources);

                htmlWriter.enableJavaScript();
            }
        }

        super.encodeEnd(htmlWriter);
    }

    @Override
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.SVG;
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
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.SVG,
                    "filter");
        }
    }

    @Override
    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        FileItemSource[] sources = (FileItemSource[]) jsWriter
                .getComponentRenderContext().getAttribute(STYLE_FILES_PROPERTY);

        if (sources != null) {
            FacesContext facesContext = jsWriter.getFacesContext();

            for (FileItemSource source : sources) {
                String src = source.getSource();

                IContentAccessor contentAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, src,
                                IContentFamily.STYLE);

                IGeneratedResourceInformation generatedResourceInformation = new BasicGeneratedResourceInformation();
                IGenerationResourceInformation generationInformation = null;

                src = contentAccessor.resolveURL(facesContext,
                        generatedResourceInformation, generationInformation);

                if (src != null) {
                    jsWriter.writeMethodCall("f_appendStyleSheet")
                            .writeString(src).writeln(");");
                }
            }
        }
    }

    @Override
    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    @Override
    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {
        return new CssFilesCollectorDecorator(component);
    }

}
