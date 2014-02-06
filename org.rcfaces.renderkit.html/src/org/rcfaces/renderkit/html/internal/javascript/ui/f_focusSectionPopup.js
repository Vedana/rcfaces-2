/*
 * $Id: f_focusSectionPopup.js,v 1.2 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * Focus manager class.
 * 
 * @class public f_focusSectionPopup extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/12/11 10:19:48 $
 */

var __statics = {

	/**
	 * @field private static
	 */
	_PopupSection : undefined,
		
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer : function() {
		f_focusSectionPopup._PopupSection=undefined; // Element
	},
	/**
	 * @method public static
	 * @param f_event
	 *            event
	 * @param hidden
	 *            HTMLElement fromComponent
	 * @return Boolean
	 */
	ShowFocusSection: function(event) {
		if (f_focusSectionPopup._PopupSection && f_focusSectionPopup._PopupSection.parentNode) {
			return true;
		}
		
		var focusComponent = f_focusManager.ComputeFocusComponent(event);
		if (!focusComponent) {
			return true;
		}
		
		var form=f_core.GetParentForm(focusComponent);
		if (!form) {
			return true;
		}

		var prevH=null;
		for(var c=focusComponent;c;) {
			if (c.nodeType==f_core.ELEMENT_NODE) {
				var tname=c.tagName;
				if (tname && tname.charAt(0).toUpperCase()=="H") {
					prevH=c;
					break;
				}
			}
			if (c.previousSibling) {
				c=c.previousSibling;
				continue;
			}
			c=c.parentNode;
			if (!c || c.nodeType!=f_core.ELEMENT_NODE) {
				break;
			}
		}
		
		var doc=focusComponent.ownerDocument;
		
		var popup=f_core.CreateElement(doc.body, "div", {
			classname: "f_focusSection"
		});
		f_focusSectionPopup._PopupSection=popup;
		
		var span=f_core.CreateElement(popup, "div", {
			id: "f_focusSelection::span",
			className: "f_focusSection_title",
			textNode: "Arborescence de la page",
			role: "description"
		});
		
		var ul=f_core.CreateElement(popup, "ul", {
			role: "navigation"
		});
		
		var tree=f_focusSectionPopup._BuildHeadingsTree(form);
		tree._element=ul;
		
		var currentIndex=-1;
			
		var nodeIdx=0;
		var stack=tree._children.concat([]);
		for(;stack.length;) {
			var n=stack.shift();
			
			var li=f_core.CreateElement(n._parent._element, "li");			
			
			var txt=n._txt;
			if (!txt) {
				txt="Titre non spécifié";
			}
			
			var s="";
			for(var sn=n;sn._parent;sn=sn._parent) {
				s=String(sn._parent._children.indexOf(sn)+1)+(s?".":"")+s;
			}

			var h=f_core.CreateElement(li, "a", {
				href: f_core.CreateJavaScriptVoid0(),
				id: "f_focusSelection::node"+(nodeIdx++),
				role: "presentation",
				_node: n
			}, "LABEL", {
				className: "f_section_l"+n._level,
				textNode: "Section "+s+": "+txt			
			});
			
			if (!n._focusables.length) {
				fa_audioDescription.AppendAudioDescription(h, "Aucun champ saisissable", "focus");
				h.className="f_focusSelection_noFocus";
			}
			
			if (n._children.length) {
				var ul=f_core.CreateElement(li, "ul");
				n._element=ul;
				
				stack.unshift.apply(stack, n._children);
			}
			
			if (prevH==n._component) {
				currentIndex=nodeIdx-1; // Le suivant !
			}
		}
		
		if (!nodeIdx) {
			return;
		}
		
		var firstNode=doc.getElementById("f_focusSelection::node0");
		
		var oldTarget=null;
		var oldTargetOutline=null;
		//f_core.SetFocus(firstNode, true);
		
		var clbks={
			close: function(evt) {
				if (oldTarget) {
					if (oldTargetOutline!==undefined) {
						oldTarget.style.outline=oldTargetOutline;
						oldTargetOutline=undefined;
					}
					oldTarget=undefined;
				}

				f_focusSectionPopup._PopupSection=undefined;
				
				f_popup.UnregisterWindowClick(popup);		

				var focs=undefined;
				var firstNode=doc.getElementById("f_focusSelection::node"+currentIndex);
				if (firstNode && firstNode._node) {
					focs=firstNode._node._focusables;
				}
				
				popup.parentNode.removeChild(popup);
				
				if (focs && focs.length) {
					var elt=doc.getElementById(focs[0]);
					if (elt) {
						f_core.SetFocus(elt, true);
					}
				}
			},
			
			/**
			 * @method public
			 */
			exit: function(evt) {
				f_core.Debug(f_focusSectionPopup, "_OpenPopup.exit: evt: "+evt);

				var target = evt.target || evt.srcElement;
				
				if (false) {
					console.log("Event=", evt, " target=",target);
				}
				
				if (target.nodeType==f_core.ELEMENT_NODE) {
					return;
				}
				
				clbks.close();
			},
			/**
			 * @method public
			 */
			keyDown: function(evt) {
				f_core.Debug(f_focusSectionPopup, "_OpenPopup.keyDown: popup keyDown: "+evt.keyCode);
				
				if (!evt.ctrlKey) {
					return clbks.close(evt);
				}
				
				switch(evt.keyCode) {
				case f_key.VK_TAB:
			 		return true;
				}
				
				return false;
			},
			/**
			 * @method public
			 */
			keyUp: function(evt) {
				f_core.Debug(f_focusSectionPopup, "_OpenPopup.keyUp: popup keyUp: "+evt.keyCode);
				
				if (!evt.ctrlKey) {
					return clbks.close(evt);
				}
				
				switch(evt.keyCode) {
				case f_key.VK_TAB:
			 		return true;
				}
				return false;
			},
			/**
			 * @method public
			 */
			keyPress: function(evt) {
				f_core.Debug(f_focusSectionPopup, "_OpenPopup.keyPress: popup keyPress: "+evt.keyCode);
				
				if (oldTarget) {
					if (oldTargetOutline!==undefined) {
						oldTarget.style.outline=oldTargetOutline;
						oldTargetOutline=undefined;
					}
					oldTarget=undefined;
				}
				
				if (evt.shiftKey) {
					currentIndex=(currentIndex+nodeIdx-1) % nodeIdx;
				} else {
					currentIndex=(currentIndex+1) % nodeIdx;
				}

				var firstNode=doc.getElementById("f_focusSelection::node"+currentIndex);
				f_core.SetFocus(firstNode, true);		
				
				var fs=firstNode._node._focusables;
				if (fs && fs.length) {
					oldTarget=doc.getElementById(fs[0]);
					if (oldTarget) {
						oldTargetOutline=oldTarget.style.outline;
						oldTarget.style.outline="red 3px solid";
					}
				}
				
				return f_core.CancelJsEvent(evt);
			}
		};
		
		if (f_popup.RegisterWindowClick(clbks, popup, popup)==false) {			
			f_core.Debug(f_focusSectionPopup, "_OpenPopup: Register refused to open the popup of focusManagerSection='"+popup+"'.");
			return true;
		}
		
		return false;
	},
	/**
	 * @method private statoc
	 * @param Element from
	 * @return Object
	 */
	_BuildHeadingsTree: function(from) {
		var root={
				_children: [],
				_focusables: [],
				_parent: null,
				_level: 0
		};

		var focusableTags=new RegExp(f_core._FOCUSABLE_TAGS, "i");

		var levels=[ root ];
		
		for(var c=from;c;) {
			var visible=false;
			if (c.nodeType==f_core.ELEMENT_NODE) {
				visible=true;
				
				var vis=f_core.GetCurrentStyleProperty(c, "visibility");
				if (vis && vis.toLowerCase()=="hidden") {
					visible=false;
				}  else {
					vis=f_core.GetCurrentStyleProperty(c, "display");
					if (vis && vis.toLowerCase()=="none") {
						visible=false;
					}
				}
			}
			
			if (visible) {
				var tagName=c.tagName.toUpperCase();
				if (tagName.charAt(0)=="H") {					
					var level=parseInt(tagName.substring(1), 10);
					
					for (;level<levels.length;) {
						var lv=levels.pop();
						if (!lv._children.length && !lv._focusables.length) {
							levels[levels.length-1]._children.pop();
						}
					}
				
					for(;level>=levels.length;) {						
						var n={
								_children: [],
								_focusables: [],
								_parent: levels[levels.length-1],
								_level: level,
								_component: c
						};
						levels[levels.length-1]._children.push(n);
						levels.push(n);
					}	
					
					var txt=f_core.GetTextNode(c);
					levels[levels.length-1]._txt=txt;
				}
	
				if ((typeof(c.tabIndex)=="number" && c.tabIndex>=0) || (focusableTags.test(tagName) && (!c.tabIndex || c.tabIndex>=0))) {
					if (tagName!="INPUT" || !c.type || c.type.toUpperCase()!="HIDDEN") {
						var id=c.id;
						if (!id) {
							id=f_core.AllocateAnoIdentifier();
							c.id=id;
						}
						
						var idx = id.lastIndexOf("::");
						if (idx > 0) {
							//id=id.substring(0, idx);
						}
						
						levels[levels.length-1]._focusables.push(id);
					}
				}
			
				if (c.firstChild) {
					c=c.firstChild;
					continue;
				}
			}
			
			if (c.nextSibling) {
				c=c.nextSibling;
				continue;
			}
			
			if (c==from) {
				break;
			}
			
			for(;;) {
				var p=c.parentNode;
				if (!p || p.nodeType!=f_core.ELEMENT_NODE || p==from) {
					c=null;
					break;
				}
				
				if (p.nextSibling) {
					c=p.nextSibling;
					break;
				}
				c=p;
			}
		}
		
		for (;levels.length;) {
			var lv=levels.pop();
			if (!lv._children.length && !lv._focusables.length) {
				levels[levels.length-1]._children.pop();
			}
		}
		
		return root;
	}
};

var __members = {

	f_focusSectionPopup : function() {
		this.f_super(arguments);

	}
};

new f_class("f_focusSectionPopup", {
	extend : f_object,
	statics : __statics,
	members : __members
});
