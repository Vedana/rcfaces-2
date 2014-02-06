/*
 * $Id: ITransientAttributesManager.java,v 1.1 2011/04/12 09:25:50 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.manager;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:50 $
 */
public interface ITransientAttributesManager {
    Object getTransientAttribute(String name);

    Object setTransientAttribute(String name, Object value);
}
