/*
 * $Id: FiltredContentModelWrapper.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public class FiltredContentModelWrapper extends ContentModelWrapper implements
		IFiltredModel {
	public void setFilter(IFilterProperties filter) {
		IContentModel contentModel = getContentModel();

		if ((contentModel instanceof IFiltredModel) == false) {
			return;
		}

		((IFiltredModel) contentModel).setFilter(filter);
	}
}
