/*
 * $Id: fa_readOnly.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect ReadOnly
 *
 * @aspect public abstract fa_readOnly
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._readOnly=undefined; //Boolean
	},
	*/
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isReadOnly: function() {
		if (this._readOnly===undefined) {
			// Appel depuis le constructor de l'objet !
			this._readOnly=f_core.GetBooleanAttributeNS(this, "readOnly", false);
		}

		return this._readOnly;
	},
	/**
	 * @method public
	 * @param optional Boolean set
	 * @return void
	 */
	f_setReadOnly: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isReadOnly()==set) {
			return;
		}
		
		this._readOnly = set;
	
		this.fa_updateReadOnly(set);
		
		this.f_setProperty(f_prop.READONLY, set);
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateReadOnly: f_class.ABSTRACT
};

new f_aspect("fa_readOnly", {
	members: __members
});
	
