/*
 * $Id: f_hyperLink.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * class f_hyperLink
 *
 * @class f_hyperLink extends f_input, fa_immediate, fa_value
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __members = {

	f_hyperLink: function() {
		this.f_super(arguments);

		this.f_setForcedEventReturn(f_event.SELECTION, false);
		
		var input=this.f_getInput();
		if (input.tagName.toLowerCase()=="a") {
			if (!input.href) {
				input.href=f_core.CreateJavaScriptVoid0();
			}
		}
		
		var d=f_core.GetAttribute(this, "disabled");
		if (d) {
			this.f_setDisabled(true);
		}
	},
	/**
	 * Returns the text of the link.
	 *
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return f_core.GetTextNode(this, true);
	},
	/**
	 * Set the text of the link.
	 *
	 * @method public
	 * @param String text
	 * @return void
	 */
	f_setText: function(text) {
		f_core.SetTextNode(this, text, this.f_getAccessKey());
		
		this.f_setProperty(f_prop.TEXT,text);
	},
	f_fireEvent: function(type, evt, item, value, selectionProvider, detail, stage) {
		if (type==f_event.SELECTION) {			
			if (this.f_isReadOnly() || this.f_isDisabled()) {
				return false;
			}
			
			if (!value) {
				value=this.f_getValue();
			}
		}	
		
		return this.f_super(arguments, type, evt, item, value, selectionProvider, detail, stage);
	}
};

new f_class("f_hyperLink", {
	extend: f_input,
	aspects: [ fa_immediate, fa_value ],
	members: __members
});
