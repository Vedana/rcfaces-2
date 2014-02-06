/*
 * $Id: CameliaComponents.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.component;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IRendererExtension;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class CameliaComponents {
    
    private static final Log LOG = LogFactory.getLog(CameliaComponents.class);

    public static final String FAMILY = "rcfaces";

    public static void processDecodes(FacesContext context,
            UIComponent component, Renderer renderer) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Process decodes of '" + component.getId() + "'.");
        }

        IRendererExtension rendererExtension = (IRendererExtension) renderer;

        try {
            renderer.decode(context, component);

        } catch (RuntimeException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Process decodes of '" + component.getId()
                        + "' throws exception.", e);
            }

            context.renderResponse();
            throw e;
        }

        if (rendererExtension.getDecodesChildren()) {
            try {
                rendererExtension.decodeChildren(context, component);

            } catch (RuntimeException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Process decodes of children of component '"
                            + component.getId() + "' throws exception.", e);
                }

                context.renderResponse();
                throw e;
            }

        } else {
            try {
                Iterator<UIComponent> kids = component.getFacetsAndChildren();
                while (kids.hasNext()) {
                    UIComponent kid = kids.next();
                    kid.processDecodes(context);
                }

            } catch (RuntimeException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                                    "Process decodes (by iterator) of children of component '"
                                    + component.getId() + "' throws exception.",
                            e);
                }

                throw e;
            }
        }

        try {
            rendererExtension.decodeEnd(context, component);

        } catch (RuntimeException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Process end decodes of children of component '"
                        + component.getId() + "' throws exception.", e);
            }

            context.renderResponse();
            throw e;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Process decodes of '" + component.getId() + "' DONE.");
        }

    }
}
