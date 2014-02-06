/*
 * $Id: ResolveSuper.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.IJsMember;
import com.vedana.camelia.generator.js.parser.IJsMethod;
import com.vedana.camelia.generator.js.parser.IJsType;
import com.vedana.camelia.generator.js.parser.JsModifier;
import com.vedana.camelia.generator.js.parser.JsOptimizer;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.camelia.generator.js.parser.Tools;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.ArrayLiteral;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class ResolveSuper implements IJsClassProcessor {

    private static final String SUPER_RESOLVED_PROPERTY = "SUPER_RESOLVED";

    public boolean process(JsStats stats, IJsClass jsClass) {

        if (jsClass.getMetaProperty(SUPER_RESOLVED_PROPERTY) != null) {
            return false;
        }

        jsClass.putMetaProperty(SUPER_RESOLVED_PROPERTY, Boolean.TRUE);

        boolean modified = false;

        for (IJsType parent : jsClass.listAspects()) {
            IJsClass parentClass = parent.getJsClass();
            if (parentClass == null) {
                // Cela peut arriver pour les type HTMLElement
                continue;
            }

            modified |= process(stats, parentClass);
        }

        IJsType parent = jsClass.getParent();
        if (parent != null && parent.getJsClass() != null) {
            IJsClass parentClass = parent.getJsClass();
            if (parentClass != null) {
                modified |= process(stats, parentClass);
            }
        }

        IJsMember members[] = jsClass.listMembers();

        for (IJsMember m : members) {

            if ((m instanceof IJsMethod) == false) {
                continue;
            }

            IJsMethod method = (IJsMethod) m;

            if (method.getReturn(stats) != null || method.isStatic()
                    || method.isAbstract()) {
                continue;
            }

            final List<MethodInvocation> superCall = new ArrayList<MethodInvocation>();

            FunctionDeclaration fd = method.getDeclaration();

            if (fd == null) {
                System.err.println("Inlined ? " + method.getJsClass().getName()
                        + "." + method.getName());
                continue;
            }
            fd.getBody().getStatements().accept(new ASTVisitor() {

                @Override
                public boolean visit(MethodInvocation invocation) {
                    Expression exp = invocation.getObject();

                    if (exp instanceof FieldAccess) {
                        FieldAccess fa = (FieldAccess) exp;

                        if (fa.getProperty().getName().equals("f_super")) {
                            if (fa.getObject() instanceof ThisLiteral) {
                                superCall.add(invocation);
                            }
                        }
                    }

                    return true;
                }
            });

            String memberName = m.getName();

            boolean constructor = (m.getName().equals(jsClass.getName()));
            boolean finalizer = (m.getName().equals("f_finalize"));

            MethodInvocation mi = null;
            NodeList parentList = fd.getBody().getStatements();

            int idx = 0;

            if (superCall.isEmpty()) {
                // Si c'est un constructeur ou un finalizer

                if (constructor == false && finalizer == false) {
                    continue;
                }

                if (constructor) {
                    idx = parentList.size();
                }

                if (finalizer) {
                    idx = 0;
                }

            } else {
                MethodInvocation mis[] = superCall
                        .toArray(new MethodInvocation[superCall.size()]);

                if (mis.length > 1) {
                    System.err.println("Plusieurs super ????"); // on traite pas
                    // ...
                    continue;
                }

                mi = mis[0];
            }

            if (mi != null) {
                for (; idx < parentList.size(); idx++) {
                    if (parentList.get(idx) == mi) {
                        break;
                    }
                }

                if (idx == parentList.size()) {
                    // C'est dans un return, un if .... on laisse tomber !
                    continue;
                }
            }

            List<String> l = new ArrayList<String>();

            if (jsClass.isAspect() == false) {

                if (m.getMetaDoc("dontInheritMethod") == null) {
                    inheritMethods(l, jsClass, memberName, constructor,
                            finalizer);
                }

            } else {
                inheritAspectMethods(l, jsClass, memberName, constructor,
                        finalizer);

            }

            if (l.isEmpty()) {
                if (superCall.isEmpty() == false) {
                    for (ASTNode node : superCall) {
                        System.out.println("Optimize: remove super call of "
                                + method.getJsClass().getName() + "."
                                + method.getName() + "() " + node);

                        node.replaceBy(null);
                    }

                }
                continue;
            }

            System.out.println("Optimize: inline super methods of "
                    + method.getJsClass().getName() + "." + method.getName()
                    + "() (" + (l.size() / 3) + " inlines)");

            modified = true;

            int idxBefore = idx;
            int idxAfter = idx;

            if (idx < parentList.size()) {
                idxAfter++;
            }

            boolean noParameters = ((l.size() / 3) < 2) || (mi == null)
                    || (mi.getParameters().size() < 2);
            String parametersName = null;
            for (Iterator<String> it = l.iterator(); it.hasNext();) {
                String type = it.next();
                String cname = it.next();
                String mname = it.next();

                IJsClass cobject = stats.getJsClass(cname);

                IJsMethod cmethod = (IJsMethod) cobject.getMember(mname);

                if (noParameters
                /* && isMethodDecorated(cobject, mname, constructor) == false */) {

                    if (cmethod != null) {

                        FunctionDeclaration fd4 = cmethod.getDeclaration();

                        if (fd4.getParameters().size() == 0
                                && Tools.canInlineBlock(fd4.getBody())) {

                            if ("method".equals(type) && mi != null) {
                                mi.replaceBy(null);
                                mi = null;
                            }

                            idxAfter += Tools.inlineCode(stats, parentList, fd4
                                    .getBody(), idxBefore);

                            System.out.println("Optimize: inline code of "
                                    + cobject.getName() + "." + mname
                                    + " into " + method.getJsClass().getName()
                                    + "." + method.getName());
                            continue;
                        }
                    } else {
                        // System.err.println("CMethod ?");
                    }
                }

                RefName ref1 = new RefName(cname, null);
                RefName ref3 = new RefName(
                        JsOptimizer.MEMBERS_ACCESS_FIELDNAME, null);

                FieldAccess fa3 = new FieldAccess(ref1, ref3, null);

                RefName ref2 = new RefName(mname, null);
                FieldAccess fa = new FieldAccess(fa3, ref2, null);

                if (cobject.isAspect()) {
                    String t = null;
                    if (cobject.getMember(mname, JsModifier.AFTER) != null) {
                        t = "after";

                    } else if (cobject.getMember(mname, JsModifier.BEFORE) != null) {
                        t = "before";
                    }

                    if (t != null) {
                        RefName ref4 = new RefName(t, null);

                        fa = new FieldAccess(fa, ref4, null);
                    }
                }

                MethodInvocation mib;
                if (noParameters) {
                    FieldAccess fa2 = new FieldAccess(fa, new RefName("call",
                            null), null);

                    mib = new MethodInvocation(fa2, new Expression[0], null);

                    mib.getParameters().add(0, new ThisLiteral(null));
                    if (mi != null) {
                        mib.getParameters()
                                .addAll(mi.getParameters().toArray());

                        mib.getParameters().remove(1); // Retire arguments
                    }

                } else if (mi.getParameters().size() == 2) {
                    if (parametersName == null && mi != null) {
                        Expression firstParam = (Expression) mi.getParameters()
                                .get(1);

                        if (firstParam instanceof RefName) {
                            parametersName = ((RefName) firstParam).getName();

                        } else {
                            parametersName = "__super_parameters";

                            Value value = new Value(new DefName(parametersName,
                                    null), firstParam, null);
                            VarExpression var = new VarExpression(
                                    new Value[] { value }, null);

                            parentList.add(idx++, var);
                        }
                    }

                    FieldAccess fa2;
                    if ("__super_parameters".equals(parametersName)) {
                        fa2 = new FieldAccess(fa, new RefName("apply", null),
                                null);
                    } else {
                        fa2 = new FieldAccess(fa, new RefName("call", null),
                                null);
                    }

                    mib = new MethodInvocation(fa2, new Expression[0], null);
                    mib.getParameters().add(0, new ThisLiteral(null));
                    if (mi != null) {
                        mib.getParameters().add(1,
                                new RefName(parametersName, null));
                    }

                } else {
                    if (parametersName == null && mi != null) {
                        parametersName = "__super_parameters";
                        ArrayLiteral lit = new ArrayLiteral(mi.getParameters(),
                                null);
                        lit.getExpressions().remove(0);

                        Value value = new Value(new DefName(parametersName,
                                null), lit, null);
                        VarExpression var = new VarExpression(
                                new Value[] { value }, null);

                        parentList.add(idx++, var);
                    }

                    FieldAccess fa2 = new FieldAccess(fa, new RefName("apply",
                            null), null);

                    mib = new MethodInvocation(fa2, new Expression[0], null);
                    mib.getParameters().add(0, new ThisLiteral(null));
                    if (mi != null) {
                        mib.getParameters().add(1,
                                new RefName(parametersName, null));
                    }
                }

                if ("before".equals(type)) {
                    parentList.add(idxBefore++, mib);
                    idxAfter++;

                } else if ("after".equals(type)) {
                    parentList.add(idxAfter, mib);

                } else if (mi != null) {
                    mi.replaceBy(mib);
                    mi = mib;

                } else {
                    parentList.add(idxBefore, mib);
                    idxAfter++;
                }
            }
        }

        return modified;
    }

    private void inheritMethods(List<String> l, IJsClass jsclass,
            String memberName, boolean constructor, boolean finalizer) {

        IJsType pType = jsclass.getParent();
        for (; pType != null; pType = pType.getJsClass().getParent()) {
            IJsClass p = pType.getJsClass();
            if (p == null) {
                // Ca peut arriver pour HTMLElement ou Object
                break;
            }

            IJsMethod aspectMember = null;
            String apectNameMember = null;

            for (IJsType aspectType : p.listAspects()) {
                IJsClass aspect = aspectType.getJsClass();
                if (aspect == null) {
                    // Ca peut etre le cas de HTMLElement
                    continue;
                }

                inheritAspectMethods(l, aspect, memberName, constructor,
                        finalizer);

                if (constructor) {
                    memberName = aspect.getName();
                }

                IJsMethod before = (IJsMethod) aspect.getMember(memberName,
                        JsModifier.BEFORE);

                IJsMethod after = (IJsMethod) aspect.getMember(memberName,
                        JsModifier.AFTER);

                IJsMethod throwing = (IJsMethod) aspect.getMember(memberName,
                        JsModifier.THROWING);

                IJsMethod main = (IJsMethod) aspect.getMember(memberName, 0);

                if (main != null) {
                    if (constructor) {
                        after = main;
                        main = null;

                    } else if (finalizer) {
                        before = main;
                        main = null;

                    } else {
                        aspectMember = main;
                        apectNameMember = aspect.getName();
                        main = null;
                    }
                }

                if (before != null) {
                    l.add("before");
                    l.add(aspect.getName());
                    l.add(memberName);
                }

                if (after != null) {
                    l.add("after");
                    l.add(aspect.getName());
                    l.add(memberName);
                }

                if (throwing != null) {
                    l.add("throwing");
                    l.add(aspect.getName());
                    l.add(memberName);
                }
            }

            if (constructor) {
                memberName = p.getName();
            }

            String cname = p.getName();
            IJsMethod m = (IJsMethod) p.getMember(memberName);
            if (m == null) {
                m = aspectMember;
                cname = apectNameMember;
            }

            if (m != null) {
                l.add("method");
                l.add(cname);
                l.add(memberName);

                break;
            }
        }
    }

    private void inheritAspectMethods(List<String> l, IJsClass jsClass,
            String memberName, boolean constructor, boolean finalizer) {

        for (IJsType aspect : jsClass.listAspects()) {

            IJsClass aspectClass = aspect.getJsClass();

            inheritAspectMethods(l, aspectClass, memberName, constructor,
                    finalizer);

            if (constructor) {
                memberName = aspectClass.getName();
            }

            IJsMethod before = (IJsMethod) aspectClass.getMember(memberName,
                    JsModifier.BEFORE);

            IJsMethod after = (IJsMethod) aspectClass.getMember(memberName,
                    JsModifier.AFTER);

            IJsMethod throwing = (IJsMethod) aspectClass.getMember(memberName,
                    JsModifier.THROWING);

            IJsMethod main = (IJsMethod) aspectClass.getMember(memberName, 0);
            if (main != null && main.isAbstract()) {
                main = null;
            }

            if (main != null) {
                if (constructor) {
                    after = main;
                    main = null;

                } else if (finalizer) {
                    before = main;
                    main = null;

                } else {
                    l.add("method");
                    l.add(aspectClass.getName());
                    l.add(memberName);
                }
            }

            if (before != null) {
                l.add("before");
                l.add(aspectClass.getName());
                l.add(memberName);
            }

            if (after != null) {
                l.add("after");
                l.add(aspectClass.getName());
                l.add(memberName);
            }

            if (throwing != null) {
                l.add("throwing");
                l.add(aspectClass.getName());
                l.add(memberName);
            }
        }
    }

}
