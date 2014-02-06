package org.rcfaces.core.component.capability;

import org.rcfaces.core.internal.capability.ICriteriaContainer;
import org.rcfaces.core.model.ICriteriaSelectedResult;
import org.rcfaces.core.model.ISelectedCriteria;

/**
 * 
 * @author Oeuillot Olivier
 * 
 */
public interface ICriteriaManagerCapability {
	ICriteriaContainer[] listCriteriaContainers();

	ICriteriaContainer[] listSelectedCriteriaContainers();

	void setSelectedCriteriaContainers(ICriteriaContainer[] containers);

	ICriteriaSelectedResult processSelectedCriteria();

	ICriteriaSelectedResult processSelectedCriteria(ISelectedCriteria[] configs);
}
