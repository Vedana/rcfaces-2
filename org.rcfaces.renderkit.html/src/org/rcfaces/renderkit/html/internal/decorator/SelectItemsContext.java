/*
 * $Id: SelectItemsContext.java,v 1.4 2013/11/13 12:53:31 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ICheckableCapability;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CheckTools;
import org.rcfaces.core.internal.tools.ExpansionTools;
import org.rcfaces.core.internal.tools.SelectionTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:31 $
 */
public class SelectItemsContext {

    private static final Log LOG = LogFactory.getLog(SelectItemsContext.class);

    private static final Object OPEN_HAS_CHILD = "HAS CHILD";

    private static final Object OPEN_NO_CHILD = "NO CHILD";

    private static final Object REFUSE_CHILD = "REFUSE CHILD";

    private static final Object NOT_OPENED = "NOT OPENED";

    private static final boolean LOG_TREE = LOG.isDebugEnabled();

    private static final boolean LOG_STACK = LOG.isTraceEnabled();
    static {
        if (LOG_TREE) {
            LOG.info("LOG_TREE enabled !");
        }
        if (LOG_STACK) {
            LOG.info("LOG_STACK enabled !");
        }
    }

    // private final AbstractSelectItemsRenderer renderer;

    private final ISelectItemNodeWriter selectItemNodeWriter;

    private final List<Object> items = new ArrayList<Object>(16);

    private final IComponentRenderContext componentContext;

    // private int id = 0;

    private final UIComponent rootComponent;

    private UIComponent cachedComponentClient;

    private String cachedComponentClientId;

    private int depth = 0;

    private int preloadedLevelDepth = -1;

    private Set<Object> selectionValues = null;

    private Set<Object> checkValues = null;

    private Set<Object> expandValues = null;

    private boolean expandValuesModified = false;

    public SelectItemsContext(ISelectItemNodeWriter renderer,
            IComponentRenderContext componentRenderContext,
            UIComponent rootComponent, Object value) {
        this.selectItemNodeWriter = renderer;
        this.componentContext = componentRenderContext;

        this.rootComponent = rootComponent;
        initializeValue(rootComponent, value);
    }

    protected void initializeValue(UIComponent component, Object value) {

        if (component instanceof ICheckableCapability) {
            if (((ICheckableCapability) component).isCheckable()) {
                initializeCheckValue(value);
                return;
            }
        }

        initializeSelectionValue(value);
    }

    protected final int getPreloadedLevelDepth() {
        return preloadedLevelDepth;
    }

    protected final void setPreloadedLevelDepth(int preloadedLevelDepth) {
        this.preloadedLevelDepth = preloadedLevelDepth;
    }

    protected void initializeSelectionValue(Object value) {
        this.selectionValues = SelectionTools.valuesToSet(value, true);
    }

    protected void initializeCheckValue(Object values) {
        // Les CHECK doit être mutable
        this.checkValues = CheckTools.valuesToSet(values,
                isCheckValuesAlterable());
    }

    protected boolean isCheckValuesAlterable() {
        return false;
    }

    protected void initializeExpansionValue(Object values) {
        this.expandValues = ExpansionTools.valuesToSet(values, true);
    }

    public final UIComponent getRootComponent() {
        return rootComponent;
    }

    private final IComponentRenderContext getComponentRenderContext() {
        return componentContext;
    }

    public final boolean isValueSelected(SelectItem item, Object value) {
        if (selectionValues == null || selectionValues.isEmpty()) {
            return false;
        }

        /*
         * Object value = item.getValue(); if (value == null) { return false; }
         */

        return selectionValues.contains(value);
    }

    public final boolean isValueChecked(SelectItem item, Object value) {
        if (checkValues == null || checkValues.isEmpty()) {
            return false;
        }

        return checkValues.contains(value);
    }

    public final void removeValueChecked(SelectItem item) {
        if (checkValues == null || checkValues.isEmpty()) {
            return;
        }

        checkValues.remove(item.getValue());
    }

    public final boolean isValueExpanded(SelectItem item, Object value) {
        if (expandValues == null || expandValues.isEmpty()) {
            return false;
        }

        return expandValues.contains(value);
    }

    public final void setValueExpanded(SelectItem item, Object value) {
        if (expandValues == null) {
            expandValues = new HashSet<Object>();
            expandValuesModified = true;

        } else if (expandValuesModified == false) {
            expandValuesModified = true;
            expandValues = new HashSet<Object>(expandValues);
        }

        expandValues.add(value);
    }

    public final String getComponentClientId(UIComponent component) {
        if (component == this.cachedComponentClient) {
            return cachedComponentClientId;
        }

        this.cachedComponentClient = component;

        IComponentRenderContext componentRenderContext = getComponentRenderContext();
        IRenderContext renderContext = componentRenderContext
                .getRenderContext();

        cachedComponentClientId = renderContext.getComponentClientId(component);

        return cachedComponentClientId;
    }

