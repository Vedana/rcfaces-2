/*
 * $Id: IDroppableCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IDroppableCapability {
    String[] getDropTypes();

    void setDropTypes(String[] types);

    int getDropEffects();

    void setDropEffects(int effects);

    boolean isDroppable();

    void setDroppable(boolean droppable);
}
