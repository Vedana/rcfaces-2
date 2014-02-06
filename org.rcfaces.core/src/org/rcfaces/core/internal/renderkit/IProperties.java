/*
 * $Id: IProperties.java,v 1.2 2011/10/11 09:29:09 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import java.io.Serializable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2011/10/11 09:29:09 $
 */
public interface IProperties {

    boolean containsKey(Serializable name);

    Object getProperty(Serializable name);

    String getStringProperty(Serializable name);

    String getStringProperty(Serializable name, String defaultValue);

    boolean getBoolProperty(Serializable name, boolean defaultValue);

    Boolean getBooleanProperty(Serializable name);

    int getIntProperty(Serializable name, int defaultValue);

    Number getNumberProperty(Serializable name);

}
