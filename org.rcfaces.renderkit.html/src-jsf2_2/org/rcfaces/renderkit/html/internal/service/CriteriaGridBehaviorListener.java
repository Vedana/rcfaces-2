package org.rcfaces.renderkit.html.internal.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.rcfaces.core.component.ComponentsGridComponent;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class CriteriaGridBehaviorListener extends CriteriaGridService implements
		AjaxBehaviorListener, Serializable {

	private static final long serialVersionUID = 383406056983763146L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "criteria.request";
	
	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		UIComponentBase listComponent = (UIComponentBase) event.getComponent();
		String listId = listComponent.getClientId();
		
		try {
			ProcessCriteriaService(facesContext, (ICriteriaManagerCapability) listComponent, listId);
		} finally {
			facesContext.responseComplete();
		}
	}
	
	
	public static void addAjaxBehavior(ICriteriaManagerCapability criteriaManagerComponent, FacesContext context) {

        Map<String, List<ClientBehavior>> behaviorsByName = ((UIComponentBase )criteriaManagerComponent)
                .getClientBehaviors();
                
        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }
       
        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        clientBehavior.setImmediate(true);
        
        clientBehavior.addAjaxBehaviorListener(new CriteriaGridBehaviorListener());
        ((UIComponentBase )criteriaManagerComponent).addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);
    }
}