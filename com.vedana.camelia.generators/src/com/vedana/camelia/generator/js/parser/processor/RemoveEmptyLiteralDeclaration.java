/*
 * $Id: RemoveEmptyLiteralDeclaration.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.List;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class RemoveEmptyLiteralDeclaration implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        List<Value> removedVars = new ArrayList<Value>();

        VarExpression vs[] = Visitors.visitVarExpressions(jsFile.getDocument()
                .getStatements(), true);
        for (int i = 0; i < vs.length; i++) {
            VarExpression v = vs[i];
            if (v.getParent() != null) {
                continue;
            }

            if (v.getValues().size() != 1) {
                continue;
            }

            Value vl = (Value) v.getValues().get(0);
            if ((vl.getRight() instanceof ObjectLiteral) == false) {
                continue;
            }

            ObjectLiteral obj = (ObjectLiteral) vl.getRight();
            if (obj.getValues().size() > 0) {
                continue;
            }

            removedVars.add(vl);
        }

        if (removedVars.isEmpty() == false) {
            for (Value v : removedVars) {
                final String name = ((DefName) v.getLeft()).getName();

                final List<RefName> rnames = new ArrayList<RefName>();
                jsFile.getDocument().getStatements().accept(new ASTVisitor() {
                    private static final String REVISION = "$Revision: 1.1 $";

                    @Override
                    public boolean visit(RefName node) {
                        if (node.getName().equals(name) == false) {
                            return false;
                        }

                        rnames.add(node);

                        return true;
                    }

                    @Override
                    public boolean visit(FunctionDeclaration node) {
                        return false;
                    }

                });

                if (rnames.size() != 1) {
                    System.err
                            .println("PROBLEME ! Ref reference plusieurs fois");
                    System.exit(-1);
                }

                RefName rn = rnames.get(0);

                if (rn.getParent().getParent() instanceof ObjectLiteral) {

                    ASTNode oldValue = ((Value) rn.getParent()).getValue();

                    rn.getParent().replaceBy(null);

                    modified = true;

                    if (oldValue instanceof RefName) {
                        // En theorie il faudrait virer le bout de code ...
                        // mais c'est fait ailleur !
                    }

                    continue;
                }

                rn.replaceBy(new NullLiteral(rn.getRegion()));

                v.getParent().replaceBy(null);

                modified = true;
            }
        }

        return modified;
    }

}
