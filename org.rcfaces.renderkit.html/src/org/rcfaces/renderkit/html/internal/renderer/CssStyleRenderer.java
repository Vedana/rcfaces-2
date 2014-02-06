/*
 * $Id: CssStyleRenderer.java,v 1.4 2013/11/26 13:55:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.style.IStyleContentAccessorHandler;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.component.CssStyleComponent;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.rcfaces.renderkit.html.internal.css.ICssConfig;
import org.rcfaces.renderkit.html.internal.css.StylesheetsServlet;
import org.rcfaces.renderkit.html.internal.decorator.CssFilesCollectorDecorator;
import org.rcfaces.renderkit.html.internal.decorator.FilesCollectorDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.style.CssFilesCollectorGenerationInformation;
import org.rcfaces.renderkit.html.internal.style.CssGenerationInformation;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/26 13:55:57 $
 */
public class CssStyleRenderer extends AbstractFilesCollectorRenderer {

    private static final Log LOG = LogFactory.getLog(CssStyleRenderer.class);

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        IHtmlProcessContext htmlProcessContext = componentRenderContext
                .getHtmlRenderContext().getHtmlProcessContext();

        CssStyleComponent cssStyleComponent = (CssStyleComponent) componentRenderContext
                .getComponent();

        String userAgent = cssStyleComponent.getUserAgent(facesContext);
        if (userAgent != null && userAgent.length() > 0) {
            if (htmlWriter.getHtmlComponentRenderContext()
                    .getHtmlRenderContext().isUserAgentVary() == false) {
                throw new FacesException(
                        "In order to use userAgentVary property, you must declare <v:init userAgentVary=\"true\" ...>");
            }

            if (UserAgentRuleTools.accept(facesContext, cssStyleComponent) == false) {
                return;
            }
        }

        boolean useMetaContentStyleType = htmlProcessContext
                .useMetaContentStyleType();

        String src = cssStyleComponent.getSrc(facesContext);
        String srcCharSet = cssStyleComponent.getSrcCharSet(facesContext);

        FileItemSource sources[] = null;
        FilesCollectorDecorator filesCollectorDecorator = (FilesCollectorDecorator) getComponentDecorator(componentRenderContext);
        if (filesCollectorDecorator != null) {
            sources = filesCollectorDecorator.listSources();
        }

        boolean mergeStyles = cssStyleComponent.isMergeStyles(facesContext);
        boolean processRules = cssStyleComponent.isProcessRules(facesContext);

        String requiredModules = cssStyleComponent
                .getRequiredModules(facesContext);

        String requiredSets = cssStyleComponent.getRequiredSets(facesContext);
        if (requiredModules != null || requiredSets != null) {
            sources = addRequired(htmlWriter, sources, requiredModules,
                    requiredSets);
        }

        if (processRules == false) {
            Set<String> forceProcessRules = htmlProcessContext
                    .listCssProcessRulesForced();
            if (forceProcessRules != null
                    && forceProcessRules.isEmpty() == false) {
            	if (requiredModules != null
                        && containsProcessRulesId(forceProcessRules,
                                requiredModules)) {
                    processRules = true;

                } else if (requiredSets != null
                        && containsProcessRulesId(forceProcessRules,
                        requiredSets)) {
                    processRules = true;
                }
            }
        }
        

        if (src != null || (sources != null && sources.length > 0)) {

            boolean srcFiltred = false;

            if (mergeStyles) {
                IGenerationResourceInformation generationInformation = null;

                if (sources != null && sources.length > 0) {
                    if (src == null) {
                        src = IStyleContentAccessorHandler.MERGE_FILTER_NAME
                                + IContentAccessor.FILTER_SEPARATOR
                                + sources[0].getSource();

                        if (sources.length > 1) {
                            FileItemSource fs[] = new FileItemSource[sources.length - 1];
                            System.arraycopy(sources, 1, fs, 0,
                                    sources.length - 1);

                            sources = fs;
                        } else {
                            sources = null;
                        }
                    }

                    if (sources != null) {
                        generationInformation = new CssFilesCollectorGenerationInformation(
                                sources, false, processRules);
                    }
                }

                IContentAccessor contentAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, src,
                                IContentFamily.STYLE);

                IGeneratedResourceInformation generatedResourceInformation = new BasicGeneratedResourceInformation();

                src = contentAccessor.resolveURL(facesContext,
                        generatedResourceInformation, generationInformation);

                // srcCharset=generatedResourceInformation.getCharset();

                srcFiltred = true;

                sources = null;

                processRules = false; // Déjà fait !
            }

