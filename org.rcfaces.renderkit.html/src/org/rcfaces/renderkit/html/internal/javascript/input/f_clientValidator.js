/*
 * $Id: f_clientValidator.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_clientValidator class
 *
 * @class hidden f_clientValidator extends f_object
 * @author Olivier Oeuillot
 * @author Joel Merlin
 */
 
var __statics = {

	/**
	 * @field hidden static final Number
	 */
	SUCCESS: 0,

	/**
	 * @field hidden static final Number
	 */
	FILTER: 1,

	/**
	 * @field hidden static final Number
	 */
	TRANSLATOR: 2,

	/**
	 * @field hidden static final Number
	 */
	CHECKER: 3,

	/**
	 * @field hidden static final Number
	 */
	FORMATTER: 4,

	/**
	 * @field hidden static final Number
	 */
	BEHAVIOR: 5,

	/**
	 * @field private static Object
	 */
	_Expressions: undefined,
	
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_clientValidator._Expressions=undefined; // Map<String,Function>
	},
	
	/**
	 * @method static hidden
	 * @return f_clientValidator Returns the validator associated to the component or <code>null</code>.
	 */
	GetValidator: function(component) {
		var validator=component._validator;
		if (!validator) {
			return null;
		}		
				
		if (validator._lazyFocus) {
			validator._lazyFocus=undefined;
			
			validator.f_installValidator();
		}		
		
		return validator;
	},
	
	/**
	 * @method static hidden
	 * @return f_clientValidator Returns the validator associated to the component or <code>null</code>.
	 */
	InstallValidator: function(component) {

		var validator=undefined;
		
		var clientValidators=f_core.GetAttributeNS(component, "clientValidator", null);
		if (clientValidators!==null) { // Il peut être "" !
			var parameters=undefined;
			if (clientValidators) {
				parameters=f_core.ParseParameters(clientValidators);
			}
			
			validator=f_clientValidator.f_newInstance(component, parameters, true);
		}
		
		var validators=f_core.GetAttributeNS(component, "validators", null);
		if (validators) {
			if (!validator) {
				validator=f_clientValidator.f_newInstance(component);
			}
			
			validator._installValidatorObjects(validators);
		}
	
		
		return validator;
	},
	/**
	 * @method private static 
	 * @param String expr
	 * @param Boolean resolveObject
	 * @return Object
	 */
	_EvalFunction: function(expr, resolveObject) {
		var expressions=f_clientValidator._Expressions;

		var f=undefined;
		if (!expressions) {
			expressions=new Object;
			f_clientValidator._Expressions=expressions;

		} else {
			f=expressions[expr];
			if (f) {
				return f;
			}
		}
	
		if (!resolveObject && expr.charAt(0)=='/') {
			// Une regexp !
			var flags=expr.lastIndexOf('/');
			if (flags>0) {
				f=new RegExp(expr.substring(1, flags), expr.substring(flags+1));
				
			} else {
				f=new RegExp(expr.substring(1));
			}
			
		} else {
			try {
				f=f_core.WindowScopeEval(expr);
				
			} catch (x) {
				f_core.Error(f_clientValidator, "_EvalFunction: Can not eval expression '"+expr+"'.", x);
				return null;
			}
		}
		
		if (resolveObject) {
			f_core.Assert(typeof(f)=="object", "f_clientValidator._EvalFunction: Invalid expression for object : '"+expr+"'='"+f+"'.");

		} else {
			f_core.Assert(typeof(f)=="function" || (f instanceof RegExp), "f_clientValidator._EvalFunction: Invalid expression for function : '"+expr+"'='"+f+"'.");
		}
			
		expressions[expr]=f;
		
		return f;
	},

	/**
	 * @method private static
	 * @param f_event evt
	 * @return Boolean
	 * @context object:this
	 */
	_OnFocus: function(evt) {
		f_core.Debug(f_clientValidator, "_OnFocus: focus on client validator ");
		if (this.f_isReadOnly()) {
			return true;
		}
	
		var validator=this._validator;
	
		validator._applyInputValue();
		validator._hasFocus = true;
		
		return true;
	},
	/**
	 * @method private static
	 * @param f_event evt
	 * @return Boolean
	 * @context object:this
	 */
	_OnBlur: function(evt) {
		f_core.Debug(f_clientValidator, "_OnFocus: focus on client validator ");

		if (this.f_isReadOnly()) {
			return true;
		}

		var validator=this._validator;

		// var bRet = 
		validator._applyAutoCheck(this._input.value, false);
		validator._applyOutputValue();
		validator._hasFocus = undefined;
		
		//return bRet;
		// On appelle les autres BLURs ...
		return true;
	},
	/**
	 * @method private static
	 * @param f_event event
	 * @return Boolean
	 * @context object:this
	 */
	_OnKeyPress: function(event) {
		var jsEvent = event.f_getJsEvent();
		var keyCode = jsEvent.keyCode;
		var charCode = jsEvent.charCode;
		
		if (jsEvent.altKey ^ jsEvent.ctrlKey) {
			// ignore les touches combinees
			return true;
		}

		var validator=this._validator;
		
		var component=validator._component;
		
		if (component.f_isReadOnly() || component.f_isDisabled()) {
			// On laisse la possibilité de traiter des callbacks fonctionnelles
			return true;
		}
		
		var keyChar;
		
		if (!charCode) {
			keyChar = String.fromCharCode(keyCode);

		} else {
			keyChar = String.fromCharCode(charCode);
		}
				
		f_core.Debug(f_clientValidator, "_OnKeyPress: keyCode="+keyCode+" charCode="+charCode+" shift="+jsEvent.shift+" ctrl="+jsEvent.ctrl+" alt="+jsEvent.alt+" keyChar="+keyChar+"("+((keyChar.length>0)?keyChar.charCodeAt(0):"")+")");
		if (f_core.IsGecko()) {
			if (keyCode>0 || jsEvent.ctrlKey) {
				return true;
			}
			keyCode=charCode;
		} else if (keyCode < 32) { // fonctionement IE et Webkit
			return true;
		} 
		
		validator.f_setInputValue(this._input.value);
		
		// Filters
		var bRet = validator._applyFilters(keyCode, keyChar);
		if (!bRet) {
			return bRet;	
		}
	
		// Translators
		var retCode = validator._applyTranslators(keyCode, keyChar);
		if (retCode != keyCode) {
			return f_clientValidator._ChangeKeyCode(this, retCode, jsEvent);
		}
		
		return bRet;
	},
	/**
	 * @method private static
	 * @context object:this
	 */
	_OnKeyUp: function(event) {
		var jsEvent = event.f_getJsEvent();

		var validator=this._validator;
		
		var component=validator._component;
		
		if (component.f_isReadOnly() || component.f_isDisabled()) {
			// On laisse la possibilité de traiter des callbacks fonctionnelles
			return true;
		}

		var keyCode = jsEvent.keyCode;
		var shift = jsEvent.shiftKey;
		var ctrl = jsEvent.ctrlKey;
		var alt = jsEvent.altKey;

		f_core.Debug(f_clientValidator, "_OnKeyUp: keyCode="+keyCode+" shift="+shift+" ctrl="+ctrl+" alt="+alt);
		
		validator.f_setInputValue(this._input.value);

		validator._applyProcessors(keyCode, shift, ctrl, alt);
	
		return true;
	},	
	/**
	 * @method private static
	 */
	_ChangeKeyCode: function(component, retCode, jsEvent) {
		if (f_core.IsInternetExplorer()) {
			jsEvent.keyCode=retCode;
			return true;
		}
	
		if (f_core.IsGecko() || f_core.IsWebkit()) {
			var ch=String.fromCharCode(retCode);
			
			// initKeyEvent() : Un trou de sécurité ??? ! 
			// C'était pourtant bien pratique !
			// bref, comme d'ab ... on bidouille ...
			// Fred : exact de la bidouille
			// JBM : et j'en rajoute !!
			if (f_core.IsGeckoDisableDispatchKeyEvent() || f_core.IsWebkit()) { 
				var input=component._input;
				
				var oldScrollTop=input.scrollTop;
				var oldScrollLeft=input.scrollLeft;
				var oldScrollWidth=input.scrollWidth;
				
				var selectionStart=input.selectionStart;
				var selectionEnd=input.selectionEnd;
				
				var value=input.value;
				
				f_core.Debug(f_clientValidator, "_ChangeKeyCode: oldScrollTop="+oldScrollTop+
						" oldScrollLeft="+oldScrollLeft+
						" oldScrollWidth="+oldScrollWidth+
						" selectionStart="+selectionStart+
						" selectionEnd="+selectionEnd+
						" value='"+value+"'");
				
				// Gestion du maxTextLength
				if (component.f_getMaxTextLength) {
					var max = component.f_getMaxTextLength();
					if (max >= 0 && value.length >= max) {
						return true;
					}
				}
				
				input.value = value.substring(0, selectionStart)+ ch + value.substring(selectionEnd);
				input.setSelectionRange(selectionStart + ch.length, selectionStart + ch.length);
								
				f_core.Debug(f_clientValidator, "_ChangeKeyCode: iScrollTop="+input.scrollTop+
						" iScrollLeft="+input.scrollLeft+
						" iScrollWidth="+input.scrollWidth+
						" selectionStart="+input.selectionStart+
						" selectionEnd="+input.selectionEnd+
						" value='"+input.value+"'");
			
				var deltaW = input.scrollWidth - oldScrollWidth;
				if (!input.scrollTop) {
					input.scrollTop=oldScrollTop;
				}
				if (!input.scrollLeft) {
					input.scrollLeft=oldScrollLeft+deltaW;
				}
				
				return false;
			}

			// Justement, le fameux trou de sécurité ...
			var keyEvent=document.createEvent("KeyEvents");
			keyEvent.initKeyEvent("keypress", true, true, document.defaultView, false, false, false, false, 0, retCode) ;
			component.dispatchEvent(keyEvent);
			
			return false;
		}
		
		return true;
	},
	/**
	 * @method private static hidden
	 * @context object:validator
	 */
	PerformMessageError: function(validator, type, lastError, lastErrorArgs) {
		f_core.Debug(f_clientValidator, "Perform message error. type='"+type+"' "+((lastError)?("severity='"+lastError.severity+"' summary='"+lastError.summary+"' detail='"+lastError.detail+"'"):("no error"))+"'.");
		if (!lastError) {
			return false;
		}
		
		var component=validator._component;
		
		var messageContext=f_messageContext.Get(component);
		if (!messageContext) {
			return;
		}
		
		var severity=lastError.severity;
		if (!severity) {
			severity=f_messageObject.SEVERITY_ERROR;
		}
		
		var message=new f_messageObject(severity, lastError.summary, lastError.detail);
		messageContext.f_addMessageObject(component, message);

		// Au message context ou f_message de se debrouiller ?
		// f_core.SetFocus(component, true);

		return true; // On arrete la, les messages ...
	},
	/**
	 * @method private static
	 * @context object:validator
	 */
	_PerformAlertError: function(validator, type, lastError, lastErrorArgs) {
		if (!lastError) {
			return false;
		}

		f_core.Debug(f_clientValidator, "PerformAlertError: Add alert error. type='"+type+"' "+((lastError)?("summary='"+lastError.summary+"' detail='"+lastError.detail+"'"):("no error"))+"'.");
		
		var message=lastError.summary;
		if (!message) {
			message=lastError.detail;
		}
		
		if (!message) {
			return;
		}
		
		alert(message);
		
		return true; // On arrete la, les messages ...
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator val 
	 * @param RegExp expr
	 * @param Number keyCode
	 * @param String keyChar
	 * @return Boolean
	 */
	Filter_generic: function(val,expr,keyCode,keyChar) {
		f_core.Assert(expr instanceof RegExp, "f_clientValidator.Filter_generic: Not a regular expression. '"+expr+"'.");
		
		return (expr.test(keyChar));
	},
	
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator 
	 * @param RegExp expr
	 * @param Number keyCode
	 * @param String keyChar
	 * @return Number
	 */
	Translator_generic: function(validator, expr, keyCode, keyChar) {
		return keyCode;
	},

	/*=============================================================================
		ERROR HANDLERS
	=============================================================================*/
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_msg: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, true, false, false);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_msg_color: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, true, true, false);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_msg_color_focus: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, true, true, true);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_msg_focus: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, true, false, true);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_color: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, false, true, false);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_color_focus: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, false, true, true);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_focus: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, false, false, true);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_null: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, false, false, false);
	},
	/**
	 * @method public static
	 * @context object:validator
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @return Boolean
	 */
	Error_default: function(validator, type, error) {
		return f_clientValidator.Error_generic(validator, type, error, true, true, true);
	},
	/**
	 * @method hidden static 
	 * @param f_clientValidator validator
	 * @param Number type
	 * @param f_messageObject error
	 * @param Boolean useMessage
	 * @param Boolean useColor
	 * @param Boolean useFocus
	 * @return Boolean
	 * @context object:validator
	 */
	Error_generic: function(validator, type, error, useMessage, useColor, useFocus) {
		f_core.Debug(f_clientValidator, "Error_generic: type='"+type+"' error='"+error+"' useMessage="+useMessage+" useColor="+useColor+" useFocus="+useFocus);

		var setMsg = false;
		var setCol = false;
		var unsetCol = false;
		var setFoc = false;
		
		switch (type) {
		case f_clientValidator.FILTER: 
			break;
			
		case f_clientValidator.TRANSLATOR: 
			break;
			
		case f_clientValidator.CHECKER:
		case f_clientValidator.FORMATTER:
			setMsg = !!error;
			setCol = setMsg;
			setFoc = setCol;
			break;

		case f_clientValidator.BEHAVIOR:
			setMsg = !!error;
			setFoc = setMsg;
			break;

		case f_clientValidator.SUCCESS:
			unsetCol = true;
			break;

		default:
			f_core.Error(f_clientValidator, "Error_generic: Unknown Error Type: "+type);
			break;
		}

//		alert("GENERIC ERROR: "+validator+"/"+type+"/"+error+"/"+useMessage+"\n"+setMsg+"/"+setCol+"/"+setFoc);		

		var component = validator.f_getComponent();

		if (useMessage && setMsg) {
			//MESSAGE(error[0], "adonis_validatorEx", error[1], error[2]);
			alert("summary="+error.f_getSummary()+"\ndetail="+error.f_getDetail());
		}
		if (useColor && setCol) {
			if (this._oldComponentColor === undefined) {
				this._oldComponentColor = component.f_getForegroundColor();
			}
			component.f_setForegroundColor("red");
		}
		if (useColor && unsetCol) {
			if (this._oldComponentColor !== undefined) {
				component.f_setForegroundColor(this._oldComponentColor);
			}
		}
		if (useFocus && setFoc) {
//			alert("Set focus !");
			f_core.SetFocus(component, true);
		}
		return true;
	}
};

