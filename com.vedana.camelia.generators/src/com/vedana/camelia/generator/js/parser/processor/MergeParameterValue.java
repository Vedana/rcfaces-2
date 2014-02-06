/*
 * $Id: MergeParameterValue.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;

public class MergeParameterValue implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {
        boolean modified = false;

        MethodInvocation mis[] = Visitors.visitMethodInvocations(jsFile
                .getDocument().getStatements(), false);

        for (MethodInvocation mi : mis) {
            NodeList parameters = mi.getParameters();
            if (parameters.size() < 1) {
                continue;
            }

            Map<String, List<RefName>> refs = new HashMap<String, List<RefName>>();

            for (int i = 0; i < parameters.size(); i++) {
                Expression parameter = (Expression) parameters.get(i);

                if (parameter instanceof RefName) {
                    RefName refName = (RefName) parameter;

                    List<RefName> lrs = refs.get(refName.getName());
                    if (lrs == null) {
                        lrs = new ArrayList<RefName>();
                        refs.put(refName.getName(), lrs);
                    }

                    lrs.add(refName);

                    continue;
                }

                if (Tools.isUselessExpression(parameter) == false) {
                    break;
                }
            }

            if (refs.size() == 0) {
                continue;
            }

            if (Tools.isUselessExpression(mi.getObject()) == false) {
                continue;
            }

            boolean found = false;
            int idx = 0;
            NodeList nodes = mi.getParentList();
            if (nodes == null) {
                continue;
            }
            for (; idx < nodes.size(); idx++) {
                if (nodes.get(idx) != mi) {
                    continue;
                }

                found = true;
                break;
            }

            if (found == false) {
                continue;
            }

            for (idx--; idx >= 0; idx--) {
                ASTNode p = (ASTNode) nodes.get(idx);
                if ((p instanceof Assignment) == false) {
                    if ((p instanceof Expression)
                            && Tools.isUselessExpression((Expression) p)) {
                        continue;
                    }
                    break;
                }

                Assignment a = (Assignment) p;

                if ((a.getLeft() instanceof Name) == false) {
                    break;
                }

                List<RefName> rs = refs.get(((Name) a.getLeft()).getName());
                if (rs == null) {
                    continue;
                }

                if (rs.size() > 1) {
                    continue;
                }

                System.out.println("Optimize: move assignement " + a
                        + " into parameter " + rs.get(0));

                a.replaceBy(null);
                rs.get(0).replaceBy(a);

                modified = true;
            }

        }

        return modified;
    }
}
