package org.rcfaces.renderkit.html.internal.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.rcfaces.core.component.KeyEntryComponent;
import org.rcfaces.core.component.KeyLabelComponent;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class KeyLabelBehaviorListener extends KeyLabelService
		implements AjaxBehaviorListener, Serializable {

	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "keyLabel.request";

	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		KeyLabelComponent keyEntryComponent = (KeyLabelComponent) event.getComponent();
		String keyEntryId = keyEntryComponent.getClientId();

		
		 try {
			 processKeyLabelService(facesContext, keyEntryComponent, keyEntryId);
		} finally {
			facesContext.responseComplete();
		}
		
		
	}

	public static void addAjaxBehavior(KeyLabelComponent keyLabelComponent,
			FacesContext context) {

		Map<String, List<ClientBehavior>> behaviorsByName = keyLabelComponent
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
				.addAjaxBehaviorListener(new KeyLabelBehaviorListener());
		keyLabelComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
				clientBehavior);
	}

}
