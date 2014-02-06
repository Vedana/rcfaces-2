package org.rcfaces.renderkit.html.internal.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.capability.IGridComponent;

public class AdditionalInformationBehaviorListener extends AdditionalInformationService implements
		AjaxBehaviorListener, Serializable {

	
	private static final long serialVersionUID = -7994774864444668499L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "grid.additionalInformation";
	
	  private static final Log LOG = LogFactory
	            .getLog(AdditionalInformationBehaviorListener.class);
	
	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		UIComponent gridComponent = event.getComponent();
		String componentId = gridComponent.getClientId();
		
		try {
			processGridRefresh(facesContext, gridComponent, componentId);
		} catch (IOException e) {
			LOG.error("Call of service '" + BEHAVIOR_SERVICE_EVENT_NAME
                    + "' throw an IO exception !", e);
		} finally {
			facesContext.responseComplete();
		}
		
	}
	
	public static void addAjaxBehavior(UIComponentBase gridComponent, FacesContext context) {

        Map<String, List<ClientBehavior>> behaviorsByName = gridComponent
                .getClientBehaviors();
                
        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }
       
        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        clientBehavior.setImmediate(true);
        
        clientBehavior.addAjaxBehaviorListener(new AdditionalInformationBehaviorListener());
        gridComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);
    }
}