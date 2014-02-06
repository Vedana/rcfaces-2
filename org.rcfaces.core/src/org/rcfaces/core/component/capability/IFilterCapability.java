/*
 * $Id: IFilterCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import org.rcfaces.core.model.IFilterProperties;

/**
 * An object that represents the filter to use on the server side.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IFilterCapability {

    /**
     * Return an object that represent the filter to use on the server side.
     * 
     * @return filter
     */
    IFilterProperties getFilterProperties();

    /**
     * Sets an object that represent the filter to use on the server side.
     * 
     * @param properties
     *            filter
     */
    void setFilterProperties(IFilterProperties properties);
}
