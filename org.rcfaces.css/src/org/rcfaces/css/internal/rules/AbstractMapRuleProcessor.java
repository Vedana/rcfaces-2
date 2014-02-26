/*
 * $Id: AbstractMapRuleProcessor.java,v 1.1.2.1 2012/11/28 10:53:22 oeuillot Exp $
 */
package org.rcfaces.css.internal.rules;

import java.util.HashMap;
import java.util.Map;

import org.rcfaces.css.internal.CssPropertyListIterator;

import com.steadystate.css.dom.Property;

public class AbstractMapRuleProcessor implements IPropertyRuleProcessor {

    protected final Map<String, String> mapPropertyName = new HashMap<String, String>();

    protected AbstractMapRuleProcessor() {
    }

    @Override
    public void process(CssPropertyListIterator declarationList,
            UserAgentPropertyRule ur, Property p) {

        String name = p.getName();

        String map = mapPropertyName.get(name);
        if (map != null) {
            declarationList.addProperty(map, p.getValue(), p.isImportant(), p,
                    true);
            return;
        }
    }

}
