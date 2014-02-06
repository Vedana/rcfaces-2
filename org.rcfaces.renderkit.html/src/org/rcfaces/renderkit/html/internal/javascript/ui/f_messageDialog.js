/*
 * $Id: f_messageDialog.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p><strong>f_messageDialog</strong> represents popup modal window.
 *
 * @class public final f_messageDialog extends f_dialog
 * @author Fred Lefevere-Laoide Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */
var __statics = {
	/**
	 * @field private static final
	 */
	_EVENTS: {
			selection: f_event.SELECTION
	},
	
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		width: 500,
		height: 300,
		dialogPriority: 0,
		styleClass: "f_messageDialog",
		backgroundMode: f_shell.GREYED_BACKGROUND_MODE
	},
	
    /**
     * @method private static
     * @param Event evt the event
     * @return Boolean
     * @context object:messageBox
     */
    _OnClick: function(evt) {
    	var button=this;
		var messageBox=button._messageBox;
		
		f_core.Debug(f_messageDialog, "_OnClick: entering "+this);

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (messageBox.f_getEventLocked(evt, true)) {
			f_core.Debug(f_messageDialog, "_OnClick : messageBox.f_getEventLocked(true)");
			return false;
		}
		
		f_core.Debug(f_messageDialog, "_OnClick: before messageBox.f_buttonOnClick(button);");
		messageBox.f_buttonOnClick(button, evt);
		
		return f_core.CancelJsEvent(evt);
    }

    /*
     * <p>js listener example</p>
     * dans le tag : SelectionListener="return ListenerExample(event);"
     *
     * @method public static
     * @param f_event evt
     * @return Boolean
     *
    ListenerExample: function(evt) {
    	// var value = evt.f_getValue();
    	return true;
    }
    */
};

