/*
 * $Id: f_menuBar.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Class MenuBar
 *
 * @class f_menuBar extends f_menuBarBase, fa_immediate, fa_readOnly, fa_disabled
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {

	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItem_mouseOver: function(evt) {
		var item=this._item;
		var menuBar=item._menu;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menuBar.f_getEventLocked(evt, false)) {
			return false;
		}

		menuBar._menuBarItem_over(item, evt);
		
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItem_mouseOut: function(evt) {
		var item=this._item;
		var menuBar=item._menu;

// Pas bloqué !		if (f_core.GetJsEventLocked(false)) return false;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		menuBar._menuBarItem_out(item);

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItemInput_click: function(evt) {
		var menuItem=this._item;
		var menuBar=menuItem._menu;
	
		f_core.Debug(f_menuBar, "_MenuBarItemInput_click: click on item='"+menuItem+"' menuBar='"+menuBar+"'.");

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menuBar.f_getEventLocked(evt)) {
			return false;
		}

		try {
			menuBar._menuBarItem_select(menuItem, false, evt);
			
			f_menuBar._MenuBarItem_setFocus(menuItem);
		
		} catch (x) {
			f_core.Error(f_menuBar, "_MenuBarItemInput_click: Click exception", x);
		}
				
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 */
	_MenuBarItem_setFocus: function(menuBarItem) {
		if (menuBarItem._hasFocus) {
			return;
		}

		var uiItem=menuBarItem._menu.f_getUIItem(menuBarItem);
		if (!uiItem) {
			return;
		}

		uiItem.focus();		
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItem_keyDown: function(evt) {
		var menuItem=this._item;
		var menuBar=menuItem._menu;
	
		f_core.Debug(f_menuBar, "_MenuBarItem_keyDown: key item='"+menuItem+"' menuBar='"+menuBar+"'.");
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menuBar.f_getEventLocked(evt)) {
			return false;
		}

		return fa_menuCore.OnKeyDown(menuBar, evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItem_focus: function(evt) {
		var menuItem=this._item;
		var menuBar=menuItem._menu;

		f_core.Debug(f_menuBar, "_MenuBarItem_focus: focus item='"+menuItem+"' menuBar='"+menuBar+"'.");

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menuBar.f_getEventLocked(evt, false)) {
			return false;
		}

		menuBar._hasFocus=true;
		
		var old=menuBar.f_uiGetSelectedItem(menuBar);
		
//		f_core.Info("f_menuBar", "Focus: old="+old+" param="+menuItem);
		
		if (old==menuItem) {
			return true;
		}
		
		if (old) {
			menuBar.f_uiDeselectItem(menuItem);
			menuBar.f_closeAllPopups();
		}
			
		menuBar.f_uiSelectItem(menuItem);

		if (menuBar.f_isItemDisabled(menuItem)) {
//			f_core.Info("f_menuBar", "Focus-DISABLED cur="+menuBar._selectedMenuItem);
			return true;
		}
		
		if (menuBar._openMode) {
			menuBar.f_openUIPopup(menuItem, evt, true);
		}
				
		return true;
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:menuBar
	 */
	_MenuBarItem_blur: function(evt) {
		var menuItem=this._item;
		var menuBar=menuItem._menu;
	
		f_core.Debug(f_menuBar, "_MenuBarItem_blur: blur item='"+menuItem+"' menuBar='"+menuBar+"'.");

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (menuBar.f_getEventLocked(evt, false)) { // Pas d'affichage ...
			return false;
		}
	
//		menuBar._openMode=undefined;
			
		f_core.Info(f_menuBar, "_MenuBarItem_blur: Blur clear openMode");
		
		var old=menuBar.f_uiGetSelectedItem(menuBar);
		if (old!=menuItem) {
			return true;
		}

		menuBar.f_uiDeselectItem(menuItem);
		menuBar.f_closeAllPopups();

		return true;
	}
}
 
var __members = {
	/*
	f_menuBar: function() {
		this.f_super(arguments);
		
	},
	*/
	f_finalize: function() {
		this._selectedMenuItem=undefined;		
		this.f_super(arguments);
	},
	f_serialize: function() {
		this.f_serializeItems();
			
		this.f_super(arguments);
	},
	f_setDomEvent: function(type, target) {
		if (type==f_event.SELECTION) {
			return;
		}
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		if (type==f_event.SELECTION) {
			return;
		}
		this.f_super(arguments, type, target);
	},
	
	/* ********************************************************************
	
		MenuBarItem
		
	 * ********************************************************************/
	fa_destroyItems: function(items) {
		var uiMenuItems=this._uiMenuItems;
		for(var i=0;i<items.length;i++) {
			var item=items[i];
			
			var uiItem=uiMenuItems[item];
			if (!uiItem) {
				continue;
			}
			
			delete uiMenuItems[item];
		
			this.f_destroyMenuBarItem(uiItem);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_destroyMenuBarItem: function(uiItem) {
		uiItem.onmouseover=null;
		uiItem.onmouseout=null;
		uiItem.onmousedown=null;
		uiItem.onclick=null;
		uiItem.onblur=null;
		uiItem.onfocus=null;
		uiItem.onkeypress=null;
		uiItem.onkeydown=null;
		
		uiItem._item=undefined;

		f_core.VerifyProperties(uiItem);
	},
	/**
	 * @method private
	 * @return void
	 */
	_menuBarItem_over: function(menuBarItem, jsEvt) {
		var old=this.f_uiGetSelectedItem(this);
				
		var openMode=this._openMode;
		
		f_core.Info(f_menuBar, "_menuBarItem_over: cur="+old+" param="+menuBarItem+" openMode="+this._openMode);
		
		if (old==menuBarItem) {
			// Le selectionné est le méme que l'ancien selectionné !
		
			if (!this.f_isItemDisabled(old) && openMode) {
				this.f_openUIPopup(menuBarItem, jsEvt, false);
			}
			
			return;
		}
			
		if (old) {
			this.f_uiDeselectItem(old);
	
			this.f_closeAllPopups();
		}

		this.f_uiSelectItem(menuBarItem);
				
		if (this.f_isItemDisabled(menuBarItem)) {	
			this._openMode=openMode;

//			f_core.Info(f_menuBar, "OVER DISABLED: cur="+this._selectedMenuItem+" openMode="+this._openMode);
			return;
		}
		
		this._openMode=openMode;
		if (openMode) {
			f_menuBar._MenuBarItem_setFocus(menuBarItem);

			this.f_openUIPopup(menuBarItem, jsEvt, false);
		}
	},
	/**
	 * @method private
	 * @return void
	 */
	_menuBarItem_out: function(menuBarItem) {
		var old=this.f_uiGetSelectedItem(this);
		
		f_core.Info(f_menuBar, "_menuBarItem_out: cur="+old+" param="+menuBarItem+" openMode="+this._openMode);

		if (old!=menuBarItem) {
			return;
		}

		if (this._openMode) { // old._popupOpened
			return;
		}

		this.f_uiDeselectItem(old);
		this.f_closeAllPopups();
	},
	/**
	 * @method private
	 * @return void
	 */
	_menuBarItem_select: function(menuBarItem, autoSelect, jsEvent) {
		var old=this.f_uiGetSelectedItem(this);

		if (old && this.f_uiIsPopupOpened(old)) {
			// Un popup deja ouvert ?
			this._openMode=undefined;
			
			this.f_closeAllPopups();

			this.f_updateMenuBarItemStyle(old);

			return;			
		}
	
		if (this.f_isDisabled() || this.f_isItemDisabled(menuBarItem)) {
			if (old) {
				this.f_uiDeselectItem(old);
			}
			return;
		}
		
		if (!this.f_hasVisibleItemChildren(menuBarItem)) {
			// Pas de popup !
			
			if (this.f_isReadOnly()) {
				return;
			}
			
			var value=this.f_getItemValue(menuBarItem);
			
			this.f_performItemSelect(menuBarItem, value, jsEvent);

			return;
		}
		this._openMode=true;

		this.f_openUIPopup(menuBarItem, jsEvent, autoSelect);
	},
	f_openUIPopup: function(menuItem, jsEvent, autoSelect, positionInfos) {
		if (!positionInfos) {
			var parentItem=this.f_getParentItem(menuItem);

			if (parentItem==this) {			
				positionInfos={
					position: f_popup.BOTTOM_LEFT_COMPONENT,
					component: this.f_getUIItem(menuItem),
					deltaX: -1,
					deltaY: 1
				}	
			}
		}
	
		return this.f_super(arguments, menuItem, jsEvent, autoSelect, positionInfos);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateMenuBarItemStyle: function(menuBarItem) {
	
		var component=this.f_getUIItem(menuBarItem);
		// MenuBarItem 
		var className="f_menuBar_bitem";
		
		var suffix="";
		if (this.f_isDisabled() || this.f_isItemDisabled(menuBarItem)) {
			suffix+="_disabled";
	
			if (this._selectedMenuItem==menuBarItem) {
				suffix+="_hover";
			}
			
		} else if (this._selectedMenuItem==menuBarItem) {
			if (this.f_uiIsPopupOpened(menuBarItem)) {
				suffix+="_selected";
			}
			
			suffix+="_hover";
		}
		
		if (suffix) {
			className+=" "+className+suffix;
		}
		
		if (component.className!=className) {
			component.className=className;
		}
	},
	f_accessKeyMenuItem: function(menuItem, jsEvent) {
		f_core.Debug(f_menuBar, "f_accessKeyMenuItem: menuItem='"+menuItem+"' key="+jsEvent.keyCode);

		if (menuItem==null) {
			var menuItems=this.f_listVisibleItemChildren(this);
			if (!menuItems || !menuItems.length) {
				return;
			}
			
			menuItem=menuItems[0];
		}

		var parent=this.f_getParentItem(menuItem);
		
		if (parent==this) {
			if (this.f_uiIsPopupOpened(menuItem)) {
				// Si le popup du menuBarItem est ouvert, on considère comme scope le premier enfant du popup !
				var menuItems=this.f_listVisibleItemChildren(menuItem);				
				
				menuItem=menuItems[0];
			} else {
				// On recherche parmis les menuBarItems
			}
		}

		this.f_super(arguments, menuItem, jsEvent);
	},
	f_nextMenuItem: function(menuItem, jsEvt) {

		f_core.Debug(f_menuBar, "f_nextMenuItem: menuItem="+menuItem+" evt='"+jsEvt+"'.");

		var parentItem=this.f_getParentItem(menuItem);
	
		if (parentItem!=this) {
			// Ok c'est pas un menuBarItem
			this.f_super(arguments, menuItem, jsEvt);
			return;
		}
		
		this.f_uiSelectItem(menuItem);
		this._openMode=true;
		this.f_openUIPopup(menuItem, jsEvt, false);
		
		var menuItems=this.f_listVisibleItemChildren(menuItem);		
		if (menuItems && menuItems.length) {
			this.f_menuItem_over(menuItems[0], false, jsEvt);
		}
	},
	f_previousMenuItem: function(menuItem, evt) {

		f_core.Debug(f_menuBar, "f_previousMenuItem: menuItem="+menuItem+" evt='"+evt+"'.");

		var parentItem=this.f_getParentItem(menuItem);
	
		if (parentItem!=this) {
			// Ok c'est pas un menuBarItem
			this.f_super(arguments, menuItem, evt);
			return;
		}
		
		this.f_uiSelectItem(menuItem);
		this._openMode=true;
		this.f_openUIPopup(menuItem, jsEvt, false);
		
		var menuItems=this.f_listVisibleItemChildren(menuItem);		
		if (menuItems && menuItems.length) {
			this.f_menuItem_over(menuItems[0], false, jsEvt);
		}
	},
	/**
	 * @method protected
	 * @inherited
	 */
	f_nextMenuItemLevel: function(menuItem, evt) {

		f_core.Debug(f_menuBar, "f_nextMenuItemLevel: menuItem="+menuItem+" evt='"+evt+"'.");

		var parentItem=this.f_getParentItem(menuItem);
	
		if (parentItem!=this) {
			// Ok c'est pas un menuBarItem
			
			// On verifie que l'on est pas au bout !			
			if (this.f_hasVisibleItemChildren(menuItem)) {			
				this.f_super(arguments, menuItem, evt);
				return;
			}
			
			// On passe au menuBarItem suivant car nous étions au bout des popups !
			menuItem=this.f_uiGetSelectedItem(this);
		}
	
		this.f_closeAllPopups();
		
		// On recherche notre item
		var items=this.f_listVisibleItemChildren(this);
		for(var i=0;i<items.length;i++) {
		
			var item=items[i];
			if (menuItem!=item) {
				continue;
			}
			
			i++;
			if (i==items.length) {
				i=0;
			}	

			item=items[i];

			var selectedItem=this._selectedMenuItem;
			if (item==selectedItem) {
				break;
			}			

			this.f_getUIItem(item).focus();
			break;
		}
	},
	/**
	 * @method protected
	 * @inherited
	 */
	f_previousMenuItemLevel: function(menuItem, evt) {

		f_core.Debug(f_menuBar, "f_prevMenuItemLevel: menuItem="+menuItem+" evt='"+evt+"'.");

		var parentItem=this.f_getParentItem(menuItem);
	
		if (parentItem!=this) {
			// Ok c'est pas un menuBarItem

			// On verifie que c'est pas le premier niveau !		
			if (this.f_getParentItem(parentItem)!=this) {			
				this.f_super(arguments, menuItem, evt);
				return;
			}
			
			// On passe au menuBarItem précedent car nous étions au premier niveau !
			menuItem=this.f_uiGetSelectedItem(this);
		}
	
		this.f_closeAllPopups();
		
		// On recherche notre item
		var items=this.f_listVisibleItemChildren(this);
		for(var i=0;i<items.length;i++) {
		
			var item=items[i];
			if (menuItem!=item) {
				continue;
			}
			
			i--;
			if (i<0) {
				i=items.length-1;
			}	

			item=items[i];

			var selectedItem=this._selectedMenuItem;
			if (item==selectedItem) {
				break;
			}			

			this.f_getUIItem(item).focus();
			break;
		}
	},
	
	/**
	 * @method hidden
	 * @return Object item
	 */
	f_declareBarItem2: function(id, properties) {
		var item=this.f_declareBarItem(id, properties._label, properties._value, properties._accessKey, properties._disabled);
		
		if (properties._imageURL) {
			this.f_setItemImages(item, 
				properties._imageURL, 
				properties._disabledImageURL, 
				properties._hoverImageURL, 
				properties._selectedImageURL);
		}
		
		if (properties._clientDatas) {
			this.f_setItemClientDatas(item, properties._clientDatas);
		}
		
		if (properties._styleClass) {
			item._styleClass=properties._styleClass;
		}
		
		return item;
	},
	
	/**
	 * @method hidden
	 * @return Object item
	 */
	f_declareBarItem: function(id, label, value, accessKey, disabled) {
		var menuBarItem=this.f_appendItem(this, id, label, value, accessKey, null, disabled);
		
		var uiItem=this.ownerDocument.createElement("button");
		this._uiMenuItems[menuBarItem]=uiItem; // Ben oui 
		uiItem._item=menuBarItem;
		
		uiItem.id=id;
		uiItem.tabIndex=-1;
		uiItem.className="f_menuBar_bitem";
		
		f_component.AddLabelWithAccessKey(uiItem, label, accessKey);

		uiItem.onmouseover=f_menuBar._MenuBarItem_mouseOver;
		uiItem.onmouseout=f_menuBar._MenuBarItem_mouseOut;
				
		if (accessKey) {
			uiItem.accessKey=accessKey;
			
			f_key.AddKeyHandler(null, accessKey, this, null, uiItem);
		}			
		
		uiItem.tabIndex=this._tabIndex;
		uiItem.onkeydown=f_menuBar._MenuBarItem_keyDown;
		uiItem.onclick=f_menuBar._MenuBarItemInput_click;
		uiItem.onfocus=f_menuBar._MenuBarItem_focus;
		uiItem.onblur=f_menuBar._MenuBarItem_blur;
		uiItem.hideFocus=true;

		this.f_updateMenuBarItemStyle(menuBarItem);

		if (this._items.length==1) {
			var dummies=this.getElementsByTagName("a");
			
			for(var i=0;i<dummies.length;i++) {
				this.removeChild(dummies[i]);
			}
		}
		
		f_core.AppendChild(this, uiItem);

		return menuBarItem;
	},
	fa_focusMenuItem: function(menuBarItem) {
			// On verifie si c'est un menuBar !
		for(;menuBarItem._parentItem;) {
			menuBarItem=menuBarItem._parentItem;
		}
	
//		f_core.Info("f_menuBar", "Focus menuItem "+this._selectedMenuItem+"/"+menuBarItem);
	
		var old=this._selectedMenuItem;
		if (old==menuBarItem) {
			return;
		}
	
		f_menuBar._MenuBarItem_setFocus(menuBarItem);
		
		if (old) {
			this.f_uiDeselectItem(old);
		}

		this.f_uiSelectItem(menuBarItem);
	},
	fa_updateDisabled: function() {
		if (!this.fa_componentUpdated) {
			return;
		}
		
		var l=this.f_listVisibleItemChildren(this);
		for(var i=0;i<l.length;i++) {
			this.fa_updateItemStyle(l[i]);
		}
	},
	fa_updateReadOnly: function() {
	},
	fa_getSelectionProvider: function() {
		return null;
	},
	f_setFocus: function() {
		if (!f_core.ForceComponentVisibility(this)) {
			return;
		}

		if (this.f_isDisabled()) {
			return false;
		}
		
		var items=this.f_listVisibleItemChildren(this);
		if (!items.length) {
			return false;
		}
		
		for(var i=0;i<items.length;i++) {
			var menuBarItem=items[i];
			
			/* On accepte le focus sur un item disabled !
			if (menuBarItem._disabled) {
				continue;
			}
			*/
			
			var uiItem=this.f_getUIItem(menuBarItem);
			if (!uiItem) {
				continue;
			}

			uiItem.focus();
			return true;
		}
		
		return false;
	},
	f_closeAllPopups: function() {
		f_core.Debug(fa_menuCore, "menuBar.f_closeAllPopups: close all popups");
		
		var items=this.f_listVisibleItemChildren(this);
		for(var i=0;i<items.length;i++) {
			var item=items[i];
			this.f_closeUIPopup(item);
		}
	},
	fa_updateItemStyle: function(menuItem) {
		if (this.f_getParentItem(menuItem)==this) {
			this.f_updateMenuBarItemStyle(menuItem);
			return;
		}
		
		this.f_super(arguments, menuItem);		
	},
	
	fa_getMenuScopeName: function(menuItem) {
		var cid=this.id;
		if (menuItem==this) {
			return cid;
		}
		
		return cid+"::"+menuItem._id;
	},
	/**
	 * @method protected
	 * @param Object menuItem
	 * @return void
	 */
	f_uiUpdateItemStyle: function(menuItem) {
		if (this.f_getParentItem(menuItem)==this) {
			this.f_updateMenuBarItemStyle(menuItem);
			return;
		}
		
		this.f_super(arguments, menuItem);		
	},
	f_performItemSelect: function(item, value, jsEvent) {		
		this._openMode=undefined;
	
		this.f_super(arguments, item, value, jsEvent);
	},
	f_clickOutside: function() {
		this._openMode=undefined;
	
		this.f_super(arguments);
	},
	f_keyCloseMenuItem: function(menuItem, evt) {	
		this._openMode=undefined;
	
		if (this.f_getParentItem(menuItem)==this) {
			this.f_closeUIPopup(menuItem);
			return;
		}		
	
		this.f_super(arguments, menuItem, evt);
	},
	f_uiGetSelectedItem: function(menuItem) {
		if (menuItem==this) {
			return this._selectedMenuItem;
		}
		
		return this.f_super(arguments, menuItem);
	},
	f_uiSelectItem: function(menuItem) {
		f_core.Debug(f_menuBar, "f_uiSelectItem: item="+menuItem);

		var menuItemParent=this.f_getParentItem(menuItem);
		f_core.Assert(typeof(menuItemParent)=="object" && (!menuItemParent.nodeType || menuItemParent==this) && menuItemParent._menu, "fa_menuCore.f_uiSelectItem: Invalid menuItemParent parameter ("+menuItemParent+")");
		f_core.Assert(menuItemParent!=menuItem, "Invalid menuItem, same as parent. (parent="+menuItemParent+")");

		if (menuItemParent==this) {
			var old=this._selectedMenuItem;
			
			this._selectedMenuItem=menuItem;
			
			if (old) {			
				this.f_updateMenuBarItemStyle(old);
			}
			
			this.f_updateMenuBarItemStyle(menuItem);
			return;
		}
		
		return this.f_super(arguments, menuItem);
	},
	f_uiDeselectItem: function(menuItem) {
		f_core.Debug(f_menuBar, "f_uiDeselectItem: menuItem="+menuItem);

		var menuItemParent=this.f_getParentItem(menuItem);
		f_core.Assert(typeof(menuItemParent)=="object" && (!menuItemParent.nodeType || menuItemParent==this) && menuItemParent._menu, "fa_menuCore.f_uiSelectItem: Invalid menuItemParent parameter ("+menuItemParent+")");
		f_core.Assert(menuItemParent!=menuItem, "Invalid menuItem, same as parent. (parent="+menuItemParent+")");

		if (menuItemParent==this) {
			var old=this._selectedMenuItem;
			if (!old) {
				return;
			}
		
			this._selectedMenuItem=undefined;
			
			if (old) {			
				this.f_updateMenuBarItemStyle(old);
			}
			
			return;
		}
		
		return this.f_super(arguments, menuItem);
	},
	
	/**
	 * @method protected
	 * @return Object
	 */
	fa_getPopupCallbacks: function() {
		var menuBar=this;
		
		return {
			/**
			 * @method public
			 */
			exit: menuBar.f_clickOutside,
			/**
			 * @method public
			 */
			keyDown: function(evt) {
				if (!evt) {
					evt = f_core.GetJsEvent(this);
				}
		
				return fa_menuCore.OnKeyDown(menuBar, evt);
			},
			/**
			 * @method public
			 */
			keyUp: function(evt) {
				return true;
			},
			/**
			 * @method public
			 */
			keyPress: function(evt) {
				return true;
			}
		}
	},
	fa_getKeyProvider: function() {
		return null;
	},
	fa_isRootMenuItem: function(parent) {
		return parent==this;
	},
	/**
	 * @method hidden
	 * @param Object popupDocument
	 * @return Boolean
	 */
	f_isPopupLock: function(popupDocument) {
		var menuItem=this.f_uiGetSelectedItem(this);
		if (!menuItem) {
			return false;
		}
	
		var popup=this.f_getUIPopup(menuItem);
	
		return (popup==popupDocument); // On reste bloqué si ce n'est pas le meme
	}
};

new f_class("f_menuBar", {
	extend: f_menuBarBase,
	aspects: [ fa_readOnly, fa_disabled, fa_immediate ],
	statics: __statics,
	members: __members
});
