/*
 * $Id: CssParserProvider.java,v 1.1.2.1 2012/11/15 10:13:47 oeuillot Exp $
 */
package org.rcfaces.css.internal;

import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1.2.1 $ $Date: 2012/11/15 10:13:47 $
 */
public class CssParserProvider extends AbstractProvider {

    private static final String CSS_PARSER_SERVICE_ID = "org.rcfaces.css.CSS_PARSER";

    @Override
    public String getId() {
        return CSS_PARSER_SERVICE_ID;
    }

}
