/*
 * $Id: IScriptContentAccessorHandler.java,v 1.1 2011/04/12 09:25:37 oeuillot Exp $
 */
package org.rcfaces.core.internal.script;

import org.rcfaces.core.internal.contentAccessor.ICompositeContentAccessorHandler;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:37 $
 */
public interface IScriptContentAccessorHandler extends
        ICompositeContentAccessorHandler {

    String SCRIPT_CONTENT_PROVIDER_ID = "org.rcfaces.core.SCRIPT_CONTENT_PROVIDER";

    IScriptOperation getScriptOperation(String operationId);

    boolean isOperationSupported(String operationId,
            IContentAccessor contentAccessor);
}
