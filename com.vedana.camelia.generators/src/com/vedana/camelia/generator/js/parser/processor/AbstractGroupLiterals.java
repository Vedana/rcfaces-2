/*
 * $Id: AbstractGroupLiterals.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.List;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;

abstract class AbstractGroupLiterals implements IJsFileProcessor {

    protected boolean process(JsStats stats, IJsFile jsFile,
            ISetLiteralCondition setLiteralCondition) {
        boolean modified = false;

        Assignment assignements[] = Visitors.visitAssignments(jsFile
                .getDocument().getStatements(), false);

        for (Assignment as : assignements) {

            if (as.getOperation() != null) {
                continue;
            }

            Assignment asBase = as;
            for (; as.getRight() instanceof Assignment;) {
                as = (Assignment) as.getRight();
            }

            if (setLiteralCondition.testLiteral(as, null) == false) {
                continue;
            }

            ASTNode parent = as.getParent();
            if (parent == null) {
                // D�ja trait� !
                continue;
            }

            List<ASTNode> removed = new ArrayList<ASTNode>();
            ASTNode next = asBase.getNextSibling();
            int cnt = 1;
            for (;;) {
                if ((next instanceof Assignment) == false) {
                    break;
                }

                Assignment an = (Assignment) next;

                if (an.getOperation() != null) {
                    break;
                }

                Assignment anBase = an;

                for (; an.getRight() instanceof Assignment;) {
                    an = (Assignment) an.getRight();
                }

                if (setLiteralCondition.testLiteral(an, as) == false) {
                    break;
                }

                stats.removeChars(setLiteralCondition.getLiteralName(as));

                removed.add(next);
                ASTNode nx2 = next.getNextSibling();
                next.replaceBy(null);
                as.setRight(anBase);
                as = an;

                next = nx2;
                cnt++;

                modified = true;
            }

            if (cnt > 1) {
                System.out.println("Optimize: Group litterals " + cnt + " '"
                        + setLiteralCondition.getLiteralName(as) + "' at "
                        + parent.getRegion() + " (type: "
                        + setLiteralCondition.getName() + ") removed="
                        + removed);
            }
        }

        return modified;
    }

    protected interface ISetLiteralCondition {
        boolean testLiteral(Assignment assignment, Assignment original);

        String getLiteralName(Assignment assignment);

        String getName();
    }

}
