/*
 * $Id: ISelectItemFinder.java,v 1.1 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.component.iterator;

import java.util.Map;

import javax.faces.component.UIComponent;

import org.rcfaces.core.item.ISelectItemPath;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
 */
public interface ISelectItemFinder {
    ISelectItemPath[] search(UIComponent component,
            Map<String, Object> filterProperies);
}
