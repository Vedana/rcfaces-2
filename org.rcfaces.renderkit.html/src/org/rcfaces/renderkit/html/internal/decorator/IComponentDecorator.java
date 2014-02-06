/*
 * $Id: IComponentDecorator.java,v 1.1 2011/04/12 09:28:17 oeuillot Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:17 $
 */
public interface IComponentDecorator {

    String HALIGN_LEFT = "left";

    String HALIGN_RIGHT = "right";

    String HALIGN_CENTER = "center";

    String VALIGN_CENTER = "middle";

    String VALIGN_BOTTOM = "bottom";

    String VALIGN_TOP = "top";

    void addChildDecorator(IComponentDecorator decorator);

    void encodeContainer(IHtmlWriter writer, Renderer renderer)
            throws WriterException;

    void encodeContainerEnd(IHtmlWriter writer, Renderer renderer)
            throws WriterException;

    void encodeJavaScript(IJavaScriptWriter jsWriter) throws WriterException;

    void decode(IRequestContext context, UIComponent component,
            IComponentData componentData);

    String[] getDefaultUnlockedProperties(FacesContext facesContext,
            UIComponent component);

}
