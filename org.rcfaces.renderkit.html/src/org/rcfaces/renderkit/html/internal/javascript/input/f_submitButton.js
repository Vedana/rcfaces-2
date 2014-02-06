/*
 * $Id: f_submitButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * f_submitButton class
 *
 * @class f_submitButton extends f_button
 */
var __members = {
	f_submitButton: function() {
		this.f_super(arguments);

		this.f_setForcedEventReturn(f_event.SELECTION, undefined);
	}
};

new f_class("f_submitButton", {
	extend: f_button,
	members: __members 
});
