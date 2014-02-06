/*
 * $Id: f_help.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_help package
 *
 * @class hidden f_help extends f_object
 * @author Joel Merlin
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
var __statics = {

	/**
	 * @field private static final String
	 */
	_ID: "VFCHelpWindow",
	
	// Default is 800x600 resolution, 4/3 screen, centered
	/**
	 * @field private static final Number
	 */
	_X: 100,

	/**
	 * @field private static final Number
	 */
	_Y: 100,

	/**
	 * @field private static final Number
	 */
	_W: 600,

	/**
	 * @field private static final Number
	 */
	_H: 450,

	/**
	 * @field private static final String
	 */
	_F: "scrollbars,resizable,status=no",

	/**
	 * @field private static boolean
	 */
	_Installed: undefined,

	/**
	 * @field private static f_component
	 */
	_FocusElement: undefined,

	/**
	 * @field private static f_component
	 */
	_HelpZone: undefined,
	
	/**
	 * @field private static Window
	 */
	_HelpWindow: undefined,

	/**
	 * @method hidden static
	 */
	SetHelpMessageZone: function(elt) {
		f_help._HelpZone = elt;
	},
	/**
	 * @method hidden static
	 */
	GetHelpMessageZone: function() {
		return f_help._HelpZone;
	},
	/**
	 * @method hidden static
	 */
	Install: function() {
		if (f_help._Installed) {
			return;
		}	
		f_help._Installed = true;
		
		if (f_core.IsInternetExplorer()) {
			window.onhelp = f_help._IE_open;
			return;	
		}

		document.onkeydown = f_help._NS_open;
	},
	/**
	 * @method private static
	 */
	_IE_open: function() {
		if (f_help._FocusElement) {
			return f_help._Open(f_help._FocusElement);
		}

		return false;
	},
	/**
	 * @method private static
	 * @context event:jsEvent
	 */
	_NS_open: function(jsEvent) {
		if (f_help._FocusElement && jsEvent.keyCode==f_key.VK_F1) {
			return f_help._Open(f_help._FocusElement);
		}

		return true;
	},
	/*
	 * @method private static
	 * @context event:jsEvent
	 *
	_OnFocus: function(jsEvent) {
		f_help._FocusElement = jsEvent.f_getComponent();
	},
	*/
	/*
	 * @method private static
	 *
	_OnBlur: function(evt) {
		if (f_help._FocusElement == evt.f_getComponent()) {
			f_help._FocusElement = undefined;
		}
	},
	*/
	/**
	 * @method hidden static
	 * @context event:evt
	 */
	OnShowHelpMessage: function(evt) {
		var component=evt.f_getComponent();
		
		var zone = f_help._HelpZone;
		if (!zone) {
			window.status = component.f_getHelpMessage();
			
		} else {
			zone.f_showMessage(component);
		}
		return true;
	},
	/**
	 * @method hidden static
	 * @context event:evt
	 */
	OnHideHelpMessage: function(evt) {
		var component=evt.f_getComponent();
		var zone = f_help._HelpZone;
		if (!zone) {
			window.status = "";

		} else {
			zone.f_hideMessage(component);
		}
		
		return true;
	},
	/**
	 * @method hidden static
	 */
	_Open: function(elt) {
		var url=null;
		if (elt && typeof(elt.f_getHelpURL)=="function") {
			url = elt.f_getHelpURL();
		}
		
		if (!url) {
			return false;
		}
		
		var win=f_help._HelpWindow;
		if (!win || win.closed) {
			var id = f_env.Get("WINHELP_ID", f_help._ID);
			var w = f_env.Get("WINHELP_W", f_help._W);
			var h = f_env.Get("WINHELP_H", f_help._H);
			var x = f_env.Get("WINHELP_X", f_help._X);
			var y = f_env.Get("WINHELP_Y", f_help._Y);
			// var f = f_env.Get("WINHELP_FEATURES", f_help._F);
			win = f_core.OpenWindow(window, { 
				url: url,
				target: id,
				x: x,
				y: y,
				width: w,
				height: h });
			
			f_help._HelpWindow = win;
		}
			
		win.location.href = url;
		win.focus();
		
		return false;
	},
	/**
	 * @method protected static
	 */
	DocumentComplete: function() {
		if (f_help._HelpZone!==false) {
			return;
		}
		
		f_help._HelpZone = f_core.GetChildByClass(document, f_helpMessageZone.f_getName());
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_help._FocusElement=undefined;
		f_help._HelpZone=undefined;
		f_help._HelpWindow=undefined;
	}
}

new f_class("f_help", {
	statics: __statics
});
