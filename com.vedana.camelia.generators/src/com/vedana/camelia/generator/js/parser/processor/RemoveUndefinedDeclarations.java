/*
 * $Id: RemoveUndefinedDeclarations.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.UndefinedLiteral;
import com.vedana.js.dom.Value;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class RemoveUndefinedDeclarations implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        ObjectLiteral ols[] = Visitors.visitObjectLitterals(jsFile
                .getDocument().getStatements(), true);
        // Il ne faut surtout pas rentrer dans les fonctions !!!
        for (int i = 0; i < ols.length; i++) {
            ObjectLiteral ol = ols[i];

            NodeList values = ol.getValues();

            for (int j = 0; j < values.size();) {
                Value value = (Value) values.get(j);

                if ((value.getRight() instanceof UndefinedLiteral) == false) {
                    j++;
                    continue;
                }

                value.replaceBy(null);

                modified = true;
            }
        }

        Assignment asss[] = Visitors.visitAssignments(jsFile.getDocument()
                .getStatements(), true);
        // Il ne faut surtout pas rentrer dans les fonctions !!!
        for (int i = 0; i < asss.length; i++) {
            Assignment as = asss[i];

            if ((as.getLeft() instanceof FieldAccess) == false) {
                continue;
            }

            if ((as.getRight() instanceof UndefinedLiteral) == false) {
                continue;
            }

            as.replaceBy(null);

            modified = true;
        }

        return modified;
    }

}
