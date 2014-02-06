package org.rcfaces.core.internal.component;

import javax.el.ValueExpression;

public interface IUnproxifyValueExpression  {

	ValueExpression process(ValueExpression valueExpression);
	
}
