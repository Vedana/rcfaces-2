/*
 * $Id: IResolvedContentWrapper.java,v 1.1 2011/04/12 09:25:17 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentStorage;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:17 $
 */
public interface IResolvedContentWrapper extends IResolvedContent {
    IResolvedContent getResolvedContent();
}
