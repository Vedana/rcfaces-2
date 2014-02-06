/*
 * $Id: GroupStringLiteral.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.StringLiteral;

public class GroupStringLiteral extends AbstractGroupLiterals {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(stats, jsFile, new ISetLiteralCondition() {

            public boolean testLiteral(Assignment assignment,
                    Assignment original) {
                if (StringLiteral.class.isInstance(assignment.getRight()) == false) {
                    return false;
                }

                if (original == null) {
                    return true;
                }

                if (((StringLiteral) assignment.getRight()).getString().equals(
                        ((StringLiteral) original.getRight()).getString())) {
                    return true;
                }

                return false;
            }

            public String getLiteralName(Assignment assignment) {
                return ((StringLiteral) assignment.getRight()).getString();
            }

            public String getName() {
                return "String";
            }
        });
    }

}
