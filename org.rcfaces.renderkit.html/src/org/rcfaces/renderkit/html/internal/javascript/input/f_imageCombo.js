/*
 * $Id: f_imageCombo.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * class f_imageCombo
 *
 * @class f_imageCombo extends f_imageButton, fa_subMenu, fa_itemsWrapper
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	/**
	 * @field private static final String
	 */
	_MENU_ID: "#popup"
};

var __members = {

	f_imageCombo: function() {
		this.f_super(arguments);
		
		this.f_insertEventListenerFirst(f_event.KEYDOWN, this._onKeyDown);		
	},
	/** 
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_onKeyDown: function(event) {
		var code=event.f_getJsEvent().keyCode;

		if (code!=f_key.VK_DOWN && code!=f_key.VK_UP) {
			return true;
		}

		event.f_preventDefault();

		var menu=this.f_getSubMenuById(f_imageCombo._MENU_ID);
		if (menu) {
			menu.f_open(event.f_getJsEvent(), {
				component: this,
				position: f_popup.BOTTOM_COMPONENT
			}, (code==f_key.VK_DOWN)?1:-1);
		}
		
		return false;
	},
	/** 
	 * @method protected
	 * @param f_event evt
	 * @return Boolean
	 */
	f_imageButtonSelect: function(evt) {
		f_core.Debug(f_imageCombo, "f_imageButtonSelect: evt="+evt);
		if (!this._focus)  {
			this.f_setFocus();
			
		}

		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}
		
		if (f_popup.VerifyMouseDown(this, evt.f_getJsEvent())) {
			f_core.Debug(f_imageCombo, "f_imageButtonSelect: mouse down outside !");
			return false;
		}
		
		if (evt.f_getItem()) {
			// Selection d'un item !
			f_core.Debug(f_imageCombo, "f_imageButtonSelect: item already selected");
			return true;
		}
	
		var menu=this.f_getSubMenuById(f_imageCombo._MENU_ID);
		if (!menu) {
			f_core.Debug(f_imageCombo, "f_imageButtonSelect: no menu");
			return true;
		}
		
		menu.f_open(evt, {
			component: this,
			position: f_popup.BOTTOM_COMPONENT,
			ariaOwns: this
		});
		
		f_core.Debug(f_imageCombo, "f_imageButtonSelect: Menu open (menu='"+menu+"')");
		return false;
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	fa_componentCaptureMenuEvent: function() {
		return this;
	},	
	fa_getItemsWrapper: function() {
		return this.f_getSubMenuById(f_imageCombo._MENU_ID);
	},
	f_isPopupLock: function(popupDocument, event) {
		return false;
	}
};

new f_class("f_imageCombo", {
	extend: f_imageButton, 
	aspects: [ fa_subMenu, fa_itemsWrapper ],
	statics: __statics,
	members: __members
});