/*
 * $Id: InstanceOfLevel3.java,v 1.2 2011/10/20 16:19:20 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.IJsMember;
import com.vedana.camelia.generator.js.parser.JsMember;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.StringLiteral;

public class InstanceOfLevel3 implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        InfixExpression fas[] = Visitors.visitInfixes(jsFile.getDocument()
                .getStatements(), Operation.INSTANCE_OF, false);

        for (InfixExpression exp : fas) {

            IJsMember jsMember = null;
            for (ASTNode a = exp; a != null; a = a.getParent()) {
                if (a instanceof FunctionDeclaration) {

                    jsMember = (JsMember) a
                            .getProperty(JsOptimizer.MEMBER_PROPERTY);
                    if (jsMember != null) {
                        break;
                    }

//                    System.out.println("Invalid jsMember ? " + jsMember);

                } else if (a instanceof ObjectLiteral) {
                    jsMember = (JsMember) a
                            .getProperty(JsOptimizer.MEMBER_PROPERTY);
                    if (jsMember != null) {
                        break;
                    }

//                    System.out.println("Invalid jsMember ? " + jsMember);

                }
            }

            if (jsMember == null) {
                continue;
            }

            String contextInfo = jsMember.getMetaDoc("dontReplaceInstanceOf");
            if (contextInfo != null) {
                continue;
            }

            Expression right = exp.getRight();
            if ((right instanceof RefName) == false) {
                continue;
            }

            modified = true;

            RefName refName = new RefName("_rcfacesIO", null);

            MethodInvocation mi = new MethodInvocation(refName,
                    new Expression[] {
                            exp.getLeft(),
                            new StringLiteral(((RefName) right).getName(),
                                    right.getRegion()),
                            new RefName("window", null) }, exp.getRegion());

            exp.replaceBy(mi);
        }

        return modified;
    }
}
