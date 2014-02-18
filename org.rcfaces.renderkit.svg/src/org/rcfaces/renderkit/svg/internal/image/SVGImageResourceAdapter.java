/*
 * $Id: SVGImageResourceAdapter.java,v 1.2 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.images.AbstractImageResourceAdapter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:39 $
 */
public class SVGImageResourceAdapter extends AbstractImageResourceAdapter {

    private static final Log LOG = LogFactory
            .getLog(SVGImageResourceAdapter.class);

    private final Object batickBridge_Lock = new Object();

    private BatikLazyLoadingBridge batikBridge;

    public SVGImageResourceAdapter() {
    }

    protected BatikLazyLoadingBridge getBatikBridge() {
        synchronized (batickBridge_Lock) {
            if (batikBridge == null) {
                batikBridge = new BatikLazyLoadingBridge();
            }
        }

        return batikBridge;
    }

    @Override
    public BufferedImage adaptContent(FacesContext facesContext,
            InputStream inputStream,
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {

        return getBatikBridge().adaptContent(facesContext, inputStream,
                generationInformation, generatedInformation);
    }
}
