/*
 * $Id: f_imageCheckButton.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * class f_imageCheckButton
 *
 * @class f_imageCheckButton extends f_imageButton, fa_selected
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {
	
	/**
	 * @method protected
	 * @param f_event event
	 * @return Boolean
	 */
	f_performImageSelection: function(event) {
		this.f_setSelected(!this.f_isSelected());
		return true;
	},
	fa_updateSelected: function() {
		this._updateImage();
	},
	/**
	 * @method protected
	 */
	f_serialize: function() {
		// Dans tous les cas, il faut renvoyer au serveur l'état
		// car il peut utiliser des Beans request !
		this.f_setProperty(f_prop.SELECTED,this.f_isSelected());

		this.f_super(arguments);
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @return Object The value associated.
	 */
	f_getValue: function() {
		return this.f_isSelected();
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @param Object value
	 * @return Boolean If value is recognized.
	 */
	f_setValue: function(value) {
		this.f_setSelected(!!value);
	}
};

new f_class("f_imageCheckButton", {
	extend: f_imageButton, 
	aspects: [fa_selected],
	members: __members
});
