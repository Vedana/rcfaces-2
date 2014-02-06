/*
 * $Id: TextEditorComboRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TextEditorComboComponent;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "type", "for" })
public class TextEditorComboRenderer extends ComboRenderer {

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        TextEditorComboComponent component = (TextEditorComboComponent) writer
                .getComponentRenderContext().getComponent();

        component.setDisabled(true);

        super.encodeBegin(writer);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TEXT_EDITOR_COMBO;
    }

    protected void writeComboAttributes(IHtmlWriter htmlWriter)
            throws WriterException {

        super.writeComboAttributes(htmlWriter);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        TextEditorComboComponent combo = (TextEditorComboComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        String type = combo.getType(facesContext);
        if (type != null) {
            htmlWriter.writeAttributeNS("type", type);
        }

        String forProperty = combo.getFor(facesContext);
        if (forProperty != null) {
            htmlWriter.writeAttributeNS("for", forProperty);

            htmlWriter.getJavaScriptEnableMode().enableOnInit(); // Pour
                                                                 // positionner
                                                                 // la combo
                                                                 // en
                                                                 // fonction
                                                                 // de l'état
                                                                 // de la
                                                                 // target
        }
    }
}
