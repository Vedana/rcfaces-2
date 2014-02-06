/*
 * $Id: IProvidersRegistry.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.internal.config;

import org.apache.commons.digester.Digester;
import org.rcfaces.core.provider.IProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IProvidersRegistry {

    IProvider getProvider(String id);

    Digester getConfigDigester();
}
