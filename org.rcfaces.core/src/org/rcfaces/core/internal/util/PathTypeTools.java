/*
 * $Id: PathTypeTools.java,v 1.1 2011/04/12 09:25:20 oeuillot Exp $
 */
package org.rcfaces.core.internal.util;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:20 $
 */
public class PathTypeTools {
    private static final Log LOG = LogFactory.getLog(PathTypeTools.class);

    public static String convertAbsolutePathToContextType(
            FacesContext facesContext, String url) {

        // On peut peut-etre retirer le contextPath !

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        String contextPath = facesContext.getExternalContext()
                .getRequestContextPath();

        if (url.startsWith(contextPath)) {
            String resolvedURL = url.substring(contextPath.length());

            if (resolvedURL.startsWith("/") == false) {
                resolvedURL = "/" + url;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Convert absolute path '" + url
                        + "' to context path '" + resolvedURL + "'.");
            }

            return resolvedURL;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("CAN NOT convert absolute path '" + url
                    + "' to context path.");
        }

        return null;
    }

    public static String convertContextPathToAbsoluteType(
            FacesContext facesContext, String url) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        String contextPath = facesContext.getExternalContext()
                .getRequestContextPath();
        if (contextPath.endsWith("/")) {
            if (url.startsWith("/")) {

                String converted = contextPath + url.substring(1);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Convert context path '" + url
                            + "' to absolute path '" + converted + "'.");
                }

                return converted;
            }

            String converted = contextPath + url;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Convert context path '" + url
                        + "' to absolute path '" + converted + "'.");
            }

            return converted;
        }

        if (url.startsWith("/")) {
            String converted = contextPath + url;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Convert context path '" + url
                        + "' to absolute path '" + converted + "'.");
            }

            return converted;

        }

        String converted = contextPath + "/" + url;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert context path '" + url + "' to absolute path '"
                    + converted + "'.");
        }

        return converted;
    }

    public static String convertRelativePathToContextPath(
            FacesContext facesContext, String url, IContentPath basePath) {

        IProcessContext processContext = AbstractProcessContext
                .getProcessContext(facesContext);

        String converted = processContext.getAbsolutePath(url, false);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert relative path '" + url + "' to context path '"
                    + converted + "'.");
        }

        return converted;

    }

}
