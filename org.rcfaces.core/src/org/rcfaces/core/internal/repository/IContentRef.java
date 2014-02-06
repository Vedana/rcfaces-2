/*
 * $Id: IContentRef.java,v 1.1 2013/11/13 12:53:24 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import org.rcfaces.core.internal.repository.IRepository.ICriteria;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:24 $
 */
public interface IContentRef {
    ICriteria getCriteria();
}
