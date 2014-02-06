/*
 * $Id: IPreferencesCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import org.rcfaces.core.preference.IComponentPreferences;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IPreferencesCapability {

    /**
     * Returns an object that holds the preferences for the component.
     * 
     * @return ComponentPreference object
     */
    IComponentPreferences getPreferences();

    /**
     * Sets an object that holds the preferences for the component.
     * 
     * @param preferences
     *            ComponentPreference object
     */
    void setPreferences(IComponentPreferences preferences);
}
