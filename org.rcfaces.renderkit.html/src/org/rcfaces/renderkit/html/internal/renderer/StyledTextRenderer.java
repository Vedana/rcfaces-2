/*
 * $Id: StyledTextRenderer.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.component.TextComponent;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public class StyledTextRenderer extends TextRenderer {
    

    protected boolean writeText(IHtmlWriter writer, TextComponent textComponent)
            throws WriterException {

        String text = textComponent.getText(writer.getComponentRenderContext()
                .getFacesContext());

        if (text == null || text.length() < 1) {
            return false;
        }
        text = ParamUtils.formatMessage(textComponent, text);

        if (text.length() > 0) {
            writer.write(text);
        }

        return false;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.STYLED_TEXT;
    }
}