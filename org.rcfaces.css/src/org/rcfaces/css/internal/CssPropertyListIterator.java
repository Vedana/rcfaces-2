/*
 * $Id: CssPropertyListIterator.java,v 1.1.2.1 2012/11/27 16:23:02 oeuillot Exp $
 */
package org.rcfaces.css.internal;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSValue;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;

public class CssPropertyListIterator {

    private final List<Property> properties;

    private int position;

    public CssPropertyListIterator(CSSStyleRule styleRule) {
        properties = ((CSSStyleDeclarationImpl) styleRule.getStyle())
                .getProperties();
    }

    public boolean hasNext() {
        return position < properties.size();
    }

    public Property next() {
        Property p = properties.get(position);

        position++;

        return p;
    }

    public void remove() {
        position--;

        properties.remove(position);
    }

    public void removeEndsWith(String propertyName) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);

            if (p.getName().endsWith(propertyName) == false) {
                continue;
            }

            properties.remove(i);
            i--;

            if (position > i) {
                position--;
            }
        }
    }

    public Property[] toArray() {
        return properties.toArray(new Property[properties.size()]);
    }

    public Property addProperty(String propertyName, CSSValue value,
            Property p, boolean removeSamePropertyName) {
        return addProperty(propertyName, value, p.isImportant(), p,
                removeSamePropertyName);
    }

    public Property addProperty(String propertyName, CSSValue value,
            boolean important, Property after, boolean removeSamePropertyName) {

        boolean found = false;
        int position = 0;
        for (Iterator<Property> it = properties.iterator(); it.hasNext();) {
            Property p = it.next();

            if (p == after) {
                position++;
                found = true;
                continue;
            }

            if (p.getName().equals(propertyName) == false) {
                if (found == false) {
                    position++;
                }
                continue;
            }

            if (removeSamePropertyName) {
                p.setUserData(CssSteadyStateParser.DELETED_RULE_PROPERTY,
                        Boolean.TRUE);
                it.remove();
            }
        }

        if (found == false) {
            position = properties.size();
        }

        Property newProperty = new Property(propertyName, value, important);
        properties.add(position, newProperty);

        newProperty.setUserData(CssSteadyStateParser.VIRTUAL_RULE_PROPERTY,
                Boolean.TRUE);

        return newProperty;
    }
}
