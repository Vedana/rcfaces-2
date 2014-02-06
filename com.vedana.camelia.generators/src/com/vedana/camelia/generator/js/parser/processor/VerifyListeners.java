/*
 * $Id: VerifyListeners.java,v 1.4 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.HashSet;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.IJsMethod;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.RefName;

public class VerifyListeners implements IJsFileProcessor {

    private static final Set<String> JAVASCRIPT_EVENT_HANDLERS = new HashSet<String>();
    static {
        JAVASCRIPT_EVENT_HANDLERS.add("onmousedown");
        JAVASCRIPT_EVENT_HANDLERS.add("onmouseup");
        JAVASCRIPT_EVENT_HANDLERS.add("onmouseover");
        JAVASCRIPT_EVENT_HANDLERS.add("onmouseout");
        JAVASCRIPT_EVENT_HANDLERS.add("onkeydown");
        JAVASCRIPT_EVENT_HANDLERS.add("onkeyup");
        JAVASCRIPT_EVENT_HANDLERS.add("onkeypress");
        JAVASCRIPT_EVENT_HANDLERS.add("onclick");
        JAVASCRIPT_EVENT_HANDLERS.add("onchange");
        JAVASCRIPT_EVENT_HANDLERS.add("onfocus");
        JAVASCRIPT_EVENT_HANDLERS.add("onblur");
        JAVASCRIPT_EVENT_HANDLERS.add("onreset");
        JAVASCRIPT_EVENT_HANDLERS.add("onsubmit");
        JAVASCRIPT_EVENT_HANDLERS.add("ondblclick");
        JAVASCRIPT_EVENT_HANDLERS.add("onload");
        JAVASCRIPT_EVENT_HANDLERS.add("onunload");
        JAVASCRIPT_EVENT_HANDLERS.add("onreadystatechange");

    }

    public boolean process(JsStats stats, IJsFile jsFile) {

        FieldAccess fas[] = Visitors.visitFieldAccess(jsFile.getDocument()
                .getStatements(), false);

        for (int i = 0; i < fas.length; i++) {
            FieldAccess fa = fas[i];

            String name = fa.getProperty().getName().toLowerCase();
            if (name.startsWith("on") == false) {
                continue;
            }

            if (JAVASCRIPT_EVENT_HANDLERS.contains(name) == false) {
                continue;
            }

            ASTNode eq = fa;
            for (; eq != null; eq = eq.getParent()) {
                if (eq instanceof Assignment) {
                    break;
                }
            }

            if (eq == null) {
                continue;
            }

            Assignment assignment = (Assignment) eq;

            Expression right = assignment.getRight();
            if ((right instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess faR = (FieldAccess) right;

            if ((faR.getObject() instanceof RefName) == false) {
                continue;
            }

            String clsName = ((RefName) faR.getObject()).getName();
            String memberName = faR.getProperty().getName();

            IJsClass jsClass = stats.getJsClass(clsName);
            if (jsClass == null) {
                continue;
            }

            IJsMethod jsMember = (IJsMethod) jsClass.getMember(memberName);

            verifyJsOnListeners(stats, jsMember, jsMember, faR);
        }

        for (int i = 0; i < fas.length; i++) {
            FieldAccess fa = fas[i];

            if ((fa.getObject() instanceof RefName) == false) {
                continue;
            }

            if (((RefName) fa.getObject()).getName().equals("f_core") == false) {
                continue;
            }

            if (fa.getProperty().getName().equals("AddEventListener") == false) {
                continue;
            }

            Expression param = (Expression) ((MethodInvocation) fa.getParent())
                    .getParameters().get(2);

            if ((param instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess faR = (FieldAccess) param;

            if ((faR.getObject() instanceof RefName) == false) {
                continue;
            }

            String clsName = ((RefName) faR.getObject()).getName();
            String memberName = faR.getProperty().getName();

            IJsClass jsClass = stats.getJsClass(clsName);
            if (jsClass == null) {
                continue;
            }

            IJsMethod jsMember = (IJsMethod) jsClass.getMember(memberName);

            verifyJsOnListeners(stats, jsMember, jsMember, faR);
        }

        for (int i = 0; i < fas.length; i++) {
            FieldAccess fa = fas[i];

            if (fa.getProperty().getName().equals("f_insertEventListenerFirst") == false
                    && fa.getProperty().getName().equals("f_addEventListener") == false
                    && fa.getProperty().getName().equals("addEventListener") == false) {
                continue;
            }

            ASTNode p = fa.getParent();
            if ((p instanceof MethodInvocation) == false) {
                continue;
            }

            Expression param = (Expression) ((MethodInvocation) p)
                    .getParameters().get(1);

            if ((param instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess faR = (FieldAccess) param;

            if ((faR.getObject() instanceof RefName) == false) {
                continue;
            }

            String clsName = ((RefName) faR.getObject()).getName();
            String memberName = faR.getProperty().getName();

            IJsClass jsClass = stats.getJsClass(clsName);

            if (jsClass == null) {
                continue;
            }

            IJsMethod jsMember = (IJsMethod) jsClass.getMember(memberName);

            verifyJsOnListeners(stats, jsMember, jsMember, faR);
        }

        return false;
    }

    private void verifyJsOnListeners(JsStats stats, IJsMethod jsMember,
            IJsMethod source, ASTNode ref) {

        if (jsMember == null || jsMember.isStatic() == false) {
            System.err.println("Invalid jsMember ??? member=" + jsMember
                    + "  source=" + source + " ref=" + ref);
            return;
        }

        String contextInfo = jsMember.getMetaDoc("context");
        if (contextInfo != null) {
            return;
        }

        // Une redirection ?

        IJsMethod redirect = jsMember.resolveDeclaration(stats);
        if (redirect != null) {
            verifyJsOnListeners(stats, redirect, source, ref);
            return;
        }

        System.err.println("Invalid context info for member " + jsMember);
    }

}
