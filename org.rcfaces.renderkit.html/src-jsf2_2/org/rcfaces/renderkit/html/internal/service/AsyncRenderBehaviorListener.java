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
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class AsyncRenderBehaviorListener extends AsyncRenderService
		implements AjaxBehaviorListener, Serializable {

	
	private static final long serialVersionUID = 718202827154436984L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "asyncRender.request";

	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		UIComponentBase component = (UIComponentBase) event.getComponent();
		String keyEntryId = component.getClientId();
		
		try  {
			processAsyncRender(facesContext, component, keyEntryId);
		} finally {
			facesContext.responseComplete();
		}
	}

	public static void addAjaxBehavior(UIComponentBase component,
			FacesContext context) {

		Map<String, List<ClientBehavior>> behaviorsByName = component
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
				.addAjaxBehaviorListener(new AsyncRenderBehaviorListener());
		component.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
				clientBehavior);
	}

}
