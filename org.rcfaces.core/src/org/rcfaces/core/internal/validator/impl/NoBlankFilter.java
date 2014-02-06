/*
 * $Id: NoBlankFilter.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IFilterTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class NoBlankFilter implements IFilterTask {
    

    private static final Log LOG = LogFactory.getLog(NoBlankFilter.class);

    public boolean applyFilter(IClientValidatorContext context, char keyChar) {

        return keyChar != ' ';
    }

}
