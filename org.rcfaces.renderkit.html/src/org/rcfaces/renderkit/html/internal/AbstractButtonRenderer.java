/*
 * $Id: AbstractButtonRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import javax.faces.component.UIComponent;

import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public abstract class AbstractButtonRenderer extends AbstractCssRenderer {

    

    protected String getButtonType(UIComponent component) {
        return IHtmlWriter.BUTTON_INPUT_TYPE;
    }

    protected final IHtmlWriter writeButtonAttributes(IHtmlWriter writer)
            throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        return writeButtonAttributes(writer, component.getId());
    }

    protected final IHtmlWriter writeButtonAttributes(IHtmlWriter writer,
            String id) throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        if (id != null) {
            writer.writeName(id);
        }

        String type = getButtonType(component);
        if (type != null) {
            writer.writeType(type);
        }

        if (component instanceof IDisabledCapability) {
            writeEnabled(writer, (IDisabledCapability) component);
        }

        return writer;
    }
}