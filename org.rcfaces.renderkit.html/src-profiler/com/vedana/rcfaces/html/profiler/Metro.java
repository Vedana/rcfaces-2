/*
 * $Id: Metro.java,v 1.1 2011/04/12 09:28:28 oeuillot Exp $
 */
package com.vedana.rcfaces.html.profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Metro {

    private static boolean groupByEtat = false;

    private static String output = null;

    private static String user;

    private static String session;

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static void main(String args[]) throws IOException, ParseException {

        boolean dir = true;

        int i = 0;
        for (; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-groupByEtat")) {
                groupByEtat = true;
                continue;
            }

            if (arg.equals("-output")) {
                output = args[++i];
                continue;
            }

            if (arg.equals("-user")) {
                user = args[++i];
                continue;
            }

            if (arg.equals("-session")) {
                session = args[++i];
                continue;
            }

            if (args[0].equals("-dir")) {
                dir = true;
                break;
            }

            break;
        }

        if (dir) {
            parseDir(args, i);
        }

        parseFile(args[i]);
    }

    private static void parseDir(String args[], int pos) {

    }

    private static void parseFile(String filename) throws IOException,
            ParseException {
        String buffer = loadFile(filename);

        PrintStream out;

        if ("-".equals(output)) {
            out = System.out;

        } else {
            out = new PrintStream(new FileOutputStream(new File(filename
                    + ".csv")));
        }

        parseFile(out, buffer);

        out.close();
    }

    private static void parseFile(PrintStream out, String buffer)
            throws IOException, ParseException {

        Map tps = new LinkedHashMap();

        int idx = 0;

        if (session != null || user != null) {
            out.print("Filtres:");

            if (session != null) {
                out.print(" session='" + session + "'");
            }
            if (user != null) {
                out.print(" user='" + user + "'");
            }

            out.println();

            out.println();
        }

        out
                .println("horodatage;nom etat;t1t0;t2t0;t1t31;t31t2;t1t3;t5t4;t51t4;tejb;tn;tl;services");

        List dates = new ArrayList();

        for (;;) {
            int p = buffer.indexOf("<METROLOGIE>", idx);
            if (p < 0) {
                break;
            }
            idx = p + 1;

            int p2 = buffer.indexOf("</METROLOGIE>", p);

            String m = buffer.substring(p, p2);

            if (m.indexOf("T1_T0") < 0) {
                continue;
            }

            String nom = search("NOM_TRANSACTION", m);
            String nomEtat = search("NOM_ETAT", m);

            /*
             * String ip = search("IP_INSTANCE", m); if
             * (ip.startsWith("85.61.")) { // Portable continue; }
             */
            if (user != null) {
                String luser = search("ID_USER", m);
                if (user.equals(luser) == false) {
                    continue;
                }
            }

            if (session != null) {
                String lsession = search("ID_SESSION", m);
                if (session.equals(lsession) == false) {
                    continue;
                }
            }

            String horo = search("HORODATAGE", m);

            Date d = dateFormat.parse(horo);
            if (false) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.set(Calendar.SECOND,
                        calendar.get(Calendar.SECOND) % 10);
                d = calendar.getTime();
            }

            String t1t0 = search("T1_T0", m);
            String t2t0 = search("T2_T0", m);
            String t1t31 = search("T1_T31", m);
            String t31t2 = search("T31_T2", m);
            String t1t3 = search("T1_T3", m);
            String t5t4 = search("T5_T4", m);
            String t51t4 = search("T51_T4", m);
            String tejb = search("T_EJB_NAV", m);
            String tn = search("TN", m);
            String tl = searchTL("TL", m);

            out.println(horo + ";" + nomEtat + ";" + t1t0 + ";" + t2t0 + ";"
                    + t1t31 + ";" + t31t2 + ";" + t1t3 + ";" + t5t4 + ";"
                    + t51t4 + ";" + tejb + ";" + tn + ";" + tl);

            List l = (List) tps.get(nomEtat);
            if (l == null) {
                l = new ArrayList();
                tps.put(nomEtat, l);
            }

            if (t2t0.length() > 0 && t1t31.length() > 0) {
                l
                        .add(new T(d, Integer.parseInt(t2t0)
                                + Integer.parseInt(t1t31)));

                dates.add(d);
            }

        }

        if (groupByEtat) {
            out.println();

            out.print("date;");
            boolean first = true;
            for (Iterator it = tps.keySet().iterator(); it.hasNext();) {

                if (first) {
                    first = false;
                } else {
                    out.print(";");
                }

                out.print(it.next());
            }

            out.println();

            Collections.sort(dates);

            for (Iterator it2 = dates.iterator(); it2.hasNext();) {
                Date d = (Date) it2.next();

                out.print(dateFormat.format(d) + ";");

                first = true;
                for (Iterator it = tps.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Entry) it.next();

                    if (first) {
                        first = false;
                    } else {
                        out.print(";");
                    }

                    List l = (List) entry.getValue();

                    if (l.isEmpty()) {
                        continue;
                    }

                    T t = (T) l.get(0);
                    if (t.d.compareTo(d) > 0) {
                        continue;
                    }

                    l.remove(0);

                    out.print(t.time);
                }

                out.println();
            }
        }

        out.close();
    }

    private static String search(String tag, String m) {

        int idx = m.indexOf("<" + tag + ">");
        if (idx < 0) {
            return null;
        }
        int idx2 = m.indexOf("</" + tag + ">");

        String v = m.substring(m.indexOf(">", idx) + 1, idx2);

        return v.replace(';', '_');
    }

    private static String searchTL(String tag, String m) {

        int lp = 0;

        int cnt = 0;

        StringBuffer sb = new StringBuffer();

        for (;;) {
            int idx = m.indexOf("<" + tag, lp);
            if (idx < 0) {
                break;
            }
            lp = idx + 1;

            int idx2 = m.indexOf("</" + tag + ">", idx);

            String v = m.substring(m.indexOf("\"", idx) + 1, idx2);

            v = v.replace('>', ' ');
            v = v.replace('\"', ':');

            int c = Integer.parseInt(v.substring(v.indexOf(": ") + 2, v
                    .length()));

            cnt += c;

            v = v.substring(0, v.indexOf(':') + 1) + c;

            if (sb.length() > 0) {
                sb.append(",");
            }

            sb.append(v.replace(';', '_'));
        }

        return cnt + ";" + sb.toString();
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

    private static class T {

        public Date d;

        public int time;

        public T(Date d, int time) {
            this.d = d;
            this.time = time;
        }
    }
}
