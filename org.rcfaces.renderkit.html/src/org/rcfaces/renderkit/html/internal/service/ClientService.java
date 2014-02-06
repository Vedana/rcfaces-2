/*
 * $Id: ClientService.java,v 1.5 2013/11/13 12:53:33 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ServiceComponent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.documentBuilder.IDocumentBuilderProvider;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.service.AbstractClientService;
import org.rcfaces.core.internal.service.ClientServiceException;
import org.rcfaces.core.internal.service.IClientService;
import org.rcfaces.core.internal.service.IClientServiceRegistry;
import org.rcfaces.core.lang.ApplicationException;
import org.rcfaces.core.progressMonitor.IProgressMonitor;
import org.rcfaces.renderkit.html.internal.HtmlProcessContextImpl;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlTools.ILocalizedComponent;
import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:33 $
 */
public class ClientService extends AbstractClientService {

    private static final Log LOG = LogFactory.getLog(ClientService.class);

    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";

    private static final String PARAMETER = "data";

    private static final String PARAMETER_TYPE = "type";

    private static final String SYNC_MODE = "syncMode";

    private static final String COMPONENT_ID = "X-Camelia-Component-Id";

    private static final String REQUEST_ID = "X-Camelia-Request-Id";

    private static final String CAMELIA_CONTENT_TYPE = "X-Camelia-Content-Type";

    private static final String CAMELIA_ERROR_CODE = "X-Camelia-Error-Code";

    private static final String CAMELIA_PROGRESS_MONITOR = "X-Camelia-ProgressMonitor";

    private static final String CLIENT_SERVICE_VERSION = "1.0.0";

