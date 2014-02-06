/*
 * $Id: ITypedEvent.java,v 1.1 2011/04/12 09:25:16 oeuillot Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:16 $
 */
public interface ITypedEvent {

    UIComponent getComponent();

    int getType();
}
