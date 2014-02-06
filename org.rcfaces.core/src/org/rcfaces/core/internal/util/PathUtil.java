/*
 * $Id: PathUtil.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class PathUtil {

    

    private static final Log LOG = LogFactory.getLog(PathUtil.class);

    public static String normalizePath(String path) {

        IPath p = new Path(path);

        return p.toString();
        /*
         * boolean modified = false;
         * 
         * StringTokenizer st = new StringTokenizer(path, "/", true);
         * 
         * List l = new ArrayList((st.countTokens() / 2) + 1); boolean sep =
         * true; for (; st.hasMoreTokens();) { String segment = st.nextToken();
         * if (segment.equals("/")) { if (sep) { sep = false; continue; }
         * modified = true; continue; } sep = true; l.add(segment); }
         * 
         * for (int i = 0; i < l.size();) { String segment = (String) l.get(i);
         * 
         * if (segment.equals("..")) { modified = true;
         * 
         * l.remove(i); if (i < 1) { continue; }
         * 
         * i--; l.remove(i); continue; }
         * 
         * if (segment.equals(".")) { modified = true;
         * 
         * l.remove(i); continue; }
         * 
         * i++; }
         * 
         * if (modified == false) { return path; }
         * 
         * StringAppender sa = new StringAppender(path.length()); for (Iterator
         * it = l.iterator(); it.hasNext();) { sa.append('/');
         * sa.append((String) it.next()); }
         * 
         * return sa.toString();
         */
    }
}