var __members = {
	
	
	/**
	 * @field private boolean
	 */
	_lazyFocus: undefined,
	
	/**
	 * @method public
	 * @param f_textEntry component
	 * @param optional Object parameters
	 * @return optional Boolean parseComponentAttributes
	 * @return void
	 */
	f_clientValidator: function(component, parameters, parseComponentAttributes) {
		f_core.Assert(component.nodeType, "f_clientValidator(): Invalid component parameter ("+component+")");

		this._component = component;
		this._parseComponentAttributes=parseComponentAttributes;
		this._parameters=parameters;
				
		if (component.f_getInput) {
			this._input = component.f_getInput();
			
		} else {
			this._input = component;
		}
				
		f_core.Assert(!component._validator, "f_clientValidator.constructor: Only one validator by component! (id="+ component.id+")");
		component._validator = this;
		
		if (false) {
			var internalValue=f_core.GetAttributeNS(component, "internalValue", undefined);
			
			if (internalValue!==undefined) { //window._rcfacesLazyFocusInit /* && parameters && parameters["org.rcfaces.LAZY_FOCUS_INIT"] */) {
				var self=this;
				
				this._lazyFocus=true;
				
				component.f_insertEventListenerFirst(f_event.FOCUS, function(event){
					component.f_removeEventListener(f_event.FOCUS, arguments.callee);
					
					if (!self._lazyFocus) {
						return;
					}
					self._lazyFocus=false;
					
					self.f_installValidator();
					
					return this.f_fireEvent(event);
				});
				
				return;
			}
		}
		
		this.f_installValidator();
	},
	f_finalize: function() {
		f_core.RemoveCheckListener(this._component, this);	

		this._input = undefined;
		this._component = undefined;
		this._parameters = undefined; // Map<string, any>

// 		this._keyPressInstalled = undefined; // boolean
//		this._keyUpInstalled = undefined; // boolean
//		this._hasFocus = undefined; // boolean
//		this._firstApplyed = undefined; // boolean
//		this._checked=undefined; // boolean
//		this._outputValue=undefined; // string
//		this._initialValue=undefined; // string
//		this._initialFormattedValue=undefined; // string

		this._filters = undefined; // function[]
		this._translators = undefined; // function[]
		this._processors = undefined; // function[]
		this._converter = undefined; // object
		
		this._onError=undefined;  // function
		this._onErrorArguments=undefined;  // object[]
		
		this._onCheckError=undefined;  // function 
		this._onCheckErrorArguments=undefined; // object[]
	},
	f_performCheckPre: function(event) {		
		// On applique pour générer les erreurs !
		var value=this.f_getInputValue(true);
		
		this._checked=(this._applyAutoCheck(value, true)!==false);
		
		f_core.Debug(f_clientValidator, "f_performCheckPre: Precheck of component '"+this._component.id+"' returns "+this._checked+" value='"+value+"'.");
	},
	/**
	 * @method hidden
	 * @param f_event event
	 * @return Boolean 
	 */
	f_performCheckValue: function(event) {
		if (this._checked) {
			return true;
		}

		return false;
	},
	/**
	 * @method private
	 */
	_onReset: function() {
		f_core.Debug(f_clientValidator, "_onReset: Reset component '"+this._component.id+"' (initialValue='"+this._initialValue+"')");

		this._verifyFirstFocus();
	
		var self=this;
		window.setTimeout(function() {
			var clientValidator=self;
			self=null;
			
			// Il faut faire ca en asynchrone, apres le traitement du reset ...
			
			//var bRet = 
			clientValidator._applyAutoCheck(clientValidator._initialValue, false);
			
			if (clientValidator._hasFocus) {
				clientValidator._applyInputValue();
				
			} else {
				clientValidator._applyOutputValue();
			}
		}, 100);
	},
	/**
	 * Called while setting textEntry text !
	 * 
	 * @method hidden
	 * @param String value
	 * @return Boolean
	 */
	f_updateValue: function(value) {	
/*		if (value===undefined) {
			value=this._input.value;
		}
	*/
	
		if (value===null || value===undefined) {
			value="";
		}
		
		f_core.Debug(f_clientValidator, "f_updateValue: Update value '"+value+"' (hasFocus="+this._hasFocus+").");
		
		// Check and format the updated value
		var bRet = this._applyAutoCheck(value, false);
		
		if (this._hasFocus) {
			this._applyInputValue();
			
		} else {
			this._applyOutputValue();
		}
		
		return bRet;
	},
	/**
	 * 
	 * 
	 * @method hidden
	 */
	f_isValidValue: function() {
		return this._applyAutoCheck(this._input.value, true);
	},
	_getInitialValue: function() { 
		return this._initialValue; 
	},
	/**
	 * @method hidden
	 * @param String val
	 * @return void
	 */
	f_setInputValue: function(val) { 
		if (this._inputValue != val) {
			f_core.Debug(f_clientValidator, "f_setInputValue: Change internal input value '"+val+"'.");
		}
		
		this._inputValue = val; 
	},
	/**
	 * @method hidden
	 * @return String
	 */
	f_getValue: function() { 
		var value=this.f_getInputValue(true);

		this._applyAutoCheck(value, false);
		
		var v=this.f_getOutputValue();
		
		f_core.Debug(f_clientValidator, "f_getValue: Return internal value  input='"+value+"' output='"+v+"'.");

		return v;
	},
	/**
	 * @method hidden
	 * @return String
	 */
	f_serializeValue: function() {
		var value=this.f_getInputValue(true);

		this._applyAutoCheck(value, false);
		
		var v=this.f_getInputValue(false);
		
		f_core.Debug(f_clientValidator, "f_serializeValue: Return serialized value input='"+value+"' serialized='"+v+"'.");

		return v;
	},
	/**
	 * @method hidden
	 * @return String
	 */
	f_getInputValue: function(verifyFocus) { 
		/**
		 * @author Joel Merlin
		 * Check for an extern call that occurs before field validation. This can
		 * happen when requesting internal value while calling a_getText() during
		 * key or text changed events...
		 * This is not a recommanded practice since validation has not yet occured.
		 * But in such case, we set the input value with raw value.
		 *
		 * Some times, when user modify text fields by using backspaces or delete, inputValue is not computed !
		 * so if the component has focus, we return input's value !
		 */
		if (verifyFocus && this._hasFocus) {
			return this._input.value;
		}
		
		this._verifyFirstFocus();
		
		return this._inputValue; 
	},
	/**
	 * @method hidden
	 * @param String val
	 * @return void
	 */
	f_setOutputValue: function(val) { 
		if (this._outputValue != val) {
			f_core.Debug(f_clientValidator, "f_setOutputValue: Change internal output value to '"+val+"'.");
		}
		
		this._outputValue = val; 
	},
	/**
	 * @method hidden
	 * @return String
	 */
	f_getOutputValue: function() { 
		/**
		 * @author Joel Merlin
		 * This call is private and should NEVER be used outside validatorEx code.
		 * However, if we are in a transient state, rather send back raw text value.
		 */
		return this._outputValue; 
	},
	/**
	 * @method hidden
	 * @return HTMLElement
	 */
	f_getComponent: function() {
		return this._component;
	},
	/**
	 * @method private
	 * @return void
	 */
	_applyInputValue: function() {
		var input = this._input;
		var inVal = this.f_getInputValue();
	
		this._verifyFirstFocus();
		
		f_core.Debug(f_clientValidator, "_applyInputValue: Set value '"+inVal+"'.");
		if (input.value != inVal) {
			input.value = inVal;
		}
	
		// On selectionne car IE remet le focus au début du champ sinon !
		//input.select();
	},
	/**
	 * @method private
	 * @return void
	 */
	_verifyFirstFocus: function() {
		if (this._firstApplyed) {
			return;
		}
		this._firstApplyed=true;
		
		var componentValue=this._input.value;
		if (componentValue==this._initialFormattedValue) {
			return;
		}
		
		f_core.Debug(f_clientValidator, "_verifyFirstFocus: Value has changed ! modify initial value ...");
		
		this._initialValue = componentValue;
		
		this.f_setInputValue(componentValue);
	},
	/**
	 * @method private
	 * @return void
	 */
	_applyOutputValue: function() {
		var value=this.f_getOutputValue();
		
		f_core.Debug(f_clientValidator, "_applyOutputValue: Set value '"+value+"'.");
		this._input.value=value;
	},
	/**
	 * @method private
	 * @param Number keyCode
	 * @param String keyChar
	 * @param optional Object cache
	 * @return Boolean
	 */
	_applyFilters: function(keyCode, keyChar, cache) {
		var filters=this._filters;
		if (!filters) {
			return true;
		}
		
		var bRet = true;

		for (var i=0; i<filters.length; i++) {
			var f = filters[i];
			if (f instanceof RegExp) {
				bRet = f_clientValidator.Filter_generic(this, f, keyCode, keyChar);
				
			} else if (f instanceof Function) {
				bRet = f.call(window, this, keyCode, keyChar, cache, i);
			}
			
			if (!bRet) {
				break;
			}
		}
		
		return bRet;
	},
	/**
	 * @method private
	 * @return void
	 */
	_applyProcessors: function(keyCode, shift, ctrl, alt) {
		var processors=this._processors;
		if (!processors) {
			return;
		}
		
		var component=this.f_getComponent();
		var params=[ this, keyCode, shift, ctrl, alt ];
		
		for (var i=0; i<processors.length; i++) {
			var p = processors[i];
			if (typeof(p)!="function") {
				continue;
			}
			
			var bRet = p.apply(component, params);
			if (!bRet) { 
				break;
			}
		}
	},
	/**
	 * @method private
	 * @param Number keyCode
	 * @param String keyChar
	 * @param Object cache
	 * @return Number
	 */
	_applyTranslators: function(keyCode, keyChar, cache) {
		var translators=this._translators;
		if (!translators) {
			return keyCode;
		}

//		var component=this.f_getComponent();
		for (var i=0; i<translators.length; i++) {
			var t = translators[i];
			
			var retCode=keyCode;
			if (t instanceof RegExp) {
				retCode = f_clientValidator.Translator_generic(this, t, keyCode, keyChar);
				
			} else if (t instanceof Function) {
				retCode = t.call(window, this, keyCode, keyChar, cache, i);
			}
			
			if (retCode == keyCode) {
				continue;
			}	
			
			keyCode = retCode;
			if (i+1<translators.length) {
				keyChar = String.fromCharCode(retCode);
			}
		}

		return keyCode;
	},
	/**
	 * @method private
	 * @return String
	 */
	_applyCheckers: function(checkVal) {
		var checkers=this._checkers;
		
		if (!checkers) {
			return checkVal;
		}
		
		var component=this.f_getComponent();
		for (var i=0; i<checkers.length; i++) {
			var c = checkers[i];
			f_core.Assert(typeof(c)=="function", "f_clientValidator._applyCheckers: Unknown type of checker '"+c+"'.");
			
			var newVal = c.call(component, this, checkVal);

			f_core.Debug(f_clientValidator, "_applyCheckers: Check (#"+i+") value current='"+checkVal+"' new='"+newVal+"'.");

			if (newVal === null) { // Une erreur, on arrete la ...
				return null;
			}

			if (newVal==checkVal) { // Prochain checker
				continue;
			}

			this.f_setInputValue(newVal);
			checkVal=newVal;
		}
		
		return checkVal;
	},
	/**
	 * @method private
	 * @return String
	 */
	_applyFormatters: function() {
		var formatters=this._formatters;
		var formatVal = this.f_getOutputValue();

		if (!formatters) {
			return formatVal;
		}
	
		var component=this.f_getComponent();
		for (var i=0; i<formatters.length; i++) {
			var f = formatters[i];
			f_core.Assert(typeof(f)=="function", "f_clientValidator._applyFormatters: Unknown type of translator '"+f+"'.");

			formatVal = f.call(component, this, formatVal);
			if (formatVal === null) {
				break;
			}
			
			this.f_setOutputValue(formatVal);
		}

		return formatVal;
	},
	/**
	 * @method private
	 * @return String
	 */
	_applyBehaviors: function() {
		var behaviors=this._behaviors;
		if (!behaviors) {
			return null;
		}

		var component=this.f_getComponent();
		var bRet=undefined;
		for (var i=0; i<behaviors.length; i++) {
			var f = behaviors[i];
			f_core.Assert(typeof(f)=="function", "f_clientValidator._applyBehaviors: Unknown type of behavior '"+f+"'.");
			
			bRet = f.call(component, this, this.f_getOutputValue());
			if (!bRet) {
				break;
			}
		}
		return bRet;
	},
	/**
	 * @method private
	 * @param String curVal Current value
	 * @param Boolean check Check mode
	 * @return Boolean
	 */
	_applyAutoCheck: function(curVal, check) {
		var bRet = true;
		var bValid;
		var fError = (check)? this._onCheckError:this._onError;
		var fErrorArguments = (check)? this._onCheckErrorArguments:this._onErrorArguments;
		var handled=undefined;
		
		if (!fError && check) {
			fError=f_clientValidator.PerformMessageError;
		}
		
		this.f_setInputValue(curVal);
		this.f_setOutputValue(curVal);

		var transVal=curVal;

		// Call filters and translators
		var hasTranslators=!!this._translators;
		var hasFilters=!!this._filters;
		
		if (hasFilters || hasTranslators) {
			transVal = "";
			
			var cacheFilters=new Object;
			var cacheTranslators=new Object;
			for (var i=0; i<curVal.length; i++) {
				var ch=curVal.charAt(i);
				var cch=curVal.charCodeAt(i);
				
				if (hasFilters) {
					bValid = this._applyFilters(cch, ch, cacheFilters);
					if (!bValid) {
						continue;
					}
				}
				
				if (hasTranslators) {					
					var t=this._applyTranslators(cch, ch, cacheTranslators);

					if (t!=cch) {					
						ch = String.fromCharCode(t);
					}
				}
				
				transVal+=ch;
			}
		}
		
		// f_core.Debug(f_clientValidator, "Apply auto check after filters input='"+this._inputValue+"' output='"+this._outputValue+"'.");
		
		if (curVal != transVal) {
			curVal=transVal;
			this.f_setInputValue(transVal);
			this.f_setOutputValue(transVal);
		}
	
		// f_core.Debug(f_clientValidator, "Apply auto check after translators input='"+this._inputValue+"' output='"+this._outputValue+"'.");
	
		// Call checkers
		// @JM Checker has to deal with empty string
		var checkVal = this._applyCheckers(curVal);
		f_core.Debug(f_clientValidator, "_applyAutoCheck: apply checkers returns '"+checkVal+"' curVal='"+curVal+"'");

		if (checkVal === null) {
			f_core.Debug(f_clientValidator, "_applyAutoCheck: Applyed Checker returns error '"+this.f_getLastError()+"' for component '"+this._component.id+"'.");
			bRet = false;
			if (fError) {
				try {
					handled = fError.call(fError, this, f_clientValidator.CHECKER, this.f_getLastError(), fErrorArguments);
					
				} catch (x) {
					f_core.Error(f_clientValidator, "_applyAutoCheck: Call of error function for component '"+this._component.id+"' throws exception.", x);
				}
			}
		} else {
			if (fError) {
				try {
					handled = fError.call(fError, this, f_clientValidator.CHECKER);
					
				} catch (x) {
					f_core.Error(f_clientValidator, "_applyAutoCheck: Call of error function for component '"+this._component.id+"' throws exception.", x);
				}
			}
			if (curVal!=checkVal) {
				this.f_setInputValue(checkVal);
				this.f_setOutputValue(checkVal);
			}
		}

		// f_core.Debug(f_clientValidator, "Apply auto check after checkers input='"+this._inputValue+"' output='"+this._outputValue+"'.");
	
		// Call formatters
		if (checkVal) {
			var formatVal = this._applyFormatters();
			f_core.Debug(f_clientValidator, "_applyAutoCheck: apply formatters returns '"+formatVal+"'");

			if (formatVal == null) {
				f_core.Debug(f_clientValidator, "_applyAutoCheck: Applyed formatters returns error '"+this.f_getLastError()+"' for component '"+this._component.id+"'.");

				bRet = false;
				if (fError) {
					try {
						handled = fError.call(fError,this,f_clientValidator.FORMATTER,this.f_getLastError(), fErrorArguments);
						
					} catch (x) {
						f_core.Error(f_clientValidator, "_applyAutoCheck: Call of error function for component '"+this._component.id+"' throws exception.", x);
					}
				}
			} else {
				if (fError) {
					try {
						handled = fError.call(fError, this, f_clientValidator.FORMATTER);
						
					} catch (x) {
						f_core.Error(f_clientValidator, "_applyAutoCheck: Call of error function for component '"+this._component.id+"' throws exception.", x);
					}
				}
				this.f_setOutputValue(formatVal);
			}
		}
	
		// f_core.Debug(f_clientValidator, "Apply auto check after formatters input='"+this._inputValue+"' output='"+this._outputValue+"'.");
	
		if (bRet) {
			// Call behaviors
			var ret = this._applyBehaviors();
			f_core.Debug(f_clientValidator, "_applyAutoCheck: apply behaviors returns '"+ret+"'");
			
			// If set, get the returned value
			if (ret!==undefined) {
				bRet = ret;
			}
	
			try {
				// Otherwise, check error
				if (bRet == false) {
					f_core.Debug(f_clientValidator, "_applyAutoCheck: Applyed behaviors returns error '"+this.f_getLastError()+"' for component '"+this._component.id+"'. (handled="+handled+")");
	
					if (fError && !handled) {
						handled = fError.call(fError, this, f_clientValidator.BEHAVIOR, this.f_getLastError(), fErrorArguments);
					}
					
				} else {
					if (fError) {
						handled = fError.call(fError, this, f_clientValidator.BEHAVIOR);
					}
				}

			} catch (x) {
				f_core.Error(f_clientValidator, "_applyAutoCheck: Call of error function for component '"+this._component.id+"' throws exception.", x);
			}
		}
		
		// f_core.Debug(f_clientValidator, "Apply auto check after behaviors input='"+this._inputValue+"' output='"+this._outputValue+"'.");
			
		// Return text entry check status
		return bRet;
	},
	/**
	 * @method hidden final
	 * @param Object expr A function or a Regexp 
	 * @return void
	 */
	f_addFilter: function(expr) {
		f_core.Assert(typeof(expr)=="function" || (expr instanceof RegExp), "f_clientValidator.f_addFilter: Filter parameter must be a function or a regexp. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addFilter: Add filter to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		if (!this._keyPressInstalled) {
			this._component.f_insertEventListenerFirst(f_event.KEYPRESS, f_clientValidator._OnKeyPress);
			this._keyPressInstalled = true;
		}
		
		var filters=this._filters;
		if (!filters) {
			filters = new Array;
			this._filters = filters;
		}
		filters.push(expr);
	},
	/**
	 * @method hidden final
	 * @param Object expr A function or a Regexp 
	 * @return void
	 */
	f_addProcessor: function(expr) {
		f_core.Assert(typeof(expr)=="function" || (expr instanceof RegExp), "f_clientValidator.f_addProcessor: Processor parameter must be a function or a regexp. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addProcessor: Add processor to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));
		
		if (!this._keyUpInstalled) {
			this._component.f_insertEventListenerFirst(f_event.KEYUP, f_clientValidator._OnKeyUp);
			this._keyUpInstalled = true;
		}
		
		var processors=this._processors;
		if (!processors) {
			processors = new Array;
			this._processors=processors;
		}
		processors.push(expr);
	},
	/**
	 * @method hidden final
	 * @param Object expr A function or a Regexp 
	 * @return void
	 */
	f_addTranslator: function(expr) {
		f_core.Assert(typeof(expr)=="function" || (expr instanceof RegExp), "f_clientValidator.f_addTranslator: Translator parameter must be a function or a regexp. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addTranslator: Add translator to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		if (!this._keyPressInstalled) {
			this._component.f_insertEventListenerFirst(f_event.KEYPRESS, f_clientValidator._OnKeyPress);
			this._keyPressInstalled = true;
		}
		
		var translators=this._translators;
		if (!translators) {
			translators = new Array;
			this._translators = translators;
		}
		translators.push(expr);
	},
	/**
	 * @method hidden final
	 * @param function expr 
	 * @return void
	 */
	f_addChecker: function(expr) {
		f_core.Assert(typeof(expr)=="function", "f_clientValidator.f_addChecker: Checker parameter must be a function. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addChecker: Add checker function to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		var checkers=this._checkers;
		if (!checkers) {
			checkers=new Array;
			this._checkers = checkers;
		}
		checkers.push(expr);
	},
	/**
	 * @method hidden final
	 * @param function expr 
	 * @return void
	 */
	f_addFormatter: function(expr) {
		f_core.Assert(typeof(expr)=="function", "f_clientValidator.f_addFormatter: Formatter parameter must be a function. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addFormatter: Add formatter function to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		var formatters=this._formatters;
		if (!formatters) {
			formatters = new Array;
			this._formatters = formatters;
		}
		formatters.push(expr);
	},
	/**
	 * @method hidden final
	 * @param function expr 
	 * @return void
	 */
	f_addBehavior: function(expr) {
		f_core.Assert(typeof(expr)=="function", "f_clientValidator.f_addBehavior: Behavior parameter must be a function. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_addBehavior: Add behavior function to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		var behaviors=this._behaviors;
		if (!behaviors) {
			behaviors = new Array;
			this._behaviors=behaviors;
		}
		behaviors.push(expr);
	},
	/**
	 * @method hidden final
	 * @param function expr 
	 * @return void
	 */
	f_setOnError: function(expr) {
		f_core.Assert(typeof(expr)=="function", "f_clientValidator.f_setOnError: OnError parameter must be a function. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_setOnError: Set onError function to validator attached to component '"+this._component.id+"' :\n"
		+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		this._onError = expr;
		
		if (arguments.length<2) {
			return;
		}
		
		var l=f_core.PushArguments(null, arguments, 1);
		
		this._onErrorArguments=l;
	},
	/**
	 * @method hidden final
	 * @param function expr 
	 * @return void
	 */
	f_setOnCheckError: function(expr) {
		f_core.Assert(typeof(expr)=="function", "f_clientValidator.f_setOnCheckError: OnCheckError parameter must be a function. ("+expr+")");

		f_core.Debug(f_clientValidator, "f_setOnCheckError: Set onCheckError function to validator attached to component '"+this._component.id+"' :\n"
			+((String(expr).length>64)?(String(expr).substring(0, 64)+"  ..."):(String(expr))));

		this._onCheckError = expr;
		
		if (arguments.length<2) {
			return;
		}
		
		var l=f_core.PushArguments(null, arguments, 1);
		
		this._onCheckErrorArguments=l;
	},
	/**
	 * @method hidden final
	 * @param Object converter Converter
	 * @return void
	 */
	f_setConverter: function(converter) {
		f_core.Assert(typeof(converter)=="object", "f_clientValidator.f_setConverter: Converter must be an object. ("+converter+")");
		f_core.Assert(typeof(converter.f_getAsObject)=="function", "f_clientValidator.f_setConverter: f_getAsObject of Converter must be a function. ("+converter.f_getAsObject+")");
		f_core.Assert(typeof(converter.f_getAsString)=="function", "f_clientValidator.f_setConverter: f_getAsString of Converter must be a function. ("+converter.f_getAsString+")");		
		
		this._converter=converter;
	},
	/**
	 * @method hidden final
	 * @return Object converter
	 */
	f_getConverter: function() {
		return this._converter;
	},
	/**
	 * @method public final
	 * @param String summary
	 * @param String detail
	 * @param Number severity
	 * @return void
	 */
	f_setLastError: function(summary, detail, severity) {
		f_core.Debug(f_clientValidator, "f_setLastError: summary='"+summary+"' detail='"+detail+"' severity='"+severity+"'.");
	
		if (typeof(severity)=="string") {
			try {
				severity=parseInt(severity, 10);
				
			} catch (x) {
				f_core.Error(f_clientValidator, "f_setLastError: Invalid severity expression '"+severity+"'.", x);
			}
		}
	
		this._lastErrorObject={
			summary: summary,
			detail: detail,
			severity: severity
		};
	},
	/**
	 * @method public final
	 * @return Object
	 */
	f_getLastError: function() {
		return this._lastErrorObject;
	},
	/**
	 * @method hidden
	 * @param String name
	 * @param Object value
	 */
	f_addParameter: function(name, value) {
		var ps=this._parameters;
		if (!ps) {
			ps=new Object;
			this._parameters=ps;
		}
		ps[name]=value;
	},
	/**
	 * @method public final
	 * @param String name
	 * @param optional String def Returned value if parameter is not found
	 * @return String Value associated to the parameter.
	 */
	f_getParameter: function(name, def) {
		if (!this._parameters) {
			return def;
		}
		
		var r=this._parameters[name];
		if (r===undefined) {
			return def;
		}
		
		return r;
	},
	/**
	 * @method public final
	 */
	f_getIntParameter: function(name, def) {
		var r = this.f_getParameter(name);
		if (r === undefined) {
			return def;
		}
		return parseInt(r, 10);
	},
	/**
	 * @method public final
	 */
	f_getStringParameter: function(name, def) {
		var r = this.f_getParameter(name);
		if (r === undefined) {
			return def;
		}
		return r;
	},
	/**
	 * @method public final
	 */
	f_getBoolParameter: function(name, def) {
		var r = this.f_getParameter(name);
		if (r === undefined) {
			return def;
		}
		return (r == "true");
	},
	/**
	 * @method public final
	 */
	f_getObject: function() {
		return this._object;
	},
	/**
	 * @method public final
	 */
	f_setObject: function(object) {
		this._object=object;
	},
	/**
	 * @method public final
	 */
	f_getConvertedValue: function() {
		var value=this.f_getValue();
		
		var converter=this._converter;
		if (!converter) {
			return value;
		}
		
		try {
			return converter.f_getAsObject(this, value);
				
		} catch (x) {
			f_core.Error(f_clientValidator, "Exception when calling converter with string '"+value+"'. (converter='"+converter+"')", x);
			
			throw x;
		}
	},
	/**
	 * @method public final
	 * @param Object value
	 */
	f_setConvertedValue: function(value) {		
		var converter=this._converter;
		if (!converter) {
			f_core.Debug(f_clientValidator, "No conversion, returns false");

			return false;
		}

		try {
			value=converter.f_getAsString(this, value);
			
		} catch (x) {
			f_core.Error(f_clientValidator, "Exception when calling converter with object '"+value+"'. (converter='"+converter+"')", x);
			
			throw x;
		}

		f_core.Debug(f_clientValidator, "Update value of converted value="+value);
		
		this.f_updateValue(value);
		
		return true;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_installValidator: function() {
		var component=this._component;

		var parseComponentAttributes=this._parseComponentAttributes;
		if (parseComponentAttributes) {
			this._parseComponentAttributes=undefined;
					
			this.f_parseComponentAttributes(component);

		} else {
			var componentValue=this._input.value;
			if (componentValue === undefined || componentValue == null) {
				componentValue="";
			}
			
			this.f_setInputValue(componentValue);
			
			this._initialValue = componentValue;
	
			this._outputValue = "";
			
		}
		
		f_core.AddCheckListener(component, this);	
		
		if (component.f_isFocusEventManager && component.f_isFocusEventManager()) {
			return;
		}
		
		var validator=this;
		component.f_insertEventListenerFirst(f_event.RESET, function(event) {
			return validator._onReset(event);
		});
		
		/*

		 		f_core.Debug(f_clientValidator, "f_installValidator: Construct new validator for component '"+component.id+"' with params='"+this._parameters+"' initialValue='"+componentValue+"'.");
		*/
		
		
		component.f_insertEventListenerFirst(f_event.FOCUS, f_clientValidator._OnFocus);
		component.f_insertEventListenerFirst(f_event.BLUR, f_clientValidator._OnBlur);
	},
	/**
	 * @method protected
	 * @param optional String component
	 * @return void
	 */
	f_parseComponentAttributes: function(component) {
		var internalValue=f_core.GetAttributeNS(component, "internalValue");
		this._initialValue=(internalValue)?internalValue:"";
		
		var filters=f_core.GetAttributeNS(component, "vFilter");
		if (filters) {
			var s=filters.split(':');
			for(var i=0;i<s.length;i++) {
				var filter=f_clientValidator._EvalFunction(s[i]);
			
				this.f_addFilter(filter);
			}
		}
		
		var translators=f_core.GetAttributeNS(component, "vTranslator");
		if (translators) {
			var s=translators.split(':');
			for(var i=0;i<s.length;i++) {
				var translator=f_clientValidator._EvalFunction(s[i]);
				
				this.f_addTranslator(translator);
			}
		}

		var checkers=f_core.GetAttributeNS(component, "vChecker");
		if (checkers) {
			var s=checkers.split(':');
			for(var i=0;i<s.length;i++) {
				var checker=f_clientValidator._EvalFunction(s[i]);
				this.f_addChecker(checker);
			}
		}

		var formatters=f_core.GetAttributeNS(component, "vFormatter");
		if (formatters) {
			var s=formatters.split(':');
			for(var i=0;i<s.length;i++) {
				var formatter=f_clientValidator._EvalFunction(s[i]);
				this.f_addFormatter(formatter);
			}
		}

		var behaviors=f_core.GetAttributeNS(component, "vBehavior");
		if (behaviors) {
			var s=behaviors.split(':');
			for(var i=0;i<s.length;i++) {
				var behavior=f_clientValidator._EvalFunction(s[i]);
				this.f_addBehavior(behavior);
			}
		}

		var errors=f_core.GetAttributeNS(component, "vError");
		if (errors) {
			var s=errors.split(':');
			for(var i=0;i<s.length;i++) {
				var error=f_clientValidator._EvalFunction(s[i]);
				this.f_setOnError(error);
			}
		}

		var checkErrors=f_core.GetAttributeNS(component, "vCheckError");
		if (checkErrors) {
			var s=checkErrors.split(':');
			for(var i=0;i<s.length;i++) {
				var checkError=f_clientValidator._EvalFunction(s[i]);
				this.f_setOnCheckError(checkError);
			}
		}

		var converters=f_core.GetAttributeNS(component, "converter");
		if (converters) {
			var s=converters.split(':');
			for(var i=0;i<s.length;i++) {
				var converter=f_clientValidator._EvalFunction(s[i], true);
				this.f_setConverter(converter);
			}
		}
		
		if (internalValue===undefined) {
			internalValue=this._input.value;
		}
		
		this._applyAutoCheck(internalValue, false);
		this._applyOutputValue();
	
		this._initialFormattedValue=this._input.value;
	},
	/**
	 * @method private
	 * @param Array validators
	 * @return void 
	 */
	_installValidatorObjects: function(validators) {
		var expressions=f_clientValidator._Expressions;
		if (!expressions) {
			expressions=new Object;
			f_clientValidator._Expressions=expressions;
		}
		
		var vs=validators.split(';');
		
		var event=new f_event(this, f_event.INIT);
		var oldEvent=undefined;
		try {
			oldEvent=f_event.SetEvent(event);
			
			for(var i=0;i<vs.length;i++) {
				var v=decodeURIComponent(vs[i]);
				
				try {
					var f=expressions[v];
					if (!f) {
						f=new window.Function("event", v);
						expressions[v]=f;
					}
					
					f.call(this, event);
					
				} catch (x) {
					f_core.Error(f_clientValidator, "_InstallValidatorObjects: Can not initialize validator object '"+v+"'", x);
				}
			}
		} finally {
			f_event.SetEvent(oldEvent);

			f_classLoader.Destroy(event);
		}
	}
};

new f_class("f_clientValidator", {
	extend: f_object,
	statics: __statics,
	members: __members
});
