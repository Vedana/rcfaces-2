/*
 * $Id: DefaultAdaptable.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.IAdapterManager;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class DefaultAdaptable implements IAdaptable {

    private static final Log LOG = LogFactory.getLog(DefaultAdaptable.class);

    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        IAdapterManager adapterManager = RcfacesContext.getCurrentInstance()
                .getAdapterManager();

        return adapterManager.getAdapter(this, adapter, parameter);
    }
}
