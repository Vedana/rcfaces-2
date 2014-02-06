/*
 * $Id: SimplifyIfBlocks.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Statement;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.TrueLiteral;

public class SimplifyIfBlocks implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        IfStatement ifs[] = Visitors.visitIfs(jsFile.getDocument()
                .getStatements(), false);
        
        for (IfStatement ifNode : ifs) {

            Expression condition = ifNode.getCondition();

            if (condition instanceof PrefixExpression) {
                if (((PrefixExpression) condition).getOperation() == Operation.NOT) {
                    Expression notCondition = ((PrefixExpression) condition)
                            .getExpression();

                    if ((notCondition instanceof FalseLiteral)
                            || (notCondition instanceof NullLiteral)) {

                        Tools.replaceNode(ifNode, ifNode.getIfTrue());
                        modified = true;
                        continue;
                    }
                    if (notCondition instanceof TrueLiteral) {
                        Tools.replaceNode(ifNode, ifNode.getIfFalse());
                        modified = true;
                        continue;
                    }

                }
            }

            if ((condition instanceof FalseLiteral)
                    || (condition instanceof NullLiteral)) {

                Tools.replaceNode(ifNode, ifNode.getIfFalse());
                modified = true;
                continue;
            }
            if (condition instanceof TrueLiteral) {
                Tools.replaceNode(ifNode, ifNode.getIfTrue());
                modified = true;
                continue;
            }

            Statement body = ifNode.getIfTrue();
            if (body instanceof Block) {
                NodeList ss = ((Block) body).getStatements();

                if (ss.size() == 0) {
                    ifNode.setIfTrue(null);
                    modified = true;

                } else if (ss.size() == 1) {
                    Statement st = (Statement) ss.get(0);

                    if ((st instanceof IfStatement) == false
                            || ifNode.getIfFalse() == null) {
                        // On ne simplifie pas les IF car si derriere il y a un
                        // else
                        // ...
                        ifNode.setIfTrue(st);
                        modified = true;
                    }
                }
            }

            body = ifNode.getIfFalse();
            if (body instanceof Block) {
                NodeList ss = ((Block) body).getStatements();

                if (ss.size() == 0) {
                    ifNode.setIfFalse(null);
                    modified = true;

                } else if (ss.size() == 1) {
                    Statement st = (Statement) ss.get(0);

                    if ((st instanceof IfStatement) == false
                            || ifNode.getIfFalse() == null) {
                        // On ne simplifie pas les IF car si derriere il y a un
                        // else
                        // ...
                        ifNode.setIfFalse(st);
                        modified = true;
                    }
                }
            }

            if (ifNode.getIfTrue() == null && ifNode.getIfFalse() == null) {
                boolean replace = false;

                if (condition instanceof PrefixExpression) {
                    if (((PrefixExpression) condition).getOperation() == Operation.NOT) {
                        condition = ((PrefixExpression) condition)
                                .getExpression();
                    }
                }

                if (condition instanceof RefName) {
                    replace = true;
                }
                if (condition instanceof FieldAccess) {
                    FieldAccess fa = (FieldAccess) condition;

                    Expression object = fa.getObject();
                    if ((object instanceof RefName)
                            || (object instanceof ThisLiteral)) {
                        replace = true;
                    }
                }

                if (replace) {
                    ifNode.replaceBy(null);
                    modified = true;

                } else {
                    Tools.replaceNode(ifNode, condition);
                    modified = true;
                }
            }
        }

        return modified;
    }

}
