/* 
 * $Id: f_comboGrid.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */


/**
 * 
 * @class f_comboGrid extends f_keyEntry, fa_dataGridPopup
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	
	/**
	 * @field private static final String
	 */
	_INPUT_ID_SUFFIX: "::input",

	/**
	 * @field private static final String
	 */
	_BUTTON_ID_SUFFIX: "::button",

	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:comboGrid
	 */
	_OnButtonMouseDown: function(evt) {
		var comboGrid=this._comboGrid;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if(evt.timeStamp - fa_dataGridPopup.LAST_OUTSIDE < 100){
			return false;
		}
	
		if (comboGrid.f_getEventLocked(evt)) {
			return false;
		}
		
		if (comboGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}
		
		comboGrid._onButtonMouseDown(evt);
		
		return f_core.CancelJsEvent(evt);		
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:comboGrid
	 */
	_OnButtonMouseUp: function(evt) {
		var comboGrid=this._comboGrid;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		comboGrid._onButtonMouseUp(evt);
		
		return f_core.CancelJsEvent(evt);		
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:comboGrid
	 */
	_OnButtonMouseOver: function(evt) {
		var comboGrid=this._comboGrid;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (comboGrid.f_getEventLocked(evt, false)) {
			return false;
		}
		
		if (comboGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}
		
		comboGrid._onButtonMouseOver(evt);
		
		return f_core.CancelJsEvent(evt);		
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:comboGrid
	 */
	_OnButtonMouseOut: function(evt) {
		var comboGrid=this._comboGrid;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		comboGrid._onButtonMouseOut(evt);
		
		return f_core.CancelJsEvent(evt);		
	}
};

var __members = {
	
	/**
	 * @field private boolean
	 */
	_buttonOver: undefined,
	
	/**
	 * @field private boolean
	 */
	_buttonDown: undefined,

	f_comboGrid: function() {
		this.f_super(arguments);
		
		var button=this.ownerDocument.getElementById(this.id+f_comboGrid._BUTTON_ID_SUFFIX);
		this._button=button;
		
		button._comboGrid=this;
		button.onmousedown=f_comboGrid._OnButtonMouseDown;
		button.onmouseup=f_comboGrid._OnButtonMouseUp;
		button.onmouseover=f_comboGrid._OnButtonMouseOver;
		button.onmouseout=f_comboGrid._OnButtonMouseOut;
	},

	f_finalize: function() {
			
		// this._buttonOver=undefined; // boolean
		// this._buttonDown=undefined; // boolean
	
		var button=this._button;
		if (button) {
			this._button=undefined; // HTMLImageElement
			
			button._comboGrid=undefined; // f_comboGrid
			
			button.onmousedown=null;
			button.onmouseup=null;
			button.onmouseover=null;
			button.onmouseout=null;

			f_core.VerifyProperties(button);
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	f_initializeInput: function() {
		return this.ownerDocument.getElementById(this.id+f_comboGrid._INPUT_ID_SUFFIX);
	},
	
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return void
	 */
	_onButtonMouseDown: function(jsEvent) {
		this._buttonDown=true;
		this.f_updateButtonStyle();		
	
		var menuOpened=this.f_isDataGridPopupOpened();
		
		f_core.Debug(f_comboGrid, "_onButtonMouseDown: menuOpened="+menuOpened);
		
		if (menuOpened) {
			this.f_closePopup(jsEvent);
			return
		}
		
		this.f_setFocus();
		this.f_openPopup(jsEvent);
	},
	/**
	 * @method private
	 * @param Event evt
	 * @return void
	 */
	_onButtonMouseUp: function(evt) {
		if (!this._buttonDown) {
			return;
		}
		this._buttonDown=false;
		this.f_updateButtonStyle();	
	},
	/**
	 * @method private
	 * @param Event evt
	 * @return void
	 */
	_onButtonMouseOver: function(evt) {
		this._buttonOver=true;
		this.f_updateButtonStyle();
	},
	/**
	 * @method private
	 * @param Event evt
	 * @return void
	 */
	_onButtonMouseOut: function(evt) {
		if (!this._buttonOver) {
			return;
		}
		
		this._buttonDown=false; // Obligé sinon sous IE on sait plus quand c'est relaché
		this._buttonOver=false;
		this.f_updateButtonStyle();	
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateButtonStyle: function() {
		var button=this._button;
		if (!button) {
			return;
		}
		
		var className="f_comboGrid_button";
		
		if (this.f_isDisabled()) {
		//	className+=" "+className+"_disabled";
		// porté par la classe principale 

		} else if (this._buttonDown) {
			className+=" "+className+"_selected";

		} else if (this._buttonOver) {
			className+=" "+className+"_hover";
		}
		
		if (button.className!=className) {
			button.className=className;
		}
	},

	/**
	 * @method protected
	 * @param Event jsEvent
	 * @param optional Boolean autoSelect
	 * @return void
	 */
	f_openPopup: function(jsEvent, autoSelect) {
		f_core.Debug(f_comboGrid, "f_openPopup: open popup");
		
		this.f_openDataGridPopup(jsEvent, null, autoSelect);
	},
	/**
	 * @method protected
	 * @param Event jsEvent
	 * @return void
	 */
	f_closePopup: function(jsEvent) {
		f_core.Debug(f_comboGrid, "f_closePopup: close popup");
			
		this.f_closeDataGridPopup(jsEvent);
	},
	/**
	 * @method protected
	 */
	f_getEventLocked: function(evt, showAlert, mask) {
		if (this.f_isDataGridPopupOpened()) {
			if (!mask) {
				mask=0;
			}	
			
			mask|=f_event.POPUP_LOCK;
		}

		f_core.Debug(f_comboGrid, "f_getEventLocked: menuOpened="+this.f_isDataGridPopupOpened()+" mask="+mask+" showAlert="+showAlert);

		return this.f_super(arguments, evt, showAlert, mask);
	},	
	/**
	 * @method protected
	 * @param f_event evt
	 * @return Boolean
	 */
	f_onCancelDown: function(evt) {
		var jsEvt=evt.f_getJsEvent();
		if (jsEvt.cancelBubble) {
			f_core.Debug(f_comboGrid, "_onCancelDown: Event has been canceled !");
			return true;
		}

		f_core.Debug(f_comboGrid, "_onCancelDown: Event keyCode="+jsEvt.keyCode);

		if (!f_core.IsInternetExplorer()) { // a supprimer quand IE7 8 auront disparu
		
			switch(jsEvt.keyCode) {
				
			case f_key.VK_DOWN:
			case f_key.VK_UP:
			case f_key.VK_ESPACE:
				if (this.f_isDataGridPopupOpened()) {
					return f_core.CancelJsEvent(jsEvt);
				}
			}
	}
		
		return this.f_super(arguments, evt);
	},
	/**
	 * @method private
	 * @param f_event evt
	 * @return Boolean
	 */
	f_onSuggest: function(evt) {
		var jsEvt=evt.f_getJsEvent();
		if (jsEvt.cancelBubble || this.f_isDisabled()) {
			f_core.Debug(f_comboGrid, "f_onSuggest: Event has been canceled !");
			return true;
		}		
		
		var menuOpened=this.f_isDataGridPopupOpened();
		if (menuOpened) {
			
			switch(jsEvt.keyCode) { //pb IE 
				case f_key.VK_DOWN:
				case f_key.VK_UP:
					return false;
			}
			
			// Aie aie aie
			this.f_closePopup(jsEvt);
			return true;
		}
		
		var ret = this.f_super(arguments, evt);
		if (!ret) {
			return ret;
		}
		
		switch(jsEvt.keyCode) {
		case f_key.VK_DOWN:
		case f_key.VK_UP:
			this.f_openPopup(jsEvt);

			return f_core.CancelJsEvent(jsEvt);
		}
		
		return ret;
	},

	/**
	 * @method protected
	 * @param f_event evt
	 * @return Boolean
	 * @override
	 */
	f_onKeyPress: function(evt) {
		var jsEvt=evt.f_getJsEvent();
		
		if (f_core.IsWebkit()) {
			switch(jsEvt.keyCode) {
			case f_key.VK_RETURN:
			case f_key.VK_ENTER:
				
				var now=new Date().getTime()-this._closePopupDate;
				
				if (now<100) {				
					return f_core.CancelJsEvent(jsEvt);
				}
				break;
			}
		}
		
		return this.f_super(arguments, evt);
	},

	/**
	 * @method private
	 * @param f_event event
	 * @return void
	 */
	f_onBlur: function(event) {
		f_core.Debug(f_comboGrid, "f_onBlur: formattedValue='"+this._formattedValue+"' (inputValue='"+this._inputValue+"')");
		
		var menuOpened=this.f_isDataGridPopupOpened();
		if (menuOpened) {
			return;
		}
		
		return this.f_super(arguments, event);
	}
};

new f_class("f_comboGrid", {
	extend: f_keyEntry,
	aspects: [ fa_dataGridPopup ],
	statics: __statics,
	members: __members
});
