/*
 * $Id: ArrayIndexesModel.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.model.AbstractIndexesModel;
import org.rcfaces.core.model.ICommitableObject;
import org.rcfaces.core.model.IIndexesModel;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class ArrayIndexesModel extends AbstractIndexesModel implements
        Serializable, ICommitableObject {
    

    private static final long serialVersionUID = 7393822820762985697L;

    private static final Log LOG = LogFactory.getLog(ArrayIndexesModel.class);

    protected static final int[] EMPTY_SELECTION = new int[0];

    private static final boolean VERIFY_GARBAGE = LOG.isDebugEnabled();
    static {
        if (VERIFY_GARBAGE) {
            LOG.info("Verify GARBAGE enabled");
        }
    }

    private int selectionIndexes[] = EMPTY_SELECTION;

    private int lastPos = 0;

    private int count = 0;

    private boolean garbaged = true;

    private boolean commited;

    public ArrayIndexesModel() {
    }

    public ArrayIndexesModel(int[] indexes) {
        setIndexes(indexes, false);
    }

    public int[] listSortedIndexes() {
        if (count == 0) {
            return EMPTY_SELECTION;
        }

        garbage();

        int a[] = new int[count];
        System.arraycopy(selectionIndexes, 0, a, 0, count);

        Arrays.sort(a);

        return a;
    }

    public int getFirstIndex() {
        if (count == 0) {
            return -1;
        }

        garbage();

        return selectionIndexes[0];
    }

    private void garbage() {
        if (garbaged) {
            return;
        }

        garbaged = true;
        if (count == lastPos) {
            return;
        }

        int lastEmpty = -1;
        int last = -1;
        for (int i = 0; i < lastPos; i++) {
            int n = selectionIndexes[i];

            if (n < 0) {
                if (lastEmpty < 0) {
                    lastEmpty = i;
                }

                continue;
            }

            if (lastEmpty >= 0) {
                selectionIndexes[lastEmpty] = n;
                last = lastEmpty;
                lastEmpty++;
                continue;
            }

            last = i;

            continue;
        }

        // Assure que count==last
        if (count != last + 1) {
            LOG.error("Y a un probleme ! (count!=last)");
        }

        if (VERIFY_GARBAGE) {
            Set<Integer> set = new HashSet<Integer>(count);
            for (int i = 0; i < count; i++) {
                if (set.add(new Integer(i)) == false) {
                    LOG.error("Y a un GROS probleme avec le garbage !");
                }
            }
        }

        lastPos = count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.ISelectionModel#clearSelections()
     */
    public void clearIndexes() {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }
        count = 0;
        lastPos = 0;
        garbaged = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.ISelectionModel#isSelected(int)
     */
    public boolean containsIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Invalid index (" + index
                    + " < 0)");
        }

        if (count == 0) {
            return false;
        }

        garbage();

        for (int i = 0; i < count; i++) {
            int n = selectionIndexes[i];

            if (index == n) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.ISelectionModel#addSelection(int)
     */
    public boolean addIndex(int index) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        if (index < 0) {
            throw new IllegalArgumentException("Invalid index (" + index
                    + " < 0)");
        }

        for (int i = 0; i < lastPos; i++) {
            int n = selectionIndexes[i];

            if (index == n) {
                return false;
            }
        }

        if (lastPos < selectionIndexes.length) {
            selectionIndexes[lastPos++] = index;
            count++;
            garbaged = false;
            return true;
        }

        garbage();

        if (lastPos < selectionIndexes.length) {
            selectionIndexes[lastPos++] = index;
            count++;
            garbaged = false;
            return true;
        }

        // On agrandi
        int d = count / 2;
        if (d < 8) {
            d = 8;
        }
        int n[] = new int[count + d];
        if (count > 0) {
            System.arraycopy(selectionIndexes, 0, n, 0, count);
        }

        selectionIndexes = n;

        selectionIndexes[lastPos++] = index;
        count++;
        garbaged = false;

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.ISelectionModel#removeSelection(int)
     */
    public boolean removeIndex(int index) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        if (index < 0) {
            throw new IllegalArgumentException("Invalid index (" + index
                    + " < 0)");
        }
        if (count == 0) {
            return false;
        }

        for (int i = 0; i < lastPos; i++) {
            if (selectionIndexes[i] != index) {
                continue;
            }

            selectionIndexes[i] = -1;
            count--;
            garbaged = false;
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#setIndexes(int[])
     */
    public void setIndexes(int[] indexes) {
        setIndexes(indexes, true);
    }

    public void setIndexes(int[] indexes, boolean copy) {
        if (commited) {
            throw new IllegalStateException("Already commited indexes model.");
        }

        if (indexes == null || indexes.length < 1) {
            selectionIndexes = EMPTY_SELECTION;
            lastPos = 0;
            count = 0;
            return;
        }

        if (copy) {
            selectionIndexes = new int[indexes.length];
            System.arraycopy(indexes, 0, selectionIndexes, 0, indexes.length);

        } else {
            selectionIndexes = indexes;
        }

        garbaged = true;
        count = selectionIndexes.length;
        lastPos = count;

        for (int i = 0; i < selectionIndexes.length; i++) {
            if (selectionIndexes[i] >= 0) {
                continue;
            }

            garbaged = false;
            count--;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IIndexesModel#countIndexes()
     */
    public int countIndexes() {
        return count;
    }

    public IIndexesModel copy() {
        garbage();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Copy model");
        }

        return new ArrayIndexesModel(selectionIndexes);
    }

    public void commit() {
        garbage();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Commit model");
        }

        commited = true;
    }

    public boolean isCommited() {
        return commited;
    }

}
