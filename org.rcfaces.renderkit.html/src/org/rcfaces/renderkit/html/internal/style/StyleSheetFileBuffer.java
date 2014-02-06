/*
 * $Id: StyleSheetFileBuffer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.content.FileBuffer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class StyleSheetFileBuffer extends FileBuffer implements IStyleSheetFile {
    

    private static final Log LOG = LogFactory
            .getLog(StyleSheetFileBuffer.class);

    public StyleSheetFileBuffer(String bufferName) {
        super(bufferName);
    }

}
