/*
 * $Id: BasicContentPath.java,v 1.1 2011/04/12 09:25:29 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:29 $
 */
public class BasicContentPath extends AbstractContentPath {
    private static final Log LOG = LogFactory.getLog(BasicContentPath.class);

    private String path;

    public BasicContentPath(IContentPath parent, String path) {
        super(parent);

        resolvePath(null, path);
    }

    public String getPath() {
        return path;
    }

    protected void resolvePath(FacesContext facesContext, String url) {
        if (url == null || url.length() < 1) {
            throw new FacesException("Invalid url '" + url + "'.");
        }

        int slash = url.indexOf('/');
        if (url.length() > 1 && url.charAt(0) == '$') {
            if (url.startsWith(IContentPath.CONTEXT_KEYWORD)
                    && (slash < 0 || slash == IContentPath.CONTEXT_KEYWORD
                            .length())) {
                setPathType(IContentPath.CONTEXT_PATH_TYPE);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Macro " + IContentPath.CONTEXT_KEYWORD
                            + " for url '" + url + "'.");
                }

                if (slash > 0) {
                    this.path = url.substring(slash + 1);
                    return;
                }

                this.path = "";
                return;
            }

            // Invalid macro
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invalid macro type for '" + url + "'.");
            }

            setPathType(IContentPath.UNDEFINED_PATH_TYPE);
            this.path = "";
            return;
        }

        // C'est soit absolute, soit context
        if (slash == 0) {

            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            String requestContextPath = facesContext.getExternalContext()
                    .getRequestContextPath();
            if (requestContextPath.length() == 0
                    || url.startsWith(requestContextPath)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Return context path type for '" + url + "'.");
                }
                setPathType(IContentPath.CONTEXT_PATH_TYPE);

                this.path = url.substring(requestContextPath.length() + 1);
                return;

            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Return absolute path type for '" + url + "'.");
            }
            setPathType(IContentPath.ABSOLUTE_PATH_TYPE);

            this.path = url;
            return;
        }

        int colon = url.indexOf(':');
        if (colon < 0) {
            // Ca commence pas par un '/' et y a pas de ':'
            if (LOG.isDebugEnabled()) {
                LOG.debug("Return relative path type for '" + url + "'.");
            }

            setPathType(IContentPath.RELATIVE_PATH_TYPE);
            this.path = url;
            return;
        }
        if (colon == 0) {
            // Invalide
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invalid path type for '" + url + "'.");
            }
            setPathType(IContentPath.UNDEFINED_PATH_TYPE);

            this.path = url;
            return;
        }

        int doubleColon = url.indexOf(IContentAccessor.FILTER_SEPARATOR, colon);
        if (doubleColon > 0 && (slash < 0 || slash > doubleColon)
                && (colon == doubleColon)) {
            // Y a un doubleColon
            // un slash n'est pas avant
            // un colon seul n'est pas avant

            if (LOG.isDebugEnabled()) {
                LOG.debug("Filter path type for '" + url + "'.");
            }
            setPathType(IContentPath.FILTER_PATH_TYPE);
            this.path = url;
            return;
        }

        if (colon > 0 && (slash < 0 || slash > colon)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("External path type for '" + url + "'.");
            }
            setPathType(IContentPath.EXTERNAL_PATH_TYPE);

            this.path = url;
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Relative path type (default) for '" + url + "'.");
        }
        setPathType(IContentPath.RELATIVE_PATH_TYPE);
        this.path = url;
    }
}
