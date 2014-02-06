/*
 * $Id: DataGridService.java,v 1.5 2013/12/11 10:19:48 jbmeslin Exp $
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
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.DataColumnComponent;
import org.rcfaces.core.component.DataGridComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.tools.CriteriaTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.model.DefaultSortedComponent;
import org.rcfaces.core.model.ISelectedCriteria;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlProcessContextImpl;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.renderer.DataGridRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:19:48 $
 */
public class DataGridService extends AbstractHtmlService {
	

	private static final String SERVICE_ID = Constants.getPackagePrefix()
			+ ".DataGrid";

	private static final Log LOG = LogFactory.getLog(DataGridService.class);

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final String DATAGRID_SERVICE_VERSION = "1.0.0";

	public DataGridService() {
	}

	public static DataGridService getInstance(FacesContext facesContext) {

		IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
				facesContext).getServicesRegistry();
		if (serviceRegistry == null) {
			// Designer mode
			return null;
		}

		return (DataGridService) serviceRegistry.getService(facesContext,
				RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
	}

	public void service(FacesContext facesContext, String commandId) {
		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();

		UIViewRoot viewRoot = facesContext.getViewRoot();

		String dataGridId = (String) parameters.get("gridId");
		if (dataGridId == null) {
			sendJsError(facesContext, null, INVALID_PARAMETER_SERVICE_ERROR,
					"Can not find 'dataGridId' parameter.", null);
			return;
		}
		
		
		if (viewRoot.getChildCount() == 0) {
			sendJsError(facesContext, dataGridId,
					SESSION_EXPIRED_SERVICE_ERROR, "No view !", null);
			return;
		}

		ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
				facesContext, dataGridId);
		if (localizedComponent == null) {
			// Cas special: la session a du expir�e ....

			sendJsError(facesContext, dataGridId,
					INVALID_PARAMETER_SERVICE_ERROR,
					"Component is not found !", null);

			return;
		}

		try {
			UIComponent component = localizedComponent.getComponent();

			if ((component instanceof DataGridComponent) == false) {
				sendJsError(facesContext, dataGridId,
						INVALID_PARAMETER_SERVICE_ERROR,
						"Can not find dataGridComponent (id='" + dataGridId
								+ "').", null);
				return;
			}

			
			DataGridComponent dgc = (DataGridComponent) component;
			
			processDataGridRefresh(facesContext, dgc, dataGridId);

		} finally {
			localizedComponent.end();
		}

