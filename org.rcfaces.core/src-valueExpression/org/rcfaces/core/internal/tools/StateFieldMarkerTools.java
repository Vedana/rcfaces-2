/*
 * $Id: StateFieldMarkerTools.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot
 * @version $Revision: 1.2 $
 */
public class StateFieldMarkerTools {
    

    private static final Log LOG = LogFactory
            .getLog(StateFieldMarkerTools.class);

    private static final String STATE_FIELD_MARKER_PROPERTY = "org.rcfaces.core.internal.STATE_FIELD_MARKER";

    public static String getStateFieldMarker(FacesContext facesContext) {
        Map applicationMap = facesContext.getExternalContext()
                .getApplicationMap();

        String stateFieldMarker = (String) applicationMap
                .get(STATE_FIELD_MARKER_PROPERTY);
        if (stateFieldMarker != null) {
            return stateFieldMarker;
        }

        synchronized (STATE_FIELD_MARKER_PROPERTY) {
            stateFieldMarker = (String) applicationMap
                    .get(STATE_FIELD_MARKER_PROPERTY);
            if (stateFieldMarker != null) {
                return stateFieldMarker;
            }

            stateFieldMarker = getStateMarker(facesContext);

            applicationMap.put(STATE_FIELD_MARKER_PROPERTY, stateFieldMarker);
        }

        LOG.info("Save state field marker is '" + stateFieldMarker + "'.");
        return stateFieldMarker;
    }

    private static String getStateMarker(FacesContext facesContext) {

        Map map = facesContext.getExternalContext().getInitParameterMap();
        String stateMarker = (String) map
                .get(Constants.SAVE_STATE_FIELD_MARKER_PARAMETER);
        if (stateMarker != null) {
            LOG
                    .debug("Save state marker is defined into application init parameters.");
            return stateMarker;
        }

        try {
            stateMarker = System
                    .getProperty(Constants.SAVE_STATE_FIELD_MARKER_SYSTEM_PARAMETER);
            if (stateMarker != null) {
                LOG
                        .debug("Save state marker is defined into system parameters.");

                return stateMarker;
            }

        } catch (Throwable th) {
            LOG.debug("Search into System properties.", th);
        }

        StringWriter writer = new StringWriter(512);
        ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        try {
            ResponseWriter responseWriter = oldResponseWriter
                    .cloneWithWriter(writer);
            facesContext.setResponseWriter(responseWriter);

            facesContext.getApplication().getViewHandler().writeState(
                    facesContext);

            String marker = writer.toString();

            if (LOG.isTraceEnabled()) {
                LOG.trace("Marker=" + marker);
            }

            return marker;

        } catch (IOException e) {
            LOG.info("Can not get state marker.", e);

            return "";

        } finally {
            facesContext.setResponseWriter(oldResponseWriter);
        }
    }

    public static String getStateValue(FacesContext facesContext) throws IOException {

        StringWriter writer = new StringWriter(512);
        ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        try {
            ResponseWriter responseWriter;
            if (oldResponseWriter != null) {
                responseWriter = oldResponseWriter.cloneWithWriter(writer);

            } else {
                responseWriter = facesContext.getRenderKit()
                        .createResponseWriter(writer, null, "UTF-8");

            }
            facesContext.setResponseWriter(responseWriter);

            StateManager stateManager = facesContext.getApplication()
                    .getStateManager();
            stateManager.writeState(facesContext, stateManager
                    .saveView(facesContext));

            String state = writer.toString();

            if (LOG.isTraceEnabled()) {
                LOG.trace("State=" + state);
            }

            return state;

        } catch (IOException e) {
            LOG.debug("Can not get value of serialized state of view.", e);

            throw e;

        } finally {
            if (oldResponseWriter != null) {
                facesContext.setResponseWriter(oldResponseWriter);
            }
        }
    }

}
