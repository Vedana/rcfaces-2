/*
 * $Id: CameliaPhaseListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class CameliaPhaseListener implements PhaseListener {
    

    private static final long serialVersionUID = -6370096499604012889L;

    private static final String PHASE_ID_PROPERTY_NAME = "org.rcfaces.core.PHASE_ID";

    public void beforePhase(PhaseEvent event) {
        event.getFacesContext().getExternalContext().getRequestMap().put(
                PHASE_ID_PROPERTY_NAME, event.getPhaseId());
    }

    public void afterPhase(PhaseEvent event) {
        event.getFacesContext().getExternalContext().getRequestMap().remove(
                PHASE_ID_PROPERTY_NAME);
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    protected static PhaseId getCurrentPhaseId(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();

            // Le facesContext peut être null !
            // Principalement dans la phase d'init de Faces lors des tests des
            // composants.
            if (facesContext == null) {
                return null;
            }
        }

        return (PhaseId) facesContext.getExternalContext().getRequestMap().get(
                PHASE_ID_PROPERTY_NAME);
    }

    public static boolean isApplyingRequestValues() {
        return PhaseId.APPLY_REQUEST_VALUES.equals(getCurrentPhaseId(null));
    }

    public static boolean isApplyingRequestValuesPhase(FacesContext facesContext) {
        return PhaseId.APPLY_REQUEST_VALUES
                .equals(getCurrentPhaseId(facesContext));
    }

    public static boolean isRestoreViewPhase() {
        return PhaseId.RESTORE_VIEW.equals(getCurrentPhaseId(null));
    }

    public static boolean isRestoreViewPhase(FacesContext facesContext) {
        return PhaseId.RESTORE_VIEW.equals(getCurrentPhaseId(facesContext));
    }

}
