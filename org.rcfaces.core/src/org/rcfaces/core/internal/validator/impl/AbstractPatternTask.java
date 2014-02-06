/*
 * $Id: AbstractPatternTask.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IFilterTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public abstract class AbstractPatternTask extends AbstractClientValidatorTask
        implements IFilterTask {
    

    private static final Log LOG = LogFactory.getLog(AbstractPatternTask.class);

    protected abstract Pattern getPattern(IClientValidatorContext context);

    public boolean applyFilter(IClientValidatorContext context, char keyChar) {

        Pattern pattern = getPattern(context);

        return pattern.matcher(new StringAppender(keyChar)).matches();
    }
}
