/*
 * $Id: f_viewDialog.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p><strong>f_viewDialog</strong> represents popup modal view.
 *
 * @class public f_viewDialog extends f_dialog, fa_immediate
 * @author Fred Lefevere-Laoide (latest mdialogodification by $Author: jbmeslin $)
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		width: 500,
		height: 400,
		priority: 0,
		styleClass: "f_viewDialog",
		backgroundMode: f_shell.GREYED_BACKGROUND_MODE
	}
	
};

var __members = {

	/**
	 * @field private String
	 */
	_viewURL: undefined,
	
	/**
	 * @field private Map
	 */
	_parameters: undefined,
	
	/**
	 * @field private HTMLIFrameElement
	 */
	_iframe: undefined,

	/**
	 * <p>Construct a new <code>f_viewDialog</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 */
	f_viewDialog: function(style) {
		this.f_super(arguments, style | f_shell.PRIMARY_MODAL_STYLE);
		
		if (this.nodeType==f_core.ELEMENT_NODE) {
			
			this._parameters=f_core.ParseDataAttribute(this,f_core._VNS+":parameter");
			
			var viewURL=f_core.GetAttributeNS(this, "viewURL", "about:blank");
			this.f_setViewURL(viewURL);

			if (f_core.GetBooleanAttributeNS(this, "visible", true)) {
				this.f_open();
			}
		}		
	},

	/*
	 * <p>Destruct a new <code>f_messageDialog</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {
		// this._viewURL=undefined // string
		var iframe=this._iframe;
		if (iframe) {	
			this._iframe=undefined; // HtmlIFrame
			
			this.f_finalizeIframe(iframe);
		}
		this._parameters=undefined;
		this._loadFrame=undefined;
		this.f_super(arguments);		
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_getDefaultFeatures: function() {
		return f_viewDialog._DEFAULT_FEATURES;
	},
	
	/**
	 *  <p>Returns value associated to the parameter's key.</p>
	 *
	 * @method public 
	 * @param String key
	 * @return String value
	 */
	f_getParameter: function(key) {
		f_core.Assert(typeof(key)=="string", "f_viewDialog.f_getParameter: Invalid key parameter ("+key+")");

		if(this._parameters) {
			return this._parameters[key];
		}
		return null;
	},
	
	/**
	 *  <p>add or replace a parameter.</p>
	 *
	 * @method public 
	 * @param String key
	 * @param String value
	 * @return void
	 */
	f_setParameter: function(key,value) {
		f_core.Assert(typeof(key)=="string", "f_viewDialog.f_setParameter: Invalid key parameter ("+key+")");
		f_core.Assert(typeof(value)=="string", "f_viewDialog.f_setParameter: Invalid value parameter ("+value+")");
		if(!this._parameters) {
			this._parameters = new Object;
		}
		this._parameters[key] = value;
	},
	
	/**
	 *  <p>Delete a parameter.</p>
	 *
	 * @method public 
	 * @param String key
	 * @return void
	 */
	f_removeParameter: function(key) {
		f_core.Assert(typeof(key)=="string", "f_viewDialog.f_removeParameter: Invalid key parameter ("+key+")");
		if(this._parameters) {
			delete this._parameters[key];
		}
	},
	
	

	/**
	 *  <p>Return the viewURL URL.</p>
	 *
	 * @method public 
	 * @return String viewURL
	 */
	f_getViewURL: function() {
		return this._viewURL;
	},
	
	/**
	 *  <p>Sets the viewURL URL.</p>
	 *
	 * @method public 
	 * @param String viewURL
	 * @return void
	 */
	f_setViewURL: function(viewURL) {
    	f_core.Assert((typeof(viewURL)=="string"), "f_shell.f_setViewURL: Invalid parameter '"+viewURL+"'.");
		this._viewURL = f_env.ResolveContentUrl(viewURL);
		
		if (this._iframe && this.f_getStatus()==f_shell.OPENED_STATUS) { 
			this._iframe.src=this.f_getIFrameUrl();
		}
	},
	
	/**
	 *  <p>returns the url to show in the iFrame 
	 *  </p>
	 *
	 * @method protected
	 * @return String 
	 */
	f_getIFrameUrl: function() {
		var url=this.f_getViewURL();
		if (!url) {
			url="about:blank";
		}
		
		var param = this._parameters;
		if (param){
			var ds="";
			var first=true;
			
			var system=f_env.GetSystemParameterNames();
			
			for(var key in param) {
				
				if (system[key]) {
					url=f_core.AddParameter(url, key, param[key]);
					continue;
				}
				
				if (first) {
					first=false;
				} else {
					ds+=",";
				}
				
				ds+=encodeURIComponent(key)+"="+encodeURIComponent(param[key]);
			}
			
			if (ds) {				
				url=f_core.AddParameter(url, f_core.REQUEST_PARAMETERS_UTF8, ds);
			}
		}

		url=f_core.AddParameter(url, f_core.REQUEST_PARAMETERS_KEY, f_core.AllocateRequestKey());
		
		return url;
	},
	
	f_fillBody: function(base) {
		var iframe=f_core.CreateElement(base, "iframe", {
			frameBorder: 0, //ajouter le frameBorder avant l'ajout au DOM
			className: "f_viewDialog_frame",
			title: ""
		});
		this._iframe=iframe;
		
		iframe.style.width=this.f_getWidth();
		iframe.style.height=this.f_getHeight();
		
		var self=this;
		var version = f_core.GetBrowserVersion();
		if (f_core.IsInternetExplorer() && version < 9 ) { //ie 9
			f_core.Debug(f_viewDialog, "f_fillBody: IE use onreadystatechange ");
			
			this._loadFrame = function() {
				if (window._rcfacesExiting) {
					return false;
				}

				var doc=f_core.GetFrameDocument(iframe);
				try {
					self.f_performFrameReady(iframe, doc);

				} catch (x) {					
					f_core.Error(f_viewDialog, "f_fillBody: f_performFrameReady throws exception.", x);
				}
			};
			
			f_core.AddEventListener(iframe, "load", this._loadFrame);
			
		} else {
			f_core.Debug(f_viewDialog, "f_fillBody: Firefox use onload ");
			iframe.onload=function() {
				if (window._rcfacesExiting) {
					return false;
				}

				f_core.Debug(f_viewDialog, "f_fillBody: on ready state change: "+this+" state="+this.readyState);
	
				this.onload=null;
				
				var doc=f_core.GetFrameDocument(this);
				try {
					self.f_performFrameReady(this, doc);

				} catch (x) {					
					f_core.Error(f_viewDialog, "f_fillBody: f_performFrameReady throws exception.", x);
				}
			};
		}
		
		iframe.src=this.f_getIFrameUrl();
	},
	
	/**
	 * @method protected
	 * @param HtmlIFrameElement iframe
	 * @return void
	 */
	f_finalizeIframe: function(iframe) {
		iframe.onreadystatechange=null;
		iframe.onload=null;
		
		f_core.VerifyProperties(iframe);		
	},
	/**
	 * @method protected
	 * @param HtmlIFrameElement iframe
	 * @param Document doc
	 * @return void
	 */
	f_performFrameReady: function(iframe, doc) {
		f_core.Debug(f_frameShellDecorator, "f_performFrameReady: frame '"+iframe+"' is ready ! (doc="+doc+")");
		
		this.f_setBody(doc.body);
		
		var component=f_focusManager.Get().f_getFocusComponent();
		if (!component) {
			return;
		}
		
		var shellDecorator=this.f_getShellDecorator();
		if (!shellDecorator) {
			return;
		}

		var title=shellDecorator.f_getTitle();
		if (title) {
			doc.title=title;
			iframe.title=title;
		}
		
		try {
			if (shellDecorator.f_isIntoShell(component)) {
				return false;
			}
			
			var comp=f_core.GetNextFocusableComponent(doc.body);
			if (comp) {
				f_core.SetFocus(comp, true);
			}
		
		} catch (ex) {
			f_core.Error(f_viewDialog, "f_performFrameReady: Try to identify a focus component", ex);
		}
	},

	f_preDestruction: function() {
		if (f_env.GetPerformanceTimingFeatures()) {
			var iframe=this._iframe;
			if (iframe) {
				var localWin=iframe.contentWindow;
				if (localWin.f_core) {			
					f_core.FramePerformanceTimingLog(localWin);
				}
			}
		}
		
		this.f_super(arguments);
	},
	
	/* Plus nécessaire par la redéfinition de  _OnExit 
	f_preDestruction: function() {
		if (window._RCFACES_LEVEL3) {
			var iframe=this._iframe;
			if (f_core.IsInternetExplorer() && iframe) {
				
				// Pour IE, probleme de synchronisme de fermeture !
				
				// Pas de super, car on passe en asynchrone !
				iframe.parentNode.removeChild(iframe);
		
				var self=this;
				var callee=arguments.callee;
				window.setTimeout(function() {
					self.f_super({callee: callee, length: 0 });
					
					self=null;
					callee=null;
				}, 10);
				
				return;
			}
		}
		this.f_super(arguments);
	},
	*/
	f_postDestruction: function() {
		this._iframe=undefined; // HtmlIFrame
		
		this.f_super(arguments);
	},

	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_viewDialog viewURL='"+this._viewURL+"']";
	}
};

new f_class("f_viewDialog", {
	extend: f_dialog,
	aspects: [ fa_immediate, fa_clientData ],
	members: __members,
	statics: __statics
});
