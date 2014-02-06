/*
 * $Id: SelectItemAdapter.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class SelectItemAdapter {

    private static final Log LOG = LogFactory.getLog(SelectItemAdapter.class);

    private static final SelectItem[] SELECT_ITEM_EMPTY_ARRAY = new SelectItem[0];

    public static SelectItem[] adapt(Object value[]) {
        return adapt(value, null, null);
    }

    public static SelectItem[] adapt(Object value[], Object parameter,
            FacesContext facesContext) {
        if (value == null || value.length == 0) {
            return SELECT_ITEM_EMPTY_ARRAY;
        }

        return adapt(Arrays.asList(value), parameter, facesContext);
    }

    public static SelectItem[] adapt(Collection< ? > collection) {
        return adapt(collection, null, null);
    }

    public static SelectItem[] adapt(Collection< ? > collection,
            Object parameter, FacesContext facesContext) {
        if (collection == null || collection.isEmpty()) {
            return SELECT_ITEM_EMPTY_ARRAY;
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        List<SelectItem> ret = null;

        int count = collection.size();
        for (Iterator< ? > it = collection.iterator(); it.hasNext(); count--) {
            Object value = it.next();

            if (value == null) {
                continue;
            }

            SelectItem selectItem = SelectItemTools.convertToSelectItem(
                    facesContext, value);
            if (selectItem != null) {
                if (ret == null) {
                    ret = new ArrayList<SelectItem>(count);
                }
                ret.add(selectItem);
                continue;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Can not convert value '" + value
                        + "' to selectItem !");
            }
        }

        if (ret == null) {
            return SELECT_ITEM_EMPTY_ARRAY;
        }

        return ret.toArray(new SelectItem[ret.size()]);
    }
}
