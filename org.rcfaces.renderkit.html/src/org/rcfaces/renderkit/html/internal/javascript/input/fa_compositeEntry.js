/*
 * $Id: fa_compositeEntry.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * Aspect Composite entry
 *
 * @aspect hidden abstract fa_compositeEntry extends fa_disabled, fa_readOnly
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics={	

	/**
	 * @field protected static final String
	 */
	DEFAULT_TYPE: "default",
	
	/**
	 * @method private static
	 * @context object:compositeEntry
	 */
	_OnInputKeyDown: function(evt) {
		var compositeEntry=this._compositeEntry;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (compositeEntry.f_getEventLocked(evt)) {
			return false;
		}

		if (compositeEntry.f_isDisabled() || compositeEntry.f_isReadOnly()) {
			return f_core.CancelJsEvent(evt);
		}
	
		if (compositeEntry._onInputKeyDown(evt, this)) {
			return true;
		}
		
		return f_core.CancelJsEvent(evt);
	},
	
	/**
	 * @method private static
	 * @context object:compositeEntry
	 */
	_OnInputKeyPress: function(evt) {
		var compositeEntry=this._compositeEntry;
	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (compositeEntry.f_getEventLocked(evt, false)) {
			return false;
		}

		if (compositeEntry.f_isDisabled() || compositeEntry.f_isReadOnly()) {
			return f_core.CancelJsEvent(evt);
		}
	
		if (compositeEntry._onInputKeyPress(evt, this)) {
			return true;
		}
		
		return f_core.CancelJsEvent(evt);		
	},
	
	/**
	 * @method private static
	 * @context object:compositeEntry
	 */
	_OnInputBlur: function(evt) {
		var compositeEntry=this._compositeEntry;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (compositeEntry.f_getEventLocked(evt, false)) {
			return false;
		}

		if (compositeEntry.f_isDisabled() || compositeEntry.f_isReadOnly()) {
			return f_core.CancelJsEvent(evt);
		}
	
		return compositeEntry._onInputBlur(this, evt);		
	}
};

