/*
 * $Id: fa_menuCore.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Aspect Menu
 * 
 * @aspect public abstract fa_menuCore extends fa_groupName, fa_items, fa_aria
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {

	/**
	 * @field private static final Number
	 */
	_ITEM_IMAGE_WIDTH : 18,

	/**
	 * @field private static final Number
	 */
	_ITEM_IMAGE_HEIGHT : 18,

	/**
	 * @field private static final String
	 */
	_BLANK_IMAGE_URL : "/menu/blank.gif",

	/**
	 * @field private static Number
	 */
	_ItemIds : 0,

	/**
	 * @method private static
	 * @return String
	 * @context none
	 */
	_ItemToString : function() {
		return this._id;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:menu
	 */
	_MenuItem_mouseOver : function(evt) {
		var item = this._item;
		var menu = item._menu;

		f_core.Debug(fa_menuCore, "_MenuItem_mouseOver: menu=" + menu
				+ " item=" + item);

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menu.f_getEventLocked(evt, false)) {
			return false;
		}

		menu.f_menuItem_over(item, true, evt);

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:menu
	 */
	_MenuItem_mouseOut : function(evt) {
		var item = this._item;
		var menu = item._menu;

		// Pas bloqué ! if (f_core.GetJsEventLocked(false)) return false;

		f_core.Debug(fa_menuCore, "_MenuItem_mouseOut: menu=" + menu + " item="
				+ item);

		menu._menuItem_out(item);

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:menu
	 */
	_MenuItem_mouseDown : function(evt) {
		var item = this._item;
		var menu = item._menu;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (menu.f_getEventLocked(evt)) {
			return false;
		}

		menu.f_menuItem_select(item, evt);

		return f_core.CancelJsEvent(evt);
	},

	/**
	 * @method private static
	 */
	_SeparatorItem_click : f_core.CancelJsEventHandler,

	/**
	 * @method private static
	 */
	_MenuItem_click : f_core.CancelJsEventHandler,

	/**
	 * @method hidden static final
	 * @param fa_menuCore
	 *            menu
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            evt
	 * @return Boolean
	 */
	OnKeyDown : function(menu, evt) {
		f_core.Assert(evt, "Event is null !");

		var menuItem = null;
		var parent = menu;
		// On recherche le sous-menu (enfant) ouvert ...
		for (;;) {
			var pi = menu.f_uiGetSelectedItem(parent);
			if (!pi) {
				break;
			}
			menuItem = pi;

			if (!menu.f_uiIsPopupOpened(pi)) {
				break;
			}

			parent = pi;
		}

		f_core.Debug(fa_menuCore, "OnKeyDown: key down code=" + evt.keyCode
				+ " menu=" + menu + " menuItem='" + menuItem + "'.");

		var code = evt.keyCode;

		var cancel = undefined;
		switch (code) {
		case f_key.VK_CONTEXTMENU:
			cancel = true;
			break;

		case f_key.VK_DOWN: // FLECHE VERS LE BAS
			menu.f_nextMenuItem(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_UP: // FLECHE VERS LE HAUT
			menu.f_previousMenuItem(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_RIGHT: // FLECHE VERS LA DROITE
			menu.f_nextMenuItemLevel(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_LEFT: // FLECHE VERS LA GAUCHE
			menu.f_previousMenuItemLevel(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_HOME: // HOME
		// @TODO menu.f_nextMenuItemLevel(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_END: // END
		// @TODO menu.f_previousMenuItemLevel(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_TAB:
			// Rien ....
			if (!menu.fa_tabKeySelection || !menu.fa_tabKeySelection()) {
				break;
			}

			// C'est un RETURN alors !

		case f_key.VK_RETURN:
		case f_key.VK_ENTER:
			menu.f_keySelectMenuItem(menuItem, evt);
			cancel = true;
			break;

		case f_key.VK_ESCAPE:
			menu.f_keyCloseMenuItem(menuItem, evt);
			cancel = true;
			break;

		default:
			f_core.Debug(fa_menuCore, "OnKeyDown: Default key: menu=" + menu
					+ " menuItem=" + menuItem);

			if (!evt.altKey) {
				cancel = true;
				menu.f_accessKeyMenuItem(menuItem, evt);
			}
		}

		if (cancel) {
			return f_core.CancelJsEvent(evt);
		}

		return true;
	},
	/**
	 * @field private static number
	 */
	_KeyGenerator : 0,
	/**
	 * @method private static
	 */
	_ComputeKey : function(obj) {
		if (obj._menuHashKey) {
			return obj._menuHashKey;
		}

		obj._menuHashKey = "key" + (fa_menuCore._KeyGenerator++);

		return obj._menuHashKey;
	}
};

var __members = {
	fa_menuCore : function() {

		this._uiMenuItems = new Object;
		this._uiMenuPopups = new Object;
		this._menu = this;

		if (this.tagName) {
			this._itemImageWidth = f_core.GetNumberAttributeNS(this,
					"itemImageWidth");
			this._itemImageHeight = f_core.GetNumberAttributeNS(this,
					"itemImageHeight");

			if (this._itemImageWidth || this._itemImageHeight) {
				f_core.Debug(fa_menuCore,
						"fa_menuCore: Set item image width/height by tag attributes width="
								+ this._itemImageWidth + " height="
								+ this._itemImageHeight + ".");
			}
		}

		this._blankMenuImageURL = f_env.GetStyleSheetBase()
				+ fa_menuCore._BLANK_IMAGE_URL;

		f_imageRepository.PrepareImage(this._blankMenuImageURL);
	},
	f_finalize : function() {
		this._menu = undefined; // fa_menuCore

		// this._blankMenuImageURL=undefined; // string
		// this._itemImageWidth=undefined; // number
		// this._itemImageHeight=undefined; // number

		this._uiMenuItems = undefined; // Map<Object, HTMLElement>
		this._uiMenuPopups = undefined; // Map<Object, HTMLElement>
	},
	/**
	 * @method public
	 * @param Object
	 *            parentItem Parent object or <code>null</code>
	 * @param String
	 *            id Identifier of the item. (can be <code>null</code>)
	 * @param String
	 *            groupName Group name of the item.
	 * @param String
	 *            label Label of the item.
	 * @param optional
	 *            String value
	 * @param optional
	 *            boolean checked
	 * @param optional
	 *            Boolean notFireChecked
	 * @param optional
	 *            String accessKey
	 * @param optional
	 *            String tooltip
	 * @param optional
	 *            boolean disabled
	 * @param optional
	 *            boolean visible
	 * @param optional
	 *            String acceleratorKey
	 * @return Object
	 */
	f_appendRadioItem : function(parentItem, id, groupName, label, value,
			checked, notFireChecked, accessKey, tooltip, disabled, visible,
			acceleratorKey) {
		f_core.Assert(typeof (groupName) == "string",
				"fa_menuCore.f_appendRadioItem: groupName parameter is invalid. ("
						+ groupName + ")");

		var item = this.f_appendItem(parentItem, id, label, value, accessKey,
				tooltip, disabled, visible, acceleratorKey);

		item._inputType = fa_items.AS_RADIO_BUTTON;
		if (groupName) {
			this.f_setItemGroupName(item, groupName);
		}
		if (checked) {
			this.f_setItemChecked(item, checked, notFireChecked);
		}

		return item;
	},
	/**
	 * @method public
	 * @param Object
	 *            parentItem Parent object or <code>null</code>
	 * @param String
	 *            id Identifier of the item. (can be <code>null</code>)
	 * @param String
	 *            label Label of the item.
	 * @param optional
	 *            String value
	 * @param optional
	 *            boolean checked
	 * @param optional
	 *            Boolean notFireChecked
	 * @param optional
	 *            String accessKey
	 * @param optional
	 *            String tooltip
	 * @param optional
	 *            boolean disabled
	 * @param optional
	 *            boolean visible
	 * @param optional
	 *            String acceleratorKey
	 * @return Object
	 */
	f_appendCheckItem : function(parentItem, id, label, value, checked,
			notFireChecked, accessKey, tooltip, disabled, visible,
			acceleratorKey) {
		var item = this.f_appendItem(parentItem, id, label, value, accessKey,
				tooltip, disabled, visible, acceleratorKey);

		item._inputType = fa_items.AS_CHECK_BUTTON;
		if (checked) {
			this.f_setItemChecked(item, checked, notFireChecked);
		}

		return item;
	},
	/**
	 * Properties: _value: value _label: label _description: description
	 * _disabled: disabled _groupName: groupName _type: type _checked: checked
	 * _accessKey: accessKey _visible: visible at: acceleratorKey st: styleClass
	 * re: removeAllWhenShow me: menuItemListeners img: image imge: image
	 * expanded imgd: image disabled imgh: image hover imgs: image selected
	 * 
	 * 
	 * @method hidden
	 * @return Object
	 */
	f_appendItem2 : function(parentItem, id, properties) {

		if (properties._value === null && !properties._label) {
			var resourceBundle = f_resourceBundle.Get(fa_criteriaManager);
			properties._label = "(" + resourceBundle.f_get("EMPTY_LABEL") + ")";
			properties._value = '\x01';
		}

		var item = this.f_appendItem(parentItem, id, properties._label,
				properties._value, properties._accessKey,
				properties._description, properties._disabled,
				properties._visible, properties._acceleratorKey);

		switch (properties._type) {
		case fa_items.AS_RADIO_BUTTON:
			if (properties._groupName) {
				this.f_setItemGroupName(item, properties._groupName);
			}
			// On continue

		case fa_items.AS_CHECK_BUTTON:
			item._inputType = properties._type;
			if (properties._checked) {
				this.f_setItemChecked(item, true);
			}
			break;
		}

		if (properties._removeAllWhenShow) {
			item._removeAllWhenShow = true;
		}

		if (properties._imageURL) {
			this.f_setItemImages(item, properties._imageURL,
					properties._disabledImageURL, properties._hoverImageURL,
					properties._selectedImageURL);
		}

		if (properties._clientDatas) {
			this.f_setItemClientDatas(item, properties._clientDatas);
		}

		if (properties._styleClass) {
			this.f_setItemStyleClass(item, properties._styleClass);
		}

		return item;
	},
	/**
	 * Add an item to a component.
	 * 
	 * @method public
	 * @param Object
	 *            parentItem Parent object or <code>null</code>
	 * @param String
	 *            id Identifier of the item. (can be <code>null</code>)
	 * @param String
	 *            label Label of the item.
	 * @param optional
	 *            String value Value of the item.
	 * @param optional
	 *            String accessKey Access key of the item.
	 * @param optional
	 *            String tooltip
	 * @param optional
	 *            boolean disabled
	 * @param optional
	 *            boolean visible
	 * @param optional
	 *            String acceleratorKey
	 * @return Object
	 */
	f_appendItem : function(parentItem, id, label, value, accessKey, tooltip,
			disabled, visible, acceleratorKey) {
		f_core.Assert(parentItem === null || typeof (parentItem) == "object",
				"fa_menuCore.f_appendItem: parentItem parameter is invalid. ("
						+ parentItem + ")");
		f_core.Assert(id === null || typeof (id) == "string",
				"fa_menuCore.f_appendItem: id parameter is invalid. (" + id
						+ ")");
		f_core.Assert(typeof (label) == "string",
				"fa_menuCore.f_appendItem: label parameter is invalid. ("
						+ label + ")");

		if (!parentItem) {
			parentItem = this;
		}

		var menuItem = new Object;
		menuItem._inputType = fa_items.AS_PUSH_BUTTON;
		menuItem._accessKey = accessKey;
		menuItem._value = value;
		menuItem._parentItem = parentItem;
		menuItem._menu = this;
		menuItem._label = label;
		menuItem._acceleratorKey = acceleratorKey;

		if (f_core.IsDebugEnabled(fa_menuCore)) {
			menuItem.toString = function() {
				return "[MenuItem id='" + this._id + "' value='" + this._value
						+ "' inputType='" + this._inputType + "' label='"
						+ this._label + "' accessKey='" + this._accessKey
						+ "' acceleratorKey='" + this._acceleratorKey + "']";
			};
		} else {
			// Attention la clef de l'objet en renvoyé par le toString() !!!!
			menuItem.toString = fa_menuCore._ItemToString;
		}

		if (!id) {
			id = this.id + "::" + (fa_menuCore._ItemIds++);
		}
		menuItem._id = id;

		this.f_addItem(parentItem, menuItem);

		if (disabled) {
			this.f_setItemDisabled(menuItem, disabled);
		}
		if (tooltip) {
			this.f_setItemToolTip(menuItem, tooltip);
		}
		if (visible === false) {
			this.f_setItemVisible(menuItem, visible);
		}

		return menuItem;
	},
	/**
	 * @method public
	 * @param Object
	 *            parentItem Parent object or <code>null</code>
	 * @return Object
	 */
	f_appendSeparatorItem : function(parentItem) {
		if (!parentItem) {
			parentItem = this;
		}

		var item = new Object;
		item._inputType = fa_items.AS_SEPARATOR;
		item._disabled = true;
		item._parentItem = parentItem;
		item._menu = this;
		item.toString = function() {
			return "[MenuItemSeparator]";
		};

		this.f_addItem(parentItem, item);

		return item;
	},
	/**
	 * @method protected
	 */
	f_setItemImageSize : function(itemImageWidth, itemImageHeight) {
		f_core.Assert(typeof (itemImageWidth) == "number" && itemImageWidth,
				"fa_menuCore.f_setItemImageSize: Invalid itemImageWidth parameter ("
						+ itemImageWidth + ").");
		f_core.Assert(typeof (itemImageHeight) == "number" && itemImageHeight,
				"fa_menuCore.f_setItemImageSize: Invalid itemImageHeight parameter ("
						+ itemImageHeight + ").");

		this._itemImageWidth = itemImageWidth;
		this._itemImageHeight = itemImageHeight;

		f_core.Debug(fa_menuCore, "Set item image size to width="
				+ this._itemImageWidth + " height=" + this._itemImageHeight
				+ ".");
	},
	/**
	 * @method protected
	 */
	f_createPopup : function(container, parentItem) {
		var popupObject = undefined;
		var doc;

		if (f_popup.Ie_enablePopup()) {
			// container = popup object

			popupObject = container;
			doc = container.document;
			container = doc.body;

		} else {
			doc = container.ownerDocument;
		}

		var uiPopup = doc.createElement("ul");

		var cs = "f_menu_popup";

		if (parentItem._component && parentItem._component.f_getStyleClass) {
			var pcs = parentItem._component.f_getStyleClass();
			if (pcs) {
				cs += " " + pcs + "_popup";
			}
		}
		uiPopup.className = cs;

		if (!popupObject) {
			uiPopup.style.visibility = "hidden";
			popupObject = uiPopup;
		}

		uiPopup._popupObject = popupObject;

		f_core.AppendChild(container, uiPopup);

		var key = fa_menuCore._ComputeKey(parentItem);
		this._uiMenuPopups[key] = uiPopup;

		uiPopup.id = this.fa_getMenuScopeName(parentItem);
		uiPopup._item = parentItem;

		if (!this._itemImageWidth) {
			this._itemImageWidth = fa_menuCore._ITEM_IMAGE_WIDTH;
		}

		if (!this._itemImageHeight) {
			this._itemImageHeight = fa_menuCore._ITEM_IMAGE_HEIGHT;

			f_core
					.Debug(
							fa_menuCore,
							"fa_menuCore.f_createPopup: Use default size for item image width/height, width="
									+ this._itemImageWidth
									+ " height="
									+ this._itemImageHeight + ".");
		}

		var sep = true;

		var items = this.f_listVisibleItemChildren(parentItem);
		for ( var i = 0; i < items.length; i++) {
			var item = items[i];

			if (item._inputType == fa_items.AS_SEPARATOR) {
				if (sep) {
					continue;
				}

				// D'autres items apres ?
				for ( var j = i + 1; j < items.length; j++) {
					if (items[j]._inputType != fa_items.AS_SEPARATOR) {
						break;
					}
				}
				if (j == items.length) {
					break;
				}
			}

			var uiItem = doc.createElement("li");
			var itemId = item._id;
			if (itemId) {
				uiItem.id = itemId;
			}
			f_core.AppendChild(uiPopup, uiItem);

			var key = fa_menuCore._ComputeKey(item);
			this._uiMenuItems[key] = uiItem;
			uiItem._item = item;
			sep = false;

			switch (item._inputType) {
			case fa_items.AS_CHECK_BUTTON:
				uiItem.setAttribute("role", "menuitemcheckbox");
				break;

			case fa_items.AS_RADIO_BUTTON:
				uiItem.setAttribute("role", "menuitemradio");
				break;

			case fa_items.AS_SEPARATOR:
				uiItem.setAttribute("role", "menuitemseparator");
				uiItem.className = "f_menu_item_sep";
				uiItem.onmousedown = fa_menuCore._SeparatorItem_click;
				sep = true;

			default:
				uiItem.setAttribute("role", "option");
				break;
			}

			if (sep) {
				continue;
			}

			uiItem.className = "f_menu_item";

			var image = f_core.CreateElement(uiItem, "img", {
				align : "middle",
				valign : "middle",
				border : 0,
				width : this._itemImageWidth,
				height : this._itemImageHeight,
				src : this._blankMenuImageURL,
				className : "f_menu_item_image"
			});
			fa_aria.SetElementAriaLabelledBy(image, this
					.fa_getMenuScopeName(parentItem)
					+ "::label");
			uiItem._icon = image;

			var div = doc.createElement("label");

			var classname = "f_menu_item_text";
			var value = this.f_getItemValue(item);

			if (value === fa_criteriaManager.DEFAULT_NULL_VALUE) {
				classname += "_empty";
			}
			div.className = classname;

			div.id = this.fa_getMenuScopeName(parentItem) + "::label";

			var accessKey = item._accessKey;
			if (accessKey) {
				uiItem._accessKey = accessKey;
				uiItem.accessKey = accessKey;
			}

			var label = this.f_getItemLabel(item);
			f_component.AddLabelWithAccessKey(div, label, accessKey);

			f_core.AppendChild(uiItem, div);

			var acceleratorKey = item._acceleratorKey;
			if (acceleratorKey) {
				var htmlAcceleratorKey = f_core.EncodeHtml(acceleratorKey);
				var accelV = doc.createElement("label");
				accelV.className = "f_menu_item_accelV";
				accelV.innerHTML = htmlAcceleratorKey;
				f_core.AppendChild(uiItem, accelV);

				var accel = doc.createElement("label");
				accel.className = "f_menu_item_accel";
				accel.innerHTML = htmlAcceleratorKey;
				f_core.AppendChild(uiItem, accel);
			}

			uiItem.onmouseover = fa_menuCore._MenuItem_mouseOver;
			uiItem.onmouseout = fa_menuCore._MenuItem_mouseOut;
			uiItem.onmousedown = fa_menuCore._MenuItem_mouseDown;
			uiItem.onclick = fa_menuCore._MenuItem_click;

			this.f_uiUpdateItemStyle(item, uiItem);
		}

		return uiPopup;
	},

	f_hasSelectionAutoClose : function() {
		return this._selectionAutoClose;
	},

	f_setSelectionAutoClose : function(autoClose) {
		this._selectionAutoClose = autoClose;
	},

	/**
	 * @method protected final
	 */
	f_getUIItem : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object"
				&& (!menuItem.nodeType || menuItem == this) && menuItem._menu,
				"fa_menuCore.f_getUIItem: Invalid menuItem parameter ("
						+ menuItem + ")");

		var key = fa_menuCore._ComputeKey(menuItem);
		var mi = this._uiMenuItems[key];
		f_core.Assert(mi, "fa_menuCore.f_getUIItem: No uiMenuItem for '"
				+ menuItem + "'.");
		return mi;
	},
	/**
	 * @method protected final
	 */
	f_getUIPopup : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object"
				&& (!menuItem.nodeType || menuItem == this) && menuItem._menu,
				"fa_menuCore.f_getUIPopup: Invalid menuItem parameter ("
						+ menuItem + ")");

		var key = fa_menuCore._ComputeKey(menuItem);
		var mi = this._uiMenuPopups[key];
		// f_core.Debug(fa_menuCore, "fa_menuCore.f_getUIPopup: For popup
		// '"+menuItem+"'
		// => "+mi);
		return mi;
	},
	/**
	 * @method protected final
	 * @return Boolean
	 */
	f_uiIsPopupOpened : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object"
				&& (!menuItem.nodeType || menuItem == this) && menuItem._menu,
				"fa_menuCore.f_uiIsPopupOpened: Invalid menuItem parameter ("
						+ menuItem + ")");

		return this.f_getUIPopup(menuItem) != null;
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_uiGetSelectedItem : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object"
				&& (!menuItem.nodeType || menuItem == this) && menuItem._menu,
				"fa_menuCore.f_uiGetSelectedItem: Invalid menuItem parameter ("
						+ menuItem + ")");

		var popup = this.f_getUIPopup(menuItem);
		if (!popup) {
			return null;
		}

		return popup._selectedMenuItem;
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	f_accessKeyMenuItem : function(menuItem, jsEvent) {
		f_core.Debug(fa_menuCore, "f_accessKeyMenuItem: menuItem='" + menuItem
				+ "' key=" + jsEvent.keyCode);

		var code = jsEvent.keyCode;

		var key = String.fromCharCode(code).toUpperCase();

		var parent = this.f_getParentItem(menuItem);

		var menuItems = this.f_listVisibleItemChildren(parent);
		for ( var i = 0; i < menuItems.length; i++) {
			menuItem = menuItems[i];

			var accessKey = this.f_getItemAccessKey(menuItem);

			f_core.Debug(fa_menuCore, "f_accessKeyMenuItem: Compare '"
					+ accessKey + "' and '" + key + "'.");
			if (accessKey != key) {
				continue;
			}

			if (this.f_isItemDisabled(menuItem)) {
				return;
			}

			this.f_uiSelectItem(menuItem);
			if (this.f_hasVisibleItemChildren(menuItem)) {
				this.f_openUIPopup(menuItem, jsEvent, true);
				return;
			}

			// Appel de la callback de selection
			var value = this.f_getItemValue(menuItem);
			this.f_performItemSelect(menuItem, value, jsEvent);
			return;
		}
	},
	/**
	 * @method protected final
	 * @return void
	 */
	f_uiSelectItem : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object" && !menuItem.nodeType
				&& menuItem._menu,
				"fa_menuCore.f_uiSelectItem: Invalid menuItem parameter ("
						+ menuItem + ")");

		var menuItemParent = this.f_getParentItem(menuItem);
		f_core.Assert(typeof (menuItemParent) == "object"
				&& (!menuItemParent.nodeType || menuItemParent == this)
				&& menuItemParent._menu,
				"fa_menuCore.f_uiSelectItem: Invalid menuItemParent parameter ("
						+ menuItemParent + ")");
		f_core.Assert(menuItemParent != menuItem,
				"fa_menuCore.f_uiSelectItem: Invalid menuItem, same as parent. (parent="
						+ menuItemParent + ")");

		var popup = this.f_getUIPopup(menuItemParent, true);
		f_core.Assert(popup,
				"fa_menuCore.f_uiSelectItem: Invalid popup for item="
						+ menuItemParent);

		var old = popup._selectedMenuItem;

		if (old == menuItem) {
			return;
		}

		popup._selectedMenuItem = menuItem;

		if (old) {
			var oldPopupItem = this.f_getUIPopup(old);

			if (oldPopupItem) {
				this.f_closeUIPopup(old);
			}

			this.f_uiUpdateItemStyle(old);
		}

		this.f_uiUpdateItemStyle(menuItem);

		var uiItem = this.f_getUIItem(menuItem);
		if (popup.scrollHeight > popup.clientHeight) {
			var scrollBody=popup;

			if (uiItem.offsetTop - scrollBody.scrollTop < 0) {
				scrollBody.scrollTop = uiItem.offsetTop;

			} else if (uiItem.offsetTop + uiItem.offsetHeight - scrollBody.scrollTop > scrollBody.clientHeight) {
				scrollBody.scrollTop = uiItem.offsetTop + uiItem.offsetHeight
						- scrollBody.clientHeight;
			}
		}

		var value = this.f_getItemValue(menuItem);
		var detail = f_event.NewDetail({
			uiItem: uiItem,
			uiPopup: popup
		});
		this.f_fireEvent("itemHover", null, menuItem, value, null, detail);
	},
	/**
	 * @method protected final
	 * @return void
	 */
	f_uiDeselectItem : function(menuItem) {
		f_core.Assert(typeof (menuItem) == "object" && !menuItem.nodeType
				&& menuItem._menu,
				"fa_menuCore.f_uiDeselectItem: Invalid menuItem parameter ("
						+ menuItem + ")");

		var menuItemParent = this.f_getParentItem(menuItem);
		f_core.Assert(typeof (menuItemParent) == "object"
				&& (!menuItemParent.nodeType || menuItemParent == this)
				&& menuItemParent._menu,
				"fa_menuCore.f_uiSelectItem: Invalid menuItemParent parameter ("
						+ menuItemParent + ")");
		f_core.Assert(menuItemParent != menuItem,
				"fa_menuCore.f_uiDeselectItem: Invalid menuItem, same as parent. (parent="
						+ menuItemParent + ")");

		var popup = this.f_getUIPopup(menuItemParent, true);
		f_core.Assert(popup,
				"fa_menuCore.f_uiSelectItem: Invalid popup for item="
						+ menuItemParent);

		var old = popup._selectedMenuItem;
		if (!old) {
			return;
		}

		popup._selectedMenuItem = undefined;

		this.f_uiUpdateItemStyle(old);
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            evt
	 * @return void
	 */
	f_nextMenuItem : function(menuItem, evt) {
		f_core.Debug(fa_menuCore, "f_nextMenuItem: menuItem=" + menuItem);

		var parent = this.f_getParentItem(menuItem);

		var menuItems = this.f_listVisibleItemChildren(parent);

		for ( var i = 0; i < menuItems.length; i++) {
			var item = menuItems[i];

			if (item != menuItem) {
				continue;
			}

			for ( var j = 0; j < menuItems.length; j++) {
				i++;
				if (i == menuItems.length) {
					i = 0;
				}

				item = menuItems[i];
				if (item._inputType == fa_items.AS_SEPARATOR) {
					continue;
				}

				this.f_uiSelectItem(item);
				return;
			}

			f_core.Debug(fa_menuCore,
					"f_nextMenuItem: can not find next menuItem !");
			return;
		}

		f_core.Debug(fa_menuCore,
				"f_nextMenuItem: can not find current menuItem !");
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            evt
	 * @return void
	 */
	f_previousMenuItem : function(menuItem, evt) {

		var parent = this.f_getParentItem(menuItem);

		var menuItems = this.f_listVisibleItemChildren(parent);

		for ( var i = 0; i < menuItems.length; i++) {
			var item = menuItems[i];

			if (item != menuItem) {
				continue;
			}

			for ( var j = 0; j < menuItems.length; j++) {
				i--;
				if (i < 0) {
					i = menuItems.length - 1;
				}

				item = menuItems[i];
				if (item._inputType == fa_items.AS_SEPARATOR) {
					continue;
				}

				this.f_uiSelectItem(item);
				break;
			}

			break;
		}
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	f_nextMenuItemLevel : function(menuItem, jsEvent) {

		if (this.f_isItemDisabled(menuItem)) {
			return;
		}

		if (!this.f_uiIsPopupOpened(menuItem)) {
			this.f_openUIPopup(menuItem, jsEvent, true);
			return;
		}

		var menuItems = this.f_listVisibleItemChildren(menuItem);

		if (menuItems && menuItems.length) {
			this.f_menuItem_over(menuItems[0], false, jsEvent);
		}
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            evt
	 * @return void
	 */
	f_previousMenuItemLevel : function(menuItem, evt) {
		var parent = this.f_getParentItem(menuItem);

		// Le parent est forcement ouvert !
		this.f_closeUIPopup(parent);
	},
	/**
	 * Selection de l'item par une touche ! (item root)
	 * 
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	f_keySelectMenuItem : function(menuItem, jsEvent) {
		f_core.Debug(fa_menuCore, "f_keySelectMenuItem: key select item '"
				+ menuItem + "'.");

		// Le menu est en mode ReadOnly ?
		if (this.f_isReadOnly()) {
			return;
		}

		// Notre menuItem root est-il desactivé ?
		if (this.f_isItemDisabled(menuItem)) {
			return;
		}

		// a t-il un popup ?
		// (cas d'un menubar)
		if (!this.f_hasVisibleItemChildren(menuItem)) {
			// Non ...

			// Appel de la callback de selection
			var value = this.f_getItemValue(menuItem);
			this.f_performItemSelect(menuItem, value, jsEvent);
			return;
		}

		// Notre vrai item selectionné a t-il des enfants ?
		if (this.f_hasVisibleItemChildren(menuItem)) {
			this.f_openUIPopup(menuItem, evt, true);
			return;
		}

		// pas d'enfants
		this.f_closeUIPopup(menuItem);

		// Appel de la callback de selection
		var value = this.f_getItemValue(menuItem);
		this.f_performItemSelect(menuItem, value, jsEvent);
	},
	/**
	 * Fermeture du menu par une touche !
	 * 
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            evt
	 * @return void
	 */
	f_keyCloseMenuItem : function(menuItem, evt) {
		f_core
				.Assert(!menuItem || typeof (menuItem) == "object",
						"fa_menuCore.f_keyCloseMenuItem: Item parameter must be an object !");

		if (!menuItem) {
			this.f_closeUIPopup(this);
			return;
		}

		var parentItem = this.f_getParentItem(menuItem);

		f_core.Debug(fa_menuCore, "f_keyCloseMenuItem: close parent '"
				+ parentItem + "' of item '" + menuItem + "'.");
		this.f_closeUIPopup(parentItem);
	},
	/**
	 * Returns parent of item.
	 * 
	 * @method protected final
	 * @param Object
	 *            menuItem
	 * @return Object its parent.
	 */
	f_getParentItem : function(item) {
		f_core
				.Assert(typeof (item) == "object",
						"fa_menuCore.f_getParentItem: Item parameter must be an object !");
		return item._parentItem;
	},
	/**
	 * Gestion du OVER d'un menuItem
	 * 
	 * @method protected final
	 * @param Object
	 *            menuItem
	 * @param Boolean
	 *            open
	 * @param Event
	 *            evt
	 * @param Boolean
	 *            autoSelect
	 * @return void
	 */
	f_menuItem_over : function(menuItem, open, evt, autoSelect) {
		var parent = this.f_getParentItem(menuItem);

		var parentPopup = this.f_getUIPopup(parent);
		if (!parentPopup) {
			return;
		}

		var oldMenuItem = this.f_uiGetSelectedItem(parent);
		f_core.Debug(fa_menuCore, "f_menuItem_over: " + menuItem + " parent="
				+ parent + " old=" + oldMenuItem + " open=" + open);

		// Un autre était déjà over ???
		if (oldMenuItem && oldMenuItem != menuItem) {
			// Eventuellement on ferme le popup !
			this.f_closeUIPopup(oldMenuItem);

			this.f_uiDeselectItem(oldMenuItem);
		}

		this.f_uiSelectItem(menuItem);

		/*
		 * On accepte les disabled over ! if (open && menuItem._disabled ) {
		 * return; }
		 */

		if (this.f_isItemDisabled(menuItem)) {
			return;
		}

		if (this.f_hasVisibleItemChildren(menuItem) && open) {
			this.f_openUIPopup(menuItem, evt, autoSelect);
		}
	},
	/**
	 * Gestion du OUT d'un menuItem
	 * 
	 * @method private
	 * @param Object
	 *            menuItem
	 * @return void
	 */
	_menuItem_out : function(menuItem) {
		if (this.f_uiIsPopupOpened(menuItem)) {
			return;
		}

		this.f_uiDeselectItem(menuItem);
	},
	/**
	 * Gestion du MOUSE BUTTON d'un menuItem
	 * 
	 * @method protected final
	 * @param Object
	 *            menuItem
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	f_menuItem_select : function(menuItem, jsEvent) {

		// Il a un popup ?
		if (this.f_hasVisibleItemChildren(menuItem)) {
			// On ignore
			return;
		}

		if (this.f_isItemDisabled(menuItem)) {
			return;
		}

		if (this.f_isReadOnly()) {
			this.f_closeUIPopup(menuItem);
			return;
		}

		var value = this.f_getItemValue(menuItem);

		this.f_performItemSelect(menuItem, value, jsEvent);
	},
	/**
	 * @method protected
	 */
	fa_updateItemStyle : function(item) {
		// On s'en fiche car le style est créé a chaque ouverture de popup !
	},

	f_uiUpdateItemStyle : function(item, uiItem) {
		if (item._inputType == fa_items.AS_SEPARATOR) {
			return;
		}

		if (!uiItem) {
			uiItem = this.f_getUIItem(item);
		}

		var parent = this.f_getParentItem(item);
		var selectedParentItem = this.f_uiGetSelectedItem(parent);
		var selected = (selectedParentItem == item);

		var disabled = this.f_isItemDisabled(item);

		// f_core.Debug(fa_menuCore, "f_uiUpdateItemStyle: item="+item+"
		// uiItem="+uiItem.id+" over="+selected+" disabled="+disabled+"
		// selectedParentItem="+selectedParentItem);

		var suffix = "";
		var imageURL = item._imageURL;
		var itemStyleClass = this.f_getItemStyleClass(item);

		var eventComponent = undefined;
		var parentComponent = parent._component;
		if (parentComponent && parentComponent.f_getEventElement) {
			eventComponent = parentComponent.f_getEventElement();
		}

		if (this.f_hasVisibleItemChildren(item)) {
			var popupOpened = this.f_uiIsPopupOpened(item);

			// f_core.Debug(fa_menuCore, "f_uiUpdateItemStyle:
			// popupOpened="+popupOpened+"
			// over="+selected);

			if (popupOpened || selected) {
				suffix += "_selected";
				fa_aria.SetElementAriaSelected(uiItem, true);
			} else {
				suffix += "_popup";

			}

			if (!selected) {
				uiItem.removeAttribute(fa_aria.ARIA_SELECTED);
			}

			if (disabled) {
				suffix += "_disabled";
				fa_aria.SetElementAriaDisabled(uiItem, true);
				var disabledImageURL = item._disabledImageURL;
				if (disabledImageURL) {
					imageURL = disabledImageURL;
				}
			} else {
				uiItem.removeAttribute(fa_aria.ARIA_DISABLED);
				var expandedImageURL = item._expandedImageURL;
				if (expandedImageURL) {
					imageURL = expandedImageURL;
				}
			}

		} else if (disabled) {
			suffix += "_disabled";
			fa_aria.SetElementAriaDisabled(uiItem, true);
			if (selected) {
				suffix += "_hover";
				if (eventComponent) {
					fa_aria.SetElementAriaActiveDescendant(eventComponent,
							uiItem.id);
				}
			}

			var disabledImageURL = item._disabledImageURL;
			if (disabledImageURL) {
				imageURL = disabledImageURL;
			}

		} else if (selected) {
			suffix += "_hover";
			if (eventComponent) {
				fa_aria.SetElementAriaActiveDescendant(eventComponent,
						uiItem.id);
			}
			var hoverImageURL = item._hoverImageURL;
			if (hoverImageURL) {
				imageURL = hoverImageURL;
			}

		} else if (item._checked) {
			var selectedImageURL = item._selectedImageURL;
			if (selectedImageURL) {
				imageURL = selectedImageURL;
			}
		}

		var itemClassName = "f_menu_item";
		var className = itemClassName;

		if (itemStyleClass) {
			className += " " + itemStyleClass;
		}

		if (suffix) {
			className += " " + itemClassName + suffix;

			if (itemStyleClass && itemStyleClass.indexOf(' ') < 0) {
				itemStyleClass += " " + itemStyleClass + suffix;
			}
		}

		// f_core.Debug(fa_menuCore, "f_uiUpdateItemStyle:
		// className="+className);

		if (uiItem.className != className) {
			uiItem.className = className;
		}

		var icon = uiItem._icon;
		if (icon) {
			var iconClassName = "f_menu_item_image";

			var suffix = "";
			if (!imageURL) {
				if (item._checked) {
					var style = item._inputType;

					if (style == fa_items.AS_CHECK_BUTTON) {
						suffix = "_check";

					} else if (style == fa_items.AS_RADIO_BUTTON) {
						suffix = "_radio";
					}
				}

				if (selected) {
					suffix += "_hover";
				}

				if (suffix) {
					iconClassName += " " + iconClassName + suffix;
				}
			}

			imageURL = (typeof (imageURL) == "string") ? "url('" + imageURL
					+ "')" : "";

			var iconStyle = icon.style;
			if (iconStyle.backgroundImage != imageURL) {
				iconStyle.backgroundImage = imageURL;
			}
			if (icon.className != iconClassName) {
				icon.className = iconClassName;
			}
		}
	},
	/**
	 * @method protected
	 * @param Object
	 *            item
	 * @param any
	 *            value
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	f_performItemSelect : function(item, value, jsEvent) {

		this.f_fireEvent(f_event.PRE_SELECTION, jsEvent, item, value);

		if (this.f_hasSelectionAutoClose() !== false) {
			this.f_closeAllPopups();
		}

		switch (item._inputType) {
		case fa_items.AS_CHECK_BUTTON:
			var state = this.f_isItemChecked(item);

			this.f_setItemChecked(item, !state);

			// Dans ce cas un event CHECK est envoyé !
			return;

		case fa_items.AS_RADIO_BUTTON:
			this.f_setItemChecked(item, true);

			// Dans ce cas un event CHECK est envoyé !
			return;
		}

		var selectionProvider = this.fa_getSelectionProvider();

		this.f_fireEvent(f_event.SELECTION, jsEvent, item, value,
				selectionProvider);

	},
	/**
	 * Returns the label of the item.
	 * 
	 * @method public
	 * @param any
	 *            item The value of the item, or the item object.
	 * @return String The label.
	 */
	f_getItemLabel : function(item) {
		if (typeof (item) != "object") {
			item = this.f_getItemByValue(item, true);
		}

		return item._label;
	},
	/**
	 * Set the label of the item.
	 * 
	 * @method public
	 * @param any
	 *            item The value of the item, or the item object.
	 * @param String
	 *            label Label of the item.
	 * @return void
	 */
	f_setItemLabel : function(item, label) {
		f_core.Assert(typeof (label) == "string",
				"f_setItemLabel: Label parameter is not a string !");
		if (typeof (item) != "object") {
			item = this.f_getItemByValue(item, true);
		}

		item._label = label;
	},
	/**
	 * Returns a list of items
	 * 
	 * @method public
	 * @param Object
	 *            item
	 * @return Object
	 */
	f_getCheckedItemInGroup : function(item) {
		return this.f_mapIntoGroup(this.f_getItemGroupName(item), function(itemValue) {
			if (this.f_isItemChecked(itemValue)) {
				return i;
			}
		});
	},
	/**
	 * @method public
	 * @param Object
	 *            item
	 * @return Object[]
	 */
	f_listAllInGroup : function(item) {
		return this.f_listGroup(this.f_getItemGroupName(item));
	},
	/**
	 * Remove all items
	 * 
	 * @method public
	 * @param Object
	 *            menuItem
	 * @return void
	 */
	f_removeAllItems : function(menuItem) {
		var items = menuItem._items;
		if (!items) {
			return;
		}

		menuItem._items = new Array;
	},
	fa_getRadioScope : function() {
		return this;
	},
	fa_destroyItems : function(items) {
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_preparePopup : function(menuItem) {
		f_core.Debug(fa_menuCore, "_preparePopup: Popup menu '" + menuItem
				+ "'");

		if (this.f_getUIPopup(menuItem)) {
			f_core.Debug(fa_menuCore, "_preparePopup: Popup menu '" + menuItem
					+ "' is already opened !");
			f_core.CancelJsEvent();
			return false;
		}

		// Efface tout si necessaire !
		if (menuItem._removeAllWhenShow) {
			// Effaces tous les items !
			// f_core.Debug(fa_menuCore, "_preparePopup: Remove all items of
			// '"+menuItem+"'.");

			this.f_removeAllItems(menuItem);
		}

		if (menuItem.f_isActionListEmpty
				&& !menuItem.f_isActionListEmpty(f_event.MENU)) {
			f_core.Debug(fa_menuCore,
					"_preparePopup: Call menu callbacks for menuItem '"
							+ menuItem + "'.");

			// Appel les callbacks !
			var evt = new f_event(this, f_event.MENU, null, menuItem, null,
					this.fa_getSelectionProvider());
			try {
				if (menuItem.f_fireEvent(evt) === false) {
					// Refuse l'affichage !

					f_core
							.Debug(fa_menuCore,
									"_preparePopup: One callback refuse to open the menu.");
					return false;
				}

			} finally {
				f_classLoader.Destroy(evt);
			}
		}

		return this.f_hasVisibleItemChildren(menuItem);
	},
	/**
	 * @method private
	 * @param Object
	 *            menuItem
	 * @return HTMLElement
	 */
	_getPopupContainer : function(menuItem) {
		var parent = this.f_getParentItem(menuItem);
		f_core.Debug(fa_menuCore, "_getPopupContainer: menuItem='" + menuItem
				+ "' parent='" + parent + "' this='" + this + "'");

		if (f_popup.Ie_enablePopup()) {
			if (this.fa_isRootMenuItem(parent)) {
				return f_popup.Ie_GetPopup(document);
			}

			var parentPopup = this.f_getUIPopup(parent);
			f_core.Debug(fa_menuCore, "_getPopupContainer: parentPopup="
					+ parentPopup);

			return f_popup.Ie_GetPopup(parentPopup.ownerDocument);
		}

		if (!parent) {
			return document.body;
		}

		var parentPopup = this.f_getUIPopup(parent);
		if (!parentPopup) {
			return document.body;
		}

		return parentPopup.ownerDocument.body;
	},
	/**
	 * @method protected
	 */
	_showPositionedMenuPopup : function(popup, positionInfos) {
	},
	/**
	 * @method hidden
	 * @param Event
	 *            jsEvent
	 * @param optional
	 *            Object positionInfos
	 * @param optional
	 *            number autoSelect
	 * @return Boolean
	 */
	f_open: function(jsEvent, positionInfos, autoSelect) {
		f_core.Debug(fa_menuCore, "f_open: Open menu " + this + ".");

		if (!positionInfos) {
			positionInfos = {
				position : f_popup.MOUSE_POSITION
			};
		}
		if (jsEvent) {
			positionInfos.jsEvent = jsEvent;
		}

		return this.f_openUIPopup(this, jsEvent, autoSelect, positionInfos);
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_openUIPopup : function(menuItem, jsEvent, autoSelect, positionInfos) {

		// On verifie l'AJAX !

		if (!this._preparePopup(menuItem)) {
			f_core.Debug(fa_menuCore, "f_openUIPopup: Prepare popup of '"
					+ menuItem.id + "' returns false !");
			return false;
		}

		var parentItem = this.f_getParentItem(menuItem);

		f_core.Debug(fa_menuCore, "f_openUIPopup: parentItem=" + parentItem
				+ " positionInfos=" + positionInfos + " autoSelect="
				+ autoSelect);

		f_core.Debug(fa_menuCore, "f_openUIPopup: selectionProvider="
				+ this.fa_getSelectionProvider());

		var container = this._getPopupContainer(menuItem);
		f_core.Assert(container,
				"fa_menuCore.f_openUIPopup: Invalid popup container !");
		f_core.Debug(fa_menuCore, "Get container of '" + menuItem
				+ "' returns '" + container + "'.");

		var popup = this.f_createPopup(container, menuItem);
		f_core.Assert(popup,
				"fa_menuCore.f_openUIPopup: Invalid popup object (container="
						+ container + ")");

		var parentPopup = undefined;
		if (parentItem) {
			parentPopup = this.f_getUIPopup(parentItem);
		}
		if (!parentPopup) {
			f_core.Debug(fa_menuCore, "f_openUIPopup: Register windows this="
					+ this + " menuItem=" + menuItem + " popup=" + popup);

			if (f_popup.RegisterWindowClick(this.fa_getPopupCallbacks(), this,
					popup, this.fa_getKeyProvider()) == false) {
				f_core.Debug(fa_menuCore, "f_openUIPopup: Register FAILED");
				return false;
			}
		} else {
			f_core.Debug(fa_menuCore, "f_openUIPopup: Child popup parentItem="
					+ parentItem + " this=" + this + " menuItem=" + menuItem
					+ " popup=" + popup);
		}

		try {
			if (menuItem != this) {
				this.f_uiUpdateItemStyle(menuItem);
			}

			var scopeName = this.fa_getMenuScopeName(menuItem);
			f_key.EnterScope(scopeName);

			if (!positionInfos) {
				positionInfos = {
					component : this.f_getUIItem(menuItem),
					position : f_popup.RIGHT_COMPONENT
				};
			}

			if (f_popup.Ie_enablePopup()) {
				f_popup.Ie_openPopup(popup._popupObject, positionInfos);

			} else {
				f_popup.Gecko_openPopup(popup._popupObject, positionInfos);
			}
			
			if (positionInfos.ariaOwns) {
				positionInfos.ariaOwns.setAttribute("aria-owns", popup._popupObject.id);
				
				popup._popupObject.setAttribute("aria-expanded", true);
			}

		} catch (x) {
			if (!parentItem) {
				f_popup.UnregisterWindowClick(this);
			}

			f_core.Debug(fa_menuCore, "f_openUIPopup: exception", x);
			throw x;
		}

		if (autoSelect) {
			var menuItems = this.f_listVisibleItemChildren(menuItem);

			if (menuItems && menuItems.length) {
				var selectItem = null;

				if (autoSelect < 0) {
					for ( var i = menuItems.length - 1; i >= 0; i--) {
						var item = menuItems[i];
						if (this.f_isItemDisabled(item)) {
							continue;
						}

						selectItem = item;
						break;
					}
				} else {
					for ( var i = 0; i < menuItems.length; i++) {
						var item = menuItems[i];
						if (this.f_isItemDisabled(item)) {
							continue;
						}

						selectItem = item;
						break;
					}
				}

				if (!selectItem) {
					selectItem = menuItems[0];
				}

				this.f_menuItem_over(selectItem, false, jsEvent);
			}
		}

		return true;
	},
	/**
	 * @method protected
	 * @param Object
	 *            menuItem
	 * @param optional
	 *            Object popup
	 * @return void
	 */
	f_closeUIPopup: function(menuItem, popup) {
		f_core
				.Assert(
						!popup
								|| this._uiMenuPopups[fa_menuCore
										._ComputeKey(menuItem)] == popup,
						"fa_menuCore.f_closeUIPopup: Invalid popup or menuItem. menuItem="
								+ menuItem + " popup=" + popup);

		if (window.__rcfaces_noclosemenu) {
			return;
		}

		if (!popup) {
			popup = this.f_getUIPopup(menuItem);

			if (!popup) {
				f_core.Debug(fa_menuCore, "f_closeUIPopup: Popup menu '"
						+ menuItem + "' is already closed !");
				return;
			}
		}

		/* TODO
		if (positionInfos.ariaOwns) {
			positionInfos.ariaOwns.setAttribute("aria-owns", popup._popupObject.id);
			
			popup._popupObject.setAttribute("aria-expanded", true);
		}
		*/

		if (popup._item === undefined) {
			// Déjà fermé !

			f_core.Debug(fa_menuCore, "f_closeUIPopup: Popup menu '" + menuItem
					+ "' is already closed !");
			return;
		}

		f_core.Debug(fa_menuCore, "f_closeUIPopup: menuItem=" + menuItem
				+ " popup=" + popup);

		var popupObject = popup._popupObject;
		popup._popupObject = undefined; // Object

		popup._item = undefined; // Object

		var selectedItem = popup._selectedMenuItem;

		f_core.Debug(fa_menuCore, "f_closeUIPopup: child selected="
				+ selectedItem);

		if (selectedItem) {
			popup._selectedMenuItem = undefined; // Object

			this.f_closeUIPopup(selectedItem);
		}

		var scopeName = this.fa_getMenuScopeName(menuItem);
		f_key.ExitScope(scopeName);

		if (menuItem != this) {
			this.f_uiUpdateItemStyle(menuItem);
		}

		if (f_popup.Ie_enablePopup()) {
			f_popup.Ie_closePopup(popupObject);

		} else {
			f_popup.Gecko_closePopup(popupObject);
		}

		var uiMenuItems = this._uiMenuItems;
		var items = this.f_listItemChildren(menuItem);
		for ( var i = 0; i < items.length; i++) {
			var item = items[i];

			var key = fa_menuCore._ComputeKey(item);
			var uiItem = uiMenuItems[key];
			if (!uiItem) {
				continue;
			}

			delete uiMenuItems[key];

			uiItem._item = undefined; // Object
			uiItem._icon = undefined; // HTMLImageElement
			uiItem.onclick = null;
			uiItem.onmousedown = null;
			uiItem.onmouseover = null;
			uiItem.onmouseout = null;

			f_core.VerifyProperties(uiItem);
		}

		var key = fa_menuCore._ComputeKey(menuItem);
		delete this._uiMenuPopups[key];
		// f_core.Debug(fa_menuCore, "f_closeUIPopup: remove one popup="+popup+"
		// menuItem="+menuItem);

		if (f_popup.Ie_enablePopup()) {
			f_popup.Ie_releasePopup(popupObject);

		} else {
			f_popup.Gecko_releasePopup(popupObject);
		}

		var parentPopup = undefined;
		var parentItem = this.f_getParentItem(menuItem);
		if (parentItem) {
			parentPopup = this.f_getUIPopup(parentItem);
		}

		if (!parentPopup) {
			f_popup.UnregisterWindowClick(this);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_closeAllPopups : function() {
		f_core.Debug(fa_menuCore, "f_closeAllPopups: close all popups");
		this.f_closeUIPopup(this);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_clickOutside : function() {
		f_core.Debug(fa_menuCore, "f_clickOutside: click outside !");

		this.f_closeAllPopups();
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_focusMenuItem : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_getSelectionProvider : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_tabKeySelection : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_getMenuScopeName : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_getKeyProvider : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @param Object
	 *            parent
	 * @return Boolean
	 */
	fa_isRootMenuItem : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @return Object
	 */
	fa_getPopupCallbacks : f_class.ABSTRACT
};

new f_aspect("fa_menuCore", __statics, __members, fa_groupName, fa_items,
		fa_aria);
