/*
 * $Id: IOperationContentLoader.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.internal.content;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.content.AbstractBufferOperationContentModel.ContentInformation;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IOperationContentLoader {
    String loadContent(FacesContext facesContext,
            IResourceLoaderFactory resourceLoaderFactory, String path,
            String defaultCharset, ContentInformation contentInfoRef[]);
}
