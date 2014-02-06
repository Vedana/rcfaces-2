/*
 * $Id: IJsClassProcessor.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsClass;
import com.vedana.camelia.generator.js.parser.JsStats;

public interface IJsClassProcessor extends IJsProcessor {
    boolean process(JsStats stats, IJsClass jsClass);

}
