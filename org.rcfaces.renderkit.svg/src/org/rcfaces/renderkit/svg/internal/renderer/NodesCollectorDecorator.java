/*
 * $Id: NodesCollectorDecorator.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.renderer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.decorator.AbstractSelectItemsDecorator;
import org.rcfaces.renderkit.html.internal.decorator.SelectItemsContext;
import org.rcfaces.renderkit.svg.component.GroupComponent;
import org.rcfaces.renderkit.svg.component.NodeComponent;
import org.rcfaces.renderkit.svg.component.PathComponent;
import org.rcfaces.renderkit.svg.item.GroupItem;
import org.rcfaces.renderkit.svg.item.INodeItem;
import org.rcfaces.renderkit.svg.item.NodeItem;
import org.rcfaces.renderkit.svg.item.PathItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class NodesCollectorDecorator extends AbstractSelectItemsDecorator {

    private static final INodeItem NODE_ITEM_EMPTY_ARRAY[] = new INodeItem[0];

    private final boolean selectableSupport;

    private List<SelectItem> nodes;

    private List<Object> nodesStack = new ArrayList<Object>();

    private boolean selectable;

    public NodesCollectorDecorator(UIComponent component,
            boolean selectableSupport) {
        super(component, null);

        this.selectableSupport = selectableSupport;
    }

    @Override
    protected SelectItemsContext createHtmlContext() {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        return new SelectItemsContext(this, componentRenderContext,
                getComponent(), null);
    }

    @Override
    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        return null;
    }

    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (isVisible == false || selectItem.isDisabled()) {
            return SKIP_NODE;
        }

        if (selectItem instanceof INodeItem) {
            if (nodes == null) {
                nodes = new ArrayList<SelectItem>(8);
            }
            nodes.add(selectItem);

            if (hasChild) {
                nodesStack.add(nodes);
                nodes = null;
            }

            if (selectableSupport && ((INodeItem) selectItem).isSelectable()) {
                selectable = true;
            }
        }

        return EVAL_NODE;
    }

    @SuppressWarnings("unchecked")
    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (isVisible && hasChild && (selectItem instanceof INodeItem)
                && nodes != null) {

            SelectItem children[] = nodes.toArray(new SelectItem[nodes.size()]);

            ((SelectItemGroup) selectItem).setSelectItems(children);

            nodes = (List<SelectItem>) nodesStack.remove(nodesStack.size() - 1);
        }
    }

    @Override
    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof GroupComponent) {
            return new GroupItem((GroupComponent) component);
        }
        if (component instanceof PathComponent) {
            return new PathItem((PathComponent) component);
        }
        if (component instanceof NodeComponent) {
            return new NodeItem((NodeComponent) component);
        }

        return null;
    }

    public INodeItem[] listNodes() {
        if (nodes == null || nodes.isEmpty()) {
            return NODE_ITEM_EMPTY_ARRAY;
        }

        return nodes.toArray(new INodeItem[nodes.size()]);
    }

    public boolean isItemSelectable() {
        return selectable;
    }

}
