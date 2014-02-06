/*
 * $Id: GridTools.java,v 1.5 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsColumnComponent;
import org.rcfaces.core.component.ComponentsGridComponent;
import org.rcfaces.core.component.DataColumnComponent;
import org.rcfaces.core.component.DataGridComponent;
import org.rcfaces.core.component.capability.IOrderCapability;
import org.rcfaces.core.component.capability.ISortedChildrenCapability;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.component.iterator.IComponentsColumnIterator;
import org.rcfaces.core.component.iterator.IDataColumnIterator;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.capability.ISortedComponentsCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.CollectionTools.IComponentValueType;
import org.rcfaces.core.internal.util.ComponentIterators;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.OrderedSet;
import org.rcfaces.core.model.DefaultSortedComponent;
import org.rcfaces.core.model.IDataModel;
import org.rcfaces.core.model.ISortedComponent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
 */
public class GridTools {

    private static final Log LOG = LogFactory.getLog(GridTools.class);

    public static final int SELECTION_VALUES_TYPE = 1;

    public static final int CHECK_VALUES_TYPE = 2;

    private static final IDataColumnIterator EMPTY_DATA_COLUMN_ITERATOR = new DataColumnListIterator(
            Collections.<DataColumnComponent> emptyList());

    private static final IColumnIterator EMPTY_COLUMNS_ITERATOR = new ColumnListIterator(
            Collections.<UIColumn> emptyList());

    private static final IComponentsColumnIterator EMPTY_COMPONENTS_COLUMN_ITERATOR = new ComponentsColumnListIterator(
            Collections.<ComponentsColumnComponent> emptyList());

    private static final String ALL_INDEX = "all";

    private static final ISortedComponent[] SORTED_COMPONENTS_EMPTY_ARRAY = new ISortedComponent[0];

    private static final UIColumn[] COLUMN_EMPTY_ARRAY = new UIColumn[0];

    private static final UIComponent[] COMPONENT_EMPTY_ARRAY = new UIComponent[0];

    @SuppressWarnings("unchecked")
    public static <T extends UIColumn> IColumnIterator listColumns(
            IColumnsContainer component, Class<T> filter) {
        List<UIColumn> list = (List<UIColumn>) ComponentIterators.list(
                (UIComponent) component, filter);
        if (list.isEmpty()) {
            return EMPTY_COLUMNS_ITERATOR;
        }

        return new ColumnListIterator(list);
    }

    public static IDataColumnIterator listDataColumns(IGridComponent component) {
        List<DataColumnComponent> list = ComponentIterators.list(
                (UIComponent) component, DataColumnComponent.class);
        if (list.isEmpty()) {
            return EMPTY_DATA_COLUMN_ITERATOR;
        }

        return new DataColumnListIterator(list);
    }

