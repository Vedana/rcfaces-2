/*
 * $Id: IIndexesModel.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.model;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IIndexesModel {

    /**
     * Number of selected items.
     */
    int countIndexes();

    /**
     * List all indexes sorted by ascendending.
     */
    int[] listSortedIndexes();

    /**
     * Returns the first index.
     */
    int getFirstIndex();

    /**
     * Clear all selection.
     */
    void clearIndexes();

    /**
     * Specify the indexes of selected items.
     */
    void setIndexes(int indexes[]);

    /**
     * Returns if the index of item is selected.
     */
    boolean containsIndex(int index);

    boolean addIndex(int index);

    boolean removeIndex(int index);

    Object[] listSelectedObjects(Object toArray[], Object value);

    Object getFirstSelectedObject(Object cachedValue);

    IIndexesModel copy();
}
