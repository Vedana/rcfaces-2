/*
 * $Id: IComponentRefModel.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.model;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IComponentRefModel {
    void setComponent(UIComponent component);
}