            if (src != null && srcFiltred == false) {
                if (processRules) {
                    src = IStyleContentAccessorHandler.PROCESS_FILTER_NAME
                            + IContentAccessor.FILTER_SEPARATOR + src;
                }

                IContentAccessor contentAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, src,
                                IContentFamily.STYLE);

                src = contentAccessor.resolveURL(facesContext, null,
                        new CssGenerationInformation(processRules));
            }

            if (src != null) {

                htmlWriter.startElement(IHtmlWriter.LINK);
                htmlWriter.writeRel(IHtmlRenderContext.STYLESHEET_REL);
                if (useMetaContentStyleType == false) {
                    htmlWriter.writeType(IHtmlRenderContext.CSS_TYPE);
                }
                if (srcCharSet != null) {
                    htmlWriter.writeCharSet(srcCharSet);
                }

                htmlWriter.writeHRef(src);

                htmlWriter.endElement(IHtmlWriter.LINK);
            }

            if (sources != null) {
                for (int i = 0; i < sources.length; i++) {
                    FileItemSource source = sources[i];

                    htmlWriter.startElement(IHtmlWriter.LINK);
                    htmlWriter.writeRel(IHtmlRenderContext.STYLESHEET_REL);
                    if (useMetaContentStyleType == false) {
                        htmlWriter.writeType(IHtmlRenderContext.CSS_TYPE);
                    }
                    if (source.getCharSet() != null) {
                        htmlWriter.writeCharSet(source.getCharSet());
                    }

                    String itemSrc = source.getSource();
                    if (srcFiltred == false && processRules) {
                        itemSrc = IStyleContentAccessorHandler.PROCESS_FILTER_NAME
                                + IContentAccessor.FILTER_SEPARATOR + itemSrc;
                    }
                    IContentAccessor contentAccessor = ContentAccessorFactory
                            .createFromWebResource(facesContext, itemSrc,
                                    IContentFamily.STYLE);

                    CssFilesCollectorGenerationInformation generation = new CssFilesCollectorGenerationInformation(
                            null, source.isFrameworkResource(), processRules);

                    itemSrc = contentAccessor.resolveURL(facesContext, null,
                            generation);

                    htmlWriter.writeHRef(itemSrc);

                    htmlWriter.endElement(IHtmlWriter.LINK);
                }
            }
        }

        String text = cssStyleComponent.getText(facesContext);
        if (text != null && text.trim().length() > 0) {
            htmlWriter.startElement(IHtmlWriter.STYLE);
            if (useMetaContentStyleType == false) {
                htmlWriter.writeType(IHtmlRenderContext.CSS_TYPE);
            }

            htmlWriter.write(text);

            htmlWriter.endElement(IHtmlWriter.STYLE);

        }
    }

    private static boolean containsProcessRulesId(
            Set<String> forceProcessRules, String ids) {

    	if(ids == null) {
    		return false;
    	}
    	
        StringTokenizer st = new StringTokenizer(ids, ", ");
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();
            if (forceProcessRules.contains(token) == false) {
                continue;
            }

            return true;
        }

        return false;
    }

    private FileItemSource[] addRequired(IHtmlWriter writer,
            FileItemSource[] sources, String requiredModules,
            String requiredSets) {

        IHtmlProcessContext htmlProcessContext = writer
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getHtmlProcessContext();

        List<FileItemSource> sl = new ArrayList<FileItemSource>(
                Arrays.asList(sources));

        if (requiredModules != null) {
            StringTokenizer st = new StringTokenizer(requiredModules, ",");
            for (; st.hasMoreTokens();) {
                String moduleName = st.nextToken().trim();

                ICssConfig cssConfig = StylesheetsServlet.getConfig(
                        htmlProcessContext, moduleName);

                if (cssConfig == null) {
                    LOG.error("Unknown module '" + moduleName + "'");
                    continue;
                }

                String uri = cssConfig.getDefaultStyleSheetURI()
                        + "/"
                        + cssConfig.getStyleSheetFileName(htmlProcessContext
                                .getClientBrowser());

                uri = PathTypeTools
                        .convertContextPathToAbsoluteType(writer
                                .getHtmlComponentRenderContext()
                                .getFacesContext(), uri);

                sl.add(new FileItemSource(uri, null, null, true));
            }
        }

        if (requiredSets != null) {

        }

        return sl.toArray(new FileItemSource[sl.size()]);
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component) {
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {
        return new CssFilesCollectorDecorator(component);
    }
}
