/**
 * Classe ItemsList.
 *
 * @class f_itemsList extends f_component, fa_readOnly, fa_disabled, fa_items, fa_subMenu
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	/** 
	 * @method private static
	 * @return String
	 * @context none
	 */
	_ItemToString: function() {
		return this._id;
	}
};

var __members = {

	/**
	 * <p>Construct a new <code>f_itemsList</code> with no
     * initial values.</p>
	 *
	 * @method public
	 */
	f_itemsList: function() {
		this.f_super(arguments);
		
		this._sepId=0;
		
		this._uiItems=new Object;
	},

	/**
	 * <p>Destruct a <code>f_itemsList</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {
		this._uiItems=undefined; // Map<String, Object>
//		this._sepId=undefined; // number
		
		this.f_super(arguments);
	},

	/*
	 * @method hidden 
	 * @return void
	 *
	f_update: function() {		
		return this.f_super(arguments);
	},
	*/
	/**
	 * @method hidden 
	 * @return void
	 */
	f_serialize: function() {
		this.f_serializeItems();
			
		this.f_super(arguments);
	},
	/**
	 * @method hidden 
	 * @return void
	 */
	fa_destroyItems: function(items) {
	},	
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.DBLCLICK:
		case f_event.SELECTION: 
		case f_event.BLUR:
		case f_event.FOCUS:
		case f_event.KEYDOWN:
		case f_event.KEYPRESS:
		case f_event.KEYUP:
			return;
		}
		
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.DBLCLICK:
		case f_event.SELECTION: 
		case f_event.BLUR:
		case f_event.FOCUS:
		case f_event.KEYDOWN:
		case f_event.KEYPRESS:
		case f_event.KEYUP:
			return;
		}
		
		this.f_super(arguments, type, target);
	},
	/**
	 * @method hidden
	 * @param String id
	 * @param Object properties
	 * @return Object
	 */
	f_appendToolItem2: function(id, properties) {
		
		var inputType=properties._inputType;

		var item=new Object();
		
		if (inputType==fa_items.AS_SEPARATOR) {			
			item._inputType=inputType;
			item._id="#SEP#"+(this._sepId++);
			item._value=item._id;
							
			if (f_core.IsDebugEnabled(f_itemsList)) {
				item.toString=function() {
					return "[toolItemSeparator id='"+item._id+"']";
				};
			} else {
				item.toString=f_itemsList._ItemToString;
			}

			this.f_addItem(this, item);

			f_core.Debug(f_itemsList, "f_appendToolItem2: append a separator !");
		
			return item;

		} else if (!inputType) {
			inputType=fa_items.AS_PUSH_BUTTON;
		}
		
		item._inputType=inputType;
		
		var component=this.f_findComponent(id);
		f_core.Assert(component, "f_itemsList.f_appendToolItem2: Can not find component associated to id '"+id+"'.");
		
		var item=new Object();
		item._id=component.id;
		item._value=properties._value;
		item._disabled=properties._disabled;

		if (properties._visible===false) {
			this.f_setItemVisible(item, false);
		}
		
		if (properties._clientDatas) {
			this.f_setItemClientDatas(item, properties._clientDatas);
		}
		
		if (f_core.IsDebugEnabled(f_itemsList)) {
			item.toString=function() {
				return "[toolItem id='"+this._id+"' value='"+this._value+"' inputType='"+this._inputType+"' disabled="+this._disabled+"]";
			};				
		} else {
			item.toString=f_itemsList._ItemToString;
		}
		this.f_addItem(this, item);
		
		f_core.Assert(!this._uiItems[item._value], "f_itemsList.f_appendToolItem2: Value is already used ! ('"+item._value+"')");
		
		this._uiItems[item._value]=component;
		
		f_core.Debug(f_itemsList, "f_appendToolItem2: append item: "+item);
		
		switch(inputType) {
		case fa_items.AS_PUSH_BUTTON:
		case fa_items.AS_CHECK_BUTTON:
		case fa_items.AS_RADIO_BUTTON:
			this._itemConfigureItemComponent(item, component);
			break;

		case fa_items.AS_DROP_DOWN_MENU:
			this._itemConfigureDropMenuItemComponent(item, component);
			break;

		case fa_items.AS_SUBMIT_BUTTON: // trop tard !?
		case fa_items.AS_RESET_BUTTON: // trop tard !?
			break;

		default:
			f_core.Debug(f_itemsList, "f_appendToolItem2: unknown input type of item='"+item+"'.");
		}
		
		return item;
	},
	/**
	 * @method protected
	 * @param f_event event
	 * @return void
	 */
	_itemConfigureItemComponent: function(item, component) {
		var itemsList=this;
		
		var selectionCallback=function(event) {
			return itemsList._itemOnSelect(event);
		};
	
		component.f_addEventListener(f_event.SELECTION, selectionCallback);		
	},
	/**
	 * @method protected
	 * @param f_event event
	 * @return void
	 */
	_itemConfigureDropMenuItemComponent: function(item, component) {
		var itemsList=this;
		
		var selectionCallback=function(event) {
			return itemsList._itemMenuOnSelect(event);
		};
	
		component.f_addEventListener(f_event.SELECTION, selectionCallback);		
	},
	/**
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_itemOnSelect: function(event) {
		var itemComponent=event.f_getComponent();
		var itemValue=this._getItemValueByComponent(itemComponent);		
		
		if (itemValue===undefined) {
			f_core.Debug(f_itemsList, "_itemOnSelect: Can not find item value='"+itemValue+"'.");
			return true;
		}
		
		var item=this.f_getItemByValue(itemValue, true);

		f_core.Debug(f_itemsList, "_itemOnSelect: Call SELECTION on item='"+item+"' value='"+itemValue+"'.");
		
		this.f_fireEvent(f_event.SELECTION, event.f_getJsEvent(), item, itemValue);
		
		return false;
	},
	/**
	 * @method private
	 * @param HTMLElement component
	 * @return String
	 */
	_getItemValueByComponent: function(component) {
		var uiItems=this._uiItems;
		
		for(var itemValue in uiItems) {					
			if (uiItems[itemValue]==component) {
				return itemValue;
			}
		}
		
		return undefined;
	},
	/**
	 * @method private
	 * @param f_event event
	 * @return Boolean
	 */
	_itemMenuOnSelect: function(event) {
		var itemValue=event.f_getValue(); // Rechercher l'item
		
		var itemComponent=event.f_getComponent();
		var componentValue=this._getItemValueByComponent(itemComponent);
		
		if (componentValue===undefined) {
			f_core.Debug(f_itemsList, "_itemMenuOnSelect: Can not find item value='"+itemValue+"'.");
			return true;
		}
		
		var item=this.f_getItemByValue(componentValue, true);

		f_core.Debug(f_itemsList, "_itemMenuOnSelect: Call SELECTION on item='"+item+"' value='"+itemValue+"'.");
		
		this.f_fireEvent(f_event.SELECTION, event.f_getJsEvent(), item, itemValue);
		
		return false;
	},
	/**
	 * @method public
	 * @return void
	 */
	fa_updateDisabled: function() {
		var disabled=this.f_isDisabled();
		
		var items=this.f_listItemChildren();
		for(var i=0;i<items.length;i++) {
			var component=this._uiItems[items[i]];
			if (!component) {
				continue;
			}
			
			component.f_setDisabled(disabled);
		}
	},
	/**
	 * @method public
	 * @return void
	 */
	fa_updateReadOnly: function() {
		var readOnly=this.f_isReadOnly();
		
		var items=this.f_listItemChildren();
		for(var i=0;i<items.length;i++) {
			var component=this._uiItems[items[i]];
			if (!component) {
				continue;
			}
				
			component.f_setReadOnly(readOnly);
		}
	},
	/**
	 * @method public
	 * @param Object item Item object or the item's value
	 * @return void
	 */
	fa_updateItemStyle: function(item) {
		var component=this.f_getItemComponent(item);
		if (!component) {
			return;
		}
		
		component.f_setDisabled(this.f_isItemDisabled(item));
		component.f_setVisible(this.f_isItemVisible(item));
		//component.f_setReadOnly(this.f_isItemReadOnly(item));
	},
	/*
	 * @method hidden
	 * @return void
	 *
	f_hideToolItems: function() {
		for(var i=0;i<arguments.length;i++) {
			var itemValue=arguments[i];
			
		}
	},
	*/
	/**
	 * @method hidden
	 * @param Object item Item object or the item's value
	 * @return f_component
	 */
	f_getItemComponent: function(item) {
		var itemValue=item;
		
		if (typeof(item)=="object") {
			itemValue=this.f_getItemValue(item);
		}
		
		return this._uiItems[itemValue];		
	}
	/*,
	/ **
	 * @method public
	 * @param String value Value of the item.
	 * @param String label Label to set.
	 * @return Boolean <code>true</code> if success, <code>false</code> otherwise.
	 * /
	f_setItemLabel: function(value, label) {
		f_core.Assert(typeof(label)=="string", "f_itemsList.f_setItemLabel: Invalid label parameter. ("+label+")");
		
		var component=this._getItemComponent(item);
		if (!component) {
			return false;
		}

		if (typeof(component.f_setText)!="function") {
			return false;
		}
		
		component.f_setText(label);
		return true;
	},
	/ **
	 * @method public
	 * @param String value Value of the item.
	 * @return String Label of the item.
	 * /
	f_getItemLabel: function(value) {
		var component=this._getItemComponent(item);
		if (!component) {
			return null;
		}

		if (typeof(component.f_getText)!="function") {
			return null;
		}
		
		return component.f_getText();
	}
	*/
};
 
new f_class("f_itemsList", {
	extend: f_component,
	aspects: [ fa_readOnly, fa_disabled, fa_items, fa_subMenu ],
	statics: __statics,
	members: __members
});
