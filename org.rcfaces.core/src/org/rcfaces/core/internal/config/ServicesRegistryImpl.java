/*
 * $Id: ServicesRegistryImpl.java,v 1.3 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.config;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.service.IService;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.util.ClassLocator;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
 */
public class ServicesRegistryImpl extends AbstractRenderKitRegistryImpl
        implements Serializable, PhaseListener, IServicesRegistry {
    

    private static final long serialVersionUID = -2873554843764179473L;

    private static final Log LOG = LogFactory
            .getLog(ServicesRegistryImpl.class);

    private static final boolean TEST_SERVICE_WAIT = true;

    protected static final String CAMELIA_HEADER = "X-Camelia";

    private static final String SERVICE_WAIT_PROPERTY = "org.rcfaces.core.services.WAIT";

    private static final String SERVICE_PROPERTY = "org.rcfaces.core.config.SERVICE_REGISTRY";

    public ServicesRegistryImpl() {
    }

    @Override
    protected String getApplicationPropertyId() {
        return SERVICE_PROPERTY;
    }

    public IService getService(FacesContext facesContext, String renderKitId,
            String serviceId) {

        RenderKit renderKit = (RenderKit) getRenderKit(facesContext,
                renderKitId);
        if (renderKit == null) {
            throw new FacesException(
                    "Can not get the service repository associated to the renderKit '"
                            + facesContext.getViewRoot().getRenderKitId()
                            + "' !");
        }

        ServiceFacade service = renderKit.getServiceById(serviceId);
        if (service == null) {
            throw new FacesException("Service '" + serviceId
                    + "' is not defined !");
        }

        return service.getService(facesContext);
    }

    public void beforePhase(PhaseEvent event) {

        if (LOG.isDebugEnabled()) {
            FacesContext facesContext = event.getFacesContext();

            Map<String, String> headers = facesContext.getExternalContext()
                    .getRequestHeaderMap();
            String commandId = headers.get(CAMELIA_HEADER);

            LOG.debug("Before phase '" + event.getPhaseId() + "' viewId="
                    + facesContext.getViewRoot() + " commandId=" + commandId);
        }
    }

    public final PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void afterPhase(PhaseEvent event) {
    	
    	 if(RcfacesContext.isJSF2_0()) {
         	return;
         }
    	
        FacesContext facesContext = event.getFacesContext();

        Map<String, String> headers = facesContext.getExternalContext()
                .getRequestHeaderMap();
        String commandId = headers.get(CAMELIA_HEADER);

        if (LOG.isDebugEnabled()) {
            LOG.debug("After phase '" + event.getPhaseId() + "' viewId="
                    + facesContext.getViewRoot() + " commandId=" + commandId);
        }

        if (commandId == null) {
            return;
        }

        RenderKit renderKit = (RenderKit) getRenderKit(facesContext, null);
        if (renderKit == null) {
            throw new FacesException(
                    "Can not get the service repository associated to the renderKit '"
                            + facesContext.getViewRoot().getRenderKitId()
                            + "' !");
        }
        
       

        ServiceFacade facade = renderKit.getServiceByCommandId(commandId);
        if (facade == null) {
            LOG.error("Can not find command '" + commandId + "'.");

            throw new FacesException("Can not find service '" + commandId
                    + "'.");
        }

        IService service = facade.getService(facesContext);

        if (TEST_SERVICE_WAIT) {
            String wait = facesContext.getExternalContext().getInitParameter(
                    SERVICE_WAIT_PROPERTY);

            if (wait != null && wait.length() > 0) {

                int w = Integer.parseInt(wait);

                if (w > 0) {
                    synchronized (wait) {
                        LOG.debug("WAIT ...");
                        try {
                            wait.wait(w);

                        } catch (Exception ex) {
                            LOG.debug(ex);
                        }
                    }
                }
            }
        }

        try {
            service.service(facesContext, commandId);

        } catch (IOException ex) {
            LOG.error("Call of service '" + commandId
                    + "' throw an IO exception !", ex);

        } catch (RuntimeException ex) {
            LOG.error("Call of service '" + commandId
                    + "' throw an exception !", ex);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Service done viewId=" + facesContext.getViewRoot()
                    + " commandId=" + commandId);
        }
    }

    @Override
    protected AbstractRenderKitRegistryImpl.RenderKit createRenderKit() {
        return new RenderKit();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
     */
    public static final class ServiceFacade {
        

        private String className;

        private String id;

        private boolean unavailable;

        private IService service;

        public ServiceFacade() {
        }

        public final void setId(String id) {
            this.id = id;
        }

        public final void setClassName(String className) {
            this.className = className;
        }

        public synchronized IService getService(FacesContext facesContext) {
            if (service != null) {
                return service;
            }

            if (unavailable) {
                throw new FacesException("Service '" + id
                        + "' is unavailable !");
            }

            try {
                unavailable = true;

                Class< ? extends IService> clazz = ClassLocator.load(className,
                        this, facesContext, IService.class);

                service = clazz.newInstance();

                service.initialize(facesContext);

                unavailable = false;

                return service;

            } catch (Throwable th) {
                LOG.error("Can not start service '" + id + "'.", th);

                throw new FacesException("Can not start service '" + id + "'.",
                        th);
            }

        }

        public String getId() {
            return id;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
     */
    public static class RenderKit extends
            AbstractRenderKitRegistryImpl.RenderKit {
        

        private final Map<String, ServiceFacade> serviceFacadeByCommandId;

        private final Map<String, ServiceFacade> serviceFacadeByServiceId;

        public RenderKit() {
            serviceFacadeByCommandId = new HashMap<String, ServiceFacade>(32);

            serviceFacadeByServiceId = new HashMap<String, ServiceFacade>(16);
        }

        public ServiceFacade getServiceById(String serviceId) {
            return serviceFacadeByServiceId.get(serviceId);
        }

        public ServiceFacade getServiceByCommandId(String commandId) {
            return serviceFacadeByCommandId.get(commandId);
        }

        public void addService(ServiceFacade serviceFacade) {
            serviceFacadeByServiceId.put(serviceFacade.getId(), serviceFacade);
        }

        public void addCommand(String commandId, ServiceFacade serviceFacade) {
            serviceFacadeByCommandId.put(commandId, serviceFacade);
        }

    }

    public void configureRules(Digester digester) {

        digester.addRule("rcfaces-config/services/render-kit", new Rule() {
            

            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                String renderKitId = attributes.getValue("render-kit-id");

                RenderKit renderKit = (RenderKit) allocate(renderKitId);

                super.digester.push(renderKit);
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                super.digester.pop();
            }
        });

        digester.addObjectCreate("rcfaces-config/services/render-kit/service",
                ServiceFacade.class);
        digester.addSetProperties("rcfaces-config/services/render-kit/service",
                "id", "id");
        digester.addSetProperties("rcfaces-config/services/render-kit/service",
                "class", "className");
        digester.addRule("rcfaces-config/services/render-kit/service/command",
                new Rule() {
                    

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        ServiceFacade service = (ServiceFacade) super.digester
                                .peek();
                        RenderKit renderKit = (RenderKit) super.digester
                                .peek(1);

                        String commandId = attributes.getValue("id");

                        renderKit.addCommand(commandId, service);
                    }
                });
        digester.addSetNext("rcfaces-config/services/render-kit/service",
                "addService");
    }
}