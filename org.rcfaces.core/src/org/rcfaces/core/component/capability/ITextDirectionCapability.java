/*
 * $Id: ITextDirectionCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface ITextDirectionCapability {

    int LEFT_TO_RIGHT_TEXT_DIRECTION = 0;

    int RIGHT_LEFT_TEXT_DIRECTION = 1;

    int DEFAULT_TEXT_DIRECTION = LEFT_TO_RIGHT_TEXT_DIRECTION;

    int getTextDirection();

    void setTextDirection(int textDirection);
}
