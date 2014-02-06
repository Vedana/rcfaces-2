/*
 * $Id: f_imageResetButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * A reset button, resets the form which contains itself.
 *
 * @class f_imageResetButton extends f_imageButton
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __members = {

	/**
	 * @method protected
	 * @param f_event event
	 * @return Boolean
	 */
	f_imageButtonSelectEnd: function(event) {
		var form = f_core.GetParentForm(this);
		if (form) {
			form.reset();
			
		} else {
			f_core.Error(f_imageResetButton, "f_performImageSelection: FORM component was not found !");
		}
		
		return true;
	}
}

new f_class("f_imageResetButton", {
	extend: f_imageButton,
	members: __members
});
