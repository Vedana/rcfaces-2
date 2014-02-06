/*
 * $Id: AbstractJavaScriptRenderer.java,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.component.capability.IToolTipIdCapability;
import org.rcfaces.core.component.iterator.IToolTipIterator;
import org.rcfaces.core.internal.capability.IRCFacesComponent;
import org.rcfaces.core.internal.capability.IToolTipComponent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository.IClass;
import org.rcfaces.renderkit.html.internal.util.ListenerTools;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
public abstract class AbstractJavaScriptRenderer extends
        AbstractJavaScriptRenderer0 implements IJavaScriptComponentRenderer {

    private static final Log LOG = LogFactory
            .getLog(AbstractJavaScriptRenderer.class);

    // private static final String JAVASCRIPT_INITIALIZED =
    // "camelia.html.javascript.initialized";

    // private static final boolean FORCE_JAVASCRIPT_WRITER = false;

    // private static final String FIRST_COMPONENT = "camelia.component.first";

    // private static final String LAZY_COMPONENTS = "camelia.component.lazy";

    public static final String LAZY_INIT_TAG = "init";

    // private static final String INIT_BY_NAME = "javascript.InitByName";

    private static final boolean ENCODE_EVENT_ATTRIBUTE_ON_COLLECTOR_MODE = true;

    private static final String CONTAINS_TOOLTIP_PROPERTY = "org.rcfaces.renderkit.CONTAINS_TOOLTIP";

    /*
     * protected void setInitializeByName(IComponentRenderContext renderContext)
     * { renderContext.setAttribute(INIT_BY_NAME, Boolean.TRUE); }
     */
    public final void initializeJavaScript(IJavaScriptWriter writer)
            throws WriterException {

        IJavaScriptRenderContext javaScriptRenderContext = writer
                .getJavaScriptRenderContext();

        javaScriptRenderContext.initializeJavaScriptDocument(writer);
    }

    public void initializePendingComponents(IJavaScriptWriter writer)
            throws WriterException {

        IJavaScriptRenderContext javascriptRenderContext = writer
                .getJavaScriptRenderContext();

        javascriptRenderContext.initializePendingComponents(writer);
    }

    public void initializeJavaScriptComponent(IJavaScriptWriter writer)
            throws WriterException {

        initializePendingComponents(writer);

        String javaScriptClassName = getJavaScriptClassName();
        if (javaScriptClassName == null) {
            return;
        }

        IHtmlComponentRenderContext componentRenderContext = writer
                .getHtmlComponentRenderContext();

        componentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext()
                .initializeJavaScriptComponent(writer);
    }

    public void releaseJavaScript(IJavaScriptWriter writer)
            throws WriterException {
        IHtmlComponentRenderContext componentRenderContext = writer
                .getHtmlComponentRenderContext();

        IHtmlRenderContext renderContext = (IHtmlRenderContext) componentRenderContext
                .getRenderContext();

        IJavaScriptRenderContext javascriptRenderContext = renderContext
                .getJavaScriptRenderContext();

        boolean sendCompleteComponent = sendCompleteComponent(componentRenderContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Release Javascript javascriptInitialized="
                    + javascriptRenderContext.isInitialized() + " writer.open="
                    + writer.isOpened() + "  canUseLazyTag="
                    + renderContext.canUseLazyTag() + " sendComplete="
                    + sendCompleteComponent);
        }

        javascriptRenderContext.releaseComponentJavaScript(writer,
                sendCompleteComponent, this);
    }

    /**
     * Generation of 'f_completeComponent' javascript call.
     * 
     * @param htmlComponentContext
     * @return If the 'f_completeComponent' javascript call might be generated.
     */
    protected abstract boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext);

    protected String getJavaScriptClassName() {
        return null;
    }

    protected final IHtmlWriter writeCoreAttributes(IHtmlWriter writer)
            throws WriterException {
        IHtmlWriter w = super.writeCoreAttributes(writer);

        String javascriptClass = getJavaScriptClassName();
        if (javascriptClass != null) {
            w.writeAttributeNS("class", javascriptClass);
        }

        return w;
    }

    public final IHtmlWriter writeJavaScriptAttributes(IHtmlWriter writer)
            throws WriterException {
        // Les evenements ....
        if (encodeEventsInAttributes(writer) == false) {
            return writer;
        }

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        if (componentRenderContext.getRenderContext().getProcessContext()
                .isDesignerMode()) {
            return writer;
        }

        UIComponent component = componentRenderContext.getComponent();

        // On recherche les ActionListeners
        Map<String, FacesListener[]> listenersByType = ListenerTools
                .getListenersByType(ListenerTools.ATTRIBUTE_NAME_SPACE,
                        component);

        if (hasComponentAction(component)) {
            String listenerType = getActionEventName(ListenerTools.ATTRIBUTE_NAME_SPACE);
            if (listenerType == null) {
                throw new FacesException(
                        "Unknown listenerType for action attribute.");
            }

            if (listenersByType.containsKey(listenerType) == false) {
                listenersByType = Collections.singletonMap(listenerType,
                        EventsRenderer.getSubmitJavaScriptListeners());
            }
        }

        if (listenersByType.isEmpty() == false) {
            StringAppender sa = new StringAppender(128);

            appendAttributeEventForm(sa, writer, listenersByType);

            if (sa.length() > 0) {
                writer.writeAttributeNS("events", sa.toString());
            }

            if (listenersByType.containsKey(ListenerTools.ATTRIBUTE_NAME_SPACE
                    .getInitEventName())) {
                writer.getJavaScriptEnableMode().enableOnInit();
            }
        }

        return writer;
    }

    protected boolean encodeEventsInAttributes(IHtmlWriter writer) {
        if (writer.getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {
            return true;
        }

        return ENCODE_EVENT_ATTRIBUTE_ON_COLLECTOR_MODE;

    }

    protected String getActionEventName(INameSpace nameSpace) {
        return null;
    }

    protected static final void appendAttributeEventForm(StringAppender sa,
            IHtmlWriter htmlWriter, Map<String, FacesListener[]> listenersByType) {
        IRenderContext renderContext = htmlWriter.getComponentRenderContext()
                .getRenderContext();

        IJavaScriptEnableMode javaScriptEnableMode = htmlWriter
                .getJavaScriptEnableMode();

        for (Map.Entry<String, FacesListener[]> entry : listenersByType
                .entrySet()) {
            String listenerType = entry.getKey();

            FacesListener listeners[] = entry.getValue();

            boolean submitSupport = true;
            if (ListenerTools.ATTRIBUTE_NAME_SPACE.getValidationEventName()
                    .equals(listenerType)) {
                submitSupport = false;
            }

            EventsRenderer.encodeAttributeEventListeners(renderContext, sa,
                    listenerType, listeners, submitSupport,
                    javaScriptEnableMode);
        }
    }

    protected void encodeJavaScript(IJavaScriptWriter writer)
            throws WriterException {
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        super.encodeEnd(writer);

        encodeEndJavaScript((IHtmlWriter) writer);
    }

    protected void encodeEndJavaScript(IHtmlWriter writer)
            throws WriterException {
        IHtmlRenderContext htmlRenderContext = writer
                .getHtmlComponentRenderContext().getHtmlRenderContext();
        UIComponent component = htmlRenderContext.getComponent();

        IJavaScriptWriter js = htmlRenderContext.getJavaScriptRenderContext()
                .removeJavaScriptWriter(writer);
        if (js != null) {
            // Le Javascript writer a �t� referm�, on ignore dans ce cas ...

            js = null;
        }

        boolean enableJavascript = false;

        boolean hasAction = false;
        Map<String, FacesListener[]> listenersByType = null;
        if (encodeEventsInAttributes(writer) == false
                && htmlRenderContext.getProcessContext().isDesignerMode() == false) {
            // On recherche l'attribut Action
            hasAction = hasComponentAction(component);

            // On recherche les ActionListeners
            listenersByType = ListenerTools.getListenersByType(
                    ListenerTools.JAVASCRIPT_NAME_SPACE, component);
            if (listenersByType.size() > 0 || hasAction) {
                enableJavascript = true;
            }
        }

        IJavaScriptRenderContext javascriptRenderContext = htmlRenderContext
                .getJavaScriptRenderContext();
        javascriptRenderContext.computeRequires(writer, this);
        if (javascriptRenderContext.isRequiresPending()) {
            // Rien on peut utiliser le tag v:init !
            enableJavascript = true;
        }

        boolean javaScriptStubForced = javascriptRenderContext
                .isJavaScriptStubForced();
        if (js == null) {
            if (enableJavascript == false) {
                JavaScriptEnableModeImpl javaScriptEnableMode = (JavaScriptEnableModeImpl) writer
                        .getJavaScriptEnableMode();

                enableJavascript = (javaScriptEnableMode.getMode() > 0);
            }

            if (enableJavascript == false && javaScriptStubForced) {
                IRCFacesComponent cameliaParent = null;

                for (UIComponent parent = component.getParent(); parent != null; parent = parent
                        .getParent()) {

                    if (parent instanceof IRCFacesComponent) {
                        cameliaParent = (IRCFacesComponent) parent;
                        break;
                    }
                }

                if (cameliaParent != null) {
                    javaScriptStubForced = false;

                } else if (javascriptRenderContext
                        .isJavaScriptRendererDeclaredLazy(writer) == false) {
                    enableJavascript = true;
                }
            }

            if (enableJavascript) {
                js = htmlRenderContext.getJavaScriptRenderContext()
                        .getJavaScriptWriter(writer, this);
            }
        }

        if (js != null) {
            if (javaScriptStubForced) {
                js.ensureInitialization();

                javascriptRenderContext.clearJavaScriptStubForced();
            }

            try {
                encodeJavaScript(js);

                if ((listenersByType != null && listenersByType.isEmpty() == false)
                        || hasAction) {

                    String actionListenerType = null;
                    if (hasAction) {
                        actionListenerType = getActionEventName(ListenerTools.JAVASCRIPT_NAME_SPACE);
                        if (actionListenerType == null) {
                            throw new FacesException(
                                    "Unknown listenerType for action attribute.");
                        }
                    }

                    EventsRenderer.encodeEventListeners(js,
                            js.getComponentVarName(), listenersByType,
                            actionListenerType);
                }

                if (hasComponenDecoratorSupport()) {
                    IComponentDecorator componentDecorator = getComponentDecorator(writer
                            .getComponentRenderContext());
                    if (componentDecorator != null) {
                        componentDecorator.encodeJavaScript(js);
                    }
                }
                
            } finally {
                js.end();
            }
        }
    }

    protected IHtmlWriter writeAccessKey(IHtmlWriter writer,
            IAccessKeyCapability accessKeyCapability) throws WriterException {
        if (useHtmlAccessKeyAttribute()) {
            return super.writeAccessKey(writer, accessKeyCapability);
        }

        String ak = accessKeyCapability.getAccessKey();

        if (ak != null && ak.length() > 0) {
            writer.writeAttributeNS("accessKey", ak);
            writer.getJavaScriptEnableMode().enableOnAccessKey();
        }

        return writer;
    }

    protected boolean useHtmlAccessKeyAttribute() {
        return false;
    }

    // protected final IHtmlWriter writeHelp(IHtmlWriter writer,
    // IHelpCapability helpComponent) {
    // if ((helpComponent.getHelpURL() != null)
    // || (helpComponent.getHelpMessage() != null)) {
    // // writer.enableJavaScript(); // ????
    // }
    //
    // return writer;
    // }

    protected final void declareLazyJavaScriptRenderer(IHtmlWriter writer) {

        writer.getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext()
                .declareLazyJavaScriptRenderer(writer);
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        String className = getJavaScriptClassName();
        if (className != null) {
            javaScriptRenderContext.appendRequiredClass(className, null);
        }

        if (writer.getComponentRenderContext().containsAttribute(
                CONTAINS_TOOLTIP_PROPERTY)) {
            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.COMPONENT, "toolTip");

        }
    }

    protected void markContainsTooltip(IComponentRenderContext renderContext) {
        renderContext.setAttribute(CONTAINS_TOOLTIP_PROPERTY, Boolean.TRUE);
    }

    public String getResourceBundleValue(IHtmlWriter htmlWriter, String key) {

        return getResourceBundleValue(htmlWriter, getJavaScriptClassName(), key);
    }

    public String getResourceBundleValue(IHtmlWriter htmlWriter,
            String refClassName, String key) {

        IJavaScriptRenderContext javaScriptRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext();

        return getResourceBundleValue(javaScriptRenderContext, refClassName,
                htmlWriter.getComponentRenderContext().getRenderContext()
                        .getProcessContext().getUserLocale(), key);
    }

    public static String getResourceBundleValue(
            IJavaScriptRenderContext javaScriptRenderContext,
            String jsClassName, Locale locale, String key) {

        IClass jsClass = javaScriptRenderContext.getRepository()
                .getClassByName(jsClassName);

        if (jsClass == null) {
            return null;
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                jsClass.getResourceBundleName(), locale);

        try {
            return resourceBundle.getString(key);

        } catch (MissingResourceException ex) {
            LOG.debug(ex);

            return null;
        }
    }

    protected final void writeFirstTooltipClientId(IHtmlWriter htmlWriter)
            throws WriterException {

        IComponentRenderContext componentContext = htmlWriter
                .getComponentRenderContext();

        UIComponent component = componentContext.getComponent();

        String tooltipClientId = null;

        if (component instanceof IToolTipIdCapability) {
            String tooltipId = ((IToolTipIdCapability) component)
                    .getToolTipId();

            if (tooltipId != null) {
                tooltipClientId = componentContext.getRenderContext()
                        .computeBrotherComponentClientId(component, tooltipId);
            }
        }

        if (tooltipClientId == null) {
            if (component instanceof IToolTipComponent) {
                IToolTipIterator iterator = ((IToolTipComponent) component)
                        .listToolTips();

                if (iterator.count() > 0) {
                    ToolTipComponent tooltipComponent = iterator.next();

                    FacesContext facesContext = componentContext
                            .getFacesContext();

                    tooltipClientId = tooltipComponent
                            .getClientId(facesContext);
                }

            }
        }

        if (tooltipClientId != null) {
            htmlWriter.writeAttribute("v:toolTipId", tooltipClientId);

            markContainsTooltip(htmlWriter.getComponentRenderContext());
        }
    }
}
