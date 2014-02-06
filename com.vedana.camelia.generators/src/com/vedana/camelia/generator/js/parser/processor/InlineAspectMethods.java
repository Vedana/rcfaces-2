/*
 * $Id: InlineAspectMethods.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.lang.reflect.Modifier;
import java.util.List;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsMethod;
import com.vedana.camelia.generator.js.parser.IJsType;
import com.vedana.camelia.generator.js.parser.JsClass;
import com.vedana.camelia.generator.js.parser.JsMethod;
import com.vedana.camelia.generator.js.parser.JsModifier;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Statement;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.Value;

public class InlineAspectMethods implements IJsClassProcessor {

    private static final String ASPECT_INLINED = "ASPECT INLINED";

    public boolean process(JsStats stats, IJsClass aspect) {

        if (aspect.getMetaProperty(ASPECT_INLINED) != null) {
            return false;
        }

        aspect.putMetaProperty(ASPECT_INLINED, Boolean.TRUE);

        boolean modified = false;

        for (IJsType parent : aspect.listAspects()) {
            IJsClass parentClass = parent.getJsClass();
            if (parentClass == null) {
                continue;
            }

            modified |= process(stats, parentClass);
        }

        List<IJsClass> classesWhichUseAspect = aspect.listReverseAspect();

        modified |= inlineAspectMethod(stats, aspect, classesWhichUseAspect,
                aspect.getName(), JsModifier.AFTER, true);
        modified |= inlineAspectMethod(stats, aspect, classesWhichUseAspect,
                "f_finalize", JsModifier.BEFORE, false);

        /*
        modified |= inlineAspectMethod(stats, aspect, classesWhichUseAspect,
                "f_update", JsModifier.BEFORE, false);
        modified |= inlineAspectMethod(stats, aspect, classesWhichUseAspect,
                "f_update", JsModifier.AFTER, false);
*/
        return modified;
    }

    private static boolean inlineAspectMethod(JsStats stats, IJsClass jsClass,
            List<IJsClass> classesWhichUseAspect, String name, int defaultMask,
            boolean isConstructor) {

        IJsMethod member = (IJsMethod) jsClass.getMember(name, defaultMask);
        if (member == null && defaultMask != 0) {
            member = (IJsMethod) jsClass.getMember(name, 0);
        }
        if (member == null) {
            return false;
        }

        // Verifie la complexité de la méthode !
        // Si trop complexe, on laisse tomber !
        if (Tools.canInlineBlock(member.getDeclaration().getBody()) == false) {
            return false;
        }

        boolean modified = false;

        System.out
                .println("Optimize: inline aspect method "
                        + member.getJsClass().getName() + "."
                        + member.getName() + "()");

        for (IJsClass c : classesWhichUseAspect) {
            System.out.println("  into " + c.getName() + "." + member.getName()
                    + "()");

            if (isConstructor) {
                name = c.getName();
            }

            IJsMethod m = (IJsMethod) c.getMember(name);
            if (m == null) {
                // Il faut le créer !
                Block body;

                if (c.isAspect() == false) {
                    FieldAccess superObject = new FieldAccess(new ThisLiteral(
                            null), new RefName("f_super", null), null);

                    MethodInvocation callSuper = new MethodInvocation(
                            superObject, new Expression[] { new RefName(
                                    "arguments", null) }, null);

                    body = new Block(new Statement[] { callSuper }, null);

                } else {
                    body = new Block(new Statement[] {}, null);
                }

                FunctionDeclaration funcDeclaration = new FunctionDeclaration(
                        null, new Parameter[0], body, null);

                Value value = new Value(new DefName(name, null),
                        funcDeclaration, null);

                JsModifier modifier = new JsModifier(c.getName(), name,
                        funcDeclaration, JsModifier.METHOD_TYPE,
                        Modifier.PUBLIC);
                m = new JsMethod(c, value, funcDeclaration, false, modifier,
                        null);
                ((JsClass) c).newMember(m);
            }

            FunctionDeclaration fd = m.getDeclaration();

            Tools.inlineCode(stats, fd.getBody().getStatements(), member
                    .getDeclaration().getBody(),
                    (defaultMask == JsModifier.BEFORE) ? 0 : -1);

            modified = true;
        }

        ((JsClass) jsClass).removeMember(member);

        for (IJsClass c : classesWhichUseAspect) {
            if (c.isAspect() == false) {
                continue;
            }

            // On peut peut-etre retirer le SUPER !

            if (isConstructor) {
                name = c.getName();
            }

            IJsMethod m = (IJsMethod) c.getMember(name);
            if (m == null) {
                continue;
            }

            if (Tools.isMethodDecorated(c, name, isConstructor)) {
                continue;
            }

            // On peut supprimer le super !
            m.getDeclaration().getBody().getStatements().accept(
                    new ASTVisitor() {

                        @Override
                        public boolean visit(MethodInvocation invocation) {
                            Expression exp = invocation.getObject();

                            if (exp instanceof FieldAccess) {
                                FieldAccess fa = (FieldAccess) exp;

                                if (fa.getProperty().getName()
                                        .equals("f_super")) {
                                    if (fa.getObject() instanceof ThisLiteral) {
                                        invocation.replaceBy(null);
                                        return false;
                                    }
                                }
                            }

                            return true;
                        }
                    });

            modified = true;
        }

        return modified;
    }
}
