/*
 * $Id: fa_disabled.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Disable state.
 *
 * @aspect abstract fa_disabled
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._disabled=undefined;  // Boolean
		// this._initialDisabled=undefined; // Boolean
	},
	*/
	/**
	 * Returns the disable state.
	 *
	 * @method public
	 * @return Boolean <code>true</code> if the component is disabled.
	 */
	f_isDisabled: function() {
		if (this._disabled===undefined) {
			// Appel depuis le constructor de l'objet !
		  	this._disabled=f_core.GetBooleanAttributeNS(this, "disabled", false);
		  	this._initialDisabled=this._disabled;
		}
		
		return this._disabled;
	},
	/**
	 * Set the disabled state.
	 *
	 * @method public
	 * @param optional Boolean set <code>true</code> to disable the component
	 * @return void
	 */
	f_setDisabled: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isDisabled()==set) {
			return;
		}
		
		this._disabled = set;
		
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(f_prop.DISABLED, set);

		this.fa_updateDisabled(set);
	},

	/**
	 * @method hidden
	 * @return Boolean
	 */
	fa_getInitialDisabled: function() {
		if (this._initialDisabled===undefined) {
			this.f_isDisabled();
		}
		
		return this._initialDisabled;
	},
	
	/**
	 * @method protected abstract
	 * @param Boolean set
	 * @return void
	 */
	fa_updateDisabled: f_class.ABSTRACT,


	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT
};

new f_aspect("fa_disabled", {
	members: __members
});	
