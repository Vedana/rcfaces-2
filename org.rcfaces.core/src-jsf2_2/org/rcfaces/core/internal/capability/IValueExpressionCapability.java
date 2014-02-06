/*
 * $Id: IValueExpressionCapability.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

import java.io.Serializable;

import javax.el.ValueExpression;

public interface IValueExpressionCapability {
    ValueExpression getValueExpression(Serializable name);
}
