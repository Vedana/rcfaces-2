/*
 * $Id: IFunctionRuleProcessor.java,v 1.1.2.1 2012/11/27 16:23:02 oeuillot Exp $
 */
package org.rcfaces.css.internal.rules;

import org.rcfaces.css.internal.CssPropertyListIterator;
import org.w3c.css.sac.LexicalUnit;

import com.steadystate.css.dom.Property;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1.2.1 $ $Date: 2012/11/27 16:23:02 $
 */
public interface IFunctionRuleProcessor {

    void process(CssPropertyListIterator declarationList,
            UserAgentPropertyRule ur, Property p, LexicalUnit lexicalUnit);

}
