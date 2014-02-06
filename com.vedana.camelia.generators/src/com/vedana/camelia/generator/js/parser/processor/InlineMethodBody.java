/*
 * $Id: InlineMethodBody.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.IJsMethod;
import com.vedana.camelia.generator.js.parser.IJsType;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ThisLiteral;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class InlineMethodBody implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        // On recherche des appels statiques ou un this.xxxx

        boolean modified = false;

        FieldAccess fas[] = Visitors.visitFieldAccess(jsFile.getDocument()
                .getStatements(), false);
        for (FieldAccess fa : fas) {
            if ((fa.getParent() instanceof MethodInvocation) == false) {
                continue;
            }
            
            MethodInvocation methodInvocation = (MethodInvocation) fa
                    .getParent();

            if (fa.getObject() instanceof RefName) {
                String name = ((RefName) fa.getObject()).getName();

                IJsClass jsClass = stats.getJsClass(name);
                if (jsClass == null) {
                    continue;
                }

                IJsMethod member = (IJsMethod) jsClass.getMember(fa
                        .getProperty().getName());
                if (member == null) {
                    continue;
                }

                if (member.isStatic() == false
                        || member.isMemberOrClassFinal() == false) {
                    continue;
                }

                modified |= inlineStaticMethodAccess(stats, methodInvocation,
                        member);

                continue;
            }

            if (fa.getObject() instanceof ThisLiteral) {

                IJsMethod member = searchMember(stats, jsFile.getJsClass(), fa
                        .getProperty().getName());

                if (member == null || member.isMemberOrClassFinal() == false) {
                    continue;
                }

                modified |= inlineThisMethodAccess(stats, methodInvocation,
                        member);

                continue;
            }
        }

        return modified;
    }

    private IJsMethod searchMember(JsStats stats, IJsClass clazz, String name) {

        IJsMethod member = (IJsMethod) clazz.getMember(name);
        if (member != null) {
            return member;
        }

        if (clazz.getParent() != null) {
            if (clazz.getParent().getJsClass() != null) {
                member = searchMember(stats, clazz.getParent().getJsClass(),
                        name);
                if (member != null && member.isAbstract() == false) {
                    return member;
                }
            }
        }

        for (IJsType aspect : clazz.listAspects()) {
            if (aspect.getJsClass() == null) {
                continue;
            }

            member = searchMember(stats, aspect.getJsClass(), name);
            if (member != null && member.isAbstract() == false) {
                return member;
            }
        }

        return null;
    }

    private boolean inlineThisMethodAccess(JsStats stats,
            MethodInvocation methodInvocation, IJsMethod member) {

        FunctionDeclaration fd = member.getDeclaration();
        Block body = fd.getBody();

        // On ne peut pas traiter le super !!!
        if (Tools.containsSuperCall(body)) {
            return false;
        }

        if (methodInvocation.getParent() instanceof Block) {
            // Pas de return dans l'appel à remplacer !!!
            if (Tools.countReturn(body) > 0) {
                // Il y a un return ....

                // On laisse tomber !

                return false;
            }
        } else {
            // On a besoin d'une valeur de retour !

            if (Tools.countReturn(fd.getBody()) != 1) {
                // Probleme, pas de return, ou trop de return !
                return false;
            }
        }

        int count = Visitors.countNodes(body.getStatements());
        if (count > 32) {
            // Le code est trop gros !
            return false;
        }

        System.out.println("YO !");

        return true;
    }

    private boolean inlineStaticMethodAccess(JsStats stats,
            MethodInvocation methodInvocation, IJsMethod member) {

        System.out.println("YO !");

        return false;
    }

}
