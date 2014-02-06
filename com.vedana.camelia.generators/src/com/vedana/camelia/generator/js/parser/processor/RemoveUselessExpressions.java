/*
 * $Id: RemoveUselessExpressions.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.Expression;

public class RemoveUselessExpressions implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        Block blocks[] = Visitors.visitBlocks(jsFile.getDocument()
                .getStatements(), false);

        for (Block b : blocks) {
            ASTNode ns[] = b.getStatements().toArray();

            for (int j = 0; j < ns.length; j++) {
                ASTNode node = ns[j];

                if ((node instanceof Expression) == false) {
                    continue;
                }

                Expression expression = (Expression) node;

                if (Tools.isUselessExpression(expression) == false) {
                    continue;
                }

                System.out.println("Optimize: remove useless code "
                        + expression);

                expression.replaceBy(null);

                modified = true;
            }
        }

        return modified;
    }

}
