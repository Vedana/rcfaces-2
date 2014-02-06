/*
 * $Id: MultiWindowOptimizer.java,v 1.4 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.vedana.js.Context;
import com.vedana.js.IRegion;
import com.vedana.js.Operation;
import com.vedana.js.Parser;
import com.vedana.js.TokenStream;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Document;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.HookExpression;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class MultiWindowOptimizer {

    private static final Set<String> multiWindowSystemClass = new HashSet<String>();
    static {
        multiWindowSystemClass.add("activexobject");
    }

    static void multiWindow(JsStats stats) {
        // Il faut rechercher tous les accés à des méthodes statiques
        // f_core.Get =>
        // window.f_core.Get
        //
        // document =>
        // window.document
        //
        // Transformer sytematiquement les accés directe à "window" sous
        // var
        // __win=this._window // D'une méthode static
        // __win=this._kclass._window // On recherche une classe utilisée ! ou
        // on fait this._kclass
        // __win=????._kclass._window

        if (true) {
            for (IJsClass jsClass : stats.listClasses()) {
                IJsMember members[] = jsClass.listMembers();

                for (IJsMember member : members) {

                    if ((member instanceof IJsMethod) == false
                            || ((IJsMethod) member).isAbstract()
                            || ((IJsMethod) member).getDeclaration() == null
                            || member.isStatic() == false) {
                        continue;
                    }

                    if (member
                            .getMetaProperty(JsOptimizer.WINDOW_INDEPENDANT_PROPERTY) != null) {
                        continue;
                    }

                    if ("independant".equals(member.getMetaDoc("context"))) {
                        member.putMetaProperty(
                                JsOptimizer.WINDOW_INDEPENDANT_PROPERTY,
                                Boolean.TRUE);
                        System.out.println("Use META independant property: "
                                + member.getJsClass().getName() + "."
                                + member.getName());
                        continue;
                    }

                    boolean dependance = MultiWindowOptimizer
                            .computeWindowDependance(stats,
                                    ((IJsMethod) member).getDeclaration(),
                                    new ArrayList<FunctionDeclaration>());

                    Boolean b = Boolean.valueOf(dependance == false);

                    if (b.booleanValue()) {
                        System.out
                                .println("Use computed independant property: "
                                        + member.getJsClass().getName() + "."
                                        + member.getName());
                    }
                    member.putMetaProperty(
                            JsOptimizer.WINDOW_INDEPENDANT_PROPERTY, b);
                }
            }
        }

        for (IJsClass jsClass : stats.listClasses()) {
            IJsMember members[] = jsClass.listMembers();

            for (IJsMember member : members) {

                if ((member instanceof IJsField)
                        && (((IJsField) member).getExpression() instanceof ObjectLiteral)) {
                    ObjectLiteral obj = (ObjectLiteral) ((IJsField) member)
                            .getExpression();

                    NodeList nl = obj.getValues();
                    for (int i = 0; i < nl.size(); i++) {
                        Value value = (Value) nl.get(i);
                        if (value.getValue() instanceof FunctionDeclaration) {

                            MultiWindowOptimizer.multiWindowMethod(stats,
                                    member, false,
                                    (FunctionDeclaration) value.getValue());
                        }
                    }
                }

                if ((member instanceof IJsField)
                        || ((IJsMethod) member).isAbstract()) {
                    continue;
                }

                MultiWindowOptimizer.multiWindowMethod(stats, member,
                        member.isStatic(),
                        ((IJsMethod) member).getDeclaration());
            }
        }

    }

    private static void multiWindowMethod(final JsStats stats,
            IJsMember member, boolean staticModifier,
            FunctionDeclaration functionDeclaration) {
        // On regroupe les accés "window" en __win
        // On transforme document par __win.document

        if (functionDeclaration == null) {
            // C'es un alias ?

            return;
        }

        final NodeList nodeList = functionDeclaration.getBody().getStatements();

        final int firstPosition[] = new int[] { 9999999 };

        final Map<String, List<RefName>> access = new HashMap<String, List<RefName>>();

        final Set<String> diNames = new HashSet<String>();

        String dis = member.getMetaDoc(JsOptimizer.DONT_INLINE_META);
        if (dis != null) {
            StringTokenizer st = new StringTokenizer(dis);
            for (; st.hasMoreTokens();) {
                diNames.add(st.nextToken());
            }
        }

        final List<RefName> documents = new ArrayList<RefName>();
        final List<RefName> windows = new ArrayList<RefName>();

        final boolean hasInnerFunction[] = new boolean[1];

        final Map<String, List<RefName>> independantAccess = new HashMap<String, List<RefName>>();

        nodeList.accept(new ASTVisitor() {

            @Override
            public boolean visit(FunctionDeclaration node) {
                hasInnerFunction[0] = true;

                return super.visit(node);
            }

            @Override
            public boolean visit(RefName name) {
                if ((name.getParent() instanceof FieldAccess) == false
                        || ((FieldAccess) name.getParent()).getObject() == name) {

                    if (name.getName().equals("window")) {

                        int pos = MultiWindowOptimizer.computePosition(name,
                                nodeList);
                        if (pos < firstPosition[0]) {
                            firstPosition[0] = pos;
                        }

                        windows.add(name);

                        return false;

                    } else if (name.getName().equals("document")) {

                        int pos = MultiWindowOptimizer.computePosition(name,
                                nodeList);
                        if (pos < firstPosition[0]) {
                            firstPosition[0] = pos;
                        }

                        documents.add(name);

                        return false;
                    }
                }

                if (name.getName().equalsIgnoreCase("Array")) {
                    return false; // Finalement on surcharge Instanceof
                }

                if ((name.getParent() instanceof FieldAccess)
                        && ((FieldAccess) name.getParent()).getObject() == name) {

                    IJsClass jsc = stats.getJsClass(name.getName());
                    if (jsc != null) {
                        String memberName = ((FieldAccess) name.getParent())
                                .getProperty().getName();

                        IJsMember mem = jsc.getMember(memberName);
                        if (mem != null) {
                            Boolean b = (Boolean) mem
                                    .getMetaProperty(JsOptimizer.WINDOW_INDEPENDANT_PROPERTY);
                            if (b != null && b.booleanValue()) {
                                // C'est independant !

                                // independantAccess.

                                List<RefName> l = independantAccess.get(name
                                        .getName());
                                if (l == null) {
                                    l = new ArrayList<RefName>();

                                    independantAccess.put(name.getName(), l);
                                }

                                l.add(name);

                                return false;
                            }
                        }

                        List<RefName> l = access.get(name.getName());
                        if (l == null) {
                            l = new ArrayList<RefName>();

                            access.put(name.getName(), l);
                        }

                        l.add(name);

                        return false;
                    }
                }

                if ((name.getParent() instanceof FieldAccess) == false) {

                    if (stats.containsClass(name.getName())
                            || multiWindowSystemClass.contains(name.getName())) {

                        List<RefName> l = access.get(name.getName());
                        if (l == null) {
                            l = new ArrayList<RefName>();

                            access.put(name.getName(), l);
                        }

                        l.add(name);

                        return false;
                    }

                }

                return false;
            }
        });

        if (windows.isEmpty() && documents.isEmpty() && access.isEmpty()) {
            return;
        }

        List<RefName> dontInlines = new ArrayList<RefName>();

        if (independantAccess.isEmpty() == false) {

            for (Map.Entry<String, List<RefName>> entry : access.entrySet()) {
                String kname = entry.getKey();
                List<RefName> l = entry.getValue();

                List<RefName> il = independantAccess.remove(kname);
                if (il != null) {
                    l.addAll(il);
                }
            }

            for (Map.Entry<String, List<RefName>> entry : independantAccess
                    .entrySet()) {
                String kname = entry.getKey();
                List<RefName> l = entry.getValue();

                if (l.size() * kname.length() < (",y=t._k._k._w.").length()
                        + kname.length() + l.size()) {
                    System.out.println("Dont redirect independant call for '"
                            + kname + "': " + l);
                    continue;
                }

                access.put(kname, l);
            }

        }

        for (List<RefName> l : access.values()) {
            for (RefName name : l) {
                int pos = MultiWindowOptimizer.computePosition(name, nodeList);
                if (pos < firstPosition[0]) {
                    firstPosition[0] = pos;
                }

                if (diNames.contains(name.getName())) {
                    dontInlines.add(name);
                }
            }
        }

        boolean thisRedirected = MultiWindowOptimizer
                .getContextMetaType(member) != null;
        boolean thisPrototypeClassConstructor = false;
        if (thisRedirected == false) {
            // Pour les classes qui héritent directement de Object
            // et que le membre est le constructeur ... BEN NON POUR TOUTES LES
            // METHODES

            IJsClass jsClass = member.getJsClass();
            if (jsClass.getParent() == null && jsClass.isAspect() == false) {
                // if (member.modifier.name.equals(member.modifier.className)) {
                if (member.isStatic() == false) {
                    thisPrototypeClassConstructor = true;
                    thisRedirected = true;
                }
            }
        }

        String __win = "__win";
        boolean __winDefined = false;
        if ("window".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            String windowRef = MultiWindowOptimizer
                    .getContextMetaParameter(member);

            if (windowRef != null) {
                __win = windowRef;
                __winDefined = true;
            }
        }

        OuterThis outerThis = null;
        // String outerThis = "__this";
        // boolean outerThisUsed = false;
        // Value outerThisValue = null;
        if (hasInnerFunction[0]) {
            VarExpression var;

            ASTNode first = (ASTNode) nodeList.get(0);
            if (first instanceof VarExpression) {
                var = (VarExpression) first;

            } else {
                var = new VarExpression(new Value[0], null);
                nodeList.add(0, var);
            }

            DefName dn = new DefName(OuterThis.NAME, null);
            Value outerThisValue = new Value(dn, new ThisLiteral(null), null);
            var.getValues().add(0, outerThisValue);

            dn.putProperty(JsOptimizer.FINAL_VALUE_PROPERTY, ThisLiteral.class);

            outerThis = new OuterThis(outerThisValue);
        }

        try {

            // Un seul acces sans windows ni document
            if (windows.isEmpty() && documents.isEmpty() && access.size() == 1
                    && access.values().iterator().next().size() == 1) {
                RefName rname = access.values().iterator().next().get(0);

                boolean useOuterThis = (MultiWindowOptimizer.sameFunction(
                        functionDeclaration, rname) == false);

                if (thisRedirected == false
                        && rname.getName()
                                .equals(member.getJsClass().getName())
                        && staticModifier) {

                    Expression tl = MultiWindowOptimizer.createThisConstant(
                            member, useOuterThis ? outerThis : null,
                            rname.getRegion());
                    tl.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                            rname.getName());

                    rname.replaceBy(tl);

                    return;
                }

                Expression window = MultiWindowOptimizer.computeWindow(member,
                        staticModifier, thisPrototypeClassConstructor,
                        useOuterThis ? outerThis : null, rname.getRegion());

                RefName r0 = new RefName(rname.getName(), null);
                r0.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                        rname.getName());

                FieldAccess fa4 = new FieldAccess(window, r0, null);

                rname.replaceBy(fa4);
                return;
            }

            // Un acces a windows
            if (windows.isEmpty() == false) {
                if (documents.isEmpty() && access.isEmpty()
                        && windows.size() == 1) {
                    RefName rn = windows.get(0);

                    Expression window = MultiWindowOptimizer.computeWindow(
                            member, staticModifier,
                            thisPrototypeClassConstructor, outerThis,
                            rn.getRegion());

                    rn.replaceBy(window);
                    return;
                }

                for (RefName name : windows) {
                    RefName newRefName = new RefName(__win, name.getRegion());
                    name.replaceBy(newRefName);
                }
            }

            boolean mustDefineDoc = false;

            // Un acces a document
            if (documents.isEmpty() == false) {
                if (documents.size() == 1) {
                    RefName rn = documents.get(0);

                    Expression window = new RefName(__win, null); // computeWindow
                    // (member,
                    // staticModifier,thisPrototypeClassConstructor);

                    FieldAccess fa4 = new FieldAccess(window, new RefName(
                            "document", null), null);

                    rn.replaceBy(fa4);

                } else {

                    // On ajoute document

                    mustDefineDoc = true;

                    for (RefName name : documents) {
                        RefName newRefName = new RefName("__doc",
                                name.getRegion());
                        name.replaceBy(newRefName);
                    }
                }
            }

            VarExpression var;

            ASTNode first = (ASTNode) nodeList.get(firstPosition[0]);
            if (first instanceof VarExpression) {
                var = (VarExpression) first;

            } else {
                var = new VarExpression(new Value[0],
                        functionDeclaration.getRegion());
                nodeList.add(firstPosition[0], var);
            }

            Expression window = MultiWindowOptimizer.computeWindow(member,
                    staticModifier, thisPrototypeClassConstructor, outerThis,
                    var.getRegion());

            int winPos = 0;

            if (var.getValues().size() > 0) {
                if (((Value) var.getValues().get(0)).getValue() instanceof ThisLiteral) {
                    winPos++;
                }
            }

            if (__winDefined == false) {
                Value value = new Value(new DefName(__win, null), window, null);

                var.getValues().add(winPos++, value);
            }

            if (mustDefineDoc) {
                Value value2 = new Value(new DefName("__doc", null),
                        new FieldAccess(new RefName(__win, null), new RefName(
                                "document", null), null), null);

                // TODO le positionnement n'est pas certain !
                var.getValues().add(winPos++, value2);
            }

            for (Map.Entry<String, List<RefName>> entry : access.entrySet()) {
                String name = entry.getKey();
                List<RefName> l = entry.getValue();

                if (JsOptimizer.GROUP_CLASS_WINDOW == false || l.size() < 2) {
                    RefName rname = l.get(0);

                    if (thisRedirected == false
                            && rname.getName().equals(
                                    member.getJsClass().getName())
                            && staticModifier) {

                        boolean useOuterThis = (MultiWindowOptimizer
                                .sameFunction(functionDeclaration, rname) == false);

                        Expression th0 = MultiWindowOptimizer
                                .createThisConstant(member,
                                        (useOuterThis) ? outerThis : null,
                                        rname.getRegion());
                        th0.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                                rname.getName());

                        rname.replaceBy(th0);
                        continue;
                    }
                    RefName r0 = new RefName(name, rname.getRegion());
                    r0.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                            rname.getName());

                    rname.replaceBy(new FieldAccess(new RefName(__win, null),
                            r0, null));

                } else {

                    String wname = "__win_" + name;

                    int fp2 = 999999;

                    for (RefName rname : l) {
                        if (thisRedirected == false
                                && name.equals(member.getJsClass().getName())) {
                            if (staticModifier) {

                                if (MultiWindowOptimizer.sameFunction(
                                        functionDeclaration, rname)) {
                                    Expression th0 = MultiWindowOptimizer
                                            .createThisConstant(member,
                                                    outerThis,
                                                    rname.getRegion());
                                    th0.putProperty(
                                            JsOptimizer.INDIRECT_CLASS_PROPERTY,
                                            rname.getName());

                                    rname.replaceBy(th0);

                                } else {
                                    RefName r0 = outerThis.createRef(rname
                                            .getRegion());
                                    r0.putProperty(
                                            JsOptimizer.INDIRECT_CLASS_PROPERTY,
                                            rname.getName());

                                    rname.replaceBy(r0);

                                }
                                continue;
                            }
                        }

                        if (dontInlines.contains(rname)) {
                            RefName r0 = new RefName(name, rname.getRegion());
                            r0.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                                    rname.getName());

                            rname.replaceBy(new FieldAccess(new RefName(__win,
                                    null), r0, null));
                            continue;
                        }

                        int pos = MultiWindowOptimizer.computePosition(rname,
                                nodeList);
                        if (pos < fp2) {
                            fp2 = pos;
                        }

                        RefName r0 = new RefName(wname, null);
                        r0.putProperty(JsOptimizer.INDIRECT_CLASS_PROPERTY,
                                name);
                        rname.replaceBy(r0);
                    }

                    if (fp2 < 999999) {
                        FieldAccess fa4;
                        fa4 = new FieldAccess(new RefName(__win, null),
                                new RefName(name, null), null);

                        Value value2 = new Value(new DefName(wname, null), fa4,
                                null);

                        ASTNode first2 = (ASTNode) nodeList.get(fp2);
                        VarExpression var2;
                        if (first2 instanceof VarExpression) {
                            var2 = (VarExpression) first2;

                        } else {
                            var2 = new VarExpression(new Value[0], null);
                            nodeList.add(fp2, var2);
                        }

                        int pos2 = 0;
                        if (var == var2) {
                            pos2 = winPos++;
                        }

                        var2.getValues().add(pos2, value2);
                    }
                }
            }

            if (var.getValues().size() == 0) {
                var.replaceBy(null);
            }
        } finally {
            if (outerThis != null && outerThis.isUsed() == false) {
                Value outerThisValue = outerThis.getValue();

                if (((VarExpression) outerThisValue.getParent()).getValues()
                        .size() == 1) {
                    ((VarExpression) outerThisValue.getParent())
                            .replaceBy(null);
                } else {
                    outerThisValue.replaceBy(null);
                }
            }
        }
    }

    protected static boolean computeWindowDependance(final JsStats stats,
            FunctionDeclaration fd, final List<FunctionDeclaration> processing) {

        processing.add(fd);

        final boolean dependant[] = new boolean[1];

        fd.getBody().getStatements().accept(new ASTVisitor() {

            @Override
            public boolean visit(RefName name) {
                if ((name.getParent() instanceof FieldAccess) == false
                        || ((FieldAccess) name.getParent()).getObject() == name) {

                    if (name.getName().equals("window")) {

                        dependant[0] = true;
                        return false;

                    } else if (name.getName().equals("document")) {

                        dependant[0] = true;
                        return false;
                    }
                }

                if (name.getName().equals("Array")) {
                    return false;
                }

                if ((name.getParent() instanceof FieldAccess)
                        && ((FieldAccess) name.getParent()).getObject() == name) {

                    IJsClass jsc = stats.getJsClass(name.getName());
                    if (jsc != null) {
                        String memberName = ((FieldAccess) name.getParent())
                                .getProperty().getName();

                        IJsMember mem = jsc.getMember(memberName);
                        if (mem instanceof IJsMethod) {
                            if (((IJsMethod) mem).getDeclaration() == null) {
                                dependant[0] = true;
                                return false;
                            }

                            if (processing.contains(((IJsMethod) mem)
                                    .getDeclaration())) {
                                return false;
                            }

                            Boolean b = (Boolean) mem
                                    .getMetaProperty(JsOptimizer.WINDOW_INDEPENDANT_PROPERTY);

                            if (b == null) {

                                boolean bb;
                                if ("independant".equals(mem
                                        .getMetaDoc("context"))) {
                                    bb = false;
                                } else {
                                    bb = computeWindowDependance(stats,
                                            ((IJsMethod) mem).getDeclaration(),
                                            processing);
                                }

                                b = Boolean.valueOf(bb == false);

                                mem.putMetaProperty(
                                        JsOptimizer.WINDOW_INDEPENDANT_PROPERTY,
                                        b);
                            }

                            if (b.booleanValue()) {
                                // On laisse tomber !
                                return false;
                            }
                        }

                        dependant[0] = true;
                        return false;
                    }
                }

                if ((name.getParent() instanceof FieldAccess) == false) {

                    if (stats.containsClass(name.getName())) {
                        dependant[0] = true;

                        return false;
                    }

                }

                return false;
            }
        });

        if (dependant[0] == false) {
            // System.out.println("Independant " + fd);
        }

        processing.remove(processing.size() - 1);

        return dependant[0];
    }

    private static boolean sameFunction(FunctionDeclaration fd, ASTNode node) {

        for (; node != null; node = node.getParent()) {
            if ((node instanceof FunctionDeclaration) == false) {
                continue;
            }

            if (node == fd) {
                return true;
            }

            return false;
        }

        return false;
    }

    private static Expression computeWindow(IJsMember member,
            boolean staticModifier, boolean thisPrototypeClassConstructor,
            OuterThis outerThis, IRegion region) {

        if ("window".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            return MultiWindowOptimizer.getContextMetaRef(member);
        }

        if ("event".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            return new MethodInvocation(new RefName("_rcfacesGW", null),
                    new Expression[] { new ThisLiteral(null),
                            MultiWindowOptimizer.getContextMetaRef(member) },
                    region);
        }

        if ("document".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            return new MethodInvocation(new RefName("_rcfacesGW", null),
                    new Expression[] { MultiWindowOptimizer
                            .getContextMetaRef(member) }, region);
        }

        if ("event".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            // window =
            // ((evt)?evt.view:(this.nodeType==9)?this.parentWindow:(this.
            // ownerDocument)?(this.ownerDocument.parentWindow):this));

            FieldAccess f2 = new FieldAccess(
                    MultiWindowOptimizer.getContextMetaRef(member),
                    new RefName("target", null), null);

            FieldAccess f1 = new FieldAccess(f2, new RefName("ownerDocument",
                    null), null);

            FieldAccess firefox = new FieldAccess(f1, new RefName(
                    "defaultView", null), null);

            Expression ieFalse2 = MultiWindowOptimizer.createThisConstant(
                    member, null, null);

            FieldAccess ieTrue2 = new FieldAccess(
                    new FieldAccess(MultiWindowOptimizer.createThisConstant(
                            member, null, null), new RefName("ownerDocument",
                            null), null), new RefName("parentWindow", null),
                    null);

            FieldAccess ieCond2 = new FieldAccess(
                    MultiWindowOptimizer.createThisConstant(member, null, null),
                    new RefName("ownerDocument", null), null);

            HookExpression ieFalse1 = new HookExpression(ieCond2, ieTrue2,
                    ieFalse2, null);

            FieldAccess ieTrue1 = new FieldAccess(
                    MultiWindowOptimizer.createThisConstant(member, null, null),
                    new RefName("parentWindow", null), null);

            InfixExpression ieCond1 = new InfixExpression(Operation.EQUALS,
                    new FieldAccess(MultiWindowOptimizer.createThisConstant(
                            member, null, null), new RefName("nodeType", null),
                            null), new NumberLiteral(9, null), null);

            HookExpression ie = new HookExpression(ieCond1, ieTrue1, ieFalse1,
                    null);

            return new HookExpression(
                    MultiWindowOptimizer.getContextMetaRef(member), firefox,
                    ie, region);
        }

        if (member.getJsClass().getName().equals("f_core")
                || member.getJsClass().getName().equals("f_classLoader")) {
            Expression fa2 = MultiWindowOptimizer.createThisConstant(member,
                    outerThis, region);

            FieldAccess fa3 = new FieldAccess(fa2,
                    new RefName("_window", null), null);

            return fa3;
        }

        if (member.getJsClass().getName().equals("f_class")
                || member.getJsClass().getName().equals("f_aspect")) {
            if (staticModifier == false) {
                Expression fa1 = MultiWindowOptimizer.createThisConstant(
                        member, outerThis, null);

                Expression fa2 = new FieldAccess(fa1, new RefName(
                        "_classLoader", null), null);

                FieldAccess fa3 = new FieldAccess(fa2, new RefName("_window",
                        null), region);

                return fa3;
            }
        }

        Expression root;

        String objectRef = null;
        if ("object".equals(MultiWindowOptimizer.getContextMetaType(member))) {
            objectRef = MultiWindowOptimizer.getContextMetaParameter(member);
        }

        if (objectRef != null) {
            if (objectRef.equals("this")) {
                root = new FieldAccess(MultiWindowOptimizer.createThisConstant(
                        member, outerThis, null), new RefName("_kclass", null),
                        null);

            } else {
                root = new FieldAccess(new RefName(objectRef, null),
                        new RefName("_kclass", null), null);
            }

        } else if (thisPrototypeClassConstructor) {
            root = new FieldAccess(MultiWindowOptimizer.createThisConstant(
                    member, outerThis, null), new RefName("_kclass", null),
                    region);

        } else if (staticModifier) {
            root = MultiWindowOptimizer.createThisConstant(member, outerThis,
                    region);

        } else {
            root = new FieldAccess(MultiWindowOptimizer.createThisConstant(
                    member, outerThis, region), new RefName("_kclass", null),
                    null);
        }

        Expression fa2 = new FieldAccess(root,
                new RefName("_classLoader", null), null);

        FieldAccess fa3 = new FieldAccess(fa2, new RefName("_window", null),
                region);

        return fa3;
    }

    public static final String getContextMetaType(IJsMember member) {
        return MultiWindowOptimizer.getContextMeta(member, 0);
    }

    private static final String getContextMetaParameter(IJsMember member) {
        return MultiWindowOptimizer.getContextMeta(member, 1);
    }

    private static final Expression getContextMetaRef(IJsMember member) {
        String memberString = getContextMetaParameter(member);

        Context context = new Context(Context.VERSION_1_4);

        TokenStream stream = new TokenStream(new StringReader(memberString),
                "raw", 0, context);

        Parser parser = new Parser();

        Document document;
        try {
            document = parser.parse(stream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return (Expression) document.getStatements().get(0);
    }

    private static final String getContextMeta(IJsMember member, int token) {
        String context = member.getMetaDoc("context");
        if (context == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(context, " :");

        if (st.countTokens() < token) {
            return null;
        }

        for (; token > 0; token--) {
            st.nextToken();
        }

        return st.nextToken();
    }

    private static Expression createThisConstant(IJsMember member,
            OuterThis outerThis, IRegion region) {

        if (outerThis != null) {
            return outerThis.createRef(region);
        }

        return new ThisLiteral(region);
    }

    private static int computePosition(ASTNode node, NodeList nodeList) {

        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i) == node) {
                return i;
            }
        }

        if (node.getParent() == null) {
            return -1;
        }

        return computePosition(node.getParent(), nodeList);
    }

    private static void multiWindowStaticMethod(IJsMember member) {
        // TODO Auto-generated method stub

    }

    private static class OuterThis {

        public static final String NAME = "__this";

        private final String name;

        private final Value value;

        private boolean used;

        public OuterThis(Value outerThisValue) {
            this.value = outerThisValue;
            this.name = NAME;
        }

        public Value getValue() {
            return value;
        }

        public boolean isUsed() {
            return used;
        }

        public RefName createRef(IRegion region) {
            used = true;
            System.out.println("Indirection: use indirect this " + region);
            return new RefName(name, region);
        }
    }
}
