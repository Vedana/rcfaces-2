/*
 * $Id: AdditionalInformationService.java,v 1.5 2013/12/11 10:19:48 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.HtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.renderer.IAdditionalInformationGridRenderer;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:19:48 $
 */
public class AdditionalInformationService extends AbstractGridService {
	

	private static final Log LOG = LogFactory
			.getLog(AdditionalInformationService.class);

	private static final String ADDITIONAL_INFORMATION_SERVICE_VERSION = "1.0.0";

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private void writeAdditionalInformations(FacesContext facesContext,
			PrintWriter printWriter, IGridComponent gridComponent,
			IAdditionalInformationGridRenderer dgr, String rowValue, String rowIndex, String id)
			throws WriterException {

		CharArrayWriter cw = null;
		PrintWriter pw = printWriter;
		if (LOG.isTraceEnabled()) {
			cw = new CharArrayWriter(2000);
			pw = new PrintWriter(cw);
		}

		Object states[] = dgr
				.getAdditionalInformationsRenderContextState(gridComponent);
		if (states == null) {
			throw new FacesException(
					"Can not get render context state for additional informations of gridComponent='"
							+ gridComponent + "'");
		}

		IHtmlRenderContext renderContext = HtmlRenderContext
				.restoreRenderContext(facesContext, states[0], true);

		renderContext.pushComponent((UIComponent) gridComponent,
				((UIComponent) gridComponent).getClientId(facesContext));

		dgr.renderAdditionalInformation(renderContext, pw, gridComponent,
				RESPONSE_CHARSET, rowValue, rowIndex);
		
		

		IJavaScriptWriter jsWriter = new JavaScriptResponseWriter(facesContext,
				printWriter, RESPONSE_CHARSET, (UIComponent) gridComponent, id);
		String viewStateId;
		try {
			
			String varId = jsWriter.getComponentVarName();
			viewStateId = saveViewAndReturnStateId(facesContext);
			if (viewStateId != null) {
				jsWriter.write("<script> //<![CDATA[");
				jsWriter.write("var ").write(varId).write('=')
				.writeCall("f_core", "GetElementByClientId")
				.writeString(id).writeln(", document);");
				jsWriter.writeCall("f_classLoader", "ChangeJsfViewId")
						.write(varId).write(',').writeString(viewStateId)
						.write(')');
				
				jsWriter.write("//]]></script>");
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
		if (LOG.isTraceEnabled()) {
			pw.flush();

			LOG.trace(cw.toString());

			printWriter.write(cw.toCharArray());
		}
	}

	@Override
	protected void writeElement(FacesContext facesContext,
			PrintWriter printWriter, IColumnsContainer component,
			Renderer gridRenderer, String rowValue, String rowIndex, String componentId)
			throws WriterException {

		writeAdditionalInformations(facesContext, printWriter,
				(IGridComponent) component, (IAdditionalInformationGridRenderer) gridRenderer, rowValue, rowIndex, componentId);
	}
}
