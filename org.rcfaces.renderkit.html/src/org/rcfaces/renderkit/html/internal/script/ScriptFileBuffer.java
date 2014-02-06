/*
 * $Id: ScriptFileBuffer.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.script;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.content.FileBuffer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
public class ScriptFileBuffer extends FileBuffer implements IScriptFile {


    private static final Log LOG = LogFactory.getLog(ScriptFileBuffer.class);

    public ScriptFileBuffer(String bufferName) {
        super(bufferName);
    }

}
