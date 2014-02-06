/*
 * $Id: f_pager.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * @class public f_pager extends f_component, fa_pager, fa_tabIndex
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {

	/**
	 * @method protected static
	 */
	_AddSpan : function(container, spanClass, text, classBase) {
		if (!classBase) {
			classBase = "f_pager_value";
		}
		return f_core.CreateElement(container, "span", {
			className : classBase + " " + classBase + "_" + spanClass,
			textNode : text
		});
	},

	/**
	 * @method protected static
	 */
	_AddText : function(container, text) {
		f_core.AppendChild(container, container.ownerDocument
				.createTextNode(text));
	},

	/**
	 * @method private static
	 */
	_AddButton : function(dataPager, container, buttonClass, text, tooltip,
			index, parameters, classBase) {
		if (parameters) {
			if (parameters["label"]) {
				text = parameters["label"];
			}
			if (parameters["tooltip"]) {
				tooltip = parameters["tooltip"];
			}
		}

		var button;

		var doc = dataPager.ownerDocument;

		var suffix = "";
		if (index === undefined || index < 0) {
			button = doc.createElement("span");
			suffix += "_disabled";

		} else {
			button = doc.createElement("a");
			button._index = index;
			button.href = f_core.CreateJavaScriptVoid0();
			button.onclick = f_pager._PositionSelect;
			button.onkeydown = f_pager._PositionKey;
			button._pager = dataPager;
			button.tabIndex = -1;
		}

		if (!classBase) {
			classBase = "f_pager_button";
		}
		var cls = classBase + " " + classBase + "_" + buttonClass;

		if (suffix) {
			cls += " " + classBase + suffix + " " + classBase + "_"
					+ buttonClass + suffix;
		}

		if (button.className != cls) {
			button.className = cls;
		}
		if (tooltip) {
			button.title = tooltip;
		}

		f_core.AppendChild(button, doc.createTextNode(text));

		f_core.AppendChild(container, button);

		var buttons = dataPager._buttons;
		if (!buttons) {
			buttons = new Array;
			dataPager._buttons = buttons;
		}
		button.id = dataPager.id + "::" + buttons.length;
		buttons.push(button);

		return button;
	},

	/**
	 * @method private static
	 */
	_SearchButtons : function(list, parent) {
		var children = parent.childNodes;
		if (!children || !children.length) {
			return;
		}
		for ( var i = 0; i < children.length; i++) {
			var child = children[i];

			if (!child.tagName) {
				continue;
			}
			var index = f_core.GetAttributeNS(child, "index");
			if (!index) {
				index = child._index;
			}
			if (index) {
				list.push(child);
				continue;
			}

			f_pager._SearchButtons(list, child);
		}

		return list;
	},

	/**
	 * @method private static
	 * @context object:dataPager
	 */
	_PositionKey : function(evt) {
		var dataPager = this._pager;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataPager.f_getEventLocked(evt)) {
			return false;
		}

		var code = evt.keyCode;
		var cancel = false;

		switch (code) {
		case f_key.VK_RIGHT:
			var buttons = dataPager._buttons;
			for ( var i = 0; i < buttons.length; i++) {
				var button = buttons[i];

				if (button != this) {
					continue;
				}

				button.tabIndex = -1;
				this._focusedButton=undefined;

				for (var j = 0; j < buttons.length; j++) {
					i = (i + 1) % buttons.length;

					var but = buttons[i];
					if (!but._pager) {
						continue;
					}

					but.tabIndex = dataPager.fa_getTabIndex();
					this._focusedButton=but;

					f_core.SetFocus(but);
					break;
				}
				break;
			}

			cancel = true;
			break;

		case f_key.VK_LEFT:
			var buttons = dataPager._buttons;
			for ( var i = 0; i < buttons.length; i++) {
				var button = buttons[i];

				if (button != this) {
					continue;
				}

				button.tabIndex = -1;
				this._focusedButton=undefined;

				for ( var j = 0; j < buttons.length; j++) {
					i = (i - 1 + buttons.length) % buttons.length;

					var but = buttons[i];

					if (!but._pager) {
						continue;
					}

					but.tabIndex = dataPager.fa_getTabIndex();
					this._focusedButton=but;

					f_core.SetFocus(but);
					break;
				}
				break;
			}

			cancel = true;
			break;
		}

		if (cancel) {
			return f_core.CancelJsEvent(evt);
		}
		return true;
	},

	/**
	 * @method private static
	 * @context object:dataPager
	 */
	_PositionSelect : function(evt) {
		var dataPager = this._pager;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (dataPager.f_getEventLocked(evt)) {
			return false;
		}

		var v_index = f_core.GetAttributeNS(this, "index");
		if (typeof (v_index) != "number") {
			v_index = this._index;

			if (typeof (v_index) != "number") {
				return false;
			}
		}

		dataPager.f_changePosition(v_index);

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method hidden static
	 */
	Create : function(parent, refComponent, forId, styleClass) {

		var properties = {
			id : refComponent.id + ":pager",
			className : "f_pager"
		};
		properties[f_core._VNS + ":for"] = forId;

		f_dataGridPopup.CopyProperties(properties, refComponent, f_core._VNS
				+ ":message", f_core._VNS + ":zeroResultMessage", f_core._VNS
				+ ":oneResultMessage", f_core._VNS + ":manyResultMessage",
				f_core._VNS + ":manyResultMessage2");

		if (styleClass) {
			properties["className"] += " " + styleClass;
		}

		var pager = f_core.CreateElement(parent, "div", properties);

		f_class.Init(pager, f_pager, [ parent ]);

		pager.f_completeComponent();

		return pager;
	}
};

