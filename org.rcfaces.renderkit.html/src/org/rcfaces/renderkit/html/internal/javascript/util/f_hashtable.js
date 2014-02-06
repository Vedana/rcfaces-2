/*
 * $Id: f_hashtable.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * @class public final f_hashtable extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {

	/**
	 * @field private Object
	 */
 	_underlyingHash: undefined,
 	
 	/**
	 * @field private Number
	 */
 	_count: undefined,

	f_hashtable: function() {
		this._underlyingHash=new Object;
		this._count=0;
	},
	f_finalize: function() {
		this._underlyingHash=undefined; // Map<String, List<Object>>
//		this._count=undefined; // number
	},
	/**
	 * @method public
	 * @param Object key
	 * @param Object value
	 * @return Object old value associated to the key.
	 */
	f_put: function(key, value) {
		var hashCode = this._computeHashCode(key);

		var hash = this._underlyingHash;
		var items = hash[hashCode];
		if (!items) {
			hash[hashCode] = [{_key:key, _value:value}];
			
			this._count++;
			return undefined;
		}

		for(var i=0;i<items.length;i++){
			var item=items[i];
			
			if (item._key != key ) {
				continue;
			}

			var old=item._value;
			item._value = value;
			return old;
		}

		items.push({_key:key, _value:value});
		this._count++;

		return undefined;
	},
	/**
	 * @method private
	 * @param Object key
	 * @return String
	 */
	_computeHashCode: function(key) {
		if (typeof(key) != "object"){
			return key.toString();
		}
		
        if (typeof(key.f_hashCode)=="function") {
			return key.f_hashCode();
		}
		
		if (!key.constructor) {
			return key.toString();
		}
		
		switch(key.constructor) {
		case Array:
		case String:
		case Number:
		case Date:
			return key.toString();

		default:
			var stringKey = 'k:';
			for(var item in key) {
				stringKey += item[0];
			}
			return stringKey;
		}
		
		return -1;
	},

	/**
	 * @method public
	 * @return void
	 */
	f_clear: function() { 
		this._underlyingHash = {}; 
		this._count=0;
	},

	/**
	 * @method public
	 * @param Object key
	 * @return Object
	 */
	f_remove: function(key) {
		if (!this._count) {
			return undefined;
		}
		
		var stringKey = this._computeHashCode(key);

		var items = this._underlyingHash[stringKey];
		if(!items) {
			return undefined;
		}

		for(var i=0;i < items.length;i++){
			var item=items[i];
			if( item._key != key ) {
				continue;
			}
			
			var old=item._value;
			item.splice(i, 1);
			this._count--;
			return old;
		}
		
		return undefined;
	},
	/**
	 * @method public
	 * @param Object key
	 * @return Object
	 */
	f_get: function(key) {
		if (!this._count) {
			return undefined;
		}

		var stringKey = this._computeHashCode(key);

		var items = this._underlyingHash[stringKey];
		if(!items) {
			return undefined;
		}

		for(var i=0;i<items.length;i++){
			var item=items[i];
			if(item._key != key) {
				continue;
			}
			
			return item._value;
		}
		
		return undefined;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isEmpty: function() {
		return !this._count;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getSize: function() {
		return this._count;
	},
	/**
	 * @method public
	 * @return any[]
	 */
	f_keySet: function() {
		var hash = this._underlyingHash;
	
		var ret=new Array;	
		for(var itemHash in hash) {
			var items = hash[itemHash];
	
			for(var i=0;i<items.length;i++){
				ret.push(items[i]);
			}
		}
	
		return ret;
	},
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_hashtable size='"+this._count+"']";
	}
}

new f_class("f_hashtable", {
	members: __members
});