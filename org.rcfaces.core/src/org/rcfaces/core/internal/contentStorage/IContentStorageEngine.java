/*
 * $Id: IContentStorageEngine.java,v 1.1 2011/04/12 09:25:17 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.model.IContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:17 $
 */
public interface IContentStorageEngine {

    IContentStorageRepository getRepository();

    IContentAccessor registerRaw(FacesContext facesContext, Object ref,
            IGeneratedResourceInformation information);

    IContentAccessor registerContentModel(FacesContext facesContext,
            IContentModel contentModel, IGeneratedResourceInformation generatedInformation,
            IGenerationResourceInformation generationInformation);

}
