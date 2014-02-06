/*
 * $Id: fa_subMenu.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Aspect fa_SubMenu
 *
 * @aspect public abstract fa_subMenu
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __members = {
	f_finalize: function() {
		this._subMenus=undefined; // Map<String,f_menu>
		// this._subMenuCount=undefined; // number
	},
	f_update: {
		after: function() {
			var subMenus=this._subMenus;
			if (subMenus) {
				for(var name in subMenus) {
					var menu=subMenus[name];
					
					menu.f_update();
				}
			}
		}
	},
	/**
	 * @method hidden
	 * @param String menuId
	 * @param optional Object properties
	 * @return Object
	 */
	f_newSubMenu: function(menuId, properties) {
		f_class.IsClassDefined("f_menu", true);
		
		if (!properties) {
			properties=new Object;
		}
		
		var id=properties._id;
		
		if (!id) {
		// La forme de menuId n'est peut-etre pas normalisée !
			
			var cnt=this._subMenuCount;
			if (!cnt) {
				cnt=0;
			}
			cnt++;
			this._subMenuCount=cnt;
			
			id=this.id+"__subMenu"+cnt;
		}

		var componentEventRedirect=this.fa_componentCaptureMenuEvent();
		
		var selectionProvider=null;
		if (this.f_getSelection) {
			selectionProvider=this;
		}
		
		var menu=f_menu.f_newInstance(this, selectionProvider, componentEventRedirect, id, menuId, properties._itemImageWidth, properties._itemImageHeight, properties._removeAllWhenShown);

		var subMenus=this._subMenus;
		if (!subMenus) {
			subMenus=new Object;
			this._subMenus=subMenus;
		}
		
		subMenus[menuId]=menu;

		f_core.Debug(fa_subMenu, "f_newSubMenu: Define new menuId='"+menuId+"' id='"+id+"' for component '"+this.id+"'.");
		
		return menu;
	},
	/**
	 * List all menus associated to the component.
	 *
	 * @method public
	 * @return f_menu[]
	 */
	f_listSubMenus: function() {
		var l=new Array;

		if (!f_class.IsClassDefined("f_menu")) {
			return l;
		}
		
		var subMenus=this._subMenus;
		if (!subMenus) {
			return l;
		}

		for(var name in subMenus) {
			var menu=subMenus[name];
			
			l.push(menu);
		}
		
		return l;
	},
	/**
	 * Returns the menu associated with an identifier.
	 * 
	 * @method public
	 * @param String menuId Identifier of menu
	 * @return f_menu The menu or <code>null</code>.
	 */
	f_getSubMenuById: function(menuId) {
		f_core.Assert(typeof(menuId)=="string", "fa_subMenu.f_getSubMenuById: MenuId parameter is not a string !");
		
		var subMenus=this._subMenus;
		if (!subMenus) {
			return null;
		}
		
		var menu=subMenus[menuId];

		return (menu)?menu:null;
	},
	
	/**
	 * @method protected abstract
	 * @return HTMLElement
	 */
	fa_componentCaptureMenuEvent: f_class.ABSTRACT
};

new f_aspect("fa_subMenu", {
	members: __members
});
