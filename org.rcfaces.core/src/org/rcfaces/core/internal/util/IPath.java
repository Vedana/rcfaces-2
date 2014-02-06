/*
 * $Id: IPath.java,v 1.1 2011/04/12 09:25:20 oeuillot Exp $
 */
package org.rcfaces.core.internal.util;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:20 $
 */
public interface IPath {

    IPath makeAbsolute();

    IPath makeRelative();

    boolean isAbsolute();

    String lastSegment();

    IPath append(IPath path);

    IPath removeFirstSegments(int count);

    IPath removeLastSegments(int count);

    String segment(int index);

    int segmentCount();

    String[] segments();

    IPath uptoSegment(int count);
}
