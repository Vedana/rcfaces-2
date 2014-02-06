/*
 * $Id: IContainerManager.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.manager;

import java.util.List;

import javax.faces.component.UIComponent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:52 $
 */
public interface IContainerManager {

    int getChildCount();

    List<UIComponent> getChildren();
}