var __members={

	fa_compositeEntry: function() {
		var inputs=this.getElementsByTagName("input");
		f_core.Assert(inputs.length, "fa_compositeEntry(): Can not find any Input !");
		
		this._inputs=inputs;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			
			var separators=f_core.GetAttributeNS(input,"separators");
			if (separators) {
				// Ce sont les separateurs du precedant !
				inputs[i-1]._separators=separators;
			}
			
			input._type=f_core.GetAttributeNS(input,"type");
			
			if (i+1<inputs.length) {
				input._nextInput=inputs[i+1];
				inputs[i+1]._predInput=input;
			}
			
			input._compositeEntry=this;
			input.onkeypress=fa_compositeEntry._OnInputKeyPress;
			input.onblur=fa_compositeEntry._OnInputBlur;
			input.onkeydown=fa_compositeEntry._OnInputKeyDown;

			if (typeof(this.fa_initializeInput)=="function") {
				this.fa_initializeInput(input);	
			}
		}
	},
	f_finalize: function() {		
	
		var inputs=this._inputs;
		if (inputs) {
			this._inputs=undefined; // HtmlInputElement[]
			
			for(var i=0;i<inputs.length;i++) {
				var input=inputs[i];
				
				if (typeof(this.fa_finalizeInput)=="function") {
					this.fa_finalizeInput(input);
				}
				
				// input._separators=undefined; // string
				// input._type=undefined; // string
				
				input._nextInput=undefined; // HtmlInputElement
				input._predInput=undefined; // HtmlInputElement
				input._compositeEntry=undefined; // fa_compositeEntry
				
				input.onkeypress=null; // function
				input.onkeydown=null; // function
				input.onblur=null; // function
				
				f_core.VerifyProperties(input);
			}
		}
	},
	/**
	 * @method protected final
	 * @param String type
	 * @return HTMLInputElement
	 */
	fa_getInputByType: function(type) {
		var inputs=this._inputs;
		if (!inputs) {
			return null;
		}
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			
			if (input._type==type) {
				return input;
			}
		}
		
		return null;
	},
	/**
	 * @method protected abstract optional
	 */
	fa_initializeInput: f_class.OPTIONAL_ABSTRACT,
	/**
	 * @method protected abstract
	 */
	fa_finalizeInput: f_class.OPTIONAL_ABSTRACT,
	/**
	 * @method private
	 */
	_onInputKeyDown: function(jsEvent, input) {
		// permet de capter le TAB sous IE !
		var keyCode = jsEvent.keyCode;

		var sel=f_core.GetTextSelection(input);
		f_core.Debug(fa_compositeEntry, "KeyDown '"+keyCode+"' sel0="+sel[0]+" sel1="+sel[1]);

		switch(keyCode) {
		case f_key.VK_LEFT:
		case f_key.VK_HOME:
		case f_key.VK_BACK_SPACE:
			if (!sel[0] && !sel[1]) {
				var predInput=input._predInput;
				if (predInput) {
					var setLastPos=false;
					if (keyCode==f_key.VK_HOME) {
						for(;predInput._predInput;predInput=predInput._predInput);
						
						f_core.SelectText(predInput, 0);
						setLastPos=0;
						
					} else if (keyCode==f_key.VK_LEFT && jsEvent.ctrlKey) {
						f_core.SelectText(predInput, 0);						
						setLastPos=0;
						
					} else {
						setLastPos=predInput.value.length;
					}

					predInput.focus();
					
					if (setLastPos!==false && f_core.IsInternetExplorer()) {
						f_core.SelectText(predInput, setLastPos);
					}
				}
				
				return  false;				
			}
			break;
			
		case f_key.VK_END:
		case f_key.VK_RIGHT:
			if (sel[0]==sel[1] && sel[0]==input.value.length) {
				var nextInput=input._nextInput;
				
				if (nextInput) {
					if (keyCode==f_key.VK_END) {
						for(;nextInput._nextInput;nextInput=nextInput._nextInput);

						var vnextInputLength=nextInput.value.length;
						f_core.SelectText(nextInput, vnextInputLength);

					} else if (keyCode==f_key.VK_RIGHT && jsEvent.ctrlKey) {
						var vnextInputLength=nextInput.value.length;
						f_core.SelectText(nextInput, vnextInputLength);
					}
					
					nextInput.focus();
				}
				
				return  false;				
			}
			break;
			
		case f_key.VK_UP:
			if (typeof(this.fa_performStep)!="function") {
				break;
			}
			return this.fa_performStep(input, 1, input._min, input._max, input._step);			

		case f_key.VK_DOWN:
			if (typeof(this.fa_performStep)!="function") {
				break;
			}
			return this.fa_performStep(input, -1, input._min, input._max, input._step);
			
		case f_key.VK_PAGE_UP:
			if (typeof(this.fa_performSet)!="function" || isNaN(input._max)) {
				break;
			}
			return this.fa_performSet(input, input._max, input._min, input._max, input._step);
			
		case f_key.VK_PAGE_DOWN:
			if (typeof(this.fa_performSet)!="function" || isNaN(input._min)) {
				break;
			}
			return this.fa_performSet(input, input._min, input._min, input._max, input._step);
		}		
		
		return true;
	},
	/**
	 * @method protected abstract optional
	 */
	fa_performStep: f_class.OPTIONAL_ABSTRACT,
	/**
	 * @method protected abstract optional
	 */
	fa_performSet: f_class.OPTIONAL_ABSTRACT,
	/**
	 * @method private
	 */
	_onInputKeyPress: function(jsEvent, input) {
		var keyCode = jsEvent.keyCode;
		var charCode = jsEvent.charCode;
		
		var keyChar;
		
		if (!charCode) {
			keyChar = String.fromCharCode(keyCode);

		} else {
			keyChar = String.fromCharCode(charCode);
		}

		if (keyCode==f_key.VK_TAB) {
			// Deja traité .. normalement !
				
			f_core.Debug(fa_compositeEntry, "KeyPress: tab key");
			
			return true;
		}
				
		f_core.Debug(fa_compositeEntry, "KeyPress: keyCode="+keyCode+" charCode="+charCode+" shift="+jsEvent.shift+" ctrl="+jsEvent.ctrl+" alt="+jsEvent.alt+" keyChar="+keyChar+"("+((keyChar.length>0)?keyChar.charCodeAt(0):"")+") min="+input._min+" max="+input._max+" default="+input._default);
	
		if (f_core.IsInternetExplorer() || f_core.IsWebkit()) {
			if (keyCode < 32) {
				return true;
			}
			
		} else if (f_core.IsGecko()) {
			if (keyCode>0) {
				switch(keyCode) {
				case f_key.VK_UP:
				case f_key.VK_DOWN:
				case f_key.VK_PAGE_UP:
				case f_key.VK_PAGE_DOWN:
					return false;
				}
				
				return true;
			}
			keyCode=charCode;
		}
		
		// charCode=String.fromCharCode(keyCode);
		f_core.Debug(fa_compositeEntry, "KeyPress2: keyCode="+keyCode+" keyChar='"+keyChar+"' separators="+input._separators);
		
		var ret=this.fa_keyPressed(input, keyChar, jsEvent);
		if (ret===null) {
			ret=false;
			
			// Un separateur ou TAB ?
			var separators=input._separators;
			if (separators && separators.indexOf(keyChar)>=0) {
				// Un séparateur ou TAB !
				ret=this._onSeparatorPressed(input, true, jsEvent);
			}
		}
		
		if (ret) {
			return true;
		}
		return f_core.CancelJsEvent(jsEvent);
	},
	/**
	 * @method protected abstract
	 */
	fa_keyPressed: f_class.ABSTRACT,
	/**
	 * @method private
	 */
	_onInputBlur: function(input, jsEvent) {
		
		this.fa_formatInput(input, true);

		return true;
	},
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_formatInput: f_class.ABSTRACT,
	/**
	 * @method private
	 */
	_onSeparatorPressed: function(input, separator) {
		f_core.Debug(fa_compositeEntry, "_onSeparatorPressed on input '"+input.id+"' separator="+separator);
		
		this.fa_formatInput(input);
		
		if (separator) {
			var nextInput=input._nextInput;
			if (nextInput) {
				nextInput.focus();
			}

			// De toute facon on refuse la saisie du séparateur
			return false;
		}
		
		// C'est un TAB, on laisse faire ....
		return true;
	},
	/**
	 * @method protected
	 */
	fa_updateReadOnly: function() {
		var inputs=this._inputs;
		var readOnly=this._readOnly;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
		
			input.readOnly=readOnly;
		}
	},
	/**
	 * @method protected
	 */
	fa_updateDisabled: function() {
		var inputs=this._inputs;
		var disabled=this._disabled;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
		
			input.disabled=disabled;
		}
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		var inputs=this._inputs;
		if (!inputs) {
			return false;
		}

		return inputs[0];
	},
	
	f_setFocus: function() {
		var inputs=this._inputs;
		if (!inputs) {
			return false;
		}
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			
			try {
				input.focus();
				return true;
				
			} catch (ex) {
				f_core.Error(fa_compositeEntry, "f_setFocus: Exception while calling focus() of '"+input.id+"' component='"+this.id+"'.", ex);
			}			
		}
		
		return false;
	}
};
 
new f_aspect("fa_compositeEntry", __statics, __members, fa_disabled, fa_readOnly);
