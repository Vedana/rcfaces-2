/*
 * $Id: ViewSessionPatchStateManager.java,v 1.2 2013/07/03 12:25:08 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
 */
public class ViewSessionPatchStateManager extends StateManager {
    

    private static final String FACES_VIEW_LIST = "com.sun.faces.VIEW_LIST";

    private static final String VIEW_PATCH_PARAMETER = Constants
            .getPackagePrefix()
            + ".VIEW_SESSION_PATCH";

    private static final Log LOG = LogFactory
            .getLog(ViewSessionPatchStateManager.class);

    private final StateManager parent;

    private Boolean enabled;

    public ViewSessionPatchStateManager(StateManager stateManager) {
        this.parent = stateManager;
    }

    public SerializedView saveSerializedView(FacesContext context) {
        synchronized (this) {
            if (enabled == null) {
                if ("true".equalsIgnoreCase(context.getExternalContext()
                        .getInitParameter(VIEW_PATCH_PARAMETER))) {
                    enabled = Boolean.TRUE;
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Enable view-session patch.");
                    }
                } else {
                    enabled = Boolean.FALSE;
                }
            }
        }

        if (enabled == Boolean.TRUE) {
            Map sessionMap = context.getExternalContext().getSessionMap();

            synchronized (this) {
                if (sessionMap != null) {
                    List viewList = (List) sessionMap.get(FACES_VIEW_LIST);
                    if (viewList != null) {
                        viewList.remove(context.getViewRoot().getViewId());

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("View list size=" + viewList.size());
                        }
                    }
                }
            }
        }

        return parent.saveSerializedView(context);
    }

    public void writeState(FacesContext context, SerializedView state)
            throws IOException {
        parent.writeState(context, state);
    }

    public UIViewRoot restoreView(FacesContext context, String viewId,
            String renderKitId) {

        return parent.restoreView(context, viewId, renderKitId);
    }

    /* ---- */

    protected Object getTreeStructureToSave(FacesContext context) {
        return null;
    }

    protected Object getComponentStateToSave(FacesContext context) {
        return null;
    }

    protected UIViewRoot restoreTreeStructure(FacesContext context,
            String viewId, String renderKitId) {
        return null;
    }

    protected void restoreComponentState(FacesContext context,
            UIViewRoot viewRoot, String renderKitId) {
    }
}
