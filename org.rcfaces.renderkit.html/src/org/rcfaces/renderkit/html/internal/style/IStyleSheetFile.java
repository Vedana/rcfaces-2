/*
 * $Id: IStyleSheetFile.java,v 1.1 2011/04/12 09:28:22 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import java.io.IOException;

import org.rcfaces.core.internal.content.IFileBuffer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:22 $
 */
public interface IStyleSheetFile extends IFileBuffer {

    void initialize(String contentType, byte styleSheetContent[], long lastModified) throws IOException;
}
