/*
 * $Id: MergeIfCascades.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.BinaryExpression;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.ParenthesizedExpression;
import com.vedana.js.dom.Statement;

public class MergeIfCascades implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        IfStatement ifStatements[] = Visitors.visitIfs(jsFile.getDocument()
                .getStatements(), false);

        for (IfStatement ifStatement : ifStatements) {
            if (ifStatement.getIfFalse() != null) {
                continue;
            }

            Statement expression = ifStatement.getIfTrue();

            if (expression == null) {
                continue;
            }

            if (expression instanceof Block) {
                if (((Block) expression).getStatements().size() != 1) {
                    continue;
                }

                expression = (Statement) ((Block) expression).getStatements()
                        .get(0);
            }

            if ((expression instanceof IfStatement) == false) {
                continue;
            }

            IfStatement ifStatement2 = (IfStatement) expression;

            if (ifStatement2.getIfFalse() != null) {
                continue;
            }

            String if2toString = ifStatement2.toString();

            Expression cond = ifStatement.getCondition();
            ifStatement.setCondition(null);

            Expression cond2 = ifStatement2.getCondition();
            ifStatement2.setCondition(cond2);

            Statement ifTrue2 = ifStatement2.getIfTrue();
            ifStatement2.setIfTrue(null);

            BinaryExpression bin = new InfixExpression(Operation.AND,
                    new ParenthesizedExpression(cond, null),
                    new ParenthesizedExpression(cond2, null), null);

            ifStatement.setCondition(bin);

            ifStatement.setIfTrue(ifTrue2);

            System.out.println("Optimize: merge cascaded if statements: "
                    + ifStatement + " and " + if2toString);
            modified = true;
        }

        return modified;
    }

}
