/*
 * $Id: AbstractClientService.java,v 1.3 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/01/11 15:47:01 $
 */
public abstract class AbstractClientService extends AbstractService {
     private static final Log LOG = LogFactory
            .getLog(AbstractClientService.class);

    private static final String CLIENT_SERVICE_REGISTRY_PROPERTY = "camelia.client.service.Repository";

    protected IClientServiceRegistry getClientServiceRegistry(
            FacesContext facesContext) {

        Map<String, Object> applicationMap = facesContext.getExternalContext()
                .getApplicationMap();
        synchronized (AbstractClientService.class) {
            IClientServiceRegistry clientServiceRegistry = (IClientServiceRegistry) applicationMap
                    .get(CLIENT_SERVICE_REGISTRY_PROPERTY);
            if (clientServiceRegistry != null) {
                return clientServiceRegistry;
            }

            clientServiceRegistry = createClientServiceRegistry();

            applicationMap.put(CLIENT_SERVICE_REGISTRY_PROPERTY,
                    clientServiceRegistry);

            return clientServiceRegistry;
        }
    }

    protected IClientServiceRegistry createClientServiceRegistry() {
        return new ClientServiceRegistryImpl();
    }

}
