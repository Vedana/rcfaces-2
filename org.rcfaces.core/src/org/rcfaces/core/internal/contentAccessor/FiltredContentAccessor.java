package org.rcfaces.core.internal.contentAccessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:23 $
 */
public class FiltredContentAccessor extends AbstractContentAccessor implements
        IFiltredContentAccessor {
    private static final Log LOG = LogFactory
            .getLog(FiltredContentAccessor.class);

    private final String filter;

    public FiltredContentAccessor(String filter, IContentAccessor parentAccessor) {
        super(parentAccessor);
        this.filter = filter;

        setPathType(IContentPath.FILTER_PATH_TYPE);
    }

    public Object getContentRef() {
        return null;
    }

    public String getPath() {
        return null;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return "[FiltredContentAccessor filter='" + filter + "' contentType="
                + getContentFamily() + " pathType="
                + AbstractContentPath.getPathTypeName(getPathType())
                + " versionHandler=" + getContentVersionHandler() + " root="
                + getParentAccessor() + "]";
    }

}