/*
 * $Id: IAdditionalInformationComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

import org.rcfaces.core.component.iterator.IAdditionalInformationIterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IAdditionalInformationComponent {
    /**
     * 
     */
    void showAdditionalInformation(Object rowValue);

    /**
     * Show all additional informations.
     */
    void showAllAdditionalInformations();

    /**
     * 
     */
    void hideAdditionalInformation(Object rowValue);

    /**
     * Hide all shown additional informations.
     */
    void hideAllAdditionalInformations();

    IAdditionalInformationIterator listAdditionalInformations();
}
