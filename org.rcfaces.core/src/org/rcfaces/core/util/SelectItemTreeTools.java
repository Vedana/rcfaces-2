/*
 * $Id: SelectItemTreeTools.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.rcfaces.core.item.BasicSelectItemPath;
import org.rcfaces.core.item.ISelectItemPath;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.util.SelectItemTools.DefaultSelectItemNodeHandler;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public class SelectItemTreeTools {

    private static final ISelectItemNodeFactory DEFAULT_FACTORY = new ISelectItemNodeFactory() {

        public SelectItemNode newSelectItemNode(SelectItemNode parent,
                SelectItem selectItem, String id, int depth) {
            return new SelectItemNode(parent, id, selectItem, depth);
        }
    };

    public static SelectItemNode constructTree(UIComponent component) {
        return constructTree(null, component, null, null);
    }

    public static SelectItemNode constructTree(FacesContext facesContext,
            UIComponent component, IFilterProperties filterProperties,
            ISelectItemNodeFactory factory) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        if (factory == null) {
            factory = DEFAULT_FACTORY;
        }

        TreeConstructorHandler docParser = new TreeConstructorHandler(
                facesContext, factory);

        SelectItemTools.traverseSelectItemTree(facesContext, component,
                filterProperties, docParser);

        return docParser.getRootNode();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
     */
    public static class SelectItemNode {
        private static final SelectItemNode[] EMPTY_ARRAY = new SelectItemNode[0];

        private final SelectItem selectItem;

        private List<SelectItemNode> children = null;

        private final String id;

        private final int depth;

        private final SelectItemNode parent;

        private ISelectItemPath path;

        protected SelectItemNode(SelectItemNode parent, String id,
                SelectItem selectItem, int depth) {
            this.parent = parent;
            this.selectItem = selectItem;
            this.id = id;
            this.depth = depth;
        }

        public void addChild(SelectItemNode child) {
            if (children == null) {
                children = new ArrayList<SelectItemNode>();
            }
            children.add(child);
        }

        public SelectItem getSelectItem() {
            return selectItem;
        }

        public SelectItemNode[] getChildren() {
            if (children == null) {
                return EMPTY_ARRAY;
            }
            return children.toArray(new SelectItemNode[children.size()]);
        }

        public String getId() {
            return id;
        }

        public SelectItemNode getParent() {
            return parent;
        }

        public int getDepth() {
            return depth;
        }

        public SelectItemNode searchById(String id) {
            if (id.equals(getId())) {
                return this;
            }

            SelectItemNode chilren[] = getChildren();
            for (int i = 0; i < chilren.length; i++) {
                SelectItemNode si = chilren[i].searchById(id);
                if (si != null) {
                    return si;
                }
            }

            return null;
        }

        public SelectItemNode searchByValue(Object value) {
            if (selectItem != null && value.equals(selectItem.getValue())) {
                return this;
            }

            SelectItemNode chilren[] = getChildren();
            for (int i = 0; i < chilren.length; i++) {
                SelectItemNode si = chilren[i].searchByValue(value);
                if (si != null) {
                    return si;
                }
            }

            return null;
        }

        public SelectItemNode getRoot() {
            SelectItemNode node = this;
            for (; node.getParent() != null; node = node.getParent()) {
            }

            return node;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + depth;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof SelectItemNode)) {
                return false;
            }
            SelectItemNode other = (SelectItemNode) obj;
            if (depth != other.depth) {
                return false;
            }
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            return true;
        }

        public ISelectItemPath getPath() {
            if (path != null) {
                return path;
            }

            List<SelectItem> segments = new LinkedList<SelectItem>();
            for (SelectItemNode node = this; node != null; node = node
                    .getParent()) {

                segments.add(0, node.getSelectItem());
            }

            path = new BasicSelectItemPath(segments);

            return path;
        }

        public <T> T forEachNode(INodeHandler<T> handler) {
            List<SelectItemNode> list = new LinkedList<SelectItemNode>();

            list.add(this);

            for (; list.isEmpty() == false;) {
                SelectItemNode node = list.remove(0);

                T t = handler.process(node);
                if (t != null) {
                    return t;
                }

                if (node.hasChildren()) {
                    list.addAll(0, Arrays.asList(node.getChildren()));
                }
            }

            return null;
        }

        public boolean hasChildren() {
            return children != null && children.size() > 0;
        }
    }

    public interface INodeHandler<T> {
        T process(SelectItemNode node);
    }

    public static SelectItem searchById(SelectItemNode node, String id) {
        SelectItemNode sn = node.searchById(id);
        if (sn == null) {
            return null;
        }

        return sn.getSelectItem();
    }

    public interface ISelectItemNodeFactory {
        SelectItemNode newSelectItemNode(SelectItemNode parent,
                SelectItem selectItem, String id, int depth);
    }

    protected static class TreeConstructorHandler extends
            DefaultSelectItemNodeHandler<Boolean> {

        private final ISelectItemNodeFactory factory;

        private final Set<String> selectItemIds = new HashSet<String>();

        private final List<SelectItemTreeTools.SelectItemNode> stack = new LinkedList<SelectItemTreeTools.SelectItemNode>();

        private final SelectItemTreeTools.SelectItemNode root;

        private final FacesContext facesContext;

        private String componentClientId;

        public TreeConstructorHandler(FacesContext facesContext,
                ISelectItemNodeFactory factory) {
            this.facesContext = facesContext;
            this.factory = factory;

            root = factory.newSelectItemNode(null, null, null, 0);
        }

        public SelectItemTreeTools.SelectItemNode getRootNode() {
            return root;
        }

        @Override
        public Boolean beginTree(UIComponent component) {
            componentClientId = component.getClientId(facesContext);

            stack.add(root);

            return null;
        }

        @Override
        public Boolean endTree(UIComponent component) {
            stack.remove(0); // Pour la frime !

            return null;
        }

        @Override
        public Boolean beginNode(UIComponent component, SelectItem selectItem) {

            String selectItemId = componentClientId;

            if (selectItemIds.add(componentClientId) == false) {
                selectItemId = componentClientId + "::" + selectItemIds.size();
                selectItemIds.add(componentClientId);
            }

            SelectItemNode parentNode = stack.get(0);

            SelectItemNode node = factory.newSelectItemNode(parentNode,
                    selectItem, selectItemId, parentNode.depth + 1);

            parentNode.addChild(node);

            stack.add(0, node);

            return null;
        }

        @Override
        public Boolean endNode(UIComponent component, SelectItem selectItem) {
            stack.remove(0);

            return null;
        }

    }
}
