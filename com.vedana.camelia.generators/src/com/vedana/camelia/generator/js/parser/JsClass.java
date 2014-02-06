/*
 * $Id: JsClass.java,v 1.4 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.ArrayLiteral;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.CatchClause;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.ForStatement;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.IfStatement;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NewExpression;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ReturnStatement;
import com.vedana.js.dom.Statement;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.ThrowStatement;
import com.vedana.js.dom.TryStatement;
import com.vedana.js.dom.Value;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.4 $ $Date: 2013/11/14 14:08:48 $
 */
public class JsClass extends JsMetaProperties implements IJsClass {

    private static final IJsType[] JS_TYPE_EMPTY_ARRAY = new IJsType[0];

    private boolean external;

    private final String className;

    private String packageName;

    private final Set<String> dependencyClassNames = new HashSet<String>();

    private Set<IJsType> dependencyJsClasses = null;

    private final boolean isAspect;

    private List<IJsMember> members = new ArrayList<IJsMember>();

    private JsModifier modifier;

    private IJsType parent;

    private List<IJsType> aspects = new ArrayList<IJsType>();

    private Map<String, JsBundleClass> bundlesByName = new HashMap<String, JsBundleClass>();

    private List<IJsClass> reverseExtension = new ArrayList<IJsClass>();

    private List<IJsClass> reverseAspect = new ArrayList<IJsClass>();

    private ObjectLiteral membersListNodes;

    private ObjectLiteral staticMembersListNodes;

    private List<NodeList> statements = new ArrayList<NodeList>();

    private NewExpression newExpression;

    private IJsType[] parameters = JS_TYPE_EMPTY_ARRAY;

