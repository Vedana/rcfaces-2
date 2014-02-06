/*
 * $Id: RemoveLogs.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class RemoveLogs extends AbstractRemoveMethod {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        modified |= process(jsFile, "f_core", "Assert");
        modified |= process(jsFile, "f_core", "Debug");
        modified |= process(jsFile, "f_core", "Info");
        modified |= process(jsFile, "f_core", "Trace");
        modified |= process(jsFile, "f_core", "Warn");

        return modified;
    }

}
