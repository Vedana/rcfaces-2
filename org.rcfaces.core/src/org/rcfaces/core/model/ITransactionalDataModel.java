/*
 * $Id: ITransactionalDataModel.java,v 1.1 2011/04/12 09:25:43 oeuillot Exp $
 */
package org.rcfaces.core.model;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:43 $
 */
public interface ITransactionalDataModel {
    void enableTransactionalObjects(boolean enable);
}
