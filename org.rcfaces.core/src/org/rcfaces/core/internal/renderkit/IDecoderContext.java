/*
 * $Id: IDecoderContext.java,v 1.1 2011/04/12 09:25:41 oeuillot Exp $
 */
package org.rcfaces.core.internal.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.render.Renderer;

public interface IDecoderContext {
    Renderer getRenderer();

    UIComponent getComponent();

    IProcessContext getProcessContext();
}
