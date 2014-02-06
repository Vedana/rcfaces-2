/*
 * $Id: IContentModel.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.model;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IContentModel {

    // String RESPONSE_EXPIRATION_PROPERTY = "org.rfcaces.response.EXPIRATION";

    void setInformations(IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation);

    Object getWrappedData();

    String getContentEngineId();

    void setContentEngineId(String contentEngineId);

    boolean checkNotModified();
}
