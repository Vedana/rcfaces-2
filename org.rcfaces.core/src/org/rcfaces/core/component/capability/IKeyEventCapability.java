/*
 * $Id: IKeyEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * Aggregates keyDown, keyUp and keyPressed capabilities
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IKeyEventCapability extends IKeyPressEventCapability,
        IKeyUpEventCapability, IKeyDownEventCapability {

}