    public void service(FacesContext facesContext, String commandId)
            throws IOException {

        try {
            if ("client.newService".equals(commandId)) {
                createOperation(facesContext);
                return;
            }

            if ("client.infoService".equals(commandId)) {
                infoClientService(facesContext);
                return;
            }

            AbstractHtmlService.sendJsError(facesContext, null,
                    AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not identify command '" + commandId + "'.", null);

        } catch (RuntimeException ex) {
            LOG.error("Catch runtime exception !", ex);

            throw ex;
        }
    }
    
    

    private void createOperation(FacesContext facesContext) throws IOException {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map requestHeader = externalContext.getRequestHeaderMap();

        String componentId = (String) requestHeader.get(COMPONENT_ID);
        if (componentId == null) {
            AbstractHtmlService.sendJsError(facesContext, null,
                    AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not get '" + COMPONENT_ID + "' parameter !", null);
            return;
        }
        
        ILocalizedComponent localizedComponent = HtmlTools.localizeComponent(
                facesContext, componentId);
        if (localizedComponent == null) {
            AbstractHtmlService.sendJsError(facesContext, componentId,
                    AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not get view '" + componentId + "' !", null);
            return;
        }

        UIComponent component = localizedComponent.getComponent();

        try {
        	if ((component instanceof ServiceComponent) == false) {
                AbstractHtmlService.sendJsError(facesContext, componentId,
                        AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                        "Component '" + componentId
                                + "' does not implement ServiceComponent !",
                        null);
                return;
            }

            ServiceComponent serviceComponent = (ServiceComponent) component;
            
            processService(facesContext, serviceComponent);

        } finally {
            localizedComponent.end();
        }
    }
    
    
    protected void processService(FacesContext facesContext,
			ServiceComponent serviceComponent) throws IOException {

    	 Map requestHeader = facesContext.getExternalContext().getRequestHeaderMap();

         String componentId = (String) requestHeader.get(COMPONENT_ID);
         
         UIViewRoot viewRoot = facesContext.getViewRoot();

         if (viewRoot.getChildCount() == 0) {
             AbstractHtmlService.sendJsError(facesContext, componentId,
                     AbstractHtmlService.SESSION_EXPIRED_SERVICE_ERROR,
                     "No view !", null);
             return;
         }
         
         String requestId = (String) requestHeader.get(REQUEST_ID);
         if (requestId == null) {
             AbstractHtmlService.sendJsError(facesContext, componentId,
                     AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                     "Can not get '" + REQUEST_ID + "' parameter !", null);
             return;
         }
    	
    	
        int syncMode = IClientServiceRegistry.SYNC_MODE;

        String syncModeAtt = (String) requestHeader.get(SYNC_MODE);
        if ("asynchronous".equals(syncModeAtt)) {
            syncMode = IClientServiceRegistry.ASYNC_MODE;
        }

        Object parameter = null;

        String progressMonitorValue = (String) requestHeader
                .get(CAMELIA_PROGRESS_MONITOR);

        boolean formParameters = true;
        String cameliaContentType = (String) requestHeader
                .get(CAMELIA_CONTENT_TYPE);
        if ("xml".equals(cameliaContentType)) {
            formParameters = false;

            // Deserialize le Document xml
            parameter = null; // <<< ICI
            progressMonitorValue = null;
        }

        // On a besoin du processContext pour "parser"/formater les dates
        // qui
        // proviennent du client !
        IProcessContext processContext = HtmlProcessContextImpl
                .getProcessContext(facesContext);

        if (formParameters) {
            Map request = facesContext.getExternalContext().getRequestParameterMap();

            String parameterType = (String) request.get(PARAMETER_TYPE);

            String parameterString = (String) request.get(PARAMETER);

            parameter = deserializeParameter(processContext,
                    serviceComponent, parameterType, parameterString);
        }

        IClientServiceRegistry operationsRegistry = getClientServiceRegistry(facesContext);

        IClientService clientService;
        try {
            clientService = operationsRegistry.createClientService(
                    requestId, serviceComponent, parameter, syncMode);

        } catch (ClientServiceException ex) {
            LOG.error("Can not create operation '" + requestId + "'.", ex);

            clientService = null;
        }

        IProgressMonitor progressMonitor = null;

        if (syncMode == IClientServiceRegistry.SYNC_MODE) {
            operationsRegistry.startClientService(clientService);

            Object ret;
            try {
                ret = operationsRegistry.waitClientService(clientService,
                        progressMonitor);

            } catch (ApplicationException ex) {
                sendClientError(facesContext, ex, processContext, serviceComponent);
                return;
            }

            sendResponse(facesContext, ret, progressMonitor,
                    processContext, serviceComponent);

            return;
        }

        sendOperationStatus(facesContext, clientService, true);
    	
      
	}

    private void sendClientError(FacesContext facesContext,
            ApplicationException ex, IProcessContext processContext,
            UIComponent component) {

        HttpServletResponse response = (HttpServletResponse) facesContext
                .getExternalContext().getResponse();

        AbstractHtmlService.setNoCache(response);

        response.setContentType("x-camelia/error; charset="
                + AbstractHtmlService.RESPONSE_CHARSET);

        AbstractHtmlService
                .setCameliaResponse(response, CLIENT_SERVICE_VERSION);

        if (ex.getErrorCode() != 0) {
            response.setHeader(CAMELIA_ERROR_CODE, String.valueOf(ex
                    .getErrorCode()));
        }

        if (ex.getErrorMessage() != null) {
            response.setHeader(CAMELIA_CONTENT_TYPE, "string");

            try {
                PrintWriter pw = response.getWriter();

                pw.print(ex.getErrorMessage());

            } catch (IOException ex2) {
                LOG.error("Can not send error !", ex2);
            }
        }

        facesContext.responseComplete();

    }

    protected void sendResponse(FacesContext facesContext, Object ret,
            IProgressMonitor progressMonitor, IProcessContext processContext,
            UIComponent component) throws IOException {
        HttpServletResponse response = (HttpServletResponse) facesContext
                .getExternalContext().getResponse();

        AbstractHtmlService.setNoCache(response);

        String buffer = null;
        String type = "string";
        String contenType = "text/plain";
        if (ret instanceof Document) {
            contenType = "text/xml";
            type = "xml";

        } else if (ret instanceof Map) {
            buffer = HtmlTools.encodeParametersFromMap((Map) ret, '&',
                    processContext, component);
            type = "object";

        } else if (ret == null) {
            type = "null";
            buffer = null;

        } else {
            buffer = String.valueOf(ret);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Prepare response: contentType='" + type + "' buffer='"
                    + buffer + "'");
        }

        response.setContentType(contenType + "; charset="
                + AbstractHtmlService.RESPONSE_CHARSET);
        response.setHeader(CAMELIA_CONTENT_TYPE, type);
        AbstractHtmlService
                .setCameliaResponse(response, CLIENT_SERVICE_VERSION);

        PrintWriter pw;
        try {
            pw = response.getWriter();

        } catch (IOException ex) {
            LOG.error("Can not open writer !", ex);

            throw ex;
        }

        try {
            if (buffer != null) {
                pw.print(buffer);

            } else if (type.equals("xml")) {
                // C'est un document ... on serialize !

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Serialize xml document into stream !");
                }

                RcfacesContext.getInstance(facesContext)
                        .getDocumentBuilderProvider().serialize(pw,
                                (Document) ret);
            }

        } catch (IOException ex) {
            LOG.error("Can not send response !", ex);

            throw ex;
        }

        try {
            pw.flush();

        } catch (RuntimeException ex) {
            LOG.error("Can not flush writer !", ex);
        }

        facesContext.responseComplete();
    }

    private Object deserializeParameter(IProcessContext processContext,
            UIComponent component, String type, String parameterString) {
        if ("xml".equals(type)) {
            IDocumentBuilderProvider documentBuilderProvider = RcfacesContext
                    .getInstance(processContext.getFacesContext())
                    .getDocumentBuilderProvider();

            try {
                Document document = documentBuilderProvider
                        .parse(new StringReader(parameterString));

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Deserialize XML type parameter '"
                            + parameterString + "' => " + document);
                }

                return document;

            } catch (IOException e) {
                LOG.error("Can not parse xml document from service !", e);

                throw new FacesException(
                        "Can not parse xml document from service !", e);
            }
        }

