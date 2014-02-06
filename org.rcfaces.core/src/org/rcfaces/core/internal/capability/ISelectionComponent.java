/*
 * $Id: ISelectionComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface ISelectionComponent {

    /**
     * 
     */
    void select(Object rowValue);

    /**
     * Selects all of the items in the receiver. If the receiver is
     * single-select, do nothing.
     */
    void selectAll();

    void deselect(Object rowValue);

    /**
     * Deselects all selected items in the receiver.
     */
    void deselectAll();
}
