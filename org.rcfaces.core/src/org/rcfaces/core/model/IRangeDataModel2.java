/*
 * $Id: IRangeDataModel2.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.model;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IRangeDataModel2 {
    void setRowRange(int index, int length, boolean testLastRow);
}
