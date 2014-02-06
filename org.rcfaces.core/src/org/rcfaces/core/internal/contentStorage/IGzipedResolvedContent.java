/*
 * $Id: IGzipedResolvedContent.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IGzipedResolvedContent extends IResolvedContent {

    IResolvedContent getGzipedContent();
}
