/*
 * $Id: GridServerSort.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ISortEventCapability;
import org.rcfaces.core.event.SortEvent;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.listener.SortActionListener;
import org.rcfaces.core.model.IRangeDataModel;
import org.rcfaces.core.model.ISortedComponent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public final class GridServerSort {

    private static final Log LOG = LogFactory.getLog(GridServerSort.class);

    private static final Long LONG_0 = new Long(0l);

    private static final Double DOUBLE_0 = new Double(0.0);

    private static final Map<String, ISortMethod> SORT_ALIASES = new HashMap<String, ISortMethod>(
            8);

    static {
        SORT_ALIASES.put(ISortEventCapability.SORT_INTEGER, new SortLong());
        SORT_ALIASES.put(ISortEventCapability.SORT_NUMBER, new SortDouble());
        SORT_ALIASES.put(ISortEventCapability.SORT_ALPHA, new SortAlpha());
        SORT_ALIASES.put(ISortEventCapability.SORT_ALPHA_IGNORE_CASE,
                new SortAlphaIgnoreCase());
        SORT_ALIASES.put(ISortEventCapability.SORT_TIME, new SortDate());
        SORT_ALIASES.put(ISortEventCapability.SORT_DATE, new SortDate());
    }

    public static int[] computeSortedTranslation(FacesContext facesContext,
            IGridComponent data, DataModel dataModel,
            ISortedComponent sortedComponents[]) {

        ISortMethod< ? > sortMethods[] = new ISortMethod[sortedComponents.length];

        for (int i = 0; i < sortMethods.length; i++) {
            UIColumn columnComponent = (UIColumn) sortedComponents[i]
                    .getComponent();

            if ((columnComponent instanceof ISortEventCapability) == false) {
                continue;
            }

            sortMethods[i] = getSortMethod(
                    (ISortEventCapability) columnComponent, data);
        }

        int rowCount = data.getRowCount();

        List<Object> datas[] = new List[sortedComponents.length];
        for (int i = 0; i < datas.length; i++) {
            if (rowCount > 0) {
                datas[i] = new ArrayList<Object>(rowCount);

            } else {
                datas[i] = new ArrayList<Object>();
            }
        }

        if (dataModel instanceof IRangeDataModel) {
            // Charge tout !
            ((IRangeDataModel) dataModel).setRowRange(0, rowCount);
        }

        try {
            for (int rowIndex = 0;; rowIndex++) {
                data.setRowIndex(rowIndex);

                if (data.isRowAvailable() == false) {
                    break;
                }

                Object rowData = null;
                boolean rowDataInitialized = false;

                for (int i = 0; i < datas.length; i++) {
                    UIComponent column = sortedComponents[i].getComponent();

                    Object value = null;

                    if (column instanceof ValueHolder) {
                        value = ValuesTools.getValue(column);
                    }

                    ISortMethod< ? > sortMethod = sortMethods[i];
                    if (sortMethod == null) {
                        throw new FacesException(
                                "Can not get sort method for column #" + i
                                        + " id=" + column.getId());
                    }
                    value = sortMethod
                            .convertValue(facesContext, column, value);

                    if (value == null) {
                        if (rowDataInitialized == false) {
                            rowDataInitialized = true;

                            rowData = data.getRowData();
                        }
                        // Avoid crahes when compare
                        // then WHY get the full row Data when the column value
                        // is null ?
                        if (rowData instanceof Comparable) {
                            value = rowData;
                        }
                    }

                    datas[i].add(value);
                }
            }
        } finally {
            data.setRowIndex(-1);
        }

        int translations[] = new int[datas[0].size()];
        for (int i = 0; i < translations.length; i++) {
            translations[i] = i;
        }
        if (translations.length < 2) {
            return translations;
        }

        Object ds[][] = new Object[datas.length][];
        Comparator<Object> comparators[] = new Comparator[datas.length];
        boolean sortOrders[] = new boolean[datas.length];
        for (int i = 0; i < ds.length; i++) {
            ds[i] = datas[i].toArray();

            ISortMethod sortMethod = sortMethods[i];
            if (sortMethod == null) {
                throw new FacesException("No sort method #" + i + " for grid '"
                        + ((UIComponent) data).getId() + "' of view '"
                        + facesContext.getViewRoot().getViewId() + "'");
            }

            comparators[i] = sortMethod.getComparator();
            sortOrders[i] = sortedComponents[i].isAscending();
        }

        for (int i = 0; i < translations.length; i++) {

            next_element: for (int j = i; j > 0; j--) {
                int j0 = translations[j - 1];
                int j1 = translations[j];

                for (int k = 0; k < sortMethods.length; k++) {
                    Object o1 = ds[k][j0];
                    Object o2 = ds[k][j1];

                    if (comparators[k] == null) {
                        continue;
                    }

                    int order = comparators[k].compare(o1, o2);
                    if (order == 0) {
                        continue;
                    }

                    if (sortOrders[k]) {
                        if (order < 0) {
                            break next_element;
                        }
                    } else if (order > 0) {
                        break next_element;
                    }

                    translations[j] = j0;
                    translations[j - 1] = j1;
                    continue next_element;
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            Set set2 = new HashSet(translations.length);
            LOG.debug("Valid SORT translation ...");
            for (int i = 0; i < translations.length; i++) {
                if (set2.add(new Integer(translations[i])) == false) {

                    LOG.debug("*** INVALID TRANSLATION ***");
                    continue;
                }
            }
        }

        return translations;
    }

    private static ISortMethod< ? > getSortMethod(
            ISortEventCapability columnComponent, IGridComponent gridComponent) {

        FacesListener facesListeners[] = columnComponent.listSortListeners();

        for (int j = 0; j < facesListeners.length; j++) {
            FacesListener facesListener = facesListeners[j];

            // Priorité coté JAVASCRIPT, on verra le serveur dans un
            // deuxieme temps ...
            if (facesListener instanceof SortActionListener) {
                return new SortAction((SortActionListener) facesListener,
                        (UIComponent) columnComponent, gridComponent);
            }

            if ((facesListener instanceof IScriptListener) == false) {
                continue;
            }

            IScriptListener scriptListener = (IScriptListener) facesListener;

            ISortMethod< ? > sortMethod = (ISortMethod< ? >) SORT_ALIASES
                    .get(scriptListener.getCommand());
            if (sortMethod == null) {
                continue;
            }

            return sortMethod;
        }

        return null;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private interface ISortMethod<T> {

        Comparator<T> getComparator();

        Object convertValue(FacesContext facesContext, UIComponent component,
                Object value);

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static abstract class AbstractSortMethod<T> implements
            ISortMethod<T>, Comparator<T> {

        public Comparator<T> getComparator() {
            return this;
        }

        public int compare(T o1, T o2) {
            if (o1 == null) {
                return (o2 == null) ? 0 : -1;

            } else if (o2 == null) {
                return 1;
            }

            return ((Comparable<T>) o1).compareTo(o2);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortLong extends AbstractSortMethod {

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (value == null) {
                return LONG_0;
            }

            if (value instanceof Number) {
                return value;
            }

            if (value instanceof String) {
                String s = (String) value;
                if (s.length() < 1) {
                    return LONG_0;
                }

                long l = Long.parseLong(s);
                if (l == 0l) {
                    return LONG_0;
                }

                return new Long(l);
            }

            return LONG_0;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortDouble extends AbstractSortMethod {

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (value == null) {
                return DOUBLE_0;
            }

            if (value instanceof Number) {
                return value;
            }

            if (value instanceof String) {
                String s = (String) value;
                if (s.length() < 1) {
                    return DOUBLE_0;
                }

                double d = Double.parseDouble(s);
                if (d == 0.0) {
                    return DOUBLE_0;
                }

                return new Double(d);
            }

            return DOUBLE_0;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortAlpha extends AbstractSortMethod {

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (value == null) {
                return "";
            }

            if (value instanceof String) {
                return value;
            }

            value = ValuesTools.valueToString(value, component, facesContext);

            if (value == null) {
                return "";
            }

            return value;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortAlphaIgnoreCase extends AbstractSortMethod {

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (value == null) {
                return "";
            }

            if (value instanceof String) {
                return ((String) value).toLowerCase();
            }

            value = ValuesTools.valueToString(value, component, facesContext);
            if (value == null) {
                return "";
            }

            return ((String) value).toLowerCase();
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortDate extends AbstractSortMethod {

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (value == null) {
                return null;
            }

            if (value instanceof Date) {
                return value;
            }

            throw new FacesException(
                    "Invalid Date for \"date\" sort method ! (class="
                            + value.getClass() + " object=" + value + ")");
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class SortAction extends AbstractSortMethod {

        private final Comparator comparator;

        private final SortEvent.ISortConverter converter;

        public SortAction(SortActionListener listener,
                UIComponent dataColumnComponent, IGridComponent dataModel) {
            SortEvent sortEvent = new SortEvent(dataColumnComponent, dataModel);

            listener.processSort(sortEvent);

            comparator = sortEvent.getSortComparator();
            if (comparator == null) {
                throw new FacesException("Comparator of sortEvent is NULL !");
            }

            converter = sortEvent.getSortConverter();
        }

        public Object convertValue(FacesContext facesContext,
                UIComponent component, Object value) {
            if (converter == null) {
                return value;
            }

            return converter.convertValue(facesContext, component, value);
        }

        public Comparator getComparator() {
            return comparator;
        }
    }
}
