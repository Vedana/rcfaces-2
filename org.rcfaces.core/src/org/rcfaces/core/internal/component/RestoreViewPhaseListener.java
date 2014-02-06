/*
 * $Id: RestoreViewPhaseListener.java,v 1.2 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
 */
public class RestoreViewPhaseListener implements PhaseListener {
    

    private static final long serialVersionUID = -6370096499604012889L;

    private static final String RESTORE_VIEW_PHASE_PROPERTY_NAME = "org.rcfaces.core.RESTORE_VIEW";

    public void afterPhase(PhaseEvent event) {
        event.getFacesContext().getExternalContext().getRequestMap().remove(
                RESTORE_VIEW_PHASE_PROPERTY_NAME);
    }

    public void beforePhase(PhaseEvent event) {
        event.getFacesContext().getExternalContext().getRequestMap().put(
                RESTORE_VIEW_PHASE_PROPERTY_NAME, Boolean.TRUE);
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
