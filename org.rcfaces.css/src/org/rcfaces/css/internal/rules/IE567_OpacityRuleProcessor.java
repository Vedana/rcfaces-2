/*
 * $Id: IE567_OpacityRuleProcessor.java,v 1.1.2.3 2012/11/27 16:23:02 oeuillot Exp $
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
 * @version $Revision: 1.1.2.3 $ $Date: 2012/11/27 16:23:02 $
 */
public class IE567_OpacityRuleProcessor implements IPropertyRuleProcessor {

    @Override
    public void process(CssPropertyListIterator declarationList,
            UserAgentPropertyRule ur, Property p) {

        CSSValue value = p.getValue();

        // filter : alpha(opacity=85);

        if (value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE
                || ((CSSPrimitiveValue) value).getPrimitiveType() != CSSPrimitiveValue.CSS_NUMBER) {
            return;
        }

        double lv = ((CSSPrimitiveValue) value)
                .getFloatValue(CSSPrimitiveValue.CSS_NUMBER);

        LexicalUnit lexicalUnit = LexicalUnitImpl.createIdent(null,
                "progid:DXImageTransform.Microsoft.Alpha(opacity="
                        + ((int) (lv * 100)) + ")");

        CSSValueImpl newValue = new CSSValueImpl(lexicalUnit);

        declarationList.addProperty("filter", newValue, p.isImportant(), p,
                true);
    }
}
