/*
 * $Id: IE8_OpacityRuleProcessor.java,v 1.1.2.2 2012/11/27 16:23:02 oeuillot Exp $
 */
package org.rcfaces.css.internal.rules;

import org.rcfaces.css.internal.CssPropertyListIterator;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.LexicalUnitImpl;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1.2.2 $ $Date: 2012/11/27 16:23:02 $
 */
public class IE8_OpacityRuleProcessor implements IPropertyRuleProcessor {

    @Override
    public void process(CssPropertyListIterator declarationList,
            UserAgentPropertyRule ur, Property p) {

        CSSValue value = p.getValue();

        // -ms-filter: "alpha(opacity=85)"; /* IE 8 */

        if (value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE
                || ((CSSPrimitiveValue) value).getPrimitiveType() != CSSPrimitiveValue.CSS_NUMBER) {
            return;
        }

        double lv = ((CSSPrimitiveValue) value)
                .getFloatValue(CSSPrimitiveValue.CSS_NUMBER);

        LexicalUnit lexicalUnit = LexicalUnitImpl.createString(null,
                "alpha(opacity=" + ((int) (lv * 100)) + ")");

        CSSValueImpl newValue = new CSSValueImpl(lexicalUnit);

        declarationList.addProperty("-ms-filter", newValue, p.isImportant(), p,
                true);
    }
}
