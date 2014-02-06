/*
 * $Id: f_helpButton.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/** 
 * f_helpButton class
 *
 * @class f_helpButton extends f_imageButton
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __statics = {
	/**
	 * @field private static final String
	 */
	_IMAGE: "/helpButton/helpButton.gif",

	/**
	 * @field private static final String
	 */
	_HOVER: "/helpButton/helpButtonHover.gif"
};

var __members = {
	f_helpButton: function() {
		this.f_super(arguments);

		var image=this._image;
		if (image && !image.src) {
			var i = f_env.Get("HELPBUTTON_IMAGE_URL");
			if (!i) {
				i=f_env.GetStyleSheetBase()+ f_helpButton._IMAGE;
			}
			f_imageRepository.PrepareImage(i);
			image.src=i;
			this.f_setImageURL(i);
			
			var h = f_env.Get("HELPBUTTON_HOVER_URL");
			if (!h) {
				h=f_env.GetStyleSheetBase()+ f_helpButton._HOVER;
			}
			f_imageRepository.PrepareImage(h);
			this.f_setHoverImageURL(h);
		}
		
		this.f_addEventListener(f_event.SELECTION, function() {
			f_help._Open(this);
			return false;		
		});
	}
};

new f_class("f_helpButton", {
	extend: f_imageButton,
	statics: __statics,
	members: __members
} );
