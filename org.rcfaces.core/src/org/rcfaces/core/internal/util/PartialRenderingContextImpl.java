/*
 * $Id: PartialRenderingContextImpl.java,v 1.1 2011/04/12 09:25:20 oeuillot Exp $
 */
package org.rcfaces.core.internal.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.partialRendering.IPartialRenderingContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:20 $
 */
public class PartialRenderingContextImpl implements IPartialRenderingContext {

    private final FacesContext facesContext;

    private final Set registredComponents = new HashSet();

    private final Map updateRegistredComponents = new HashMap();

    public PartialRenderingContextImpl(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public void register(UIComponent component) {
        String clientId = component.getClientId(facesContext);

        registredComponents.add(clientId);

        updateRegistredComponents.remove(clientId);
    }

    public void update(UIComponent component, String property, Map parameters) {
        String clientId = component.getClientId(facesContext);

        if (registredComponents.contains(clientId)) {
            return;
        }

        Map updates = (Map) updateRegistredComponents.get(clientId);
        if (updates == null) {
            updates = new HashMap();
            updateRegistredComponents.put(clientId, updates);
        }

        updates.put(property, parameters);
    }

    public Set listRegistred() {
        return registredComponents;
    }

    public Map listUpdates() {
        return updateRegistredComponents;
    }
}
