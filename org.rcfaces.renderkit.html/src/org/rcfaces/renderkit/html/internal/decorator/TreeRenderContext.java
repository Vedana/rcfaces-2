/*
 * $Id: TreeRenderContext.java,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.component.capability.IClientFullStateCapability;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
public class TreeRenderContext extends SelectItemsJsContext {

    private final boolean checkable;

    private final boolean userExpandable;

    private final boolean selectable;

    private final boolean writeSelectionState;

    private final boolean writeCheckState;

    private String lastInteractiveParent = null;

    private final boolean schrodingerCheckable;

    public TreeRenderContext(ISelectItemNodeWriter renderer,
            IComponentRenderContext componentRenderContext,
            TreeComponent treeComponent, int depth, boolean sendFullStates,
            String containerVarId) {
        super(renderer, componentRenderContext, treeComponent, null);

        FacesContext facesContext = componentRenderContext.getFacesContext();

        checkable = treeComponent.isCheckable(facesContext);
        if (checkable) {
            writeCheckState = (treeComponent
                    .getClientCheckFullState(facesContext) == IClientFullStateCapability.NONE_CLIENT_FULL_STATE);
        } else {
            writeCheckState = false;
        }

        selectable = treeComponent.isSelectable(facesContext);
        if (selectable) {
            writeSelectionState = (treeComponent
                    .getClientSelectionFullState(facesContext) == IClientFullStateCapability.NONE_CLIENT_FULL_STATE);
        } else {
            writeSelectionState = false;
        }

        schrodingerCheckable = treeComponent
                .isSchrodingerCheckable(facesContext);

        userExpandable = treeComponent.isExpandable(facesContext);

        int preloadLevel = treeComponent.getPreloadedLevelDepth(facesContext);

        if (preloadLevel > 0) {
            preloadLevel += depth;

            setPreloadedLevelDepth(preloadLevel);
        }

        Object value = treeComponent.getValue();
        Object values[] = null;
        if (value != null) {
            if (value.getClass().isArray()) {
                values = (Object[]) value;

            } else if (value instanceof Collection) {
                values = ((Collection) value).toArray();

            } else {
                values = new Object[] { value };
            }
        }

        initializeTreeValue(facesContext, treeComponent, values);

        if (containerVarId != null) {
            pushVarId(containerVarId);
        }
    }

    @Override
    protected boolean isCheckValuesAlterable() {
        return isSchrodingerCheckable();
    }

    protected void initializeValue(UIComponent component, Object value) {
    }

    protected void initializeTreeValue(FacesContext facesContext,
            TreeComponent treeComponent, Object values[]) {
        if (checkable) {
            // L'attribut checkValue est prioritaire, par contre lors de la mise
            // à jour,
            // la valeur de checkValue sera mise à NULL, et l'état check sera
            // mis dans value ! (pour le submit)

            Object checkValues = treeComponent.getCheckedValues(facesContext);
            if (checkValues != null) {
                initializeCheckValue(checkValues);

            } else {
                initializeCheckValue(values);
            }

            if (selectable) {
                initializeSelectionValue(treeComponent
                        .getSelectedValues(facesContext));
            }

        } else if (selectable) {
            Object selectionValues = treeComponent
                    .getSelectedValues(facesContext);
            if (selectionValues != null) {
                initializeSelectionValue(selectionValues);

            } else {
                initializeSelectionValue(values);
            }
        }

        initializeExpansionValue(treeComponent.getExpandedValues(facesContext));
    }

    public final boolean writeCheckFullState() {
        return writeCheckState;
    }

    public final boolean writeSelectionFullState() {
        return writeSelectionState;
    }

    public final boolean isUserExpandable() {
        return userExpandable;
    }

    public boolean isFirstInteractiveChild(String parentVarId) {
        if (lastInteractiveParent == parentVarId) {
            return false;
        }

        lastInteractiveParent = parentVarId;
        return true;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isSchrodingerCheckable() {
        return schrodingerCheckable;
    }

}
