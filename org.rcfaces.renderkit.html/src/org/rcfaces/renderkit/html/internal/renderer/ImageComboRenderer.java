/*
 * $Id: ImageComboRenderer.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.ImageComboComponent;
import org.rcfaces.core.component.capability.IShowDropDownMarkCapability;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.SubMenuDecorator;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public class ImageComboRenderer extends ImageButtonRenderer {
    

    private static final String MENU_ID = "#popup";

    private static final String COMPONENT_SUFFIX_ID = "popup";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_COMBO;
    }
    

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        ImageComboComponent imageComboComponent = (ImageComboComponent) component;

        SubMenuDecorator subMenuDecorator = new SubMenuDecorator(
                imageComboComponent, MENU_ID, COMPONENT_SUFFIX_ID,
                imageComboComponent.isRemoveAllWhenShown(facesContext),
                getItemImageWidth(imageComboComponent),
                getItemImageHeight(imageComboComponent));

        ImageComboDecorator decorator = new ImageComboDecorator(
                (IImageButtonFamilly) component);

        decorator.addChildDecorator(subMenuDecorator);

        return decorator;
    }

    protected int getItemImageHeight(IMenuComponent menuComponent) {
        return -1;
    }

    protected int getItemImageWidth(IMenuComponent menuComponent) {
        return -1;
    }

    protected String getComboImageWidth(IHtmlWriter htmlWriter) {
        return "7";
    }

    protected String getComboImageHeight(IHtmlWriter htmlWriter) {
        return null;
    }

    protected String getComboImageHorizontalAlignment(IHtmlWriter htmlWriter) {
        return IComponentDecorator.HALIGN_CENTER;
    }

    protected String getComboImageVerticalAlignment(IHtmlWriter htmlWriter) {
        return IComponentDecorator.VALIGN_CENTER;
    }

    protected boolean isShowDropDownMark(IImageButtonFamilly imageButtonFamilly) {
        if ((imageButtonFamilly instanceof IShowDropDownMarkCapability) == false) {
            return false;
        }

        return ((IShowDropDownMarkCapability) imageButtonFamilly)
                .isShowDropDownMark();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
     */
    protected class ImageComboDecorator extends ImageButtonDecorator {
        

        private final boolean showDropDownMark;

        private boolean firstLine = true;

        public ImageComboDecorator(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);

            showDropDownMark = isShowDropDownMark(imageButtonFamilly);
        }

        
        
        
        protected void writeEndRow(int nextRowCount) throws WriterException {
            if (firstLine == false || showDropDownMark == false) {
                super.writeEndRow(nextRowCount);
                return;
            }
            firstLine = false;

            writeComboImage(nextRowCount);

            super.writeEndRow(nextRowCount);
        }

        protected int computeHorizontalSpan() {
            int span = super.computeHorizontalSpan();

            if (showDropDownMark) {
                span++;
            }

            return span;
        }

        protected boolean isCompositeComponent() {
            return true;
        }

        protected void writeEndCompositeComponent() throws WriterException {
            if (htmlBorderWriter == null && showDropDownMark) {
                writeComboImage();
            }

            super.writeEndCompositeComponent();
        }

        protected String getComboImageWidth() {
            return ImageComboRenderer.this.getComboImageWidth(writer);
        }

        protected String getComboImageHeight() {
            return ImageComboRenderer.this.getComboImageHeight(writer);
        }

        protected String getComboImageHorizontalAlignment() {
            return ImageComboRenderer.this
                    .getComboImageHorizontalAlignment(writer);
        }

        protected String getComboImageVerticalAlignment() {
            return ImageComboRenderer.this
                    .getComboImageVerticalAlignment(writer);
        }

        protected String getInputRole() {
        	return IAccessibilityRoles.LISTBOX;
        }
    }

}
