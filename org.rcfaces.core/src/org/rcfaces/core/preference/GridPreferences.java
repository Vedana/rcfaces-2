/*
 * $Id: GridPreferences.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.preference;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.capability.IOrderedChildrenCapability;
import org.rcfaces.core.component.capability.ISortedChildrenCapability;
import org.rcfaces.core.component.capability.IWidthRangeCapability;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.tools.GridTools;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public class GridPreferences extends AbstractComponentPreferences {

    private static final long serialVersionUID = -1760014871350310345L;

    private static final int SAVE_COLUMNS_ORDER = 0x0001;

    private static final int SAVE_SORTED_COLUMN_IDS = 0x0002;

    private static final int SAVE_COLUMN_SIZES = 0x0004;

    private static final int SAVE_FILTER_PROPERTIES = 0x0008;

    private static final int SAVE_POSITION = 0x0010;

    private int saveMask = SAVE_COLUMNS_ORDER | SAVE_COLUMN_SIZES
            | SAVE_SORTED_COLUMN_IDS | SAVE_COLUMN_SIZES;

    private String columnsOrder;

    private String sortedColumnIds;

    private int position = -1;

    private Map<String, String> columnSizes;

    private IFilterProperties filterProperties;

    // Constructeur public pour le StateHolder !
    public GridPreferences() {
    }

    public void loadPreferences(FacesContext facesContext, UIComponent component) {
        if ((component instanceof IGridComponent) == false) {
            throw new FacesException("Can not load dataGrid preferences !");
        }

        IGridComponent gridComponent = (IGridComponent) component;

        if (gridComponent instanceof IOrderedChildrenCapability) {
            if (columnsOrder != null) {
                GridTools.setOrderIds(gridComponent, columnsOrder);
            }
        }

        if (gridComponent instanceof ISortedChildrenCapability) {
            if (sortedColumnIds != null) {
                GridTools.setSortIds(gridComponent, sortedColumnIds);
            }
        }

        if (columnSizes != null) {
            IColumnIterator dataColumnIterator = gridComponent.listColumns();

            if (dataColumnIterator.count() > 0) {
                Map<String, IWidthRangeCapability> cols = new HashMap<String, IWidthRangeCapability>(
                        dataColumnIterator.count());
                for (; dataColumnIterator.hasNext();) {
                    UIColumn columnComponent = dataColumnIterator.next();

                    String columnId = columnComponent.getId();
                    if (columnId == null) {
                        continue;
                    }

                    if ((columnComponent instanceof IWidthRangeCapability) == false) {
                        continue;
                    }

                    cols.put(columnId, (IWidthRangeCapability) columnComponent);
                }

                for (Map.Entry<String, String> entry : columnSizes.entrySet()) {
                    String columnId = entry.getKey();
                    String columnWidth = entry.getValue();

                    IWidthRangeCapability columnComponent = cols.get(columnId);
                    if (columnId == null) {
                        continue;
                    }

                    columnComponent.setWidth(columnWidth);
                }
            }
        }

        if (filterProperties != null
                && (gridComponent instanceof IFilterCapability)) {
            ((IFilterCapability) gridComponent)
                    .setFilterProperties(filterProperties);
        }

        if (position >= 0) {
            gridComponent.setFirst(position);
        }
    }

    public void savePreferences(FacesContext facesContext, UIComponent component) {
        if ((component instanceof IGridComponent) == false) {
            throw new FacesException("Can not save dataGrid preferences !");
        }

        IGridComponent gridComponent = (IGridComponent) component;

        if (isSaveColumnsOrder()) {
            if (gridComponent instanceof IOrderedChildrenCapability) {
                columnsOrder = GridTools.getOrderIds(gridComponent);
            }
        }

        if (isSaveSortedColumnIds()) {
            if (gridComponent instanceof ISortedChildrenCapability) {
                sortedColumnIds = GridTools.getSortIds(gridComponent);
            }
        }

        if (isSaveColumnSizes()) {
            columnSizes = null;

            IColumnIterator dataColumnIterator = gridComponent.listColumns();

            if (dataColumnIterator.count() > 0) {
                columnSizes = new HashMap<String, String>(
                        dataColumnIterator.count());
                for (; dataColumnIterator.hasNext();) {
                    UIColumn columnComponent = dataColumnIterator.next();

                    String columnId = columnComponent.getId();
                    if (columnId == null) {
                        continue;
                    }

                    if ((columnComponent instanceof IWidthRangeCapability) == false) {
                        continue;
                    }

                    String columnWidth = ((IWidthRangeCapability) columnComponent)
                            .getWidth();
                    if (columnWidth == null) {
                        continue;
                    }

                    columnSizes.put(columnId, columnWidth);

                }
            }
        }

        if (isSaveFilterProperties()
                && (gridComponent instanceof IFilterCapability)) {
            filterProperties = ((IFilterCapability) gridComponent)
                    .getFilterProperties();
        }

        if (isSavePosition()) {
            position = gridComponent.getFirst();
        }
    }

    public Object saveState(FacesContext context) {
        return new Object[] { new Integer(saveMask), columnsOrder,
                sortedColumnIds, columnSizes, filterProperties,
                new Integer(position) };
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;

        saveMask = ((Integer) values[0]).intValue();
        columnsOrder = (String) values[1];
        sortedColumnIds = (String) values[2];
        columnSizes = (Map<String, String>) values[3];
        filterProperties = (IFilterProperties) values[4];
        position = ((Integer) values[5]).intValue();
    }

    public final boolean isSaveColumnSizes() {
        return (saveMask & SAVE_COLUMN_SIZES) > 0;
    }

    public final void setSaveColumnSizes(boolean saveColumnSizes) {
        saveMask = (saveMask & ~SAVE_COLUMN_SIZES);
        saveMask |= (saveColumnSizes) ? SAVE_COLUMN_SIZES : 0;

        columnSizes = null;
    }

    public final boolean isSaveColumnsOrder() {
        return (saveMask & SAVE_COLUMNS_ORDER) > 0;
    }

    public final void setSaveColumnsOrder(boolean saveColumnsOrder) {
        saveMask = (saveMask & ~SAVE_COLUMNS_ORDER);
        saveMask |= (saveColumnsOrder) ? SAVE_COLUMNS_ORDER : 0;

        columnsOrder = null;
    }

    public final boolean isSaveSortedColumnIds() {
        return (saveMask & SAVE_SORTED_COLUMN_IDS) > 0;
    }

    public final void setSaveSortedColumnIds(boolean saveSortedColumnIds) {
        saveMask = (saveMask & ~SAVE_SORTED_COLUMN_IDS);
        saveMask |= (saveSortedColumnIds) ? SAVE_SORTED_COLUMN_IDS : 0;

        sortedColumnIds = null;
    }

    public final boolean isSaveFilterProperties() {
        return (saveMask & SAVE_FILTER_PROPERTIES) > 0;
    }

    public final void setSaveFilterProperties(boolean saveFilterProperties) {
        saveMask = (saveMask & ~SAVE_FILTER_PROPERTIES);
        saveMask |= (saveFilterProperties) ? SAVE_FILTER_PROPERTIES : 0;

        filterProperties = null;
    }

    public final boolean isSavePosition() {
        return (saveMask & SAVE_POSITION) > 0;
    }

    public final void setSavePosition(boolean savePosition) {
        saveMask = (saveMask & ~SAVE_POSITION);
        saveMask |= (savePosition) ? SAVE_POSITION : 0;

        position = -1;
    }

}
