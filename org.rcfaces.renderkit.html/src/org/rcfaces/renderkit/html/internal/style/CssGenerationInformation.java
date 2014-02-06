/*
 * $Id: CssGenerationInformation.java,v 1.1 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:04 $
 */
public class CssGenerationInformation extends
        BasicGenerationResourceInformation implements
        IProcessRulesGenerationInformation {

    public CssGenerationInformation(boolean processRules) {
        if (processRules) {
            setAttribute(PROCESS_RULES_ATTRIBUTE, Boolean.TRUE);
        }
    }

    public boolean isProcessRulesEnabled() {
        return Boolean.TRUE.equals(getAttribute(PROCESS_RULES_ATTRIBUTE));
    }
}