/**
 * Class f_externalBox.
 * 
 * @class f_externalBox extends f_component
 * @author jbmeslin@vedana.com
 * @version $Revision: 1.0
 */

var __statics = {
		
	/**
	 * @field public static final Number
	 */
	FIRST_LOAD: 1,
	
	/**
	 * @field public static final Number
	 */
	NEXT_LOAD: 0,	
		
	/**
	 * @method private static
	 * @context object:externalBox
	 */
	_OnLoad : function(evt) {

		var externalBox = this;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		var event;
		if (externalBox._firstLoad) {
			externalBox._firstLoad = false;
			event = new f_event(externalBox, f_event.LOAD, evt, null, null,
					null, f_externalBox.FIRST_LOAD);
		} else {
			event = new f_event(externalBox, f_event.LOAD, evt, null, null,
					null, f_externalBox.NEXT_LOAD);
		}
		externalBox.f_fireEvent(event);
	}

};

var __members = {

	f_externalBox : function() {
		this.f_super(arguments);

		this._contentURL = f_core.GetAttributeNS(this,"contentURL");
		this.onload = f_externalBox._OnLoad;
	},

	f_finalize : function() {
		this.onload = null;
		// this._contentURL=undefined; // String
		// this._firstLoad = undefined; // Boolean
		this.f_super(arguments);
	},

	/**
	 * Marks the receiver as visible if the argument is true, and marks it invisible
	 * otherwise. <br>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, marking it visible may not actually cause it
	 * to be displayed.
	 * 
	 * @method public
	 * @param Boolean
	 *            visible the new visibility state
	 * @return void
	 */
	f_setVisible : function(visible) {
		this.f_super(arguments, visible);
	
		if (visible && !this.src) {
			this.src = this.f_getContentURL();
		}
	},

	/**
	 * Returns the content URL.
	 * <p>
	 * The src attribut of IFRAM HTML element
	 * </p>
	 * 
	 * @method public
	 * @return String url
	 */
	f_getContentURL : function() {
		return this._contentURL;
	},

	/**
	 * Sets the content URL.
	 * <p>
	 * The src attribut of IFRAM HTML element
	 * </p>
	 * 
	 * @method public
	 * @param String
	 *            url
	 * @return void
	 */
	f_setContentURL : function(contentURL) {
		if (contentURL != this._contentURL) {
			this._contentURL = contentURL;
			this.f_setProperty(f_prop.CONTENT_URL,contentURL);
			this._firstLoad = true;
			this.src = contentURL;
		}
	},

	/**
	 * Refresh the src URL
	 * 
	 * @method public 
	 * @return void
	 */
	f_refresh : function() {
		if (this._contentURL) {
			this.src = this._contentURL;
		}
	}
};

new f_class("f_externalBox", {
	extend : f_component,
	statics : __statics,
	members : __members
});
