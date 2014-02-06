/*
 * $Id: RCFacesJSCSGenerator.java,v 1.3 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vedana.camelia.generator.js.parser.IJsMethod.IJsParam;
import com.vedana.camelia.generator.js.parser.IJsMethod.IJsReturn;
import com.vedana.js.Operation;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.InfixExpression;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.TrueLiteral;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class RCFacesJSCSGenerator extends JsOptimizer {

    private static final String MAIN_PACKAGE_NAME = "org.rcfaces.js";

    public static void main(String args[]) throws Exception {
        new RCFacesJSCSGenerator().files(args);
    }

    @Override
    protected AliasDictionnary process(JsStats stats, List<JsFile> jsFiles,
            long total, List<JsFile> linkFiles, Target target,
            AliasDictionnary aliasDictionnary)
            throws UnsupportedEncodingException, FileNotFoundException {
        List<JsComment> commentsList = new ArrayList<JsComment>();

        declareSystemClasses(stats);

        for (JsFile jsFile : jsFiles) {
            parseMembers(jsFile.file, jsFile.getNodes(), jsFile.comments,
                    stats, jsFile);

            commentsList.addAll(Arrays.asList(jsFile.comments));
        }

        computeModifiers(stats);

        List<IJsClass> cs = new ArrayList<IJsClass>();

        cs.addAll(stats.listClasses());

        Collections.sort(cs, new Comparator<IJsClass>() {

            public int compare(IJsClass c0, IJsClass c1) {
                return computeDepth(c0) - computeDepth(c1);
            }

            private int computeDepth(IJsClass jsc) {
                if (jsc.getParent() == null) {
                    return 0;
                }

                return computeDepth(jsc.getParent().getJsClass()) + 1;
            }

        });

        String packageName = MAIN_PACKAGE_NAME;

        for (JsFile jsFile : jsFiles) {
            IJsClass clazz = jsFile.jsClass;
            if (clazz == null) {
                continue;
            }

            String pkg = clazz.getPackageName();
            /*
             * if (pkg != null && pkg.length() > 0 && "core".equals(pkg) ==
             * false) { pkg = "." + pkg; } else { pkg = ""; }
             */

            if (pkg == null) {
                continue;
            }

            File outputBase = new File(jsFile.output.outputFolder,
                    (pkg).replace('.', '/'));
            outputBase.mkdirs();

            File f = new File(outputBase, clazz.getName() + ".java");

            writeJSSS(f, packageName, clazz, stats);

            // Writer writer = new OutputStreamWriter(new
            // FileOutputStream("c:\\temp\\O2\\"+file.getName()+ "c"), "UTF-8");
        }

        return null;
    }

    private void declareSystemClasses(JsStats stats) {
        stats.addJsClass("boolean", new JsClass("boolean", false));
        stats.addJsClass("number", new JsClass("double", false));
        stats.addJsClass("int", new JsClass("int", false));
        stats.addJsClass("double", new JsClass("double", false));
        stats.addJsClass("String", new JsClass("java.lang.String", false));
        stats.addJsClass("Date", new JsClass("java.util.Date", false));
        stats.addJsClass("Object", new JsClass("java.lang.Object", false));
        stats.addJsClass("Error", new JsClass("java.lang.Exception", false));
        stats.addJsClass("HTMLElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLElement", true));
        stats.addJsClass("HTMLFormElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLFormElement", true));
        stats.addJsClass("HTMLInputElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLInputElement", true));
        stats.addJsClass("HTMLTableRowElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLTableRowElement", true));
        stats.addJsClass("HTMLTableColElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLTableColElement", true));
        stats.addJsClass("HTMLLIElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLLIElement", true));
        stats.addJsClass("HTMLOptionElement", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLOptionElement", true));
        stats.addJsClass("HTMLDocument", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.HTMLDocument", true));
        stats.addJsClass("Document", new JsClass("org.w3c.dom.Document", true));
        stats.addJsClass("Node", new JsClass("org.w3c.dom.Node", true));
        stats.addJsClass("Any", new JsClass("java.lang.Object", false));
        stats.addJsClass("Function", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.Function", false));
        stats.addJsClass("Window", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.Window", false));
        stats.addJsClass("Event", new JsClass(
                MAIN_PACKAGE_NAME + ".lang.Event", false));
        // stats.addJsClass("Error", new JsClass(MAIN_PACKAGE_NAME +
        // ".lang.Error", false));
        stats.addJsClass("RegExp", new JsClass(MAIN_PACKAGE_NAME
                + ".lang.RegExp", false));

    }

    protected void writeJSSS(File f, String packageName, IJsClass clazz,
            JsStats stats) throws UnsupportedEncodingException,
            FileNotFoundException {

        List<IJsMember> members = new ArrayList<IJsMember>();

        members.addAll(Arrays.asList(clazz.listMembers()));

        IJsType aspects[] = clazz.listAspects();
        for (int i = 0; i < aspects.length; i++) {
            addAspectMembers(members, aspects[i].getJsClass());
        }

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(f), "UTF-8"));

        IJsMethod constructor = null;
        for (Iterator<IJsMember> it = members.iterator(); it.hasNext();) {
            IJsMember member = it.next();

            if (member.getName().equals("f_finalize")) {
                it.remove();
                continue;
            }

            if (member instanceof IJsMethod) {
                if (((IJsMethod) member).resolvePublic(stats) == false) {
                    it.remove();
                    continue;
                }

                if (member.getName().equals(clazz.getName())) {
                    constructor = (IJsMethod) member;
                }

                if (clazz.isAspect() && member.isStatic() == false) {
                    continue;
                }
                continue;
            }

            if (member instanceof IJsField) {
                if (member.isPublic() == false) {
                    it.remove();
                    continue;
                }

                if (clazz.isAspect() && member.isStatic() == false) {
                    continue;
                }

                continue;
            }

            it.remove();
        }

        writer.println("/**");
        writer.println(" * @author RCFaces/JS-SS generator");
        writer.println(" * @version");
        writer.println(" */");

        writer.println();

        writer.println("package " + clazz.getPackageName() + ";");
        writer.println();

        if (clazz.isPublic() || clazz.isProtected() == false) {
            writer.print("public ");
        }

        boolean isInterface = clazz.isAspect();
        if (clazz.getName().equals("fa_namingContainer")) {
            isInterface = false;
        }

        if (isInterface) {
            writer.print("interface ");
        } else {
            writer.print("class ");
        }

        writer.print(clazz.getName());

        IJsType templates[] = clazz.listParameters();
        if (templates != null && templates.length > 0) {
            writer.print("<");
            for (int i = 0; i < templates.length; i++) {
                if (i > 0) {
                    writer.print(", ");
                }

                writer.print(templates[i].toString(clazz));
            }

            writer.print(">");
        }

        if (clazz.getParent() != null) {
            String pto = clazz.getParent().toString(clazz);
            if (pto.equals("java.lang.Object") == false) {
                writer.print(" extends " + pto);
            }
        }

        if (aspects.length > 0) {
            if (clazz.isAspect()) {
                writer.print(" extends");
            } else {
                writer.print(" implements");
            }

            boolean first = true;
            for (int i = 0; i < aspects.length; i++) {

                String as = aspects[i].toString(clazz);

                if (first == false) {
                    writer.print(",");
                } else {
                    first = false;
                }
                writer.print(" " + aspects[i].toString(clazz));
            }
        }
        writer.println(" {");

        if (constructor != null) {
            // writeConstructor(writer, clazz, constructor);
        }

        for (IJsMember member : members) {

            if (member == constructor && clazz.isAspect()) {
                continue;
            }

            if (member instanceof IJsMethod) {

                if (((IJsMethod) member).resolvePublic(stats) == false) {
                    continue;
                }

                if (member.isStatic()
                        && (member.getName().equals("toString") || member
                                .getName().equals("f_getName"))) {
                    continue;
                }

                writeMethod(writer, clazz, (IJsMethod) member, stats,
                        isInterface, 0);
                continue;
            }

            if (member instanceof IJsField) {

                if (member.isPublic() == false) {
                    continue;
                }

                writeField(writer, clazz, (IJsField) member, stats);
                continue;
            }
        }

        writer.println("}");

        writer.close();
    }

    protected void writeField(PrintWriter writer, IJsClass clazz,
            IJsField field, JsStats stats) {

        String literalValue = null;
        String type = null;

        Expression value = field.getExpression();
        if (value instanceof NumberLiteral) {

            double d = ((NumberLiteral) value).getNumber();

            if (Math.floor(d) == d) {
                if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE) {
                    type = "int";
                    literalValue = String.valueOf((int) d);

                } else {
                    type = "long";
                    literalValue = String.valueOf((long) d) + "l";
                }

            } else {
                type = "double";
                literalValue = String.valueOf(d);
            }

        } else if (value instanceof TrueLiteral) {
            type = "boolean";
            literalValue = "true";

        } else if (value instanceof FalseLiteral) {
            type = "boolean";
            literalValue = "false";

        } else if (value instanceof NullLiteral) {
            type = "Object";
            literalValue = "null";

        } else if (value instanceof StringLiteral) {
            type = "String";
            literalValue = encodeJavaScriptString(((StringLiteral) value)
                    .getString());
        } else if ((value instanceof PrefixExpression)
                && ((PrefixExpression) value).getOperation().equals(
                        Operation.NEGATIVE)
                && (((PrefixExpression) value).getExpression() instanceof NumberLiteral)) {

            double d = ((NumberLiteral) ((PrefixExpression) value)
                    .getExpression()).getNumber();

            if (Math.floor(d) == d) {
                if (d < Integer.MAX_VALUE && d > Integer.MIN_VALUE) {
                    type = "int";
                    literalValue = String.valueOf((int) d);

                } else {
                    type = "long";
                    literalValue = String.valueOf((long) d) + "l";
                }

            } else {
                type = "double";
                literalValue = String.valueOf(d);
            }

        } else if ((value instanceof InfixExpression)
                && ((InfixExpression) value).getOperation().equals(
                        Operation.LEFT_SHIFT)) {
            type = "int";

            literalValue = ((int) ((NumberLiteral) ((InfixExpression) value)
                    .getLeft()).getNumber())
                    + "<<"
                    + ((int) ((NumberLiteral) ((InfixExpression) value)
                            .getRight()).getNumber());
        } else {
            System.out.println("??? " + value);

            type = field.getType(stats).toString(clazz);
            literalValue = null;
        }

        writer.println();
        writer.print("\tpublic");
        if (field.isStatic()) {
            writer.print(" static");
        }
        if (field.isFinal()) {
            writer.print(" final");
        }

        writer.print(" " + type);

        writer.print(" " + field.getName() + "=" + literalValue + ";");
        writer.println();
    }

    private static void writeConstructor(PrintWriter writer, IJsClass clazz,
            JsMethod constructor) {

    }

    private void writeMethod(PrintWriter writer, IJsClass clazz,
            IJsMethod method, JsStats stats, boolean isInterface, int nbOptional) {

        List<String> params = null;
        Map<String, IJsType> typeOfParams = null;
        List<String> additionalComments = null;
        String returnDoc = null;
        IJsType returnType = null;
        String methodTitle = null;

        params = new ArrayList<String>();

        IJsParam jsParams[] = method.resolveParams(stats);

        if (jsParams == null) {
            return;
        }

        for (IJsParam p : jsParams) {
            params.add(p.getName());
        }

        List<String> optionalList = new ArrayList<String>();

        IJsReturn ret = method.resolveReturn(stats);
        if (ret != null) {
            returnType = ret.getJsType();
        }

        JsComment comment = method.getComment();
        if (comment != null) {
            methodTitle = comment.getTitle();

            String decls[] = comment.listDeclarations();
            for (int i = 0; i < decls.length;) {
                String nd = decls[i++];
                String d = decls[i++];

                if (nd.equals("see") || nd.equals("link") || nd.equals("since")) {
                    if (additionalComments == null) {
                        additionalComments = new ArrayList<String>();
                    }

                    additionalComments.add("@" + nd + " " + d);
                    continue;
                }

                if (nd.equals("return")) {
                    TypeTokenizer st = new TypeTokenizer(d, " ");

                    st.nextToken();
                    returnDoc = st.endOfText().trim();
                    continue;
                }
                if (nd.equals("javareturntype")) {
                    returnType = JsType.parse(stats, d.trim(), clazz, false);
                    continue;
                }
            }
        } else {
            if (isMethodInherited(clazz, method.getName(), true)) {
                return;
            }
        }

        Map<String, String> docOfParams = new HashMap<String, String>();

        for (IJsParam p : jsParams) {
            if (p.isHidden()) {
                if (params != null) {
                    params.remove(p.getName());
                }
                continue;
            }

            if (p.isOptional()) {
                optionalList.add(p.getName());
            }

            if (typeOfParams == null) {
                typeOfParams = new HashMap<String, IJsType>();
            }
            typeOfParams.put(p.getName(), p.getJsType());

            if (p.getComment() != null) {
                docOfParams.put(p.getName(), p.getComment().trim());
            }
        }

        writer.println("\n\t/**");
        if (methodTitle != null && methodTitle.trim().length() > 0) {
            writer.println("\t * " + methodTitle);
        }
        writer.println("\t *");

        if (params != null) {

            if (optionalList.isEmpty() == false && nbOptional > 0) {
                for (int i = 0; i < nbOptional; i++) {
                    String name = optionalList.remove(optionalList.size() - 1);

                    params.remove(name);
                }
            }

            for (Iterator<String> it = params.iterator(); it.hasNext();) {
                String param = it.next();

                writer.print("\t * @param " + param);

                String doc = null;
                if (docOfParams != null) {
                    doc = docOfParams.get(param);
                }

                if (doc != null) {
                    writer.print(" " + doc);
                }

                writer.println();
            }
        }

        if (returnDoc != null && returnDoc.length() > 0) {
            writer.println("\t * @return " + returnDoc);
        }

        if (additionalComments != null) {
            for (Iterator<String> it = additionalComments.iterator(); it
                    .hasNext();) {
                String c = it.next();

                writer.println("\t * " + c);
            }
        }

        writer.println("\t */");

        writer.print("\n\t");
        if (isInterface == false) {
            writer.print("public");
            if (method.isStatic()) {
                writer.print(" static");
            }
            if (method.isFinal()) {
                writer.print(" final");
            }
        }

        if (returnType != null) {
            if (method.getJsClass() != clazz) {
                returnType = translateType(returnType, clazz, method);
            }

            String s = returnType.toString(clazz);

            s = s.replaceAll("java\\.lang\\.", "");

            writer.print(" ");
            writer.print(s);

        } else if (method.getName().equals(clazz.getName()) == false) {

            writer.print(" void");
        }

        writer.print(" " + method.getName() + "(");

        if (params != null) {
            boolean first = true;
            for (Iterator<String> it = params.iterator(); it.hasNext();) {
                String p = it.next();

                if (first) {
                    first = false;
                } else {
                    writer.print(", ");
                }

                IJsType tp = null;
                if (typeOfParams != null) {
                    tp = typeOfParams.get(p);

                    if (tp != null) {
                        if (method.getJsClass() != clazz) {
                            tp = translateType(tp, clazz, method);
                        }
                    }
                }

                if (tp == null) {
                    writer.print("XXX");

                } else {
                    String s = tp.toString(clazz);

                    s = s.replaceAll("java\\.lang\\.", "");

                    writer.print(s + " " + p);
                }
            }
        }

        writer.print(")");
        if (isInterface) {
            writer.println(";");

        } else {

            writer.println(" {");

            if (method.getName().equals(clazz.getName())) {
                // Constructor ...

                IJsType typeParent = clazz.getParent();
                if (typeParent != null) {
                    IJsClass parentClass = typeParent.getJsClass();
                    if (parentClass != null) {
                        IJsMethod parentConstructor = (IJsMethod) parentClass
                                .getMember(parentClass.getName());

                        if (parentConstructor != null) {
                            IJsParam psc[] = parentConstructor
                                    .resolveParams(stats);

                            if (psc != null && psc.length > 0
                                    && psc[0].isOptional() == false) {
                                writer.print("\t\tsuper(");

                                boolean first = true;
                                for (int i = 0; i < psc.length; i++) {
                                    if (psc[i].isOptional()) {
                                        break;
                                    }

                                    if (first) {
                                        first = false;
                                    } else {
                                        writer.print(" ,");
                                    }

                                    String pt = psc[i].getJsType().getJsClass()
                                            .getName();

                                    if ("double".equals(pt) || "int".equals(pt)
                                            || "long".equals(pt)) {
                                        writer.print("0");

                                    } else if ("boolean".equals(pt)) {
                                        writer.print("false");

                                    } else {
                                        writer.print("null");
                                    }

                                }

                                writer.println(");");
                            }
                        }

                    }
                }

            } else if (returnType != null) {
                String returnTypeName = returnType.toString();

                if ("double".equals(returnTypeName)
                        || "int".equals(returnTypeName)
                        || "long".equals(returnTypeName)) {
                    writer.println("\t\treturn 0;");

                } else if ("boolean".equals(returnTypeName)) {
                    writer.println("\t\treturn false;");

                } else if ("void".equals(returnTypeName)) {
                    // Rien !

                } else {
                    writer.println("\t\treturn null;");
                }
            }

            writer.println("\t}");
        }

        writer.println();

        if (optionalList.isEmpty() == false) {
            writeMethod(writer, clazz, method, stats, isInterface,
                    nbOptional + 1);
        }
    }

    private IJsType translateType(IJsType type, IJsClass current,
            IJsMethod method) {

        return translateType(type, current, method.getJsClass());
    }

    public static IJsType translateType(IJsType type, IJsClass current,
            IJsClass target) {
        if (current.equals(target)) {
            return type;
        }

        IJsType t = current.getParent();
        if (t != null) {
            IJsClass tp = t.getJsClass();
            if (tp != null) {
                IJsType jt = translateType(type, tp, target);
                if (jt != null) {
                    IJsType tts[] = t.listTemplates();
                    IJsType ttps[] = tp.listParameters();

                    assert tts.length == ttps.length : "Probleme de parametrage ?";

                    for (int i = 0; i < ttps.length; i++) {
                        if (ttps[i].equals(type)) {
                            jt = tts[i];
                            break;
                        }
                    }

                    return jt;
                }
            }
        }

        for (IJsType aspect : current.listAspects()) {
            IJsClass tp = aspect.getJsClass();
            if (tp != null) {
                IJsType jt = translateType(type, tp, target);
                if (jt != null) {
                    IJsType tts[] = aspect.listTemplates();
                    IJsType ttps[] = tp.listParameters();

                    assert tts.length == ttps.length : "Probleme de parametrage ?";

                    for (int i = 0; i < ttps.length; i++) {
                        if (ttps[i].equals(type)) {
                            jt = tts[i];
                            break;
                        }
                    }

                    return jt;
                }
            }
        }
        return null;
    }

    private int countArray(String returnType) {
        int cnt = 0;
        for (int pos = 0;;) {
            pos = returnType.indexOf('[', pos);
            if (pos < 0) {
                break;
            }
            pos++;
            cnt++;
        }

        return cnt;
    }

    private static boolean isMethodInherited(IJsClass clazz, String name,
            boolean first) {

        if (first == false) {
            IJsMember member = clazz.getMember(name);

            if (member != null) {
                if (((IJsMethod) member).isAbstract()) {
                    return false;
                }
                return true;
            }
        }

        for (IJsType aspect : clazz.listAspects()) {
            if (isMethodInherited(aspect.getJsClass(), name, false)) {
                return true;
            }
        }

        if (clazz.getParent() != null) {
            return isMethodInherited(clazz.getParent().getJsClass(), name,
                    false);
        }

        return false;
    }

    private static void addAspectMembers(List<IJsMember> members,
            IJsClass aspect) {
        IJsMember ms[] = aspect.listMembers();

        next_member: for (IJsMember m : ms) {

            if ((m instanceof IJsMethod) == false) {
                continue;
            }

            if (m.isPublic() == false) {
                continue;
            }

            if (m.getName().equals(aspect.getName())) {
                continue;
            }

            if (((IJsMethod) m).isAfter() || ((IJsMethod) m).isBefore()) {
                continue;
            }

            if (m.getName().equals("f_finalize")) {
                continue;
            }

            for (IJsMember m2 : members) {
                if (m2.getName().equals(m.getName())) {

                    continue next_member;
                }
            }

            members.add(m);
        }

        for (IJsType aspect2 : aspect.listAspects()) {
            addAspectMembers(members, aspect2.getJsClass());
        }
    }

    @Override
    protected String computePackageName(String folderName) {

        int idx = folderName.lastIndexOf('/');
        if (idx > 0) {
            folderName = MAIN_PACKAGE_NAME + "."
                    + folderName.substring(0, idx).replace('/', '.');
        } else {
            folderName = MAIN_PACKAGE_NAME;
        }

        return folderName;
    }
}
