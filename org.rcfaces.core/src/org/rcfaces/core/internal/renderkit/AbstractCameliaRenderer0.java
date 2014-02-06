/*
 * $Id: AbstractCameliaRenderer0.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IPartialRenderingCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.designer.IDesignerEngine;
import org.rcfaces.core.internal.tools.AsyncModeTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.lang.IAdaptable;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public abstract class AbstractCameliaRenderer0 extends Renderer implements
        IDefaultUnlockedPropertiesRenderer {

    private static final Log LOG = LogFactory
            .getLog(AbstractCameliaRenderer0.class);

    private static final boolean LOG_DEBUG = LOG.isDebugEnabled();

    private static final String HIDE_CHILDREN_PROPERTY = "camelia.ASYNC_TREE_MODE";

    private static final String COMPONENT_HIDDEN = "camelia.COMPONENT_HIDDEN";

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final Serializable defaultUnlockedProperties[];

    protected AbstractCameliaRenderer0() {
        Set<Serializable> unlockedProperties = new HashSet<Serializable>();
        addUnlockProperties(unlockedProperties);

        if (unlockedProperties.isEmpty() == false) {
            defaultUnlockedProperties = unlockedProperties
                    .toArray(new Serializable[unlockedProperties.size()]);

        } else {
            defaultUnlockedProperties = EMPTY_STRING_ARRAY;
        }
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
    }

    public Serializable[] getDefaultUnlockedProperties(FacesContext facesContext,
            UIComponent component) {
        return defaultUnlockedProperties;
    }

    @Override
    public final void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {

        if (LOG_DEBUG) {
            LOG.debug("Encode begin START '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }

        super.encodeBegin(context, component);

        IRenderContext renderContext = getRenderContext(context);

        String clientId = renderContext.getComponentClientId(component);

        renderContext.pushComponent(component, clientId);

        IComponentWriter writer = renderContext.getComponentWriter();

        /*
         * if (component instanceof IVisibilityCapability) {
         * IComponentRenderContext componentRenderContext = writer
         * .getComponentRenderContext();
         * 
         * if (componentRenderContext.containsAttribute(COMPONENT_HIDDEN) ==
         * false) { if (Boolean.FALSE.equals(((IVisibilityCapability) component)
         * .getVisibleState())) {
         * 
         * // Visibilit� PHANTOM
         * 
         * componentRenderContext.setAttribute(COMPONENT_HIDDEN, component); } }
         * }
         */

        try {
            encodeBegin(writer);

        } catch (RuntimeException e) {
            LOG.error("Encode begin of component '" + clientId
                    + "' throws an exception.", e);

            throw new WriterException(null, e, component);
        }

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        if (LOG_DEBUG) {
            LOG.debug("Encode begin END '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }
    }

    protected abstract void encodeBegin(IComponentWriter writer)
            throws WriterException;

    protected abstract IRenderContext getRenderContext(FacesContext context);

    protected void hideChildren(IComponentRenderContext componentRenderContext) {
        componentRenderContext.setAttribute(HIDE_CHILDREN_PROPERTY,
                Boolean.TRUE);
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {
        if ((this instanceof IAsyncRenderer) == false) {

            if (LOG_DEBUG) {
                LOG.debug("Encode children of '"
                        + component.getClientId(facesContext) + "'");
            }

            super.encodeChildren(facesContext, component);
            return;
        }

        if (LOG_DEBUG) {
            LOG.debug("Encode children of '"
                    + component.getClientId(facesContext) + "' IASync Renderer");
        }

        IRenderContext renderContext = getRenderContext(facesContext);

        IComponentWriter componentWriter = renderContext.getComponentWriter();

        IComponentRenderContext componentRenderContext = componentWriter
                .getComponentRenderContext();

        if (componentRenderContext.containsAttribute(HIDE_CHILDREN_PROPERTY)) {
            if (LOG_DEBUG) {
                LOG.debug("Encode END children of '"
                        + component.getClientId(facesContext)
                        + "'  HIDE_CHILDREN");
            }
            return;
        }

        super.encodeChildren(facesContext, component);

        if (LOG_DEBUG) {
            LOG.debug("Encode END children of '"
                    + component.getClientId(facesContext) + "'");
        }
    }

    @Override
    public boolean getRendersChildren() {
        if ((this instanceof IAsyncRenderer) == false) {
            return false;
        }

        if (RcfacesContext.isJSF1_2() || RcfacesContext.isJSF2_0()) {
            // En jsf 1.2 nous sommes forcement en parcours d'arbre
            return true;
        }

        // Jsf 1.1 : on doit distinguer d'un parcours par TAG ou par
        // programmation

        if (AsyncModeTools.isTagProcessor(null)) {
			// Nous sommes en mode TAG, c'est le tag qui détourne le flux.
            // NON => pas forcement, pas en mode tree !
            return false;
        }

        // Nous sommes en mode de rendu par parcours d'arbre ...
        return true;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {

        if (LOG_DEBUG) {
            LOG.debug("Encode end START '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }

        IRenderContext renderContext = getRenderContext(context);

        IComponentWriter writer = renderContext.getComponentWriter();

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        renderContext.encodeEnd(writer);

        try {
            encodeEnd(writer);

        } catch (RuntimeException e) {

            String clientId = renderContext.getComponentClientId(component);

            LOG.error("Encode end of component '" + clientId
                    + "' throws an exception.", e);

            throw new WriterException(null, e, component);
        }

        super.encodeEnd(context, component);

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        /*
         * if (component instanceof IVisibilityCapability) {
         * IComponentRenderContext componentRenderContext = writer
         * .getComponentRenderContext();
         * 
         * if (componentRenderContext.getAttribute(COMPONENT_HIDDEN) ==
         * component) {
         * componentRenderContext.removeAttribute(COMPONENT_HIDDEN); } }
         */

        if (component instanceof IPartialRenderingCapability) {
            IPartialRenderingCapability partialRenderingCapability = (IPartialRenderingCapability) component;

            if (partialRenderingCapability.isPartialRendering()) {

            }
        }

        renderContext.popComponent(component);

        if (LOG_DEBUG) {
            LOG.debug("Encode end END '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
    }

    protected abstract IRequestContext getRequestContext(FacesContext context);

    @Override
    public void decode(FacesContext context, UIComponent component) {

        if (LOG_DEBUG) {
            LOG.debug("Decode START '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }

        IRequestContext requestContext = getRequestContext(context);

        String requestComponentId = getRequestComponentId(requestContext,
                component);

        IComponentData componentData = requestContext.getComponentData(
                component, requestComponentId, this);

        decode(requestContext, component, componentData);

        if (LOG_DEBUG) {
            LOG.debug("Decode END '" + component.getClientId(context)
                    + "' rendererType=" + component.getRendererType());
        }
    }

    protected String getRequestComponentId(IRequestContext requestContext,
            UIComponent component) {
        return requestContext.getComponentId(component);
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
    }

    protected void decodeEvent(IRequestContext context, UIComponent component,
            IEventData eventData) {
    }

    public void decodeChildren(FacesContext context, UIComponent component) {

        if (LOG_DEBUG) {
            LOG.debug("Decode chidlren START '"
                    + component.getClientId(context) + "'");
        }

        for (Iterator<UIComponent> children = component.getFacetsAndChildren(); children
                .hasNext();) {
            UIComponent child = children.next();

            decodeChild(context, component, child);
        }

        if (LOG_DEBUG) {
            LOG.debug("Decode chidlren END '" + component.getClientId(context)
                    + "'");
        }
    }

    public void decodeChild(FacesContext context, UIComponent parent,
            UIComponent child) {
        child.processDecodes(context);
    }

    public void decodeEnd(FacesContext context, UIComponent component) {
    }

    public boolean getDecodesChildren() {
        return false;
    }

    protected String convertValue(FacesContext facesContext,
            ValueHolder valueHolder) {
        return ValuesTools.valueToString(valueHolder, facesContext);
    }

    protected String convertValue(FacesContext facesContext,
            UIComponent component, Object value) {
        return ValuesTools.valueToString(value, component, facesContext);
    }

    protected <T> T getAdapter(Class<T> adapter, Object object) {
        return getAdapter(adapter, object, this);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAdapter(Class<T> adapter, Object object,
            Object params) {

        if (object == null) {
            throw new NullPointerException("Object is null !");
        }

        if (object instanceof IAdaptable) {
            return ((IAdaptable) object).getAdapter(adapter, params);
        }

        if (adapter.isAssignableFrom(object.getClass())) {
            return (T) object;
        }

        return null;
    }

    @Override
    public Object getConvertedValue(FacesContext context,
            UIComponent component, Object submittedValue)
            throws ConverterException {

        return ValuesTools.convertStringToValue(context, component,
                submittedValue, true);
    }

    protected static final Renderer getRenderer(FacesContext facesContext,
            UIComponent component) {
        String rendererType = component.getRendererType();
        if (rendererType == null) {
            LOG.error("Invalid renderType for component id="
                    + component.getId() + " component=" + component);
            return null;
        }

        RenderKit renderKit = facesContext.getRenderKit();

        if (renderKit == null) {
            LOG.error("No renderKit associated to renderKitId='"
                    + facesContext.getViewRoot().getRenderKitId() + "'.");

            return null;
        }

        Renderer renderer = renderKit.getRenderer(component.getFamily(),
                rendererType);

        if (LOG_DEBUG) {
            LOG.debug("getRenderer(id='" + component.getId() + " family='"
                    + component.getFamily() + "' rendererType='" + rendererType
                    + "' class='" + component.getClass().getName()
                    + "') for renderKitId='"
                    + facesContext.getViewRoot().getRenderKitId() + "' => "
                    + renderer);
        }

        return renderer;
    }

    protected Object getValue(UIComponent component) {
        return ValuesTools.getValue(component);
    }

    protected boolean isComponentVisible(
            IComponentRenderContext componentRenderContext) {
        return componentRenderContext.containsAttribute(COMPONENT_HIDDEN) == false;
    }

    protected final void designerBeginChildren(IComponentWriter writer,
            String facetName) throws WriterException {

        IDesignerEngine designerEngine = writer.getComponentRenderContext()
                .getRenderContext().getProcessContext().getDesignerEngine();

        if (designerEngine == null) {
            return;
        }

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        designerEngine.beginChildren(writer.getComponentRenderContext()
                .getComponent(), facetName, writer);
    }

    protected final void designerEndChildren(IComponentWriter writer,
            String facetName) throws WriterException {
        IDesignerEngine designerEngine = writer.getComponentRenderContext()
                .getRenderContext().getProcessContext().getDesignerEngine();

        if (designerEngine == null) {
            return;
        }

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        designerEngine.endChildren(writer.getComponentRenderContext()
                .getComponent(), facetName, writer);
    }

    protected final void designerEditableZone(IComponentWriter writer,
            String propertyName) throws WriterException {

        IDesignerEngine designerEngine = writer.getComponentRenderContext()
                .getRenderContext().getProcessContext().getDesignerEngine();

        if (designerEngine == null) {
            return;
        }

        if (writer instanceof ISgmlWriter) {
            ((ISgmlWriter) writer).endComponent();
        }

        designerEngine.editableZone(writer.getComponentRenderContext()
                .getComponent(), propertyName, writer);
    }
}