/*
 * $Id: f_imageSubmitButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * class f_imageSubmitButton
 *
 * @class f_imageSubmitButton extends f_imageButton
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __members = {

	f_imageSubmitButton: function() {
		this.f_super(arguments);
		
		//this.onmousedown=f_core.CancelJsEventHandler;
		// this.onclick=f_core.CancelJsEventHandler;
	},
	
	/**
	 * @method protected
	 * @param f_event event
	 * @return Boolean
	 */
	f_imageButtonSelectEnd: function(event) {	
		/*	
		var form = f_core.GetParentForm(this);
		if (form) {
			f_core.Debug(f_imageSubmitButton, "f_performImageSelection: Submit form !");
			
		} else {
			f_core.Error(f_imageSubmitButton, "f_performImageSelection: FORM component was not found !");
		}
	*/
		
		if (window._rcfacesExiting) {
			return false;
		}
		
		if (window.f_shellManager) {
			var shell=f_shellManager.GetShell(this);
			if (shell) {
				var status=shell.f_getStatus();
				
				switch(status) {
				case f_shell.CLOSING_STATUS:
				case f_shell.ABOUT_TO_CLOSE_STATUS:
				case f_shell.CLOSED_STATUS:
				case f_shell.DESTROYING_STATUS:
					return false;
				}				
			}
		}
		
		f_core.SubmitEvent(event);
				
		return false;
	}
};

new f_class("f_imageSubmitButton", {
	extend: f_imageButton,
	members: __members
});
