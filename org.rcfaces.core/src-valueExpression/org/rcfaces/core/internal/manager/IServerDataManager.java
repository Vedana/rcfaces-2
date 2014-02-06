/*
 * $Id: IServerDataManager.java,v 1.1 2011/04/12 09:25:44 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.manager;

import javax.el.ValueExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:44 $
 */
public interface IServerDataManager {
    Object setServerData(String name, Object data);

    void setServerData(String name, ValueExpression binding);
}
