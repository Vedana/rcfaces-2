/*
 * $Id: AlphaFrFilter.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class AlphaFrFilter extends AlphaFilter {
    

    private static final Log LOG = LogFactory.getLog(AlphaFrFilter.class);

    private static final String LATIN_ACCENT_FR = "�������������������������������";

    protected void appendRegExp(StringAppender sa) {
        super.appendRegExp(sa);

        sa.append(LATIN_ACCENT_FR);
    }

}
