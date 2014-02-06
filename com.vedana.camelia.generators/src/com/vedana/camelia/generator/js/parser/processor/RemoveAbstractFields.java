/*
 * $Id: RemoveAbstractFields.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;

public class RemoveAbstractFields implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        modified |= removeFields(jsFile.getDocument().getStatements(),
                "f_class", "ABSTRACT");
        modified |= removeFields(jsFile.getDocument().getStatements(),
                "f_class", "OPTIONAL_ABSTRACT");

        return modified;
    }

    private static final boolean removeFields(NodeList nodes, String claz,
            String method) {
        boolean modified = false;

        FieldAccess calls[] = Visitors.visitFieldAccess(nodes, false);
        for (FieldAccess call : calls) {
            Expression p1 = call.getObject();
            if ((p1 instanceof RefName) == false) {
                continue;
            }

            if (((RefName) p1).getName().equals(claz) == false) {
                continue;
            }

            if (call.getProperty().getName().equals(method) == false) {
                continue;
            }

            if ((call.getParent() instanceof Value) == false) {
                continue;
            }

            Value value = (Value) call.getParent();

            value.replaceBy(null);

            modified = true;
        }

        return modified;
    }

}
