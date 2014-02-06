/*
 * $Id: fa_focusStyleClass.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Focus style class.
 *
 * @aspect abstract fa_focusStyleClass
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._focusStyleClass=undefined; // string
	},
	*/
	/**
	 * @method public
	 * @return String
	 */
	f_getFocusStyleClass: function() {
		if (this._focusStyleClass===undefined) {
			// Appel depuis le constructor de l'objet !
			this._focusStyleClass=f_core.GetAttributeNS(this, "focusStyleClass");
		}
		
		return this._focusStyleClass;
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT
}

new f_aspect("fa_focusStyleClass", {
	members: __members
});

