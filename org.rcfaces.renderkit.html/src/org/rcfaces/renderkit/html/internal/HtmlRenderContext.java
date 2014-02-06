/*
 * $Id: HtmlRenderContext.java,v 1.3 2013/12/11 10:19:48 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.manager.ITransientAttributesManager;
import org.rcfaces.core.internal.renderkit.AbstractRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IScriptRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.service.AbstractAsyncRenderService;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:19:48 $
 */
public class HtmlRenderContext extends AbstractRenderContext implements
        IHtmlRenderContext {
    private static final Log LOG = LogFactory.getLog(HtmlRenderContext.class);

    private static final String META_DATA_INITIALIZED_PROPERTY = "org.rcfaces.renderkit.html.META_DATA_INITIALIZED";

    protected static final String RENDER_CONTEXT_PROPERTY = "camelia.render.html.context";

    private static final String JAVASCRIPT_CONTEXT_PROPERTY = "camelia.html.javascript.context";

    private static final int INTERACTIVE_RENDER_COMPONENTS_INITIAL_SIZE = 4 * 3;

    private List<Object> interactiveRenderComponents;

    private UIComponent lastInteractiveRenderComponent;

    private String lastInteractiveRenderComponentClientId;

    private IJavaScriptRenderContext javaScriptRenderContext;

    private boolean noLazyTag = false;

    private boolean asyncRenderer = false;

    private IHtmlProcessContext htmlProcessContext;

    private String invalidBrowserURL;

    private boolean disabledContentMenu;

    private Set<String> clientMessageIdFilter;

    private boolean clientMessageIdFilterReadOnly;

    private boolean userAgentVary;

    private String waiRolesNS = null;

    private boolean clientValidation = true;

    private int void0;

    public void initialize(FacesContext facesContext) {
        super.initialize(facesContext);

        AbstractAsyncRenderService service = AbstractAsyncRenderService
                .getInstance(facesContext);
        if (service != null) {
            asyncRenderer = service.isAsyncRenderEnable();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialize htmlRenderContext asyncRenderer="
                    + asyncRenderer + ".");
        }

        htmlProcessContext = HtmlProcessContextImpl
                .getHtmlProcessContext(facesContext);
    }

    public IScriptRenderContext getScriptRenderContext() {
        return getJavaScriptRenderContext();
    }

    public final IJavaScriptRenderContext getJavaScriptRenderContext() {
        if (javaScriptRenderContext != null) {
            return javaScriptRenderContext;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map<String, Object> requestMap = facesContext.getExternalContext()
                .getRequestMap();

        javaScriptRenderContext = (IJavaScriptRenderContext) requestMap
                .get(JAVASCRIPT_CONTEXT_PROPERTY);
        if (javaScriptRenderContext != null) {
            return javaScriptRenderContext;
        }

        javaScriptRenderContext = allocateJavaScriptContext(facesContext);

        requestMap.put(JAVASCRIPT_CONTEXT_PROPERTY, javaScriptRenderContext);

        return javaScriptRenderContext;
    }

    protected IJavaScriptRenderContext allocateJavaScriptContext(
            FacesContext facesContext) {
        return new JavaScriptRenderContext(facesContext);
    }

    public void restoreState(FacesContext facesContext, Object state) {
        Object ret[] = (Object[]) state;
        if (ret == null) {
            return;
        }

        super.restoreState(facesContext, ret[0]);

        if (ret[1] != null) {
            IJavaScriptRenderContext javaScriptRenderContext = getJavaScriptRenderContext();
            javaScriptRenderContext.restoreState(facesContext, ret[1]);
        }

        waiRolesNS = (String) ret[2];
    }

    public Object saveState(FacesContext facesContext) {
        Object ret[] = new Object[3];

        ret[0] = super.saveState(facesContext);

        if (javaScriptRenderContext != null) {
            ret[1] = javaScriptRenderContext.saveState(facesContext);
        }

        ret[2] = waiRolesNS;

        return ret;
    }

    public void pushInteractiveRenderComponent(IHtmlWriter writer,
            IJavaScriptRenderContext newJavaScriptRenderContext)
            throws WriterException {
        if (interactiveRenderComponents == null) {
            interactiveRenderComponents = new ArrayList<Object>(
                    INTERACTIVE_RENDER_COMPONENTS_INITIAL_SIZE);
        }

        interactiveRenderComponents.add(lastInteractiveRenderComponent);
        interactiveRenderComponents.add(lastInteractiveRenderComponentClientId);
        interactiveRenderComponents.add(javaScriptRenderContext);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        lastInteractiveRenderComponent = componentRenderContext.getComponent();

        IJavaScriptRenderContext javaScriptRenderContext = getJavaScriptRenderContext();

        if (newJavaScriptRenderContext == null) {
            // On ne les change que si c'est un JavaScriptRenderContext imposé
            lastInteractiveRenderComponentClientId = componentRenderContext
                    .getComponentClientId();

            newJavaScriptRenderContext = javaScriptRenderContext.createChild();
        }

        javaScriptRenderContext.pushChild(newJavaScriptRenderContext, writer);

        this.javaScriptRenderContext = newJavaScriptRenderContext;
    }

    public void popInteractiveRenderComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        if (interactiveRenderComponents.isEmpty()) {
            throw new IllegalStateException(
                    "No more elements into interactive render components");
        }

        int pos = interactiveRenderComponents.size() - 3;

        lastInteractiveRenderComponent = (UIComponent) interactiveRenderComponents
                .remove(pos);
        lastInteractiveRenderComponentClientId = (String) interactiveRenderComponents
                .remove(pos);

        IJavaScriptRenderContext oldJavaScriptRenderContext = javaScriptRenderContext;
        javaScriptRenderContext = (IJavaScriptRenderContext) interactiveRenderComponents
                .remove(pos);

        oldJavaScriptRenderContext
                .popChild(javaScriptRenderContext, htmlWriter);
    }

    public UIComponent getCurrentInteractiveRenderComponent() {
        return lastInteractiveRenderComponent;
    }

    public String getCurrentInteractiveRenderComponentClientId() {
        return lastInteractiveRenderComponentClientId;
    }

    public void encodeEnd(IComponentWriter writer) throws WriterException {
        if (lastInteractiveRenderComponent == writer
                .getComponentRenderContext().getComponent()) {
            popInteractiveRenderComponent((IHtmlWriter) writer);
        }

        super.encodeEnd(writer);
    }

    public boolean canUseLazyTag() {
        if (noLazyTag) {
            return false;
        }

        return getCurrentInteractiveRenderComponent() == null;
    }

    public String getComponentClientId(UIComponent component) {
        if (htmlProcessContext.isFlatIdentifierEnabled()) {
            String id = component.getId();

            if (id == null || id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
                return component.getClientId(getFacesContext());
            }

            return id;
        }

        return component.getClientId(getFacesContext());
    }

    public String computeBrotherComponentClientId(UIComponent brotherComponent,
            String componentId) {
        if (htmlProcessContext.isFlatIdentifierEnabled()) {
            return componentId;
        }

        String brotherComponentId = brotherComponent
                .getClientId(getFacesContext());

        if (Constants.CLIENT_NAMING_SEPARATOR_SUPPORT) {
            String separatorChar = htmlProcessContext.getNamingSeparator();

            if (separatorChar != null) {
                int idx = brotherComponentId.lastIndexOf(separatorChar);
                for (; idx > 0; idx--) {
                    if (brotherComponentId.indexOf(separatorChar, idx - 1) != idx - 1) {
                        break;
                    }
                }

                return brotherComponentId.substring(0,
                        idx + separatorChar.length())
                        + componentId;
            }
        }

        int idx = brotherComponentId
                .lastIndexOf(NamingContainer.SEPARATOR_CHAR);
        if (idx < 0) {
            return componentId;
        }

        return brotherComponentId.substring(0, idx + 1) + componentId;
    }

    protected IComponentWriter createWriter(UIComponent component) {
        return new HtmlWriterImpl(this);
    }

    public IComponentWriter createWriter(UIComponent component,
            ResponseWriter responseWriter) {
        return new HtmlWriterImpl(this, responseWriter);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/12/11 10:19:48 $
     */
    protected static class HtmlWriterImpl extends AbstractHtmlWriter {

        private static final String ENABLE_JAVASCRIPT_PROPERTY = "camelia.html.javascript.enable";

        private JavaScriptEnableModeImpl enableJavaScriptMode;

        public HtmlWriterImpl(HtmlRenderContext context) {
            this(context, context.getFacesContext().getResponseWriter());
        }

        protected HtmlWriterImpl(HtmlRenderContext context,
                ResponseWriter responseWriter) {
            super(context, responseWriter);

            enableJavaScriptMode = (JavaScriptEnableModeImpl) ((ITransientAttributesManager) getComponent())
                    .getTransientAttribute(ENABLE_JAVASCRIPT_PROPERTY);
        }

        public IJavaScriptEnableMode getJavaScriptEnableMode() {
            if (enableJavaScriptMode != null) {
                return enableJavaScriptMode;
            }

            enableJavaScriptMode = new JavaScriptEnableModeImpl();

            ((ITransientAttributesManager) getComponent())
                    .setTransientAttribute(ENABLE_JAVASCRIPT_PROPERTY,
                            enableJavaScriptMode);

            return enableJavaScriptMode;
        }

        public void enableJavaScript() {
            getJavaScriptEnableMode().enableOnInit();
        }

    }

    public IHtmlProcessContext getHtmlProcessContext() {
        return htmlProcessContext;
    }

    public IProcessContext getProcessContext() {
        return htmlProcessContext;
    }

    public boolean isAsyncRenderEnable() {
        return asyncRenderer;
    }

    public String getInvalidBrowserURL() {
        return invalidBrowserURL;
    }

    public boolean isDisabledContextMenu() {
        return disabledContentMenu;
    }

    public void setDisabledContextMenu(boolean state) {
        this.disabledContentMenu = state;
    }

    public void setInvalidBrowserURL(String invalidBrowserURL) {
        this.invalidBrowserURL = invalidBrowserURL;
    }

    public Set getClientMessageIdFilters() {
        if (clientMessageIdFilter == null) {
            return Collections.EMPTY_SET;
        }

        return clientMessageIdFilter;
    }

    public void setClientMessageId(Set<String> ids) {
        clientMessageIdFilter = ids;
        clientMessageIdFilterReadOnly = true;
    }

    public void addClientMessageIds(Set ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        if (clientMessageIdFilter == null) {
            clientMessageIdFilter = new HashSet<String>(ids.size());

        } else if (clientMessageIdFilterReadOnly) {
            clientMessageIdFilter = new HashSet<String>(clientMessageIdFilter);
            clientMessageIdFilterReadOnly = false;
        }

        for (Iterator it = ids.iterator(); it.hasNext();) {
            String id = (String) it.next();

            if (ALL_CLIENT_MESSAGES.equals(id)) {
                clientMessageIdFilter.remove(NO_CLIENT_MESSAGES);
                return;
            }

            clientMessageIdFilter.add(id);
        }
    }

    public String getWaiRolesNS() {
        return waiRolesNS;
    }

    public void setWaiRolesNS(String waiRolesNS) {
        this.waiRolesNS = waiRolesNS;
    }

    public void setClientValidation(boolean clientValidation) {
        this.clientValidation = clientValidation;
    }

    public boolean isClientValidation() {
        return clientValidation;
    }

    public int getAsyncRenderMode(
            IAsyncRenderModeCapability asyncRenderModeCapability) {

        int mode = asyncRenderModeCapability.getAsyncRenderMode();

        if (RcfacesContext.isJSF1_2() == false && RcfacesContext.isJSF2_0() == false) {
            return mode;
        }

        if (mode == IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE) {
            mode = IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE;

            if (LOG.isDebugEnabled()) {
                LOG.debug("BUFFER_ASYNC_RENDER_MODE can not be used with JSF 1_2 (componentId='"
                        + ((UIComponent) asyncRenderModeCapability).getId()
                        + "'");
            }
        }

        return mode;
    }

    public static final IHtmlRenderContext restoreRenderContext(
            FacesContext facesContext, Object state, boolean noLazyTag) {
        HtmlRenderContext renderContext = (HtmlRenderContext) getRenderContext(facesContext);

        renderContext.restoreState(facesContext, state);

        if (noLazyTag) {
            renderContext.noLazyTag = true;
        }

        return renderContext;
    }

    static final IHtmlRenderContext getRenderContext(FacesContext context) {
        if (context == null) {
            context = FacesContext.getCurrentInstance();
        }

        Map<String, Object> requestMap = context.getExternalContext()
                .getRequestMap();

        IHtmlRenderContext renderContext = (IHtmlRenderContext) requestMap
                .get(RENDER_CONTEXT_PROPERTY);
        if (renderContext != null) {
            return renderContext;
        }

        renderContext = createRenderContext(context);
        requestMap.put(RENDER_CONTEXT_PROPERTY, renderContext);

        return renderContext;
    }

    static final IHtmlRenderContext createRenderContext(FacesContext context) {
        HtmlRenderContext hrc = new HtmlRenderContext();
        hrc.initialize(context);

        return hrc;
    }

    public static void setMetaDataInitialized(FacesContext facesContext) {
        facesContext.getExternalContext().getRequestMap()
                .put(META_DATA_INITIALIZED_PROPERTY, Boolean.TRUE);
    }

    public void setUserAgentVary(boolean userAgentVary) {
        this.userAgentVary = userAgentVary;
    }

    public boolean isUserAgentVary() {
        return userAgentVary;
    }

    public int allocateJavaScriptVoid0() {
        return void0++;
    }

}