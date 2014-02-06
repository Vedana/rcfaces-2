/*
 * $Id: f_divShellDecorator.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 *
 *
 * @class hidden f_divShellDecorator extends f_shellDecorator
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	
	/**
	 * @field private static final String
	 */
	_SHELL_DECORATOR_IDENTIFIER: "divShellDecorator"
}

var __members = {
	
	/**
	 * @field private HTMLIFrame;
	 */
	_div: undefined,
	
	f_divShellDecorator: function(shell) {
		this.f_super(arguments, shell);
	},
	f_finalize: function() {		
		var div=this._div;
		if (div) {	
			this._div=undefined; // HtmlIFrame
			
			this.f_finalizeDiv(div);
		}
		
		this.f_super(arguments);
	},
	
	f_getId: function() {
		return f_divShellDecorator._SHELL_DECORATOR_IDENTIFIER;
	},
	
	/**
	 * @method hidden
	 * @param Function functionWhenReady
	 * @return void
	 */	 
	f_createDecoration: function(functionWhenReady) {
		f_core.Assert(!this._div, "f_divShellDecorator.f_createDecoration: Invalid state, div is not null ! ("+this._div+")");

		f_core.Debug(f_divShellDecorator, "f_createDecoration: create new decoration");

		var div = document.createElement("div");
		this._div=div;

		div.id = this._shell.f_getId()+"::div";
		div.name = div.id+"::name";
	
		var shell=this._shell;
		
		var shellIdentifier=this.f_registerShell(shell);
		div._rcfacesShellIdentifier=shellIdentifier;
		div._rcfacesShellDecoratorIdentifier=this.f_getId();
		f_core.Assert(div._rcfacesShellDecoratorIdentifier, "f_divShellDecorator.f_createDecoration: Invalid id '"+div._rcfacesShellDecoratorIdentifier+"'.");
		
		var className="f_shellDecorator_frame";
		if (this._shell.f_getStyle() & f_shell.TRANSPARENT) {
			if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6)) {
				div.allowTransparency = true;
			}
			className+=" "+className+"_transparent";

		} else {
			className+=" "+className+"_border";			
		}
		
		div.className=className;
				
		this.f_prepareFrame(div);
				
		functionWhenReady.call(window, this, shell);
		
		f_core.InsertBefore(document.body, div, document.body.firstChild);
		
		f_core.Debug(f_divShellDecorator, "f_createDecoration: wait decoration creation");
	},
	
	/**
	 * @method hidden
	 * @return void
	 */	 
	f_destroyDecoration: function() {
		f_core.Assert(this._div, "f_divShellDecorator.f_destroyDecoration: Invalid state, div is null !");
		
		var div=this._div;
		if (div) {
			this._div=undefined;
			
			this.f_finalizeDiv(div);

			div.parentNode.removeChild(div);
		}

		this.f_super(arguments);
	},
	
	/**
	 * @method protected
	 * @param HTMLDivElement iframe
	 * @return void
	 */
	f_finalizeDiv: function(div) {
		
		f_core.VerifyProperties(div);		
	},
	/**
	 * @method private
	 * @return void
	 */
	f_prepareFrame: function(div) {
		f_core.Debug(f_divShellDecorator, "f_prepareFrame: decorate div="+div);
		
		var style=this._shell.f_getStyle();		
		
		var body=f_core.CreateElement(div, "div");
		
		body.style.width="100%";
		body.style.height="100%";
	
		var className="f_shellDecorator_body";
		if (style && f_shell.TRANSPARENT) {
			className+=" "+className+"_transparent";
		}
		
		var shellStyleClass=this._shell.f_getStyleClass();
		if (shellStyleClass) {
			className+=" "+shellStyleClass;
		}
		body.className=className;	

		this.f_decorateShell(body);
	},
	f_showShell: function() {

		if (!this._div) {
			return;
		}

		this._div.style.visibility="visible";		
	},
	f_hideShell: function() {

		if (!this._div) {
			return;
		}

		this._div.style.visibility="hidden";		
	},
	f_setShellBounds: function(shell, x, y, width, height) {
		
		var iframe=this._div;
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
		
		
		var table=this._div.firstChild; //.firstChild;
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

		var iframe=this._div;
		if (!iframe) {
			return false;
		}

		for(;target;target=target.parentNode) {
			if (target._shell._div==iframe) {
				return true;
			}
		}
		
		return false;		
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_setFocus: function() {
		// On recherche le premier element pouvant avoir le focus
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_performViewResizeEvent: function() {
		var iframe=this._div;
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
}


new f_class("f_divShellDecorator", {
	extend: f_shellDecorator,
	members: __members,
	statics: __statics
});
