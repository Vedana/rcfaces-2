/*
 * $Id: IImageButtonFamilly.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.familly;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.IBorderCapability;
import org.rcfaces.core.component.capability.IBorderTypeCapability;
import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.component.capability.IImageSizeCapability;
import org.rcfaces.core.component.capability.IReadOnlyCapability;
import org.rcfaces.core.component.capability.ISelectionEventCapability;
import org.rcfaces.core.component.capability.IStatesImageCapability;
import org.rcfaces.core.component.capability.ITabIndexCapability;
import org.rcfaces.core.component.capability.ITextCapability;
import org.rcfaces.core.component.capability.ITextPositionCapability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IImageButtonFamilly extends IImageCapability,
        IStatesImageCapability, IBorderCapability, IBorderTypeCapability,
        ITextCapability, ISelectionEventCapability, IReadOnlyCapability,
        IDisabledCapability, ITextPositionCapability, IImageSizeCapability, ITabIndexCapability {

    String getImageURL(FacesContext facesContext);

    boolean isReadOnly(FacesContext facesContext);

    boolean isDisabled(FacesContext facesContext);

    boolean isBorder(FacesContext facesContext);

    String getBorderType(FacesContext facesContext);

    String getHoverImageURL(FacesContext facesContext);

    String getSelectedImageURL(FacesContext facesContext);

    String getDisabledImageURL(FacesContext facesContext);
    
    Integer getTabIndex(FacesContext facesContext);

    String getText(FacesContext facesContext);

    int getTextPosition(FacesContext facesContext);

    int getImageWidth(FacesContext facesContext);

    int getImageHeight(FacesContext facesContext);

    IContentAccessors getImageAccessors(FacesContext facesContext);
}
