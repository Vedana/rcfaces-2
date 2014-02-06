/*
 * $Id: IProcessRulesGenerationInformation.java,v 1.1 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:04 $
 */
public interface IProcessRulesGenerationInformation {

    String PROCESS_RULES_ATTRIBUTE = "org.rcfaces.ProcessRules";

    boolean isProcessRulesEnabled();
}
