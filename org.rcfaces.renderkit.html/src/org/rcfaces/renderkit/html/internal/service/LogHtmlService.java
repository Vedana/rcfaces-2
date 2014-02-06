/*
 * $Id: LogHtmlService.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.service;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletResponse;

import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.service.log.LogService;
import org.rcfaces.renderkit.html.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class LogHtmlService extends LogService {
    

    private static final String SERVICE_ID = Constants.getPackagePrefix()
            + ".Log";

    private static final String LOG_SERVICE_VERSION = "1.0.0";

    public void initialize(FacesContext facesContext) {
    }

    public static LogService getInstance(FacesContext facesContext) {

        IServicesRegistry serviceRegistry = RcfacesContext.getInstance(
                facesContext).getServicesRegistry();
        if (serviceRegistry == null) {
            // Designer mode
            return null;
        }

        return (LogService) serviceRegistry.getService(facesContext,
                RenderKitFactory.HTML_BASIC_RENDER_KIT, SERVICE_ID);
    }

    public void service(FacesContext facesContext, String commandId) {
        Map parameters = facesContext.getExternalContext()
                .getRequestParameterMap();

        UIViewRoot viewRoot = facesContext.getViewRoot();

        ILogger logger = getLogger();

        for (int i = 0;; i++) {
            String name = (String) parameters.get("name" + i);
            if (name == null) {
                break;
            }
            String message = (String) parameters.get("message" + i);

            ClientException clientException = null;
            String exception = (String) parameters.get("exception" + i);
            if (exception != null) {
                clientException = new ClientException(viewRoot.getId(),
                        exception);
            }

            int iLevel = -1;
            String level = (String) parameters.get("level" + i);
            if (level != null) {
                iLevel = Integer.parseInt(level);
            }

            long lDate = 0;
            String date = (String) parameters.get("date" + i);
            if (date != null) {
                lDate = Long.parseLong(date);
            }

            logger.logException(facesContext, viewRoot, name, lDate, message,
                    iLevel, clientException);
        }

        ServletResponse response = (ServletResponse) facesContext
                .getExternalContext().getResponse();

        AbstractHtmlService.setNoCache(response);
        AbstractHtmlService.setCameliaResponse(response, LOG_SERVICE_VERSION);

        facesContext.responseComplete();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
     */
    public static final class ClientException extends Exception {
        

        private static final long serialVersionUID = 6346621862119596920L;

        private final String viewId;

        ClientException(String viewId, String exception) {
            super(exception);

            this.viewId = viewId;
        }

        public final String getViewId() {
            return viewId;
        }
    }
}
