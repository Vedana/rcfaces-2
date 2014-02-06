/*
 * $Id: IOnErrorTask.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.validator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IOnErrorTask extends IClientValidatorTask {

    int CHECK_TASK = 1;

    int FORMATTER_TASK = 2;

    int BEHAVIOR_TASK = 4;

    void performError(IClientValidatorContext context, int task,
            boolean errorPerformed);
}
