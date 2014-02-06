/*
 * $Id: SimplifyExpression.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Operation;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.BinaryExpression;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.IOperation;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.ParenthesizedExpression;
import com.vedana.js.dom.TrueLiteral;
import com.vedana.js.dom.UnaryExpression;

public class SimplifyExpression implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        final boolean modifiedRef[] = new boolean[1];

        jsFile.getDocument().getStatements().accept(new ASTVisitor() {

            @Override
            public boolean preVisit(ASTNode node) {
                if (node instanceof Assignment) {
                    return true;
                }

                if ((node instanceof BinaryExpression)
                        || (node instanceof UnaryExpression)) {

                    int level = 0;
                    if (node.getParent() instanceof Expression) {
                        level = Tools.getLevel((Expression) node.getParent());
                    }

                    modifiedRef[0] = simplifyExpression((Expression) node,
                            level);

                    return false;
                }

                return true;
            }
        });

        return modifiedRef[0];
    }

    private boolean simplifyExpression(Expression expression, int predOperator) {
        boolean modified = false;

        if (expression instanceof ParenthesizedExpression) {
            Expression exp = ((ParenthesizedExpression) expression)
                    .getExpression();

            int curLevel = Tools.getLevel(exp);
            if (predOperator < Tools.getLevel(exp)) {
                // Pas le <= car il peut y avoir des problemes d'associativité
                // ex: 4+(c-'C') != 4+c-'C'
                // On remplace ...

                System.out.println("Optimize: simplify expression: "
                        + expression);

                expression.replaceBy(exp);

                modified = true;
            }

            modified |= simplifyExpression(exp, curLevel);
            return modified;
        }

        int level = Tools.getLevel(expression);

        if (expression instanceof InfixExpression) {
            InfixExpression inf = (InfixExpression) expression;

            modified |= simplifyExpression(inf.getLeft(), level);

            modified |= simplifyExpression(inf.getRight(), level);

            if (expression instanceof IOperation) {
                IOperation op = (IOperation) expression;

                if (op.getOperation() == Operation.AND) {
                    if (Tools.isFalseLiteral(inf.getLeft())
                            || Tools.isFalseLiteral(inf.getRight())) {
                        expression.replaceBy(new FalseLiteral(null));
                        modified = true;

                    } else if (Tools.isTrueLiteral(inf.getLeft())) {
                        expression.replaceBy(inf.getRight());
                        modified = true;

                    } else if (Tools.isTrueLiteral(inf.getRight())) {
                        expression.replaceBy(inf.getLeft());
                        modified = true;
                    }

                } else if (op.getOperation() == Operation.OR) {
                    if (Tools.isTrueLiteral(inf.getLeft())
                            || Tools.isTrueLiteral(inf.getRight())) {
                        expression.replaceBy(new TrueLiteral(null));
                        modified = true;

                    } else if (Tools.isFalseLiteral(inf.getLeft())) {
                        expression.replaceBy(inf.getRight());
                        modified = true;

                    } else if (Tools.isFalseLiteral(inf.getRight())) {
                        expression.replaceBy(inf.getLeft());
                        modified = true;
                    }
                }
            }

            return modified;
        }

        if (expression instanceof UnaryExpression) {
            UnaryExpression bin = (UnaryExpression) expression;

            if (bin instanceof IOperation) {
                if (((IOperation) bin).getOperation() == Operation.VOID) {
                    return modified;
                }
            }

            modified |= simplifyExpression(bin.getExpression(), level);
            return modified;
        }

        return modified;
    }
}
