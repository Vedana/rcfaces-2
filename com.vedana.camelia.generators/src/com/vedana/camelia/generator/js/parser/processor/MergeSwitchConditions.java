/*
 * $Id: MergeSwitchConditions.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.SwitchStatement;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class MergeSwitchConditions implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        Assignment assignments[] = Visitors.visitAssignments(jsFile
                .getDocument().getStatements(), false);

        for (Assignment assignment : assignments) {
            if ((assignment.getLeft() instanceof Name) == false) {
                continue;
            }

            Name defName = (Name) assignment.getLeft();

            if ((assignment.getNextSibling() instanceof SwitchStatement) == false) {
                continue;
            }

            SwitchStatement ifStatement = (SwitchStatement) assignment
                    .getNextSibling();

            if ((ifStatement.getExpression() instanceof RefName) == false) {
                continue;
            }

            RefName refName = (RefName) ifStatement.getExpression();

            if (refName.getName().equals(defName.getName()) == false) {
                continue;
            }

            System.out
                    .println("Optimize: merge assignement into switch condition "
                            + ifStatement.getExpression());

            assignment.replaceBy(null);
            refName.replaceBy(assignment);

            modified = true;
        }

        Value values[] = Visitors.visitValues(jsFile.getDocument()
                .getStatements(), false);

        next_value: for (int i = 0; i < values.length; i++) {
            Value value = values[i];

            DefName defName = value.getName();

            SwitchStatement ifStatement;

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

            if ((ve.getNextSibling() instanceof SwitchStatement) == false) {
                continue;
            }

            ifStatement = (SwitchStatement) ve.getNextSibling();

            if ((ifStatement.getExpression() instanceof RefName) == false) {
                continue;
            }

            RefName refName = (RefName) ifStatement.getExpression();

            if (refName.getName().equals(defName.getName()) == false) {
                continue;
            }

            System.out.println("Optimize: merge value into switch condition "
                    + ifStatement.getExpression());

            Expression right = value.getRight();
            value.setRight(null);

            refName.replaceBy(new Assignment(null, new RefName(refName
                    .getName(), null), right, null));

            modified = true;
        }

        return modified;
    }

}
