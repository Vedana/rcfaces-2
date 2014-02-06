/*
 * $Id: FunctionOptimizer.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.ForInStatement;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.TrueLiteral;
import com.vedana.js.dom.UndefinedLiteral;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class FunctionOptimizer {

    public static void analyseFunctions(FunctionDeclaration parentFunction,
            NodeList nodes, final JsStats stats, Locals locals,
            String className, Set<String> classNames) throws Exception {

        int maxSize = 0;

        FunctionDeclaration functions[] = Visitors.visitFunctions(nodes, true);
        for (int i = 0; i < functions.length; i++) {
            FunctionDeclaration function = functions[i];

            if (locals != null) {
                // Parametrize le nom de la fonction, et les variables associés
                if (function.getName() instanceof DefName) {
                    DefName defName = (DefName) function.getName();

                    locals.addParameter(defName);

                    RefName refsMethod[] = Visitors.visitRefNames(nodes, false);
                    for (int j = 0; j < refsMethod.length; j++) {
                        RefName ref = refsMethod[j];
                        if (ref.getName().equals(defName.getName()) == false) {
                            continue;
                        }
                        locals.addParameter(ref);
                    }
                }
            }

            Locals lp = (locals != null) ? locals.push() : new Locals(stats);

            analyseFunction(parentFunction, function, stats, lp, className,
                    classNames);

            if (lp.paramSize() > maxSize) {
                maxSize = lp.paramSize();
                // MAJ du nombre maxi de parametres ...
            }

            lp.pop();
        }
    }

    private static void analyseFunction(FunctionDeclaration parentFunction,
            FunctionDeclaration function, final JsStats stats, Locals locals,
            String className, Set<String> classNames) throws Exception {

        JsMember member = (JsMember) function
                .getProperty(JsOptimizer.MEMBER_PROPERTY);

        if (parentFunction == null && (function.getName() instanceof DefName)) {
            DefName def = (DefName) function.getName();

            JsPrivateStaticMember cst = stats.getPrivateStaticMember(className,
                    def.getName());
            if (cst != null) {
                stats.addPrivateMember(def, cst, false);

            } else if (stats.canTranslate.contains(def.getName())) {
                stats.addName(def.getName(), def, false);

            } else {
                stats.addChars(def.getName());
            }
        }

        DefName defs[] = Visitors.visitDefNames(function.getParameters(), true);
        for (int j = 0; j < defs.length; j++) {
            DefName def = defs[j];

            locals.addParameter(def);
        }

        defs = Visitors.visitDefNames(function.getBody().getStatements(), true);
        for (int j = 0; j < defs.length; j++) {
            DefName def = defs[j];

            if (def.getParent() instanceof Value) {
                if (def.getParent().getParent() instanceof ObjectLiteral) {

                    // On traite les cas { x: n, y: 2}

                    if (def.getName().startsWith("_")
                            || stats.canTranslate.contains(def.getName())) {
                        stats.addName(def.getName(), def, false);

                    } else {
                        stats.addChars(def.getName());
                    }
                    continue;
                }
            }

            locals.addParameter(def);
        }

        // System.out.println("Function#" + i + ":");
        // System.out.println(" params=" + function.getParameters());
        // System.out.println(" locals=" + locals);

        RefName refs[] = Visitors.visitRefNames(function.getBody()
                .getStatements(), true);
        for (int j = 0; j < refs.length; j++) {
            RefName ref = refs[j];

            if (locals.containsRef(ref.getName())) {
                // Reference à une variable locale ...
                boolean notLocal = false;

                if (ref.getParent() instanceof FieldAccess) {
                    FieldAccess fa = (FieldAccess) ref.getParent();

                    if (fa.getProperty() == ref) {
                        // on a le cas de : f_core.a avec a connu comme variable
                        // locale !
                        notLocal = true;
                    }
                }

                if (notLocal == false) {
                    locals.addParameter(ref);
                    continue;
                }
            }

            JsPrivateStaticMember cst = Tools.searchStaticMember(ref,
                    className, stats, classNames);
            if (cst != null) {
                // C'est un acces privé !
                stats.addPrivateMember(ref, cst, true);
                continue;
            }

            if ((ref.getName().startsWith("_") || stats.canTranslate
                    .contains(ref.getName()))
                    && stats.isLanguageReserved(ref.getName()) == false) {
                stats.addName(ref.getName(), ref, true);
                continue;
            }

            stats.addChars(ref.getName());
        }

        NodeList varValues = null;
        boolean varDeclared = false;
        NodeList sts = function.getBody().getStatements();
        if (sts.size() > 0 && sts.get(0) instanceof VarExpression) {
            varDeclared = true;
            varValues = ((VarExpression) sts.get(0)).getValues();
        }

        {
            if (JsOptimizer.MERGE_VARIABLES) {
                mergeVariables(function.getBody().getStatements());
            }

            if (JsOptimizer.CONCAT_VAR_TO_BEGIN) {
                concatVarsToBegin(function.getBody().getStatements(), stats,
                        locals);
            }

            if (JsOptimizer.GROUP_ASSIGNMENT_LITERALS) {
                groupSetLiterals(stats, function, locals, ThisLiteral.class,
                        "this", "this", true);

                if (parentFunction == null) {
                    groupSetLiterals(stats, function, locals,
                            NullLiteral.class, "null", "null", false);
                    groupSetLiterals(stats, function, locals,
                            FalseLiteral.class, "false", "!1", false);
                    groupSetLiterals(stats, function, locals,
                            TrueLiteral.class, "true", "!0", false);
                    groupSetLiterals(stats, function, locals,
                            UndefinedLiteral.class, "undefined", "", false);
                }
            }

            if (JsOptimizer.GROUP_LITERALS) {
                StringLiteral sls[] = Visitors.visitStringLiterals(function
                        .getBody().getStatements(), false);

                Map<String, List<StringLiteral>> dictionnary = new HashMap<String, List<StringLiteral>>();
                for (int j = 0; j < sls.length; j++) {
                    StringLiteral sl = sls[j];
                    String s = sl.getString();

                    List<StringLiteral> l = dictionnary.get(s);
                    if (l == null) {
                        l = new ArrayList<StringLiteral>();
                        dictionnary.put(s, l);
                    }

                    l.add(sl);
                }

                int cnt = 0;
                if (varValues == null) {
                    cnt -= "var ".length();
                }

                for (Iterator<Map.Entry<String, List<StringLiteral>>> it = dictionnary
                        .entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, List<StringLiteral>> entry = it.next();

                    String s = entry.getKey();
                    List<StringLiteral> l = entry.getValue();

                    if (l.size() < 2) {
                        it.remove();
                        continue;
                    }

                    cnt += (s.length() + 2) * l.size() - "ab=\"\",".length();
                }

                if (cnt > 0) {
                    if (varValues == null) {
                        VarExpression ve = new VarExpression(new Value[0], null);
                        function.getBody().getStatements().add(0, ve);

                        varValues = ve.getValues();
                        varDeclared = true;
                    }

                    int idx = 0;
                    for (Map.Entry<String, List<StringLiteral>> entry : dictionnary
                            .entrySet()) {
                        String s = entry.getKey();
                        List<StringLiteral> l = entry.getValue();

                        System.out.println("Optimize: Merge strings ("
                                + l.size() + ") \"" + s + "\"");

                        DefName defName = new DefName(JsOptimizer.STRING_PREFIX
                                + idx + ":" + s, null);
                        locals.addParameter(defName);

                        Value ass = new Value(defName, new StringLiteral(s,
                                null), null);
                        varValues.add(0, ass);

                        for (StringLiteral sl : l) {
                            RefName r = new RefName(defName.getName(), null);

                            stats.removeChars(sl.getString());
                            sl.replaceBy(r);
                            locals.addParameter(r);
                        }
                    }
                }
            }

            if (JsOptimizer.CONCAT_VAR_TO_BEGIN) {
                concatVarsToBegin(function.getBody().getStatements(), stats,
                        locals);

                if (sts.size() > 0 && sts.get(0) instanceof VarExpression) {
                    varDeclared = true;
                    varValues = ((VarExpression) sts.get(0)).getValues();
                }
            }

            // Il faut le faire à la fin, car la déclaration des nouveaux
            // parametres
            // Peut interferer avec les closes (function dans function)
            analyseFunctions(function, function.getBody().getStatements(),
                    stats, locals, className, classNames);

            Set<String> groupClasses;
            if (JsOptimizer.MULTI_WINDOW) {
                groupClasses = JsOptimizer.NATIVE_GROUP_CLASSES;
            } else {
                groupClasses = JsOptimizer.GROUP_CLASSES;
            }

            if (JsOptimizer.GROUP_CLASS_ACCESS && parentFunction == null) {
                String s[] = groupClasses.toArray(new String[groupClasses
                        .size()]);

                Set<String> dontInline = new HashSet<String>();
                if (member != null) {
                    String v = member.modifier
                            .getMeta(JsOptimizer.DONT_INLINE_META);

                    if (v != null) {
                        StringTokenizer st = new StringTokenizer(v);
                        for (; st.hasMoreTokens();) {
                            dontInline.add(st.nextToken());
                        }
                    }
                }

                for (int i = 0; i < s.length; i++) {

                    if (stats.canTranslate.contains(s[i])) {
                        continue;
                    }

                    if (dontInline.contains(s[i])) {
                        continue;
                    }

                    Map<String, List<FieldAccess>> optimizedMethodAccess = new HashMap<String, List<FieldAccess>>();
                    int optimizedCount = 0;

                    List<RefName> l = new ArrayList<RefName>();
                    refs = Visitors.visitRefNames(function.getBody()
                            .getStatements(), false);
                    for (int j = 0; j < refs.length; j++) {
                        RefName ref = refs[j];
                        String refName = ref.getName();

                        if (refName.equals(s[i]) == false) {
                            continue;
                        }

                        if (ref.getParent() instanceof FieldAccess) {
                            FieldAccess fa = (FieldAccess) ref.getParent();

                            Expression obj = fa.getObject();
                            if ((obj instanceof RefName) == false
                                    || ref.getName().equals(
                                            ((RefName) obj).getName()) == false) {
                                continue;
                            }

                            String prop = fa.getProperty().getName();

                            boolean optimize = false;
                            if (classNames.contains(refName)) {
                                if (Character.isUpperCase(prop.charAt(0))) {

                                    IJsMember member2 = searchFinalField(fa
                                            .getProperty(), refName, stats,
                                            classNames);

                                    if (member2 != null) {
                                        optimize = true;

                                        // On optimise que si c'est static et
                                        // final !
                                    } else {
                                        // System.out.println("NON ! " +
                                        // member);
                                    }
                                }
                            } else if (refName.equals("arguments")) {
                                if ("length".equals(prop)) {
                                    optimize = true;
                                }
                            } else if (refName.equals("Math")) {
                                optimize = true;
                            }

                            if (optimize) {
                                String key = prop;
                                List<FieldAccess> lo = optimizedMethodAccess
                                        .get(key);
                                if (lo == null) {
                                    lo = new ArrayList<FieldAccess>();
                                    optimizedMethodAccess.put(key, lo);
                                }

                                lo.add(fa);

                                if (lo.size() > 1) {
                                    // On le fait et on le force !
                                    varDeclared = true;
                                    optimizedCount++;
                                }
                            }

                        } else if (classNames.contains(refName)) {

                            if (refName.equals(className) == false) {
                                IJsClass rc = stats.getJsClass(className);
                                if (rc != null
                                        && JsOptimizer.coreClasses
                                                .contains(refName) == false
                                        && rc
                                                .containsDependency(stats,
                                                        refName) == false) {

                                    System.out.println("No dependencies: "
                                            + refName + " for class '"
                                            + className + "'.");
                                    continue;
                                }
                            }
                        } else {
                            // C'est un acces à arguments
                            // Ou un truc du style x instanceof f_xxx
                        }

                        l.add(ref);
                    }

                    int cnt = ("a=" + s[i] + ",").length();
                    if (varDeclared == false) {
                        cnt += "var ".length(); // var a ajouter
                    }

                    DefName varDefName = null;
                    if ((l.size() - optimizedCount) * s[i].length() >= cnt) {
                        if (varValues == null) {
                            VarExpression ve = new VarExpression(new Value[0],
                                    null);
                            function.getBody().getStatements().add(0, ve);

                            varValues = ve.getValues();
                            varDeclared = true;
                        }

                        varDefName = new DefName(
                                JsOptimizer.FIELD_ACCESSOR_PREFIX + s[i], null);
                        RefName varRefName = new RefName(s[i], null);
                        locals.addParameter(varDefName);

                        Value ass = new Value(varDefName, varRefName, null);
                        varValues.add(0, ass);

                        for (RefName r : l) {
                            stats.removeChars(r.getName());
                            r.setName(varDefName.getName());
                            locals.addParameter(r);
                        }
                    }

                    for (Map.Entry<String, List<FieldAccess>> entry : optimizedMethodAccess
                            .entrySet()) {
                        String name = entry.getKey();
                        List<FieldAccess> lo = entry.getValue();

                        if (lo.size() < 2) {
                            continue;
                        }

                        System.out.println("Optimize2: Optimize field access ("
                                + lo.size() + ") " + s[i] + "." + name);

                        if (varValues == null) {
                            VarExpression ve = new VarExpression(new Value[0],
                                    null);
                            function.getBody().getStatements().add(0, ve);

                            varValues = ve.getValues();
                            varDeclared = true;
                        }

                        DefName defName = new DefName(
                                JsOptimizer.METHOD_ACCESSOR_PREFIX + s[i] + "!"
                                        + name, null);
                        locals.addParameter(defName);

                        RefName varRefName;
                        int pos; // On le positionne aprés le def du nom
                        if (varDefName == null) {
                            varRefName = new RefName(s[i], null);
                            pos = 0; // Pas de def, on le positionne avant !
                            // stats.addName(varRefName.getName(), varRefName,
                            // true);
                            stats.addRef(varRefName, className, classNames);

                        } else {
                            varRefName = new RefName(varDefName.getName(), null);
                            pos = 1;
                            locals.addParameter(varRefName);
                        }

                        RefName frn = lo.get(0).getProperty(); // new
                        // RefName(name,
                        // null);
                        lo.get(0).setProperty(null);
                        // stats.addRef(canTranslate, frn, className,
                        // classNames);

                        FieldAccess fa = new FieldAccess(varRefName, frn, null);

                        Value ass = new Value(defName, fa, null);
                        varValues.add(pos, ass);

                        for (FieldAccess facess : lo) {
                            RefName r = new RefName(defName.getName(), null);

                            stats.removeChars(name);
                            facess.replaceBy(r);
                            locals.addParameter(r);
                        }
                    }
                }

            }

            /*
             * refs = Visitors.visitRefNames(function.getBody().getStatements(),
             * true); for (int j = 0; j < refs.length; j++) { RefName ref =
             * refs[j];
             * 
             * if (locals.contains(ref.getName())) { addParameter(stats, locals,
             * ref, false); continue; } }
             */
        }
    }

    private static IJsMember searchFinalField(RefName ref, String className,
            JsStats stats, Set<String> classNames) {
        if ((ref.getParent() instanceof FieldAccess) == false) {
            return null;
        }
        FieldAccess fa = (FieldAccess) ref.getParent();
        if (fa.getProperty() == ref) {
            if (fa.getObject() instanceof RefName) {
                RefName rr = (RefName) fa.getObject();

                if (classNames.contains(rr.getName())) {
                    IJsMember cst = stats.searchFinalField(rr.getName(), ref
                            .getName());

                    return cst;
                }

            } else if (fa.getObject() instanceof ThisLiteral) {
                FunctionDeclaration f = Tools.getParentFunction(fa);
                if (f != null && (f.getParent() instanceof Value)) {
                    Value vf = (Value) f.getParent();

                    if (vf.getLeft() instanceof DefName) {
                        String name = ((DefName) vf.getLeft()).getName();

                        if (name.equals("Finalizer")
                                || name.equals("Initializer")) {

                            IJsMember cst = stats.searchFinalField(className,
                                    ref.getName());

                            return cst;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static void concatVarsToBegin(NodeList statements, JsStats stats,
            Locals locals) {
        VarExpression vs[] = Visitors.visitVarExpressions(statements, true);
        if (vs.length < 2) {
            return;
        }

        List<String> l = new ArrayList<String>();

        for (int j = 0; j < vs.length; j++) {
            VarExpression v = vs[j];

            if (v.getParent() instanceof ForInStatement) {
                Value value = (Value) v.getValues().get(0);

                DefName valueName = (DefName) value.getLeft();

                // Note le nom du parametre
                l.add(valueName.getName());

                RefName newRef = new RefName(valueName.getName(), null);
                v.replaceBy(newRef);

                locals.addParameter(newRef);

                continue;
            }

            if (v == statements.get(0)) {
                // C'est le premier VAR, on laisse tomber !
                continue;
            }

            ASTNode values[] = v.getValues().toArray();

            Expression newExpression = null;
            InfixExpression lastExpression = null;

            int pos = 0;
            NodeList pc = v.getParentList();
            if (pc == null) {
                // un for(var x=YYY,y=ZZZZ
                // if (values.length == 1) {
                pos = -1;
                // }

            } else {
                for (; pos < pc.size(); pos++) {
                    if (pc.get(pos) == v) {
                        break;
                    }
                }
            }
            if (pos >= 0) {
                v.replaceBy(null);
            }

            for (int k = 0; k < values.length; k++) {
                Value value = (Value) values[k];

                DefName valueName = (DefName) value.getLeft();

                // Note le nom du parametre
                l.add(valueName.getName());

                if (value.getRight() == null) {
                    // On a un var x
                    // On le remplace par rien !

                    if (pos < 0) {
                        value.replaceBy(null);
                    }
                    continue;
                }

                // On a un var x=2
                // On le transforme en x=2;
                RefName newRef = new RefName(valueName.getName(), null);

                Assignment ass = new Assignment(null, newRef, value.getRight(),
                        value.getRegion());
                if (pos >= 0) {
                    pc.add(pos++, ass);

                } else if (newExpression != null) {
                    if (lastExpression == null) {
                        lastExpression = new InfixExpression(Operation.COMMA,
                                newExpression, ass, null);
                        newExpression = lastExpression;

                    } else {
                        InfixExpression ie = new InfixExpression(
                                Operation.COMMA, lastExpression.getRight(),
                                ass, null);
                        lastExpression.setRight(ie);
                        lastExpression = ie;
                    }

                } else {
                    newExpression = ass;
                }

                locals.addParameter(newRef);
            }

            if (pos < 0) {
                if (newExpression != null) {
                    v.replaceBy(newExpression);

                } else if (v.getValues().size() == 0) {
                    v.replaceBy(null);
                }
            }
        }

        NodeList values = null;

        if (statements.size() > 0) {
            if (statements.get(0) instanceof VarExpression) {
                VarExpression ve = (VarExpression) statements.get(0);

                values = ve.getValues();
            }
        }

        int cnt = (values == null) ? 2 : 1;

        if (l.size() < cnt) {
            return;
        }

        if (values == null) {
            VarExpression ve = new VarExpression(new Value[0], null);
            statements.add(0, ve);

            values = ve.getValues();
        }

        next_name: for (String vname : l) {
            ASTNode vs2[] = values.toArray();
            for (int j = 0; j < vs2.length; j++) {
                Value v = (Value) vs2[j];
                DefName d = (DefName) v.getLeft();

                if (d.getName().equals(vname)) {
                    continue next_name;
                }
            }

            DefName newDefName = new DefName(vname, null);
            locals.addParameter(newDefName);

            values.add(values.size(), new Value(newDefName, null, null));
        }

        ASTNode as[] = statements.toArray();
        int valuesPos = 0;

        setVar: for (int j = 1; j < as.length; j++) {
            ASTNode n = as[j];

            if ((n instanceof Assignment) == false) {
                break;
            }

            Assignment a = (Assignment) n;

            if (a.getOperation() != null) {
                break;
            }

            if ((a.getLeft() instanceof DefName) == false) {
                break;
            }

            Value v;
            DefName rn = (DefName) a.getLeft();
            for (;;) {
                if (valuesPos >= values.size()) {
                    break setVar;
                }

                v = (Value) values.get(valuesPos);
                if (rn.getName().equals(((DefName) v.getLeft()).getName())) {
                    break;
                }

                valuesPos++;
            }

            ((Value) values.get(j)).setRight(a.getRight());
            statements.replaceBy(null, a);
        }
    }

    private static void mergeVariables(NodeList nodeList) {
        FunctionDeclaration fds[] = Visitors.visitFunctions(nodeList, true);

        for (int i = 0; i < fds.length; i++) {
            FunctionDeclaration fd = fds[i];

            final List<String> values = new ArrayList<String>();
            final Map<String, DefName> vars = new HashMap<String, DefName>();

            Block block = fd.getBody();

            final List<VarPos> lposs = new ArrayList<VarPos>();

            // rechercher si on utilise arguments !! Auquel cas il ne faut pas
            // toucher aux parametres ...

            final boolean ret[] = new boolean[1];

            block.getStatements().accept(new ASTVisitor() {
                @Override
                public boolean visit(RefName name) {
                    if (name.getName().equals("arguments")) {
                        ret[0] = true;
                    }
                    return false;
                }
            });

            NodeList ps = fd.getParameters();
            for (int j = 0; j < ps.size(); j++) {
                Parameter parameter = (Parameter) ps.get(j);

                String name = parameter.getName();

                if (values.contains(name)) {
                    continue;
                }

                values.add(name);
                vars.put(name, parameter);

                VarPos vp = new VarPos();
                vp.start = 0;
                vp.readOnly = ret[0];

                if (ret[0]) {
                    // System.out.println("RET");
                }
                lposs.add(vp);
            }

            block.accept(new ASTVisitor() {

                @Override
                public void endVisit(Value value) {
                    if ((value.getParent() instanceof VarExpression) == false) {
                        return;
                    }

                    String name = value.getName().getName();

                    int pos = values.indexOf(name);
                    if (pos >= 0) {
                        if (lposs.get(pos).start == 0) {
                            System.err
                                    .println("Alert parameter and name have the same name");
                        }
                        return;
                    }
                    values.add(name);
                    lposs.add(new VarPos());
                }
            });

            final VarPos poss[] = lposs.toArray(new VarPos[lposs.size()]);

            // On recherche si des variables referencés sont utilisées dans les
            // functions filles
            FunctionDeclaration funcChildren[] = Visitors.visitFunctions(block
                    .getStatements(), false);
            for (int j = 0; j < funcChildren.length; j++) {

                funcChildren[j].getBody().accept(new ASTVisitor() {

                    @Override
                    public boolean visit(RefName name) {
                        ASTNode parent = name.getParent();
                        if (parent instanceof FieldAccess) {
                            if (((FieldAccess) parent).getProperty() == name) {
                                return false;
                            }
                        }

                        int idx = values.indexOf(name.getName());
                        if (idx < 0) {
                            return false;
                        }
                        poss[idx].readOnly = true;

                        return false;
                    }
                });
            }

            NodeList nlb = block.getStatements();
            for (int j = 0; j < nlb.size(); j++) {
                ASTNode ast = (ASTNode) nlb.get(j);
                final int jj = j;

                ast.accept(new ASTVisitor() {

                    @Override
                    public boolean visit(DefName name) {
                        if (vars.containsKey(name.getName()) == false) {
                            vars.put(name.getName(), name);
                        }

                        return visit((Name) name);
                    }

                    @Override
                    public boolean visit(RefName name) {
                        ASTNode parent = name.getParent();
                        if (parent instanceof FieldAccess) {
                            if (((FieldAccess) parent).getProperty() == name) {
                                return false;
                            }
                        }

                        return visit((Name) name);
                    }

                    public boolean visit(Name name) {
                        int pos = values.indexOf(name.getName());
                        if (pos < 0) {
                            return false;
                        }

                        poss[pos].access.add(name);

                        if (poss[pos].start < 0) {
                            poss[pos].start = jj;
                        }
                        if (poss[pos].end < jj) {
                            poss[pos].end = jj;
                        }

                        return true;
                    }
                });
            }

            Arrays.sort(poss, new Comparator<VarPos>() {

                public int compare(VarPos v0, VarPos v1) {
                    return v0.start - v1.start;
                }

            });

            for (int j = 0; j < poss.length; j++) {
                // On recherche les fusions

                if (poss[j].start < 0 || poss[j].readOnly) {
                    continue;
                }

                for (;;) {
                    int found = -1;
                    int foundPos = -1;

                    for (int k = j + 1; k < poss.length; k++) {
                        if (poss[k].start < 0 || poss[k].readOnly) {
                            continue;
                        }
                        if ((poss[k].start > poss[j].end || poss[k].end < poss[k].start) == false) {
                            continue;
                        }

                        // On recherche le remplacement qui va le moins loin
                        if (found > 0 && foundPos < poss[k].end) {
                            continue;
                        }
                        found = k;
                        foundPos = poss[k].end;
                    }

                    if (found < 0) {
                        break;
                    }

                    System.out.println("Optimize: merge var '"
                            + values.get(found) + "' [" + poss[found].start
                            + "," + poss[found].end + "] with '"
                            + values.get(j) + "' [" + poss[j].start + ","
                            + poss[j].end + "]" + vars.get(values.get(j)));

                    // On fusionne !
                    poss[found].start = -1;
                    poss[j].end = poss[found].end;

                    for (Name name : poss[found].access) {
                        if (name instanceof DefName) {
                            if (name.getParent().getParent() instanceof VarExpression) {
                                Value value = (Value) name.getParent();

                                if (value.getRight() == null) {
                                    value.setRight(new UndefinedLiteral(null));
                                }
                            }
                        }

                        name.setName(values.get(j));
                    }
                }
            }
        }
    }

    private static final void groupSetLiterals(JsStats stats,
            FunctionDeclaration function, Locals locals, Class< ? > cls,
            String litteralName, String litteralAssign, boolean stopByFunction)
            throws Exception {

        NodeList statements = function.getBody().getStatements();

        int assignSize = litteralAssign.length();
        if (assignSize > 0) {
            assignSize++; // pour le =
        }
        assignSize += 2; // ,a

        VarExpression varExpression = null;
        // Recherche le var !
        if (statements.size() > 0) {
            ASTNode st = (ASTNode) statements.get(0);
            if (st instanceof VarExpression) {
                varExpression = (VarExpression) st;
            }
        }
        if (varExpression == null) {
            assignSize += 5; // 'var ' ... ';'
        }

        ASTNode litterals[] = Visitors.visitNodesByClass(statements, cls,
                stopByFunction);

        // On recherche une constante finale ... en on discute meme pas de la
        // taille !
        if (varExpression != null) {
            NodeList values = varExpression.getValues();
            for (int i = 0; i < values.size(); i++) {
                Value value = (Value) values.get(i);

                Class< ? > finalValue = (Class< ? >) value.getLeft()
                        .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);
                if (finalValue == null) {
                    continue;
                }

                if (cls.equals(finalValue) == false) {
                    continue;
                }

                for (int j = 0; j < litterals.length; j++) {

                    if (litterals[j] == value.getRight()) {
                        continue;
                    }

                    stats.removeChars(litteralName);

                    RefName refName = new RefName(((Name) value.getLeft())
                            .getName(), null);
                    refName.putProperty(JsOptimizer.FINAL_VALUE_PROPERTY,
                            finalValue);
                    locals.addParameter(refName);
                    litterals[j].replaceBy(refName);
                }

                return;
            }
        }

        int total = litterals.length
                * ((litteralAssign.length() > 0) ? litteralAssign.length()
                        : litteralName.length());
        if (total < assignSize) {
            return;
        }

        DefName defName = new DefName(
                JsOptimizer.LITERAL_PREFIX + litteralName, null);
        locals.addParameter(defName);
        defName.putProperty(JsOptimizer.FINAL_VALUE_PROPERTY, cls);

        Expression exValue = null;
        if (litteralAssign.length() > 0) {
            exValue = (Expression) cls.getConstructors()[0]
                    .newInstance(new Object[] { null });
        }
        Value value = new Value(defName, exValue, null);

        if (varExpression == null) {
            VarExpression ast = new VarExpression(new Value[] { value }, null);

            statements.add(0, ast);

        } else {
            varExpression.getValues().add(0, value);
        }

        for (int j = 0; j < litterals.length; j++) {
            stats.removeChars(litteralName);

            RefName refName = new RefName(defName.getName(), null);
            refName.putProperty(JsOptimizer.FINAL_VALUE_PROPERTY, cls);
            locals.addParameter(refName);
            litterals[j].replaceBy(refName);
        }
    }

    private static class VarPos {
        public boolean readOnly;

        int start = -1;

        int end;

        List<Name> access = new ArrayList<Name>();
    }

    private static class Locals {

        private Map<String, List<Name>> names = new HashMap<String, List<Name>>();

        private Locals parent;

        private final JsStats stats;

        public Locals(JsStats stats) {
            this.stats = stats;
        }

        public Locals(Locals parent) {
            this(parent.stats);

            this.parent = parent;

            this.names = parent.names;
        }

        public void addParameter(Name node) {
            List<Name> idx = names.get(node.getName());
            if (idx == null) {
                idx = new ArrayList<Name>();
                names.put(node.getName(), idx);
            }

            idx.add(node);
        }

        public Locals push() {
            return new Locals(this);
        }

        public void pop() {
            if (parent != null) {

                return;
            }

            List<String> sorted = new ArrayList<String>(names.keySet());
            Collections.sort(sorted, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    List<Name> l1 = names.get(o1);
                    List<Name> l2 = names.get(o2);

                    return l2.size() - l1.size();
                }
            });

            int idx = 0;
            for (String n : sorted) {
                stats.addParamNodes(idx++, names.get(n));
            }
        }

        public int paramSize() {
            return names.size();
        }

        public boolean containsRef(String name) {
            return names.containsKey(name);
        }

        public Locals getParent() {
            return parent;
        }

    }

}
