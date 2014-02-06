/*
 * $Id: IndexesModels.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import javax.faces.model.DataModel;

import org.rcfaces.core.internal.tools.ArrayIndexesModel;

/**
 * IIndexesModel constructors.
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public class IndexesModels {

    private static final Object[] OBJECT_EMPTY_ARRAY = new Object[0];

    /**
     * Returns an IIndexesModel which all items are selected.
     * 
     * @param length
     *            Number of item of the selection.
     */
    public static IIndexesModel selectAll(int length) {
        int s[] = new int[length];
        for (int i = 0; i < length; i++) {
            s[i] = i;
        }

        return new ArrayIndexesModel(s);
    }

    public static IIndexesModel selectAll(Collection collection, int length) {
        IIndexesModel indexesModel = new CollectionIndexesModel(collection);

        select(indexesModel, 0, length);

        return indexesModel;
    }

    public static IIndexesModel selectAll(Map map, int length) {
        IIndexesModel indexesModel = new MapIndexesModel(map);

        select(indexesModel, 0, length);

        return indexesModel;
    }

    public static void select(IIndexesModel model, int start, int length) {

        for (int i = 0; i < length; i++) {
            model.addIndex(start + i);
        }
    }

    public static Object[] listSelectedObject(Object destination[],
            Object value, IIndexesModel indexesModel) {
        if (indexesModel == null) {
            throw new NullPointerException("IndexesModel is null !");
        }

        int indexes[] = indexesModel.listSortedIndexes();
        if (value == null || indexes == null || indexes.length < 1) {
            return OBJECT_EMPTY_ARRAY;
        }

        if (value instanceof Object[]) {
            Object values[] = (Object[]) value;

            List l = new ArrayList(indexes.length);
            for (int i = 0; i < indexes.length; i++) {
                int idx = indexes[i];
                if (idx < 0 || idx >= values.length) {
                    continue;
                }

                l.add(values[idx]);
            }

            return l.toArray(destination);
        }

        if (value instanceof Collection) {
            if (value instanceof RandomAccess) {
                List values = (List) value;
                int valuesLength = values.size();

                List l = new ArrayList(indexes.length);
                for (int i = 0; i < indexes.length; i++) {
                    int idx = indexes[i];
                    if (idx < 0 || idx >= valuesLength) {
                        continue;
                    }

                    l.add(values.get(idx));
                }

                return l.toArray(destination);

            }

            Object values[] = ((Collection) value).toArray();

            List l = new ArrayList(indexes.length);
            for (int i = 0; i < indexes.length; i++) {
                int idx = indexes[i];
                if (idx < 0 || idx >= values.length) {
                    continue;
                }

                l.add(values[idx]);
            }

            return l.toArray(destination);
        }

        if (value instanceof DataModel) {
            DataModel dataModel = (DataModel) value;

            boolean closeDataModel = false;
            try {
                List l = new ArrayList(indexes.length);
                for (int i = 0; i < indexes.length; i++) {
                    int idx = indexes[i];
                    if (idx < 0) {
                        continue;
                    }

                    dataModel.setRowIndex(idx);
                    closeDataModel = true;

                    if (dataModel.isRowAvailable() == false) {
                        continue;
                    }

                    l.add(dataModel.getRowData());
                }

                return l.toArray(destination);

            } finally {
                if (closeDataModel) {
                    dataModel.setRowIndex(-1);
                }
            }
        }

        throw new IllegalArgumentException("Value type '" + value.getClass()
                + "' is not supported !");
    }

    public static Object getFirstSelectedObject(Object value,
            IIndexesModel indexesModel) {
        if (indexesModel == null) {
            throw new NullPointerException("IndexesModel is null !");
        }

        int index = indexesModel.getFirstIndex();
        if (value == null || index < 0) {
            return null;
        }

        if (value instanceof Object[]) {
            Object values[] = (Object[]) value;

            return values[index];
        }

        if (value instanceof List) {
            List values = (List) value;

            return values.get(index);
        }

        if (value instanceof Collection) {
            if (index == 0) {
                return ((Collection) value).iterator().next();
            }

            Object values[] = ((Collection) value).toArray();

            return values[index];
        }

        if (value instanceof DataModel) {
            DataModel dataModel = (DataModel) value;

            if (dataModel instanceof IRangeDataModel) {
                ((IRangeDataModel) dataModel).setRowRange(index, 1);
            }
            dataModel.setRowIndex(index);
            try {
                if (dataModel.isRowAvailable() == false) {
                    return null;
                }

                return dataModel.getRowData();
            } finally {
                dataModel.setRowIndex(-1);
            }
        }

        throw new IllegalArgumentException("Value type '" + value.getClass()
                + "' is not supported !");
    }
}
