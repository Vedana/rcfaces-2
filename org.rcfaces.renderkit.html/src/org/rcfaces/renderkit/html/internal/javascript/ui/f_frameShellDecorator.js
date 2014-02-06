/*
 * $Id: f_frameShellDecorator.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 *
 *
 * @class hidden f_frameShellDecorator extends f_shellDecorator
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __statics = {
	/**
	 * @field private static final String
	 */
	_SHELL_DECORATOR_IDENTIFIER: "frameShellDecorator"
};

var __members = {
	
	/**
	 * @field private HTMLIFrame;
	 */
	_iframe: undefined,
	
	f_frameShellDecorator: function(shell) {
		this.f_super(arguments, shell);
	},
	f_finalize: function() {		
		var iframe=this._iframe;
		if (iframe) {	
			this._iframe=undefined; // HtmlIFrame
			
			this.f_finalizeIframe(iframe);
		}
		
		this.f_super(arguments);
	},
	
	f_getId: function() {
		return f_frameShellDecorator._SHELL_DECORATOR_IDENTIFIER;
	},
	
	/**
	 * @method hidden
	 * @param Function functionWhenReady
	 * @return void
	 */	 
	f_createDecoration: function(functionWhenReady) {
		f_core.Assert(!this._iframe, "f_frameShellDecorator.f_createDecoration: Invalid state, iframe is not null ! ("+this._iframe+")");

		f_core.Debug(f_frameShellDecorator, "f_createDecoration: create new decoration");

		var shellId=this._shell.f_getId();
		var iframe = document.createElement("iframe");
		iframe.id = shellId+"::iframe";
		iframe.name = shellId+"::fname";			
		this._iframe=iframe;

		iframe.title=""; // De décoration
		
		var shell=this._shell;
		
		var shellIdentifier=this.f_registerShell(shell);
		iframe._rcfacesShellIdentifier=shellIdentifier;
		iframe._rcfacesShellDecoratorIdentifier=this.f_getId();
		f_core.Assert(iframe._rcfacesShellDecoratorIdentifier, "f_frameShellDecorator.f_createDecoration: Invalid id '"+iframe._rcfacesShellDecoratorIdentifier+"'.");

		iframe.frameBorder = 0;
		iframe.scrolling="no";
		
		var className="f_shellDecorator_frame";
		if (this.f_isTransparentShell()) {
			if (f_core.IsInternetExplorer()) {
				iframe.allowTransparency = true;
			}
			className+=" "+className+"_transparent";

		} else {
			className+=" "+className+"_border";			
		}

		var styleClass=this.f_getStyleClass();
		if (styleClass) {
			className+=" "+styleClass;
			if (this._shell.f_getStyle() & f_shell.TRANSPARENT) {
				className+=" "+styleClass+"_transparent";
	
			} else {
				className+=" "+styleClass+"_border";			
			}
		}
		
		iframe.className=className;
		
		f_core.InsertBefore(document.body, iframe, document.body.firstChild);
					
		var self=this;
		if (f_core.IsInternetExplorer() && (!f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_9))) {
			f_core.Debug(f_frameShellDecorator, "f_createDecoration: IE use onreadystatechange ");

			var loadFrame = function() {
				if (window._rcfacesExiting) {
					return false;
				}
				
				var frameWindow=f_core.GetFrameWindow(iframe);				
				frameWindow.setTimeout(function() {
					var doc=f_core.GetFrameDocument(iframe);
					try {
						self.f_prepareFrame(iframe, doc);
	
					} catch (x) {					
						f_core.Error(f_viewDialog, "f_createDecoration: f_performFrameReady throws exception.", x);
					}
					
					functionWhenReady.call(window, self, shell);
				}, 100);
			};
			
			f_core.AddEventListener(iframe, "load", loadFrame);
				
		} else {
			f_core.Debug(f_frameShellDecorator, "f_createDecoration: Firefox use onload ");
			iframe.onload=function() {
				if (window._rcfacesExiting) {
					return false;
				}

				f_core.Debug(f_frameShellDecorator, "f_createDecoration.onLoad: decoration created: "+this);
	
				this.onload=null;
				
				var doc=f_core.GetFrameDocument(iframe);
				self.f_prepareFrame(iframe, doc);
				
				functionWhenReady.call(window, self, shell);
			};
		}

		iframe.src="about:blank";
		
		f_core.Debug(f_frameShellDecorator, "f_createDecoration: wait decoration creation");
	},
	
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_isTransparentShell: function() {
		return (this._shell.f_getStyle() & f_shell.TRANSPARENT); 
	},
	
	/**
	 * @method hidden
	 * @return void
	 */	 
	f_destroyDecoration: function() {
		f_core.Assert(this._iframe, "f_frameShellDecorator.f_destroyDecoration: Invalid state, iframe is null !");
		
		var iframe=this._iframe;
		if (iframe) {
			this._iframe=undefined;
			
			this.f_finalizeIframe(iframe);
			
			window.setTimeout(function() {
				iframe.parentNode.removeChild(iframe);
				iframe=undefined;
			}, 10);
		}

		this.f_super(arguments);
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
	 * @method private
	 * @return void
	 */
	f_prepareFrame: function(iframe, shellDocument) {
		f_core.Debug(f_frameShellDecorator, "f_prepareFrame: decorate document="+shellDocument);
		
		var style=this._shell.f_getStyle();		
		
		var body=shellDocument.body;
		body.topmargin=0;
		body.leftmargin=0;
		body.marginheight=0;
		body.marginwidth=0;
	
		var className="f_shellDecorator_body";
		if (style && f_shell.TRANSPARENT) {
			className+=" "+className+"_transparent";
		}

		var styleClass=this.f_getStyleClass();
		if (styleClass) {
			className+=" "+styleClass+"_body";
			if (style && f_shell.TRANSPARENT) {
				className+=" "+styleClass+"_body_transparent";
			}
		}
		
		var shellStyleClass=this._shell.f_getStyleClass();
		if (shellStyleClass) {
			className+=" "+shellStyleClass;
		}
		body.className=className;	

		if (style & f_shell.COPY_STYLESHEET) {
			// Copie le BASE href
			
			var head=shellDocument.head;
			if (!head) {
				var root=shellDocument.documentElement;
				
				var head=f_core.GetFirstElementByTagName(root, "head");		
				if (!head) {
					head=f_core.CreateElement(root, "head");					
				}
			}

			var baseHref=document.baseURI || document.URL;
			
			if (!f_core.GetFirstElementByTagName(head, "base")) {
				f_core.CreateElement(head, "base", {
					href: baseHref
				});
			}
		}
		
		f_core.CopyStyleSheets(shellDocument, document);

		this.f_decorateShell(body);
	},
	/**
	 * Return a personalized css class name.
	 *
	 * @method protected
	 * @return String
	 */
	f_getStyleClass: function() {
		return null;
	},
	f_showShell: function() {

		if (!this._iframe) {
			return;
		}

		this._iframe.style.visibility="visible";		
	},
	f_hideShell: function() {

		if (!this._iframe) {
			return;
		}

		this._iframe.style.visibility="hidden";		
	},
	f_setShellBounds: function(shell, x, y, width, height) {
		
		var iframe=this._iframe;
		if (!iframe) {
			return;
		}
		
		// Def pos and size
		iframe.style.top = y+"px";
		iframe.style.left = x+"px";
		iframe.style.height = height+"px";
		iframe.style.width = width+"px";
						
		iframe._initialWidth=width;
		iframe._initialHeight=height;
		
		var doc=f_core.GetFrameDocument(iframe);
		
		var table=doc.body; //.firstChild;
		if (table) {
			table.style.height = height+"px";
			table.style.width = width+"px";			
		}
	},
	/**
	 * @method hidden
	 * @param Object target
	 * @return Boolean
	 */
	f_isIntoShell: function(target) {

		var iframe=this._iframe;
		if (!iframe) {
			return false;
		}
		
		var targetDocument=null;
		
		switch(target.nodeType) {
		case f_core.DOCUMENT_NODE:
			targetDocument=target;
			break;
			
		case f_core.ELEMENT_NODE:
			if (target.tagName.toLowerCase()=="iframe") {
				targetDocument=f_core.GetFrameDocument(target);
				break;
			}

			targetDocument=target.ownerDocument;
			break;

		default:
			// Qu'est que c'est ????
			
			targetDocument=target.document;
		}

		if (!targetDocument) {
			f_core.Debug(f_shellManager, "f_isIntoShell: type de noeud "+target.nodeType);
			return false;			
		}
 		
 		var frameDocument = f_core.GetFrameDocument(iframe);

     	if (targetDocument==frameDocument) {
 			f_core.Debug(f_shellManager, "f_isIntoShell: Same document ("+targetDocument.location+")");
     		return true;
     	}
     	
     	try {
     		f_core.Debug(f_shellManager, "f_isIntoShell: Different document ("+targetDocument.location+"/"+frameDocument.location+")");
     	} catch (x) {
     		// Probleme de sécurité possible !
     	}
     	return false;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_setFocus: function() {
		var iframe=this._iframe;
		if (!iframe) {
			return;
		}
		
		var doc=f_core.GetFrameDocument(iframe);
		var next=f_core.GetNextFocusableComponent(doc.body);	
		if (next) {
			f_core.SetFocus(next, true);
			return;
		}

		var win=f_core.GetFrameWindow(iframe);
		win.focus();
		this._shell.f_setFocus();
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_performViewResizeEvent: function() {
		var iframe=this._iframe;
		if (!iframe) {
			return;
		}
		
		var x=parseInt(iframe.style.left);
		var y=parseInt(iframe.style.top);
		var w=iframe._initialWidth;
		var h=iframe._initialHeight;
		
		var screenSize=f_shellManager.GetScreenSize(iframe.ownerDocument);
		if (f_core.IsGecko()) {
			screenSize.width-=f_core.ComputeBorderLength(iframe, "left", "right");
			screenSize.height-=f_core.ComputeBorderLength(iframe, "top", "bottom");
		}
		
		if (x+w>=screenSize.width) {
			w=screenSize.width-x-1;
			if (w<1) {
				w=1;
			}
		}
		if (w!=parseInt(iframe.style.width)) {
			iframe.style.width=w+"px";
		}

		if (y+h>=screenSize.height) {
			h=screenSize.height-y-1;
			if (h<1) {
				h=1;
			}
		}			
		if (h!=parseInt(iframe.style.height)) {
			iframe.style.height=h+"px";
		}		
	}
};

new f_class("f_frameShellDecorator", {
	extend: f_shellDecorator,
	members: __members,
	statics: __statics
});
