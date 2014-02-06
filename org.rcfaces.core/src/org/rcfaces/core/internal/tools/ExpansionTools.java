/*
 * $Id: ExpansionTools.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IExpandedValuesCapability;
import org.rcfaces.core.lang.provider.ICheckProvider;
import org.rcfaces.core.lang.provider.IExpansionProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class ExpansionTools extends CollectionTools {

    private static final Log LOG = LogFactory.getLog(ExpansionTools.class);

    private static final IValuesAccessor EXPANSION_PROVIDER_VALUES_ACCESSOR = new IValuesAccessor() {

        public int getCount(Object expandProvider) {
            return ((IExpansionProvider) expandProvider)
                    .getExpandedValuesCount();
        }

        public Object getFirst(Object expandProvider, Object refValues) {
            return null;
        }

        public Object[] listValues(Object expandProvider, Object refValues) {
            return convertToObjectArray(((IExpansionProvider) expandProvider)
                    .getExpandedValues());
        }

        public void setAdaptedValues(Object expandedValueProvider,
                Object expandedValues) {
            ((IExpansionProvider) expandedValueProvider)
                    .setExpandedValues(expandedValues);
        }

        public Object getAdaptedValues(Object value) {
            return value;
        }

        public Object getComponentValues(UIComponent component) {

            return ((IExpandedValuesCapability) component).getExpandedValues();
        }

        public void setComponentValues(UIComponent component, Object values) {

            ((IExpandedValuesCapability) component).setExpandedValues(values);
        }

        public Class< ? > getComponentValuesType(FacesContext facesContext,
                UIComponent component) {

            return ((IExpandedValuesCapability) component)
                    .getExpandedValuesType(facesContext);
        }

    };

    public static int getCount(UIComponent component, Object expandedValues) {
        IValuesAccessor valuesAccessor = getValuesAccessor(expandedValues,
                IExpansionProvider.class, getValueAccessor(component), true,
                true);

        if (valuesAccessor == null) {
            return 0;
        }

        return valuesAccessor.getCount(expandedValues);
    }

    public static Object[] listValues(UIComponent component,
            Object expandedValues, Object refValue) {
        IValuesAccessor valuesAccessor = getValuesAccessor(expandedValues,
                IExpansionProvider.class, getValueAccessor(component), true,
                true);

        if (valuesAccessor == null) {
            return EMPTY_VALUES;
        }

        return valuesAccessor.listValues(expandedValues, refValue);
    }

    public static Object getAdaptedValues(Object value, boolean useValue) {
        IValuesAccessor valuesAccessor = getValuesAccessor(value,
                IExpansionProvider.class, EXPANSION_PROVIDER_VALUES_ACCESSOR,
                useValue, true);

        if (valuesAccessor == null) {
            return null;
        }

        return valuesAccessor.getAdaptedValues(value);
    }

    public static boolean setAdaptedValues(Object value, Object values) {
        IValuesAccessor valuesAccessor = getValuesAccessor(value,
                IExpansionProvider.class, EXPANSION_PROVIDER_VALUES_ACCESSOR,
                false, true);

        if (valuesAccessor == null) {
            return false;
        }

        valuesAccessor.setAdaptedValues(value, values);
        return true;
    }

    public static void expand(FacesContext facesContext, UIComponent component,
            Object rowValue) {
        select(component, getValueAccessor(component), rowValue);
    }

    public static void expandAll(FacesContext facesContext,
            UIComponent component) {
        selectAll(component, getValueAccessor(component), null);
    }

    public static void collapse(FacesContext facesContext,
            UIComponent component, Object rowValue) {
        deselect(component, getValueAccessor(component), rowValue);
    }

    public static void collapseAll(FacesContext facesContext,
            UIComponent component) {
        deselectAll(component, getValueAccessor(component));
    }

    public static void setExpansionValues(FacesContext facesContext,
            UIComponent component, Set<Object> valuesSet) {

        setValues(component, getValueAccessor(component), valuesSet);
    }

    public static Set<Object> expansionValuesToSet(FacesContext facesContext,
            UIComponent component, boolean immutable) {
        return valuesToSet(component, getValueAccessor(component), immutable);
    }

    protected static IValuesAccessor getValueAccessor(UIComponent component) {

        return EXPANSION_PROVIDER_VALUES_ACCESSOR;
    }
}
