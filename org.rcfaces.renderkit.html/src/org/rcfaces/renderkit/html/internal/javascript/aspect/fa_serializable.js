/*
 * $Id: fa_serializable.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect serializable. 
 *
 * @aspect hidden abstract fa_serializable
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
	
	/**
	 * @field private Boolean
	 */
	 _noPropertyUpdates: undefined,
	 
	/**
	 * @field private Object
	 */
	 _properties: undefined,

	f_finalize: function() {
		this._properties = undefined;  // Map<string, Object>
//		this._noPropertyUpdates=undefined; // boolean
	},

	/** 
	 * @method hidden
	 */
	f_ignorePropertyChanges: function() {
		this._noPropertyUpdates=true;
	},
	/**
	 * 
	 * @method protected
	 * @return String
	 */
	f_getProperty: function(name) {
		f_core.Assert(typeof(name)=="string", "fa_serializable.f_getProperty: Invalid name parameter '"+name+"'");
		if (!this._properties) {
			return undefined;
		}
		
		return this._properties[name];
	},
	/**
	 * 
	 * @method protected
	 * @return void
	 */
	f_setProperty: function(name, value, isList, listSep) {
		f_core.Assert(typeof(name)=="string", "fa_serializable.f_setProperty: Invalid name parameter '"+name+"'");

		if (this.fa_componentUpdated===false || this._noPropertyUpdates) {
			f_core.Debug(fa_serializable, "f_setProperty: Ignore set property (component is not updated !)");
			return;
		}

		if (isList) {		
			if (!value || !value.length) {
				f_core.Debug(fa_serializable, "f_setProperty: No values to set for property '"+name+"'. (value='"+value+"')");
				return;
			}
			
			if (!listSep) {
				listSep='\x01';
			}
			
			var values=undefined;
			if (value instanceof Array) {
				values=value.join(listSep);

			} else {
				for (var propertyName in value) {
					if (!values) {
						values=[propertyName];
						continue;
					}
					
					values.push(propertyName);
				}
				
				if (values && values.length>1) {
					values=values.join(listSep);					
				}
			}
		
			if (!values) {
				f_core.Debug(fa_serializable, "f_setProperty: No values to set for property '"+name+"'. (value='"+value+"')");
				return;
			}
			
			value=values;
		}
		
		f_core.Assert(typeof(value)=="string" 
			|| typeof(value)=="number" 
			|| typeof(value)=="boolean" 
			|| (value instanceof Array) 
			|| (value instanceof Date)
			|| (f_class.IsClassDefined("f_time") && (value instanceof f_time))
			|| (f_class.IsClassDefined("f_period") && (value instanceof f_period))
			|| value===null || value===undefined, "fa_serializable.f_setProperty: Invalid value '"+value+"'.");
		
		var properties=this._properties;
		if (!properties) {
			if (value===undefined) {
				return;
			}
			
			properties=new Object;
			this._properties = properties;

			f_core.Debug(fa_serializable, "f_setProperty: Create property map for object '"+this.id+"'.");
		}
		
		f_core.Info(fa_serializable, "f_setProperty: Set property '"+name+"' to '"+value+"' (type='"+typeof(value)+"').");
		
		if (this._kclass._classLoader._serializing) {
			// Pas d'evenement dans ce cas !
			
			properties[name] = value;
			return;
		}
		
		var oldValue=properties[name];		
		properties[name] = value;
		
		var performPropertyChange=this.f_performPropertyChange;
		if (performPropertyChange) {			
			performPropertyChange.call(this, name, value, oldValue);
		}
	},
	/**
	 *
	 *
	 * @method hidden
	 * @return String Serialized form.
	 */
	f_serialize0: function() {
		var serialize=this.f_serialize;
		if (serialize) {
			try {
				serialize.call(this);
				
			} catch (x) {
				f_core.Error(fa_serializable, "f_serialize0: Can not serialize component '"+this.id+"'.", x);
			}
		}
		
		var p = this._properties;
		if (!p) {	
			return null;
		}

		return f_core.EncodeObject(p, ",");
	},
	
	/**
	 * @method protected abstract optional
	 */
	fa_componentUpdated: f_class.OPTIONAL_ABSTRACT,
	
	/**
	 * @method protected abstract optional
	 */
	f_performPropertyChange: f_class.OPTIONAL_ABSTRACT,
	
	/**
	 * @method protected abstract optional
	 */
	f_serialize: f_class.OPTIONAL_ABSTRACT
};

new f_aspect("fa_serializable", {
	members: __members
});
