/*
 * $Id: NodeItem.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.rcfaces.core.internal.contentAccessor.IResourceKeyParticipant;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.ItemTools;
import org.rcfaces.renderkit.svg.component.NodeComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class NodeItem extends SelectItemGroup implements INodeItem, StateHolder {

    private static final long serialVersionUID = 8556900994943378471L;

    private static final SelectItem[] SELECT_ITEM_EMPTY_ARRAY = new SelectItem[0];

    private Map<String, Object> serverDatas;

    private Map<String, String> clientDatas;

    private boolean transientValue;

    private boolean rendered;

    private boolean selectable;

    private String alternateText;

    public NodeItem() {

    }

    public NodeItem(INodeItem nodeItem, INodeItem items[]) {
        super(null, null, false, convertToNodeItems(items));

        setValue(nodeItem.getTargetId());
        setRendered(nodeItem.isRendered());
    }

    private static SelectItem[] convertToNodeItems(INodeItem[] items) {
        if (items == null || (items instanceof SelectItem[])) {
            return (SelectItem[]) items;
        }

        if (items.length == 0) {
            return SELECT_ITEM_EMPTY_ARRAY;
        }

        SelectItem dest[] = new SelectItem[items.length];
        System.arraycopy(items, 0, dest, 0, items.length);

        return dest;
    }

    public NodeItem(NodeComponent component) {
        super(component.getItemLabel(), component.getItemDescription(),
                component.isItemDisabled(), SELECT_ITEM_EMPTY_ARRAY);

        setValue(component.getItemValue());

        setRendered(component.isRendered());

        if (component.getServerDataCount() > 0) {
            Map<String, Object> map = component.getServerDataMap();

            getServerDataMap().putAll(map);
        }

        if (component.getClientDataCount() > 0) {
            Map<String, String> map = component.getClientDataMap();

            getClientDataMap().putAll(map);
        }
    }

    public String getTargetId() {
        return (String) getValue();
    }

    public void setTargetId(String id) {
        setValue(id);
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public final boolean isSelectable() {
        return selectable;
    }

    public final void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public final String getAlternateText() {
        return alternateText;
    }

    public final void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public void addNodeItem(INodeItem nodeItem) {
        SelectItem items[] = getSelectItems();
        if (items == null || items.length == 0) {
            setSelectItems(new SelectItem[] { (SelectItem) nodeItem });
            return;
        }

        List<SelectItem> l = new ArrayList<SelectItem>(items.length + 1);
        l.addAll(Arrays.asList(items));
        l.add((SelectItem) nodeItem);

        setSelectItems(l.toArray(new SelectItem[l.size()]));

    }

    public boolean isTransient() {
        return transientValue;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientValue = newTransientValue;
    }

    public void restoreState(FacesContext context, Object state) {
        Object states[] = (Object[]) state;

        setValue(states[0]);
        setLabel((String) states[1]);
        setDescription((String) states[2]);
        setDisabled(states[3] != null);

        setRendered(states[4] != null);

        setSelectable(states[5] != null);

        setAlternateText((String) states[6]);

        if (states[7] != null) {
            clientDatas = (Map<String, String>) UIComponentBase
                    .restoreAttachedState(context, states[7]);
        }

        if (states[8] != null) {
            serverDatas = (Map<String, Object>) UIComponentBase
                    .restoreAttachedState(context, states[8]);
        }

        int idx = 9;

        if (states.length <= idx) {
            setSelectItems(SELECT_ITEM_EMPTY_ARRAY);
            return;
        }

        SelectItem selectItems[] = new SelectItem[states.length - idx];
        for (int i = 0; i < selectItems.length; i++) {
            selectItems[i] = (SelectItem) UIComponentBase.restoreAttachedState(
                    context, states[i + idx]);
        }

        setSelectItems(selectItems);
    }

    public Object saveState(FacesContext context) {
        SelectItem children[] = getSelectItems();
        if (children == null) {
            children = SELECT_ITEM_EMPTY_ARRAY;
        }

        Object ret[] = new Object[6 + children.length];

        ret[0] = getValue();
        ret[1] = getLabel();
        ret[2] = getDescription();
        ret[3] = isDisabled() ? Boolean.TRUE : null;
        ret[4] = isRendered() ? Boolean.TRUE : null;
        ret[5] = isSelectable() ? Boolean.TRUE : null;
        ret[6] = getAlternateText();

        if (isClientDataEmpty() == false) {
            ret[7] = UIComponentBase.saveAttachedState(context, clientDatas);
        }

        if (isServerDataEmpty() == false) {
            ret[8] = UIComponentBase.saveAttachedState(context, serverDatas);
        }

        int idx = 9;

        for (int i = 0; i < children.length; i++) {
            ret[i + idx] = UIComponentBase.saveAttachedState(context,
                    children[i]);
        }

        return ret;
    }

    public void participeKey(StringAppender sa) {
        SelectItem nodes[] = getSelectItems();

        ItemTools.participeKey(sa, this, NodeItem.class);

        if (isClientDataEmpty() == false) {
            ItemTools.participeKey(sa, clientDatas);
        }

        for (int i = 0; i < nodes.length; i++) {
            SelectItem node = nodes[i];
            if (node instanceof IResourceKeyParticipant) {
                ((IResourceKeyParticipant) node).participeKey(sa);
            }
        }

    }

    public boolean isServerDataEmpty() {
        if (serverDatas == null) {
            return true;
        }

        return serverDatas.isEmpty();
    }

    public Map<String, Object> getServerDataMap() {
        if (serverDatas == null) {
            serverDatas = new HashMap<String, Object>();
        }

        return serverDatas;
    }

    public boolean isClientDataEmpty() {
        if (clientDatas == null) {
            return true;
        }

        return clientDatas.isEmpty();
    }

    public Map<String, String> getClientDataMap() {
        if (clientDatas == null) {
            clientDatas = new HashMap<String, String>();
        }

        return clientDatas;
    }

}
