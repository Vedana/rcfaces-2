/*
 * $Id: IAcceleratorKeyCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A string that indicates the (composed) key used to execute an action from the
 * keyboard.
 * <p>
 * [Shift|Alt|Ctrl] + &lt;Any key&gt;
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IAcceleratorKeyCapability {

    /**
     * Returns a string that indicates the (composed) key used to execute an
     * action from the keyboard.
     * 
     * @return [Shift|Alt|Ctrl] + &lt;Any key&gt;
     */
    String getAcceleratorKey();

    /**
     * Sets a string that indicates the (composed) key used to execute an action
     * from the keyboard.
     * 
     * @param acceleratorKey
     *            [Shift|Alt|Ctrl] + &lt;Any key&gt;
     */
    void setAcceleratorKey(String acceleratorKey);
}
