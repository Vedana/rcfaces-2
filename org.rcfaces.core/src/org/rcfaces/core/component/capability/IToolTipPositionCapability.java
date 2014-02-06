/*
 * $Id: IToolTipPositionCapability.java,v 1.1 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

import org.rcfaces.core.internal.capability.IColumnsContainer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/12/11 10:17:38 $
 */
public interface IToolTipPositionCapability extends IColumnsContainer {
    String MOUSE_POSITION = "mouse";

    String MIDDLE_COMPONENT_POSITION = "middle";

    String BOTTOM_COMPONENT_POSITION = "bottom";

    String LEFT_COMPONENT_POSITION = "left";

    String BOTTOM_LEFT_COMPONENT_POSITION = "bottom-left";

    String MIDDLE_LEFT_COMPONENT_POSITION = "middle-left";

    String RIGHT_COMPONENT_POSITION = "right";

    String BOTTOM_RIGHT_COMPONENT_POSITION = "bottom-right";

    String MIDDLE_RIGHT_COMPONENT_POSITION = "middle-right";

    String DEFAULT_POSITION = "default";
}
