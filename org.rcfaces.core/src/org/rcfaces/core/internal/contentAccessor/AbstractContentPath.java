/*
 * $Id: AbstractContentPath.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.PathTypeTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public abstract class AbstractContentPath implements IContentPath {
    private static final Log LOG = LogFactory.getLog(AbstractContentPath.class);

    private final IContentPath parentContentPath;

    private int pathType;

    public AbstractContentPath(IContentPath parent) {
        this.parentContentPath = parent;
    }

    public int getPathType() {
        if (pathType > 0) {
            return pathType;
        }

        if (parentContentPath != null) {
            return parentContentPath.getPathType();
        }

        return 0;
    }

    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    public IContentPath getParentContentPath() {
        return parentContentPath;
    }

    public String convertToPathType(FacesContext facesContext,
            int targetPathType) {
        switch (targetPathType) {
        case IContentPath.CONTEXT_PATH_TYPE:
            return convertToContextPathType(facesContext);
        }

        return null;
    }

    private String convertToContextPathType(FacesContext facesContext) {
        String path = getPath();

        String relativePath;

        switch (getPathType()) {
        case IContentPath.EXTERNAL_PATH_TYPE:
            LOG.error("Can not make operation on an external URL ! (" + path
                    + ")");
            return null;

        case IContentPath.CONTEXT_PATH_TYPE:
            return path;

        case IContentPath.ABSOLUTE_PATH_TYPE:
            relativePath = PathTypeTools.convertAbsolutePathToContextType(
                    facesContext, path);

            if (relativePath == null) {
                LOG.error("Can not transform Absolute path to Context path ! ("
                        + path + ")");
                return null;
            }

            return relativePath;

        case IContentPath.RELATIVE_PATH_TYPE:
            relativePath = PathTypeTools.convertRelativePathToContextPath(
                    facesContext, path, null);

            if (relativePath == null) {
                LOG.error("Can not transform Relative path to Context path ! ("
                        + path + ")");
                return null;
            }

            return relativePath;
        }

        LOG.error("Invalid state ! (" + path + ")");
        return null;
    }

    public static final String getPathTypeName(int pathType) {
        switch (pathType) {
        case IContentPath.UNDEFINED_PATH_TYPE:
            return "undefined";

        case IContentPath.ABSOLUTE_PATH_TYPE:
            return "absolute";

        case IContentPath.CONTEXT_PATH_TYPE:
            return "context";

        case IContentPath.EXTERNAL_PATH_TYPE:
            return "external";

        case IContentPath.RELATIVE_PATH_TYPE:
            return "relative";

        case IContentPath.FILTER_PATH_TYPE:
            return "filter";
        }

        return "*** Invalid (" + pathType + ") ***";
    }

}
