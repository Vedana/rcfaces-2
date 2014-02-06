/*
 * $Id: f_resourceBundle.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * @class public final f_resourceBundle extends Object
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 */

var __statics = {
	/**
	 * @field private static 
	 */
	_Resources: undefined,
	
	/**
	 * @field private static 
	 */
	_Loading: undefined,
	
	/**
	 * @field private static 
	 */
	_Preloaded: undefined,

	/**
	 * @method public static final
	 * @param String name Name of resourceBundle. (can be a f_class !)
	 * @param hidden boolean create Create ResourceBundle if not found !
	 * @return f_resourceBundle
	 */
	Get: function(name, create) {
		f_core.Assert(name, "f_resourceBundle.Get: Name parameter is invalid: "+name);
	
		if ((name instanceof f_class) || (name instanceof f_aspect) || typeof(name.f_getName)=="function") {
			name=name.f_getName();
		}
		
		f_core.Assert(typeof(name)=="string", "Name of resourceBundle must be a string or f_class ! ("+name+")");
	
		if (!name) {
			return null;
		}
	
		var resources=f_resourceBundle._Resources;
		if (!resources) {
			resources=new Object();
			f_resourceBundle._Resources=resources;
		}
	
		var resource=resources[name];
		if (resource) {
			return resource;
		}
		
		var preloadedValues=f_resourceBundle._Preloaded;
		if (preloadedValues) {
			var pv=preloadedValues[name];
			if (pv) {
				delete preloadedValues[name];
			
				resource=new f_resourceBundle(name);
				resources[name]=resource;
				
				resource._setAll(pv);
				
				return resource;
			}
		}
		
		if (!create) {
			f_core.Debug(f_resourceBundle, "Can not find resourceBundle '"+name+"'.");
			return resource;
		}
		
		resource=new f_resourceBundle(name);
		resources[name]=resource;
		return resource;
	},
	
	/**
	 * @method public static final 
	 * @param String name Name of resourceBundle. (can be a f_class !)
	 * @param Object values
	 * @return void
	 */
	Define: function(name, values) {
		var resourceBundle=f_resourceBundle.Get(name, true);
		
		f_core.Debug(f_resourceBundle, "Define resourceBundle for '"+resourceBundle._name+"' with values '"+values+"'.");
		
		resourceBundle._putAll(values);
	},
	
	/**
	 * @method public static final
	 * @param String name Name of resourceBundle. (can be a f_class !)
	 * @param Object values
	 * @return void
	 */
	Define2: function(name, values) {
		var p=f_resourceBundle._Preloaded;
		if (!p) {
			p=new Object;
			f_resourceBundle._Preloaded=p;
		}		
	
		p[name]=values;
	},
	
	/**
	 * @method static final hidden
	 * @param String name Name of baseName of a previous request.
	 * @param Object values Object or an Array
	 * @return void
	 */
	DefineLoaded: function(baseName, values) {
		f_core.Assert(f_resourceBundle._Loading, "Resource bundle base='"+name+"' is not requested !");
	
		var loading=f_resourceBundle._Loading;
	
		var bundleName=loading[baseName];
		f_core.Assert(bundleName, "Resource bundle bundleName='"+bundleName+"' is not known !");
	
		loading[baseName]=undefined;
	
		f_core.Debug(f_resourceBundle, "Loaded: baseName='"+baseName+"' bundleName='"+bundleName+"' values="+values);
			
		f_resourceBundle.Define(bundleName, values);
	},
	
	/**
	 * @method static final hidden
	 * @param String bunddleName Name of resourceBundle.
	 * @param String baseName
	 * @return void
	 */
	Load: function(bundleName, baseName, url, override) {
		f_core.Assert(override || f_resourceBundle.Get(bundleName)==null, "Resource bundle '"+bundleName+"' is already defined !");
	
		var loading=f_resourceBundle._Loading;
		if (!loading) {
			loading=new Array;
			f_resourceBundle._Loading=loading;
		}
	
		f_core.Debug(f_resourceBundle, "Load bundleName='"+bundleName+"' baseName='"+baseName+"' located at url '"+url+"'.");
	
		loading[baseName]=bundleName;
		
		document.write("<SCRIPT type=\"text/javascript\" charset=\"UTF-8\" src=\""+url+"\"></SCRIPT>");
	},
	
	/**
	 * INTERNAL USE: Methodes pour le multiWindow
	 * 
	 * @method hidden static
	 * @return void
	 */
	CopyResourcesToChild: function(newResources) {
	
		// On force l'initialisation des resources ...
		for(var name in f_resourceBundle._Preloaded) {
			f_resourceBundle.Get(name);
		}

		var resources=f_resourceBundle._Resources;
		for(var name in resources) {
			newResources[name]=resources[name];
		}
	},
	/**
	 * @method hidden static
	 * @return Object
	 */
	PrepareParentCopy: function() {
		var newResources=new Object();
		f_resourceBundle._Resources=newResources;		
		
		return newResources;
	},	
	
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_resourceBundle._Resources=undefined; // Map<String, f_resourceBundle>
//		f_resourceBundle._Preloaded=undefined; // Map<String, Map<String, String>>
//		f_resourceBundle._Loading=undefined; // Map<String, String>
	}
}


