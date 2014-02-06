/*
 * $Id: MoveClassDeclaration.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.List;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class MoveClassDeclaration implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        final List<DefName> defs = new ArrayList<DefName>();

        NodeList nodes = jsFile.getDocument().getStatements();

        nodes.accept(new ASTVisitor() {

            @Override
            public boolean visit(DefName node) {
                String name = node.getName();

                if (name.equals("__members") == false
                        && name.equals("__statics") == false
                        && name.equals("__resources") == false) {
                    return false;
                }

                if ((node.getParent() instanceof Value) == false) {
                    return false;
                }

                Value value = (Value) node.getParent();

                if ((value.getParent() instanceof VarExpression) == false) {
                    return false;
                }

                VarExpression varExpression = (VarExpression) value.getParent();

                if (varExpression.getValues().size() != 1) {
                    // return false;
                }

                defs.add(node);

                return false;
            }
        });

        boolean modified = false;

        for (final DefName d : defs) {
            final List<RefName> l = new ArrayList<RefName>();

            nodes.accept(new ASTVisitor() {

                @Override
                public boolean visit(RefName name) {
                    if (name.getName().equals(d.getName())) {
                        l.add(name);
                    }
                    return false;
                }

            });

            if (l.isEmpty()) {
                System.out.println("Optimize: remove empty access '"
                        + d.getName() + "' ???");

                d.replaceBy(null);
                modified = true;
                continue;
            }

            /*
             * NE MARCHE PAS if (false && l.isEmpty()) { // Pas référencé !
             * nodes.replaceBy(null, d.getParent().getParent()); continue; }
             */
            if (l.size() != 1) {
                continue;
            }

            Value value = (Value) d.getParent();

            VarExpression varExpression = (VarExpression) value.getParent();

            if (varExpression.getValues().size() == 1) {
                System.out.println("Optimize: remove empty declaration (2) '"
                        + d.getName() + "' " + varExpression);

                nodes.replaceBy(null, varExpression);
                modified = true;

            } else {
                value.replaceBy(null);
                modified = true;

                System.out.println("Optimize: remove declaration '"
                        + d.getName() + "' " + varExpression);
            }

            RefName refName = l.get(0);

            if ((value.getRight() instanceof ObjectLiteral)
                    && ((ObjectLiteral) value.getRight()).getValues().size() == 0
                    && (refName.getParent() instanceof Value)) {

                System.out.println("Optimize: remove definition '"
                        + d.getName() + "' " + varExpression
                        + " (empty literal object)");
                refName.getParent().replaceBy(null);
                continue;
            }

            refName.replaceBy(value.getRight());
            modified = true;

            // System.out.println(d);
        }

        return modified;
    }

}
