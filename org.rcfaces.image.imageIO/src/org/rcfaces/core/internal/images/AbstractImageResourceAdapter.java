/*
 * $Id: AbstractImageResourceAdapter.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public abstract class AbstractImageResourceAdapter implements
        IImageIOResourceAdapter {

    private static final Log LOG = LogFactory
            .getLog(AbstractImageResourceAdapter.class);

    public boolean isContentSupported(String contentType, String suffix) {
        return true;
    }

    public BufferedImage adaptContent(FacesContext facesContext,
            InputStream inputStream,
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        return null;
    }

}
