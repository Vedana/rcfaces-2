/*
 * $Id: MergeDoubleAssigment.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.VarExpression;

public class MergeDoubleAssigment implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        Assignment rs[] = Visitors.visitAssignments(jsFile.getDocument()
                .getStatements(), false);

        next_return: for (Assignment r : rs) {

            if ((r.getRight() instanceof RefName) == false) {
                continue;
            }

            final RefName ref = (RefName) r.getRight();

            NodeList ns = r.getParentList();
            if (ns == null || ns.size() == 1) {
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
            if ((pred instanceof Assignment) == false) {
                continue;
            }

            Assignment ass = (Assignment) pred;

            for (;;) {
                if (ass.getLeft() instanceof RefName) {
                    break;
                }

                if (ass.getRight() instanceof Assignment) {
                    ass = (Assignment) ass.getRight();
                    continue;
                }

                continue next_return;
            }

            if (((RefName) ass.getLeft()).getName().equals(ref.getName()) == false) {
                continue;
            }

            ASTNode parent = pred.getParent();
            if (parent instanceof VarExpression) {
                // Aucun cas de figure !
                continue;
            }

            final boolean detected[] = new boolean[1];
            // On vérifie qu'a Gauche il n'y ait pas référence à la variable
            r.getLeft().accept(new ASTVisitor() {

                @Override
                public boolean visit(RefName name) {
                    if (ref.getName().equals(name.getName())
                            && (name.getParent() instanceof FieldAccess) == false) {
                        detected[0] = true;
                        return false;
                    }
                    return super.visit(name);
                }
            });

            if (detected[0]) {
                continue;
            }

            pred.replaceBy(null);

            r.setRight((Expression) pred);

            System.out.println("Optimize: Merge assignement affectation " + r);

            modified = true;
        }

        return modified;
    }

}
