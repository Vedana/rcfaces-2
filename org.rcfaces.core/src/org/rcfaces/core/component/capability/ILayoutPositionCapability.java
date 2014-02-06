/*
 * $Id: ILayoutPositionCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface ILayoutPositionCapability {
    Number getLeft();

    void setLeft(Number left);

    Number getRight();

    void setRight(Number right);

    Number getTop();

    void setTop(Number top);

    Number getBottom();

    void setBottom(Number bottom);
    
    Number getHorizontalCenter();

    void setHorizontalCenter(Number horizontalCenter);

    Number getVerticalCenter();

    void setVerticalCenter(Number verticalCenter);
}
