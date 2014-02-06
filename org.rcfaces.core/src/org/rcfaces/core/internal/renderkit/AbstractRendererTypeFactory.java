/*
 * $Id: AbstractRendererTypeFactory.java,v 1.1 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:02 $
 */
public abstract class AbstractRendererTypeFactory extends AbstractProvider
        implements IRenderTypeFactory {
    protected static final String PREFIX_PROVIDER_ID = "org.rcfaces.core.RENDERER_TYPE_FACTORY:";

    private static final String RENDER_TYPE_FACTORY_PROPERTY = "org.rcface.core.RENDER_TYPE_FACTORY";

    private static final String RENDER_TYPE_FACTORY_PROPERTY_PREFIX = "org.rcface.core.RENDER_TYPE_FACTORY$";

    private static final Object APPLICATION_ACCESS_LOCK = new Object();

    public static IRenderTypeFactory get() {
        return get((FacesContext) null);
    }

    public static IRenderTypeFactory get(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();

            if (facesContext == null) {
                throw new FacesException("No faces context !");
            }
        }

        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> requestMap = externalContext.getApplicationMap();

        AbstractRendererTypeFactory rendererTypeFactory = (AbstractRendererTypeFactory) requestMap
                .get(RENDER_TYPE_FACTORY_PROPERTY);
        if (rendererTypeFactory != null) {
            return rendererTypeFactory;
        }

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        if (rcfacesContext == null) {
            throw new IllegalStateException("Can not find RCFaces context");
        }

        String renderKitId = facesContext.getViewRoot().getRenderKitId();
        String providerId = PREFIX_PROVIDER_ID + renderKitId;

        String applicationRenderKitId = RENDER_TYPE_FACTORY_PROPERTY_PREFIX
                + renderKitId;

        Map<String, Object> applicationMap = externalContext
                .getApplicationMap();

        synchronized (APPLICATION_ACCESS_LOCK) {
            rendererTypeFactory = (AbstractRendererTypeFactory) applicationMap
                    .get(applicationRenderKitId);
            if (rendererTypeFactory == null) {
                rendererTypeFactory = (AbstractRendererTypeFactory) rcfacesContext
                        .getProvidersRegistry().getProvider(providerId);

                if (rendererTypeFactory == null) {
                    throw new IllegalStateException(
                            "Can not find rendererTypeFactory for renderKitId '"
                                    + renderKitId + "'.");
                }
            }

            applicationMap.put(applicationRenderKitId, rendererTypeFactory);
        }

        requestMap.put(RENDER_TYPE_FACTORY_PROPERTY, rendererTypeFactory);

        return rendererTypeFactory;
    }

    public static IRenderTypeFactory get(RcfacesContext rcfacesContext,
            String renderKitId) {
        String providerId = PREFIX_PROVIDER_ID + renderKitId;

        AbstractRendererTypeFactory rendererTypeFactory = (AbstractRendererTypeFactory) rcfacesContext
                .getProvidersRegistry().getProvider(providerId);

        return rendererTypeFactory;
    }

    protected AbstractRendererTypeFactory() {
    }
}
