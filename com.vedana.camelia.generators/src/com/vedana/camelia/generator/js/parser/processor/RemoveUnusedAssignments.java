/*
 * $Id: RemoveUnusedAssignments.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class RemoveUnusedAssignments implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        Set<String> alreadyDone = new HashSet<String>();

        Assignment assignments[] = Visitors.visitAssignments(jsFile
                .getDocument().getStatements(), false);

        for (Assignment assignment : assignments) {

            if ((assignment.getLeft() instanceof Name) == false) {
                continue;
            }

            final Name name = (Name) assignment.getLeft();

            if (alreadyDone.add(name.getName()) == false) {
                continue;
            }

            final List<Assignment> access = new ArrayList<Assignment>();
            access.add(assignment);

            final boolean failed[] = new boolean[1];

            jsFile.getDocument().getStatements().accept(new ASTVisitor() {

                @Override
                public boolean visit(DefName node) {
                    return visitName(node);
                }

                @Override
                public boolean visit(RefName node) {
                    return visitName(node);
                }

                protected boolean visitName(Name n) {
                    if (n == name) {
                        return true;
                    }

                    if (n.getName().equals(name.getName()) == false) {
                        return true;
                    }

                    if (n.getParent() instanceof Value) {
                        Value v = (Value) n.getParent();

                        if (v.getValue() == null) {
                            // Un var tout seul ... on continue

                            access.add(v);
                            return true;
                        }

                        failed[0] = true; // Dans la valeur ... pas d'optim
                        return false;
                    }

                    if (n.getParent() instanceof Assignment) {
                        Assignment a = (Assignment) n.getParent();

                        if (a.getLeft() == n) {
                            // A gauche de l'assignement ... OK ... on continue

                            access.add(a);
                            return true;
                        }

                        failed[0] = true; // A droite ... pas d'optim
                        return false;

                    }

                    // Un accés normal !!!! pas d'optim
                    failed[0] = true;
                    return false;
                }
            });

            if (failed[0]) {
                continue;
            }

            System.out.println("Unused ? " + assignment + " " + access);
            for (Assignment as : access) {
                if (as instanceof Value) {

                    // Une valeur .... on peut peut-etre la deplacer

                    Value v = (Value) as;

                    if (v.getRight() == null) {

                        System.out.println("Optimize: remove unused var '"
                                + name.getName() + "' " + v);

                        if (((VarExpression) v.getParent()).getValues().size() == 1) {
                            v.getParent().replaceBy(null);
                            continue;
                        }

                        v.replaceBy(null);
                        continue;
                    }

                    Tools.removeValueAndMoveExpression(v);
                    continue;
                }

                System.out.println("Optimize: remove unused var '"
                        + name.getName() + "' assignment " + as);
                as.replaceBy(as.getRight());
            }

        }

        return modified;
    }
}
