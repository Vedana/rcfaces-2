/*
 * $Id: ISelectionRangeComponent.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface ISelectionRangeComponent extends ISelectionComponent {

    /**
     * Selects the item at the given zero-relative index in the receiver. If the
     * item at the index was already selected, it remains selected. Indices that
     * are out of range are ignored.
     * 
     * @param index
     *            the index of the item to select
     */
    void select(int index);

    /**
     * Selects the items at the given zero-relative indices in the receiver. The
     * current selection is not cleared before the new items are selected. If
     * the item at a given index is not selected, it is selected. If the item at
     * a given index was already selected, it remains selected. Indices that are
     * out of range and duplicate indices are ignored. If the receiver is
     * single-select and multiple indices are specified, then all indices are
     * ignored.
     * 
     * @param indices
     *            the array of indices for the items to select
     */
    void select(int[] indices);

    /**
     * Selects the items in the range specified by the given zero-relative
     * indices in the receiver. The range of indices is inclusive. The current
     * selection is not cleared before the new items are selected. If an item in
     * the given range is not selected, it is selected. If an item in the given
     * range was already selected, it remains selected. Indices that are out of
     * range are ignored and no items will be selected if start is greater than
     * end. If the receiver is single-select and there is more than one item in
     * the given range, then all indices are ignored.
     * 
     * @param start
     *            the start of the range
     * @param end
     *            the end of the range
     */
    void select(int start, int end);

    /**
     * Deselects the item at the given zero-relative index in the receiver. If
     * the item at the index was already deselected, it remains deselected.
     * Indices that are out of range are ignored.
     * 
     * @param index
     *            the index of the item to deselect
     */
    void deselect(int index);

    /**
     * Deselects the items at the given zero-relative indices in the receiver.
     * If the item at the given zero-relative index in the receiver is selected,
     * it is deselected. If the item at the index was not selected, it remains
     * deselected. Indices that are out of range and duplicate indices are
     * ignored.
     * 
     * @param indices
     *            the array of indices for the items to deselect
     */
    void deselect(int[] indices);

    /**
     * Deselects the items at the given zero-relative indices in the receiver.
     * If the item at the given zero-relative index in the receiver is selected,
     * it is deselected. If the item at the index was not selected, it remains
     * deselected. The range of the indices is inclusive. Indices that are out
     * of range are ignored.
     * 
     * @param start
     *            the start index of the items to deselect
     * @param end
     *            the end index of the items to deselect
     */
    void deselect(int start, int end);
}
