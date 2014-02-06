/*
 * $Id: f_popup.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 *
 * @class hidden f_popup extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	 
	/**
	 * @field hidden static final Number
	 */
	IE_POPUP_POPUP_TYPE: 0,
	 
	/**
	 * @field hidden static final Number
	 */
	IE_FRAME_POPUP_TYPE: 1,
	

	/**
	 * @field hidden static final Number
	 */
	MOUSE_POSITION:0, 

	/**
	 * @field hidden static final Number
	 */
	MIDDLE_COMPONENT: 1,

	/**
	 * @field hidden static final Number
	 */
	BOTTOM_COMPONENT: 2,

	/**
	 * @field hidden static final Number
	 */
	LEFT_COMPONENT: 4,

	/**
	 * @field hidden static final Number
	 */
	BOTTOM_LEFT_COMPONENT: 8,

	/**
	 * @field hidden static final Number
	 */
	RIGHT_COMPONENT: 16,
	/**
	 * @field hidden static final Number
	 */
	BOTTOM_RIGHT_COMPONENT: 32,
	
	/**
	 * @field hidden static final String
	 */
	NO_CONTEXT_POPUP: "noContextPopup",
	
	/**
	 * @field hidden static
	 */
	Callbacks: undefined,
	
	/**
	 * @field hidden static
	 */
	Popup: undefined,
	
	/**
	 * @field hidden static
	 */
	Component: undefined,
	
	/**
	 * @field private static
	 */
	_OldContextMenu: undefined,
	
	/**
	 * @field private static
	 */
	_Installed: undefined,
	
	/**
	 * @field private static
	 */
	_KeyProvider: undefined,
	
	/**
	 * @field private static boolean
	 */
	_LockPopupEvents: undefined,
	
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {
		if (f_popup.Ie_enablePopup()) {
			var popup=f_popup._Ie_PreparePopup(document);
			
			// BUG IE: S'il y a une restriction de domain en JS (Erreur d'accés)
			popup.document._rootPopup=true;
		}
	},
	DocumentComplete: function() {
		if (f_popup.Ie_enablePopup()) {
			var _doc=document;
		
			window.setTimeout(function() {
				if (!_doc._rcfacesIEPopup) {
					return;
				}
				
				f_popup._Ie_InitializePopup(_doc._rcfacesIEPopup, _doc);
				
				_doc=null;
			},10);
		}
	},
	
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_popup.Callbacks=undefined; // Map of functions
		f_popup.Popup=undefined; // HTMLComponent
		f_popup.Component=undefined;  // f_component
		f_popup._OldContextMenu=undefined; // function
		// f_popup._Installed=undefined; // boolean
		f_popup._KeyProvider=undefined; // f_component
		
		if (document._rcfacesIEPopup) {
			var ps=[document];
			
			for(;ps.length;) {
				var doc=ps.pop();
				
				var popup=doc._rcfacesIEPopup;
				if (!popup) {
					continue;
				}
				// doc._rcfacesStylesInitialized=undefined;
				doc._rcfacesIEPopup=undefined;
								
				ps.push(popup.document);				
			}
		}
	},
	/**
	 * @method hidden static
	 */
	Ie_enablePopup: function() {
		if (window._rcfacesIePopupEnable===false) {
			return false;
		}
		
		return f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6);
	},
	/**
	 * @method private static
	 */
	_Ie_PreparePopup: function(doc, useIt) {		
		var popup=doc._rcfacesIEPopup;
		
		if (!popup) {
			popup=f_popup._Ie_CreatePopup(doc);
			doc._rcfacesIEPopup=popup;
		}
				
		if (useIt) {
			var pdocument=popup.document;
	
			f_popup._Ie_InitializePopup(popup, doc);
			
			f_popup._Ie_PreparePopup(pdocument);

			// Ajoute les ressources qui n'avaient pas été encore initialisées ...
			if (!pdocument._rcfacesStylesInitialized) {
				pdocument._rcfacesStylesInitialized=true;
				
				f_core.CopyStyleSheets(pdocument, document, pdocument.styleSheets.length);
			}
		}
				
		return popup;
	},
	/**
	 * @method hidden static
	 * @param Document doc
	 * @param Number type
	 * @param function readyFunction
	 * @return HTMLElement
	 */
	Ie_GetPopup: function(doc, type, readyFunction) {
		f_core.Debug(f_popup, "Ie_GetPopup: Prepare popup for document="+doc+" type="+type);

		if (type==f_popup.IE_FRAME_POPUP_TYPE) {
			// En mode frame !
			
			var iframe=doc.createElement("IFRAME");
			iframe.frameBorder = 0;
			
			iframe.onreadystatechange=function() {
				f_core.Debug(f_popup, "Ie_GetPopup.readyStateChange: frame created");

				if (this.readyState != "interactive") {
					return;
				}	
				
				this.onreadystatechange=null;
				
				var pdocument=iframe.contentWindow.document;
				
				f_core.CopyStyleSheets(pdocument, doc, pdocument.styleSheets.length);

				var body=pdocument.body;
				body.topmargin=0;
				body.leftmargin=0;
				body.marginheight=0;
				body.marginwidth=0;
							
				body.className="f_popup_iframe_body";			
				
				if (typeof(readyFunction)=="function") {
					readyFunction.call(f_popup, iframe, pdocument);
				}
			};
			
			iframe.className="f_popup_iframe";
			
			f_core.InsertBefore(doc.body, iframe, doc.body.firstChild);
			
			iframe.src="about:blank";
			
			return undefined;
		}

		var popup=f_popup._Ie_PreparePopup(doc, true);
		
		f_core.Assert(popup, "f_popup.Ie_GetPopup: Invalid popup from document="+doc);
		f_core.Debug(f_popup, "Ie_GetPopup: Prepared popup="+popup);

		var body=popup.document.body;
		for(;body.firstChild;) {
			body.removeChild(body.firstChild);
		}

		f_core.Debug(f_popup, "Ie_GetPopup: Returned popup="+popup);
		
		if (typeof(readyFunction)=="function") {
			readyFunction.call(f_popup, popup, popup.document);
		}
	
		return popup;
	},
	/**
	 * @method private static
	 * @return void
	 */
	_Ie_InitializePopup: function(popup, doc) {
		var pdocument=popup.document;
		if (pdocument._rcfacesInitializedPopup) {
			return;
		}
		
		pdocument._rcfacesInitializedPopup=true;
		
		var bases=doc.getElementsByTagName("BASE");
		if (bases.length) {
			var base=bases[bases.length-1];	// On prend le dernier !
	
			var head=f_core.GetFirstElementByTagName(pdocument, "head");
			if (!head) {
				head=pdocument.createElement("head");
				pdocument.appendChild(head);
			}
	
			var pbase=pdocument.createElement(base.tagName);
			pbase.href=base.href;

			f_core.AppendChild(head, pbase);
		}
		
		f_core.CopyStyleSheets(pdocument, doc);		
	},
	/**	 * @method private static
	 */
	_Ie_CreatePopup: function(doc) {
		var popup=doc.parentWindow.createPopup();
		
		return popup;
	},
	/**
	 * @method hidden static
	 */
	RegisterWindowClick: function(callbacks, component, popup, keyProvider) {
		f_core.Assert(component, "f_popup.RegisterWindowClick: Component parameter is null !");
		f_core.Assert(typeof(callbacks)=="object", "f_popup.RegisterWindowClick: Callback parameter is null !");

		f_core.Debug(f_popup, "RegisterWindowClick: Register callbacks on component='"+component.id+"'.");

		var doc=component.ownerDocument;

		if (!f_popup._OldContextMenu) {
			var oldContextMenu=doc.body.oncontextmenu;
			if (!oldContextMenu) {
				oldContextMenu=f_popup.NO_CONTEXT_POPUP;
			}
			f_popup._OldContextMenu=oldContextMenu;

			doc.body.oncontextmenu=f_core.CancelJsEventHandler;
		}
		
		if (!f_popup._LockPopupEvents) {
			f_popup._LockPopupEvents=true;
		
			f_event.EnterEventLock(f_event.POPUP_LOCK);
		}
		
		f_core.Debug(f_popup, "RegisterWindowClick: Register popup on "+component.id);

		var oldComponent=f_popup.Component;
		if (oldComponent) {
			//alert("Already old component !");
			
			// On clot le precedant
			f_popup.Component=undefined;
			
			var oldCallbacks=f_popup.Callbacks;
			if (oldCallbacks) {
				f_core.Debug(f_popup, "RegisterWindowClick: exit old component "+oldComponent.id);

				oldCallbacks.exit.call(oldComponent);
				f_popup.Callbacks=undefined;
			}
			f_popup.Popup=undefined;
			
			return false;
		}
		
		f_popup.Callbacks=callbacks;
		f_popup.Component=component;
		f_popup.Popup=popup;
		f_popup._KeyProvider=keyProvider;
		
		if (f_popup._Installed) {			
			return true;
		}
		f_popup._Installed=true;

		// Dans le cas  IE pas de Register Window click
		if (f_popup.Ie_enablePopup()) {			
			
			if (keyProvider) {
				keyProvider.f_insertEventListenerFirst(f_event.KEYDOWN, f_popup._OnKeyDown);
				keyProvider.f_insertEventListenerFirst(f_event.KEYUP, f_popup._OnKeyUp);
				keyProvider.f_insertEventListenerFirst(f_event.KEYPRESS, f_popup._OnKeyPress);
			}
			
			if (popup.tagName=="IFRAME") {
				var body=doc.body;
				body.onmousedown=f_popup._Ie_OnMouseDown;
				body.onkeyup=f_popup._OnKeyUpJs;
				body.onkeydown=f_popup._OnKeyDownJs;
				body.onkeypress=f_popup._OnKeyPressJs;

				body=popup.contentWindow.document.body;
				body.onkeyup=f_popup._OnKeyUpJs;
				body.onkeydown=f_popup._OnKeyDownJs;
				body.onkeypress=f_popup._OnKeyPressJs;

				//document.body.onfocus=f_popup._Ie_OnMouseFocus;
			}
			
			return true;
		}
		
		f_core.AddEventListener(doc,"mousedown", f_popup._Gecko_OnMouseDown, doc);		
		f_core.AddEventListener(doc,"click", f_popup._Gecko_OnClick, doc);		
		f_core.AddEventListener(doc,"dblclick", f_popup._Gecko_OnClick, doc);
		
		if(!f_core.IsInternetExplorer()) {
			
			f_core.AddEventListener(doc,"blur", f_popup._Gecko_OnBlur, doc);
			f_core.AddEventListener(doc,"focus", f_popup._Gecko_OnFocus, doc);
		}
		
		f_core.AddEventListener(doc,"keydown", f_popup._OnKeyDownJs, doc);
		f_core.AddEventListener(doc,"keyup", f_popup._OnKeyUpJs, doc);
		f_core.AddEventListener(doc,"keypress", f_popup._OnKeyPressJs, doc);		
		f_core.AddEventListener(doc,"contextmenu", f_popup._Gecko_OnContextMenu, doc);
		return true;
	},
	/**
	 * @method private static
	 */
	_UnlockPopupEvent: function() {	
		if (!f_popup._LockPopupEvents) {
			return;
		}
		
		f_popup._LockPopupEvents=undefined;
		
		f_event.ExitEventLock(f_event.POPUP_LOCK);
	},
	/**
	 * @method hidden static
	 */
	UnregisterWindowClick: function(component) {	
		f_core.Debug(f_popup, "Unregister callbacks on component='"+component.id+"'.");

		var doc=component.ownerDocument;
		
		f_popup._UnlockPopupEvent();

		var contextMenu=f_popup._OldContextMenu;
		if (contextMenu) {
			f_popup._OldContextMenu=undefined;

			if (contextMenu==f_popup.NO_CONTEXT_POPUP) {
				contextMenu=null; // null pour IE

			} else if (!contextMenu) {
				contextMenu=null;
			}
			
			doc.body.oncontextmenu=contextMenu;
		}

		f_core.Debug(f_popup, "Unregister popup on "+component.id);
					
		var keyProvider=f_popup._KeyProvider;
		f_popup._KeyProvider=undefined;			
		
		f_popup.Callbacks=undefined;
		f_popup.Component=undefined;
		
		var popup=f_popup.Popup;
		f_popup.Popup=undefined;
		
		if (!f_popup._Installed) {
			return;
		}
		
		f_popup._Installed=undefined;		

		// Dans le cas  IE pas de Register Window click
		if (f_popup.Ie_enablePopup()) {
			if (keyProvider) {
				keyProvider.f_removeEventListener(f_event.KEYDOWN, f_popup._OnKeyDown);
				keyProvider.f_removeEventListener(f_event.KEYUP, f_popup._OnKeyUp);
				keyProvider.f_removeEventListener(f_event.KEYPRESS, f_popup._OnKeyPress);		
			}
			
			if (popup.tagName=="IFRAME") {
				var body=doc.body;				
				body.onmousedown=null;
				body.onkeyup=null;
				body.onkeydown=null;
				body.onkeypress=null;

				body=popup.contentWindow.document.body;
				body.onkeyup=null;
				body.onkeydown=null;
				body.onkeypress=null;
			}
			
			return;
		}

		f_core.RemoveEventListener(doc,"mousedown", f_popup._Gecko_OnMouseDown, doc);		
		f_core.RemoveEventListener(doc,"click", f_popup._Gecko_OnClick, doc);		
		f_core.RemoveEventListener(doc,"dblclick", f_popup._Gecko_OnClick, doc);		
		f_core.RemoveEventListener(doc,"blur", f_popup._Gecko_OnBlur, doc);
		f_core.RemoveEventListener(doc,"focus", f_popup._Gecko_OnFocus, doc);
		f_core.RemoveEventListener(doc,"keydown", f_popup._OnKeyDownJs, doc);
		f_core.RemoveEventListener(doc,"keyup", f_popup._OnKeyUpJs, doc);
		f_core.RemoveEventListener(doc,"keypress", f_popup._OnKeyPressJs, doc);		
		f_core.RemoveEventListener(doc,"contextmenu", f_popup._Gecko_OnContextMenu, doc);
		
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context document:this
	 */
	_Gecko_OnContextMenu: function(evt) {

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context document:this
	 */
	_Gecko_OnMouseDown: function(evt) {
		
		var target = evt.target || evt.srcElement;
		
		f_core.Debug(f_popup, "_OnMouseDown on "+this+" target="+target+"/"+target.className+"  popupComponent="+f_popup.Component);

		var component=f_popup.Component;
		if (!component) {
			return true;
		}
		
		// Si la target n'est pas dans une popup on ferme !
		
		var found=f_popup.IsChildOfDocument(target, evt);
		f_core.Debug(f_popup, "OnMouseDown search parent="+found);

		if (found) {
			if (f_popup.VerifyMouseDown(component, evt)) {
				f_core.Debug(f_popup, "_OnMouseDown: event into popup !");
				return true;
			}
			evt.cancelBubble=true; // On accepte l'evenement, mais on le passe surtout pas au parent !
			return true;
		}
	
		f_core.Debug(f_popup, "_OnMouseDown: click outside: close the popup !");
		var clb=f_popup.Callbacks;
		f_popup.Callbacks=undefined;
		
		clb.exit.call(component, evt);		
		
		// On Poursuit l'evenement ? finalement OUI, car c'est comme ca sur le bureau Windows !
	//	evt.cancelBubble=true; // A VERIFIER: On accepte l'evenement, mais on le passe surtout pas au parent !
		return false;
	},
	/**
	 * On verifie que la target de l'evenement est bien dans la popup ouverte
	 * 
	 * @method hidden static
	 * @return Boolean <code>true</code> if the target is contain by the current popup.
	 */
	IsChildOfDocument: function(target, event) {
		f_core.Debug(f_popup, "IsChildOfDocument: Search parent target='"+target+"' document='"+f_popup.Popup+"'.");

		var popupDocument=f_popup.Popup;
		if (!popupDocument) {
			// Pas de popup
			
			f_core.Debug(f_popup, "IsChildOfDocument: no popup document => false");
			return false;
		}
				
		for(;target;target=target.parentNode) {
			f_core.Debug(f_popup, "IsChildOfDocument: Test child '"+target+"' #"+target.id+" popupObject='"+target._popupObject+"' popupParent='"+target._popupParent+"'");
		
			if (target==popupDocument) {
				// on tombe sur notre popup => retourne TRUE
				
				f_core.Debug(f_popup, "IsChildOfDocument: parent is popup document => true");
				return true;
			}

			if (target._menu==popupDocument || target._popupObject) {
				// C'est un sous menu => retourne TRUE
				
				f_core.Debug(f_popup, "IsChildOfDocument: menu or popupObject => true");
				return true;
			}
						
			var isPopupLock=target.f_isPopupLock; // Le composant qui a ouvert la popup peut s'inclure dans la popup
			
			if (typeof(isPopupLock)=="function") {
				var cret=isPopupLock.call(target, popupDocument, event);
				
				if (cret===false) {
					// Notre composant indique de ne pas considerer la target dans la popup

					f_core.Debug(f_popup, "IsChildOfDocument: f_isPopupLock returns false");
					return false;
				}
				
				if (cret===true) {
					// Notre composant indique que la target est bien dans la popup
					f_core.Debug(f_popup, "IsChildOfDocument: f_isPopupLock returns true");
					return true;
				}
			}
		}
		
		// La target n'est pas dans la popup
		
		f_core.Debug(f_popup, "IsChildOfDocument: no parent popup => false");
		return false;
	},
	_Ie_OnMouseFocus: function() {
		// alert("Focus !");
	},
	/**
	 * @method private static
	 * @return Boolean
	 * @context event:evt
	 */
	_Ie_OnMouseDown: function(evt) {
		var evt = f_core.GetJsEvent(this);
		
		f_core.Debug(f_popup, "_Ie_OnMouseDown: click on "+this+" fromElement="+evt.srcElement+"/"+evt.srcElement.className);
		
		var component=f_popup.Component;
		if (!component) {
			return true;
		}
		
		var found=f_popup.IsChildOfDocument(evt.srcElement, evt);
		f_core.Debug(f_popup, "_Ie_OnMouseDown: search parent="+found);

		if (found) {
			evt.cancelBubble=true; // On accepte l'evenement, mais on le passe surtout pas au parent !
			return true;
		}
	
		f_core.Debug(f_popup, "_Ie_OnMouseDown: click outside: close the popup !");
		var clb=f_popup.Callbacks;
		f_popup.Callbacks=undefined;
		
		var component=f_popup.Component;
		clb.exit.call(component, evt);		
		
		// On Poursuit l'evenement ? finalement OUI, car c'est comme ca sur le bureau Windows !
	//	evt.cancelBubble=true; // A VERIFIER: On accepte l'evenement, mais on le passe surtout pas au parent !
		return false;
	},
	/**
	 * @method private static
	 * @return Boolean
	 * @context document:this
	 */
	_Gecko_OnClick: function(evt) {
		var target = evt.target || evt.srcElement;
		f_core.Debug(f_popup, "OnClick: click on "+this+" target="+target+"/"+target.className);

		if (!f_popup.Component) {
			return true;
		}

		if (f_core.IsPopupButton(evt)) {
			return f_core.CancelJsEvent(evt);
		}
		
		return true;
	},
	/**
	 * @method private static
	 * @context document:this
	 */
	_Gecko_OnBlur: function(evt) {	
		var target = evt.target || evt.srcElement;
		f_core.Debug(f_popup, "OnBlur on "+this+" target="+target+"/"+target.className);

		if (!f_popup.Component) {
			return;
		}
		var found=f_popup.IsChildOfDocument(target, evt);
		f_core.Debug(f_popup, "OnFocus search parent="+found);

		if (found) {
			return true;
		}
		if (f_popup.Callbacks) {
			try {
				f_popup.Callbacks.exit.call(f_popup.Component, evt);
				
			} catch (x) {
				f_core.Error(f_popup, "exit callback throws exception", x);
			}
		}
		
		return true;
	},
	/**
	 * @method private static
	 * @context document:this
	 */
	_Gecko_OnFocus: function(evt) {
		if (window._rcfacesExiting) {
			return;
		}
		var target = evt.target || evt.srcElement;
		f_core.Debug(f_popup, "_Gecko_OnFocus: on "+this+" target="+target+"/"+target.className);

		if (!f_popup.Component) {
			return;
		}
		
		var found=f_popup.IsChildOfDocument(target, evt);
		f_core.Debug(f_popup, "OnFocus search parent="+found);

		if (found) {
			return true;
		}
	
		if (f_popup.Callbacks) {
			try {
				f_popup.Callbacks.exit.call(f_popup.Component, evt);
				
			} catch (x) {
				f_core.Error(f_popup, "Exit callback throws exception", x);
	
			} finally {
				f_popup.Callbacks=undefined;
			}
		}
		
		return true;
	},
	/**
	 * @method private static
	 * @param f_event evt 
	 * @return Boolean
	 * @context object:this
	 */
	_OnKeyDown: function(evt) {
		return f_popup._OnKeyDownJs(evt._jsEvent);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context event:evt
	 */
	_OnKeyDownJs: function(evt) {	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
	
		var component=f_popup.Component;
		if (!component) {
			f_core.Debug(f_popup, "_OnKeyDownJs: keyCode="+evt.keyCode+" on "+this+" no component");

			return true;
		}
	
		var target=evt.target;
		if (!target) {
			target=evt.srcElement;
		}
		f_core.Debug(f_popup, "_OnKeyDownJs: keyCode="+evt.keyCode+" on "+this+" component:"+component+" target:"+target);
		
		var callbacks=f_popup.Callbacks;
		if (!callbacks) {
			// Bizarre ... pas possible ???
			return f_core.CancelJsEvent(evt);
		}
		if (evt.altKey) { // ?
			try {
				if (callbacks.exit.call(component, evt)===true) {
					return true;
				}
				
			} catch (x) {
				f_core.Error(f_popup, "_OnKeyDownJs: Exit callback throws exception", x);
				
			} finally {
				f_popup.Callbacks=undefined;
			}

			return f_core.CancelJsEvent(evt);
		}
		
		try {
			var keyDown=callbacks.keyDown;
			if (keyDown) {
				if (keyDown.call(component, evt, f_popup.Popup)===false) {
					return f_core.CancelJsEvent(evt);
				}
				
				return true;
			}			
		} catch (x) {
			f_core.Error(f_popup, "_OnKeyDownJs: KeyDown callback throws exception", x);
		}
			
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param f_event evt 
	 * @return Boolean
	 * @context object:this
	 */
	_OnKeyUp: function(evt) {				
		return f_popup._OnKeyUpJs(evt._jsEvent);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context event:evt
	 */
	_OnKeyUpJs: function(evt) {	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		var component=f_popup.Component;
		if (!component) {
			f_core.Debug(f_popup, "_OnKeyUpJs["+evt.keyCode+"] on "+this+" no component");

			return true;
		}
	
		var target=evt.target;
		if (!target) {
			target=evt.srcElement;
		}
		f_core.Debug(f_popup, "_OnKeyUpJs["+evt.keyCode+"] on "+this+" component:"+component+" target:"+target);
		
		var callbacks=f_popup.Callbacks;
		try {
			if (callbacks.keyUp) {
				if (callbacks.keyUp.call(component, evt, f_popup.Popup)===false) {
					return f_core.CancelJsEvent(evt);
				}
			}
			
		} catch (x) {
			f_core.Error(f_popup, "_OnKeyUpJs: KeyUp callback throws exception", x);
		}
		
		return true;
	},
	/**
	 * @method private static
	 * @param f_event evt 
	 * @return Boolean
	 * @context object:this
	 */
	_OnKeyPress: function(evt) {
		return f_popup._OnKeyPressJs(evt._jsEvent);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context event:evt
	 */
	_OnKeyPressJs: function(evt) {	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		var component=f_popup.Component;
		if (!component) {
			f_core.Debug(f_popup, "_OnKeyPressJs["+evt.keyCode+"] on "+this+" no component");

			return true;
		}
	
		var callbacks=f_popup.Callbacks;
		if (!callbacks) {
			f_core.Debug(f_popup, "_OnKeyPressJs["+evt.keyCode+"] on "+this+" no callbacks");

			return true;
		}

		var target=evt.target;
		if (!target) {
			target=evt.srcElement;
		}
		f_core.Debug(f_popup, "_OnKeyPressJs["+evt.keyCode+"] on "+this+" component:"+component+" target:"+target);		

		try {
			if (callbacks.keyPress) {
				if (callbacks.keyPress.call(component, evt, f_popup.Popup)===false) {
					return f_core.CancelJsEvent(evt);
				}
			}
			
		} catch (x) {
			f_core.Error(f_popup, "_OnKeyPressJs: KeyPress callback throws exception", x);
		}
		
		return true;
	},
	/**
	 * @method hidden static
	 * @return Boolean
	 */
	VerifyLock: function() {
		if (f_popup.Ie_enablePopup()) {
			// IE ferme tout seul la popup lors d'un click hors de la popup
			// On verifie que la popup est encore ouverte ....
			
			var popup=f_popup.Popup;

			if (popup && popup.tagName=="IFRAME") {
				return true; // Pas de probleme de popup si c'est une IFrame
			}
			
			popup=document._rcfacesIEPopup;

			f_core.Debug(f_popup, "VerifyLock: test popup ! popup="+popup+" open="+popup.isOpen);

			if (popup && !popup.isOpen) {
				// Ca va pas !
				// Le popup est fermé et personne n'est prévenu !
				
				f_core.Debug(f_popup, "VerifyLock: close not opened popup ! (tagName="+popup.tagName+")");
				
				var cbs=f_popup.Callbacks;
				if (cbs) {
					f_popup.Callbacks=undefined;

					cbs.exit.call(f_popup.Component, null);
				}
				
				return false; // FALSE= La popup a �t� ferm�e, on revient en scope normal
			}	
		}
		
		// Le navigateur ne ferme pas la popup tout seul !
		return true; // TRUE= RAS
	},
	/**
	 * Verify que l'evenement a eu lieu dans une popup !
	 * @method hidden static 
	 */	
	VerifyMouseDown: function(component, jsEvent) {
		var target = jsEvent.target || jsEvent.srcElement;
			
		// On a un probleme ! 
		// Les evenements clicks sont traités par notre composant !
		
		// Il y a des sous-composants dans le menu 
		// l'evenement peut provenir de l'un d'eux ... 
		// dans le doute on recherche dans les parents
		for(;target;) {
			if (target._popupObject) {
				// On laisse tomber ...
				
				return true;
			}
			target=target.parentNode;
		}
		
		return false;
		
	},
	/**
	 * @method hidden static
	 * @return void
	 */
	Ie_openPopup: function(popup, positionInfos) {
		f_core.Assert(typeof(popup)=="object", "f_popup.Ie_openPopup: Invalid popup parameter '"+popup+"'.");
		f_core.Assert(typeof(positionInfos)=="object", "f_popup.Ie_openPopup: Invalid positionInfos parameter '"+positionInfos+"'.");
		f_core.Assert(typeof(positionInfos.position)=="number", "f_popup.Ie_openPopup: Invalid positionInfos.position parameter '"+positionInfos.position+"'.");

		f_core.Debug(f_popup, "Ie_openPopup: open popup '"+popup+"' positionInfos="+positionInfos.position);
			
		if (popup.tagName=="IFRAME") {
			this.Gecko_openPopup(popup, positionInfos);
			return;
		}
		
		// Positionnement d'un composant Popup
		
		var popupDocument=popup.document;
	
		popupDocument.hideFocus=true;

		var pbody=popupDocument.body;
		pbody.onunload=f_popup._Ie_unload;

		var firstChild=pbody.firstChild;
				
		popup.show(0, 0, 0, 0);
		
		var popupX = 0;
		var popupY = 0;
		var popupW = firstChild.offsetWidth;
		var popupH = firstChild.offsetHeight;
		var popupComponent=positionInfos.component;
	
		switch(positionInfos.position) {
		case f_popup.RIGHT_COMPONENT:
			popupX=popupComponent.offsetWidth;
			break;

		case f_popup.BOTTOM_COMPONENT:
			popupY=popupComponent.offsetHeight;
			break;

		case f_popup.BOTTOM_LEFT_COMPONENT:
			popupY=popupComponent.offsetHeight;
			break;

		case f_popup.BOTTOM_RIGHT_COMPONENT:
			popupX=popupComponent.offsetWidth;
			popupY=popupComponent.offsetHeight;
			break;

		case f_popup.MIDDLE_COMPONENT:
			popupX=popupComponent.offsetWidth/2;
			popupY=popupComponent.offsetHeight/2;
			break;
			
		case f_popup.MOUSE_POSITION:
			var jsEvent=positionInfos.jsEvent;
			
			// Calcule la position de la souris 
			var eventPos=f_core.GetJsEventPosition(jsEvent);
			
			popupComponent=(jsEvent.srcElement || jsEvent.targetElement || jsEvent.toElement).ownerDocument.body;
			popupX=eventPos.x; //-cursorPos.x;
			popupY=eventPos.y; //-cursorPos.y;

			break;
		}
		
//		f_core.Debug(f_popup, "Ie_openPopup: Popup position x="+popupX+" y="+popupY+" w="+popupW+" h="+popupH+" component="+popupComponent);
		
		if (positionInfos.deltaX) {
			popupX+=positionInfos.deltaX;
		}
		
		if (positionInfos.deltaY) {
			popupY+=positionInfos.deltaY;
		}
		
		if (positionInfos.deltaWidth) {
			popupW+=positionInfos.deltaWidth;
		}
		
		if (positionInfos.deltaHeight) {
			popupH+=positionInfos.deltaHeight;
		}

		var viewSize=f_core.GetViewSize(null, popupComponent.ownerDocument);
		
		var bw=viewSize.width;
		var bh=viewSize.height;
		
		var scrollPosition=f_core.GetScrollOffsets(popupComponent.ownerDocument);
		bw+=scrollPosition.x;
		bh+=scrollPosition.y;

		
		if (popupW+popupX>bw) {
			popupX +=  (bw-popupW-popupX);
		}
		
		if (popupH+popupY>bh) {
			popupY += (bh-popupH-popupY);
		}
		
		f_core.Debug(f_popup, "Ie_openPopup: Open popup x="+popupX+" y="+popupY+" w="+popupW+" h="+popupH+" componentPosition="+popupComponent.id+"/"+popupComponent.tagName);
		
		popup.show(popupX, popupY, popupW, popupH, popupComponent);		
		
		//window.title="Title= "+popupW+"/"+popupH+"/"+popupW+"/"+popupH;

		var seps=popupDocument.getElementsByTagName("li");
		// Il faut motiver les composants ?????
		// Merci IE .... au moins il y a une solution !
		for(var i=0;i<seps.length;i++) {
			seps[i].style.visibility="inherit";
		}
	},
	/**
	 * @method private static
	 * @context event:evt
	 */
	_Ie_unload: function(evt) {
		var doc=this.document;
		var body=doc.body;

		body.onunload=null;	
				
		f_core.Debug(f_popup, "_Ie_unload: Unload popup '"+this.id+"' rootPopup="+doc._rootPopup);		
		
		var component = f_popup.Component;
		if (doc._rootPopup) {
			var cbs=f_popup.Callbacks;
			if (cbs && component) {
				cbs.exit.call(component, evt);
				f_popup.Callbacks=undefined;
			}
		}
	},
	/**
	 * @method hidden static
	 * @param HTMLElement popup Popup object
	 * @return void
	 */
	Ie_closePopup: function(popup) {
		if (popup.tagName=="IFRAME") {
			popup.style.visibility="hidden";
			return;
		}
		
		if (!popup.isOpen) {
			return;
		}

		f_core.Debug(f_popup, "Ie_closePopup: Close popup '"+popup.id+"'.");		

		popup.hide();
	},
	/**
	 * @method hidden static
	 * @param HTMLElement popup Popup object
	 * @return void
	 */
	Ie_releasePopup: function(popup) {
		if (popup.tagName=="IFRAME") {
			// On le traite en asynchrone pour des problemes d'evenement en cours de traitement
			window.setTimeout(function() {
				popup.parentNode.removeChild(popup);
			}, 10);
			return;
		}
		
		try {
			var body=popup.document.body;
			for(;body.firstChild;) {
				body.removeChild(body.firstChild);
			}
		} catch (x) {
			f_core.Debug(f_popup, "Ie_releasePopup: Can not remode body", x);
		}
	},
	/**
	 * @method hidden static 
	 * @param HTMLElement popup
	 * @return void
	 */
	Gecko_closePopup: function(popup) {
		popup.style.visibility="hidden";
		popup.style.display="none";
	},
	/**
	 * @method hidden static 
	 * @param HTMLElement popup
	 * @return void
	 */
	Gecko_releasePopup: function(popup) {
		popup.parentNode.removeChild(popup);
	},
	/**
	 * @method hidden static 
	 * @param Object popup
	 * @param Object positionInfos
	 * @return void
	 */
	Gecko_openPopup: function(popup, positionInfos) {
		f_core.Assert(typeof(popup)=="object", "f_popup.Gecko_openPopup: Invalid popup parameter '"+popup+"'.");
		f_core.Assert(typeof(positionInfos)=="object", "f_popup.Gecko_openPopup: Invalid positionInfos parameter '"+positionInfos+"'.");
		f_core.Assert(typeof(positionInfos.position)=="number", "f_popup.Gecko_openPopup: Invalid positionInfos.position parameter '"+positionInfos.position+"'.");
		
		var component=positionInfos.component;	
		var offsetX=0;
		var offsetY=0;
		var offsetWidth=popup.offsetWidth;
		var offsetHeight=popup.offsetHeight;
		var heightSetted=false;
		
		if (positionInfos.popupWidth) {
			offsetWidth=positionInfos.popupWidth;
		}
		if (positionInfos.maxPopupHeight && popup.offsetHeight>positionInfos.maxPopupHeight) {
			heightSetted=true;
			offsetHeight=positionInfos.maxPopupHeight;
		}
		
		popup.style.display="block"; // On remet le display block pour pouvoir avoir la taille !

		if (component) {
			var absPos=f_core.GetAbsolutePosition(component);
			offsetX=absPos.x;
			offsetY=absPos.y;
		}
		
		switch(positionInfos.position) {
		
		case f_popup.BOTTOM_COMPONENT:
		case f_popup.BOTTOM_LEFT_COMPONENT:
			offsetY+=component.offsetHeight;
			break;
					
		case f_popup.BOTTOM_RIGHT_COMPONENT:
			offsetY+=component.offsetHeight;
		case f_popup.RIGHT_COMPONENT:
			offsetX+=component.offsetWidth;
			break;

		case f_popup.MIDDLE_COMPONENT:
			offsetX+=component.offsetWidth/2;
			offsetY+=component.offsetHeight/2;
			break;
			
		case f_popup.MOUSE_POSITION:
			var jsEvent=positionInfos.jsEvent;
			
			// Calcule la position de la souris 
			var eventPos=f_core.GetJsEventPosition(jsEvent);
			var cursorPos=f_core.GetAbsolutePosition(popup);
			
			offsetX=eventPos.x-cursorPos.x;
			offsetY=eventPos.y-cursorPos.y;

			f_core.Debug(f_popup, "Gecko_openPopup: (mouse position) X="+offsetX+" Y="+offsetY+" eventX="+eventPos.x+" eventY="+eventPos.y+" cursorPosX="+cursorPos.x+" cursorPosY="+cursorPos.y);
			
			break;
		}
		
		if (component) {
			f_core.Debug(f_popup, "Gecko_openPopup: X="+offsetX+" Y="+offsetY+" cx="+component.offsetLeft+" cy="+component.offsetTop+" cw="+component.offsetWidth+" ch="+component.offsetHeight);
		}
		
		if (positionInfos.deltaX) {
			offsetX+=positionInfos.deltaX;
		}
		
		if (positionInfos.deltaY) {
			offsetY+=positionInfos.deltaY;
		}
		
		if (positionInfos.deltaWidth) {
			offsetWidth+=positionInfos.deltaWidth;
		}
		
		if (positionInfos.deltaHeight) {
			offsetHeight+=positionInfos.deltaHeight;
		}
				
		offsetX+=2; // Border par défaut !
		
		var positions={ x: offsetX, y: offsetY };
		
		var viewSize=f_core.GetViewSize(null, popup.ownerDocument);
		
		var bw=viewSize.width;
		var bh=viewSize.height;
		var scrollPosition=f_core.GetScrollOffsets(popup.ownerDocument);
		bw+=scrollPosition.x;
		bh+=scrollPosition.y;

		var absPos=f_core.GetAbsolutePosition(popup.offsetParent);

		f_core.Debug(f_core, "Gecko_openPopup: bw="+bw+" bh="+bh+" absPos.x="+absPos.x+" absPos.y="+absPos.y+" positions.x="+positions.x+" positions.y="+positions.y+" popupWidth="+offsetWidth+" popupHeight="+popup.offsetHeight);

		if (offsetWidth+positions.x+absPos.x>bw) {
			positions.x=bw-offsetWidth-absPos.x;

			f_core.Debug(f_core, "Gecko_openPopup: change x position to "+positions.x);
		}
		
		if (offsetHeight > bh - scrollPosition.y) {
			positions.y=0;
			positions.x=0;
			
		} else if (offsetHeight+positions.y+absPos.y>bh) {
			if (component) {
				var aeAbs = f_core.GetAbsolutePosition(component);
				positions.y=aeAbs.y-offsetHeight;
			} else {
				positions.y=bh-offsetHeight-absPos.y;
			}
			f_core.Debug(f_core, "Gecko_openPopup: change y position to "+positions.y);
		} 

		var popupStyle=popup.style;

		if (popup._item) {
			var menu=popup._item._menu;
			var parentPopup=menu.f_getUIPopup(popup._item);		
			
			if (!popupStyle.zIndex && parentPopup) {
				var zbuf=parentPopup.style.zIndex;
				var ppop=0;
				
				if (!zbuf) {
					ppop=1000;
					
				} else {
					ppop=parseInt(zbuf, 10);
				}
				
				popupStyle.zIndex=String(ppop+1);
			}
		}

		popupStyle.left=positions.x+"px";
		popupStyle.top=positions.y+"px";
		
		if (positionInfos.popupWidth) {
			popupStyle.width=offsetWidth+"px";
		}
		if (heightSetted) {
			popupStyle.height=offsetHeight+"px";
		}

		popupStyle.visibility="inherit";
	},
	/**
	 * @return HTMLElement
	 */
	GetComponent: function() {
		return f_popup.Component;
	}
};

var __prototype = {
	
	/**
	 * @field private Number
	 */
	_mode: undefined,
	
	f_popup: function(mode) {
		this._mode=mode;
	}
};

new f_class("f_popup", {
	statics: __statics
});
