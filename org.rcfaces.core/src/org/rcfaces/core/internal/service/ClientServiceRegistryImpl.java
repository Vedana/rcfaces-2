/*
 * $Id: ClientServiceRegistryImpl.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ServiceComponent;
import org.rcfaces.core.event.IServiceEventListener;
import org.rcfaces.core.event.ServiceEvent;
import org.rcfaces.core.lang.ApplicationException;
import org.rcfaces.core.progressMonitor.IProgressMonitor;
import org.rcfaces.core.progressMonitor.SubProgressMonitor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class ClientServiceRegistryImpl implements IClientServiceRegistry {

    private static final Log LOG = LogFactory
            .getLog(ClientServiceRegistryImpl.class);

    private final Map<String, IClientService> clientServicesByRequestId = new HashMap<String, IClientService>(
            32);

    public IClientService getClientServiceById(String requestId) {
        return clientServicesByRequestId.get(requestId);
    }

    public IClientService createClientService(String requestId,
            ServiceComponent component, Object parameter, int syncMode)
            throws ClientServiceException {

        IClientService clientService = new ClientServiceImpl(requestId,
                component, parameter,
                syncMode == IClientServiceRegistry.ASYNC_MODE);

        return clientService;
    }

    public void startClientService(IClientService clientService) {
    }

    public Object waitClientService(IClientService clientService,
            IProgressMonitor progressMonitor) {
        ClientServiceImpl clientServiceImpl = (ClientServiceImpl) clientService;

        ServiceComponent serviceComponent = clientServiceImpl.popComponent();

        FacesListener fls[] = serviceComponent.listServiceEventListeners();

        ClientServiceEventReturnValue event = new ClientServiceEventReturnValue(
                serviceComponent, clientServiceImpl.popParameter(),
                progressMonitor, fls.length);

        Object returnValue = null;
        for (int i = 0; i < fls.length; i++) {
            IServiceEventListener serviceEventListener = (IServiceEventListener) fls[i];

            event.beginListener(i);

            serviceEventListener.processServiceEvent(event);

            event.endListener(i);

            ApplicationException applicationException = event
                    .getApplicationException();
            if (applicationException != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Throw application exception.",
                            applicationException);
                }

                throw applicationException;
            }

            returnValue = event.getReturnValue();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Return value of event ='" + returnValue + "'.");
            }

            if (returnValue != null) {
                break;
            }
        }

        event.listenersDone();

        return returnValue;
    }

    public void releaseClientService(IClientService clientService) {
        clientServicesByRequestId.remove(clientService.getClientServiceId());
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    private static class ClientServiceImpl implements IClientService {
        private static final String REVISION = "$Revision: 1.4 $";

        private String clientServiceId;

        private int status;

        private int progress;

        private int errorCode;

        private String errorMessage;

        private Object parameter;

        private boolean asyncMode;

        private ServiceComponent component;

        ClientServiceImpl(String requestId, ServiceComponent component,
                Object parameter, boolean asyncMode) {
            this.clientServiceId = requestId;
            this.parameter = parameter;
            this.asyncMode = asyncMode;
            this.component = component;
        }

        public String getClientServiceId() {
            return clientServiceId;
        }

        public int getStatus() {
            return status;
        }

        public int getProgress() {
            return progress;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public final Object popParameter() {
            Object parameter = this.parameter;
            this.parameter = null;

            return parameter;
        }

        public final boolean isAsyncMode() {
            return asyncMode;
        }

        public final ServiceComponent popComponent() {
            ServiceComponent serviceComponent = this.component;
            this.component = null;

            return serviceComponent;
        }

        public void cancel() {
            // TODO Auto-generated method stub

        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    private static final class ClientServiceEventReturnValue extends
            ServiceEvent implements IEventReturnValue,
            IApplicationExceptionCapability {

        private static final long serialVersionUID = 8740362936333831513L;

        private final int nbListeners;

        private int listenerIndex = 0;

        private Object returnValue;

        private IProgressMonitor progressMonitor;

        private ApplicationException applicationException;

        public ClientServiceEventReturnValue(UIComponent component,
                Object data, IProgressMonitor progressMonitor, int nbListeners) {
            super(component, data);

            if (progressMonitor != null) {
                progressMonitor = new SubProgressMonitor(progressMonitor,
                        nbListeners);
            }
            this.progressMonitor = progressMonitor;
            this.nbListeners = nbListeners;
        }

        public void listenersDone() {
            if (progressMonitor == null) {
                return;
            }

            progressMonitor.done();
        }

        public void beginListener(int i) {
            listenerIndex = i;
        }

        public void endListener(int i) {
            if (progressMonitor == null) {
                return;
            }

            getProgressMonitor().done();

            resetProgressMonitor();
        }

        public void setReturnValue(Object ret) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Set return value to '" + ret + "'.");
            }

            this.returnValue = ret;
        }

        public Object getReturnValue() {
            return returnValue;
        }

        public ApplicationException getApplicationException() {
            return applicationException;
        }

        public void setApplicationException(
                ApplicationException applicationException) {
            this.applicationException = applicationException;
        }

        @Override
        protected IProgressMonitor createProgressMonitor() {
            if (progressMonitor == null) {
                return super.createProgressMonitor();
            }

            return new SubProgressMonitor(progressMonitor, 1);
        }

    }
}
