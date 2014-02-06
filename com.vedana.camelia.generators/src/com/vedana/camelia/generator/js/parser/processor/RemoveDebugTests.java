/*
 * $Id: RemoveDebugTests.java,v 1.2 2011/10/12 15:54:01 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Statement;

public class RemoveDebugTests implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        IfStatement ifs[] = Visitors.visitIfs(jsFile.getDocument()
                .getStatements(), false);
        for (int i = 0; i < ifs.length; i++) {
            IfStatement ifNode = ifs[i];

            Expression exp = ifNode.getCondition();

            if (replaceIsXXXEnabled(exp, ifNode)) {
                modified = true;
                continue;
            }

        }

        return modified;
    }

    private boolean replaceIsXXXEnabled(Expression exp, IfStatement ifNode) {
        if (exp instanceof InfixExpression) {
            InfixExpression binaryExpression = (InfixExpression) exp;

            if (binaryExpression.getOperation() == Operation.AND) {
                if (replaceIsXXXEnabled(binaryExpression.getLeft(), ifNode)) {
                    return true;
                }

                if (replaceIsXXXEnabled(binaryExpression.getRight(), ifNode)) {
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

        if (propertyName.equals("IsDebugEnabled") == false
                && propertyName.equals("IsInfoEnabled") == false
                && propertyName.equals("IsWarnEnabled") == false
                && propertyName.equals("IsTraceEnabled") == false) {
            return false;
        }

        Expression object = fieldAccess.getObject();
        if ((object instanceof RefName) == false) {
            return false;
        }

        if (((RefName) object).getName().equals("f_core") == false) {
            return false;
        }

        Statement falseStatement = ifNode.getIfFalse();
        if (falseStatement != null) {
            if (((Block) falseStatement).getStatements().size() == 1) {
                ifNode.replaceBy((ASTNode) ((Block) falseStatement)
                        .getStatements().get(0));
                return true;
            }
            ifNode.replaceBy(ifNode.getIfFalse());
            return true;
        }

        ifNode.replaceBy(null);

        return true;
    }

}
