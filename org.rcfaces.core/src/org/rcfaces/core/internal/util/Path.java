/*
 * $Id: Path.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class Path implements IPath {

    private static final Log LOG = LogFactory.getLog(Path.class);

    private final List<String> segments;

    private final boolean absolute;

    private Path(List<String> segments, boolean absolute) {
        this.segments = new ArrayList<String>(segments);
        this.absolute = absolute;
    }

    public Path(String path) {
        StringTokenizer st = new StringTokenizer(path, "/", true);

        segments = new ArrayList<String>(st.countTokens() / 2 + 2);

        boolean _absolute = false;
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            if (token.equals("/")) {
                if (segments.isEmpty()) {
                    _absolute = true;
                }

                continue;
            }

            segments.add(token);
        }

        absolute = _absolute;

        normalizePath(segments, absolute);
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public IPath makeAbsolute() {
        if (absolute) {
            return this;
        }
        return new Path(segments, true);
    }

    public IPath makeRelative() {
        if (absolute == false) {
            return this;
        }
        return new Path(segments, false);
    }

    public String segment(int index) {
        if (index < 0 || index >= segments.size()) {
            throw new IndexOutOfBoundsException();
        }

        return segments.get(index);
    }

    public int segmentCount() {
        return segments.size();
    }

    public String[] segments() {
        return segments.toArray(new String[segments.size()]);
    }

    public String lastSegment() {
        return segment(segments.size() - 1);
    }

    public IPath uptoSegment(int count) {
        if (count < 1) {
            return new Path(Collections.<String> emptyList(), absolute);
        }

        if (count >= segments.size()) {
            return new Path(segments, absolute);
        }

        return new Path(segments.subList(0, count), absolute);
    }

    public IPath append(IPath path) {
        if (path.isAbsolute()) {
            return path;
        }

        List<String> l = new ArrayList<String>(segments);

        l.addAll(Arrays.asList(path.segments()));

        normalizePath(l, absolute);

        return new Path(l, absolute);
    }

    public IPath removeFirstSegments(int count) {
        if (count < 0 || count >= segments.size()) {
            return new Path(Collections.<String> emptyList(), absolute);
        }

        return new Path(segments.subList(count, segments.size()), absolute);
    }

    public IPath removeLastSegments(int count) {
        return uptoSegment(segments.size() - count);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (absolute ? 1231 : 1237);
        result = prime * result
                + ((segments == null) ? 0 : segments.hashCode());
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
        final Path other = (Path) obj;
        if (absolute != other.absolute)
            return false;
        if (segments == null) {
            if (other.segments != null)
                return false;
        } else if (!segments.equals(other.segments))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringAppender sa = new StringAppender(segments.size() * 16);

        boolean first = true;
        for (Iterator<String> it = segments.iterator(); it.hasNext();) {
            if (first) {
                first = false;
                if (absolute) {
                    sa.append('/');
                }
            } else {
                sa.append('/');
            }

            sa.append(it.next());
        }

        return sa.toString();
    }

    private static void normalizePath(List<String> segments, boolean absolute) {
        for (int i = 0; i < segments.size();) {
            String segment = segments.get(i);

            if (segment.length() == 0 || segment.equals(".")) {
                segments.remove(i);
                continue;
            }

            if (segment.equals("..")) {
                if (i == 0) {
                    if (absolute == false) {
                        i++;
                        continue;
                    }
                    segments.remove(i);
                    continue;
                }

                if (segments.get(i - 1).equals("..")) {
                    i++;
                    continue;
                }

                segments.remove(i--);
                segments.remove(i);
                continue;
            }

            i++;
        }
    }
}