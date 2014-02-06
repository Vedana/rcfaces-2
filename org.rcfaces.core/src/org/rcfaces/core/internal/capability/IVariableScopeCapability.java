/*
 * $Id: IVariableScopeCapability.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;


/**
 * A couple string-binding specifying the name of a variable representing a
 * shortcut to a binding. ex: if scopeVar "bat" is associated to scopeValue
 * "bean.attribute1.attribute2" then the use of "bat.attribute3" will be
 * equivalente to "bean.attribute1.attribute2.attribute3"
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface IVariableScopeCapability {

    /**
     * Returns a string value specifying the name of a variable representing a
     * shortcut to a binding.
     * 
     * @return variable name
     */
    String getScopeVar();

    /**
     * Sets a string value specifying the name of a variable representing a
     * shortcut to a binding.
     * 
     * @param scopeVar
     *            variable name
     */
    void setScopeVar(String scopeVar);

    /**
     * Returns a value binding associated to a variable representing a shortcut.
     * 
     * @return value binding
     */
    Object getScopeValue();

    /**
     * Sets a value binding associated to a variable representing a shortcut.
     * 
     * @param valueBinding
     *            value binding
     */
    void setScopeValue(Object valueBinding);
    

    /**
     * Returns <code>true</code> if the value is stored when the view is serialized.
     * 
     * @return <code>true</code> if the value is saved.
     */
    boolean isScopeSaveValue();

    /**
     * Sets if the value must be stored when the view is serialized.
     * 
     * @param saveValue
     */
    void setScopeSaveValue(boolean saveValue);
}
