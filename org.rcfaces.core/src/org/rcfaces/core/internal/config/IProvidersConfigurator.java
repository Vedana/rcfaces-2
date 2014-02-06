/*
 * $Id: IProvidersConfigurator.java,v 1.1 2011/04/12 09:25:42 oeuillot Exp $
 */
package org.rcfaces.core.internal.config;

import org.apache.commons.digester.Digester;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:42 $
 */
public interface IProvidersConfigurator {
    void parseConfiguration(Digester digester);
}
