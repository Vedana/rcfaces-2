/*
 * $Id: f_helpMessageZone.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/** 
 * f_helpMessageZone class
 *
 * @class f_helpMessageZone extends f_component
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */
var __members = {

	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return f_core.GetTextNode(this);
	},
	/**
	 * @method public
	 * @param String text
	 * @return void
	 */
	f_setText: function(text) {
		if (text == this.f_getText()) {
			return;
		}
		
		f_core.SetTextNode(this, text);
		this.f_setProperty(f_prop.TEXT,text);
	},
	/**
	 * @method hidden
	 * @param f_component elt
	 * @return void
	 */
	f_showMessage: function(elt) {
		this._component = elt;
		var msg = elt.f_getHelpMessage();
		if (!msg) {
			msg = "";
		}
		f_core.SetTextNode(this, msg);
	},
	/**
	 * @method hidden
	 * @param f_component elt
	 * @return void
	 */
	f_hideMessage: function(elt) {
		if (this._component != elt) {
			return;
		}
		f_core.SetTextNode(this, "");
		this._component = undefined;
	}
}

new f_class("f_helpMessageZone", {
	extend: f_component,
	members: __members
});
