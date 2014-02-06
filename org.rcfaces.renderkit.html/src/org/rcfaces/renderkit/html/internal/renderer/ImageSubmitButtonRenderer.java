/*
 * $Id: ImageSubmitButtonRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class ImageSubmitButtonRenderer extends ImageButtonRenderer {
    

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_SUBMIT_BUTTON;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
   
        // Il faut l'activer sur le INIT pour récuperer la touche SUBMIT
        ((IHtmlWriter) writer).getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(writer);
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new ImageSubmitButtonDecorator((IImageButtonFamilly) component);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
     */
    protected class ImageSubmitButtonDecorator extends ImageButtonDecorator {
        

        public ImageSubmitButtonDecorator(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected String getInputElement() {
            return IHtmlWriter.INPUT;
        }
    }
}
