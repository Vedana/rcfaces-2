package org.rcfaces.renderkit.html.internal.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.rcfaces.core.component.ImageComponent;
import org.rcfaces.core.component.KeyEntryComponent;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class ImageBehaviorListener extends ImageService
		implements AjaxBehaviorListener, Serializable {

	private static final long serialVersionUID = 5336495415795828145L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "image.request";

	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ImageComponent imageComponent = (ImageComponent) event.getComponent();
		String keyEntryId = imageComponent.getClientId();

		try {
			 processImageService(facesContext, imageComponent, keyEntryId);
		} finally {
			facesContext.responseComplete();
		}
		
		
	}

	public static void addAjaxBehavior(ImageComponent imageComponent,
			FacesContext context) {

		Map<String, List<ClientBehavior>> behaviorsByName = imageComponent
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
				.addAjaxBehaviorListener(new ImageBehaviorListener());
		imageComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
				clientBehavior);
	}

}
