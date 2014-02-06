/*
 * $Id: AbstractRemoveMethod.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.js.Visitors;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;

public abstract class AbstractRemoveMethod implements IJsFileProcessor {

    protected boolean process(IJsFile jsFile, String className,
            String methodName) {
        return process(jsFile, className, methodName, false);
    }

    protected boolean process(IJsFile jsFile, String className,
            String methodName, boolean removeMethod) {

        NodeList nodes = jsFile.getDocument().getStatements();

        boolean modified = false;

        if (removeMethod) {
            DefName defs[] = Visitors.visitDefNames(nodes, false);
            for (int i = 0; i < defs.length; i++) {
                if (defs[i].getName().equals(methodName) == false) {
                    continue;
                }

                defs[i].getParent().replaceBy(null);

                modified = true;
            }
        }

        MethodInvocation calls[] = Visitors
                .visitMethodInvocations(nodes, false);
        for (int i = 0; i < calls.length; i++) {
            MethodInvocation call = calls[i];

            Expression p1 = call.getObject();
            if ((p1 instanceof FieldAccess) == false) {
                continue;
            }

            if (methodName.equals(((FieldAccess) p1).getProperty().getName()) == false) {
                continue;
            }

            Expression p3 = ((FieldAccess) p1).getObject();
            if ((p3 instanceof RefName) == false) {
                continue;
            }

            if (className.equals(((RefName) p3).getName()) == false) {
                continue;
            }

            call.replaceBy(null);

            modified = true;
        }

        return modified;

    }

}
