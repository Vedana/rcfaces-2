/*
 * $Id: fa_itemClientDatas.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * 
 *
 * @aspect public abstract fa_itemClientDatas
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {

	/**
	 * @method public
	 * @param Object item Item object.
	 * @param String key Key of property.
	 * @return String Value associated to the specified property.
	 */
	f_getItemClientData: function(item, key) {
		if (typeof(item)!="object") {
			item=this.f_getItemByValue(item, true);
		}
	
		f_core.Assert(typeof(item)=="object", "fa_itemClientDatas.f_getItemClientData: Invalid item object. ("+item+")");
		f_core.Assert(typeof(key)=="string", "fa_itemClientDatas.f_getItemClientData: Invalid key parameter ! ("+key+")");
		
		var clientDatas=item._clientDatas;
		if (!clientDatas) {
			return undefined;
		}
		
		return clientDatas[key];
	},
	/**
	 * @method public
	 * @param Object item Item object.
	 * @return Object
	 */
	f_getItemClientDatas: function(item) {
		if (typeof(item)!="object") {
			item=this.f_getItemByValue(item, true);
		}
	
		f_core.Assert(typeof(item)=="object", "fa_itemClientDatas.f_getItemClientDatas: Invalid item object. ("+item+")");
		
		var clientDatas=item._clientDatas;
		if (!clientDatas) {
			return new Object;
		}
		
		return f_core.CopyObject(clientDatas);
	},
	/**
	 * @method public
	 * @param Object item Item object.
	 * @param String key Key of property.
	 * @param optional String value Value of property.
	 * @return String old value
	 */
	f_setItemClientData: function(item, key, value) {
		if (typeof(item)!="object") {
			item=this.f_getItemByValue(item, true);
		}
	
		f_core.Assert(typeof(item)=="object", "fa_itemClientDatas.f_setItemClientData: Invalid item object. ("+item+")");
		f_core.Assert(typeof(key)=="string", "fa_itemClientDatas.f_setItemClientData: Invalid key parameter ! ("+key+")");
		f_core.Assert(value===undefined || value===null || typeof(value)=="string", "fa_itemClientDatas.f_setItemClientData: Invalid value parameter ! ("+value+")");
		
		var clientDatas=item._clientDatas;
		if (!clientDatas) {
			if (value===undefined) {
				return value;
			}
			
			clientDatas=new Object;
			item._clientDatas=clientDatas;
		}
		
		var old=clientDatas[key];
		clientDatas[key]=value;
		
		return old;
	},
	/**
	 * @method hidden
	 */
	f_setItemClientDatas: function(item, clientDatas) {
		if (typeof(item)!="object") {
			item=this.f_getItemByValue(item, true);
		}
	
		f_core.Assert(typeof(item)=="object", "fa_itemClientDatas.f_setItemClientDatas: Invalid item object. ("+item+")");
		f_core.Assert(typeof(clientDatas)=="object", "fa_itemClientDatas.f_setItemClientDatas: Invalid clientDatas parameter ! ("+clientDatas+")");
		
		item._clientDatas=clientDatas;
	},
	/**
	 * Returns the item associated to the specified value.
	 * 
	 * @method public abstract
	 * @param String value Value of an item.
	 * @param hidden Boolean assertIfNotFound 
	 * @return Object Item associated with the value.
	 */
	f_getItemByValue: f_class.ABSTRACT
};

new f_aspect("fa_itemClientDatas", {
	members: __members
});
