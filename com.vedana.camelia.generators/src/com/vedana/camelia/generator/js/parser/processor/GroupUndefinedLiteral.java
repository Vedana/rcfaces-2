/*
 * $Id: GroupUndefinedLiteral.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.UndefinedLiteral;

public class GroupUndefinedLiteral extends AbstractGroupLiterals {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(stats, jsFile, new ISetLiteralCondition() {

            public boolean testLiteral(Assignment assignment,
                    Assignment original) {
                return UndefinedLiteral.class.isInstance(assignment.getRight());
            }

            public String getLiteralName(Assignment assignment) {
                return "undefined";
            }

            public String getName() {
                return "Undefined";
            }
        });
    }

}
