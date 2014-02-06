/*
 * $Id: ParameterDecoderPhaseListener.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.phase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
public class ParameterDecoderPhaseListener implements PhaseListener {

    private static final Log LOG = LogFactory
            .getLog(ParameterDecoderPhaseListener.class);

    private static final long serialVersionUID = 3496726043805184521L;

    private static final String RCFACES_UTF8_PARAMETERS_PARAMETER = "__rcfaces_utf8";

    private static final String REQUEST_CHARSET = "UTF-8";

    public void afterPhase(PhaseEvent event) {

    }

    public void beforePhase(PhaseEvent event) {
        ExternalContext externalContext = event.getFacesContext()
                .getExternalContext();

        Map parameters = externalContext.getRequestParameterMap();

        String parameter = (String) parameters
                .get(RCFACES_UTF8_PARAMETERS_PARAMETER);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Parameter='" + parameter + "'");
        }

        if (parameter == null) {
            return;
        }

        Map<String, Object> requestMap = externalContext.getRequestMap();

        StringTokenizer st = new StringTokenizer(parameter, ",");
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            int idx = token.indexOf('=');
            try {
                String key = URLDecoder.decode(token.substring(0, idx),
                        REQUEST_CHARSET);
                String value = URLDecoder.decode(token.substring(idx + 1),
                        REQUEST_CHARSET);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Set parameter Token='" + parameter
                            + "' => key='" + key + "' value='" + value + "'");
                }

                requestMap.put(key, value);

            } catch (UnsupportedEncodingException ex) {
                LOG.error("Can not decode '" + token + "'", ex);
            }
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}
