/*
 * $Id: fa_selected.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Selected
 *
 * @aspect public abstract fa_selected
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._selected=undefined; // boolean
		// this._initialSelection=undefined; // boolean
	},
	*/
	/**
	 * Retourne <code>true</code> si le composant est désactivé.
	 *
	 * @method public
	 * @return Boolean
	 */
	f_isSelected: function() {
		if (this._selected===undefined) {
			// Appel depuis le constructor de l'objet !
			this._selected=f_core.GetBooleanAttributeNS(this, "selected", false);
			this._initialSelection=this._selected;
		}
		
		return this._selected;
	},
	/**
	 * Spécifie si le composant est selectionné.
	 *
	 * @method public
	 * @param optional Boolean set <code>true</code> pour selectionner le composant.
	 * @return void
	 */
	f_setSelected: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isSelected()==set) {
			return;
		}
		
		this._selected = set;
	
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(f_prop.SELECTED, set);
	
		this.fa_updateSelected(set);
	},
	
	/**
	 * @method hidden
	 * @return Boolean
	 */
	fa_getInitialSelection: function() {
		if (this._initialSelection===undefined) {
			this.f_isSelected();
		}
		
		return this._initialSelection;
	},
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateSelected: f_class.ABSTRACT,


	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT
}

new f_aspect("fa_selected", {
	members: __members
});
	
