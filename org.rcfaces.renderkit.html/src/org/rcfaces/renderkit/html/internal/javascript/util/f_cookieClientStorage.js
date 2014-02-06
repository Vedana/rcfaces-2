/*
 * $Id: f_cookieClientStorage.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 *
 * @class hidden f_cookieClientStorage extends f_clientStorage
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {
	/**
	 * @field private static final String
	 */
	_COOKIE_PREFIX: "_CM_",
		
	/**
	 * @field private static final Number
	 */
	_STORAGE_MAX_SIZE: 4096
}

var __members = {
	f_getStorageType: function() {
		return f_clientStorage.COOKIE_STORAGE_TYPE;
	},
	f_getStorageMaxSize: function() {
		return f_cookieClientStorage._STORAGE_MAX_SIZE;
	},
	f_get: function(name) {
		f_core.Assert(typeof(name)=="string", "Invalid name parameter ("+name+")");

		name=f_cookieClientStorage._COOKIE_PREFIX+name;
		
		return f_core.GetCookieValue(name);
	},
	f_set: function(name, value) {
		f_core.Assert(typeof(name)=="string", "Invalid name parameter ("+name+")");
		f_core.Assert(value===null || typeof(value)=="string", "Invalid value parameter ("+value+")");
	
		name=f_cookieClientStorage._COOKIE_PREFIX+name;
	
		var old=f_core.GetCookieValue(name);
	
		f_core.SetCookieValue(name, value);
		
		return old;
	}
}

new f_class("f_cookieClientStorage", {
	extend: f_clientStorage,
	statics: __statics,
	members: __members
});
