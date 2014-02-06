/*
 * $Id: f_grid.js,v 1.10 2013/12/16 13:07:19 jbmeslin Exp $
 */

/**
 * 
 * @class public abstract f_grid extends f_component, fa_disabled, fa_immediate,
 *        fa_pagedComponent, fa_subMenu, fa_commands, fa_selectionManager<String[]>,
 *        fa_scrollPositions, fa_additionalInformationManager, fa_droppable,
 *        fa_draggable, fa_autoScroll, fa_aria, fa_toolTipContainer,
 *        fa_tabIndex, fa_autoOpen
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.10 $ $Date: 2013/12/16 13:07:19 $
 */

var __statics = {
		
	/**
	 * @field private static final Boolean
	 */
	_GENERATE_HEADERS_ATTRIBUTE: false,

	/**
	 * @field hidden static final String
	 */
	_EMPTY_DATA_MESSAGE_ID_SUFFIX : "::emptyDataMessage",

	/**
	 * @field hidden static final String
	 */
	_DATA_BODY_SCROLL_ID_SUFFIX : "::dataBody_scroll",

	/**
	 * @field hidden static final String
	 */
	_DATA_TITLE_SCROLL_ID_SUFFIX : "::dataTitle_scroll",

	/**
	 * @field hidden static final String
	 */
	_DATA_TABLE_ID_SUFFIX : "::dataTable",

	/**
	 * @field hidden static final String
	 */
	_FIXED_HEADER_ID_SUFFIX : "::fixedHeader",

	/**
	 * @field private static final String
	 */
	_DEFAULT_ALIGNMENT : "left",

	/**
	 * @field private static final String[]
	 */
	_DEFAULT_ROW_STYLE_CLASSES : [ "f_grid_row_odd", "f_grid_row_even" ],

	/**
	 * @field protected static final Number
	 */
	IMAGE_WIDTH : 16,

	/**
	 * @field protected static final Number
	 */
	IMAGE_HEIGHT : 16,

	/**
	 * @field private static final Number
	 */
	_CURSOR_WIDTH : 8,

	/**
	 * @field private static final Number
	 */
	_COLUMN_MIN_WIDTH : 4,

	/**
	 * @field private static final Number
	 */
	_COLUMN_MAX_WIDTH : 4096,

	/**
	 * @field private static final Number
	 */
	_DRAG_TIMER : 25,

	/**
	 * @field private static final Number
	 */
	_DRAG_DELTA : 5,

	/**
	 * @field protected static final Boolean
	 */
	USE_BACKGROUND_IMAGE : false,

	/**
	 * @field private static final String
	 */
	_ROW_MENU_ID : "#row",

	/**
	 * @field private static final String
	 */
	_BODY_MENU_ID : "#body",

	/**
	 * @field private static final String
	 */
	_HEAD_MENU_ID : "#head",

	/**
	 * @field private static final Number
	 */
	_SORT_PADDING : 18,

	/**
	 * @field protected static final Number
	 */
	FULL_WAITING : 0,

	/**
	 * @field protected static final Number
	 */
	END_WAITING : 1,

	/**
	 * @field protected static final Number
	 */
	ROWS_WAITING : 2,

	/**
	 * @field private static
	 */
	_DragOldCursor : undefined,

	/**
	 * @field private static
	 */
	_DragColumn : undefined,

	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	RowMouseOver : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		var row = f_grid.GetRowFromEvent(this, evt, dataGrid);

		if (!row || row._over) {
			return;
		}

		row._over = true;
		dataGrid.fa_updateElementStyle(row);

		return true;
	},

	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	RowMouseOut : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		var row = f_grid.GetRowFromEvent(this, evt, dataGrid);

		if (!row || !row._over) {
			return;
		}

		row._over = false;
		dataGrid.fa_updateElementStyle(row);

		return true;
	},
	/**
	 * @method protected static
	 * @param HTMLElement
	 *            eventThisObject
	 * @param Event
	 *            evt
	 * @param f_grid
	 *            dataGrid
	 * @return Boolean
	 * @context object:dataGrid
	 */
	GetRowFromEvent : function(eventThisObject, evt, dataGrid) {

		var target=undefined;
		if (evt.target) {
			target = evt.target;

		} else if (evt.srcElement) {
			target = evt.srcElement;
		}

		if (!target || target.nodeType != f_core.ELEMENT_NODE) {
			return null;
		}

		for (; target && (target.nodeType==f_core.ELEMENT_NODE); target = target.parentNode) {

			if (target._dataGrid && target._dataGrid != dataGrid) {
				continue;
			}

			var tagName = target.tagName.toLowerCase();

			if (tagName == "tr" && target._dataGrid) {
				return target;
			}

			if (tagName == "td" || tagName == "th") {
				continue;
			}

			if (tagName == "input" || tagName == "a" || tagName == "label") {
				continue;
			}

			break;
		}

		return null;
	},

	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	RowMouseDown : function(evt) {
		var dataGrid = this._dataGrid;

		f_core.Debug(f_grid, "RowMouseDown: mouse down on row of '" + dataGrid
				+ "'");
		try {
			if (!evt) {
				evt = f_core.GetJsEvent(this);
			}

			if (dataGrid.f_getEventLocked(evt)) {
				f_core.Debug(f_grid, "RowMouseDown: event already locked");
				return false;
			}

			if (!f_grid.VerifyTarget(evt)) {
				f_core.Debug(f_grid, "RowMouseDown: invalid target");
				return true;
			}
			
			if (f_grid.IsTargetButton(this, evt)) {
				f_core.Debug(f_grid, "RowMouseDown: button target");
				return true;
			}

			if (dataGrid.f_isDisabled()
					|| (dataGrid.f_isReadOnly && dataGrid.f_isReadOnly())) {
				return f_core.CancelJsEvent(evt);
			}

			var sub = f_core.IsPopupButton(evt);

			var selection = fa_selectionManager.ComputeMouseSelection(evt);

			var selectOnMousedown = false;
			var srcElement = evt.target ? evt.target : evt.srcElement;
			if (srcElement && this._label
					&& srcElement.tagName == this._label.tagName) {
				selectOnMousedown = true;
			}

			var details = f_event.NewDetail();

			var cellRef = {};
			var col = dataGrid._searchColumnByElement(srcElement, cellRef);
			if (col) {
				details = dataGrid._fillColumnDetails(details, col, cellRef.value);
			}

			dataGrid._cursorCellIdx = undefined;

			dataGrid
					.f_moveCursor(this, true, evt, selection,
							fa_selectionManager.BEGIN_PHASE, selectOnMousedown,
							details);

			// On deplace le cursor avant de donner le focus !
			dataGrid.f_forceFocus();

			if (sub && this._selected) {
				var menu = dataGrid.f_getSubMenuById(f_grid._ROW_MENU_ID);
				if (menu) {
					if (menu.f_closeAllpopups) {
						menu.f_closeAllpopups();
					}
				}

			} else if (dataGrid._dragAndDropEngine) {
				dataGrid._dragRow(evt);
			}

			return f_core.CancelJsEvent(evt);
		} finally {
			f_core.Debug(f_grid, "RowMouseDown: mouse down on row of '"
					+ dataGrid + "' EXITED");
		}
	},

	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	RowMouseUp : function(evt) {
		var dataGrid = this._dataGrid;

		f_core.Debug(f_grid, "RowMouseUp: mouse up on row of '" + dataGrid
				+ "'");
		try {
			if (!evt) {
				evt = f_core.GetJsEvent(this);
			}

			if (dataGrid.f_getEventLocked(evt)) {
				f_core.Debug(f_grid, "RowMouseUp: event already locked");
				return false;
			}

			if (!f_grid.VerifyTarget(evt)) {
				f_core.Debug(f_grid, "RowMouseUp: invalid target");
				return true;
			}
			
			if (f_grid.IsTargetButton(this, evt)) {
				f_core.Debug(f_grid, "RowMouseUp: button target");
				return true;
			}

			if (dataGrid.f_isDisabled()
					|| (dataGrid.f_isReadOnly && dataGrid.f_isReadOnly())) {
				return f_core.CancelJsEvent(evt);
			}

			var srcElement = evt.target ? evt.target : evt.srcElement;

			var sub = f_core.IsPopupButton(evt);

			var selection = fa_selectionManager.ComputeMouseSelection(evt);

			var details = f_event.NewDetail();

			var cellRef = {};
			var col = dataGrid._searchColumnByElement(srcElement, cellRef);
			if (col) {
				details = dataGrid._fillColumnDetails(details, col, cellRef.value);
			}

			dataGrid._cursorCellIdx = undefined;

			dataGrid.f_moveCursor(this, true, evt, selection,
					fa_selectionManager.END_PHASE, undefined, details);

			// On deplace le cursor avant de donner le focus !
			dataGrid.f_forceFocus();

			if (sub && this._selected) {
				var menu = dataGrid.f_getSubMenuById(f_grid._ROW_MENU_ID);
				if (menu) {
					menu.f_open(evt, {
						position : f_popup.MOUSE_POSITION
					});
				}
			}

			return f_core.CancelJsEvent(evt);

		} finally {
			f_core.Debug(f_grid, "RowMouseUp: mouse up on row of '" + dataGrid
					+ "' EXITED");
		}
	},
	/**
	 * @method protected static
	 * @param Element
	 *            thiz
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context event:evt
	 */
	IsTargetButton : function(thiz, evt) {
		// On recherche si on tombe sur un composant qui capture la selection,
		// et ne provoque pas la selection de la ligne
		// if (thiz._dataGrid || thiz._row) {
		// return true;
		// }

		var target = undefined;
		if (evt.target) {
			target = evt.target;

		} else if (evt.srcElement) {
			target = evt.srcElement;
		}

		if (!target || target.nodeType != f_core.ELEMENT_NODE) {
			return true;
		}
		
		var tagName = target.tagName;
		if (tagName) {
			switch (tagName.toLowerCase()) {
			case "input":
			case "select":
				return true;
			}
		}
		
		return false;
	},
	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context event:evt
	 */
	VerifyTarget : function(evt) {
		if (this._dataGrid || this._row) {
			return true;
		}

		var target = undefined;
		if (evt.target) {
			target = evt.target;

		} else if (evt.srcElement) {
			target = evt.srcElement;
		}

		if (!target || target.nodeType != f_core.ELEMENT_NODE) {
			return true;
		}

		for (; target; target = target.parentNode) {

			if (target.nodeType != f_core.ELEMENT_NODE) {
				return true;
			}
			
			if (target._dataGrid || target._row) {
				return true;
			}

			var tagName = target.tagName;
			if (tagName) {
				switch (tagName.toLowerCase()) {
				case "input":
				case "select":
				case "a":
					return false;
				}
			}

			if (f_core.GetAttributeNS(target, "class")) {
				// Un objet RCFACES !

				f_core.Debug(f_grid, "VerifyTarget: Initialize target='"
						+ target + "'");

				var win = f_core.GetWindow(target.ownerDocument);
				var classLoader = f_classLoader.Get(win);

				var obj = classLoader.f_init(target, true, true);

				if (!obj) {
					continue;
				}

				if (obj.f_getFocusableElement && obj.f_getFocusableElement()) {
					// Il peut traiter le focus, on lui donne la main !
					return false;
				}

				// Notre focus ne traite pas le focus ... on passe au parent ...
				continue;
			}
		}

		return true;
	},
	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	FiltredCancelJsEventHandler : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		f_core.Debug(f_grid, "FiltredCancelJsEventHandler: Cancel event type='"
				+ evt.type + "' event='" + evt + "'.");

		return f_core.CancelJsEventHandler(evt);
	},
	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	RowMouseDblClick : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (f_core.IsPopupButton(evt) || dataGrid.f_isDisabled()
				|| !this._selected) {
			return f_core.CancelJsEvent(evt);
		}

		var srcElement = evt.target ? evt.target : evt.srcElement;

		var cellRef = {};
		var col = dataGrid._searchColumnByElement(srcElement, cellRef);

		var details = f_event.NewDetail();
		dataGrid._fillColumnDetails(details, col, cellRef.value);

		dataGrid.f_fireEvent(f_event.DBLCLICK, evt, this, this._index,
				dataGrid, details);

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_BodyMouseDown : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		var sub = f_core.IsPopupButton(evt);
		if (!sub) {
			return f_core.CancelJsEvent(evt);
		}

		dataGrid.f_forceFocus();

		var menuId = f_grid._BODY_MENU_ID;

		// S'il y a une seule selection, on bascule en popup de ligne !
		if (this.f_isSelectable() && dataGrid._currentSelection.length) {
			menuId = f_grid._ROW_MENU_ID;
		}

		var menu = dataGrid.f_getSubMenuById(menuId);
		if (menu) {
			if (menu.f_closeAllpopups) {
				menu.f_closeAllpopups();
			}
		}

		return f_core.CancelJsEvent(evt);
	},

	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_BodyMouseUp : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		var sub = f_core.IsPopupButton(evt);
		if (!sub) {
			return f_core.CancelJsEvent(evt);
		}

		dataGrid.f_forceFocus();

		var menuId = f_grid._BODY_MENU_ID;

		// S'il y a une seule selection, on bascule en popup de ligne !
		if (this.f_isSelectable() && dataGrid._currentSelection.length) {
			menuId = f_grid._ROW_MENU_ID;
		}

		var menu = dataGrid.f_getSubMenuById(menuId);
		if (menu) {
			menu.f_open(evt, {
				position : f_popup.MOUSE_POSITION
			});
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method protected static
	 * @param HTMLTableElement
	 *            element
	 * @param optional
	 *            Boolean assertIfNotFound
	 * @return HTMLTableRowElement
	 */
	GetFirstRow : function(element, assertIfNotFound) {
		f_core.Assert(element && element.tagName.toLowerCase() == "table",
				"f_grid.GetFistRow: Invalid table parameter (" + element + ")");

		var child = element.firstChild;
		for (; child; child = child.nextSibling) {
			switch (child.tagName.toLowerCase()) {
			case "tbody":
				if (child.firstChild) {
					return child.rows[0];
				}
				break;

			case "tr":
				return child;
			}
		}

		f_core.Assert(!assertIfNotFound,
				"f_grid.GetFirstRow: Component TR not found from '" + element
						+ "' !");
		return null;
	},
	/**
	 * @method protected static
	 * @param HTMLTableElement
	 *            element
	 * @return HTMLTableRowElement
	 */
	ListRows : function(element) {
		f_core.Assert(element && element.tagName.toLowerCase() == "table",
				"f_grid.ListRows: Invalid table parameter (" + element + ")");

		var copy=0;
		var rows = null;
		var child = element.firstChild;
		for (; child; child = child.nextSibling) {
			switch (child.tagName.toLowerCase()) {
			case "thead":
			case "tbody":
				if (child.firstChild) {
					if (copy==0) {
						var rs=child.rows;
						if (rs) {
							copy=1;
							rows=rs;
						}
					} else {
						if (copy==1) {
							rows=f_core.PushArguments(null, rows);
							copy=2;
						}
						f_core.PushArguments(rows, child.rows);
					}
				}
				break;

			case "tr":
				if (!rows) {
					rows = new Array;
					copy=2;

				} else if (copy==1) {
					rows=f_core.PushArguments(null, rows);
					copy=2;
				}

				rows.push(child);
			}
		}

		if (!rows) {
			return [];
		}
		return rows;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onfocus : function(evt) {
		var dataGrid = this._dataGrid;
		if (!dataGrid) {
			dataGrid=this.parentNode._dataGrid;
		}
		try {
			if (dataGrid._ignoreFocus) {
				return false;
			}

			f_core.Debug(f_grid, "_Link_onfocus: Get focus for '" + dataGrid
					+ "'");

			if (!evt) {
				evt = f_core.GetJsEvent(this);
			}

			if (!dataGrid._loading && dataGrid.f_getEventLocked(evt, false)) {
				return false;
			}

			if (!f_grid.VerifyTarget(evt)) {
				return true;
			}

			if (dataGrid._focus) {
				return true;
			}

			dataGrid._focus = true;

			if (dataGrid.f_isSelectable()) {
				if (dataGrid._focusOnInput) {
					var oldCursor = dataGrid._cursor;
					
					var row = f_grid.GetRowFromEvent(dataGrid, evt, dataGrid);
					if (row) {
						dataGrid._cursor=row;
	
						if (oldCursor) {
							dataGrid.fa_updateElementStyle(oldCursor);
						}
					}
					
				} else if (!dataGrid._cursor) {
					var currentSelection = dataGrid._currentSelection;
					if (currentSelection.length) {
						dataGrid._cursor = currentSelection[0];
						dataGrid._initCursorValue = undefined;
					}

					if (!dataGrid._cursor && dataGrid._table) {
						var tr = f_grid.GetFirstRow(dataGrid._table);

						if (tr) {
							dataGrid._cursor = tr;
							dataGrid._initCursorValue = undefined;
						}
					}
				}

				dataGrid._updateCurrentSelection();

			} else if (!dataGrid._cursor && dataGrid._table) {
				var tr = f_grid.GetFirstRow(dataGrid._table);

				if (tr) {
					dataGrid._cursor = tr;
					dataGrid._initCursorValue = undefined;
				}
			}

			var cursor = dataGrid._cursor;
			if (cursor) {
				dataGrid.fa_updateElementStyle(cursor);

				dataGrid.fa_showElement(cursor, true);
			}

			dataGrid.f_fireEvent(f_event.FOCUS, evt);

		} catch (x) {
			f_core.Error(f_grid, "_Link_onfocus: throws exception.", x);
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onblur : function(evt) {
		var dataGrid = this._dataGrid;
		if (!dataGrid) {
			dataGrid=this.parentNode._dataGrid;
		}

		try {
			if (dataGrid._ignoreFocus) {
				return true;
			}

			f_core.Debug(f_grid, "_Link_onblur: Lost focus for '" + dataGrid
					+ "'");

			if (!evt) {
				evt = f_core.GetJsEvent(this);
			}

			// On bloque pas le "blur" car lors d'une ouverture d'une popup, il
			// faut le traiter !
			// if (dataGrid.f_getEventLocked(evt, false)) {
			// return false;
			// }

			if (!dataGrid._focus) {
				return true;
			}

			dataGrid._focus = false;

			dataGrid._updateCurrentSelection();

			dataGrid.f_fireEvent(f_event.BLUR, evt);

		} catch (x) {
			f_core.Error(f_grid, "_Link_onfocus: throws exception.", x);
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onkeypress : function(evt) {
		var dataGrid = this._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		var code = evt.charCode;
		if (code === undefined) {
			code = evt.keyCode;
		}

		if (evt.keyCode == f_key.VK_TAB) {
			return true;

		} else if (f_key.IsPrintable(code)) {
			return dataGrid.f_fireEvent(f_event.KEYPRESS, evt);
			// return true;
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onkeydown : function(evt) {
		var dataGrid = this._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		// On peut vouloir faire PAGE-DOWN/UP avec un repeat ! => pas de boite
		// d'alerte !

		var showAlert = !dataGrid._waitingLoading
				&& !f_key.IsModifierKey(evt.keyCode);

		if (dataGrid.f_getEventLocked(evt, showAlert)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (!dataGrid._focus) {
			return true;
		}

		return dataGrid.f_fireEvent(f_event.KEYDOWN, evt);
		// return dataGrid.f_performKeyDown(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onkeyup : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		// On peut vouloir faire PAGE-DOWN/UP avec un repeat ! => pas de boite
		// d'alerte !
		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (!dataGrid._focus) {
			return true;
		}

		return dataGrid.f_fireEvent(f_event.KEYUP, evt);

		// return dataGrid.f_performKeyDown(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Link_onmousewheel : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			// Il faut bloquer ... sinon ca risque de scroller la page !
			return f_core.CancelJsEvent(evt);
		}

		if (!dataGrid._focus) {
			return true;
		}

		if (f_core.GetBooleanAttributeNS(dataGrid, "wheelSelection", true) == false) {
			return true;
		}

		var wheel = evt.wheelDelta;

		if (f_core.IsGecko()) {
			wheel = -evt.detail;
		}

		if (wheel > 0) {
			dataGrid._previousCursorRow(evt);

		} else if (wheel < 0) {
			dataGrid._nextCursorRow(evt);
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Title_onMouseOver : function(evt) {
		var column = this._column;
		if (!column) {
			return false;
		}
		var dataGrid = column._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)
				|| !dataGrid._columnCanBeSorted) {
			return false;
		}

		// En drag ?
		if (f_grid._DragColumn) {
			return false;
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		if (dataGrid._columnOver == column) {
			return true;
		}

		var oldColumn = dataGrid._columnOver;

		dataGrid._columnOver = column;

		if (oldColumn) {
			dataGrid._updateTitleStyle(oldColumn);
		}

		dataGrid._updateTitleStyle(column);

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Title_onMouseOut : function(evt) {
		var column = this._column;
		if (!column) {
			return false;
		}

		var dataGrid = column._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		// En drag ?
		if (f_grid._DragColumn) {
			return false;
		}

		// if (dataGrid.f_isDisabled() || !dataGrid._columnCanBeSorted ||
		// !column._method) {
		// return false;
		// }

		if (dataGrid._columnSelected == column) {
			dataGrid._columnSelected = null;
		}

		if (dataGrid._columnOver != column) {
			return true;
		}

		dataGrid._columnOver = null;

		dataGrid._updateTitleStyle(column);

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_Title_onMouseDown : function(evt) {

		var column = this._column;
		if (!column) {
			return false;
		}

		var dataGrid = column._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		f_core.Debug(f_grid, "_Title_onMouseDown: perform event " + evt);

		if (evt.preventDefault) {
			evt.preventDefault();
		} else {
			evt.returnValue = false;
		}

		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		var sub = f_core.IsPopupButton(evt);
		if (sub) {
			var menuId = f_grid._HEAD_MENU_ID;
			if (column._menuPopupId) {
				menuId = column._menuPopupId;
			}
			var menu = dataGrid.f_getSubMenuById(menuId);

			if (menu) {
				menu.f_open(evt, {
					position : f_popup.MOUSE_POSITION
				});
			}
			return f_core.CancelJsEvent(evt);
		}

		// alert("CB="+dataGrid._columnCanBeSorted);
		if (!dataGrid._columnCanBeSorted || !column._method) {
			// NON au CancelJsEvent on veut pouvoir attraper sur le TitleClick

			if (column.f_isActionListEmpty(f_event.SELECTION)) {
				// pas de TitleClick ... cancel alors !
				return f_core.CancelJsEvent(evt);
			}
		}

		f_core.Debug(f_grid, "_Title_onMouseDown: select column='" + column
				+ "'");

		dataGrid._columnSelected = column;
		dataGrid._updateTitleStyle(column);

		if (f_core.IsGecko()) {
			f_grid._TitleReleaseTimer();

			// GROS BUG firefox, il existe une zone en dessous de la baseline
			// qui n'envoie pas de CLICK !
			f_grid._TitleClicked = false;
			f_grid._TitleMouseDownTime = new Date().getTime();
		}

		return true;
	},
	/**
	 * @method private static
	 * @return void
	 */
	_TitleReleaseTimer : function() {
		var id = f_grid._TitleWaitClickTimerId;
		if (!id) {
			return;
		}
		f_grid._TitleWaitClickTimerId = undefined;
		window.clearTimeout(id);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:column
	 */
	_Title_onKeyDown : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		var ascending = undefined;
		var cancel = undefined;

		var code = evt.keyCode;
		switch (code) {
		case f_key.VK_DOWN: // FLECHE VERS LE BAS
			ascending = false;
			break;

		case f_key.VK_UP: // FLECHE VERS LE HAUT
			ascending = true;
			break;

		case f_key.VK_HOME:
			var columns = dataGrid._columns;
			for ( var i = 0; i < columns.length; i++) {
				var cl = columns[i];

				if (!cl._visibility) {
					continue;
				}

				if (cl != column) {
					dataGrid.f_setFocusColumn(cl);
				}
				break;
			}
			cancel = true;
			break;

		case f_key.VK_END:
			var columns = dataGrid._columns;
			for ( var i = columns.length - 1; i >= 0; i--) {
				var cl = columns[i];

				if (!cl._visibility) {
					continue;
				}

				if (cl != column) {
					dataGrid.f_setFocusColumn(cl);
				}
				break;
			}
			cancel = true;
			break;

		case f_key.VK_LEFT:
			var columns = dataGrid._columns;
			var pred = null;
			for ( var i = 0; i < columns.length; i++) {
				var cl = columns[i];

				if (!cl._visibility) {
					continue;
				}

				if (cl == column) {
					if (pred) {
						dataGrid.f_setFocusColumn(pred);
					}
					break;
				}

				pred = cl;
			}
			cancel = true;
			break;

		case f_key.VK_RIGHT:
			var columns = dataGrid._columns;
			var next = false;
			for ( var i = 0; i < columns.length; i++) {
				var cl = columns[i];

				if (!cl._visibility) {
					continue;
				}

				if (next) {
					dataGrid.f_setFocusColumn(cl);
					break;
				}
				next = (cl == column);
			}
			break;
		}

		if (f_core.IsGecko()) {
			f_grid._TitleReleaseTimer();

			// GROS BUG firefox, il existe une zone en dessous de la baseline
			// qui n'envoie pas de CLICK !
			f_grid._TitleClicked = false;
		}

		if (ascending !== undefined) {
			if (column.f_fireEvent(f_event.SELECTION, evt, column, ascending,
					dataGrid) === false) {
				return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
			}

			var append = (evt.shiftKey);

			dataGrid.f_setColumnSort(column, ascending, append);
			cancel = true;
		}

		if (cancel) {
			return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:column
	 */
	_Title_onFocus : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		// S'il y a un scroll, il faut l'aligner ...
		if (dataGrid._scrollTitle) {

			var _dataGrid = dataGrid;

			window.setTimeout(function() {
				if (window._rcfacesExiting) {
					return false;
				}

				var scrollTitleLeft = _dataGrid._scrollTitle.scrollLeft;

				var scrollBodyLeft = _dataGrid._scrollBody.scrollLeft;

				if (scrollTitleLeft != scrollBodyLeft) {
					_dataGrid._scrollBody.scrollLeft = scrollTitleLeft;
				}

				_dataGrid = null;
			}, 50);
		}

		if (dataGrid.f_isDisabled()) {
			return f_core.CancelJsEvent(evt);
		}

		if (column._focus) {
			return true;
		}

		column._focus = true;

		if (column.f_fireEvent(f_event.FOCUS, evt, column, null, dataGrid) === false) {
			return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:column
	 */
	_Title_onBlur : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (!column._focus) {
			return true;
		}

		column._focus = false;

		if (column.f_fireEvent(f_event.BLUR, evt, null, null, dataGrid) === false) {
			return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:column
	 */
	_Title_onClick : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (f_grid._TitleClicked !== undefined) {
			f_grid._TitleReleaseTimer();

			f_core.Debug(f_grid, "_Title_onClick: already clicked ? =>"
					+ f_grid._TitleClicked);

			if (f_grid._TitleClicked) { // Que Firefox
				return false;
			}
			f_grid._TitleClicked = true;
		}

		f_core.Debug(f_grid, "_Title_onClick: perform event " + evt);

		if (dataGrid.f_getEventLocked(evt, false)) {
			f_core
					.Debug(f_grid,
							"_Title_onClick: getEventLocked returns FALSE");
			return false;
		}

		if (dataGrid.f_isDisabled()) {
			f_core.Debug(f_grid,
					"_Title_onClick: Datagrid is disabled, stop it");
			return f_core.CancelJsEvent(evt);
		}

		if (column.f_fireEvent(f_event.SELECTION, evt, null, null, dataGrid) === false) {

			f_core.Debug(f_grid,
					"_Title_onClick: event Selection returns false");

			return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
		}

		if (!dataGrid._columnCanBeSorted || !column._method) {
			return f_core.CancelJsEvent(evt);
		}
		
		var append = (evt.shiftKey);

		f_core.Debug(f_grid, "_Title_onClick: call set column sort append="
				+ append);

		dataGrid.f_setColumnSort(column, undefined, append);

		return f_core.CancelJsEvent(evt);// On bloque le FOCUS !
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:column
	 */
	_Title_onMouseUp : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		f_core.Debug(f_grid, "_Title_onMouseUp: perform event " + evt);

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		/*
		 * if (dataGrid.f_isDisabled() / * || !dataGrid._columnCanBeSorted * /) {
		 * return f_core.CancelJsEvent(evt); }
		 */

		var oldColumn = dataGrid._columnSelected;
		if (!oldColumn) {
			return f_core.CancelJsEvent(evt);
		}

		f_core.Debug(f_grid, "_Title_onMouseUp: deselect old column='"
				+ dataGrid._columnSelected + "'");

		dataGrid._columnSelected = undefined;

		dataGrid._updateTitleStyle(column);

		f_grid._TitleReleaseTimer();

		var date = f_grid._TitleMouseDownTime; // Que pour Firefox
		f_core.Debug(f_grid, "_Title_onMouseUp: date=" + date + "  buttons="
				+ f_core.GetEvtButton(evt));

		if (date && f_core.GetEvtButton(evt) == f_core.LEFT_MOUSE_BUTTON) {
			var buttons = evt.which;
			var ctrlKey = evt.ctrlKey;
			var altKey = evt.altKey;
			var shiftKey = evt.shiftKey;
			var metaKey = evt.metaKey;
			var detail = evt.detail;

			var now = new Date().getTime();
			var delta = now - date;

			f_core.Debug(f_grid, "_Title_onMouseUp: delta=" + delta
					+ ", start timeout callback");

			if (delta < 1000) {
				// Il faut que le DOWN et UP se fasse en moins de 1s !
				f_grid._TitleWaitClickTimerId = window.setTimeout(function() {
					if (window._rcfacesExiting) {
						return;
					}
					if (!f_grid._TitleWaitClickTimerId) {
						return;
					}

					f_core.Debug(f_grid,
							"_Title_onMouseUp: Timeout callback wake up !");

					var doc = dataGrid.ownerDocument;
					var evt = doc.createEvent('MouseEvents');
					evt.initMouseEvent('click', true, true, doc.defaultView,
							detail, 0, 0, 0, 0, ctrlKey, altKey, shiftKey,
							metaKey, buttons, null);

					f_grid._TitleVerifyClick(dataGrid, column, evt);
				}, 200);
			}
		}
	},
	/**
	 * @method private static
	 */
	_TitleVerifyClick : function(dataGrid, column, evt) {
		if (f_grid._TitleClicked) {
			f_core
					.Debug(f_grid,
							"_TitleVerifyClick: ignore verification ... click already performed !");

			return false;
		}
		f_grid._TitleClicked = true;

		f_core.Debug(f_grid, "_TitleVerifyClick: perform verify click !");

		if (column.f_fireEvent(f_event.SELECTION, evt, null, null, dataGrid) === false) {

			f_core.Debug(f_grid,
					"_TitleVerifyClick: event Selection returns false");

			return;// On bloque le FOCUS !
		}

		var append = (evt.shiftKey);

		f_core.Debug(f_grid, "_TitleVerifyClick: call set column sort append="
				+ append);

		dataGrid.f_setColumnSort(column, undefined, append);
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_OnScroll : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		var scrollBody = dataGrid._scrollBody;

		if (dataGrid._scrollTitle) {
			var scrollTitleLeft = dataGrid._scrollTitle.scrollLeft;
			var scrollBodyLeft = scrollBody.scrollLeft;

			if (scrollTitleLeft != scrollBodyLeft) {
				dataGrid._scrollTitle.scrollLeft = scrollBodyLeft;
			}
		}

		return dataGrid._verifyWaitingPosition(evt);
	},
	/**
	 * @method protected static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	GotFocus : function(evt) {
		var dataGrid = this._dataGrid;
		var row = this._row;

		if (!dataGrid && row) {
			dataGrid = row._dataGrid;
		}

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (row) {
			dataGrid.f_forceFocus(row);
			return true;
		}

		dataGrid.f_forceFocus(dataGrid);
		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_TitleCursorMouseDown : function(evt) {
		var column = this._column;
		var dataGrid = column._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}

		var doc = dataGrid.ownerDocument;

		f_core.AddEventListener(doc, "mousemove", f_grid._TitleCursorDragMove,
				dataGrid);
		f_core.AddEventListener(doc, "mouseup", f_grid._TitleCursorDragStop,
				dataGrid);

		f_core.CancelJsEvent(evt);

		var eventPos = f_core.GetJsEventPosition(evt, doc);
		dataGrid._dragEventPos = eventPos;
		var cursorPos = f_core.GetAbsolutePosition(this);
		dataGrid._dragDeltaX = eventPos.x - cursorPos.x
				+ dataGrid._scrollTitle.scrollLeft;

		f_grid._DragColumn = column;

		var ths = dataGrid._title.getElementsByTagName("th");
		// var c=this.style.cursor;
		for ( var i = 0; i < ths.length; i++) {
			ths[i].oldCursorStyle = ths[i].style.cursor;
			ths[i].style.cursor = "e-resize";
		}

		f_grid._DragOldCursor = doc.body.style.cursor;
		doc.body.style.cursor = "e-resize";

		return false;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context event:evt
	 */
	_TitleCursorDragMove : function(evt) {
		try {
			var column = f_grid._DragColumn;
			if (!column) {
				return false;
			}

			var dataGrid = column._dataGrid;
			if (!evt) {
				evt = f_core.GetJsEvent(this);
			}

			var doc = dataGrid.ownerDocument;

			var eventPos = f_core.GetJsEventPosition(evt, doc);
			var cursorPos = f_core.GetAbsolutePosition(column._cursor);

			var dw = eventPos.x - cursorPos.x
					+ dataGrid._scrollTitle.scrollLeft - dataGrid._dragDeltaX;

			f_grid._DragCursorMove(dataGrid, column, dw);

		} catch (x) {
			f_core.Error(f_grid, "_TitleCursorDragMove: exception", x);
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @return void
	 * @context window:this
	 */
	_DragMoveTimer : function() {
		if (window._rcfacesExiting) {
			return;
		}

		try {
			var column = f_grid._DragColumn;
			if (!column) {
				return;
			}

			var dataGrid = column._dataGrid;

			var dw = 0;
			if (dataGrid._dragDeltaX > 0) {
				dw = -f_grid._DRAG_DELTA;
			} else {
				// dw=f_grid._DRAG_DELTA;
			}

			f_core.Debug(f_grid, "_DragMoveTimer dw='" + dw + "'");

			if (dw) {
				f_grid._DragCursorMove(dataGrid, column, dw);

				var cursorPos = f_core
						.GetAbsolutePosition(f_grid._DragColumn._cursor);
				dataGrid._dragDeltaX = dataGrid._dragEventPos.x - cursorPos.x
						+ dataGrid._scrollTitle.scrollLeft;
			}

		} catch (x) {
			f_core.Error(f_grid, "_DragMoveTimer: exception", x);
		}
	},
	/**
	 * @method private static
	 * @return Number
	 */
	_DragCursorMove : function(dataGrid, column, dw) {

		var doc = dataGrid.ownerDocument;

		if (dataGrid._dragTimerId) {
			f_core.GetWindow(doc).clearTimeout(dataGrid._dragTimerId);
			dataGrid._dragTimerId = undefined;
		}

		var head = column._head;
		var hw = parseInt(head.style.width);
		var w = hw + dw;

		f_core.Debug(f_grid, "_DragCursorMove: dw=" + dw + " w=" + w
				+ " columnOffsetWidth=" + column._col.offsetWidth);

		// document.title="W="+w+"/"+dw;

		if (w < column._minWidth) {
			w = column._minWidth;
		}
		if (w > column._maxWidth) {
			w = column._maxWidth;
		}

		dw = w - hw;

		if (dw == 0) {
			return 0;
		}

		var tcol = column._tcol;
		var col = column._col;
		var tableOffsetWidth = dataGrid._table.offsetWidth;

		var twidth = 0;
		if (column._ascendingOrder !== undefined) {
			twidth -= dataGrid._sortPadding;
		}

		if (false && f_core.IsInternetExplorer()) {
			// AVANT !
			if (tableOffsetWidth) {
				dataGrid._table.style.width = (tableOffsetWidth + dw) + "px";
			}

			col.style.width = w + "px";
			head.style.width = w + "px";

			column._widthSetted = w;

			var bw = w - this._textLeftRightPadding;
			if (bw < 0) {
				bw = 0;
			}
			column._box.style.width = bw + "px";

			var lw = bw + twidth;
			if (lw < 0) {
				lw = 0;
			}
			column._label.style.width = lw + "px";

		} else {
			if (tcol) {
				// tcol.style.width=w+"px";
			}

			var cellMargin = 0;

			col.style.width = w + "px"; // Colonne Des données ...

			column._widthSetted = w; // On desactive le calcul automatique
										// pour cette colonne ...
			column._widthPercent = undefined;
			column._widthComputed = undefined;

			var w1 = w - cellMargin;
			w1 = ((w1 > 0) ? w1 : 0);
			head.style.width = w1 + "px";

			var w2 = w - dataGrid._textLeftRightPadding;
			w2 = ((w2 > 0) ? w2 : 0);
			column._box.style.width = w2 + "px";
			column._box.style.maxWidth = w2 + "px";

			var w3 = w - dataGrid._textLeftRightPadding + twidth;
			w3 = ((w3 > 0) ? w3 : 0);
			column._label.style.width = w3 + "px";
			column._label.style.maxWidth = w3 + "px";

			f_core.Debug(f_grid, "_DragCursorMove: set head.width=" + w1
					+ "  box.w=" + w2 + "  label.w=" + w3 + " w=" + w
					+ " tlrp=" + dataGrid._textLeftRightPadding + " twidth="
					+ twidth);

			var totalCols = 0;
			var columns = dataGrid._columns;
			for ( var i = 0; i < columns.length; i++) {
				var cl = columns[i];

				if (!cl._visibility) {
					continue;
				}

				f_core.Assert(cl._col,
						"f_grid._DragCursorMove: Invalid column '" + cl + "'.");

				totalCols += parseInt(cl._col.style.width, 10);
			}

			dataGrid._table.style.width = (totalCols) + "px";

			f_core.Debug(f_grid, "_DragCursorMove: set total="+totalCols);
		}

		var scrollTitle = dataGrid._scrollTitle;
		var scrollBody = dataGrid._scrollBody;

		if (scrollTitle) {
			var scrollTitleLeft = scrollTitle.scrollLeft;
			var scrollBodyLeft = scrollBody.scrollLeft;

			if (scrollTitleLeft != scrollBodyLeft) {
				scrollTitle.scrollLeft = scrollBodyLeft;
			}
		}

		f_core.Debug(f_grid, "_DragCursorMove: scrollLeft="
				+ scrollBody.scrollLeft + " clientWidth="
				+ scrollBody.clientWidth + " scrollWidth="
				+ scrollBody.scrollWidth + " offsetWidth="
				+ scrollBody.offsetWidth);

		if (scrollBody.scrollLeft > 0
				&& scrollBody.scrollWidth == scrollBody.clientWidth
						+ scrollBody.scrollLeft) {
			dataGrid._dragTimerId = f_core.GetWindow(doc).setTimeout(
					f_grid._DragMoveTimer, f_grid._DRAG_TIMER);
		}

		return dw;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 * @context event:evt
	 */
	_TitleCursorDragStop : function(evt) {
		try {
			var column = f_grid._DragColumn;
			if (!column) {
				// Cela peut survenir si les stops sont enchainés ....
				return false;
			}

			var dataGrid = column._dataGrid;

			var doc = dataGrid.ownerDocument;

			if (dataGrid._dragTimerId) {
				f_core.GetWindow(doc).clearTimeout(dataGrid._dragTimerId);
				dataGrid._dragTimerId = undefined;
			}

			f_core.RemoveEventListener(doc, "mousemove",
					f_grid._TitleCursorDragMove, dataGrid);
			f_core.RemoveEventListener(doc, "mouseup",
					f_grid._TitleCursorDragStop, dataGrid);

			doc.body.style.cursor = f_grid._DragOldCursor;
			f_grid._DragOldCursor = undefined;

			var ths = dataGrid._title.getElementsByTagName("th");
			for ( var i = 0; i < ths.length; i++) {
				ths[i].style.cursor = ths[i].oldCursorStyle;
				ths[i].oldCursorStyle = undefined;
			}

			column._restoreClass = undefined;

			f_grid._DragColumn = undefined;
			dataGrid._dragDeltaX = undefined;

		} catch (x) {
			f_core.Error(f_grid, "_TitleCursorDragStop: exception", x);
		}

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_SortIndication_onmouseover : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataGrid.f_getEventLocked(evt, false)) {
			return false;
		}

		if (this._over) {
			return true;
		}

		this._over = true;

		dataGrid._updateSortManager();

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_SortIndication_onmouseout : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (!this._over && !this._selected) {
			return true;
		}

		this._over = undefined;
		this._selected = undefined;

		dataGrid._updateSortManager();

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_SortIndication_onmousedown : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (this._selected) {
			return true;
		}

		this._selected = true;

		dataGrid._updateSortManager();

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_SortIndication_onmouseup : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (!this._selected) {
			return true;
		}

		this._selected = undefined;

		dataGrid._updateSortManager();

		return true;
	},
	/**
	 * @method private static
	 * @param Event
	 *            evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_SortIndication_onclick : function(evt) {
		var dataGrid = this._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		dataGrid.f_showSortManager(evt);
	},
	/**
	 * @method hidden static
	 * @param String
	 *            text1
	 * @param String
	 *            text2
	 * @return Number
	 */
	Sort_Server : function(text1, text2) {
		// Pas d'implementation, car la fonction est filtrée avant !
		return 0;
	},
	/**
	 * @method public static
	 * @param String
	 *            name
	 * @param Function
	 *            callback
	 * @return void
	 */
	RegisterSortManager : function(name, callback) {
		var sortManagers = f_grid._SortManagers;
		if (!sortManagers) {
			sortManagers = new Object;
			f_grid._SortManagers = sortManagers;
		}

		sortManagers[name] = callback;
	},
	Finalizer : function() {
		f_grid._SortManagers = undefined;
	},
	/**
	 * @method private static
	 * @param HTMLTableElement
	 *            table
	 * @return HTMLTableColElement[]
	 */
	_ListCols : function(table) {
		var l = new Array;
		for ( var node = table.firstChild; node; node = node.nextSibling) {
			if (node.tagName.toLowerCase() != "col") {
				break;
			}

			l.push(node);
		}

		return l;
	},

	/**
	 * Return the current ComponetGrid
	 * 
	 * @method public static
	 * @param f_component
	 *            component
	 * @return f_grid
	 */
	GetGridFromComponent : function(component) {
		while (!component._dataGrid && component) {
			component = component.parentNode;
		}
		return component._dataGrid;
	}
};

var __members = {
	
	/**
	 * @field protected Boolean
	 */
	_loading: undefined,

	/**
	 * @field private Number
	 */
	_additionalInformationCount : 0,

	/**
	 * Active la recherche par les touches
	 * 
	 * @field protected Boolean
	 */
	_keyRowSearch : undefined,

	/**
	 * Spécifie l'index de la colonne quand il faut rechercher un token
	 * 
	 * @field protected Number
	 */
	_keySearchColumnIndex : undefined,

	/**
	 * 
	 * 
	 * @field protected String
	 */
	_additionnalOpenImageURL : undefined,

	/**
	 * 
	 * 
	 * @field protected String
	 */
	_additionnalCloseImageURL : undefined,

	/**
	 * 
	 * 
	 * @field protected Number
	 */
	_cursorCellIdx : undefined,

	f_grid: function() {
		this.f_super(arguments);

		this._rowsPool = new Array;
		this._cellsPool = new Array;
		// this._colsPool=new Array;

		this._showCursor = false;

		this._cellStyleClass = "f_grid_cell";
		this._rowStyleClass = "f_grid_row";

		this._resizable = f_core.GetBooleanAttributeNS(this, "resizable");

		this._initCursorValue = f_core.GetAttributeNS(this, "cursorValue");

		this._showValue = f_core.GetAttributeNS(this, "showValue");

		this._headerVisible = f_core.GetBooleanAttributeNS(this,
				"headerVisible", true);

		this._sortManager = f_core.GetAttributeNS(this, "sortManager");

		this._emptyDataMessage = f_core
				.GetAttributeNS(this, "emptyDataMessage");
		if (this._emptyDataMessage) {
			this._emptyDataMessageLabel = this.ownerDocument
					.getElementById(this.id
							+ f_grid._EMPTY_DATA_MESSAGE_ID_SUFFIX);
			f_core.Assert(this._emptyDataMessageLabel,
					"f_grid.f_grid: Label not found");
		}

		var rowStyleClass = f_core.GetAttributeNS(this, "rowStyleClass");
		if (rowStyleClass) {
			this._rowStyleClasses = rowStyleClass.split(",");

		} else {
			this._rowStyleClasses = this.f_getDefaultRowStyleClasses();
		}

		if (this.f_isDraggable()) {
			this._dragAndDropEngine = f_dragAndDropEngine.Create(this);
		}

		if (this.f_isDroppable()) {
			this._bodyDroppable = f_core.GetBooleanAttributeNS(this,
					"bodyDroppable", false);
		}

		this._sortPadding = f_core.GetNumberAttributeNS(this, "sortPadding",
				f_grid._SORT_PADDING);

		this.f_initializeTableLayout();

		this._tbody.style.display = "none";

		this._blankImageURL = f_env.GetBlankImageURL();

		this.f_openActionList(f_event.MOUSEDOWN);
		this.f_openActionList(f_event.MOUSEUP);
		this.f_openActionList(f_event.SELECTION);

		if (this._sortManager) {
			var sortIndicator = this.ownerDocument.createElement("A");
			sortIndicator.href = f_core.CreateJavaScriptVoid0();
			sortIndicator.className = "f_grid_sortManager";
			sortIndicator._dataGrid = this;

			sortIndicator.onmouseover = f_grid._SortIndication_onmouseover;
			sortIndicator.onmouseout = f_grid._SortIndication_onmouseout;
			sortIndicator.onmousedown = f_grid._SortIndication_onmousedown;
			sortIndicator.onmouseup = f_grid._SortIndication_onmouseup;
			sortIndicator.onclick = f_grid._SortIndication_onclick;

			this._sortIndicator = sortIndicator;

			if (this.id != this._scrollBody.id) {
				f_core.InsertBefore(this, sortIndicator, this._scrollBody);
			} else {
				f_core.AppendChild(this, sortIndicator);
			}

			var img = this.ownerDocument.createElement("IMG");
			img.className = "f_grid_sortManager_image";
			img.src = this._blankImageURL;
			img.width = 16;
			img.height = 16;

			var resourceBundle = f_resourceBundle.Get(f_grid);
			var title = resourceBundle.f_get("SORT_CONFIGURATION");
			img.alt = title;
			sortIndicator.title = title;

			f_core.AppendChild(sortIndicator, img);
		}

		this._focusOnInput=this.f_isCheckable && this.f_isCheckable();
		
		var focus;
		
		if (this._focusOnInput) {
			var scrollBody=this._scrollBody;
			
			// Le focus est positionné dans les INPUTs !
			scrollBody.onkeydown = f_grid._Link_onkeydown;
			scrollBody.onkeypress = f_grid._Link_onkeypress;
			scrollBody.onkeyup = f_grid._Link_onkeyup;
			scrollBody.tabIndex = -1; // Explicite ... y a des bugs chez
			// firefox !
			 
		} else if (f_core.IsGecko()) {
			focus = this.ownerDocument.getElementById(this.id
					+ f_grid._DATA_BODY_SCROLL_ID_SUFFIX);

			if (focus) {
				focus.onfocus = f_grid._Link_onfocus;
				focus.onblur = f_grid._Link_onblur;
				focus.onkeydown = f_grid._Link_onkeydown;
				focus.onkeypress = f_grid._Link_onkeypress;
				focus.onkeyup = f_grid._Link_onkeyup;
				focus._dataGrid = this;
				focus.tabIndex = this.fa_getTabIndex();
				this._cfocus = focus;

			} else {
				this.onfocus = f_grid._Link_onfocus;
				this.onblur = f_grid._Link_onblur;
				this.tabIndex = this.fa_getTabIndex();
				this._cfocus = this;
				this._dataGrid = this;
			}

		} else {
			var doc = this.ownerDocument;

			focus = doc.createElement("a");
			this._cfocus = focus;
			focus.className = "f_grid_focus";
			focus.onfocus = f_grid._Link_onfocus;
			focus.onblur = f_grid._Link_onblur;
			focus.onkeydown = f_grid._Link_onkeydown;
			focus.onkeypress = f_grid._Link_onkeypress;
			focus.onkeyup = f_grid._Link_onkeyup;
			focus.href = f_core.CreateJavaScriptVoid0();
			focus._dataGrid = this;
			focus.tabIndex = this.fa_getTabIndex();

			// this.tabIndex=-1;

			if (f_core.IsInternetExplorer()) {
				this.hideFocus = true;

				var self = this;
				var onbeforedeactivate = function() {
					var evt = f_core.GetJsEvent(this);

					var next = evt.toElement;

					// f_core.Debug(f_grid, "On before DE activate
					// "+next.tagName);

					if (!next) {
						return;
					}

					if (next.parentNode._dataGrid == self) { /*
																 * ||
																 * (next._column &&
																 * next._column._dataGrid==self)
																 */
						switch (next.tagName.toLowerCase()) {
						case "input":
						case "a":
							break;

						default:
							f_core.Debug(f_grid,
									"CANCEL On before DE activate "
											+ next.tagName);

							return f_core.CancelJsEvent(evt);
						}
					}

					return true;
				};

				focus.onbeforedeactivate = onbeforedeactivate;

				if (this._title) {
					this._title.onbeforeactivate = onbeforedeactivate;
				}
				if (this._scrollTitle) {
					this._scrollTitle.onbeforeactivate = onbeforedeactivate;
				}

			}

			if (this._scrollBody && this._scrollBody != this) {
				f_core.InsertBefore(this, focus, this._scrollBody);

			} else {
				f_core.InsertBefore(this, focus, this.firstChild);
			}

		}

		this._table.onmouseover = f_grid.RowMouseOver;
		this._table.onmouseout = f_grid.RowMouseOut;

		this.f_insertEventListenerFirst(f_event.KEYDOWN, this._performKeyDown);
	},
	f_finalize : function() {
		f_grid._TitleReleaseTimer();

		if (f_grid._DragColumn) {
			f_grid._TitleCursorDragStop();
		}

		if (this._rowsPool) {
			this.f_releaseRows();
			this._rowsPool = undefined;
		}

		if (this._cellsPool) {
			this.f_releaseCells();
			this._cellsPool = undefined;
		}
		/*
		 * if (this._colsPool) { this._releaseCols(this._colsPool);
		 * this._colsPool=null; }
		 */
		var sortIndicator = this._sortIndicator;
		if (sortIndicator) {
			this._sortIndicator = undefined; // HtmlAElement

			sortIndicator._dataGrid = undefined;

			sortIndicator.onmouseover = null;
			sortIndicator.onmouseout = null;
			sortIndicator.onmousedown = null;
			sortIndicator.onmouseup = null;
		}

		this._dragAndDropEngine = undefined;
		this._targetDragAndDropEngine = undefined;
		
		this._inputTabIndex = undefined; // HTMLInputELement
		// this._tabIndex=undefined; // Number
		// this._cursorCellIdx=undefined; // Number
		
		// this._additionnalOpenImageURL=undefined; // String
		// this._additionnalCloseImageURL=undefined; // String

		// this._bodyDroppable=undefined; Boolean
		// this._sortPadding=undefined; // Number
		// this._additionalIndexes=undefined; // number[]
		// this._submittedIndexes=undefined; // number[]
		// this._keySearchColumnIndex=undefined; // number

		// this._showValue=undefined; // String
		// this._headerVisible=undefined; // Boolean
		// this._sb=undefined; // Boolean
		// this._cellStyleClass=undefined; // String
		// this._rowStyleClass=undefined; // String
		// this._emptyDataMessageShown=undefined; // Boolean

		// this._showCursor=undefined; // Boolean
		// this._sortManager=undefined; // String

		// this._endRowIndex=undefined; // number
		this._shadowRows = undefined; // HtmlTRElement[]
		// this._waitingMode=undefined; // number
		// this._visibleColumnsCount=undefined; // number
		// this._titleLayout=undefined; // Boolean
		// this._documentComplete=undefined; // Boolean

		// this._blankImageURL=undefined; // string
		// this._columnsStyleClass=undefined; // string
		// this._waitingIndex=undefined; // number
		// this._waitingSelection=undefined; // number

		// this._partialWaiting=undefined; // Boolean
		// this._loading=undefined; // Boolean
		// this._emptyDataMessage=undefined; // String
		// this._refreshFullUpdateState=undefined; // Boolean

		this._thead = undefined; // HTMLTheadElement
		this._emptyDataMessageLabel = undefined; // HTMLElement

		var waiting = this._waiting;
		if (waiting) {
			this._waiting = undefined; // f_waiting

			f_classLoader.Destroy(waiting);
		}

		this._waitingRow = undefined; // HTMLTableRowElement
		// this._waitingLoading=undefined; // Boolean

		var pagedWaiting = this._pagedWaiting;
		if (pagedWaiting) {
			this._pagedWaiting = undefined; // f_waiting

			f_classLoader.Destroy(pagedWaiting);
		}

		this._currentSorts = undefined; // HTMLTableColElement
		// this._columnCanBeSorted=undefined; // Boolean

		// this._createFakeTH=undefined; // Boolean

		// this._oldHeight=undefined; // string
		// this._oldHeightStyle=undefined; // string

		var tbody = this._tbody;
		if (tbody) {
			this._tbody = undefined;

			if (tbody != this) {
				f_core.VerifyProperties(tbody);
			}
		}

		var table = this._table;
		if (table) {
			this._table = undefined;

			table._dataGrid = undefined;
			table.onbeforeactivate = null;
			table.onmouseover = null;
			table.onmouseout = null;

			if (table != this) {
				f_core.VerifyProperties(table);
			}
		}

		if (this._columns) {
			this._releaseColumns();
			this._columns = undefined;
		}
		this._columnOver = undefined; // HTMLTableColElement
		this._columnSelected = undefined; // HTMLTableColElement
		// this._rowValueColumnIndex=undefined; // string

		var cfocus = this._cfocus;
		if (cfocus) {
			this._cfocus = undefined;
			this.onbeforeactivate = null;

			cfocus.onbeforedeactivate = null;
			cfocus.onfocus = null;
			cfocus.onblur = null;
			cfocus.onkeydown = null;
			cfocus.onkeypress = null;
			cfocus.onkeyup = null;
			cfocus.onmousewheel = null;
			cfocus._dataGrid = undefined; // f_dataGrid

		} else {
			this.onfocus = null;
			this.onblur = null;
			this.onkeydown = null;
			this.onkeypress = null;
			this.onkeyup = null;
			this.onmousewheel = null;
			this._dataGrid = undefined; // f_dataGrid
		}

		var scrollBody = this._scrollBody;
		if (scrollBody) {
			this._scrollBody = undefined;

			scrollBody.onscroll = null;
			scrollBody.onmousedown = null;
			scrollBody.onmouseup = null;
			scrollBody.onclick = null;
			scrollBody.onbeforeactivate = null;
			scrollBody.onmousewheel = null;
			scrollBody.onkeydown = null;
			scrollBody.onkeypress = null;
			scrollBody.onkeyup = null;

			scrollBody._dataGrid = undefined; // f_dataGrid

			if (scrollBody != this) {
				f_core.VerifyProperties(scrollBody);
			}
		}

		var title = this._title;
		if (title) {
			this._title = undefined;

			title.onbeforeactivate = null;
			if (title != this) {
				f_core.VerifyProperties(title);
			}
		}

		var scrollTitle = this._scrollTitle;
		if (scrollTitle) {
			this._scrollTitle = undefined;

			scrollTitle.onbeforeactivate = null;
			scrollTitle.onscroll = null;
			scrollTitle._dataGrid = undefined;

			if (scrollTitle != this) {
				f_core.VerifyProperties(scrollTitle);
			}
		}

		// Il faut tester le verify properties qu'a la fin des undefined !
		if (cfocus && cfocus != this) {
			f_core.VerifyProperties(cfocus);
		}

		// this._cursor=undefined; // fait dans f_releaseRows()

		// this._initSort=undefined; // Boolean
		// this._resizable=undefined; // Boolean
		// this._sortIndexes = undefined; // String

		this.f_super(arguments);
	},
	f_getFocusableElement : function() {
		return this._cfocus;
	},
	f_setDomEvent : function(type, target) {
		switch (type) {
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
	f_clearDomEvent : function(type, target) {
		switch (type) {
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
	 * @method protected
	 */
	f_getEventLocked : function(evt, showAlert) {
		if (this._loading) {
			if (showAlert !== false) {
				if (this._showLoadingAlert === undefined) {
					var alertLoadingMessage = f_core.GetAttributeNS(this,
							"alertLoadingMessage");
					if (alertLoadingMessage === undefined) {
						this._showLoadingAlert = true;
					} else if (alertLoadingMessage == "") {
						this._showLoadingAlert = false;
					} else {
						this._showLoadingAlert = true;
						this._alertLoadingMessage = alertLoadingMessage;
					}
				}

				if (this._showLoadingAlert !== false) {
					if (this._alertLoadingMessage === undefined) {
						var resourceBundle = f_resourceBundle.Get(f_grid);
						this._alertLoadingMessage = "f_grid: "
								+ resourceBundle.f_get("EVENT_LOCKED");
					}
					f_core
							.Debug(f_grid,
									"f_getEventLocked: popup error dialog, loading ...");

					alert(this._alertLoadingMessage);
				}
			}
			return true;
		}

		return this.f_super(arguments, evt, showAlert);
	},
	f_getMainStyleClass : function() {
		return "f_grid";
	},
	/**
	 * @method protected
	 * @return String[]
	 */
	f_getDefaultRowStyleClasses : function() {
		return f_grid._DEFAULT_ROW_STYLE_CLASSES;
	},
	/**
	 * @method private
	 */
	_normalizeIndexes : function() { 
		
		if (!this._additionalIndexes || !this._additionalIndexes.length) {
			this._additionalIndexes = [0 ,0];
		}
		if (!this._submittedIndexes || !this._submittedIndexes.length) {
			this._submittedIndexes = [0 ,0];
		}
	},

	f_serialize : function() {
		this._normalizeIndexes();

		if (this._resizable && this._titleLayout) {
			var columns = this._columns;
			var v = "";
			for ( var i = 0; i < columns.length; i++) {
				var col = columns[i];

				if (!col._visibility || !col._resizable) {
					continue;
				}

				// Palliatif de Fred pour pb ie
				// See AbstractGridRenderContext.java 506
				if (col._col.offsetWidth > 0) {
					if (v) {
						v += ",";
					}

					v += col._col.offsetWidth;
				}
			}
			this.f_setProperty(f_prop.COLUMN_WIDTHS, v);
		}

		var serializedIndexes = this._submittedIndexes;
		if (serializedIndexes && serializedIndexes.length) {
			this.f_setProperty(f_prop.SERIALIZED_INDEXES, serializedIndexes
					.join(','));
		}
		f_core.Debug(f_dataGrid, "f_serialize: serializedIndexes="
				+ serializedIndexes);
		this._submittedIndexes = null;

		var cursor = this._cursor;
		var cursorValue = null;
		if (cursor) {
			cursorValue = this.fa_getElementValue(cursor);
			if (typeof (cursorValue) == "number") {
				cursorValue = String(cursorValue);
			}
		}

		this.f_setProperty(f_prop.CURSOR, cursorValue);

		if (this._sortIndexes !== undefined) {
			this.f_setProperty(f_prop.SORT_INDEX, this._sortIndexes);
		}

		return this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @param Number
	 *            first
	 * @param Number
	 *            rows
	 * @return Number[]
	 */
	f_addSerializedIndexes : function(nStart, nLength) {

		var additionalIndexes = this.f_listAdditionalIndexes();
		this._additionalIndexes = this._addIndexes(nStart, nLength, additionalIndexes);
		
		var submittedIndexes = this.f_listSubmittedIndexes();
		this._submittedIndexes = this._addIndexes(nStart, nLength, submittedIndexes);

		return additionalIndexes;
	},
	/**
	 * @method private
	 * @param Number
	 *            first
	 * @param Number
	 *            rows
	 * @return Number[]
	 */
	_addIndexes : function(nStart, nLength, serializedIndexes) {
	
		var nEnd = nStart + nLength;

		var found = false;

		for ( var i = 0; i < serializedIndexes.length; i += 2) {
			var aStart = serializedIndexes[i];
			var aLength = serializedIndexes[i + 1];
			var aEnd = aStart + aLength;

			if (nEnd < aStart) {
				// Interval avant

				serializedIndexes.unshift(nStart, nEnd - nStart);
				found = true;
				break;
			}

			if (nStart > aEnd) {
				// Interval apres
				// On essaye les suivants ...
				continue;
			}

			if (nEnd <= aEnd) {
				// On fusionne, le nouveau est juste avant l'ancien
				var start = aStart < nStart ? aStart : nStart;
				serializedIndexes[i] = start
				serializedIndexes[i + 1] = aEnd - start;
				found = true;
				break;
			}

			nStart = aStart;

			if (serializedIndexes.length == 2) {
				serializedIndexes[0] = nStart;
				serializedIndexes[1] = nEnd - nStart;
				found = true;
				break;
			}

			// Le nouveau depasse à droite l'ancien
			serializedIndexes.splice(i, 2);
			i -= 2;
		}

		if (!found) {
			serializedIndexes.push(nStart, nEnd - nStart);
		}
		
		return serializedIndexes;
	},

	/**
	 * @method protected
	 * @return void
	 */
	f_clearAdditionalIndexes : function() {
		this._additionalIndexes = undefined;
	},
	/**
	 * @method protected
	 * @return Array
	 */
	f_listAdditionalIndexes : function() {

		var additionalIndexes = this._additionalIndexes;
		if (!additionalIndexes) {
			additionalIndexes = new Array;
			this._additionalIndexes = additionalIndexes;
		}

		return additionalIndexes;
	},
	
	
	/**
	 * @method protected
	 * @return Array
	 */
	f_listSubmittedIndexes : function() {

		var submittedIndexes = this._submittedIndexes;
		if (!submittedIndexes) {
			submittedIndexes = new Array;
			this._submittedIndexes = submittedIndexes;
		}

		return submittedIndexes;
	},

	f_update : function() {
		var rowCount = this._rowCount;

		if (this._rows > 0 && !this._paged) {
			// Pas de mode page,
			// On affiche un wait !

			if (rowCount >= 0) {
				// On affiche des lignes vides ....

				this._waitingMode = f_grid.ROWS_WAITING;

				this.f_addWaitingRows();

			} else {
				this._waitingMode = f_grid.END_WAITING;

				this.f_addPagedWait();
			}

		} else {
			this._waitingMode = f_grid.FULL_WAITING;
		}

		f_core.Debug(f_grid, "f_update: Set waiting mode to '"
				+ this._waitingMode + "' (rows=" + this._rows + " paged="
				+ this._paged + " rowCount=" + this._rowCount + ")");

		if (this._tbody && !f_core.GetParentNode(this._tbody)) {
			// f_core.Assert(this._tbody.parentNode!=this._table,
			// "f_grid.f_update: Tbody has not been detached !");
			// C'est normal dans un componentsGrid

			f_core.AppendChild(this._table, this._tbody);

			this.f_updateScrollPosition();
		}

		this.f_super(arguments);

		if (this._initSort) {
			this._initSort = undefined;

			this._sortTable(false);
		}

		var scrollBody = this._scrollBody;
		// Des popups ?
		var menu = this.f_getSubMenuById(f_grid._BODY_MENU_ID);
		if (menu) {
			scrollBody.onmousedown = f_grid._BodyMouseDown;
			scrollBody.onmouseup = f_grid._BodyMouseUp;// f_grid.FiltredCancelJsEventHandler;
			scrollBody.onclick = f_grid.FiltredCancelJsEventHandler;
		}

		// this._tbody.style.visibility="inherit";
		if (f_core.IsInternetExplorer()) {
			this._tbody.style.display = "block";
		} else {
			this._tbody.style.display = "table-row-group";
		}

		/*
		 * var columns = this._columns; for (var i = 0; i < columns.length; i++) {
		 * var column=columns[i]; if (column._visibility) {
		 * this._updateTitleStyle(column); } }
		 */

		this.f_performPagedComponentInitialized();

		// On a besoin de faire des calculs CSS quand le grid est affiché !
		if (!this.f_isVisible()) {
			this.f_getClass().f_getClassLoader().f_addVisibleComponentListener(
					this);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_documentComplete : function() {
		this.f_super(arguments);

		// this._documentComplete=true;

		if (!this.f_isVisible()) {
			return;
		}

		this.f_performComponentVisible();
	},
	/**
	 * @method hidden
	 */
	f_performComponentVisible : function() {
		f_core.Debug(f_grid, "f_performComponentVisible: rows=" + this._rows
				+ " paged=" + this._paged + " rowCount=" + this._rowCount);

		this.f_updateTitle();

		if (false && f_core.IsGecko()) {
			if (this._table.parentNode.offsetHeight == 0) {
				// BUG de layout de Firefox !
				this._table.parentNode.style.height = this._table.offsetHeight
						+ "px";
			}
		}

		this.f_updateScrollPosition();

		if (this._interactiveShow) {
			this.f_setFirst(this._first, this._cursor);
		}

		if (this._thead && f_core.IsGecko()) {
			this._thead.style.display = "block"; // danger accessibilite

			if (this._table.caption) {
				this._table.caption.style.display = "block";
			}
		}
	},
	/**
	 * @method protected
	 */
	f_removePagedWait : function() {
		f_core.Assert(this._waitingMode == f_grid.END_WAITING,
				"f_grid.f_removePagedWait: Invalid waiting mode !");

		var waiting = this._pagedWaiting;
		if (waiting) {
			this._pagedWaiting = undefined;

			// Pas de destroy car des références peuvent trainer ...
			// f_classLoader.Destroy(waiting);
		}

		var waitingRow = this._waitingRow;
		if (waitingRow) {
			this._waitingRow = undefined;

			f_classLoader.Destroy(waitingRow);
		}
	},
	/**
	 * @method protected
	 */
	f_addWaitingRows : function() {
		f_core.Debug(f_grid, "f_addWaitingRows: rowCount=" + this._rowCount
				+ " rows=" + this._rows);

		var doc = this.ownerDocument;

		if (this._rows == this._rowCount) {
			// alert("RowCount="+this._rowCount+"/"+this._rows);
			this._waitingRow = undefined;
			return;
		}

		var shadows = this._shadowRows;
		if (shadows) {
			this._waitingRow = shadows[0];
			return;
		}
		shadows = new Array;
		this._shadowRows = shadows;

		var body = this._tbody;

		var diff = this._rowCount - this._rows - shadows.length;
		for ( var i = 0; i < diff; i++) {
			var tr = doc.createElement("tr");
			shadows.push(tr);
			f_core.AppendChild(body, tr);

			var td = doc.createElement("td");
			td.colSpan = this._visibleColumnsCount;
			td.className = this._cellStyleClass + "_shadow f_grid_cell_shadow";

			f_core.AppendChild(tr, td);

			td.innerHTML = "&nbsp;";
		}

		this._waitingRow = shadows[0];

		// On verifie qu'il n'est pas visible ?

		var self = this;
		f_core.GetWindow(doc).setTimeout(function() {
			self._verifyWaitingPosition();
			self = null;
		}, 100);
	},
	/**
	 * @method private
	 */
	f_addPagedWait : function() {
		f_core.Assert(this._waitingMode == f_grid.END_WAITING,
				"f_grid.f_addPagedWait: Invalid waiting mode !");

		f_core.Debug(f_grid, "f_addPagedWait: rowCount=" + this._rowCount
				+ " rows=" + this._rows);

		var doc = this.ownerDocument;

		var poolSize = this._rowsPool.length;
		if (poolSize == this._rowCount) {
			// alert("RowCount="+this._rowCount+"/"+this._rows);
			return;
		}

		this.f_removePagedWait();

		var tbody = this._tbody;
		f_core
				.Assert(tbody,
						"f_grid.f_addPagedWait: No Tbody for dataGrid ???");

		var waitTR = doc.createElement("tr");
		f_core.AppendChild(tbody, waitTR);
		this._waitingRow = waitTR;
		this._waitingLoading = undefined;

		var rowIdx = this._rowsPool.length;

		waitTR.className = this._rowStyleClasses[rowIdx
				% this._rowStyleClasses.length];

		var td = doc.createElement("td");
		f_core.AppendChild(waitTR, td);
		td.colSpan = this._visibleColumnsCount;

		var waiting = f_waiting.Create(td, null, true);
		this._pagedWaiting = waiting;

		waiting.f_show();

		// On verifie qu'il n'est pas visible ?

		var self = this;
		f_core.GetWindow(doc).setTimeout(function() {
			self._verifyWaitingPosition();
			self = null;
		}, 100);
	},

	/**
	 * @method private
	 */
	_performPagedLoading : function(evt, cursorIndex) {
		this._waitingLoading = true;

		this.f_appendCommand(function(dataGrid) {
			var poolSize = dataGrid._rowsPool.length;

			dataGrid.f_callServer(poolSize, 0, cursorIndex, undefined, true);
		});
	},

	/**
	 * @method private
	 */
	_performRowsLoading : function(evt, endIndex) {
		this._waitingLoading = true;

		var index = this._endRowIndex;

		f_core.Debug(f_grid, "_performRowsLoading: endIndex=" + endIndex
				+ " cur=" + index);

		if (index && endIndex < index) {
			return;
		}

		this._endRowIndex = endIndex;

		this.f_appendCommand(function(dataGrid) {
			var index = this._endRowIndex;
			var poolSize = dataGrid._rowsPool.length;

			if (!index || index < poolSize) {
				f_core.Debug(f_grid,
						"_performRowsLoading.appendCommand: Ignore index="
								+ index + " poolSize=" + poolSize);

				dataGrid.f_processNextCommand();
				return;
			}

			this._endRowIndex = undefined;

			var length = index - poolSize;
			if (length < this._rows) {
				length = this._rows;
			}

			if (poolSize + length >= this._rowCount) {
				length = this._rowCount - poolSize;
			}

			f_core.Debug(f_grid,
					"_performRowsLoading.appendCommand: Call server poolSize="
							+ poolSize + " length=" + length + " end=" + index
							+ " rows=" + this._rows);

			dataGrid.f_callServer(poolSize, length, index, undefined, true);
		});
	},

	/* ****************************************************************** */

	/**
	 * List the columns of the grid
	 * 
	 * @method public
	 * @return f_gridColumn[] An array of column object.
	 */
	f_getColumns : function() {
		return f_core.PushArguments(null, this._columns);
	},
	/**
	 * Returns the name associated to the column.
	 * 
	 * @method public
	 * @param f_gridColumn
	 *            column The column object
	 * @return String The name of the column.
	 */
	f_getColumnName : function(column) {
		var labelComponent = column._label;

		if (!labelComponent) {
			var text = column._text;
			return text;
		}

		return f_core.GetTextNode(labelComponent);
	},
	/**
	 * Returns the id associated to the column.
	 * 
	 * @method public
	 * @param f_gridColumn
	 *            column The column object
	 * @return String The Id of the column.
	 */
	f_getColumnId : function(column) {
		var idComponent = column.f_getId();

		if (idComponent) {
			return idComponent;
		}

		return this.f_getColumnName(column);
	},
	/**
	 * @method public
	 * @param f_gridColumn
	 *            column
	 * @return Number Ascending:1 Descending:-1 not-sorted:0
	 * @deprecated Use column.f_getColumnOrderState()
	 */
	f_getColumnOrderState : function(column) {
		return column.f_getColumnOrderState();
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_setColumns2 : function() {
		var columns = new Array;
		this._columns = columns;

		var v = 0;
		for ( var i = 0; i < arguments.length;) {
			var column = arguments[i++];

			if (column) {
				f_gridColumn.f_decorateInstance(column);

				if (column._visibility === undefined) {
					column._visibility = true;
				}

				if (column._valueColumn) {
					this._rowValueColumnIndex = i - 1;
				}

				if (column._keySearch) {
					this._keySearchColumnIndex = i - 1;
				}

				var defaultCellImage = column._defaultCellImageURL;
				if (defaultCellImage) {
					f_imageRepository.PrepareImage(defaultCellImage);
				}

				if (!column._align) {
					column._align = f_grid._DEFAULT_ALIGNMENT;
				}

				if (this._resizable && column._resizable) {
					if (!column._minWidth || column._minWidth < 1) {
						column._minWidth = f_grid._COLUMN_MIN_WIDTH;
					}

					if (!column._maxWidth
							|| column._maxWidth < column._minWidth) {
						column._maxWidth = f_grid._COLUMN_MAX_WIDTH;
					}

					column._resizable = (column._minWidth >= f_grid._COLUMN_MIN_WIDTH && column._maxWidth >= column._minWidth);
				}

				if (column._titleToolTipId) {

				}

			} else {
				column = f_gridColumn.f_newInstance();

				column._visibility = true;
				column._align = f_grid._DEFAULT_ALIGNMENT;
			}

			column._index = columns.length;
			column._dataGrid = this;

			if (column._visibility) {
				v++;
			}

			columns.push(column);
		}

		this._visibleColumnsCount = v;

		this.f_updateColumnsLayout();

		for ( var i = 0; i < columns.length;) {
			var column = columns[i++];

			var sorter = column._sorter;
			if (sorter) {
				column._sorter = undefined;

				this._installSorter(column, sorter);
			}

			if (column._visibility) {
				// this._updateTitleStyle(column);
			}
		}

	},
	/**
	 * @method private
	 * @return void
	 */
	_installSorter : function(column, method) {
		f_core.Assert(column,
				"f_grid._installSorter: Invalid column parameter '" + column
						+ "'.");

		this._columnCanBeSorted = true;

		if (typeof (method) != "function") {
			try {
				method = f_core.WindowScopeEval(method);

			} catch (x) {
				f_core.Error(f_grid,
						"_installSorter: Can not eval sort method '" + method
								+ "'.", x);

				throw x;
			}

			f_core.Assert(typeof (method) == "function",
					"f_grid._installSorter: Bad sort method for column '"
							+ column._index + "' !");
		}

		column._method = method;
	},
	/**
	 * @method public
	 * @param Object
	 *            row
	 * @return Boolean
	 */
	f_isRowSelected : function(row) {
		return !!row._selected;
	},
	/**
	 * @method protected
	 */
	fa_updateElementStyle : function(row, updateCells) {
		var suffix = "";
		if (this.f_isDisabled()) {
			suffix = "_disabled"; // +"_disabled"; // La classe par du
			// .f_grid_disabled

			if (row._selected) {
				suffix += "_selected";
			}

		} else if (row._selected) {
			// Pas de classe avec parité !
			fa_aria.SetElementAriaSelected(row, true);
			suffix = "_selected";
			if (this._focus) {
				suffix += "_focus";
				if (this._serviceGridId) {
					var dataGridPopup = f_core
							.GetElementByClientId(this._serviceGridId);
					if (dataGridPopup._ariaInput) {
						fa_aria.SetElementAriaActiveDescendant(
								dataGridPopup._ariaInput, row.id);
					} else {
						fa_aria.SetElementAriaActiveDescendant(
								this._scrollBody, row.id);
					}
				} else {
					fa_aria.SetElementAriaActiveDescendant(this._scrollBody,
							row.id);
				}
			}

		} else if (this.f_isSelectable()) {
			suffix = "_normal";
		}

		var className = this._rowStyleClass;
		var cl = className;
		var rowClassName = row._className;
		if (rowClassName) {
			cl += " " + rowClassName;
		}

		cl += " f_grid_row" + suffix;
		if (rowClassName) {
			cl += " " + rowClassName + suffix;
		}

		if (row._dndOver) {
			suffix += "_dndOver";

			cl += " f_grid_row" + suffix;
			if (rowClassName) {
				cl += " " + rowClassName + suffix;
			}

		} else if (row._over) {
			suffix += "_over";

			cl += " f_grid_row" + suffix;
			if (rowClassName) {
				cl += " " + rowClassName + suffix;
			}
		}

		var updateFirstOnly = true;
		if (row.className != cl) {
			row.className = cl;

			updateFirstOnly = false;
		}

		if (this._cursor == row) {
			if (!row._hasCursor) {
				row._hasCursor = true;
			}

		} else if (row._hasCursor) {
			row._hasCursor = undefined;
		}

		if (updateCells !== false) {
			this.f_updateCellsStyle(row, updateFirstOnly);
		}

		var button = row._additionalButton;
		if (button) {
			var buttonClassName = "f_grid_additional_button";

			var additionalImageURL = undefined;
			var shown = undefined;

			var content = row._additionalContent;
			if (content === false) {
				buttonClassName += " f_grid_additional_button_no_content";

			} else {
				shown = this.fa_isAdditionalElementVisible(row);

				if (shown) {
					buttonClassName += " f_grid_additional_button_expanded";					
					additionalImageURL = this._additionnalOpenImageURL;

				} else {
					buttonClassName += " f_grid_additional_button_collapsed";			
					additionalImageURL = this._additionnalCloseImageURL;
				}

				if (this.f_isDisabled()) {
					buttonClassName += "_disabled";
				}
			}

			if (button.className != buttonClassName) {
				button.className = buttonClassName;
			}

			if (additionalImageURL !== undefined
					&& button.src != additionalImageURL) {
				button.src = additionalImageURL;

				var rb=f_resourceBundle.Get(f_grid);
				
				var additionalAlt = rb.f_formatParams(
								(shown) ? "COLLAPSE_BUTTON" : "EXPAND_BUTTON",
								{
									value : row._lineHeader
								});

				button.title = button.alt = additionalAlt;

				var additionalRowMessage=rb.f_get((shown)?"COLLAPSABLE_ROW":"EXPANDABLE_ROW", "Ligne dépliable");			

				fa_audioDescription.SetAudioDescription(row._input._label, additionalRowMessage, "additional", row._input._label.id);
				
				row._input.setAttribute("aria-expanded", shown);
			}
		}
	},
	/**
	 * @method protected
	 */
	f_updateCellsStyle : function(row, firstOnly) {
		var td = row.firstChild;
		for (; td; td = td.nextSibling) {
			var tagName =  td.tagName.toLowerCase();
			if (tagName== "td" || tagName=="th") {
				break;
			}
		}
		if (!td) {
			return;
		}


		var cols = this._columns;
		var idx = 0;
		var selected = row._selected;
		var cursorCellIdx = this._cursorCellIdx;

		if (cursorCellIdx !== undefined) {
			firstOnly = false;
		}

		for ( var i = 0; i < cols.length && td; i++) {
			var col = cols[i];
			if (!col._visibility) {
				continue;
			}

			var className = [ this._cellStyleClass ];
			if (selected) {
				className.push(" f_grid_cell_selected");
			}

			if (col._cellClickable || td._clickable) {
				// Sur le TD ! et c'est sur le Datagrid seulement !
				className.push(" f_dataGrid_cell_clickable");
			}

			var cclassName = td._cellStyleClass;

			if (!cclassName) {
				var cellStyleClasses = col._cellStyleClasses;

				if (cellStyleClasses) {
					cclassName = cellStyleClasses[row._rowIndex
							% cellStyleClasses.length];
				}
			}

			if (cclassName) {
				var cs = cclassName.split(" ");
				for ( var j = 0; j < cs.length; j++) {
					className.push(" ", cs[j]);
					if (selected) {
						className.push("_selected");
					}
				}
			}

			if (!idx) {
				className.push(" f_grid_cell_left");
			}

				if (this._cursor == row && this._focus && this._showCursor) {
				if (cursorCellIdx !== undefined) {
					if (col._index == cursorCellIdx) {
						className.push(" f_grid_cell_cursor");
					}

				} else if (!idx) {
					className.push(" f_grid_cell_cursor");
				}
			}

			className.push(" f_grid_cell_align_" + col._align);

			var sclassName = className.join("");

			if (td.className != sclassName) {
				td.className = sclassName;
			}

			if (firstOnly) {
				break;
			}

			for (td = td.nextSibling; td; td = td.nextSibling) {
				var tagName =  td.tagName.toLowerCase();
				if (tagName== "td" || tagName=="th") {
					break;
				}
			}
			if (!td) {
				return;
			}
			idx++;
		}
	},
	/**
	 * @method hidden
	 */
	f_setRowCount : function(rowCount) {
		this._rowCount = rowCount;
		this._maxRows = rowCount;
	},
	fa_updateDisabled : function(disabled) {
		if (!this.fa_componentUpdated) {
			return;
		}
		var suffix = (disabled) ? "_disabled" : null;

		this.className = this.f_computeStyleClass(suffix);

		var table = this._table;
		if (!table) {
			return;
		}
		var className = "f_grid_table";
		if (disabled) {
			className += " " + className + "_disabled";
		}
		table.className = className;

		var rows = this._table.rows;
		for ( var i = 0; i < rows.length; i++) {
			var row = rows[i];

			if (row._index === undefined) {
				continue;
			}

			var input = row._input;
			if (input) {
				input.disabled = disabled;
			}

			this.fa_updateElementStyle(row);
		}

		if (this._headerVisible) {
			var cols = this._columns;
			for ( var i = 0; i < cols.length; i++) {
				var col = cols[i];

				if (!col._visibility) {
					continue;
				}

				this._updateTitleStyle(col);
			}
		}
	},
	f_filterEvent : function(type, jsEvent) {
		if (!jsEvent
				|| (type != f_event.SELECTION && type != f_event.MOUSEDOWN && type != f_event.MOUSEUP)) {
			return undefined;
		}

		if (!f_grid.VerifyTarget(jsEvent)) {
			return true;
		}

		if (this.f_isSelectable()) {
			// return false; ??? Je ne sais pas si ca sert encore !
		}

		return this.f_super(arguments, type, jsEvent);
	},
	/**
	 * Returns the value of the row specified by its index.
	 * 
	 * @method public
	 * @param any
	 *            rowIndex Row object.
	 * @return String the key of the row.
	 */
	f_getRowValueAtIndex : function(rowIndex) {
		var row = this.f_getRow(rowIndex, true, true);
		if (!row) {
			return null;
		}

		return row._index;
	},
	/**
	 * Returns the value of the row.
	 * 
	 * @method public
	 * @param any
	 *            rowObject Row object.
	 * @return String the key of the row.
	 */
	f_getRowValue : function(rowObject) {
		var row = this.f_getRow(rowObject, true);
		if (!row) {
			return null;
		}

		return row._index;
	},
	/**
	 * Returns the row associated to a value.
	 * 
	 * @method public
	 * @param any
	 *            value Value of the row, or a row object.
	 * @param hidden
	 *            Boolean throwError Throws error if row is not found.
	 * @return Object row associated or <code>null</code>.
	 */
	f_getRowByValue : function(value, throwError) {
		f_core.Assert(value !== undefined && value !== null,
				"f_grid.f_getRowByValue: Invalid value '" + value + "'.");

		if (value._dataGrid === this) {
			return value;
		}

		var rows = this.fa_listVisibleElements();
		if (!rows) {
			f_core.Debug(f_grid,
					"f_getRowByValue: Empty rows to get row by value " + value);
			if (throwError) {
				throw new Error("Can not find row with value '" + value + "'.");
			}

			return null;
		}

		for ( var i = 0; i < rows.length; i++) {
			var row = rows[i];

			if (row._index != value) {
				continue;
			}

			return row;
		}

		f_core.Debug(f_grid, "f_getRowByValue: Can not find row by value "
				+ value);

		if (throwError) {
			throw new Error("Can not find row with value '" + value + "'.");
		}
		return null;
	},
	/*
	 * Returns the index of the row associated to the specified value.
	 * 
	 * @method public @param any rowValue Value of the row, or a row object.
	 * @return Number Index of the row or <code>-1</code> if not found.
	 * 
	 * f_rowIndexOf: function(rowValue) { var rows=this._tbody.rows; if (!rows ||
	 * rows.length<1) { return -1; }
	 * 
	 * for(var i=0;i<rows.length;i++) { var row=rows[i];
	 * 
	 * if (row!=value && row._index!=value) { continue; }
	 * 
	 * return i; }
	 * 
	 * return -1; },
	 */
	/**
	 * @method protected
	 */
	f_getRow : function(rowIndex, throwError, indexByValue) {
		if (!this._tbody) {
			f_core.Debug(f_grid, "f_getRow: No body to get row #" + rowIndex);

			if (throwError) {
				throw new Error("Can not find row '" + rowIndex + "'.");
			}
			return null;
		}

		var row;

		if (typeof (rowIndex) != "number") {
			row = rowIndex;
			f_core.Assert(row._dataGrid,
					"f_grif.f_getRow: Object is not a row of a datagrid !");

		} else {
			var rows = this.fa_listVisibleElements(); // this._tbody.rows;
			if (!rows.length) {
				if (throwError) {
					throw new Error("Can not find row #" + rowIndex
							+ ". (no visible rows)");
				}
				return null;
			}

			var r = undefined;
			if (indexByValue) {
				if (rowIndex >= 0 && rowIndex < rows.length) {
					r = rows[rowIndex];
				}

			} else {
				for ( var i = 0; i < rows.length; i++) {
					if (rows[i]._index != rowIndex) {
						continue;
					}

					r = rows[i];
					break;
				}
			}

			if (r === undefined) {
				if (throwError) {
					var first = this._first;

					throw new Error("Row index out of ranges " + first + "<"
							+ rowIndex + "<" + (rows.length + first)
							+ ", or row not found.");
				}
				return null;
			}

			row = r;
		}

		var cells = row._cells;
		if (!cells) {
			f_core.Debug(f_grid, "f_getRow: No cells for row #" + rowIndex);
			return null;
		}

		return row;
	},
	/**
	 * Specify the index of the first row which starts the grid.
	 * 
	 * @method public
	 * @param Number
	 *            index
	 * @param Number
	 *            cursorIndex The cursor index. (can be undefined)
	 * @param hidden
	 *            Boolean selection New cursor position
	 * @param hidden
	 *            Boolean ignoreInteractive Change the position only (no server
	 *            call)
	 * @return Boolean Returns <code>false</code>.
	 */
	f_setFirst : function(index, cursorIndex, selection, ignoreInteractive) {
		// var oldFirst=this._first;

		this.f_setProperty(f_prop.FIRST, index);
		if (cursorIndex) {
			this.f_setProperty(f_prop.CURSOR, cursorIndex);
		}

		if (ignoreInteractive === true) {
			return false;
		}

		if (this._interactive) {
			this.f_appendCommand(function(grid) {
				grid.f_callServer(index, 0, cursorIndex, selection);
			});

			return false;
		}

		f_core._Submit(null, this, f_event.CHANGE);

		return false;
	},

	/**
	 * Refresh the structure of the grid.
	 * 
	 * @method public
	 * 
	 * @param Boolean
	 *            fullUpdate to force rowCount and pager update
	 * 
	 * @return Boolean
	 */
	f_refreshContent : function(fullUpdate) {
		if (!this._interactive) {
			return false;
		}

		this.f_appendCommand(function(dataGrid) {
			dataGrid.f_callServer(0, undefined, undefined, undefined,
					undefined, fullUpdate);
		});

		return true;
	},
	/**
	 * Set the refreshFullUpdateState that force the refresh to be full
	 * 
	 * @method public
	 * 
	 * @param optional
	 *            Boolean fullUpdate to force rowCount and pager update
	 * 
	 * @return void
	 */
	f_setRefreshFullUpdateState : function(fullUpdate) {
		this._refreshFullUpdateState = (fullUpdate !== false);
	},
	/**
	 * Get the refreshFullUpdateState that force the refresh to be full
	 * 
	 * @method public
	 * 
	 * @return Boolean
	 */
	f_isRefreshFullUpdateState : function() {
		return this._refreshFullUpdateState === true;
	},
	/**
	 * @method protected
	 */
	f_performErrorEvent : function(param, messageCode, message) {
		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * @method hidden
	 */
	fa_cancelFilterRequest : function() {
		// Appeler par la génération du serveur !
	},
	_releaseColumns : function() {
		var columns = this._columns;
		for ( var i = 0; i < columns.length; i++) {
			var column = columns[i];

			var head = column._head;
			if (head) {
				column._head = undefined;

				head._column = undefined;
				head.onmouseout = null;
				head.onmouseover = null;
				head.onmousedown = null;
				head.onmouseup = null;
				head.onclick = null;
				head.onbeforeactivate = null;

				f_core.VerifyProperties(head);
			}

			var label = column._label;
			if (label) {
				column._label = undefined;

				label._column = undefined;
				// head.onmouseout=null;
				// head.onmouseover=null;
				// label.onmousedown=null;
				// label.onmouseup=null;
				label.onfocus = null;
				label.onblur = null;
				label.onclick = null;
				label.onkeydown = null;

				f_core.VerifyProperties(label);
			}

			var cursor = column._cursor;
			if (cursor) {
				column._cursor = undefined;

				cursor._column = undefined;

				cursor.onmousedown = null;
				cursor.onclick = null;

				f_core.VerifyProperties(cursor);
			}

			var col = column._col;
			if (col) {
				column._col = undefined; // HTMLTableColElement

				f_core.VerifyProperties(col);
			}

			var col = column._col2;
			if (col) {
				column._col2 = undefined; // HTMLTableColElement

				f_core.VerifyProperties(col);
			}

			var sorterImage = column._sorterImage;
			if (sorterImage) {
				column._sorterImage = undefined; // HTMLImageElement

				f_core.VerifyProperties(sorterImage);
			}

			// column._align=undefined; // String
			column._tcol = undefined; // HTMLThElement
			// // column._tcell=undefined; // HTMLTdElement
			column._box = undefined; // HTMLDivElement
			column._image = undefined; // HTMLImageElement

			// column._titleImageURL=undefined; // String
			// column._titleDisabledImageURL=undefined; // String
			// column._titleHoverImageURL=undefined; // String
			// column._titleSelectedImageURL=undefined; // String

			// column._index=undefined; // number
			column._dataGrid = undefined; // f_grid
			// column._id=undefined; // String
			column._method = undefined; // function
			// column._visibility=undefined; // Boolean
			// column._cellStyle=undefined; // String
			// column._cellToolTipText=undefined; // String
			// column._autoFilter=undefined; // Boolean
			// column._ascendingOrder=undefined; // Boolean

			// column._cellImage=undefined; // Boolean
			// column._defaultCellImageURL=undefined; // String

			// Les colonnes sont devenus des objets
			// f_core.VerifyProperties(column);
			f_classLoader.Destroy(column);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_releaseRows : function() {
		f_core.Debug(f_grid, "f_releaseRows: release all rows");
		this._cursor = undefined; // HTMLTableRowElement

		var list = this._rowsPool;
		if (!list || !list.length) {
			return;
		}
		this._rowsPool = new Array;

		this.f_releaseRow.apply(this, list);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_releaseRow : function() {
		for ( var i = 0; i < arguments.length; i++) {
			var row = arguments[i];

			if (row._index === undefined) {
				continue;
			}

			var input = row._input;
			if (input) {
				row._input = undefined;
				row._checkbox = undefined;

				input._row = undefined; // HtmlTRElement
				input._label = undefined;
				input._dataGrid = undefined;
				input._input = undefined; // ????
				input.onmousedown = null;
				input.onmouseup = null;
				input.onclick = null;
				input.ondblclick = null;
				input.onfocus = null;
				input.onblur = null;
				input.onbeforeactivate=null;
				

				f_core.VerifyProperties(input);
			}

			input = row._additionalButton;
			if (input) {
				row._input = undefined;

				input._row = undefined; // HtmlTRElement
				input.onmousedown = null;
				input.onmouseup = null;
				input.onclick = null;
				input.ondblclick = null;
				input.onfocus = null;

				f_core.VerifyProperties(input);
			}

			// row._additional=undefined; // Boolean
			// row._additionalContent=undefined; // String

			var additionalRow = row._additionalRow;
			if (additionalRow) {
				row._additionalRow = undefined; // HTMLRowElement

				additionalRow._parentNode = undefined; // HTMLRowElement
				additionalRow._row = undefined; // HtmlRowElement
				additionalRow._dataGrid = undefined; // f_grid
				// additionalRow._additionalBody=undefined; // Boolean
				additionalRow._waiting = undefined; // f_waiting
			}
			// 			
			// row._className=undefined; // string
			row._cells = undefined; // HtmlElement[]
			row._label = undefined; // HtmlLabelElement
			row._dataGrid = undefined; // f_grid
			// row._index=undefined; // string
			// // row._cellsStyleClass=undefined; // string
			// row._selected=undefined; // Boolean
			row._cellImages = undefined; // HTMLImageELement[]
			// row._hasCursor=undefined; // Boolean
			// row._rowIndex=undefined; // number

			row.onmousedown = null;
			row.onmouseup = null;
			row.onclick = null;
			row.ondblclick = null;
			// row.onmouseout=null;
			// row.onmouseover=null;
			row.onfocus = null;

			// f_core.VerifyProperties(row);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_releaseCells : function() {
		var list = this._cellsPool;
		if (!list || !list.length) {
			return;
		}

		var cell;
		while (cell = list.pop()) {
			cell.onmouseup = null;
			cell.onmousedown = null;
			cell.ondblclick = null;
			cell.onclick = null;
			cell.onfocus = null;
			cell.onbeforeactivate = null;

			cell._label = undefined;
			cell._input = undefined; // Element
			// cell._className=undefined; // string
			cell._dataGrid = undefined; // f_grid
			// cell._cellStyleClasses=undefined; // string[]
			// cell._text=undefined; // string

			// f_core.VerifyProperties(cell);
		}
	},
	/**
	 * @method private
	 * @param f_event
	 *            evt
	 * @return Boolean
	 */
	_performKeyDown : function(evt) {
		var jsEvent = evt.f_getJsEvent();

		return this.f_performKeyDown(jsEvent);
	},
	/**
	 * @method protected
	 * @param Event
	 *            evt
	 * @return Boolean
	 */
	f_performKeyDown : function(evt) {
		var cancel = false;

		var selection = fa_selectionManager.ComputeKeySelection(evt);

	
		var code = evt.keyCode;

		switch (code) {
		case f_key.VK_DOWN: // FLECHE VERS LE BAS
			this._nextCursorRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_UP: // FLECHE VERS LE HAUT
			this._previousCursorRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_PAGE_DOWN: // FLECHE VERS LE BAS
			this._nextPageRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_PAGE_UP: // FLECHE VERS LE HAUT
			this._previousPageRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_END: // FIN
			this._selectLastRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_HOME: // HOME
			this._selectTopRow(evt, selection);
			cancel = true;
			break;

		case f_key.VK_SPACE:
			if (this.f_isCheckable && this.f_isCheckable()) {
				if (evt && evt.target
						&& evt.target.tagName.toLowerCase() == "input") {
					// L'input gere de lui-meme le click ! donc on fait pas le
					// job 2x
					 cancel = true;
					 break;
				 }
				
				var cursor = this._cursor;
				if (cursor) {
					this.fa_performElementCheck(cursor, true, evt, !this
							.fa_isElementChecked(this._cursor));
				}
				cancel = true;
				break;
			}

			// Continue comme une selection ....

		case f_key.VK_RETURN:
		case f_key.VK_ENTER:
			if (this._cursor && this.f_isSelectable()) {
				this.f_performElementSelection(this._cursor, true, evt,
						selection, undefined, undefined, this
								._fillColumnDetails());
			}
			cancel = true;
			break;

		case f_key.VK_CONTEXTMENU:
			this._openContextMenu(evt);
			cancel = true;
			break;

		case f_key.VK_LEFT: // FLECHE A GAUCHE
			cancel = this._processRowLeftKey(evt);
			break;

		case f_key.VK_RIGHT: // FLECHE A DROITE
			cancel = this._processRowRightKey(evt);
			break;

		default:
			if (this._keyRowSearch && f_key.IsLetterOrDigit(code) && !evt.ctrlKey && !evt.altKey && !evt.metaKey) {
				this.f_searchRowNode(code, evt, selection);

				// Dans tous les cas !
				cancel = true;

			} else {
				// Rien on laisse faire !
			}

		}

		if (cancel) {
			return f_core.CancelJsEvent(evt);
		}

		return true;
	},
	/**
	 * @method protected
	 * @param Event
	 *            evt
	 * @return Boolean
	 */
	_processRowLeftKey : function(evt) {
		var additionalInformations = this._additionalInformations;
		if (additionalInformations && (!this._cellFocusable || evt.ctrlKey)) {
			var cursor = this._cursor;
			if (cursor) {
				this._keyHideAdditionalInformation(evt, cursor);
				return true;
			}
		}

		return undefined;
	},
	/**
	 * @method protected
	 * @param Event
	 *            evt
	 * @return Boolean
	 */
	_processRowRightKey : function(evt) {
		var additionalInformations = this._additionalInformations;
		if (additionalInformations && (!this._cellFocusable || evt.ctrlKey)) {
			var cursor = this._cursor;
			if (cursor) {
				this._keyShowAdditionalInformation(evt, cursor);
				return true;
			}
		}

		return undefined;
	},

	/**
	 * @method protected
	 * @param Number
	 *            code Keycode
	 * @param Event
	 *            evt
	 * @param Boolean
	 *            selection
	 * @return Boolean Success
	 */
	f_searchRowNode : function(code, evt, selection) {
		return false;
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @return void
	 */
	_openContextMenu : function(evt) {
		if (!this._cursor) {
			return;
		}

		var menu = this.f_getSubMenuById(f_grid._ROW_MENU_ID);
		if (menu) {
			menu.f_open(evt, {
				component : this._cursor,
				position : f_popup.LEFT_COMPONENT,
				deltaX : 4,
				deltaY : 4
			});
		}
	},
	_updateCurrentSelection : function() {
		var cursorRow = this._cursor;

		if (this.f_isSelectable()) {
			var currentSelection = this._currentSelection;
			for ( var i = 0; i < currentSelection.length; i++) {
				var r = currentSelection[i];
				if (cursorRow == r) {
					cursorRow = undefined;
				}

				this.fa_updateElementStyle(r);
			}
		}

		if (cursorRow) {
			this.fa_updateElementStyle(cursorRow);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_forceFocus: function() {
		f_core.Debug(f_grid, "f_forceFocus: force focus=" + this._focus);

		if (this._focus || this._ignoreFocus) {
			return;
		}

		var cursor = this._cursor;
		if (cursor) {
			this.fa_showElement(cursor, true);
		}

		var cfocus = this._cfocus;
		if (!cfocus && this._inputTabIndex) {
			cfocus=this._inputTabIndex;
		}
		
		if (cfocus) {
			if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6)) {
				// pour *vraiment* donner les focus sous IE
				// genere une exception sous FF : passer par une focntion
				// anonyme ?
				window.setTimeout(cfocus.focus, 0);
			} else {
				// fonctionnement de base
				cfocus.focus();
			}

			return;
		}

		this.focus();
	},
	f_setFocus : function() {
		f_core.Debug(f_grid, "f_setFocus: set focus=" + this._focus);

		if (!f_core.ForceComponentVisibility(this)) {
			return;
		}
		if (this._focus || this.f_isDisabled()) {
			return;
		}

		var cfocus = this.f_getFocusableElement();
		if (cfocus && typeof (cfocus.focus) == "function") {
			cfocus.focus();
			return true;
		}

		if (typeof (this.focus) == "function") {
			this.focus();
		}
		return true;
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param Boolean
	 *            selection
	 * @return void
	 */
	_nextCursorRow : function(evt, selection) {
		// var trs=this._rowsPool; // Attention trs EST DANS LE DESORDRE !

		var tr = this._cursor;
		if (!tr || tr.parentNode != this._tbody) {
			// Selection du premier cursor

			for (tr = this._tbody.firstChild; tr && !tr._dataGrid; tr = tr.nextSibling)
				;

			if (tr) {
				this.f_moveCursor(tr, true, evt, selection);
			}

			return;
		}

		for (tr = tr.nextSibling; tr && !tr._dataGrid; tr = tr.nextSibling)
			;

		if (tr) {
			// Si le CONTROL est appuyé on ne bouge que le curseur !
			this.f_moveCursor(tr, true, evt, selection);
			return;
		}

		// Plus de TR !

		// Page suivante ?
		if (!this._rows) {
			// Pas de page
			return;
		}

		var nextFirst = this._first + this._rows;

		if (!this._paged) { // Rows est défini, mais nous ne sommes pas en mode
			// page !
			var waitingRow = this._waitingRow;
			if (waitingRow) {
				this._performPagedLoading(evt, nextFirst);

				var scrollBody = this._scrollBody;

				var pos = waitingRow.offsetTop + waitingRow.offsetHeight
						- scrollBody.offsetHeight;
				scrollBody.scrollTop = pos;
			}

			return;
		}

		if (this._rowCount >= 0) {
			if (nextFirst >= this._rowCount) {
				return;
			}
		} else {
			if (nextFirst > this._maxRows) {
				return;
			}
		}

		this.f_setFirst(nextFirst, nextFirst, selection);
	},
	_previousCursorRow : function(evt, selection) {
		// var trs=this._rowsPool;

		var tr = this._cursor;
		if (!tr || tr.parentNode != this._tbody) {
			// Selection du dernier

			for (tr = this._tbody.lastChild; tr && !tr._dataGrid; tr = tr.previousSibling)
				;
			if (tr) {
				this.f_moveCursor(tr, true, evt, selection);
			}

			return;
		}

		for (tr = tr.previousSibling; tr && !tr._dataGrid; tr = tr.previousSibling)
			;
		if (tr) {
			this.f_moveCursor(tr, true, evt, selection);
			return;
		}

		// Plus de rows

		// Page pr?cedente ?
		if (!this._rows || !this._paged) {
			// Pas de page
			return;
		}

		if (this._first < 1) {
			return;
		}

		var nextFirst = this._first - this._rows;
		if (nextFirst < 0) {
			nextFirst = 0;
		}

		var nextPos = nextFirst + this._rows - 1;
		if (nextPos >= this._rowCount) {
			nextPos = this._rowCount - 1;
		}

		this.f_setFirst(nextFirst, nextPos, selection);
	},
	_nextPageRow : function(evt, selection) {
		var trs = this._rowsPool;
		if (!trs.length) {
			return;
		}

		// Il faut rechercher le dernier visible !
		var last = null;
		var tr = this._tbody.firstChild;
		for (; tr; tr = tr.nextSibling) {
			if (!tr._dataGrid) {
				continue;
			}

			if (tr.offsetTop + tr.offsetHeight / 2 - this._scrollBody.scrollTop > this._scrollBody.clientHeight) {
				// On le voit plus !
				break;
			}

			last = tr;
		}

		if (last && last != this._cursor) {
			this.f_moveCursor(last, true, evt, selection);
			return;
		}

		if (this._rows && this._paged) {
			// Table Page par Page

			// On cherche le dernier

			var tr = this._tbody.lastChild;
			for (; tr && !tr._dataGrid; tr = tr.previousSibling)
				;

			if (tr && this._cursor != tr) {
				this.f_moveCursor(tr, true, evt, selection);
				return;
			}

			// Page suivante ...

			if (this._rows < 1) {
				return;
			}

			var nextFirst = this._first + this._rows;
			if (this._rowCount >= 0) {
				if (nextFirst >= this._rowCount) {
					return;
				}
			} else {
				if (nextFirst > this._maxRows) {
					return;
				}
			}

			var nextPos = nextFirst + this._rows - 1;
			if (nextPos >= this._rowCount) {
				nextPos = this._rowCount - 1;
			}

			this.f_setFirst(nextFirst, nextPos, selection);

			return;
		}

		// Pas de page par page ...

		// On recherche notre index, et la hauteur d'une ligne
		var trh = 0;
		var idx = -1;
		var i = 0;
		for (tr = this._tbody.firstChild; tr; tr = tr.nextSibling, i++) {
			if (!tr._dataGrid) {
				continue;
			}

			if (trh < 1) {
				trh = tr.offsetHeight;
			}

			if (tr == this._cursor) {
				idx = i;
			}

			if (idx >= 0 && trh > 0) {
				break; // On a trouvé l'index et la hauteur d'une ligne
			}
		}
		if (trh <= 0 || idx < 0) {
			// On a pas trouvé notre index, ou la hauteur d'une ligne
			return;
		}

		var h = this._scrollBody.clientHeight;

		// 
		var pos = Math.floor(idx + h / trh);

		f_core.Debug(f_grid, "_nextPageRow: Pos=" + pos + " idx=" + idx + " h="
				+ h + " trh=" + trh + " rowCount=" + this._rowCount + " trs="
				+ trs.length);

		if (pos >= trs.length) {
			pos = trs.length - 1;
		}
		// f_core.Assert(pos>=trs.length, "f_grid._nextPageRow: Invalid position
		// !");

		// On se positionne sur le row recherché !
		tr = this._tbody.childNodes[pos];

		if (tr == this._cursor) {
			tr = tr.nextSibling;
		}

		// On cherche juste apres !
		for (; tr && !tr._dataGrid; tr = tr.nextSibling)
			;

		f_core.Debug(f_grid, "_nextPageRow: Found next row=" + tr);

		if (!tr) {
			tr = this._tbody.childNodes[pos];

			if (tr != this._cursor) {
				// On cherche juste avant !
				for (; tr && !tr._dataGrid; tr = tr.previousSibling)
					;

				f_core.Debug(f_grid, "_nextPageRow: Found previous row=" + tr);
			}
		}

		// alert("Paged="+this._paged+" pos="+pos+" trs.length="+trs.length);
		if (!this._paged && pos == trs.length - 1) {
			var waitingRow = this._waitingRow;

			// f_core.Debug(f_grid, "Waiting row="+waitingRow);
			if (waitingRow) {
				this._performPagedLoading(evt, pos);

				var scrollBody = this._scrollBody;

				var pos = waitingRow.offsetTop + waitingRow.offsetHeight
						- scrollBody.offsetHeight;
				scrollBody.scrollTop = pos;
			} else {
				// Pas de waiting row ?
			}
		}

		if (tr) {
			this.f_moveCursor(tr, true, evt, selection);
		}
	},
	_previousPageRow : function(evt, selection) {
		var trs = this._rowsPool;
		if (!trs.length) {
			return;
		}

		// Il faut rechercher le dernier visible !
		var tr = null;
		for (tr = this._tbody.firstChild; tr; tr = tr.nextSibling) {
			if (!tr._dataGrid) {
				continue;
			}

			if (tr.offsetTop + tr.offsetHeight / 2 - this._scrollBody.scrollTop < 0) {
				continue;
			}

			// Il est visible !
			if (tr && tr != this._cursor) {
				this.f_moveCursor(tr, true, evt, selection);
				return;
			}

			break;
		}

		// Le curseur est en haut de notre page !

		if (this._rows && this._paged && this._scrollBody.scrollTop == 0) {
			// Page pr?c?dante ...

			if (this._first <= 0) {
				return;
			}

			var nextFirst = this._first - this._rows;
			if (nextFirst < 0) {
				nextFirst = 0;
			}

			this.f_setFirst(nextFirst, nextFirst, selection);

			return;
		}

		// On recherche notre index, et la hauteur d'une ligne
		var trh = 0;
		var idx = -1;
		var i = 0;
		for (tr = this._tbody.firstChild; tr; tr = tr.nextSibling, i++) {
			if (!tr._dataGrid) {
				continue;
			}

			if (trh < 1) {
				trh = tr.offsetHeight;
			}

			if (tr == this._cursor) {
				idx = i;
			}

			if (idx >= 0 && trh) {
				break; // On a trouvé l'index et la hauteur d'une ligne
			}
		}

		if (trh <= 0 || idx < 0) {
			// On a pas trouvé notre index, ou la hauteur d'une ligne
			f_core.Debug(f_grid,
					"_previousPageRow: Can not compute the size of a row !");

			return;
		}

		var h = this._scrollBody.clientHeight;

		var pos = Math.floor(idx - h / trh);
		if (pos < 0) {
			pos = 0;
		}

		// On se positionne sur le row recherché !
		tr = this._tbody.childNodes[pos];

		if (tr == this._cursor) { // On tombe encore sur le curseur !
			tr = tr.previousSibling;
		}

		// On cherche juste avant !
		for (; tr && !tr._dataGrid; tr = tr.previousSibling)
			;

		if (tr) {
			this.f_moveCursor(tr, true, evt, selection);
		}
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param Boolean
	 *            selection
	 * @return void
	 */
	_selectLastRow : function(evt, selection) {
		var tr = this._tbody.lastChild;
		for (; tr && !tr._dataGrid; tr = tr.previousSibling)
			;

		if (!tr) {
			return;
		}

		if (tr != this._cursor) {
			this.f_moveCursor(tr, true, evt, selection);
			return;
		}

		var rowCount = this._rowCount; // Nombre total
		var rows = this._rows; // Nombre a afficher (0= pas de mode page)

		// Le meme .... ben on va à la derniere page
		if (rowCount < rows || rows < 1 || !this._paged) {
			return;
		}

		var nextFirst = rowCount - ((rowCount + rows - 1) % rows) - 1;
		if (nextFirst <= this._first) {
			return;
		}

		var nextPos = rowCount - 1;

		this.f_setFirst(nextFirst, nextPos, selection);
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param Boolean
	 *            selection
	 * @return void
	 */
	_selectTopRow : function(evt, selection) {
		var tr = this._tbody.firstChild;
		for (; tr && !tr._dataGrid; tr = tr.nextSibling)
			;

		if (!tr) {
			return;
		}

		if (tr != this._cursor) {
			this.f_moveCursor(tr, true, evt, selection);
			return;
		}

		// Le meme .... ben on va ? la premiere page

		if (this._first < 1 || !this._paged) {
			return;
		}

		var nextFirst = 0;
		var nextPos = 0;

		this.f_setFirst(nextFirst, nextPos, selection);
	},
	/**
	 * @method hidden
	 */
	f_enableSorters : function(sortColumnIndex1, ascending1, sortColumnIndex2,
			ascending2) {
		var cols = this._columns;
		var currentSorts = this._currentSorts;
		if (!currentSorts) {
			currentSorts = new Array;
			this._currentSorts = currentSorts;
		}

		for ( var i = 0; i < arguments.length;) {
			var sortColumnIndex = arguments[i++];
			var ascending = !!arguments[i++];

			f_core.Assert(
					sortColumnIndex >= 0 && sortColumnIndex < cols.length,
					"f_grid.f_enableSorters: Bad sortColumnIndex !");

			var col = cols[sortColumnIndex];
			col._ascendingOrder = ascending;

			currentSorts.push(col);

			this._updateTitleStyle(col);

			if (col._method != f_grid.Sort_Server) {
				this._initSort = true;
			}
		}
		this._setSortIndexes();
	},
	/**
	 * @method public
	 * @param f_gridColumn
	 *            col Column to sort
	 * @param optional
	 *            Boolean ascending Sort ascending.
	 * @param optional
	 *            Boolean append Append the sort.
	 * @param optional
	 *            f_gridColumn col2 Column 2
	 * @param optional
	 *            Boolean ascending2 Sort ascending2.
	 * @return void
	 */
	f_setColumnSort : function(col, ascending, append, col2, ascending2) {
		// var args=[false];

		f_core.Debug(f_grid, "f_setColumnSort: append=" + append + " col1="
				+ col + " asc1=" + ascending + " col2=" + col2 + " asc2="
				+ ascending2);

		if (!col._method) {
			return;
		}

		if (ascending === undefined) {
			if (col._ascendingOrder === undefined) {
				ascending = true;
			} else {
				ascending = !col._ascendingOrder;
			}
		}

		var currentSorts = this._currentSorts;
		if (!currentSorts) {
			currentSorts = new Array;
			this._currentSorts = currentSorts;
		}

		f_core.Debug(f_grid, "f_setColumnSort: Sort col=" + col._index
				+ " ascending=" + ascending + " append=" + append);

		if (this._rows > 0) { // Dans le cas d'un page par page, on revient en
			// position 0
			this._changeFirst(0);
		}

		if (currentSorts.length) {
			// Ajout Fred pour éviter le cas où on appelle un tri sur
			// plusieurs col dont la première est déjà triée
			if ((append || currentSorts.length == 1)
					&& currentSorts[currentSorts.length - 1] == col && !col2) {
				// f_core.Debug(f_grid, "f_setColumnSort: Just inverse");
				col._ascendingOrder = ascending;

				this._updateTitleStyle(col);
				this._sortTable(true);
				return;
			}
		}

		if (!append && currentSorts.length) {

			f_core.Debug(f_grid, "f_setColumnSort: Remove olds");

			for ( var i = 0; i < currentSorts.length; i++) {
				var old = currentSorts[i];

				old._ascendingOrder = undefined;
				this._updateTitleStyle(old);
			}

			currentSorts = new Array;
			this._currentSorts = currentSorts;
		}

		if (!currentSorts.f_addElement(col)) {
			// Déjà connu !

			// f_core.Debug(f_grid, "f_setColumnSort: Already known ???");

			if (col._ascendingOrder == ascending && append) {
				// Et dans le même sens !
				return;
			}
		}

		f_core.Debug(f_grid, "f_setColumnSort: Change order '" + ascending
				+ "'");

		if (col._ascendingOrder != ascending) {
			col._ascendingOrder = ascending;
			this._updateTitleStyle(col);
		}

		if (!append) {
			for ( var i = 3; i < arguments.length;) {
				col = arguments[i++];
				ascending = arguments[i++];

				if (!currentSorts.f_addElement(col)) {
					continue;
				}

				col._ascendingOrder = !!ascending;
				this._updateTitleStyle(col);
			}
		}

		this._sortTable(true);
	},

	/**
	 * gives the ordered set of sorted columns
	 * 
	 * @author Fred Lefevere-Laoide
	 * @method public
	 * @return f_gridColumn[]
	 */
	f_getSortedColumns : function() {
		var currentSorts = this._currentSorts;
		if (currentSorts && currentSorts.length) {

			f_core.Debug(f_grid, "f_getSortedColumns: create the array");

			var newSorts = new Array;

			newSorts.push.apply(newSorts, currentSorts);

			return newSorts;
		}
		return new Array;
	},

	/**
	 * Clean the sort
	 * 
	 * @author Fred Lefevere-Laoide
	 * @method public
	 * @return void
	 */
	f_clearSort : function() {
		var currentSorts = this._currentSorts;
		if (!currentSorts) {
			return;
		}
		if (currentSorts.length) {

			f_core.Debug(f_grid, "f_clearSort: Remove olds");

			for ( var i = 0; i < currentSorts.length; i++) {
				var old = currentSorts[i];

				old._ascendingOrder = undefined;
				this._updateTitleStyle(old);
			}

			currentSorts = new Array;
			this._currentSorts = currentSorts;
		}
	},

	/**
	 * @method private
	 */
	_updateTitleStyle : function(column) {
		f_core.Assert(column && column._head,
				"f_grid._updateTitleStyle: Invalid column parameter (" + column
						+ ")");

		var suffix = "";
		if (this.f_isDisabled()) {
			// rien

		} else if (this._columnSelected == column) {
			suffix += "_selected";

		} else if (this._columnOver == column) {
			suffix += "_over";
		}

		var tcellClassName = "f_grid_tcell";
		var columnStyleClass = column._styleClass;

		var className = tcellClassName;
		if (columnStyleClass) {
			className += " " + columnStyleClass;
		}
		if (suffix) {
			className += " " + tcellClassName + suffix;
			if (columnStyleClass) {
				className += " " + columnStyleClass + suffix;
			}
		}

		if (column._method) {
			className += " " + tcellClassName + "_sorter";
		}

		var head = column._head;
		if (head.className != className) {
			head.className = className;
		}

		className = "f_grid_ttext";

		var stextClassName = "f_grid_stext";

		var suffix = "";
		var wc = className;
		var titleAlt = this.f_getColumnName(column);
		var sorterAttributeName = "defSorter";
		if (column._ascendingOrder !== undefined) {
			if (column._ascendingOrder) {
				suffix = "_ascending";
				titleAlt += " "
						+ f_resourceBundle.Get(f_grid).f_get("ASCENDING_SORT");
				sorterAttributeName = "ascSorter";

			} else {
				suffix = "_descending";
				titleAlt += " "
						+ f_resourceBundle.Get(f_grid).f_get("DESCENDING_SORT");
				sorterAttributeName = "descSorter";
			}
			className += " " + className + suffix;
			stextClassName += " " + stextClassName + suffix;

		} else if (column._method || column._sorter) {
			titleAlt += " "+f_resourceBundle.Get(f_grid).f_get("NO_SORT");
		}

		if (column._sorterImage) {
			column._sorterImage.src = f_core.GetAttributeNS(this,
					sorterAttributeName, "");
		}

		this._updateTitleCellBody(column);

		var box = column._box;
		var label = column._label;

		if (column._restoreClass) {
			column._restoreClass = className;
			className = wc;
		}

		if (label.className != className) {
			label.className = className;
		}
		if (box.className != stextClassName) {
			box.className = stextClassName;
		}
		if (label.title != titleAlt) {
			label.title = titleAlt;
		}

		var image = column._image;
		if (image) {
			var imageURL = undefined;

			if (this.f_isDisabled()) {
				imageURL = column._titleDisabledImageURL;

			} else if (this._columnSelected == column) {
				imageURL = column._titleSelectedImageURL;

			} else if (this._columnOver == column) {
				imageURL = column._titleHoverImageURL;
			}

			if (!imageURL) {
				imageURL = column._titleImageURL;
			}

			if (image.src != imageURL) {
				image.src = imageURL;
			}
		}
	},
	/**
	 * @method private
	 * @param Object
	 *            column
	 * @param optional
	 *            Number swidth
	 * @return void
	 */
	_updateTitleCellBody : function(column, swidth) {

		if( f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_7)) {
			return;
		}
		
		if (swidth === undefined) {
			var cw = column._head.style.width;
			if (!cw) {
				cw = column._head.offsetWidth;
			}

			swidth = parseInt(cw, 10);
		}

		if (this._textLeftRightPadding === undefined) {
			this._textLeftRightPadding = f_core.ComputePaddingBoxBorderLength(
					column._box.parentNode, "left", "right");
			// alert(this._textLeftRightPadding);
		}

		if (this._textLeftRightPadding > 0) {
			swidth -= this._textLeftRightPadding;
			if (swidth < 0) {
				swidth = 0;
			}
		}

		var box = column._box;
		var label = column._label;

		var boxStyle = box.style;
		var sw = (swidth > 0 ? swidth : 0) + "px";
		if (boxStyle.width != sw) {
			boxStyle.width = sw;
			boxStyle.maxWidth = sw;
		}

		var cursor = column._cursor;
		if (cursor) {
			// cursor.style.left=(swidth-4)+"px";
		}

		if (column._ascendingOrder !== undefined) {
			swidth -= this._sortPadding;
			sw = (swidth > 0 ? swidth : 0) + "px";
		}

		var labelStyle = label.style;
		if (labelStyle.width != sw) {
			labelStyle.width = sw;
			labelStyle.maxWidth = sw;
		}
	},
	fa_updateFilterProperties : function(filterProperties) {
		if (!this._interactive) {
			return false;
		}

		this.f_appendCommand(function(dataGrid) {
			if (dataGrid._rows > 0) {
				// Page par page !
				// On ne sait plus le nombre de lignes ...
				dataGrid._rowCount = -1;
				dataGrid._maxRows = dataGrid._rows;
			}

			dataGrid.f_callServer(0);
		});

		return false;
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	fa_componentCaptureMenuEvent : function() {
		return null;
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_verifyWaitingPosition : function(evt) {
		var scrollBody = this._scrollBody;

		var waitingRow = this._waitingRow;
		if (waitingRow && f_core.GetParentNode(waitingRow)) {
			var sbTop = scrollBody.scrollTop + scrollBody.offsetHeight;

			var pos = waitingRow.offsetTop - sbTop;

			if (pos < 0) {
				if (this._waitingMode == f_grid.END_WAITING) {
					if (!this._waitingLoading) {
						this._performPagedLoading(evt);
					}

				} else {
					// Recherche le dernier index visible !

					var nodes = waitingRow.parentNode.childNodes;

					var i = 0;
					for (; i < nodes.length; i++) {
						var node = nodes[i];

						if (node.offsetTop > sbTop) {
							break;
						}
					}

					if (this._waitingLoading) {
						this._performRowsLoading(evt, i);

					} else { // Pour eviter que l'on lance le download, on
						// attend que ca se stabilise
						this._waitingLoading = true;

						i += Math.floor(this._rows / 2); // On prend une
						// petite marge ...

						var self = this;
						f_core.GetWindow(dataGrid.ownerDocument).setTimeout(
								function() {
									self._performRowsLoading(evt, i);

									self = null;
								}, 100);
					}
				}

			}
		}

		return true;
	},
	/**
	 * @method private
	 * 
	 * @return void
	 */
	_setSortIndexes : function() {
		var currentSorts = this._currentSorts;
		if (!currentSorts || !currentSorts.length) {
			return;
		}
		var serial = new Array;

		for ( var i = 0; i < currentSorts.length; i++) {
			var col = currentSorts[i];
			serial.push(col._index, col._ascendingOrder);
		}

		this._sortIndexes = serial.join(",");
	},
	/**
	 * @method private
	 * @param optional
	 *            Boolean userSort
	 * @return void
	 */
	_sortTable : function(userSort) {
		this.f_updateSortBreadCrumbs();

		var currentSorts = this._currentSorts;
		if (!currentSorts || !currentSorts.length) {
			return;
		}

		var methods = new Array;
		var tdIndexes = new Array;
		var ascendings = new Array;

		var serverSort = false;
		var columns = this._columns;

		var serial = new Array;

		for ( var i = 0; i < currentSorts.length; i++) {
			var col = currentSorts[i];

			var method = col._method;
			methods.push(method);
			if (method == f_grid.Sort_Server) {
				serverSort = true;
			}

			ascendings.push(col._ascendingOrder);

			var columnIndex = col._index;
			var tdIndex = 0;
			for ( var j = 0; j < columns.length; j++) {
				var col2 = columns[j];
				if (!col2._visibility) {
					continue;
				}

				if (columnIndex == j) {
					break;
				}

				tdIndex++;
			}
			tdIndexes.push(tdIndex);

			serial.push(col._index, col._ascendingOrder);
		}

		this._sortIndexes = serial.join(",");

		// Gestion du scroll horizontal sur FF
		var scrollLeft = this._scrollBody.scrollLeft;

		if (userSort
				&& this.f_fireEvent(f_event.SORT, null, currentSorts) === false) {

			this.f_resetScrollLeft(scrollLeft);

			return;
		}

		if (this._rowCount < 0 || (this._rows && this._rows < this._rowCount)
				|| serverSort) {
			// Plusieurs pages !
			// Il faut partir coté serveur !

			f_core.Debug(f_grid, "_sortTable: SERVER:\nserial='"
					+ this._sortIndexes + "'\nrowCount=" + this._rowCount
					+ "\nrows=" + this._rows);

			this.f_setFirst(this._first);

			this.f_resetScrollLeft(scrollLeft);

			return;
		}

		f_core.Debug(f_grid, "_sortTable: CLIENT:\ntdIndexes=" + tdIndexes
				+ "\nascendings=" + ascendings + "\nSort=" + methods);

		this.f_sortClientSide(methods, ascendings, tdIndexes);

		this.f_resetScrollLeft(scrollLeft);
	},

	/**
	 * reset the scrollLeft when a column is sorted Firefox only
	 * 
	 * @method private
	 * @param Number
	 *            scrollLeft
	 * @return void
	 */
	f_resetScrollLeft : function(scrollLeft) {
		if (scrollLeft == 0 || f_core.IsGecko() == false) {
			return;
		}
		var _datagrid = this;
		window.setTimeout(function() {
			if (window._rcfacesExiting) {
				return;
			}

			if (scrollLeft != _datagrid._scrollBody.scrollLeft) {
				_datagrid._scrollBody.scrollLeft = scrollLeft;
				_datagrid._scrollTitle.scrollLeft = scrollLeft;
			}
			_datagrid = null;
		}, 1);
	},

	/**
	 * Return the value of the row which contains the specified component.
	 * 
	 * @method public
	 * @param f_component
	 *            component Component or HTMLElement
	 * @return Object Value of the row
	 * @deprecated
	 */
	f_getRowValueFromCommponent : function(component) {
		return this.f_getRowValueFromComponent(component);
	},

	/**
	 * Return the value of the row which contains the specified component.
	 * 
	 * @method public
	 * @param f_component
	 *            component Component or HTMLElement
	 * @return Object Value of the row
	 */
	f_getRowValueFromComponent : function(component) {
		while (component && typeof (component._rowIndex) != "number") {
			component = component.parentNode;
		}

		if (!component) {
			return null;
		}

		return component._index;
	},

	/**
	 * Select a row
	 * 
	 * @method public
	 * @param any
	 *            rowValue Value associated to the row
	 * @param optional
	 *            Boolean append Inclusive or Exclusive selection
	 * @param optional
	 *            Boolean show Show the selected row.
	 * @param optional
	 *            hidden Event jsEvent
	 * @return Boolean Returns <code>true</code> if success.
	 */
	f_selectRow : function(rowValue, append, show, jsEvent) {
		var row = this.f_getRowByValue(rowValue, true);

		if (this.f_isRowSelected(row)) {
			return false;
		}

		var selection = (append) ? fa_selectionManager.APPEND_SELECTION : 0;

		return this.f_performElementSelection(row, show, jsEvent, selection);
	},
	/**
	 * Deselect a row
	 * 
	 * @method public
	 * @param any
	 *            rowValue Value associated to the row
	 * @param optional
	 *            Boolean show Show the deselected row.
	 * @param optional
	 *            hidden Event jsEvent
	 * @return Boolean Returns <code>true</code> if success.
	 */
	f_deselectRow : function(rowValue, show, jsEvent) {
		var row = this.f_getRowByValue(rowValue, true);

		if (!this.fa_isElementSelected(row)) {
			return false;
		}

		return this.f_performElementSelection(row, show, jsEvent, false);
	},
	/**
	 * Returns <code>true</code> if the receiver is checked, and
	 * <code>false</code> otherwise
	 * 
	 * @method public
	 * @param any
	 *            rowValue Value associated to the row, or a row object.
	 * @return Boolean The checked state of the row
	 */
	f_isSelected : function(rowValue) {
		var row = this.f_getRowByValue(rowValue, true);

		return this._isElementValueSelected(row);
	},

	fa_getElementItem : function(row) {
		return row;
	},

	fa_getElementValue : function(row) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_getElementValue: Invalid element parameter ! ("
						+ row + ")");

		return row._index;
	},

	fa_isElementDisabled : function(row) {
		return false;
	},

	fa_isElementSelected : function(row) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_isElementSelected: Invalid element parameter ! ("
						+ row + ")");

		return row._selected;
	},

	fa_setElementSelected : function(row, selected) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_setElementSelected: Invalid element parameter ! ("
						+ row + ")");

		row._selected = selected;
	},

	fa_showElement : function(row, giveFocus) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_showElement: Invalid element parameter ! (" + row
						+ ")");

		var scrollBody = this._scrollBody;
		f_core.Debug(f_grid, "fa_showElement: row.y=" + row.offsetTop
				+ " row.h=" + row.offsetHeight + " scrollBody.y="
				+ scrollBody.scrollTop + " scrollBody.h="
				+ scrollBody.clientHeight);

		if (row.offsetTop - scrollBody.scrollTop < 0) {
			// Bug Firefox de repositionnement de la scrollbar
			scrollBody.scrollTop = -1;
			scrollBody.scrollTop = 99999;
			scrollBody.scrollTop = row.offsetTop;

			f_core.Debug(f_grid, "fa_showElement: set scrollTop to "
					+ row.offsetTop);

		} else if (row.offsetTop + row.offsetHeight - scrollBody.scrollTop > scrollBody.clientHeight) {
			// Bug Firefox de repositionnement de la scrollbar
			scrollBody.scrollTop = -1;
			scrollBody.scrollTop = 99999;
			scrollBody.scrollTop = row.offsetTop + row.offsetHeight
					- scrollBody.clientHeight;

			f_core
					.Debug(
							f_grid,
							"fa_showElement: set scrollTop to "
									+ (row.offsetTop + row.offsetHeight - scrollBody.clientHeight));
		}

		f_core.ShowComponent(row);
	},

	fa_listVisibleElements : function(ordered) {
		if (ordered) {
			var body = this._table.tBodies[0];
			f_core.Assert(body,
					"f_grid._sortTable: No body for data table of dataGrid !");

			var trs = new Array;
			var childNodes = body.rows;
			// var idx=0;
			for ( var i = 0; i < childNodes.length; i++) {
				var row = childNodes[i];
				if (row._index === undefined) {
					continue;
				}

				trs.push(row);
			}
			return trs;
		}
		return this._rowsPool;
	},
	fa_getScrolledComponent : function() {
		return this._scrollBody;
	},
	fa_getScrolledHorizontalTitle : function() {
		return this._scrollTitle;
	},
	fa_getScrolledVerticalTitle : function() {
		return null;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_initializeTableLayout : function() {
		// var table = f_core.GetChildByCssClass(this, "f_grid_table");
		var table = this.ownerDocument.getElementById(this.id
				+ f_grid._DATA_TABLE_ID_SUFFIX);
		f_core
				.Assert(table,
						"f_grid.f_initializeTableLayout: Can not find table 'f_grid_table'");
		this._table = table;
		table._dataGrid = this;

		f_core.Assert(table.tBodies.length <= 1,
				"f_grid.f_initializeTableLayout: Too many TBODY ! ("
						+ table.tBodies.length + ")");
		var firstTBody = table.tBodies[0];

		this._tbody = firstTBody; // bodies[0];
		if (firstTBody && !firstTBody.firstChild) {
			table.removeChild(firstTBody);
		}
		this._tbody.style.display = "none";

		this._thead = table.tHead;
		if (this._thead) {
			// this._thead.style.display = "none";
		}

		var scrollBody = this;
		var catchScrollEvent = false;
		this._title = this.ownerDocument.getElementById(this.id
				+ f_grid._FIXED_HEADER_ID_SUFFIX);
		// this._title=f_core.GetChildByCssClass(this,"f_grid_fttitle");
		if (this._title) {
			// this._scrollTitle=f_core.GetChildByCssClass(this,
			// "f_grid_dataTitle_scroll");
			this._scrollTitle = this.ownerDocument.getElementById(this.id
					+ f_grid._DATA_TITLE_SCROLL_ID_SUFFIX);
		}

		if (true) {
			// var dataBodyClassName="f_grid_dataBody_scroll";
			// scrollBody=f_core.GetChildByCssClass(this, dataBodyClassName);
			scrollBody = this.ownerDocument.getElementById(this.id
					+ f_grid._DATA_BODY_SCROLL_ID_SUFFIX);

			if (!scrollBody) {
				scrollBody = this;
			}

			catchScrollEvent = true;
		}

		if (scrollBody) {
			this._scrollBody = scrollBody;
			scrollBody._dataGrid = this;

			if (catchScrollEvent) {
				scrollBody.onscroll = f_grid._OnScroll;
			}

			if (f_core.IsGecko()) {
				scrollBody.addEventListener("DOMMouseScroll",
						f_grid._Link_onmousewheel, false);
			} else {
				scrollBody.onmousewheel = f_grid._Link_onmousewheel;
			}
		}
	},
	/**
	 * @method hidden
	 */
	f_updateColumnsLayout : function() {
		if (this._columnsLayoutPerformed) {
			return;
		}
		this._columnsLayoutPerformed = true;

		var heads = undefined;
		var cols;

		var columns = this._columns;

		if (this._title) {
			heads = this._title.getElementsByTagName("li");

		} else {
			// Ancien mode, ou le tableau ne faisait qu'un seul TABLE

			if (this._visibleHeader) {
				heads = this._table.getElementsByTagName("th");
			}
		}
		cols = this._table.getElementsByTagName("col");

		var resourceBundle = f_resourceBundle.Get(f_grid);
		var headCursorTitle = resourceBundle.f_get("COLUMN_RESIZE");

		var isInternetExplorer = f_core.IsInternetExplorer();

		var v = 0;
		for ( var i = 0; i < columns.length;) {
			var column = columns[i++];

			if (!column._visibility) {
				continue;
			}

			column._col = cols[v];

			if (!heads) {
				continue;
			}

			var head = heads[v];
			if (!head) {
				continue;
			}

			head.onmouseover = f_grid._Title_onMouseOver;
			head.onmouseout = f_grid._Title_onMouseOut;
			head.onmousedown = f_grid._Title_onMouseDown;
			head.onmouseup = f_grid._Title_onMouseUp;
			head.onclick = f_grid._Title_onClick; // OO  TODO .... A VERIFIER
											 	 // JBM FIXME .... accessibilite
			// head.onbeforeactivate=f_core.CancelJsEventHandler;
			// head.tabIndex=-1;

			head._column = column;
			column._head = head;

			if (column._titleToolTipId) {
				head._toolTipId = column._titleToolTipId;
				head._toolTipContent = column._titleToolTipContent;
			}

			var box = f_core.GetFirstElementByTagName(head, "div");
			f_core
					.Assert(box && box.nodeType == f_core.ELEMENT_NODE,
							"f_grid.f_updateColumnsLayout: Invalid structure of header (no DIV)");
			column._box = box;

			var label = f_core.GetFirstElementByTagName(box,
					(column._sorter !== undefined) ? "a" : "div");
			if (!label && column._sorter) {
				// C'est triable mais pas (encore) focusable !
				label = f_core.GetFirstElementByTagName(box, "div");
			}
			f_core
					.Assert(label && label.nodeType == f_core.ELEMENT_NODE,
							"f_grid.f_updateColumnsLayout: Invalid structure of header (no Label)");
			column._label = label;

			if (column._sorter) {
				// label.onmousedown=f_grid._Title_onMouseDown;
				// label.onmouseup=f_grid._Title_onMouseUp;
				label.onfocus = f_grid._Title_onFocus;
				label.onblur = f_grid._Title_onBlur;
				label.onclick = f_grid._Title_onClick;
				label.onkeydown = f_grid._Title_onKeyDown;
				label._column = column;

				var tid = label.id.substring(0, label.id.lastIndexOf("::"))
						+ "::sorter";
				column._sorterImage = document.getElementById(tid);
			}

			if (column._hasImage) {
				var image = f_core.GetFirstElementByTagName(label, "img");
				if (image) {
					column._image = image;
				}
			}

			if (column._resizable) {
				var cursor = f_core.CreateElement(box, "div", {
					title : headCursorTitle,
					className : "f_grid_colCursor"
				});
				column._cursor = cursor;
				cursor._column = column;
				cursor.onmousedown = f_grid._TitleCursorMouseDown;
				cursor.onclick = f_core.CancelJsEventHandler;

				if (isInternetExplorer) {
					// Ben oui ... il faut bien !
					cursor.style.right = "-8px";
				}
			}

			v++;
		}
	},
	/**
	 * @method protected
	 */
	f_updateTitle : function() {
		if (!this._title) {
			return;
		}

		var body = this._scrollBody;
		var clientWidth = body.clientWidth;
		var offsetWidth = body.offsetWidth;
		var scrollBarWidth = offsetWidth - clientWidth;
		var verticalScrollBar = (scrollBarWidth > 0);
		
		var ie = f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_7)
				|| f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_6);
		
		if (scrollBarWidth <= 0) {
			// Ben si y a pas de scrollbar a droite, on cherche en bas !
			scrollBarWidth = body.offsetHeight - body.clientHeight;

			if (scrollBarWidth <= 0) {
				scrollBarWidth = 1;
			}
		}

		
		var sw = this.style.width;
		if (sw && sw.indexOf("px") > 0) {
			var swPixel = parseInt(sw);

			swPixel -= f_core.ComputeContentBoxBorderLength(this, "left",
					"right");
			if (this._scrollTitle) {
				this._scrollTitle.style.width = swPixel + "px";
			}
			body.style.width = swPixel + "px";

			offsetWidth = swPixel;
			if (verticalScrollBar) {
				clientWidth = offsetWidth - scrollBarWidth;
			}
			
		}

		if (!this._columnsLayoutPerformed) {
			this.f_updateColumnsLayout();
		}

		this._titleLayout = true;

		var columns = this._columns;

		var total = 0; // total des colonnes fixe en px
		var totalPercent = 0; // total des %
		var totalZero = 0; // total colone sans taille donnee
		var colToProcess = new Array();
		
		for (var i = 0; i < columns.length; i++) {
			var column = columns[i];
			if (column._visibility === false) {
				continue;
			}

			var col = undefined;
			if (ie) {
				// the header column tag col does not have any size. So we get a
				// cell.
				var rows = this._rowsPool;
				if (rows.length > 0 && rows[0]._cells) {
					col = rows[0]._cells[i];
				}
			}

			if (!col) {
				col = column._col;
			}

			if (!col) {
				break;
			}

			if (column._widthComputed) {
				totalZero++;
				colToProcess.push(column);
				continue;
			}

			if (column._widthPercent !== undefined) {
				totalPercent += column._widthPercent;
				colToProcess.push(column);
				continue;
			}

			if (!column._widthSetted) {
				
				var styleWidth = col.style.width;
				
				if (f_core.IsInternetExplorer(f_core.INTERNET_EXPLORER_7)) {
					styleWidth = col.currentStyle.width;
				}
				var w = parseInt(styleWidth);
				
				if(ie && !isNaN(w) ) {
					column._borderAndPaddingWidth = f_core
							.ComputeContentBoxBorderLength(col, "left", "right");
						//f_core.ComputeIEBoxModelWidth(col);
					col.style.width = (w-column._borderAndPaddingWidth);
					column._col.style.width =(w-column._borderAndPaddingWidth);
				}
				
				if (styleWidth.indexOf("%") > 0) {
					totalPercent += w;
					column._widthPercent = w;
					colToProcess.push(column);
					continue;
				}

				if (!w) {
					totalZero++;
					column._widthComputed = true;
					colToProcess.push(column);
					continue;
				}

				if (isNaN(w) || w < 0) {
					w = 0;
				}

				column._widthSetted = w;
			}

			var w = column._widthSetted;
			total += w;

			if (this._textLeftRightPadding === undefined) {
				this._textLeftRightPadding = f_core
						.ComputePaddingBoxBorderLength(column._box.parentNode,
								"left", "right");
			}

			var cw = w;
			if (f_core.IsWebkit(f_core.WEBKIT_SAFARI)) {
				cw -= this._textLeftRightPadding;
			}
			
			if (ie){
				cw-=column._borderAndPaddingWidth;
			}
			
			if (cw < 0) {
				cw = 0;
			}
			
			column._head.style.width = cw + "px";
			
			this._updateTitleCellBody(column, w);
		}

		// deuxième tour s il y a pas de donnée pour trident et webkit
		if (colToProcess.length) {

			var totalNonPx = clientWidth - total;

			for ( var i = 0; i < colToProcess.length; i++) {
				var column = colToProcess[i];

				column._tempWidth = 0;
			}

			if (totalNonPx > 0 && totalPercent > 0) {
				var totalPercent = totalNonPx / 100;

				// On affecte les %
				for ( var i = 0; i < colToProcess.length; i++) {
					var column = colToProcess[i];

					var percent = column._widthPercent;
					if (!percent) {
						continue;
					}

					var w = Math.floor(percent * totalPercent);

					if (column._maxWidth && w > column._maxWidth) {
						w = column._maxWidth;
					}

					if (totalNonPx < w) {
						w = totalNonPx;
					}

					column._tempWidth += w;

					totalNonPx -= w;
				}

				// On verifie les mins ...
				for ( var i = 0; i < colToProcess.length; i++) {
					var column = colToProcess[i];

					var percent = column._widthPercent;
					if (!percent) {
						continue;
					}

					var minWidth = column._minWidth;
					if (!minWidth) {
						continue;
					}

					var diff = minWidth - column._tempWidth;

					if (diff < 0) {
						continue;
					}

					if (diff > totalNonPx) {
						diff = totalNonPx;
					}

					column._tempWidth += diff;

					totalNonPx -= diff;
				}
			}

			if (totalNonPx > 0 && totalZero > 0) {

				var cnt = totalZero;

				// On affecte les colonnes sans taille !
				for ( var i = 0; i < colToProcess.length; i++) {
					var column = colToProcess[i];

					if (!column._widthComputed) {
						continue;
					}

					var w = Math.floor(totalNonPx / cnt);

					if (column._maxWidth && w > column._maxWidth) {
						w = column._maxWidth;
					}

					if (column._minWidth && w < column._minWidth) {
						w = column._maxWidth;
					}

					if (totalNonPx < w) {
						w = totalNonPx;
					}
					
					var tmpW = w;
					
					if(ie) {
						
						var currentCell = this._rowsPool[1]._cells[column._index];
						
						if (currentCell.currentStyle) {
							w -= f_core.ComputeContentBoxBorderLength(
									currentCell, "left", "right");
							
						}
					}

					column._tempWidth += w;

					totalNonPx -= tmpW;
					cnt--;
				}
			}

			for ( var i = 0; i < colToProcess.length; i++) {
				var column = colToProcess[i];

				var w = column._tempWidth;
				var cw = w;
				if (f_core.IsWebkit(f_core.WEBKIT_SAFARI)) {
					cw -= cellMargin;
				}
				if (cw < 0) {
					cw = 0;
				}
				column._head.style.width = cw + "px";
				this._updateTitleCellBody(column, w);
				column._col.style.width = (cw) + "px";
				column._col.width = cw;

				total += w;
				
			

//				f_core.Debug(f_grid, "Total=" + total + " w=" + w);
			}
		}

		var t2 = new Date().getTime();

		if (scrollBarWidth > 0) {
			var h = this.offsetHeight - this._title.parentNode.offsetHeight - 2;
			if (h < 0) {
				h = 0;
			}
			// body.style.height=h+"px";
		}

		if (ie && !f_core.GetBooleanAttributeNS(this, "sb", true)) { // ns
			// this._title.style.width=total+"px";

			if (!body.style.width) {
				body.style.width = (total + 2) + "px";
				this._title.parentNode.style.width = total + "px";

			} else {
				this._title.parentNode.style.width = (parseInt(
						body.style.width, 10) - 2)
						+ "px";
			}
		} else {
			// body.style.width = total+"px";
			if (this._table) {
				this._table.style.width = total + "px";
			}
		}

		this._title.scrollLeft = this._scrollBody.scrollLeft;

		var t3 = new Date().getTime();

		// document.title="t1="+(t3-t0)+" t2="+(t2-t0)+" t3="+(t1-t0);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateScrollPosition : function() {
		var showValue = this._showValue;
		if (showValue) {
			this.f_showRow(showValue);
			return;
		}

		this.fa_initializeScrollBars();
	},

	/**
	 * @method private
	 * @return void
	 */
	_updateSortManager : function() {
		var sortIndicator = this._sortIndicator;

		var prefix = "";

		if (this.f_isDisabled()) {
			prefix += "_disabled";

		} else {
			if (sortIndicator._selected) {
				prefix += "_selected";
			}
			if (sortIndicator._over) {
				prefix += "_over";
			}
		}

		var className = "f_grid_sortManager";
		if (prefix) {
			className = className + prefix + " " + className;
		}

		if (sortIndicator.className != className) {
			sortIndicator.className = className;
		}
	},

	/**
	 * @method protected
	 * @param Event
	 *            jsEvt
	 * @return void
	 */
	f_showSortManager : function(jsEvt) {
		f_core.Debug(f_grid, "f_showSortManager: Call sort manager !");

		var sortManager = this._sortManager;

		var sortCallback = sortManager;

		if (sortManager.indexOf('(') < 0) {
			var sortManagers = f_grid._SortManagers;
			if (!sortManagers) {
				f_core.Debug(f_grid, "f_showSortManager: No sort manager for '"
						+ sortManager + "'.");
				return;
			}

			sortCallback = sortManagers[sortManager];
		}

		f_core.Debug(f_grid, "f_showSortManager: Call sort manager: "
				+ sortCallback);

		if (typeof (sortCallback) == "string") {
			sortCallback = new window.Function("event", sortCallback);
		}

		var event = new f_event(this, f_event.SORT);
		try {
			sortCallback.call(window, event);

		} finally {
			f_classLoader.Destroy(event);
		}
	},
	/**
	 * Show a row.
	 * 
	 * @method public
	 * @param any
	 *            rowValue Value associated to the row
	 * @return Boolean Return <code>true</code> if the row associated to the
	 *         rowValue is found.
	 */
	f_showRow : function(rowValue) {
		var row = this.f_getRowByValue(rowValue, true);

		f_core
				.Debug(f_grid, "f_showRow: show row '" + rowValue + "' => "
						+ row);

		if (row === null) {
			return false;
		}

		this.fa_showElement(row);

		return true;
	},

	/**
	 * @method public
	 * @return Boolean
	 */
	f_hasElementAdditionalInformation : function(rowValue) {
		if (!this._additionalInformations) {
			return false;
		}

		var row = this.f_getRowByValue(rowValue, true);

		var additional = row._additional;
		if (typeof (additional) == "boolean") {
			return additional;
		}

		return false;
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param Object
	 *            cursor
	 * @return void
	 */
	_keyShowAdditionalInformation : function(evt, cursor) {
		if (this.fa_isAdditionalElementVisible(cursor)) {
			return;
		}

		if (!this.f_hasAdditionalElement(cursor)) {
			return;
		}

		this.fa_performElementAdditionalInformation(cursor, true, evt, true);
	},
	/**
	 * @method private
	 * @param Event
	 *            evt
	 * @param Object
	 *            cursor
	 * @return void
	 */
	_keyHideAdditionalInformation : function(evt, cursor) {
		if (!this.fa_isAdditionalElementVisible(cursor)) {
			return;
		}

		this.fa_performElementAdditionalInformation(cursor, true, evt, false);
	},
	/**
	 * @method protected
	 * @param Object
	 *            row
	 * @return Boolean
	 */
	fa_isAdditionalElementVisible : function(row) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_isAdditionalElementVisible: Invalid element parameter ! ("
						+ row + ")");

		return !!row._additional;
	},
	/**
	 * @method protected
	 * @param Object
	 *            row
	 * @param Boolean
	 *            additional
	 * @param Boolean
	 *            animated
	 * @return void
	 */
	fa_setAdditionalElementVisible : function(row, additional, animated) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.fa_setAdditionalElementVisible: Invalid element parameter ! ("
						+ row + ")");

		if (row._additional == additional) {
			return;
		}

		row._additional = additional;

		if (additional) {
			this.f_showAdditionalContent(row, animated);

		} else {
			this.f_hideAdditionalContent(row, animated);
		}
	},


	/**
	 * @method protected
	 * @param Object
	 *            row
	 * @return Boolean
	 */
	f_hasAdditionalElement : function(row) {
		f_core.Assert(row && row.tagName.toLowerCase() == "tr",
				"f_grid.f_hasAdditionalElement: Invalid element parameter ! ("
						+ row + ")");

		return row._additionalContent !== false;
	},
	/**
	 * @method protected
	 * @param HTMLTableRowElement
	 *            row
	 * @param Boolean
	 *            animated animated
	 * @return void
	 */
	f_showAdditionalContent : function(row, animated) {

		var additionalRow = row._additionalRow;

		if (additionalRow) {
			// L'additional content est déjà construit !

			if (!additionalRow.parentNode || !additionalRow.parentOffset) {
				// Déjà affiché !

				f_core.InsertBefore(row.parentNode, additionalRow,
						row.nextSibling);
			}

			// On sait jamais, apres un tri, ca peut changer !
			additionalRow.className = row._className;

			return;
		}

		var additionalContent = row._additionalContent;
		if (additionalContent === false) {
			return;
		}
		// row._additionalContent=true;

		this._additionalInformationCount++;

		var doc = row.ownerDocument;

		var shadowRows = this._shadowRows;

		var additionalCell = null;

		if (shadowRows && shadowRows.length) {
			additionalRow = shadowRows.shift();
			additionalCell = row.firstChild;

			while (additionalCell.hasChildNodes()) {
				additionalCell.removeChild(additionalCell.lastChild);
			}

			f_core.Assert(additionalRow.tagName.toLowerCase() == "tr",
					"f_grid.f_addRow2: Invalid row ! " + additionalRow);

			additionalRow.parentNode.removeChild(additionalRow);

		} else {
			additionalRow = doc.createElement("tr");
		}

		f_core.InsertBefore(this._tbody, additionalRow, row.nextSibling);

		additionalRow.setAttribute("role", "description");
		additionalRow.setAttribute("aria-live", "polite");
		additionalRow._additionalBody = true;
		additionalRow._row = row;
		additionalRow._parentNode = row;
		additionalRow.className = row._className + " f_grid_additionalRow";
		row._additionalRow = additionalRow;

		if (row._additionalHeight) {
			additionalRow.style.height = parseInt(row._additionalHeight, 10)
					+ "px";
			additionalRow.style.verticalAlign = "top";
		}

		if (!additionalCell) {
			additionalCell = doc.createElement("td");
			f_core.AppendChild(additionalRow, additionalCell);
		}

		additionalCell.className = "f_grid_additional_body";
		additionalCell.colSpan = this._visibleColumnsCount;

		if (additionalContent) {
			this.f_getClass().f_getClassLoader().f_loadContent(this,
					additionalCell, additionalContent);
			return;
		}

		if (additionalContent !== undefined) {
			// ???
			alert("Unknown type '" + additionalContent + "'");
			return;
		}

		if (additionalRow._waiting) {
			// En cours d'ouverture ...
			return;
		}

		// On récupère en AJAX ....

		var request = new f_httpRequest(this, f_httpRequest.TEXT_HTML_MIME_TYPE);
		var self = this;

		request
				.f_setListener({
					onInit : function(request) {
						var waiting = additionalRow._waiting;
						if (!waiting) {
							waiting = f_waiting.Create(
									additionalRow.firstChild, null, true,
									undefined, f_waiting.LEFT, 20);
							additionalRow._waiting = waiting;
						}

						waiting.f_setText(f_waiting.GetLoadingMessage());
						waiting.f_show();
					},
					/**
					 * @method public
					 */
					onError : function(request, status, text) {

						var waiting = additionalRow._waiting;
						if (waiting) {
							additionalRow._waiting = undefined;

							waiting.f_hide();
							waiting.f_close();

							f_classLoader.Destroy(waiting);
						}

						f_core.Info(f_grid,
								"f_showAdditionalContent.onError: Bad status: "
										+ status);

						try {
							additionalRow._additionalContent = false;

							self.f_hideAdditionalContent(row, animated);

						} catch (x) {
							self.f_performErrorEvent(x,
									f_error.RESPONSE_EVALUATION_SERVICE_ERROR,
									"Evaluation exception");
						}

						self.f_performErrorEvent(request, f_error.HTTP_ERROR,
								text);
					},
					/**
					 * @method public
					 */
					onProgress : function(request, content, length, contentType) {
						var waiting = additionalRow._waiting;
						if (waiting) {
							waiting.f_setText(f_waiting.GetReceivingMessage());
						}
					},
					/**
					 * @method public
					 */
					onLoad : function(request, content, contentType) {

						var waiting = additionalRow._waiting;
						if (waiting) {
							additionalRow._waiting = undefined;

							waiting.f_hide();
							waiting.f_close();

							f_classLoader.Destroy(waiting);
						}

						var ret = undefined;

						if (request.f_getStatus() != f_httpRequest.OK_STATUS) {
							self.f_performErrorEvent(request,
									f_error.INVALID_RESPONSE_SERVICE_ERROR,
									"Bad http response status ! ("
											+ request.f_getStatusText() + ")");
							ret = false;

						} else {
							var responseContentType = request
									.f_getResponseContentType().toLowerCase();
							if (responseContentType
									.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE) >= 0) {
								var code = f_error
										.ComputeApplicationErrorCode(request);

								self
										.f_performErrorEvent(request, code,
												content);
								ret = false;

							} else if (responseContentType
									.indexOf(f_httpRequest.TEXT_HTML_MIME_TYPE) < 0) {
								self.f_performErrorEvent(request,
										f_error.RESPONSE_TYPE_SERVICE_ERROR,
										"Unsupported content type: "
												+ responseContentType);

								ret = false;
							}
						}

						if (ret === undefined) {
							ret = request.f_getResponse();
						}

						try {
							additionalRow._additionalContent = ret;

							if (ret === false) {
								self.f_hideAdditionalContent(row, animated);

							} else {

								self.f_getClass().f_getClassLoader()
										.f_loadContent(self, additionalCell,
												ret);

								self.f_addSerializedIndexes(
										additionalRow._row._rowIndex, 1);
							}

						} catch (x) {
							self.f_performErrorEvent(x,
									f_error.RESPONSE_EVALUATION_SERVICE_ERROR,
									"Evaluation exception");
						}
					}
				});

		// alert("Params="+params);
		var params = {
				gridId : this.id,
				rowValue : row._index,
				rowIndex : row._rowIndex,
				serializedIndexes : [row._rowIndex, 1]
			};
		
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!params) {
				params={};
			}
			
			params["javax.faces.behavior.event"]= "grid.additionalInformation";
			params["javax.faces.source"]= this.id;
			params["javax.faces.partial.execute"]= this.id;
		} else {
			request.f_setRequestHeader("X-Camelia", "grid.additionalInformation");
		}

		

		if (this._paged) {
			// On synchronise 1 SEULE ligne, on envoi pas les indexes !

			var serializedState = this.f_getClass().f_getClassLoader()
					.f_getSerializedState();
			f_core.Debug(f_dataGrid, "f_callServer: serializedState="
					+ serializedState);
			if (!serializedState) {
				serializedState = ""; // Il faut informer le service que nous
				// sommes en mode paginé !
			}

			params[f_core.SERIALIZED_DATA] = serializedState;

			f_classLoader.SerializeInputsIntoParam(params, this, false);
		}

		request.f_doFormRequest(params);
	},
	
	/**
	 * @method protected
	 * @param any
	 *            column Column object or index
	 * @return void
	 */
	f_setFocusColumn : function(column) {
		if (typeof (column) == "object") {
			f_core.SetFocus(column._label);
		}
	},
	/**
	 * @method protected
	 * @param HTMLTableRowElement
	 *            row
	 * @param Boolean
	 *            animated
	 * @return void
	 */
	f_hideAdditionalContent : function(row, animated) {

		var additionalRow = row._additionalRow;

		if (!additionalRow) {
			return;
		}

		if (true) {
			additionalRow.className = "f_grid_hiddenAV";
			return;
		}

		row._additionalRow = undefined;

		if (row._additionalContent !== false) {
			// On l'efface !

			additionalRow._parentNode = undefined; // HTMLRowElement
			additionalRow._row = undefined; // HtmlRowElement
			additionalRow._dataGrid = undefined; // f_grid

			var waiting = additionalRow._waiting;
			if (waiting) {
				additionalRow._waiting = undefined; // f_waiting

				f_classLoader.Destroy(waiting);
			}

			if (additionalRow.hasChildNodes()) {

				this.f_getClass().f_getClassLoader().f_garbageObjects(
						undefined, additionalRow);

				while (additionalRow.hasChildNodes()) {
					additionalRow.removeChild(additionalRow.lastChild);
				}

				this.f_getClass().f_getClassLoader().f_completeGarbageObjects();
			}
		}

		row.parentNode.removeChild(additionalRow);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateSortBreadCrumbs : function() {
		var currentSorts = this._currentSorts;
		if (!currentSorts) {
			return;
		}

		var ids = new Array;
		var indexes = new Array;
		var texts = new Array;

		var exp = /\|/g;

		for ( var i = 0; i < currentSorts.length; i++) {
			var col = currentSorts[i];

			var index = col._index + (col._ascendingOrder ? "+" : "-");
			indexes.unshift(index);

			var id = col.id;
			if (!id) {
				id = "-";
			}
			ids.unshift(id.replace(exp, " "));

			var text = col._text;
			if (text === undefined) {
				text = this.f_getColumnName(col);
			}
			texts.unshift(text.replace(exp, " "));
		}

		f_core.SetAttributeNS(this, "sortBreadCrumbsIds", ids.join("|"));
		f_core
				.SetAttributeNS(this, "sortBreadCrumbsIndexes", indexes
				.join("|"));
		f_core.SetAttributeNS(this, "sortBreadCrumbsTexts", texts.join("|"));
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_showEmptyDataMessage : function() {
		var label = this._emptyDataMessageLabel;
		if (!label) {
			return;
		}

		var scrollBody = this._scrollBody;
		if (!scrollBody.offsetHeight) { // Le tableau s'est tassé, on affiche
			// pas le message !
			return;
		}

		var parent = f_core.GetParentNode(label); // Ben si "parent" est
		// utilisé ???

		this._emptyDataMessageShown = true;

		label.style.width = parent.offsetWidth + "px";

		var top = parent.scrollTop + Math.floor(parent.offsetHeight / 2);

		label.style.top = top + "px";

		label.style.display = "block";

		if (label.offsetHeight > 0) {
			top -= Math.floor(label.offsetHeight / 2);
			label.style.top = top + "px";
		}
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_hideEmptyDataMessage : function() {
		if (!this._emptyDataMessageShown) {
			return;
		}
		this._emptyDataMessageShown = undefined;

		var label = this._emptyDataMessageLabel;
		if (!label) {
			return;
		}

		label.style.display = "none";
	},
	/**
	 * @method public
	 * @param String
	 *            message
	 * @return void
	 */
	f_setEmptyDataMessage : function(message) {
		var label = this._emptyDataMessageLabel;
		if (!label) {
			return;
		}

		f_core.SetTextNode(label, message);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getEmptyDataMessage : function() {
		var label = this._emptyDataMessageLabel;
		if (!label) {
			return null;
		}

		return f_core.GetTextNode(label, true);
	},

	/**
	 * @method public
	 * @param f_event
	 *            event
	 * @return String Identifier of column or <code>null</code> if not found.
	 */
	f_computeEventColumnId : function(event) {
		f_core.Assert(event instanceof f_event,
				"f_grid.f_getEventColumnIndex: Invalid event parameter '"
						+ event + "'.");

		var jsEvent = event.f_getJsEvent();
		var target = jsEvent.target ? jsEvent.target : jsEvent.srcElement;
		return this.f_computeColumnIdByElement(target);
	},

	/**
	 * @method public
	 * @param HTMLElement
	 *            target
	 * @return String Identifier of column or <code>null</code> if not found.
	 */
	f_computeColumnIdByElement : function(target) {
		var column = this._searchColumnByElement(target);
		if (!column) {
			return column;
		}

		return column._id;
	},
	/**
	 * @method public
	 * @param HTMLElement
	 *            target
	 * @param optional Object cellRef  Object.value will contain the cell which catches the event
	 * @return String Identifier of column or <code>null</code> if not found.
	 */
	_searchColumnByElement : function(target, cellRef) {

		f_core.Assert(target && target.nodeType == f_core.ELEMENT_NODE,
				"f_grid.f_computeColumnIdByElement: Invalid target parameter '"
						+ target + "'.");

		var lastCell = undefined;

		for (; target; target = target.parentNode) {
			if (target == this || target == this._scrollBody) {
				// On tombe sur le BODY ... on laisse tomber
				return null;
			}

			switch (target.tagName.toUpperCase()) {
			case "TD":
			case "TH":
				lastCell = target;
				break;

			case "TR":
				if (target._dataGrid != this) {
					break;
				}

				if (!lastCell) {
					return undefined;
				}

				var tds = target.childNodes;
				var index = 0;
				for ( var i = 0; i < tds.length; i++) {
					var td = tds[i];
					if (td.nodeType != f_core.ELEMENT_NODE) {
						continue;
					}
					var tagName = td.tagName.toUpperCase();
					if (tagName != "TD" && tagName != "TH") {
						continue;
					}

					if (td != lastCell) {
						index++;
						continue;
					}
					
					if (cellRef) {
						cellRef.value=td;
					}

					var columns = this._columns;
					for ( var i = 0; i < columns.length; i++) {
						var cl = columns[i];

						if (!cl._visibility) {
							continue;
						}

						if (!index) {
							return cl;
						}

						index--;
					}

					break;
				}

				break;
			}
		}

		return null;
	},
	/**
	 * 
	 * @method public
	 * @param any[]
	 *            rowValues List of values whose specified rows.
	 * @return Number Number of removed rows.
	 */
	f_clearArray : function(rowValues) {
		f_core.Assert(rowValues instanceof Array,
				"f_grid.f_clearArray: Invalid values parameter '" + values
						+ "'.");

		return this.f_clear.apply(this, rowValues);
	},
	/**
	 * 
	 * @method public
	 * @return Number Number of removed rows.
	 */
	f_clearAll : function() {
		var visibleElements = this.fa_listVisibleElements();
		if (!visibleElements.length) {
			return 0;
		}

		return this.f_clear.apply(this, visibleElements);
	},
	/**
	 * @method public
	 * @return void
	 */
	f_expandAllAdditionalInformations : function() {
		var elements = this.fa_listVisibleElements();
		var values = new Array();

		for ( var i = 0; i < elements.length; i++) {
			var value = this.f_getRowValue(elements[i]);
			if (value === undefined) {
				continue;
			}

			values.push(value);
		}

		if (!values.length) {
			return;
		}

		this.f_expandAdditionalInformations(values);
	},
	/**
	 * @method public
	 * @return void
	 */
	f_collapseAllAdditionalInformations : function() {
		this.f_expandAdditionalInformations();
	},
	/**
	 * 
	 * @method public abstract
	 * @param any...
	 *            rowValue1 The value of the row to remove
	 * @return Number Number of removed rows.
	 */
	f_clear : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 */
	f_callServer : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 */
	f_sortClientSide : f_class.ABSTRACT,

	/**
	 * Set the width of the component
	 * 
	 * @override
	 * @method public
	 * @param Number
	 *            width Width of the component.
	 * @return void
	 */
	f_updateWidth : function(width) {
		this.f_super(arguments, width);

		this.f_updateTitle();
	},
	/**
	 * 
	 * Set the height of the component.
	 * 
	 * @override
	 * @method public
	 * @param Number
	 *            height Height of the component.
	 * @return void
	 */
	f_updateHeight : function(height) {
		f_core.Assert(typeof (height) == "number",
				"f_component.f_setHeight: h parameter must be a number ! ("
						+ height + ")");

		var oldHeight = this.offsetHeight;
		var difference = oldHeight - height;
		this.style.height = height + "px";

		var scrollBody = this.ownerDocument.getElementById(this.id
				+ f_grid._DATA_BODY_SCROLL_ID_SUFFIX);
		if (scrollBody) {
			var oldbody = scrollBody.offsetHeight;
			height = oldbody - difference;
			scrollBody.style.height = height + "px";
		}
	},

	/**
	 * select all rows in the current page
	 * 
	 * @method public
	 * @return void
	 */
	f_selectAllPage : function() {
		if (!this.f_isSelectable()) {
			return;
		}
		var first = this.f_getFirst();
		var last = 1;
		var rowCount = this.f_getRowCount();
		var rows = -1;
		if (this._rows) {
			rows = this._rows;
		}
		if (rowCount > 0 && ((rows > 0 && rowCount < rows) || rows < 0)) {
			last = rowCount;
		} else if (rows > 0) {
			last = rows;
		}

		var end = first + last - 1;
		if (end > rowCount) {
			end = rowCount - 1; // evite de selectioner des lignes en trop
		}
		this._selectRange(this.f_getRow(first), this.f_getRow(end),
				fa_selectionManager.RANGE_SELECTION);
	},

	/**
	 * unSelect all rows in the current page
	 * 
	 * @method public
	 * @return void
	 */
	f_unselectAll : function() {
		if (this.f_isSelectable()) {
			this.f_setSelection([]);
		}
	},
	/**
	 * 
	 * @method private
	 * @param Event
	 *            jsEvent
	 * @return Boolean
	 */
	_dragRow : function(jsEvent) {
		var dnd = this._dragAndDropEngine;
		if (!dnd) {
			return false;
		}

		var selection = new Object;
		selection._items = new Array;
		selection._itemsValue = new Array;
		var itemsDragTypes = new Array;
		var currentSelection = this._currentSelection;
		var lastEffects = undefined;

		for ( var i = 0; i < currentSelection.length; i++) {

			var row = currentSelection[i];

			var dragTypes = row._dragTypes;
			if (dragTypes === undefined) {
				dragTypes = this._dragTypes;
			}

			var dragEffects = row._dragEffects;
			if (dragEffects === undefined) {
				dragEffects = this._dragEffects;
			}
			f_core.Debug(f_grid, "_dragRow: dragEffects=0x" + dragEffects
					+ " dragTypes='" + dragTypes + "'");

			if (!dragEffects || !dragTypes) {
				return false;
			}

			if (lastEffects) {
				lastEffects = dragEffects & lastEffects;
			} else {
				lastEffects = dragEffects;
			}

			if (itemsDragTypes.length) {
				itemsDragTypes = f_dragAndDropEngine.ComputeTypes(dragTypes,
						itemsDragTypes);
			} else {
				itemsDragTypes = dragTypes;
			}

			selection._items[i] = row;
			selection._itemsValue[i] = this.fa_getElementValue(row);
		}

		if (!lastEffects) {
			return false;
		}
		if (!itemsDragTypes.length) {
			return false;
		}

		selection._dragEffects = lastEffects;
		selection._dragTypes = itemsDragTypes;
		selection._itemsElement = currentSelection;
		var ret = dnd.f_start(jsEvent, selection);

		f_core.Debug(f_grid, "_dragRow: start returns '" + ret + "'");

		return ret;
	},

	/**
	 * @method public
	 * @return Array
	 */
	f_getDragItems : function(selection) {
		return selection._items;
	},

	/**
	 * @method public
	 * @return Array
	 */
	f_getDragItemsValue : function(selection) {
		return selection._itemsValue;
	},

	/**
	 * @method public
	 * @return Array
	 */
	f_getDragItemsElement : function(selection) {
		return selection._itemsElement;
	},

	/**
	 * @method public
	 * @return Array
	 */
	f_getDragTypes : function(selection) {
		return selection._dragTypes;
	},

	/**
	 * @method public
	 * @return Number
	 */
	f_getDragEffects : function(selection) {
		return selection._dragEffects;
	},

	f_queryDropInfos : function(dragAndDropEngine, jsEvent, element) {
		this._targetDragAndDropEngine = dragAndDropEngine;

		if (this._scrollBody) {
			this.fa_installAutoScroll();
		}

		var found = this._findRowByHTMLElement(element);
		if (!found) {
			return null;
		}

		var row = found._row;
		var rowElement = found._rowElement;

		if (this._bodyDroppable !== true && row == this) {
			return null;
		}

		var dropTypes = row._dropTypes;
		if (dropTypes === undefined) {
			dropTypes = this._dropTypes;
		}

		var dropEffects = row._dropEffects;
		if (dropEffects === undefined) {
			dropEffects = this._dropEffects;
		}

		if (!dropTypes || !dropEffects) {
			return null;
		}

		var rowValue = this.fa_getElementValue(row);

		return {
			item : row,
			itemValue : rowValue,
			targetItemElement : rowElement,
			dropTypes : dropTypes,
			dropEffects : dropEffects
		};
	},
	f_overDropInfos : function(dragAndDropEngine, infos) {
		var row = infos.item;

		row._dndOver = true;
		this.fa_updateElementStyle(row);
	},
	f_outDropInfos : function(dragAndDropEngine, infos) {
		var row = infos.item;

		row._dndOver = false;
		this.fa_updateElementStyle(row);
	},
	f_releaseDropInfos : function() {
		this._targetDragAndDropEngine = undefined;

		this.fa_uninstallAutoScroll();
	},
	fa_getLastMousePosition : function() {
		return this._targetDragAndDropEngine.fa_getLastMousePosition();
	},

	fa_getScrollableContainer : function() {
		return this._scrollBody;
	},
	fa_findAutoOpenElement : function(htmlElement) {
		// Recherche un additional !

		if (!this._additionalInformations) {
			return null;
		}

		var found = this._findRowByHTMLElement(htmlElement);
		if (!found) {
			return null;
		}

		var row = found._row;
		if (row == this || !this.f_hasAdditionalElement(row)
				|| this.fa_isAdditionalElementVisible(row)) {
			return null;
		}

		return row;
	},
	fa_performAutoOpenElement : function(row) {
		this.fa_performElementAdditionalInformation(row, true);
	},
	fa_isSameAutoOpenElement : function(elt1, elt2) {
		return elt1._rowIndex == elt2._rowIndex;
	},
	/**
	 * @method private
	 * @param HTMLElement
	 *            element
	 * @return Object
	 */
	_findRowByHTMLElement : function(element) {
		var row = null;
		var rowElement = null;

		for (; element; element = element.parentNode) {
			if (element._rows) {
				// Racine de l'arbre
				row = this;
				rowElement = this;
				break;
			}

			if (element._dataGrid == this && element._rowIndex !== undefined) {
				row = element;
				rowElement = element;
				break;
			}
		}

		if (!row) {
			return null;
		}

		return {
			_row : row,
			_rowElement : rowElement
		};
	},

	fa_autoScrollPerformed : function() {
		if (this._targetDragAndDropEngine) {
			this._targetDragAndDropEngine.f_updateMousePosition();
		}
	},
	/**
	 * @method hidden
	 * @param Number
	 *            newFirst
	 * @return void
	 */
	_changeFirst : function(newFirst) {
		this._first = newFirst;
		this.f_setProperty(f_prop.FIRST, newFirst);
	},
	/**
	 * @method protected
	 * @param Object
	 *            details
	 * @return Object
	 */
	_fillColumnDetails : function(details, column, cell) {

		return details;
	},

	/**
	 * @method protected
	 * @param Object
	 *            tooltip
	 * @param Boolean
	 *            sow
	 * @param Event
	 *            jsEvent
	 * @return void
	 */
	fa_setToolTipVisible : function(tooltip, show, jsEvent) {

		if (show) {
			var item = tooltip.f_getElementItem();

			if (!tooltip.f_isContentSpecified()) {
				var tagName = item.tagName.toUpperCase();

				if ((tagName == "TD" || tagName == "TH")
						&& item.parentNode._index !== undefined) {
					this.f_showToolTip(tooltip, jsEvent,
							f_toolTip.BOTTOM_COMPONENT);
					return;
				}
				if (tagName == "TR" && item._index != undefined) {
					this.f_showToolTip(tooltip, jsEvent,
							f_toolTip.BOTTOM_COMPONENT);
					return;
				}
			}

			tooltip.f_show(tooltip.f_getStateId(), jsEvent,
					f_toolTip.BOTTOM_COMPONENT);

		} else {
			tooltip.f_hide(tooltip.f_getStateId());
		}
	},

	/**
	 * @method protected
	 * @param Element
	 *            elementItem
	 * @param String tooltipId
	 * @return Object
	 */
	_computeTooltipRowContext: function(elementItem, tooltipId) {
		var row=null;
		switch (elementItem.tagName.toUpperCase()) {
		case "TR":
			row = elementItem;
			if (!tooltipId) {
				tooltipId = "#row";
			}
			break;
	
		case "TD":
		case "TH":
			row = elementItem.parentNode;
			if (!tooltipId) {
				tooltipId = "#cell";
			}
			break;
		}
		
		return {
			_row: row,
			_rowValue: row._index,
			_rowIndex: row._rowIndex,
			_tooltipId: tooltipId
		};
	}
};

new f_class("f_grid", {
	extend : f_component,
	aspects : [ fa_disabled, fa_pagedComponent, fa_subMenu, fa_commands,
			fa_selectionManager, fa_scrollPositions, fa_immediate,
			fa_additionalInformationManager, fa_droppable, fa_draggable,
			fa_autoScroll, fa_autoOpen, fa_aria, fa_gridToolTipContainer,
			fa_tabIndex ],
	statics : __statics,
	members : __members
});