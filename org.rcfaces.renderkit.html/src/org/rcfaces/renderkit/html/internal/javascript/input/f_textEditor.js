/*
 * $Id: f_textEditor.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * f_textEditor class
 *
 * @class f_textEditor extends f_component, fa_disabled, fa_readOnly
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	
	/**
	 * @field public static final String
	 */
	TEXT_HTML_MIME_TYPE: "text/html",
	
	/**
	 * @field public static final String
	 */
	TEXT_PLAIN_MIME_TYPE: "text/plain",

	/**
	 * @field private static final String[]
	 */
	_TEXT_STYLES: ["font-weight", "font-size", "font-family", "font-style", "text-decoration", "color", "background-color"],
	
	/**
	 * @field private static final Map<string, number>
	 */
	_BUTTON_STATE: {
		bold: 0x001,
		italic: 0x002,
		underline: 0x004,
		subscript: 0x008,
		superscript: 0x010,
		justifyleft: 0x020,
		justifycenter: 0x040,
		justifyright: 0x080,
		justifyfull: 0x100,
		strikethrough: 0x200
	},
	
	/**
	 * @field private static final Map<string, number>
	 */
	_BUTTON_ENABLED: {
		undo: 0x0001,
		redo: 0x0002,
		indent: 0x0004,
		outdent: 0x008,
		copy: 0x010,
		cut: 0x020,
		paste: 0x040,
		decreasefontsize: 0x080,
		increasefontsize: 0x100,
		insertorderedlist: 0x200,
		insertunorderedlist: 0x400
	},
	
	
	/**
	 * @field private static final Map<string, number>
	 */
	_PLAIN_TEXT_ENABLED: {
		undo: true,
		redo: true,
		copy: true,
		cut: true,
		paste: true
	},
	
	/**
	 * @field private static final Number
	 */
	_BUTTONS_UPDATE_TIMER: 500,
	
	/**
	 * @field private static Array[]
	 */
	_TextEditors: undefined,
	
	/**
	 * @method hidden static
	 * @context object:this
	 */
	_OnLoad: function(textEditor) {
		try {
			f_textEditor.f_getClassLoader().f_init(textEditor, false, true);
			
			textEditor._onLoad();
			
		} catch (x) {			
			f_core.Error(f_textEditor, "_OnLoad: load exception on textEditor "+textEditor.id, x);
		}
	},

	/**
	 * @method hidden static
	 * @param String textEditorId
	 * @param f_textEditorImageButton button
	 * @return void
	 */
	RegisterTextEditorButton: function(textEditorId, button) {
		f_core.Debug(f_textEditor, "RegisterTextEditorButton: register button '"+button.id+"' for editor '"+textEditorId+"'.");
		
		var textEditors=f_textEditor._TextEditors;
		if (!textEditors) {
			textEditors=new Object;
			f_textEditor._TextEditors=textEditors;
		}
		
		var buttons=textEditors[textEditorId];
		if (!buttons) {
			buttons=new Array;
			textEditors[textEditorId]=buttons;
		}
		
		buttons.push(button);
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_textEditor._TextEditors=undefined;
	},
	
	/**
	 * @method hidden static
	 * @param String textEditorId
	 * @param f_textEditorImageButton button
	 * @return void
	 */
	PerformCommand: function(textEditorId, button, parameter) {
		f_core.Assert(typeof(textEditorId)=="string", "f_textEditor.PerformCommand: Invalid textEditorId parameter ("+textEditorId+")");
		f_core.Assert(button && button.f_getType, "f_textEditor.PerformCommand: Invalid button parameter ("+button+")");
		
		var textEditors=f_textEditor._TextEditors;
		if (!textEditors) {
			f_core.Debug(f_textEditor, "PerformCommand: No registred text editors.");
			return;
		}

		var buttons=textEditors[textEditorId];
		if (!buttons) {
			f_core.Debug(f_textEditor, "PerformCommand: No buttons for textEditor '"+textEditorId+"'.");
			return;
		}
		
		var textEditor=buttons._textEditor;
		if (textEditor===undefined) {
			textEditor=f_core.GetElementByClientId(textEditorId);
			buttons._textEditor=textEditor;
		}
		
		if (!textEditor) {
			f_core.Error(f_textEditor, "PerformCommand: Can not find textEditor '"+textEditorId+"'.");
			return;
		}
		
		textEditor._performButtonCommand(button.f_getType(), parameter);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context event:evt
	 */
	_OnFocus: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		var win;
		if (!this.nodeType) {
			// Le this est la window !
			win=f_core.GetWindow(evt.srcElement);
			
		} else {
			win=f_core.GetWindow(this);
		}
		
		var textEditor=win.frameElement;
		
		return textEditor._onFocus(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context event:evt
	 */
	_OnBlur: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		var win;
		if (!this.nodeType) {
			// Le this est la window !
			win=f_core.GetWindow(evt.srcElement);
			
		} else {
			win=f_core.GetWindow(this);
		}
		
		var textEditor=win.frameElement;
		
		return textEditor._onBlur(evt);
	}
};

