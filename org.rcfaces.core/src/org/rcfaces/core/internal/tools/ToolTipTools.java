package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.iterator.IToolTipIterator;
import org.rcfaces.core.internal.util.ComponentIterators;

/**
 * 
 * @author jbmeslin@vedana.com
 * 
 */
public class ToolTipTools extends CollectionTools {

    private static final Log LOG = LogFactory.getLog(ToolTipTools.class);

    private static final IToolTipIterator EMPTY_TOOLTIP_ITERATOR = new ToolTipListIterator(
            Collections.<ToolTipComponent> emptyList());

	public static IToolTipIterator listToolTips(UIComponent component) {
        List<ToolTipComponent> list = ComponentIterators.list(component,
                ToolTipComponent.class);
		if (list.isEmpty()) {
			return EMPTY_TOOLTIP_ITERATOR;
		}

		return new ToolTipListIterator(list);
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:59 $
	 */
	private static final class ToolTipListIterator extends
            ComponentIterators.ComponentListIterator<ToolTipComponent>
            implements IToolTipIterator {

        protected ToolTipListIterator(List<ToolTipComponent> list) {
			super(list);
		}

		public final ToolTipComponent next() {
            return nextComponent();
		}

		public ToolTipComponent[] toArray() {
			return (ToolTipComponent[]) toArray(new ToolTipComponent[count()]);
		}
	}
}
