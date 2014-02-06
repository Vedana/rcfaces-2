/*
 * $Id: CollectionTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.util.StringList;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.OrderedSet;
import org.rcfaces.core.model.ICommitableObject;
import org.rcfaces.core.model.IIndexesModel;
import org.rcfaces.core.model.IRangeDataModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class CollectionTools {

    private static final Log LOG = LogFactory.getLog(CollectionTools.class);

    protected static final Object[] EMPTY_VALUES = new Object[0];

    private static final Object[] EMPTY_STRING_ARRAY = new String[0];

    private static final boolean SORT_INDICES = true;

    private static final Map<Class< ? >, Class< ? >> IMPLEMENTATION_TYPES = new HashMap<Class< ? >, Class< ? >>(
            64);
    static {
        IMPLEMENTATION_TYPES.put(Collection.class, ArrayList.class);
        IMPLEMENTATION_TYPES.put(List.class, ArrayList.class);
        IMPLEMENTATION_TYPES.put(Set.class, HashSet.class);
        IMPLEMENTATION_TYPES.put(Map.class, HashMap.class);

        IMPLEMENTATION_TYPES.put(IIndexesModel.class, ArrayIndexesModel.class);
    }

    private static final IValuesAccessor NULL_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object value) {
            return 0;
        }

        public Object getFirst(Object value, Object refValues) {
            return null;
        }

        public Object[] listValues(Object value, Object refValues) {
            return EMPTY_VALUES;
        }
    };

    private static final IValuesAccessor ARRAY_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object array) {
            return Array.getLength(array);
        }

        public Object getFirst(Object array, Object refValues) {
            if (getCount(array) < 1) {
                return null;
            }

            return Array.get(array, 0);
        }

        public Object[] listValues(Object array, Object refValues) {
            return (Object[]) array;
        }
    };

    private static final IValuesAccessor STRING_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object array) {
            return StringList.countTokens((String) array);
        }

        public Object getFirst(Object array, Object refValues) {
            return StringList.getFirstToken((String) array);
        }

        public Object[] listValues(Object array, Object refValues) {
            return convertToObjectArray(array);
        }

    };

    private static final IValuesAccessor COLLECTION_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object collection) {
            return ((Collection) collection).size();
        }

        public Object getFirst(Object collection, Object refValues) {
            Collection cl = (Collection) collection;

            if (cl.isEmpty()) {
                return null;
            }

            if (cl instanceof List) {
                return ((List) cl).get(0);
            }

            return cl.iterator().next();
        }

        public Object[] listValues(Object collection, Object refValues) {
            return ((Collection) collection).toArray();
        }
    };

    private static final IValuesAccessor MAP_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object map) {
            return ((Map) map).size();
        }

        public Object getFirst(Object map, Object refValues) {
            return ((Map) map).keySet().iterator().next();
        }

        public Object[] listValues(Object map, Object refValues) {
            return ((Map) map).keySet().toArray();
        }
    };

    private static final IValuesAccessor INDEXES_MODEL_VALUES_ACCESSOR = new AbstractValuesAccessor() {

        public int getCount(Object indexesModel) {
            return ((IIndexesModel) indexesModel).countIndexes();
        }

        public Object getFirst(Object indexesModel, Object refValues) {
            return ((IIndexesModel) indexesModel)
                    .getFirstSelectedObject(refValues);
        }

        public Object[] listValues(Object indexesModel, Object refValues) {
            return ((IIndexesModel) indexesModel).listSelectedObjects(null,
                    refValues);
        }
    };

    protected static IValuesAccessor getValuesAccessor(Object values,
            Class< ? > providerClass, IValuesAccessor providerValuesAccessor,
            boolean useValue, boolean indexSupported) {

        if (values == null) {
            if (useValue == false) {
                return null;
            }
            return NULL_VALUES_ACCESSOR;
        }

        if (values.getClass().isArray()) {
            if (useValue == false) {
                return null;
            }
            return ARRAY_VALUES_ACCESSOR;
        }

        if (values instanceof String) {
            if (useValue == false) {
                return null;
            }
            return STRING_VALUES_ACCESSOR;
        }

        if (values instanceof Collection) {
            if (useValue == false) {
                return null;
            }
            return COLLECTION_VALUES_ACCESSOR;
        }

        if (values instanceof Map) {
            if (useValue == false) {
                return null;
            }
            return MAP_VALUES_ACCESSOR;
        }

        if (indexSupported && (values instanceof IIndexesModel)) {
            if (useValue == false) {
                return null;
            }
            return INDEXES_MODEL_VALUES_ACCESSOR;
        }

        if (providerClass == null) {
            return null;
        }

        if (values.getClass().isAssignableFrom(providerClass)) {
            return providerValuesAccessor;
        }

        if (values instanceof IAdaptable) {
            Object provider = ((IAdaptable) values).getAdapter(providerClass,
                    null);

            if (provider != null) {
                return new ProviderValuesAccessor(providerValuesAccessor,
                        provider);
            }
        }

        Object provider = RcfacesContext.getCurrentInstance()
                .getAdapterManager().getAdapter(values, providerClass, null);

        if (provider != null) {
            return new ProviderValuesAccessor(providerValuesAccessor, provider);
        }

        return null;
    }

    public static Object getEmptyValues() {
        return EMPTY_VALUES;
    }

    protected static Object[] convertToObjectArray(Object values) {
        if (values == null) {
            return null;
        }

        if (values instanceof String) {
            Set set = valuesToSet(values, true);
            if (set.isEmpty()) {
                return EMPTY_STRING_ARRAY;
            }

            return set.toArray(new String[set.size()]);
        }

        if (values instanceof Object[]) {
            return (Object[]) values;
        }

        if (values instanceof Collection) {
            return ((Collection) values).toArray();
        }

        if (values instanceof Map) {
            return ((Map) values).keySet().toArray();
        }

        throw new FacesException("Can not convert object '" + values
                + "' to object array.");
    }

    private static Object createNewValues(UIComponent component,
            IValuesAccessor valuesAccessor) {
        Class type = valuesAccessor.getComponentValuesType(null, component);
        if (type == null) {

            if ((component instanceof IComponentValueTypeCapability) == false) {
                throw new FacesException(
                        "Can not identify IComponentValueType for component id='"
                                + component.getId() + " renderType='"
                                + component.getRendererType() + "'");
            }

            IComponentValueType componentValueType = ((IComponentValueTypeCapability) component)
                    .getComponentValueType();

            Object values = componentValueType.createNewValue(component);

            valuesAccessor.setComponentValues(component, values);

            return values;
        }

        if (type.isArray()) {
            Object values = Array.newInstance(type.getComponentType(), 0);

            valuesAccessor.setComponentValues(component, values);

            return values;
        }

        Class implementationType = (Class) IMPLEMENTATION_TYPES.get(type);
        if (implementationType != null) {
            type = implementationType;
        }

        Object values;
        try {
            values = type.newInstance();

        } catch (Throwable th) {
            throw new FacesException("Can not instanciate values for type '"
                    + type + "'", th);
        }

        valuesAccessor.setComponentValues(component, values);

        return values;
    }

    public static void select(UIComponent component,
            IValuesAccessor valuesAccessor, int indices[]) {
        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            values = createNewValues(component, valuesAccessor);
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            for (int i = 0; i < indices.length; i++) {
                indexesModel.addIndex(indices[i]);
            }

            return;
        }

        if (component instanceof IGridComponent) {
            List<Object> rowDatas = getRowDatas((IGridComponent) component,
                    indices);
            if (rowDatas.isEmpty()) {
                return;
            }

            select(component, valuesAccessor, values, rowDatas);
            return;
        }
    }

    public static void select(UIComponent component,
            IValuesAccessor valuesAccessor, int start, int end) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            values = createNewValues(component, valuesAccessor);
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            for (; start < end; start++) {
                indexesModel.addIndex(start);
            }

            return;
        }

        if (component instanceof IGridComponent) {
            List<Object> rowDatas = getRowDatas((IGridComponent) component,
                    start, end);
            if (rowDatas.isEmpty()) {
                return;
            }

            select(component, valuesAccessor, values, rowDatas);
            return;
        }
    }

    public static void select(UIComponent component,
            IValuesAccessor valuesAccessor, int index) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            values = createNewValues(component, valuesAccessor);
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            indexesModel.addIndex(index);
            return;
        }

        if (component instanceof IGridComponent) {
            Object rowData = getRowData((IGridComponent) component, index);
            if (rowData == null) {
                LOG.error("No rowData for index='" + index + "'.");
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Select index=" + index + " => " + rowData
                        + "   selectedValues=" + values);
            }

            select(component, valuesAccessor, values,
                    Collections.singleton(rowData));

            return;
        }
    }

    public static void selectAll(UIComponent component,
            IValuesAccessor valuesAccessor, IAllValuesProvider allValuesProvider) {

        if ((component instanceof IGridComponent) == false) {
            throw new UnsupportedOperationException(
                    "Can not list all values of component '"
                            + component.getId() + "'.");
        }

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            values = createNewValues(component, valuesAccessor);
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            int rowCount = getRowCount(component);
            for (int i = 0; i < rowCount; i++) {
                indexesModel.addIndex(i);
            }

            return;
        }

        if (component instanceof IGridComponent) {
            List rowDatas = getRowDatas((IGridComponent) component);
            if (rowDatas.isEmpty()) {
                return;
            }

            select(component, valuesAccessor, values, rowDatas);
            return;
        }

        if (allValuesProvider != null) {
            List<Object> rowDatas = allValuesProvider.listAllValues(component);
            if (rowDatas.isEmpty()) {
                return;
            }

            select(component, valuesAccessor, values, rowDatas);
            return;
        }
    }

    public static void select(UIComponent component,
            IValuesAccessor valuesAccessor, Object rowValue) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            values = createNewValues(component, valuesAccessor);
        }

        if (values instanceof IIndexesModel) {
            int index = searchIndexIntoDataModel((IGridComponent) component,
                    rowValue);
            if (index < 0) {
                return;
            }

            select(component, valuesAccessor, index);

            return;
        }

        select(component, valuesAccessor, values,
                Collections.singletonList(rowValue));
    }

    private static <T> T select(UIComponent component,
            IValuesAccessor valuesAccessor, T values,
            Collection<Object> rowDatas) {

        if (values.getClass().isArray()) {

            int length = Array.getLength(values);

            List<Object> l = null;

            next_data: for (Iterator<Object> it = rowDatas.iterator(); it
                    .hasNext();) {
                Object rowData = it.next();

                for (int i = 0; i < length; i++) {
                    if (Array.get(values, i).equals(rowData) == false) {
                        continue;
                    }

                    continue next_data;
                }

                if (l == null) {
                    l = new ArrayList<Object>();

                } else if (l.contains(rowData)) {
                    continue;
                }

                l.add(rowData);
            }

            if (l == null) {
                return values;
            }

            Class< ? > type = values.getClass().getComponentType();

            T newValues = (T) Array.newInstance(type, length + l.size());

            System.arraycopy(values, 0, newValues, 0, length);

            for (Iterator<Object> it = l.iterator(); it.hasNext(); length++) {
                Array.set(newValues, length, it.next());
            }

            valuesAccessor.setComponentValues(component, newValues);

            return newValues;
        }

        if (values instanceof String) {
            Set<String> set = CollectionTools.<String> valuesToSet(values,
                    false);

            set.addAll((Collection) rowDatas);

            String newValues = StringList.joinTokens(set);

            if (newValues.equals(values)) {
                return values;
            }

            valuesAccessor.setComponentValues(component, newValues);

            return (T) newValues;
        }

        if (values instanceof Collection) {
            Collection<Object> collection = CollectionTools
                    .<Object> cloneCollection(component, valuesAccessor,
                            (Collection<Object>) values);

            if (rowDatas instanceof Set) {
                collection.addAll((Set) rowDatas);
                return (T) collection;
            }

            for (Object rowData : rowDatas) {

                if (collection.contains(rowData)) {
                    continue;
                }

                collection.add(rowData);
            }

            return (T) collection;
        }

        throw new FacesException("Select index is not implemented for values="
                + values);
    }

    private static <T> Collection<T> cloneCollection(UIComponent component,
            IValuesAccessor valuesAccessor, Collection<T> collection) {

        boolean copy = true;

        if (collection instanceof ICommitableObject) {
            copy = ((ICommitableObject) collection).isCommited();
        }

        if (copy == false) {
            return collection;
        }

        try {
            Method method = collection.getClass().getMethod("clone",
                    (Class[]) null);

            collection = (Collection<T>) method.invoke(collection,
                    (Object[]) null);

        } catch (Throwable th) {
            LOG.info("Can not copy the collection ! ("
                    + collection.getClass().getName() + ")", th);
        }

        valuesAccessor.setComponentValues(component, collection);

        return collection;
    }

    private static int getRowCount(UIComponent component) {

        IGridComponent gridComponent = (IGridComponent) component;

        int index = gridComponent.getRowCount();

        if (index >= 0) {
            return index;
        }

        try {
            for (index = 0;; index++) {
                gridComponent.setRowIndex(index);

                if (gridComponent.isRowAvailable()) {
                    return index;
                }
            }
        } finally {
            gridComponent.setRowIndex(-1);
        }
    }

    public static void deselectAll(UIComponent component,
            IValuesAccessor valuesAccessor) {

        Object values = valuesAccessor.getComponentValues(component);
        if (values == null) {
            return;
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            indexesModel.clearIndexes();
            return;
        }

        if (values instanceof Collection) {
            Collection collection = cloneCollection(component, valuesAccessor,
                    (Collection) values);

            collection.clear();
            return;
        }

        if (values instanceof String) {
            if ("".equals(values)) {
                return;
            }

            valuesAccessor.setComponentValues(component, "");

            return;
        }

        if (values instanceof Object[]) {
            if (Array.getLength(values) == 0) {
                return;
            }

            Class type = values.getClass().getComponentType();

            values = Array.newInstance(type, 0);

            valuesAccessor.setComponentValues(component, values);
            return;
        }

        throw new FacesException("Deselect all is not implemented for values="
                + values);
    }

    public static void deselect(UIComponent component,
            IValuesAccessor valuesAccessor, Object rowValue) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            return;
        }

        if (values instanceof IIndexesModel) {
            int index = searchIndexIntoDataModel((IGridComponent) component,
                    rowValue);

            if (index < 0) {
                return;
            }

            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            indexesModel.removeIndex(index);

            return;
        }

        deselect(component, valuesAccessor, values,
                Collections.singletonList(rowValue));
    }

    public static void deselect(UIComponent component,
            IValuesAccessor valuesAccessor, int index) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            return;
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            indexesModel.removeIndex(index);
            return;
        }

        if (component instanceof IGridComponent) {
            Object rowData = getRowData((IGridComponent) component, index);

            if (rowData == null) {
                LOG.error("No rowData for index='" + index + "'.");
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Deselect index=" + index + " => " + rowData
                        + "   selectedValues=" + values);
            }

            deselect(component, valuesAccessor, values,
                    Collections.singleton(rowData));
            return;
        }
    }

    public static void deselect(UIComponent component,
            IValuesAccessor valuesAccessor, int indices[]) {

        if (indices == null || indices.length < 1) {
            return;
        }

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            return;
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            for (int i = 0; i < indices.length; i++) {
                indexesModel.removeIndex(indices[i]);
            }

            return;
        }

        if (component instanceof IGridComponent) {
            List< ? > rowDatas = getRowDatas((IGridComponent) component,
                    indices);
            if (rowDatas.isEmpty()) {
                return;
            }

            deselect(component, valuesAccessor, values, rowDatas);
            return;
        }
    }

    public static void deselect(UIComponent component,
            IValuesAccessor valuesAccessor, int start, int end) {

        Object values = valuesAccessor.getComponentValues(component);

        if (values == null) {
            return;
        }

        if (values instanceof IIndexesModel) {
            IIndexesModel indexesModel = cloneIndexModel(component,
                    valuesAccessor, (IIndexesModel) values);

            for (; start < end; start++) {
                indexesModel.removeIndex(start);
            }

            return;
        }

        if (component instanceof IGridComponent) {
            List< ? > rowDatas = getRowDatas((IGridComponent) component, start,
                    end);
            if (rowDatas.isEmpty()) {
                return;
            }

            deselect(component, valuesAccessor, values, rowDatas);
            return;
        }
    }

    private static Object deselect(UIComponent component,
            IValuesAccessor valuesAccessor, Object values,
            Collection< ? > rowDatas) {

        if (values.getClass().isArray()) {

            int length = Array.getLength(values);
            if (length == 0) {
                return values;
            }

            for (Object rowData : rowDatas) {
                for (int i = 0; i < length;) {
                    if (Array.get(values, i).equals(rowData) == false) {
                        i++;
                        continue;
                    }

                    length--;
                    if (i >= length) {
                        break;
                    }

                    System.arraycopy(values, i + 1, values, i, length - i);
                }
            }

            if (Array.getLength(values) == length) {
                // On a pas touché au tableau !
                return values;
            }

            Class< ? > type = values.getClass().getComponentType();

            Object newValues = Array.newInstance(type, length);
            if (length > 0) {
                System.arraycopy(values, 0, newValues, 0, length);
            }

            valuesAccessor.setComponentValues(component, newValues);

            return newValues;
        }

        if (values instanceof String) {
            Set<String> set = CollectionTools.<String> valuesToSet(values,
                    false);

            set.removeAll(rowDatas);

            String newValues = StringList.joinTokens(set);

            if (newValues.equals(values)) {
                return values;
            }

            valuesAccessor.setComponentValues(component, newValues);

            return newValues;
        }

        if (values instanceof Collection) {
            Collection< ? > collection = cloneCollection(component,
                    valuesAccessor, (Collection< ? >) values);

            if (collection instanceof Set) {
                collection.removeAll(rowDatas);
                return collection;
            }

            for (Object rowData : rowDatas) {
                collection.remove(rowData);
            }

            return collection;
        }

        throw new FacesException(
                "Deselect index is not implemented for selectedValues="
                        + values);
    }

    private static IIndexesModel cloneIndexModel(UIComponent component,
            IValuesAccessor valuesAccessor, IIndexesModel indexesModel) {

        boolean copy = true;

        if (indexesModel instanceof ICommitableObject) {
            copy = ((ICommitableObject) indexesModel).isCommited();
        }

        if (copy == false) {
            return indexesModel;
        }

        indexesModel = indexesModel.copy();
        valuesAccessor.setComponentValues(component, indexesModel);

        return indexesModel;
    }

    private static <T> List<T> getRowDatas(IGridComponent gridComponent,
            int[] indices) {

        int rowCount = gridComponent.getRowCount();
        if (rowCount > 0) {
            DataModel dataModel = gridComponent.getDataModelValue();

            if (dataModel instanceof IRangeDataModel) {
                ((IRangeDataModel) dataModel).setRowRange(0, rowCount);
            }
        }

        if (SORT_INDICES) {
            indices = (int[]) indices.clone();
            Arrays.sort(indices);
        }

        List<T> rowDatas = null;
        try {
            for (int i = 0; i < indices.length; i++) {
                int index = indices[i];
                gridComponent.setRowIndex(index);
                if (gridComponent.isRowAvailable() == false) {
                    LOG.error("Row not available for index='" + index + "'.");
                    continue;
                }

                T rowData = (T) gridComponent.getRowData();

                if (rowData == null) {
                    LOG.error("No rowData for index='" + index + "'.");
                    continue;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Get row at index=" + index + " => " + rowData);
                }

                if (rowDatas == null) {
                    rowDatas = new ArrayList<T>(indices.length - i);
                }
                rowDatas.add(rowData);
            }

        } finally {
            gridComponent.setRowIndex(-1);
        }

        if (rowDatas == null) {
            return Collections.emptyList();
        }

        return rowDatas;
    }

    private static Object getRowData(IGridComponent gridComponent, int index) {

        DataModel dataModel = gridComponent.getDataModelValue();

        if (dataModel instanceof IRangeDataModel) {
            ((IRangeDataModel) dataModel).setRowRange(index, 1);
        }

        Object rowData = null;
        gridComponent.setRowIndex(index);
        try {
            if (gridComponent.isRowAvailable() == false) {
                LOG.error("Row not available for index='" + index + "'.");
                return null;
            }

            rowData = gridComponent.getRowData();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get row at index=" + index + " => " + rowData);
            }

        } finally {
            gridComponent.setRowIndex(-1);
        }

        if (rowData == null) {
            LOG.error("RowData is null for index='" + index + "'.");
        }

        return rowData;
    }

    private static List getRowDatas(IGridComponent gridComponent) {

        int rowCount = gridComponent.getRowCount();
        if (rowCount > 0) {
            DataModel dataModel = gridComponent.getDataModelValue();

            if (dataModel instanceof IRangeDataModel) {
                ((IRangeDataModel) dataModel).setRowRange(0, rowCount);
            }
        }

        List rowDatas = null;
        try {
            for (int index = 0;; index++) {
                gridComponent.setRowIndex(index);

                if (gridComponent.isRowAvailable() == false) {
                    LOG.debug("Row not available for index='" + index + "'.");
                    break;
                }

                Object rowData = gridComponent.getRowData();

                if (rowData == null) {
                    LOG.debug("RowData is null for index='" + index + "'.");
                    continue;
                }

                if (rowDatas == null) {
                    int size = rowCount - index;

                    if (size < 8) {
                        size = 8;
                    }

                    rowDatas = new ArrayList(size);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Get row at index=" + index + " => " + rowData);
                }

                rowDatas.add(rowData);
            }

        } finally {
            gridComponent.setRowIndex(-1);
        }

        if (rowDatas == null) {
            return Collections.EMPTY_LIST;
        }

        return rowDatas;
    }

    private static <T> List<T> getRowDatas(IGridComponent gridComponent,
            int start, int end) {

        DataModel dataModel = gridComponent.getDataModelValue();

        if (dataModel instanceof IRangeDataModel) {
            ((IRangeDataModel) dataModel).setRowRange(start, end - start + 1);
        }

        List<T> rowDatas = null;
        try {
            for (int index = start; index <= end; index++) {
                gridComponent.setRowIndex(index);

                if (gridComponent.isRowAvailable() == false) {
                    LOG.error("Row not available for index='" + index + "'.");
                    break;
                }

                T rowData = (T) gridComponent.getRowData();

                if (rowData == null) {
                    LOG.error("RowData is null for index='" + index + "'.");
                    break;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Get index=" + index + " => " + rowData);
                }

                if (rowDatas == null) {
                    rowDatas = new ArrayList<T>(end - index + 1);
                }

                rowDatas.add(rowData);
            }

        } finally {
            gridComponent.setRowIndex(-1);
        }

        if (rowDatas == null) {
            return Collections.emptyList();
        }

        return rowDatas;
    }

    private static int searchIndexIntoDataModel(IGridComponent component,
            Object rowValue) {
        int rowCount = component.getRowCount();
        if (rowCount > 0) {
            DataModel dataModel = component.getDataModelValue();

            if (dataModel instanceof IRangeDataModel) {
                ((IRangeDataModel) dataModel).setRowRange(0, rowCount);
            }
        }

        try {
            for (int index = 0;; index++) {
                component.setRowIndex(index);
                if (component.isRowAvailable() == false) {
                    break;
                }

                Object rowData = component.getRowData();

                if (rowData == null) {
                    LOG.error("RowData is null for index='" + index + "'.");
                    break;
                }

                if (rowData.equals(rowValue) == false) {
                    continue;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find row " + rowData + " => index=" + index);
                }

                return index;
            }

        } finally {
            component.setRowIndex(-1);
        }

        return -1;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class ProviderValuesAccessor implements IValuesAccessor {

        private final IValuesAccessor providerValuesAccessor;

        private final Object provider;

        public ProviderValuesAccessor(IValuesAccessor providerValuesAccessor,
                Object provider) {
            this.providerValuesAccessor = providerValuesAccessor;
            this.provider = provider;
        }

        public int getCount(Object value) {
            return providerValuesAccessor.getCount(provider);
        }

        public Object getFirst(Object value, Object refValues) {
            return providerValuesAccessor.getFirst(provider, null);
        }

        public Object[] listValues(Object value, Object refValues) {
            return providerValuesAccessor.listValues(provider, null);
        }

        public Object getAdaptedValues(Object value) {
            return provider;
        }

        public Object getComponentValues(UIComponent component) {
            return providerValuesAccessor.getComponentValues(component);
        }

        public void setComponentValues(UIComponent component, Object values) {
            providerValuesAccessor.setComponentValues(component, values);
        }

        public Class getComponentValuesType(FacesContext facesContext,
                UIComponent component) {
            return providerValuesAccessor.getComponentValuesType(null,
                    component);
        }

        public void setAdaptedValues(Object value, Object values) {
            providerValuesAccessor.setAdaptedValues(value, values);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    protected interface IValuesAccessor {
        Object getFirst(Object value, Object refValues);

        int getCount(Object value);

        Object[] listValues(Object value, Object refValues);

        Object getAdaptedValues(Object values);

        void setAdaptedValues(Object value, Object values);

        Object getComponentValues(UIComponent component);

        void setComponentValues(UIComponent component, Object values);

        Class getComponentValuesType(FacesContext facesContext,
                UIComponent component);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static abstract class AbstractValuesAccessor implements
            IValuesAccessor {

        public Object getComponentValues(UIComponent component) {
            throw new IllegalStateException("Not implemented !");
        }

        public void setComponentValues(UIComponent component, Object values) {
            throw new IllegalStateException("Not implemented !");
        }

        public Class getComponentValuesType(FacesContext facesContext,
                UIComponent component) {
            throw new IllegalStateException("Not implemented !");
        }

        public Object getAdaptedValues(Object value) {
            return value;
        }

        public void setAdaptedValues(Object value, Object values) {
            throw new IllegalStateException("Not supported");
        }
    }

    public static Object adaptValues(Class target, Collection collection) {
        if (target == null || target.equals(Object.class)
                || target.equals(Object[].class)) {
            return collection.toArray();
        }

        if (target.isArray()) {
            Object array = Array.newInstance(target.getComponentType(),
                    collection.size());

            if (array instanceof Object[]) {
                return collection.toArray((Object[]) array);
            }

            Object src[] = collection.toArray();

            System.arraycopy(src, 0, array, 0, collection.size());

            return array;
        }

        if (Set.class.isAssignableFrom(target)) {
            if (collection instanceof Set) {
                return collection;
            }
            return new HashSet(collection);
        }

        if (List.class.isAssignableFrom(target)) {
            if (collection instanceof List) {
                return collection;
            }
            return new ArrayList(collection);
        }

        if (Collection.class.isAssignableFrom(target)) {
            return collection;
        }

        throw new FacesException("Invalid collection type '" + target + "'.");
    }

    public static Set __convertSelection(Object selection) {
        if (selection instanceof Object[]) {
            return new OrderedSet(Arrays.asList((Object[]) selection));
        }

        if (selection instanceof Collection) {
            return new OrderedSet((Collection) selection);
        }

        if (selection == null) {
            return new OrderedSet();
        }

        throw new FacesException(
                "Bad type of value for attribute selectedValues/checkedValues !");
    }

    protected static void setValues(UIComponent component,
            IValuesAccessor valuesAccessor, Collection<Object> values) {

        Object newValues = createNewValues(component, valuesAccessor);

        select(component, valuesAccessor, newValues, values);
    }

    protected static Set<Object> valuesToSet(UIComponent component,
            IValuesAccessor valuesAccessor, boolean immutable) {
        Object value = valuesAccessor.getComponentValues(component);

        return valuesToSet(value, immutable);
    }

    public static <T> Set<T> valuesToSet(Object value, boolean immutable) {
        if (value == null) {
            if (immutable == false) {
                return new OrderedSet<T>();
            }
            return Collections.emptySet();
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);

            if (length < 1) {
                if (immutable == false) {
                    return new OrderedSet<T>();
                }
                return Collections.emptySet();
            }

            Set<T> set = new OrderedSet<T>();

            for (int i = 0; i < length; i++) {
                set.add((T) Array.get(value, i));
            }

            if (LOG.isDebugEnabled() && immutable) {
                return Collections.unmodifiableSet(set);
            }

            return set;
        }

        if (value instanceof String) {
            String ss[] = StringList.parseTokensList((String) value);

            if (ss.length == 0) {
                if (immutable == false) {
                    return new OrderedSet<T>();
                }
                return Collections.emptySet();
            }

            Set<T> set = new OrderedSet<T>(Arrays.<T> asList((T[]) ss));

            if (LOG.isDebugEnabled() && immutable) {
                return Collections.unmodifiableSet(set);
            }

            return set;
        }

        if (value instanceof Set) {
            if (immutable == false) {
                return new OrderedSet<T>((Set<T>) value);
            }

            if (LOG.isDebugEnabled() && immutable) {
                return Collections.unmodifiableSet((Set<T>) value);
            }

            return (Set<T>) value;
        }

        if (value instanceof Collection) {
            Collection<T> col = (Collection<T>) value;
            if (col.isEmpty() && immutable) {
                return Collections.emptySet();
            }

            Set<T> set = new OrderedSet<T>(col);

            if (LOG.isDebugEnabled() && immutable) {
                return Collections.unmodifiableSet(set);
            }

            return set;
        }

        if (immutable == false) {
            Set<T> set = new OrderedSet<T>();
            set.add((T) value);

            return set;
        }

        return Collections.singleton((T) value);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public interface IComponentValueType {
        Object createNewValue(UIComponent component);
    }

    /**
     * INTERNAL Stuff
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public interface IComponentValueTypeCapability {
        IComponentValueType getComponentValueType();
    }

    public interface IAllValuesProvider {
        List<Object> listAllValues(UIComponent component);
    }
}