		facesContext.responseComplete();
	}
	
	protected void processDataGridRefresh(FacesContext facesContext,
			DataGridComponent dataGridComponent, String dataGridId) {
		
		 Map parameters = facesContext.getExternalContext()
                 .getRequestParameterMap();
		 
		String index_s = (String) parameters.get("index");
		if (index_s == null) {
			sendJsError(facesContext, dataGridId,
					INVALID_PARAMETER_SERVICE_ERROR,
					"Can not find 'index' parameter.", null);
			return;
		}

		String forcedRows_s = (String) parameters.get("rows");
		 
		 boolean unknownRowCount = "true".equals(parameters
					.get("unknownRowCount"));

			String filterExpression = (String) parameters.get("filterExpression");

			int rowIndex = Integer.parseInt(index_s);
			int forcedRows = -1;
			if (forcedRows_s != null && forcedRows_s.length() > 0) {
				forcedRows = Integer.parseInt(forcedRows_s);
				if (forcedRows < 1) {
					forcedRows = -1;
				}
			}

			String showAdditional = (String) parameters.get("showAdditional");
			String hideAdditional = (String) parameters.get("hideAdditional");
		
			/*
			 * JBM : En jsf2 le decode est déjà fait dans la phase applyrequest.
			 * Il nous reste a supprimer les index qu'on a utilisé pendant le decode pour avoir les bons pendant l'encode
			 * */
			dataGridComponent.clearDecodedIndex();
			
			ISortedComponent sortedComponents[] = null;

			String sortIndex_s = (String) parameters.get("sortIndex");
			if (sortIndex_s != null) {
				DataColumnComponent columns[] = dataGridComponent.listDataColumns().toArray();

				StringTokenizer st1 = new StringTokenizer(sortIndex_s, ", ");

				sortedComponents = new ISortedComponent[st1.countTokens() / 2];

				for (int i = 0; st1.hasMoreTokens(); i++) {
					String tok1 = st1.nextToken();
					String tok2 = st1.nextToken();

					int idx = Integer.parseInt(tok1);
					boolean order = "true".equalsIgnoreCase(tok2);

					sortedComponents[i] = new DefaultSortedComponent(
							columns[idx], idx, order);
				}
			}

			ISelectedCriteria[] criteriaConfigs = null;
			String criteria_s = (String) parameters.get("criteria");
			criteriaConfigs = CriteriaTools.computeCriteriaConfigs(
					facesContext, dataGridComponent, criteria_s);

			DataGridRenderer dgr = getDataGridRenderer(facesContext, dataGridComponent);
			if (dgr == null) {
				sendJsError(facesContext, dataGridId,
						INVALID_PARAMETER_SERVICE_ERROR,
						"Can not find dataGridRenderer.", null);
				return;
			}

			ServletResponse response = (ServletResponse) facesContext
					.getExternalContext().getResponse();

			setNoCache(response);
			response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
					+ "; charset=" + RESPONSE_CHARSET);

			setCameliaResponse(response, DATAGRID_SERVICE_VERSION);

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

				writeJs(facesContext, printWriter, dataGridComponent, dataGridId, dgr,
						rowIndex, forcedRows, sortedComponents,
						filterExpression, unknownRowCount, showAdditional,
						hideAdditional, criteriaConfigs);

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

	private DataGridRenderer getDataGridRenderer(FacesContext facesContext,
			DataGridComponent component) {

		Renderer renderer = getRenderer(facesContext, component);

		if ((renderer instanceof DataGridRenderer) == false) {
			return null;
		}

		return (DataGridRenderer) renderer;
	}

	private void decodeSubComponents(FacesContext facesContext,
			DataGridComponent dgc, Map parameters) {

		int first = -1;
		int rows = -1;

		String serializedFirst = (String) parameters.get("serializedFirst");
		if (serializedFirst != null) {
			first = Integer.parseInt(serializedFirst);
		}

		String serializedRows = (String) parameters.get("serializedRows");
		if (serializedRows != null) {
			rows = Integer.parseInt(serializedRows);
		}

		if (first < 0 || rows < 1) {
			return;
		}

		
		dgc.setFirst(first);
	    dgc.setRows(rows);
	    
		String serializedIndexes = (String) parameters.get("serializedIndexes");
		if (serializedIndexes != null) {
			for (StringTokenizer st = new StringTokenizer(serializedIndexes,
					","); st.hasMoreTokens();) {

				int zfirst = Integer.parseInt(st.nextToken());
				int zrows = Integer.parseInt(st.nextToken());

				dgc.addDecodedIndexes(zfirst, zrows);
			}
		}

		//dgc.processDecodes(facesContext);
	}

	private void writeJs(FacesContext facesContext, PrintWriter printWriter,
			DataGridComponent dgc, String componentClientId,
			DataGridRenderer dgr, int rowIndex, int forcedRows,
			ISortedComponent sortedComponents[], String filterExpression,
			boolean unknownRowCount, String showAdditional,
			String hideAdditional, ISelectedCriteria[] criteriaContainers)
			throws IOException {

		IProcessContext processContext = HtmlProcessContextImpl
				.getHtmlProcessContext(facesContext);

		CharArrayWriter cw = null;
		PrintWriter pw = printWriter;
		if (LOG.isTraceEnabled()) {
			cw = new CharArrayWriter(2000);
			pw = new PrintWriter(cw);
		}
		
		

		IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
				pw, RESPONSE_CHARSET, dgc, componentClientId);

		DataGridRenderer.DataGridRenderContext tableContext = dgr
				.createTableContext(processContext,
						jsWriter.getJavaScriptRenderContext(), dgc, rowIndex,
						forcedRows, sortedComponents, filterExpression,
						showAdditional, hideAdditional, criteriaContainers);

		String varId = jsWriter.getComponentVarName();

		jsWriter.write("var ").write(varId).write('=')
				.writeCall("f_core", "GetElementByClientId")
				.writeString(componentClientId).writeln(", document);");

		jsWriter.writeMethodCall("f_startNewPage").writeInt(rowIndex)
				.writeln(");");

		String rowVarId = jsWriter.getJavaScriptRenderContext()
				.allocateVarName();
		tableContext.setRowVarName(rowVarId);

		dgr.encodeJsTransactionalRows(jsWriter, tableContext, false,
				unknownRowCount);
		
		dgc.setFirst(rowIndex);

		
		jsWriter.writeMethodCall("f_updateNewPage").writeln(");");

		//if (hasAdditionalInformations(dgc)) {

			String viewStateId = saveViewAndReturnStateId(facesContext);
			if (viewStateId != null) {
				jsWriter.writeCall("f_classLoader", "ChangeJsfViewId")
						.write(varId).write(',').writeString(viewStateId)
						.write(')');
			}
		//}
		
		

		if (LOG.isTraceEnabled()) {
			pw.flush();

			LOG.trace(cw.toString());

			printWriter.write(cw.toCharArray());
		}
	}

	private boolean hasAdditionalInformations(DataGridComponent dgc) {
		int count = dgc.listAdditionalInformations().count();

		if (count > 0) {
			return true;
		}
		return false;
	}

}