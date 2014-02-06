/*
* * $Id: f_component.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */
 
/** 
 * f_component class
 *
 * @class f_component extends f_eventTarget, fa_serializable, fa_clientData
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @author Joel Merlin
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
 
var __statics = {
	
	/**
	 * @field public static final Number
	 */
	HIDDEN_MODE_SERVER: 1,

	/**
	 * @field public static final Number 
	 */
	HIDDEN_MODE_PHANTOM: 2,

	/**
	 * @field public static final Number
	 */
	HIDDEN_MODE_IGNORE: 4,

	/**
	 * @field public static final Number
	 */
	DEFAULT_HIDDEN_MODE: 4,
	
	/**
	 * @method static hidden
	 * @param HTMLElement parent
	 * @param String label
	 * @param optional String accessKey
	 * @return optional Boolean removeText
	 * @return HTMLElement Underlined zone component
	 */
	AddLabelWithAccessKey: function(parent, label, accessKey, removeText) {
		f_core.Assert(parent.nodeType==f_core.ELEMENT_NODE, "Invalid parent parameter '"+parent+"'.");
		f_core.Assert(typeof(label)=="string", "Invalid label parameter '"+label+"'.");
		
		if (removeText) {			
			while(parent.firstChild) {
				parent.removeChild(parent.firstChild);
			}
		}

		var doc=parent.ownerDocument;
	
		if (!accessKey || accessKey.length!=1) {
			f_core.AppendChild(parent, doc.createTextNode(label));
			
			return null;
		}
		accessKey=accessKey.toUpperCase();
		
		var lab=label.toUpperCase();
		
		var idx=lab.indexOf(accessKey);
		
		if (idx<0) {
			f_core.AppendChild(parent, doc.createTextNode(label));
	
			return null;
		}
		
		var fragment=parent.ownerDocument.createDocumentFragment();
		
		if (idx) {
			f_core.AppendChild(fragment, doc.createTextNode(label.substring(0, idx)));
		}

		var sub=f_core.CreateElement(fragment, "u", {
			className: "f_accessKey",
			textnode: label.substring(idx, idx+1)
		});
		
		if (idx+1<lab.length) {
			var l=label.substring(idx+1, lab.length);

			f_core.AppendChild(fragment, doc.createTextNode(l));
		}
		
		f_core.AppendChild(parent, fragment);
		
		return sub;
	},
	
	/**
	 * @method protected static final
	 */
	GetDefaultHiddenMode: function() {
		return f_component.DEFAULT_HIDDEN_MODE;
	}
};

