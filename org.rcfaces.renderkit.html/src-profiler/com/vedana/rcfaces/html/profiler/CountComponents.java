/*
 * $Id: CountComponents.java,v 1.1 2011/04/12 09:28:28 oeuillot Exp $
 */
package com.vedana.rcfaces.html.profiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CountComponents {
    public static void main(String args[]) throws IOException {

        int p = 0;
        String componentsList = null;
        String benchJSFilename = null;

        for (; p < args.length; p++) {
            if (args[p].equals("-components")) {
                componentsList = args[++p];
                continue;
            }
            if (args[p].equals("-benchJS")) {
                benchJSFilename = args[++p];
                continue;
            }

            break;
        }

        String s = loadFile(args[p]);

        Map components = new HashMap();

        if (componentsList != null) {
            StringTokenizer st = new StringTokenizer(componentsList, ",");
            for (; st.hasMoreTokens();) {
                String tok = st.nextToken();
                components.put(tok, new Comp(tok));
            }
        }

        int idx = 0;
        for (;;) {
            int idx2 = s.indexOf("v:class=", idx);
            if (idx2 < 0) {
                break;
            }

            int p1 = s.lastIndexOf("<", idx2);
            int p2 = s.indexOf(">", idx2);

            idx = p2 + 1;

            String tag = s.substring(p1 + 1, p2).trim();

            p1 = tag.indexOf(' ');
            tag = tag.substring(p1 + 1).trim();

            if (tag.endsWith("/")) {
                tag = tag.substring(0, tag.length() - 1);
            }

            Map parameters = new HashMap();

            char cs[] = tag.toCharArray();
            int deb = 0;
            String name = null;
            for (int i = 0; i < cs.length; i++) {
                char c = cs[i];
                if (c == '=') {
                    name = tag.substring(deb, i);
                    deb = i + 1;
                    continue;
                }
                if (Character.isWhitespace(c)) {
                    if (deb < 0) {
                        continue;
                    }
                    String v = tag.substring(deb, i);

                    if (v.startsWith("\"") && v.endsWith("\"")) {
                        v = v.substring(1, v.length() - 1);
                    }

                    deb = -1;

                    if (name == null) {
                        name = v;
                        v = "";
                    }

                    parameters.put(name, v);
                    name = null;
                    continue;
                }
                if (deb < 0) {
                    deb = i;
                }

                if (c == '\"' || c == '\'') {
                    for (i++; i < cs.length; i++) {
                        if (cs[i] == c) {
                            break;
                        }
                    }
                }
            }

            if (deb > 0) {
                String v = tag.substring(deb, cs.length);

                if (v.startsWith("\"") && v.endsWith("\"")) {
                    v = v.substring(1, v.length() - 1);
                }

                if (name == null) {
                    name = v;
                    v = "";
                }

                parameters.put(name, v);
            }

            // System.out.println(parameters);

            String vclass = (String) parameters.get("v:class");

            Comp i1 = (Comp) components.get(vclass);
            if (i1 == null) {
                i1 = new Comp(vclass);
                components.put(vclass, i1);
            }

            i1.newComponent(parameters);
        }

        if (benchJSFilename != null) {
            benchJs(components, s, benchJSFilename);
            return;
        }

        List keys = new ArrayList(components.keySet());
        Collections.sort(keys);

        System.out.print("");
        for (Iterator it = keys.iterator(); it.hasNext();) {

            System.out.print(it.next() + ";");
        }
        System.out.println("total");

        int cnt = 0;
        System.out.print("");
        for (Iterator it = keys.iterator(); it.hasNext();) {
            Comp comp = (Comp) components.get(it.next());

            System.out.print(comp.ps.size() + ";");
            cnt += comp.ps.size();
        }
        System.out.println(cnt);

    }

    private static String loadFile(String filename) throws IOException {

        FileReader reader = new FileReader(filename);

        BufferedReader buf = new BufferedReader(reader);

        StringBuffer sb = new StringBuffer(256000);

        for (;;) {
            String s = buf.readLine();
            if (s == null) {
                break;
            }

            sb.append(s);
            sb.append('\n');
        }

        return sb.toString();
    }

    private static void benchJs(Map components, String buffer,
            String benchJsFilename) throws IOException {

        String jsContent = loadFile(benchJsFilename);

        Map timeMarkers = new HashMap();

        int idx = 0;
        for (;;) {
            int i = jsContent.indexOf("javascriptCollector.", idx);
            if (i < 0) {
                break;
            }

            idx = i + 1;

            int i1 = jsContent.indexOf("(", i);
            int i2 = jsContent.indexOf(")", i1);

            String n = jsContent.substring(i1 + 2, i2);

            i1 = jsContent.lastIndexOf("\n", i);
            i2 = jsContent.indexOf(" ", i1);

            int t = Integer.parseInt(jsContent.substring(i1 + 1, i2));

            timeMarkers.put(n, new Integer(t));

        }

        idx = 0;

        Map cidToComp = new HashMap();
        for (Iterator it = components.values().iterator(); it.hasNext();) {
            Comp comp = (Comp) it.next();

            for (Iterator it2 = comp.ps.iterator(); it2.hasNext();) {
                Map m = (Map) it2.next();

                cidToComp.put(m.get("id"), comp.vclass);
            }
        }

        for (;;) {
            int i = buffer.indexOf("javascriptCollector.", idx);
            if (i < 0) {
                break;
            }

            idx = i + 1;

            i = buffer.indexOf("(", i);
            int i2 = buffer.indexOf(")", i);

            int tname = Integer.parseInt(buffer.substring(i + 2, i2));

            int j1 = buffer.indexOf("javascriptCollector.", i2);
            int j2 = buffer.indexOf("_rcfacesClassLoader.", i2);
            if (j2 < 0 || j1 < 0 || j1 < j2) {
                continue;
            }

            j1 = buffer.indexOf("\"", j2);
            j2 = buffer.indexOf("\"", j1 + 1);

            String id = buffer.substring(j1 + 1, j2);

            System.out
                    .println(id
                            + ";"
                            + cidToComp.get(id)
                            + ";"
                            + (((Integer) timeMarkers.get("" + (tname + 1)))
                                    .intValue() - ((Integer) timeMarkers.get(""
                                    + (tname))).intValue()));

        }
    }

    private static class Comp {

        private final List ps = new ArrayList();

        private final String vclass;

        public Comp(String vclass) {
            this.vclass = vclass;
        }

        public void newComponent(Map parameters) {
            ps.add(parameters);
        }

    }
}
