/*
 * $Id: SimplifyEmptyFinally.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.TryStatement;

public class SimplifyEmptyFinally implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        TryStatement tries[] = Visitors.visitTries(jsFile.getDocument()
                .getStatements(), false);

        for (TryStatement tryStatement : tries) {
            Block finallyBlock = tryStatement.getFinally();
            if (finallyBlock != null
                    && finallyBlock.getStatements().size() == 0) {

                if (tryStatement.getCatchClauses().size() == 0) {
                    System.out.println("Optimize: Simplify try/finally block "
                            + tryStatement);

                    tryStatement.replaceBy(tryStatement.getTryBlock());

                    modified = true;
                }
            }
        }

        return modified;
    }

}
