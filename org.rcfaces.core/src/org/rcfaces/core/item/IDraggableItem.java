/*
 * $Id: IDraggableItem.java,v 1.1 2011/04/12 09:25:29 oeuillot Exp $
 */
package org.rcfaces.core.item;

import javax.faces.model.SelectItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:29 $
 */
public interface IDraggableItem {
    String[] getDragTypes(SelectItem draggedSelectItem);

    int getDragEffects(SelectItem draggedSelectItem);
}
