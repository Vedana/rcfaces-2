/*
 * $Id: f_textEditorImageButton.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * class f_textEditorImageButton
 *
 * @class f_textEditorImageButton extends f_imageButton, fa_selected
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {
	f_textEditorImageButton: function() {
		this.f_super(arguments);
		
		this._type=f_core.GetAttributeNS(this,"type");
		this._for=f_core.GetAttributeNS(this,"for");
	},
	/*
	f_finalize: function() {
		// this._for=undefined; // string
		// this._type=undefined; // string
		
		this.f_super(arguments);
	},
	*/
	f_update: function() {
		this.f_super(arguments);
		
		f_textEditor.RegisterTextEditorButton(this._for, this);		
	},
	f_performImageSelection: function() {
		var type=this._type;
		if (!type) {
			return false;
		}
			
		f_textEditor.PerformCommand(this._for, this);

		return false;
	},
	fa_updateSelected: function() {
		this._updateImage();
	},	
	/**
	 * @method public
	 * @return String
	 */
	f_getType: function() {
		return this._type;
	}
}

new f_class("f_textEditorImageButton", {
	extend: f_imageButton,
	aspects: [ fa_selected ],
	members: __members 
});
