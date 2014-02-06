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

import org.rcfaces.core.component.KeyEntryComponent;
import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class ItemsBehaviorListener extends ItemsService
		implements AjaxBehaviorListener, Serializable {

	private static final long serialVersionUID = 6876533049375175894L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "items.request";

	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		UIComponentBase component =  (UIComponentBase) event.getComponent();
		String componentId = component.getClientId();
		
		
		try {
			 processItemsRefresh(facesContext, (IFilterCapability) component, componentId);
		} finally {
			facesContext.responseComplete();
		}

		
	}

	

	public static void addAjaxBehavior(IFilterCapability filterComponent,
			FacesContext context) {

		Map<String, List<ClientBehavior>> behaviorsByName = ((UIComponentBase ) filterComponent)
				.getClientBehaviors();

		List<ClientBehavior> clientBehaviors = behaviorsByName
				.get(BEHAVIOR_SERVICE_EVENT_NAME);

		if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
			return;
		}

		RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context
				.getApplication().createBehavior(
						RCFacesAjaxBehavior.BEHAVIOR_ID);
		clientBehavior.setImmediate(true);

		clientBehavior
				.addAjaxBehaviorListener(new ItemsBehaviorListener());
		((UIComponentBase ) filterComponent).addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
				clientBehavior);
	}

}
