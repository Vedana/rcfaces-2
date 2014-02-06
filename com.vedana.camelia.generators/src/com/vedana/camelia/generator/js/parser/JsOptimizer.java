/*
 * $Id: JsOptimizer.java,v 1.8 2014/02/05 16:03:40 jbmeslin Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.vedana.camelia.generator.js.parser.JsStats.CharCount;
import com.vedana.camelia.generator.js.parser.JsStats.NameCount;
import com.vedana.camelia.generator.js.parser.processor.ConcatVars;
import com.vedana.camelia.generator.js.parser.processor.CountAndReplaceStaticConstants;
import com.vedana.camelia.generator.js.parser.processor.GroupFalseLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupNullLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupNumberLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupRefLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupStringLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupThisLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupTrueLiteral;
import com.vedana.camelia.generator.js.parser.processor.GroupUndefinedLiteral;
import com.vedana.camelia.generator.js.parser.processor.IJsFileProcessor;
import com.vedana.camelia.generator.js.parser.processor.InlineAspectMethods;
import com.vedana.camelia.generator.js.parser.processor.InlineLevel3;
import com.vedana.camelia.generator.js.parser.processor.InstanceOfLevel3;
import com.vedana.camelia.generator.js.parser.processor.MergeDoubleAssigment;
import com.vedana.camelia.generator.js.parser.processor.MergeIfCascades;
import com.vedana.camelia.generator.js.parser.processor.MergeIfConditions;
import com.vedana.camelia.generator.js.parser.processor.MergeIfReturns;
import com.vedana.camelia.generator.js.parser.processor.MergeReferenceReturnAssignement;
import com.vedana.camelia.generator.js.parser.processor.MergeReturnValueAssignement;
import com.vedana.camelia.generator.js.parser.processor.MergeSwitchConditions;
import com.vedana.camelia.generator.js.parser.processor.MoveClassDeclaration;
import com.vedana.camelia.generator.js.parser.processor.RemoveAbstractFields;
import com.vedana.camelia.generator.js.parser.processor.RemoveDebugTests;
import com.vedana.camelia.generator.js.parser.processor.RemoveEmptyLiteralDeclaration;
import com.vedana.camelia.generator.js.parser.processor.RemoveLevel3;
import com.vedana.camelia.generator.js.parser.processor.RemoveLogs;
import com.vedana.camelia.generator.js.parser.processor.RemoveTargetTests;
import com.vedana.camelia.generator.js.parser.processor.RemoveUndefinedDeclarations;
import com.vedana.camelia.generator.js.parser.processor.RemoveUnusedConstants;
import com.vedana.camelia.generator.js.parser.processor.RemoveUnusedParameters;
import com.vedana.camelia.generator.js.parser.processor.RemoveUnusedVariables;
import com.vedana.camelia.generator.js.parser.processor.RemoveUselessExpressions;
import com.vedana.camelia.generator.js.parser.processor.RemoveVerifyProperties;
import com.vedana.camelia.generator.js.parser.processor.ReplaceFalses;
import com.vedana.camelia.generator.js.parser.processor.ReplaceNewObjectArray;
import com.vedana.camelia.generator.js.parser.processor.ReplaceTrues;
import com.vedana.camelia.generator.js.parser.processor.ResolveSuper;
import com.vedana.camelia.generator.js.parser.processor.SearchUnusedVariables;
import com.vedana.camelia.generator.js.parser.processor.SimplifyEmptyFinally;
import com.vedana.camelia.generator.js.parser.processor.SimplifyExpression;
import com.vedana.camelia.generator.js.parser.processor.SimplifyFinalizer;
import com.vedana.camelia.generator.js.parser.processor.SimplifyForBlocks;
import com.vedana.camelia.generator.js.parser.processor.SimplifyIfBlocks;
import com.vedana.camelia.generator.js.parser.processor.SimplifyLogName;
import com.vedana.camelia.generator.js.parser.processor.TransformAppendChild;
import com.vedana.camelia.generator.js.parser.processor.VerifyListeners;
import com.vedana.camelia.generator.js.parser.processor.VerifyVariables;
import com.vedana.js.Context;
import com.vedana.js.FormattedWriter;
import com.vedana.js.IComment;
import com.vedana.js.ITokenOutput;
import com.vedana.js.Operation;
import com.vedana.js.OutputVisitor;
import com.vedana.js.Parser;
import com.vedana.js.TokenStream;
import com.vedana.js.Visitors;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.Document;
import com.vedana.js.dom.Expression;
import com.vedana.js.dom.FalseLiteral;
import com.vedana.js.dom.FieldAccess;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.MethodInvocation;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.NullLiteral;
import com.vedana.js.dom.NumberLiteral;
import com.vedana.js.dom.ObjectLiteral;
import com.vedana.js.dom.Parameter;
import com.vedana.js.dom.PrefixExpression;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.StringLiteral;
import com.vedana.js.dom.TrueLiteral;
import com.vedana.js.dom.UndefinedLiteral;
import com.vedana.js.dom.Value;
import com.vedana.js.dom.VarExpression;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.8 $
 */
public class JsOptimizer {

    private static String ACCEPTED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_0123456789"; //

    private static boolean DEBUG_GENERATION = false;

    private static boolean PROFILER_GENERATION = false;

    private static boolean SCAN_DIRECTORY = false;

    // Pas bon pour l'UTF8
    // �����������������������������������������������������";

    private static boolean SIMPLIFY_NAMES = true;

    private static boolean SIMPLIFY_AST = true;

    // __ simplify ___

    private static boolean MERGE_IF_CASACADES = true;

    private static boolean SIMPLIFY_EXPRESSION = true;

    private static boolean SIMPLIFY_EMPTY_FINALLY = true;

    static boolean REMOVE_ABSTRACT = true;

    private static boolean REMOVE_LOGS = true;

    private static boolean SIMPLIFY_LOG_NAME = true;

    private static boolean REMOVE_VERIFY_PROPERTIES = true;

    private static boolean REPLACE_TRUE_FALSE = true;

    private static boolean SIMPLIFY_FINALIZER = true;

    private static boolean REPLACE_NEW_OBJECT_ARRAY = true;

