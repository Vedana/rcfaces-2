/*
 * $Id: f_layoutManager.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Layout manager
 *
 * @aspect public f_layoutManager extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	Get: function() {
		var instance=f_layoutManager._Instance;
		if (instance) {
			return instance;
		}
		
		instance = new f_layoutManager();
		f_layoutManager._Instance=instance;
		
		return instance;
	},
	DocumentComplete: function () {
		var layoutManager = f_layoutManager.Get();
		
		if (!layoutManager) {
			return;
		}
		
		layoutManager._install(true);
	
		layoutManager.f_layoutAllComponents();
	},

	/**
	 * @method private static
	 * @param Element component
	 * @param String name
	 */
	_GetNumberAttributeInCache: function(component, name) {
		var keyName="_"+name;
		var n=component[keyName];
		if (n!==undefined) {
			return n;
		}
		
		n = f_core.GetNumberAttributeNS(component, name, 0);
		component[keyName]=n;
		
		return n;
	}

};

var __members = {
	
	f_layoutManager: function() {
		this._onLayoutIds=new Array();
	},
	f_finalize: function() {

		this._uninstall();

//		this._onLayoutIds = undefined; // List<String>
//		this._installed = undefined; // Boolean
//		this._documentComplete = undefined; // Boolean
		
		this._layoutFunc=undefined; // Function
		
//		this.f_super(arguments); // Y a pas d'heritage
	},
	/**
	 * @method public
	 * @param Element component
	 * @return Boolean
	 */
	f_removeComponent: function(component) {
		var onLayoutIds=this._onLayoutIds;	
		if (!onLayoutIds) {
			return false;
		}
		
		return onLayoutIds.f_removeElement(component.id);
	},
	/**
	 * @method public
	 * @param Element component
	 * @return Boolean
	 */
	f_addComponent: function(component) {
		var onLayoutIds=this._onLayoutIds;
		if (!onLayoutIds) {
			onLayoutIds=[];
			
			this._onLayoutIds=onLayoutIds;
			
			this._install();
		}
		
		return onLayoutIds.f_addElement(component.id);
	},
	/**
	 * @method public
	 * @param Array list
	 * @return void
	 */
	f_addComponentClientIds: function(list) {
		var onLayoutIds=this._onLayoutIds;
		if (onLayoutIds) {
			onLayoutIds.push.apply(onLayoutIds, list);
			
		} else {		
			onLayoutIds=list;			
			this._onLayoutIds=onLayoutIds;
		}
		
		this._install();
	},
	/**
	 * @method private
	 * @param Boolean documentComplete
	 * @return void
	 */
	_install: function(documentComplete) {
		if (this._installed) {
			return;
		}
		if (documentComplete) {
			this._documentComplete=true;
		}
		if (!this._documentComplete) {
			return;
		}
		this._installed=true;
		
		var self=this;
		this._layoutFunc=function() {
			self.f_layoutAllComponents();
		};
		
		f_core.AddResizeEventListener(null, this._layoutFunc);		
	},
	/**
	 * @method private
	 * @return void
	 */
	_uninstall: function() {
		if (!this._installed) {
			return;
		}
		this._installed=false;
		
		var layoutFunc=this._layoutFunc;
		if (layoutFunc) {
			this._layoutFunc=undefined;
		
			f_core.RemoveResizeEventListener(null, layoutFunc);
		}
	},
	/**
	 * @method public
	 * @param f_component component
	 * @param optional Object layoutContext
	 * @return void
	 */
	f_layoutComponent: function(component, layoutContext) {
	
		var parentComponent=component.offsetParent;
	
		var parentSize=undefined;
		if (layoutContext) {
			parentSize=layoutContext[parentComponent.id];
		}
		
		if (!parentSize) {
			 parentSize=f_core.GetComponentSize(parentComponent);			
	
			 if (layoutContext) {
				 layoutContext[parentComponent.id]=parentSize;
			 }
		}
	
		var layout = f_layoutManager._GetNumberAttributeInCache(component, "layout");
	
		var componentSize=undefined;
		if (layout & 0x03) {
			componentSize=f_core.GetComponentSize(component);
		}
	
		if (layout & 0x01) {
			// Vertical Center
			
			var verticalCenter=f_layoutManager._GetNumberAttributeInCache(component, "verticalCenter");
			
			component.style.top = ((parentSize.height - componentSize.height)/2 + verticalCenter)+"px";
		}
		
		if (layout & 0x02) {
			// Horizontal Center
			var horizontalCenter=f_layoutManager._GetNumberAttributeInCache(component, "horizontalCenter");
			
			component.style.left = ((parentSize.width - componentSize.width)/2 + horizontalCenter)+"px";
		}
		
		if ((layout & 0x100) && !f_class.IsObjectInitialized(component)) {
			try {
				this.f_getClass().f_getClassLoader().f_init(component, false, false);
				
			} catch (ex) {
				f_core.Error(f_layoutManager, "_layoutComponent: Can not initialize component '"+component.id+"'.", ex);
			}			
						
			var onInitComponentListeners=this._onInitComponentListeners;
			if (onInitComponentListeners) {
				this._callOnInitComponentListeners(onInitComponentListeners, component);
			}
		}
		
		if (layout & 0x10) {
			// Height
			
			var top = parseInt(component.style.top, 10);
			var bottom=f_layoutManager._GetNumberAttributeInCache(component, "bottom");
			
			var newHeight = parentSize.height-top-bottom;
			
			try {
				if (component.f_setHeight) {			
					component.f_setHeight(newHeight, false);
					
				} else {
					component.style.height = newHeight+"px";
				}
			} catch (x) {
				f_core.Error(f_layoutManager, "Can not set height of component '"+component.id+"' newHeight="+newHeight, x);
			}

		}
		
		if (layout & 0x20) {
			// Width
			
			var left = parseInt(component.style.left, 10);
			var right=f_layoutManager._GetNumberAttributeInCache(component, "right");
			
			var newWidth = parentSize.width-left-right;
			
			try {
				if (component.f_setWidth) {		
					component.f_setWidth(newWidth, false);
				
				} else {
					component.style.width = newWidth+"px";
				}
			} catch (x) {
				f_core.Error(f_layoutManager, "Can not set width of component '"+component.id+"' newWidth="+newWidth, x);
			}
		}
	},
	/**
	 * @method public
	 * @return void
	 */
	f_layoutAllComponents: function() {
		var onLayoutIds=this._onLayoutIds;	
		if (!onLayoutIds) {
			return;
		}
		
		f_core.Debug(f_layoutManager, "f_layoutAllComponents: Starts "+onLayoutIds.length+" components layout.");

		var cache=new Object();
		for (var i=0; i<onLayoutIds.length; i++) {
			var componentId=onLayoutIds[i];
			
			var component=document.getElementById(componentId);
			if (!component) {
				f_core.Error(f_layoutManager,"f_layoutAllComponents["+i+"/"+onLayoutIds.length+"]: Can not find component '"+componentId+"' to layout.");
				continue;
			}

			this.f_layoutComponent(component, cache);
		}

		f_core.Debug(f_layoutManager, "f_layoutAllComponents: End of layout.");
	},

	/**
	 * @method public
	 * @param Element component
	 * @param optional Number horizontalCenter
	 * @param optional Number verticalCenter
	 * @param optional Number left
	 * @param optional Number top
	 * @param optional Number right
	 * @param optional Number bottom
	 */
	f_setLayoutConstraint: function(component, horizontalCenter, verticalCenter, left, top, right, bottom) {
		var layoutMode = 0x00;
		
		var style=component.style;
		
		var rightStyle="auto";
		var leftStyle="auto";
		var bottomStyle="auto";
		var topStyle="auto";

		if (horizontalCenter) {
			layoutMode|=0x02;
			component._horizontalCenter=horizontalCenter;
			
		} else if (left) {
			leftStyle=left+"px";
			
			if (right) {
				layoutMode|=0x20;
				component._right=right;
			}
		} else if (right) {
			rightStyle=right+"px";
		}
		
		if (verticalCenter) {
			layoutMode|=0x01;
			component._verticalCenter=verticalCenter;
			
		} else if (top) {
			topStyle=top+"px";
			
			if (bottom) {
				layoutMode|=0x10;
				component._bottom=bottom;
			}
		} else if (bottom) {
			bottomStyle=bottom+"px";
		}
		
		style.right=rightStyle;
		style.left=leftStyle;
		style.bottom=bottomStyle;
		style.top=topStyle;

		component._layoutMode=layoutMode;
		
		if (layoutMode) {
			if (this.f_addLayoutComponent(component.id)) {
				// ?
			}
		} else {
			if (this.f_removeLayoutComponent(component.id)) {
				// ?
			}
		}

		this.f_layoutComponents();
	}

};

new f_class("f_layoutManager", {
	members: __members,
	statics: __statics
});