var __members = {

	/**
	 * @method hidden
	 * @return void
	 */
	f_component: function() {
		this.f_super(arguments);
		this.fa_componentUpdated = false;
		
		var accessKey=f_core.GetAttributeNS(this,"accessKey");
		if (!accessKey) {
			accessKey=this.accessKey;
		}
		if (accessKey) {
			this._accessKey=accessKey;
		}
		
		if (accessKey) {
			f_key.AddKeyHandler(null, accessKey, this, this.f_performAccessKey);
		}
	},
	/*
	f_finalize: function() {
		this._hiddenMode = undefined; // string 
		this._helpMessage = undefined; // string
		this._helpURL=undefined; // string
		this._accessKey=undefined; // string
		this.fa_componentUpdated = undefined; // boolean
		this._oldDisplay = undefined; // string

		this._styleClass = undefined; // String;
		this._computedStyleClass=undefined; // String

		this.f_super(arguments);		
	},
	*/

	/**
	 * Returns the idenfiant of the component.
	 * 
	 * @method public
	 * @return String Identifier
	 */
	f_getId: function() {
		return this.id;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getX: function() {
		return this._getSize(this.style.left);
	},
	
	/**
	 * @method private
	 * @return Number
	 */
	_getSize: function(size) {
		if (!size) {
			return undefined;
		}
		
		return parseInt(size, 10);
	},
	/**
	 * @method public
	 * @param Number x
	 * @return void
	 */
	f_setX: function(x) {
		f_core.Assert(typeof(x)=="number" || x===undefined, "f_component.f_setX: x parameter must be a number ! ("+x+")");
		

		if (y===undefined) {
			this.style.left="auto";
		} else {
			this.style.left = x+"px";
		}
		this.f_setProperty(f_prop.X,x);
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getY: function() {
		return this._getSize(this.style.top);
	},
	/**
	 * @method public
	 * @param Number y
	 * @return void
	 */
	f_setY: function(y) {
		f_core.Assert(typeof(y)=="number" || y===undefined, "f_component.f_setY: y parameter must be a number ! ("+y+")");

		if (y===undefined) {
			this.style.top="auto";
		} else {
			this.style.top = y+"px";
		}
		this.f_setProperty(f_prop.Y,y);
	},
	/**
	 * Returns the width of the component.
	 *
	 * @method public
	 * @return Number
	 */
	f_getWidth: function() {
		return this._getSize(this.style.width);
	},
	/**
	 * Set the width of the component
	 *
	 * @method public
	 * @param Number width Width of the component.  (undefined value is supported)
	 * @param hidden Boolean persistence
	 * @return void
	 */
	f_setWidth: function(width, persistence) {
		f_core.Assert(typeof(width)=="number" || width===undefined, "f_component.f_setWidth: w parameter must be a number ! ("+width+")");
		
		this.f_updateWidth(width);
		
		if (persistence!==false) {
			this.f_setProperty(f_prop.WIDTH, width);
		}
	},
	/**
	 * Update the width of the component
	 *
	 * @method protected
	 * @param Number width Width of the component.
	 * @return void
	 */
	f_updateWidth: function(width) {
		if (width===undefined) {
			this.style.width="auto";
			return;
		}
		this.style.width = width+"px";		
	},
	/**
	 * Returns the height of the component.
	 *
	 * @method public
	 * @return Number
	 */
	f_getHeight: function() {
		return this._getSize(this.style.height);
	},
	/**
	 * Set the height of the component.
	 * 
	 * @method public
	 * @param Number height Height of the component.
	 * @param hidden Boolean persistence
	 * @return void
	 */
	f_setHeight: function(height, persistence) {
		f_core.Assert(typeof(height)=="number" || height===undefined, "f_component.f_setHeight: h parameter must be a number ! ("+height+")");

		this.f_updateHeight(height);

		if (persistence!==false) {
			this.f_setProperty(f_prop.HEIGHT, height);
		}
	},
	/**
	 * Update the height of the component
	 *
	 * @method protected
	 * @param Number width Height of the component.
	 * @return void
	 */
	f_updateHeight: function(height) {
		if (height===undefined) {
			this.style.height="auto";
			return;
		}
		this.style.height = height+"px";		
	},
	/**
	 *  Returns the background color of the component.
	 * 
	 * @method public
	 * @return String
	 */
	f_getBackgroundColor: function() {
		return this.style.backgroundColor;
	},
	/**
	 * Set the background color of the component.
	 * 
	 * @method public
	 * @param String color
	 * @return void
	 */
	f_setBackgroundColor: function(color) {
		f_core.Assert(color===null || typeof(color)=="string", "f_component.f_setBackgroundColor: Background color parameter must be a string ! ("+color+")");

		this.style.backgroundColor = color;
		this.f_setProperty(f_prop.BACKGROUND,color);
	},
	/**
	 * Returns the foreground color of the component.
	 *
	 * @method public
	 * @return String
	 */
	f_getForegroundColor: function() {
		return this.style.color;
	},
	/**
	 * Set the foreground color of the component.
	 *
	 * @method public
	 * @param String color
	 * @return void
	 */
	f_setForegroundColor: function(color) {
		f_core.Assert(color===null || typeof(color)=="string", "f_component.f_setForegroundColor: Foreground color parameter must be a string ! ("+color+")");

		this.style.color = color;
		this.f_setProperty(f_prop.FOREGROUND,color);
	},
	/**
	 * Returns the receiver's tool tip text, or <code>null</code> if it has not been set.
	 * 
	 * @method public
	 * @return String the receiver's tool tip text
	 */
	f_getToolTipText: function() {
		return this.title;
	},
	/**
	 * Sets the receiver's tool tip text to the argument, 
	 * which may be <code>null</code> indicating that no tool tip text should be shown.
	 
	 * @method public
	 * @param String title the new tool tip text (or <code>null</code>)
	 * @return void
	 */
	f_setToolTipText: function(title) {
		f_core.Assert(title===null || typeof(title)=="string", "f_component.f_setToolTipText: Title parameter must be a string ! ("+title+")");

		this.title = title;
		this.f_setProperty(f_prop.TOOLTIP,title);
	},
	/**
	 * Returns <code>true</code> if the receiver is visible, and <code>false</code> otherwise.
	 * <br>
	 * If one of the receiver's ancestors is not visible or some other condition makes the receiver not visible,
	 * this method may still indicate that it is considered visible even though it may not actually be showing.
	 *
	 * @method public final
	 * @return Boolean the receiver's visibility state
	 */
	f_getVisible: function() {
		var visible=this._visible;
		if (visible!==undefined) {
			return visible;
		}
		
		var hiddenMode=this.f_getHiddenMode();
		if (hiddenMode==f_component.HIDDEN_MODE_PHANTOM) {
			visible=(this.style.visibility!="hidden");

		} else {
			visible=(this.style.display!="none");
		}
		
		this._visible=visible;

		return visible;
	},
	/**
	 * Returns <code>true</code> if the receiver is visible and all ancestors up to and including the receiver's nearest ancestor document are visible.
	 * Otherwise, <code>false</code> is returned.
	 *
	 * @method public final
	 * @return Boolean the receiver's visibility state
	 */
	f_isVisible: function() {
		if (!this.f_getVisible()) {
			return false;
		}
		
		return f_core.IsComponentVisible(this);
	},
	/**
	 * Marks the receiver as visible if the argument is true, and marks it invisible otherwise.
	 * <br>
	 * If one of the receiver's ancestors is not visible or some other condition makes the receiver not visible, 
	 * marking it visible may not actually cause it to be displayed.
	 *
	 * @method public
	 * @param Boolean visible the new visibility state
	 * @return void
	 */
	f_setVisible: function(visible) {
		f_core.Assert(typeof(visible)=="boolean", "f_component.f_setVisible: Visible parameter must be a boolean ! ("+visible+")");

		visible=!!visible;

		if (visible==this.f_getVisible()) {
			return;
		}
		
		this._visible=visible;
		
		this.f_updateVisibility(visible);

		if (visible===true) {
			this.f_getClass().f_getClassLoader().fireVisibleEvent(this);
		}

		this.f_setProperty(f_prop.VISIBLE, visible);		
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateVisibility: function(visible) {
		var style=this.style;

		var hiddenMode=this.f_getHiddenMode();
		if (hiddenMode==f_component.HIDDEN_MODE_PHANTOM) {
			style.visibility=(visible)?"inherit":"hidden";
			return;
		}

		// Mode IGNORE et SERVEUR
		if (!visible) {
			if (style.display != "none") {
				this._oldDisplay = style.display;
				style.display="none";
			}
			return;
		}
			
		if (this._oldDisplay) {
			style.display = this._oldDisplay;
			return;
		}
		
		var display=f_core.GetDefaultDisplayMode(this);
		style.display = display;
		this._oldDisplay = display;
	},
	/**
	 * @method public
	 * @param String mode
	 * @return void
	 * @see f_component#DEFAULT_HIDDEN_MODE f_component.DEFAULT_HIDDEN_MODE
	 * @see f_component#HIDDEN_MODE_IGNORE f_component.HIDDEN_MODE_IGNORE
	 * @see f_component#HIDDEN_MODE_PHANTOM f_component.HIDDEN_MODE_PHANTOM
	 * @see f_component#HIDDEN_MODE_SERVER f_component.HIDDEN_MODE_SERVER
	 */
	f_setHiddenMode: function(mode) {
		f_core.Assert(typeof(mode)=="number", "f_component.f_setHiddenMode: Hidden mode parameter must be a number ! ("+mode+")");

		if (mode==this.f_getHiddenMode()) {
			return;
		}

		this._hiddenMode = mode;
	},
	/**
	 * @method public
	 * @return Number
	 * @see f_component#DEFAULT_HIDDEN_MODE f_component.DEFAULT_HIDDEN_MODE
	 * @see f_component#HIDDEN_MODE_IGNORE f_component.HIDDEN_MODE_IGNORE
	 * @see f_component#HIDDEN_MODE_PHANTOM f_component.HIDDEN_MODE_PHANTOM
	 * @see f_component#HIDDEN_MODE_SERVER f_component.HIDDEN_MODE_SERVER
	 */
	f_getHiddenMode: function() {
		var hiddenMode=this._hiddenMode;
		if (hiddenMode!==undefined) {
			return hiddenMode;
		}

		hiddenMode=f_core.GetAttributeNS(this,"hiddenMode");
		if (hiddenMode) {
			hiddenMode=parseInt(hiddenMode, 10);

		} else {
			hiddenMode=f_component.GetDefaultHiddenMode();
		}
		
		this._hiddenMode=hiddenMode;

		return hiddenMode;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getHelpURL: function() {
		var helpURL=this._helpURL;
		if (helpURL!==undefined) {
			return helpURL;
		}
		
		var helpURL=f_core.GetAttributeNS(this,"helpURL", null);

		this._helpURL=helpURL;
		
		return helpURL;
	},
	/**
	 * @method public
	 * @param String url
	 * @return void
	 */
	f_setHelpURL: function(url) {
		f_core.Assert(url===null || typeof(url)=="string", "f_component.f_setHelpURL: Help URL parameter must be a string ! ("+url+")");

		if (url==this.f_getHelpURL()) {
			return;
		}
		
		this._helpURL = url;
	
		/*
		if (this._helpURLSet) {
			return;
		}
		this._helpURLSet=true;
		
		var f_help=this.f_getClassLoader().getClass("f_help");
		if (!f_help) {
			return;
		}

		f_help.Install();
		this.f_insertEventListenerFirst(f_event.FOCUS, f_help._OnFocus);
		this.f_insertEventListenerFirst(f_event.BLUR, f_help._OnBlur);
		*/
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getHelpMessage: function() {
		var helpMessage=this._helpMessage;
		if (helpMessage!==undefined) {
			return helpMessage;
		}
		
		helpMessage=f_core.GetAttributeNS(this,"helpMessage", null);
		this._helpMessage=helpMessage;		
		
		return helpMessage;
	},
	/**
	 * @method public
	 * @param String msg
	 * @return void
	 */
	f_setHelpMessage: function(msg) {
		f_core.Assert(msg===null || typeof(msg)=="string", "f_component.f_setHelpMessage: Message parameter must be a string ! ("+msg+")");

		var helpMessage=this._helpMessage;
		if (helpMessage==msg) {
			return;
		}

		this._helpMessage = msg;

/*
		if (this._helpMessageSet) {
			return;
		}
		this._helpMessageSet=true;

		var f_help=this.f_getClassLoader().getClass("f_help");
		if (!f_help) {
			return;
		}
		
		f_help.SetHelpMessageZone();
		this.f_addEventListener(f_event.MOUSEOVER,f_help.OnShowHelpMessage);
		this.f_addEventListener(f_event.MOUSEOUT,f_help.OnHideHelpMessage);
		this.f_addEventListener(f_event.FOCUS,f_help.OnShowHelpMessage);
		this.f_addEventListener(f_event.BLUR,f_help.OnHideHelpMessage);
		*/
	},
	/**
	 * @method protected
	 * @param f_event evt
	 * @return void
	 */
	f_performAccessKey: f_key.DefaultAccessKey,
	/**
	 * @method public
	 * @return void
	 */
	f_setFocus: function() {
		f_core.Assert(false, "f_component.f_setFocus: Focus method not implemented !");
	},
	/**
	 * @method protected final
	 * @return String
	 */
	f_getStyleClass: function() {
		var styleClass=this._styleClass;
		if (styleClass!==undefined) {
			return styleClass;
		}
		
		styleClass=f_core.GetAttributeNS(this,"styleClass");
		if (!styleClass) {
			styleClass=null;
		}
		
		this._styleClass=styleClass;
		
		return styleClass;
	},
	/** 
	 * @method protected
	 * @return String
	 */
	f_getMainStyleClass: function() {
		return this.f_getClass().f_getName();
	},
	/** 
	 * @method protected final
	 * @param optional String suffix
	 * @return String
	 */
	f_computeStyleClass: function(suffix) {				
		if (suffix) {
			var c=[];
			// Un suffix, pas de cache !
			var mainStyleClass=this.f_getMainStyleClass();
			
			var ss=mainStyleClass.split(" ");
			for(var i=0;i<ss.length;i++) {
				var clazz=ss[i];
				
				c.push(clazz);
				c.push(clazz+suffix);
			}
			
			var styleClass=this.f_getStyleClass();
			if (styleClass) {
				var ss=styleClass.split(" ");
				for(var i=0;i<ss.length;i++) {
					var clazz=ss[i];
					
					c.push(clazz);
					c.push(clazz+suffix);
				}
			}
			
			return c.join(" ");	
		}

		var computedStyleClass=this._computedStyleClass;
		if (computedStyleClass===undefined) {
			computedStyleClass=this.f_getMainStyleClass();
			
			var styleClass=this.f_getStyleClass();
			if (styleClass) {
				computedStyleClass+=" "+styleClass;
			}
			
			this._computedStyleClass=computedStyleClass;
		}
		
		return computedStyleClass;
	},
	/**
	 * @method public
	 * @param optional Boolean scroll Scroll into view to show the component.
	 *		(<code>true</code> align on top, <code>false</code> align on bottom)
	 * @return Boolean if the component can be shown.
	 */	 
	f_show: function(scroll) {
		if (!this.f_parentShow()) {
			f_core.Debug(f_component, "f_show: Show component '"+this.id+"' returns false");
			return false;
		}
		
		if (scroll!==undefined) {
			this.scrollIntoView(scroll);
		}
		
		return true;
	},
	/**
	 * @method protected
	 * @return Boolean
	 */	 
	f_parentShow: function() {
		var parent=f_core.GetParentComponent(this);
		if (!parent) {
			return true;
		}
		
		return parent.f_parentShow();
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getAccessKey: function() {
		return this._accessKey;
	},
	/**
	 * @method public
	 * @return HTMLElement
	 */
	f_getParent: function() {
		var parent = this.parentNode;
		if (!parent) {
			return parent;
		}		
		
		var parentComponent = this.f_getClass().f_getClassLoader().f_init(parent, true, true);
		
		return parentComponent;
	},
	/**
	 * @method public
	 * @return f_component
	 */
	f_getParentComponent: function() {
		return f_core.GetParentComponent(this);
	},
	/**
	 *
	 *
	 * @method protected
	 * @return void
	 */
	f_documentComplete: function() {
	},
	/**
	 *
	 *
	 * @method protected
	 * @return void
	 */
	f_update: function(set) {
		this.fa_componentUpdated = (set===undefined)? true:set;		
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_completeComponent: function() {
		
		if (f_class.PROFILE_COMPONENT) {
			f_core.Profile(false, "f_component.f_completeComponent("+this.id+" / "+this._kclass._name+")");
		}

	 	try {
	 		this.f_update(true);
	 		
	 	} catch (x) {
		// 	alert(x);

	 		f_core.Error(f_component, "f_completeComponent: Call of f_update throws exception ! (#"+this.id+"."+this.className+") class='"+this._kclass._name+"'", x);	 		
	 	}
		
		if (f_class.PROFILE_COMPONENT) {
			f_core.Profile(true, "f_class.f_completeComponent("+this.id+" / "+this._kclass._name+")");
		}

		if (!this._hasInitListeners) {
			return;
		}
		this._hasInitListeners=undefined;
		
		//this.f_fireEvent(f_event.INIT);
		
		this.f_getClass().f_getClassLoader().f_fireInitListener(this);
	},
	f_serialize: function() {
		f_core.Assert(this.fa_componentUpdated, "f_component.f_serialize: Method fa_componentUpdated not called for component '"+this.id+"/"+this._kclass+"'.");
	},
	
	/**
     * <p>Search for and return the {@link f_component} with an <code>id</code>
     * that matches the specified search expression (if any), according to the
     * algorithm described below.</p>
     *
     * <p>Component identifiers are required to be unique within the scope of
     * the closest ancestor {@link fa_namingContainer} that encloses this
     * component (which might be this component itself).  If there are no
     * {@link fa_namingContainer} components in the ancestry of this component,
     * the root component in the tree is treated as if it were a
     * {@link fa_namingContainer}, whether or not its class actually implements
     * the {@link fa_namingContainer} interface.</p>
     *
     * <p>A <em>search expression</em> consists of either an
     * identifier (which is matched exactly against the <code>id</code>
     * property of a {@link f_component}, or a series of such identifiers
     * linked by the {@link fa_namingContainer#SeparatorChar} character value.
     * The search algorithm operates as follows:</p>
     * <ul>
     * <li>Identify the {@link f_component} that will be the base for searching,
     *     by stopping as soon as one of the following conditions is met:
     *     <ul>
     *     <li>If the search expression begins with the the separator character
     *         (called an "absolute" search expression),
     *         the base will be the root {@link f_component} of the component
     *         tree.  The leading separator character will be stripped off,
     *         and the remainder of the search expression will be treated as
     *         a "relative" search expression as described below.</li>
     *     <li>Otherwise, if this {@link f_component} is a
     *         {@link fa_namingContainer} it will serve as the basis.</li>
     *     <li>Otherwise, search up the parents of this component.  If
     *         a {@link fa_namingContainer} is encountered, it will be the base.
     *         </li>
     *     <li>Otherwise (if no {@link fa_namingContainer} is encountered)
     *         the root {@link f_component} will be the base.</li>
     *     </ul></li>
     * <li>The search expression (possibly modified in the previous step) is now
     *     a "relative" search expression that will be used to locate the
     *     component (if any) that has an <code>id</code> that matches, within
     *     the scope of the base component.  The match is performed as follows:
     *     <ul>
     *     <li>If the search expression is a simple identifier, this value is
     *         compared to the <code>id</code> property, and then recursively
     *         through the facets and children of the base {@link f_component}
     *         (except that if a descendant {@link fa_namingContainer} is found,
     *         its own facets and children are not searched).</li>
     *     <li>If the search expression includes more than one identifier
     *         separated by the separator character, the first identifier is
     *         used to locate a {@link fa_namingContainer} by the rules in the
     *         previous bullet point.  Then, the <code>findComponent()</code>
     *         method of this {@link fa_namingContainer} will be called, passing
     *         the remainder of the search expression.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @method public 
     *
 	 * @param String... id Identifier of component.
     *
     * @return HTMLElement the found {@link f_component}, or <code>null</code>
     *  if the component was not found.
     *
     * @exception Error if an intermediate identifier
     *  in a search expression identifies a {@link f_component} that is
     *  not a {@link f_namingContainer}
     */
	f_findComponent: function(id) {
		return fa_namingContainer.FindComponents(this, arguments);
	},
	/**
	 * Find the sibling component.
	 *
	 * @method public
 	 * @param String... id Identifier of component.
     * @return HTMLElement the found {@link f_component}, or <code>null</code>
     *  if the component was not found.
     * @see #f_findComponent
	 */
	f_findSiblingComponent: function(id) {
		return fa_namingContainer.FindSiblingComponents(this, arguments);
	},	
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		return null;	
	},
	/**
	 * @method public
	 * @return Document
	 */
	f_getDocument: function() {
		return this.f_getClassLoader().f_getDocument();
	},
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		var s="[f_component";
		
		if (this.id) {
			s+=" id=\""+this.id+"\"";
		}

		var kclazz=this._kclass;
		if (kclazz) {
			s+=" class=\""+kclazz.f_getName()+"\"";
			
		} else {
			s+=" class=*undefined*";
		}
		
		if (this.tagName) {
			s+=" tag="+this.tagName.toLowerCase();
		}
		
		if (this.className) {
			s+=" styleClass=\""+this.className+"\"";
		}
		
		return s+"]";
	}
};

new f_class("f_component", {
	extend: f_eventTarget,
	aspects: [fa_serializable, fa_clientData],
	statics: __statics,
	members: __members
});
