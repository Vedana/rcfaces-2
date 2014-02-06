/*
 * $Id: AbstractActionListener.java,v 1.3 2013/12/19 15:46:48 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import java.util.Arrays;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.tools.ListenersTools.IMethodExpressionCreator;
import org.rcfaces.core.internal.util.ForwardMethodExpression;

//import com.sun.faces.el.ELConstants;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/19 15:46:48 $
 */
abstract class AbstractActionListener implements StateHolder,
        IServerActionListener, IPartialRenderingListener {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractActionListener.class);

    private static final Class[] FACES_PARAMETERS = new Class[] { FacesEvent.class };

    private static final Class[] NO_PARAMETERS = new Class[0];

    private String expression;

    private MethodExpression speciedMethodExpression;

    private transient boolean forwarNameMethodInitialized;

    private transient MethodExpression forwardNameMethodExpression;

    private transient boolean argsMethodExpressionInitialized;

    private transient MethodExpression argsMethodExpression;

    private transient boolean noArgsMethodExpressionInitialized;

    private transient MethodExpression noArgsMethodExpression;

    private transient boolean facesArgsMethodExpressionInitialized;

    private transient MethodExpression facesArgsMethodExpression;

    private boolean transientValue;

    private boolean partialRendering;

    protected AbstractActionListener() {
        // Pour la deserialisation
    }

    protected AbstractActionListener(String expression) {
        this.expression = expression;
    }

    protected AbstractActionListener(String expression, boolean partialRendering) {
        this(expression);
        this.partialRendering = partialRendering;
    }

    public boolean isPartialRendering() {
        return partialRendering;
    }

    public void setPartialRendering(boolean partialRendering) {
        this.partialRendering = partialRendering;
    }

    /*
     * public final MethodExpression getMethodExpression() { return
     * methodBinding; }
     */
    protected void process(FacesEvent event) throws AbortProcessingException {

        FacesContext facesContext = getFacesContext();

        Object parameters[] = new Object[] { event };

        if (speciedMethodExpression != null) {
            Exception th = tryMethodExpression(facesContext,
                    speciedMethodExpression, parameters, event);

            if (th == null) {
                return;
            }

            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }

            throw new FacesException(
                    "Exception when calling forward name method '" + expression
                            + "'.", th);
        }

        if (forwarNameMethodInitialized == false) {
            forwarNameMethodInitialized = true;

            // Format #[xxx] ???
            String forwardName = getForwardName(expression);
            if (forwardName != null) {
                // Oui !
                forwardNameMethodExpression = getForwardMethodExpression();
            }
        }

        if (forwardNameMethodExpression != null) {
            Exception th = tryMethodExpression(facesContext,
                    forwardNameMethodExpression, null, event);

            if (th == null) {
                return;
            }

            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }

            throw new FacesException(
                    "Exception when calling forward name method '" + expression
                            + "'.", th);
        }

        Exception firstThrowable = null;

        if (argsMethodExpressionInitialized == false) {
            argsMethodExpressionInitialized = true;
            argsMethodExpression = getArgumentsMethodExpression(facesContext);
        }
        if (argsMethodExpression != null) {
            Exception th = tryMethodExpression(facesContext,
                    argsMethodExpression, parameters, event);
            if (th == null) {
                return;
            }
            firstThrowable = th;
        }

        if (facesArgsMethodExpressionInitialized == false) {
            facesArgsMethodExpressionInitialized = true;
            facesArgsMethodExpression = getFacesArgumentsMethodExpression(facesContext);
        }
        if (facesArgsMethodExpression != null) {
            Exception th = tryMethodExpression(facesContext,
                    facesArgsMethodExpression, parameters, event);
            if (th == null) {
                return;
            }
            if (firstThrowable == null) {
                firstThrowable = th;
            }
        }

        if (noArgsMethodExpressionInitialized == false) {
            noArgsMethodExpressionInitialized = true;
            noArgsMethodExpression = getNoArgsMethodExpression(facesContext);
        }
        if (noArgsMethodExpression != null) {
            Exception th = tryMethodExpression(facesContext,
                    noArgsMethodExpression, null, event);
            if (th == null) {
                return;
            }
            if (firstThrowable == null) {
                firstThrowable = th;
            }
        }

        LOG.error("Can not find method associated to expression '" + expression
                + "'.", firstThrowable);
    }

    private Exception tryMethodExpression(FacesContext facesContext,
            MethodExpression methodBinding, Object parameters[],
            FacesEvent event) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Try method expression '" + methodBinding
                    + "' parameters='" + Arrays.asList(parameters)
                    + "' event='" + event + "'");
        }

        try {
            Object ret = methodBinding.invoke(facesContext.getELContext(),
                    parameters);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Call method expression '" + methodBinding
                        + "' parameters='" + Arrays.asList(parameters)
                        + "' event='" + event + "' returns '" + ret + "'");
            }

            processReturn(facesContext, methodBinding, event, ret);

            return null;

        } catch (PropertyNotFoundException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Property not found for expression '" + expression
                        + "'.", ex);
            }

            return ex;

        } catch (MethodNotFoundException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Method not found for expression '" + expression
                        + "'.", ex);
            }

            return ex;

        } catch (ELException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Evaluation error for expression '" + expression
                        + "'.", ex);
            }

            return processException(ex, event);

        } catch (RuntimeException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Evaluation exception for expression '" + expression
                        + "'.", ex);
            }

            throw ex;
        }
    }

    protected Exception processException(ELException ex, FacesEvent event) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Process exception '" + ex + "' event='" + event
                    + "', ex");
        }

        Throwable cause = ex.getCause();

        if (cause instanceof AbortProcessingException) {
            throw (AbortProcessingException) cause;
        }

        if (cause instanceof MethodNotFoundException) {
            return (Exception) cause;
        }

        if (cause instanceof PropertyNotFoundException) {
            return (Exception) cause;
        }

        if (cause instanceof NoSuchMethodException) {
            return (Exception) cause;
        }

        throw ex;
    }

    private MethodExpression getArgumentsMethodExpression(
            FacesContext facesContext) {
        return facesContext.getApplication().getExpressionFactory()
                .createMethodExpression(facesContext.getELContext(),
                        expression, null, listParameterClasses());
    }

    private MethodExpression getFacesArgumentsMethodExpression(
            FacesContext facesContext) {
        return facesContext.getApplication().getExpressionFactory()
                .createMethodExpression(facesContext.getELContext(),
                        expression, null, FACES_PARAMETERS);
    }

    private MethodExpression getNoArgsMethodExpression(FacesContext facesContext) {
        return facesContext.getApplication().getExpressionFactory()
                .createMethodExpression(facesContext.getELContext(),
                        expression, null, NO_PARAMETERS);
    }

    protected abstract Class[] listParameterClasses();

    private MethodExpression getForwardMethodExpression() {
        forwarNameMethodInitialized = true;

        noArgsMethodExpression = new ForwardMethodExpression(expression);
        return noArgsMethodExpression;
    }

    protected static final String getForwardName(String expression) {
        int len = expression.length() - 1;

        if (len < 3) {
            return null;
        }

        if (expression.startsWith("#[") == false) {
            return null;
        }

        if (expression.charAt(len) != ']') {
            return null;
        }

        return expression.substring(2, len);
    }

    protected final FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public boolean isTransient() {
        return transientValue;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientValue = newTransientValue;
    }

    public final void restoreState(FacesContext context, Object state) {
        Object ret[] = (Object[]) state;

        expression = (String) ret[0];
        if (expression == null) {
            throw new NullPointerException("Expression is null !");
        }

        partialRendering = Boolean.TRUE.equals(ret[1]);

        speciedMethodExpression = (MethodExpression) ret[2];

        forwarNameMethodInitialized = false;
    }

    public final Object saveState(FacesContext context) {
        Object objects[] = { expression,
                (partialRendering) ? Boolean.TRUE : null,
                speciedMethodExpression };

        return objects;
    }

    protected void processReturn(FacesContext facesContext,
            MethodExpression binding, FacesEvent event, Object ret) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Call of method binding='" + binding + "' event='"
                    + event + "' returns " + ret);
        }

        if ((ret instanceof String) == false) {
            if (LOG.isInfoEnabled() && ret != null) {
                LOG.info("Return of method binding='" + binding + "' event='"
                        + event + "' is not a String !");
            }
            return;
        }

        String outcome = (String) ret;

        NavigationHandler navHandler = facesContext.getApplication()
                .getNavigationHandler();

        navHandler.handleNavigation(facesContext,
                binding.getExpressionString(), outcome);

        facesContext.renderResponse();
    }

    public boolean equals(Object object) {
        if (object == null
                || (object instanceof AbstractActionListener) == false) {
            return false;
        }

        AbstractActionListener s = (AbstractActionListener) object;

        if (expression != s.expression) {
            if (expression == null || expression.equals(s.expression) == false) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        if (expression == null) {
            return 0;
        }

        return expression.hashCode();
    }

    public MethodExpression getSpeciedMethodExpression() {
        return speciedMethodExpression;
    }

    public void createMethodExpression(FacesContext facesContext,
            IMethodExpressionCreator methodExpressionCreator) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        ELContext elContext = facesContext.getELContext();

        Class parameters[] = listParameterClasses();

        RuntimeException firstEx = null;
        try {
            speciedMethodExpression = methodExpressionCreator.create(
                    expression, parameters);

            MethodInfo methodInfo = speciedMethodExpression
                    .getMethodInfo(elContext);
            // On provoque la recherche d'info pour vérifier que la méthode
            // existe :-)

            if (LOG.isDebugEnabled()) {
                LOG.debug("Method expression for '" + expression
                        + "' with parameter '" + parameters[0].getName()
                        + "' detected ! " + speciedMethodExpression
                        + " methodInfo=" + methodInfo);
            }

            return;

        } catch (RuntimeException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create method expression for '" + expression
                        + "' with parameter '" + parameters[0].getName()
                        + "' throws exception.", ex);
            }

            firstEx = ex;
        }

        try {
            speciedMethodExpression = methodExpressionCreator.create(
                    expression, FACES_PARAMETERS);

            MethodInfo methodInfo = speciedMethodExpression
                    .getMethodInfo(elContext);

            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("Method expression for '" + expression
                                + "' with FacesEvent parameter detected ! "
                                + speciedMethodExpression + " methodInfo="
                                + methodInfo);
            }

            return;

        } catch (RuntimeException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create method expression for '" + expression
                        + "' with facesEvent parameter throws exception.", ex);
            }
        }

        try {
            speciedMethodExpression = methodExpressionCreator.create(
                    expression, NO_PARAMETERS);

            MethodInfo methodInfo = speciedMethodExpression
                    .getMethodInfo(elContext);

            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("Method expression for '" + expression
                                + "' with no parameter detected ! "
                                + speciedMethodExpression + " methodInfo="
                                + methodInfo);
            }

            return;

        } catch (RuntimeException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create method expression for '" + expression
                        + "' without parameters throws exception.", ex);
            }
        }

        throw firstEx;
    }
}
