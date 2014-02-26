/*
 * $Id: SelectorsRule.java,v 1.1 2008/03/27 14:27:27 oeuillot Exp $
 */
package com.steadystate.css.parser.selectors;

import org.w3c.css.sac.SelectorList;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2008/03/27 14:27:27 $
 */
public interface SelectorsRule {
    SelectorList getSelectorList();
}
