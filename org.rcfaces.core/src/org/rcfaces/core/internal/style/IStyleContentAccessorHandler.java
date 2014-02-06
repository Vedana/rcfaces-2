/*
 * $Id: IStyleContentAccessorHandler.java,v 1.2 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.style;

import org.rcfaces.core.internal.contentAccessor.ICompositeContentAccessorHandler;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:02 $
 */
public interface IStyleContentAccessorHandler extends
        ICompositeContentAccessorHandler {

    String STYLE_CONTENT_PROVIDER_ID = "org.rcfaces.core.STYLE_CONTENT_PROVIDER";

    String MERGE_FILTER_NAME = "merge";
    
    String PROCESS_FILTER_NAME = "process";

    IStyleOperation getStyleOperation(String operationId);
}
