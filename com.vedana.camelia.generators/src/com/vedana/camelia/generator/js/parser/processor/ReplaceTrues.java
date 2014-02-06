/*
 * $Id: ReplaceTrues.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.TrueLiteral;

public class ReplaceTrues implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        TrueLiteral trues[] = Visitors.visitTrues(jsFile.getDocument()
                .getStatements(), false);
        for (TrueLiteral trueNode: trues) {

            stats.removeChars("true");

            PrefixExpression not = new PrefixExpression(Operation.NOT,
                    new NumberLiteral(0, null), trueNode.getRegion());
            trueNode.replaceBy(not);
            modified = true;

            not
                    .putProperty(JsOptimizer.FINAL_VALUE_PROPERTY,
                            TrueLiteral.class);
        }

        return modified;
    }

}