    public JsClass(String className, boolean isAspect) {
        int idx = className.indexOf('.');
        if (idx > 0) {
            setPackageName(className.substring(0, idx));
            className = className.substring(idx + 1);
        }

        assert className != null && className.indexOf('<') < 0
                && className.indexOf('[') < 0 : "Invalid className "
                + className;

        this.className = className;
        this.isAspect = isAspect;

        modifier = new JsModifier(className, className, null, 0,
                Modifier.PUBLIC);
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public IJsType[] listParameters() {
        return parameters;
    }

    public void setTemplates(IJsType[] templates) {
        this.parameters = templates;
    }

    public final String getName() {
        return className;
    }

    public void setParent(IJsType parent) {
        assert parent != null : "Parent is null";

        this.parent = parent;

        if (parent instanceof JsClass) {
            ((JsClass) parent).reverseExtension.add(this);
        }
    }

    public void addMember(JsStats stats, String name, JsMember member) {

        for (IJsMember m : members) {
            if (m.getName().equals(member.getName()) == false) {
                continue;
            }

            if (m.isStatic() != member.isStatic()) {
                continue;
            }

            stats.errorLog.error("Member already defined : " + member, null,
                    this);
        }

        members.add(member);
    }

    public void setModifier(JsModifier modifier) {
        this.modifier = modifier;
    }

    public JsModifier getModifier() {
        return modifier;
    }

    public IJsMember getMember(String name) {

        return getMember(name, 0);
    }

    public IJsMember getMember(String name, int mask) {
        for (IJsMember member : members) {
            if (member.getName().equals(name)
                    && member.getDecorationMask() == mask) {
                return member;
            }
        }

        return null;
    }

    public void addDependency(String className) {
        dependencyClassNames.add(className);
    }

    public boolean containsDependency(JsStats stats, String className) {
        return listDependencies(stats).contains(className);
    }

    public Set<IJsType> listDependencies(JsStats stats) {
        if (dependencyJsClasses != null) {
            return dependencyJsClasses;
        }

        List<String> l = new ArrayList<String>(dependencyClassNames);

        dependencyJsClasses = new HashSet<IJsType>(l.size());

        for (String name : l) {

            IJsClass rc = stats.getJsClass(name);

            dependencyJsClasses.addAll(rc.listDependencies(stats));
        }

        return dependencyJsClasses;
    }

    public IJsType getParent() {
        return parent;
    }

    public IJsMember[] listMembers() {
        Collection<IJsMember> c = members;

        return c.toArray(new IJsMember[c.size()]);
    }

    public IJsType[] listAspects() {
        return aspects.toArray(new IJsType[aspects.size()]);
    }

    public void addAspect(IJsType rc) {
        aspects.add(rc);

        if (rc.getJsClass() == null) {
            return;
        }

        ((JsClass) rc.getJsClass()).reverseAspect.add(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        final JsClass other = (JsClass) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }

    public boolean containsAspect(IJsType aspectClass) {
        return aspects.contains(aspectClass);
    }

    public boolean containsAspectIgnoreTemplate(IJsType jsAspectClass) {
        for (IJsType docAspect : aspects) {
            if (docAspect.getJsClass() == null) {
                continue;
            }

            if (docAspect.getJsClass().getName()
                    .equals(jsAspectClass.getJsClass().getName())) {
                return true;
            }
        }

        return false;
    }

    private void updateJsClass(JsStats stats, JsComment classComment) {
        TypeTokenizer st = new TypeTokenizer(classComment.getValue(),
                ", \t\r\n");
        int step = 0;
        int v = Modifier.PUBLIC;
        boolean _final = false;
        boolean _abstract = false;

        int type = classComment.getType().equals("aspect") ? JsModifier.ASPECT_TYPE
                : JsModifier.CLASS_TYPE;

        for (; st.hasMoreTokens();) {
            String tok = st.nextToken();

            if (step == 0) {
                if ("public".equalsIgnoreCase(tok)) {
                    v = Modifier.PUBLIC;
                    continue;
                }

                if ("protected".equalsIgnoreCase(tok)) {
                    v = Modifier.PROTECTED;
                    continue;
                }

                if ("private".equalsIgnoreCase(tok)) {
                    v = Modifier.PRIVATE;
                    continue;
                }

                if ("hidden".equalsIgnoreCase(tok)) {
                    v = 0;
                    continue;
                }

                if ("final".equalsIgnoreCase(tok)) {
                    _final = true;
                    continue;
                }

                if ("abstract".equalsIgnoreCase(tok)) {
                    _abstract = true;
                    continue;
                }

                if (tok.startsWith(getName()) == false) {
                    stats.errorLog.error("INVALID @class class name for '"
                            + getName() + "'.", null, this);
                    break;
                }

                if (_abstract) {
                    v |= Modifier.ABSTRACT;
                }

                if (_final) {
                    v |= Modifier.FINAL;
                }

                JsModifier mm = new JsModifier(tok, null, null, type, v);

                setModifier(mm);

                step = 1;
                continue;
            }
            if (step == 1) {
                // Extends !
                if (tok.equals("extends")) {

                    if (classComment.getType().equals("aspect")) {
                        step = 3;
                        continue;
                    }

                    step = 2;
                    continue;
                }

                stats.errorLog.error("INVALID class declaration syntax", null,
                        this);
                break;
            }
            if (step == 2) {
                // C'est le nom de la superclass !

                step = 3;

                IJsType superrc = JsType.parse(stats, tok, this, false);
                if (superrc == null) {
                    if (tok.equals("Object")) {
                        break;
                    }

                    stats.errorLog.error("Unknown class parent: " + tok, null,
                            this);
                    break;
                }

                setParent(superrc);
                continue;
            }
            if (step == 3) {
                step = 3;

                IJsType aspectrc = JsType.parse(stats, tok, this, false);
                if (aspectrc == null) {
                    if (isSystemAspect(tok)) {
                        continue;
                    }
                    stats.errorLog.error("Unknown Aspect: " + tok, null, this);
                    break;
                }

                addAspect(aspectrc);
                continue;
            }

        }

        if (step == 0) {

            if (_abstract) {
                v |= Modifier.ABSTRACT;
            }

            if (_final) {
                v |= Modifier.FINAL;
            }

            JsModifier mm = new JsModifier(getName(), null, null, type, v);

            setModifier(mm);
        }

    }

    public static String extractClassName(JsComment jsComment) {
        for (TypeTokenizer st = new TypeTokenizer(jsComment.getValue()); st
                .hasMoreTokens();) {
            String tok = st.nextToken();

            if ("public".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("protected".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("private".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("hidden".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("static".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("final".equalsIgnoreCase(tok)) {
                continue;
            }

            if ("abstract".equalsIgnoreCase(tok)) {
                continue;
            }

            return tok;
        }

        return null;
    }

    public static JsComment searchClassComment(JsStats stats, String fileName,
            JsComment[] comments) {

        JsComment result = null;

        for (int i = 0; i < comments.length; i++) {
            JsComment comment = comments[i];

            String type = comment.getType();
            if ("class".equals(type) || "aspect".equals(type)) {
                if (result != null) {
                    stats.errorLog.error(
                            "Multiple @class or @aspect declarations "
                                    + comment.getComment().getText(), null,
                            fileName);
                }
                result = comment;
            }
        }

        return result;
    }

    private void parseClass(NodeList nodeList, NewExpression newExpression,
            JsComment comments[], JsStats stats) {

        this.newExpression = newExpression;

        NodeList parameters = newExpression.getParameters();

        String className = ((StringLiteral) parameters.get(0)).getString();
        if (getName().equals(className) == false) {
            stats.errorLog.error("Different class name ! " + newExpression,
                    null, this);
        }

        if (parameters.size() < 2) {
            return;
        }

        if ((parameters.get(1) instanceof ObjectLiteral) == false) {
            // Premier LookId

            // ASTNode lookid = (ASTNode) parameters.get(1);
            RefName staticMembers = null;
            RefName members = null;

            if (parameters.size() > 2) {
                if (parameters.get(2) instanceof RefName) {
                    staticMembers = (RefName) parameters.get(2);
                }

                if (parameters.size() > 3) {
                    if (parameters.get(3) instanceof RefName) {
                        members = (RefName) parameters.get(3);
                    }

                    if (parameters.size() > 4) {
                        String parentClassName = ((RefName) parameters.get(4))
                                .getName();

                        IJsClass parentClass = stats
                                .getJsClass(parentClassName);
                        if (parentClass.equals(getParent().getJsClass()) == false) {
                            stats.errorLog.error(
                                    "Different declaration for parent className '"
                                            + parentClassName + "' "
                                            + parameters.get(4), null, this);
                        }

                        int cntAspects = 0;
                        for (int i = 5; parameters.size() > i; i++) {
                            String aspectClassName = ((RefName) parameters
                                    .get(i)).getName();

                            if (isSystemAspect(aspectClassName)) {
                                continue;
                            }

                            IJsType aspectClass = JsType.parse(stats,
                                    aspectClassName, null, false);
                            if (containsAspect(aspectClass) == false) {
                                stats.errorLog
                                        .error("Different declaration for aspect '"
                                                + aspectClassName
                                                + "' "
                                                + parameters.get(i), null, this);
                            }
                            cntAspects++;
                        }
                        if (cntAspects != listAspects().length) {
                            stats.errorLog.error("Different count of aspects "
                                    + className, null, this);
                        }
                    }
                }
            }

            if (members != null || staticMembers != null) {
                parseMembers(comments, staticMembers, members, nodeList, false,
                        stats);
            }

            return;
        }

        ObjectLiteral objectLiteral = (ObjectLiteral) parameters.get(1);

        RefName staticMembers = null;
        RefName members = null;

        NodeList values = objectLiteral.getValues();
        for (int i = 0; i < values.size(); i++) {
            Value value = (Value) values.get(i);

            String valueName = value.getName().getName();

            if ("statics".equals(valueName)) {
                staticMembers = (RefName) value.getRight();
                continue;
            }

            if ("members".equals(valueName)) {
                members = (RefName) value.getRight();
                continue;
            }

            if ("extend".equals(valueName)) {
                String parentClassName = ((RefName) value.getRight()).getName();

                IJsClass parentClass = stats.getJsClass(parentClassName);
                if (parentClass == null) {
                    stats.errorLog.error("Can not get parent class '"
                            + parentClassName + "' " + value.getRight(), null,
                            this);

                    continue;
                }

                if (parentClass.equals(getParent().getJsClass()) == false) {
                    stats.errorLog
                            .error("Different declaration for parent className '"
                                    + parentClassName + "' " + value.getRight(),
                                    null, this);
                }
                continue;
            }

            if ("aspects".equals(valueName)) {
                NodeList aspects = ((ArrayLiteral) value.getRight())
                        .getExpressions();

                int cntAspects = 0;
                for (int j = 0; j < aspects.size(); j++) {
                    String aspectClassName = ((RefName) aspects.get(j))
                            .getName();

                    if (isSystemAspect(aspectClassName)) {
                        cntAspects++;
                        continue;
                    }

                    IJsType aspectClass = JsType.parse(stats, aspectClassName,
                            null, false);
                    if (containsAspectIgnoreTemplate(aspectClass) == false) {
                        stats.errorLog.error(
                                "Different declaration for aspect '"
                                        + aspectClassName + "' "
                                        + aspects.get(j), null, this);
                    }
                    cntAspects++;
                }

                if (cntAspects != listAspects().length) {
                    stats.errorLog.error("Different count of aspects "
                            + className, null, this);
                }
                continue;
            }

        }

        if (members != null || staticMembers != null) {
            parseMembers(comments, staticMembers, members, nodeList, false,
                    stats);
        }
    }

    private boolean isSystemAspect(String aspectClassName) {
        if ("HTMLElement".equals(aspectClassName)) {
            return true;
        }
        return false;
    }

    private void parseAspect(NodeList nodeList, NewExpression newExpression,
            JsComment comments[], JsStats stats) {

        this.newExpression = newExpression;

        NodeList parameters = newExpression.getParameters();

        String aspectName = ((StringLiteral) parameters.get(0)).getString();
        if (getName().equals(aspectName) == false) {
            stats.errorLog.error("Different aspect name ! " + newExpression,
                    null, this);
        }

        if (parameters.size() < 2) {
            return;
        }

        if ((parameters.get(1) instanceof ObjectLiteral) == false) {
            // Premier LookId

            RefName staticMembers = null;
            RefName members = null;

            if (parameters.size() > 1) {
                if (parameters.get(1) instanceof RefName) {
                    staticMembers = (RefName) parameters.get(1);
                }

                if (parameters.size() > 2) {
                    if (parameters.get(2) instanceof RefName) {
                        members = (RefName) parameters.get(2);
                    }

                    int cntAspect = 0;
                    for (int i = 3; parameters.size() > i; i++) {
                        String aspectClassName = ((RefName) parameters.get(i))
                                .getName();

                        if (isSystemAspect(aspectClassName)) {
                            cntAspect++; // C'est bon !
                            continue;
                        }

                        IJsType aspectClass = JsType.parse(stats,
                                aspectClassName, null, false);
                        if (containsAspect(aspectClass) == false) {
                            stats.errorLog.error(
                                    "Unknown declaration of aspect '"
                                            + aspectClassName + "' "
                                            + parameters.get(i), null, this);
                        }

                        cntAspect++;
                    }

                    if (cntAspect != listAspects().length) {
                        stats.errorLog.error("Different count of aspects "
                                + aspectName, null, this);
                    }
                }
            }

            if (members != null || staticMembers != null) {
                parseMembers(comments, staticMembers, members, nodeList, true,
                        stats);
            }

            return;
        }

        ObjectLiteral objectLiteral = (ObjectLiteral) parameters.get(1);

        RefName staticMembers = null;
        RefName members = null;

        NodeList values = objectLiteral.getValues();
        for (int i = 0; i < values.size(); i++) {
            Value value = (Value) values.get(i);

            String valueName = value.getName().getName();

            if ("statics".equals(valueName)) {
                staticMembers = (RefName) value.getRight();
                continue;
            }

            if ("members".equals(valueName)) {
                members = (RefName) value.getRight();
                continue;
            }

            if ("extend".equals(valueName)) {
                if ((value.getRight() instanceof ArrayLiteral) == false) {
                    throw new IllegalArgumentException(
                            "Invalid extend declaration for aspect '"
                                    + getName() + "'.");
                }
                NodeList aspects = ((ArrayLiteral) value.getRight())
                        .getExpressions();

                int cntAspects = 0;

                for (int j = 0; j < aspects.size(); j++) {
                    String aspectClassName = ((RefName) aspects.get(j))
                            .getName();

                    if (isSystemAspect(aspectClassName)) {
                        continue;
                    }

                    IJsType aspectClass = JsType.parse(stats, aspectClassName,
                            this, false);
                    if (containsAspect(aspectClass) == false) {
                        stats.errorLog.error("Unknown declaration for aspect '"
                                + aspectClassName + "' " + aspects.get(j),
                                null, this);
                    }
                    cntAspects++;
                }

                if (cntAspects != listAspects().length) {
                    stats.errorLog.error("Different count of aspects "
                            + aspectName, null, this);
                }
                continue;
            }

        }

        if (members != null || staticMembers != null) {
            parseMembers(comments, staticMembers, members, nodeList, true,
                    stats);
        }

    }

    private void parseMembers(JsComment[] comments,
            final RefName staticMembers, final RefName members,
            NodeList nodeList, boolean isAspect, JsStats stats) {

        if (staticMembers != null) {
            final DefName defName[] = new DefName[1];

            nodeList.accept(new ASTVisitor() {

                @Override
                public boolean visit(DefName node) {
                    if (node.getName().equals(staticMembers.getName())) {
                        defName[0] = node;
                        return false;
                    }
                    return true;
                }
            });

            ObjectLiteral objectLiteral = (ObjectLiteral) ((Value) defName[0]
                    .getParent()).getValue();

            staticMembersListNodes = objectLiteral;

            parseMembers(comments, objectLiteral.getValues(), true, isAspect,
                    stats);
        }

        if (members != null) {
            final DefName defName[] = new DefName[1];

            nodeList.accept(new ASTVisitor() {

                @Override
                public boolean visit(DefName node) {
                    if (node.getName().equals(members.getName())) {
                        defName[0] = node;
                        return false;
                    }
                    return true;
                }
            });

            ObjectLiteral objectLiteral = (ObjectLiteral) ((Value) defName[0]
                    .getParent()).getValue();

            membersListNodes = objectLiteral;

            parseMembers(comments, objectLiteral.getValues(), false, isAspect,
                    stats);
        }
    }

    private void parseMembers(JsComment comments[], NodeList members,
            boolean isStatic, boolean isAspect, JsStats stats) {

        for (int i = 0; i < members.size(); i++) {
            Value member = (Value) members.get(i);

            String memberName = member.getName().getName();
            Expression memberValue = member.getValue();

            JsComment jsComment = searchComment(stats, comments, member);

            int defaultModifier = 0; // Package
            if (memberName.equals("f_finalize")) {
                defaultModifier = Modifier.PROTECTED;

            } else if (memberName.equals(getName())) {
                defaultModifier = Modifier.PUBLIC;
            }

            if (memberValue instanceof FunctionDeclaration) {
                parseMember(member, memberName, memberValue, jsComment,
                        isStatic, false, false, JsModifier.METHOD_TYPE, stats,
                        defaultModifier, 0);
                continue;
            }

            if (memberValue instanceof FieldAccess) {
                // Abstract ?
                FieldAccess fa = (FieldAccess) memberValue;

                if (fa.getObject() instanceof RefName) {
                    String objName = ((RefName) fa.getObject()).getName();

                    if (objName.equals("f_class")) {
                        if (fa.getProperty().getName().equals("ABSTRACT")) {
                            parseMember(member, memberName, null, jsComment,
                                    isStatic, true, false,
                                    JsModifier.METHOD_TYPE, stats,
                                    defaultModifier, 0);
                            continue;
                        }
                        if (fa.getProperty().getName()
                                .equals("OPTIONAL_ABSTRACT")) {
                            parseMember(member, memberName, null, jsComment,
                                    isStatic, true, true,
                                    JsModifier.METHOD_TYPE, stats,
                                    defaultModifier, 0);
                            continue;
                        }
                    } else if (stats.containsClass(objName)) {
                        // Lien directe de méthode !
                        parseMember(member, memberName, null, jsComment,
                                isStatic, false, false, JsModifier.METHOD_TYPE,
                                stats, defaultModifier, 0);
                        continue;
                    }
                }
            }
            if (isAspect && (memberValue instanceof ObjectLiteral)) {
                ObjectLiteral objectLiteral = (ObjectLiteral) memberValue;

                NodeList decls = objectLiteral.getValues();

                if (isStatic) {
                    stats.errorLog.error("Can not decorate a static method ! "
                            + memberValue, null, this);
                    continue;
                }

                for (int j = 0; j < decls.size(); j++) {
                    Value decl = (Value) decls.get(j);
                    String declName = decl.getName().getName();

                    if ("before".equals(declName)) {
                        parseMember(member, memberName, decl.getRight(),
                                jsComment, false, false, false,
                                JsModifier.METHOD_TYPE, stats, defaultModifier,
                                JsModifier.BEFORE);
                        continue;
                    }

                    if ("after".equals(declName)) {
                        parseMember(member, memberName, decl.getRight(),
                                jsComment, false, false, false,
                                JsModifier.METHOD_TYPE, stats, defaultModifier,
                                JsModifier.AFTER);
                        continue;
                    }
                    if ("throwing".equals(declName)) {
                        parseMember(member, memberName, decl.getRight(),
                                jsComment, false, false, false,
                                JsModifier.METHOD_TYPE, stats, defaultModifier,
                                JsModifier.THROWING);
                        continue;
                    }

                    stats.errorLog.error("Unknown aspect keyword '" + declName
                            + "' for '" + memberValue + "'", null, this);
                }

                continue;
            }

            System.out
                    .println("Register "
                            + getName()
                            + "."
                            + memberName
                            + " => "
                            + ((jsComment != null && jsComment.getNode() != null) ? jsComment
                                    .getNode() : null));

            parseMember(member, memberName, memberValue, jsComment, isStatic,
                    false, false, JsModifier.FIELD_TYPE, stats, 0, 0);
        }
    }

    /*
     * Deja fait dans le JsComment private void searchReserveKeyword(IComment
     * comment, JsStats stats) { String rcm = comment.getText(); int idx0 = 0;
     * int idx = 0;
     * 
     * for (;;) { idx = rcm.indexOf("@reserve ", idx0); if (idx < 0) { break; }
     * 
     * String reserve = rcm.substring(idx + 9); int idx2 = reserve.indexOf('@');
     * if (idx2 > 0) { reserve = reserve.substring(0, idx2); }
     * 
     * reserve = reserve.trim();
     * 
     * for (StringTokenizer st = new StringTokenizer(reserve, " \n\t\r,;"); st
     * .hasMoreTokens();) { String n = st.nextToken();
     * stats.addCameliaReserverd(n); }
     * 
     * idx0 = idx + 9; } }
     */

    private void parseMember(ASTNode definition, String memberName,
            Expression memberValue, JsComment jsComment, boolean isStatic,
            boolean isAbstract, boolean isOptionalAbstract, int type,
            JsStats stats, int defaultModifier, int aspectPosition) {

        if (jsComment != null) {
            String typeDoc = jsComment.getType();
            if (typeDoc == null) {
                stats.errorLog.error("No type specified " + memberValue, null,
                        this);

            } else if ("field".equals(typeDoc)) {
                if (type != JsModifier.FIELD_TYPE) {
                    stats.errorLog.error("Invalid TYPE (not a field) for '"
                            + memberName + "' " + memberValue, null, this);
                }

            } else if ("method".equals(typeDoc)) {
                if (type != JsModifier.METHOD_TYPE) {
                    stats.errorLog.error("Invalid TYPE (not a method) for "
                            + memberName + " " + memberValue, null, this);
                }
            }
        }

        boolean _static = false;
        boolean _final = false;
        boolean _abstract = false;
        int v = defaultModifier;
        if (jsComment != null && jsComment.getValue() != null) {
            for (StringTokenizer st = new StringTokenizer(jsComment.getValue()); st
                    .hasMoreTokens();) {
                String tok = st.nextToken();

                if ("public".equalsIgnoreCase(tok)) {
                    v = Modifier.PUBLIC;
                    continue;
                }

                if ("protected".equalsIgnoreCase(tok)) {
                    v = Modifier.PROTECTED;
                    continue;
                }

                if ("private".equalsIgnoreCase(tok)) {
                    v = Modifier.PRIVATE;
                    continue;
                }

                if ("hidden".equalsIgnoreCase(tok)) {
                    v = 0;
                    continue;
                }

                if ("static".equalsIgnoreCase(tok)) {
                    _static = true;
                    continue;
                }

                if ("final".equalsIgnoreCase(tok)) {
                    _final = true;
                    continue;
                }

                if ("abstract".equalsIgnoreCase(tok)) {
                    _abstract = true;
                    continue;
                }
            }

            if (_static != isStatic) {
                stats.errorLog.error("Different static definition name="
                        + memberName + " value=" + memberValue, null, this);
            }

            if (_abstract != isAbstract) {
                stats.errorLog.error("Different abstract definition name="
                        + memberName + " value=" + memberValue, null, this);
            }
            if (_abstract && getModifier().isAbstract() == false) {
                stats.errorLog.error("Abstract method without abstract class "
                        + className + " member: name=" + memberName + " value="
                        + memberValue, null, this);
            }
        } else {
            _static = isStatic;

            /*
             * NON ! if (modifier != null) { _final = modifier.isFinal(); }
             */
        }

        if (_abstract) {
            v |= Modifier.ABSTRACT;
        }

        if (_final) {
            v |= Modifier.FINAL;
        }

        if (_static) {
            v |= Modifier.STATIC;
        }

        if (isOptionalAbstract) {
            v |= JsModifier.OPTIONAL_ABSTRACT;
        }

        v |= aspectPosition;

        JsModifier modifier = new JsModifier(getName(), memberName, definition,
                type, v);

        if (jsComment != null) {

            Map<String, String> metas = new HashMap<String, String>();
            String decs[] = jsComment.listDeclarations();
            for (int i = 0; i < decs.length;) {
                metas.put(decs[i++], decs[i++]);
            }

            if (metas.isEmpty() == false) {
                modifier.setMetas(metas);
            }
        }

        JsMember member = null;
        if (type == JsModifier.METHOD_TYPE) {
            member = new JsMethod(this, definition, memberValue, false,
                    modifier, jsComment);

        } else if (type == JsModifier.FIELD_TYPE) {
            member = new JsField(this, definition, memberValue, modifier,
                    jsComment);

        } else {
            throw new IllegalStateException("Type ???");
        }

        addMember(stats, memberName, member);

        Map<String, List<JsModifier>> modifiers = stats.modifiers;
        List<JsModifier> lm = modifiers.get(memberName);
        if (lm == null) {
            lm = new ArrayList<JsModifier>();
            modifiers.put(memberName, lm);
        }
        lm.add(modifier);

        if (memberValue != null) {
            memberValue.putProperty(JsOptimizer.MEMBER_PROPERTY, member);
        }
    }

    private IJsType searchReturnType(JsStats stats, String memberName,
            Expression memberValue, JsComment jsComment) {
        String docReturnType = null;
        boolean jsDoc = false;

        boolean hasReturn = false;

        if (jsComment != null) {
            docReturnType = jsComment.getReturnType();

            jsDoc = (docReturnType != null);

            if ("void".equals(docReturnType)) {
                docReturnType = null;
            }

            hasReturn = (docReturnType != null);
        }

        if (memberValue instanceof FunctionDeclaration) {
            NodeList statements = ((FunctionDeclaration) memberValue).getBody()
                    .getStatements();

            ReturnStatement returns[] = Visitors.visitReturns(statements, true);

            if (returns.length == 0) {
                if (docReturnType != null) {
                    stats.errorLog.error("Different return type for member '"
                            + memberName
                            + "': no return keyword and jsdoc specify '"
                            + docReturnType + "'.", memberValue, this);

                    hasReturn = false;
                }
            }

            for (int i = 0; i < returns.length; i++) {
                ReturnStatement rs = returns[i];

                Expression retExp = rs.getExpression();
                if (retExp == null) {
                    if (docReturnType != null) {
                        stats.errorLog
                                .error("Different return type for member '"
                                        + memberName
                                        + "' no value for return and jsdoc specify '"
                                        + docReturnType + "'.", memberValue,
                                        this);
                    }
                    continue;
                }
                hasReturn = true;
                if (docReturnType == null) {
                    if (jsDoc) {
                        stats.errorLog
                                .error("Different return type for member '"
                                        + memberName
                                        + "' a value for return and jsdoc specify 'void'.",
                                        memberValue, this);
                        continue;
                    }

                    continue;
                }

                verifyType(retExp, docReturnType);
                continue;
            }

            if (hasReturn) {
                ASTNode lastNode = (ASTNode) statements
                        .get(statements.size() - 1);

                if (verifyReturnEnd(lastNode) == false) {
                    stats.errorLog.error("No return keyword for member '"
                            + memberName + "'", lastNode, this);

                }
            }
        }

        if (docReturnType != null) {
            return JsType.parse(stats, docReturnType, this, false);
        }

        return null;
    }

    private boolean verifyReturnEnd(ASTNode lastNode) {
        if (lastNode instanceof ReturnStatement) {
            return true;
        }
        if (lastNode instanceof ThrowStatement) {
            return true;
        }

        if (lastNode instanceof IfStatement) {
            IfStatement s = (IfStatement) lastNode;

            if (s.getIfTrue() == null
                    || verifyReturnEnd(s.getIfTrue()) == false) {
                return false;
            }

            if (s.getIfFalse() == null
                    || verifyReturnEnd(s.getIfFalse()) == false) {
                return false;
            }

            return true;
        }

        if (lastNode instanceof TryStatement) {
            TryStatement s = (TryStatement) lastNode;

            if (s.getTryBlock() == null
                    || verifyReturnEnd(s.getTryBlock()) == false) {
                return false;
            }

            NodeList ccs = s.getCatchClauses();
            for (int j = 0; j < ccs.size(); j++) {
                CatchClause cc = (CatchClause) ccs.get(j);

                if (verifyReturnEnd(cc.getBody()) == false) {
                    return false;
                }
            }

            return true;
        }

        if (lastNode instanceof Block) {
            NodeList ns = ((Block) lastNode).getStatements();

            return verifyReturnEnd((ASTNode) ns.get(ns.size() - 1));
        }

        if (lastNode instanceof ForStatement) {
            ForStatement forStatement = (ForStatement) lastNode;

            if (forStatement.getCondition() != null) {
                return false;
            }

            Statement st = forStatement.getBody();

            if (st instanceof Block) {
                ReturnStatement rs[] = Visitors.visitReturns(
                        ((Block) st).getStatements(), true);

                return rs.length > 0;
            }

            return verifyReturnEnd(st);
        }

        return false;
    }

    private void verifyType(Expression retExp, String returnType) {
        // TODO Auto-generated method stub

    }

    public void parseMembers(JsStats stats, NodeList nodeList,
            JsComment[] comments) {

        // Parfois nous sommes en multi fichier pour une classe !
        this.statements.add(nodeList);

        JsComment classComment = searchClassComment(stats, getName(), comments);
        if (classComment == null) {
            if (this instanceof JsBundleClass) {
                // Un bundle
            } else {
                stats.errorLog.error("No class comment for '" + className
                        + "'.", null, this);
            }
        } else {
            updateJsClass(stats, classComment);
        }

        if (this instanceof JsBundleClass) {
            // Ou resourceBundle
            return;
        }

        NewExpression newExpressions[] = Visitors.visitNews(nodeList, true);
        for (int i = 0; i < newExpressions.length; i++) {
            NewExpression newExpression = newExpressions[i];

            String name = ((RefName) newExpression.getName()).getName();

            if (name.equals("f_class")) {
                parseClass(nodeList, newExpression, comments, stats);
                return;
            }

            if (name.equals("f_aspect")) {
                parseAspect(nodeList, newExpression, comments, stats);
                return;
            }
        }

        // Format f_core, ....

        if (stats.forceSemiPrototypeClassType.contains(getName())) {
            // if ("f_class".equals(getName())) {
            parseSemiPrototype(comments, nodeList, stats);
            return;
        }

        if (stats.forceDirectStaticMembersClassType.contains(getName())) {
            // if ("f_core".equals(getName())) {
            parseDirectStaticMembers(comments, nodeList, stats);
            return;
        }

        if (stats.forcePrototypeMembersClassType.contains(getName())) {
            // "f_classLoader".equals(getName()) ||
            // "f_multiWindowClassLoader".equals(getName()) ||
            // "Array".equals(getName()) || "f_aspect".equals(getName())) {
            parsePrototypeMembers(comments, nodeList, stats);

            return;
        }

        stats.errorLog.error("Unknown class format", null, this);
    }

    private void parsePrototypeMembers(JsComment[] comments, NodeList nodeList,
            JsStats stats) {
        // On recherche le constructeur

        FunctionDeclaration fds[] = Visitors.visitFunctions(nodeList, true);
        for (int i = 0; i < fds.length; i++) {
            FunctionDeclaration fd = fds[i];

            if ((fd.getName() instanceof DefName) == false) {
                continue;
            }

            if (((DefName) fd.getName()).getName().equals(getName()) == false) {
                continue;
            }

            // Constructeur

            JsComment jsComment = searchComment(stats, comments, fd);

            parseMember(fd, getName(), fd, jsComment, false, false, false,
                    JsModifier.METHOD_TYPE, stats, 0, 0);

        }

        Assignment assignements[] = Visitors.visitAssignments(nodeList, true);

        for (int i = 0; i < assignements.length; i++) {
            Assignment assignement = assignements[i];

            if ((assignement.getLeft() instanceof FieldAccess) == false) {
                continue;
            }
            FieldAccess fa = (FieldAccess) assignement.getLeft();

            String memberName = fa.getProperty().getName();

            if (fa.getObject() instanceof FieldAccess) {
                fa = (FieldAccess) fa.getObject();
            }

            if ((fa.getObject() instanceof RefName) == false) {
                continue;
            }

            if (((RefName) fa.getObject()).getName().equals(getName()) == false) {
                continue;
            }

            Expression value = assignement.getRight();

            if (memberName.equals("prototype")) {
                if (value instanceof ObjectLiteral) {
                    ObjectLiteral members = (ObjectLiteral) value;

                    parseMembers(comments, members.getValues(), false, false,
                            stats);
                }
                continue;
            }

            int type = (value instanceof FunctionDeclaration) ? JsModifier.METHOD_TYPE
                    : JsModifier.FIELD_TYPE;

            JsComment jsComment = searchComment(stats, comments, assignement);

            boolean isStatic = fa.getProperty().getName().equals("prototype") == false;

            parseMember(assignement, memberName, value, jsComment, isStatic,
                    false, false, type, stats, 0, 0);
        }
    }

    private JsComment searchComment(JsStats stats, JsComment comments[],
            ASTNode node) {
        JsComment jsComment = null;
        for (int j = 0; j < comments.length; j++) {
            if (node == comments[j].getNode()) {
                if (jsComment != null
                        && jsComment.getComment().getOffset() > comments[j]
                                .getComment().getOffset()) {
                    continue;

                }
                jsComment = comments[j];
            }
        }

        /*
         * if (jsComment != null) { searchReserveKeyword(jsComment.getComment(),
         * stats); }
         */

        return jsComment;

    }

    private void parseSemiPrototype(JsComment[] comments, NodeList nodeList,
            JsStats stats) {

        // Pas de constructeur !

        FieldAccess fas[] = Visitors.visitFieldAccess(nodeList, true);
        for (int i = 0; i < fas.length; i++) {
            FieldAccess fa = fas[i];

            if (fa.getProperty().getName().equals("_DeclarePrototypeClass") == false) {
                continue;
            }

            NodeList parameters = ((MethodInvocation) fa.getParent())
                    .getParameters();

            RefName staticMembersName = (RefName) parameters.get(2);
            RefName membersName = (RefName) parameters.get(3);

            parseMembers(comments, staticMembersName, membersName, nodeList,
                    false, stats);
            return;
        }

        stats.errorLog.error("Unknown f_class format", null, this);

    }

    private void parseDirectStaticMembers(JsComment comments[],
            NodeList nodeList, JsStats stats) {
        parseMembers(comments, new RefName(getName(), null), null, nodeList,
                false, stats);
    }

    public void print() {
        System.out.println("Class"
                + ((modifier != null) ? modifier.formatFlags() : "") + " '"
                + getName() + "'");

        for (IJsMember member : members) {
            System.out.println("  " + member);
        }
    }

    public JsBundleClass getBundle(String bundle) {
        return bundlesByName.get(bundle);
    }

    public void addBundle(JsBundleClass bundleClass) {
        bundlesByName.put(bundleClass.getBundeName(), bundleClass);
    }

    public boolean isAspect() {
        return isAspect;
    }

    public List<IJsClass> listReverseAspect() {
        return reverseAspect;
    }

    /*
     * public final ObjectLiteral getMembersDeclarationNode() { return
     * membersListNodes; }
     * 
     * public final ObjectLiteral getStaticMembersDeclarationNode() { return
     * staticMembersListNodes; }
     */
    public void removeMember(IJsMember member) {
        if (members.remove(member) == false) {
            System.err.println("*** Unknown method");
            return;
        }

        if (member.isStatic()) {
            if (staticMembersListNodes == null) {
                for (NodeList nodeList : statements) {
                    if (nodeList
                            .replaceBy(null, ((JsMember) member).definition)) {
                        return;
                    }
                }
                return;
            }

            staticMembersListNodes.getValues().replaceBy(null,
                    ((JsMember) member).definition);
            return;
        }

        membersListNodes.getValues().replaceBy(null,
                ((JsMember) member).definition);
    }

    public void newMember(IJsMember member) {
        if (members.add(member) == false) {
            System.err.println("*** Already known method");
            return;
        }

        // Value value=new Value(new DefName(member.modifier.name, null),
        // member.value, null);
        // member.definition=value;

        if (membersListNodes == null) {
            addMembersListNodes();
        }

        membersListNodes.getValues().add(0, ((JsMember) member).definition);

    }

    private void addMembersListNodes() {
        membersListNodes = new ObjectLiteral(new Value[0], null);

        NodeList parameters = newExpression.getParameters();
        if (parameters.get(1) instanceof ObjectLiteral) {
            ObjectLiteral obj = (ObjectLiteral) parameters.get(1);

            obj.getValues().add(
                    0,
                    new Value(new DefName("members", null), membersListNodes,
                            null));

            return;
        }

        parameters.remove(3);
        parameters.add(3, membersListNodes);
    }

    @Override
    public String toString() {
        return "[" + getName() + "]";
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;

        assert packageName != null && packageName.indexOf('<') < 0
                && packageName.indexOf('[') < 0 : "Invalid packageName "
                + packageName;
    }

    public String toString(IJsClass relativeClass, boolean printTemplate) {
        StringBuilder sb = new StringBuilder(128);

        if (getPackageName() != null
                && (relativeClass == null || relativeClass.getPackageName()
                        .equals(getPackageName()) == false)) {
            sb.append(getPackageName());
            sb.append('.');
        }

        sb.append(getName());

        if (parameters.length > 0 && printTemplate == true) {

            sb.append('<');

            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(parameters[i].toString(relativeClass));
            }
            sb.append('>');
        }

        return sb.toString();
    }

    public boolean isFinal() {
        return modifier.isFinal();
    }

    public boolean isProtected() {
        return modifier.isProtected();
    }

    public boolean isPublic() {
        return modifier.isPublic();
    }

}