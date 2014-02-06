/*
 * $Id: JavaScriptTools.java,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
public class JavaScriptTools {
    

    private static final Log LOG = LogFactory.getLog(JavaScriptTools.class);

    @SuppressWarnings("unused")
    public static String writeMessage(FacesContext facesContext,
            IJavaScriptWriter js, FacesMessage facesMessage)
            throws WriterException {

        String bundleVar = null;

        IJavaScriptRenderContext javaScriptRenderContext = js
                .getJavaScriptRenderContext();

        String key = javaScriptRenderContext.allocateVarName();

        String summary = facesMessage.getSummary();
        if (summary != null) {
            if (bundleVar != null) {
                summary = ContextTools.resolveText(facesContext, bundleVar,
                        summary);
            }

            summary = js.allocateString(summary);
        }

        String detail = facesMessage.getDetail();
        if (detail != null) {
            if (bundleVar != null) {
                detail = ContextTools.resolveText(facesContext, bundleVar,
                        detail);
            }
            detail = js.allocateString(detail);
        }

        js.write("var ").write(key).write('=').writeConstructor(
                "f_messageObject");

        IObjectLiteralWriter objectLiteralWriter = js.writeObjectLiteral(true);

        Severity severity = facesMessage.getSeverity();
        // La severity ne peut etre null !
        if (severity != null) {
            objectLiteralWriter.writeSymbol("_severity").writeInt(
                    severity.getOrdinal());
        }

        if (summary != null) {
            objectLiteralWriter.writeSymbol("_summary").write(summary);
        }

        if (detail != null) {
            objectLiteralWriter.writeSymbol("_detail").write(detail);
        }

        if (facesMessage instanceof IClientDataCapability) {
            IClientDataCapability clientDataCapability = (IClientDataCapability) facesMessage;

            if (clientDataCapability.getClientDataCount() > 0) {

                IJavaScriptWriter paramWriter = objectLiteralWriter
                        .writeSymbol("_clientDatas");

                HtmlTools.writeObjectLiteralMap(paramWriter,
                        clientDataCapability.getClientDataMap(), true);
            }
        }

        objectLiteralWriter.end().writeln(");");

        return key;
    }
}
