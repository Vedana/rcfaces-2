/*
 * $Id: DecodeJsProfiler.java,v 1.1 2011/04/12 09:28:28 oeuillot Exp $
 */
package com.vedana.rcfaces.html.profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DecodeJsProfiler {
    public static void main(String args[]) throws IOException {

        if (args[0].equals("-dir")) {
            parseDir(new File(args[1]));
            return;
        }

        FileReader reader = new FileReader(args[0]);

        BufferedReader buf = new BufferedReader(reader);

        PrintWriter writer = new PrintWriter(System.out);

        parseFile(buf, writer);

        writer.close();
    }

    private static void parseDir(File file) throws IOException {
        File fs[] = file.listFiles();

        for (int i = 0; i < fs.length; i++) {
            File f = fs[i];

            if (f.getName().endsWith(".txt") == false) {
                continue;
            }

            File out = new File(f.getAbsolutePath() + ".csv");

            FileReader reader = new FileReader(f);

            BufferedReader buf = new BufferedReader(reader);

            PrintWriter writer = new PrintWriter(new FileWriter(out));

            parseFile(buf, writer);

            writer.close();

            reader.close();
        }
    }

    private static void parseFile(BufferedReader buf, PrintWriter writer)
            throws IOException {
        String pred = "?";
        int pageInit = -1;
        int pageInitEnd = -1;
        int scriptStart = -1;
        int scriptEnd = -1;
        int onloadStart = -1;
        int onloadEnd = -1;
        int submitStart = -1;
        int submitEnd = -1;
        int exitStart = -1;
        int exitEnd = -1;
        int nbComponents = -1;
        boolean exiting = false;

        List logs = new ArrayList();
        List logsURL = new ArrayList();

        for (;;) {
            String s = buf.readLine();
            if (s == null) {
                break;
            }

            int idx = s.indexOf(" ");

            int date = Integer.parseInt(s.substring(0, idx));

            int idx1 = s.indexOf("[");
            int idx2 = s.indexOf("]", idx1);

            String url = s.substring(idx1 + 1, idx2).trim();

            idx1 = s.indexOf("-", idx2);

            String cmd = s.substring(idx1 + 1).trim();

            if (cmd.equals("page.init")) {
                pageInit = date;
                continue;
            }

            if (cmd.equals("f_core.SubmitEvent")) {
                submitStart = date;
                continue;
            }

            if (cmd.startsWith("f_core._submit(")) {
                submitEnd = date;
                continue;
            }

            if (cmd.startsWith("f_core.onExit(")) {
                exitStart = date;
                exiting = (cmd.indexOf("(true)") > 0);

                continue;
            }

            if (cmd.equals("f_core.onExit [true]")) {
                exitEnd = date;

                logs.add(new int[] { pageInit, pageInitEnd, scriptStart,
                        scriptEnd, onloadStart, onloadEnd, submitStart,
                        submitEnd, exitStart, exitEnd, nbComponents,
                        exiting ? 1 : 0 });

                pageInitEnd = -1;
                pred = url.substring(url.lastIndexOf('/') + 1);
                pred = pred.substring(0, pred.lastIndexOf(".jsf"));

                logsURL.add(pred);

                continue;
            }

            if (cmd.startsWith("f_core.multiWindow.f_requiresBundles")) {
                if (pageInitEnd < 0) {
                    pageInitEnd = date;
                    continue;
                }

                scriptStart = date;
                continue;
            }

            if (cmd.startsWith("javascriptCollector.components")) {
                idx1 = cmd.indexOf('(');
                idx2 = cmd.indexOf(')');
                nbComponents = Integer.parseInt(cmd.substring(idx1 + 1, idx2));
                continue;
            }

            if (cmd.equals("javascriptCollector [true]")) {
                scriptEnd = date;
                continue;
            }

            if (cmd.equals("f_core.onInit")) {
                onloadStart = date;
                continue;
            }

            if (cmd.equals("f_core.onInit [true]")) {
                onloadEnd = date;
                continue;
            }
        }

        writer
                .println("url;init js;decodage html;temps script;mise en page;onload;submit;exit;exit async;tp poste;nb composants");

        logsURL.add("?");

        Iterator it1 = logs.iterator();
        Iterator it2 = logsURL.iterator();

        it2.next();

        for (; it1.hasNext();) {
            int is[] = (int[]) it1.next();

            log(writer, (String) it2.next(), is[0], is[1], is[2], is[3], is[4],
                    is[5], is[6], is[7], is[8], is[9], is[10], is[11] == 1);

        }
    }

    private static void log(PrintWriter writer, String name, int pageInit,
            int pageInitEnd, int scriptStart, int scriptEnd, int onloadStart,
            int onloadEnd, int submitStart, int submitEnd, int exitStart,
            int exitEnd, int nbComponents, boolean async) {

        writer.println(name + ";" + (pageInitEnd - pageInit) + ";"
                + (scriptStart - pageInitEnd) + ";" + (scriptEnd - scriptStart)
                + ";" + (onloadStart - scriptEnd) + ";"
                + (onloadEnd - onloadStart) + ";" + (submitEnd - submitStart)
                + ";" + (exitEnd - exitStart) + ";" + async + ";"
                + (onloadEnd - pageInit) + ";" + nbComponents);
    }
}
