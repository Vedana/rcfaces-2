/*
 * $Id: f_expandBar.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * class f_expandBar
 *
 * @class public f_expandBar extends f_component, fa_disabled, fa_readOnly, fa_collapsed, fa_groupName, fa_overStyleClass, fa_asyncRender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */
 
var __statics = {

	/**
	 * @field private static final String
	 */
	_HEAD_CLASSNAME: "f_expandBar_head",

	/**
	 * @field private static final String
	 */
	_HEAD_ID_SUFFIX: "::head",

	/**
	 * @field private static final String
	 */
	_BODY_ID_SUFFIX: "::body",

	/**
	 * @field private static final String
	 */
	_CONTENT_ID_SUFFIX: "::content",

	/**
	 * @field private static final String
	 */
	_LABEL_ID_SUFFIX: "::label",

	/**
	 * @field private static final String
	 */
	_BUTTON_ID_SUFFIX: "::button",
	
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:expandBar
	 */
	_OnHeadOver: function(evt) {
		var expandBar=this._link;
		
		var cls=f_expandBar._HEAD_CLASSNAME;
		this.className=cls+" "+cls+"_over";
		
		return true;
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:expandBar
	 */
	_OnHeadOut: function(evt) {
		var expandBar=this._link;

		this.className=f_expandBar._HEAD_CLASSNAME;
		
		return true;
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:expandBar
	 */
	_OnHeadClick: function(evt) {
		var expandBar=this._link;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
	
		expandBar.f_fireEvent(f_event.SELECTION, evt);
		
		return f_core.CancelJsEvent(evt);
	}
};

var __members = {
	f_expandBar: function() {
		this.f_super(arguments);

		var doc=this.ownerDocument;

		var userExpandable=f_core.GetBooleanAttributeNS(this,"userExpandable", true);

		var txt=null;
		var head=doc.getElementById(this.id+f_expandBar._HEAD_ID_SUFFIX);
		if (head) {
			this._head=head;
			head._link=this;
			
			if (userExpandable) {
				head.onmouseover=f_expandBar._OnHeadOver;
				head.onmouseout=f_expandBar._OnHeadOut;
			}
					
			var text=doc.getElementById(this.id+f_expandBar._LABEL_ID_SUFFIX);
			if (text) {
				this._text=text;
				text._link=this;
				
				if (userExpandable) {
					text.onclick=f_expandBar._OnHeadClick;
				}
				
				txt=f_core.GetTextNode(text, true);
				if (txt) {
					txt=f_core.DecodeHtml(txt);
				}
			}
		}
		
		var body=doc.getElementById(this.id+f_expandBar._BODY_ID_SUFFIX);
		if (body) {
			this._body=body;
			
			this._content=doc.getElementById(this.id+f_expandBar._CONTENT_ID_SUFFIX);
		}
			
		this._normalText=f_core.GetAttributeNS(this,"text", txt);
		this._collapsedText=f_core.GetAttributeNS(this,"collapsedText", txt);
	
		var groupName=f_core.GetAttributeNS(this,"groupName");
		if (groupName ) {	
			this._groupName=groupName;
		
			this.f_addToGroup(groupName, this.id);
		}
	
		if (userExpandable) {
			this.f_insertEventListenerFirst(f_event.SELECTION, this._onSelect);
		}
	},
	f_finalize: function() {
		// this._normalText=undefined; // String
		// this._collapsedText=undefined; // String
		// this._groupName=undefined; // String
	
		var effect=this._effect;
		if (effect) {
			this._effect=undefined;
		
			// On force la destruction !
			f_classLoader.Destroy(effect);
		}
	
		this._body=undefined;
		
		var content=this._content;
		if (content) {
			this._content=undefined; // HTMLDivElement
			f_core.VerifyProperties(content);
		}
			
		var head=this._head;
		if (head) {
			this._head=undefined; // HtmlLiElement
			
			head.onmouseover=null;
			head.onmouseout=null;
			head._link=undefined; // f_expandBar
			
			f_core.VerifyProperties(head);
		}

		var text=this._text;
		if (text) {
			this._text=undefined; // HtmlLabelElement		
			text._link=undefined; // f_expandBar
			text.onclick=null;

			f_core.VerifyProperties(text);
		}

		var button=this._button;
		if (button) {
			this._button=undefined; // HtmlInputElement
			
			button.onclick=null;
			button.f_link=undefined; // f_expandBar
			
			f_core.VerifyProperties(button);
		}
		
		this.f_super(arguments);
	},
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
		case f_event.BLUR: 
		case f_event.FOCUS: 
		case f_event.KEYDOWN:
		case f_event.KEYUP:
			var link=this.f_getButton();
			
			if (!link || !link.offsetTop) {
				return;
			}
			
			target=link;
			break;
		}
						
		this.f_super(arguments, type, target);
	},
	/**
	 * @method protected
	 */
	f_getButton: function() {
		var button=this._button;
		if (button!==undefined) {
			return button;
		}
		
		button=this.ownerDocument.getElementById(this.id+f_expandBar._BUTTON_ID_SUFFIX);
		if (button) {
			button.f_link=this;
		}
		
		this._button=button;
		return button;
	},
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION:
		case f_event.BLUR:
		case f_event.FOCUS:
		case f_event.KEYDOWN:
		case f_event.KEYUP:
			var link=this._button;
			if (!link) {
				return;
			}
			
			target=link;
			break;
		}
				
		this.f_super(arguments, type, target);
	},
	/** 
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_onSelect: function(event) {
		if (!this._focus)  {
			this.f_setFocus();
		}

		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}
		if (this.f_fireEvent(f_event.EXPAND,event.f_getJsEvent()) === false ){
			return false;
		}
		this.f_setCollapsed(!this.f_isCollapsed());
		return true;
	},	
	
	
	
	/**
	 * @method public
	 * @return void
	 */
	f_setFocus: function() {
		if (!f_core.ForceComponentVisibility(this)) {
			return;
		}
		
		if (this.f_isDisabled()) {
			return;
		}
				
		var cmp=this._button;
		if (!cmp) {
			cmp=this;
		}
		
		if( cmp.visible){ // IE
			f_core.Debug(f_expandBar, "f_setFocus: focus component '"+cmp+"' for expandBar '"+this.id+"'.");
			cmp.focus();
		}
	},
	/**
	 * @method protected
	 * @param f_event evt
	 * @return void
	 */
	f_performAccessKey: function(evt) {
		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}

		var ret=this.f_fireEvent(f_event.SELECTION, evt);
			
		this.f_setFocus();
		
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method protected
	 */
	f_updateStyleClass: function() {
		var over=this.f_isMouseOver();
	
		var suffix="";
		if (this.f_isCollapsed()) {
			suffix+="_collapsed";

		} else if (over) {
			suffix="_over";
		}
	
		var className=this.f_computeStyleClass(suffix);
		
		if (over) {
			var overStyleClass=this.f_getOverStyleClass();
			if (overStyleClass) {
				className+=" "+overStyleClass;
			}
		}	
		
		if (className!=this.className) {
			this.className=className;
		}
	},
	fa_updateCollapsed: function(set) {
		var body=this._body;
		if (!body) {	
			return;
		}
		
		var button=this.f_getButton();
		if (button) {
			var alt;
			if (set) {
				alt=f_resourceBundle.Get(f_expandBar).f_get("EXPAND_BUTTON");
				
			} else {
				alt=f_resourceBundle.Get(f_expandBar).f_get("COLLAPSE_BUTTON");
			}
			
			if (button.alt!=alt) {
				button.alt=alt;
			}
		}
		
		this.f_updateStyleClass();
		
		var effect=this.f_getEffect();
		
		var collapsedText=this._collapsedText;
		if (collapsedText && this._text) {
			if (!set) {
				collapsedText=this._normalText;
			}
			
			f_core.Debug(f_expandBar, "fa_updateCollapsed: Change text to '"+collapsedText+"'.");
			
			f_core.SetTextNode(this._text, collapsedText, this._accessKey);
		}
		
		f_core.Debug(f_expandBar, "fa_updateCollapsed: Call effect '"+effect+"'.");
		
		var suffix="";
		if (effect) {
			effect.f_performEffect(set);
			suffix=false;

		} else if (set) {
			suffix+="_collapsed";
		}
		
		if (suffix!==false) {
			var content=this._content;

			var contentClassName="f_expandBar_content";
			var bodyClassName="f_expandBar_body";
			if (suffix) {
				contentClassName+=" "+contentClassName+"_collapsed";
				bodyClassName+=" "+bodyClassName+"_collapsed";
			}
			
			if (content.className!=contentClassName) {
				content.className=contentClassName;
			}
			if (body.className!=bodyClassName) {
				body.className=bodyClassName;
			}
		}
		
		var groupName=this.f_getGroupName();		
		if (!set && groupName) {
			var p=this;
			
			this.f_mapIntoGroupOfComponents(groupName, function(item) {
				if (item==p) {
					return;
				}
				
				if (item.f_isCollapsed && item.f_isCollapsed()) {
					return;
				}
				
				item.f_setCollapsed(true);
			});
		}
	},
	/**
	 * @method protected
	 * @return f_effect
	 */
	f_getEffect: function() {
		var effect=this._effect;
		if (effect!==undefined) {
			return effect;
		}
		
		var content=this._content;
		var body=this._body;		
		
		effect = f_core.GetAttributeNS(this,"effect");
		if (effect) {
			effect=f_core.CreateEffectByName(effect, content, function(value) {
				
				var suffix="";
				if (value==0) {
					display="none";
					suffix+="_collapsed";
					
				} else if (value!=1) {
					suffix+="_effect";				
				}
				
				var contentClassName="f_expandBar_content";
				var bodyClassName="f_expandBar_body";
				if (suffix) {
					contentClassName+=" "+contentClassName+suffix;
					bodyClassName+=" "+bodyClassName+suffix;
				}
				
				if (content.className!=contentClassName) {
					content.className=contentClassName;
				}
				if (body.className!=bodyClassName) {
					body.className=bodyClassName;
				}				
			});
		}
		if (!effect) {
			effect=null;
		}
		this._effect=effect;
		
		return effect;
	},
	/**
	 * Returns the group name of this expandBar, or <code>null</code> if it is not defined !
	 *
	 * @method public
	 * @return String The group name.
	 */
	f_getGroupName: function() {
		return this._groupName;
	},
	/**
	 * @method public
	 * @param String text Text to change.
	 * @return void
	 */
	f_setText: function(text) {
		f_core.Assert(typeof(text)=="string", "f_expandBar.f_setText: Invalid text parameter ! ('"+text+"')");

		if (this._normalText == text) {
			return;
		}
		this._normalText=text;

		if (!this.f_isCollapsed() && this._collapsedText) {
			var textLabel=this._text;
			if (textLabel) {
				f_core.SetTextNode(textLabel, text, this._accessKey);
			}
		}
				
		this.f_setProperty(f_prop.TEXT, text);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return this._normalText;
	},
	/**
	 * @method public
	 * @param String text Text to change.
	 * @return void
	 */
	f_setCollapsedText: function(text) {
		f_core.Assert(typeof(text)=="string", "f_expandBar.f_setCollapsedText: Invalid text parameter ! ('"+text+"')");

		if (this._collapsedText == text) {
			return;
		}
		this._collapsedText=text;

		if (this.f_isCollapsed()) {
			var textLabel=this._text;
			if (textLabel) {
				var htmlText=f_core.EncodeHtml(text);
				
				f_core.SetTextNode(textLabel, htmlText, this._accessKey);
			}
		}
				
		this.f_setProperty(f_prop.COLLAPSED_TEXT, text);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getCollapsedText: function() {
		return this._collapsedText;
	},
	/**
	 * @method protected
	 * @return Boolean
	 */	 
	f_parentShow: function() {
		this.f_setCollapsed(false);
		
		return this.f_super(arguments);		
	},
	f_documentComplete: function() {
		var sh=this.style.height;
		if (sh && sh.indexOf("px")>0) {
			this.f_updateHeight(parseInt(sh, 10));
		}
	},
	f_updateHeight: function(height) {
		f_core.Assert(typeof (height) == "number",
				"f_expandBar.f_setHeight: height parameter must be a number ! ("
						+ height + ")");

		this.style.height = height + "px";
		
		var border=f_core.ComputeContentBoxBorderLength(this, "top", "bottom");
		height-=border;
		
		var head=this._head;
		if (head) {
			var hh=head.offsetHeight;
			height-=hh;
		}
		
		var body=this._body;
		if (body) {
			body.style.height=height + "px";
			
			var content=this._content;
			if (content && content!=body) {
				var borderBody=f_core.ComputeContentBoxBorderLength(body, "top", "bottom");
				height-=borderBody;
			
				content.style.height=height+"px";
			}
		}
	}
};

new f_class("f_expandBar", {
	extend: f_component,
	aspects: [ fa_disabled, fa_readOnly, fa_collapsed, fa_groupName, fa_overStyleClass, fa_asyncRender ],
	statics: __statics,
	members: __members
});
