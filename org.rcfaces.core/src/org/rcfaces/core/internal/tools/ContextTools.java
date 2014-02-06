/*
 * $Id: ContextTools.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public final class ContextTools {

    private static final Log LOG = LogFactory.getLog(ContextTools.class);

    public static final Object resolveAttribute(FacesContext facesContext,
            String attributeName) {

        ExternalContext externalContext = facesContext.getExternalContext();

        Object value = externalContext.getRequestMap().get(attributeName);
        if (value != null) {
            return value;
        }

        Map session = externalContext.getSessionMap();
        if (session != null) {
            value = session.get(attributeName);
            if (value != null) {
                return value;
            }
        }

        value = externalContext.getApplicationMap().get(attributeName);
        if (value != null) {
            return value;
        }

        return null;
    }

    public static final String resolveText(FacesContext facesContext,
            String bundleVar, String attributeName) {

        Object bundle = ContextTools.resolveAttribute(facesContext, bundleVar);
        if (bundle instanceof Map) {
            String rtext = (String) ((Map) bundle).get(attributeName);
            if (rtext != null) {
                return rtext;
            }

            return "???" + attributeName + " (key not found)???";
        }

        return "???" + attributeName + " (bundle not found)???";
    }

    public static Locale getUserLocale(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot != null) {
            Locale locale = viewRoot.getLocale();
            if (locale != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Get locale from viewRoot: " + locale);
                }
                return locale;
            }
        }

        Locale locale = facesContext.getApplication().getViewHandler()
                .calculateLocale(facesContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Calculate locale from view handler: " + locale);
        }

        return locale;
    }

    public static TimeZone getUserTimeZone(FacesContext facesContext) {
        // TODO Auto-generated method stub
        return null;
    }
}
