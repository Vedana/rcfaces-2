/*
 * $Id: IGridComponent.java,v 1.3 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

import javax.faces.model.DataModel;

import org.rcfaces.core.component.iterator.IColumnIterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:17:38 $
 */
public interface IGridComponent extends IColumnsContainer {
    IColumnIterator listColumns();

    /**
     * Returns a DataModel if the value of the component is a DataModel
     */
    DataModel getDataModelValue();

    void setRowIndex(int index);

    Object getRowData();

    int getRowCount();

    int getRows();

    int getFirst();

    String getVar();

    String getRowCountVar();

    String getRowIndexVar();

    void setFirst(int position);

    boolean isRowAvailable();
}
