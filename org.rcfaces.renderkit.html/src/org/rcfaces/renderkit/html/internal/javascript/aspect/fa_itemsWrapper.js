/*
 * $Id: fa_itemsWrapper.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Items
 *
 * @aspect public abstract fa_itemsWrapper extends fa_itemClientDatas
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
	f_finalize: function() {
		this._itemsWrapper=undefined; // fa_items
	},

	/**
	 * @method public
	 * @param Object item
	 * @param String message
	 * @return void
	 */
	f_setItemToolTip: function(item, message) {
		this._getItemsWrapper().f_setItemToolTip(item, message);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemToolTip: function(item) {
		return this._getItemsWrapper().f_getItemToolTip(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param String imageURL
	 * @return void
	 */
	f_setItemImageURL: function(item, imageURL) {
		this._getItemsWrapper().f_setItemImageURL(item, imageURL);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemImageURL: function(item) {
		return this._getItemsWrapper().f_getItemImageURL(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param String imageURL
	 * @return void
	 */
	f_setItemDisabledImageURL: function(item, imageURL) {
		this._getItemsWrapper().f_setItemDisabledImageURL(item, imageURL);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemDisabledImageURL: function(item) {
		return this._getItemsWrapper().f_getItemDisabledImageURL(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param String imageURL
	 * @return void
	 */
	f_setItemHoverImageURL: function(item, imageURL) {
		this._getItemsWrapper().f_setItemHoverImageURL(item, imageURL);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemHoverImageURL: function(item) {
		return this._getItemsWrapper().f_getItemHoverImageURL(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param String imageURL
	 * @return void
	 */
	f_setItemSelectedImageURL: function(item, imageURL) {
		this._getItemsWrapper().f_setItemSelectedImageURL(item, imageURL);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemSelectedImageURL: function(item) {
		return this._getItemsWrapper().f_getItemSelectedImageURL(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return Boolean
	 */
	f_isItemDisabled: function(item) {		
		return this._getItemsWrapper().f_isItemDisabled(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param Boolean disabled
	 * @return void
	 */
	f_setItemDisabled: function(item, disabled) {
		this._getItemsWrapper().f_setItemDisabled(item, disabled);
	},
	/**
	 * @method public
	 * @param Object item Item to verify visibility. (This parameter can be a String as the value of the item !)
	 * @return Boolean
	 */
	f_isItemVisible: function(item) {
		return this._getItemsWrapper().f_isItemVisible(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param Boolean visible
	 * @return void
	 */
	f_setItemVisible: function(item, visible) {
		this._getItemsWrapper().f_setItemVisible(item, visible);
	},
	/**
	 * @method public
	 * @param Object item Item to check. (This parameter can be a String as the value of the item !)
	 * @return Boolean
	 */
	f_isItemChecked: function(item) {
		return this._getItemsWrapper().f_isItemChecked(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @param Boolean checked
	 * @param optional Boolean notFireChecked
	 * @return void
	 */
	f_setItemChecked: function(item, checked, notFireChecked) {
		this._getItemsWrapper().f_setItemChecked(item, checked, notFireChecked);
	},
	/**
	 * @method public
	 * @param Object value
	 * @return Object
	 */
	f_getItemByValue: function(value) {
		return this._getItemsWrapper().f_getItemByValue(value);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return any
	 */
	f_getItemValue: function(item) {
		return this._getItemsWrapper().f_getItemValue(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemAccessKey: function(item) {
		return this._getItemsWrapper().f_getItemAccessKey(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String
	 */
	f_getItemGroupName: function(item) {
		return this._getItemsWrapper().f_getItemGroupName(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return Object[]
	 */
	f_listItemChildren: function(item) {
		return this._getItemsWrapper().f_listItemChildren(item);
	},
	/**
	 * @method public
	 * @param Object item
	 * @return Boolean
	 */
	f_hasItemChildren: function(item) {
		return this._getItemsWrapper().f_hasItemChildren(item);
	},
	/**
	 * @method public
	 * @param Object item Item object.
	 * @param String key Key of property.
	 * @return String Value associated to the specified property.
	 */
	f_getItemClientData: function(item, key) {
		return this._getItemsWrapper().f_getItemClientData(item, key);
	},
	/**
	 * @method public
	 * @param Object item Item object.
	 * @param String key Key of property.
	 * @param optional String value Value of property.
	 * @return String old value
	 */
	f_setItemClientData: function(item, key, value) {
		return this._getItemsWrapper().f_setItemClientData(item, key, calue);
	},
	/**
	 * @method public
	 * @param Object item Item object.
	 * @return Object Values associated to the specified property.
	 */
	f_getItemClientDatas: function(item) {
		return this._getItemsWrapper().f_getItemClientDatas(item);
	},
	/**
	 * @method private
	 */
	_getItemsWrapper: function() {
		var itemsWrapper=this._itemsWrapper;
		
		if (itemsWrapper) {
			return itemsWrapper;
		}
		
		itemsWrapper=this.fa_getItemsWrapper();
		f_core.Assert(itemsWrapper, "Items wrapper is not defined !");
		
		this._itemsWrapper=itemsWrapper;
		
		return itemsWrapper;
	},
	
	/**
	 * @method protected abstract
	 */
	fa_getItemsWrapper: f_class.ABSTRACT
}

new f_aspect("fa_itemsWrapper", {
	members: __members
});
