/*
 * $Id: fa_selectionManager.js,v 1.4 2014/01/07 13:48:20 jbmeslin Exp $
 */

/**
 * Aspect SelectionManager
 * 
 * @aspect public abstract fa_selectionManager<T> extends fa_itemsManager,
 *         fa_selectionProvider<T>, fa_clientFullState
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2014/01/07 13:48:20 $
 */
var __statics = {

	/**
	 * @field hidden static final Number
	 */
	EXCLUSIVE_SELECTION : 1,

	/**
	 * @field hidden static final Number
	 */
	APPEND_SELECTION : 2,

	/**
	 * @field hidden static final Number
	 */
	RANGE_SELECTION : 4,

	/**
	 * @field hidden static final Number
	 */
	ACTIVATE_SELECTION : 8,

	/**
	 * @field hidden static final Number
	 */
	STARTRANGE_SELECTION : 16,

	/**
	 * @field hidden static final Number
	 */
	REFRESH_SELECTION : 32,

	/**
	 * @field public static final String
	 */
	BEGIN_PHASE : "begin",

	/**
	 * @field public static final String
	 */
	END_PHASE : "end",

	/**
	 * @method hidden static
	 */
	ComputeMouseSelection : function(evt) {
		var selection = 0;

		if (f_core.IsAppendRangeMode(evt)) {
			selection |= fa_selectionManager.RANGE_SELECTION;
		}

		if (f_core.IsAppendMode(evt)) {
			selection |= fa_selectionManager.APPEND_SELECTION;
		}

		if (!selection) {
			selection |= fa_selectionManager.EXCLUSIVE_SELECTION;
		}

		selection |= fa_selectionManager.ACTIVATE_SELECTION;

		if (!(selection & fa_selectionManager.RANGE_SELECTION)) {
			selection |= fa_selectionManager.STARTRANGE_SELECTION;
		}

		return selection;
	},

	/**
	 * @method hidden static
	 */
	ComputeKeySelection : function(evt) {
		var keySelection = 0;

		if (f_core.IsAppendRangeMode(evt)) {
			keySelection |= fa_selectionManager.RANGE_SELECTION;
			// keySelection |= fa_selectionManager.STARTRANGE_SELECTION;

		} else if (!f_core.IsAppendMode(evt)) {
			keySelection |= fa_selectionManager.EXCLUSIVE_SELECTION;
			keySelection |= fa_selectionManager.STARTRANGE_SELECTION;

		} else {
			// Nous sommes en mode CONTROL
		}

		var code = evt.keyCode;
		switch (code) {
		case f_key.VK_RETURN:
		case f_key.VK_ENTER:
			keySelection |= fa_selectionManager.ACTIVATE_SELECTION;

		case f_key.VK_SPACE:
			if (!(keySelection & fa_selectionManager.RANGE_SELECTION)) {
				keySelection |= fa_selectionManager.STARTRANGE_SELECTION;
			}
			if (evt.ctrlKey) {
				keySelection |= fa_selectionManager.APPEND_SELECTION;
			}
			break;
		}

		return keySelection;
	},
	/**
	 * @method hidden static
	 */
	SetSelectionCardinality : function(object, cardinality, selectionFullState) {
		object._selectionCardinality = cardinality;
		object._selectionFullState = (selectionFullState) ? (new Array) : null;
	}
};

