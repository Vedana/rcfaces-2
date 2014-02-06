/*
 * $Id: f_textEditorCombo.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * class f_textEditorCombo
 *
 * @class f_textEditorCombo extends f_combo
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {
	f_textEditorCombo: function() {
		this.f_super(arguments);
		
		this._type=f_core.GetAttributeNS(this,"type");
		this._for=f_core.GetAttributeNS(this,"for");
		
		this.f_addEventListener(f_event.SELECTION, this._onSelection);
	},
	/*
	f_finalize: function() {
		// this._for=undefined; // string
		// this._type=undefined; // string
		
		this.f_super(arguments);
	},
	*/
	f_update: function() {
		this.f_super(arguments);
		
		f_textEditor.RegisterTextEditorButton(this._for, this);		
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getType: function() {
		return this._type;
	},
	/**
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_onSelection: function(event) {
		var type=this._type;
		if (!type) {
			return false;
		}		
		
		f_textEditor.PerformCommand(this._for, this, event.f_getValue());
		
		return true;
	}
}

new f_class("f_textEditorCombo", {
	extend: f_combo,
	members: __members
});
