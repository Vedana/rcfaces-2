/*
 * $Id: JsMethod.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.Value;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public class JsMethod extends JsMember implements IJsMethod {

    public JsMethod(IJsClass jsClass, ASTNode definition, Expression value,
            boolean frameworkPrivate, JsModifier modifier, JsComment comment) {
        super(jsClass, definition, value, modifier, comment);
    }

    public IJsType getType(JsStats stats) {

        if (getComment() == null) {
            // hérité !?
            return null;
        }

        String returnType = getComment().getReturnType();
        if (returnType == null) {
            return null;
        }

        return JsType.parse(stats, returnType, jsClass, false);
    }

    public IJsParam[] listParams(JsStats stats) {

        if (getComment() == null) {
            // hérité !?
            return null;
        }

        List<IJsParam> ps = new ArrayList<IJsParam>();

        String docParams[] = getComment().listParams();
        if (docParams.length == 0 && getComment().getReturnType() == null) {

            FunctionDeclaration fd = getDeclaration();
            if (fd != null) {
                NodeList parameters = fd.getParameters();

                IJsType anyType = JsType.parse(stats, "any", null, false);

                for (int i = 0; i < parameters.size(); i++) {
                    Parameter p = (Parameter) parameters.get(i);

                    ps.add(new Param(p.getName(), anyType, false, false, null));
                }
            }

            return ps.toArray(new IJsParam[ps.size()]);
        }

        for (String dp : docParams) {
            IJsParam p = parseParam(stats, dp);
            if (p == null) {
                continue;
            }

            ps.add(p);
        }

        if (ps.size() > 1) {
            // On verifie l'ordre !!!!
            final FunctionDeclaration fd = getDeclaration();
            if (fd != null) {
                // On peut pas verifier les abstracts

                final List<String> positions = new ArrayList<String>();

                NodeList parameters = fd.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    Parameter p = (Parameter) parameters.get(i);

                    positions.add(p.getName());
                }

                Collections.sort(ps, new Comparator<IJsParam>() {

                    public int compare(IJsParam o1, IJsParam o2) {
                        int idx1 = positions.indexOf(o1.getName());
                        int idx2 = positions.indexOf(o2.getName());

                        if (idx1 < 0) {
                            System.err.println("???? parameter " + o1 + " "
                                    + fd);
                        }
                        if (idx2 < 0) {
                            System.err.println("???? parameter " + o1 + " "
                                    + fd);
                        }

                        return idx1 - idx2;
                    }
                });

                // System.out.println("Order: " + ps);
            }
        }

        if (docParams.length == 0 && getComment().getReturnType() == null) {
            // Rien de rien !!!
            return null;
        }

        return ps.toArray(new IJsParam[ps.size()]);
    }

    public IJsParam[] resolveParams(JsStats stats) {
        if (getComment() != null) {
            // if (getComment().listParams().length > 0) {
            IJsParam ps[] = listParams(stats);
            if (ps != null) {
                return ps;
            }
            // }

            // if (getComment().getReturnType() != null) {
            // Pas de parametres !
            // return new IJsParam[0];
            // }
        }

        IJsMethod parents[] = Tools.searchParentMember(this);
        for (IJsMethod parent : parents) {
            IJsParam ps[] = parent.resolveParams(stats);
            if (ps != null) {

                for (int i = 0; i < ps.length; i++) {
                    IJsType returnType = ps[i].getJsType();

                    IJsType ts = RCFacesJSCSGenerator.translateType(returnType,
                            getJsClass(), parent.getJsClass());

                    if (ts != returnType) {
                        ps[i] = ((Param) ps[i]).changeJsType(ts);
                    }
                }

                return ps;
            }
        }

        return null;
    }

    public IJsReturn resolveReturn(JsStats stats) {
        if (getComment() != null && getComment().getReturnType() != null) {
            return getReturn(stats);
        }

        return resolveReturn(stats, getJsClass());
    }

    private IJsReturn resolveReturn(JsStats stats, IJsClass jsClass) {

        List<IJsType> ancestors = new ArrayList<IJsType>();

        IJsType parentType = jsClass.getParent();
        if (parentType != null) {
            ancestors.add(parentType);
        }

        IJsType aspectsTypes[] = jsClass.listAspects();
        if (aspectsTypes != null) {
            ancestors.addAll(Arrays.asList(aspectsTypes));
        }

        for (IJsType ancestorType : ancestors) {
            IJsClass ancestorClass = ancestorType.getJsClass();
            if (ancestorClass == null) {
                continue;
            }

            IJsMember member = ancestorClass.getMember(getName());
            if ((member instanceof IJsMethod) == false) {
                continue;
            }

            IJsReturn jsReturn = ((IJsMethod) member).resolveReturn(stats);
            if (jsReturn == null) {
                continue;
            }

            IJsType returnType = jsReturn.getJsType();
            if (returnType == null) {
                return jsReturn;
            }

            IJsType tps[] = ancestorType.listTemplates();
            if (tps.length == 0) {
                return jsReturn;
            }

            IJsType pms[] = ancestorClass.listParameters();
            if (pms == null || pms.length == 0) {
                return jsReturn;
            }

            // On transforme du pms => tps
            IJsType retType = Tools.transformType(returnType, tps, pms);
            if (retType.equals(returnType)) {
                return jsReturn;
            }

            return ((JsReturn) jsReturn).changeJsType(retType);

        }

        return null;
    }

    public IJsReturn getReturn(JsStats stats) {

        if (getComment() == null) {
            // hérité !?
            return null;
        }

        String returnType = getComment().getReturnType();
        if (returnType == null || returnType.trim().length() == 0) {
            return null;
        }

        IJsType jsReturn = JsType.parse(stats, returnType, getJsClass(), true);

        return new JsReturn(jsReturn, getComment().getReturnValue());
    }

    private IJsParam parseParam(JsStats stats, String dp) {

        TypeTokenizer st = new TypeTokenizer(dp);

        boolean hidden = false;
        boolean optional = false;
        IJsType type = null;
        for (;;) {
            String tok = st.nextToken();
            if ("hidden".equals(tok)) {
                hidden = true;
                continue;
            }
            if ("optional".equals(tok)) {
                optional = true;
                continue;
            }

            type = JsType.parse(stats, tok, jsClass, false);
            break;
        }

        if (st.hasMoreTokens() == false) {
            return null;
        }

        String name = st.nextToken();
        String comment = st.endOfText();

        return new Param(name, type, hidden, optional, comment);
    }

    public FunctionDeclaration getDeclaration() {
        if (value instanceof FunctionDeclaration) {
            return (FunctionDeclaration) value;
        }

        return null;
    }

    public boolean isAbstract() {
        return modifier.isAbstract();
    }

    public IJsMethod resolveDeclaration(JsStats stats) {

        if (modifier.node instanceof Value) {
            Value value = (Value) modifier.node;

            if (value.getRight() instanceof FieldAccess) {
                FieldAccess fa = (FieldAccess) value.getRight();

                if (fa.getObject() instanceof RefName) {

                    String clsName = ((RefName) fa.getObject()).getName();
                    String memberName = fa.getProperty().getName();

                    IJsClass jsClass2 = stats.getJsClass(clsName);

                    IJsMethod method = (IJsMethod) jsClass2
                            .getMember(memberName);

                    if (method == null) {
                        return null;
                    }

                    IJsMethod resolvedMethod = method.resolveDeclaration(stats);
                    if (resolvedMethod != null) {
                        return resolvedMethod;
                    }

                    return method;
                }
            }
        }

        return null;
    }

    public boolean isAfter() {
        return modifier.isAfter();
    }

    public boolean isBefore() {
        return modifier.isBefore();
    }

    public boolean resolvePublic(JsStats stats) {
        if (getComment() != null) {
            return modifier.isPublic();
        }

        IJsMethod ms[] = Tools.searchParentMember(this);
        for (IJsMethod m : ms) {
            if (m.getComment() != null) {
                return m.isPublic();
            }
        }

        return super.isPublic();
    }

    public static class Param implements IJsParam {
        private String name;

        private final boolean hidden;

        private final boolean optional;

        private IJsType jsType;

        private String comment;

        public Param(String name, IJsType jsType, boolean hidden,
                boolean optional, String comment) {
            this.name = name;
            this.jsType = jsType;
            this.hidden = hidden;
            this.optional = optional;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public boolean isHidden() {
            return hidden;
        }

        public boolean isOptional() {
            return optional;
        }

        public IJsType getJsType() {
            return jsType;
        }

        public String getComment() {
            return comment;
        }

        public IJsParam changeJsType(IJsType retType) {
            return new Param(name, retType, hidden, optional, comment);
        }
    }

    public static class JsReturn implements IJsReturn {

        private final IJsType jsType;

        private final String comment;

        public JsReturn(IJsType jsType, String comment) {
            this.jsType = jsType;
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public IJsType getJsType() {
            return jsType;
        }

        public IJsReturn changeJsType(IJsType retType) {
            return new JsReturn(retType, comment);
        }

    }

    @Override
    public String toString() {
        return "[JsMethod '" + getName() + "' modifier={"
                + modifier.formatFlags() + "} declaration='" + getDeclaration()
                + "']";
    }
}
