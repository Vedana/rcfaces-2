/*
 * $Id: f_box.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * Box class.
 *
 * @class public f_box extends f_component, fa_asyncRender, fa_subMenu, fa_overStyleClass
 */
 
var __statics = {
	/**
	 * @field private static final String
	 */
	_BODY_MENU_ID: "#body"
};

var __members = {
	/**
	 * @field private String
	 */
	_defaultMenuId: undefined,
	
/*
 * 	f_box: function() {
 *     this.f_super(arguments);
 *  },
 * 
	f_finalize: function() {
		// this._defaultMenuId=undefined; // String
		
		this.f_super(arguments);
	},
	*/
	f_update: function() {
		this.f_super(arguments);
		
		var menu=this.f_getSubMenuById(f_box._BODY_MENU_ID);
		if (menu) {
			this.f_insertEventListenerFirst(f_event.MOUSEDOWN, this._performMenuMouseDown);
		}
	},
	/**
	 * Specify the default menu identifier.
	 *
	 * @method public
	 * @param String menuId Identifier of the menu.
	 * @return void
	 */
	f_setDefaultMenuId: function(menuId) {
		this._defaultMenuId=menuId;
	},
	
	/**
	 * Returns the current menu identifier.
	 *
	 * @method public
	 * @return String Identifier of the menu.
	 */
	f_getDefaultMenuId: function() {
		var d=this._defaultMenuId;
		
		return (d)?d:null;
	},
	/**
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_performMenuMouseDown: function(event) {		
		var jsEvent=event.f_getJsEvent();
		
		var sub=f_core.IsPopupButton(jsEvent);
		if (!sub) {
			return true;
		}
		
		var menuId=this.f_getDefaultMenuId();
		if (!menuId) {
			menuId=f_box._BODY_MENU_ID;
		}
		
		var menu=this.f_getSubMenuById(menuId);
		if (menu) {
			menu.f_open(jsEvent);
				
			return event.f_preventDefault();
		}
		
		return true;
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	fa_componentCaptureMenuEvent: function() {
		return null;
	},
	/**
	 * @method private
	 * @return void
	 */
	f_updateStyleClass: function() {
		var over=this.f_isMouseOver();
	
		var suffix=null;
		if (over) {
			suffix="_over";
		}
	
		var className=this.f_computeStyleClass(suffix);
		
		if (over) {
			var overStyleClass=this.f_getOverStyleClass();
			if (overStyleClass) {
				className+=" "+overStyleClass;
			}
		}
				
		if (this.className!=className) {
			this.className=className;
		}
	}
};
 
new f_class("f_box", {
	extend: f_component,
	aspects: [ fa_asyncRender, fa_subMenu, fa_overStyleClass ],
	statics: __statics,
	members: __members
});

