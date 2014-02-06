/*
 * $Id: PartialRenderingService.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsGridComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.util.PartialRenderingContextImpl;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.partialRendering.IPartialRenderingContext;
import org.rcfaces.core.partialRendering.PartialRenderingContextFactory;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlProcessContextImpl;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.renderer.ComponentsGridRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public class PartialRenderingService extends AbstractHtmlService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".PartialRendering";

    private static final Log LOG = LogFactory
            .getLog(PartialRenderingService.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final int INITIAL_SIZE = 8000;

    private static final String RENDER_CONTEXT_STATE = "camelia.cls.renderContext";

    private static final String PARTIAL_RENDERING_SERVICE_VERSION = "1.0.0";

    public PartialRenderingService() {
    }

    public static PartialRenderingService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (PartialRenderingService) serviceRegistry.getService(
                facesContext, RenderKitFactory.HTML_BASIC_RENDER_KIT,
                SERVICE_ID);
    }

    public void service(FacesContext facesContext, String commandId) {
        Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();

        String sourceComponentId = (String) parameters.get("sourceComponentId");
        if (sourceComponentId == null) {
            sendJsError(facesContext, null, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find 'sourceComponentId' parameter.", null);
            return;
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot.getChildCount() == 0) {
            sendJsError(facesContext, sourceComponentId,
                    SESSION_EXPIRED_SERVICE_ERROR, "No view !", null);
            return;
        }

        ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
                facesContext, sourceComponentId);
        if (localizedComponent == null) {
            // Cas special: la session a du expir�e ....

            sendJsError(facesContext, sourceComponentId,
                    INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find sourceComponentId component (id='"
                            + sourceComponentId + "').", null);

            return;
        }

        try {
            UIComponent sourceComponent = localizedComponent.getComponent();

            if ((sourceComponent instanceof ComponentsGridComponent) == false) {
                sendJsError(facesContext, sourceComponentId,
                        INVALID_PARAMETER_SERVICE_ERROR,
                        "Invalid sourceComponentId component (id='"
                                + sourceComponentId + "').", null);
                return;
            }

            viewRoot.decode(facesContext);

            // Execute l'evenement
            IPartialRenderingContext partialRenderingContext = PartialRenderingContextFactory
                    .get(facesContext);

            ServletResponse response = (ServletResponse) facesContext
                    .getExternalContext().getResponse();

            setNoCache(response);
            response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
                    + "; charset=" + RESPONSE_CHARSET);
            setCameliaResponse(response, PARTIAL_RENDERING_SERVICE_VERSION);

            boolean useGzip = canUseGzip(facesContext);

            PrintWriter printWriter = null;
            try {

                if (useGzip == false) {
                    printWriter = response.getWriter();

                } else {
                    ConfiguredHttpServlet.setGzipContentEncoding(
                            (HttpServletResponse) response, true);

                    OutputStream outputStream = response.getOutputStream();

                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
                            outputStream, DEFAULT_BUFFER_SIZE);

                    Writer writer = new OutputStreamWriter(gzipOutputStream,
                            RESPONSE_CHARSET);

                    printWriter = new PrintWriter(writer, false);
                }

                writeJs(facesContext, printWriter, sourceComponent,
                        sourceComponentId, (PartialRenderingContextImpl) partialRenderingContext);

            } catch (IOException ex) {
                throw new FacesException(
                        "Can not write dataGrid javascript rows !", ex);

            } catch (RuntimeException ex) {
                LOG.error("Catch runtime exception !", ex);

                throw ex;

            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        } finally {
            localizedComponent.end();
        }
        facesContext.responseComplete();
    }

    private ComponentsGridRenderer getComponentsGridRenderer(
            FacesContext facesContext, ComponentsGridComponent component) {

        Renderer renderer = getRenderer(facesContext, component);

        if ((renderer instanceof ComponentsGridRenderer) == false) {
            LOG.error("Renderer is not a valid type (AbstractGridRenderer) => "
                    + renderer);
            return null;
        }

        return (ComponentsGridRenderer) renderer;
    }

    private void writeJs(FacesContext facesContext, PrintWriter printWriter,
            UIComponent sourceComponent, String sourceClientId,
            PartialRenderingContextImpl partialRenderingContext)
            throws IOException {

        IProcessContext processContext = HtmlProcessContextImpl
                .getHtmlProcessContext(facesContext);

        CharArrayWriter cw = null;
        PrintWriter pw = printWriter;
        if (LOG.isTraceEnabled()) {
            cw = new CharArrayWriter(2000);
            pw = new PrintWriter(cw);
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();

        JavaScriptResponseWriter jsWriter = new JavaScriptResponseWriter(
                facesContext, pw, RESPONSE_CHARSET, sourceComponent,
                sourceClientId);

        ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        ResponseStream oldResponseStream = facesContext.getResponseStream();

        try {
            CharArrayWriter myWriter = new CharArrayWriter(1);

            /*
            ResponseWriter newWriter = facesContext.getRenderKit()
                    .createResponseWriter(myWriter, contentType,
                            RESPONSE_CHARSET);

            facesContext.setResponseWriter(newWriter);

            IRenderContext renderContext = HtmlRenderContext
                    .restoreRenderContext(facesContext, states[0], true);

            renderContext.pushComponent(dgc, componentClientId);

            jsWriter.setRenderContext(renderContext);

            // IComponentTreeRenderProcessor
            // componentTreeRenderProcessor=ComponentTreeRenderProcessorFactory.
            // get(facesContext)

            ComponentsGridRenderer.ComponentsGridRenderContext listContext = dgr
                    .createComponentsGridContext(processContext, jsWriter
                            .getJavaScriptRenderContext(), dgc, rowIndex,
                            forcedRows, sortedComponents, filterExpression,
                            showAdditional, hideAdditional);

            int rowCount = dgr.encodeChildren(jsWriter, listContext, true,
                    unknownRowCount);

            if (rowCount >= 0) {
                jsWriter.writeMethodCall("f_setRowCount").writeInt(rowCount)
                        .writeln(");");
            }

            jsWriter.writeMethodCall("f_updateNewPage").writeln(");");
*/
            saveView(facesContext, null);

        } finally {

            if (oldResponseWriter != null) {
                facesContext.setResponseWriter(oldResponseWriter);
            }

            if (oldResponseStream != null) {
                facesContext.setResponseStream(oldResponseStream);
            }
        }

        if (LOG.isTraceEnabled()) {
            pw.flush();

            LOG.trace(cw.toString());

            printWriter.write(cw.toCharArray());
        }
    }

    public void setupComponent(IComponentRenderContext componentRenderContext) {
        UIComponent dataListComponent = componentRenderContext.getComponent();

        IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext) componentRenderContext
                .getRenderContext();

        FacesContext facesContext = htmlRenderContext.getFacesContext();

        Object state = htmlRenderContext.saveState(facesContext);

        if (state != null) {
            String contentType = facesContext.getResponseWriter()
                    .getContentType();

            dataListComponent.getAttributes().put(RENDER_CONTEXT_STATE,
                    new Object[] { state, contentType });
        }
    }
}