/*
 * $Id: fa_value.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Value
 *
 * @aspect public abstract fa_value
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._value=undefined; // string
	},
	*/
	/**
	 * Returns the component's value
	 *
	 * @method public
	 * @return String or <code>null</code> if not defined !
	 */
	f_getValue: function() {
		return this.f_getInternalValue();
	},
	/**
	 * Returns <code>true</code> if the component is value
	 *
	 * @method protected
	 * @return String or <code>null</code> if not defined !
	 */
	f_getInternalValue: function() {	
		var value=this._value;
		if (value!==undefined) {
			return value;
		}
		
		value=f_core.GetAttributeNS(this, "value", null);
		this._value=value;
		
		return value;
	},
	/**
	 * Set value state.
	 *
	 * @method public
	 * @param String value
	 * @return void
	 */
	f_setValue: function(value) {
		f_core.Assert(value===null || typeof(value)=="string", "Value parameter must be a string or null ! ("+value+")");

		if (this.f_getValue()==value) {
			return;
		}
		
		this._value = value;
	
		if (this.fa_updateValue) {
			this.fa_updateValue(value);
		}
	
		this.f_setProperty(f_prop.VALUE, value);
	},
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateValue: f_class.OPTIONAL_ABSTRACT,

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT
}

new f_aspect("fa_value", {
	members: __members
});
