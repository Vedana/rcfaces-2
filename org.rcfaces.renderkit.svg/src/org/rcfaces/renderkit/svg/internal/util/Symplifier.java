/*
 * $Id: Symplifier.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.util;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class Symplifier {

    private static double d2(Point p1, Point p2) {
        double d1 = p1.x - p2.x;
        double d2 = p1.y - p2.y;

        return Math.sqrt(d1 * d1 + d2 * d2);
    }

    public static Shape simplifyShape(double lengthTolerance,
            double flatnessTolerance, Shape shape) {

        if (flatnessTolerance < 1.0) {
            flatnessTolerance = 1.0;
        }

        PathIterator pathIterator = shape.getPathIterator(null,
                flatnessTolerance);

        float fs[] = new float[6];

        GeneralPath ret = new GeneralPath();

        List<Point> l = new ArrayList<Point>();

        for (; pathIterator.isDone() == false; pathIterator.next()) {

            int type = pathIterator.currentSegment(fs);

            if (type == PathIterator.SEG_MOVETO) {
                if (l.isEmpty() == false) {
                    drawShape(lengthTolerance, ret, l);
                    l.clear();
                }

                Point point = new Point();
                point.x = (int) fs[0];
                point.y = (int) fs[1];

                l.add(point);
                continue;
            }

            if (type == PathIterator.SEG_LINETO) {
                Point point = new Point();
                point.x = (int) fs[0];
                point.y = (int) fs[1];

                l.add(point);
                continue;
            }

            if (type == PathIterator.SEG_CLOSE) {
                drawShape(lengthTolerance, ret, l);
                l.clear();
                continue;
            }
        }

        if (l.isEmpty() == false) {
            drawShape(lengthTolerance, ret, l);
        }

        return ret;
    }

    private static void drawShape(double tol, GeneralPath ret, List<Point> l) {
        Point pts[] = l.toArray(new Point[l.size()]);

        Point symplified[] = simplifyShape(tol, pts);

        ret.moveTo(symplified[0].x, symplified[0].y);
        for (int i = 1; i < symplified.length; i++) {
            Point pt = symplified[i];
            ret.lineTo(pt.x, pt.y);
        }

        ret.closePath();
    }

    private static Point[] simplifyShape(double tolerance, Point source[]) {
        double tol2 = tolerance * tolerance; // tolerance squared
        int n = source.length;
        Point temp[] = new Point[n]; // vertex buffer
        boolean mk[] = new boolean[n]; // marker buffer

        // STAGE 1. Vertex Reduction within tolerance of prior vertex cluster
        temp[0] = source[0]; // start at the beginning
        int k = 1;
        int pv = 0;
        for (int i = 1; i < n; i++) {
            if (d2(source[i], source[pv]) < tol2) {
                continue;
            }
            temp[k++] = source[i];
            pv = i;
        }
        if (pv < n - 1) {
            temp[k++] = source[n - 1]; // finish at the end
        }

        // STAGE 2. Douglas-Peucker polyline simplification
        mk[0] = mk[k - 1] = true; // mark the first and last vertices
        int cnt = 2 + simplifyDP(tol2, temp, 0, k - 1, mk);

        Point symplified[] = new Point[cnt];
        int m = 0;
        for (int i = 0; i < k; i++) {
            if (mk[i] == false) {
                continue;
            }

            symplified[m++] = temp[i];
        }

        return symplified;
    }

    private static int simplifyDP(double tolerance2, Point[] points, int j,
            int k, boolean mk[]) {
        if (k <= j + 1) { // there is nothing to simplify
            return 0;
        }

        int maxi = j; // index of vertex farthest from S
        double maxd2 = 0; // distance squared of farthest vertex
        double cu = d2(points[j], points[k]);

        Point p = new Point();

        for (int i = j + 1; i < k; i++) {
            // compute distance squared
            double cw = d2(points[i], points[j]);

            double dv2;
            if (cw <= 0) {
                dv2 = d2(points[i], points[j]);

            } else if (cu <= cw) {
                dv2 = d2(points[i], points[k]);

            } else {
                double b = cw / cu;

                p.x = (int) (points[j].x + b * (points[k].x - points[j].x));
                p.y = (int) (points[j].y + b * (points[k].y - points[j].y));
                dv2 = d2(points[i], p);
            }
            // test with current max distance squared
            if (dv2 <= maxd2) {
                continue;
            }
            // v[i] is a new max vertex
            maxi = i;
            maxd2 = dv2;
        }

        int cnt = 0;
        if (maxd2 > tolerance2) {
            // split the polyline at the farthest vertex from S
            mk[maxi] = true; // mark v[maxi] for the simplified polyline
            cnt++;
            // recursively simplify the two subpolylines at v[maxi]
            cnt += simplifyDP(tolerance2, points, j, maxi, mk); // polyline v[j]
            // to v[maxi]
            cnt += simplifyDP(tolerance2, points, maxi, k, mk); // polyline
            // v[maxi] to
            // v[k]
        }

        // else the approximation is OK, so ignore intermediate vertices
        return cnt;
    }
}
