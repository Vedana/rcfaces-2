/*
 * $Id: TooltipContainerRenderContext.java,v 1.1 2013/12/11 10:19:48 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.component.iterator.IToolTipIterator;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.capability.IToolTipComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/12/11 10:19:48 $
 */
public class TooltipContainerRenderContext {

    private static final Log LOG = LogFactory
            .getLog(TooltipContainerRenderContext.class);

    private Set<ToolTipComponent> gridToolTips; // #head, #body, #row +
                                                // (v:toolTipId) + #cell

    protected final UIComponent component;

    public TooltipContainerRenderContext(UIComponent component) {
        this.component = component;

        if (component instanceof IToolTipComponent) {
            gridToolTips = new HashSet<ToolTipComponent>();
        }
    }

    public boolean hasTooltips() {
        return gridToolTips != null && gridToolTips.isEmpty() == false;
    }

    public void registerTooltip(ToolTipComponent tooltipComponent) {
        gridToolTips.add(tooltipComponent);

    }

    // Nous devons CONNAITRE en avance s'il y a des tooltips !
    public boolean containsTooltips() {
        if (component instanceof IToolTipComponent) {
            if (((IToolTipComponent) component).listToolTips().hasNext()) {
                return true;
            }
        }

        if (component instanceof IColumnsContainer) {
            for (IColumnIterator it = ((IColumnsContainer) component)
                    .listColumns(); it.hasNext();) {
                UIColumn column = it.next();

                if (column instanceof IToolTipComponent) {
                    if (((IToolTipComponent) column).listToolTips().hasNext()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public ToolTipComponent findTooltipByIdOrName(
            IComponentRenderContext componentRenderContext, UIComponent ref,
            String name, UIComponent nameContainerRef) {

        FacesContext facesContext = componentRenderContext.getFacesContext();

        if (ref instanceof IToolTipComponent) {
            IToolTipIterator it = ((IToolTipComponent) ref).listToolTips();

            for (; it.hasNext();) {
                ToolTipComponent toolTipComponent = it.next();

                if (toolTipComponent.isRendered() == false) {
                    continue;
                }

                String toolTipId = toolTipComponent.getToolTipId(facesContext);
                if (name == null) {
                    if (toolTipId == null) {
                        registerTooltip(toolTipComponent);
                        return toolTipComponent;

                    }
                    continue;
                }

                if (name.equals(toolTipId)) {
                    registerTooltip(toolTipComponent);
                    return toolTipComponent;
                }

                String toolTipClientId = toolTipComponent
                        .getClientId(facesContext);
                if (name.equals(toolTipClientId)) {

                    registerTooltip(toolTipComponent);
                    return toolTipComponent;
                }
            }
        }

        if (name == null || name.length() < 1) {
            return null;
        }

        char start = name.charAt(0);

        if (start == '#') {
            return null;
        }

        if (start != ':') {
            if (nameContainerRef == null) {
                nameContainerRef = ref;
            }

            String newName = componentRenderContext.getRenderContext()
                    .computeBrotherComponentClientId(nameContainerRef, name);
            if (newName != null) {
                name = newName;
            }

        }

        UIComponent comp = componentRenderContext.getFacesContext()
                .getViewRoot().findComponent(name);

        if ((comp instanceof ToolTipComponent) == false) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Can not find tooltip associated to clientId '" + name
                        + "'");
            }
            return null;
        }

        ToolTipComponent toolTipComponent = (ToolTipComponent) comp;
        if (toolTipComponent.isRendered() == false) {
            return null;
        }

        registerTooltip(toolTipComponent);
        return toolTipComponent;
    }

    public final Collection<ToolTipComponent> listToolTips() {
        return gridToolTips;
    }
}
