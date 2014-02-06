/*
 * $Id: ToolTipService.java,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.HtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.renderer.ITooltipGridRenderer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
public class ToolTipService extends AbstractGridService {

	private static final Log LOG = LogFactory.getLog(ToolTipService.class);

	protected void writeTooltips(FacesContext facesContext,
			PrintWriter printWriter, IColumnsContainer gridComponent,
			ITooltipGridRenderer dgr, String rowValue, String rowIndex,
			String toolTipId) throws WriterException {

		CharArrayWriter cw = null;
		PrintWriter pw = printWriter;
		if (LOG.isTraceEnabled()) {
			cw = new CharArrayWriter(2000);
			pw = new PrintWriter(cw);
		}

		ResponseWriter newWriter = facesContext.getRenderKit()
				.createResponseWriter(printWriter, getResponseContentType(),
						AbstractHtmlService.RESPONSE_CHARSET);

		facesContext.setResponseWriter(newWriter);

		Object states[] = dgr.getTooltipsRenderContextState(gridComponent);
		if (states == null) {
			throw new FacesException(
					"Can not get render context state for tooltip of gridComponent='"
							+ gridComponent + "'");
		}

		IHtmlRenderContext renderContext = HtmlRenderContext
				.restoreRenderContext(facesContext, states[0], true);

		renderContext.pushComponent((UIComponent) gridComponent,
				((UIComponent) gridComponent).getClientId(facesContext));

		IHtmlWriter htmlWriter = (IHtmlWriter) renderContext
				.getComponentWriter();

		dgr.renderTooltip(htmlWriter, gridComponent, RESPONSE_CHARSET,
				rowValue, rowIndex, toolTipId);

		if (LOG.isTraceEnabled()) {
			pw.flush();

			LOG.trace(cw.toString());

			printWriter.write(cw.toCharArray());
		}
	}

	@Override
	protected String saveView(FacesContext facesContext,
			ResponseWriter responseWriter) throws IOException {
		return null;
	}

	@Override
	protected void writeElement(FacesContext facesContext,
			PrintWriter printWriter, IColumnsContainer component,
			Renderer gridRenderer, String rowValue, String rowIndex,
			String componentId) throws WriterException {

		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();

		String tooltipId = (String) parameters.get("toolTipId");

		writeTooltips(facesContext, printWriter, component,
				(ITooltipGridRenderer) gridRenderer, rowValue, rowIndex,
				tooltipId);

	}
}
