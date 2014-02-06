/*
 * $Id: IDataModel.java,v 1.1 2011/04/12 09:25:43 oeuillot Exp $
 */
package org.rcfaces.core.model;

import javax.faces.FacesException;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:43 $
 */
public interface IDataModel {
    /**
     * <p>
     * Return a flag indicating whether there is <code>rowData</code>
     * available at the current <code>rowIndex</code>. If no
     * <code>wrappedData</code> is available, return <code>false</code>.
     * </p>
     * 
     * @exception FacesException
     *                if an error occurs getting the row availability
     */
    boolean isRowAvailable();

    /**
     * <p>
     * Return the number of rows of data objects represented by this
     * {@link DataModel}. If the number of rows is unknown, or no
     * <code>wrappedData</code> is available, return -1.
     * </p>
     * 
     * @exception FacesException
     *                if an error occurs getting the row count
     */
    int getRowCount();

    /**
     * <p>
     * Return an object representing the data for the currenty selected row
     * index. If no <code>wrappedData</code> is available, return
     * <code>null</code>.
     * </p>
     * 
     * @exception FacesException
     *                if an error occurs getting the row data
     * @exception IllegalArgumentException
     *                if now row data is available at the currently specified
     *                row index
     */
    Object getRowData();

    /**
     * <p>
     * Return the zero-relative index of the currently selected row. If we are
     * not currently positioned on a row, or no <code>wrappedData</code> is
     * available, return -1.
     * </p>
     * 
     * @exception FacesException
     *                if an error occurs getting the row index
     */
    int getRowIndex();

    /**
     * <p>
     * Set the zero-relative index of the currently selected row, or -1 to
     * indicate that we are not positioned on a row. It is possible to set the
     * row index at a value for which the underlying data collection does not
     * contain any row data. Therefore, callers may use the
     * <code>isRowAvailable()</code> method to detect whether row data will be
     * available for use by the <code>getRowData()</code> method.
     * </p>
     * 
     * <p>
     * If there is no <code>wrappedData</code> available when this method is
     * called, the specified <code>rowIndex</code> is stored (and may be
     * retrieved by a subsequent call to <code>getRowData()</code>), but no
     * event is sent. Otherwise, if the currently selected row index is changed
     * by this call, a {@link DataModelEvent} will be sent to the
     * <code>rowSelected()</code> method of all registered
     * {@link DataModelListener}s.
     * </p>
     * 
     * @param rowIndex
     *            The new zero-relative index (must be non-negative)
     * 
     * @exception FacesException
     *                if an error occurs setting the row index
     * @exception IllegalArgumentException
     *                if <code>rowIndex</code> is less than -1
     */
    void setRowIndex(int rowIndex);

    /**
     * <p>
     * Return the object representing the data wrapped by this {@link DataModel},
     * if any.
     * </p>
     */
    Object getWrappedData();

    /**
     * <p>
     * Set the object representing the data collection wrapped by this
     * {@link DataModel}. If the specified <code>data</code> is
     * <code>null</code>, detach this {@link DataModel} from any previously
     * wrapped data collection instead.
     * </p>
     * 
     * <p>
     * If <code>data</code> is non-<code>null</code>, the currently
     * selected row index must be set to zero, and a {@link DataModelEvent} must
     * be sent to the <code>rowSelected()</code> method of all registered
     * {@link DataModelListener}s indicating that this row is now selected.
     * </p>
     * 
     * @param data
     *            Data collection to be wrapped, or <code>null</code> to
     *            detach from any previous data collection
     * 
     * @exception ClassCastException
     *                if <code>data</code> is not of the appropriate type for
     *                this {@link DataModel} implementation
     */
    void setWrappedData(Object data);
}
