/*
 * $Id: ReplaceFalses.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.PrefixExpression;

public class ReplaceFalses implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        FalseLiteral falses[] = Visitors.visitFalses(jsFile.getDocument()
                .getStatements(), false);
        for (FalseLiteral falseNode : falses) {
            stats.removeChars("false");
            PrefixExpression not = new PrefixExpression(Operation.NOT,
                    new NumberLiteral(1, null), falseNode.getRegion());

            not.putProperty(JsOptimizer.FINAL_VALUE_PROPERTY,
                    FalseLiteral.class);

            falseNode.replaceBy(not);
            modified = true;

        }

        /*
         * LA REGLE EST FAUSSE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         * 
         * InfixExpression cmps[] = Visitors.visitInfixes(nodes,
         * Operation.EQUALS, false); for (int i = 0; i < cmps.length; i++) {
         * InfixExpression inNode = cmps[i];
         * 
         * ASTNode right = inNode.getRight(); if ((right instanceof
         * NumberLiteral) == false) { continue; }
         * 
         * NumberLiteral nl = (NumberLiteral) right; if (nl.getNumber() != 0.0)
         * { continue; }
         * 
         * PrefixExpression not = new PrefixExpression(Operation.NOT, inNode
         * .getLeft(), inNode.getRegion()); inNode.replaceBy(not); }
         * 
         * cmps = Visitors.visitInfixes(nodes, Operation.NOT_EQUALS, false); for
         * (int i = 0; i < cmps.length; i++) { InfixExpression inNode = cmps[i];
         * 
         * ASTNode right = inNode.getRight(); if ((right instanceof
         * NumberLiteral) == false) { continue; }
         * 
         * NumberLiteral nl = (NumberLiteral) right; if (nl.getNumber() != 0.0)
         * { continue; }
         * 
         * inNode.replaceBy(inNode.getLeft()); }
         */

        return modified;
    }

}
