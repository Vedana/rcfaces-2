/*
 * $Id: JsStats.java,v 1.6 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.JsOptimizer.JsFile;
import com.vedana.camelia.generator.js.parser.JsOptimizer.Target;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.Value;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.6 $ $Date: 2013/11/14 14:08:48 $
 */
public class JsStats {

    private final Set<String> languageReserved = new HashSet<String>();
    {
        languageReserved.add("if");
        languageReserved.add("for");
        languageReserved.add("do");
        languageReserved.add("not");
        languageReserved.add("in");
        languageReserved.add("new");
        languageReserved.add("var");
        languageReserved.add("try");
        languageReserved.add("int");

        // Component !
        languageReserved.add("id");
        languageReserved.add("dir");
        languageReserved.add("x");
        languageReserved.add("y");
        languageReserved.add("src");
        languageReserved.add("alt");
        languageReserved.add("ltr");
        languageReserved.add("rtl");

        // RESERVED.add("_Camelia_frameSetAppend");
        languageReserved.add("$");

    }

    private final Set<String> cameliaReserved = new HashSet<String>();
    {
        cameliaReserved.add("f_finalize");
        cameliaReserved.add("f_documentComplete");
        cameliaReserved.add("Finalizer");
        cameliaReserved.add("Initializer");
        cameliaReserved.add("DocumentComplete");
    }

    private final Set<String> staticObjects = new HashSet<String>();
    {
        staticObjects.add("__static"); // Pour le classLoader Camelia
        // STATIC_OBJECTS.add("f_core"); // On ajoute les fichiers
        // STATIC_OBJECTS.add("f_vb");
    }

    private Map<String, IJsClass> classesRepository = new HashMap<String, IJsClass>();

    public Set<String> canTranslate = new HashSet<String>();

    // Map staticFinalConstants = new HashMap();

    private Map<String, JsPrivateStaticMember> privateMemberConstants = new HashMap<String, JsPrivateStaticMember>();

    public final Map<String, List<JsModifier>> modifiers = new HashMap<String, List<JsModifier>>();

    public final Map<String, NameCount> nameCount = new HashMap<String, NameCount>();

    public final Map<String, List<StringLiteral>> symbols = new HashMap<String, List<StringLiteral>>();

    public final Map<String, CharCount> charCount = new HashMap<String, CharCount>();

    public CharCount charsCount[];

    private final ParamCount paramNodes[] = new ParamCount[128];

    public String parametersAlias[];

    public Map<String, List<String>> privatesByClassNames = new HashMap<String, List<String>>();

    public final IErrorLog errorLog;

    public Map<String, Expression> inlineConstants = new HashMap<String, Expression>();

    public Set<String> forceSemiPrototypeClassType = new HashSet<String>();

    public Set<String> forceDirectStaticMembersClassType = new HashSet<String>();

    public Set<String> forcePrototypeMembersClassType = new HashSet<String>();

    private Map<JsFile, Target> targetedFile = new HashMap<JsFile, Target>();

    public JsStats(IErrorLog log) {
        errorLog = log;
    }

    public void addPrivateMember(Name ref, JsPrivateStaticMember cst,
            boolean isRef) {

        String cname = cst.getJsClass().getName();
        List<String> l = privatesByClassNames.get(cname);
        if (l == null) {
            l = new ArrayList<String>();
            privatesByClassNames.put(cname, l);
        }

        int idx = l.indexOf(ref.getName());
        if (idx < 0) {
            idx = l.size();
            l.add(ref.getName());
        }

        addName(JsOptimizer.PRIVATE_MEMBER_PREFIX + idx, ref, isRef);
    }

    public void removeChars(String name) {
        if (name == null || name.length() < 1) {
            return;
        }

        for (int i = 0; i < name.length(); i++) {
            String c = name.substring(i, i + 1);

            CharCount count = charCount.get(c);
            if (count == null) {
                continue;
            }

            count.count--;
        }
    }

