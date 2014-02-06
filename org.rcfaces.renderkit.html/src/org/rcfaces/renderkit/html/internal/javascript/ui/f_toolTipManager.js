/**
 * Class Tooltip Manager
 * 
 * @class public f_toolTipManager extends f_object, fa_commands
 * @author jbmeslin@vedana.com (latest modification by $Author: jbmeslin $)
 * @author olivier.oeuillot@vedana.com
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:19:48 $
 */

var __statics = {

	/**
	 * @field private static f_toolTipManager
	 */
	_Instance: undefined,

	/**
	 * @field private final static Number
	 */
	_DEFAULT_DELAY_MS: 600,

	/**
	 * @field private final static Number
	 */
	_DEFAULT_NEIGHBOUR_THRESHOLD_MS: 100,

	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return void
	 * @context event:evt
	 */
	_ElementOver: function(evt) {

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (window._rcfacesExiting) {
			return;
		}
			
		// f_core.Debug(f_toolTipManager, "_ElementOver: event="+evt+"
		// target="+evt.target);
		
		var instance = f_toolTipManager.Get();

		try {
			instance._elementOver(evt);

		} catch (x) {
			f_core.Error(f_toolTipManager, "_ElementOver: exception", x);
		}
	},

	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return void
	 * @context event:evt
	 */
	_ElementOut: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (window._rcfacesExiting) {
			return;
		}

		// f_core.Debug(f_toolTipManager, "_ElementOut: event="+evt+"
		// target="+evt.target);

		var instance = f_toolTipManager.Get();

		try {
			instance._elementOut(evt);

		} catch (x) {
			f_core.Error(f_toolTipManager, "_ElementOut: exception", x);
		}
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return void
	 * @context event:evt
	 */
	_HideToolTip: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (window._rcfacesExiting) {
			return;
		}

		// f_core.Debug(f_toolTipManager, "_HideToolTip: event="+evt+"
		// target="+evt.target);

		var instance = f_toolTipManager.Get();

		try {
			instance._hideToolTipEvent(evt);

		} catch (x) {
			f_core.Error(f_toolTipManager, "_HideToolTip: exception", x);
		}
	},
	/**
	 * @method public static
	 * @param f_event event
	 * @return void
	 */
	OpenTooltip: function(event) {
		var instance = f_toolTipManager.Get();
		var evt=event.f_getJsEvent();
		var component = evt.target || evt.srcElement;
		if (component && component.f_getClientData) {
			var target=component.f_getClientData("aria.tooltip.retarget");
			if (target) {
				var te=fa_namingContainer.FindSiblingComponents(component, target);
				if (te) {
					component=te;
				}
			}
		}

		try {
			instance._elementOver(event.f_getJsEvent(), component);

		} catch (x) {
			f_core.Error(f_toolTipManager, "OpenTooltip: exception", x);
		}
		
	},
	
	/**
	 * @method public static
	 * @return f_toolTipManager
	 */
	Get: function() {
		var instance = f_toolTipManager._Instance;
		if (!instance) {
			instance = f_toolTipManager.f_newInstance();
			f_toolTipManager._Instance = instance;
		}

		return instance;
	},
		
	/**
	 * @method hidden static
	 * @param Document doc
	 * @param Element rootElement
	 * @return void
	 */
	InstallOverCallbacks: function(doc, rootElement) {
		f_core.AddEventListener(rootElement, "mouseover",
				f_toolTipManager._ElementOver, doc);

		f_core.AddEventListener(rootElement, "mouseout",
				f_toolTipManager._ElementOut, doc);		

		// Touches

		f_core.AddEventListener(rootElement, "keydown",
				f_toolTipManager._HideToolTip, doc);
	},
	
	/**
	 * @method hidden static
	 * @param Document doc
	 * @param Element rootElement
	 * @return void
	 */
	UninstallOverCallbacks: function(doc, rootElement) {
		f_core.RemoveEventListener(rootElement, "mouseover",
				f_toolTipManager._ElementOver, doc);

		f_core.RemoveEventListener(rootElement, "mouseout",
				f_toolTipManager._ElementOut, doc);		

		// Touches

		f_core.RemoveEventListener(rootElement, "keydown",
				f_toolTipManager._HideToolTip, doc);
	},	

	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_toolTipManager._Instance = undefined; // f_toolTipManager
	}

};

