package org.rcfaces.core.model;

import java.util.List;

import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.item.CriteriaItem;

/**
 * 
 * @author Olivier Oeuillot
 */
public interface ICriteriaSelectedResult {

	ICriteriaManagerCapability getCriteriaManager();

	ISelectedCriteria[] listSelectedCriteria();

	CriteriaItem[] getAvailableCriteriaItems(
			ICriteriaConfiguration criteriaConfiguration);

	ICriteriaConfiguration[] listAvailableCriteriaConfiguration();

	int getResultCount();

	List<?> getResult();

}
