/*
 * $Id: IClientDataItem.java,v 1.2 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
 */
public interface IClientDataItem {
    Map<String, String> getClientDataMap();

    boolean isClientDataEmpty();
}