var __members = {
	fa_selectionManager : function() {
		this.f_isSelectable();
	},
	f_finalize : function() {
		this._currentSelection = undefined; // HtmlElement[]
		this._lastSelectedElement = undefined; // HtmlElement

		// this._selectedElementValues=undefined; // string[] or number[]
		// this._deselectedElementValues=undefined; // string[] or number[]
		// this._clearAllSelectedElements=undefined; // boolean

		// this._selectionFullState=undefined; // string[] or number[]
		// this._clientSelectionFullState=undefined; // number

		// this._selectable=undefined; // boolean
		// this._selectionCardinality=undefined; // boolean
		// this._clientSelectionFullState=undefined // boolean
	},
	/**
	 * Returns <code>true</code> is the component is selectable.
	 * 
	 * @method public
	 * @return Boolean <code>true</code> is the component is selectable.
	 */
	f_isSelectable : function() {
		var selectable = this._selectable;
		if (selectable !== undefined) {
			return selectable;
		}

		if (this._selectionCardinality === undefined) {
			var v_selectionCardinality = f_core.GetNumberAttributeNS(this,
					"selectionCardinality", undefined);

			if (v_selectionCardinality === undefined) {
				this._selectable = false;
				return false;
			}
			this._selectionCardinality = v_selectionCardinality;

			var clientSelectionFullState = f_core.GetNumberAttributeNS(this,
					"clientSelectionFullState",
					fa_clientFullState.NONE_CLIENT_FULL_STATE);
			if (clientSelectionFullState) {
				this._clientSelectionFullState = clientSelectionFullState;

				this._selectionFullState = new Array;
			}
		}

		this._selectable = true;

		this._selectedElementValues = new Array;
		this._deselectedElementValues = new Array;
		this._currentSelection = new Array;

		return true;
	},
	f_serialize : {
		before : function() {
			if (!this._selectable) {
				return;
			}

			if (this._clientSelectionFullState == fa_clientFullState.TWOWAYS_CLIENT_FULL_STATE) {
				this.f_setProperty(f_prop.DESELECTED_ITEMS, f_prop.ALL_VALUE);

				this.f_setProperty(f_prop.SELECTED_ITEMS,
						this.f_getSelection(), true);
				return;
			}

			var selectedElementValues = this._selectedElementValues;
			if (selectedElementValues.length) {
				this.f_setProperty(f_prop.SELECTED_ITEMS,
						selectedElementValues, true);
			}

			if (this._clearAllSelectedElements) {
				this.f_setProperty(f_prop.DESELECTED_ITEMS, f_prop.ALL_VALUE);

			} else {
				var deselectedElementValues = this._deselectedElementValues;
				if (deselectedElementValues.length) {
					this.f_setProperty(f_prop.DESELECTED_ITEMS,
							deselectedElementValues, true);
				}
			}
		}
	},
	/**
	 * @method protected
	 */
	f_getCursorElement : function() {
		return this._cursor;
	},
	/**
	 * @method protected
	 */
	f_setCursorElement : function(element) {
		this._cursor = element;
	},
	/**
	 * @method protected
	 */
	f_moveCursor : function(element, show, evt, selection, phaseName,
			selectOnMousedown, details) {
		f_core
				.Assert(element,
						"fa_selectionManager.f_moveCursor: Invalid parameter to move cursor !");
		f_core.Assert(typeof (show) == "boolean" || show === undefined,
				"fa_selectionManager.f_moveCursor: Invalid show parameter type ('"
						+ show + "')");
		f_core.Assert(
				typeof (phaseName) == "string" || phaseName === undefined,
				"fa_selectionManager.f_moveCursor: Invalid phaseName parameter type ('"
						+ phaseName + "')");
		f_core.Assert(typeof (selectOnMousedown) == "boolean"
				|| selectOnMousedown === undefined,
				"fa_selectionManager.f_moveCursor: Invalid selectOnMousedown parameter type ('"
						+ selectOnMousedown + "')");
		f_core.Assert(typeof (details) == "object" || details === undefined,
				"fa_selectionManager.f_moveCursor: Invalid details parameter type ('"
						+ details + "')");

		if (false) {
			console.log("f_moveCursor: element='" + element + "' show='" + show
					+ "' evt='" + evt + "' selection='" + selection
					+ "' phaseName='" + phaseName + "' selectOnMousedown="
					+ selectOnMousedown + " details=" + details);
		}

		var old = this.f_getCursorElement();

		if (element != old) {
			this.f_setCursorElement(element);

			if (old) {
				this.fa_updateElementStyle(old);
			}

			if (element) {
				this.fa_updateElementStyle(element);
			}
		}

		if (!element) {
			return;
		}

		f_core.Debug(fa_selectionManager,
				"f_moveCursor: Move cursor to element '"
						+ this.fa_getElementValue(element)
						+ "'"
						+ ((selection) ? " selection=0x"
								+ selection.toString(16) : "") + " disabled="
						+ this.fa_isElementDisabled(element));

		if (selection) {
			if (this.f_performElementSelection(element, show, evt, selection,
					phaseName, selectOnMousedown, details) === false) {
				//show = false;
				// On peut deplacer le curseur sans qu'une selection soit effective !!! (si disabled)
			}
		}

		if (show) {
			this.fa_showElement(element, true);
		}

		if (!this._selectable) {
			return;
		}

		if ((selection & fa_selectionManager.STARTRANGE_SELECTION)
				&& !this.fa_isElementDisabled(element)) {
			f_core.Debug(fa_selectionManager,
					"f_moveCursor: Set lastSelectedElement to '"
							+ this.fa_getElementValue(element) + "'.");
			this._lastSelectedElement = element;
		}

		if (false && f_core.IsDebugEnabled(fa_selectionManager)) {
			var s = "SelectedValues=";
			var selectedElementValues = this._selectedElementValues;
			if (!selectedElementValues.length) {
				s += "EMPTY";
			} else {
				s += selectedElementValues.join(",");
			}

			s += "\ndeselectedValues=";
			var deselectedElementValues = this._deselectedElementValues;
			if (!deselectedElementValues.length) {
				s += "EMPTY";

			} else {
				s += deselectedElementValues.join(",");
			}

			if (this._clearAllSelectedElements) {
				s += " CLEAR ALL";
			}

			s += "\nselection=";
			var currentSelection = this._currentSelection;
			if (!currentSelection.length) {
				s += "EMPTY";

			} else {
				s += currentSelection.join(",");
			}

			f_core.Debug(fa_selectionManager, "f_moveCursor: " + s);
		}
	},
	/**
	 * @method protected
	 * @param HTMLElement
	 *            element
	 * @param Boolean
	 *            selected
	 * @return Boolean
	 */
	f_updateElementSelection : function(element, selected) {
		// Suivant l'état enregistré, on recalcule l'état !

		var value = this.fa_getElementValue(element);

		selected = this._isElementValueSelected(value, selected);
		this.fa_setElementSelected(element, selected);

		if (!selected) {
			return false;
		}

		this._currentSelection.push(element);

		return true;
	},
	_selectElement : function(element, value, show) {
		if (this.fa_isElementSelected(element)) {
			return;
		}

		this.fa_setElementSelected(element, true);
		this.fa_updateElementStyle(element);

		this._currentSelection.push(element);

		if (value === undefined) {
			value = this.fa_getElementValue(element);
		}

		if (!this._deselectedElementValues.f_removeElement(value)) {
			this._selectedElementValues.f_addElement(value);
		}

		if (show) {
			this.fa_showElement(element, true);
		}
	},
	_deselectElement : function(element, value) {
		if (!this.fa_isElementSelected(element)) {
			return false;
		}

		this.fa_setElementSelected(element, false);
		this.fa_updateElementStyle(element);

		this._currentSelection.f_removeElement(element);

		if (value === undefined) {
			value = this.fa_getElementValue(element);
		}

		if (this._selectedElementValues.f_removeElement(value)) {
			this._lastDeSelectedElement = element;
			return true;
		}

		if (this._clearAllSelectedElements) {
			return false;
		}

		return this._deselectedElementValues.f_addElement(value);
	},
	_deselectAllElements : function() {
		var currentSelection = this._currentSelection;
		if (currentSelection.length) {
			this._currentSelection = new Array;

			for ( var i = 0; i < currentSelection.length; i++) {
				var element = currentSelection[i];

				this.fa_setElementSelected(element, false);
				this.fa_updateElementStyle(element);
			}
		}

		this._clearAllSelectedElements = true;
		this._deselectedElementValues = new Array;
		this._selectedElementValues = new Array;
	},
	_selectRange : function(first, last, appendSelection) {
		// on deselectionne tout ... puis on selectionne le range !

		f_core.Debug(fa_selectionManager, "_selectRange: Select range from '"
				+ this.fa_getElementValue(first) + "'=>'"
				+ this.fa_getElementValue(last) + "' appendMode="
				+ appendSelection);

		var elements = this.fa_listVisibleElements(true);
		if (!elements) {
			return;
		}

		var l = new Array;
		var append = false;
		for ( var i = 0; i < elements.length; i++) {
			var element = elements[i];

			var elementValue = this.fa_getElementValue(element);
			if (append && !this.fa_isElementDisabled(element)) {
				l.push(elementValue);
			}

			if (element != first && element != last) {
				continue;
			}

			if (append) {
				append = false;
				break;
			}

			if (!this.fa_isElementDisabled(element)) {
				l.push(elementValue);
			}

			if (first == last) {
				break;
			}

			append = true;
		}

		if (append || !l.length) {
			// Y a un probleme !
			// Ou on selectionne un truc non selectionnable !
			return;
		}

		return this._selectElementsRange(l, appendSelection, false, elements);
	},
	/**
	 * @method private
	 * @param Array
	 *            <T> l
	 * @param Boolean
	 *            appendSelection
	 * @param Boolean
	 *            show
	 * @param Array
	 *            <Object> elements
	 * @return void
	 */
	_selectElementsRange : function(l, appendSelection, show, elements) {
		if (f_core.IsDebugEnabled(fa_selectionManager)) {
			var s = "Range select: " + l.length + " elements: ";

			if (!l.length) {
				s += " EMPTY ???";

			} else {
				s += l.join(",");
			}

			f_core.Debug("fa_selectionManager", s);
		}

		var elementByValue = new Object;
		if (elements === undefined) {
			elements = this.fa_listVisibleElements();
		}

		for ( var i = 0; i < elements.length; i++) {
			var element = elements[i];

			elementByValue[this.fa_getElementValue(element)] = element;
		}

		var selectedElementValues = this._selectedElementValues;
		for ( var i = 0; i < selectedElementValues.length;) {
			var selectedElementValue = selectedElementValues[i];

			var found = false;
			for ( var j = 0; j < l.length; j++) {
				if (selectedElementValue != l[j]) {
					continue;
				}

				// On le laisse selectionné, on le retire de notre liste "à
				// selectionner" !
				l.splice(j, 1);
				found = true;
				break;
			}

			if (found || appendSelection) {
				i++;
				continue;
			}

			var element = elementByValue[selectedElementValue];

			if (element) {
				this._deselectElement(element);
				continue;
			}

			// Pas dans les visibles, on supprime directement du tableau.
			selectedElementValues.splice(i, 1);
		}

		if (!this._clearAllSelectedElements) {
			var deselectedElementValues = this._deselectedElementValues;

			for ( var i = 0; i < deselectedElementValues.length;) {
				var deselectedElementValue = deselectedElementValues[i];

				var found = false;
				for ( var j = 0; j < l.length; j++) {
					if (deselectedElementValue != l[j]) {
						continue;
					}

					// On le retire de la deselection !
					found = true;
					l.splice(j, 1);
					break;
				}

				if (!found) {
					i++;
					continue;
				}

				// On le retire de la liste des "déselectionnés" et on le
				// reselectionne !
				var element = elementByValue[deselectedElementValue];
				if (element) {
					this._selectElement(element, deselectedElementValue, show);
					show = false;
					continue;
				}

				deselectedElementValues.splice(i, 1);
			}
		}

		for ( var i = 0; i < l.length; i++) {
			var value = l[i];
			var element = elementByValue[value];
			if (!element) {
				// La valeur n'est pas affichée !

				selectedElementValues.push(value);
				continue;
			}

			this._selectElement(element, value, show);
			show = false;
		}
	},

	/**
	 * @method protected
	 * @param Object
	 *            element
	 * @param Boolean
	 *            show
	 * @param Event
	 *            evt
	 * @param Number
	 *            selection Mask of type of selection
	 * @param String
	 *            phaseName
	 * @param optional
	 *            Boolean selectOnMousedown force selection on mousedown
	 * @param optional
	 *            Object detail
	 * @return Boolean
	 */
	f_performElementSelection : function(element, show, evt, selection,
			phaseName, selectOnMousedown, detail) {
		var cardinality = this._selectionCardinality;
		var mouseup = true;
		if (phaseName) {
			mouseup = (phaseName == fa_selectionManager.END_PHASE);
		}

		this._phaseName = phaseName;
		if (!cardinality) {
			return false;
		}
		
		if (f_core.IsDebugEnabled(fa_selectionManager)) {
			f_core.Debug(fa_selectionManager, "f_performElementSelection: "
					+ " exclusive='"
					+ ((selection & fa_selectionManager.EXCLUSIVE_SELECTION) > 0)
					+ "'" + " append='"
					+ ((selection & fa_selectionManager.APPEND_SELECTION) > 0)
					+ "'" + " range='"
					+ ((selection & fa_selectionManager.RANGE_SELECTION) > 0)
					+ "'  disabled=" + this.fa_isElementDisabled(element));
		}

		if (this.fa_isElementDisabled(element)) {
			return false;
		}

		var rangeMode = (selection & fa_selectionManager.RANGE_SELECTION);

		// alert("Select="+this._selectionCardinality+"/"+node._value+"/"+li._node._selected);

		var elementSelected = this.fa_isElementSelected(element);
		if (elementSelected && f_core.IsPopupButton(evt)) {
			return true;
		}
		var elementValue = this.fa_getElementValue(element);
		if (!detail) {
			detail = f_event.NewDetail();
		}
		detail.selection = selection;
		if (selection & fa_selectionManager.ACTIVATE_SELECTION) {
			detail.value |= f_event.ACTIVATE_DETAIL;
			detail.activate = true;
		}
		if (selection & fa_selectionManager.REFRESH_SELECTION) {
			detail.value |= f_event.REFRESH_DETAIL;
			detail.refresh = true;
		}
		if (selection) {
			detail.value |= 1;
		}

		var item = this.fa_getElementItem(element);

		if (!mouseup
				&& this.fa_firePreSelectionChangedEvent(evt, detail, item,
						elementValue) === false) {
			this._cancelSelection = true;
			return false;
		}

		if (selectOnMousedown
				&& (selection & fa_selectionManager.EXCLUSIVE_SELECTION)) {
			this._cancelSelection = true;
		}

		if (mouseup && this._cancelSelection) {
			this._cancelSelection = false;
			return false;
		}

		switch (cardinality) {
		case fa_cardinality.OPTIONAL_CARDINALITY:
			if (elementSelected) {
				// Deselection seulement !
				if (selection & fa_selectionManager.APPEND_SELECTION && mouseup) {
					this._deselectAllElements();
					break;
				}
			}

			if (selection & fa_selectionManager.APPEND_SELECTION
					&& selectOnMousedown) {
				// En modre append la selection se fait au mouseup
				return false;
			}
			// On continue ....

		case fa_cardinality.ONE_CARDINALITY:
			// Fred : mais il faut quand même provoquer le selected
			// if (elementSelected) {
			// On ne peut pas deselectionner un élément déjà selectionné
			// return false;
			// }

			if (selectOnMousedown || mouseup) {
				// On deselectionne tout: 1 seul doit rester selectionner
				this._deselectAllElements();
				this._selectElement(element, elementValue, show);
			}

			break;

		case fa_cardinality.ONEMANY_CARDINALITY:
			if (elementSelected && !rangeMode) {
				if (this._currentSelection.length < 2) {
					// Un seul selectionné: on arrete tout !
					break;
				}
			}

			// On continue ...

		case fa_cardinality.ZEROMANY_CARDINALITY:

			var lastSelectedElement = this._lastSelectedElement;
			if (rangeMode) {
				var lastSelectedElement = this._lastSelectedElement;
				if (!lastSelectedElement) {
					f_core.Debug(fa_selectionManager,
							"f_performElementSelection: No lastSelectedElement set to '"
									+ this.fa_getElementValue(element) + "'.");

					this._lastSelectedElement = element;
					lastSelectedElement = element;
				}

				// Nous sommes en range mode .....
				if (mouseup || selectOnMousedown) {
					this._selectRange(element, lastSelectedElement,
							(selection & fa_selectionManager.APPEND_SELECTION));
				}
			} else if (elementSelected) {

				if (selection & fa_selectionManager.APPEND_SELECTION && mouseup) {
					// On est juste en ajout: pas de déselection complete !
					this._deselectElement(element, elementValue);
					break;
				}

				var selections = this.f_getSelection();
				if (selection & fa_selectionManager.EXCLUSIVE_SELECTION
						&& (mouseup || selectOnMousedown)
						&& selections.length > 1) {
					// On deselectionne tout !
					this._deselectAllElements();
				}

			} else if (selection & fa_selectionManager.EXCLUSIVE_SELECTION) {
				// On deselectionne tout !
				if (mouseup || selectOnMousedown) {
					this._deselectAllElements();
				}
			}

			if (selection & fa_selectionManager.APPEND_SELECTION && !mouseup) {
				break;
			}

			if (selection & fa_selectionManager.APPEND_SELECTION
					&& (mouseup || selectOnMousedown)) {
				var deselectedElement = this._lastDeSelectedElement;
				if (deselectedElement && deselectedElement == element) {
					this._lastDeSelectedElement = null;
					// break;
				}

			}

			if (selectOnMousedown || mouseup) {
				this._selectElement(element, elementValue, show);
			}
			break;
		}

		if (!mouseup && !selectOnMousedown) {
			return true;
		}
		this.fa_fireSelectionChangedEvent(evt, detail, item, elementValue);

		return true;
	},

	/**
	 * @method hidden
	 * @return void
	 */
	f_setSelectionStates : function(selectionFullState) {
		this._selectionFullState = selectionFullState;
	},
	/**
	 * @method protected
	 */
	f_getClientSelection : function() {
		return this._currentSelection;
	},
	/**
	 * @method public
	 * @return T An array of selected values.
	 */
	f_getSelection : function() {
		var ret = new Array;
		if (!this._selectable) {
			return ret;
		}

		if (this._clientSelectionFullState) {
			if (!this._clearAllSelectedElements) {
				var selectionFullState = this._selectionFullState;
				if (selectionFullState && selectionFullState.length) {
					ret.push.apply(ret, selectionFullState);
				}
			}

			var selectedElementValues = this._selectedElementValues;
			if (selectedElementValues.length) {
				ret.f_addElements.apply(ret, selectedElementValues);
			}

			var deselectedElementValues = this._deselectedElementValues;
			if (deselectedElementValues.length) {
				ret.f_removeElements.apply(ret, deselectedElementValues);
			}

			return ret;
		}

		// Nous ne sommes pas en fullstate, on ne renvoit que ce que l'on voit !
		var currentSelection = this._currentSelection;
		for ( var i = 0; i < currentSelection.length; i++) {
			var element = currentSelection[i];

			var value = this.fa_getElementValue(element);
			if (value === undefined) {
				continue;
			}

			ret.push(value);
		}

		return ret;
	},
	_isElementValueSelected : function(value, defaultValue) {
		var selected = defaultValue;

		var selectionFullState = this._selectionFullState;
		if (!selected && selectionFullState) {
			selected = selectionFullState.f_contains(value);
		}

		if (selected && !this._clearAllSelectedElements) {
			// On recherche s'il n'a pas été deselectionné !
			if (this._deselectedElementValues.f_contains(value)) {
				// Il a été deselectionné !
				return false;
			}

			// Il n'a pas été deselectionné !
			return true;
		}

		// Tout a été deselectionné, ou c'etait pas sélectionné à la création du
		// composant!

		return this._selectedElementValues.f_contains(value);
	},
	/**
	 * @method public
	 * @param T
	 *            selection The new selection.
	 * @return optional Boolean show Show the first new selected element.
	 * @return void
	 */
	f_setSelection : function(selection, show) {
		f_core.Assert(typeof (selection) == "object",
				"fa_selectionManager.f_setSelection: Invalid selection parameter ("
						+ selection + ")");
		f_core.Assert(show === undefined || typeof (show) == "boolean",
				"fa_selectionManager.f_setSelection: Invalid show parameter ("
						+ show + ")");

		f_core.Debug(fa_selectionManager, "f_setSelection: Set selection to '"
				+ selection + "' show='" + show + "'.");

		if (!selection || !selection.length) {
			this._deselectAllElements();
			this.fa_fireSelectionChangedEvent();
			return;
		}

		this._deselectAllElements(); // ??
		this._selectElementsRange(selection, show);

		this.fa_fireSelectionChangedEvent();
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_firePreSelectionChangedEvent : function(evt, detail, item, elementValue) {

		return this.f_fireEvent(f_event.PRE_SELECTION, evt, item, elementValue,
				this, detail);
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_fireSelectionChangedEvent : function(evt, detail, item, elementValue) {

		return this.f_fireEvent(f_event.SELECTION, evt, item, elementValue,
				this, detail);
	},

	/**
	 * @method protected abstract
	 * @param any
	 *            element
	 * @return Boolean
	 */
	fa_isElementSelected : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @param any
	 *            element
	 * @param Boolean
	 *            selected
	 * @return void
	 */
	fa_setElementSelected : f_class.ABSTRACT,

	/**
	 * @method protected abstract
	 * @param any
	 *            element
	 * @return Object
	 */
	fa_getElementValue : f_class.ABSTRACT
};

new f_aspect("fa_selectionManager", {
	extend : [ fa_itemsManager, fa_selectionProvider, fa_clientFullState ],
	statics : __statics,
	members : __members
});
