/*
 * $Id: MessagesRenderer.java,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.MessagesComponent;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
public class MessagesRenderer extends AbstractCssRenderer {

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IComponentRenderContext componentContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentContext.getFacesContext();

        MessagesComponent messagesComponent = (MessagesComponent) componentContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.TABLE);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        boolean globalOnly = messagesComponent.isGlobalOnly();
        if (globalOnly) {
            htmlWriter.writeAttributeNS("globalOnly", true);
        }

        if (messagesComponent.isShowSummary()) {
            htmlWriter.writeAttributeNS("showSummary", true);
        }

        if (messagesComponent.isShowDetail()) {
            htmlWriter.writeAttributeNS("showDetail", true);
        }

        if (messagesComponent.isShowActiveComponentMessage(facesContext)) {
            htmlWriter.writeAttributeNS("showActiveComponentMessage", true);
        }

        int maxCount = messagesComponent.getMaxCount(facesContext);
        if (maxCount > 0) {
            htmlWriter.writeAttributeNS("maxCount", maxCount);
        }

        htmlWriter.endElement(IHtmlWriter.TABLE);

        htmlWriter.getJavaScriptEnableMode().enableOnMessage();
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.ALERT;
    }


    protected String getJavaScriptClassName() {
        return JavaScriptClasses.MESSAGES;
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {

        MessagesComponent messagesComponent = (MessagesComponent) component;

        Boolean showDetail = componentData.getBooleanProperty("showDetail");
        if (showDetail != null) {
            messagesComponent.setShowDetail(showDetail.booleanValue());
        }

        Boolean showSummary = componentData.getBooleanProperty("showSummary");
        if (showSummary != null) {
            messagesComponent.setShowDetail(showSummary.booleanValue());
        }
        /*
         * String forValue=componentData.getProperty("for"); if (forValue!=null)
         * { messageComponent.setFor(forValue); }
         */

        super.decode(context, component, componentData);
    }
}
