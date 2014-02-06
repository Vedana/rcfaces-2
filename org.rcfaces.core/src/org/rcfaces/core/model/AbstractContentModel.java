/*
 * $Id: AbstractContentModel.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public abstract class AbstractContentModel implements IContentModel {

    private Object wrappedData;

    private String contentEngineId;

    public AbstractContentModel() {
    }

    public AbstractContentModel(Object wrappedData) {
        this.wrappedData = wrappedData;
    }

    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
    }

    public Object getWrappedData() {
        return wrappedData;
    }

    public void setWrappedData(Object wrappedData) {
        this.wrappedData = wrappedData;
    }

    public String computeURL() {
        return computeURL(this);
    }

    public static String computeURL(IContentModel contentModel) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        IContentAccessor contentAccessor = ContentAccessorFactory
                .createFromWebResource(facesContext, contentModel,
                        IContentFamily.USER);

        if (contentAccessor == null) {
            return null;
        }

        return contentAccessor.resolveURL(facesContext, null, null);
    }

    public String getContentEngineId() {
        return contentEngineId;
    }

    public void setContentEngineId(String contentEngineId) {
        if (contentEngineId != null) {
            contentEngineId = contentEngineId.intern();
        }
        this.contentEngineId = contentEngineId;
    }

    public boolean checkNotModified() {
        return true;
    }

}
