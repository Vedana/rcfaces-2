/*
 * $Id: Tools.java,v 1.2 2011/10/12 15:54:01 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vedana.js.Operation;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.ArrayAccess;
import com.vedana.js.dom.ArrayLiteral;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.BinaryExpression;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Document;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.HookExpression;
import com.vedana.js.dom.ICopyContext;
import com.vedana.js.dom.IOperation;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.NewExpression;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.ParenthesizedExpression;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.RegExpNode;
import com.vedana.js.dom.Statement;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.ThisLiteral;
import com.vedana.js.dom.TrueLiteral;
import com.vedana.js.dom.UndefinedLiteral;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

public class Tools {

    public static boolean containsSuperCall(Block body) {

        MethodInvocation mis[] = Visitors.visitMethodInvocations(body
                .getStatements(), false);
        for (int i = 0; i < mis.length; i++) {
            if ((mis[i].getObject() instanceof FieldAccess) == false) {
                continue;
            }

            FieldAccess fa = (FieldAccess) mis[i].getObject();

            if (fa.getProperty().getName().equals("f_super")) {
                return true;
            }
        }

        return false;
    }

    public static int countReturn(Block body) {
        return Visitors.visitReturns(body.getStatements(), false).length;
    }

    public static boolean canInlineBlock(Block body) {
        if (countReturn(body) > 0) {
            return false;
        }

        if (containsSuperCall(body)) {
            return false;
        }

        int count = Visitors.countNodes(body.getStatements());

        return count < 32;
    }

    public static boolean isMethodDecorated(IJsClass cobject, String mname,
            boolean constructor) {

        for (IJsType cType : cobject.listAspects()) {
            IJsClass c = cType.getJsClass();

            if (constructor) {
                mname = c.getName();
            }

            if (c.getMember(mname) != null) {
                return true;
            }

            if (isMethodDecorated(c, mname, constructor)) {
                return true;
            }
        }

        return false;
    }

    public static int inlineCode(JsStats stats, NodeList statements,
            Block source, int start) {

        int idxInclusion = JsOptimizer.INDEX_INCLUSIONS++;

        Block bodyCopied = (Block) copy(stats, source, "_in" + idxInclusion
                + "_");

        NodeList statementsToCopy = bodyCopied.getStatements();

        if (start < 0) {
            start = statements.size();
        }
        for (int j = 0; j < statementsToCopy.size(); j++) {
            statements.add(start + j, (ASTNode) statementsToCopy.get(j));
        }

        return statementsToCopy.size();
    }

    public static ASTNode copy(final JsStats stats, ASTNode body,
            final String defPrefix) {
        ASTNode node = body.copy(new ICopyContext() {

            private Map<String, String> names = new HashMap<String, String>();

            public ASTNode copy(ASTNode node) {
                if (node instanceof DefName) {
                    String n = ((DefName) node).getName();

                    String name = names.get(n);
                    if (name == null) {
                        name = defPrefix + "_" + names.size();
                        names.put(n, name);
                    }

                    if (node instanceof Parameter) {
                        return new Parameter(name, null);
                    }

                    return new DefName(name, null);

                } else if (node instanceof RefName) {
                    String n = ((RefName) node).getName();

                    String name = names.get(n);
                    if (name != null) {
                        return new RefName(name, null);
                    }
                }

                if (node == null) {
                    return null;
                }

                ASTNode c = node.copy(this);

                c.setSourceName(node.getSourceName());

                return c;
            }

            public List<ASTNode> copy(NodeList nodes) {
                List<ASTNode> l = new ArrayList<ASTNode>(nodes.size());
                for (int i = 0; i < nodes.size(); i++) {
                    ASTNode node = copy((ASTNode) nodes.get(i));

                    l.add(node);
                }

                return l;
            }

        });

        node.setSourceName(body.getSourceName());

        return node;
        /*
         * List l = new ArrayList();
         * 
         * l.add(new RefName("copy", null));
         * 
         * return new Block((Statement[]) l.toArray(new Statement[l.size()]),
         * null);
         */
    }

    public static boolean isUselessExpression(Expression expression) {

        if (isFalseLiteral(expression)) {
            return true;

        } else if (expression instanceof RefName) {
            return true;

        } else if (isThisLiteral(expression)) {
            return true;

        } else if (isThisLiteral(expression)) {
            return true;

        } else if (expression instanceof StringLiteral) {
            return true;

        } else if (expression instanceof NumberLiteral) {
            return true;

        } else if (isNullLiteral(expression)) {
            return true;

        } else if (isUndefinedLiteral(expression)) {
            return true;

        } else if (expression instanceof RegExpNode) {
            return true;

        } else if (expression instanceof FieldAccess) {
            FieldAccess fa = (FieldAccess) expression;

            return isUselessExpression(fa.getObject());

        } else if (expression instanceof Assignment) {
            return false;

        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryAccess = (BinaryExpression) expression;

            return isUselessExpression(binaryAccess.getLeft())
                    && isUselessExpression(binaryAccess.getRight());

        } else if (expression instanceof ParenthesizedExpression) {
            ParenthesizedExpression pe = (ParenthesizedExpression) expression;

            return isUselessExpression(pe.getExpression());

        } else if (expression instanceof PrefixExpression) {
            PrefixExpression prefixExpression = (PrefixExpression) expression;

            Operation operation = prefixExpression.getOperation();

            if (operation == Operation.DELETE
                    || operation == Operation.INCREMENT
                    || operation == Operation.DECREMENT) {
                return false;
            }

            if (operation == Operation.VOID
                    && prefixExpression.getExpression() == null) {
                return true;
            }

            return isUselessExpression(prefixExpression.getExpression());
        }

        return false;
    }

    public static boolean isUndefinedLiteral(Expression object) {

        if (object instanceof UndefinedLiteral) {
            return true;
        }

        Class< ? > cls = (Class< ? >) object
                .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);

        if (UndefinedLiteral.class.equals(cls)) {
            return true;
        }

        return false;
    }

    public static boolean isNullLiteral(Expression object) {

        if (object instanceof NullLiteral) {
            return true;
        }

        Class< ? > cls = (Class< ? >) object
                .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);

        if (NullLiteral.class.equals(cls)) {
            return true;
        }

        return false;
    }

    public static boolean isThisLiteral(Expression object) {

        if (object instanceof ThisLiteral) {
            return true;
        }

        Class< ? > cls = (Class< ? >) object
                .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);

        if (ThisLiteral.class.equals(cls)) {
            return true;
        }

        return false;
    }

    public static boolean isTrueLiteral(Expression object) {

        if (object instanceof TrueLiteral) {
            return true;
        }

        Class< ? > cls = (Class< ? >) object
                .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);

        if (TrueLiteral.class.equals(cls)) {
            return true;
        }

        return false;
    }

    public static boolean isFalseLiteral(Expression object) {

        if (object instanceof FalseLiteral) {
            return true;
        }

        Class< ? > cls = (Class< ? >) object
                .getProperty(JsOptimizer.FINAL_VALUE_PROPERTY);

        if (FalseLiteral.class.equals(cls)) {
            return true;
        }

        return false;
    }

    public static void replaceNode(Statement dest, Statement source) {
        if (source instanceof Block) {
            Block block = (Block) source;

            NodeList nl = dest.getParentList();
            for (int i = 0; i < nl.size(); i++) {
                ASTNode a = (ASTNode) nl.get(i);
                if (a != dest) {
                    continue;
                }

                nl.remove(i);
                NodeList n2 = block.getStatements();
                for (int j = 0; j < n2.size(); j++) {
                    nl.add(i + j, (ASTNode) n2.get(j));
                }

                return;
            }
        }

        if (dest.getParent() == null) {
            dest.getParentList().replaceBy(source, dest);
            return;
        }

        dest.replaceBy(source);
    }

    public static FunctionDeclaration getParentFunction(ASTNode parent) {
        FunctionDeclaration fd = null;

        for (; parent != null; parent = parent.getParent()) {
            if (parent instanceof FunctionDeclaration) {
                fd = (FunctionDeclaration) parent;
            }
        }

        return fd;
    }

    public static JsPrivateStaticMember searchStaticDef(DefName def,
            String className, JsStats stats) {
        JsPrivateStaticMember psm = stats.getPrivateStaticMember(className, def
                .getName());
        if (psm == null) {
            return null;
        }
        if (psm.definition != def.getParent()) {
            System.out.println("Definition ???? ");
            return null;
        }

        return psm;
    }

    public static JsPrivateStaticMember searchStaticMember(RefName ref,
            String className, JsStats stats, Set<String> classNames) {
        if ((ref.getParent() instanceof FieldAccess) == false) {
            return null;
        }

        FieldAccess fa = (FieldAccess) ref.getParent();
        if (fa.getProperty() == ref) {

            String idrClass = (String) fa.getObject().getProperty(
                    JsOptimizer.INDIRECT_CLASS_PROPERTY);
            if (idrClass != null) {
                JsPrivateStaticMember cst = stats.getPrivateStaticMember(
                        idrClass, ref.getName());
                return cst;
            }

            if (fa.getObject() instanceof FieldAccess) {
                FieldAccess fa2 = (FieldAccess) fa.getObject();

                RefName rr = fa2.getProperty();

                if (classNames.contains(rr.getName())) {
                    JsPrivateStaticMember cst = stats.getPrivateStaticMember(rr
                            .getName(), ref.getName());

                    // System.out.println("S=" + key + " => " + cst);
                    return cst;
                }

            } else if (fa.getObject() instanceof RefName) {
                RefName rName = (RefName) fa.getObject();
                String rclassName = rName.getName();

                if (classNames.contains(rclassName)) {
                    JsPrivateStaticMember cst = stats.getPrivateStaticMember(
                            rclassName, ref.getName());

                    // System.out.println("S=" + key + " => " + cst);
                    return cst;
                }

                if (className.equals("f_class")
                        && rclassName.equals("__statics")) { // La classe
                    // f_class
                    // appelle
                    // __static._X() privé ?

                    JsPrivateStaticMember cst = stats.getPrivateStaticMember(
                            "f_class", ref.getName());

                    // System.out.println("S=" + key + " => " + cst);
                    return cst;
                }

            } else if (fa.getObject() instanceof ThisLiteral) {

                FunctionDeclaration f = getParentFunction(fa);
                if (f != null && (f.getParent() instanceof Value)) {
                    Value vf = (Value) f.getParent();

                    if (vf.getLeft() instanceof DefName) {
                        String name = ((DefName) vf.getLeft()).getName();

                        IJsClass jsClass = stats.getJsClass(className);
                        IJsMember jsMember = jsClass.getMember(name);

                        if (jsMember != null && jsMember.isStatic()) {

                            JsPrivateStaticMember cst = stats
                                    .getPrivateStaticMember(className, ref
                                            .getName());

                            return cst;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static boolean isOnlyDigit(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (Character.isDigit(token.charAt(i)) == false) {
                return false;
            }
        }

        return true;
    }

    public static boolean isLocalVariable(ASTNode root, final Name ref) {
        FunctionDeclaration fd = Tools.getParentFunction(root);

        final boolean found[] = new boolean[1];

        fd.accept(new ASTVisitor() {

            @Override
            public boolean visit(DefName defName) {
                if (defName.getName().equals(ref.getName())) {
                    found[0] = true;
                    return false;
                }
                return super.visit(defName);
            }

            @Override
            public boolean visit(Parameter parameter) {
                return visit((DefName) parameter);
            }

        });

        return found[0];
    }

    public static IJsMethod[] searchParentMember(JsMethod jsMethod) {
        List<IJsMethod> methods = new ArrayList<IJsMethod>();

        searchParentMember(jsMethod.getJsClass(), jsMethod.getName(), methods);

        return methods.toArray(new IJsMethod[methods.size()]);
    }

    private static void searchParentMember(IJsClass jsClass, String memberName,
            List<IJsMethod> methods) {

        IJsType parentType = jsClass.getParent();
        if (parentType != null) {
            IJsClass parentClass = parentType.getJsClass();

            if (parentClass != null) {
                IJsMethod method = (IJsMethod) parentClass
                        .getMember(memberName);
                if (method != null) {
                    methods.add(method);

                } else {
                    searchParentMember(parentClass, memberName, methods);
                }
            }
        }

        IJsType aspects[] = jsClass.listAspects();
        for (IJsType aspect : aspects) {
            IJsClass aspectClass = aspect.getJsClass();
            if (aspectClass == null) {
                continue;
            }

            IJsMethod method = (IJsMethod) aspectClass.getMember(memberName);
            if (method != null) {
                methods.add(method);

                continue;
            }

            searchParentMember(aspectClass, memberName, methods);

        }
    }

    public static boolean verifyASTNode(ASTNode n) {
        if (n instanceof Document) {
            return true;
        }

        if (n.getParent() != null) {
            return verifyASTNode(n.getParent());
        }

        return false;
    }

    public static int verifyASTNodes(Collection< ? extends ASTNode> nodes) {

        int cnt = 0;

        for (Iterator< ? extends ASTNode> it = nodes.iterator(); it.hasNext();) {
            ASTNode n = it.next();

            if (Tools.verifyASTNode(n)) {
                continue;
            }

            it.remove();
            cnt++;
        }

        return cnt;
    }

    public static IJsType transformType(IJsType source, IJsType[] destType,
            IJsType[] sourceTypes) {

        for (int i = 0; i < sourceTypes.length; i++) {
            if (sourceTypes[i].equals(source)) {
                return destType[i];
            }
        }

        return source;
    }

    public static int getLevel(Expression expression) {

        if (expression instanceof Assignment) {
            return 5;
        }

        if (expression instanceof HookExpression) {
            return 10;
        }

        if (expression instanceof IOperation) {
            Operation operation = ((IOperation) expression).getOperation();

            if (operation == Operation.OR) {
                return 20;
            }

            if (operation == Operation.AND) {
                return 30;
            }

            if (operation == Operation.BITOR) {
                return 40;
            }

            if (operation == Operation.BITXOR) {
                return 41;
            }

            if (operation == Operation.BITAND) {
                return 50;
            }

            if (operation == Operation.SHALLOW_EQUALS
                    || operation == Operation.EQUALS
                    || operation == Operation.SHALLOW_NOT_EQUALS
                    || operation == Operation.NOT_EQUALS) {
                return 60;
            }

            if (operation == Operation.IN || operation == Operation.LESS
                    || operation == Operation.GREATER
                    || operation == Operation.INSTANCE_OF
                    || operation == Operation.GREATER_EQUALS
                    || operation == Operation.LESS_EQUALS) {
                return 70;
            }

            if (operation == Operation.LEFT_SHIFT
                    || operation == Operation.SIGNED_RIGHT_SHIFT
                    || operation == Operation.UNSIGNED_RIGHT_SHIFT) {
                return 80;
            }

            if (operation == Operation.ADD || operation == Operation.SUB) {
                return 90;
            }

            if (operation == Operation.MUL || operation == Operation.DIV
                    || operation == Operation.MOD) {
                return 100;
            }

            if (operation == Operation.NOT || operation == Operation.BITNOT
                    || operation == Operation.TYPEOF
                    || operation == Operation.VOID
                    || operation == Operation.POSITIVE
                    || operation == Operation.NEGATIVE
                    || operation == Operation.DELETE) {
                return 110;
            }

            if (operation == Operation.INCREMENT
                    || operation == Operation.DECREMENT) {
                return 115;
            }

            if (operation == Operation.COMMA) {
                return 0;
            }

            throw new IllegalStateException("Unknown operation " + operation);
        }

        if (expression instanceof MethodInvocation) {
            return 117;
        }

        if (expression instanceof ParenthesizedExpression) {
            return 120;
        }

        if (expression instanceof FieldAccess) {
            return 120;
        }

        if (expression instanceof UndefinedLiteral) {
            return 120;
        }

        if (expression instanceof NullLiteral) {
            return 120;
        }

        if (expression instanceof FalseLiteral) {
            return 120;
        }

        if (expression instanceof TrueLiteral) {
            return 120;
        }

        if (expression instanceof NumberLiteral) {
            return 120;
        }

        if (expression instanceof ThisLiteral) {
            return 120;
        }

        if (expression instanceof StringLiteral) {
            return 120;
        }

        if (expression instanceof NewExpression) {
            return 120;
        }

        if (expression instanceof ArrayAccess) {
            return 120;
        }

        if (expression instanceof ObjectLiteral) {
            return 120;
        }

        if (expression instanceof FunctionDeclaration) {
            return 120;
        }

        if (expression instanceof Name) {
            return 120;
        }

        if (expression instanceof ArrayLiteral) {
            return 120;
        }

        if (expression instanceof VarExpression) {
            return 120;
        }

        throw new IllegalStateException("Unknown expression");
    }

    public static void addNodeAfter(ASTNode brother, ASTNode newNode) {
        NodeList nl = brother.getParentList();

        for (int i = 0; i < nl.size(); i++) {
            if (nl.get(i) == brother) {
                nl.add(i + 1, newNode);
                return;
            }
        }

        nl.add(nl.size(), newNode);

    }

    public static void removeValueAndMoveExpression(Value v) {

        DefName name = v.getName();

        boolean canRemove = true;

        VarExpression var = (VarExpression) v.getParent();
        NodeList values = var.getValues();
        for (int i = values.size() - 1; i >= 0; i--) {
            Value v2 = (Value) values.get(i);

            if (v2 == v) {
                break;
            }

            if (v2.getLeft() != null
                    && canRemove(v2.getRight(), name.getName()) == false) {
                canRemove = false;
                break;
            }
        }

        if (canRemove == false) {
            return;
        }

        System.out.println("Optimize: remove unused var '" + name.getName()
                + "' value=" + v);
        v.replaceBy(null);

        if (v.getRight() == null || Tools.isUselessExpression(v.getRight())) {
            if (var.getValues().size() == 0) {
                var.replaceBy(null);
            }

            return;
        }

        if (var.getValues().size() == 0) {
            var.replaceBy(v.getRight());
            return;
        }

        Tools.addNodeAfter(var, v.getRight());
    }

    private static boolean canRemove(Expression exp, final String name) {
        if (exp == null) {
            return true;
        }

        if (Tools.isUselessExpression(exp) == false) {
            return false;
        }

        final boolean found[] = new boolean[1];

        exp.accept(new ASTVisitor() {

            @Override
            public void endVisit(RefName refName) {
                if (refName.getName().equals(name)) {
                    found[0] = true;
                }

                super.endVisit(refName);
            }
        });

        if (found[0]) {
            return false;
        }

        return true;
    }

    public static boolean isSameValue(Expression exp1, Expression exp2) {
        if (Tools.isThisLiteral(exp1) && Tools.isThisLiteral(exp2)) {
            return true;
        }

        if (Tools.isTrueLiteral(exp1) && Tools.isTrueLiteral(exp2)) {
            return true;
        }

        if (Tools.isFalseLiteral(exp1) && Tools.isFalseLiteral(exp2)) {
            return true;
        }

        if (Tools.isUndefinedLiteral(exp1) && Tools.isUndefinedLiteral(exp2)) {
            return true;
        }

        if (Tools.isNullLiteral(exp1) && Tools.isNullLiteral(exp2)) {
            return true;
        }

        if (exp1.getClass().equals(exp2.getClass()) == false) {
            return false;
        }

        if (exp1 instanceof NumberLiteral) {
            if (((NumberLiteral) exp1).getNumber() == ((NumberLiteral) exp2)
                    .getNumber()) {
                return true;
            }
        }

        if (exp1 instanceof StringLiteral) {
            if (((StringLiteral) exp1).getString().equals(
                    ((StringLiteral) exp2).getString())) {
                return true;
            }
        }

        if (exp1 instanceof RefName) {
            if (((RefName) exp1).getName().equals(((RefName) exp2).getName())) {
                return true;
            }
        }

        return false;
    }

}
