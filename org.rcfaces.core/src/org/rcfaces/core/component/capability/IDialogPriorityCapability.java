/*
 * $Id: IDialogPriorityCapability.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A int indicating the priority for this component. Permits to show a higher
 * priority dialog before a lower priority one.
 * 
 * @author Fred (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface IDialogPriorityCapability {

    int MAX_PRIORITY = 1000;

    int getDialogPriority();

    void setDialogPriority(int priority);
}
