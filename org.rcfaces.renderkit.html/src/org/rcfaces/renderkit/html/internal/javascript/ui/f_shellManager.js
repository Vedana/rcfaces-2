/*
 * $Id: f_shellManager.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p><strong>f_shellManager</strong> represents shell manager.
 *
 * @class public f_shellManager extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */
var __statics = {
	/**
	 * @method public static
	 * @return f_shellManager
	 */
	Get: function() {
		var shellManager=f_shellManager._singleton;
		
		if (shellManager) {
			return shellManager;
		}
		
		shellManager=new f_shellManager();
		f_shellManager._singleton=shellManager;		
		
		return shellManager;
	},
	DocumentComplete: function() {
		f_shellManager._documentComplete=true;	
		
		var shellManager=f_shellManager._singleton;
		
		if (shellManager) {
	     	shellManager.f_showNextShell();
		}			
	},
    /**
     * <p>For IE 6 only : Hide selects that get over the Div</p>
     *
     * @method protected static
	 * @return void
     */
    HideSelect: function() {
		var tags=f_core.GetElementsByTagName(document, "select");

		for (var i=0;i<tags.length;i++) {
			var tag=tags[i];
			
			var old=tag._visibility_old;
			if (old === undefined) {
				old=tag.style.visibility;
				if (!old) {
					old="inherit";
				}
				tag._visibility_old=old;
			}

			tag.style.visibility="hidden";
		}		
    },

    /**
     * <p>For IE 6 only : Show selects that get over the Div</p>
     *
     * @method protected static
	 * @return void
     */
    ShowSelect: function() {
		var tags=f_core.GetElementsByTagName(document, "select");

		for (var i=0;i<tags.length;i++) {
			var tag=tags[i];
			if (tag._visibility_old) {
				tag.style.visibility=tag._visibility_old;
				tag._visibility_old=undefined;
			}
		}
    },
	/**
     *
     * @method private static
     * @param Event evt
     * @return Boolean
     * @context event:evt
     */
	_OnFocus: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (window._rcfacesExiting) {
     		// On sait jamais, nous sommes peut etre dans un context foireux ...
     		return true;
     	}
    	
     	f_core.Debug(f_shellManager, "_OnFocus: entering on "+this.tagName+"#"+this.id+"."+this.className+" event="+evt);
 
 		var shellManager=f_shellManager.Get();
 
 		var shell=shellManager.f_getTopShell();
 		if (!shell) {
 			// Plus de frame visibles ... (on peut être en cours de fermeture ...)
 	  		f_core.Info(f_shellManager, "_OnFocus: No top shell ?");
			return true;
 		}
      	 			
		var target=undefined;
		if (evt.target) {
			target = evt.target;
			
		} else if (evt.srcElement) {
			target = evt.srcElement;
		}
		
		if (!target) {
    		f_core.Info(f_shellManager, "_OnFocus: No target identified");
			return true;
		}

		var shellDecorator=shellManager.f_getShellDecorator(shell);
		
		if (shellDecorator.f_isIntoShell(target)) {
			f_core.Info(f_shellManager, "_OnFocus: Shell decorator="+shellDecorator+" into shell "+target);
			return true;
		}
		f_core.Info(f_shellManager, "_OnFocus: Shell decorator="+shellDecorator+" set focus "+target);
    	
     	shell.f_setFocus();
     	
     	return f_core.CancelJsEvent(evt);
     },
	 
	/**
     * @method public static
     * @param optional Document doc
     * @return Object size (width, height)
     */
    GetScreenSize: function(doc) {
 		var viewSize=f_core.GetViewSize(null, doc);
 		var docSize=f_core.GetDocumentSize(null, doc);
 		
 		var size= { 
 			width: (viewSize.width>docSize.width)?viewSize.width:docSize.width, 
 			height: (viewSize.height>docSize.height)?viewSize.height:docSize.height
 		};
 		
 		//document.title="viewSize:"+viewSize.width+","+viewSize.height+"  docSize="+docSize.width+","+docSize.height+"  size="+size.width+","+size.height;
 		
 		return size;
	},
	/**
	 * @method public static
	 * @param HTMLElement component
	 * @return f_shell
	 */
	GetShell: function(component) {
		f_core.Assert(component, "f_shellManager.GetShell: Invalid component parameter '"+component+"'.");
		
		var root=component;
		
		try {
			for(;component;) {
				if (component._shell) {
					return component._shell;
				}
				if (component._rcfacesShellDecoratorIdentifier) {
					var win=f_core.GetWindow(component);
				
					var shell=win.f_shellDecorator.GetShellFromIdentifier(component._rcfacesShellIdentifier);
					return shell;
				}
				
				var parent=component.parentNode;
				if (parent && parent.nodeType!=f_core.DOCUMENT_NODE) {
					component=parent;
					continue;
				}
				
				var win=f_core.GetWindow(component);
				if (!win) {
					break;
				}
				
				component=win.frameElement;
			}		
			
		} catch (ex) {
			f_core.Error(f_shellManager, "Can not find shell from root='"+root.id+"' and component='"+component.id+"'.", ex);
		}
		
		return null;
	},
	/**
	 * @method public static
	 * @param Object object A f_event object or a component (rcfaces or html)
	 * @param any returnValue
	 * @return Boolean Returns <code>true</code> if the shell is found.
	 */
	CloseShell: function(object, returnValue) {
		f_core.Debug(f_shellManager, "CloseShell: Request close shell '"+object+"'  returnValue='"+returnValue+"'");
		if (object instanceof f_event) {
			object=object.f_getComponent();
		}
		
		if (!object || object.nodeType!=f_core.ELEMENT_NODE) {
			return false;
		}
		
		var shell=f_shellManager.GetShell(object);
		if (!shell) {
			return false;
		}
		
		shell.f_close(returnValue);
		
		return true;
	},
	
	
	/**
	 * @method public static
	 * @param String name
	 * @param Function constructor
	 * @return void
	 */
	RegisterShellDecorator: function(name, constructor) {
		var shellDecorators=f_shellManager._ShellDecorators;
		if (!shellDecorators) {
			shellDecorators=new Object;
			f_shellManager._ShellDecorators = shellDecorators;
		}
		shellDecorators[name]=constructor;
	},
	Finalizer: function() {
		var shellManager=f_shellManager._singleton;
		if (shellManager) {
			f_shellManager._singleton=undefined; // f_shellManager
				
			if (shellManager._modalStyleInstalled) {
				shellManager._modalStyleInstalled=undefined;
				
				
				var capture=document;
				if (f_core.IsInternetExplorer(7) || f_core.IsInternetExplorer(8)) {
					capture=undefined;
				}

				f_core.RemoveEventListener(document, "focus", f_shellManager._OnFocus, capture);
			}
		}

		f_shellManager._documentComplete=undefined; //Boolean

		f_shellManager._ShellDecorators=undefined;
	}
};

