/*
 * $Id: AbstractFiltredDataModel.java,v 1.1 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.IAdapterManager;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:00 $
 */
public abstract class AbstractFiltredDataModel<E> extends DataModel implements
        IAdaptable, IFiltredModel, StateHolder {

    private static final Log LOG = LogFactory
            .getLog(AbstractFiltredDataModel.class);

    private List<E> filtredDatas;

    private int rowIndex;

    private IFilterProperties filterProperties;

    private int rowCount = -1;

    private boolean transientFlag;

    private DataModel wrappedDataModel;

    private DataModelListener dataModelListener = new DataModelListener() {

        public void rowSelected(DataModelEvent event) {
            if (filterProperties != null) {
                return;
            }

            fireRowIndexEvent(event.getRowIndex());
        }
    };

    public AbstractFiltredDataModel() {
        super();
    }

    public AbstractFiltredDataModel(Object[] array) {
        this(new ArrayDataModel(array));
    }

    public AbstractFiltredDataModel(DataModel dataModel) {
        setWrappedDataModel(dataModel);
    }

    protected void setWrappedDataModel(DataModel dataModel) {

        if (wrappedDataModel != null) {
            wrappedDataModel.removeDataModelListener(dataModelListener);
        }

        wrappedDataModel = dataModel;

        if (dataModel == null) {
            rowCount = -1;
            resetFilterResources();
            return;
        }

        wrappedDataModel.addDataModelListener(dataModelListener);
        rowCount = dataModel.getRowCount();
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        if (adapter.isAssignableFrom(getClass())) {
            return (T) this;
        }

        IAdapterManager adapterManager = RcfacesContext.getCurrentInstance()
                .getAdapterManager();

        return adapterManager.getAdapter(this, adapter, parameter);
    }

    @Override
    public boolean isRowAvailable() {
        initializeFiltredDatas();

        if (filterProperties == null) {
            return wrappedDataModel.isRowAvailable();
        }

        if (rowIndex < 0) {
            return false;
        }

        if (rowCount < 0) {
            return wrappedDataModel.isRowAvailable();
        }

        if (rowIndex >= filtredDatas.size()) {
            return false;
        }

        return true;
    }

    @Override
    public int getRowCount() {
        initializeFiltredDatas();

        if (filterProperties == null) {
            return wrappedDataModel.getRowCount();
        }

        if (rowCount < 0) {
            return -1;
        }

        return filtredDatas.size();
    }

    @Override
    public Object getRowData() {
        initializeFiltredDatas();

        if (filterProperties == null) {
            return wrappedDataModel.getRowData();
        }

        if (rowIndex < 0) {
            throw new FacesException("Invalid rowIndex '" + rowIndex + "'.");
        }

        if (rowCount < 0) {
            return wrappedDataModel.getRowData();
        }

        if (rowIndex >= filtredDatas.size()) {
            throw new FacesException("Invalid rowIndex '" + rowIndex + "'.");
        }

        return filtredDatas.get(rowIndex);
    }

    @Override
    public int getRowIndex() {
        initializeFiltredDatas();

        if (filterProperties == null) {
            return wrappedDataModel.getRowIndex();
        }

        return rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        initializeFiltredDatas();

        if (filterProperties == null) {
            wrappedDataModel.setRowIndex(rowIndex);
            return;
        }

        int oldRowIndex = this.rowIndex;

        if (oldRowIndex == rowIndex || (oldRowIndex < 0 && rowIndex < 0)) {
            return;
        }

        if (rowIndex < 0) {
            resetFilterResources();
            this.rowIndex = rowIndex;

            fireRowIndexEvent(rowIndex);
            return;
        }

        this.rowIndex = rowIndex;

        if (rowCount >= 0) {
            // On prend la liste, pas la peine de se repositionner !
            return;
        }

        DataModel dataModel = wrappedDataModel;

        if (oldRowIndex < 0) {
            oldRowIndex = -1;
        }

        if (oldRowIndex > rowIndex) {
            // Il faut rechercher depuis le début à cause du filtre
            dataModel.setRowIndex(-1);
            oldRowIndex = -1;
        }

        int dataModelRowIndex = dataModel.getRowIndex();

        for (dataModelRowIndex++; oldRowIndex < rowIndex; dataModelRowIndex++) {
            dataModel.setRowIndex(dataModelRowIndex);

            if (dataModel.isRowAvailable() == false) {
                return;
            }

            @SuppressWarnings("unchecked")
            E rowData = (E) dataModel.getRowData();

            if (accept(rowData, dataModelRowIndex, filterProperties) == false) {
                continue;
            }

            oldRowIndex++;
        }

        fireRowIndexEvent(rowIndex);
    }

    private void fireRowIndexEvent(int index) {
        DataModelListener[] listeners = getDataModelListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }

        Object rowData = null;
        if (isRowAvailable()) {
            rowData = getRowData();
        }

        DataModelEvent event = new DataModelEvent(this, index, rowData);

        for (DataModelListener listener : listeners) {
            if (listener != null) {
                listener.rowSelected(event);
            }
        }

    }

    protected void initializeFiltredDatas() {
        if (filterProperties == null) {
            return;
        }

        if (filtredDatas != null) {
            return;
        }

        if (rowCount >= 0) {
            filtredDatas = constructFiltredList(filterProperties);

        } else {
            filtredDatas = Collections.emptyList();
        }
    }

    protected List<E> constructFiltredList(IFilterProperties filterProperties) {

        List<E> filtredDatas = new ArrayList<E>();

        Object[] obj = (Object[]) getWrappedData();

        if (obj != null) {
            for (int i = 0; i < obj.length; i++) {
                @SuppressWarnings("unchecked")
                E rowData = (E) obj[i];

                if (accept(rowData, i, filterProperties) == false) {
                    continue;
                }

                filtredDatas.add(rowData);
            }
        }

        return filtredDatas;
    }

    protected abstract boolean accept(E rowData, int rowIndex,
            IFilterProperties filterProperties);

    protected void resetFilterResources() {
        filtredDatas = null;
        filterProperties = null;
    }

    public void setFilter(IFilterProperties filter) {
        this.filterProperties = filter;
    }

    public Object saveState(FacesContext context) {
        return wrappedDataModel;
    }

    public void restoreState(FacesContext context, Object state) {
        DataModel dataModel = (DataModel) state;

        setWrappedDataModel(dataModel);
    }

    public boolean isTransient() {
        return transientFlag;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientFlag = newTransientValue;
    }

    @Override
    public Object getWrappedData() {
        return wrappedDataModel.getWrappedData();
    }

    @Override
    public void setWrappedData(Object data) {
        resetFilterResources();
        rowIndex = -1;

        wrappedDataModel.setWrappedData(data);
    }

}
