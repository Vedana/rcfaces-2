/*
 * $Id: RemoveUnusedVariables.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.ForInStatement;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class RemoveUnusedVariables extends SearchUnusedVariables {

    @Override
    protected void unusedParameterDetected(DefName df) {

        if (df.getParent().getParent().getParent() instanceof ForInStatement) {
            return;
        }

        if (df.getParent().getParent() instanceof VarExpression) {
            Value value = (Value) df.getParent();
            VarExpression varExpression = (VarExpression) value.getParent();

            if (value.getRight() == null
                    || Tools.isUselessExpression(value.getRight())) {
                System.out.println("Optimise: Remove unused var (1): " + df);
                value.replaceBy(null);

                if (varExpression.getValues().size() == 0) {
                    varExpression.replaceBy(null);
                }

            } else {
                if (varExpression.getValues().size() == 1) {

                    System.out
                            .println("Optimise: Remove unused var (2): " + df);
                    varExpression.replaceBy(value.getRight());

                } else {
                    // Rien...

                    Tools.removeValueAndMoveExpression(value);
                }
            }
        } else {
            System.out.println("????");
        }
    }

}
