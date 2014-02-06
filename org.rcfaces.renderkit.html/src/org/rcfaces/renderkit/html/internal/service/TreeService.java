/*
 * $Id: TreeService.java,v 1.4 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;
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
import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.item.IVisibleItem;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlProcessContextImpl;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.decorator.ISelectItemNodeWriter;
import org.rcfaces.renderkit.html.internal.decorator.SelectItemsContext;
import org.rcfaces.renderkit.html.internal.decorator.TreeDecorator;
import org.rcfaces.renderkit.html.internal.renderer.TreeRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:10 $
 */
public class TreeService extends AbstractHtmlService {
    private static final Log LOG = LogFactory.getLog(TreeService.class);

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".Tree";

    private static final boolean LOG_TREE = false;

    // private static final boolean LOG_JAVASCRIPT = true;

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final String TREE_SERVICE_VERSION = "1.0.0";

    public TreeService() {
    }

    public static TreeService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (TreeService) serviceRegistry.getService(facesContext,
                RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
    }

    public void service(FacesContext facesContext, String commandId) {
    	
        Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();

        String treeId = (String) parameters.get("treeId");
        if (treeId == null) {
            sendJsError(facesContext, null, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find 'treeId' parameter.", null);
            return;
        }

        String waitingId = (String) parameters.get("waitingId");
        if (waitingId == null) {
            sendJsError(facesContext, treeId, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find 'waitingId' parameter.", null);
            return;
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot.getChildCount() == 0) {
            sendJsError(facesContext, treeId, SESSION_EXPIRED_SERVICE_ERROR,
                    "No view !", waitingId);
            return;
        }

        ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
                facesContext, treeId);
        if (localizedComponent == null) {
            // Cas special: la session a du expir�e ....

            sendJsError(facesContext, treeId, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find treeComponent (id='" + treeId + "').",
                    waitingId);

            return;
        }

        UIComponent component = localizedComponent.getComponent();
        try {
            
            if ((component instanceof TreeComponent) == false) {
                sendJsError(facesContext, treeId,
                        INVALID_PARAMETER_SERVICE_ERROR,
                        "Invalid treeComponent (id='" + treeId + "').",
                        waitingId);
                return;
            }

            processTreeRefresh(facesContext, (TreeComponent) component, treeId,
                    waitingId);

        } finally {
            localizedComponent.end();
        }

        facesContext.responseComplete();
    }
    

    protected void processTreeRefresh(FacesContext facesContext,
    		TreeComponent treeComponent, String treeId, String waitingId) {
    	
    	 Map parameters = facesContext.getExternalContext()
                 .getRequestParameterMap();
    	
    	 String node = (String) parameters.get("node");
    	 
         if (node == null) {
             sendJsError(facesContext, treeId, INVALID_PARAMETER_SERVICE_ERROR,
                     "Can not find 'node' parameter.", waitingId);
             return;
         }
         
         String filterExpression = (String) parameters.get("filterExpression");
    	
        TreeRenderer treeRenderer = getTreeRenderer(facesContext,
                treeComponent);
        if (treeRenderer == null) {
            sendJsError(facesContext, treeId,
                    INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find treeRenderer. (treeId='" + treeId + "')",
                    waitingId);
            return;
        }

        ServletResponse response = (ServletResponse) facesContext
                .getExternalContext().getResponse();

        setNoCache(response);
        response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
                + "; charset=" + RESPONSE_CHARSET);
        setCameliaResponse(response, TREE_SERVICE_VERSION);

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

            writeJs(facesContext, printWriter, treeComponent, treeId,
                    treeRenderer, waitingId, node, filterExpression);

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

    private TreeRenderer getTreeRenderer(FacesContext facesContext,
            TreeComponent treeComponent) {

        Renderer renderer = getRenderer(facesContext, treeComponent);

        if ((renderer instanceof TreeRenderer) == false) {
            return null;
        }

        return (TreeRenderer) renderer;
    }

    private void writeJs(FacesContext facesContext, PrintWriter printWriter,
            TreeComponent treeComponent, String treeId,
            TreeRenderer treeRenderer, String waitingId, String node,
            String filterExpression) throws IOException {

        if (filterExpression != null) {
            IFilterProperties filtersMap = HtmlTools.decodeFilterExpression(
                    null, treeComponent, filterExpression);

            treeComponent.setFilterProperties(filtersMap);
        }

        HtmlProcessContextImpl.getHtmlProcessContext(facesContext);

        CharArrayWriter cw = null;
        PrintWriter pw = printWriter;
        if (LOG.isTraceEnabled()) {
            cw = new CharArrayWriter(2000);
            pw = new PrintWriter(cw);
        }

        // String waitingVarId = "_rootNode";

        IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
                pw, RESPONSE_CHARSET, treeComponent, treeId);

        String varId = jsWriter.getComponentVarName();
        String waitingVarId = jsWriter.getJavaScriptRenderContext()
                .allocateVarName();

        jsWriter.write("var ").write(varId).write('=')
                .writeCall("f_core", "GetElementByClientId")
                .writeString(treeId).writeln(", document);");

        jsWriter.write("var ").write(waitingVarId).write('=')
                .writeMethodCall("f_getWaitingNode").write(waitingId)
                .writeln(");");

        // Le varName a utiliser est celui du waitingVarId !
        // Car les noeuds sont encod�s par rapport � un parent nomm� par le
        // componentVarName
        jsWriter.setComponentVarName(varId);

        ISelectItemNodeWriter nodeWriter = treeRenderer
                .getSelectItemNodeWriter(jsWriter
                        .getHtmlComponentRenderContext());

        FilterNodeRenderer nodeRenderer = new FilterNodeRenderer(node,
                nodeWriter);

        treeRenderer.encodeNodes(jsWriter, treeComponent, nodeRenderer,
                nodeRenderer.getDepth(), waitingVarId);

        /*
         * TreeRenderContext treeRenderContext = treeRenderer.createTreeContext(
         * facesContext, dgc, rowIndex, sortIndex, sortOrder, filterExpression);
         */

        // treeRenderer.encodeJsTransactionalRows(jsWriter, treeRenderContext);
        jsWriter.writeMethodCall("f_clearWaiting").write(waitingId)
                .writeln(");");

        if (LOG.isTraceEnabled()) {
            pw.flush();

            LOG.trace(cw.toString());

            printWriter.write(cw.toCharArray());
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:10 $
     */
    private static class FilterNodeRenderer implements ISelectItemNodeWriter {

        private final ISelectItemNodeWriter parent;

        private final int indexes[];

        private int currentIndex = 0;

        private SelectItem mainNode = null;

        private boolean mainNodeHasChild = false;

        public FilterNodeRenderer(String node, ISelectItemNodeWriter parent) {
            this.parent = parent;

            StringTokenizer st = new StringTokenizer(node, ",");
            indexes = new int[st.countTokens()];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = Integer.parseInt(st.nextToken()) + 1;
            }
        }

        public SelectItemsContext getContext() {
            return parent.getContext();
        }

        public int getDepth() {
            return indexes.length;
        }

        public void encodeNodeInit(UIComponent component, SelectItem selectItem) {

            if (selectItem instanceof IVisibleItem) {
                if (((IVisibleItem) selectItem).isVisible() == false) {
                    return;
                }
            }

            SelectItemsContext context = getContext();
            if (LOG_TREE) {
                System.out.println("Init>" + context.getDepth() + "  "
                        + selectItem.getLabel());
            }

            int depth = context.getDepth() - 1;

            if (depth >= 0 && depth < indexes.length && --indexes[depth] == 0) {
                context.setValueExpanded(selectItem, selectItem.getValue());
            }

            parent.encodeNodeInit(component, selectItem);
        }

        public int encodeNodeBegin(UIComponent component,
                SelectItem selectItem, boolean hasChild, boolean isVisible)
                throws WriterException {

            if (selectItem instanceof IVisibleItem) {
                if (((IVisibleItem) selectItem).isVisible() == false) {
                    return SKIP_NODE;
                }
            }

            int depth = getContext().getDepth() - 1;
            if (depth >= 0 && depth < indexes.length) {
                if (indexes[depth] != 0) {
                    return SKIP_NODE;
                }
                mainNode = selectItem;
                return EVAL_NODE;
            }

            mainNodeHasChild = true;
            return parent.encodeNodeBegin(component, selectItem, hasChild,
                    isVisible);
        }

        public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
                boolean hasChild, boolean isVisible) throws WriterException {
            int depth = getContext().getDepth() - 1;
            if (depth >= 0 && depth < indexes.length) {
                return;
            }

            parent.encodeNodeEnd(component, selectItem, hasChild, isVisible);
        }

        public void refreshNode(UIComponent component) throws WriterException {
            if (mainNode != null && parent instanceof TreeDecorator) {
                ((TreeDecorator) parent)
                        .refreshNode(mainNode, mainNodeHasChild);
            }
        }

    }
}