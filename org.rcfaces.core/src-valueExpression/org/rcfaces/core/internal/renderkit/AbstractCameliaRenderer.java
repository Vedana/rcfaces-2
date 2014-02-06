/*
 * $Id: AbstractCameliaRenderer.java,v 1.3 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.capability.IValueExpressionCapability;
import org.rcfaces.core.internal.capability.IVariableScopeCapability;
import org.rcfaces.core.internal.component.Properties;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
 */
public abstract class AbstractCameliaRenderer extends AbstractCameliaRenderer1 {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractCameliaRenderer.class);

    private static final String VARIABLE_SCOPE_PROPERTY = "camelia.VARIABLE_SCOPE";

    @Override
    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();
        if ((component instanceof IVariableScopeCapability) == false) {
            return;
        }

        IVariableScopeCapability variableScopeCapability = (IVariableScopeCapability) component;

        String var = variableScopeCapability.getScopeVar();
        if (var == null || var.length() < 1) {
            return;
        }

        Object value = variableScopeCapability.getScopeValue();
        if (value == null) {
            return;
        }

        ValueExpression valueExpression = ((IValueExpressionCapability) component)
                .getValueExpression(Properties.SCOPE_VALUE);

        writer.getComponentRenderContext()
                .getRenderContext()
                .pushScopeVar(var, value, valueExpression,
                        variableScopeCapability.isScopeSaveValue());

        writer.getComponentRenderContext().setAttribute(
                VARIABLE_SCOPE_PROPERTY, var);
    }

    @Override
    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        String scopeVar = (String) writer.getComponentRenderContext()
                .getAttribute(VARIABLE_SCOPE_PROPERTY);
        if (scopeVar != null) {
            writer.getComponentRenderContext().getRenderContext()
                    .popScopeVar(scopeVar);
        }

        super.encodeEnd(writer);
    }
}