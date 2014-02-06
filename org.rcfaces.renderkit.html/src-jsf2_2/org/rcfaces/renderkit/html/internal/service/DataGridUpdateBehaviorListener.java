package org.rcfaces.renderkit.html.internal.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.rcfaces.core.component.DataGridComponent;
import org.rcfaces.core.internal.behaviors.RCFacesAjaxBehavior;

public class DataGridUpdateBehaviorListener extends DataGridService implements
		AjaxBehaviorListener, Serializable {

	private static final long serialVersionUID = 4009487636338525316L;
	private static final String BEHAVIOR_SERVICE_EVENT_NAME = "dataGrid.update";
	
	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent event)
			throws AbortProcessingException {
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		DataGridComponent gridComponent = (DataGridComponent) event.getComponent();
		String dataGridId = gridComponent.getClientId();
		
		try {
			processDataGridRefresh(facesContext, gridComponent, dataGridId);
		} finally {
			facesContext.responseComplete();
		}
		
	}
	
	
	public static void addAjaxBehavior(DataGridComponent dataGridComponent, FacesContext context) {

        Map<String, List<ClientBehavior>> behaviorsByName = dataGridComponent
                .getClientBehaviors();
                
        List<ClientBehavior> clientBehaviors = behaviorsByName
                .get(BEHAVIOR_SERVICE_EVENT_NAME);
        
        if (clientBehaviors != null && clientBehaviors.isEmpty() == false) {
            return;
        }
       
        RCFacesAjaxBehavior clientBehavior = (RCFacesAjaxBehavior) context.getApplication()
        		.createBehavior(RCFacesAjaxBehavior.BEHAVIOR_ID);
        clientBehavior.setImmediate(true);
        
        clientBehavior.addAjaxBehaviorListener(new DataGridUpdateBehaviorListener());
        dataGridComponent.addClientBehavior(BEHAVIOR_SERVICE_EVENT_NAME,
                clientBehavior);
    }
}