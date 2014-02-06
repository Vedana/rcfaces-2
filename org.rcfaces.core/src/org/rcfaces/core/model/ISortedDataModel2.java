/*
 * $Id: ISortedDataModel2.java,v 1.1 2011/04/12 09:25:43 oeuillot Exp $
 */
package org.rcfaces.core.model;

import javax.faces.component.UIColumn;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:43 $
 */
public interface ISortedDataModel2 {
    boolean isColumnSortable(UIColumn column);
}
