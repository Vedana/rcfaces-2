/*
 * $Id: FilterExpressionTools.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.lang.FilterPropertiesMap;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public final class FilterExpressionTools {
    

    private static final Log LOG = LogFactory
            .getLog(FilterExpressionTools.class);

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    /**
     * 
     */
    public static final IFilterProperties EMPTY = new IFilterProperties() {
        

        private static final long serialVersionUID = -3817846186098661680L;

        public boolean containsKey(Serializable propertyName) {
            return false;
        }

        public Object put(Serializable filterName, Object value) {
            throw new UnsupportedOperationException(
                    "Not supported for EMPTY filtersMap !");
        }

        public Object remove(Serializable filterName) {
            throw new UnsupportedOperationException(
                    "Not supported for EMPTY filtersMap !");
        }

        public String[] listNames() {
            return STRING_EMPTY_ARRAY;
        }

        public void clear() {
            throw new UnsupportedOperationException(
                    "Not supported for EMPTY filtersMap !");
        }

        public boolean isEmpty() {
            return true;
        }

        public int size() {
            return 0;
        }

        public void putAll(Map map) {
            throw new UnsupportedOperationException(
                    "Not supported for EMPTY filtersMap !");
        }

        public Map toMap() {
            return Collections.EMPTY_MAP;
        }

        public boolean isTransient() {
            return false;
        }

        public void restoreState(FacesContext context, Object state) {
        }

        public Object saveState(FacesContext context) {
            return null;
        }

        public void setTransient(boolean newTransientValue) {
        }

        public Object getProperty(Serializable name) {
            return null;
        }

        public String getStringProperty(Serializable name) {
            return null;
        }

        public String getStringProperty(Serializable name, String defaultValue) {
            return defaultValue;
        }

        public boolean getBoolProperty(Serializable name, boolean defaultValue) {
            return defaultValue;
        }

        public Boolean getBooleanProperty(Serializable name) {
            return null;
        }

        public int getIntProperty(Serializable name, int defaultValue) {
            return defaultValue;
        }

        public Number getNumberProperty(Serializable name) {
            return null;
        }

        public int hashCode() {
            return 31;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            return true;
        }

    };

    public static IFilterProperties create(Map map) {
        return new FilterPropertiesMap(map);
    }
}
