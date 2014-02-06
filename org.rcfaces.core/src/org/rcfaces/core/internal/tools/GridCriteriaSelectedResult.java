package org.rcfaces.core.internal.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.DataModel;

import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.internal.capability.ICriteriaContainer;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.renderkit.AbstractCameliaRenderer;
import org.rcfaces.core.item.CriteriaItem;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.model.ISelectedCriteria;

/**
 * 
 * @author Olivier Oeuillot
 */
public class GridCriteriaSelectedResult extends AbstractCriteriaSelectedResult {

	public GridCriteriaSelectedResult(IGridComponent gridComponent,
			ISelectedCriteria[] configs) {
		super((ICriteriaManagerCapability) gridComponent, configs);
	}

	@Override
	protected void fillDatas(List<Object> result,
			Map<ICriteriaConfiguration, CriteriaItem[]> criteriaItemsByContainer) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		IGridComponent gridComponent = (IGridComponent) getCriteriaManager();

		DataModel dataModel = gridComponent.getDataModelValue();

		IComponentRefModel componentRefModel = (IComponentRefModel) AbstractCameliaRenderer
				.getAdapter(IComponentRefModel.class, dataModel, gridComponent);

		if (componentRefModel != null) {
			componentRefModel.setComponent((UIComponent) gridComponent);
		}

		if (gridComponent instanceof IFilterCapability) {
			IFilterProperties filtersMap = ((IFilterCapability) gridComponent)
					.getFilterProperties();

			IFiltredModel filtredDataModel = (IFiltredModel) AbstractCameliaRenderer
					.getAdapter(IFiltredModel.class, dataModel, gridComponent);
			if (filtersMap != null) {
				if (filtredDataModel != null) {
					filtredDataModel.setFilter(filtersMap);
				}
			}
		}

		ISelectedCriteria[] selectedCriteria = listSelectedCriteria();

		Set<ICriteriaContainer> criteriaContainers = new HashSet<ICriteriaContainer>(
				Arrays.asList(getCriteriaManager().listCriteriaContainers()));

		Set<Object>[] selectedValues = new Set[selectedCriteria.length];
		Set<Object>[] possibleValues = new Set[selectedCriteria.length];
		for (int i = 0; i < possibleValues.length; i++) {
			possibleValues[i] = new HashSet<Object>();
			selectedValues[i] = selectedCriteria[i].listSelectedValues();

			criteriaContainers.remove(selectedCriteria[i].getConfig()
					.getCriteriaContainer());
		}

		ICriteriaConfiguration[] notSelectedContainers = new ICriteriaConfiguration[criteriaContainers
				.size()];
		Set<Object>[] notPossibleValues = new Set[notSelectedContainers.length];
		int j = 0;
		for (Iterator<ICriteriaContainer> it = criteriaContainers.iterator(); it
				.hasNext(); j++) {
			ICriteriaContainer cc = it.next();

			notSelectedContainers[j] = cc.getCriteriaConfiguration();
			notPossibleValues[j] = new HashSet<Object>();
		}
		
		try {
			next_row: for (int idx = 0;; idx++) {
				gridComponent.setRowIndex(idx);

				if (gridComponent.isRowAvailable() == false) {
					break;
				}
				boolean nextRow  = false;
				for (int i = 0; i < selectedCriteria.length; i++) {
					ISelectedCriteria sc = selectedCriteria[i];

					Object dataValue = CriteriaTools.getDataValue(facesContext,
							gridComponent, sc.getConfig());
					
					if (dataValue == null) {
						continue;
					}
					
					boolean outCriteria = false;
					if(nextRow == false)  {
						for (int z = 0; z< selectedCriteria.length; z++ ){
							if(z==i){
								continue;
							}
							ISelectedCriteria sc2 = selectedCriteria[z];
							Object dataValue2 = CriteriaTools.getDataValue(facesContext,
									gridComponent, sc2.getConfig());
							if (selectedValues[z].contains(dataValue2)== false){
								outCriteria = true;
							}
						}
						if (outCriteria == false)
							possibleValues[i].add(dataValue);
					}
					if (selectedValues[i].contains(dataValue) == false) {
						nextRow  = true;
						continue next_row;
					}
				}

				for (int i = 0; i < notSelectedContainers.length; i++) {
					Object dataValue = CriteriaTools.getDataValue(facesContext,
							gridComponent, notSelectedContainers[i]);
					
					if (dataValue == null) {
						continue;
					}
					
					notPossibleValues[i].add(dataValue);
				}

				result.add(gridComponent.getRowData());
				
			}
			
		} finally {
			gridComponent.setRowIndex(-1);
		}
		
		for (int i = 0; i < selectedCriteria.length; i++) {
			ISelectedCriteria sc = selectedCriteria[i];

		Set<Object> values = null;
			values = possibleValues[i];
			fillValues(facesContext, sc.getConfig(), criteriaItemsByContainer,
					values);
		}

		for (int i = 0; i < notSelectedContainers.length; i++) {
			ICriteriaConfiguration sc = notSelectedContainers[i];

			Set<Object> values = notPossibleValues[i];
			fillValues(facesContext, sc, criteriaItemsByContainer, values);
		}
	}

	private void fillValues(
			FacesContext facesContext,
			ICriteriaConfiguration config,
			Map<ICriteriaConfiguration, CriteriaItem[]> criteriaItemsByContainer,
			Set<Object> values) {

		Converter labelConverter = config.getLabelConverter();
		UIComponent component = (UIComponent) config;

		List<CriteriaItem> criteriaItems = new ArrayList<CriteriaItem>(
				values.size());

		for (Iterator<Object> it = values.iterator(); it.hasNext();) {
			Object criteriaValue = it.next();

			String criteriaLabel = ValuesTools.convertValueToString(
					criteriaValue, labelConverter, component, facesContext);

			CriteriaItem criteriaItem = new CriteriaItem();
			criteriaItem.setLabel(criteriaLabel);
			criteriaItem.setValue(criteriaValue);

			criteriaItems.add(criteriaItem);
		}

		Collections.sort(criteriaItems, new Comparator<CriteriaItem>() {

			public int compare(CriteriaItem o1, CriteriaItem o2) {

				return o1.getLabel().compareTo(o2.getLabel());
			}
		});

		CriteriaItem[] criteriaItemsArray = criteriaItems
				.toArray(new CriteriaItem[criteriaItems.size()]);

		criteriaItemsByContainer.put(config, criteriaItemsArray);
	}
}
