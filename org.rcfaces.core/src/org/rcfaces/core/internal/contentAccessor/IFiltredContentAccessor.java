/*
 * $Id: IFiltredContentAccessor.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface IFiltredContentAccessor extends IContentAccessor {
    String getFilter();
}
