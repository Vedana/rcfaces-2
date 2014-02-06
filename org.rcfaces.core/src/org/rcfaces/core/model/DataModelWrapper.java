/*
 * $Id: DataModelWrapper.java,v 1.2 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:58 $
 */
public class DataModelWrapper extends DataModel implements IDataModel {

    private DataModel dataModel;

    protected void setWrappedDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    protected DataModel getWrappedDataModel() {
        return dataModel;
    }

    @Override
    public void addDataModelListener(DataModelListener listener) {
        dataModel.addDataModelListener(listener);
    }

    @Override
    public DataModelListener[] getDataModelListeners() {
        return dataModel.getDataModelListeners();
    }

    @Override
    public int getRowCount() {
        return dataModel.getRowCount();
    }

    @Override
    public Object getRowData() {
        return dataModel.getRowData();
    }

    @Override
    public int getRowIndex() {
        return dataModel.getRowIndex();
    }

    @Override
    public Object getWrappedData() {
        return dataModel.getWrappedData();
    }

    @Override
    public boolean isRowAvailable() {
        return dataModel.isRowAvailable();
    }

    @Override
    public void removeDataModelListener(DataModelListener listener) {
        dataModel.removeDataModelListener(listener);
    }

    @Override
    public void setRowIndex(int rowIndex) {
        dataModel.setRowIndex(rowIndex);
    }

    @Override
    public void setWrappedData(Object data) {
        dataModel.setWrappedData(data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dataModel == null) ? 0 : dataModel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DataModelWrapper other = (DataModelWrapper) obj;
        if (dataModel == null) {
            if (other.dataModel != null)
                return false;
        } else if (!dataModel.equals(other.dataModel))
            return false;
        return true;
    }

}
