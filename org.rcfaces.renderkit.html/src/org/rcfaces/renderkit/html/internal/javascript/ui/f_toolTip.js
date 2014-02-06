/**
 * @class f_toolTip extends f_component, fa_asyncRender
 * @author jbmeslin@vedana.com
 * @version $Revision: 1.3 $ $Date: 2013/12/11 10:19:48 $
 */

var __statics = {

	/**
	 * @field hidden static final Number
	 */
	MOUSE_POSITION: 0,

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
	 * @field hidden static final Number
	 */
	MIDDLE_RIGHT_COMPONENT: 48,

	/**
	 * @field hidden static final Number
	 */
	MIDDLE_LEFT_COMPONENT: 12,
	

	/**
	 * @field private static final Number
	 */
	_DEFAULT_POSITION: 8

};

var __members = {

	/**
	 * @field private HTMLElement
	 */
	_elementContainer: undefined,

	/**
	 * @field private HTMLElement
	 */
	_elementItem: undefined,

	/**
	 * @field private String
	 */
	_toolTipPosition: undefined,

	/**
	 * @field private Number
	 */
	_stateId: 0,

	/**
	 * @field private String
	 */
	_toolTipId: undefined,

	f_toolTip: function() {
		this.f_super(arguments);

		if (!this.parentNode._tooltipContainer) {
			var tc=this.ownerDocument.getElementById("__rcfaces_tooltipContainer");
			if (!tc) {
				var body = this.ownerDocument.body;
				
				tc=f_core.CreateElement(body, "div", {
					id: "__rcfaces_tooltipContainer",
					"aria-live": "polite"
				});
			}
			
			this.parentNode.removeChild(this);
			tc.appendChild(this);
		}

		this._visible = false;
		this._tooltipElement=true;
	},

	f_finalize: function() {
		this._elementContainer = undefined; // HTMLElement
		this._elementItem = undefined; // HTMLElement
		// this._toolTipPosition=undefined; // String
		// this._toolTipId=undefined; // String

		this.f_super(arguments);
	},

	/**
	 * @method protected
	 */
	f_performErrorEvent: function(param, messageCode, message) {
		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},

	/**
	 * @method hidden
	 * @return HTMLElement
	 */
	fa_getInteractiveParent: function() {
		return this; // div tooltip
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getToolTipId: function() {
		if (this._toolTipId === undefined) {
			this._toolTipId = f_core.GetAttribute(this, "v:toolTipId", null);
		}

		return this._toolTipId;
	},

	/**
	 * @method hidden
	 * @param HTMLElement
	 *            component Main component
	 * @param HTMLElement
	 *            elementItem Sub component or main component
	 * @param String
	 *            Default tooltip position
	 * @return Number
	 */
	f_initialize: function(component, elementItem, toolTipPosition) {
		this._elementContainer = component;
		this._elementItem = elementItem;
		this._toolTipPosition = toolTipPosition;

		this._stateId++;

		return this._stateId;
	},
	/**
	 * @method hidden
	 * @return HTMLElement
	 */
	f_getElementContainer: function() {
		return this._elementContainer;
	},
	/**
	 * @method hidden
	 * @return Number
	 */
	f_getStateId: function() {
		if (!this._elementContainer) {
			return -1;
		}
		return this._stateId;
	},
	/**
	 * @method hidden
	 * @return HTMLElement
	 */
	f_getElementItem: function() {
		return this._elementItem;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_clear: function() {
		this._elementContainer = undefined;
		this._elementItem = undefined;
		this._toolTipPosition = undefined;
	},
	/**
	 * @method public
	 * @param optional
	 *            Event jsEvent
	 * @param optional
	 *            String defaultPosition
	 * @param optional
	 *            Element refPosition
	 * @return Boolean
	 */
	f_show: function(stateId, jsEvent, defaultPosition, refPosition) {

		if (stateId != this._stateId) {
			return false;
		}

		var ref = null;
		var position = f_core.GetNumberAttribute(this, "v:position", undefined);
		if (position === undefined) {
			ref = (this._elementItem) ? this._elementItem
					: this._elementContainer;
		}

		if (position === undefined && defaultPosition !== undefined) {
			position = defaultPosition;
			ref = refPosition;
		}

		if (position < 0 || position === undefined) {
			position = f_toolTip._DEFAULT_POSITION;
		}

		if (!ref || ref.nodeType != f_core.ELEMENT_NODE) {
			if (this._elementItem) {
				ref = this._elementItem;
			}

			if (!ref || ref.nodeType != f_core.ELEMENT_NODE) {
				ref = this._elementContainer;
			}
		}

		if (!ref) {
			f_core.Assert(ref, "f_toolTip.f_show: No ref component ?");
			return false;
		}

		this._computePosition(ref, position /*f_toolTip.BOTTOM_LEFT_COMPONENT*/, jsEvent,
				this, {});

		if (this.f_isContentSpecified()) {
			this._interactive=false;
		}
		
		var parentNode=this.parentNode;
		parentNode.removeChild(this);

		this.f_setVisible(true);
		
		parentNode.appendChild(this);
		
		return true;
	},
	/**
	 * @method public
	 * @param Number
	 *            stateId
	 * @return void
	 */
	f_hide: function(stateId) {

		if (stateId != this._stateId) {
			return false;
		}

		this.f_setVisible(false);

		return true;
	},

	/**
	 * @method private
	 * @return void
	 */
	_computePosition: function(component, position, jsEvent, popup,
			positionInfos) {
		f_core.Assert(component,
				"f_toolTip._computePosition: Invalid component parameter '"
						+ component + "'.");
		f_core.Assert(typeof(position)=="number",
				"f_toolTip._computePosition: Invalid position parameter '"
						+ position + "'.");

		var offsetX = 0;
		var offsetY = 0;
		var offsetWidth= 0;
		var offsetHeight=0;

		if (component) {
			var absPos = f_core.GetAbsolutePosition(component);
			offsetX = absPos.x;
			offsetY = absPos.y;
			
			if (component.getBoundingClientRect) {
				var bbox=component.getBoundingClientRect();
				
				offsetWidth=Math.floor(bbox.width);
				offsetHeight=Math.floor(bbox.height);
				
			} else {
				offsetWidth=component.offsetWidth;
				offsetHeight=component.offsetHeight;
			}
		}

		switch (position) {

		case f_toolTip.LEFT_COMPONENT:
			break;

		case f_toolTip.MIDDLE_LEFT_COMPONENT:
			offsetY += offsetHeight/2;
			break;

		case f_toolTip.BOTTOM_COMPONENT:
		case f_toolTip.BOTTOM_LEFT_COMPONENT:
			offsetY += offsetHeight;
			break;

		case f_toolTip.MOUSE_POSITION:
			var eventPos = f_core.GetJsEventPosition(jsEvent);

			if (false) {
				console.log("_computePosition: (mouse position) X="
						+ offsetX + " Y=" + offsetY + " eventX=" + eventPos.x
						+ " eventY=" + eventPos.y + " component="+component);
			}
			
			offsetX = eventPos.x;// - cursorPos.x;
			offsetY = eventPos.y;// - cursorPos.y;

			offsetX+=8; // SInon la 
			offsetY+=8;
			
			f_core.Debug(f_toolTip, "_computePosition: (mouse position) X="
					+ offsetX + " Y=" + offsetY + " eventX=" + eventPos.x
					+ " eventY=" + eventPos.y);

			break;

		case f_toolTip.RIGHT_COMPONENT:
			offsetX += offsetWidth;
			break;

		case f_toolTip.MIDDLE_RIGHT_COMPONENT:
			offsetY += offsetHeight/2;
			offsetX += offsetWidth;
			break;

		case f_toolTip.BOTTOM_RIGHT_COMPONENT:
			offsetY += offsetHeight;
			offsetX += offsetWidth;
			break;

		case f_toolTip.MIDDLE_COMPONENT:
			offsetX += offsetWidth / 2;
			offsetY += offsetHeight / 2;
			break;
		}

		if (f_core.IsDebugEnabled(f_toolTip) && component) {
			f_core.Debug(f_toolTip, "_computePosition: X=" + offsetX + " Y="
					+ offsetY + " cw=" + offsetWidth
					+ " ch=" + offsetHeight);
		}

		if (positionInfos.deltaX) {
			offsetX += positionInfos.deltaX;
		}

		if (positionInfos.deltaY) {
			offsetY += positionInfos.deltaY;
		}

		if (positionInfos.deltaWidth) {
			offsetWidth += positionInfos.deltaWidth;
		}

		if (positionInfos.deltaHeight) {
			offsetHeight += positionInfos.deltaHeight;
		}

		offsetX += 2; // Border du tooltip par défaut !

		var positions = {
			x: offsetX,
			y: offsetY
		};

		var viewSize = f_core.GetViewSize(null, popup.ownerDocument);

		var bw = viewSize.width;
		var bh = viewSize.height;
		var scrollPosition = f_core.GetScrollOffsets(popup.ownerDocument);
		bw += scrollPosition.x;
		bh += scrollPosition.y;

		var absPos = (popup.offsetParent) ? f_core
				.GetAbsolutePosition(popup.offsetParent) : {
			x: 0,
			y: 0
		};

		var pWidth = popup.offsetWidth;
		if (!pWidth) {
			pWidth = parseInt(popup.style.width);
		}

		var pHeight = popup.offsetHeight;
		if (!pHeight) {
			pHeight = parseInt(popup.style.height);
		}

		f_core.Debug(f_toolTip, "_computePosition: bw=" + bw + " bh=" + bh
				+ " absPos.x=" + absPos.x + " absPos.y=" + absPos.y
				+ " positions.x=" + positions.x + " positions.y=" + positions.y
				+ " popupWidth=" + pWidth + " popupHeight=" + pHeight);

		if (pWidth + positions.x + absPos.x > bw) {
			positions.x = bw - pWidth - absPos.x;

			f_core.Debug(f_toolTip, "_computePosition: change x position to "
					+ positions.x);
		}

		if (pHeight > bh - scrollPosition.y) {
			positions.y = 0;
			positions.x = 0;

		} else if (pHeight + positions.y + absPos.y > bh) {
			if (component) {
				var aeAbs = f_core.GetAbsolutePosition(component);
				positions.y = aeAbs.y - pHeight;

			} else {
				positions.y = bh - pHeight - absPos.y;
			}

			f_core.Debug(f_toolTip, "_computePosition: change y position to "
					+ positions.y);
		}

		var popupStyle = popup.style;

		popupStyle.left = positions.x + "px";
		popupStyle.top = positions.y + "px";
	},
	f_updateVisibility: function(visible) {
		this.f_super(arguments, visible);

		/*
		 * var style=this.style; if (visible) { style.opacity="0.85"; } else {
		 * style.opacity="0"; }
		 */
	},
	
	f_cleanContent: function() {
		this.f_asyncDestroyWaiting();
		
		var gs=[ false ];
		var children=this.childNodes;
		for(var i=0;i<children.length;i++) {
			var child=children[i];
			if (child.nodeType==f_core.ELEMENT_NODE) {
				gs.push(child);
			}
		}
		
		if (gs.length>1) {
			var classLoader=this.f_getClass().f_getClassLoader();
			classLoader.f_garbageObjects.apply(classLoader, gs);
		}
	
		for (; this.firstChild;) {
			this.removeChild(this.firstChild);
		}

	},
	/**
	 * @method hidden
	 * @param String content
	 * @return void
	 */
	f_setContent: function(content) {
		this.f_getClass().f_getClassLoader()
				.f_loadContent(this,
						this, content);
	},
	/**
	 * @method protected
	 * @param String content
	 * @return void
	 */
	f_asyncSetContent: function(content) {
		this.f_setContent(content);		
	},
	/**
	 * @method hidden
	 * @param Boolean state
	 * @return void
	 */
	f_setContentSpecified: function(state) {
		this._contentSpecified = !!state;
	},
	/**
	 * @method hidden
	 * @return Boolean
	 */
	f_isContentSpecified: function() {
		return this._contentSpecified;
	}

};

new f_class("f_toolTip", {
	extend: f_component,
	aspects: [ fa_asyncRender ],
	members: __members,
	statics: __statics
});
