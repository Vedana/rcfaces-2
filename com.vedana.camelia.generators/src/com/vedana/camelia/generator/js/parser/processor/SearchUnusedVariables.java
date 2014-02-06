/*
 * $Id: SearchUnusedVariables.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.CatchClause;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class SearchUnusedVariables implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        FunctionDeclaration fds[] = Visitors.visitFunctions(jsFile
                .getDocument().getStatements(), true);

        for (FunctionDeclaration fd : fds) {
            DefName defNames[] = Visitors.visitDefNames(fd.getBody()
                    .getStatements(), false);

            for (int j = 0; j < defNames.length; j++) {
                final DefName df = defNames[j];

                if (df instanceof Parameter) {
                    continue;
                }

                NodeList nl = null;
                boolean searchNode = true;

                if (df.getParent() instanceof CatchClause) {
                    continue;

                } else if (df.getParent() instanceof Value) {
                    if (((Value) df.getParent()).getName() == df) {

                        ASTNode pp = df.getParent().getParent();
                        if (pp instanceof ObjectLiteral) {
                            continue;
                        }

                        if (pp instanceof VarExpression) {
                            for (ASTNode n = df; n != null; n = n.getParent()) {
                                if (n instanceof Block) {
                                    nl = ((Block) n).getStatements();
                                    break;
                                }
                            }
                        }
                    }
                } else if (df.getParent() instanceof FunctionDeclaration) {
                    for (ASTNode n = df; n != null; n = n.getParent()) {
                        if (n instanceof Block) {
                            nl = ((Block) n).getStatements();
                            break;
                        }
                    }
                }

                if (nl == null) {
                    System.err
                            .println("searchUnusedVars: Unknown type of node ????");
                    continue;
                }

                final boolean found[] = new boolean[1];

                final boolean _defined = (searchNode == false);

                nl.accept(new ASTVisitor() {

                    boolean defined = _defined;

                    @Override
                    public boolean visit(DefName name) {
                        if (name == df) {
                            defined = true;
                            return false;
                        }

                        return true;
                    }

                    @Override
                    public boolean visit(RefName name) {
                        if (defined == false) {
                            return true;
                        }

                        if (name.getName().equals(df.getName())) {
                            found[0] = true;
                            return false;
                        }

                        return true;
                    }

                    @Override
                    public boolean visit(FunctionDeclaration fd) {
                        NodeList params = fd.getParameters();

                        for (int j = 0; j < params.size(); j++) {
                            Parameter parameter = (Parameter) params.get(j);

                            if (parameter.getName().equals(df.getName())) {
                                return false;
                            }
                        }

                        return true;
                    }

                });

                if (found[0]) {
                    continue;
                }

                unusedParameterDetected(df);
            }
        }

        return false;
    }

    protected void unusedParameterDetected(DefName df) {

        System.err.println("WARNING: Unused var: " + df);
    }

}
