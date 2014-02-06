/*
 * $Id: f_env.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * 
 *
 * @class public final f_env extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */

var __statics = {
	/**
	 * @field public static final String 
	 */
	BLANK_IMAGE_URL: "/blank.gif",

	/**
	 * @field private static final String 
	 */
	_STYLESHEET_BASE: "styleSheet.base",
	
	/**
	 * @field private static final String 
	 */
	_CANCEL_EXTERNAL_SUBMIT: "cancel.external.submit",

	/**
	 * @field private static final String 
	 */
	_DOMAIN_NAME: "camelia.domain",
	
	/**
	 * @field private static String
	 */
	 _StyleSheetBase: undefined,
	
	/**
	 * @field private static String
	 */
	_CONTEXT_KEYWORD: "$context",
	/**
	 * @field private static String
	 */
	_BaseURI: undefined,
	
	/**
	 * @field private static String
	 */
	_JsBaseURI: undefined,
	
	/**
	 * @field private static String
	 */
	_ViewURI: undefined,

	/**
	 * @field private static String
	 */
	_LocaleName: undefined,

	/**
	 * @field private static Boolean
	 */
	_LockSubmitUntilPageComplete: undefined,
		
	/**
	 * @field hidden static Boolean
	 */
	_SensitiveCaseTagName: undefined,
		
	/**
	 * @field private static Boolean
	 */
	_ClientValidationDisabled: undefined,
	
	/**
	 * @field private static Number
	 */
	_EnablePerformanceTiming: undefined,

	/**
	 * @field private static String
	 */
	_BlankImageURL: undefined,

	/*
	Initializer: function() {
		// Defaults are in f_helpButton, change these for customization
		// HELPBUTTON_IMAGE_URL = "stylesheets/help/helpButton.gif";
		// HELPBUTTON_HOVER_URL = "stylesheets/help/helpButtonHover.gif";

		// Defaults are in F_HELP, change these for customization
		// WINHELP_ID = "VFCHelpWindow
		// WINHELP_X = 100;
		// WINHELP_Y = 100;
		// WINHELP_W = 600;
		// WINHELP_H = 450;
		// WINHELP_FEATURES = "scrollbars,resizable,status=no";

		// CORE_LOCK_MESSAGE = "Window has been locked...";
	},
	*/
	
	/**
	 * @method public static  
	 * @param String name Name of the key.
	 * @param any val Value to associate to the specified key
	 * @return any Previous value.
	 */
	Set: function(name, val) {
		f_core.Assert(typeof(name)=="string", "f_env.Set: Invalid name '"+name+"'.");

		var ret = f_env[name];
		f_env[name] = val;
		return ret;
	},
	
	/**
	 * @method public static  
	 * @param String name
	 * @param optional any defaultValue
	 * @return any Value associated to name, or specified default value.
	 */
	Get: function(name, defaultValue) {
		f_core.Assert(typeof(name)=="string", "f_env.Get: Invalid name '"+name+"'.");
		
		var v=f_env[name];
		if (v===undefined) {
			return defaultValue;
		}
		
		return v;
	},
	
	/**
	 * @method public static  
	 * @param String name
	 * @return any Previous value associated to specified name.
	 */
	Delete: function(name) {
		f_core.Assert(typeof(name)=="string", "f_env.Delete: Invalid name '"+name+"'.");

		var old=f_env[name];
		if (old !== undefined) {
			delete f_env[name];
		}
		
		return old;
	},

	/**
	 * @method hidden static  
	 * @return String
	 */
	GetBaseURI: function() {
		return f_env._BaseURI;
	},
	
	/**
	 * @method hidden static  
	 * @param String uri
	 * @return String
	 */
	ComputeJavaScriptURI: function(uri) {
		f_core.Assert(typeof(uri)=="string", "f_env.ComputeJavaScriptURI: invalid uri parameter '"+uri+"'.");

		var baseURI=f_env._JsBaseURI;
		if (!baseURI) {
			return uri;
		}
		return baseURI+"/"+uri;
	},
	/**
	 * @method hidden static  
	 * @return String The url of the rcfaces resources, or null if it is not defined !
	 */
	GetStyleSheetBase: function() {
		if (f_env._StyleSheetBase) {
			return f_env._StyleSheetBase;
		}
		
		var sb=f_env.Get(f_env._STYLESHEET_BASE);
		if (sb) {
			f_env._StyleSheetBase=sb;
			return sb;
		}
		
		f_core.Debug(f_env, "GetStyleSheetBase: not yet initialized !");
		return null;
	},
	/**
	 * @method hidden static  
	 * @return String
	 */
	GetLocaleName: function() {
		return f_env._LocaleName;
	},
	
	/**
	 * @method hidden static
	 * @param String baseURI
	 * @param String viewURI
	 * @param String localeName
	 * @param String jsBaseURI
	 * @param optional String styleSheetBaseURI 
	 * @return void
	 */
	Initialize: function(baseURI, viewURI, localeName, jsBaseURI, styleSheetBaseURI) {
		f_env._BaseURI=baseURI;		
		f_env._JsBaseURI=jsBaseURI;
		f_env._LocaleName=localeName;
		if (!styleSheetBaseURI) {
			styleSheetBaseURI=jsBaseURI;
		}
		f_env._StyleSheetBase=styleSheetBaseURI;
		
		if (viewURI.charAt(0)=="/") {
			// On ajoute le protocole, le host et le port
			
			var url=window.location.toString();
			var idx=url.indexOf("//");
			if (idx>0) {
				idx=url.indexOf("/", idx+2);
				if (idx>0) {
					viewURI=url.substring(0, idx)+viewURI;
				}
			}
		}
		
		f_env._ViewURI=viewURI;
		
		f_core.Debug(f_env, "Initialize: Set Page base URI to '"+baseURI+"'.");
		f_core.Debug(f_env, "Initialize: Set View base URI to '"+viewURI+"'.");
		f_core.Debug(f_env, "Initialize: Set Javascript base URI to '"+jsBaseURI+"'.");
		f_core.Debug(f_env, "Initialize: Set Css base URI to '"+styleSheetBaseURI+"'.");
	},
	
	/**
	 * @method hidden static 
	 * @return Boolean
	 */
	GetCancelExternalSubmit: function() {
		return f_env.Get(f_env._CANCEL_EXTERNAL_SUBMIT, false);
	},
	
	/**
	 * @method hidden static 
	 * @return void
	 */
	EnableSubmitLockUntilPageComplete: function() {
		f_env._LockSubmitUntilPageComplete=true;
	},
	
	/**
	 * @method hidden static 
	 * @return Boolean
	 */
	IsSubmitUntilPageCompleteLocked: function() {
		return f_env._LockSubmitUntilPageComplete;
	},
		
	/**
	 * @method hidden static 
	 * @return String
	 */
	GetViewURI: function() {
		var uri=f_env._ViewURI;
		if (uri) {
			return uri;
		}
		
		return window.location.toString();
	},
	
	/**
	 * @method hidden static 
	 * @return Boolean
	 */
	GetCheckValidation: function() {
		return (f_env._ClientValidationDisabled!==true);
	},
	
	/**
	 * @method public static 
	 * @return void
	 */
	EnableSensitiveCaseTagName: function() {
		if (f_core.IsGecko()) { // Gecko n'est pas sensitive case !
			return;
		}
		
		f_core.Debug(f_env, "Enable sensitiveCaseTagName");
		f_env._SensitiveCaseTagName=true;
	},
	
	/**
	 * @method public static 
	 * @param Number features
	 * @return void
	 */
	EnablePerformanceTiming: function(features) {
		f_env._EnablePerformanceTiming=features;
	},
	
	/**
	 * @method hidden static 
	 * @return Number
	 */
	GetPerformanceTimingFeatures: function() {
		return f_env._EnablePerformanceTiming;
	},
	
	/**
	 * @method hidden static 
	 * @return String
	 */
	GetOpenWindowErrorMessage: function() {
		var bundle=f_resourceBundle.Get(f_env);
		if (!bundle) {
			return null;
		}
			
		return bundle.f_get("OPEN_WINDOW_ERROR_MESSAGE");
	},
	/**
	 * @method hidden static 
	 * @return String the domain
	 */
	GetDomain: function() {
		var domain=f_env.Get(f_env._DOMAIN_NAME);
		if (domain) {
			return domain;
		}
			
		var url=window.location.toString();
		var idx=url.indexOf("//");
		if (idx<0) {
			f_env.Set(f_env._DOMAIN_NAME, "");

			f_core.Debug(f_env, "Invalid domain from url '"+url+"'.");
			return "";
		}
		
		var domain;
		
		var idx2=url.indexOf("/", idx+2);
		if (idx2>idx) {
			domain=url.substring(idx+2, idx2);
		} else {
			domain=url.substring(idx+2);
		}
		
		var idx2=domain.indexOf(":");
		if (idx2>0) {
			domain=domain.substring(0, idx2);
		}
		
		var ds=domain.split(".");
		var dsl=ds.length;
		if (dsl>1) {
			domain=ds[dsl-2]+"."+ds[dsl-1];
		}
		
		f_env.Set(f_env._DOMAIN_NAME, domain);
		
		f_core.Debug(f_env, "Set domain to '"+domain+"' (url='"+url+"')");
		return domain;
	},
     /**
     * @method public static
     * @param String url for the content
     * @param optional Window myWindow a window with the right location
     * @return String fully formed url
     */
    ResolveContentUrl: function(url, myWindow) {
    	if (!myWindow) {
    		myWindow=window;
    	}
    	f_core.Assert(typeof(myWindow)=="object", "f_env.ResolveContentUrl: Invalid parameter window '"+myWindow+"'."+typeof(myWindow));
    	f_core.Assert(myWindow.location, "f_env.ResolveContentUrl: Invalid parameter window has no location '"+myWindow+"'.");
    	f_core.Assert((typeof(url)=="string"), "f_env.ResolveContentUrl: Invalid parameter url '"+url+"'.");
	    
	    f_core.Debug(f_env, "ResolveContentUrl : entering with (myWindow='"+myWindow+"' url='"+url+"')");
	    
    	// Check for protocol in url
    	var pos=url.indexOf(":");
    	if (pos>0 && pos<url.indexOf("/")) {
		    f_core.Debug(f_env, "ResolveContentUrl : url already formed");
    		return url;
    	}
    	
    	// check for window protocol
    	var windowUrl=myWindow.location.toString();
    	pos=windowUrl.indexOf("//");
    	if (pos<0) {
		    f_core.Debug(f_env, "ResolveContentUrl: window.location does not have a protocol ...");
    		return url;
    	}
    	
    	// extract protocol://domain:port from windowUrl into base and rest into remain
    	pos=windowUrl.indexOf("/",pos+2);
    	var base=windowUrl;
    	var remain="";
    	if (pos>=0) {
	    	base=windowUrl.substring(0,pos);
	    	remain=windowUrl.substring(pos+1);
    	}
    	// If url absolute
    	if (!url.indexOf("/")) {
    		return base+url;

    	} else if (!url.indexOf(f_env._CONTEXT_KEYWORD)) {
    		// if $context : return 
    		return base+f_env._BaseURI+url.substring(f_env._CONTEXT_KEYWORD.length);
    	}
    	
    	//if no remain
    	if (!remain.length) {
    		return base+"/"+url;
    	}
    	
    	// extract the last par of remain (page)
    	var remains=remain.split("/");
    	if (remain.charAt(remain.length-1) != "/") {
    		remains.pop();
    	}
    	if (remains.length && "/"+remains[0] == f_env._BaseURI) {
			remains.shift();
		}
		
    	// del .. from url
    	var adds=url.split("/");
    	while (adds.length && remains.length && adds[0]=="..") {
    		adds.shift();
    		remains.pop();
    	}
    	//reconstruct complete url
    	return base+"/"+remains.concat(adds).join("/");
	},
	/** 
	 * @method hidden static
	 * @return void
	 */	
	DisableClientValidation: function() {
		f_env._ClientValidationDisabled=true;
	},
	/**
	 * @method public static
	 * @return String
	 */
	GetBlankImageURL: function() {
		var url=f_env._BlankImageURL;
		if (url) {
			return url;	
		}
		
		url=f_env.ResolveContentUrl(f_env.GetStyleSheetBase()+f_env.BLANK_IMAGE_URL);
		
		f_env._BlankImageURL=url;
		
		f_imageRepository.PrepareImage(url);
		
		return url;
	},
	/**
	 * @method public static 
	 * @param String... systemParameterNames
	 * @return void
	 */
	SetSystemParameterNames: function(systemParameterNames) {
		var names=f_env._SystemParameterNames;
		if (!names) {
			names=new Object();
			f_env._SystemParameterNames=names;
		}
		
		for(var i=0;i<arguments.length;i++) {
			names[arguments[i]]=true;
		}
	},
	/**
	 * @method public static 
	 * @return Object
	 */
	GetSystemParameterNames: function() {
		var names=f_env._SystemParameterNames;
		if (!names) {
			names=new Object();
			f_env._SystemParameterNames=names;
		}

		return names;
	}
};

new f_class("f_env", {
	statics: __statics,
	_systemClass: true
});
