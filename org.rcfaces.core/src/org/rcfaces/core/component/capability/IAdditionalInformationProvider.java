/*
 * $Id: IAdditionalInformationProvider.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IAdditionalInformationProvider {
    Object getAdditionalInformationValues();

    void setAdditionalInformationValues(Object additionalInformationValues);

    int getAdditionalInformationValuesCount();

    Object getFirstAdditionalInformationValue();

    Object[] listAdditionalInformationValues();
}
