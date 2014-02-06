/*
 * $Id: KeyLabelService.java,v 1.4 2014/01/03 11:12:23 jbmeslin Exp $
 */

package org.rcfaces.renderkit.html.internal.service;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ImageComponent;
import org.rcfaces.core.component.KeyLabelComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.renderer.KeyLabelRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2014/01/03 11:12:23 $
 */
public class KeyLabelService extends AbstractHtmlService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".KeyLabel";

    private static final Log LOG = LogFactory.getLog(KeyLabelService.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final int INITIAL_SIZE = 8000;

    private static final String KEYLABEL_SERVICE_VERSION = "1.0.0";

    public KeyLabelService() {
    }

    public static KeyLabelService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (KeyLabelService) serviceRegistry.getService(facesContext,
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

            if ((component instanceof KeyLabelComponent) == false) {
                sendJsError(
                        facesContext,
                        componentId,
                        INVALID_PARAMETER_SERVICE_ERROR,
                        "Component can not be filtred. (not an KeyLabelComponent)",
                        null);
                return;
            }

            KeyLabelComponent keyLabelComponent = (KeyLabelComponent) component;
            processKeyLabelService(facesContext, keyLabelComponent, componentId);
            

        } finally {
            localizedComponent.end();
        }

        facesContext.responseComplete();
    }
    
    protected void processKeyLabelService(FacesContext facesContext,
    		KeyLabelComponent keyLabelComponent, String componentId) {
    	
    	Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();
    	
    	 String filterExpression = (String) parameters.get("filterExpression");
    	
    	ServletResponse response = (ServletResponse) facesContext
                .getExternalContext().getResponse();

        setNoCache(response);
        response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
                + "; charset=" + RESPONSE_CHARSET);

        setCameliaResponse(response, KEYLABEL_SERVICE_VERSION);

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

            IFilterProperties filterProperties = HtmlTools
                    .decodeFilterExpression(null, keyLabelComponent,
                            filterExpression);

            KeyLabelRenderer keyLabelRenderer = getKeyLabelRenderer(
                    facesContext, keyLabelComponent);

            writeJs(facesContext, printWriter, keyLabelComponent,
                    keyLabelRenderer, componentId, filterProperties);

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

    private KeyLabelRenderer getKeyLabelRenderer(FacesContext facesContext,
            KeyLabelComponent keyLabelComponent) {

        Renderer renderer = getRenderer(facesContext, keyLabelComponent);

        if ((renderer instanceof KeyLabelRenderer) == false) {
            return null;
        }

        return (KeyLabelRenderer) renderer;
    }

    private void writeJs(FacesContext facesContext, PrintWriter printWriter,
            KeyLabelComponent component, KeyLabelRenderer renderer,
            String componentId, IFilterProperties filterProperties)
            throws IOException {

        CharArrayWriter cw = null;
        PrintWriter pw = printWriter;
        if (LOG.isTraceEnabled()) {
            cw = new CharArrayWriter(2000);
            pw = new PrintWriter(cw);
        }

        IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
                pw, RESPONSE_CHARSET, component, componentId);

        String varId = jsWriter.getComponentVarName();

        jsWriter.write("var ").write(varId).write('=')
                .writeCall("f_core", "GetElementByClientId")
                .writeString(componentId).writeln(");");

        component.setFilterProperties(filterProperties);

        Map<SelectItem, String> styles = new HashMap<SelectItem, String>();
        SelectItem sis[] = renderer.computeSelectItems(component,
                filterProperties, styles);

        boolean first = true;
        jsWriter.writeMethodCall("_updateItems");
        for (int i = 0; i < sis.length; i++) {
            if (first == false) {
                jsWriter.write(',');
            }
            if (writeJsItem(jsWriter, component, sis[i], styles.get(sis[i]))) {
                first = false;
            }
        }

        jsWriter.writeln(");");

        if (LOG.isTraceEnabled()) {
            pw.flush();

            LOG.trace(cw.toString());

            printWriter.write(cw.toCharArray());
        }
    }

    protected boolean writeJsItem(IJavaScriptWriter jsWriter,
            KeyLabelComponent keyLabelComponent, SelectItem selectItem,
            String computedStyleClass) throws WriterException {

        Object value = selectItem.getValue();
        String label = selectItem.getLabel();
        if (label == null || label.trim().length() < 1) {
            return false;
        }

        label = ParamUtils.formatMessage(keyLabelComponent, label);

        IObjectLiteralWriter ow = jsWriter.writeObjectLiteral(true);

        ow.writeSymbol("_label").writeString(label);

        if (label.equals(value) == false) {

            String svalue = ValuesTools.valueToString(value, keyLabelComponent,
                    jsWriter.getFacesContext());

            if (svalue != null) {
                ow.writeSymbol("_value").writeString(svalue);
            }
        }

        if (selectItem.isDisabled()) {
            ow.writeSymbol("_disabled").writeBoolean(true);
        }

        if (selectItem instanceof IStyleClassItem) {
            IStyleClassItem sc = (IStyleClassItem) selectItem;

            String stc = sc.getStyleClass();
            if (stc != null) {
                ow.writeSymbol("_styleClass").writeString(stc);
            }
        }

        if (computedStyleClass != null) {
            ow.writeSymbol("_computedStyleClass").writeString(
                    computedStyleClass);
        }

        ow.end();

        return true;
    }
}