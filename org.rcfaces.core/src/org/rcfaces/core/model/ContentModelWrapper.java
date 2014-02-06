/*
 * $Id: ContentModelWrapper.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class ContentModelWrapper implements IContentModel {
    private IContentModel contentModel;

    public void setContentModel(IContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public IContentModel getContentModel() {
        return contentModel;
    }

    public boolean checkNotModified() {
        return contentModel.checkNotModified();
    }

    public String getContentEngineId() {
        return contentModel.getContentEngineId();
    }

    public Object getWrappedData() {
        return contentModel.getWrappedData();
    }

    public void setContentEngineId(String contentEngineId) {
        contentModel.setContentEngineId(contentEngineId);
    }

    @Override
    public boolean equals(Object obj) {
        return contentModel.equals(obj);
    }

    @Override
    public int hashCode() {
        return contentModel.hashCode();
    }

    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        contentModel.setInformations(generationInformation,
                generatedInformation);
    }

}
