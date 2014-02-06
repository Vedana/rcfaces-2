/*
 * $Id: f_event.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * f_event class
 * 
 * @class public final f_event extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) & Joel
 *         Merlin
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */

var __members = {

	/**
	 * @field private String
	 */
	_type : undefined,

	/**
	 * @field private Element
	 */
	_component : undefined,

	/**
	 * @field private Event
	 */
	_jsEvent : undefined,

	/**
	 * @field private Object
	 */
	_item : undefined,

	/**
	 * @field private any
	 */
	_value : undefined,

	/**
	 * @field private f_selectionProvider
	 */
	_selectionProvider : undefined,

	/**
	 * @field private String
	 */
	_serializedValue : undefined,

	/**
	 * @field private Number
	 */
	_detail : undefined,

	/**
	 * @field private Object
	 */
	_detailObject : undefined,

	/**
	 * @method public
	 * @param f_object
	 *            component
	 * @param String
	 *            type
	 * @param optional
	 *            Event jsEvent
	 * @param optional
	 *            Object item
	 * @param optional
	 *            any value
	 * @param optional
	 *            fa_selectionProvider selectionProvider
	 * @param optional
	 *            any detail
	 * @param optional
	 *            String serializedValue
	 */
	f_event : function(component, type, jsEvent, item, value,
			selectionProvider, detail, serializedValue) {
		f_core.Assert(typeof (type) == "string",
				"f_event.f_event: Bad type of event '" + type + "'");
		f_core
				.Assert(
						component
								&& (component.nodeType == f_core.ELEMENT_NODE || component._kclass),
						"f_event.f_event: Bad component '" + component + "'.");
		f_core.Assert(serializedValue === undefined
				|| typeof (serializedValue) == "string",
				"f_event.f_event: Bad serializedValue type '" + serializedValue
						+ "'.");
		f_core.Assert(typeof (detail) == "object"
				|| typeof (detail) == "number" || detail === undefined,
				"f_event.f_event: Invalid detail parameter (" + detail + ")");

		this._type = type;
		this._component = component;
		this._jsEvent = jsEvent;
		this._item = item;
		this._value = value;
		this._selectionProvider = selectionProvider;
		this._serializedValue = serializedValue;

		if (detail && typeof (detail) == "object") {
			f_core.Assert(typeof (detail.value) == "number",
					"f_event.f_event: Invalid value field of detail object ("
							+ detail.value + ")");

			this._detailObject = detail;
			this._detail = detail.value;

		} else {
			this._detail = detail;
		}
	},

	/**
	 * @method hidden
	 * @return void
	 */
	f_finalize : function() {
		// this._type = undefined; // String
		this._component = undefined; // component
		this._jsEvent = undefined; // JsEvent
		this._item = undefined; // any
		this._value = undefined; // any
		this._detail = undefined; // any
		this._detailObject = undefined; // any
		// this._immediate = undefined; // Boolean
		// this._serializedValue=undefined; // String
		this._selectionProvider = undefined; // fa_selectionProvider
	},

	/**
	 * Returns the type of event.
	 * 
	 * @method public
	 * @return String The type of event.
	 */
	f_getType : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getType: Invalid number of parameter");

		return this._type;
	},

	/**
	 * Returns the Javascript event if any.
	 * 
	 * @method public
	 * @return Event The Javascript event.
	 */
	f_getJsEvent : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getJsEvent: Invalid number of parameter");

		return this._jsEvent;
	},

	/**
	 * Returns the item associated to the event.
	 * 
	 * @method public
	 * @return Object An item.
	 */
	f_getItem : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getItem: Invalid number of parameter");

		return this._item;
	},

	/**
	 * Returns the component associated to the event.
	 * 
	 * @method public
	 * @return f_object The component associated to the event.
	 */
	f_getComponent : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getComponent: Invalid number of parameter");

		return this._component;
	},

	/**
	 * Returns the value of the item associated to the event.
	 * 
	 * @method public
	 * @return any The value of the item associated to the event.
	 */
	f_getValue : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getValue: Invalid number of parameter");

		return this._value;
	},

	/**
	 * Returns the value of the item associated to the event.
	 * 
	 * @method hidden
	 * @return any The value of the item associated to the event.
	 */
	f_getSerializedValue : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getSerializedValue: Invalid number of parameter");

		var s = this._serializedValue;
		if (s !== undefined) {
			return s;
		}

		return this._value;
	},

	/**
	 * Returns the selectionProvider wich contains the item associated to the
	 * event.
	 * 
	 * @method public
	 * @return fa_selectionProvider Returns the selectionProvider associated to
	 *         the item.
	 */
	f_getSelectionProvider : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getSelectionProvider: Invalid number of parameter");

		return this._selectionProvider;
	},

	/**
	 * Returns a detail about the event.
	 * 
	 * @method public
	 * @return any A detail value.
	 */
	f_getDetail : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getDetail: Invalid number of parameter");

		return this._detail;
	},

	/**
	 * Returns a detail about the event.
	 * 
	 * @method public
	 * @return Object A detail value.
	 */
	f_getDetailObject : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.f_getDetail: Invalid number of parameter");

		return this._detailObject;
	},

	/**
	 * <p>
	 * Search for and return the {@link f_component} with an <code>id</code>
	 * that matches the specified search expression (if any).
	 * 
	 * @method public
	 * @param String...
	 *            id Identifier of component.
	 * @return HTMLElement the found {@link f_component}, or <code>null</code>
	 *         if the component was not found.
	 * @see f_component#f_findComponent f_component.f_findComponent()
	 */
	f_findComponent : function(id) {
		f_core.Assert(arguments.length,
				"f_event.f_findComponent: Invalid number of parameter");

		var component = this._component;

		f_core.Assert(component.f_findComponent,
				"f_event.f_findComponent: Component '" + component
						+ "' has no f_findComponent method.");

		return component.f_findComponent.apply(component, arguments);
	},
	/**
	 * <p>
	 * Search for and return the sibling {@link f_component} with an
	 * <code>id</code> that matches the specified search expression (if any).
	 * 
	 * @method public
	 * @param String...
	 *            id Identifier of component.
	 * @return HTMLElement the found {@link f_component}, or <code>null</code>
	 *         if the component was not found.
	 * @see f_component#f_findComponent f_component.f_findComponent()
	 */
	f_findSiblingComponent : function(id) {
		f_core.Assert(arguments.length,
				"f_event.f_findSiblingComponent: Invalid number of parameter");

		var component = this._component;

		f_core.Assert(component.f_findSiblingComponent,
				"f_event.f_findSiblingComponent: Component '" + component
						+ "' has no f_findSiblingComponent method.");

		return component.f_findSiblingComponent.apply(component, arguments);
	},
	/**
	 * <p>
	 * Search for and return the sibling {@link f_component} with an
	 * <code>id</code> that matches the specified search expression (if any).
	 * 
	 * @method public
	 * @param String...
	 *            id Identifier of component.
	 * @return HTMLElement the found {@link f_component}, or <code>null</code>
	 *         if the component was not found.
	 * @see #f_findSiblingComponent f_findSiblingComponent(x)
	 */
	$ : function(id) {
		return this.f_findSiblingComponent.apply(this, arguments);
	},
	/**
	 * <p>
	 * Search for and return the {@link f_component} with an <code>id</code>
	 * that matches the specified search expression (if any).
	 * 
	 * @method public
	 * @param String...
	 *            id Identifier of component.
	 * @return HTMLElement the found {@link f_component}, or <code>null</code>
	 *         if the component was not found.
	 * @see #f_findComponent f_findComponent()
	 */
	$$ : function(id) {
		return this.f_findComponent.apply(this, arguments);
	},

	/**
	 * Prevent the default process of the event.
	 * 
	 * @method public
	 * @return Boolean <code>false</code> value.
	 */
	f_preventDefault : function() {
		var evt = this._jsEvent;
		f_core.Assert(evt,
				"f_event.f_preventDefault: Javascript Event is null !");

		f_core.CancelJsEvent(evt);

		return false;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isImmediate : function() {
		return this._immediate;
	},
	/**
	 * @method public
	 * @param Boolean
	 *            immediate
	 * @return void
	 */
	f_setImmediate : function(immediate) {
		f_core.Assert(immediate === undefined
				|| typeof (immediate) == "boolean",
				"f_event.f_setImmediate: Invalid immediate parameter ("
						+ immediate + ").");

		this._immediate = immediate;
	},

	/**
	 * @method public
	 * @return String
	 */
	toString : function() {
		return "[f_event type='" + this._type + "' component='"
				+ this._component + "' value='" + this._value + "' item='"
				+ this._item + "' detail='" + this._detail + "' immediate='"
				+ this._immediate + "' jsEvent='" + this._jsEvent + "']";
	}
};

