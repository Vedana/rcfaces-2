/*
 * $Id: ICheckRangeComponent.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface ICheckRangeComponent extends ICheckComponent {

    /**
     * Checks the item at the given zero-relative index in the receiver. If the
     * item at the index was already checked, it remains checked. Indices that
     * are out of range are ignored.
     * 
     * @param index
     *            the index of the item to check
     */
    void check(int index);

    /**
     * Checks the items at the given zero-relative indices in the receiver. If
     * the item at a given index is not checked, it is checked. If the item at a
     * given index was already checked, it remains checked. Indices that are out
     * of range and duplicate indices are ignored. If the receiver is
     * single-check and multiple indices are specified, then all indices are
     * ignored.
     * 
     * @param indices
     *            the array of indices for the items to check
     */
    void check(int[] indices);

    /**
     * Checks the items in the range specified by the given zero-relative
     * indices in the receiver. The range of indices is inclusive. The current
     * selection is not cleared before the new items are checked. If an item in
     * the given range is not checked, it is checked. If an item in the given
     * range was already selected, it remains selected. Indices that are out of
     * range are ignored and no items will be checked if start is greater than
     * end. If the receiver is single-check and there is more than one item in
     * the given range, then all indices are ignored.
     * 
     * @param start
     *            the start of the range
     * @param end
     *            the end of the range
     */
    void check(int start, int end);

    /**
     * Unchecks the item at the given zero-relative index in the receiver. If
     * the item at the index was already deselected, it remains deselected.
     * Indices that are out of range are ignored.
     * 
     * @param index
     *            the index of the item to uncheck
     */
    void uncheck(int index);

    /**
     * Unchecks the items at the given zero-relative indices in the receiver. If
     * the item at the given zero-relative index in the receiver is selected, it
     * is deselected. If the item at the index was not selected, it remains
     * deselected. Indices that are out of range and duplicate indices are
     * ignored.
     * 
     * @param indices
     *            the array of indices for the items to uncheck
     */
    void uncheck(int[] indices);

    /**
     * Uncheks the items at the given zero-relative indices in the receiver. If
     * the item at the given zero-relative index in the receiver is selected, it
     * is deselected. If the item at the index was not selected, it remains
     * deselected. The range of the indices is inclusive. Indices that are out
     * of range are ignored.
     * 
     * @param start
     *            the start index of the items to uncheck
     * @param end
     *            the end index of the items to uncheck
     */
    void uncheck(int start, int end);
}
