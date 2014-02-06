/*
 * $Id: VariableResolver.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class VariableResolver {

    private static final Log LOG = LogFactory.getLog(VariableResolver.class);

    /**
     * Search and instanciate (if necessary) a backing bean by its name.
     * 
     * @param variable
     *            The variable name of the backing bean.
     * @return An object (can not return <code>null</code>)
     * @throws FacesException
     *             if the variable is unknown.
     */
    public static Object resolve(String variable) {
        return resolve(variable, null);
    }

    /**
     * Search and instanciate (if necessary) a backing bean by its name.
     * 
     * @param variable
     *            The variable name of the backing bean.
     * @param facesContext
     *            The facesContext or <code>null</code>.
     * @return An object (can not return <code>null</code>)
     * @throws FacesException
     *             if the variable is unknown.
     */
    public static Object resolve(String variable, FacesContext facesContext) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        if (Constants.VERIFY_VARIABLE_SYNTAX) {
            char chs[] = variable.toCharArray();

            for (int i = 0; i < chs.length; i++) {
                if (i == 0) {
                    if (Character.isJavaIdentifierStart(chs[i]) == false) {
                        throw new FacesException(
                                "Illegal start of variable name '" + variable
                                        + "'.");
                    }

                    continue;
                }

                if (Character.isJavaIdentifierPart(chs[i]) == false) {
                    throw new FacesException(
                            "Illegal character into variable name '" + variable
                                    + "'. (position=" + i + ")");
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Try to resolve variable '" + variable + "'.");
        }

        Object ret;
        if (false /* JSF 1.2 */) {
            /*
             * @TODO if JSF 1.2 : faces.getELContext();
             */
        } else {
            javax.faces.el.VariableResolver variableResolver = facesContext
                    .getApplication().getVariableResolver();

            ret = variableResolver.resolveVariable(facesContext, variable);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved variable '" + variable + "' = " + ret);
        }

        if (ret == null) {
            throw new FacesException("No variable associated to name '"
                    + variable + "'.");
        }

        return ret;
    }
}
