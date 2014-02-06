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
import javax.faces.convert.Converter;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.component.DataGridComponent;
import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.tools.CriteriaTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.item.CriteriaItem;
import org.rcfaces.core.model.ICriteriaSelectedResult;
import org.rcfaces.core.model.ISelectedCriteria;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * 
 * @author Olivier Oeuillot
 */
public class CriteriaGridService extends AbstractHtmlService {
	

	private static final String SERVICE_ID = Constants.getPackagePrefix()
			+ ".CriteriaGrid";

	private static final Log LOG = LogFactory.getLog(CriteriaGridService.class);

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final int INITIAL_SIZE = 8000;

	private static final String CRITERIA_SERVICE_VERSION = "1.0.0";

	public CriteriaGridService() {
	}

	public static CriteriaGridService getInstance(FacesContext facesContext) {

		IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
				facesContext).getServicesRegistry();
		if (serviceRegistry == null) {
			// Designer mode
			return null;
		}

		return (CriteriaGridService) serviceRegistry.getService(facesContext,
				RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
	}

	public void service(FacesContext facesContext, String commandId) {
		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();

		UIViewRoot viewRoot = facesContext.getViewRoot();

		String componentId = (String) parameters.get("gridId");
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

			if ((component instanceof ICriteriaManagerCapability) == false) {
				sendJsError(
						facesContext,
						componentId,
						INVALID_PARAMETER_SERVICE_ERROR,
						"Component can not be filtred by criteria. (not an ICriteriaManagerCapability)",
						null);
				return;
			}

			ICriteriaManagerCapability keyLabelComponent = (ICriteriaManagerCapability) component;
			
			ProcessCriteriaService(facesContext, keyLabelComponent, componentId);
			

		} finally {
			localizedComponent.end();
		}

		facesContext.responseComplete();
	}
	
	public void ProcessCriteriaService(FacesContext facesContext,
			ICriteriaManagerCapability keyLabelComponent, String componentId) {
		
		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();
		
		ServletResponse response = (ServletResponse) facesContext
				.getExternalContext().getResponse();

		setNoCache(response);
		response.setContentType(IHtmlRenderContext.JAVASCRIPT_TYPE
				+ "; charset=" + RESPONSE_CHARSET);

		setCameliaResponse(response, CRITERIA_SERVICE_VERSION);

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

			ISelectedCriteria[] selectedCriteria = null;
			String criteria_s = (String) parameters.get("selectedCriteria");
			if (criteria_s != null) {
				selectedCriteria = CriteriaTools.computeCriteriaConfigs(
						facesContext, (IGridComponent) keyLabelComponent,
						criteria_s);
			}

			
			String tokenId = (String) parameters.get("tokenId");
			writeJs(facesContext, printWriter, keyLabelComponent,
					componentId, selectedCriteria, tokenId);

		} catch (IOException ex) {

			throw new FacesException(
					"Can not write criteria javascript result !", ex);

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
			ICriteriaManagerCapability component, String componentId,
			ISelectedCriteria[] selectedCriteria, String tokenId)
			throws IOException {

		ICriteriaSelectedResult result = component
				.processSelectedCriteria(selectedCriteria);

		CharArrayWriter cw = null;
		PrintWriter pw = printWriter;
		if (LOG.isTraceEnabled()) {
			cw = new CharArrayWriter(2000);
			pw = new PrintWriter(cw);
		}

		IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
				pw, RESPONSE_CHARSET, (UIComponent) component, componentId);

		String varId = jsWriter.getComponentVarName();

		jsWriter.write("var ").write(varId).write('=')
				.writeCall("f_core", "GetElementByClientId")
				.writeString(componentId).writeln(");");

		// component.setFilterProperties(filterProperties);

		jsWriter.writeMethodCall("_processSelectedCriteriaResult");

		jsWriter.writeString(tokenId).write(',')
				.writeInt(result.getResultCount()).write(',');

		jsWriter.write('[');
		ICriteriaConfiguration[] resultCriteria = result
				.listAvailableCriteriaConfiguration();
		if (resultCriteria != null && resultCriteria.length > 0) {
			boolean first = true;
			for (int i = 0; i < resultCriteria.length; i++) {
				ICriteriaConfiguration rc = resultCriteria[i];

				if (first) {
					first = false;
				} else {
					jsWriter.write(',');
				}

				writeSelectedCriteria(jsWriter, rc, result);
			}
			jsWriter.write(']');
		}

		jsWriter.writeln(");");

		if (LOG.isTraceEnabled()) {
			pw.flush();

			LOG.trace(cw.toString());

			printWriter.write(cw.toCharArray());
		}
	}

	private void writeSelectedCriteria(IJavaScriptWriter jsWriter,
			ICriteriaConfiguration configuration, ICriteriaSelectedResult result)
			throws WriterException {

		IObjectLiteralWriter ow = jsWriter.writeObjectLiteral(true);

		ow.writeSymbol("id").writeString(
				configuration.getCriteriaContainer().getId());

		CriteriaItem[] criteriaItems = result
				.getAvailableCriteriaItems(configuration);

		UIComponent refComponent = (UIComponent) configuration;
		Converter converter = configuration.getCriteriaConverter();

		IJavaScriptWriter ijs = ow.writeSymbol("values").write('[');
		int vir = 0;
		for (int j = 0; j < criteriaItems.length; j++) {
			CriteriaItem ci = criteriaItems[j];

			if (vir > 0) {
				ijs.write(',');
			}
			vir++;

			Object itemValue = ci.getValue();

			String itemConvertedValue = ValuesTools.convertValueToString(
					itemValue, converter, refComponent,
					jsWriter.getFacesContext());
			
			if (itemConvertedValue == null) {
				continue;
			}

			IObjectLiteralWriter itemsW = ijs.writeObjectLiteral(true);

			if (itemConvertedValue!= null &&ci.getLabel() != null 
					&& itemConvertedValue.equals(ci.getLabel()) == false) {
				itemsW.writeSymbol("label").writeString(ci.getLabel());
			}

			itemsW.writeSymbol("value").writeString(itemConvertedValue);

			itemsW.end();
		}

		ijs.write(']');

		ow.end();

	}
}