var __members = {

	/**
	 * @field f_toolTip
	 */
	_currentTooltip: undefined,

	/**
	 * @field Number
	 */
	_timerId: undefined,

	/**
	 * @field Number
	 */
	_showDelayMs: undefined,

	/**
	 * @field Number
	 */
	_neighbourThresholdMs: undefined,

	/**
	 * @field Number
	 */
	_lastToolTipClose: undefined,

	/**
	 * @field Boolean
	 */
	_listenerInstalled: undefined,

	f_toolTipManager: function() {
		this.f_super(arguments);

		var delay = undefined;
		var neighbourThresholdMs = undefined;

		if (this.nodeType == f_core.ELEMENT_NODE) {
			delay = f_core.GetNumberAttributeNS(this, "delay", -1);
			neighbourThresholdMs = f_core.GetNumberAttributeNS(this,
					"neighbourThreshold", -1);
		}

		if (delay < 0 || delay === undefined) {
			delay = f_toolTipManager._DEFAULT_DELAY_MS;
		}
		if (neighbourThresholdMs < 0 || neighbourThresholdMs === undefined) {
			neighbourThresholdMs = f_toolTipManager._DEFAULT_NEIGHBOUR_THRESHOLD_MS;
		}

		var mainToolTipManager = f_toolTipManager._Instance;		
		if (mainToolTipManager) {
			// Une instance existe déjà !
			
			mainToolTipManager._showDelayMs = delay;
			mainToolTipManager._neighbourThresholdMs = neighbourThresholdMs;
			return;
		}

		this._showDelayMs = delay;
		this._neighbourThresholdMs = neighbourThresholdMs;

		f_toolTipManager.InstallOverCallbacks(document, document.body);

		// Focus
		f_core.AddEventListener(document.body, "focus",
				f_toolTipManager._HideToolTip, document);

		// Souris
		f_core.AddEventListener(document.body, "mousedown",
				f_toolTipManager._HideToolTip, document);

		this._listenerInstalled=true;
		
	},

	f_finalize: function() {
		this._currentTooltip = undefined;
		// this._listenerInstalled=undefined; // Boolean

		var timerId = this._timerId;
		if (timerId) {
			this._timerId = undefined;

			clearTimeout(timerId);
		}

		if (this._listenerInstalled) {
			this._listenerInstalled=undefined; 
	
			f_toolTipManager.UninstallOverCallbacks(document, document.body);
		
			// Focus
			f_core.RemoveEventListener(document.body, "focus",
					f_toolTipManager._HideToolTip, document);
	
			// Clavier
			f_core.RemoveEventListener(document.body, "mousedown",
					f_toolTipManager._HideToolTip, document);
		}
		
		this.f_super(arguments);
	},

	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param optional Element element
	 * @return void
	 */
	_elementOver: function(evt, element) {
		
		var target = evt.target || evt.srcElement;
		element = element || target || this._getElementAtPosition(evt);
		
		var tooltipContainer = this._getToolTipContainerForElement(element);

		if (false) {
			console.log("_ElementOver: event="+evt+" target="+evt.target+" element="+element+" container="+tooltipContainer);
		}
		
		if (element._tooltipElement) {
			if (false) {
				console.log("Our tooltip !");
			}
			return;
		}
		
		var tooltipInfos = null;
		if (tooltipContainer) {
			tooltipInfos = tooltipContainer.fa_getToolTipForElement(element, evt);
		}

		if (f_core.IsDebugEnabled(f_toolTipManager)) {
			f_core.Debug(f_toolTipManager, "_elementOver: over="
					+ tooltipContainer + " element=" + element.id);
			if (tooltipInfos) {
				f_core.Debug(f_toolTipManager, "_elementOver: item="
						+ tooltipInfos.item.id + " container="
						+ tooltipInfos.container.id);
			}
		}

		var currentTooltip = this._currentTooltip;

		if (currentTooltip
				&& tooltipInfos
				&& tooltipInfos.container == currentTooltip
						.f_getElementContainer()
				&& tooltipInfos.item == currentTooltip.f_getElementItem()) {

			// Le même tooltip que celui qui est déjà affiché !
			f_core.Debug(f_toolTipManager, "_elementOver: Same TOOLTIP");
			return;
		}

		var timerId = this._timerId;
		if (timerId) {
			this._timerId = undefined;

			window.clearTimeout(timerId);
		}

		if (currentTooltip) {
			this.f_hideToolTip(currentTooltip);
			currentTooltip.f_clear();

			this._currentTooltip = undefined;
		}

		if (!tooltipInfos) {
			// Pas de container de tooltip, rien à faire
			return;
		}

		var tooltip = this.f_getToolTipByClientId(tooltipInfos.toolTipClientId,
				tooltipInfos.toolTipContent,
				tooltipInfos.container.ownerDocument);
		if (!tooltip) {
			// Pas de tooltip trouvé
			return;
		}

		tooltip.f_initialize(tooltipContainer, tooltipInfos.item);
		this._currentTooltip = tooltip;

		var self = this;

		if (this._showDelayMs == 0) {
			f_core.Debug(f_toolTipManager, "_elementOver: No delay show tooltip "+tooltip.id);
			this.f_showToolTip(tooltip, evt);
			return;
		}

		if (this._neighbourThresholdMs >= 0 && this._lastToolTipClose > 0) {
			var now = new Date().getTime();

			if (now < this._lastToolTipClose + this._neighbourThresholdMs) {
				f_core.Debug(f_toolTipManager, "_elementOver: Neighbour show tooltip "+tooltip.id);
				this.f_showToolTip(tooltip, evt);
				return;
			}
		}

		this._timerId = window.setTimeout(function() {
			f_core.Debug(f_toolTipManager, "_elementOver: Time out show tooltip "+tooltip.id);
			
			self.f_showToolTip(tooltip, evt);
			self = undefined;
			tooltip = undefined;

		}, this._showDelayMs);

		return;
	},
	/**
	 * @method protected
	 * @param String
	 *            tooltipClientId
	 * @param String tooltipContent
	 * @return f_toolTip
	 */
	f_getToolTipByClientId: function(tooltipClientId, tooltipContent, doc) {

		if (tooltipContent) {
			tooltipClientId="__dynamicToolTipContent";
		}
		
		var tooltipComponent = f_core.GetElementByClientId(tooltipClientId);

		if (tooltipComponent) {
			if (tooltipContent) {
				tooltipComponent.f_setContentSpecified(true);
				tooltipComponent.f_setContent(tooltipContent);
			}
			return tooltipComponent;
		}

		tooltipComponent = f_core.CreateElement(doc.body, "div", {
			className: "f_toolTip",
			id: tooltipClientId
		});

		f_core.SetAttributeNS(tooltipComponent, "class", "f_toolTip");
		
		tooltipComponent.setAttribute("role", "description");        
		tooltipComponent.setAttribute("aria-relevant", "additions all");
		tooltipComponent.setAttribute("aria-atomic", "true");
		tooltipComponent.setAttribute("aria-live", "polite");
		
		tooltipComponent = this.f_getClass().f_getClassLoader().f_init(
				tooltipComponent, true, true);

		if (tooltipContent) {
			tooltipComponent.f_setContentSpecified(true);
			tooltipComponent.f_setContent(tooltipContent);
		}
		return tooltipComponent;
	},

	/**
	 * @method protected
	 * @param f_toolTip
	 *            tooltip
	 * @param optional
	 *            Event jsEvent
	 * @return void
	 */
	f_showToolTip: function(tooltip, jsEvent) {
		if (!tooltip) {
			return;
		}

		var tooltipContainer = tooltip.f_getElementContainer();
		if (!tooltipContainer) {
			return;
		}

		tooltipContainer.fa_setToolTipVisible(tooltip, true, jsEvent);

		f_core.Debug(f_toolTipManager, "f_showToolTip: tooltipContainer="
				+ tooltipContainer.id + " tooltipItem="+tooltip.f_getElementItem().id+" tooltip=" + tooltip.id);
	},

	/**
	 * @method protected
	 * @param f_toolTip
	 *            tooltip
	 * @param optional
	 *            Event jsEvent
	 * @return void
	 */
	f_hideToolTip: function(tooltip, jsEvent) {
		if (!tooltip) {
			return;
		}

		var tooltipContainer = tooltip.f_getElementContainer();
		if (!tooltipContainer) {
			return;
		}

		if (!tooltip.f_isVisible()) {
			return;
		}

		this._lastToolTipClose = new Date().getTime();

		tooltipContainer.fa_setToolTipVisible(tooltip, false, jsEvent);

		f_core.Debug(f_toolTipManager, "f_hideToolTip: tooltipContainer="
				+ tooltipContainer.id + " tooltip=" + tooltip.id);

	},

	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @return void
	 */
	_elementOut: function(evt) {
	},

	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @return void
	 */
	_hideToolTipEvent: function(evt) {
		var currentTooltip = this._currentTooltip;

		if (!currentTooltip) {
			return;
		}

		this._currentTooltip = undefined;

		this.f_hideToolTip(currentTooltip);
		currentTooltip.f_clear();
	},

	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @return Element element
	 */
	_getElementAtPosition: function(evt) {

		var eventPos = f_core.GetJsEventPosition(evt);
		if (!eventPos) {
			return undefined;
		}
		var de = f_core.SearchComponentByAbsolutePosition(eventPos.x,
				eventPos.y);
		var element = (de.length) ? de[de.length - 1] : null;

		if (element) {
			return element;
		}

		return undefined;
	},

	/**
	 * @method private
	 * @param Element
	 *            element
	 * @return Any container
	 */
	_getToolTipContainerForElement: function(source) {

		var parentTooltip = null;

		var comp = source;
		for (; comp; comp = comp.parentNode) {

			if (comp.nodeType == f_core.TEXT_NODE) {
				continue;
			}

			if (comp.nodeType==f_core.DOCUMENT_NODE) {
				if (comp.documentElement && comp.documentElement.tagName=="HTML") {
					break;
				}
				
				var win=f_core.GetWindow(comp);
				if (!win.frameElement) {
					break;
				}
				comp=win.frameElement;
			}
			
			if (comp.nodeType != f_core.ELEMENT_NODE) {
				break;
			}

			if (comp.title) { // et le ALT ??? 
				// Risque d'afficher un tooltip ... on laisse tomber TOUT !
				return false;
			}

			var state = f_classLoader.GetObjectState(comp);

			if (state == f_classLoader.UNKNOWN_STATE) {
				continue;
			}

			if (state == f_classLoader.LAZY_STATE) {
				var classLoader = this.f_getClass().f_getClassLoader();

				classLoader.f_init(comp, true, true);
			}

			if (comp.fa_getToolTipForElement && !parentTooltip) {
				// Il faut rechercher jusqu'à la racine pour rechercher un ALT
				// ou un TITLE
				parentTooltip = comp;
			}
		}

		return parentTooltip;
	},

	/**
	 * @method public
	 * @param Number
	 *            showDelayMs
	 * @return void
	 */
	f_setShowDelayMs: function(showDelayMs) {
		if (!showDelayMs || showDelayMs < 0) {
			showDelayMs = f_toolTipManager._DEFAULT_DELAY;
		}

		this._showDelayMs = showDelayMs;
	},

	/**
	 * @method public
	 * @param Number
	 *            neighbourThresholdMs
	 * @return void
	 */
	f_setNeighbourThresholdMs: function(neighbourThresholdMs) {
		if (!neighbourThresholdMs || neighbourThresholdMs < 0) {
			neighbourThresholdMs = f_toolTipManager._DEFAULT_DELAY;
		}

		this._neighbourThresholdMs = neighbourThresholdMs;
	}
};

new f_class("f_toolTipManager", {
	extend: f_object,
	aspects: [ fa_commands ],
	statics: __statics,
	members: __members
});
