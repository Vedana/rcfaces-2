/*
 * $Id: AdaptationParameters.java,v 1.2 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.model.IContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:20 $
 */
public class AdaptationParameters implements IAdaptable {

    private final IContentModel contentModel;

    private final IGenerationResourceInformation generationResourceInformation;

    private final IGeneratedResourceInformation generatedResourceInformation;

    public AdaptationParameters(IContentModel contentModel,
            IGenerationResourceInformation generationResourceInformation,
            IGeneratedResourceInformation generatedResourceInformation) {
        super();
        this.contentModel = contentModel;
        this.generationResourceInformation = generationResourceInformation;
        this.generatedResourceInformation = generatedResourceInformation;
    }

    public IContentModel getContentModel() {
        return contentModel;
    }

    public IGenerationResourceInformation getGenerationResourceInformation() {
        return generationResourceInformation;
    }

    public IGeneratedResourceInformation getGeneratedResourceInformation() {
        return generatedResourceInformation;
    }

    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        if (contentModel instanceof IAdaptable) {
            return ((IAdaptable) contentModel).getAdapter(adapter, parameter);
        }

        return null;
    }

}
