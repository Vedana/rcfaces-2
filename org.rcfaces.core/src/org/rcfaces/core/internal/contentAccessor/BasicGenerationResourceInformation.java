/*
 * $Id: BasicGenerationResourceInformation.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.version.HashCodeTools;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class BasicGenerationResourceInformation extends AbstractInformation
        implements IGenerationResourceInformation {

    private static final Log LOG = LogFactory
            .getLog(BasicGenerationResourceInformation.class);

    private static final String COMPONENT_CLIENT_ID_PROPERTY = "org.rcfaces.org.COMPONENT_CLIENT_ID_PROPERTY";

    private transient UIComponent component;

    private IFilterProperties filterProperties;

    private boolean processAtRequest;

    public BasicGenerationResourceInformation() {
    }

    public BasicGenerationResourceInformation(
            IComponentRenderContext componentRenderContext) {
        this(componentRenderContext.getComponent(), componentRenderContext
                .getComponentClientId());
    }

    public BasicGenerationResourceInformation(UIComponent component,
            String clientId) {
        setComponent(component, clientId);
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(IComponentRenderContext componentRenderContext) {
        setComponent(componentRenderContext.getComponent(),
                componentRenderContext.getComponentClientId());
    }

    public void setComponent(UIComponent component) {
        setComponent(component, (FacesContext) null);
    }

    public void setComponent(UIComponent component, FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        String clientId = null;
        if (facesContext != null) {
            clientId = component.getClientId(facesContext);
        }

        setComponent(component, clientId);
    }

    public void setComponent(UIComponent component, String clientId) {
        this.component = component;
        setComponentClientId(clientId);
    }

    public String getComponentClientId() {
        return (String) getAttribute(COMPONENT_CLIENT_ID_PROPERTY);
    }

    public void setComponentClientId(String componentClientId) {
        setAttribute(COMPONENT_CLIENT_ID_PROPERTY, componentClientId);
    }

    public final IFilterProperties getFilterProperties() {
        return filterProperties;
    }

    public final void setFilterProperties(IFilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    public void restoreState(FacesContext context, Object state) {
        Object states[] = (Object[]) state;

        super.restoreState(context, states[0]);

        filterProperties = (IFilterProperties) UIComponentBase
                .restoreAttachedState(context, states[1]);

        // Pas la peine de traiter processAtRequest !!
    }

    public Object saveState(FacesContext context) {
        Object states[] = new Object[2];

        states[0] = super.saveState(context);

        states[1] = UIComponentBase
                .saveAttachedState(context, filterProperties);

        // Pas la peine de traiter processAtRequest !!

        return states;
    }

    public final boolean isProcessAtRequest() {
        return processAtRequest;
    }

    public final void setProcessAtRequest(boolean processAtRequest) {
        this.processAtRequest = processAtRequest;
    }

    public String getResponseSuffix() {
        return (String) getAttribute(RESPONSE_URL_SUFFIX_PROPERTY);
    }

    public void setResponseSuffix(String suffix) {
        setAttribute(RESPONSE_URL_SUFFIX_PROPERTY, suffix);
    }

    public final String getResponseMimeType() {
        return (String) getAttribute(RESPONSE_MIME_TYPE_PROPERTY);
    }

    public final void setResponseMimeType(String contentType) {
        setAttribute(RESPONSE_MIME_TYPE_PROPERTY, contentType);
    }

    public void setResponseLastModified(long l) {
        setAttribute(RESPONSE_LAST_MODIFIED_PROPERTY, new Long(l));
    }

    public void setResponseLastModified(Date d) {
        setAttribute(RESPONSE_LAST_MODIFIED_PROPERTY, d);
    }

    public long getResponseLastModified() {
        Object l = getAttribute(RESPONSE_LAST_MODIFIED_PROPERTY);

        if (l instanceof Long) {
            return ((Long) l).longValue();
        }

        if (l instanceof Date) {
            return ((Date) l).getTime();
        }

        return -1;
    }

    public void setComputeResourceKeyFromGenerationInformation(boolean b) {
        setAttribute(COMPUTE_RESOURCE_KEY_FROM_GENERATION_INFORMATION,
                Boolean.valueOf(b));
    }

    public boolean getComputeResourceKeyFromGenerationInformation() {
        return Boolean.TRUE
                .equals(getAttribute(COMPUTE_RESOURCE_KEY_FROM_GENERATION_INFORMATION));
    }

    public static String generateResourceKeyFromGenerationInformation(
            IGenerationResourceInformation generationInformation) {

        StringAppender sa = new StringAppender(16000);
        ((IResourceKeyParticipant) generationInformation).participeKey(sa);

        return HashCodeTools.compute(null, "#resourceKey", sa, 0);
    }

}
