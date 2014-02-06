/*
 * $Id: AbstractFilteredSelectItems.java,v 1.1 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:58 $
 */
public abstract class AbstractFilteredSelectItems<T extends SelectItem> extends
        AbstractFilteredCollection<T> {

    public AbstractFilteredSelectItems() {
        this(new ArrayList<T>());
    }

    public AbstractFilteredSelectItems(T[] selecItems) {
        this(Arrays.asList(selecItems));
    }

    public AbstractFilteredSelectItems(List<T> collection) {
        super(collection);
    }

    protected AbstractFilteredSelectItems(T[] selecItems, boolean sort) {
        this((sort) ? sort(new ArrayList<T>(Arrays.asList(selecItems)))
                : Arrays.asList(selecItems));
    }

    protected AbstractFilteredSelectItems(List<T> collection, boolean sort) {
        super((sort) ? sort(new ArrayList<T>(collection)) : collection);
    }

    protected static <T extends SelectItem> List<T> sort(List<T> items) {

        Collections.sort(items, new Comparator<T>() {

            public int compare(T o1, T o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }

        });

        return items;
    }
}
