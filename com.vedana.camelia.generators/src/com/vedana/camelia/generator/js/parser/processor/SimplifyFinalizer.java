/*
 * $Id: SimplifyFinalizer.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsMethod;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.NodeList;

public class SimplifyFinalizer implements IJsClassProcessor {

    public boolean process(JsStats stats, IJsClass jsClass) {

        IJsMethod finalizer = (IJsMethod) jsClass.getMember("f_finalize");
        if (finalizer == null) {
            return false;
        }

        NodeList nodeList = finalizer.getDeclaration().getBody()
                .getStatements();

        IfStatement ifStatements[] = Visitors.visitIfs(nodeList, false);

        boolean modified = false;

        for (IfStatement ifStatement : ifStatements) {
            if (ifStatement.getIfFalse() != null) {
                continue;
            }

            if ((ifStatement.getCondition() instanceof Assignment) == false) {
                continue;
            }

            if (ifStatement.getIfTrue() instanceof Block) {
                if (((Block) ifStatement.getIfTrue()).getStatements().size() == 1) {
                    ifStatement.getIfTrue().replaceBy(
                            (ASTNode) ((Block) ifStatement.getIfTrue())
                                    .getStatements().get(0));
                }
            }

            if ((ifStatement.getIfTrue() instanceof Assignment) == false) {
                continue;
            }

            Assignment assignmentCond = (Assignment) ifStatement.getCondition();
            Assignment assignmentTrue = (Assignment) ifStatement.getIfTrue();

            if (Tools.isUndefinedLiteral(assignmentTrue.getRight()) == false) {
                continue;
            }

            if ((assignmentTrue.getLeft() instanceof FieldAccess) == false) {
                continue;
            }

            if ((assignmentCond.getRight() instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess faCond = (FieldAccess) assignmentCond.getRight();
            FieldAccess faTrue = (FieldAccess) assignmentTrue.getLeft();

            if (faCond.getProperty().getName().equals(
                    faTrue.getProperty().getName()) == false) {
                continue;
            }

            if (Tools.isThisLiteral(faCond.getObject()) == false) {
                continue;
            }

            if (Tools.isThisLiteral(faTrue.getObject()) == false) {
                continue;
            }

            // TODO Verifie l'impact du Left du cond

            modified = true;
            ifStatement.replaceBy(assignmentTrue);
        }

        return modified;
    }

}
