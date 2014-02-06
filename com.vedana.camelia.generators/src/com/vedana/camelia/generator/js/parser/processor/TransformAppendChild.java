/*
 * $Id: TransformAppendChild.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.RefName;

public class TransformAppendChild implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        MethodInvocation mis[] = Visitors.visitMethodInvocations(jsFile
                .getDocument().getStatements(), false);

        for (int i = 0; i < mis.length; i++) {
            MethodInvocation mi = mis[i];

            if ((mi.getObject() instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess fa = (FieldAccess) mi.getObject();

            if ((fa.getObject() instanceof RefName) == false) {
                continue;
            }

            if (((RefName) fa.getObject()).getName().equals("f_core") == false) {
                continue;
            }

            if (fa.getProperty().getName().equals("AppendChild")) {
                FieldAccess fa2 = new FieldAccess((Expression) mi
                        .getParameters().get(0), new RefName("appendChild",
                        null), null);

                MethodInvocation mi2 = new MethodInvocation(fa2,
                        new Expression[] { (Expression) mi.getParameters().get(
                                1) }, null);

                mi.replaceBy(mi2);

                modified = true;
                continue;
            }

            if (fa.getProperty().getName().equals("InsertBefore")) {
                FieldAccess fa2 = new FieldAccess((Expression) mi
                        .getParameters().get(0), new RefName("insertBefore",
                        null), null);

                MethodInvocation mi2 = new MethodInvocation(fa2,
                        new Expression[] {
                                (Expression) mi.getParameters().get(1),
                                (Expression) mi.getParameters().get(2) }, null);

                mi.replaceBy(mi2);

                modified = true;
                continue;
            }
        }

        return modified;
    }

}
