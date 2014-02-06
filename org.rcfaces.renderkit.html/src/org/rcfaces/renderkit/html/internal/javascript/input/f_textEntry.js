/*
 * $Id: f_textEntry.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * f_textEntry class
 *
 * @class public f_textEntry extends f_abstractEntry
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __members = {
	/*
	f_textEntry: function() {
		this.f_super(arguments);
	},
	*/
	/**
	 * @method protected
	 * @return void
	 */
	f_initializeOnFocus: function() {
		this.f_super(arguments);
				
		if (f_class.IsClassDefined("f_clientValidator")) {
			f_clientValidator.InstallValidator(this);
		}
	},
	f_finalize: function() {
		// this._autoTab=undefined;  // boolean
		// this._requiredInstalled=undefined; // boolean

		var validator=this._validator;
		if (validator) {
			this._validator=undefined;
			f_classLoader.Destroy(validator);
		}
		
		this.f_super(arguments);
	},
	f_update: function() {
		
		if (this.f_isAutoTab()) {
			this._installAutoTab();
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getMaxTextLength: function() {
		return this.f_getInput().maxLength;
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @param Object value
	 * @return Boolean If value is recognized.
	 */
	f_setValue: function(value) {
		if (typeof(value)=="string") {
			var maxTextLength=this.f_getMaxTextLength();
			
			if (maxTextLength>0 && value.length>maxTextLength) {
				value=value.substring(0, maxTextLength);
			}
		}
		
		return this.f_super(arguments, value);
	},
	/**
	 * @method public
	 * @return Boolean Returns <code>true</code> if auto tab facility is enabled.
	 */
	f_isAutoTab: function() {
		var autoTab=this._autoTab;
		if (autoTab!==undefined) {
			return autoTab;
		}
				
		autoTab=!!f_core.GetBooleanAttributeNS(this,"autoTab");
		this._autoTab=autoTab;
				
		return autoTab;
	},
	/**
	 * @method protected
	 * @param Event evt
	 * @return void
	 */
	f_performSelectionEvent: function(evt) {
		if (this.f_isDisabled()) {
			evt.f_preventDefault();
			return;
		}
		
		if (this.f_isActionListEmpty(f_event.SELECTION)) {
			return;
		}
		
		var jsEvent=evt.f_getJsEvent();
		if (!jsEvent) {
			return;
		}
		
		var code=jsEvent.keyCode;
	
		if (code!=f_key.VK_RETURN && code!=f_key.VK_ENTER) { // RETURN ou ENTER
			return;
		}

		evt.f_preventDefault();

		f_core.Debug(f_textEntry, "f_performSelectionEvent: Fire SELECTION event");
		
		this.f_fireEvent(f_event.SELECTION, jsEvent);
		
		return false;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isAutoCompletion: function() {
		var input=this.f_getInput();
		
		return f_core.GetAttribute(input, "autoComplete")!="off";	
	},
	/*
	 * @method public
	 * @return optional Boolean complete
	 * @return void
	 *
	f_setAutoCompletion: function(complete) {
		if (complete===undefined) {
			complete=true;
		}
		
		if (complete==this.f_isAutoCompletion()) {
			return;
		}
		
		var input=this.f_getInput();
	
		input.setAttribute("autoComplete", (complete)?"on":"off");
		
		this.f_setProperty(f_prop.AUTO_COMPLETION, complete);
	},
	*/
	/**
	 * @method private
	 * @return void
	 */
	_installAutoTab: function() {
		f_class.IsClassDefined("f_clientValidator", true);
		
		f_core.Debug(f_textEntry, "_installAutoTab: Install autotab processor to component '"+this.id+"' !");
		
		var validator = f_clientValidator.GetValidator(this);
		if (!validator) {
			validator = f_clientValidator.f_newInstance(this);
		}
		
		validator.f_addProcessor(f_vb.Processor_autoTab);
	},
	/**
	 * @method protected
	 * @return void
	 */
	fa_updateRequired: function() {
		this.f_updateStyleClass();

		if (this._requiredInstalled) {
			return;
		}
		
		this._requiredInstalled=true;

		f_class.IsClassDefined("f_clientValidator", true);
	
		f_core.Debug(f_textEntry, "fa_updateRequired: Install required behavior to component '"+this.id+"' !");
		
		var validator = f_clientValidator.GetValidator(this);
		if (!validator) {
			validator = f_clientValidator.f_newInstance(this);
		}
		
		validator.f_addBehavior(f_vb.Behavior_required);
	},
	f_serializeValue: function() {
		var validator=this._validator;
		if (validator) {
			var value=validator.f_serializeValue();
			
			this.f_setProperty(f_prop.TEXT, value);
			return ;
		}
		
		this.f_super(arguments);
	}
};

new f_class("f_textEntry", {
	extend: f_abstractEntry,
	members: __members
});