    public static IComponentsColumnIterator listComponentsColumns(
            IGridComponent component) {
        List<ComponentsColumnComponent> list = ComponentIterators.list(
                (UIComponent) component, ComponentsColumnComponent.class);
        if (list.isEmpty()) {
            return EMPTY_COMPONENTS_COLUMN_ITERATOR;
        }

        return new ComponentsColumnListIterator(list);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
     */
    private static final class DataColumnListIterator extends
            ComponentIterators.ComponentListIterator<DataColumnComponent>
            implements IDataColumnIterator {

        public DataColumnListIterator(List<DataColumnComponent> list) {
            super(list);
        }

        public final DataColumnComponent next() {
            return nextComponent();
        }

        public DataColumnComponent[] toArray() {
            return (DataColumnComponent[]) toArray(new DataColumnComponent[count()]);
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
     */
    private static final class ComponentsColumnListIterator extends
            ComponentIterators.ComponentListIterator<ComponentsColumnComponent>
            implements IComponentsColumnIterator {

        public ComponentsColumnListIterator(List<ComponentsColumnComponent> list) {
            super(list);
        }

        public final ComponentsColumnComponent next() {
            return nextComponent();
        }

        public ComponentsColumnComponent[] toArray() {
            return (ComponentsColumnComponent[]) toArray(new ComponentsColumnComponent[count()]);
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
     */
    private static final class ColumnListIterator extends
            ComponentIterators.ComponentListIterator<UIColumn> implements
            IColumnIterator {

        public ColumnListIterator(List<UIColumn> list) {
            super(list);
        }

        public final UIColumn next() {
            return nextComponent();
        }

        public UIColumn[] toArray() {
            return (UIColumn[]) toArray(new UIColumn[count()]);
        }

    }

    /*
     * public static final IIndexesModel getIndexesModel(Object object,
     * DataModel dataModel) { if (object == null) { return new
     * ArrayIndexesModel(); }
     * 
     * if (object instanceof IIndexesModel) { return (IIndexesModel) object; }
     * 
     * if (ALL_INDEX.equals(object)) { Object value = dataModel;
     * 
     * if (value == null) { return new ArrayIndexesModel(); }
     * 
     * Class valueClass = value.getClass(); if (valueClass.isArray()) { int size
     * = Array.getLength(value);
     * 
     * return IndexesModels.selectAll(size); }
     * 
     * if (value instanceof Collection) { Collection collection = (Collection)
     * value;
     * 
     * return IndexesModels.selectAll(collection.size()); }
     * 
     * if (value instanceof Map) { Map collection = (Map) value;
     * 
     * return IndexesModels.selectAll(collection.size()); }
     * 
     * throw new FacesException( "'all' keyword for index model, does not
     * support value type: '" + value.getClass() + "'."); }
     * 
     * if (object instanceof Collection) { return new
     * CollectionIndexesModel((Collection) object); }
     * 
     * if (object instanceof Map) { return new MapIndexesModel((Map) object); }
     * 
     * List l = new ArrayList(1); l.add(object);
     * 
     * return new CollectionIndexesModel(l); }
     */

    public static ISortedComponent[] listSortedComponents(FacesContext context,
            ISortedChildrenCapability dataGridComponent) {

        UIComponent sortedColumns[] = dataGridComponent.getSortedChildren();
        if (sortedColumns.length < 1) {
            return SORTED_COMPONENTS_EMPTY_ARRAY;
        }

        UIColumn columns[] = ((IGridComponent) dataGridComponent).listColumns()
                .toArray();

        List<ISortedComponent> l = new ArrayList<ISortedComponent>(
                sortedColumns.length);
        for (int i = 0; i < sortedColumns.length; i++) {

            UIColumn column = (UIColumn) sortedColumns[i];

            if ((column instanceof IOrderCapability) == false) {
                continue;
            }

            int index = -1;
            for (int j = 0; j < columns.length; j++) {
                if (columns[j] != column) {
                    continue;
                }
                index = j;
                break;
            }

            if (index < 0) {
                LOG.error("Can not find column #" + i + " " + column);
                continue;
            }

            l.add(new DefaultSortedComponent(column, index,
                    ((IOrderCapability) column).isAscending()));

        }

        return l.toArray(new ISortedComponent[l.size()]);
    }

    public static DataModel getDataModel(Object current, UIComponent component,
            FacesContext facesContext) {

        if (current == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: null value type '" + current
                        + "' for component '" + component.getId()
                        + ", return a ListDataModel.");
            }

            return new ListDataModel(Collections.EMPTY_LIST);
        }

        if (current instanceof DataModel) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: DataModel value type '"
                        + current + "' for component '" + component.getId()
                        + ", return the value.");
            }

            return (DataModel) current;
        }

        if (current instanceof List) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: List value type '" + current
                        + "' for component '" + component.getId()
                        + ", return a ListDataModel.");
            }

            return new ListDataModel((List< ? >) current);
        }

        if (Object[].class.isAssignableFrom(current.getClass())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: Object array value type '"
                        + current + "' for component '" + component.getId()
                        + ", return an ArrayDataModel.");
            }

            return new ArrayDataModel((Object[]) current);
        }

        if (current instanceof ResultSet) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: ResultSet value type '"
                        + current + "' for component '" + component.getId()
                        + ", return a ResultSetDataModel.");
            }

            return new ResultSetDataModel((ResultSet) current);
        }

        if (current instanceof Result) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: Result value type '" + current
                        + "' for component '" + component.getId()
                        + ", return a ResultDataModel.");
            }

            return new ResultDataModel((Result) current);
        }

        if (Constants.COLLECTION_DATAMODEL_SUPPORT) {
            if (current instanceof Collection) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("DataModel conversion: Collection value type '"
                            + current + "' for component '" + component.getId()
                            + ", return an ArrayDataModel.");
                }

                return new ArrayDataModel(((Collection< ? >) current).toArray());
            }
        }

        if (current instanceof IDataModel) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DataModel conversion: IDataModel value type '"
                        + current + "' for component '" + component.getId()
                        + ", return a DataModelWrapper.");
            }

            return new DataModelWrapper((IDataModel) current);
        }

        if (Constants.ADAPTABLE_DATAMODEL_SUPPORT) {
            if (current instanceof IAdaptable) {
                DataModel dataModel = ((IAdaptable) current).getAdapter(
                        DataModel.class, component);
                if (dataModel != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("DataModel conversion: IAdaptable value type '"
                                + current
                                + "' for component '"
                                + component.getId() + ", return a DataModel.");
                    }

                    return dataModel;
                }
            }

            RcfacesContext rcfacesContext = RcfacesContext
                    .getInstance(facesContext);

            DataModel dataModel = rcfacesContext.getAdapterManager()
                    .getAdapter(current, DataModel.class, component);

            if (dataModel != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("DataModel conversion: AdaptableFactory response for type '"
                            + current
                            + "' for component '"
                            + component.getId()
                            + ", return a DataModel.");
                }

                return dataModel;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("DataModel conversion: Unknown value type '" + current
                    + "' for component '" + component.getId()
                    + ", return a ScalarDataModel.");
        }

        return new ScalarDataModel(current);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
     */
    static final class DataModelWrapper extends DataModel implements IDataModel {

        private final IDataModel dataModel;

        public DataModelWrapper(IDataModel dataModel) {
            this.dataModel = dataModel;
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
        public void setRowIndex(int rowIndex) {
            dataModel.setRowIndex(rowIndex);
        }

        @Override
        public void setWrappedData(Object data) {
            dataModel.setWrappedData(data);
        }

    }

    public static void setOrderIds(IGridComponent gridComponent,
            String columnsOrder) {
    }

    public static String getOrderIds(IGridComponent dataGridComponent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void setSortIds(IGridComponent gridComponent,
            String sortedColumnIds) {
        if (sortedColumnIds == null) {
            return;
        }

        if (gridComponent instanceof ISortedComponentsCapability) {
            ISortedChildrenCapability sortedChildrenCapability = (ISortedChildrenCapability) gridComponent;

            if (sortedColumnIds.length() < 1) {
                sortedChildrenCapability
                        .setSortedChildren(COMPONENT_EMPTY_ARRAY);
                return;
            }

            UIColumn columns[] = gridComponent.listColumns().toArray();

            StringTokenizer st = new StringTokenizer(sortedColumnIds, ",");
            List<UIColumn> components = new ArrayList<UIColumn>(
                    st.countTokens());

            for (; st.hasMoreTokens();) {
                String id = st.nextToken().trim();

                boolean ascending = false;
                int idx = id.indexOf("::");
                if (idx >= 0) {
                    String cmd = id.substring(0, idx);
                    id = id.substring(idx + 2);

                    ascending = ("asc".equals(cmd));
                }

                UIComponent component = null;
                if (id.charAt(0) == '#') {
                    int columnIdx = Integer.parseInt(id.substring(1));

                    if (columnIdx >= columns.length) {
                        continue;
                    }

                    component = columns[columnIdx];

                } else {
                    for (int j = 0; j < columns.length; j++) {
                        if (id.equals(columns[j].getId()) == false) {
                            continue;
                        }

                        component = columns[j];
                        break;
                    }
                }

                if (component == null) {
                    continue;
                }

                if ((component instanceof UIColumn) == false) {
                    continue;
                }

                components.add((UIColumn) component);

                if (component instanceof IOrderCapability) {
                    ((IOrderCapability) component).setAscending(ascending);
                }
            }

            sortedChildrenCapability.setSortedChildren(components
                    .toArray(new UIComponent[components.size()]));
            return;
        }

    }

    public static String getSortIds(IGridComponent gridComponent) {
        if (gridComponent instanceof ISortedComponentsCapability) {
            UIComponent components[] = ((ISortedChildrenCapability) gridComponent)
                    .getSortedChildren();

            if (components.length < 1) {
                return "";
            }

            UIColumn children[] = null;

            StringAppender sa = new StringAppender(components.length * 32);
            for (int i = 0; i < components.length; i++) {
                UIComponent component = components[i];
                if (sa.length() > 0) {
                    sa.append(',');
                }

                if (component instanceof IOrderCapability) {
                    if (((IOrderCapability) component).isAscending()) {
                        sa.append("asc::");
                    } else {
                        sa.append("desc::");
                    }
                }

                String id = component.getId();
                if (id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
                    if (children == null) {
                        children = gridComponent.listColumns().toArray();
                    }

                    int index = -1;
                    for (int j = 0; j < children.length; j++) {
                        if (children[j] != component) {
                            continue;
                        }

                        index = j;
                        break;
                    }

                    if (index < 0) {
                        LOG.error("Can not find column '" + component + "'.");
                        continue;
                    }

                    sa.append('#').append(index);
                    continue;
                }

                sa.append(id);
            }

            return sa.toString();
        }
        return null;
    }

    public static final IComponentValueType DATA_GRID_VALUE_TYPE = new IComponentValueType() {

        public Object createNewValue(UIComponent component) {
            DataGridComponent dataGridComponent = (DataGridComponent) component;

            if (dataGridComponent.isRowValueColumnIdSetted()) {
                return new OrderedSet<Object>();
            }

            return new ArrayIndexesModel();
        }
    };

    public static final IComponentValueType COMBO_GRID_VALUE_TYPE = DATA_GRID_VALUE_TYPE;

    public static final IComponentValueType COMPONENTS_GRID_VALUE_TYPE = new IComponentValueType() {

        public Object createNewValue(UIComponent component) {
            ComponentsGridComponent gridComponent = (ComponentsGridComponent) component;

            if (gridComponent.isRowValueSetted()) {
                return new OrderedSet<Object>();
            }

            return new ArrayIndexesModel();
        }
    };

}
