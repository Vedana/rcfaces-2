/*
 * $Id: AbstractContentAccessor.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.Collections;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public abstract class AbstractContentAccessor extends AbstractContentPath
        implements IContentAccessor {
    private static final Log LOG = LogFactory
            .getLog(AbstractContentAccessor.class);

    private final IContentAccessor parentContentAccessor;

    private final IContentFamily contentFamily;

    private IContentVersionHandler contentVersionHandler;

    private IContentProxyHandler contentProxyHandler;

    protected AbstractContentAccessor(IContentFamily type,
            IContentVersionHandler contentVersionHandler,
            IContentProxyHandler contentProxyHandler) {
        this(type, null, contentVersionHandler, contentProxyHandler);
    }

    protected AbstractContentAccessor(IContentAccessor contentAccessor) {
        this(contentAccessor.getContentFamily(), contentAccessor,
                contentAccessor.getContentVersionHandler(), contentAccessor
                        .getContentProxyHandler());
    }

    protected AbstractContentAccessor(IContentFamily type,
            IContentAccessor contentAccessor,
            IContentVersionHandler contentVersionHandler,
            IContentProxyHandler contentProxyHandler) {
        super(contentAccessor);

        this.parentContentAccessor = contentAccessor;
        this.contentFamily = type;
        this.contentVersionHandler = contentVersionHandler;
        this.contentProxyHandler = contentProxyHandler;
    }

    public IContentAccessor getParentAccessor() {
        return parentContentAccessor;
    }

    public final IContentFamily getContentFamily() {
        return contentFamily;
    }

    public IContentVersionHandler getContentVersionHandler() {
        return contentVersionHandler;
    }

    public void setContentVersionHandler(
            IContentVersionHandler contentVersionHandler) {
        this.contentVersionHandler = contentVersionHandler;
    }

    public IContentProxyHandler getContentProxyHandler() {
        return contentProxyHandler;
    }

    public void setContentProxyHandler(IContentProxyHandler contentProxyHandler) {
        this.contentProxyHandler = contentProxyHandler;
    }

    public final String resolveURL(FacesContext facesContext,
            IGeneratedResourceInformation contentInformation,
            IGenerationResourceInformation generationInformation) {

        return resolveURL(facesContext, contentInformation,
                generationInformation, IContentPath.ABSOLUTE_PATH_TYPE
                        | IContentPath.RELATIVE_PATH_TYPE
                        | IContentPath.EXTERNAL_PATH_TYPE);
    }

    public final String resolveURL(FacesContext facesContext,
            IGeneratedResourceInformation contentInformation,
            IGenerationResourceInformation generationInformation,
            int pathTypeMask) {

        IContentAccessor contentAccessor = ContentAccessorEngine.resolveURL(
                facesContext, this, contentInformation, generationInformation);
        if (contentAccessor == null) {
            return null;
        }

        Object resolvedURL = contentAccessor.getContentRef();
        if ((resolvedURL instanceof String) == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resolved URL is not a String: "
                        + resolvedURL
                        + " (pathType="
                        + AbstractContentPath.getPathTypeName(contentAccessor
                                .getPathType()) + ")");
            }
            return null;
        }

        String resolvedURLs = (String) resolvedURL;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved URL: "
                    + resolvedURLs
                    + " (pathType="
                    + AbstractContentPath.getPathTypeName(contentAccessor
                            .getPathType()) + ")");
        }

        int currentPathType = contentAccessor.getPathType();

        if ((pathTypeMask & IContentPath.RELATIVE_PATH_TYPE) > 0) {
            if (currentPathType == IContentPath.RELATIVE_PATH_TYPE) {
                return resolvedURLs;
            }
        }

        if ((pathTypeMask & IContentPath.ABSOLUTE_PATH_TYPE) > 0) {
            if (currentPathType == IContentPath.ABSOLUTE_PATH_TYPE) {
                return resolvedURLs;
            }

            if (currentPathType == IContentPath.CONTEXT_PATH_TYPE) {

                String converted = PathTypeTools
                        .convertContextPathToAbsoluteType(facesContext,
                                resolvedURLs);

                return converted;
            }
        }

        if ((pathTypeMask & IContentPath.CONTEXT_PATH_TYPE) > 0) {
            if (currentPathType == IContentPath.CONTEXT_PATH_TYPE) {
                return resolvedURLs;
            }

            if (currentPathType == IContentPath.ABSOLUTE_PATH_TYPE) {
                String converted = PathTypeTools
                        .convertAbsolutePathToContextType(facesContext,
                                resolvedURLs);
                if (converted != null) {
                    return converted;
                }

                throw new FacesException(
                        "Absolute path type is not into the context '"
                                + resolvedURLs + "'.");

            }
        }

        if ((pathTypeMask & IContentPath.EXTERNAL_PATH_TYPE) > 0) {
            if (currentPathType == IContentPath.EXTERNAL_PATH_TYPE) {
                return resolvedURLs;
            }
        }

        if (currentPathType == IContentPath.FILTER_PATH_TYPE) {
            throw new FacesException("Filter '" + resolvedURLs
                    + "' has not been processed !");
        }

        throw new FacesException("Incompatible path type (requested=0x"
                + Integer.toHexString(pathTypeMask) + ", pathType=0x"
                + Integer.toHexString(currentPathType) + ", url='"
                + resolvedURL + "')");
    }

    public Object getAttribute(String attributeName) {
        if (parentContentAccessor != null) {
            return parentContentAccessor.getAttribute(attributeName);
        }

        return null;
    }

    public Map<String, Object> getAttributes() {
        if (parentContentAccessor != null) {
            return parentContentAccessor.getAttributes();
        }

        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "[AbstractContentAccessor contentType=" + contentFamily
                + " pathType="
                + AbstractContentPath.getPathTypeName(getPathType())
                + " versionHandler=" + contentVersionHandler + " root="
                + parentContentAccessor + "]";
    }
}