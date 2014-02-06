/*
 * $Id: f_domClientStorage.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 *
 * @class hidden f_domClientStorage extends f_clientStorage
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {
		
	/**
	 * @field private static final Number
	 */
	_STORAGE_MAX_SIZE: 5242880,
	
	/**
	 * @field private static Object
	 */
	_StorageList: undefined,

	/**
	 * @method protected static
	 */
	Initializer: function() {

		var globalStorage=window.globalStorage;
		if (!globalStorage || (globalStorage instanceof StorageList)==false) {
			return;
		}
		
		var domain=f_env.GetDomain();
		
		var storage=globalStorage[domain];
		
		f_core.Debug(f_domClientStorage, "Initializer: Use global storage (domain="+storage+") = "+storage);
		
		f_domClientStorage._Storage=storage;
	},
	
	/**
	 * @method hidden static
	 */
	IsSupported: function() {
	
		return f_domClientStorage._Storage!=null;
	}
}

var __members = {
	f_getStorageType: function() {
		return f_clientStorage.DOM_STORAGE_TYPE;
	},
	f_getStorageMaxSize: function() {
		return f_domClientStorage._STORAGE_MAX_SIZE;
	},
	f_get: function(name) {
		f_core.Assert(typeof(name)=="string", "f_domClientStorage.f_get: Invalid name parameter ("+name+")");

		var item=f_domClientStorage._Storage.getItem(name);
		if (!item) {
			return null;
		}
		
		return item.value;
	},
	f_set: function(name, value) {
		f_core.Assert(typeof(name)=="string", "f_domClientStorage.f_set: Invalid name parameter ("+name+")");
		f_core.Assert(value===null || typeof(value)=="string", "Invalid value parameter ("+value+")");
	
		var storage=f_domClientStorage._Storage;
		var item=storage.getItem(name);
		var old=null;
		if (!item) {
			if (!value) {
				return null;
			}

		} else {		
			old=item.value;
			
			if (!value) {
				storage.removeItem(name);
				return old;
			}
		}
		
		storage.setItem(name, value);
		
		return old;
	}
}

new f_class("f_domClientStorage", {
	extend: f_clientStorage,
	statics: __statics,
	members: __members
});
