/*
 * $Id: RemoveVerifyProperties.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;

public class RemoveVerifyProperties extends AbstractRemoveMethod {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(jsFile, "f_core", "VerifyProperties", true);
    }

}
