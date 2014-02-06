/*
 * $Id: CheckTools.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ICheckedValuesCapability;
import org.rcfaces.core.internal.capability.ICheckComponent;
import org.rcfaces.core.internal.capability.ICheckRangeComponent;
import org.rcfaces.core.lang.provider.ICheckProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class CheckTools extends CollectionTools {

	private static final Log LOG = LogFactory.getLog(CheckTools.class);

	private static final IValuesAccessor CHECK_PROVIDER_VALUES_ACCESSOR = new IValuesAccessor() {

		public int getCount(Object checkProvider) {
			return ((ICheckProvider) checkProvider).getCheckedValuesCount();
		}

		public Object getFirst(Object checkProvider, Object refValues) {
			return ((ICheckProvider) checkProvider).getFirstCheckedValue();
		}

		public Object[] listValues(Object checkProvider, Object refValues) {
			return convertToObjectArray(((ICheckProvider) checkProvider)
					.getCheckedValues());
		}

		public Object getAdaptedValues(Object value) {
			return value;
		}

		public void setAdaptedValues(Object checkProvider, Object checkedValues) {
			((ICheckProvider) checkProvider).setCheckedValues(checkedValues);
		}

		public Object getComponentValues(UIComponent component) {
			return ((ICheckedValuesCapability) component).getCheckedValues();
		}

		public void setComponentValues(UIComponent component, Object values) {
			((ICheckedValuesCapability) component).setCheckedValues(values);
		}

		public Class getComponentValuesType(FacesContext facesContext,
				UIComponent component) {
			return ((ICheckedValuesCapability) component)
					.getCheckedValuesType(facesContext);
		}
	};

	public static int getCount(Object checkedValues) {
		IValuesAccessor valuesAccessor = getValuesAccessor(checkedValues,
				ICheckProvider.class, CHECK_PROVIDER_VALUES_ACCESSOR, true,
				true);

		if (valuesAccessor == null) {
			return 0;
		}
		return valuesAccessor.getCount(checkedValues);
	}

	public static Object getFirst(Object checkedValues, Object refValue) {
		IValuesAccessor valuesAccessor = getValuesAccessor(checkedValues,
				ICheckProvider.class, CHECK_PROVIDER_VALUES_ACCESSOR, true,
				true);

		if (valuesAccessor == null) {
			return null;
		}

		return valuesAccessor.getFirst(checkedValues, refValue);
	}

	public static Object[] listValues(Object checkedValues, Object refValue) {
		IValuesAccessor valuesAccessor = getValuesAccessor(checkedValues,
				ICheckProvider.class, CHECK_PROVIDER_VALUES_ACCESSOR, true,
				true);

		if (valuesAccessor == null) {
			return EMPTY_VALUES;
		}

		return valuesAccessor.listValues(checkedValues, refValue);
	}

	public static Object getAdaptedValues(Object value, boolean useValue) {
		IValuesAccessor valuesAccessor = getValuesAccessor(value,
				ICheckProvider.class, CHECK_PROVIDER_VALUES_ACCESSOR, useValue,
				true);

		if (valuesAccessor == null) {
			return null;
		}

		return valuesAccessor.getAdaptedValues(value);
	}

	public static boolean setAdaptedValues(Object value, Object values) {
		IValuesAccessor valuesAccessor = getValuesAccessor(value,
				ICheckProvider.class, CHECK_PROVIDER_VALUES_ACCESSOR, false,
				true);

		if (valuesAccessor == null) {
			return false;
		}

		valuesAccessor.setAdaptedValues(value, values);
		return true;
	}

	public static void check(FacesContext facesContext,
			ICheckComponent component, Object rowValue) {
		select((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR,
				rowValue);
	}

	public static void check(FacesContext facesContext,
			ICheckRangeComponent component, int index) {
		select((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR, index);
	}

	public static void check(FacesContext facesContext,
			ICheckRangeComponent component, int indexes[]) {
		select((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR, indexes);
	}

	public static void check(FacesContext facesContext,
			ICheckRangeComponent component, int start, int end) {
		select((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR, start,
				end);
	}

	public static void checkAll(FacesContext facesContext,
			ICheckComponent component) {
		selectAll((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR, null);
	}

	public static void uncheck(FacesContext facesContext,
			ICheckComponent component, Object rowValue) {
		deselect((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR,
				rowValue);
	}

	public static void uncheck(FacesContext facesContext,
			ICheckComponent component, int index) {
		deselect((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR, index);
	}

	public static void uncheck(FacesContext facesContext,
			ICheckComponent component, int indexes[]) {
		deselect((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR,
				indexes);
	}

	public static void uncheck(FacesContext facesContext,
			ICheckComponent component, int start, int end) {
		deselect((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR,
				start, end);
	}

	public static void uncheckAll(FacesContext facesContext,
			ICheckComponent component) {
		deselectAll((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR);
	}

	public static void setCheckValues(FacesContext facesContext,
			ICheckComponent component, Set valuesSet) {

		setValues((UIComponent) component, CHECK_PROVIDER_VALUES_ACCESSOR,
				valuesSet);
	}

    public static Set<Object> checkValuesToSet(FacesContext facesContext,
			ICheckComponent component, boolean immutable) {
		return valuesToSet((UIComponent) component,
				CHECK_PROVIDER_VALUES_ACCESSOR, immutable);
	}
}
