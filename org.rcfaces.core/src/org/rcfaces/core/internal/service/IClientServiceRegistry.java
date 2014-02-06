/*
 * $Id: IClientServiceRegistry.java,v 1.1 2011/04/12 09:25:45 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.service;

import org.rcfaces.core.component.ServiceComponent;
import org.rcfaces.core.progressMonitor.IProgressMonitor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:45 $
 */
public interface IClientServiceRegistry {

    int SYNC_MODE = 1;

    int ASYNC_MODE = 2;

    IClientService getClientServiceById(String requestId);

    IClientService createClientService(String requestId,
            ServiceComponent component, Object parameter, int syncMode)
            throws ClientServiceException;

    void startClientService(IClientService clientService);

    Object waitClientService(IClientService clientService,
            IProgressMonitor progressMonitor);

    void releaseClientService(IClientService clientService);
}
