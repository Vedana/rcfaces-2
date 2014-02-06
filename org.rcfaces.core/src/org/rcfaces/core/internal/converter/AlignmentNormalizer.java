package org.rcfaces.core.internal.converter;

import java.util.HashSet;
import java.util.Set;

import javax.faces.FacesException;

import org.rcfaces.core.component.capability.IAlignmentCapability;

public class AlignmentNormalizer {

	private static final Set<String> VALUES = new HashSet<String>(3);
	static {
		VALUES.add(IAlignmentCapability.LEFT);
		VALUES.add(IAlignmentCapability.RIGHT);
		VALUES.add(IAlignmentCapability.CENTER);
	}
	
	public static String normalize(String alignment) {
		if (alignment == null) {
			return null;
		}
		alignment = alignment.trim().toLowerCase();
		if (alignment.length() == 0) {
			return null;
		}
		if (VALUES.contains(alignment)) {
			return alignment;
		}
		throw new FacesException("Incorrect alignment value \"" + alignment + "\".");
	}
}
