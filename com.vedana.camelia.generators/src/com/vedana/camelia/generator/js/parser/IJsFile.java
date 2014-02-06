/*
 * $Id: IJsFile.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.Document;

public interface IJsFile {
    IJsClass getJsClass();

    Document getDocument();
}
