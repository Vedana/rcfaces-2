/*
 * $Id: IFiltredCollection2.java,v 1.2 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.Iterator;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:58 $
 */
public interface IFiltredCollection2<T> {
	Iterator<T> iterator(UIComponent component,
            IFilterProperties filterProperties, int maximumResultNumber);

}
