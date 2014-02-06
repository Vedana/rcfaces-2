/*
 * $Id: CountAndReplaceStaticConstants.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsField;
import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.IJsMember;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.TrueLiteral;
import com.vedana.js.dom.UndefinedLiteral;

public class CountAndReplaceStaticConstants implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean mainModified = false;

        RefName refs[] = Visitors.visitRefNames(jsFile.getDocument()
                .getStatements(), false);
        for (RefName ref : refs) {
            if ((ref.getParent() instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess fa = (FieldAccess) ref.getParent();

            if (fa.getProperty() != ref) {
                continue;
            }

            if ((fa.getObject() instanceof RefName) == false) {
                continue;
            }

            IJsClass jsclass = stats.getJsClass(((RefName) fa.getObject())
                    .getName());
            if (jsclass == null) {
                continue;
            }

            IJsMember member = jsclass.getMember(ref.getName());
            if ((member instanceof IJsField) == false) {
                continue;
            }

            if (member.isStatic() == false || member.isFinal() == false) {
                continue;
            }

            IJsField field = (IJsField) member;

            Expression constantValue = field.getExpression();

            if (ref.getName().equals("MULTI_WINDOW_CLASSLOADER")) {
                if (JsOptimizer.MULTI_WINDOW == true) {
                    constantValue = new TrueLiteral(null);
                }
            }

            boolean modified = false;

            String faToString = fa.toString();

            if (constantValue instanceof NumberLiteral) {
                NumberLiteral nl = (NumberLiteral) constantValue;
                // Constantes !

                fa.replaceBy(new NumberLiteral(nl.getNumber(), fa.getRegion()));
                modified = true;

            } else if (constantValue instanceof PrefixExpression) {
                PrefixExpression prefixExpression = (PrefixExpression) constantValue;
                // Constantes !

                if (prefixExpression.getExpression() instanceof NumberLiteral) {
                    NumberLiteral nl = (NumberLiteral) prefixExpression
                            .getExpression();

                    fa.replaceBy(new PrefixExpression(prefixExpression
                            .getOperation(), new NumberLiteral(nl.getNumber(),
                            nl.getRegion()), fa.getRegion()));

                    modified = true;
                }
            } else if (constantValue instanceof TrueLiteral) {
                fa.replaceBy(new TrueLiteral(constantValue.getRegion()));
                modified = true;

            } else if (constantValue instanceof FalseLiteral) {
                fa.replaceBy(new FalseLiteral(constantValue.getRegion()));
                modified = true;

            } else if (constantValue instanceof NullLiteral) {
                fa.replaceBy(new NullLiteral(constantValue.getRegion()));
                modified = true;

            } else if (constantValue instanceof UndefinedLiteral) {
                fa.replaceBy(new UndefinedLiteral(constantValue.getRegion()));
                modified = true;

            } else if (constantValue instanceof StringLiteral) {
                StringLiteral sl = (StringLiteral) constantValue;

                if (jsclass.getName().length() + 1 + member.getName().length() > sl
                        .getRegionLength() + 2) {
                    fa.replaceBy(new StringLiteral(sl.getString(),
                            constantValue.getRegion()));
                    modified = true;
                }
            }

            if (modified) {
                System.out.println("Optimize: inline static final constant "
                        + member.getJsClass().getName() + "."
                        + member.getName() + " = " + field.getExpression()
                        + " in " + faToString);

                mainModified = true;

            } else {
                stats.registerAccess(member, fa);
            }
        }

        return mainModified;
    }

}
