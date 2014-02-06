/*
 * $Id: RemoveUnusedParameters.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.HashSet;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.RefName;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class RemoveUnusedParameters implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        FunctionDeclaration functions[] = Visitors.visitFunctions(jsFile
                .getDocument().getStatements(), true);
        for (FunctionDeclaration f : functions) {

            NodeList parameters = f.getParameters();

            Set<String> params = new HashSet<String>();

            for (int j = parameters.size() - 1; j >= 0; j--) {
                Parameter param = (Parameter) parameters.get(j);

                final String paramName = param.getName();

                if (params.add(paramName) == false) {
                    // On peut effacer !

                    System.out.println("Remove unused parameter '" + paramName
                            + "' in " + f);
                    parameters.remove(j);

                    continue;
                }

                final boolean found[] = new boolean[1];

                f.getBody().getStatements().accept(new ASTVisitor() {
                    private static final String REVISION = "$Revision: 1.1 $";

                    @Override
                    public boolean visit(RefName node) {
                        if (node.getName().equals(paramName)) {
                            found[0] = true;
                            return false;
                        }

                        return true;
                    }

                });

                if (found[0]) {
                    break; // On passe pas aux autres params !
                }

                System.out.println("Optimize: Remove unused parameter '"
                        + paramName + "' in " + f);

                parameters.remove(j);
                modified = true;
            }
        }

        return modified;
    }

}
