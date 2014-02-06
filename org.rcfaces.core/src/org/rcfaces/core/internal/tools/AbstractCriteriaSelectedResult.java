package org.rcfaces.core.internal.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.item.CriteriaItem;
import org.rcfaces.core.model.ICriteriaSelectedResult;
import org.rcfaces.core.model.ISelectedCriteria;

/**
 * 
 * @author Olivier Oeuillot
 * 
 */
public abstract class AbstractCriteriaSelectedResult implements
		ICriteriaSelectedResult {

	private final ICriteriaManagerCapability manager;
	private final ISelectedCriteria[] configs;

	private Map<ICriteriaConfiguration, CriteriaItem[]> criteriaItemsByContainer;

	private List<Object> result;

	public AbstractCriteriaSelectedResult(ICriteriaManagerCapability manager,
			ISelectedCriteria[] configs) {
		this.manager = manager;
		this.configs = configs;
	}

	public ICriteriaManagerCapability getCriteriaManager() {
		return manager;
	}

	public ISelectedCriteria[] listSelectedCriteria() {
		return configs;
	}

	public CriteriaItem[] getAvailableCriteriaItems(
			ICriteriaConfiguration configuration) {
		computeDatas();

		return criteriaItemsByContainer.get(configuration);
	}

	public int getResultCount() {
		computeDatas();

		return result.size();
	}

	public List<?> getResult() {
		computeDatas();

		return result;
	}

	protected synchronized void computeDatas() {
		if (result != null) {
			return;
		}

		result = new ArrayList<Object>();
		criteriaItemsByContainer = new HashMap<ICriteriaConfiguration, CriteriaItem[]>();

		fillDatas(result, criteriaItemsByContainer);
	}

	public ICriteriaConfiguration[] listAvailableCriteriaConfiguration() {
		Collection<ICriteriaConfiguration> c = criteriaItemsByContainer
				.keySet();

		return c.toArray(new ICriteriaConfiguration[c.size()]);
	}

	protected abstract void fillDatas(List<Object> result,
			Map<ICriteriaConfiguration, CriteriaItem[]> criteriaItemsByContainer);

}
