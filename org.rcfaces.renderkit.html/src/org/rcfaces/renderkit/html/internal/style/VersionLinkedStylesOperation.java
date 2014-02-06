/*
 * $Id: VersionLinkedStylesOperation.java,v 1.2 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import java.io.IOException;

import org.rcfaces.core.internal.style.AbstractStyleOperation;
import org.rcfaces.core.internal.style.IStyleParser;
import org.rcfaces.core.internal.style.IStyleParser.IParserContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:04 $
 */
public class VersionLinkedStylesOperation extends AbstractStyleOperation
        implements ICssOperation {

    public VersionLinkedStylesOperation() {
        setName("Versioning linked styles");
    }

    public String filter(IStyleParser cssParser,
            String styleSheetURL, String styleSheetContent,
            IParserContext parserContext) throws IOException {
        return cssParser.normalizeBuffer(styleSheetURL,
                styleSheetContent, parserContext);
    }
}
