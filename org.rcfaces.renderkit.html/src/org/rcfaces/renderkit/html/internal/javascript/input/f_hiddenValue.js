/*
 * $Id: f_hiddenValue.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * Hidden value component.
 *
 * @class public f_hiddenValue extends f_eventTarget, fa_serializable, fa_clientData
 */
 
 var __members = {
		 
	 /**
	 * Returns the idenfiant of the component.
	 * 
	 * @method public
	 * @return String Identifier
	 */
	f_getId: function() {
		return this.id;
	},
		 
	/**
	 * Returns the value.
	 *
	 * @method public
	 * @return String Or <code>null</code> if not defined !
	 */
	f_getValue: function() {
		return this.value;
	},
	/**
	 * Set the value.
	 *
	 * @method public
	 * @param optional String value
	 * @return void
	 */
	f_setValue: function(value) {
		if (value===undefined) {
			value="";
		}
		
		if (value!==null && value!==undefined && typeof(value)!="string") {
			value=String(value);
		}
	
		var oldValue=this.value;	
		this.value=value;
		
		if (this.f_performPropertyChange) {
			this.f_performPropertyChange(f_prop.VALUE, value, oldValue);
		}
	}
};

new f_class("f_hiddenValue", {
	extend: f_eventTarget,
	aspects: [fa_serializable, fa_clientData],
	members: __members
} );