    public void addChars(String name) {
        if (name == null || name.length() < 1) {
            return;
        }

        for (int i = 0; i < name.length(); i++) {
            String c = name.substring(i, i + 1);

            if (Character.isJavaIdentifierPart(c.charAt(0)) == false
                    || c.charAt(0) < 32 || c.charAt(0) > 127) {
                continue;
            }

            CharCount count = charCount.get(c);
            if (count == null) {
                count = new CharCount(c);
                charCount.put(c, count);
            }

            count.count++;
        }
    }

    public void addRef(RefName ref, String className, Set<String> classNames) {
        if (isLanguageReserved(ref.getName())) {
            return;
        }

        if (ref.getName().startsWith("_") == false
                && canTranslate.contains(ref.getName()) == false) {
            return;
        }

        addName(ref.getName(), ref, true);
    }

    public void addName(String name, ASTNode node, boolean ref) {

        NameCount nc = getNameCount(name);
        if (nc._static < 0) {
            nc._static = isStatic(name, node);
        }

        nc.nodes.add(node);

        if (ref) {
            nc.refs++;

        } else {
            nc.defs++;
        }
    }

    private int isStatic(String name, ASTNode node) {
        if ((node instanceof DefName) == false) {
            return -1;
        }

        DefName dn = (DefName) node;

        if ((dn.getParent() instanceof Value) == false) {
            return 0;
        }

        Value value = (Value) dn.getParent();

        if ((value.getParent() instanceof ObjectLiteral) == false) {
            return 0;
        }

        ObjectLiteral objectLiteral = (ObjectLiteral) value.getParent();

        if (value.getLeft() instanceof DefName) {
            DefName df = (DefName) value.getLeft();

            List<JsModifier> ms = modifiers.get(df.getName());
            if (ms != null) {
                boolean stic = false;
                for (JsModifier mm : ms) {
                    if (mm.isStatic() == false) {
                        stic = false;
                        break;
                    }
                    stic = true;
                }

                if (stic) {
                    return 1;
                }

                return 0;
            }
        }

        if (objectLiteral.getParent() instanceof Value) {
            value = (Value) objectLiteral.getParent();

            if (value.getLeft() instanceof DefName) {
                DefName df = (DefName) value.getLeft();

                String n = df.getName();

                if (staticObjects.contains(n)) {
                    return 1;
                }
            }
        }

        if (Character.isUpperCase(name.charAt(1))) {
            System.out.println("Uppercase ???? " + name + " (node=" + node
                    + ")");
            return 0;
        }

        return 0;
    }

    public void sortCharCount() {

        // On retire tous ceux qui seront transform�s !

        int removed = 0;
        for (Map.Entry<String, NameCount> me : nameCount.entrySet()) {
            String n = me.getKey();
            NameCount nc = me.getValue();

            if (n.charAt(0) == '_' && nc._static != 1) {
                removeChars(n);
                removed++;
                continue;
            }
        }

        charCount.get("_").count += removed;

        Collection<CharCount> c = charCount.values();

        Comparator<CharCount> comparator = new Comparator<CharCount>() {

            public int compare(CharCount c1, CharCount c2) {
                if (c1 == c2) {
                    return 0;
                }
                if (c1 == null) {
                    return 1;
                }
                if (c2 == null) {
                    return -1;
                }

                int l1 = c1.token.length();
                int l2 = c2.token.length();

                if (l2 > l1) {
                    return -1;
                }
                if (l1 > l2) {
                    return 1;
                }

                return c2.count - c1.count;
            }
        };

        charsCount = c.toArray(new CharCount[c.size()]);
        Arrays.sort(charsCount, comparator);
    }

    public boolean isLanguageReserved(String name) {
        if (languageReserved.contains(name.toLowerCase())
                || name.startsWith("_rcfaces")) {
            return true;
        }

        return false;
    }

    public boolean isCameliaReserved(String name) {
        return cameliaReserved.contains(name);
    }

