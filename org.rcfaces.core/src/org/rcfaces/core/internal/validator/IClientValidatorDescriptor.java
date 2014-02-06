/*
 * $Id: IClientValidatorDescriptor.java,v 1.1 2011/04/12 09:25:50 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.validator;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:50 $
 */
public interface IClientValidatorDescriptor extends IDescriptor {

    ITaskDescriptor getFilterTask();

    ITaskDescriptor getTranslatorTask();

    ITaskDescriptor getCheckerTask();

    ITaskDescriptor getFormatterTask();

    ITaskDescriptor getBehaviorTask();

    ITaskDescriptor getProcessorTask();

    ITaskDescriptor getOnErrorTask();

    ITaskDescriptor getOnCheckErrorTask();

    String getConverter();

    IServerConverter getServerConverter();

    String[] listRequiredClasses();

    // IStringAdapterDescriptor getStringFormatter();

}
