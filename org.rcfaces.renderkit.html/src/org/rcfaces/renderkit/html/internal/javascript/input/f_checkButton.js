/*
 * $Id: f_checkButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * class f_checkButton
 *
 * @class public f_checkButton extends f_button
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __statics = {
	/**
	 * @field hidden static final String
	 */
	_INPUT_ID_SUFFIX: "::input",
	
	/**
	 * @field private static final String
	 */
	_TEXT_ID_SUFFIX: "::text"
	
};

var __members = {
	f_finalize: function() {
		var labelComponent=this._label;
		if (labelComponent) {
			this._label=undefined;
			
			f_core.VerifyProperties(labelComponent);			
		}
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_initializeInput: function() {
		return this.ownerDocument.getElementById(this.id+f_checkButton._INPUT_ID_SUFFIX);
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getLabel: function() {
		var label=this._label;
		if (label!==undefined) {
			return label;
		}
		
		label=this.ownerDocument.getElementById(this.id+f_checkButton._TEXT_ID_SUFFIX);
		this._label=label;
		
		return label;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isSelected: function() {
		var input=this.f_getInput();
		
		f_core.Assert(input, "f_checkButton.f_isSelected: Input is not found for selected property !");

		return (!!input.checked);
	},

	/**
	 * @method public
	 * @param Boolean set
	 * @return void
	 */
	f_setSelected: function(set) {
		set=(set!==false);
	
		var input=this.f_getInput();

		f_core.Assert(input, "f_checkButton.f_setSelected: Input is not found for selected property !");

		if (input.checked==set) {
			return;
		}
		
		input.checked = set;
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		var label=this.f_getLabel();
		if (!label) {
			return this.f_super(arguments);
		}

		return f_core.GetTextNode(label);
	},

	/**
	 * @method public
	 * @param String text
	 * @return void
	 */
	f_setText: function(text) {
		var label=this.f_getLabel();
		if (!label) {
			this.f_super(arguments, text);
			return;
		}
		
		if (text==this.f_getText()) {
			return;
		}
		
		f_core.SetTextNode(label, text);
		this.f_setProperty(f_prop.TEXT,text);
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
		// On sérialise systematiquement car il faut traiter le cas du disabled
		var input=this.f_getInput();
		
		this.f_setProperty(f_prop.SELECTED, (input.checked)?input.value:"");
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @return Object The value associated.
	 */
	f_getValue: function() {
		return this.f_isSelected();
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @param Object value
	 * @return Boolean If value is recognized.
	 */
	f_setValue: function(value) {
		this.f_setSelected(!!value);
	}
	/**
	 * @method protected
	 *
	fa_getTabIndexElement: function() {
		return this;
	}
	*/
};

new f_class("f_checkButton", {
	extend: f_button,
	members: __members,
	statics: __statics
});
