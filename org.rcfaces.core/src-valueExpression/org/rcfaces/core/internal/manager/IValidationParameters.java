/*
 * $Id: IValidationParameters.java,v 1.1 2011/04/12 09:25:44 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.manager;

import java.util.Map;

import javax.el.ValueExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:44 $
 */
public interface IValidationParameters {
    void setValidationParameter(String name, ValueExpression value, boolean client);

    String setValidationParameter(String name, String value, boolean clientSide);

    String removeValidationParameter(String name);

    String getValidationParameter(String name);

    boolean isClientSideValidationParameter(String name);

    int getValidationParametersCount();

    Map getValidationParametersMap();

    Map getClientValidationParametersMap();
}
