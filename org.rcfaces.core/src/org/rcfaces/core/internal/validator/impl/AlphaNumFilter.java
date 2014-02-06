/*
 * $Id: AlphaNumFilter.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class AlphaNumFilter extends AlphaFilter {
    

    private static final Log LOG = LogFactory.getLog(AlphaNumFilter.class);

    private static final String NUM = "0-9";

    protected void appendRegExp(StringAppender sa) {
        super.appendRegExp(sa);

        sa.append(NUM);
    }

}
