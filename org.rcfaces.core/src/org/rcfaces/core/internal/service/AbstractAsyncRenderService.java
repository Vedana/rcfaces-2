/*
 * $Id: AbstractAsyncRenderService.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.servlet.jsp.tagext.BodyContent;

import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public abstract class AbstractAsyncRenderService implements IService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".AsyncRender";

    public static final int DEFAULT_ASYNC_RENDER_BUFFER_MODE = 0;

    public static final int IGNORE_ASYNC_RENDER_BUFFER_MODE = 1;

    public static final int BUFFER_ASYNC_RENDER_BUFFER_MODE = 2;

    public static AbstractAsyncRenderService getInstance(
            FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (AbstractAsyncRenderService) serviceRegistry.getService(
                facesContext, RenderKitFactory.HTML_BASIC_RENDER_KIT,
                SERVICE_ID);
    }

    public abstract boolean isAsyncRenderEnable();

    public abstract int getAsyncRendererBufferMode(FacesContext facesContext,
            UIComponent component);

    public abstract void setContent(FacesContext facesContext,
            UIComponent componentInstance, BodyContent bodyContent);
}
