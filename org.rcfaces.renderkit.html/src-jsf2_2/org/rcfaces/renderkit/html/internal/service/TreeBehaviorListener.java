/*
 * $Id: TreeBehaviorListener.java,v 1.1 2014/02/05 16:07:15 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class TreeBehaviorListener extends TreeService implements
        AjaxBehaviorListener, Serializable {


	private static final long serialVersionUID = 2449831604158282595L;

	private static final Log LOG = LogFactory
            .getLog(TreeBehaviorListener.class);

    private static final String BEHAVIOR_SERVICE_EVENT_NAME = "tree.request";

    public void processAjaxBehavior(AjaxBehaviorEvent event)
            throws AbortProcessingException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        TreeComponent treeComponent = (TreeComponent) event.getComponent();
        String treeId = treeComponent.getClientId();

        Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();

        String waitingId = (String) parameters.get("waitingId");
        if (waitingId == null) {
            sendJsError(facesContext, treeId, INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not find 'waitingId' parameter.", null);
            return;
        }
        
        try {
        	 processTreeRefresh(facesContext, treeComponent, treeId, waitingId);
		} finally {
			facesContext.responseComplete();
		}
        
       
    }

	public static void addTreeRefreshBehavior(TreeComponent treeComponent, FacesContext context) {
        Map<String, List<ClientBehavior>> behaviorsByName = treeComponent
                .getClientBehaviors();

        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }
        
        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        clientBehavior.addAjaxBehaviorListener(new TreeBehaviorListener());
        clientBehavior.setImmediate(true);
        
        treeComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);

    }

}
