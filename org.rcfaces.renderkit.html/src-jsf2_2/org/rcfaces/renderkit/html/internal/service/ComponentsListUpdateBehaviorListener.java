package org.rcfaces.renderkit.html.internal.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.rcfaces.core.component.ComponentsGridComponent;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class ComponentsListUpdateBehaviorListener extends ComponentsListService implements
		AjaxBehaviorListener, Serializable {

	private static final long serialVersionUID = 383406056983763146L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "componentsList.update";
	
	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		ComponentsListComponent listComponent = (ComponentsListComponent) event.getComponent();
		String listId = listComponent.getClientId();
		
		try {
			processListUpdate(facesContext, listComponent, listId);
		} finally {
			facesContext.responseComplete();
		}
	}
	
	
	public static void addAjaxBehavior(ComponentsListComponent listComponent, FacesContext context) {

        Map<String, List<ClientBehavior>> behaviorsByName = listComponent
                .getClientBehaviors();
                
        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }
       
        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        clientBehavior.setImmediate(true);
        
        clientBehavior.addAjaxBehaviorListener(new ComponentsListUpdateBehaviorListener());
        listComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);
    }
}