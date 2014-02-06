/*
 * $Id: ContentStorageAccessorHandler.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorsRegistryImpl;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessorHandler;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.model.IContentModel;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public class ContentStorageAccessorHandler extends AbstractProvider implements
        IContentAccessorHandler {
    
    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        ((ContentAccessorsRegistryImpl) rcfacesContext
                .getContentAccessorRegistry()).declareContentAccessorHandler(
                null, this);
    }

    public String getId() {
        return "ContentStorageAccessorHandler";
    }

    public IContentAccessor handleContent(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] generatedInformationRef,
            IGenerationResourceInformation generationInformation) {

        if (contentAccessor.getPathType() != IContentPath.UNDEFINED_PATH_TYPE) {
            return null;
        }

        Object ref = contentAccessor.getContentRef();
        if (ref == null || (ref instanceof String)) {
            return null;
        }

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        if (ref instanceof IContentModel) {
            IContentModel contentModel = (IContentModel) ref;
            IGeneratedResourceInformation generatedInformation = generatedInformationRef[0];

            if (contentModel instanceof IFiltredModel) {
                ((IFiltredModel) contentModel).setFilter(generationInformation
                        .getFilterProperties());

                if (contentAccessor != null) {
                    generatedInformation.setFiltredModel(true);
                }
            }

            return rcfacesContext.getContentStorageEngine()
                    .registerContentModel(facesContext, contentModel,
                            generatedInformationRef[0], generationInformation);
        }

        return rcfacesContext.getContentStorageEngine().registerRaw(
                facesContext, ref, generatedInformationRef[0]);
    }

}
