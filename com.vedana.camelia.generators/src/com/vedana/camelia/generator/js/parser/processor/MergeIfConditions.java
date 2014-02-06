/*
 * $Id: MergeIfConditions.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.ParenthesizedExpression;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.UnaryExpression;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class MergeIfConditions implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        Assignment assignments[] = Visitors.visitAssignments(jsFile
                .getDocument().getStatements(), false);

        for (Assignment assignment : assignments) {

            if ((assignment.getLeft() instanceof Name) == false) {
                continue;
            }

            Name defName = (Name) assignment.getLeft();

            if ((assignment.getNextSibling() instanceof IfStatement) == false) {
                continue;
            }

            IfStatement ifStatement = (IfStatement) assignment.getNextSibling();

            RefName refName = null;

            Expression cond = ifStatement.getCondition();

            if (cond instanceof RefName) {
                refName = (RefName) ifStatement.getCondition();

            } else if (cond instanceof UnaryExpression) {
                UnaryExpression unaryExpression = (UnaryExpression) cond;

                if (unaryExpression.getExpression() instanceof RefName) {
                    refName = (RefName) unaryExpression.getExpression();
                }
            }

            if (refName == null) {
                continue;
            }

            if (refName.getName().equals(defName.getName()) == false) {
                continue;
            }

            System.out.println("Optimize: merge assignement into if condition "
                    + ifStatement.getCondition());

            assignment.replaceBy(null);
            refName.replaceBy(new ParenthesizedExpression(assignment, null));

            modified = true;
        }

        Value values[] = Visitors.visitValues(jsFile.getDocument()
                .getStatements(), false);

        next_value: for (int i = 0; i < values.length; i++) {
            Value value = values[i];

            DefName defName = value.getName();

            IfStatement ifStatement;

            if ((value.getParent() instanceof VarExpression) == false) {
                continue;
            }

            VarExpression ve = (VarExpression) value.getParent();

            for (Value v = (Value) value.getNextSibling(); v != null; v = (Value) v
                    .getNextSibling()) {

                if (v.getRight() != null) {
                    // System.out.println("On laisse tombé !");
                    continue next_value;
                }
            }

            if ((ve.getNextSibling() instanceof IfStatement) == false) {
                continue;
            }

            ifStatement = (IfStatement) ve.getNextSibling();

            if ((ifStatement.getCondition() instanceof RefName) == false) {
                continue;
            }

            RefName refName = (RefName) ifStatement.getCondition();

            if (refName.getName().equals(defName.getName()) == false) {
                continue;
            }

            System.out.println("Optimize: merge value into if condition "
                    + ifStatement.getCondition());

            Expression right = value.getRight();
            value.setRight(null);

            refName.replaceBy(new Assignment(null, new RefName(refName
                    .getName(), null), right, null));

            modified = true;
        }

        return modified;
    }

}
