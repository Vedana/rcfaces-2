/*
 * $Id: f_input.js,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * f_input class.
 *
 * @class f_input extends f_component, fa_message, fa_tabIndex, fa_focusStyleClass
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) & Joel Merlin
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
 
var __statics = {
	/**
	 * @field private static
	 */
	_REDIRECT_INPUT: new Object,
	
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {
		var redirect=f_input._REDIRECT_INPUT;
		
 		redirect[f_event.BLUR]=true;
 		redirect[f_event.FOCUS]=true;
 		redirect[f_event.KEYPRESS]=true;
 		redirect[f_event.KEYDOWN]=true;
 		redirect[f_event.KEYUP]=true;
	}
};
 
var __members = {

	f_input: function() {
		this.f_super(arguments);
		
		if (false) {
			var self=this;
			this.f_insertEventListenerFirst(f_event.FOCUS, function(event) {
				self.f_removeEventListener(f_event.FOCUS, arguments.callee);
				
				self.f_initializeOnFocus();
				
				return self.f_fireEvent(event);
			});
		} else {
			this.f_initializeOnFocus();
		}	
		
		if (f_core.IsWebkit()) {
			this.f_setInitEventReturn(f_event.KEYPRESS, true);
		}
		
		if (f_core.IsDebugEnabled(f_input)) {
			var input=this.f_getInput();
			
			f_core.Debug(f_input, "f_input: Input associated to component '"+this.id+"' is id='"+input.id+"', tagName="+input.tagName+", name='"+input.name+"'.");
		}
	},
	f_finalize: function() {
//		this._hasFocus=undefined; // boolean
 		this._currentMessage=undefined; // f_message
		
		this._validator=undefined;
		
		this.f_super(arguments);

		// On efface l'INPUT aprés car le _input peut être reinitialisé par les classes parentes !
		var input=this._input;
		if (input) {
			this._input=undefined;

			if (input!=this) {
				f_core.VerifyProperties(input);
			}
		}
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		return this.f_getInput();	
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	f_initializeOnFocus: function() {						
		var focusStyleClass=this.f_getFocusStyleClass();
		if (focusStyleClass) {
			this.f_insertEventListenerFirst(f_event.FOCUS, this._focusFocusEvent);
			this.f_insertEventListenerFirst(f_event.BLUR, this._focusBlurEvent);
		}
	},
	/**
	 * 
	 * @method protected final
	 * @return HTMLElement
	 */
	f_getInput: function() {
		var input=this._input;
		if (input) {
			return input;
		}

		input=this.f_initializeInput();
		if (!input) {
			throw new Error("Can not find input associated to component '#"+this.id+"."+this.className+"' class='"+this.f_getClass()+"'.");
		}
		this._input=input;
		
		return input;
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		return this.f_getInput();
	},
	
	/**
	 * 
	 * @method protected
	 * @return HTMLElement
	 */
	f_initializeInput: function() {
		return this;
		/*
		var input=this.ownerDocument.getElementById(this.id+XXXXX._INPUT_ID_SUFFIX);
		if (input) {
			return input;
		}		

		return input;
		*/
	},
	/**
	 * @method public
	 * @return void
	 */
	f_setFocus: function() {
		if (!f_core.ForceComponentVisibility(this)) {
			return;
		}

		if (this.f_isDisabled && this.f_isDisabled()) {
			return;
		}
		if (!this.tabIndex) {
		//	this._tabIndex = 0;	// Default tabbing   NON ???
		}
		
		var input=this.f_getInput();
	
		f_core.Debug(f_input, "f_setFocus: Input '"+this.id+"' component="+input+" componentId="+input.id);
		
		try {
			input.focus();
			
		} catch (x) {
			f_core.Error(f_input, "f_setFocus: Error while setting focus to '"+input.id+"'.", x);
		}
		
		if (input.tagName.toLowerCase()=="textarea") {
			input.select();
			
		} else {  		
			var type=input.type;
			if (type) {
				switch(type.toUpperCase()) {
				case "TEXT":
					input.select();
					break;					
				}
			}
		}
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		var validator=this._validator;
		if (validator) {
			return validator.f_getValue();
		}
	
		return this.f_getInput().value;
	},
	/**
	 * Returns the text associated to the input.
	 * 
	 * @method public
	 * @param String text The text of the input.
	 * @return void
	 */
	f_setText: function(text) {
		var input=this.f_getInput();
		
		if (text == input.value) {
			return;
		}
		
		input.value = text;
		this.f_setProperty(f_prop.TEXT, text);

		var validator=this._validator;
		if (validator) {
			validator.f_updateValue(text);
		}		
	},
	/**
	 * Returns the disabled state. 
	 *
	 * @method public
	 * @return Boolean Returns <code>true</code> if the input is disabled.
	 */
	f_isDisabled: function() {
		return this.f_getInput().disabled;
	},
	/**
	 * Set the disabled state of this component.
	 *
	 * @method public
	 * @param Boolean disabled Set disabled state.
	 * @return void
	 */
	f_setDisabled: function(disabled) {
		this.f_getInput().disabled = disabled;
//		this.disabled = disabled;
		this.f_updateDisabled(disabled);
		this.f_setProperty(f_prop.DISABLED, disabled);
	},
	/**
	 * update the component according to its disabled state
	 * 
	 * @method protected
	 * @param Boolean disabled
	 * @return void
	 */
	f_updateDisabled: function(disabled) {
		this.f_updateStyleClass();
		
		var tabIndex = this.fa_getTabIndex(); // Initialisation eventuelle
		var tabIndexElement=this.fa_getTabIndexElement();
		
		tabIndexElement.tabIndex=(disabled)?(-1):tabIndex;
	},
	/**
	 * Returns the read only state.
	 * 
	 * @method public
	 * @return Boolean Returns <code>true</code> if the component is in read only mode.
	 */
	f_isReadOnly: function() {
		return (this.f_getInput().readOnly == true);
	},
	/**
	 * @method public
	 * @param Boolean set Set Read-only.
	 * @return void
	 */
	f_setReadOnly: function(set) {
		var input=this.f_getInput();
		if (input.readOnly == set) {
			return;
		}
	
		this.f_setProperty(f_prop.READONLY,set);
		input.readOnly = set;
				
		this.f_updateReadOnly();
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateReadOnly: function(set) {	
		this.f_updateStyleClass();
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateStyleClass: function(postSuffix) {
		var suffix="";

		if (this.f_isDisabled()) {
			suffix+="_disabled";

		} else if (this.f_isReadOnly()) {
			suffix+="_readOnly";
		}
		
		if (postSuffix) {
			suffix+=postSuffix;
		}
	
		var claz=this.f_computeStyleClass(suffix);
		if (this.className!=claz) {
			this.className=claz;
		}
	},	
	/*
	f_update: function() {
		/ * C'est déjà dans le constructeur de fa_messages ...
			this.f_performMessageChanges();
		* /
				
		return this.f_super(arguments);	
	},
	*/
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @return Object The value associated.
	 */
	f_getValue: function() {
		var validator=this._validator;
		if (validator && validator.f_getConverter()) {			
			return validator.f_getConvertedValue();
		}		
		
		return this.f_getInput().value;
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @param Object value
	 * @return Boolean If value is recognized.
	 */
	f_setValue: function(value) {
		var validator=this._validator;
		if (validator && validator.f_getConverter()) {
			if (validator.f_setConvertedValue(value)) {
				return true;
			}
		}		

		if (typeof(value)=="number") {
			value=String(value);

		} else if (typeof(value)!="string") {
			f_core.Info(f_input, "f_setValue: Invalid value: "+value + " (typeof=" + typeof(value) +")");
			return false;
		}

		f_core.Debug(f_input, "f_setValue: Value="+value);
		
		if (validator) {
			return validator.f_updateValue(value);
		}
		
		this.f_getInput().value=value;
		
		return true;
	},
	f_fireEvent: function(type, evt, item, value, selectionProvider, detail, stage) {
		if (type==f_event.CHANGE) {			
			if (this.f_isReadOnly() || this.f_isDisabled()) {
				return false;
			}
			
			if (!value) {
				value=this.f_getValue();
			}
		}	
		
		return this.f_super(arguments, type, evt, item, value, selectionProvider, detail, stage);
	},
	/**
	 * @method public
	 * @return Boolean Returns <code>true</code> if value of the component has been validated.
	 */
	f_isValid: function() {
		try {
			var messageContext=f_messageContext.Get(this);

			if (messageContext) {
				messageContext.f_clearMessages(this);
			}
		} catch (x) {
			// f_messageContext n'existe peut etre pas !
			f_core.Debug(f_input, "f_isValid: No message context !", x);
		}
		
		return this.f_validValue();
	},
	/**
	 * @method protected
	 * @return Boolean Returns <code>true</code> if value of the component has been validated.
	 */
	f_validValue: function() {
		var validator=this._validator;
		if (!validator) {
			return true;
		}
		
		return validator.f_isValidValue();		
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_computeStyleClass: function(suffix) {	
		var styleClass=this.f_super(arguments, suffix);

		if (this._hasFocus && !this.f_isDisabled()) {
			var focusStyleClass=this.f_getFocusStyleClass();

			if (focusStyleClass) {
				styleClass+=" "+focusStyleClass;
			}
		}
		
		var msg=this._currentMessage;
		if (msg) {
			var severity=msg.f_getSeverity();
			
			var severityClass=this.f_getStyleClassFromSeverity(severity);
			
			if (!severityClass && severity>=f_messageObject.SEVERITY_ERROR) {
				severityClass=this.f_getMainStyleClass()+"_error f_input_error";
			}
			
			if (severityClass) {
				styleClass+=" "+severityClass;
			}
		}

		return styleClass;
	},
	f_performMessageChanges: function() {	
		var messages=f_messageContext.ListMessages(this);
		
		var msg=undefined;
		for(var j=0;j<messages.length;j++) {
			var m=messages[j];
			
			if (!msg || msg.f_getSeverity()<m.f_getSeverity()) {
				msg=m;
			}
		}
		
		f_core.Debug(f_input, "f_performMessageChanges: Change message to '"+msg+"' for component "+this.id+".");
		
		var currentMessage=this._currentMessage;
		if (currentMessage) {
			if (msg==currentMessage || currentMessage.f_equals(msg)) {
				return;
			}
		}
		
		f_core.Assert(typeof(msg)=="object" || msg===undefined, "f_input.f_performMessageChanges: Invalid message object ("+msg+").");
		this._currentMessage=msg;
		
		this.f_updateStyleClass();
	},
	/**
	 * @method private
	 */
	_focusFocusEvent: function() {
		this._hasFocus=true;
		
		this.f_updateStyleClass();
	},
	/**
	 * @method private
	 */
	_focusBlurEvent: function() {
		this._hasFocus=undefined;
		
		this.f_updateStyleClass();
	},
	/**
	 * @method protected
	 */
	f_setDomEvent: function(type, target) {
	
		if (f_input._REDIRECT_INPUT[type]) {
			target=this.f_getInput();
		}
		
		this.f_super(arguments, type, target);
	},
	
	/**
	 * @method protected
	 */
	f_clearDomEvent: function(type, target) {
	
		if (f_input._REDIRECT_INPUT[type]) {
			target=this.f_getInput();
		}
		
		this.f_super(arguments, type, target);
	}	
};

new f_class("f_input", {
	extend: f_component, 
	aspects: [ fa_message, fa_focusStyleClass, fa_tabIndex, fa_basicToolTipContainer],
	members: __members,
	statics: __statics
});
