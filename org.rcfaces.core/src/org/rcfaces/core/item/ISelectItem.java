/*
 * $Id: ISelectItem.java,v 1.1 2011/04/12 09:25:29 oeuillot Exp $
 */
package org.rcfaces.core.item;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:29 $
 */
public interface ISelectItem {
    String getLabel();

    String getDescription();

    Object getValue();

    boolean isDisabled();
}
