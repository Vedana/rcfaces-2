/*
 * $Id: IComponentLocaleCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import java.util.Locale;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IComponentLocaleCapability {

    /**
     * Returns the locale defined for the component
     * 
     * @return the defined locale
     */
    Locale getComponentLocale();

    /**
     * Sets the locale for the component
     * 
     * @param locale
     *            locale to define
     */
    void setComponentLocale(Locale locale);
}
