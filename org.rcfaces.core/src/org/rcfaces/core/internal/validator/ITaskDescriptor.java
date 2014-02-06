/*
 * $Id: ITaskDescriptor.java,v 1.1 2011/04/12 09:25:50 oeuillot Exp $
 */
package org.rcfaces.core.internal.validator;

import org.rcfaces.core.validator.IClientValidatorTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:50 $
 */
public interface ITaskDescriptor {
    String getClientTaskExpression();

    String getClientTaskExpressionType();

    IClientValidatorTask getServerTask();
}
