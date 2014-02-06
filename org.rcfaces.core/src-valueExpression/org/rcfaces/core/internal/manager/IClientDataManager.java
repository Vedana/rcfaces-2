/*
 * $Id: IClientDataManager.java,v 1.1 2011/04/12 09:25:44 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.manager;

import javax.el.ValueExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:44 $
 */
public interface IClientDataManager {
    String setClientData(String name, String value);

    void setClientData(String name, ValueExpression binding);
}
