/*
 * $Id: f_userDataClientStorage.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 *
 * @class hidden f_userDataClientStorage extends f_clientStorage
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {
	
	/**
	 * @field private static final Number
	 */
	_STORAGE_MAX_SIZE: 1048576,
	
	/**
	 * @field private static final String
	 */
	_STORAGE_NAME: "camelia",
	
	/**
	 * @field private static
	 */
	_UserDataComponent: undefined,
	
	/**
	 * @field private static boolean
	 */
	_NeedSave: undefined,
	
	/**
	 * @field private static boolean
	 */
	_Exiting: undefined,
		
	/**
	 * @method protected static
	 */
	Finalizer: function() {
		f_userDataClientStorage._Exited=true;
		
		var userDataComponent=f_userDataClientStorage._UserDataComponent;
		if (!userDataComponent) {
			return;
		}
		
		f_userDataClientStorage._Save();

		f_userDataClientStorage._UserDataComponent=undefined; // HTMLElement
	},
	/**
	 * @method hidden static
	 */
	IsSupported: function() {			
		return f_core.IsInternetExplorer();
	},
	/**
	 * @method private static
	 */
	_GetStorageComponent: function() {
		
		var userDataComponent=f_userDataClientStorage._UserDataComponent;
		if (userDataComponent) {
			return userDataComponent;
		}
		
		userDataComponent=document.createElement("div");
		userDataComponent.className="f_userDataClientStorage";
		
		f_core.AppendChild(document.body, userDataComponent);
		
		if (!f_userDataClientStorage._Exited) {
			f_userDataClientStorage._UserDataComponent=userDataComponent;
		}
		
		try {
			userDataComponent.load(f_userDataClientStorage._STORAGE_NAME);
			
		} catch (x) {
			f_core.Error(f_userDataClientStorage, "Can not load storage", x);
		}
		
		return userDataComponent;
	},
	/**
	 * @method private static
	 */
	_Save: function(userDataComponent) {
		if (!f_userDataClientStorage._NeedSave) {
			return;
		}
		
		if (!userDataComponent) {
			userDataComponent=f_userDataClientStorage._UserDataComponent;
		
			if (!userDataComponent) {
				return;
			}
		}
						
		try {
			userDataComponent.save(f_userDataClientStorage._STORAGE_NAME);
			
			f_userDataClientStorage._NeedSave=undefined;
			
		} catch (x) {
			f_core.Error(f_userDataClientStorage, "Can not save storage", x);
		}
	}
}

var __members = {
	f_getStorageType: function() {
		return f_clientStorage.USER_DATA_STORAGE_TYPE;
	},

	f_getStorageMaxSize: function() {
		return f_userDataClientStorage._STORAGE_MAX_SIZE;
	},
	
	f_get: function(name) {
		f_core.Assert(typeof(name)=="string", "Invalid name parameter ("+name+")");
	
		var userDataComponent=f_userDataClientStorage._GetStorageComponent();
		if (!userDataComponent) {
			return null;
		}
		
		return userDataComponent.getAttribute(name);
	},
	f_set: function(name, value) {
		f_core.Assert(typeof(name)=="string", "Invalid name parameter ("+name+")");
		f_core.Assert(value===null || typeof(value)=="string", "Invalid value parameter ("+value+")");
	
		var userDataComponent=f_userDataClientStorage._GetStorageComponent();
		if (!userDataComponent) {
			return null;
		}
		
		var old=userDataComponent.getAttribute(name);
		
		f_userDataClientStorage._NeedSave=true;
		
		userDataComponent.setAttribute(name);
		
		if (f_userDataClientStorage._Exiting) {
			f_userDataClientStorage._Save(userDataComponent);
		}
		
		return old;		
	}
}

new f_class("f_userDataClientStorage", {
	extend: f_clientStorage,
	statics: __statics,
	members: __members
});
