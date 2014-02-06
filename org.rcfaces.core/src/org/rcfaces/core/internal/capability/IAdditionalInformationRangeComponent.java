/*
 * $Id: IAdditionalInformationRangeComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IAdditionalInformationRangeComponent extends
        IAdditionalInformationComponent {

    /**
     * Show the additional information associated to the item at the given
     * zero-relative index in the receiver. If the item at the index was already
     * selected, it remains selected. Indices that are out of range are ignored.
     * 
     * @param index
     *            the index of the additional information to show
     */
    void showAdditionalInformation(int index);

    /**
     * Show the additional information associated to the item at the given
     * zero-relative index in the receiver. If the item at the index was already
     * selected, it remains selected. Indices that are out of range are ignored.
     * 
     * @param index
     *            the indexes of additional informations to show
     */
    void showAdditionalInformation(int indexes[]);

    /**
     * Hide the additional information associated to the item at the given
     * zero-relative index in the receiver. If the item at the index was already
     * selected, it remains selected. Indices that are out of range are ignored.
     * 
     * @param index
     *            the index of the additional information to hide
     */
    void hideAdditionalInformation(int index);

    /**
     * Hide the additional information associated to the item at the given
     * zero-relative index in the receiver. If the item at the index was already
     * selected, it remains selected. Indices that are out of range are ignored.
     * 
     * @param indexes
     *            the indexes of additional informations to hide
     */
    void hideAdditionalInformation(int indexes[]);
}