var __members = {	
	/**
	 * @method hidden
	 * @param String name
	 */
	f_resourceBundle: function(name) {
		this._name=name;
	},
	
	/**
	 * Search a value associated to a property.
	 *
	 * @method public
	 * @param String key Key of property.
	 * @param optional String defaultValue Default value if key is not found.
	 * @return Object
	 */
	f_get: function(key, defaultValue) {
		f_core.Assert(typeof(key)=="string", "Key must be a string !");
	 	
		var properties=this._properties;
		if (!properties) {
			if (defaultValue!==undefined) {
				return defaultValue;
			}
			f_core.Error(f_resourceBundle, "No keys for resourceBundle '"+this._name+"'.");
			return "??"+key+"??";
		}
		
		var message=properties[key];
		if (message===undefined) {
			if (defaultValue!==undefined) {
				return defaultValue;
			}
			f_core.Error(f_resourceBundle, "Unknown key '"+key+"' for resourceBundle '"+this._name+"'.");
			return "??"+key+"??";
		}
		
		return message;
	},
	
	/**
	 * Search a value associated to a property.
	 *
	 * @method public
	 * @param String key Key of property.
	 * @param optional any... params Parameters which will be formatted into the string associated to the key.
	 *                 The Nth parameter will replace the '{n}' substring. (First parameter: {0}; Second parameter {1} ...)
	 * @return Object
	 */
	f_format: function(key, params) {
	
		if (arguments.length<2) {
			return this.f_formatParams(key);
		}
			
		var p=f_core.PushArguments(null, arguments, 1);
		
		return this.f_formatParams(key, p);
	},
	
	
	/**
	 * Search a value associated to a property.
	 *
	 * @method public
	 * @param String key Key of property.
	 * @param optional any[] params Parameters which will be formatted into the string associated to the key.
	 *                 The Nth element in the array will replace the '{n}' substring. (First element: {0}; Second element {1} ...)
	 * @return Object
	 */
	f_formatParams: function(key, params, defaultValue) {
	 	f_core.Assert(params==null || params===undefined || (params instanceof Array) || typeof(params)=="object", "Params parameter is invalid. (["+typeof(params)+"] "+params+").");
		
		var message=this.f_get(key, defaultValue);
		if (message===undefined) {
			if (defaultValue===undefined) {
				f_core.Error(f_resourceBundle, "Unknown key '"+key+"' for resourceBundle '"+this._name+"'.");
				return "??"+key+"??";
			}
	
			message=defaultValue;
		}
		
		if (!message) {
			return message;
		}
	
		f_core.Debug(f_resourceBundle, "Format '"+message+"' with '"+params+"'.");
		
		return f_core.FormatMessage(message, params);
	},
		
	/**
	 * @method private
	 * @return void
	 */
	_putAll: function(values) {
		var properties=this._properties;
		if (!properties) {
			properties=new Object();
			this._properties=properties;
		}
				
		if (values instanceof Array) {
			for(var i=0;i<values.length;) {
				var key=values[i++];
				var value=values[i++];
				
				properties[key]=value;
			}
			
			return;
		}
		
		for(var name in values) {
			properties[name]=values[name];
		}
	},
	/**
	 * @method private
	 * @return void
	 */
	_setAll: function(values) {
		this._properties=values;
	},
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_resourceBundle name="+this._name+"]";
	}
}

new f_class("f_resourceBundle", {
	statics: __statics,
	members: __members
});
