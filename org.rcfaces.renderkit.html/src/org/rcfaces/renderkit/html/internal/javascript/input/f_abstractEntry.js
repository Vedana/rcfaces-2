/*
 * $Id: f_abstractEntry.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * f_abstractEntry class
 *
 * @class public abstract f_abstractEntry extends f_input, fa_required, fa_selectionProvider<f_textSelection>, fa_subMenu, fa_immediate
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __statics = {
	/**
	 * @field private static final String
	 */
	_TEXT_MENU_ID: "#text"
};

var __members = {
/*
	f_abstractEntry: function() {
		this.f_super(arguments);
	},
*/
/*
	f_finalize: function() {
		// this._emptyMessage=undefined; // string
		// this._requiredInstalled=undefined; // boolean
		// this._emptyMessageInstalled=undefined; // boolean
		
		// this._emptyMessageShown=undefined; // boolean
		
		this.f_super(arguments);
	},
*/
	/**
	 * @method protected
	 * @return void
	 */
	f_initializeOnFocus: function() {
		this.f_super(arguments);
				
		this._emptyMessage=f_core.GetAttributeNS(this, "emptyMessage");
		if (this._emptyMessage) {
			this._emptyMessageShown=f_core.GetBooleanAttributeNS(this.f_getInput(), "emptyMessageShown");
		}
		
		if (this._emptyMessage) {
			this.f_installEmptyMessageCallbacks();
		}

		// On peut pas le mettre dans le f_setDomEvent, la profondeur de la pile ne le permet pas !
		this.f_insertEventListenerFirst(f_event.KEYPRESS, this.f_performSelectionEvent);		
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_installEmptyMessageCallbacks: function() {
		if (this._emptyMessageInstalled) {
			return;
		}
		this._emptyMessageInstalled=true;
		
		this.f_insertEventListenerFirst(f_event.FOCUS, this._messageFocusEvent);
		this.f_insertEventListenerFirst(f_event.BLUR, this._messageBlurEvent);
	},
	f_update: function() {

		var menu=this.f_getSubMenuById(f_abstractEntry._TEXT_MENU_ID);
		if (menu) {
			this.f_insertEventListenerFirst(f_event.MOUSEDOWN, this._performMenuMouseDown);
		}
		
		if (this.f_isRequired()) {
			this.fa_updateRequired();
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_serialize: function() {
		
		this.f_serializeValue();
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_serializeValue: function() {
		if (this.f_isDisabled()) {
			this.f_setProperty(f_prop.TEXT, this.f_getText());

		} else {
			// Le probleme est que le TEXT peut persister ... 
			// et que la valeur soit modifiée par l'utilisateur ...
			this.f_setProperty(f_prop.TEXT);
		}		
		
		if (this._emptyMessageShown) {
			// On remet la zone à vide
			this.f_getInput().value="";
		}
	},
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			return;
		}
		
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			return;
		}
		
		this.f_super(arguments, type, target);
	},
	/**
	 * @method protected
	 * @param f_event evt
	 * @return Boolean
	 */
	f_performSelectionEvent: function(evt) {
		if (this.f_isDisabled()) {
			return;
		}
		
		if (this.f_isActionListEmpty(f_event.SELECTION)) {
			return;
		}
		
		var jsEvent=evt.f_getJsEvent();
		if (!jsEvent) {
			return;
		}
		
		if (!jsEvent.ctrlKey) {
			return;
		}
			
		var code=jsEvent.keyCode;
	
		if (code!=f_key.VK_RETURN && code!=f_key.VK_ENTER) { // RETURN ou ENTER
			return;
		}

		evt.f_preventDefault();

		this.f_fireEvent(f_event.SELECTION, jsEvent);
		
		return false;
	},
	/**
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_performMenuMouseDown: function(event) {		
		var evt=event.f_getJsEvent();
		
		var sub=f_core.IsPopupButton(evt);
		if (!sub) {
			return true;
		}
		
		var menu=this.f_getSubMenuById(f_abstractEntry._TEXT_MENU_ID);
		if (menu) {
			menu.f_open(this, {
				position: f_menu.MOUSE_POSITION
				}, this, evt);
		
			return event.f_preventDefault();
		}
		
		return true;
	},
	/**
	 * @method private
	 */
	_messageFocusEvent: function() {
		if (this._emptyMessageShown) {
			this._emptyMessageShown=undefined;
		
			this.f_getInput().value="";
			
			this.f_updateStyleClass();
		}
	},
	/**
	 * @method private
	 */
	_messageBlurEvent: function() {
		if (this._emptyMessage && this.f_getText()=="") {
			this._emptyMessageShown=true;
			
			this.value=""; // Evite des effets desagreables
			
			this.f_updateStyleClass();
			
			this.value=this._emptyMessage;
		}
	},
	/**
	 * @method protected
	 * @overrided
	 */
	f_computeStyleClass: function(postSuffix) {
		var clz=this.f_super(arguments, postSuffix);
		
		if (this._emptyMessageShown) {
			clz+=" "+this.f_getMainStyleClass()+"_empty_message";
		}
		
		return clz;
	},
	/**
	 * @method public
	 * @return f_textSelection An object which defines fields 'text', 'start' and 'end'
	 */
	f_getSelection: function() {
		// Retourne deux entiers qui positionnent le debut et la fin de la selection
		var pos=f_core.GetTextSelection(this);
		
		var value=this.f_getText();
		if (!pos) {
			return new f_textSelection(0, value.length, value);
		}
		return new f_textSelection(pos[0], pos[1]-pos[0], value.substring(pos[0], pos[1]));
	},
	/**
	 * @method public
	 * @param f_textSelection selection An object which defines fields 'start' and 'end'
	 * @return optional Boolean show If possible, show the selection.
	 * @return void
	 */
	f_setSelection: function(selection, show) {
		// C'est un object avec un champ "start" et eventuellement un champ "end" >= "start"
		f_core.Assert(selection instanceof f_textSelection, "f_abstractEntry.f_setSelection: Selection must be an object which defines 'start' and 'end' field with positive numbers.");
		
		var start=selection.f_getStart();
		var end=selection.f_getLength();
		if (!end || end<0) {
			end=start;
		} else {
			end+=start;
		}
		
		f_core.Debug(f_abstractEntry, "f_setSelection: Set selection start="+start+" end="+end+".");
		
		f_core.SelectText(this.f_getInput(), start, end);
		
		if (show) {
			this.scrollIntoView();
		}
	},
	/**
	 * @method public
	 * @param Boolean show If possible, show the selection.
	 * @return void
	 */
	f_selectAll: function(show) {
		this.f_getInput().select();
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	fa_componentCaptureMenuEvent: function() {
		return null;
	},
	/**
	 * @method protected
	 * @return void
	 */
	fa_updateRequired: function() {
		// XXXXXX @TODO this._installRequiredValidator();
		this.f_updateStyleClass();
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getEmptyMessage: function() {
		return this._emptyMessage;
	},	
	/**
	 * @method public
	 * @param String message
	 * @return void
	 */
	f_setEmptyMessage: function(message) {
		this._emptyMessage=message;
		if (message) {
			this.f_installEmptyMessageCallbacks();
		}		
	},
	/**
	 * @method public abstract
	 * @return Number
	 */
	f_getMaxTextLength: f_class.ABSTRACT
};

new f_class("f_abstractEntry", {
	extend: f_input,
	aspects: [ fa_required, fa_selectionProvider, fa_subMenu, fa_immediate ],
	members: __members,
	statics: __statics
});
