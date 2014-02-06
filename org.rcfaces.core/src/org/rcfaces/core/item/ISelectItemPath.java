/*
 * $Id: ISelectItemPath.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public interface ISelectItemPath {
    SelectItem[] segments();

    SelectItem segment(int index);

    int segmentCount();

    SelectItem getSelectItem();

    String normalizePath(FacesContext facesContext, UIComponent component, Converter valueConverter);

    String normalizePath();
}
