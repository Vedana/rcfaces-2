/*
 * $Id: f_waitingShell.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p><strong>f_waitingShell</strong> represents popup modal window.
 *
 * @class public final f_waitingShell extends f_dialog
 * @author Fred Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */
var __statics = {
 	
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		priority: 0,
		width: 150,
		height: 150,
		styleClass: "f_waitingShell",
		backgroundMode: f_shell.GREYED_BACKGROUND_MODE,
		closable: false
	},
 
    /**
     * @method public static
     * @return void
     */
    WaitForSubmit: function() {
 		var waitingShell=f_waitingShell.f_newInstance();
 		
 		waitingShell.f_open();
	}
};

var __members = {

	/**
	 * <p>Construct a new <code>f_waitingShell</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param optional String imageURL
	 * @param optional String text
	 * @param optional Number width
	 * @param optional Number height
	 * @param optional Boolean showOnSubmit
	 * @param optional String backgroundMode
	 */
	f_waitingShell: function(imageURL, text, width, height, showOnSubmit, backgroundMode) {
		this.f_super(arguments, f_shell.TRANSPARENT | f_shell.LIGHT_CONTAINER_STYLE);

		if (this.nodeType==f_core.ELEMENT_NODE) {
			if (!imageURL) {
				imageURL=f_core.GetAttributeNS(this,"imageURL");
			}

			if (!text) { 
				text=f_core.GetAttributeNS(this,"text");
			}

			if (showOnSubmit===undefined) {
				showOnSubmit=f_core.GetBooleanAttributeNS(this,"visible", true);
			}
		}
		if (imageURL) {
			this.f_setImageURL(imageURL);
		}
		if (text) {
			this.f_setText(text);
		}
		if (width>0) {
			this.f_setWidth(width);
		}
		if (height>0) {
			this.f_setHeight(height);
		}
		if (showOnSubmit) {
			this.f_installShowOnSubmit();
		}
		if (backgroundMode) {
			this.f_setBackgroundMode(backgroundMode);			
		}
	},
	/*
	f_finalize: function() {
		this.f_super(arguments);
		//this._imageURL=undefined; // String
		//this._text=undefined; // String
	},
	*/
	/**
	 * @method protected
	 * @return Object
	 */
	f_getDefaultFeatures: function() {
		return f_waitingShell._DEFAULT_FEATURES;
	},
	/**
	 * @method public
	 * @return void
	 */
	f_installShowOnSubmit: function() {
		var self=this;
		var submitCb=function() {
			if (self) {
				self.f_open();
			}
			self=null;
		};
		f_core.AddPostSubmitListener(submitCb);
	},
	/**
	 *  <p>Fill a modal iFrame. 
	 *  </p>
	 *
	 * @method protected
	 * @param HTMLElement base
	 * @return void
	 */
	f_fillBody: function(base) {
		this.f_super(arguments, base);
		
		this.f_constructWaitingComponents(base);
	},
	
	/**
	 *  Construct waiting components
	 *
	 * @method protected
	 * @param HTMLElement base
	 * @return void
	 */
	f_constructWaitingComponents: function(base) {
		var tr=f_core.CreateElement(base, "table", {
			cellPadding: 0,
			cellSpacing: 0
		}, "tbody", null, "tr", null);

		var url=this.f_getImageResolvedURL();
		if (url) {
			f_core.CreateElement(tr, "td", {
				align: "center",
				valign: "middle"
			}, "img", {
				className: "f_waitingShell_image",
				src: url
			});
		}
		
		var text=this._text;
		if (text) {
			f_core.CreateElement(tr, "td", {
				align: "center",
				valign: "middle"
			}, "span", {
				className: "f_waitingShell_text",
				textNode: text
			});
		}
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
	 * @param String text
	 * @return void
	 */
	f_setText: function(text) {
		this._text=text;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return this._text;
	},

	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_waitingShell]";
	}
};

new f_class("f_waitingShell", {
	extend: f_dialog,
	statics: __statics,
	members: __members
});
