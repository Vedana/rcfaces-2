/*
 * $Id: ISelectionProvider.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.lang.provider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public interface ISelectionProvider {
    Object getSelectedValues();

    void setSelectedValues(Object selectedValues);

    int getSelectedValuesCount();

    Object getFirstSelectedValue();

    Object[] listSelectedValues();
}
