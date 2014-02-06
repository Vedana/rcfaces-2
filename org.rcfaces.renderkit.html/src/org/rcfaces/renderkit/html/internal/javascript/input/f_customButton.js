/*
 * $Id: f_customButton.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * f_customButton class
 *
 * @class f_customButton extends f_component, fa_readOnly, fa_disabled, fa_borderType, fa_value
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */ 
var __statics = {

	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:customButton
	 */
	_MouseDown: function(evt) {
		var customButton=this.f_link;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (customButton.f_getEventLocked(evt)) {
			return false;
		}
		
		return customButton._onMouseDown();
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:customButton
	 */
	_MouseUp: function(evt) {
		var customButton=this.f_link;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (customButton.f_getEventLocked(evt, false)) {
			return false;
		}

		return customButton._onMouseUp();
	}
}
 
var __members = {

	f_customButton: function() {
		this.f_super(arguments);

		this.f_setForcedEventReturn(f_event.SELECTION, false);
				
		var installSelectionListener=false;
	
		var border=this.f_getBorderComponent();
		if (border) {
			//border.onmousedown=f_customButton._MouseDown;
			//border.onmouseup=f_customButton._MouseUp;			
			installSelectionListener=true;

			this._installHoverFocus();
		}

		
		var link=f_core.GetFirstElementByTagName(this, "a", true);
		if (link) {
			this._link=link;
			link.href=f_core.CreateJavaScriptVoid0();
			if (this.tabIndex) {
				link.tabIndex=this.tabIndex;
			}
		}
		
//		this._installListeners("div");
//		this._installListeners("span");
//		this._installListeners("img");
//		alert("Install="+this._links);
		
		if (installSelectionListener) {
			this.f_insertEventListenerFirst(f_event.SELECTION,this._onSelect);
		}
		
		this._tabIndex=this.tabIndex;
		if (this.f_isDisabled()) {
			var cmp=(link)?link:this;

			cmp.tabIndex=-1;
			cmp.hideFocus=true;
		}
	},
	f_finalize: function() {
//		this._focus=undefined; // String
		this._tabIndex=undefined;
		
		var links=this._links;
		if (links) {
			this._links=undefined;
			
			for(var i=0;i<links.length;i++) {
				var link=links[i];
			
				link.onmousedown=null;
				link.onmouseup=null;
				link.f_link=undefined;	
			}
		}
				
		// Ce n'est plus qu'un indicateur,
		// car c'est soit NULL ou _image !
		this._link=undefined;

		this._hover=undefined;
		this._hoverInstalled=undefined;
		this._mouseDown = undefined;
		this._mouseDown_out = undefined;		
		
		this.f_super(arguments);
	},
	_installListeners: function(tagName) {
		var links=this._links;
		
		var divs=this.getElementsByTagName(tagName);
		for(var i=0;i<divs.length;i++) {
			var div=divs[i];
			
			div.onmousedown=f_customButton._MouseDown;
			div.onmouseup=f_customButton._MouseUp;
			div.f_link=this;
			
			if (!links) {
				links=new Array;
				this._links=links;
			}
			
			links.push(div);
		}
	},
	f_onSelect: function(evt) { 
		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}
	
		return this.f_super(arguments, evt);	
	},
	_onSelect: function() {
		this.checked = false;
		return true;
	},
	_onMouseDown: function() {
		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}
		
//		alert("Down !");
		this.f_updateLastFlatBorder();

		this._mouseDown = true;		
		this._mouseDown_out = false;		
		this._updateImage();
		
		if (!this._focus) {
			this.f_setFocus();
		}
		
		return true;				
	},
	_onMouseUp: function() {
		if (!this._mouseDown) {
			return false;
		}
		
		this._mouseDown_out = undefined;	
		this._mouseDown = undefined;	
		this._updateImage();
		return true;				
	},
	_onMouseOver: function() {
		this.f_updateLastFlatBorder();

		this._hover=true;
		
		this._updateImage();
		return true;
	},
	_onMouseOut: function() {
		this._mouseDown = undefined;		
		this._hover = undefined;
		
		this._updateImage();
		
		return true;
	},
	_onFocus: function() {
		this.f_updateLastFlatBorder();
		
		this._focus=true;
		
		this._updateImage();
		return true;
	},
	_onBlur: function() {
		if (this.f_isFlatTypeBorder()) {
			this._hover = undefined;
		}
		
		this._focus = undefined;		

		this._updateImage();
		return true;
	},
	_updateImage: function() {
		var suffix="";
		var ignoreFlat;
		if (this.f_isDisabled()) {
			suffix="_disabled";

		} else if (this._mouseDown) {
			suffix="_pushed";

		} else if (this.f_isSelected && this.f_isSelected()) {
			suffix="_selected";
			ignoreFlat=suffix;

			if (this._focus) {
				suffix+="_focus";

			} else if (this._hover) {
				suffix+="_hover";	
			}
						
		} else if (this._hover) {
			ignoreFlat=suffix;
			suffix="_hover";

		} else if (this._focus) {
			ignoreFlat=suffix;
			suffix="_focus";
		}
		
		var border=this.f_getBorderComponent();
		if (border) {
			var className=border._className+suffix;
			
			if (this.f_isFlatTypeBorder() && ignoreFlat!==undefined && this!=fa_borderType.GetCurrentBorder()) {
				className=border._className+ignoreFlat;
			}
			
			if (border.className!=className) {
				border.className=className;
			}
		}
	},
	_installHoverFocus: function() {
		if (this._hoverInstalled) {
			return;
		}

		this._hoverInstalled = true;
		this.f_insertEventListenerFirst(f_event.MOUSEOVER,this._onMouseOver);
		this.f_insertEventListenerFirst(f_event.MOUSEOUT,this._onMouseOut);
		this.f_insertEventListenerFirst(f_event.FOCUS,this._onFocus);
		this.f_insertEventListenerFirst(f_event.BLUR,this._onBlur);
	},
	f_setDomEvent: function(type, target) {
		var link=this._link;
		if (link) {
			switch(type) {
			case f_event.BLUR: 
			case f_event.FOCUS: 
				target=link;
				return;
			}
		}
						
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		var link=this._link;
		if (link) {
			switch(type) {
			case f_event.BLUR: 
			case f_event.FOCUS: 
				target=link;
				return;
			}
		}
				
		this.f_super(arguments, type, target);
	},
	fa_updateDisabled: function(disabled) {
		var cmp=(this._link)?this._link:this;

		if (disabled) {
			cmp.tabIndex=-1;
			cmp.hideFocus=true;
			
		} else {
			cmp.tabIndex=this._tabIndex;
			cmp.hideFocus=false;
		}

		if (!this.fa_componentUpdated) {
			return;
		}
		
		this._updateImage();
	},
	fa_updateReadOnly: function() {
	},
	fa_updateValue: function() {
	},
	f_update: function() {
		this._updateImage();
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return void
	 */
	f_setFocus: function() {
		var cmp=this._link;
		if (!cmp) {
			cmp=this;
		}
		
		cmp.focus();
	},
	f_performAccessKey: function(evt) {
		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}

		var ret=this.f_fireEvent(f_event.SELECTION, evt);
		
		this.f_setFocus();
		
		return ret;
	}	
};

new f_class("f_customButton", {
	extend: f_component,
	aspects: [ fa_readOnly, fa_disabled, fa_borderType, fa_value ],
	statics: __statics,
	members: __members
});
