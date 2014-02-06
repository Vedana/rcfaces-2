/*
 * $Id: RemoveTargetTests.java,v 1.1 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsOptimizer.Target;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Statement;
import com.vedana.js.dom.TrueLiteral;

public class RemoveTargetTests implements IJsFileProcessor {

    private static final String[] methodNames = { "IsInternetExplorer",
            "IsGecko", "IsWebkit" };

    private final Target target;

    public RemoveTargetTests(Target target) {
        this.target = target;
    }

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        String methodName = getMethodName();

        IfStatement ifs[] = Visitors.visitIfs(jsFile.getDocument()
                .getStatements(), false);
        for (int i = 0; i < ifs.length; i++) {
            IfStatement ifNode = ifs[i];

            Expression exp = ifNode.getCondition();

            for (String mn : methodNames) {
                if (mn.equals(methodName)) {
                    if (replaceByTrueIfNoParam(exp, ifNode, mn)) {
                        modified = true;
                    }
                    continue;
                }

                if (replaceByFalse(exp, ifNode, mn)) {
                    modified = true;
                }
            }
        }

        return modified;
    }

    private String getMethodName() {
        switch (target) {
        case fx:
            return "IsGecko";

        case ie:
            return "IsInternetExplorer";

        default:
            return "IsWebkit";
        }
    }

    private boolean replaceByFalse(Expression exp, ASTNode refNode,
            String methodName) {
        if (exp instanceof InfixExpression) {
            InfixExpression binaryExpression = (InfixExpression) exp;

            if (binaryExpression.getOperation() == Operation.AND) {
                if (replaceByFalse(binaryExpression.getLeft(), refNode,
                        methodName)) {
                    return true;
                }

                if (replaceByFalse(binaryExpression.getRight(), refNode,
                        methodName)) {
                    return true;
                }
            }

            if (binaryExpression.getOperation() == Operation.OR) {
                if (replaceByFalse(binaryExpression.getLeft(),
                        binaryExpression.getLeft(), methodName)) {
                    return true;
                }

                if (replaceByFalse(binaryExpression.getRight(),
                        binaryExpression.getRight(), methodName)) {
                    return true;
                }
            }
        }

        if ((exp instanceof MethodInvocation) == false) {
            return false;
        }
        MethodInvocation methodInvocation = (MethodInvocation) exp;

        exp = methodInvocation.getObject();
        if ((exp instanceof FieldAccess) == false) {
            return false;
        }

        FieldAccess fieldAccess = (FieldAccess) exp;
        String propertyName = fieldAccess.getProperty().getName();

        if (propertyName.equals(methodName) == false) {
            return false;
        }

        Expression object = fieldAccess.getObject();
        if ((object instanceof RefName) == false) {
            return false;
        }

        if (refNode instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) refNode;

            Statement falseStatement = ifStatement.getIfFalse();
            if (falseStatement != null) {
                if ((falseStatement instanceof Block)
                        && ((Block) falseStatement).getStatements().size() == 1) {
                    refNode.replaceBy((ASTNode) ((Block) falseStatement)
                            .getStatements().get(0));
                    System.out.println("Optimize: Inline target FALSE "
                            + ((RefName) object).getName() + " "
                            + methodInvocation);
                    return true;
                }
                refNode.replaceBy(ifStatement.getIfFalse());
                System.out
                        .println("Optimize: Optimize target FALSE "
                                + ((RefName) object).getName() + " "
                                + methodInvocation);
                return true;
            }

            System.out.println("Optimize: Remove target FALSE "
                    + ((RefName) object).getName() + " " + methodInvocation);
            refNode.replaceBy(null);

        } else {
            System.out.println("Optimize: Set target to FALSE "
                    + ((RefName) object).getName() + " " + methodInvocation);
            refNode.replaceBy(new FalseLiteral(refNode.getRegion()));
        }

        return true;
    }

    private boolean replaceByTrueIfNoParam(Expression exp, Statement refNode,
            String methodName) {
        if (exp instanceof InfixExpression) {
            InfixExpression binaryExpression = (InfixExpression) exp;

            if (binaryExpression.getOperation() == Operation.OR) {
                if (replaceByTrueIfNoParam(binaryExpression.getLeft(), refNode,
                        methodName)) {
                    return true;
                }

                if (replaceByTrueIfNoParam(binaryExpression.getRight(),
                        refNode, methodName)) {
                    return true;
                }
            }
            if (binaryExpression.getOperation() == Operation.AND) {
                if (replaceByTrueIfNoParam(binaryExpression.getLeft(),
                        binaryExpression.getLeft(), methodName)) {
                    return true;
                }

                if (replaceByTrueIfNoParam(binaryExpression.getRight(),
                        binaryExpression.getRight(), methodName)) {
                    return true;
                }
            }
        }

        if ((exp instanceof MethodInvocation) == false) {
            return false;
        }
        MethodInvocation methodInvocation = (MethodInvocation) exp;

        if (methodInvocation.getParameters().size() > 0) {
            return false;
        }

        exp = methodInvocation.getObject();
        if ((exp instanceof FieldAccess) == false) {
            return false;
        }

        FieldAccess fieldAccess = (FieldAccess) exp;
        String propertyName = fieldAccess.getProperty().getName();

        if (propertyName.equals(methodName) == false) {
            return false;
        }

        Expression object = fieldAccess.getObject();
        if ((object instanceof RefName) == false) {
            return false;
        }

        if (refNode instanceof IfStatement) {
            IfStatement ifNode = (IfStatement) refNode;

            Statement trueStatement = ifNode.getIfTrue();
            if (trueStatement != null) {
                if ((trueStatement instanceof Block)
                        && ((Block) trueStatement).getStatements().size() == 1) {
                    refNode.replaceBy((ASTNode) ((Block) trueStatement)
                            .getStatements().get(0));

                    System.out.println("Optimize: Inline target TRUE "
                            + ((RefName) object).getName() + " "
                            + methodInvocation);
                    return true;
                }
                refNode.replaceBy(trueStatement);
                System.out
                        .println("Optimize: Simplify target TRUE "
                                + ((RefName) object).getName() + " "
                                + methodInvocation);
                return true;
            }

            System.out.println("Optimize: Remove target TRUE "
                    + ((RefName) object).getName() + " " + methodInvocation);
            refNode.replaceBy(null);

        } else {
            System.out.println("Optimize: Set target TRUE "
                    + ((RefName) object).getName() + " " + methodInvocation);
            refNode.replaceBy(new TrueLiteral(refNode.getRegion()));
        }
        return true;
    }
}
