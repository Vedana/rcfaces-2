/*
 * $Id: FilteredDataModel.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import javax.faces.FacesException;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import org.rcfaces.core.model.IFilterProperties;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class FilteredDataModel {

    public static DataModel filter(DataModel dataModel,
            IFilterProperties filters) {
        if (filters == null || filters.size() < 1) {
            return dataModel;
        }

        if (dataModel instanceof ArrayDataModel) {
            return dataModel;
        }

        throw new FacesException("Can not filter dataModel '" + dataModel
                + "'.");
    }

}
