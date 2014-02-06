/*
 * $Id: fa_immediate.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Immediate.
 *
 * @aspect abstract fa_immediate
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		this._immediate=undefined;  // Boolean
	},
	*/
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isImmediate: function() {
		if (this._immediate===undefined) {
			// Appel depuis le constructor de l'objet !
			this._immediate=f_core.GetBooleanAttributeNS(this, "immediate", false);
		}
		
		return this._immediate;
	},
	/**
	 * @method hidden
	 * @param optional Boolean set
	 * @return void
	 */
	f_setImmediate: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isImmediate()==set) {
			return;
		}
		
		this._immediate = set;
		
		this.f_setProperty(f_prop.IMMEDIATE,this._immediate);
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT
}

new f_aspect("fa_immediate", {
	members: __members
});	
