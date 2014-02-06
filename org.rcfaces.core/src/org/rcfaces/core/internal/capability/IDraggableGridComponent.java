/*
 * $Id: IDraggableGridComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IDraggableGridComponent {

    String[] getRowDragTypes();

    boolean isRowDragTypesSetted();

    int getRowDragEffects();

    boolean isRowDragEffectsSetted();
}
