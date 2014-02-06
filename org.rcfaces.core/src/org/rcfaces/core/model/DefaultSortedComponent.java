/*
 * $Id: DefaultSortedComponent.java,v 1.2 2006/09/14 14:34:51 oeuillot Exp 
 */
package org.rcfaces.core.model;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class DefaultSortedComponent implements ISortedComponent {
    

    private final UIComponent component;

    private final int index;

    private final int sortMode;

    public DefaultSortedComponent(UIComponent component, int index, int sortMode) {
        this.component = component;
        this.index = index;
        this.sortMode = sortMode;
    }

    public DefaultSortedComponent(UIComponent component, int index,
            boolean sortOrder) {
        this(component, index, (sortOrder) ? ASCENDING : DESCENDING);
    }

    public UIComponent getComponent() {
        return component;
    }

    public int getIndex() {
        return index;
    }

    public int getSortMode() {
        return sortMode;
    }

    public final boolean isAscending() {
        return getSortMode() == ASCENDING;
    }

}
