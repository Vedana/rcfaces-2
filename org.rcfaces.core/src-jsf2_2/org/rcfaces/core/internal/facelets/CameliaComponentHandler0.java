/*
 * $Id: CameliaComponentHandler0.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.capability.IComponentLifeCycle;
import org.rcfaces.core.internal.tools.ListenersTools;
import org.rcfaces.core.internal.tools.ListenersTools.IListenerType;
import org.rcfaces.core.internal.tools.ListenersTools1_2;

import com.sun.faces.facelets.el.TagMethodExpression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class CameliaComponentHandler0 extends ComponentHandler {
    

    private static final Log LOG = LogFactory
            .getLog(CameliaComponentHandler0.class);

    private static final boolean debugEnabled = LOG.isDebugEnabled();

    public CameliaComponentHandler0(ComponentConfig config) {
        super(config);
    }

    @Override
    public void onComponentCreated(FaceletContext ctx, UIComponent component,
            UIComponent parent) {
        super.onComponentCreated(ctx, component, parent);

        TagAttribute binding = getAttribute("binding");
        if (binding == null) {

            if (component instanceof IComponentLifeCycle) {
                IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

                componentLifeCycle
                        .initializePhase(ctx.getFacesContext(), false);
                
                //componentLifeCycle.settedPhase(ctx.getFacesContext());
            }

            if (debugEnabled) {
                LOG.debug("Create component for id '"
                        + getComponentConfig().getTagId() + "' returns '"
                        + component + "'.");
            }
            return;
        }

        ValueExpression ve = binding.getValueExpression(ctx, Object.class);

        Object bindingValue = ve.getValue(ctx);

        if (component instanceof IComponentLifeCycle) {
            IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

            componentLifeCycle.initializePhase(ctx.getFacesContext(),
                    bindingValue != null);
            
            //componentLifeCycle.settedPhase(ctx.getFacesContext());
        }

        if (debugEnabled) {
            LOG.debug("Create component for id '"
                    + getComponentConfig().getTagId() + "' returns '"
                    + component + "'.");
        }
    }
    
    @Override
    public void onComponentPopulated(FaceletContext ctx, UIComponent component,
    		UIComponent parent) {
    	super.onComponentPopulated(ctx, component, parent);
    	
    	if (component instanceof IComponentLifeCycle) {
            IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;
            
            componentLifeCycle.settedPhase(ctx.getFacesContext());
        }

    	
    }

    protected static void actionApplyMetaData(final FaceletContext ctx,
            UIComponent instance, String expression,
            IListenerType defaultListenerType, final TagAttribute tagAttribute) {

        ListenersTools1_2.parseAction(ctx.getFacesContext(), instance,
                defaultListenerType, expression,
                new ListenersTools.IMethodExpressionCreator() {

                    public MethodExpression create(String expression,
                            Class[] paramTypes) {

                        ExpressionFactory expressionFactory = ctx
                                .getExpressionFactory();

                        MethodExpression methodExpression = expressionFactory
                                .createMethodExpression(ctx, expression, null,
                                        paramTypes);

                        return new TagMethodExpression(tagAttribute,
                                methodExpression);
                    }
                });
    }
}
