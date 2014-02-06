/*
 * $Id: SimplifyLogName.java,v 1.1 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.IJsMember;
import com.vedana.camelia.generator.js.parser.JsMember;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.MultiWindowOptimizer;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ThisLiteral;

public class SimplifyLogName implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        NodeList nodes = jsFile.getDocument().getStatements();

        MethodInvocation[] mis = Visitors.visitMethodInvocations(nodes, false);

        for (MethodInvocation mi : mis) {
            Expression exp = mi.getObject();

            if ((exp instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess fa = (FieldAccess) exp;

            Expression exp2 = fa.getObject();
            if ((exp2 instanceof RefName) == false) {
                continue;
            }

            RefName obj = (RefName) exp2;
            RefName prop = fa.getProperty();
            if ("f_core".equals(obj.getName()) == false) {
                continue;
            }

            String pname = prop.getName();
            if (pname.equals("Debug") == false
                    && pname.equals("Error") == false) {
                continue;
            }

            if (mi.getParameters().size() < 2) {
                continue;
            }
            ASTNode param0 = (ASTNode) mi.getParameters().get(0);
            if ((param0 instanceof RefName) == false) {
                continue;
            }

            if (((RefName) param0).getName().equals(
                    jsFile.getJsClass().getName()) == false) {
                continue;
            }

            IJsMember member = getMember(mi);
            if (member == null) {
                continue;
            }

            if (MultiWindowOptimizer.getContextMetaType(member) != null) {
                continue;
            }

            if (member.isStatic()) {
                param0.replaceBy(new ThisLiteral(null));

                System.out.println("Optimize: Simplify log name (Static mode) "
                        + mi);
                modified = true;
                continue;
            }

            FieldAccess fa2 = new FieldAccess(new ThisLiteral(null),
                    new RefName("_kclass", null), null);

            param0.replaceBy(fa2);

            System.out
                    .println("Optimize: Simplify log name (Class mode) " + mi);
            modified = true;
        }

        return modified;
    }

    public IJsMember getMember(ASTNode exp) {

        IJsMember jsMember = null;
        for (ASTNode a = exp; a != null; a = a.getParent()) {
            if (a instanceof FunctionDeclaration) {

                jsMember = (JsMember) a
                        .getProperty(JsOptimizer.MEMBER_PROPERTY);
                if (jsMember != null) {
                    return jsMember;
                }

                // System.out.println("Invalid jsMember ? " + jsMember);

            } else if (a instanceof ObjectLiteral) {
                jsMember = (JsMember) a
                        .getProperty(JsOptimizer.MEMBER_PROPERTY);
                if (jsMember != null) {
                    return jsMember;
                }
            }
        }

        return null;
    }
}
