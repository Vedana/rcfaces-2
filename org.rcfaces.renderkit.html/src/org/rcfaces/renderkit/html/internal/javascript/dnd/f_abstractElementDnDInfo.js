/*
 * $Id: f_abstractElementDnDInfo.js,v 1.2 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_abstractElementDnDInfo class
 *
 * @class public abstract f_abstractElementDnDInfo extends f_dragAndDropInfo
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:31 $
 */

var __statics = {
	/**
	 * @field private static final
	 */
	_DEFAULT_STYLE_CLASS: "f_elementDnDInfo"
};

var __members = {

	/**
	 * @field private Number
	 */
	_mouseDeltaX: 0,
	
	/**
	 * @field private Number
	 */
	_mouseDeltaY: 0,

		
	f_abstractElementDnDInfo: function(dragAndDropEngine) {
		this.f_super(arguments, dragAndDropEngine);
	},

	f_finalize: function() {
		var cursor=this._cursor;
		if (cursor) {
			this._cursor=undefined;
			cursor.parentNode.removeChild(cursor);			
		}
		
		// this._oldEffect= undefined; // Number
		
		this.f_super(arguments);
	},
	
	/**
	 * @method public
	 * @return void
	 */
	f_start: function() {
		
		var cursor=this.f_createElement();
		
		//cursor.className=f_abstractElementDnDInfo._DEFAULT_STYLE_CLASS;				
		
		this._cursor=cursor;
		
		this.f_fillElement(cursor);

		this.f_updateCursorStyle(0);
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_createElement: function() {
		
		var cursor=document.createElement(this.f_getMainElementType());
		
		var body=document.body;
		
		body.insertBefore(cursor, null); //body.firstChild);
		
		f_core.SetIgnoreInSearch(cursor);
	
		return cursor;
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_getMainElementType: function() {
		return "div";
	},
	/**
	 * @method protected
	 * @param HTMLElement element
	 * @return void
	 */
	f_fillElement: function(element) {		
	},
	/**
	 * @method public
	 * @return void
	 */
	f_end:function() {

		var cursor=this._cursor;
		if (cursor) {
			this._cursor=undefined;
			cursor.parentNode.removeChild(cursor);			
		}

		this.f_super(arguments);
	},

	/**
	 * @method protected
	 * @param Number mouseDeltaX
	 * @param Number mouseDeltaY
	 * @return void
	 */
	f_setOffsetPosition: function(mouseDeltaX, mouseDeltaY) {
		f_core.Assert(typeof(mouseDeltaX)=="number", "f_abstractElementDnDInfo.f_setOffsetPosition: Invalid mouseDeltaX parameter ("+mouseDeltaX+").");
		f_core.Assert(typeof(mouseDeltaY)=="number", "f_abstractElementDnDInfo.f_setOffsetPosition: Invalid mouseDeltaY parameter ("+mouseDeltaY+").");

		this._mouseDeltaX=mouseDeltaX;
		this._mouseDeltaY=mouseDeltaY;
	},
	/**
	 * @method public
	 * @param String[] types
	 * @param Number effect
	 * @param f_component targetComponent
	 * @param any targetItem
	 * @param Object targetItemValue
	 * @param Map targetAdditionalInformations
	 * @return void
	 */
	f_updateTarget: function(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations) {
		if (this._oldEffect!==effect) {
			this._oldEffect=effect;
			
			this.f_updateCursorStyle(effect);
		}
		
		this.f_super(arguments, types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);
	},

	/**
	 * @method public
	 * @param Number newPositionX
	 * @param Number newPositionY
	 * @return void
	 */
	f_move: function(newPositionX, newPositionY) {
		this.f_super(arguments, newPositionX, newPositionY);
		
		if (this._positionX==newPositionX && this._positionY==newPositionY) {
			return;
		}
		
		this._positionX=newPositionX;
		this._positionY=newPositionY;
		
		this.f_updateCursorPosition();
	},
	
	/**
	 * @method protected
	 * @param optional HTMLElement cursorElement
	 * @return void
	 */
	f_updateCursorPosition: function(cursorElement) {

		if (!cursorElement) {
			cursorElement=this.f_getCursorElement();
			if (!cursorElement) {
				return;
			}
		}
		
		var self=this;
		f_core.GetWindow(cursorElement).setTimeout(function() {
			
			if (window._rcfacesExiting) {
				return false;
			}
			
			self._updateCursorPosition(cursorElement);
			self=null;
		}, 10);
	},

	/**
	 * @method private
	 * @param HTMLElement cursorElement
	 * @return void
	 */
	_updateCursorPosition: function(cursorElement) {
		
		var pw=cursorElement.offsetWidth;
		var ph=cursorElement.offsetHeight;
		
		var screenSizes = f_core.GetViewSize(null, cursorElement.ownerDocument);
		var screenPositions = f_core.GetScrollOffsets(null, cursorElement.ownerDocument);

		var sx=screenPositions.x;
		var sy=screenPositions.y;
		var sw=screenSizes.width;
		var sh=screenSizes.height;		
		
		var px=this._positionX+this._mouseDeltaX;
		var py=this._positionY+this._mouseDeltaY;
		
		if (px+pw>sx+sw) {
			// Positionne à droite
			
			px=this._positionX-pw;
		}
		if (px<sx) {
			px=sx;
		}
		
		if (py+ph>sy+sh) {
			// Positionne en haut
			
			py=this._positionY-ph;
		}
		if (py<sy) {
			py=sy;
		}
		
		if (this._oldX!==px || this._oldY!==py) {
			this._oldX=px;
			this._oldY=py;
			
			cursorElement.style.left=px+"px";
			cursorElement.style.top=py+"px";		
			//cursorElement.style.display="block";
		}
	},
	/**
	 * @method protected
	 * @param Number effect
	 * @return void
	 */
	f_updateCursorStyle: function(effect) {
		var cursorElement=this.f_getCursorElement();
		if (!cursorElement) {
			return;
		}

		var cl=this.f_computeCursorStyle(effect);
		
		cursorElement.className=cl;
	
		this.f_updateCursorPosition();
	},
	/**
	 * @method protected
	 * @param Number effect
	 * @return String
	 */
	f_computeCursorStyle: function(effect) {
		
		var ccl=this.f_getCursorClassName();
		
		var def=f_abstractElementDnDInfo._DEFAULT_STYLE_CLASS;
		var cl=def;
		if (ccl) {
			cl+=" "+ccl;
		}
		
		if (effect>=0) {
			cl+=" "+def+"_"+effect;
			if (ccl) {
				cl+=" "+ccl+"_"+effect;
			}
		}
		
		return cl;
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_getCursorClassName: function() {
		return null;
	},
	
	/**
	 * @method protected final
	 * @return HTMLElement
	 */
	f_getCursorElement: function() {
		return this._cursor;
	},
	
	/**
	 * @method protected
	 * @param String key
	 * @return String
	 */
	f_getItemProperty: function(key, searchText, targetComponent, targetItem, targetItemValue, additionalInformations) {
		if (additionalInformations) {
			var txt=additionalInformations[key];
			
			if (txt) {
				return txt;
			}
		}
		
		if (targetItemValue!==undefined && targetItemValue!==null) {
			if (targetComponent.f_getItemClientData) {
				var txt=targetComponent.f_getItemClientData(targetItemValue, key);
				
				if (txt) {
					return txt;
				}
			}
			if (searchText && targetComponent.f_getElementLabel) {
				var txt=targetComponent.f_getElementLabel(targetItemValue);
				if (txt) {
					return txt;
				}
			}
			if (targetComponent.f_getRowValuesSet) {
				var sets=targetComponent.f_getRowValuesSet(targetItemValue);
				if (sets) {
					var txt=sets[key];
					if (txt) {
						return txt;
					}					
				}
			}
		}
		
		if (targetComponent.f_getClientData) {
			var txt=targetComponent.f_getClientData(key);
			
			if (txt) {
				return txt;
			}
		}
				
		if (searchText && targetComponent.f_getText) {
			var txt=targetComponent.f_getText();
			
			if (txt) {
				return txt;
			}
		}
	
		return null;
	}
};
		
new f_class("f_abstractElementDnDInfo", {
	extend: f_dragAndDropInfo,
	members: __members,
	statics: __statics
});