    private static boolean SIMPLIFY_IF_BLOCKS = true;

    private static boolean REMOVE_UNUSED_PARAMETERS = true;

    private static boolean SIMPLIFY_FOR_BLOCKS = true;

    private static boolean SIMPLIFY_THIS = true;

    static boolean CONCAT_VAR_TO_BEGIN = true;

    private static boolean CONCAT_VAR_MID = true;

    static boolean GROUP_ASSIGNMENT_LITERALS = true;

    private static boolean MERGE_IF_CONDITIONS = true;

    static boolean GROUP_CLASS_ACCESS = true;

    static boolean GROUP_LITERALS = true;

    private static boolean REPLACE_CONSTANTS = true;

    private static boolean SIMPLIFY_USED_CONSTANTS = true;

    private static boolean MOVE_MEMBERS_STATICS = true;

    private static boolean REMOVE_UNDEFINED_VALUES = true;

    private static boolean SEARCH_USELESS_EXPRESSION = true;

    private static boolean VERIFY_VARIABLES = false;

    private static boolean MERGE_RETURN_ASSIGNMENTS = true;

    private static boolean MERGE_DOUBLE_ASSIGNMENTS = true;

    public enum Target {
        fx, ie, chrome, safara, ios
    }

    // Level 2

    static boolean MERGE_VARIABLES = false;

    private static boolean RESOLVE_SUPER = false;

    private static boolean INLINE_ASPECTS = false;

    // Level 3 (multi window)

    public static boolean MULTI_WINDOW = false;

    public static boolean GROUP_CLASS_WINDOW = true;

    //

    static final String PARAMETER_PREFIX = "#param";

    static final String PRIVATE_MEMBER_PREFIX = "#pmember";

    static final String LITERAL_PREFIX = "#literal:";

    static final String STRING_PREFIX = "#string";

    static final String FIELD_ACCESSOR_PREFIX = "#accessor:";

    static final String METHOD_ACCESSOR_PREFIX = "#method:";

    static final Set<String> NATIVE_GROUP_CLASSES = new HashSet<String>();

    static final Set<String> GROUP_CLASSES = new HashSet<String>();
    static {
        NATIVE_GROUP_CLASSES.add("arguments");
        // GROUP_CLASSES.add("document");
        NATIVE_GROUP_CLASSES.add("window");
        NATIVE_GROUP_CLASSES.add("Math");
        NATIVE_GROUP_CLASSES.add("parseInt");
        NATIVE_GROUP_CLASSES.add("parseFloat");

        GROUP_CLASSES.addAll(NATIVE_GROUP_CLASSES);
    }

    static final Set<String> coreClasses = new HashSet<String>();

    public static final String FINAL_VALUE_PROPERTY = "value.final";

    public static final String MEMBER_PROPERTY = "member";

    static final String DONT_INLINE_META = "dontInline";

    static final String INDIRECT_CLASS_PROPERTY = "indirectClass";

    static final String WINDOW_INDEPENDANT_PROPERTY = "windowIndependant";

    private static boolean LOG_NAMES = false;

    private static boolean VERIFY_JS_ONLISTENERS = true;

    static int INDEX_INCLUSIONS = 0;

    private static String EXCLUDED_PREFIXES = "vfv-,f_vx,fa_test";

    private static String COMPILATION_LABEL;

    private static String COMPILATION_VERSION;

    private static String COMPILED_EXTENSION = "jsc";

    public static String MEMBERS_ACCESS_FIELDNAME = "_members";

    private static String FORCE_SEMI_PROTOTYPE_CLASS_TYPE = "f_class";

    private static String FORCE_DIRECT_STATIC_MEMBERS_CLASS_TYPE = "f_core";

    private static String FORCE_PROTOTYPE_MEMBERS_CLASS_TYPE = "f_classLoader,f_multiWindowClassLoader,Array,f_aspect";

    public static void main(String args[]) throws Exception {
        // expression();
        new JsOptimizer().files(args);
    }

    public void files(String args[]) throws Exception {

        Map<String, Output> outputs = new LinkedHashMap<String, Output>();
        List<File> links = new ArrayList<File>();

        Set<Target> targets = new HashSet<JsOptimizer.Target>();

        for (int i = 0; i < args.length;) {
            String s = args[i++];

            if (s.startsWith("-source:")) {
                String name = s.substring(s.indexOf(':') + 1);
                Output output = outputs.get(name);
                if (output == null) {
                    output = new Output(name);

                    outputs.put(name, output);
                }

                output.inputFolder = new File(args[i++]);
                continue;
            }

            if (s.startsWith("-dest:")) {
                String name = s.substring(s.indexOf(':') + 1);
                Output output = outputs.get(name);
                if (output == null) {
                    output = new Output(name);

                    outputs.put(name, output);
                }

                output.outputFolder = new File(args[i++]);
                continue;
            }

            if (s.equals("-label")) {
                COMPILATION_LABEL = args[i++];
                continue;
            }

            if (s.equals("-version")) {
                COMPILATION_VERSION = args[i++];
                continue;
            }

            if (s.equals("-extension")) {
                COMPILED_EXTENSION = args[i++];
                continue;
            }

            if (s.equals("-link")) {
                File f = new File(args[i++]);
                if (f.exists() == false) {
                    System.err.println("Unknown link directory (" + f + ")");
                    continue;
                }

                if (f.isDirectory() == false) {
                    System.err.println("Invalid link directory (" + f + ")");
                    continue;
                }

                links.add(f);
                continue;
            }

            if (s.equals("-target")) {
                targets.add(Target.valueOf(args[i++]));
                continue;
            }

            if (s.startsWith("+") || s.startsWith("-")) {
                boolean enable = s.startsWith("+");

                String param = null;
                int idx = s.indexOf("=");
                if (idx >= 0) {
                    param = s.substring(idx + 1);
                    s = s.substring(0, idx);
                }

                StringBuffer sb = new StringBuffer();

                int pos = 1;
                int last = pos;
                for (; pos < s.length(); pos++) {
                    if (pos < 2
                            || Character.isUpperCase(s.charAt(pos)) == false) {
                        continue;
                    }

                    String token = s.substring(last, pos).toUpperCase();
                    if (sb.length() > 0) {
                        sb.append('_');
                    }

                    sb.append(token);
                    last = pos;
                }

                if (last < pos) {
                    String token = s.substring(last, pos).toUpperCase();
                    if (sb.length() > 0) {
                        sb.append('_');
                    }

                    sb.append(token);
                }

                String fieldName = sb.toString();

                Field field = JsOptimizer.class.getDeclaredField(fieldName);

                if (field.getType().equals(Boolean.TYPE)) {
                    field.set(null, Boolean.valueOf(enable));

                } else if (field.getType().equals(String.class)) {
                    field.set(null, param);
                }

                continue;
            }
        }

        if (SIMPLIFY_NAMES == false) {
            GROUP_ASSIGNMENT_LITERALS = false;
            GROUP_LITERALS = false;
            GROUP_CLASS_ACCESS = false;
        }

        Collection<Output> c = outputs.values();

        AliasDictionnary aliasDictionnary = files(
                c.toArray(new Output[outputs.size()]),
                links.toArray(new File[links.size()]), null, null);
        for (Target target : targets) {
            files(c.toArray(new Output[outputs.size()]),
                    links.toArray(new File[links.size()]), target,
                    aliasDictionnary);
        }

    }