var __members = {

	/**
	 * @field private String
	 */
	_text: undefined,

	/**
	 * @field private String
	 */
	_defaultValue: undefined,

	/**
	 * @field private Array
	 */
	_actions: undefined,

	/**
	 * <p>Construct a new <code>f_messageDialog</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param optional String title the title or HTMLElement the suporting v:init tag
	 * @param optional String text the text to display
	 * @param optional String defaultValue the value returned if the popup is closed without selecting a button
	 */
	f_messageDialog: function(title, text, defaultValue) {
		this.f_super(arguments, f_shell.PRIMARY_MODAL_STYLE | f_shell.MOVE_STYLE);
		
		if (this.nodeType==f_core.ELEMENT_NODE) {
			this._text=f_core.GetAttributeNS(this,"text");
			this._defaultValue=f_core.GetAttributeNS(this,"defaultValue");
			
			var imageURL=f_core.GetAttributeNS(this,"imageURL");
			if (imageURL) {
				this.f_setImageURL(imageURL);
			}

			var events=f_core.GetAttributeNS(this,"events");
			if (events) {
				this.f_initEventAtts(f_messageDialog._EVENTS, events);
			}

		} else {
			if (title) {
				this.f_setTitle(title);
			}
			this._text=text;
			this._defaultValue=defaultValue;
		}

   		this._actions = new Array();
	},

	/**
	 * <p>Destruct a new <code>f_messageDialog</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {		
		// this._text=undefined; // string
		// this._defaultValue=undefined; // string
		this._actions=undefined; // List<Object>
		//this._styleClass=undefined; // string
		//this._submitButton=undefined; // HtmlInputElement
		//this._imageURL=undefined; // String
		
		this._cleanInputs();

		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_getDefaultFeatures: function() {
		return f_messageDialog._DEFAULT_FEATURES;
	},
	/**
	 *  <p>Return the text.</p>
	 *
	 * @method public 
	 * @return String The text
	 */
	f_getText: function() {
		return this._text;
	},
	
	/**
	 *  <p>Sets the text.</p>
	 *
	 * @method public 
	 * @param String text the text
	 */
	f_setText: function(text) {
    	f_core.Assert((typeof(text)=="string"), "f_messageDialog.f_setText: Invalid parameter '"+text+"'.");
		this._text = text;
	},
	
	/**
	 *  <p>Return the defaultValue.</p>
	 *
	 * @method public 
	 * @return String The defaultValue
	 */
	f_getDefaultValue: function() {
		return this._defaultValue;
	},
	
	/**
	 *  <p>Sets the defaultValue.</p>
	 *
	 * @method public 
	 * @param String defaultValue the default Value
	 */
	f_setDefaultValue: function(defaultValue) {
    	f_core.Assert(typeof(defaultValue)=="string", "f_messageDialog.f_setDefaultValue: Invalid parameter '"+defaultValue+"'.");

		this._defaultValue = defaultValue;
	},
	
	/**
	 *  <p>Adds an action to the popup.</p>
	 *
	 * @method public 
	 * @param String value the value of the action
	 * @param String text the text displayed for the action
	 * @return optional Boolean disabled true if the button must be disabled
	 * @return optional Boolean submitButton true if the button is the default value for the messageDialog
	 * @return void
	 */
	f_addAction: function(value, text, disabled, submitButton) {
    	f_core.Assert((typeof(value)=="string"), "f_messageDialog.f_addAction: Invalid parameter value '"+value+"'.");
    	f_core.Assert((typeof(text)=="string"), "f_messageDialog.f_addAction: Invalid parameter text '"+text+"'.");
    	f_core.Assert(arguments.length < 3 || (typeof(disabled)=="boolean"), "f_messageDialog.f_addAction: Invalid parameter disabled '"+disabled+"'.");
    	f_core.Assert(arguments.length < 4 || (typeof(submitButton)=="boolean"), "f_messageBox.f_addAction: Invalid parameter submitButton '"+submitButton+"'.");
		
		this._actions.push({ 
			_text: text,
			_value: value,
			_disabled: disabled, 
			_submitButton: submitButton
		});
	},
	
	/**
	 *  <p>Return the array of actions.</p>
	 *
	 * @method public 
	 * @return Array The actions
	 */
	f_getActions: function() {
		return this._actions;
	},
	
	/**
	 *  <p>draw a message box. 
	 *  The first parameter is a callback that must take a String as a first parameter and a f_messageDialog as second parameter.
	 *  the callback can be null;
	 *  </p>
	 *
	 * @method public 
	 * @param Function callback The callback function to be called when the messageBox is closed
	 * @return void
	 */
	f_openMessage: function(callback) {
		f_core.Assert(!arguments.length || typeof(callback) == "function", "f_messageDialog.f_openMessage: Invalid Callback parameter ("+callback+")");
		
     	f_core.Debug(f_messageDialog, "f_openMessage: entering ("+callback+")");
		
		// If a callback is passed : clean the selection listeners and add this callback to the listeners
		if (callback) {
			var actionList=this.f_getActionList(f_event.SELECTION);
			if (actionList) {
				actionList.f_clearActions();
			}
			
			this.f_addEventListener(f_event.SELECTION, callback);
		}
		
		this.f_open();		
	},
	/**
	 *  <p>draw a message box.
	 *  </p>
	 *
	 * @method private 
	 * @param HTMLElement the base html element to construct the dialog
	 * @return void
	 */
	f_fillBody: function(base) {

		var docBase = base.ownerDocument;
		
		// form to catch the return
		var actForm = docBase.createElement("form");
		actForm.className = "f_messageDialog_form";
		f_core.AppendChild(base, actForm);		
		
		// Creation de la table
		var table = docBase.createElement("table");
		f_core.AppendChild(actForm, table);

		table.className = "f_messageDialog_body";

		this._buttons=new Array();
		
		//set size and pos
		table.style.width=this.f_getWidth()+"px";
		table.style.height=this.f_getHeight()+"px";
		table.cellPadding=0;
		table.cellSpacing=0;

		var tbod = docBase.createElement("tbody");
		f_core.AppendChild(table, tbod);
		
		// Creation de la ligne de texte
		var ligne = docBase.createElement("tr");
		f_core.AppendChild(tbod, ligne);
		ligne.className = "f_messageDialog_body_tr";
		ligne.style.height = (this.f_getHeight() - 60)+"px";
		
		// cell for image
		cell = docBase.createElement("td");
		f_core.AppendChild(ligne, cell);
		cell.className = "f_messageDialog_image_td";
		
		var imageURL=this.f_getImageResolvedURL();
		if (imageURL) {
			zone = docBase.createElement("img");
			zone.className="f_messageDialog_image"
			zone.src=imageURL;
			f_core.AppendChild(cell, zone);
		}

		// cell for text
		var cell = docBase.createElement("td");
		f_core.AppendChild(ligne, cell);

		cell.className = "f_messageDialog_text_td";
		
		var zone = docBase.createElement("span");
		f_core.AppendChild(cell, zone);
		zone.className ="f_messageDialog_text";
		
		var text=this._text;
		if (text) {
			//text=f_core.EncodeHtml(text);
			
			f_core.SetTextNode(zone, text);
		}

		// Creation de la ligne de boutons
		var ligne = docBase.createElement("tr");
		f_core.AppendChild(tbod, ligne);
		ligne.className = "f_messageDialog_actions_tr";
		ligne.style.height = "60px";
		
		cell = docBase.createElement("td");
		f_core.AppendChild(ligne, cell);

		cell.colSpan = 2;

		cell.className = "f_messageDialog_actions_td";
		cell.align = "center";
		
		var actTable = docBase.createElement("table");
		f_core.AppendChild(cell, actTable);

		var actTbod = docBase.createElement("tbody");
		f_core.AppendChild(actTable, actTbod);

		var actTr = docBase.createElement("tr");
		f_core.AppendChild(actTbod, actTr);
		
		var actions=this._actions;
		if (!actions.length) {
			var def = this._defaultValue;
			if (!def) {
				def = f_resourceBundle.Get(f_shell).f_get("OK_BUTTON");
			}
			this.f_addAction(def, def, false, true);
		}
		
		for (var i=0; i<actions.length; i++) {
			var action=actions[i];

			var cellb = docBase.createElement("td");
			f_core.AppendChild(actTr, cellb);			

			var button = docBase.createElement("input");
			
			if (action._submitButton) {
				button.type="submit";

//				actForm.onsubmit=f_messageDialog._OnClick;
//				this._submitButton=button;
				button.onclick=f_messageDialog._OnClick;

			} else {
				button.type="button";
				button.onclick=f_messageDialog._OnClick;
			}
			button.className= "f_messageDialog_button";
			button.disabled=action._disabled;
			button._value=action._value;
			button.value=action._text;

			button._messageBox= this;
			this._buttons.push(button);			
	
			f_core.AppendChild(cellb, button);
		}
	},
	f_preDestruction: function() {
		this._cleanInputs();

		this.f_super(arguments);
	},
	/**
	 * @method private
	 * @return void
	 */
	_cleanInputs: function() {
	
		var buttons=this._buttons;
		if (buttons) {
			this._buttons=undefined;
	
			// Buttons cleaning
			for (var i=0; i<buttons.length; i++) {
				var button = buttons[i];
				button._messageBox=undefined; // f_messageBox
				button._value=undefined; // any
				button.onclick=null;
				button.onfocusin=null;
				
				f_core.VerifyProperties(button);
			}
		}	
	},
	/**
	 *  <p>callBack that will call the user provided callBack</p>
	 *
	 * @method protected 
	 * @param HTMLInputElement selectedButton The button that was pushed
	 * @param Event jsEvent
	 * @return void
	 */
	f_buttonOnClick: function(selectedButton, jsEvent) {
     	f_core.Debug(f_messageDialog, "f_buttonOnClick: entering ("+selectedButton+")");
	
		// var messageBox=base._messageBox;
		var value=selectedButton._value;
		
		var ret = this.f_fireEvent(f_event.SELECTION, jsEvent, null, value);
		if (ret===false) {
			this.f_getShellManager().f_clearPendingShells();
		}
		
		this.f_close();
	},
	

	/**
	 *  <p>Gets the image URL.</p>
	 *
	 * @method public 
	 * @return String imageURL 
	 */
	f_getImageURL: function() {
		return this._imageURL;
	},
	
	/**
	 *  <p>Gets the image resolved URL.</p>
	 *
	 * @method public 
	 * @return String imageURL 
	 */
	f_getImageResolvedURL: function() {
		if (!this._imageURL) {
			return null;
		}
		
		return f_env.ResolveContentUrl(this._imageURL);
	},
	/**
	 *  <p>Sets the image URL.</p>
	 *
	 * @method public 
	 * @param String imageURL  (or <code>null</code>)
	 * @return void
	 */
	f_setImageURL: function(imageURL) {
    	f_core.Assert(imageURL===null || typeof(imageURL)=="string", "f_shell.f_setImageURL: Invalid parameter '"+imageURL+"'.");
    	
		this._imageURL = imageURL;
	},
	
	/**
	 * @method public
	 * @return String
	 */
	_toString: function() {
		var ts = this.f_super(arguments);
		ts = ts + "\n[f_messageDialog title='"+this._title+"' text='"+this._text+"' defaultValue='"+this._defaultValue+"']";
		return ts;
	}
}

new f_class("f_messageDialog", {
	extend: f_dialog,
	statics: __statics,
	members: __members
});
