/*
 * $Id: ConcatVars.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.VarExpression;

public class ConcatVars implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;
        VarExpression vars[] = Visitors.visitVarExpressions(jsFile
                .getDocument().getStatements(), false);

        for (VarExpression varNode : vars) {
            if (varNode.getParent() == null) {
                // D�ja trait� !
                continue;
            }

            ASTNode next = varNode.getNextSibling();
            for (;;) {
                if ((next instanceof VarExpression) == false) {
                    break;
                }

                stats.removeChars("var");
                ASTNode nx2 = next.getNextSibling();
                next.replaceBy(null);
                varNode.getValues().addAll(
                        ((VarExpression) next).getValues().toArray());

                next = nx2;

                modified = true;
            }
        }

        return modified;
    }

}
