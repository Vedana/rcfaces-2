/*
 * $Id: f_menu.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Class Menu
 *
 * @class public f_menu extends f_menuBase
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {
	f_menu: function(parentComponent, selectionProvider, redirectEvents, id, menuId, itemImageWidth, itemImageHeight, removeAllWhenShown) {
		f_core.Assert(!this.ownerDocument, "f_menu object can not be a tag !");
		
		this.f_super(arguments);

		this._parentComponent=parentComponent;
		this._selectionProvider=selectionProvider;
		this._redirectEvents=redirectEvents;

		if (!id) {
			id="";
		}
		this.id=id;
		this._menuId=menuId;
		this._component=parentComponent;
		this._menu=this;
		this.ownerDocument=parentComponent.ownerDocument;
		this._removeAllWhenShown=removeAllWhenShown;
		
		if (itemImageWidth && itemImageHeight) {
			this.f_setItemImageSize(itemImageWidth, itemImageHeight);
		}
	},
	f_finalize: function() {
		this._redirectEvents=undefined; // fa_targetEvent
		// this.id=undefined; // string
 
		this._selectionProvider=undefined; // f_selectionProvider
		this._component=undefined; // f_component
		this._parentComponent=undefined; // f_component
		// this.fa_componentUpdated=undefined; // boolean
		this.ownerDocument=undefined; // Document

		this._iePopup=undefined;

		this.f_super(arguments);
	},
	f_update: function(set) {
		if (f_popup.Ie_enablePopup()) {
			// On associe le POPUP 
			
			this._iePopup=f_popup.Ie_GetPopup(this.ownerDocument);
		}	
		
		this.fa_componentUpdated = (set===undefined)? true:set;		
	},
	fa_focusMenuItem: function(item) {
		// Ca sert au changement de menuBarItem !
	},
	f_fireEvent: function(type, jsEvt, item, value, selectionProvider, detail, stage) {
		var redir=this._redirectEvents;
		if (redir) {
			f_core.Assert(typeof(redir.f_fireEvent)=="function", "f_menu.f_fireEvent: Invalid redir object ("+redir+")");
			
			f_core.Debug(f_menu, "f_fireEvent: redirect event '"+type+"' to '"+redir+"'.");
			
			return redir.f_fireEvent.apply(redir, arguments);
		}
		
		return this.f_super(arguments, type, jsEvt, item, value, selectionProvider, detail, stage);
	},
	/** 
	 * @method public
	 * @return Boolean
	 */
	f_isReadOnly: function() {
		var component=this._parentComponent;
		
		if (component.f_isReadOnly && component.f_isReadOnly()) {
			return true;
		}
		
		return false;
	},
	/** 
	 * @method public
	 * @return f_component
	 */
	f_getOwnerComponent: function() {
		return this._parentComponent;
	},
	/** 
	 * @method public
	 * @return Boolean
	 */
	f_isOpened: function() {
		return this.f_uiIsPopupOpened(this);
	},
	fa_getSelectionProvider: function() {
		return this._selectionProvider;
	},
	f_setDomEvent: function(type, target) {
		// On positionne pas de Handler !
		return;
	},
	f_clearDomEvent: function(type, target) {
		// On positionne pas de Handler !
		return;
	},
	/**
	 * @method hidden
	 * @param Boolean enable
	 * @return void
	 */
	f_setCatchOnlyPopupKeys: function(enable) {
		this._catchOnlyPopupKeys=true;
	},
	
	/**
	 * @method private
	 * @return Boolean
	 */
	_filterKey: function(phase, evt) {
		f_core.Debug(f_menu, "_filterKey: key '"+evt.keyCode+"' catchOnlyPopupKeys="+this._catchOnlyPopupKeys);
		if (!this._catchOnlyPopupKeys) {
			return false;
		}
		
		var code=evt.keyCode;
	
		switch(code) {
		case f_key.VK_DOWN: // FLECHE VERS LE BAS
		case f_key.VK_UP: // FLECHE VERS LE HAUT
	 	case f_key.VK_ESCAPE:
	 	case f_key.VK_ENTER:
		case f_key.VK_RETURN:
			return false;
			
		case f_key.VK_RIGHT: // FLECHE VERS LA DROITE
		case f_key.VK_LEFT: // FLECHE VERS LA GAUCHE
		case f_key.VK_HOME: // HOME
		case f_key.VK_END: // END
		case f_key.VK_TAB: 
	 		// En cas de selection !
	 		if (this.f_uiGetSelectedItem(this)) {
				return false;
			}
		}
		
		return true;
	},

	fa_tabKeySelection: function() {
		return true;
	},
	
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return f_component
	 * @see f_component#f_findComponent
	 */
	f_findComponent: function(id) {
		return fa_namingContainer.FindComponents(this._parentComponent, arguments);
	},
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return f_component
	 * @see f_component#f_findComponent
	 */
	f_findSiblingComponent: function(id) {
		return fa_namingContainer.FindSiblingComponents(this._parentComponent, arguments);
	},
	fa_isRootMenuItem: function(parent) {
		return !parent;
	},
	f_nextMenuItem: function(menuItem, jsEvt) {

		f_core.Debug(f_menu, "f_nextMenuItem: menuItem="+menuItem+" evt='"+jsEvt+"'.");
			
		if (menuItem) {
			// Ok c'est pas un menuBarItem
			this.f_super(arguments, menuItem, jsEvt);
			return;
		}
		
		var menuItems=this.f_listVisibleItemChildren(this);		
		if (menuItems && menuItems.length) {
			for(var i=0;i<menuItems.length;i++) {
				var item=menuItems[i];
				
				if (this.f_isItemDisabled(item)) {
					continue;
				}
				
				this.f_uiSelectItem(item);
				return;
			}
			
			this.f_uiSelectItem(menuItems[0]);
		}
	},
	f_previousMenuItem: function(menuItem, jsEvt) {

		f_core.Debug(f_menu, "f_previousMenuItem: menuItem="+menuItem+" evt='"+jsEvt+"'.");
			
		if (menuItem) {
			// Ok c'est pas un menuBarItem
			this.f_super(arguments, menuItem, jsEvt);
			return;
		}
		
		var menuItems=this.f_listVisibleItemChildren(this);		
		if (menuItems && menuItems.length) {
			for(var i=menuItems.length-1;i>=0;i--) {
				var item=menuItems[i];
				
				if (this.f_isItemDisabled(item)) {
					continue;
				}
				
				this.f_uiSelectItem(item);
				return;
			}

			this.f_uiSelectItem(menuItems[menuItems.length-1]);
		}
	},
	/**
	 * @method protected
	 * @return Object
	 */
	fa_getPopupCallbacks: function() {
		var menu=this;
		
		return {
			/**
			 * @method public
			 */
			exit: menu.f_clickOutside,
			/**
			 * @method public
			 */
			keyDown: function(evt) {
				if (menu._filterKey("down", evt)===true) {
					return true;
				}
				
				return fa_menuCore.OnKeyDown(menu, evt);
			},
			/**
			 * @method public
			 */
			keyUp: function(evt) {
				return menu._filterKey("up", evt);
			},
			/**
			 * @method public
			 */
			keyPress: function(evt) {
				switch(evt.keyCode) {
				case f_key.VK_RETURN:
			 	case f_key.VK_ENTER:
			 		//return fa_menuCore.OnKeyDown(menu, evt);
				}
				
				return true;
			}
		};
	},
	
	fa_getMenuScopeName: function(menuItem) {
		var cid=this._parentComponent.id+"::"+this.id;
		if (menuItem==this) {
			return cid;
		}
		
		return cid+"::"+menuItem._id;
	},
	fa_getKeyProvider: function() {
		return this._parentComponent;
	},
	
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_menu id="+this.id+" menuId="+this._menuId+" component="+this._component+"]";
	}
};

new f_class("f_menu", {
	extend: f_menuBase,
	members: __members
});
 