var __members = {
	f_shellManager: function() {
		this._shells=new Array;
		this._shellDecorators=new Object;
	},
	f_finalize: function() {
		this._shells=undefined; // List<Shell>
		this._waitingShells=undefined; // List<Shell>
		
		var backgroundElement=this._backgroundElement;
		if (backgroundElement) {
			this._backgroundElement=undefined; // HtmlDivElement

			backgroundElement._shellManager=undefined; // f_shellManager	
		}
		
		this._removeResizeCallback();

		// Sécurité ! (fuite mémoire IE et Firefox)
		if (this._modalStyleInstalled) {
			// Il y a une exception ...
			// this._modalStyleInstalled=undefined; // boolean
			 		
			this.f_uninstallModalStyle();
		}
	},
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @return void
	 */
	f_pushShell: function(shell) {
		f_core.Debug(f_shellManager, "f_pushShell: push shell '"+shell._id+"'.");

		this._shells.f_addElement(shell);

		if (!this._backgroundElement && shell.f_getBackgroundMode()) {
			this._hideScreen();
		}
		
		if (shell.f_getStyle() & (f_shell.PRIMARY_MODAL_STYLE | f_shell.APPLICATION_MODAL_STYLE)) {
			this.f_installModalStyle();
		}
		
		this.f_getShellDecorator(shell).f_showShell();
	},
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @return void
	 */
	f_popShell: function(shell) {
		f_core.Debug(f_shellManager, "f_popShell: pop shell '"+shell._id+"'.");

		shell.f_setStatus(f_shell.DESTROYING_STATUS);			

		var shells=this._shells;

		if (!shells.f_removeElement(shell)) {
			return;
		}
		
		// On reste en modale ?
		if (!shells.length  ||  
			!(shells[shells.length-1].f_getStyle() & (f_shell.PRIMARY_MODAL_STYLE | f_shell.APPLICATION_MODAL_STYLE))) {
			this.f_uninstallModalStyle();
		}

		if (this._backgroundElement) {
			for(var i=0;i<shells.length;i++) {
				if (shells[i].f_getBackgroundMode()) {
					return;
				}
			}		
						
			this._showScreen();
		}
	},
	/**
	 * @method public
	 * @return f_shell
	 */
	f_getTopShell: function() {
		
		var shells=this._shells;
		for(var i=shells.length;i>0;) {
			var shell=shells[--i];
			
			if (shell.f_getStatus()==f_shell.OPENED_STATUS) {
				return shell;
			}
		}
		
		return null;
	},
	/**
	 * @method private
	 * @return void
	 */
	_hideScreen: function() {		
		var backgroundMode=undefined;
		
		var shells=this._shells;
		for(var i=0;i<shells.length;i++) {
			backgroundMode=shells[i].f_getBackgroundMode();
			if (backgroundMode) {
				break;
			}
		}		
		
		if (!backgroundMode) {
			return;
		}
		
		if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6)) {
			f_shellManager.HideSelect();
		}
				
		// Creation de la div recouvrant la page
		var div = document.createElement("div");
		this._backgroundElement=div;
		div._shellManager=this;
		
		div.className="f_shellManager_background f_shellManager_background_"+backgroundMode;
		
		this._removeResizeCallback();
		
		var self=this;
		this._onResizeCB=function() {
			//get the greying div
			
			self._performResizeEvent();
		};
		
		//Resize Handler
		f_core.AddResizeEventListener(document.body, this._onResizeCB);

		this._onResizeCB();

		//Hide Selects
		if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6)) {
			f_shellManager.HideSelect();
		}
		
		//Attach
		f_core.InsertBefore(document.body, div, document.body.firstChild);
	},
	/**
	 * @method private
	 * @return void
	 */
	_performResizeEvent: function() {
		
		var self=this;
		
		function f() {
			
			var div = self._backgroundElement;
			if (div) {
				// Get the document' size
				var size=f_shellManager.GetScreenSize();
						
				//Modify the size
				div.style.width=size.width+"px";
				div.style.height=size.height+"px";		
			}

					
			var shells=self._shells;
			if (shells) {
				for(var i=0;i<shells.length;i++) {
					var shell=shells[i];
					
					if (shell.f_getStatus()!=f_shell.OPENED_STATUS) {
						continue;
					}
					
					var shellDecorator=self.f_getShellDecorator(shell);
					
					shellDecorator.f_performViewResizeEvent();
				}
			}
		}
		
		f();
		
		window.setTimeout(f,10);
	},
	/**
	 * @method private
	 * @return void
	 */
	_showScreen: function() {
		var backgroundElement=this._backgroundElement;
		if (!backgroundElement) {
			return;
		}
				
		this._backgroundElement=undefined;
		backgroundElement._shellManager=undefined;
		
		backgroundElement.parentNode.removeChild(backgroundElement);

		this._removeResizeCallback();
		
		if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6)) {
			f_shellManager.ShowSelect();
		}
	},
	/**
	 * @method private
	 * @return void
	 */
	_removeResizeCallback: function() {
		var onResizeCB=this._onResizeCB; // function
		if (!onResizeCB) {
			return;
		}
		this._onResizeCB=undefined;
		
		f_core.RemoveResizeEventListener(document.body, onResizeCB);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_installModalStyle: function(shell) {		

     	f_core.Debug(f_shellManager, "f_installModalStyle: Install modal hooks");
		
		if (this._modalStyleInstalled) {
			return;
		}
		this._modalStyleInstalled=true;
		
		
		var capture=document;
		if (f_core.IsInternetExplorer(7) || f_core.IsInternetExplorer(8)) {
			capture=undefined;
		}

		f_core.AddEventListener(document, "focus", f_shellManager._OnFocus, capture);
	},

	/**
	 * @method protected
	 * @return void
	 */
	f_uninstallModalStyle: function() {
     	f_core.Debug(f_shellManager, "f_uninstallModalStyle: Uninstall modal hooks");
	
		if (!this._modalStyleInstalled) {
			return;
		}
	
		this._modalStyleInstalled=undefined;
			
		var capture=document;
		if (f_core.IsInternetExplorer(7) || f_core.IsInternetExplorer(8)) {
			capture=undefined;
		}

		f_core.RemoveEventListener(document, "focus", f_shellManager._OnFocus, capture);
	},
	/**
	 * @method public
	 * @param f_shell shell
	 * @return f_shellDecorator
	 */
	f_getShellDecorator: function(shell) {
		var shellDecorators=this._shellDecorators;
		
		var shellId=shell.f_getId();
		
		var shellDecorator=shellDecorators[shellId];
		
		if (shellDecorator) {
			return shellDecorator;
		}
		
		shellDecorator=this.f_newShellDecorator(shell);

		this.f_setShellDecorator(shell, shellDecorator);
		
		return shellDecorator;
	},
	/**
	 * @method public
	 * @param f_shell shell
	 * @param f_shellDecorator shellDecorator
	 * @return void
	 */
	f_setShellDecorator: function(shell, shellDecorator) {
		f_core.Debug(f_shellManager, "f_getShellDecorator: create new shell decorator: "+shellDecorator);
		
		var shellId=shell.f_getId();
		var shellDecorators=this._shellDecorators;
	
		if (shellDecorators[shellId]) {
			throw new Error("Shell decorator is already setted !");
		}

		shell.f_updateDecoration(shellDecorator);
		
		shellDecorators[shellId]=shellDecorator;
	},
	/**
	 * @method protected
	 * @param f_shell shell
	 * @return f_shellDecorator
	 */
	f_newShellDecorator: function(shell) {
		var shellDecoratorName = shell.f_getShellDecoratorName();
		if (shellDecoratorName && f_shellManager._ShellDecorators) {
			var f =f_shellManager._ShellDecorators[shellDecoratorName];
			if (f) {
				var decorator = f.call(this,shell);
				if (decorator) {
					return decorator;
				}
			}
		}
		
		if (shell.f_getStyle() & f_shell.LIGHT_CONTAINER_STYLE) {
			return f_divShellDecorator.f_newInstance(shell);
		}		
	
		return f_frameShellDecorator.f_newInstance(shell);
	},
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @return void
	 */
	f_openShell: function(shell) {
		if (!f_shellManager._documentComplete) {
			f_core.Debug(f_shellManager, "f_openShell: document is not complete, push shell into pipe !");
			
			var waitingShells=this._waitingShells;
			if (!waitingShells) {
				waitingShells=new Array;
				this._waitingShells=waitingShells;				
			}
			
			waitingShells.push(shell);
			return;
		}

		shell.f_preConstruct();
				
		f_core.Debug(f_shellManager, "f_openShell: create decoration");
		
		var self=this;
		this.f_getShellDecorator(shell).f_createDecoration(function(shellDecorator, shell) {
			f_core.Debug(f_shellManager, "f_openShell: creation started ...");
			
			shell.f_setStatus(f_shell.OPENING_STATUS);

			shell.f_postConstruct();

			shell.f_fillBody(shellDecorator.f_getShellBody());
			
			shell.f_prepareOpening();
			
			shell.f_setStatus(f_shell.OPENED_STATUS);
			
			self.f_pushShell(shell);
		});		
		
	},
	/**
	 * @method hidden
	 * @param optional f_shell shell
	 * @return optional Boolean showNextShell
	 * @return void
	 */
	f_closeShell: function(shell, showNextShell) {
		f_core.Debug(f_shellManager, "f_closeShell: Requested close shell '"+shell+"' showNextShell='"+showNextShell+"'");
		if (shell.f_getStatus() == f_shell.CREATED_STATUS) {
			shell.f_setStatus(f_shell.DESTROYING_STATUS); // Directement ...
		}

		if (shell.f_getStatus() == f_shell.OPENING_STATUS || shell.f_getStatus() == f_shell.OPENED_STATUS) {
			shell.f_setStatus(f_shell.CLOSING_STATUS);
		}

		if (shell.f_getStatus()==f_shell.CLOSING_STATUS) {
			try {
				this.f_getShellDecorator(shell).f_hideShell();
			
			} catch (x) {
				f_core.Error(f_shellManager, "f_closeShell: f_hideShell throws exception self="+self, x);
			}

			try {
				shell.f_preDestruction(); // C'est le preDestruction qui positionne le status ABOUT_TO_CLOSE ...

			} catch (x) {
				f_core.Error(f_shellManager, "f_closeShell: f_preDestruction throws exception self="+self, x);
			}
		}

		if (shell.f_getStatus()==f_shell.ABOUT_TO_CLOSE_STATUS) {
			try {
				this.f_getShellDecorator(shell).f_destroyDecoration();
				// C'est le Shell decorator qui positionne CLOSED_STATUS
			} catch (x) {
				f_core.Error(f_shellManager, "f_closeShell: f_destroyDecoration throws exception self="+self, x);
			}
		}
		
		if (shell.f_getStatus()==f_shell.CLOSED_STATUS) {
			try {
				this.f_popShell(shell);
			
			} catch (x) {
				f_core.Error(f_shellManager, "f_closeShell: f_popShell throws exception self="+self, x);
			}
			
			// C'est le Shell decorator qui positionne DESTROYING_STATUS
		}
		
		if (shell.f_getStatus()==f_shell.DESTROYING_STATUS) {
			try {
				shell.f_postDestruction();
				
			} catch (x) {
				f_core.Error(f_shellManager, "f_closeShell: postDestruction throws exception self="+self, x);
			}
		}
		
		if (shell.f_getStatus()==f_shell.DESTROYED_STATUS) {
			
			if (showNextShell!==false) {
				showNextShell=(shell.f_isNextShellCanceled()===true);
			}
			
			if (showNextShell!==false) {
				this.f_showNextShell();
			}
		}
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_showNextShell: function() {
    	var waitingShells=this._waitingShells;
    	
    	if (!waitingShells) {
    		return;
    	}
    	
    	for(;waitingShells.length;) {		    		
    		var waitingShell=null;
    		
    		for(var i=0;i<waitingShells.length;i++) {
    			var ws=waitingShells[i];
    			
    			if (!waitingShell || ws._priority>waitingShell._priority) {
    				waitingShell=ws;
    			}
    		}
    	
    		if (!waitingShell) {
    			return;
    		}
    		
    		waitingShells.f_removeElement(waitingShell);
    	
    		this.f_openShell(waitingShell);
    		
    		// Nous sommes en mode modal ?
    		// On arrete alors !

			if (waitingShell.f_getStyle() & (f_shell.PRIMARY_MODAL_STYLE | f_shell.APPLICATION_MODAL_STYLE)) {
				return; 
	    	}			
    	}			    
	},
	
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @return Boolean
	 */
	f_hasShellDecorator: function (shell) {
		var shellDecorators=this._shellDecorators;
		if(shellDecorators[shell.f_getId()]){
			return true;
		}
		return false;
	},
	
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @param String key
	 * @param optional any value
	 * @return void
	 */
	f_setShellDecoration: function(shell, key, value) {
		if (key==f_shellDecorator.INSTANCE_DECORATOR) {
			this.f_setShellDecorator(shell, value);
			return;
		}
		
		this.f_getShellDecorator(shell).f_setDecorationValue(key, value);
	},
	/**
	 * @method hidden
	 * @param f_shell shell
	 * @param Number x
	 * @param Number y
	 * @param Number width
	 * @param Number height
	 * @return void
	 */
	f_setShellBounds: function(shell, x, y, width, height) {
		this.f_getShellDecorator(shell).f_setShellBounds(shell, x, y, width, height);
	},
	/**
	 * @method public
	 * @return void
	 */
	f_clearPendingShells: function() {
    	this._waitingShells=undefined;
	}
};

new f_class("f_shellManager", {
	statics: __statics,
	members: __members
});
