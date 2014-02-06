/*
 * $Id: AbstractProperties.java,v 1.4 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import java.io.Serializable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:07 $
 */
public abstract class AbstractProperties implements IProperties {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IComponentData#getBoolProperty(java
     * .lang.String, boolean)
     */
    @Override
    public final boolean getBoolProperty(Serializable name, boolean defaultValue) {
        Boolean b = getBooleanProperty(name);
        if (b == null) {
            return defaultValue;
        }

        return b.booleanValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IComponentData#getBooleanProperty
     * (java.lang.String)
     */
    @Override
    public Boolean getBooleanProperty(Serializable name) {
        Object s = getProperty(name);
        if (s == null) {
            return null;
        }

        if (s instanceof Boolean) {
            return (Boolean) s;
        }

        if (s instanceof String) {
            return Boolean.valueOf((String) s);
        }

        return null;
    }

    @Override
    public Number getNumberProperty(Serializable name) {
        Object s = getProperty(name);
        if (s == null) {
            return null;
        }

        if (s instanceof Number) {
            return (Number) s;
        }

        if (s instanceof String) {
            String ss = (String) s;

            if (ss.indexOf('.') >= 0 || ss.indexOf('E') >= 0) {
                return Double.valueOf(ss);
            }

            return Long.valueOf(ss);
        }

        return null;
    }

    @Override
    public String getStringProperty(Serializable name) {
        Object s = getProperty(name);
        if (s == null) {
            return null;
        }

        return String.valueOf(s);
    }

    @Override
    public String getStringProperty(Serializable name, String defaultValue) {
        String s = getStringProperty(name);
        if (s == null) {
            return defaultValue;
        }

        return s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IComponentData#getIntProperty(java
     * .lang.String, int)
     */
    @Override
    public int getIntProperty(Serializable name, int defaultValue) {
        Number i = getNumberProperty(name);
        if (i == null) {
            return defaultValue;
        }

        return i.intValue();
    }

    @Override
    public abstract Object getProperty(Serializable name);
}
