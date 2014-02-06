/*
 * $Id: AbstractStyleOperation.java,v 1.2 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.content.AbstractBufferOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:02 $
 */
public abstract class AbstractStyleOperation extends AbstractBufferOperation
        implements IStyleOperation {

    private static final Log LOG = LogFactory
            .getLog(AbstractStyleOperation.class);
}
