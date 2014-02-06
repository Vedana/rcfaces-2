/*
 * $Id: GeneratedScriptInformation.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.script;

import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class GeneratedScriptInformation extends BasicGeneratedResourceInformation {
    

    public GeneratedScriptInformation() {
        super();
    }

    public String getCharSet() {
        return (String) getAttribute(IScriptContentModel.CHARSET_PROPERTY);
    }

    public void setCharSet(String charSet) {
        setAttribute(IScriptContentModel.CHARSET_PROPERTY, charSet);
    }
}
