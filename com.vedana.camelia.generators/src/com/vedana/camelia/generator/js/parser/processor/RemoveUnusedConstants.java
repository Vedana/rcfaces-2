/*
 * $Id: RemoveUnusedConstants.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsField;
import com.vedana.camelia.generator.js.parser.IJsMember;
import com.vedana.camelia.generator.js.parser.JsClass;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.Expression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class RemoveUnusedConstants implements IJsClassProcessor {

    public boolean process(JsStats stats, IJsClass jsClass) {

        boolean modified = false;

        IJsMember members[] = jsClass.listMembers();
        for (IJsMember member : members) {

            if (member.isStatic() == false || member.isFinal() == false
                    || member.isPublic()
                    || (member instanceof IJsField) == false) {
                continue;
            }

            IJsField field = (IJsField) member;

            if (stats.getAccessCount(member) > 0) {
                continue;
            }

            System.out.println("Optimize: remove static final constant: "
                    + member.getJsClass().getName() + "." + member.getName());

            Expression mv = field.getExpression();
            if (mv != null && member.isPrivate() == false) {
                stats.inlineConstants.put(member.getJsClass().getName() + "."
                        + member.getName(), mv);
            }

            ((JsClass) jsClass).removeMember(member);

            stats.removeChars(member.getName());

            modified = true;
        }

        return modified;
    }
}
