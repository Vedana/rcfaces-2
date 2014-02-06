/*
 * $Id: GroupRefLiteral.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.RefName;

public class GroupRefLiteral extends AbstractGroupLiterals {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(stats, jsFile, new ISetLiteralCondition() {

            public boolean testLiteral(Assignment assignment,
                    Assignment original) {
                if (RefName.class.isInstance(assignment.getRight()) == false) {
                    return false;
                }

                if (original == null) {
                    return true;
                }

                if (((RefName) assignment.getRight()).getName().equals(
                        ((RefName) original.getRight()).getName())) {
                    return true;
                }

                return false;
            }

            public String getLiteralName(Assignment assignment) {
                return ((RefName) assignment.getRight()).getName();
            }

            public String getName() {
                return "Ref";
            }

        });
    }

}
