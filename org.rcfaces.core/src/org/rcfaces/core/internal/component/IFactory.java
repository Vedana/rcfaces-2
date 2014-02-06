/*
 * $Id: IFactory.java,v 1.2 2013/12/11 10:17:38 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.component;

import java.util.List;
import java.util.Map;

import org.rcfaces.core.internal.capability.IComponentEngine;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/12/11 10:17:38 $
 */
public interface IFactory {

    String getName();

    <T> List<T> createList(int size);

    <T, U> Map<T, U> createMap(int size);

    IComponentEngine createComponentEngine();

    IPropertiesManager createPropertiesManager(IComponentEngine engine);

    IInitializationState createInitializationState();

    /*
     * IPropertiesAccessor createPropertiesAccessor(IComponentEngine engine);
     * 
     * 
     * IPropertiesAccessor restorePropertiesAccessor(FacesContext facesContext,
     * IComponentEngine engine, Object state);
     */
}
