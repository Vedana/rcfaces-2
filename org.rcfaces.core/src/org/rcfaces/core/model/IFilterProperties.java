/*
 * $Id: IFilterProperties.java,v 1.3 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.io.Serializable;
import java.util.Map;

import javax.faces.component.StateHolder;

import org.rcfaces.core.internal.renderkit.IProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/01/11 15:46:58 $
 */
public interface IFilterProperties extends IProperties, Serializable,
        StateHolder {

    Object put(Serializable propertyName, Object value);

    Object remove(Serializable propertyName);

    String[] listNames();

    void clear();

    boolean isEmpty();

    int size();

    void putAll(Map<Serializable, Object> map);

    Map<Serializable, Object> toMap();
}
