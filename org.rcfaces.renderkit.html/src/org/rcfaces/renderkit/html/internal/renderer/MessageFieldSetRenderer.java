/*
 * $Id: MessageFieldSetRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.FieldSetComponent;
import org.rcfaces.core.component.MessageFieldSetComponent;
import org.rcfaces.core.internal.component.ISeverityImageAccessors;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "for", "showActiveComponentMessage" })
public class MessageFieldSetRenderer extends FieldSetRenderer {

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.MESSAGE_FIELD_SET;
    }

    protected void writeFieldSetAttributes(IHtmlWriter htmlWriter,
            FieldSetComponent fieldSetComponent) throws WriterException {
        super.writeFieldSetAttributes(htmlWriter, fieldSetComponent);

        MessageFieldSetComponent messageFieldSetComponent = (MessageFieldSetComponent) fieldSetComponent;

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        ISeverityImageAccessors accessors = (ISeverityImageAccessors) messageFieldSetComponent
                .getImageAccessors(facesContext);

        writeSeverityImages(htmlWriter, accessors);

        String forValue = messageFieldSetComponent.getFor(facesContext);
        if (forValue != null) {
            htmlWriter.writeAttributeNS("for", forValue);
        }

        if (messageFieldSetComponent.isShowActiveComponentMessage(facesContext)) {
            htmlWriter.writeAttributeNS("showActiveComponentMessage", true);
        }

        htmlWriter.getJavaScriptEnableMode().enableOnMessage(); // Pour les
                                                                // messages
    }

}
