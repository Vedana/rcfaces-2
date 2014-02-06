/*
 * $Id: AbstractCameliaRenderer1.java,v 1.1 2014/02/05 16:05:51 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:51 $
 */
public abstract class AbstractCameliaRenderer1 extends AbstractCameliaRenderer0 {

    @Override
    public final void decode(FacesContext context, UIComponent component) {

        IRequestContext requestContext = getRequestContext(context);

        String requestComponentId = getRequestComponentId(requestContext,
                component);

        IComponentData componentData = requestContext.getComponentData(
                component, requestComponentId, this);

        if (component instanceof ClientBehaviorHolder 
        		&& context.getCurrentPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {  //on repasse ici dans la phase Invoke du service Ajax
            decodeBehaviors(requestContext, component, componentData);
        }

        decode(requestContext, component, componentData);
    }

    protected void decodeBehaviors(IRequestContext requestContext,
            UIComponent component, IComponentData componentData) {
    	
    	if(!(component instanceof ClientBehaviorHolder)) {
            return;
        }

        Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        if(behaviors.isEmpty()) {
            return;
        }

        Map<String, String> params = requestContext.getFacesContext().getExternalContext().getRequestParameterMap();
        String behaviorEvent = params.get("javax.faces.behavior.event");

        if(null != behaviorEvent) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);

            if(behaviorsForEvent != null && !behaviorsForEvent.isEmpty()) {
               String behaviorSource = params.get("javax.faces.source");
               String clientId = component.getClientId();

               if(behaviorSource != null && clientId.startsWith(behaviorSource)) {
                   for(ClientBehavior behavior: behaviorsForEvent) {
                       behavior.decode(requestContext.getFacesContext(), component);
                   }
               }
            }
        }
    	
    }

}
