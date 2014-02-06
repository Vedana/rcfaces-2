/*
 * $Id: ItemSelectionEvent.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.PhaseId;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class ItemSelectionEvent extends SelectionEvent {
    

    private static final long serialVersionUID = 2043226704038860305L;

    private final boolean immediate;

    public ItemSelectionEvent(UIComponent component, String value, Object valueObject, Object item, int detail, boolean immediate) {
        super(component, value, valueObject, item, detail);

        this.immediate = immediate;
    }

    public void setPhaseId(PhaseId phaseId) {
        if (immediate) {
            phaseId = PhaseId.APPLY_REQUEST_VALUES;
            // On force APPLY_REQUEST_VALUE
        }
        super.setPhaseId(phaseId);
    }

}