    public AliasDictionnary files(Output outputs[], File[] links,
            Target target, AliasDictionnary aliasDictionnary) throws Exception {

        JsStats stats = new JsStats(new ErrorLog());

        fillSet(stats.forceDirectStaticMembersClassType,
                FORCE_DIRECT_STATIC_MEMBERS_CLASS_TYPE);

        fillSet(stats.forcePrototypeMembersClassType,
                FORCE_PROTOTYPE_MEMBERS_CLASS_TYPE);

        fillSet(stats.forceSemiPrototypeClassType,
                FORCE_SEMI_PROTOTYPE_CLASS_TYPE);

        Context context = new Context(Context.VERSION_1_4);

        long total = 0;

        List<JsFile> jsFiles = new ArrayList<JsFile>();

        for (Output output : outputs) {
            File base = output.inputFolder;

            File files[] = readRepository(new File(base, "repository.xml"),
                    stats);

            if (SCAN_DIRECTORY == false) {

                if (output.name.equals("html")) {
                    List<File> f = new ArrayList<File>(Arrays.asList(files));
                    f.add(new File(base, "util/f_locale_xx.js"));

                    files = f.toArray(new File[f.size()]);
                }

            } else {
                File fs[] = base.listFiles();

                List<File> f = new ArrayList<File>(Arrays.asList(files));
                for (int i = 0; i < fs.length; i++) {
                    if (f.contains(fs[i])) {
                        continue;
                    }

                    f.add(fs[i]);
                }

                files = f.toArray(new File[f.size()]);
            }

            // File files[] = listFiles(base);
            next_file: for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.getName().endsWith(".js") == false) {
                    continue;
                }

                if (file.exists() == false) {
                    continue;
                }

                StringTokenizer st = new StringTokenizer(EXCLUDED_PREFIXES, ",");
                for (; st.hasMoreTokens();) {
                    String ex = st.nextToken().trim();

                    if (file.getName().startsWith(ex)) {
                        System.out.println("Exclude : " + file.getName()
                                + "  (rule='" + ex + "')");
                        continue next_file;
                    }
                }

                /*
                 * if (file.getName().startsWith("vfa-readOnly") == false) {
                 * continue; }
                 * 
                 * 
                 * 
                 * File f2 = new File(file.getAbsolutePath() + "c"); if
                 * (f2.exists()) { f2.delete(); } continue; } }
                 */

                String fn = file.getName();
                int idx = fn.lastIndexOf('.');
                GROUP_CLASSES.add(fn.substring(0, idx));
                stats.addStaticObject(fn.substring(0, idx));

                total += file.length();

                Reader reader = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");

                System.out.println("Read #" + i + " : " + file.getName());

                TokenStream stream = new TokenStream(reader, file.getName(), 0,
                        context);

                Parser parser = new Parser();

                Document document = parser.parse(stream);

                JsFile jsFile = new JsFile(output, file, document);

                jsFiles.add(jsFile);

                IComment comments[] = stream.listComments();

                List<JsComment> cs = new ArrayList<JsComment>();
                for (int j = 0; j < comments.length; j++) {
                    JsComment cc = JsComment.createCommentContent(comments[j],
                            document.getStatements(), stats, fn);

                    if (cc == null) {
                        continue;
                    }

                    cs.add(cc);
                }

                jsFile.comments = cs.toArray(new JsComment[cs.size()]);

                reader.close();
            }
        }

        List<JsFile> jsLinkFiles = new ArrayList<JsFile>();

        int i = 0;

        for (File base : links) {

            File files[] = readRepository(new File(base, "repository.xml"),
                    stats);
            for (File file : files) {
                String fn = file.getName();

                Reader reader = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");

                System.out.println("Link #" + i + " : " + file.getName());

                TokenStream stream = new TokenStream(reader, file.getName(), 0,
                        context);

                Parser parser = new Parser();

                Document document = parser.parse(stream);

                JsFile jsFile = new JsFile(null, file, document);
                jsLinkFiles.add(jsFile);

                IComment comments[] = stream.listComments();

                List<JsComment> cs = new ArrayList<JsComment>();
                for (int j = 0; j < comments.length; j++) {
                    JsComment cc = JsComment.createCommentContent(comments[j],
                            document.getStatements(), stats, fn);

                    if (cc == null) {
                        continue;
                    }

                    cs.add(cc);
                }

                jsFile.comments = cs.toArray(new JsComment[cs.size()]);

                reader.close();
            }
        }

        AliasDictionnary dict = process(stats, jsFiles, total, jsLinkFiles,
                target, aliasDictionnary);

        return dict;
    }

    protected AliasDictionnary process(JsStats stats, List<JsFile> jsFiles,
            long total, List<JsFile> linkFiles, Target target,
            AliasDictionnary aliasDictionnary) throws Exception {

        /*
         * Pas bon pour le GZIP ! String
         * cs="abcdefjhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ��������������_"
         * ; for(int i=0;i <cs.length();i++) { char c=cs.charAt(i);
         * stats.charCount[c]=new CharCount(c); }
         */

        for (int i = 0; i < ACCEPTED_CHARS.length(); i++) {
            String c = ACCEPTED_CHARS.substring(i, i + 1);
            stats.charCount.put(c, new CharCount(c));
        }

        for (JsFile jsFile : jsFiles) {
            parseMembers(jsFile.file, jsFile.getNodes(), jsFile.comments,
                    stats, jsFile);
        }

        for (JsFile jsFile : linkFiles) {
            JsClass jsClass = parseMembers(jsFile.file, jsFile.getNodes(),
                    jsFile.comments, stats, jsFile);

            jsClass.setExternal(true);
        }

        if (SIMPLIFY_NAMES) {
            computeModifiers(stats);
        }

        if (REPLACE_CONSTANTS) {

            for (JsFile jsFile : jsFiles) {

                if (REMOVE_ABSTRACT) {
                    new RemoveAbstractFields().process(stats, jsFile);
                }

                new CountAndReplaceStaticConstants().process(stats, jsFile);

                // new InlineMethodBody().process(stats, jsFile);
            }

            // replaceStringConstants(stats);
        }

        Set<String> classNames = new HashSet<String>();

        if (INLINE_ASPECTS) {
            for (IJsClass jsClass : stats.listClasses()) {
                if (jsClass.isAspect() == false) {
                    continue;
                }

                new InlineAspectMethods().process(stats, jsClass);
            }
        }

        if (RESOLVE_SUPER) {
            for (IJsClass jsClass : stats.listClasses()) {
                new ResolveSuper().process(stats, jsClass);
            }
        }

        if (SIMPLIFY_USED_CONSTANTS) {
            for (JsFile jsFile : jsFiles) {
                new RemoveUnusedConstants().process(stats, jsFile.getJsClass());

                new RemoveEmptyLiteralDeclaration().process(stats, jsFile);
            }
        }

        if (VERIFY_JS_ONLISTENERS) {
            for (JsFile jsFile : jsFiles) {
                new VerifyListeners().process(stats, jsFile); // Pour le multi !
            }
        }

        for (JsFile jsFile : jsFiles) {

            if (SIMPLIFY_LOG_NAME) {
                new SimplifyLogName().process(stats, jsFile);
            }

            if (REMOVE_LOGS) { // Il faut faire ca avant le MULTI-window
                new RemoveLogs().process(stats, jsFile);

                new RemoveDebugTests().process(stats, jsFile);
            }

            if (target != null) {
                if (new RemoveTargetTests(target).process(stats, jsFile)) {
                    stats.registerTarget(jsFile, target);
                }
            }

            if (MULTI_WINDOW == false) {
                new RemoveLevel3().process(stats, jsFile);

            } else {
                new InlineLevel3().process(stats, jsFile);

                new InstanceOfLevel3().process(stats, jsFile);
            }

            if (REMOVE_VERIFY_PROPERTIES) { // Il faut faire ca avant le
                // MULTI-window
                new RemoveVerifyProperties().process(stats, jsFile);
            }

            new TransformAppendChild().process(stats, jsFile);
        }

        for (JsFile jsFile : jsFiles) {

            String currentClassName = computeClassName(jsFile.file);

            classNames.add(currentClassName);

            if (VERIFY_VARIABLES) {
                IJsFileProcessor processor = new VerifyVariables();

                processor.process(stats, jsFile);
            }
        }

        if (MULTI_WINDOW) {
            MultiWindowOptimizer.multiWindow(stats);
        }

        for (JsFile jsFile : jsFiles) {

            if (MOVE_MEMBERS_STATICS) {
                new MoveClassDeclaration().process(stats, jsFile);
            }
        }

        for (JsFile jsFile : jsFiles) {

            System.out.println("Processing file '" + jsFile.file.getName()
                    + "'");
            int cnt = 0;
            for (;;) {
                boolean modified = false;
                if (SIMPLIFY_AST) {
                    if (SIMPLIFY_FOR_BLOCKS) {
                        modified |= new SimplifyForBlocks().process(stats,
                                jsFile);
                    }

                    if (REPLACE_NEW_OBJECT_ARRAY) {
                        modified |= new ReplaceNewObjectArray().process(stats,
                                jsFile);
                    }

                    if (REMOVE_ABSTRACT && REPLACE_CONSTANTS == false) {
                        modified |= new RemoveAbstractFields().process(stats,
                                jsFile);
                    }

                    modified |= new SearchUnusedVariables().process(stats,
                            jsFile);

                    if (SIMPLIFY_IF_BLOCKS) {
                        modified |= new SimplifyIfBlocks().process(stats,
                                jsFile);
                    }

                    if (MERGE_IF_CONDITIONS) {
                        modified |= new MergeIfConditions().process(stats,
                                jsFile);
                        modified |= new MergeSwitchConditions().process(stats,
                                jsFile);
                    }

                    if (REMOVE_UNUSED_PARAMETERS) {
                        modified |= new RemoveUnusedParameters().process(stats,
                                jsFile);
                    }
                }

                if (SIMPLIFY_AST) {
                    if (JsOptimizer.CONCAT_VAR_MID) {
                        modified |= new ConcatVars().process(stats, jsFile);
                    }
                }

                if (SEARCH_USELESS_EXPRESSION) {
                    modified |= new RemoveUselessExpressions().process(stats,
                            jsFile);
                }

                if (REMOVE_UNDEFINED_VALUES) {
                    modified |= new RemoveUndefinedDeclarations().process(
                            stats, jsFile);
                }

                if (SIMPLIFY_AST) {
                    if (GROUP_ASSIGNMENT_LITERALS) {

                        modified |= new GroupNullLiteral().process(stats,
                                jsFile);
                        modified |= new GroupUndefinedLiteral().process(stats,
                                jsFile);

                        modified |= new GroupTrueLiteral().process(stats,
                                jsFile);
                        modified |= new GroupFalseLiteral().process(stats,
                                jsFile);

                        modified |= new GroupThisLiteral().process(stats,
                                jsFile);

                        modified |= new GroupNumberLiteral().process(stats,
                                jsFile);
                        modified |= new GroupStringLiteral().process(stats,
                                jsFile);

                        modified |= new GroupRefLiteral()
                                .process(stats, jsFile);
                    }

                    if (REPLACE_TRUE_FALSE) {
                        modified |= new SimplifyExpression().process(stats,
                                jsFile);

                        modified |= new SimplifyIfBlocks().process(stats,
                                jsFile);

                        modified |= new ReplaceTrues().process(stats, jsFile);

                        modified |= new ReplaceFalses().process(stats, jsFile);
                    }
                }

                if (SIMPLIFY_FINALIZER) {
                    modified |= new SimplifyFinalizer().process(stats,
                            jsFile.jsClass);
                }

                if (MERGE_RETURN_ASSIGNMENTS) {
                    modified |= new MergeReferenceReturnAssignement().process(
                            stats, jsFile);

                    modified |= new MergeReturnValueAssignement().process(
                            stats, jsFile);

                    modified |= new MergeIfReturns().process(stats, jsFile);
                }

                // modified |= new MergeParameterValue().process(stats, jsFile);
                // modified |= new RemoveUnusedAssignments().process(stats,
                // jsFile);

                if (MERGE_DOUBLE_ASSIGNMENTS) {
                    modified |= new MergeDoubleAssigment().process(stats,
                            jsFile);
                }

                if (MERGE_IF_CONDITIONS) {
                    modified |= new MergeIfConditions().process(stats, jsFile);
                }

                if (MERGE_IF_CASACADES) {
                    modified |= new MergeIfCascades().process(stats, jsFile);
                }

                if (SIMPLIFY_EXPRESSION) {
                    modified |= new SimplifyExpression().process(stats, jsFile);

                    modified |= new SimplifyIfBlocks().process(stats, jsFile);
                }

                if (SIMPLIFY_EMPTY_FINALLY) {
                    modified |= new SimplifyEmptyFinally().process(stats,
                            jsFile);
                }

                if (SIMPLIFY_EMPTY_FINALLY) {
                    modified |= new RemoveUnusedVariables().process(stats,
                            jsFile);
                    // removeUnusedVars(nodes);
                }

                if (modified) {

                    System.out.println("OPTIMIZED file: " + jsFile.file
                            + " ....");
                    continue;
                }

                if (cnt == 0) {
                    String currentClassName = computeClassName(jsFile.file);

                    countTokens(stats, jsFile.getNodes(), currentClassName,
                            classNames);
                } else {
                    break;
                }

                cnt++;
            }
        }

        if (SIMPLIFY_NAMES && aliasDictionnary == null) {
            aliasDictionnary = new AliasDictionnary();

            stats.sortCharCount();

            stats.statParamNodes();

            aliasDictionnary.sortNames(stats.nameCount);

            aliasDictionnary.setNames(stats, stats.charsCount);

            aliasDictionnary.statPrivates();
        }

        if (SIMPLIFY_NAMES) {
            aliasDictionnary.changeNames(stats);
        }

        if (LOG_NAMES) {
            NameCount[] _ncs = aliasDictionnary._namesCount;
            for (int i = 0; i < _ncs.length; i++) {
                NameCount n = _ncs[i];

                System.out.println("_Alias(" + i + "):" + n.name + "='"
                        + n.alias + "' [" + n.defs + "/" + n.refs + "]");
            }

            NameCount[] ncs = aliasDictionnary.namesCount;
            for (int i = 0; i < ncs.length; i++) {
                NameCount n = ncs[i];

                if (LOG_NAMES) {
                    System.out.println("Alias(" + i + "):" + n.name + "='"
                            + n.alias + "' [" + n.defs + "/" + n.refs + "/"
                            + n.nodes.size() + "]");
                }
            }

            for (Map.Entry<String, List<String>> entry : stats.privatesByClassNames
                    .entrySet()) {

                String className = entry.getKey();
                List<String> l = entry.getValue();

                int idx = 0;
                for (String field : l) {
                    System.out.println("PV:" + className + "." + field + "='"
                            + aliasDictionnary.privates[idx] + "'");

                    idx++;
                }
            }
        }

        long totalCompacted = 0;

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        GZIPOutputStream gz = new GZIPOutputStream(buf);

        File htmlOutputFile = null;

        for (JsFile jsFile : jsFiles) {
            Output output = jsFile.output;

            if (output.name.equals("html")) {
                htmlOutputFile = output.outputFolder;
            }

            String ap = jsFile.file.getAbsolutePath().toString();
            int dotAp = ap.lastIndexOf('.');

            String targetSuffix = "";
            Target localTarget = stats.getTarget(jsFile);
            if (localTarget != null) {
                if (target.equals(localTarget) == false) {
                    continue;
                }

                targetSuffix = "__A" + localTarget.name();

            } else if (target != null) {
                continue;
            }

            File outputFile = new File(output.outputFolder,
                    ap.substring(output.inputFolder.getAbsoluteFile()
                            .toString().length() + 1, dotAp)
                            + targetSuffix + "." + COMPILED_EXTENSION);

            outputFile.getParentFile().mkdirs();

            Writer writer = new OutputStreamWriter(new FileOutputStream(
                    outputFile), "UTF-8");
            // Writer writer = new OutputStreamWriter(new
            // FileOutputStream("c:\\temp\\O2\\"+file.getName()+ "c"), "UTF-8");

            if (jsFile.file.getName().equals("boot.js")) {
                if (COMPILATION_VERSION == null) {
                    COMPILATION_VERSION = new Date().toString();
                }

                writer.write("var RCFACES_JS_VERSION=\"" + COMPILATION_VERSION
                        + " ");

                if (COMPILATION_LABEL != null) {
                    writer.write(" " + COMPILATION_LABEL);
                }

                writer.write("\";\n");
            }

            StringWriter sw = new StringWriter((int) jsFile.file.length());

            FormattedWriter formattedWriter = new FormattedWriter(null, sw) {

                @Override
                protected void writeBlockCR() {
                    if (DEBUG_GENERATION) {
                        write('\n');
                    }
                }

                @Override
                protected void writeRcCR() {
                    if (DEBUG_GENERATION) {
                        write('\n');
                    }
                }

                @Override
                public ITokenOutput writeFunctionStartBlock() {
                    if (DEBUG_GENERATION) {
                        write('\n');
                    }
                    return this;
                }

                @Override
                public ITokenOutput writeEndFunction() {
                    if (DEBUG_GENERATION || PROFILER_GENERATION) {
                        write('\n');
                    }
                    return this;
                }

                @Override
                public ITokenOutput writeSemiColon(boolean statement) {
                    if (statement) {
                        write('\n');

                        return this;
                    }

                    return super.writeSemiColon(statement);
                }

                @Override
                public ITokenOutput writeLiteral(String literal) {
                    return super.writeLiteral(literal);
                }

            };

            OutputVisitor outputVisitor = new OutputVisitor(formattedWriter);
            outputVisitor.write(jsFile.getNodes(), true);

            sw.close();

            String formatted = sw.toString();

            totalCompacted += formatted.length();

            writer.write(formatted);
            gz.write(formatted.getBytes());

            writer.close();

            System.out.println("Write: " + outputFile + " ("
                    + outputFile.length() + " bytes)");
        }

        gz.close();
        int totalGzip = buf.size();

        htmlOutputFile.mkdirs();

        if (SIMPLIFY_NAMES && target == null) {
            Properties props = aliasDictionnary.listProperties(stats);

            props.put("javascript.version", COMPILATION_LABEL);

            Map<String, Expression> inlines = stats.inlineConstants;
            for (Map.Entry<String, Expression> entry : inlines.entrySet()) {

                String name = "$" + entry.getKey();
                Expression mv = entry.getValue();

                Object value = null;

                if (mv instanceof StringLiteral) {
                    String s = ((StringLiteral) mv).getString();

                    value = "\"" + s + "\"";

                } else if (mv instanceof NumberLiteral) {
                    double d = ((NumberLiteral) mv).getNumber();

                    if (d == (int) d) {
                        value = String.valueOf((int) d);

                    } else {
                        value = String.valueOf(d);
                    }

                } else if (mv instanceof TrueLiteral) {
                    value = "true";

                } else if (mv instanceof FalseLiteral) {
                    value = "false";

                } else if (mv instanceof NullLiteral) {
                    value = "null";

                } else if (mv instanceof UndefinedLiteral) {
                    value = "";

                } else if (mv instanceof PrefixExpression) {
                    PrefixExpression pe = (PrefixExpression) mv;

                    if (pe.getOperation() == Operation.NEGATIVE) {
                        double d = -((NumberLiteral) pe.getExpression())
                                .getNumber();

                        if (d == (int) d) {
                            value = String.valueOf((int) d);

                        } else {
                            value = String.valueOf(d);
                        }
                    }
                }

                if (value == null) {
                    System.out.println("Unknown type");
                    continue;
                }

                if (props.put(name, value) != null) {
                    System.out.println("Same type ??? " + name);
                }
            }

            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            props.store(writer, null);
            writer.close();

            byte buffer[] = writer.toByteArray();

            FileOutputStream fw = new FileOutputStream(
                    htmlOutputFile.getAbsolutePath() + "/symbols");
            fw.write(buffer);
            fw.close();

        } else if (SIMPLIFY_NAMES == false) {
            FileOutputStream fw = new FileOutputStream(
                    htmlOutputFile.getAbsolutePath() + "/symbols");
            fw.close();
        }

        System.out.flush();
        System.out.println("");
        System.out.println("------------------------------------------------");
        System.out.println("");
        System.out.println(" Original: " + total + " bytes");
        System.out.println("Optimized: " + totalCompacted + " bytes");
        System.out.println("  GZipped: " + totalGzip + " bytes");
        // System.out.println("   Zipped: " + totalZip + " bytes");

        return aliasDictionnary;
    }

    private static void fillSet(Set<String> set, String names) {

        StringTokenizer st = new StringTokenizer(names, ",");
        for (; st.hasMoreTokens();) {
            set.add(st.nextToken());
        }

    }

    protected String encodeJavaScriptString(String str) {

        char escape;

        if (str.indexOf('\"') < 0) {
            escape = '\"';

        } else if (str.indexOf('\'') < 0) {
            escape = '\'';

        } else {
            escape = '\"';
        }

        StringBuffer sb = new StringBuffer(str.length() * 2);

        sb.append(escape);

        char chs[] = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == '\n') {
                sb.append("\\n");
                continue;
            }
            if (c == '\r') {
                sb.append("\\r");
                continue;
            }
            if (c == escape) {
                sb.append('\\');
                sb.append(escape);
                continue;
            }
            if (c == '\t') {
                sb.append("\\t");
                continue;
            }
            if (c == '\\') {
                sb.append("\\\\");
                continue;
            }

            sb.append(c);
        }

        sb.append(escape);

        return sb.toString();
    }

    private File[] readRepository(final File repository, final JsStats stats)
            throws IOException, SAXException {

        final List<File> files = new ArrayList<File>();

        Digester digester = new Digester();

        digester.addRule("repository/module/file", new Rule() {
            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                String folderName = attributes.getValue("name");

                File f = new File(repository.getParentFile(), folderName);
                if (f.exists() == false) {
                    System.out.println("Unknown file '" + f + "'");
                }
                files.add(f);

                folderName = computePackageName(folderName);

                digester.push(folderName);
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                digester.pop();
            }
        });
        digester.addRule("repository/module/file/class", new Rule() {
            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                String className = attributes.getValue("name");

                String packageName = (String) digester.peek();

                if ("core".equals(packageName)) {
                    coreClasses.add(className);
                }

                boolean isAspect = className.startsWith("fa_");

                JsClass repositoryClass = new JsClass(className, isAspect);
                repositoryClass.setPackageName(packageName);

                stats.addJsClass(className, repositoryClass);

                super.digester.push(repositoryClass);
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                super.digester.pop();
            }
        });

        digester.addRule("repository/module/file/class/required-class",
                new Rule() {
                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        String className = attributes.getValue("name");

                        JsClass repositoryClass = (JsClass) super.digester
                                .peek();

                        repositoryClass.addDependency(className);
                    }
                });

        digester.parse(repository);

        return files.toArray(new File[files.size()]);
    }

    protected String computePackageName(String folderName) {

        int idx = folderName.indexOf('/');
        if (idx > 0) {
            folderName = folderName.substring(0, idx);
        } else {
            folderName = "core";
        }

        return folderName;
    }

    private static File[] listFiles(File base) {
        List<File> l = new ArrayList<File>();

        File fs[] = base.listFiles();

        for (int i = 0; i < fs.length; i++) {
            File f = fs[i];

            if (f.isDirectory()) {
                File ret[] = listFiles(f);

                l.addAll(Arrays.asList(ret));
                continue;
            }

            if (f.getName().endsWith("js") == false) {
                continue;
            }

            l.add(f);
        }

        return l.toArray(new File[l.size()]);
    }

    private static String computeClassName(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('/');
        if (idx >= 0) {
            name = name.substring(idx + 1);
        }

        idx = name.indexOf('.');
        if (idx >= 0) {
            name = name.substring(0, idx);
        }

        return name;
    }

    private static int computeNbUseClass(NodeList nodes, RefName ref,
            int _already[]) {
        RefName refs[] = Visitors.visitRefNames(nodes, false);

        int already = 0;
        Set<FunctionDeclaration> functionGroup = new HashSet<FunctionDeclaration>();
        for (RefName r : refs) {

            if (r.getName().equals(ref.getName()) == false) {
                continue;
            }

            FunctionDeclaration fd2 = Tools.getParentFunction(r);
            if (fd2 == null) {
                continue;
            }

            if (functionGroup.add(fd2) == false) {
                already++;
            }
        }

        _already[0] = already;
        return functionGroup.size();
    }

    private static void countTokens(final JsStats stats, NodeList nodes,
            String className, Set<String> classNames) throws Exception {

        nodes.accept(new ASTVisitor() {
            @Override
            public boolean preVisit(ASTNode node) {
                stats.addChars(node.listKeywordChars());

                return super.preVisit(node);
            }
        });

        final boolean resourceBundle[] = new boolean[1];

        nodes.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodInvocation invocation) {
                if (invocation.getObject() instanceof FieldAccess) {
                    FieldAccess fa = (FieldAccess) invocation.getObject();

                    if (invocation.getParent() != null) {
                        return super.visit(invocation);
                    }

                    if ((fa.getObject() instanceof RefName)
                            && "f_resourceBundle".equals(((RefName) fa
                                    .getObject()).getName())) {

                        if ("Define".equals(fa.getProperty().getName())) {
                            resourceBundle[0] = true;
                            return false;
                        }
                    }
                }

                return super.visit(invocation);
            }

        });

        DefName defs[] = Visitors.visitDefNames(nodes, true);
        for (int j = 0; j < defs.length; j++) {
            DefName def = defs[j];

            JsPrivateStaticMember cst = Tools.searchStaticDef(def, className,
                    stats);
            if (cst != null) {
                stats.addPrivateMember(def, cst, false);
                continue;
            }

            if ((def instanceof Parameter) == false
                    && def.getName().startsWith("_") == false
                    && stats.canTranslate.contains(def.getName()) == false) {
                stats.addChars(def.getName());
                continue;
            }

            if (def.getParent() instanceof Value) {
                Value value = (Value) def.getParent();

                ASTNode p2 = value.getParent();

                if (p2 instanceof ObjectLiteral) {
                    // ObjectLiteral ol = (ObjectLiteral) p2;

                    if (def.getName().equals(className)
                            && value.getLeft() == def) {
                        // C'est le constructeur !
                        continue;
                    }

                    if (resourceBundle[0]) {
                        continue;
                    }
                }
            }

            if (stats.isLanguageReserved(def.getName()) == false) {

                stats.addName(def.getName(), def, false);
            }
        }

        RefName refs[] = Visitors.visitRefNames(nodes, true);
        for (int j = 0; j < refs.length; j++) {
            RefName ref = refs[j];

            if (stats.isLanguageReserved(ref.getName())) {
                stats.addChars(ref.getName());
                continue;
            }

            JsPrivateStaticMember cst = Tools.searchStaticMember(ref,
                    className, stats, classNames);
            if (cst != null) {
                stats.addPrivateMember(ref, cst, true);
                continue;
            }

            if (ref.getName().startsWith("_")
                    || stats.canTranslate.contains(ref.getName())) {

                stats.addName(ref.getName(), ref, true);
                continue;
            }

            stats.addChars(ref.getName());
        }

        replace__SYMBOLS(nodes, stats);

        FunctionOptimizer.analyseFunctions(null, nodes, stats, null, className,
                classNames);
    }

    private static void replace__SYMBOLS(NodeList nodes, JsStats stats) {
        RefName names[] = Visitors.visitRefNames(nodes, false);
        for (int i = 0; i < names.length; i++) {
            RefName name = names[i];

            if (name.getName().equals("__SYMBOL") == false) {
                continue;
            }

            if (name.getParent() instanceof MethodInvocation) {
                MethodInvocation methodInvocation = (MethodInvocation) name
                        .getParent();

                NodeList parameters = methodInvocation.getParameters();

                StringLiteral sl = (StringLiteral) parameters.get(0);

                List<StringLiteral> l = stats.symbols.get(sl.getString());
                if (l == null) {
                    l = new ArrayList<StringLiteral>();
                    stats.symbols.put(sl.getString(), l);
                }

                methodInvocation.replaceBy(sl);

                l.add(sl);
                continue;
            }
        }

        DefName dnames[] = Visitors.visitDefNames(nodes, false);
        for (int i = 0; i < dnames.length; i++) {
            DefName name = dnames[i];

            if (name.getName().equals("__SYMBOL") == false) {
                continue;
            }

            if (name.getParent() instanceof Value) {

                System.out.println("Optimize: Remove __SYMBOLS declaration: "
                        + name);

                VarExpression ve = (VarExpression) name.getParent().getParent();

                name.getParent().replaceBy(null);

                if (ve.getValues().size() == 0) {
                    ve.replaceBy(null);
                }

                continue;
            }

            System.err.println("SYMBOLS ?");
        }
    }

    protected JsClass parseMembers(File file, NodeList nodeList,
            JsComment[] comments, JsStats stats, JsFile jsFile) {

        String className = null;
        String templates = null;

        JsComment classComment = JsClass.searchClassComment(stats,
                file.getName(), comments);
        if (classComment != null) {
            className = JsClass.extractClassName(classComment);

            if (className == null) {
                System.err.println("*** Error: " + file.getName()
                        + " no classname detected !");
            } else {
                int idx = className.indexOf('<');
                if (idx >= 0) {
                    int idxEnd = className.lastIndexOf('>');

                    templates = className.substring(idx + 1, idxEnd);

                    className = className.substring(0, idx);
                }
            }
        }

        String bundle = null;
        if (className == null) {
            String filename = file.getName();
            filename = filename.substring(0, filename.lastIndexOf('.'));

            className = computeClassName(file);
            int idx = className.indexOf('_', 3);
            if (idx > 0) {
                bundle = filename.substring(idx + 1);
                className = filename.substring(0, idx);
            }

        }

        JsClass jsClass;
        if (bundle == null) {
            if (stats.containsClass(className) == false) {
                jsClass = new JsClass(className, false);
                stats.addJsClass(className, jsClass);

            } else {
                jsClass = (JsClass) stats.getJsClass(className);
            }
        } else {
            jsClass = (JsClass) stats.getJsClass(className);

            JsBundleClass bundleClass = jsClass.getBundle(bundle);
            if (bundleClass == null) {
                bundleClass = new JsBundleClass(jsClass, bundle);
                jsClass.addBundle(bundleClass);
            }

            jsClass = bundleClass;
        }

        if (jsClass != null && templates != null) {
            List<IJsType> ts = new ArrayList<IJsType>();

            for (StringTokenizer st = new StringTokenizer(templates, " ,\t\n"); st
                    .hasMoreTokens();) {
                String tk = st.nextToken();

                ts.add(JsType.parse(stats, tk, null, true));
            }

            jsClass.setTemplates(ts.toArray(new IJsType[ts.size()]));
        }

        jsFile.jsClass = jsClass;

        // Recherche la décalaration de la classe !

        jsClass.parseMembers(stats, nodeList, comments);

        if (false) {
            jsClass.print();
        }

        return jsClass;
    }

    protected void computeModifiers(JsStats stats) {
        for (Map.Entry<String, List<JsModifier>> entry : stats.modifiers
                .entrySet()) {
            String name = entry.getKey();
            List<JsModifier> l = entry.getValue();

            int v = -1;
            for (JsModifier mm : l) {
                int mv = mm.getAccessibleModifier();
                if (v == mv) {
                    continue;
                }
                if (v < 0) {
                    v = mv;
                    continue;
                }

                if (v == Modifier.PUBLIC) {
                    break;
                }

                if (v == Modifier.PRIVATE) {
                    v = mv;
                    continue;
                }

                if (v == Modifier.PROTECTED) {
                    if (mv == Modifier.PUBLIC) {
                        v = mv;
                        continue;
                    }
                }

                if (v == 0) {
                    if (mv == Modifier.PUBLIC || mv == Modifier.PROTECTED) {
                        v = mv;
                        continue;
                    }
                }
            }

            boolean privateFramework = false;
            if (v != Modifier.PUBLIC) {
                if (stats.isCameliaReserved(name)) {
                    v = Modifier.PUBLIC;
                    continue;
                }

                if (stats.isStaticObject(name)) {
                    continue;
                }

                stats.canTranslate.add(name);
                privateFramework = true;
            }

            for (JsModifier mm : l) {
                ASTNode anode = mm.node;

                if ((mm.isPrivate() || mm.isHidden()) && mm.isStatic()) {

                    IJsClass cl = stats.getJsClass(mm.className);

                    if (anode instanceof Value) {
                        JsPrivateStaticMember constant = new JsPrivateStaticMember(
                                cl, anode, ((Value) anode).getRight(),
                                privateFramework, mm, null);

                        stats.declarePrivateStaticMember(mm.className, name,
                                constant);

                        // System.out.println("Put private members '" + key+
                        // "'.");

                    } else if (anode instanceof Assignment) {
                        JsPrivateStaticMember constant = new JsPrivateStaticMember(
                                cl, anode, ((Assignment) anode).getRight(),
                                privateFramework, mm, null);

                        stats.declarePrivateStaticMember(mm.className, name,
                                constant);

                        // System.out.println("Put private members '" + key+
                        // "'.");

                    } else if (anode instanceof FunctionDeclaration) {
                        JsPrivateStaticMember constant = new JsPrivateStaticMember(
                                cl, anode, (Expression) anode,
                                privateFramework, mm, null);

                        stats.declarePrivateStaticMember(mm.className, name,
                                constant);

                    } else {
                        System.err.println("Unknown member type !");
                    }

                }
            }
        }
    }

    private static class ErrorLog implements IErrorLog {

        public void error(String message, ASTNode node, String className) {
            System.err.println("*** Error:" + className + ":" + node + ": "
                    + message);
        }

        public void error(String message, ASTNode node, IJsClass clazz) {
            System.err.println("*** Error:" + clazz.getName() + ":" + node
                    + ": " + message);
        }
    }

    protected static class Output {
        public final String name;

        public File inputFolder;

        public File outputFolder;

        public Output(String name) {
            this.name = name;
        }
    }

    protected static class JsFile implements IJsFile {

        public JsComment[] comments;

        protected final Output output;

        protected final File file;

        protected final Document document;

        protected IJsClass jsClass;

        public JsFile(Output output, File file, Document document) {
            this.output = output;
            this.file = file;
            this.document = document;
        }

        public NodeList getNodes() {
            return document.getStatements();
        }

        public Document getDocument() {
            return document;
        }

        public IJsClass getJsClass() {
            return jsClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((file == null) ? 0 : file.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            JsFile other = (JsFile) obj;
            if (file == null) {
                if (other.file != null)
                    return false;
            } else if (!file.equals(other.file))
                return false;
            return true;
        }

    }
}