    public void addCameliaReserverd(String reserved) {
        cameliaReserved.add(reserved);
    }

    public boolean isStaticObject(String name) {
        return staticObjects.contains(name);
    }

    public Collection<String> listStaticObjects() {
        return staticObjects;
    }

    public void addStaticObject(String name) {
        staticObjects.add(name);
    }

    public void addParamNode(int idx, Name name) {
        if (paramNodes[idx] == null) {
            paramNodes[idx] = new ParamCount();
        }

        paramNodes[idx].nodes.add(name);
    }

    public void addParamNodes(int idx, List<Name> names) {
        if (paramNodes[idx] == null) {
            paramNodes[idx] = new ParamCount();
        }

        paramNodes[idx].nodes.addAll(names);
    }

    public ParamCount[] listParamNodes() {
        return paramNodes;
    }

    public NameCount getNameCount(String key) {
        NameCount nc = nameCount.get(key);
        if (nc == null) {
            nc = new NameCount(key);
            nameCount.put(key, nc);
        }

        return nc;
    }

    public static final class CharCount {
        public final String token;

        public int count;

        public CharCount(String token) {
            this.token = token;
        }
    }

    public static final class NameCount {
        public int defs;

        public int refs;

        public final String name;

        public String alias;

        public Set<ASTNode> nodes = new HashSet<ASTNode>();

        public int _static = -1;

        public NameCount(String name) {
            this.name = name;
        }
    }

    public static final class ParamCount {
        public final Set<ASTNode> nodes = new HashSet<ASTNode>();
    }

    public IJsClass getJsClass(String className) {

        IJsClass jsClass = classesRepository.get(className); // .toLowerCase());

        if (jsClass == null) {
            // System.out.println("? Unknown className=" + className);
            return null;
        }

        return jsClass;
    }

    public boolean containsClass(String className) {
        return classesRepository.containsKey(className); // .toLowerCase());
    }

    public void addJsClass(String className, JsClass clazz) {
        classesRepository.put(className /* .toLowerCase() */, clazz);
    }

    public Collection<IJsClass> listClasses() {
        return classesRepository.values();
    }

    public JsPrivateStaticMember getPrivateStaticMember(String className,
            String methodName) {
        return privateMemberConstants.get(className + "." + methodName);
    }

    public void declarePrivateStaticMember(String className, String methodName,
            JsPrivateStaticMember privateStaticMember) {
        privateMemberConstants.put(className + "." + methodName,
                privateStaticMember);
    }

    public IJsMember searchFinalField(String className, String memberName) {
        IJsClass jsClass = getJsClass(className);
        if (jsClass == null) {
            return null;
        }

        IJsMember member = jsClass.getMember(memberName);
        if (member == null) {
            return null;
        }

        if ((member instanceof IJsField) == false || member.isFinal() == false) {
            return null;
        }

        return member;
    }

    public void registerAccess(IJsMember member, Expression fa) {
        ((JsMember) member).access.add(fa);

    }

    public int getAccessCount(IJsMember member) {
        Tools.verifyASTNodes(((JsMember) member).access);

        return ((JsMember) member).access.size();
    }

    public void registerTarget(JsFile jsFile, Target target) {
        targetedFile.put(jsFile, target);
    }

    public Target getTarget(JsFile file) {
        return targetedFile.get(file);
    }

    public void statParamNodes() {
        int ii = 0;
        for (ParamCount pc : listParamNodes()) {
            if (pc == null) {
                ii++;
                continue;
            }

            // for (int j = 0; j <= i; j++) {
            String key = JsOptimizer.PARAMETER_PREFIX + ii;

            NameCount nc = getNameCount(key);

            for (ASTNode nn : pc.nodes) {
                if (nn instanceof DefName) {
                    nc.defs++;
                    continue;
                }
                if (nn instanceof RefName) {
                    nc.refs++;
                    continue;
                }
            }

            nc.nodes.addAll(pc.nodes);
            // }

            ii++;
        }

    }
}
