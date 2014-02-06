/*
 * $Id: fa_required.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Required. 
 *
 * @aspect public abstract fa_required
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._required=undefined; //boolean
	},
	*/
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isRequired: function() {
		var required=this._required;
		if (required===undefined) {
			// Appel depuis le constructor de l'objet !
			required=f_core.GetBooleanAttributeNS(this, "required", false);
			this._required=required;
		}

		return required;
	},
	/**
	 * @method public
	 * @param Boolean set
	 * @return void
	 */
	f_setRequired: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isRequired()==set) {
			return;
		}
		
		this._required = set;
		
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(f_prop.REQUIRED,this._required);
	
		this.fa_updateRequired(set);
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
	fa_updateRequired: f_class.ABSTRACT
};

new f_aspect("fa_required", {
	members: __members
});
