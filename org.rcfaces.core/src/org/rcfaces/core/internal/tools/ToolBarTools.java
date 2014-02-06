/*
 * $Id: ToolBarTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;

import org.rcfaces.core.component.ToolBarComponent;
import org.rcfaces.core.component.ToolFolderComponent;
import org.rcfaces.core.component.ToolItemComponent;
import org.rcfaces.core.component.iterator.IToolFolderIterator;
import org.rcfaces.core.component.iterator.IToolItemIterator;
import org.rcfaces.core.internal.util.ComponentIterators;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ToolBarTools {

    private static final IToolItemIterator EMPTY_TOOL_ITEM_ITERATOR = new ToolItemListIterator(
            Collections.<ToolItemComponent> emptyList());

    private static final IToolFolderIterator EMPTY_TOOL_FOLDER_ITERATOR = new ToolFolderListIterator(
            Collections.<ToolFolderComponent> emptyList());

    public static IToolItemIterator listToolItems(ToolFolderComponent component) {
        List<ToolItemComponent> list = ComponentIterators.list(component,
                ToolItemComponent.class);
        if (list.isEmpty()) {
            return EMPTY_TOOL_ITEM_ITERATOR;
        }

        return new ToolItemListIterator(list);
    }

    public static IToolFolderIterator listToolFolders(ToolBarComponent component) {
        List<ToolFolderComponent> list = ComponentIterators.list(component,
                ToolFolderComponent.class);
        if (list.isEmpty()) {
            return EMPTY_TOOL_FOLDER_ITERATOR;
        }

        return new ToolFolderListIterator(list);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class ToolItemListIterator extends
            ComponentIterators.ComponentListIterator<ToolItemComponent>
            implements IToolItemIterator {

        public ToolItemListIterator(List<ToolItemComponent> list) {
            super(list);
        }

        public final ToolItemComponent next() {
            return nextComponent();
        }

        public ToolItemComponent[] toArray() {
            return (ToolItemComponent[]) toArray(new ToolItemComponent[count()]);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class ToolFolderListIterator extends
            ComponentIterators.ComponentListIterator<ToolFolderComponent>
            implements IToolFolderIterator {

        public ToolFolderListIterator(List<ToolFolderComponent> list) {
            super(list);
        }

        public final ToolFolderComponent next() {
            return nextComponent();
        }

        public ToolFolderComponent[] toArray() {
            return (ToolFolderComponent[]) toArray(new ToolFolderComponent[count()]);
        }
    }

}