var __members = {

	f_pager : function() {
		this.f_super(arguments);

		this._for = f_core.GetAttributeNS(this, "for");

		this._readAttributes();

		/*
		 * f_core.Debug(f_pager, "Message='"+this._message+"'");
		 * f_core.Debug(f_pager, "ZeroMessage='"+this._zeroMessage+"'");
		 * f_core.Debug(f_pager, "OneMessage='"+this._oneMessage+"'");
		 * f_core.Debug(f_pager, "ManyMessage='"+this._manyMessage+"'");
		 * f_core.Debug(f_pager, "NoPagedMessage='"+this._noPagedMessage+"'");
		 * f_core.Debug(f_pager, "ManyMessage2='"+this._manyMessage2+"'");
		 */
		if (this._for) {
			fa_pagedComponent.RegisterPager(this._for, this);

		} else {
			f_core.Error(f_pager, "f_pager: 'for' attribute is not defined !");
		}
	},
	f_finalize : function() {

		fa_pagedComponent.UnregisterPager(this);

		this._destroyButtons();

		this._focusedButton = undefined; // HTMLElement
		this._pagedComponent = undefined; // f_pagedComponent
		// this._for=undefined; // string
		// this._message=undefined; // string
		// this._zeroMessage=undefined; // string
		// this._oneMessage=undefined; // string
		// this._manyMessage=undefined; // string
		// this._manyMessage2=undefined; // string
		// this._noPagedMessage=undefined; // string

		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	_readAttributes : function() {

		var zeroMessage;
		var oneMessage;
		var manyMessage;
		var manyMessage2;

		var message = f_core.GetAttributeNS(this, "message");
		if (message) {
			zeroMessage = f_core.GetAttributeNS(this, "zeroResultMessage");
			oneMessage = f_core.GetAttributeNS(this, "oneResultMessage");
			manyMessage = f_core.GetAttributeNS(this, "manyResultMessage");
			manyMessage2 = f_core.GetAttributeNS(this, "manyResultMessage2");

		} else {
			var resourceBundle = f_resourceBundle.Get(f_pager);

			message = resourceBundle.f_get("MESSAGE");
			zeroMessage = resourceBundle.f_get("ZERO_RESULT_MESSAGE");
			oneMessage = resourceBundle.f_get("ONE_RESULT_MESSAGE");
			manyMessage = resourceBundle.f_get("MANY_RESULTS_MESSAGE");
			manyMessage2 = resourceBundle.f_get("MANY_RESULTS_MESSAGE2");
		}

		this._message = message;
		this._zeroMessage = (zeroMessage !== undefined) ? zeroMessage : message;
		this._oneMessage = (oneMessage !== undefined) ? oneMessage : message;
		this._manyMessage = (manyMessage !== undefined) ? manyMessage : message;
		this._manyMessage2 = (manyMessage2 !== undefined) ? manyMessage2
				: this._manyMessage;

		var noPagedMessage = f_core
				.GetAttributeNS(this, "noPagedMessage", null);
		if (noPagedMessage === null) {
			var resourceBundle = f_resourceBundle.Get(f_pager);

			noPagedMessage = resourceBundle.f_get("NO_PAGED_MESSAGE");
		}
		this._noPagedMessage = noPagedMessage;
	},
	_destroyButtons : function() {
		var buttons = this._buttons;
		if (!buttons) {
			return;
		}

		this._buttons = undefined;

		for ( var i = 0; i < buttons.length; i++) {
			var button = buttons[i];

			button.onclick = null;
			button.onkeydown = null;
			button._pager = undefined;
			// button._index=undefined; // number

			f_core.VerifyProperties(button);
		}
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement : function() {
		if (this._focusedButton) {
			return this._focusedButton;
		}

		var buttons = this._buttons;
		if (!buttons) {			
			return null;
		}

		for ( var i = 0; i < buttons.length; i++) {
			var button = buttons[i];

			if (button.tagName.toLowerCase() == "a") {
				this._focusedButton=button;
				
				return button;
			}
		}

		return null;
	},

	/* ****************************************************************** */
	fa_pagedComponentInitialized : function(dataComponent) {
		this._pagedComponent = dataComponent;

		var oldVisibility = this.style.visibility;
		if (!oldVisibility) {
			oldVisibility = "inherit";
		}

		var fragment = undefined;
		var component = this;
		try {
			this.style.visibility = "hidden";

			var children = this.childNodes;
			if (children) {
				this._destroyButtons();

				while (this.hasChildNodes()) {
					this.removeChild(this.lastChild);
				}
			}

			fragment = component.ownerDocument.createDocumentFragment();

			this._computeMessage(fragment);

		} finally {

			if (fragment) {
				f_core.AppendChild(component, fragment);
			}

			if (oldVisibility) {
				this.style.visibility = oldVisibility;
			}
		}
	},
	/**
	 * @method protected
	 */
	_computeMessage : function(fragment) {
		var dataGrid = this._pagedComponent;

		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();
		var first = dataGrid.f_getFirst();
		var maxRows = dataGrid.f_getMaxRows();
		var paged = dataGrid.f_isPaged();

		var message = undefined;
		if (this._noPagedMessage && (rows < 1 || !paged)) {
			message = this._noPagedMessage;

		} else {
			if (rowCount < 0) {
				if (first + rows < maxRows) {
					message = this._manyMessage2;

				} else {
					message = this._manyMessage;
				}

			} else if (rowCount == 0) {
				message = this._zeroMessage;

			} else if (rowCount == 1) {
				message = this._oneMessage;
			}
		}

		if (message == undefined) {
			message = this._message;
		}

		f_core.Debug(f_pager, "fa_pagedComponentInitialized: Format message '"
				+ message + "' rows=" + rows + " rowCount=" + rowCount
				+ " first=" + first + " maxRows=" + maxRows);

		this._focusedButton = undefined;
		this._destroyButtons();

		this._formatMessage(fragment, message);

		var focusableElement = this.f_getFocusableElement();
		if (focusableElement) {
			focusableElement.tabIndex = this.fa_getTabIndex();
		}
	},
	/**
	 * @method protected
	 * @param DocumentFragment fragment
	 * @param String message
	 * @param Object target
	 * @return void
	 */
	_formatMessage : function(fragment, message, target) {

		var span = null;
		for ( var i = 0; i < message.length;) {
			var c = message.charAt(i++);
			if (c == "{") {
				var end = message.indexOf("}", i);
				var varName = message.substring(i, end).toLowerCase();
				i = end + 1;

				if (span && span.length) {
					this._appendSpan(fragment, span.join(""));
					span = null;
				}

				var parameters = undefined;
				var pvar = varName.indexOf(':');
				if (pvar >= 0) {
					var parameter = varName.substring(pvar + 1);
					varName = varName.substring(0, pvar);

					parameters = new Object();

					var ss = parameter.split(';');
					for ( var j = 0; j < ss.length; j++) {
						var s = ss[j];
						var p = "";
						var ep = s.indexOf('=');
						if (ep >= 0) {
							p = s.substring(ep + 1);
							s = s.substring(0, ep);
						}

						parameters[s] = p;
					}
				}

				this._processToken(fragment, varName, parameters, target);

				continue;
			}

			if (c == "\'") {
				if (!span) {
					span = new Array;
				}
				for ( var j = i;;) {
					var end = message.indexOf("'", j);
					if (end < 0) {
						span.push(message.substring(j));
						i = message.length;
						break;
					}

					if (message.charAt(end + 1) == "\'") {
						span.push(message.substring(j, end), "'");
						j = end + 2;
						continue;
					}

					span.push(message.substring(j, end));
					i = end + 1;
					break;
				}
				continue;
			}

			if (!span) {
				span = new Array;
			}
			span.push(c);
		}

		if (span && span.length) {
			this._appendSpan(fragment, span.join(""));
		}
	},
	/**
	 * @method protected
	 */
	_processToken : function(fragment, varName, parameters) {
		switch (varName) {
		case "first":
		case "position":
			this.f_appendFirstValue(fragment, "first", parameters);
			break;

		case "pageposition":
			this
					.f_appendPagePositionValue(fragment, "pagePosition",
							parameters);
			break;

		case "last":
			this.f_appendLastValue(fragment, "last", parameters);
			break;

		case "rowcount":
			this.f_appendRowCountValue(fragment, "rowCount", parameters);
			break;

		case "pagecount":
			this.f_appendPageCountValue(fragment, "pageCount", parameters);
			break;

		case "bfirst":
			this.f_appendFirstButton(fragment, "first", parameters);
			break;

		case "bprev":
			this.f_appendPrevButton(fragment, "prev", parameters);
			break;

		case "bnext":
			this.f_appendNextButton(fragment, "next", parameters);
			break;

		case "blast":
			this.f_appendLastButton(fragment, "last", parameters);
			break;

		case "bpages":
			this.f_appendPagesButtons(fragment, "goto", parameters);
			break;

		default:
			f_core.Error(f_pager, "Unknown pager message button '" + varName
					+ "'.");
		}
	},
	/**
	 * @method private
	 */
	_appendSpan : function(fragment, message) {
		if (!message) {
			return;
		}
		var idx = 0;
		for (;;) {
			var next = message.indexOf('\n', idx);
			if (next < 0) {
				f_pager._AddText(fragment, message.substring(idx));
				break;
			}

			if (idx + 1 < next) {
				f_pager._AddText(fragment, message.substring(idx, next));
			}

			f_core.AppendChild(fragment, fragment.ownerDocument
					.createElement("br"));

			idx = next + 1;
		}
	},
	/**
	 * @method protected
	 */
	f_appendRowCountValue : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;

		var rowCount = dataGrid.f_getRowCount();
		if (rowCount < 0) {
			return;
		}

		f_pager._AddSpan(fragment, cls, rowCount);
	},
	/**
	 * @method protected
	 */
	f_appendPageCountValue : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();

		if (rowCount < 0 || rows <= 0) {
			return;
		}

		var pageCount = Math.floor(((rowCount - 1) / rows) + 1);

		f_pager._AddSpan(fragment, cls, pageCount);
	},
	/**
	 * @method protected
	 */
	f_appendFirstValue : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var first = dataGrid.f_getFirst() + 1;

		f_pager._AddSpan(fragment, cls, first);
	},
	/**
	 * @method protected
	 */
	f_appendPagePositionValue : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var rows = dataGrid.f_getRows();
		var first = dataGrid.f_getFirst();

		if (rows <= 0) {
			return;
		}

		first = Math.floor(first / rows) + 1;

		f_pager._AddSpan(fragment, cls, first);
	},
	/**
	 * @method protected
	 */
	f_appendLastValue : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();
		var first = dataGrid.f_getFirst();
		var maxRows = dataGrid.f_getMaxRows();

		var last = first + rows;
		if (rowCount > 0 && last >= rowCount) {
			last = rowCount;
		} else if (maxRows > 0 && last >= maxRows) {
			last = maxRows;
		}

		f_pager._AddSpan(fragment, cls, last);
	},
	/**
	 * @method protected
	 */
	f_appendFirstButton : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var resourceBundle = f_resourceBundle.Get(f_pager);

		var first = dataGrid.f_getFirst();

		f_pager._AddButton(this, fragment, cls, resourceBundle.f_get("FIRST"),
				resourceBundle.f_get("FIRST_TOOLTIP"), (first > 0) ? 0 : -1,
				parameters);
	},
	/**
	 * @method protected
	 */
	f_appendPrevButton : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var resourceBundle = f_resourceBundle.Get(f_pager);

		var rows = dataGrid.f_getRows();
		var first = dataGrid.f_getFirst();

		var idx = first - rows;
		if (idx < 0) {
			idx = 0;
		}

		f_pager._AddButton(this, fragment, cls, resourceBundle
				.f_get("PREVIOUS"), resourceBundle.f_get("PREVIOUS_TOOLTIP"),
				(first > 0) ? idx : -1, parameters);
	},
	/**
	 * @method protected
	 */
	f_appendNextButton : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var resourceBundle = f_resourceBundle.Get(f_pager);

		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();
		var first = dataGrid.f_getFirst();
		var maxRows = dataGrid.f_getMaxRows();

		var idx = first + rows;

		var nextIndex = -1;

		if (rowCount >= 0) {
			if (idx + rows > rowCount) {
				idx = (rowCount - ((rowCount + rows - 1) % rows)) - 1;
				if (idx < 0) {
					idx = 0;
				}
			}

			if (idx > first) {
				nextIndex = idx;
			}
		} else {
			nextIndex = idx;
		}

		f_pager._AddButton(this, fragment, cls, resourceBundle.f_get("NEXT"),
				resourceBundle.f_get("NEXT_TOOLTIP"), nextIndex, parameters);
	},
	/**
	 * @method protected
	 */
	f_appendLastButton : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var resourceBundle = f_resourceBundle.Get(f_pager);

		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();
		var first = dataGrid.f_getFirst();
		var maxRows = dataGrid.f_getMaxRows();

		var idx = first + rows;

		var lastIndex = -1;

		if (rowCount >= 0) {
			if (idx + rows > rowCount) {
				idx = (rowCount - ((rowCount + rows - 1) % rows)) - 1;
				if (idx < 0) {
					idx = 0;
				}
			}

			if (idx > first) {
				lastIndex = (rowCount - ((rowCount + rows - 1) % rows)) - 1;
			}
		} else if (idx == maxRows) {
			lastIndex = maxRows;

		} else if (idx < maxRows) {
			lastIndex = (maxRows - ((maxRows + rows - 1) % rows)) - 1;
		}

		f_pager._AddButton(this, fragment, cls, resourceBundle.f_get("LAST"),
				resourceBundle.f_get("LAST_TOOLTIP"), lastIndex, parameters);
	},

	/**
	 * @method protected
	 */
	f_appendPagesButtons : function(fragment, cls, parameters) {
		var dataGrid = this._pagedComponent;
		var resourceBundle = f_resourceBundle.Get(f_pager);

		var rows = dataGrid.f_getRows();
		var rowCount = dataGrid.f_getRowCount();
		var first = dataGrid.f_getFirst();
		var maxRows = dataGrid.f_getMaxRows();

		var maxPage = 3 * 2 + 1;
		var sep = null;

		if (parameters) {
			if (parameters["separator"]) {
				sep = parameters["separator"];
			}
			if (parameters["pages"]) {
				maxPage = parseInt(parameters["pages"], 10);
			}
		}

		var selectedPage = Math.floor(first / rows);
		var nbPage;
		if (rowCount < 0) {
			nbPage = Math.floor((maxRows + rows - 1) / rows) + 1;
		} else {
			nbPage = Math.floor((rowCount + rows - 1) / rows);
		}

		var showPage = nbPage;
		if (showPage > maxPage) {
			showPage = maxPage;
		}

		var pageOffset = 0;
		if (showPage < nbPage) {
			pageOffset = selectedPage - Math.floor(showPage / 2);
			if (pageOffset + showPage > nbPage) {
				pageOffset = nbPage - showPage;
			}

			if (pageOffset < 0) {
				pageOffset = 0;
			}
		}

		if (sep === null) {
			sep = ", ";
		}

		for ( var i = 0; i < showPage; i++) {
			if (i > 0) {
				f_pager._AddText(fragment, sep);
			}

			var pi = pageOffset + i;

			var tooltipKey = "INDEX_TOOLTIP";
			var label = (pi + 1);
			if (rowCount < 0 && pi + 1 == nbPage) {
				label = "...";
				tooltipKey = "UNKNOWN_INDEX_TOOLTIP";
			}

			var tooltipIndex = resourceBundle.f_format(tooltipKey, pi + 1);

			f_pager._AddButton(this, fragment, cls, label, tooltipIndex,
					(pi == selectedPage) ? -1 : (pi * rows));
		}

	},
	/**
	 * @method hidden
	 */
	f_changePosition : function(index) {
		this._pagedComponent.f_setFirst(index);
	}
};

new f_class("f_pager", {
	extend : f_component,
	aspects : [ fa_pager, fa_tabIndex ],
	statics : __statics,
	members : __members
});
