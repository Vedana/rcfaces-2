/*
 * $Id: AbstractConverter.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public abstract class AbstractConverter implements Converter {

	private transient boolean transientFlag;

	public void restoreState(FacesContext arg0, Object state) {
	}

	public Object saveState(FacesContext arg0) {
		return null;
	}

	public void setTransient(boolean transientFlag) {
		this.transientFlag = transientFlag;
	}

	public boolean isTransient() {
		return transientFlag;
	}

}
