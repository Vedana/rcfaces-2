/*
 * $Id: MergeIfReturns.java,v 1.2 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.HookExpression;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.ParenthesizedExpression;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.ReturnStatement;
import com.vedana.js.dom.UndefinedLiteral;

public class MergeIfReturns implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        IfStatement rs[] = Visitors.visitIfs(jsFile.getDocument()
                .getStatements(), false);

        for (IfStatement i : rs) {
            ASTNode next = i.getNextSibling();

            boolean returnNext = next instanceof ReturnStatement;

            if (verifiyIf(i, returnNext) == false) {
                continue;
            }

            Expression ifTrue = ((ReturnStatement) i.getIfTrue())
                    .getExpression();
            Expression ifFalse = null;
            if ((ReturnStatement) i.getIfFalse() != null) {
                ifFalse = ((ReturnStatement) i.getIfFalse()).getExpression();
            }

            if (returnNext && ifFalse == null) {
                ifFalse = ((ReturnStatement) next).getExpression();
            }

            if (ifFalse == null) {
                ifFalse = new UndefinedLiteral(null);
                System.err.println("WARNING: not define FALSE expression ! "
                        + i);
            }
            if (ifTrue == null) {
                ifTrue = new UndefinedLiteral(null);
                // System.err.println("WARNING: not define TRUE expression ! " +
                // i);

                // Ca peut etre normal !
            }

            Expression newExpression;

            if (Tools.isTrueLiteral(ifTrue) && Tools.isFalseLiteral(ifFalse)) {
                newExpression = new PrefixExpression(Operation.NOT,
                        new PrefixExpression(Operation.NOT,
                                new ParenthesizedExpression(i.getCondition(),
                                        null), null), null);

            } else if (Tools.isFalseLiteral(ifTrue)
                    && Tools.isTrueLiteral(ifFalse)) {
                newExpression = new PrefixExpression(Operation.NOT,
                        new ParenthesizedExpression(i.getCondition(), null),
                        null);

            } else {

                newExpression = new HookExpression(new ParenthesizedExpression(
                        i.getCondition(), null), new ParenthesizedExpression(
                        ifTrue, null), new ParenthesizedExpression(ifFalse,
                        null), null);
            }

            ReturnStatement returnStatement = new ReturnStatement(
                    newExpression, i.getRegion());

            i.replaceBy(returnStatement);

            if (returnNext) {
                next.replaceBy(null);
            }

            System.out.println("Optimize: Merge if returns " + returnStatement);

        }

        return modified;
    }

    private boolean verifiyIf(IfStatement i, boolean nextReturn) {

        if (i.getIfTrue() instanceof IfStatement) {
            // if (verifiyIf((IfStatement) i.getIfTrue(), false) == false) {
            return false;
            // }

        } else if ((i.getIfTrue() instanceof ReturnStatement) == false) {
            return false;
        }

        if (i.getIfFalse() instanceof ReturnStatement) {
            return true;
        }

        if (i.getIfFalse() == null) {
            return nextReturn;
        }

        if (i.getIfFalse() instanceof IfStatement) {
            // return verifiyIf((IfStatement) i.getIfFalse(), false);
        }

        return false;
    }
}
