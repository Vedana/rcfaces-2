/*
 * $Id: InlineLevel3.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.TrueLiteral;

public class InlineLevel3 implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(stats, jsFile, "_RCFACES_LEVEL3");
    }

    protected boolean process(JsStats stats, IJsFile jsFile, String name) {

        boolean modified = false;

        FieldAccess fas[] = Visitors.visitFieldAccess(jsFile.getDocument()
                .getStatements(), false);

        for (int i = 0; i < fas.length; i++) {
            FieldAccess fa = fas[i];

            if (fa.getProperty().getName().equals(name) == false) {
                continue;
            }

            fa.replaceBy(new TrueLiteral(null));
            modified = true;
        }

        return modified;
    }

}