var __statics = {
	/**
	 * @field private static final String
	 */
	_LOCK_MESSAGE : "Window has been locked.",

	/**
	 * @field hidden static final Number
	 */
	SUBMIT_LOCK : 1,

	/**
	 * @field hidden static final Number
	 */
	MODAL_LOCK : 2,

	/**
	 * @field hidden static final Number
	 */
	POPUP_LOCK : 4,

	/**
	 * @field hidden static final Number
	 */
	DND_LOCK : 8,

	/**
	 * @field private static Number
	 */
	_EvtLock : 0,

	/**
	 * @field private static boolean
	 */
	_EvtLockMode : true,

	/**
	 * @field private static f_event
	 */
	_Event : undefined,

	// Class public constants

	/**
	 * Blur event name.
	 * 
	 * @field public static final String
	 */
	ADDITIONAL_INFORMATION : "additionalInformation",

	/**
	 * Blur event name.
	 * 
	 * @field public static final String
	 */
	BLUR : "blur",

	/**
	 * Change event name.
	 * 
	 * @field public static final String
	 */
	CHANGE : "change",

	/**
	 * Check event name.
	 * 
	 * @field public static final String
	 */
	CHECK : "check",

	/**
	 * Validation event name.
	 * 
	 * @field public static final String
	 */
	VALIDATION : "validation",

	/**
	 * Close event name.
	 * 
	 * @field public static final String
	 */
	CLOSE : "close",

	/**
	 * Click event name.
	 * 
	 * @field public static final String
	 */
	CLICK : "click",

	/**
	 * Double-click event name.
	 * 
	 * @field public static final String
	 */
	DBLCLICK : "dblclick",

	/**
	 * Drag event name.
	 * 
	 * @field public static final String
	 */
	DRAG : "drag",

	/**
	 * Drop event name.
	 * 
	 * @field public static final String
	 */
	DROP : "drop",

	/**
	 * Drop complete event name.
	 * 
	 * @field public static final String
	 */
	DROP_COMPLETE : "dropComplete",

	/**
	 * Error event name.
	 * 
	 * @field public static final String
	 */
	ERROR : "error",

	/**
	 * Expand event name.
	 * 
	 * @field public static final String
	 */
	EXPAND : "expand",

	/**
	 * Focus event name.
	 * 
	 * @field public static final String
	 */
	FOCUS : "focus",

	/**
	 * Initialize event name.
	 * 
	 * @field public static final String
	 */
	INIT : "init",

	/**
	 * Key down event name.
	 * 
	 * @field public static final String
	 */
	KEYDOWN : "keydown",

	/**
	 * Key press event name.
	 * 
	 * @field public static final String
	 */
	KEYPRESS : "keypress",

	/**
	 * Key up event name.
	 * 
	 * @field public static final String
	 */
	KEYUP : "keyup",

	/**
	 * Load event name.
	 * 
	 * @field public static final String
	 */
	LOAD : "load",

	/**
	 * Menu event name.
	 * 
	 * @field public static final String
	 */
	MENU : "menu",

	/**
	 * Mouse button down event name.
	 * 
	 * @field public static final String
	 */
	MOUSEDOWN : "mousedown",

	/**
	 * Mouse out event name.
	 * 
	 * @field public static final String
	 */
	MOUSEOUT : "mouseout",

	/**
	 * Mouse over event name.
	 * 
	 * @field public static final String
	 */
	MOUSEOVER : "mouseover",

	/**
	 * Mouse button up event name.
	 * 
	 * @field public static final String
	 */
	MOUSEUP : "mouseup",

	/**
	 * Pre Selection event name.
	 * 
	 * @field public static final String
	 */
	PRE_SELECTION : "preSelection",

	/**
	 * Property Change event name.
	 * 
	 * @field public static final String
	 */
	PROPERTY_CHANGE : "propertyChange",

	/**
	 * Reset event name.
	 * 
	 * @field public static final String
	 */
	RESET : "reset",

	/**
	 * Selection event name.
	 * 
	 * @field public static final String
	 */
	SELECTION : "selection",

	/**
	 * Sort event name.
	 * 
	 * @field public static final String
	 */
	SORT : "sort",

	/**
	 * Submit event name.
	 * 
	 * @field public static final String
	 */
	SUBMIT : "submit",

	/**
	 * Suggestion event name.
	 * 
	 * @field public static final String
	 */
	SUGGESTION : "suggestion",

	/**
	 * User event name.
	 * 
	 * @field public static final String
	 */
	USER : "user",

	/**
	 * @field public static final Number
	 */
	ACTIVATE_DETAIL : 0x100,

	/**
	 * @field public static final Number
	 */
	RESET_DETAIL : 0x200,

	/**
	 * @field public static final Number
	 */
	IMMEDIATE_DETAIL : 0x400,

	/**
	 * @field public static final Number
	 */
	REFRESH_DETAIL : 0x800,

	/**
	 * @method public static
	 * @return f_object
	 */
	GetComponent : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetComponent: Invalid number of parameter");

		var event = f_event.GetEvent();
		if (!event) {
			return null;
		}
		return event.f_getComponent();
	},
	/**
	 * @method public static
	 * @return f_object
	 */
	GetType : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetType: Invalid number of parameter");

		var event = f_event.GetEvent();
		if (!event) {
			return null;
		}
		return event.f_getType();
	},
	/**
	 * @method public static
	 * @return Object
	 */
	GetItem : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetItem: Invalid number of parameter");

		return f_event.GetEvent().f_getItem();
	},
	/**
	 * @method public static
	 * @return any
	 */
	GetValue : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetValue: Invalid number of parameter");

		return f_event.GetEvent().f_getValue();
	},
	/**
	 * @method public static
	 * @return fa_selectionProvider
	 */
	GetSelectionProvider : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetSelectionProvider: Invalid number of parameter");

		return f_event.GetEvent().f_getSelectionProvider();
	},
	/**
	 * @method public static
	 * @return Event
	 */
	GetJsEvent : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetJsEvent: Invalid number of parameter");

		var event = f_event.GetEvent();
		if (!event) {
			return null;
		}
		return f_event.GetEvent().f_getJsEvent();
	},
	/**
	 * @method public static
	 * @return any
	 */
	GetDetail : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetDetail: Invalid number of parameter");

		return f_event.GetEvent().f_getDetail();
	},
	/**
	 * @method public static
	 * @return f_event
	 */
	GetEvent : function() {
		f_core.Assert(arguments.length == 0,
				"f_event.GetEvent: Invalid number of parameter");

		return f_event._Event;
	},
	/**
	 * @method hidden static
	 */
	SetEvent : function(event) {
		var old = f_event._Event;

		f_event._Event = event;

		return old;
	},
	/**
	 * @method hidden static
	 */
	GetEventLockedMode : function() {
		return f_event._EvtLockMode;
	},
	/**
	 * @method hidden static
	 */
	SetEventLockedMode : function(set) {
		f_core.Assert(set === undefined || typeof (set) == "boolean",
				"f_event.SetEventLockedMode: invalid set parameter (" + set
						+ ").");
		if (set === undefined) {
			set = true;

		} else {
			set = !!set;
		}

		f_event._EvtLockMode = set;
	},
	/**
	 * @method hidden static
	 * @param Event
	 *            jsEvent
	 * @param optional
	 *            boolean showAlert
	 * @param optional
	 *            number mask
	 * @return Boolean Returns <code>true</code> if lock is setted !
	 * @dontInline f_popup
	 */
	GetEventLocked : function(jsEvent, showAlert, mask) {

		if (window._rcfacesExiting) {
			return true;
		}

		// f_core.Assert(jsEvent===null || (jsEvent instanceof Event),
		// "f_event.GetEventLocked: Invalid jsEvent parameter ("+jsEvent+").");
		// Le type Event n'existe pas sous IE
		f_core.Assert(showAlert === undefined
				|| typeof (showAlert) == "boolean",
				"f_event.GetEventLocked: Invalid showAlert parameter ("
						+ showAlert + ").");
		f_core.Assert(mask === undefined || typeof (mask) == "number",
				"f_event.GetEventLocked: Invalid mask parameter (" + mask
						+ ").");

		var currentLock = f_event._EvtLock;

		if (mask) {
			currentLock &= mask;
		}

		if (!currentLock) {
			// Pas de lock !
			return false;
		}

		if (currentLock == f_event.MODAL_LOCK) {
			if (!f_core.VerifyModalWindow()) {
				return false; // finalement c'est pas verouillé !
			}

			// On passe le focus dessus normalement, donc pas de boite d'alerte
			// !
			return true;
		}

		if (f_event._EvtLock & f_event.POPUP_LOCK) {
			if (f_popup.VerifyLock() === false) {
				// Finalement nous ne sommes plus en LOCK ...
				// La popup a été fermée automatiquement par le navigateur ...
				currentLock = f_event._EvtLock;
				if (mask) {
					currentLock &= ~mask;
				}
				if (!currentLock) {
					return false;
				}

			} else if (jsEvent) {
				// On va rechercher si notre evenement est dans ou en dehors de
				// la popup

				var target = jsEvent.target;
				if (!target) {
					target = jsEvent.srcElement;
				}

				var ret = undefined;
				if (target) {
					// On recherche si le click se situe dans la popup

					ret = f_popup.IsChildOfDocument(target, jsEvent);

					// Ret=TRUE le click est dans la popup ou le composant qui a
					// ouvert la popup (cf f_isPopupLock() )
				}

				f_core.Debug(f_event,
						"GetEventLocked: Search popup child: target=" + target
								+ " return=" + ret);

				if (ret) {
					// C'est un click d'un composant dans la popup !
					return false;
				}

				// Click en dehors de la popup
				return false;

			} else {
				f_core.Debug(f_event,
						"GetEventLocked: Can not test popup case !");
			}
		}

		if (currentLock == f_event.DND_LOCK) {
			return false;
		}

		var currentMode = f_event._EvtLockMode;
		if (currentLock == f_event.SUBMIT_LOCK && currentMode === false) {
			// Nous sommes en LOCK, mais c'est pas bloqué
			return false;
		}

		// Nous sommes en LOCK et c'est bloqué !

		f_core.Debug(f_event,
				"GetEventLocked: Events are locked, break current process ! (mode="
						+ currentMode + " state=" + currentLock + " show="
						+ showAlert + ")");

		if (showAlert === false) {
			return true;
		}

		var s = f_env.Get("CORE_LOCK_MESSAGE");
		if (s === undefined) {
			var bundle = f_resourceBundle.Get(f_event);
			if (bundle) {
				s = bundle.f_get("LOCK_MESSAGE");
			}

			if (!s) {
				s = f_event._LOCK_MESSAGE;
			}
		}

		if (s === null) {
			return true;
		}

		/*
		 * if (window.console && console.trace) { }
		 */

		if (f_core.IsDebugEnabled(f_event)) {
			if (jsEvent) {
				s += "\nJSEVENT informations: type=" + jsEvent.type;

				if (jsEvent.target) {
					s += "\ntarget=[" + jsEvent.target.nodeType + "]"
							+ jsEvent.target.tagName + "#" + jsEvent.target.id
							+ "." + jsEvent.target.className;
				}
				if (jsEvent.currentTarget) {
					s += "\ncurrentTarget=[" + jsEvent.currentTarget.nodeType
							+ "]" + jsEvent.currentTarget.tagName + "#"
							+ jsEvent.currentTarget.id + "."
							+ jsEvent.currentTarget.className;
				}
				if (jsEvent.fromElement) {
					s += "\nfromElement=[" + jsEvent.fromElement.nodeType + "]"
							+ jsEvent.fromElement.tagName + "#"
							+ jsEvent.fromElement.id + "."
							+ jsEvent.fromElement.className;
				}
				if (jsEvent.srcElement) {
					s += "\nsrcElement=[" + jsEvent.srcElement.nodeType + "]"
							+ jsEvent.srcElement.tagName + "#"
							+ jsEvent.srcElement.id + "."
							+ jsEvent.srcElement.className;
				}
				if (jsEvent.toElement) {
					s += "\ntoElement=[" + jsEvent.toElement.nodeType + "]"
							+ jsEvent.toElement.tagName + "#"
							+ jsEvent.toElement.id + "."
							+ jsEvent.toElement.className;
				}
			}

			f_core.Debug(f_event, "Popup event ALERT:\n" + s);
		}

		alert(s);

		return true;
	},
	/**
	 * @method hidden static
	 */
	EnterEventLock : function(set) {
		var currentLock = f_event._EvtLock;

		f_core.Assert(typeof (set) == "number",
				"f_event.EnterEventLock: Lock type is invalid ! (" + set + ")");

		if (currentLock & set) {
			f_core.Error("f_event", "Several same lock of type " + set + " !");
			return;
		}

		f_event._EvtLock |= set;
		f_core.Debug("f_event", "Enter event lock (new state="
				+ f_event._EvtLock + " old=" + currentLock + ")");
	},
	/**
	 * @method hidden static
	 */
	ExitEventLock : function(set) {
		var currentLock = f_event._EvtLock;

		f_core.Assert(typeof (set) == "number",
				"f_event.ExitEventLock: Lock type is invalid ! (" + set + ")");

		if (!(currentLock & set)) {
			f_core.Error("f_event", "No lock for type " + set + " !");
			return;
		}

		f_event._EvtLock &= (~set);
		f_core.Debug("f_event", "Exit event lock (new state="
				+ f_event._EvtLock + " old=" + currentLock + ")");

		return f_event._EvtLock;
	},
	/**
	 * Allocate detail object
	 * 
	 * @method public static
	 * @param optional Object details
	 * @return Object
	 */
	NewDetail : function(details) {
		f_core.Assert(typeof(details)=="object" || details===undefined, "f_event.NewDetail: Invalid 'details' parameter ("+details+")");
		
		if (!details) {
			details={};
		}
		if (!details.value) {
			details.value=0;
		}
		
		return details;
	}
};

new f_class("f_event", {
	statics : __statics,
	members : __members,
	_systemClass : true
// Il est systeme car on peut sortir de l'appli sur un evenement
});
