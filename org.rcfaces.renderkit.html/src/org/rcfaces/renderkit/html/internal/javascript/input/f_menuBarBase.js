/*
 * $Id: f_menuBarBase.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Class MenuBase
 *
 * @class f_menuBarBase extends f_component, fa_menuCore
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __members = {
	f_menuBarBase: function() {
		this.f_super(arguments);
		
		var tabIndex=this.tabIndex;
		if (tabIndex<=0) {
			tabIndex=0;
		}
		
		this._tabIndex=tabIndex;
		this.tabIndex=-1;
	}
	
	/*
	f_finalize: function() {
		this._tabIndex=undefined; // number
	
		this.f_super(arguments);
	}
	*/
}

new f_class("f_menuBarBase", {
	extend: f_component,
	aspects: [ fa_menuCore ],
	members: __members
});
