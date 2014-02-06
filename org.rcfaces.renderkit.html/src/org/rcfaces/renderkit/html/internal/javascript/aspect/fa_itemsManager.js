/*
 * $Id: fa_itemsManager.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect ItemsManager
 *
 * @aspect public abstract fa_itemsManager extends fa_cardinality
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __members = {

	/**
	 * @method protected abstract
	 * @param String value
	 * @return Object An item
	 */
	fa_getElementItem: f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @param Object The item
	 * @return String the value
	 */
	fa_getElementValue: f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 */
	fa_isElementDisabled: f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 */
	fa_listVisibleElements: f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 */
	fa_showElement: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 */
	fa_updateElementStyle: f_class.ABSTRACT
};

new f_aspect("fa_itemsManager", {
	extend: [ fa_cardinality ],
	members: __members
});
