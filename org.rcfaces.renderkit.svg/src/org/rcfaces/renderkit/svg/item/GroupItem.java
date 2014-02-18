/*
 * $Id: GroupItem.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.item;

import org.rcfaces.renderkit.svg.component.GroupComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class GroupItem extends NodeItem implements IGroupItem {

    private static final long serialVersionUID = -3180958048094512452L;

    public GroupItem() {
    }

    public GroupItem(IGroupItem groupItem, INodeItem nodeItems[]) {
        super(groupItem, nodeItems);
    }

    public GroupItem(GroupComponent component) {
        super(component);
    }

}
