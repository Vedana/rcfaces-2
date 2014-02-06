/*
 * $Id: MergeReturnValueAssignement.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ReturnStatement;
import com.vedana.js.dom.VarExpression;

public class MergeReturnValueAssignement implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        ReturnStatement rs[] = Visitors.visitReturns(jsFile.getDocument()
                .getStatements(), false);

        for (ReturnStatement r : rs) {
            if (r.getExpression() == null) {
                continue;
            }

            NodeList ns = r.getParentList();
            if (ns == null || ns.size() == 1) {
                // Que le return !!!
                continue;
            }

            int idx = 0;
            for (int j = 0; j < ns.size(); j++) {

                if (ns.get(j) == r) {
                    break;
                }

                idx++;
            }

            if (idx == 0) {
                continue;
            }

            ASTNode pred = (ASTNode) ns.get(idx - 1);
            // Est-ce un assignement ?
            if ((pred instanceof Assignment) == false) {
                continue;
            }

            Assignment ass = (Assignment) pred;
            // On recherche l'assignement le plus à droite

            for (;;) {
                if (ass.getRight() instanceof Assignment) {
                    // Un assignement en cascade ...
                    ass = (Assignment) ass.getRight();
                    continue;
                }

                break;
            }

            if (Tools.isSameValue(r.getExpression(), ass.getRight()) == false) {
                continue;
            }

            ASTNode parent = pred.getParent();
            pred.replaceBy(null);

            if (parent instanceof VarExpression) {
                if (((VarExpression) parent).getValues().size() == 0) {
                    parent.replaceBy(null);
                }
            }

            r.setExpression((Expression) pred);

            System.out.println("Optimize: Merge return value affectation " + r);
            modified = true;
        }

        return modified;
    }
}
