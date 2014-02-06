/*
 * $Id: f_button.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * class f_button
 *
 * @class public f_button extends f_input, fa_immediate
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __members = {

	f_button: function() {
		this.f_super(arguments);

		this.f_setForcedEventReturn(f_event.SELECTION, true);
	},

	/**
	 * @method protected
	 */
	f_setDomEvent: function(type, target) {
	
		switch(type) {
		case f_event.SELECTION:
			target=this.f_getInput();
			break;
		}
		
		this.f_super(arguments, type, target);
	},
	
	/**
	 * @method protected
	 */
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION:
			target=this.f_getInput();
			break;
		}
		
		this.f_super(arguments, type, target);
	}
};

new f_class("f_button", {
	extend: f_input,
	aspects: [ fa_immediate ],
	members: __members
});

