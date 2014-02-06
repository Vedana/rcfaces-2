/*
 * $Id: f_textArea.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_textArea class
 *
 * @class public f_textArea extends f_abstractEntry
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	
	/**
	 * @method private static
	 * @param f_event event
	 * @return Boolean
	 * @context object:textArea
	 */
	_VerifyMaxTextLength: function(event) {
		var textArea=event.f_getComponent();
		
		var maxTextLength=textArea.f_getMaxTextLength();
		if (!maxTextLength || maxTextLength<1) {
			return true;
		}
		
		var value=textArea.f_getValue();
		if (!value) {
			return true;
		}
		
		if (value.length<=maxTextLength) {
			return true;
		}
	
		var message=f_resourceBundle.Get(f_textArea).f_formatParams("MAX_TEXT_LENGTH", [maxTextLength]);
		if (!message) {	
			message=f_locale.Get().f_formatMessageParams("javax_faces_validator_LengthValidator_MAXIMUM", [maxTextLength], "Value is greater than allowable maximum of ''{0}''");
		}
		
		var messageContext=f_messageContext.Get(event);	
		messageContext.f_addMessage(textArea, f_messageObject.SEVERITY_ERROR, message, null);
		
		return false;
	},
	/**
	 * @method private static
	 * @param f_event evt
	 * @return Boolean
	 * @context object:this
	 */
	_MaxTextLengthKeyPress: function(event) {
		var jsEvent = event.f_getJsEvent();
		var keyCode = jsEvent.keyCode;
		var charCode = jsEvent.charCode;
		
		var textArea=this; // A VOIR car si l'INPUT est dettaché !?
		
		if (textArea.f_isReadOnly() || textArea.f_isDisabled()) {
			// On laisse la possibilité de traiter des callbacks fonctionnelles
			return true;
		}
		
		var keyChar;
		
		if (!charCode) {
			keyChar = String.fromCharCode(keyCode);

		} else {
			keyChar = String.fromCharCode(charCode);
		}
				
		// f_core.Debug(f_textArea, "_MaxTextLengthKeyDown: keyCode="+keyCode+" charCode="+charCode+" shift="+jsEvent.shift+" ctrl="+jsEvent.ctrl+" alt="+jsEvent.alt+" keyChar="+keyChar+"("+((keyChar.length>0)?keyChar.charCodeAt(0):"")+")");
	
		// Il faut compter les CRs !!!
		
		 if (f_core.IsGecko()) {
				if (keyCode>0 && keyCode!=13) {
					return true;
				}
				//keyCode=charCode;
			}
		 else if (keyCode < 32 && keyCode!=13) {
			return true;
		} 

		return f_textArea._ChangeText(textArea, keyChar);
	},
	/**
	 * @method private static
	 * @context event:jsEvent
	 */
	_MaxTextLengthPaste: function(jsEvent) {		
		if (!jsEvent) {
			jsEvent = f_core.GetJsEvent(this);
		}

		var textArea=this;
		
		if (textArea.f_isReadOnly() || textArea.f_isDisabled()) {
			// On laisse la possibilité de traiter des callbacks fonctionnelles
			return false;
		}

		var input=textArea.f_getInput();
		var maxTextLength=textArea._maxTextLength;
		
		var value=input.value;
		if (value.length>=maxTextLength) {
			return false;
		}
		
		var _input=input;
		var _maxTextLength=maxTextLength;
		window.setTimeout(function() {
		
			var value=_input.value;
			if (value.length<=_maxTextLength) {
				_input=null;
				_maxTextLength=null;
				return;
			}
		
			var selection=f_core.GetTextSelection(_input);
			var selectionStart=selection[0];
			var selectionEnd=selection[1];
			
			if (selectionStart>=_maxTextLength) {
				selectionStart=_maxTextLength;
			}
			if (selectionEnd>=_maxTextLength) {
				selectionEnd=_maxTextLength;
			}
			
			_input.value=value.substring(0, _maxTextLength);
	
			f_core.SelectText(_input, selectionStart, selectionEnd);
			
			_input=null;
			_maxTextLength=null;
			
		}, 10);
	},
	/**
	 * @method private static
	 * @param f_textArea textArea
	 * @param String insertedText
	 * @return Boolean
	 */
	_ChangeText: function(textArea, insertedText) {
		var input=textArea.f_getInput();
		
		var maxTextLength=textArea._maxTextLength;
		
		var value=input.value;
		if (value.length+insertedText.length<=maxTextLength) {
			return true;
		}
		
		var selection=f_core.GetTextSelection(input);
		var selectionStart=selection[0];
		var selectionEnd=selection[1];
		
		// f_core.Debug(f_textArea, "_ChangeText: selectionStart="+selectionStart+" selectionEnd="+selectionEnd+" insertedText["+insertedText.length+"]='"+insertedText+"'"); 
		
		if (selectionEnd-selectionStart+1>insertedText.length) {
			// Il y a une selection de +1 caractere !  donc 
			return true; 
		}
		
		if (textArea._ignoreWhenFull || selectionStart==maxTextLength) {
			// On est au bout de la zone de texte, on refuse
			return false;
		}		
		
		value=value.substring(0, maxTextLength-insertedText.length);
		
		// f_core.Debug(f_textArea,"_ChangeText: Set value to '"+value+"'");
		input.value=value;

		// On le remet comme il etait !		
		f_core.SelectText(input, selectionStart, selectionEnd);
		
		return true;
	},
	/**
	 * @method private static
	 */
	_InstallMaxTextLength: function(textArea) {
		textArea.f_addEventListener(f_event.KEYPRESS, f_textArea._MaxTextLengthKeyPress);
		
		var input=textArea.f_getInput();
		if (input) {
			input.onpaste=f_textArea._MaxTextLengthPaste;
		}
	},
	/**
	 * @method private static
	 */
	_UninstallMaxTextLength: function(textArea) {
		var input=textArea.f_getInput();
		if (input) {
			input.onpaste=null;
		}
	}
};

