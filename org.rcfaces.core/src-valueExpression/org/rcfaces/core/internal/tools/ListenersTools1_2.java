/*
 * $Id: ListenersTools1_2.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.listener.IServerActionListener;
import org.rcfaces.core.internal.util.ForwardMethodExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class ListenersTools1_2 extends ListenersTools {
    

    private static final Log LOG = LogFactory.getLog(ListenersTools1_2.class);

    private static final Class[] NO_PARAMETER = new Class[0];

    public static void parseListener(FacesContext facesContext,
            UIComponent component, IListenerType listenerType,
            ValueExpression expression) {
        parseListener(facesContext, component, listenerType, expression
                .getExpressionString(), false, null);
    }

    public static void parseListener(FacesContext facesContext,
            UIComponent component, IListenerType listenerType,
            ValueExpression expression, boolean defaultAction) {
        parseListener(facesContext, component, listenerType, expression
                .getExpressionString(), defaultAction, null);
    }

    public static final void parseAction(FacesContext facesContext,
            UIComponent component, IListenerType listenerType,
            ValueExpression expression) {
        parseAction(facesContext, component, listenerType, expression
                .getExpressionString(), null);
    }

    public static final void parseAction(FacesContext facesContext,
            UIComponent component, IListenerType listenerType,
            String expression, IMethodExpressionCreator methodExpressionCreator) {
        expression = expression.trim();

        if (LOG.isDebugEnabled()) {
            LOG.debug("ParseAction  component='" + component
                    + "' listenerType='" + listenerType + "' expression='"
                    + expression + "'.");
        }

        if (expression.length() < 1) {
            return;
        }

        Application application = facesContext.getApplication();

        if (component instanceof UICommand) {
            UICommand command = (UICommand) component;

            MethodExpression vb;

            if (BindingTools.isBindingExpression(expression)) {
                if (methodExpressionCreator != null) {

                    vb = methodExpressionCreator.create(expression,
                            NO_PARAMETER);

                } else {
                    vb = application.getExpressionFactory()
                            .createMethodExpression(
                                    facesContext.getELContext(), expression,
                                    null, NO_PARAMETER);
                }

            } else {
                vb = new ForwardMethodExpression(expression);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Set command action to component '"
                        + component.getId() + "' : " + expression);
            }

            command.setActionExpression(vb);
            return;
        }

        if (BindingTools.isBindingExpression(expression) == false) {
            expression = "#[" + expression + "]";
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Add server listener to component '" + component.getId()
                    + "' : " + expression);
        }

        IServerActionListener serverActionListener = listenerType
                .addActionListener(component, application, expression, false);

        if (serverActionListener != null && methodExpressionCreator != null) {

            serverActionListener.createMethodExpression(facesContext,
                    methodExpressionCreator);
        }

    }
}
