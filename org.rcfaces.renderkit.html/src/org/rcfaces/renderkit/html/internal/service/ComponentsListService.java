/*
 * $Id: ComponentsListService.java,v 1.4 2013/12/19 15:46:46 jbmeslin Exp $
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
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlRenderContext;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.renderer.ComponentsListRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/19 15:46:46 $
 */
public class ComponentsListService extends AbstractHtmlService {
	

	private static final String SERVICE_ID = Constants.getPackagePrefix()
			+ ".ComponentsList";

	private static final Log LOG = LogFactory
			.getLog(ComponentsListService.class);

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final int INITIAL_SIZE = 8000;

	private static final String RENDER_CONTEXT_STATE = "camelia.cls.renderContext";

	private static final String COMPONENTS_LIST_SERVICE_VERSION = "1.0.0";

	public ComponentsListService() {
	}

	public static ComponentsListService getInstance(FacesContext facesContext) {

		IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
				facesContext).getServicesRegistry();
		if (serviceRegistry == null) {
			// Designer mode
			return null;
		}

		return (ComponentsListService) serviceRegistry.getService(facesContext,
				RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
	}

	public void service(FacesContext facesContext, String commandId) {
		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();

		String componentsListId = (String) parameters.get("componentsListId");
		if (componentsListId == null) {
			sendJsError(facesContext, null, INVALID_PARAMETER_SERVICE_ERROR,
					"Can not find 'componentsListId' parameter.", null);
			return;
		}

		UIViewRoot viewRoot = facesContext.getViewRoot();
		if (viewRoot.getChildCount() == 0) {
			sendJsError(facesContext, componentsListId,
					SESSION_EXPIRED_SERVICE_ERROR, "No view !", null);
			return;
		}


		ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
				facesContext, componentsListId);
		if (localizedComponent == null) {
			// Cas special: la session a du expir�e ....

			sendJsError(facesContext, componentsListId,
					INVALID_PARAMETER_SERVICE_ERROR,
					"Can not find componentsListComponent (id='"
							+ componentsListId + "').", null);

			return;
		}

		
		UIComponent component = localizedComponent.getComponent();
		
		if ((component instanceof ComponentsListComponent) == false) {
			sendJsError(facesContext, componentsListId,
					INVALID_PARAMETER_SERVICE_ERROR,
					"Invalid componentsListComponent (id='"
							+ componentsListId + "').", null);
			return;
		}
		try {
			ComponentsListComponent componentList = (ComponentsListComponent) component;
			
			processListUpdate(facesContext, componentList, componentsListId);
		
		} finally {
			localizedComponent.end();
		}

		facesContext.responseComplete();
	}

	public void processListUpdate(FacesContext facesContext,
				ComponentsListComponent dgc, String componentsListId){
		
			Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();
	
			String filterExpression = (String) parameters.get("filterExpression");

			
			String index_s = (String) parameters.get("index");
			if (index_s == null) {
				sendJsError(facesContext, componentsListId,
						INVALID_PARAMETER_SERVICE_ERROR,
						"Can not find 'index' parameter.", null);
				return;
			}
			int rowIndex = Integer.parseInt(index_s);
			
			ISortedComponent sortedComponents[] = null;

			ComponentsListRenderer dgr = getDataListRenderer(facesContext, dgc);
			if (dgr == null) {
				sendJsError(facesContext, componentsListId,
						INVALID_PARAMETER_SERVICE_ERROR,
						"Can not find componentsListRenderer. (dataListId='"
								+ componentsListId + "')", null);
				return;
			}

			ServletResponse response = (ServletResponse) facesContext
					.getExternalContext().getResponse();

			setNoCache(response);
			response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
					+ "; charset=" + RESPONSE_CHARSET);
			setCameliaResponse(response, COMPONENTS_LIST_SERVICE_VERSION);

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

				writeJs(facesContext, printWriter, dgc, componentsListId, dgr,
						rowIndex, sortedComponents, filterExpression);

			} catch (IOException ex) {

				throw new FacesException("Can not write componentsList rows !",
						ex);

			} catch (RuntimeException ex) {
				LOG.error("Catch runtime exception !", ex);

				throw ex;

			} finally {
				if (printWriter != null) {
					printWriter.close();
				}
			}

	
	}

	private ComponentsListRenderer getDataListRenderer(
			FacesContext facesContext, ComponentsListComponent component) {

		Renderer renderer = getRenderer(facesContext, component);

		if ((renderer instanceof ComponentsListRenderer) == false) {
			return null;
		}

		return (ComponentsListRenderer) renderer;
	}

	private void writeJs(FacesContext facesContext, PrintWriter printWriter,
			ComponentsListComponent dgc, String componentClientId,
			ComponentsListRenderer dgr, int rowIndex,
			ISortedComponent sortedComponents[], String filterExpression)
			throws IOException {

		ComponentsListRenderer.ListContext listContext = dgr
				.createListContext(facesContext, dgc, rowIndex,
						sortedComponents, filterExpression);

		CharArrayWriter cw = null;
		PrintWriter pw = printWriter;
		if (LOG.isTraceEnabled()) {
			cw = new CharArrayWriter(2000);
			pw = new PrintWriter(cw);
		}

		Object states[] = (Object[]) dgc.getAttributes().get(
				RENDER_CONTEXT_STATE);
		String contentType = (String) states[1];

		IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
				pw, RESPONSE_CHARSET, dgc, componentClientId);

		String varId = jsWriter.getComponentVarName();

		jsWriter.write("var ").write(varId).write('=')
				.writeCall("f_core", "GetElementByClientId")
				.writeString(componentClientId).writeln(", document);");

		jsWriter.writeMethodCall("f_startNewPage").writeInt(rowIndex)
				.writeln(");");

		ResponseWriter oldWriter = facesContext.getResponseWriter();
		ResponseStream oldStream = facesContext.getResponseStream();

		try {
			CharArrayWriter myWriter = new CharArrayWriter(INITIAL_SIZE);

			ResponseWriter newWriter = facesContext.getRenderKit()
					.createResponseWriter(myWriter, contentType,
							RESPONSE_CHARSET);

			facesContext.setResponseWriter(newWriter);

			IRenderContext renderContext = HtmlRenderContext
					.restoreRenderContext(facesContext, states[0], true);

			renderContext.pushComponent(dgc, componentClientId);

			IComponentWriter writer = renderContext.getComponentWriter();

			// IComponentTreeRenderProcessor
			// componentTreeRenderProcessor=ComponentTreeRenderProcessorFactory.get(facesContext)

			dgr.encodeChildren(writer, listContext);

			newWriter.flush();

			String buffer = myWriter.toString();

			int rowCount = 10;

			jsWriter.writeMethodCall("f_updateNewPage").writeInt(rowCount)
					.write(',').writeString(buffer).writeln(");");

			String viewStateId = saveViewAndReturnStateId(facesContext);

			if (viewStateId != null) {
				jsWriter.writeCall("f_classLoader", "ChangeJsfViewId")
						.write(varId).write(',').writeString(viewStateId)
						.write(')');
			}

		} finally {
			if (oldWriter != null) {
				facesContext.setResponseWriter(oldWriter);
			}

			if (oldStream != null) {
				facesContext.setResponseStream(oldStream);
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
