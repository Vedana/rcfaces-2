/*
 * $Id: SelectionTools.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ISelectedValuesCapability;
import org.rcfaces.core.internal.capability.ISelectionComponent;
import org.rcfaces.core.internal.capability.ISelectionRangeComponent;
import org.rcfaces.core.lang.provider.ICheckProvider;
import org.rcfaces.core.lang.provider.ISelectionProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class SelectionTools extends CollectionTools {

    private static final Log LOG = LogFactory.getLog(SelectionTools.class);

    private static final IValuesAccessor SELECTION_PROVIDER_VALUES_ACCESSOR = new IValuesAccessor() {
        public int getCount(Object selectionProvider) {
            return ((ISelectionProvider) selectionProvider)
                    .getSelectedValuesCount();
        }

        public Object getFirst(Object selectionProvider, Object refValues) {
            return ((ISelectionProvider) selectionProvider)
                    .getFirstSelectedValue();
        }

        public Object[] listValues(Object selectionProvider, Object refValues) {
            return convertToObjectArray(((ISelectionProvider) selectionProvider)
                    .getSelectedValues());
        }

        public Object getAdaptedValues(Object value) {
            return value;
        }

        public void setAdaptedValues(Object selectionProvider,
                Object selectedValues) {
            ((ISelectionProvider) selectionProvider)
                    .setSelectedValues(selectedValues);
        }

        public Object getComponentValues(UIComponent component) {
            return ((ISelectedValuesCapability) component).getSelectedValues();
        }

        public void setComponentValues(UIComponent component, Object values) {
            ((ISelectedValuesCapability) component).setSelectedValues(values);
        }

        public Class getComponentValuesType(FacesContext facesContext,
                UIComponent component) {
            return ((ISelectedValuesCapability) component)
                    .getSelectedValuesType(facesContext);
        }
    };

    public static int getCount(Object selectedValues) {
        IValuesAccessor valuesAccessor = getValuesAccessor(selectedValues,
                ISelectionProvider.class, SELECTION_PROVIDER_VALUES_ACCESSOR,
                true, true);
        if (valuesAccessor == null) {
            return 0;
        }
        return valuesAccessor.getCount(selectedValues);
    }

    public static Object getFirst(Object selectedValues, Object refValue) {
        IValuesAccessor valuesAccessor = getValuesAccessor(selectedValues,
                ISelectionProvider.class, SELECTION_PROVIDER_VALUES_ACCESSOR,
                true, true);
        if (valuesAccessor == null) {
            return null;
        }
        return valuesAccessor.getFirst(selectedValues, refValue);
    }

    public static Object[] listValues(Object selectedValues, Object refValue) {
        IValuesAccessor valuesAccessor = getValuesAccessor(selectedValues,
                ISelectionProvider.class, SELECTION_PROVIDER_VALUES_ACCESSOR,
                true, true);
        if (valuesAccessor == null) {
            return EMPTY_VALUES;
        }
        return valuesAccessor.listValues(selectedValues, refValue);
    }

    public static Object getAdaptedValues(Object value, boolean useValue) {
        IValuesAccessor valuesAccessor = getValuesAccessor(value,
                ICheckProvider.class, SELECTION_PROVIDER_VALUES_ACCESSOR,
                useValue, true);

        if (valuesAccessor == null) {
            return null;
        }

        return valuesAccessor.getAdaptedValues(value);
    }

    public static boolean setAdaptedValues(Object value, Object values) {
        IValuesAccessor valuesAccessor = getValuesAccessor(value,
                ICheckProvider.class, SELECTION_PROVIDER_VALUES_ACCESSOR,
                false, true);

        if (valuesAccessor == null) {
            return false;
        }

        valuesAccessor.setAdaptedValues(value, values);
        return true;
    }

    public static void select(FacesContext facesContext,
            ISelectionComponent component, Object rowValue) {
        select((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                rowValue);
    }

    public static void select(FacesContext facesContext,
            ISelectionRangeComponent component, int index) {
        select((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                index);
    }

    public static void select(FacesContext facesContext,
            ISelectionRangeComponent component, int indexes[]) {
        select((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                indexes);
    }

    public static void select(FacesContext facesContext,
            ISelectionRangeComponent component, int start, int end) {
        select((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                start, end);
    }

    public static void selectAll(FacesContext facesContext,
            ISelectionComponent component) {
        selectAll((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                null);
    }

    public static void deselect(FacesContext facesContext,
            ISelectionComponent component, Object rowValue) {
        deselect((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                rowValue);
    }

    public static void deselect(FacesContext facesContext,
            ISelectionRangeComponent component, int index) {
        deselect((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                index);
    }

    public static void deselect(FacesContext facesContext,
            ISelectionRangeComponent component, int indexes[]) {
        deselect((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                indexes);
    }

    public static void deselect(FacesContext facesContext,
            ISelectionRangeComponent component, int start, int end) {
        deselect((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                start, end);
    }

    public static void deselectAll(FacesContext facesContext,
            ISelectionComponent component) {
        deselectAll((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR);
    }

    public static void setSelectionValues(FacesContext facesContext,
            ISelectionProvider component, Set<Object> valuesSet) {

        setValues((UIComponent) component, SELECTION_PROVIDER_VALUES_ACCESSOR,
                valuesSet);
    }

    public static Set<Object> selectionValuesToSet(FacesContext facesContext,
            ISelectionComponent component, boolean immutable) {
        return valuesToSet((UIComponent) component,
                SELECTION_PROVIDER_VALUES_ACCESSOR, immutable);
    }
}
