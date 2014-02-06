/*
 * $Id: ClientValidatorContext.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IParameter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public class ClientValidatorContext implements IClientValidatorContext {
    

    private static final Log LOG = LogFactory
            .getLog(ClientValidatorContext.class);

    private final IComponentRenderContext componentRenderContext;

    private final IParameter[] parameters;

    private String inputValue;

    private String lastErrorDetail;

    private Severity lastErrorSeverity;

    private String lastErrorSummary;

    private String outputValue;

    private Map<String, Object> attributes;

    public ClientValidatorContext(
            IComponentRenderContext componentRenderContext,
            IParameter parameters[], String value) {
        this.componentRenderContext = componentRenderContext;
        this.parameters = parameters;
        this.inputValue = value;
        this.outputValue = value;
    }

    public IComponentRenderContext getComponentRenderContext() {
        return componentRenderContext;
    }

    public FacesContext getFacesContext() {
        return componentRenderContext.getFacesContext();
    }

    public String getInput() {
        return inputValue;
    }

    public String getLastErrorDetail() {
        return lastErrorDetail;
    }

    public Severity getLastErrorSeverity() {
        return lastErrorSeverity;
    }

    public String getLastErrorSummary() {
        return lastErrorSummary;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public IParameter[] getParameters() {
        return parameters;
    }

    public void setInputValue(String inputValue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set input value new='" + inputValue + "' old='"
                    + this.inputValue + "'.");
        }
        this.inputValue = inputValue;
    }

    public void setLastError(String summary, String detail, Severity severity) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set last error summary='" + summary + "' detail='"
                    + detail + "' severity='" + severity + "'.");
        }

        this.lastErrorSeverity = severity;
        this.lastErrorSummary = summary;
        this.lastErrorDetail = detail;
    }

    public void setOutputValue(String outputValue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set output value new='" + outputValue + "' old='"
                    + this.outputValue + "'.");
        }
        this.outputValue = outputValue;
    }

    public Object getAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(key);
    }

    public boolean containsAttribute(String key) {
        if (attributes == null) {
            return false;
        }

        return attributes.containsKey(key);
    }

    public Object setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();

        }

        return attributes.put(key, value);
    }

    public Object removeAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return attributes.remove(key);
    }

}
