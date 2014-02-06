/*
 * $Id: TreeTools.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.component.TreeNodeComponent;
import org.rcfaces.core.internal.tools.CollectionTools.IComponentValueType;
import org.rcfaces.core.lang.OrderedSet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class TreeTools {
    

    private static final Object OBJECT_EMPTY_ARRAY[] = new Object[0];

    public static void setExpanded(FacesContext facesContext,
            TreeComponent treeComponent, Object itemValue, boolean expanded) {

        if (expanded) {
            ExpansionTools.expand(facesContext, treeComponent, itemValue);
            return;
        }

        ExpansionTools.collapse(facesContext, treeComponent, itemValue);
    }

    public static boolean isExpanded(FacesContext facesContext,
            TreeComponent treeComponent, Object itemValue) {

        if (itemValue == null) {
            return false;
        }

        Set values = ExpansionTools.expansionValuesToSet(facesContext,
                treeComponent, true);

        if (values == null || values.isEmpty()) {
            return false;
        }

        return values.contains(itemValue);
    }

    public static TreeComponent getTree(TreeNodeComponent component) {
        UIComponent parent = component.getParent();

        for (; parent != null; parent = parent.getParent()) {
            if (parent instanceof TreeComponent) {
                return (TreeComponent) parent;
            }

            if (parent instanceof UISelectItem) {
                continue;
            }

            throw new FacesException(
                    "Invalid parent of TreeNode component. (Parent must be a Tree or a UISelectItem).");
        }

        throw new FacesException("Parent of TreeNode component not found !");
    }

    public static final IComponentValueType TREE_VALUE_TYPE = new IComponentValueType() {
        

        public Object createNewValue(UIComponent component) {
            return new OrderedSet();
        }
    };

}
