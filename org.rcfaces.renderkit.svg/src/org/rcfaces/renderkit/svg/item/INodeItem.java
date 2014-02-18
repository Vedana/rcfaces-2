/*
 * $Id: INodeItem.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.item;

import org.rcfaces.core.internal.contentAccessor.IResourceKeyParticipant;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.ISelectItemGroup;
import org.rcfaces.core.item.IServerDataItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public interface INodeItem extends ISelectItemGroup, IClientDataItem,
        IServerDataItem, IResourceKeyParticipant {
    String getTargetId();

    boolean isRendered();

    boolean isSelectable();

    String getAlternateText();
}
