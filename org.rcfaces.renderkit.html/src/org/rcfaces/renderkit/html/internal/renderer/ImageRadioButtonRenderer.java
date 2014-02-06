/*
 * $Id: ImageRadioButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ImageRadioButtonComponent;
import org.rcfaces.core.component.capability.IRadioGroupCapability;
import org.rcfaces.core.component.capability.ISelectedCapability;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "groupName" })
public class ImageRadioButtonRenderer extends ImageCheckButtonRenderer {

    private static final Log LOG = LogFactory
            .getLog(ImageRadioButtonRenderer.class);

    protected void decodeSelection(FacesContext facesContext,
            IImageButtonFamilly imageButtonCapability, boolean selected) {
        super.decodeSelection(facesContext, imageButtonCapability, selected);

        ImageRadioButtonComponent imageRadioButtonComponent = (ImageRadioButtonComponent) imageButtonCapability;
        Object radioValue = imageRadioButtonComponent.getRadioValue();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Decode selection of componentId='"
                    + ((UIComponent) imageRadioButtonComponent).getId()
                    + "' radioValue=" + radioValue + " selected=" + selected);
        }

        if (radioValue == null) {
            return;
        }

        // La selection pouvait être déjà faite !
        if (imageRadioButtonComponent.isSelected()
                && imageRadioButtonComponent.isValueLocked(facesContext) == false) {
            ValuesTools.setValue(imageRadioButtonComponent, radioValue);
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_RADIO_BUTTON;
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {
        return new ImageRadioButtonWriter((IImageButtonFamilly) component);
    }

    protected boolean isSelected(
            ImageRadioButtonComponent imageRadioButtonComponent,
            FacesContext facesContext) {
        Object radioValue = imageRadioButtonComponent.getRadioValue();
        if (radioValue == null) {
            return imageRadioButtonComponent.isSelected(facesContext);
        }

        Object currentValue = getValue(imageRadioButtonComponent);

        return radioValue.equals(currentValue);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
     */
    protected class ImageRadioButtonWriter extends ImageCheckButtonDecorator {

        public ImageRadioButtonWriter(IImageButtonFamilly imageButtonFamilly) {
            super(imageButtonFamilly);
        }

        protected String getRole() {
            return IAccessibilityRoles.RADIO;
        }

        protected void encodeAttributes(FacesContext facesContext)
                throws WriterException {
            super.encodeAttributes(facesContext);

            String groupName = ((IRadioGroupCapability) imageButtonFamilly)
                    .getGroupName();
            if (groupName != null) {
                groupName = HtmlTools.computeGroupName(writer
                        .getHtmlComponentRenderContext().getHtmlRenderContext()
                        .getHtmlProcessContext(),
                        (UIComponent) imageButtonFamilly, groupName);

                writer.writeAttributeNS("groupName", groupName);
            }
        }

        protected boolean isSelected(ISelectedCapability imageButtonFamilly) {
            return ImageRadioButtonRenderer.this.isSelected(
                    (ImageRadioButtonComponent) imageButtonFamilly, null);
        }
    }
}
