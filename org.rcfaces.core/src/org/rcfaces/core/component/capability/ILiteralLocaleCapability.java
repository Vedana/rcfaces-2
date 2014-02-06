/*
 * $Id: ILiteralLocaleCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import java.util.Locale;

/**
 * Best practice : define this on the init component.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ILiteralLocaleCapability {

    /**
     * Returns the locale defined for literal attributes. (date, number)
     * 
     * @return the defined locale
     */
    Locale getLiteralLocale();

    /**
     * Sets the locale for literal attributes. (date, number)
     * 
     * @param locale
     *            locale to define
     */
    void setLiteralLocale(Locale locale);
}