var __members = {
	f_textEditor: function() {
		this.f_super(arguments);
		
		var mimeType=f_core.GetAttributeNS(this,"mimeType");
		if (!mimeType) {
			mimeType=f_textEditor.TEXT_HTML_MIME_TYPE;
		}
		
		this._mimeType=mimeType;		
		
		if (!this._loadInitialized) { // Gestion du Javascript Collector
			if (this._loaded) { // Déjà chargé !
				this._onLoad();
				
			} else { // On attend !
				this.onload=f_textEditor._OnLoad;
			}		
		}
				
		f_core.Assert(mimeType==f_textEditor.TEXT_HTML_MIME_TYPE || 
						mimeType==f_textEditor.TEXT_PLAIN_MIME_TYPE, "f_textEditor: Unsupported text editor mime type ("+this._mimeType+")");
	},
	f_finalize: function() {
//		this._editorStates=undefined; // number
//		this._editorEnabled=undefined; // number

		this.onload=null; // Positionné par le HTML !
		
		var contentDocument=this._contentDocument;
		if (contentDocument) {
			this._contentDocument=undefined;
		}

		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			
			window.clearInterval(timerId);
		}
		
		this._contentWindow=undefined; // Window
//		this._mimeType=undefined; // String
		// this._focused=undefined; // boolean
		
		// this._autoTab=undefined;  // boolean
		// this._requiredInstalled=undefined; // boolean
		
		this._onUnLoad();
		
		this.f_super(arguments);
	},
	/**
	 * @method private
	 * @return void
	 */
	_onLoad: function() {
		if (this._loadInitialized) {
			return;
		}
		this._loadInitialized=true;
	
		f_core.Debug(f_textEditor, "_onLoad: Initialize textEditor");
		this.onload=null;
		
		var contentWindow=this.contentWindow;
		this._contentWindow=contentWindow;

		var contentDocument;
		if (f_core.IsInternetExplorer()) {
			contentDocument=contentWindow.document;
			
		} else {
			contentDocument=this.contentDocument;
		}
		
		this._contentDocument=contentDocument;
		
		var text=f_core.GetAttributeNS(this,"text");
		if (text) {
			this.f_setText(text);
		}
		
		f_core.AddEventListener(contentDocument, "focus", f_textEditor._OnFocus);
		f_core.AddEventListener(contentDocument, "blur", f_textEditor._OnBlur);
		
		contentDocument.designMode="on";
		
		this.f_updateButtons(true);
		
		var self=this;
		this._unloadFrame=function() {
			try {
				self._onUnLoad();
				
			} catch (x) {			
				// Le Log peut etre en vrac ....
				//f_core.Error(f_textEditor, "_OnUnLoad: load exception on textEditor "+textEditor.id, x);
			}
			
			self=null;
		}		
		
		f_core.AddEventListener(contentWindow, "unload", this._unloadFrame);
	},
	/**
	 * @method private
	 * @return void
	 */
	_onUnLoad: function() {
		var unloadFrame=this._unloadFrame;
		this._unloadFrame=undefined; // function

		if (window._rcfacesExiting) {
			return;
		}		

// On Log pas, les classes sont en vrac ...
//		f_core.Debug(f_textEditor, "_onUnLoad: Unload textEditor");
		
		var contentWindow=this._contentWindow;
		if (contentWindow) {
			this._contentWindow=undefined;
			
			if (unloadFrame) {
				f_core.RemoveEventListener(contentWindow, "unload", unloadFrame);	
			}
		}
		
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return;
		}
		this._contentDocument=undefined;
		
		contentDocument.designMode="off";

		f_core.RemoveEventListener(contentDocument, "focus", f_textEditor._OnFocus);
		f_core.RemoveEventListener(contentDocument, "blur", f_textEditor._OnBlur);					
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return void
	 */
	_onFocus: function(jsEvent) {
		f_core.Debug(f_textEditor, "_onFocus: Get focus");
		
		if (this._focused) {
			return;
		}
		this._focused=true;
		
		var self=this;
		if (!this._timerId) {
			this._timerId=window.setInterval(function() {
				try {
					self.f_updateButtons();
	
				} catch (x) {
					f_core.Debug(f_textEditor, "_onFocus.timer: Exception into updateButtons method.", x);				
				}
				
			}, f_textEditor._BUTTONS_UPDATE_TIMER);
		}		
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return void
	 */
	_onBlur: function(jsEvent) {
		f_core.Debug(f_textEditor, "_onFocus: Lost focus");		
		
		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			
			window.clearInterval(timerId);
		}
		
		if (!this._focused) {
			return;
		}
		this._focused=undefined;
	},
	/*
	f_update: function() {
		
		this.f_super(arguments);
	},
	*/
	f_serialize: function() {
		var contentDocument=this._contentDocument;	
		if (contentDocument) {
			this.f_setProperty(f_prop.VALUE, this.f_getText());
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
		
		switch(this._mimeType) {
		case f_textEditor.TEXT_PLAIN_MIME_TYPE:
			return f_core.GetTextNode(contentDocument.body);
			
		default:
			return contentDocument.body.innerHTML;
		}		
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
		
		return contentDocument.body;
	},
	/**
	 * @method public
	 * @param String text
	 * @return void
	 */
	f_setText: function(text) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return;
		}

		switch(this._mimeType) {
		case f_textEditor.TEXT_PLAIN_MIME_TYPE:
			f_core.SetTextNode(contentDocument.body, text);
			return;
			
		default:
			contentDocument.body.innerHTML=text;				
		}
	},
	/**
	 * @method public
	 * @param optional Number range
	 * @return void
	 */
	f_toggleBold: function(range) {
		this._execCommand("Bold", range);
	},
	/**
	 * @method public
	 * @param optional Number range
	 * @return void
	 */
	f_toggleItalic: function(range) {
		this._execCommand("Italic", range);
	},
	/**
	 * @method public
	 * @param optional Number range
	 * @return void
	 */
	f_toggleUnderline: function(range) {
		this._execCommand("Underline", range);
	},
	/**
	 * @method private
	 * @param String command
	 * @param optional Number range
	 * @param optional param
	 * @return void
	 */
	_execCommand: function(command, range, param) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return;
		}
		
		if (typeof(range)=="number") {
			f_core.SelectText(contentDocument.body, range, range);

		} else if ((range instanceof Array) && range.length>1) {
			f_core.SelectText(contentDocument.body, range[0], range[1]);			
		}
		
		command=command.charAt(0).toUpperCase()+command.substring(1);
		
		contentDocument.execCommand(command, false, param);
	},
	_queryCommandState: function(command, param) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
	
		switch(this._mimeType) {
		case f_textEditor.TEXT_PLAIN_MIME_TYPE:
			return;
		}
				
	//	command=command.charAt(0).toUpperCase()+command.substring(1);
	
