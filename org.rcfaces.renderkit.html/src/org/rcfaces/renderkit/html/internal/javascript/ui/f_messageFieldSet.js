/*
 * $Id: f_messageFieldSet.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * class f_messageFieldSet
 *
 * @class public f_messageFieldSet extends f_fieldSet, fa_message1
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __members = {
	f_messageFieldSet: function() {
		this.f_super(arguments);
		
		this._normalText=this.f_getText();
	},
/*
	f_finalize: function() {
		// this._normalText=undefined; // string

		this.f_super(arguments);
	},
	*/
	/**
	 * @method protected
	 */
	fa_updateMessages: function() {
		var className=this.f_computeStyleClass();
		
		var currentMessage=this._currentMessage;
		if (currentMessage) {
			var cl=this.f_getStyleClassFromSeverity(currentMessage.f_getSeverity());	
			if (cl) {
				className+=" "+cl;
			}
		}
		
		if (this.className!=className) {
			this.className=className;
		}
		
		var text=null;
		if (currentMessage) {
			text=currentMessage.f_getSummary();
		}
		if (!text) {
			text=this._normalText;
		}
		
		this.f_setText(text);
	}
}
new f_class("f_messageFieldSet", null, null, __members, f_fieldSet, fa_message1);
