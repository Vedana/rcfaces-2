/*
 * $Id: ItemsService.java,v 1.4 2013/12/27 11:16:21 jbmeslin Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.capability.IMaxResultNumberCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.lang.ApplicationException;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IFilteredItemsRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/27 11:16:21 $
 */
public class ItemsService extends AbstractHtmlService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".Items";

    private static final Log LOG = LogFactory.getLog(ItemsService.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final int INITIAL_SIZE = 8000;

    private static final int ITEMS_SERVER_INTERNAL_ERROR = SERVICE_ERROR | 0x100;

    private static final String ITEMS_SERVICE_VERSION = "1.0.0";

    public ItemsService() {
    }

    public static ItemsService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (ItemsService) serviceRegistry.getService(facesContext,
                RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
    }

    public void service(FacesContext facesContext, String commandId) {
        Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();

        UIViewRoot viewRoot = facesContext.getViewRoot();

        String componentId = (String) parameters.get("componentId");
        if (componentId == null) {
            sendJsError(facesContext, null, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find 'componentId' parameter.", null);
            return;
        }

        if (viewRoot.getChildCount() == 0) {
            sendJsError(facesContext, componentId,
                    SESSION_EXPIRED_SERVICE_ERROR, "No view !", null);
            return;
        }

      

        ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
                facesContext, componentId);

        if (localizedComponent == null) {
            // Cas special: la session a du expiree .... !?

            sendJsError(facesContext, componentId,
                    INVALID_PARAMETER_SERVICE_ERROR,
                    "Component is not found !", null);

            return;
        }

        UIComponent component = localizedComponent.getComponent();

        try {

            if ((component instanceof IFilterCapability) == false) {
                sendJsError(facesContext, componentId,
                        INVALID_PARAMETER_SERVICE_ERROR, "Component (id='"
                                + componentId + "') can not be filtred.", null);
                return;
            }

           

            IFilterCapability filterCapability = (IFilterCapability) component;
            
            processItemsRefresh(facesContext, filterCapability, componentId);

           

        } finally {
            localizedComponent.end();
        }

        facesContext.responseComplete();
    }
    
    
    protected void processItemsRefresh(FacesContext facesContext,
			IFilterCapability filterCapability, String componentId) {
    	
    	 Map parameters = facesContext.getExternalContext()
                 .getRequestParameterMap();
    	
    	 int maxResultNumber = -1;
         if (filterCapability instanceof IMaxResultNumberCapability) {
             String s = (String) parameters.get("maxResultNumber");
             if (s != null) {
                 maxResultNumber = Integer.parseInt(s);
             }
         }
    	
    	  String requestId = (String) parameters.get("requestId");

          String filterExpression = (String) parameters.get("filterExpression");
    	
    	 IFilteredItemsRenderer filtredItemsRenderer;
         try {
             filtredItemsRenderer = getFilteredItemsRenderer(facesContext,
                     filterCapability);

         } catch (ApplicationException ex) {
             sendJsError(facesContext, ex, componentId);
             return;
         }

         if (filtredItemsRenderer == null) {
             sendJsError(facesContext, componentId,
                     INVALID_PARAMETER_SERVICE_ERROR,
                     "Can not find filtredItemsRenderer. (componentId='"
                             + componentId + "')", null);
             return;
         }

         ServletResponse response = (ServletResponse) facesContext
                 .getExternalContext().getResponse();

         setNoCache(response);
         response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
                 + "; charset=" + RESPONSE_CHARSET);

         setCameliaResponse(response, ITEMS_SERVICE_VERSION);

         boolean useGzip = canUseGzip(facesContext);

         PrintWriter printWriter = null;
         try {

             if (useGzip == false) {
                 printWriter = response.getWriter();

             } else {
                 ConfiguredHttpServlet
                         .setGzipContentEncoding((HttpServletResponse) response, true);

                 OutputStream outputStream = response.getOutputStream();

                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
                         outputStream, DEFAULT_BUFFER_SIZE);

                 Writer writer = new OutputStreamWriter(gzipOutputStream,
                         RESPONSE_CHARSET);

                 printWriter = new PrintWriter(writer, false);
             }

             IProcessContext processContext = AbstractProcessContext
                     .getProcessContext(facesContext);

             IFilterProperties filterProperties = HtmlTools
                     .decodeFilterExpression(processContext, (UIComponent) filterCapability,
                             filterExpression);

             writeJs(facesContext, printWriter, filterCapability,
                     componentId, filtredItemsRenderer, filterProperties,
                     maxResultNumber, requestId);

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
		
	}

    private IFilteredItemsRenderer getFilteredItemsRenderer(
            FacesContext facesContext, IFilterCapability component) {

        Renderer renderer = getRenderer(facesContext, (UIComponent) component);

        if (renderer == null
                || (renderer instanceof IFilteredItemsRenderer) == false) {
            return null;
        }

        return (IFilteredItemsRenderer) renderer;
    }

    private void writeJs(FacesContext facesContext, PrintWriter printWriter,
            IFilterCapability component, String componentId,
            IFilteredItemsRenderer dgr, IFilterProperties filterProperties,
            int maxResultNumber, String requestId) throws IOException {

        CharArrayWriter cw = null;
        PrintWriter pw = printWriter;
        if (true) {
            cw = new CharArrayWriter(2000);
            pw = new PrintWriter(cw);
        }

        IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
                pw, RESPONSE_CHARSET, (UIComponent) component, componentId);

        String varId = jsWriter.getComponentVarName();

        jsWriter.write("var ").write(varId).write('=')
                .writeCall("f_core", "GetElementByClientId")
                .writeString(componentId).writeln(", document);");

        try {
            if (requestId != null) {
                jsWriter.writeMethodCall("f_startResponse")
                        .writeString(requestId).writeln(");");
            }

        dgr.encodeFilteredItems(jsWriter, component, filterProperties,
                maxResultNumber);

            if (requestId != null) {
                jsWriter.writeMethodCall("f_endResponse")
                        .writeString(requestId).writeln(");");
            }

            pw.flush();

            if (LOG.isTraceEnabled()) {
            LOG.trace(cw.toString());
            }

            printWriter.write(cw.toCharArray());

        } catch (RuntimeException ex) {
            sendException(facesContext, printWriter, (UIComponent) component,
                    componentId, ex);
        }
        }

    protected void sendException(FacesContext facesContext,
            PrintWriter printWriter, UIComponent component, String componentId,
            RuntimeException ex) throws WriterException {
        IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
                printWriter, RESPONSE_CHARSET, component, componentId);

        String varId = jsWriter.getComponentVarName();

        jsWriter.write("var ").write(varId).write('=')
                .writeCall("f_core", "GetElementByClientId")
                .writeString(componentId).writeln(", document);");

        jsWriter.writeCall(varId, "f_performErrorEvent").write(null).write(',')
                .writeInt(1).write(',').writeString(ex.getMessage())
                .writeln(");");
    }
}