/*
 * $Id: JsPrivateStaticMember.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.camelia.generator.js.parser.JsStats.NameCount;

public class SymbolJsPrivateStaticMember extends JsPrivateStaticMember {

	private final NameCount nameCount;
	
    public SymbolJsPrivateStaticMember(IJsClass jsClass, NameCount nc) {
        super(jsClass, null, null, false, null, null);
        nameCount = nc;
    }
    
    public NameCount getNameCount() {
		return nameCount;
	}
}