    public boolean pushSelectItem(UIComponent component, SelectItem selectItem,
            boolean visible) throws WriterException {

        int size = items.size();
        depth = size / 4;

        if (size > 0) {
            Object opened = items.get(size - 1);

            if (opened == NOT_OPENED) {
                UIComponent parentComponent = (UIComponent) items.get(size - 4);
                SelectItem parentSelectItem = (SelectItem) items.get(size - 3);
                boolean parentVisible = ((Boolean) items.get(size - 2))
                        .booleanValue();

                if (LOG_TREE) {
                    String ss = "";
                    for (int s = 0; s < depth; s++) {
                        ss += "  ";
                    }
                    LOG.debug(ss + "+ " + parentSelectItem.getLabel());
                }

                int eval = selectItemNodeWriter.encodeNodeBegin(
                        parentComponent, parentSelectItem, true, parentVisible);
                if (eval == ISelectItemNodeWriter.SKIP_NODE) {
                    items.set(size - 1, REFUSE_CHILD);
                    if (LOG_STACK) {
                        LOG.trace("REFUSE children (SET) for "
                                + parentSelectItem.getLabel());
                    }
                    return false;
                }

                items.set(size - 1, OPEN_HAS_CHILD);

            } else if (opened == REFUSE_CHILD) {
                if (LOG_STACK) {
                    LOG.trace("REFUSE child: " + selectItem.getLabel());
                }
                return false;
            }
        }

        items.add(component);
        items.add(selectItem);
        items.add(Boolean.valueOf(visible));
        items.add(NOT_OPENED);

        depth = (items.size() / 4);

        if (LOG_STACK) {
            LOG.trace("PUSH: " + selectItem.getLabel() + " [" + depth + "] "
                    + visible);
        }

        selectItemNodeWriter.encodeNodeInit(component, selectItem);

        return true;
    }

    public void popSelectItem() throws WriterException {
        depth = 1; // Il y a forcement un element

        if (LOG_STACK) {
            LOG.trace("POP enter" + " [" + depth + "]");
        }

        for (ListIterator<Object> it = items.listIterator(); it.hasNext(); depth++) {
            UIComponent component = (UIComponent) it.next();
            SelectItem selectItem = (SelectItem) it.next();
            boolean visible = ((Boolean) it.next()).booleanValue();
            Object opened = it.next();

            if (opened == NOT_OPENED) {
                boolean hasChild = it.hasNext();

                if (LOG_TREE) {
                    String ss = "";
                    for (int s = 0; s < depth; s++) {
                        ss += "  ";
                    }

                    LOG.debug(ss + "+ " + selectItem.getLabel());
                }

                int eval = selectItemNodeWriter.encodeNodeBegin(component,
                        selectItem, it.hasNext(), visible);

                // S'il y a des enfants, on rappelle la m�thode
                if (eval == ISelectItemNodeWriter.SKIP_NODE) {
                    // Pas les enfants !
                    if (LOG_STACK) {
                        LOG.trace("POP BREAK");
                    }
                    for (; it.hasNext();) {
                        it.remove();
                    }

                    break;
                }

                if (hasChild) {
                    it.set(OPEN_HAS_CHILD);

                } else {
                    it.set(OPEN_NO_CHILD);
                }
            }

            if (it.hasNext()) {
                // Il reste des enfants ? ou des freres ?
                continue;
            }

            if (opened != REFUSE_CHILD) {
                if (LOG_TREE) {
                    String ss = "";
                    for (int s = 0; s < depth; s++) {
                        ss += "  ";
                    }

                    LOG.debug(ss + "- " + selectItem.getLabel());
                }

                // On ferme alors ...
                selectItemNodeWriter.encodeNodeEnd(component, selectItem,
                        opened == OPEN_HAS_CHILD, visible);
            }
        }

        // On retire le selectItem de la pile !
        int s = items.size();
        items.remove(--s);
        items.remove(--s);
        items.remove(--s);
        items.remove(--s);

        depth = (s / 4);

        if (LOG_STACK) {
            LOG.trace("POP exit" + " [" + depth + "]");
        }
    }

    public int getDepth() {
        return depth;
    }

    public Set<Object> getCheckValues() {
        if (checkValues != null) {
            return checkValues;
        }

        return Collections.emptySet();
    }

    public Set<Object> getSelectionValues() {
        if (selectionValues != null) {
            return selectionValues;
        }

        return Collections.emptySet();
    }

    public SelectItem getParentSelectItem() {
        int size = items.size();

        SelectItem parentSelectItem = (SelectItem) items.get(size - 3);

        return parentSelectItem;
    }
}
