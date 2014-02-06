/*
 * $Id: ImageService.java,v 1.4 2014/01/03 11:12:23 jbmeslin Exp $
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
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ImageComponent;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.image.GeneratedImageInformation;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2014/01/03 11:12:23 $
 */
public class ImageService extends AbstractHtmlService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".Image";

    private static final Log LOG = LogFactory.getLog(ImageService.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final int INITIAL_SIZE = 8000;

    private static final String IMAGE_SERVICE_VERSION = "1.0.0";

    public ImageService() {
    }

    public static ImageService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (ImageService) serviceRegistry.getService(facesContext,
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
            // Cas special: la session a du expir�e ....

            sendJsError(facesContext, componentId,
                    INVALID_PARAMETER_SERVICE_ERROR,
                    "Component is not found !", null);

            return;
        }

        try {

            UIComponent component = localizedComponent.getComponent();

            if ((component instanceof ImageComponent) == false) {
                sendJsError(
                        facesContext,
                        componentId,
                        INVALID_PARAMETER_SERVICE_ERROR,
                        "Component can not be filtred. (not an ImageComponent)",
                        null);
                return;
            }

            ImageComponent imageComponent = (ImageComponent) component;
            
            processImageService(facesContext, imageComponent, componentId);

           

        } finally {
            localizedComponent.end();
        }

        facesContext.responseComplete();
    }
    
    
   protected void processImageService(FacesContext facesContext,
    		ImageComponent imageComponent , String componentId) {
	   
	   Map parameters = facesContext.getExternalContext()
               .getRequestParameterMap();
	   
	   String filterExpression = (String) parameters.get("filterExpression");
	   
	   ServletResponse response = (ServletResponse) facesContext
               .getExternalContext().getResponse();

       setNoCache(response);
       response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
               + "; charset=" + RESPONSE_CHARSET);

       setCameliaResponse(response, IMAGE_SERVICE_VERSION);

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

           IFilterProperties filterProperties = HtmlTools
                   .decodeFilterExpression(null, (UIComponent) imageComponent,
                           filterExpression);

           IImageAccessors contentAccessors = (IImageAccessors) imageComponent
                   .getImageAccessors(facesContext);

           IContentAccessor imageAccessor = contentAccessors
                   .getImageAccessor();

           GeneratedImageInformation generatedImageInformation = new GeneratedImageInformation();

           BasicGenerationResourceInformation generationInformation = new BasicGenerationResourceInformation(
                   imageComponent, componentId);

           generationInformation.setFilterProperties(filterProperties);

           String url = null;
           if (imageAccessor != null) {
               url = imageAccessor.resolveURL(facesContext,
                       generatedImageInformation, generationInformation);
           }

           writeJs(facesContext, printWriter, imageComponent, componentId,
                   url, generatedImageInformation);

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

    private void writeJs(FacesContext facesContext, PrintWriter printWriter,
            IFilterCapability component, String componentId, String imageURL,
            GeneratedImageInformation imageContentInformation)
            throws IOException {

        CharArrayWriter cw = null;
        PrintWriter pw = printWriter;
        if (LOG.isTraceEnabled()) {
            cw = new CharArrayWriter(2000);
            pw = new PrintWriter(cw);
        }

        IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
                pw, RESPONSE_CHARSET, (UIComponent) component, componentId);

        String varId = jsWriter.getComponentVarName();

        jsWriter.write("var ").write(varId).write('=').writeCall("f_core",
                "GetElementByClientId").writeString(componentId).writeln(");");

        int width = imageContentInformation.getImageWidth();
        int height = imageContentInformation.getImageHeight();
        if (width > 0 && height > 0) {
            jsWriter.writeMethodCall("f_setImageSize").writeInt(width).write(
                    ',').writeInt(height).writeln(");");
        }

        jsWriter.writeMethodCall("f_setImageURL").writeString(imageURL)
                .writeln(");");

        if (LOG.isTraceEnabled()) {
            pw.flush();

            LOG.trace(cw.toString());

            printWriter.write(cw.toCharArray());
        }
    }
}