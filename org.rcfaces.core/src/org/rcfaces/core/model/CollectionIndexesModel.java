/*
 * $Id: CollectionIndexesModel.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class CollectionIndexesModel<T> extends AbstractIndexesModel implements
        Serializable, ICommitableObject {
    private static final long serialVersionUID = -3821092264981658279L;

    protected static final int[] EMPTY_SELECTION = new int[0];

    protected static final int UNKNOWN_INDEX = -1;

    protected final Collection<T> collection;

    protected boolean commited;

    public CollectionIndexesModel(Collection<T> collection) {
        this.collection = collection;
    }

    public int getFirstIndex() {
        if (collection.isEmpty()) {
            return -1;
        }

        if (collection instanceof List) {
            return getIndex(((List<T>) collection).get(0));
        }

        return getIndex(collection.iterator().next());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#listIndexes()
     */
    public final int[] listSortedIndexes() {
        if (collection.isEmpty()) {
            return EMPTY_SELECTION;
        }

        int n[] = new int[collection.size()];
        int pos = 0;
        int unknownIndex = getUnknownIndex();
        for (Iterator<T> it = collection.iterator(); it.hasNext();) {
            int idx = getIndex(it.next());

            if (idx == unknownIndex) {
                continue;
            }

            n[pos++] = idx;
        }

        if (pos == n.length) {
            if (n.length > 1) {
                Arrays.sort(n);
            }
            return n;
        }

        int n2[] = new int[pos];

        System.arraycopy(n, 0, n2, 0, pos);

        if (n2.length > 1) {
            Arrays.sort(n2);
        }

        return n2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#clearIndexes()
     */
    public final void clearIndexes() {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        collection.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#containsIndex(int)
     */
    public final boolean containsIndex(int index) {
        return collection.contains(getKey(index));
    }

    protected int getIndex(T object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        return getUnknownIndex();
    }

    protected T getKey(int index) {
        T key = (T) new Integer(index);

        return key;
    }

    protected int getUnknownIndex() {
        return UNKNOWN_INDEX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#addIndex(int)
     */
    public boolean addIndex(int index) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        return collection.add(getKey(index));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#removeIndex(int)
     */
    public final boolean removeIndex(int index) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        return collection.remove(getKey(index));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#setIndexes(int[])
     */
    public void setIndexes(int[] indexes) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        clearIndexes();

        if (indexes == null || indexes.length < 1) {
            return;
        }

        for (int i = 0; i < indexes.length; i++) {
            int val = indexes[i];
            if (val < 0) {
                continue;
            }

            addIndex(val);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#countIndexes()
     */
    public int countIndexes() {
        if (collection.isEmpty()) {
            return 0;
        }

        int count = 0;
        int unknownIndex = getUnknownIndex();
        for (Iterator<T> it = collection.iterator(); it.hasNext();) {
            int idx = getIndex(it.next());

            if (idx == unknownIndex) {
                continue;
            }

            count++;
        }

        return count;
    }

    public void commit() {
        this.commited = true;

        if (collection instanceof ArrayList) {
            ((ArrayList<T>) collection).trimToSize();
        }
    }

    public boolean isCommited() {
        return commited;
    }

    public IIndexesModel copy() {
        return new CollectionIndexesModel<T>(collection);
    }
}
