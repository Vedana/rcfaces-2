package org.rcfaces.core.internal.capability;

import org.rcfaces.core.component.capability.ICriteriaManagerCapability;

/**
 * 
 * @author Olivier Oeuillot
 * 
 */
public interface ICriteriaContainer {
	ICriteriaManagerCapability getCriteriaManager();

	ICriteriaConfiguration getCriteriaConfiguration();

	// Retourne la valeur de la colonne et de la ligne selectionné
	Object getValue();

	String getId();
}
