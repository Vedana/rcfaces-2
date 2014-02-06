/*
 * $Id: IDraggableCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IDraggableCapability {
    String[] getDragTypes();

    void setDragTypes(String[] types);

    int getDragEffects();

    void setDragEffects(int effects);

    boolean isDraggable();

    void setDraggable(boolean draggable);
}
