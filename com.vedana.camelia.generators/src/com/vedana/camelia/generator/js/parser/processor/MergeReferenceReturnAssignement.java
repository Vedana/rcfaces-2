/*
 * $Id: MergeReferenceReturnAssignement.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ReturnStatement;
import com.vedana.js.dom.VarExpression;

public class MergeReferenceReturnAssignement implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        ReturnStatement rs[] = Visitors.visitReturns(jsFile.getDocument()
                .getStatements(), false);

        for (ReturnStatement r : rs) {

            // On recherche
            // a=b=2;
            // return b;

            RefName ref = null;

            Expression expression = r.getExpression();

            if (expression instanceof RefName) {
                ref = (RefName) r.getExpression();

            } else if (expression instanceof MethodInvocation) {
                MethodInvocation mi = (MethodInvocation) expression;
                if (mi.getObject() instanceof FieldAccess) {
                    FieldAccess fa = (FieldAccess) mi.getObject();

                    if (fa.getObject() instanceof RefName) {
                        ref = (RefName) fa.getObject();
                    }
                }
            }

            if (ref == null) {
                continue;
            }

            NodeList ns = r.getParentList();
            if (ns == null || ns.size() == 1) {
                // Que le return !!!
                continue;
            }

            int idx = 0;
            for (int j = 0; j < ns.size(); j++) {

                if (ns.get(j) == r) {
                    break;
                }

                idx++;
            }

            if (idx == 0) {
                continue;
            }

            ASTNode pred = (ASTNode) ns.get(idx - 1);
            // Est-ce un assignement ?
            if (pred instanceof VarExpression) {
                NodeList vs = ((VarExpression) pred).getValues();
                pred = (ASTNode) vs.get(vs.size() - 1);
            }

            if ((pred instanceof Assignment) == false) {
                continue;
            }

            Assignment ass = (Assignment) pred;
            // On recherche l'assignement le plus à droite

            Name refAss = null;
            for (;;) {
                if (ass.getLeft() instanceof Name) {
                    if (((Name) ass.getLeft()).getName().equals(ref.getName())) {

                        refAss = (Name) ass.getLeft();
                    }
                }

                if (ass.getRight() instanceof Assignment) {
                    // Un assignement en cascade ...
                    ass = (Assignment) ass.getRight();
                    continue;
                }

                // Bizarre comme config !
                break;
            }

            if (ass.getRight() == null) {
                // On ne peut pas replacer juste var X
                continue;
            }

            if (refAss == null) {
                // On a pas trouvé de reference
                continue;
            }

            boolean addOperation = false;

            if (ass.getOperation() != null) {
                if (ass.getOperation() != Operation.ADD) {
                    System.err.println("Unsupported operation =" + ass);
                    continue;
                }

                addOperation = true;
            }

            // La variable est-elle locale ? (dans ce cas on peut peut-etre la
            // retirer de l'affectation)

            if (Tools.isLocalVariable(r, refAss)) {
                // Une variable locale, on peut peut-etre la retirer de
                // l'affectation !

                Assignment a = (Assignment) pred;
                for (;;) {
                    if ((a.getLeft() instanceof Name) == false) {
                        // On laisse tomber !
                        break;
                    }

                    if (((Name) a.getLeft()).getName().equals(refAss.getName())) {
                        // C'est notre variable !!!

                        Expression exp = a.getRight();

                        if (exp != null) {
                            pred.replaceBy(exp);
                            pred = exp;
                        }

                        break;
                    }

                    if (a.getRight() instanceof Assignment) {
                        a = (Assignment) a.getRight();
                        continue;
                    }

                    break;
                }

            }

            if (pred != null) {
                ASTNode parent = pred.getParent();
                pred.replaceBy(null);

                if (parent instanceof VarExpression) {
                    if (((VarExpression) parent).getValues().size() == 0) {
                        parent.replaceBy(null);
                    }
                }

                if (ref.getParent() instanceof FieldAccess) {
                    // System.out.println("Method invocation " +
                    // ref.getParent());

                    if (Tools.getLevel((Expression) pred) > Tools
                            .getLevel((FieldAccess) ref.getParent())) {

                        System.err.println("*** parentheses ?");
                    }
                }

            }

            if (addOperation) {
                pred = new InfixExpression(Operation.ADD, new RefName(ref
                        .getName(), null), (Expression) pred, null);
            }

            ref.replaceBy(pred);

            System.out.println("Optimize: Merge return affectation " + r);
            modified = true;
        }

        return modified;
    }

}
