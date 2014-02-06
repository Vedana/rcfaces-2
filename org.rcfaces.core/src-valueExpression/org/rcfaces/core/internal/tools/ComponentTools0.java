/*
 * $Id: ComponentTools0.java,v 1.5 2014/01/07 13:48:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2014/01/07 13:48:20 $
 */
public class ComponentTools0 {

    private static final Log LOG = LogFactory.getLog(ComponentTools0.class);

    protected static void broadcastActionCommand(FacesContext facesContext,
            UICommand component) {
        String outcome = null;

        MethodExpression binding = component.getActionExpression();
        if (binding != null) {
            try {
                Object invokeResult = binding.invoke(
                        facesContext.getELContext(), null);

                if (invokeResult != null) {
                    outcome = invokeResult.toString();
                }
                // else, default to null, as assigned above.
            } catch (RuntimeException ex) {
                throw ex;

            } catch (Exception e) {
                LOG.error(e);

                throw new FacesException(binding.getExpressionString() + ": "
                        + e.getMessage(), e);
            }
        }

        Application application = facesContext.getApplication();

        // Retrieve the NavigationHandler instance..

        NavigationHandler navHandler = application.getNavigationHandler();

        // Invoke nav handling..

        navHandler.handleNavigation(facesContext,
                (null != binding) ? binding.getExpressionString() : null,
                outcome);

        // Trigger a switch to Render Response if needed
        facesContext.renderResponse();

    }

}
