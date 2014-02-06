/*
 * $Id: AliasDictionnary.java,v 1.1 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.JsStats.CharCount;
import com.vedana.camelia.generator.js.parser.JsStats.NameCount;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Name;
import com.vedana.js.dom.StringLiteral;

public class AliasDictionnary {

    public NameCount[] namesCount;

    public NameCount[] _namesCount;

    public String[] privates = new String[256];

    public void sortNames(Map<String, NameCount> nameCounts) {

        List<NameCount> l1 = new ArrayList<NameCount>();
        List<NameCount> l2 = new ArrayList<NameCount>();

        for (Map.Entry<String, NameCount> me : nameCounts.entrySet()) {
            String n = me.getKey();
            NameCount nc = me.getValue();

            if (n.charAt(0) == '_' && nc._static != 1) {
                l2.add(nc);
                continue;
            }

            l1.add(nc);
        }

        Comparator<NameCount> comparator = new Comparator<NameCount>() {

            public int compare(NameCount c1, NameCount c2) {

                if (c1 == c2) {
                    return 0;
                }
                if (c1 == null) {
                    return 1;
                }
                if (c2 == null) {
                    return -1;
                }

                return c2.nodes.size() - c1.nodes.size();
            }
        };

        namesCount = l1.toArray(new NameCount[l1.size()]);

        int cnt = 0;
        for (NameCount nc : namesCount) {
            cnt += Tools.verifyASTNodes(nc.nodes);
        }
        if (cnt > 0) {
            System.out.println("> Remove " + cnt + " invalid refs");
        }

        Arrays.sort(namesCount, comparator);

        _namesCount = l2.toArray(new NameCount[l2.size()]);
        Arrays.sort(_namesCount, comparator);
    }

    public void setNames(JsStats stats, CharCount[] charsCount) {
        NameCount ncs[] = namesCount;

        int oneChar = charsCount.length; // 53

        int idx = 0;
        for (int i = 0; i < oneChar && idx < ncs.length; i++) {
            CharCount cc = charsCount[i];
            if (cc == null) {
                continue;
            }

            String n = cc.token;
            if (Character.isJavaIdentifierStart(n.charAt(0)) == false
                    || n.charAt(0) == '_') {
                continue;
            }

            if (stats.isLanguageReserved(n)) {
                continue;
            }

            ncs[idx++].alias = verifyName(n);
        }

        for (int i = 0; idx < ncs.length; i++) {
            CharCount cc = charsCount[i];
            if (cc == null) {
                continue;
            }

            String n = cc.token;
            if (Character.isJavaIdentifierStart(n.charAt(0))
                    && n.charAt(0) != '_') {

                if (stats.isLanguageReserved(n) == false) {

                    if (i >= oneChar) {
                        ncs[idx++].alias = verifyName(n);
                    }
                }
            }

            for (int j = 0; j <= i && idx < ncs.length; j++) {
                CharCount cc2 = charsCount[j];
                if (cc2 == null) {
                    continue;
                }

                String p = n + cc2.token;
                if (Character.isJavaIdentifierStart(p.charAt(0))
                        && p.charAt(0) != '_') {

                    if (stats.isLanguageReserved(p) == false) {
                        ncs[idx++].alias = verifyName(p);
                    }
                }

                if (idx >= ncs.length) {
                    break;
                }

                if (i != j) {
                    p = String.valueOf(p.charAt(1)) + p.charAt(0);
                    if (Character.isJavaIdentifierStart(p.charAt(0))
                            && p.charAt(0) != '_') {

                        if (stats.isLanguageReserved(p) == false) {
                            ncs[idx++].alias = verifyName(p);
                        }
                    }
                }
            }
        }

        // Les ___
        ncs = _namesCount;

        idx = 0;
        for (int i = 0; i < oneChar && idx < ncs.length; i++) {
            CharCount cc = charsCount[i];
            if (cc == null) {
                continue;
            }

            if (Tools.isOnlyDigit(cc.token)) {
                continue;
            }

            String n = "_" + cc.token;

            ncs[idx++].alias = verifyName(n);
        }

        for (int i = 0; idx < ncs.length; i++) {
            CharCount cc = charsCount[i];
            if (cc == null) {
                continue;
            }

            String n = cc.token;

            if (i >= oneChar) {
                if (Tools.isOnlyDigit(cc.token) == false) {
                    ncs[idx++].alias = verifyName("_" + n);
                }
            }

            for (int j = 0; j <= i && idx < ncs.length; j++) {
                CharCount cc2 = charsCount[j];
                if (cc2 == null) {
                    continue;
                }

                String p = n + cc2.token;

                if (Tools.isOnlyDigit(cc.token) == false) {
                    ncs[idx++].alias = verifyName("_" + p);
                }

                if (idx >= ncs.length) {
                    break;
                }

                if (i != j) {
                    p = String.valueOf(p.charAt(1)) + p.charAt(0);
                    if (stats.isLanguageReserved(p)) {
                        continue;
                    }

                    if (Tools.isOnlyDigit(cc.token) == false) {
                        ncs[idx++].alias = verifyName("_" + p);
                    }
                }
            }

            for (int j = 0; j < 10 && idx < ncs.length; j++) {

                String p = n + (char) ('0' + j);

                if (Tools.isOnlyDigit(cc.token) == false) {
                    ncs[idx++].alias = verifyName("_" + p);
                }

                if (idx >= ncs.length) {
                    break;
                }

                p = String.valueOf(p.charAt(1)) + p.charAt(0);

                if (Tools.isOnlyDigit(cc.token) == false) {
                    ncs[idx++].alias = verifyName("_" + p);
                }
            }
        }

    }

    private String verifyName(String name) {
        if (Character.isJavaIdentifierStart(name.charAt(0)) == false
                || name.charAt(0) > 127 || name.charAt(0) < 32) {
            throw new IllegalStateException("Illegal name ! (" + name + ")");
        }

        for (int i = 1; i < name.length(); i++) {
            if (Character.isJavaIdentifierPart(name.charAt(i)) == false
                    || name.charAt(i) > 127 || name.charAt(i) < 32) {
                throw new IllegalStateException("Illegal name ! (" + name + ")");
            }
        }

        return name;
    }

    public void changeNames(JsStats stats) {
        for (int i = 0; i < namesCount.length; i++) {
            NameCount nc = namesCount[i];

            changeSymbols(stats, nc);

            Set<ASTNode> l = nc.nodes;

            if (l == null) {
                continue;
            }

            for (ASTNode obj : l) {
                Name nameNode = (Name) obj;

                nameNode.setName(nc.alias);
            }
        }

        for (int i = 0; i < _namesCount.length; i++) {
            NameCount nc = _namesCount[i];

            changeSymbols(stats, nc);

            Set<ASTNode> l = nc.nodes;

            if (l == null) {
                continue;
            }

            for (ASTNode obj : l) {
                Name nameNode = (Name) obj;

                nameNode.setName(nc.alias);
            }
        }
    }

    private static void changeSymbols(JsStats stats, NameCount nc) {
        List<StringLiteral> symbs = stats.symbols.get(nc.name);
        if (symbs != null) {
            Set<StringLiteral> ss = new HashSet<StringLiteral>(symbs);

            for (StringLiteral sl : ss) {
                sl.replaceBy(new StringLiteral(nc.alias, sl.getRegion()));
            }
        }
    }

    public void statPrivates() {

        NameCount[] ncs = namesCount;
        for (int i = 0; i < ncs.length; i++) {
            NameCount n = ncs[i];

            if (n.name.startsWith(JsOptimizer.PRIVATE_MEMBER_PREFIX)) {
                privates[Integer.parseInt(n.name
                        .substring(JsOptimizer.PRIVATE_MEMBER_PREFIX.length()))] = n.alias;
            }
        }

    }

    public Properties listProperties(JsStats stats) {

        Properties props = new Properties();

        Arrays.sort(namesCount, new Comparator<NameCount>() {

            public int compare(NameCount o1, NameCount o2) {
                String s1 = o1.name;
                String s2 = o2.name;

                return s1.compareTo(s2);
            }

        });

        for (int i = 0; i < namesCount.length; i++) {
            NameCount nc = namesCount[i];

            if (nc.name.charAt(0) == '#') {
                continue;
            }

            props.setProperty(nc.name, nc.alias);
        }
        for (int i = 0; i < _namesCount.length; i++) {
            NameCount nc = _namesCount[i];

            if (nc.name.charAt(0) == '#') {
                continue;
            }
            props.setProperty(nc.name, nc.alias);
        }

        for (Map.Entry<String, List<String>> entry : stats.privatesByClassNames
                .entrySet()) {
            String className = entry.getKey();
            List<String> l = entry.getValue();

            int idx = 0;
            for (String field : l) {
                props.setProperty(className + "." + field, privates[idx]);

                idx++;
            }
        }

        return props;
    }
}
