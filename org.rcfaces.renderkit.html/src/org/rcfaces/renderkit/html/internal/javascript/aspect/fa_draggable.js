/*
 * $Id: fa_draggable.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Draggable .
 *
 * @aspect public abstract fa_draggable
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._draggable=undefined;  // Boolean
	},
	*/
	/**
	 * Returns the disable state.
	 *
	 * @method public
	 * @return Boolean <code>true</code> if the component is disabled.
	 */
	f_isDraggable: function() {
		if (this._draggable===undefined) {
			// Appel depuis le constructor de l'objet !
			var dragEffects=f_core.GetNumberAttributeNS(this, "dragEffects");
			
		  	this._draggable=(dragEffects!==undefined);
		  	if (this._draggable) {
		  		this._dragEffects=dragEffects;
		  		var ds=f_core.GetAttributeNS(this, "dragTypes");
		  		if (ds) {
		  			this._dragTypes=ds.split(",");
		  		}		  		
		  	}
		}
		
		return this._draggable;
	},

	
	/**
	 * @method public abstract
	 * @param Object selection
	 * @return Array
	 */
	f_getDragItems : f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param Object selection
	 * @return Array
	 */
	f_getDragItemsValue : f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param Object selection
	 * @return Array
	 */
	f_getDragItemsElement : f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param Object selection
	 * @return Number
	 */
	f_getDragEffects : f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param Object selection
	 * @return Array
	 */
	f_getDragTypes : f_class.ABSTRACT


}

new f_aspect("fa_draggable", {
	members: __members
});	
