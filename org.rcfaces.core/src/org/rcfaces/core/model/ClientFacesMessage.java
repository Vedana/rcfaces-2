/*
 * $Id: ClientFacesMessage.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.rcfaces.core.component.capability.IClientDataCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class ClientFacesMessage extends FacesMessage implements
        IClientDataCapability {
    private static final long serialVersionUID = 4456702905941225305L;

    private Map<String, String> clientDataMap;

    public ClientFacesMessage() {
        super();
    }

    public ClientFacesMessage(Severity severity, String summary, String detail) {
        super(severity, summary, detail);
    }

    public ClientFacesMessage(String summary, String detail) {
        super(summary, detail);
    }

    public ClientFacesMessage(String summary) {
        super(summary);
    }

    public String getClientData(String name) {
        if (clientDataMap == null) {
            return null;
        }

        return clientDataMap.get(name);
    }

    public int getClientDataCount() {
        if (clientDataMap == null) {
            return 0;
        }

        return clientDataMap.size();
    }

    public Map<String, String> getClientDataMap() {
        if (clientDataMap == null) {
            return Collections.emptyMap();
        }
        return new HashMap<String, String>(clientDataMap);
    }

    public String[] listClientDataKeys() {
        if (clientDataMap == null || clientDataMap.isEmpty()) {
            return new String[] {};
        }

        Collection<String> keys = clientDataMap.keySet();

        return keys.toArray(new String[keys.size()]);
    }

    public String removeClientData(String name) {
        if (clientDataMap == null) {
            return null;
        }

        return clientDataMap.remove(name);
    }

    public String setClientData(String name, String data) {
        if (clientDataMap == null) {
            return null;
        }

        return clientDataMap.put(name, data);
    }

}
