/*
 * $Id: AdditionalInformationTools.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.AdditionalInformationComponent;
import org.rcfaces.core.component.capability.IAdditionalInformationProvider;
import org.rcfaces.core.component.capability.IAdditionalInformationValuesCapability;
import org.rcfaces.core.component.iterator.IAdditionalInformationIterator;
import org.rcfaces.core.internal.capability.IAdditionalInformationComponent;
import org.rcfaces.core.internal.capability.IAdditionalInformationRangeComponent;
import org.rcfaces.core.internal.util.ComponentIterators;
import org.rcfaces.core.lang.provider.ICheckProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class AdditionalInformationTools extends CollectionTools {

	private static final Log LOG = LogFactory
			.getLog(AdditionalInformationTools.class);

	private static final IValuesAccessor ADDITIONAL_PROVIDER_VALUES_ACCESSOR = new IValuesAccessor() {

		public int getCount(Object additionalInformationProvider) {
			return ((IAdditionalInformationProvider) additionalInformationProvider)
					.getAdditionalInformationValuesCount();
		}

		public Object getFirst(Object additionalInformationProvider,
				Object refValues) {
			return ((IAdditionalInformationProvider) additionalInformationProvider)
					.getFirstAdditionalInformationValue();
		}

		public Object[] listValues(Object additionalInformationProvider,
				Object refValues) {
			return convertToObjectArray(((IAdditionalInformationProvider) additionalInformationProvider)
					.getAdditionalInformationValues());
		}

		public void setAdaptedValues(Object additionalInformationProvider,
				Object additionalInformationValues) {
			((IAdditionalInformationProvider) additionalInformationProvider)
					.setAdditionalInformationValues(additionalInformationValues);
		}

		public Object getAdaptedValues(Object value) {
			return value;
		}

		public Object getComponentValues(UIComponent component) {
			return ((IAdditionalInformationValuesCapability) component)
					.getAdditionalInformationValues();
		}

		public void setComponentValues(UIComponent component, Object values) {
			((IAdditionalInformationValuesCapability) component)
					.setAdditionalInformationValues(values);
		}

        public Class< ? > getComponentValuesType(FacesContext facesContext,
				UIComponent component) {
			return ((IAdditionalInformationValuesCapability) component)
					.getAdditionalInformationValuesType(facesContext);
		}
	};

	private static final IAdditionalInformationIterator EMPTY_ADDITIONAL_INFORMATION_ITERATOR = new AdditionalInformationListIterator(
            Collections.<AdditionalInformationComponent> emptyList());

	public static int getCount(Object additionalInformationValues) {
		IValuesAccessor valuesAccessor = getValuesAccessor(
				additionalInformationValues, ICheckProvider.class,
				ADDITIONAL_PROVIDER_VALUES_ACCESSOR, true, true);

		if (valuesAccessor == null) {
			return 0;
		}
		return valuesAccessor.getCount(additionalInformationValues);
	}

	public static Object getFirst(Object additionalInformationValues,
			Object refValue) {
		IValuesAccessor valuesAccessor = getValuesAccessor(
				additionalInformationValues, ICheckProvider.class,
				ADDITIONAL_PROVIDER_VALUES_ACCESSOR, true, true);

		if (valuesAccessor == null) {
			return null;
		}

		return valuesAccessor.getFirst(additionalInformationValues, refValue);
	}

	public static Object[] listValues(Object additionalInformationValues,
			Object refValue) {
		IValuesAccessor valuesAccessor = getValuesAccessor(
				additionalInformationValues, ICheckProvider.class,
				ADDITIONAL_PROVIDER_VALUES_ACCESSOR, true, true);

		if (valuesAccessor == null) {
			return EMPTY_VALUES;
		}

		return valuesAccessor.listValues(additionalInformationValues, refValue);
	}

	public static void show(FacesContext facesContext,
			IAdditionalInformationComponent component, Object rowValue) {
		select((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				rowValue);
	}

	public static void show(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int index) {
		select((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				index);
	}

	public static void show(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int indexes[]) {
		select((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				indexes);
	}

	public static void show(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int start, int end) {
		select((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				start, end);
	}

	public static void showAll(FacesContext facesContext,
			IAdditionalInformationComponent component) {
		selectAll((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				null);
	}

	public static void hide(FacesContext facesContext,
			IAdditionalInformationComponent component, Object rowValue) {
		deselect((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				rowValue);
	}

	public static void hide(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int index) {
		deselect((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				index);
	}

	public static void hide(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int indexes[]) {
		deselect((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				indexes);
	}

	public static void hide(FacesContext facesContext,
			IAdditionalInformationRangeComponent component, int start, int end) {
		deselect((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				start, end);
	}

	public static void hideAll(FacesContext facesContext,
			IAdditionalInformationComponent component) {
		deselectAll((UIComponent) component,
				ADDITIONAL_PROVIDER_VALUES_ACCESSOR);
	}

	public static IAdditionalInformationIterator listAdditionalInformations(
			UIComponent component) {
        List<AdditionalInformationComponent> list = ComponentIterators.list(
                component, AdditionalInformationComponent.class);
		if (list.isEmpty()) {
			return EMPTY_ADDITIONAL_INFORMATION_ITERATOR;
		}

		return new AdditionalInformationListIterator(list);
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
	 */
    private static final class AdditionalInformationListIterator
            extends
            ComponentIterators.ComponentListIterator<AdditionalInformationComponent>
            implements IAdditionalInformationIterator {

        public AdditionalInformationListIterator(
                List<AdditionalInformationComponent> list) {
			super(list);
		}

		public final AdditionalInformationComponent next() {
            return nextComponent();
		}

		public AdditionalInformationComponent[] toArray() {
			return (AdditionalInformationComponent[]) toArray(new AdditionalInformationComponent[count()]);
		}
	}

    public static Set<Object> additionalInformationValuesToSet(
			FacesContext facesContext,
			IAdditionalInformationComponent component, boolean immutable) {

		return valuesToSet((UIComponent) component,
				ADDITIONAL_PROVIDER_VALUES_ACCESSOR, immutable);
	}

	public static void setAdditionalInformationValues(
			FacesContext facesContext,
            IAdditionalInformationComponent component, Set<Object> valuesSet) {

		setValues((UIComponent) component, ADDITIONAL_PROVIDER_VALUES_ACCESSOR,
				valuesSet);

	}
}
