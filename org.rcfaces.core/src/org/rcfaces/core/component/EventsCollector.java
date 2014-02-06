/*
 * $Id: EventsCollector.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.component.CameliaComponents;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class EventsCollector extends UIComponentBase {

    private static final Log LOG = LogFactory.getLog(EventsCollector.class);

    private static final String EVENTS_LIST_PROPERTY_NAME = "org.rcfaces.EVENTS_LIST";

    @Override
    public String getFamily() {
        return CameliaComponents.FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
    }

    @Override
    public void queueEvent(FacesEvent event) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Queue event '" + event + "'");
        }

        List<FacesEvent> events = getEventsList(null);

        events.add(event);

        super.queueEvent(event);
    }

    public static List<FacesEvent> getEventsList(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Map<String, Object> requestMap = facesContext.getExternalContext()
                .getRequestMap();

        List<FacesEvent> list = (List<FacesEvent>) requestMap
                .get(EVENTS_LIST_PROPERTY_NAME);
        if (list == null) {
            list = new LinkedList<FacesEvent>();
            requestMap.put(EVENTS_LIST_PROPERTY_NAME, list);
        }

        return list;
    }
}
