/*
 * $Id: ICheckComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface ICheckComponent {

    /**
     * 
     */
    void check(Object rowValue);

    /**
     * Checks all of the items in the receiver. If the receiver is
     * single-checked, do nothing.
     */
    void checkAll();

    void uncheck(Object rowValue);

    /**
     * Uncheks all checked items in the receiver.
     */
    void uncheckAll();

}