        if ("object".equals(type)) {
            Map ret = HtmlTools.decodeParametersToMap(processContext,
                    component, parameterString, "&", null);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Deserialize OBJECT type parameter '"
                        + parameterString + "' => " + ret);
            }

            return ret;
        }

        if ("null".equals(type)) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Deserialize NULL type parameter '" + parameterString
                        + "' => " + null);
            }

            return null;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Deserialize default type parameter '" + parameterString
                    + "' => " + parameterString);
        }

        return parameterString;
    }

    private void infoClientService(FacesContext facesContext) {
        Map request = facesContext.getExternalContext().getRequestMap();

        String requestId = (String) request.get(REQUEST_ID);
        if (requestId == null) {
            AbstractHtmlService.sendJsError(facesContext, null,
                    AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not get '" + REQUEST_ID + "' parameter !", null);
            return;
        }

        IClientServiceRegistry operationsRegistry = getClientServiceRegistry(facesContext);

        IClientService service = operationsRegistry
                .getClientServiceById(requestId);

        sendOperationStatus(facesContext, service, true);
    }

    protected void sendClientServiceStatus(FacesContext facesContext,
            IClientService operation, boolean sendResult) {
        int status = 0;
        int progress = -1;
        int errorCode = 0;

        if (operation != null) {
            status = operation.getStatus();
            progress = operation.getProgress();
            errorCode = operation.getErrorCode();
        }

        ServletResponse response = (ServletResponse) facesContext
                .getExternalContext().getResponse();

        AbstractHtmlService.setNoCache(response);

        response.setContentType(PLAIN_TEXT_MIME_TYPE + "; charset="
                + AbstractHtmlService.RESPONSE_CHARSET);

        try {
            PrintWriter pw = response.getWriter();

            pw.println("status=" + status);
            if (progress >= 0) {
                pw.println("progress=" + progress);
            }
            if (errorCode != 0) {
                pw.println("errorCode=" + errorCode);
            }

        } catch (IOException ex) {
            LOG.error("Can not write status '" + status + "' for operation '"
                    + operation + "'.", ex);
        }

        facesContext.responseComplete();
    }

    private void cancelOperation(FacesContext facesContext) {
        Map request = facesContext.getExternalContext().getRequestMap();

        String operationKey = (String) request.get(REQUEST_ID);
        if (operationKey == null) {
            AbstractHtmlService.sendJsError(facesContext, null,
                    AbstractHtmlService.INVALID_PARAMETER_SERVICE_ERROR,
                    "Can not get '" + REQUEST_ID + "' parameter !", null);
            return;
        }

        IClientServiceRegistry clientServiceRegistry = getClientServiceRegistry(facesContext);

        IClientService clientService = clientServiceRegistry
                .getClientServiceById(operationKey);
        if (clientService != null) {
            clientService.cancel();
        }

        sendOperationStatus(facesContext, clientService, false);
    }

    private void sendOperationStatus(FacesContext facesContext,
            IClientService operation, boolean b) {
        // TODO Auto-generated method stub

    }

    public void initialize(FacesContext facesContext) {
    }

}
