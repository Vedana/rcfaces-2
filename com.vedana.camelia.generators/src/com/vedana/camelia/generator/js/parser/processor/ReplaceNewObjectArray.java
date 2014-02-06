/*
 * $Id: ReplaceNewObjectArray.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ArrayLiteral;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.NewExpression;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;

public class ReplaceNewObjectArray implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        NewExpression news[] = Visitors.visitNews(jsFile.getDocument()
                .getStatements(), false);

        for (NewExpression newNode : news) {

            Expression name = newNode.getName();
            if ((name instanceof RefName) == false) {
                continue;
            }

            String na = ((RefName) name).getName();

            NodeList params = newNode.getParameters();
            if (params.size() > 0) {
                continue;
            }

            if (newNode.getAdditionalExpression() != null) {
                System.err.println("???? Additional expression !");
                continue;
            }

            if ("object".equalsIgnoreCase(na)) {
                stats.removeChars(na);

                ObjectLiteral oln = new ObjectLiteral(null, newNode.getRegion());
                newNode.replaceBy(oln);

                modified = true;
                continue;
            }

            if ("array".equalsIgnoreCase(na)) {
                stats.removeChars(na);

                ArrayLiteral oln = new ArrayLiteral((Expression[]) null,
                        newNode.getRegion());
                newNode.replaceBy(oln);

                modified = true;
                continue;
            }
        }

        return modified;

    }

}
