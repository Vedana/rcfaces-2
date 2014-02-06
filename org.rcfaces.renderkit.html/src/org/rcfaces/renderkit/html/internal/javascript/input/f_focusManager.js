/*
 * $Id: f_focusManager.js,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * Focus manager class.
 * 
 * @class public f_focusManager extends f_object, fa_serializable
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */

var __statics = {

	/**
	 * @field private static
	 */
	_Instance : undefined,

	/**
	 * @method public static
	 * @param hidden
	 *            boolean create
	 * @return f_focusManager
	 */
	Get : function(create) {
		var instance = f_focusManager._Instance;
		if (!instance && create !== false) {
			instance = f_focusManager.f_newInstance();
		}

		return instance;
	},
	/**
	 * @method public static
	 * @param f_focusManager
	 *            focusManager
	 * @return void
	 */
	Set : function(focusManager) {
		f_focusManager._Instance = focusManager;
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer : function() {
		f_focusManager._Instance = undefined; // f_focusManager
	},
	/**
	 * @method private hidden
	 * @param Event
	 *            event
	 * @return Element
	 */
	ComputeFocusComponent : function(event) {
		var focusComponent = undefined;

		var focusManager = f_focusManager.Get();
		if (focusManager) {
			focusComponent = focusManager.f_getFocusComponent();
		}

		if (!focusComponent) {
			var component = event.f_getComponent();
			if (component && component.nodeType == f_core.ELEMENT_NODE) {

				focusComponent = f_core.GetParentForm(component);
			}
		}

		if (!focusComponent) {
			focusComponent = document.forms[0];
		}

		return focusComponent;
	},
	/**
	 * @method private hidden
	 * @param Element
	 *            focusComponent
	 * @param Function
	 *            testFunc
	 * @param optional
	 *            Element fromComponent
	 * @return Element
	 */
	WalkDOM : function(focusComponent, testFunc, fromComponent) {
		var stack = new Array();

		if (fromComponent) {
			stack.push(fromComponent);

		} else {
			var component = focusComponent;
			for (;;) {
				var fc = component;
				for (;;) {
					if (!fc.nextSibling) {
						break;
					}
					fc = fc.nextSibling;

					if (fc.nodeType != f_core.ELEMENT_NODE) {
						continue;
					}

					stack.unshift(fc);
				}
				component = component.parentNode;
				if (!component || component.nodeType != f_core.ELEMENT_NODE
						|| component.tagName.toLowerCase() == "form") {
					break;
				}
			}
		}

		for (; stack.length;) {
			var c = stack.pop();

			if (testFunc(c)) {
				return c;
			}

			var children = c.childNodes;
			if (!children.length) {
				continue;
			}

			for ( var i = 0; i < children.length; i++) {
				var c = children[children.length - i - 1]; // On inverse
				// l'ordre
				if (c.nodeType != f_core.ELEMENT_NODE) {
					continue;
				}

				stack.push(c);
			}
		}

		if (!fromComponent) {
			var parentForm = f_core.GetParentForm(focusComponent);

			return f_focusManager.WalkDOM(focusComponent, testFunc, parentForm);
		}

		return null;
	},
	/**
	 * @method public static
	 * @param f_event
	 *            event
	 * @param hidden
	 *            HTMLElement fromComponent
	 * @return Boolean
	 */
	FocusNextRequired : function(event) {

		// Force les REQUIS à s'initialiaser
		f_classLoader.Get(window).f_verifyOnSubmit();

		var focusComponent = f_focusManager.ComputeFocusComponent(event);

		f_core.Debug(f_focusManager, "FocusNextRequired: focusComponent='"
				+ focusComponent + "'");

		if (!focusComponent) {
			return false;
		}

		var found = f_focusManager.WalkDOM(focusComponent, function(c) {
			return c.f_isRequired && c.f_isRequired();
		});

		f_core.Debug(f_focusManager, "FocusNextRequired: found=" + found
				+ " fromComponent='" + fromComponent + "'");

		if (found) {
			f_core.SetFocus(found, true);
		}

		return false;
	},
	/**
	 * Search next focusable component, ignore tabIndex level, no cycle from start of document.
	 * 
	 * @method hidden static
	 * @param Element from
	 * @return Element
	 */
	SearchNextFocusable: function(from) {
		var focusableTags=new RegExp(f_core._FOCUSABLE_TAGS, "i");

		var c=from;
		
		// Pas de cycle !
		for(;;) {
			if (c.nodeType==f_core.ELEMENT_NODE) {
				if (typeof(c.tabIndex)=="number" && c.tabIndex>=0) {
					return c;
				}
				
				if (focusableTags.test(c.tagName) && (!c.tabIndex || c.tabIndex>=0)) {
					return c;
				}
			}
			
			if (c.firstChild) {
				c=c.firstChild;
				continue;
			}

			if (c.nextSibling) {
				c=c.nextSibling;
				continue;
			}
			
			for(;;) {
				var p=c.parentNode;
				if (!p || p.nodeType!=f_core.ELEMENT_NODE) {
					return null;
				}
				
				if (p.nextSibling) {
					c=p.nextSibling;
					break;
				}
				c=p;
			}
		}
		
		return null;
	}
};

var __members = {

	f_focusManager : function() {
		this.f_super(arguments);

		var instance = f_focusManager._Instance;

		if (this.nodeType == f_core.ELEMENT_NODE) {
			var setFocusIfMessage = f_core.GetBooleanAttributeNS(this,
					"setFocusIfMessage", true);
			var focusId = f_core.GetAttributeNS(this, "focusId");
			var autoFocus = f_core.GetBooleanAttributeNS(this, "autoFocus");
			var autoFocusFrom = f_core.GetAttributeNS(this, "autoFocusFrom");

			if (instance) {
				if (setFocusIfMessage === false) {
					instance.f_setFocusIfMessage(false);
				}
				if (focusId) {
					instance.f_setFocus(focusId);
				}

				return;
			}

			this.f_initialize(null, focusId, setFocusIfMessage, autoFocus, autoFocusFrom);

		} else {
			// Mode JavaScript ou collecteur

			if (instance) {
				throw new Error("FocusManager is already installed !");
			}
		}

		f_focusManager.Set(this);

		if (f_core.IsGecko()) {
			var self = this;

			this._firefoxFocusListener = function(event) {
				var element = event.target;
				self._activeElement = element;

				return true;
			};

			window.addEventListener("focus", this._firefoxFocusListener, true);
		}
	},
	f_finalize : function() {
		var messageContext = this._messageContext;
		if (messageContext) {
			this._messageContext = undefined;

			messageContext.f_removeMessageListener(this);
		}

		var firefoxFocusListener = this._firefoxFocusListener;
		if (firefoxFocusListener) {
			this._firefoxFocusListener = undefined;

			window.removeEventListener("focus", firefoxFocusListener, true);
		}

		// this._setFocusIfMessage=undefined; // boolean
		// this._initFocusId=undefined; // String
		// this._focusId=undefined; // String
		// this._documentCompleted=undefined; // boolean
		this._activeElement = undefined; // HtmlElement

		this.f_super(arguments);
	},
	/**
	 * @method hidden
	 * @param String
	 *            id
	 * @param optional
	 *            String focusId
	 * @param optional
	 *            Boolean setFocusIfMessage
	 * @param optional
	 *            Boolean autoFocus
	 * @param optional String autoFocusFrom       
	 * @return void
	 */
	f_initialize : function(id, focusId, setFocusIfMessage, autoFocus, autoFocusFrom) {
		f_core.Assert(id === null || typeof (id) == "string",
				"f_focusManager.f_setId: Invalid id parameter '" + id + "'.");

		if (id) {
			this.id = id;
		}

		if (focusId) {
			this.f_setFocus(focusId, true);
		}

		if (setFocusIfMessage !== false) {
			this._setFocusIfMessage = true;

			var messageContext = f_messageContext.Get();
			if (messageContext) {
				messageContext.f_addMessageListener(this);
				this._messageContext = messageContext;
			}
		}

		if (autoFocus) {
			this._autoFocus = true;
			this._autoFocusFrom= autoFocusFrom;
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_documentComplete : function() {
		this._documentCompleted = true;

		var focusId = this._initFocusId;
		this._initFocusId = undefined;

		f_core.Debug(f_focusManager, "f_documentComplete: focus='" + focusId
				+ "'.");

		if (!focusId && this._autoFocus) {
			var from=null;
			if (this._autoFocusFrom) {
				from=document.getElementById(this._autoFocusFrom);

				if (!from) {
					f_core.Info(f_focusManager, "f_documentComplete: focus from='" + this._autoFocusFrom
							+ "' can not be found !");
				}
			}
			if (!from) {
				from=document.body;
			}
			
			focusId=f_focusManager.SearchNextFocusable(from);

			f_core.Debug(f_focusManager, "f_documentComplete: Search next focusable from='" + from
					+ "' => "+focusId);
		}

		if (false) {
			if (window.console && window.console.log) {
				console.log("Positionne le focus sur '"+focusId+"'");
			}
		}
		
		if (!focusId) {
			return;
		}

		var activeElement = this._getActiveElement();
		if (activeElement) {
			var tagName = activeElement.tagName;
			if (tagName && tagName.toUpperCase() != "BODY") {
				// Le focus est déjà déplacé !
				return;
			}
			if (activeElement.id == focusId) {
				// Déjà positionné !
				return;
			}
		}

		this.f_setFocus(focusId, true);
	},
	/**
	 * @method private
	 * @return HTMLElement
	 */
	_getActiveElement : function() {
		var activeElement;

		if (f_core.IsInternetExplorer()) {
			activeElement = document.activeElement;

		} else {
			/*
			 * var selection=window.getSelection(); if (selection) {
			 * activeElement=selection.focusNode; }
			 */
			activeElement = this._activeElement;
		}

		f_core.Debug(f_focusManager,
				"_getActiveElement: current active element=" + activeElement
						+ " id="
						+ (activeElement ? activeElement.id : "**null**"));

		return activeElement;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getFocusId : function() {
		if (!this._documentCompleted) {
			return this._initFocusId;
		}

		var activeElement = this._getActiveElement();

		if (activeElement) {
			var id = activeElement.id;
			if (!id) {
				return id;
			}

			var idx = id.lastIndexOf("::");
			if (idx > 0) {
				id = id.substring(0, idx);
				var component = f_core.GetElementByClientId(id);

				while (!component || idx > 0) {
					idx = id.lastIndexOf(".");
					if (idx < 0) {
						break;
					}
					id = id.substring(0, idx);
					component = f_core.GetElementByClientId(id);
				}

				if (component
						&& typeof (component.f_getFocusHandler) == "function") {
					var focusHandler = component.f_getFocusHandler();
					if (focusHandler) {
						id = focusHandler.id;
					}
				}
			}

			return id;
		}

		return this._initFocusId;
	},
	/**
	 * @method public
	 * @return HTMLElement
	 */
	f_getFocusComponent : function() {
		var focusId = this.f_getFocusId();

		f_core.Debug(f_focusManager, "f_getFocusComponent: focusId='" + focusId
				+ "'");

		if (focusId) {
			return f_core.GetElementByClientId(focusId);
		}

		return null;
	},

	/**
	 * @method public
	 * @param String
	 *            focus Focus clientId, or a component.
	 * @return optional Boolean async Set focus in async mode.
	 * @return Boolean
	 */
	f_setFocus : function(focus, async) {
		f_core.Assert(typeof (focus) == "string"
				|| (focus && focus.nodeType == f_core.ELEMENT_NODE),
				"f_focusManager.f_setFocus: Focus component parameter is not invalid ("
						+ focus + ").");

		var component = focus;

		if (typeof (focus) == "string") {
			if (!this._documentCompleted) {
				this._initFocusId = focus;

				f_core.Debug(f_focusManager,
						"f_setFocus: documentComplete!=true, wait for focus (component="
								+ focus + ".)");
				return undefined;
			}

			f_core.Debug(f_focusManager, "f_setFocus: search component id='"
					+ focus + "' async='" + async + "'.");

			try {
				component = f_core.GetElementByClientId(focus);

			} catch (x) {
				// Si le composant n'est pas Camelia, ca pete !

				component = document.getElementById(focus);
			}
		} else if (!this._documentCompleted) {
			// C'est déjà positionné !
			this._initFocusId = undefined;
		}

		if (!component) {
			f_core.Info(f_focusManager, "f_setFocus: Can not find component '"
					+ focus + "' to set focus !");
			return false;
		}

		if (component == this._getActiveElement()) {
			f_core.Debug(f_focusManager,
					"f_setFocus: Focus is already setted to the component "
							+ component.id);
			return true;
		}

		f_core.Debug(f_focusManager, "f_setFocus: Set focus to component '"
				+ component.id + "'.");

		if (!f_core.SetFocus(component, async)) {
			return false;
		}

		return true;
	},
	f_serialize : function() {
		if (this.id) {
			var focusId = this.f_getFocusId();

			this.f_setProperty(f_prop.FOCUS_ID, focusId);
		}
	},
	f_performMessageChanges : function(messageContext, messageEvent) {
		f_core.Debug(f_focusManager,
				"f_performMessageChanges: MessageEvent.type="
						+ messageEvent.type);

		switch (messageEvent.type) {
		case f_messageContext.POST_CHECK_EVENT_TYPE:
		case f_messageContext.POST_PENDINGS_EVENT_TYPE:
			break;

		default:
			return;
		}

		var selectedComponentClientId = undefined;
		var selectedSeverity = -1;

		var clientIds = messageContext.f_listComponentIdsWithMessages(true);
		for ( var i = 0; i < clientIds.length; i++) {
			var clientId = clientIds[i];

			if (!clientId) { // On traite pas les globaux
				continue;
			}

			var messages = messageContext.f_listMessages(clientId);
			if (!messages || !messages.length) {
				continue;
			}

			for ( var j = 0; j < messages.length; j++) {
				var message = messages[j];
				var severity = message.f_getSeverity();

				if (selectedComponentClientId && severity <= selectedSeverity) {
					continue;
				}

				selectedComponentClientId = clientId;
				selectedSeverity = severity;
			}
		}

		f_core.Debug(f_focusManager,
				"f_performMessageChanges: focus component="
						+ selectedComponentClientId + " severity="
						+ selectedSeverity);

		if (selectedComponentClientId) {
			this.f_setFocus(selectedComponentClientId);
		}
	}
};

new f_class("f_focusManager", {
	extend : f_object,
	aspects : [ fa_serializable ],
	statics : __statics,
	members : __members
});
