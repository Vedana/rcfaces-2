/*
 * $Id: IScriptOperation.java,v 1.3 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.script;

import java.io.IOException;

import org.rcfaces.core.internal.content.IBufferOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
 */
public interface IScriptOperation extends IBufferOperation {

    String filter(String scriptURL,
            String scriptContent, IScriptOperationContext operationContext)
            throws IOException;

}
