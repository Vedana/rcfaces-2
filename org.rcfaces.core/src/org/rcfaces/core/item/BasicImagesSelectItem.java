/*
 * $Id: BasicImagesSelectItem.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.component.IExpandImageAccessors;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class BasicImagesSelectItem extends BasicSelectItem implements
        IImagesItem {

    private static final long serialVersionUID = 3611551357173361214L;

    private String imageURL;

    private String disabledImageURL;

    private String hoverImageURL;

    private String selectedImageURL;

    private String expandedImageURL;

    public BasicImagesSelectItem() {
    }

    public BasicImagesSelectItem(Object value) {
        super(value);
    }

    public BasicImagesSelectItem(Object value, String label) {
        super(value, label);
    }

    public BasicImagesSelectItem(Object value, String label, String description) {
        super(value, label, description);
    }

    public BasicImagesSelectItem(Object value, String label,
            String description, boolean disabled) {
        super(value, label, description, disabled);
    }

    public BasicImagesSelectItem(UISelectItem component) {
        super(component);

        IContentAccessors contentAccessors = null;

        FacesContext facesContext = null;
        if (component instanceof IImageCapability) {
            contentAccessors = ((IImageCapability) component)
                    .getImageAccessors();
        }

        if (contentAccessors instanceof IImageAccessors) {
            IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            IContentAccessor ca = imageAccessors.getImageAccessor();
            if (ca != null) {
                imageURL = ca.resolveURL(facesContext, null, null);
            }

            if (contentAccessors instanceof IStatesImageAccessors) {
                IStatesImageAccessors is = (IStatesImageAccessors) imageAccessors;

                ca = is.getDisabledImageAccessor();
                if (ca != null) {
                    disabledImageURL = ca.resolveURL(facesContext, null, null);
                }

                ca = is.getHoverImageAccessor();
                if (ca != null) {
                    hoverImageURL = ca.resolveURL(facesContext, null, null);
                }

                ca = is.getSelectedImageAccessor();
                if (ca != null) {
                    selectedImageURL = ca.resolveURL(facesContext, null, null);
                }

                if (contentAccessors instanceof IExpandImageAccessors) {
                    IExpandImageAccessors ei = (IExpandImageAccessors) is;

                    ca = ei.getExpandedImageAccessor();
                    if (ca != null) {
                        expandedImageURL = ca.resolveURL(facesContext, null,
                                null);
                    }
                }
            }
        }
    }

    public String getImageURL() {
        return imageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getHoverImageURL()
     */
    public String getHoverImageURL() {
        return hoverImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getSelectedImageURL()
     */
    public String getSelectedImageURL() {
        return selectedImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getExpandedImageURL()
     */
    public String getExpandedImageURL() {
        return expandedImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getDisabledImageURL()
     */
    public String getDisabledImageURL() {
        return disabledImageURL;
    }

    public void setDisabledImageURL(String disabledImageURL) {
        this.disabledImageURL = disabledImageURL;
    }

    public void setExpandedImageURL(String expandedImageURL) {
        this.expandedImageURL = expandedImageURL;
    }

    public void setHoverImageURL(String hoverImageURL) {
        this.hoverImageURL = hoverImageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setSelectedImageURL(String selectedImageURL) {
        this.selectedImageURL = selectedImageURL;
    }

}
