/*
 * $Id: SortTools.java,v 1.3 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.capability.IComponentEngine;
import org.rcfaces.core.internal.component.Properties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:17:38 $
 */
public class SortTools {

    public static UIComponent[] getSortedChildren(FacesContext facesContext,
            UIComponent component, IComponentEngine engine,  Class< ? extends UIComponent> classOfChild) {

        return ComponentTools.listChildren(facesContext, component, engine,
                classOfChild, Properties.SORTED_CHILDREN);
    }

    public static UIComponent getFirstSortedChild(FacesContext facesContext,
            UIComponent component, IComponentEngine engine,  Class< ? extends UIComponent> classOfChild) {
        UIComponent children[] = getSortedChildren(facesContext, component,
                engine, classOfChild);
        if (children == null || children.length < 1) {
            return null;
        }

        return children[0];
    }

    public static void setSortedChildren(FacesContext facesContext,
            UIComponent component, IComponentEngine engine,  Class< ? extends UIComponent> classOfChild,
            UIComponent[] children) {

        ComponentTools.setChildren(component, engine, classOfChild, children,
                Properties.SORTED_CHILDREN);

    }
}
