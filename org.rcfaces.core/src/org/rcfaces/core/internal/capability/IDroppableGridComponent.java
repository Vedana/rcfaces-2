/*
 * $Id: IDroppableGridComponent.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface IDroppableGridComponent {
    boolean isBodyDroppable();

    String[] getRowDropTypes();

    boolean isRowDropTypesSetted();

    int getRowDropEffects();

    boolean isRowDropEffectsSetted();
}
