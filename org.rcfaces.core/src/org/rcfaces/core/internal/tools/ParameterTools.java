/*
 * $Id: ParameterTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.rcfaces.core.component.iterator.IParameterIterator;
import org.rcfaces.core.internal.util.ComponentIterators;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ParameterTools {

    private static final IParameterIterator EMPTY_PARAMETER_ITERATOR = new ParameterListIterator(
            Collections.<UIParameter> emptyList());

    public static IParameterIterator listParameters(UIComponent component) {
        List<UIParameter> list = ComponentIterators.list(component,
                UIParameter.class);
        if (list.isEmpty()) {
            return EMPTY_PARAMETER_ITERATOR;
        }

        return new ParameterListIterator(list);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class ParameterListIterator extends
            ComponentIterators.ComponentListIterator<UIParameter> implements
            IParameterIterator {

        public ParameterListIterator(List<UIParameter> list) {
            super(list);
        }

        public final UIParameter next() {
            return nextComponent();
        }

        public UIParameter[] toArray() {
            return (UIParameter[]) toArray(new UIParameter[count()]);
        }
    }
}