var __members = {
	f_textArea: function() {
		this.f_super(arguments);

		this._maxTextLength=f_core.GetNumberAttributeNS(this,"maxTextLength", 0);
		if (this._maxTextLength>0) {
			this._ignoreWhenFull=f_core.GetBooleanAttributeNS(this,"ignoreWhenFull", false);

			f_textArea._InstallMaxTextLength(this);
			
			this.f_addEventListener(f_event.VALIDATION, f_textArea._VerifyMaxTextLength);
		}
	},
	f_finalize: function() {
		if (this._maxTextLength>0) {
			f_textArea._UninstallMaxTextLength(this);
		}

		// this._ignoreWhenFull=undefined;
		// this._maxTextLength=undefined; // number
	
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getMaxTextLength: function() {
		return this._maxTextLength;
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
	
		this.f_addEventListener(f_event.VALIDATION, this._validate);		
	},
	/**
	 * @method private 
	 * @param f_event event
	 * @return Boolean
	 */
	_validate: function(event) {
		if (this.f_isRequired()==false) {
			return;
		}
				
		var value=this.f_getValue();
		if (value) {
			return true;
		}
		
		var message=f_resourceBundle.Get(f_textArea).f_formatParams("REQUIRED_SUMMARY");
		if (!message) {	
			message=f_locale.Get().f_formatMessageParams("javax_faces_component_UIInput_REQUIRED", null, "Validation Error: Value is required.");
		}
		
		var messageContext=f_messageContext.Get(event);	
		messageContext.f_addMessage(this, f_messageObject.SEVERITY_ERROR, message, null);
		
		return false;
	}
};

new f_class("f_textArea", {
	extend: f_abstractEntry,
	members: __members,
	statics: __statics
});
