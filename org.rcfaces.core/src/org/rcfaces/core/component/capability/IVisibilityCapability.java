/*
 * $Id: IVisibilityCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IVisibilityCapability {

    /**
     * Marks the receiver as visible if the argument is
     * <code>{@link Boolean#TRUE TRUE}</code>, and marks it invisible if
     * argument is <code>{@link Boolean#FALSE FALSE}</code>. <br>
     * If one of the receiver's ancestors is not visible or some other condition
     * makes the receiver not visible, marking it visible may not actually cause
     * it to be displayed.
     * 
     * @param visible
     *            the new visibility state.
     */
    void setVisible(boolean visible);

    boolean isVisible();

    /**
     * Returns <code>{@link Boolean#TRUE TRUE}</code> if the receiver is
     * visible, <code>{@link Boolean#FALSE FALSE}</code> if the receiver is
     * specified "not visible", and <code>null</code> otherwise. <br>
     * If one of the receiver's ancestors is not visible or some other condition
     * makes the receiver not visible, this method may still indicate that it is
     * considered visible even though it may not actually be showing.
     * 
     * @return the receiver's visibility state
     */
    Boolean getVisibleState();
}
