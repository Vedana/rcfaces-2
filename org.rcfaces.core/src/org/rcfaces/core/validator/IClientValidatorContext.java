/*
 * $Id: IClientValidatorContext.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.validator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IClientValidatorContext {

    FacesContext getFacesContext();

    IComponentRenderContext getComponentRenderContext();

    IParameter[] getParameters();

    void setInputValue(String input);

    void setOutputValue(String output);

    String getInput();

    String getOutputValue();

    void setLastError(String summary, String detail,
            FacesMessage.Severity severity);

    String getLastErrorSummary();

    String getLastErrorDetail();

    FacesMessage.Severity getLastErrorSeverity();

    boolean containsAttribute(String key);

    Object getAttribute(String key);

    Object setAttribute(String key, Object value);

    Object removeAttribute(String key);

}
