/*
 * $Id: ServiceBehaviorListener.java,v 1.1 2014/02/05 16:07:15 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ServiceComponent;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:07:15 $
 */
public class ServiceBehaviorListener extends ClientService implements
        AjaxBehaviorListener, Serializable {

    private static final long serialVersionUID = 8226953467643041454L;

    private static final Log LOG = LogFactory
            .getLog(ServiceBehaviorListener.class);

    private static final String BEHAVIOR_SERVICE_EVENT_NAME = "service";

    public ServiceBehaviorListener() {

    }

    public void processAjaxBehavior(AjaxBehaviorEvent event)
            throws AbortProcessingException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServiceComponent serviceComponent = (ServiceComponent) event.getComponent();
        
        try {
			processService(facesContext, serviceComponent);
		} catch (IOException e) {
			LOG.error("Call of service '" + BEHAVIOR_SERVICE_EVENT_NAME
                    + "' throw an IO exception !", e);
		}finally {
			facesContext.responseComplete();
		}

    }
   

	public static void addAjaxBehavior(ServiceComponent serviceComponent, FacesContext context) {

        Map<String, List<ClientBehavior>> behaviorsByName = serviceComponent
                .getClientBehaviors();

        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }

        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        
        clientBehavior.addAjaxBehaviorListener(new ServiceBehaviorListener());
        clientBehavior.setImmediate(true);
        
        serviceComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);
       
    }
}