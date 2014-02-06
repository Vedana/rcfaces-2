/*
 * $Id: IScriptFile.java,v 1.1 2011/04/12 09:28:28 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.script;

import java.io.IOException;

import org.rcfaces.core.internal.content.IFileBuffer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:28 $
 */
public interface IScriptFile extends IFileBuffer {

    void initialize(String contentType, byte scriptContent[], long lastModified)
            throws IOException;

}
