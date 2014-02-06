/*
 * $Id: AbstractIndexesModel.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public abstract class AbstractIndexesModel implements IIndexesModel {

	public Object[] listSelectedObjects(Object toArray[], Object value) {
		return IndexesModels.listSelectedObject(toArray, value, this);
	}

	public Object getFirstSelectedObject(Object value) {
		return IndexesModels.getFirstSelectedObject(value, this);
	}
}
