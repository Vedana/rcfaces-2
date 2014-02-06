/*
 * $Id: ILayoutManagerCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface ILayoutManagerCapability {
    int INHERITED_LAYOUT_TYPE = 0;

    int NONE_LAYOUT_TYPE = 1;

    int ABSOLUTE_LAYOUT_TYPE = 2;

    int getLayoutType();

    void setLayoutType(int type);
}
