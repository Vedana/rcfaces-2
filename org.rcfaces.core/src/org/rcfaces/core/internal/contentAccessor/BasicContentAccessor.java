package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.tools.BindingTools;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:07 $
 */
public class BasicContentAccessor extends AbstractContentAccessor {
    

    private static final Log LOG = LogFactory
            .getLog(BasicContentAccessor.class);

    private final Object value;

    public BasicContentAccessor(FacesContext facesContext, String url,
            int pathType, IContentFamily contentFamily) {
        this(facesContext, url, pathType, contentFamily, null, null);
    }

    public BasicContentAccessor(FacesContext facesContext, String url,
            int pathType, IContentFamily contentFamily,
            IContentVersionHandler contentVersionHandler,
            IContentProxyHandler contentProxyHandler) {
        super(contentFamily, contentVersionHandler, contentProxyHandler);

        this.value = url;
        setPathType(pathType);
    }

    public BasicContentAccessor(FacesContext facesContext, Object url,
            IContentFamily contentFamily,
            IContentVersionHandler contentVersionHandler,
            IContentProxyHandler contentProxyHandler) {
        super(contentFamily, contentVersionHandler, contentProxyHandler);

        this.value = convertURL(facesContext, url);
    }

    public BasicContentAccessor(FacesContext facesContext, Object url,
            IContentFamily contentFamily) {
        this(facesContext, url, contentFamily, null, null);
    }

    public BasicContentAccessor(FacesContext facesContext, Object url,
            IContentAccessor root, int pathType) {
        super(root);

        if (pathType == IContentPath.UNDEFINED_PATH_TYPE) {
            this.value = convertURL(facesContext, url);

        } else {
            this.value = url;
            setPathType(pathType);
        }
    }

    public String getPath() {
        return String.valueOf(getContentRef());
    }

    public Object getContentRef() {
        return value;
    }

    protected Object convertURL(FacesContext facesContext, Object url) {
        if (url == null) {
            setPathType(IContentPath.UNDEFINED_PATH_TYPE);

            return null;
        }

        url = BindingTools.resolveBinding(facesContext, url);

        if ((url instanceof String) == false) {
            setPathType(IContentPath.UNDEFINED_PATH_TYPE);
            return url;
        }

        IContentPath cp = new BasicContentPath(getParentContentPath(),
                (String) url);

        setPathType(cp.getPathType());

        return cp.getPath();
    }

    @Override
    public String toString() {
        return "[BasicContentAccessor contentType=" + getContentFamily()
                + " pathType="
                + AbstractContentPath.getPathTypeName(getPathType())
                + " versionHandler=" + getContentVersionHandler()
                + " content='" + value + "' root=" + getParentAccessor() + "]";
    }
}