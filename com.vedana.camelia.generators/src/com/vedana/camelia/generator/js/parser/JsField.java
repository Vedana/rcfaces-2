/*
 * $Id: JsField.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;

public class JsField extends JsMember implements IJsField {

    private IJsType type;

    public JsField(IJsClass jsClass, ASTNode definition, Expression value,
            JsModifier modifier, JsComment comment) {
        super(jsClass, definition, value, modifier, comment);

    }

    public IJsType getType(JsStats stats) {
        String value = getComment().getValue();

        TypeTokenizer tt = new TypeTokenizer(value);

        for (; tt.hasMoreTokens();) {
            String tok = tt.nextToken();

            if ("final".equals(tok) || "static".equals(tok)
                    || "public".equals(tok) || "protected".equals(tok)
                    || "hidden".equals(tok) || "private".equals(tok)) {
                continue;
            }

            return JsType.parse(stats, tok, null, true);
        }

        return null;
    }

    public Expression getExpression() {
        return value;
    }

    @Override
    public String toString() {
        return "[JsField '" + getName() + "' modifier={"
                + modifier.formatFlags() + "} value=" + value + "]";
    }
}