//		f_core.Debug(f_textEditor, "_queryCommandState: Query command: '"+command+"' parameter='"+param+"'.");
		
		var ret=contentDocument.queryCommandState(command, false, param);

//		f_core.Debug(f_textEditor, "_queryCommandState: Query command: '"+command+"' => "+ret);
		
		return ret;
	},
	_queryCommandEnabled: function(command) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
		
		switch(this._mimeType) {
		case f_textEditor.TEXT_PLAIN_MIME_TYPE:
			if (!f_textEditor._PLAIN_TEXT_ENABLED[command]) {
				return null;
			}
		}
		
		
	//	command=command.charAt(0).toUpperCase()+command.substring(1);
	
//		f_core.Debug(f_textEditor, "_queryCommandEnabled: Query command: '"+command+"'.");
		
		var ret= contentDocument.queryCommandEnabled(command);

//		f_core.Debug(f_textEditor, "_queryCommandEnabled: Query command: '"+command+"' => "+ret);
		
		return ret;
	},
	_queryCommandValue: function(command) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
		
	//	command=command.charAt(0).toUpperCase()+command.substring(1);
	
//		f_core.Debug(f_textEditor, "_queryCommandValue: Query command: '"+command+"'.");
		
		var ret=contentDocument.queryCommandValue(command);

	//	f_core.Debug(f_textEditor, "_queryCommandValue: Query command: '"+command+"' => "+ret);
		
		return ret;
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_computeStyle0: function(position) {
		var contentDocument=this._contentDocument;
		if (!contentDocument) {
			return null;
		}
		
		var element=null;
		var style=null;	

		if (f_core.IsInternetExplorer()) {
			element=contentDocument.body.createTextRange().parentElement;

			if (element) {
				style=component.currentStyle;
			}

		} else if (contentDocument.createRange) {
			var selection=this._contentWindow.getSelection();
			
			if (selection && selection.rangeCount) {
				var range=selection.getRangeAt(0);
				
				var element=range.startContainer;
				for(;element.nodeType==3;element=element.parentNode);
				
			//	alert("Element= "+element.tagName+" "+element.offsetWidth);
				
				var computedStyle=contentDocument.defaultView.getComputedStyle(element, '');

				style=new Object;				
				var styles=f_textEditor._TEXT_STYLES;
				for(var i=0;i<styles.length;i++) {
					var styleName=styles[i];
					style[styleName]=computedStyle.getPropertyValue(styleName);
				}
			}
			
			selection.dettach();
		}
		
		f_core.Debug(f_textEditor, "f_computeStyle: element="+element.id+" style="+style);
		
		return style;
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_computeStyle: function(position) {
		var style= {
			bold: this._queryCommandState("Bold", position),
			italic: this._queryCommandState("Italic", position),
			underline: this._queryCommandState("Underline", position)
		}
		
		return style;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_updateButtons: function(forceEnabled) {
		if (this.f_isDisabled()) {
			return;
		}
		
		var textEditors=f_textEditor._TextEditors;
		if (!textEditors) {
			f_core.Debug(f_textEditor, "f_updateButtons: No registred text editors.");
			return;
		}

		var buttons=textEditors[this.id];
		if (!buttons) {
			f_core.Debug(f_textEditor, "f_updateButtons: No buttons for textEditor '"+this.id+"'.");
			return;
		}
		
		var oldEditorStates=this._editorStates;
		var oldEditorEnabled=this._editorEnabled;
		
		var editorStates=0;
		var editorEnabled=0;
		
		for(var i=0;i<buttons.length;i++) {
			var button=buttons[i];
			var type=button.f_getType();
			
			var mask=f_textEditor._BUTTON_STATE[type];
			if (mask) {
				var state=this._queryCommandState(type);
				
				editorStates|=(state)?mask:0;
				
				if (state ^ ((oldEditorStates & mask)>0)) {
					// Different !
					
					f_core.Debug(f_textEditor, "f_updateButtons: State changed for type '"+type+"' => "+state);
					button.f_setSelected(state);
				}
				
				if (forceEnabled) {
					button.f_setDisabled(false);
				}
				
				continue;
			}
	
				
			var mask=f_textEditor._BUTTON_ENABLED[type];
			if (mask) {
				var state=this._queryCommandEnabled(type);
				
				editorEnabled|=(state)?mask:0;
				
				if (forceEnabled || (state ^ ((oldEditorEnabled & mask)>0))) {
					// Different !
					
					f_core.Debug(f_textEditor, "f_updateButtons: Enabled changed for type '"+type+"' => "+state);

					button.f_setDisabled(!state);
				}
				
				continue;
			}
			
			switch(type) {				
			case "fontname":
			case "fontsize":
				var state=this._queryCommandEnabled(type);
				if (state) {
					button.f_setValue(this._queryCommandValue(type));
					
					if (forceEnabled) {
						button.f_setDisabled(false);
					}
				}
				break;
/*				
			case "fontsize":
				var value=this._queryCommandValue(type);
				if (value) {
					button.f_setValue("");
					return;
				}
				button.f_setValue(f_textEditor._FONT_SIZES[value]);
				return;
				*/
			}
		}

		this._editorStates=editorStates;
		this._editorEnabled=editorEnabled;
		
		f_core.Debug(f_textEditor, "f_updateButtons: Final state="+editorStates+" enabled="+editorEnabled);
	},
	_performButtonCommand: function(type, param) {
		f_core.Assert(typeof(type)=="string", "f_textEditor._performButtonCommand: Invalid type parameter ("+type+")");
		
		f_core.Debug(f_textEditor, "_performButtonCommand: type='"+type+"' param='"+param+"'.");

		var mask=f_textEditor._BUTTON_STATE[type] || f_textEditor._BUTTON_ENABLED[type];
		if (!mask) {
			switch(type) {
			case "fontname":
			case "fontsize":
				mask=true;
				break;		
			}
		}

		if (mask) {
			this._execCommand(type, null, param);

			this.f_updateButtons();
		}
		
		var contentWindow=this._contentWindow;
		if (contentWindow) {
			contentWindow.focus();
		}
	}
}

new f_class("f_textEditor", {
	extend: f_component,
	aspects: [ fa_disabled, fa_readOnly ],
	statics: __statics,
	members: __members 
});
