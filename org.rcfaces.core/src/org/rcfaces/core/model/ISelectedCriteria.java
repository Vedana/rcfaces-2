package org.rcfaces.core.model;

import java.util.Set;

import org.rcfaces.core.internal.capability.ICriteriaConfiguration;

/**
 * 
 * @author Oeuillot
 */
public interface ISelectedCriteria {
	ICriteriaConfiguration getConfig();

	Set<Object> listSelectedValues();
}
