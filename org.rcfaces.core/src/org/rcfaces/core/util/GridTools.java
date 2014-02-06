package org.rcfaces.core.util;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.capability.ISortEventCapability;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.renderkit.AbstractCameliaRenderer0;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.lang.FilterPropertiesMap;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.model.ISortedComponent;

/**
 * 
 * @author jbmeslin@vedana.com
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class GridTools {

    private static final Log LOG = LogFactory.getLog(GridTools.class);

    /**
     * 
     * @param sortedComponent
     *            table of the current sorted columns
     * @return boolean true if one is sort on server
     */
    public static boolean hasSortedServerListener(
            ISortedComponent sortedComponent[]) {

        for (ISortedComponent iSortedComponent : sortedComponent) {
            UIComponent columnComponent = iSortedComponent.getComponent();
            if ((columnComponent instanceof ISortEventCapability) == false) {
                continue;
            }
            FacesListener[] facesListeners = ((ISortEventCapability) columnComponent)
                    .listSortListeners();
            for (FacesListener facesListener : facesListeners) {
                if (facesListener instanceof IScriptListener) {
                    String command = ((IScriptListener) facesListener)
                            .getCommand();
                    if (ISortEventCapability.SORT_SERVER.equals(command)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public static UIColumn getColumn(IGridComponent dg, String columnId) {
        for (IColumnIterator it = dg.listColumns(); it.hasNext();) {
            UIColumn column = it.next();
            if (columnId.equals(column.getId()) == false) {
                continue;
            }

            return column;
        }

        return null;
    }

    public static <T> T searchRowData(FacesContext facesContext,
            IGridComponent component, IFilterGridProcessor<T> processor,
            Object selectedValue, String convertedSelectedValue) {

        DataModel dataModel = component.getDataModelValue();

        IComponentRefModel componentRefModel = AbstractCameliaRenderer0
                .getAdapter(IComponentRefModel.class, dataModel, null);

        if (componentRefModel != null) {
            componentRefModel.setComponent((UIComponent) component);
        }

        IFiltredModel filtredDataModel = AbstractCameliaRenderer0.getAdapter(
                IFiltredModel.class, dataModel, null);

        if (filtredDataModel == null) {

            if (LOG.isInfoEnabled()) {
                LOG.info("Search a row value in a not filtred DataModel ! (comboGridComponent="
                        + ((UIComponent) component).getId() + ")");
            }

            processor.initializeFilterProperties(null, dataModel);

            UIComponent rowValueColumn = processor.getRowValueColumn(component);
            if ((rowValueColumn instanceof ValueHolder) == false) {
                throw new FacesException("Can not get row value for column '"
                        + rowValueColumn + "'.");
            }
            ValueHolder columnValueHolder = (ValueHolder) rowValueColumn;

            String var = component.getVar();
            if (var == null) {
                throw new FacesException("Var attribute is null !");
            }

            Map<String, Object> requestMap = facesContext.getExternalContext()
                    .getRequestMap();
            Object oldValue = requestMap.get(var);

            try {
                for (int rowIndex = 0;; rowIndex++) {
                    dataModel.setRowIndex(rowIndex);

                    if (dataModel.isRowAvailable() == false) {
                        break;
                    }

                    Object rowData = dataModel.getRowData();

                    requestMap.put(var, rowData);

                    Object value = columnValueHolder.getValue();

                    if (selectedValue != null && selectedValue.equals(value)) {
                        return processor.process(facesContext, component,
                                rowData);
                    }

                    if (convertedSelectedValue == null) {
                        continue;
                    }

                    String convertedValue = ValuesTools.convertValueToString(
                            value, rowValueColumn, facesContext);

                    if (convertedSelectedValue.equals(convertedValue) == false) {
                        continue;
                    }

                    return processor.process(facesContext, component, rowData);
                }

            } finally {
                requestMap.put(var, oldValue);
                dataModel.setRowIndex(-1);
            }

            return null;
        }

        IFilterProperties filterProperties = ((IFilterCapability) component)
                .getFilterProperties();
        if (filterProperties == null) {
            filterProperties = new FilterPropertiesMap();

        } else {
            filterProperties = new FilterPropertiesMap(filterProperties);
        }

        if (processor.initializeFilterProperties(filterProperties, dataModel) == false) {

            if (convertedSelectedValue != null) {
                filterProperties.put("key", convertedSelectedValue);
                filterProperties.put("text", convertedSelectedValue);

                filterProperties.put("convertedValue", convertedSelectedValue);
            }

            if (selectedValue != null) {
                filterProperties.put("value", selectedValue);
            }
        }

        filtredDataModel.setFilter(filterProperties);

        try {
            dataModel.setRowIndex(0);

            boolean available = dataModel.isRowAvailable();

            if (LOG.isDebugEnabled()) {
                LOG.debug("formatValue index=0 available=" + available);
            }

            if (available == false) {
                return null;
            }

            Object rowData = dataModel.getRowData();

            String var = component.getVar();

            if (LOG.isDebugEnabled()) {
                LOG.debug("formatValue rowData='" + rowData + "' var='" + var
                        + "'");
            }

            if (var == null) {
                throw new FacesException("Var attribute is null !");
            }

            Map<String, Object> requestMap = facesContext.getExternalContext()
                    .getRequestMap();
            Object oldValue = requestMap.put(var, rowData);

            try {
                return processor.process(facesContext, component, rowData);

            } finally {
                requestMap.put(var, oldValue);
            }

        } finally {
            dataModel.setRowIndex(-1);
        }
    }

    public static <T> T searchRowData(FacesContext facesContext,
            DataModel dataModel, IFilterDataModelProcessor<T> processor) {

        IFiltredModel filtredDataModel = AbstractCameliaRenderer0.getAdapter(
                IFiltredModel.class, dataModel, null);

        if (filtredDataModel == null) {

            if (LOG.isInfoEnabled()) {
                LOG.info("Search a row value in a not filtred DataModel ! (comboGridComponent="
                        + dataModel + ")");
            }

            processor.initializeFilterProperties(null, dataModel);

            try {
                for (int rowIndex = 0;; rowIndex++) {
                    dataModel.setRowIndex(rowIndex);

                    if (dataModel.isRowAvailable() == false) {
                        break;
                    }

                    Object rowData = dataModel.getRowData();

                    if (processor.accept(facesContext, dataModel, rowData) == false) {
                        continue;
                    }

                    return processor.process(facesContext, dataModel, rowData);
                }

            } finally {
                dataModel.setRowIndex(-1);
            }

            return null;
        }

        IFilterProperties filterProperties = new FilterPropertiesMap();

        processor.initializeFilterProperties(filterProperties, dataModel);

        filtredDataModel.setFilter(filterProperties);

        try {
            dataModel.setRowIndex(0);

            boolean available = dataModel.isRowAvailable();

            if (LOG.isDebugEnabled()) {
                LOG.debug("formatValue index=0 available=" + available);
            }

            if (available == false) {
                return null;
            }

            Object rowData = dataModel.getRowData();

            return processor.process(facesContext, dataModel, rowData);

        } finally {
            dataModel.setRowIndex(-1);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
     */
    public interface IFilterGridProcessor<T> {

        boolean initializeFilterProperties(IFilterProperties filterProperties,
                DataModel dataModel);

        UIComponent getRowValueColumn(IGridComponent component);

        T process(FacesContext facesContext, IGridComponent dataComponent,
                Object rowData);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
     */
    public interface IFilterDataModelProcessor<T> {

        boolean initializeFilterProperties(IFilterProperties filterProperties,
                DataModel dataModel);

        boolean accept(FacesContext facesContext, DataModel dataModel,
                Object rowData);

        T process(FacesContext facesContext, DataModel dataModel, Object rowData);
    }
}
