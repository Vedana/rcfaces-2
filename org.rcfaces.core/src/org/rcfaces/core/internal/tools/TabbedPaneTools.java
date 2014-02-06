/*
 * $Id: TabbedPaneTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;

import org.rcfaces.core.component.TabComponent;
import org.rcfaces.core.component.TabbedPaneComponent;
import org.rcfaces.core.component.iterator.ITabIterator;
import org.rcfaces.core.internal.util.ComponentIterators;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class TabbedPaneTools extends CardBoxTools {

    private static final ITabIterator EMPTY_COMPONENT_ITERATOR = new TabListIterator(
            Collections.<TabComponent> emptyList());

    public static ITabIterator listTabs(TabbedPaneComponent component) {
        List<TabComponent> list = ComponentIterators.list(component,
                TabComponent.class);
        if (list.isEmpty()) {
            return EMPTY_COMPONENT_ITERATOR;
        }

        return new TabListIterator(list);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class TabListIterator extends
            ComponentIterators.ComponentListIterator<TabComponent> implements
            ITabIterator {

        public TabListIterator(List<TabComponent> list) {
            super(list);
        }

        public final TabComponent next() {
            return nextComponent();
        }

        public TabComponent[] toArray() {
            return (TabComponent[]) toArray(new TabComponent[count()]);
        }
    }
}
