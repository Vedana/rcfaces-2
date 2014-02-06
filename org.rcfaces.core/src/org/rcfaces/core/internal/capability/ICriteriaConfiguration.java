package org.rcfaces.core.internal.capability;

import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ISelectionCardinalityCapability;
import org.rcfaces.core.lang.provider.ISelectionProvider;

/**
 * 
 * @author Oeuillot
 * 
 */
public interface ICriteriaConfiguration extends ISelectionProvider,
		ISelectionCardinalityCapability {
	ICriteriaContainer getCriteriaContainer();

	boolean isCriteriaValueSetted();

	// Retourne l'objet associé la colonne et  la ligne selectionnée
	Object getCriteriaValue();

	// Converter de l'objet associé la colonne et  la ligne selectionnée
	Converter getCriteriaConverter();

	Converter getLabelConverter();

	int getCriteriaCardinality();

	String getCriteriaTitle();
}
