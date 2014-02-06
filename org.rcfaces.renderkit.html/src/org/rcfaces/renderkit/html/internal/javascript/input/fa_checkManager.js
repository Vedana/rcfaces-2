/*
 * $Id: fa_checkManager.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect CheckManager
 *
 * @aspect public abstract fa_checkManager extends fa_itemsManager, fa_clientFullState
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __members = {
	fa_checkManager: function() {
		this.f_isCheckable();
	},

	f_finalize: function() {
		//this._checkedElementValues=undefined; // string[] or number[]
		//this._uncheckedElementValues=undefined; // string[] or number[]
		this._currentChecks=undefined; // any[]
		// this._clearAllCheckedElements=undefined; // boolean

		//this._checkFullState=undefined; // String[] or Number[]
		//this._clientCheckFullState=undefined; // Number
		
		//this._checkCardinality=undefined; // Number
		//this._checkable=undefined; // Boolean
	},

	f_serialize: {
		before: function() {
			if (!this._checkable) {
				return;
			}	
			
			if (this._clientCheckFullState==fa_clientFullState.TWOWAYS_CLIENT_FULL_STATE) {
				this.f_setProperty(f_prop.UNCHECKED_ITEMS, f_prop.ALL_VALUE);
				
				this.f_setProperty(f_prop.CHECKED_ITEMS, this.f_getCheckedValues(), true);
				return;
			}
			
			var checkedElementValues=this._checkedElementValues;
			if (checkedElementValues.length) {
				this.f_setProperty(f_prop.CHECKED_ITEMS, checkedElementValues, true);
			}
			
			if (this._clearAllCheckedElements) {
				this.f_setProperty(f_prop.UNCHECKED_ITEMS, f_prop.ALL_VALUE);

			} else {
				var uncheckedElementValues=this._uncheckedElementValues;
				if (uncheckedElementValues.length) {
					this.f_setProperty(f_prop.UNCHECKED_ITEMS, uncheckedElementValues, true);
				}
			}
		}
	},

	/**
	 * Returns <code>true</code> if the component is checkable.
	 * 
	 * @method public
	 * @return Boolean <code>true</code> if the component is checkable.
	 */
	f_isCheckable: function() {
		var checkable=this._checkable;
		
		if (checkable!==undefined) {
			return checkable;
		}
		
		var v_checkCardinality=f_core.GetNumberAttributeNS(this,"checkCardinality", undefined);
		if (v_checkCardinality===undefined) {
			this._checkable=false;
			return false;
		}
		
		var clientCheckFullState=f_core.GetNumberAttributeNS(this,"clientCheckFullState", fa_clientFullState.NONE_CLIENT_FULL_STATE);
		if (clientCheckFullState) {
			this._clientCheckFullState=clientCheckFullState;

			this._checkFullState=new Array;
		}
		
		this._checkCardinality=v_checkCardinality;
		this._checkable=true;
		
		this._checkedElementValues=new Array;
		this._uncheckedElementValues=new Array;
		this._currentChecks=new Array;
		
		return true;
	},

	/**
	 * @method hidden
	 * @return void
	 */
	f_setCheckStates: function(checkFullState) {
		this._checkFullState=checkFullState;
	},
	/**
	 * @method private
	 */
	_checkElement: function(element, value, show) {
		if (this.fa_isElementChecked(element)) {
			return;
		}
		
		this.fa_setElementChecked(element, true);
		this.fa_updateElementStyle(element);
		
		this._currentChecks.push(element);
		
		if (value===undefined) {
			value=this.fa_getElementValue(element);
		}
		
		if (!this._uncheckedElementValues.f_removeElement(value)) {
			this._checkedElementValues.f_addElement(value);
		}
		
		if (show) {
			this.fa_showElement(element);
		}
	},
	/**
	 * @method private
	 * @param Array<T> l
	 * @param Boolean appendSelection
	 * @param Boolean show
	 * @param optional Array<Object> elements
	 * @return Boolean
	 */
	_checkElementsRange: function(l, appendSelection, show, elements) {
		if (f_core.IsDebugEnabled(fa_checkManager)) {
			var s="Range check: "+l.length+" elements: ";

			if (!l.length) {
				s+=" EMPTY ???";
				
			} else {
				s+=l.join(",");
			}
						
			f_core.Debug("fa_checkManager", s);
		}
		
		var checkFound=false;

		var elementByValue=new Object;
		if (elements===undefined) {
			elements=this.fa_listVisibleElements();
		}
		
		for(var i=0;i<elements.length;i++) {
			var element=elements[i];
			
			elementByValue[this.fa_getElementValue(element)]=element;
		}
			
		var checkedElementValues=this._checkedElementValues;
		for(var i=0;i<checkedElementValues.length;) {
			var checkedElementValue=checkedElementValues[i];
				
			var found=false;
			for(var j=0;j<l.length;j++) {
				if (checkedElementValue!=l[j]) {
					continue;
				}
				
				// On le laisse selectionné, on le retire de notre liste "à selectionner" !
				l.splice(j, 1);
				found=true;
				break;
			}
			
			if (found || appendSelection) {
				i++;
				continue;
			}

			var element=elementByValue[checkedElementValue];
			
			if (element) {
				checkFound=true;
				
				this._uncheckElement(element);
				continue;
			}

			// Pas dans les visibles, on supprime directement du tableau.
			checkedElementValues.splice(i, 1);
		}
		
		if (!this._clearAllCheckedElements) { 
			var uncheckedElementValues=this._uncheckedElementValues;

			for(var i=0;i<uncheckedElementValues.length;) {
				var uncheckedElementValue=uncheckedElementValues[i];
				
				var found=false;
				for(var j=0;j<l.length;j++) {
					if (uncheckedElementValue!=l[j]) {
						continue;
					}
					
					// On le retire de la deselection !
					checkFound=true;
					found=true;
					l.splice(j, 1);
					break;
				}
					
				if (!found) {
					i++;
					continue;
				}
				
				// On le retire de la liste des "déselectionnés" et on le reselectionne !
				var element=elementByValue[uncheckedElementValue];
				if (element) {
					this._checkElement(element, uncheckedElementValue, show);
					show=false;
					checkFound=true;
					continue;
				}

				uncheckedElementValues.splice(i, 1);
			}			
		}
		
		for(var i=0;i<l.length;i++) {
			var value=l[i];
			var element=elementByValue[value];
			if (!element) {
				// La valeur n'est pas affichée !
				
				checkedElementValues.push(value);
				continue;
			}
			
			this._checkElement(element, value, show);
			show=false;
		}		
		
		return checkFound;
	},
	/**
	 * @method private
	 */
	_uncheckElement: function(element, value) {
		if (!this.fa_isElementChecked(element)) {
			return false;
		}

		this.fa_setElementChecked(element, false);
		this.fa_updateElementStyle(element);
		
		this._currentChecks.f_removeElement(element);
		
		if (value===undefined) {
			value=this.fa_getElementValue(element);
		}
		
		if (this._checkedElementValues.f_removeElement(value)) {
			return true;
		}
	
		if (this._clearAllCheckedElements) {
			return false;
		}
			
		return this._uncheckedElementValues.f_addElement(value);
	},
	/**
	 * @method private
	 */
	_uncheckAllElements: function() {		
		var currentChecks=this._currentChecks;
		if (currentChecks.length) {
			this._currentChecks=new Array;

			for(var i=0;i<currentChecks.length;i++) {
				var element=currentChecks[i];
				
				this.fa_setElementChecked(element, false);
				this.fa_updateElementStyle(element);
			}
		}
		
		this._clearAllCheckedElements=true;
		this._uncheckedElementValues=new Array;
		this._checkedElementValues=new Array;
	},
	
	/**
	 * @method protected
	 */
	fa_updateElementCheck: function(element, checked) {
		var value=this.fa_getElementValue(element);
	
		checked=this.fa_isElementValueChecked(value, checked);
		this.fa_setElementChecked(element, checked);
		
		if (!checked) {
			return false;
		}
		
		this._currentChecks.push(element);
		
		return true;
	},
	
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_performElementCheck: function(element, show, evt, checked) {
		var cardinality=this._checkCardinality;
		if (!cardinality) {
			return false;
		}
		
		f_core.Debug(fa_checkManager, "fa_performElementCheck: performElementCheck '"+this.fa_getElementValue(element)+"' disabled="+this.fa_isElementDisabled(element)+" cardinality="+cardinality);
	
		if (this.fa_isElementDisabled(element)) {
			return false;
		}
		
		var elementChecked=this.fa_isElementChecked(element);
		if (elementChecked==checked) {
			return false;
		}
		var elementValue=this.fa_getElementValue(element);
		
		switch(cardinality) {
		case fa_cardinality.ONE_CARDINALITY:
			if (elementChecked) {
				return false;
			}
			
			// On continue ....
			
		case fa_cardinality.OPTIONAL_CARDINALITY:			
			// On décoche tout: 1 seul doit rester selectionner 
			this._uncheckAllElements();
				
			if (checked) {
				this._checkElement(element, elementValue, show);
			}
			break;
			
		case fa_cardinality.ONEMANY_CARDINALITY:
			if (elementChecked) {
				if (this._currentChecks.length<2) {
					// Un seul décoché: on arrete tout !
					return false;
				}
			}

			// On continue ...

		case fa_cardinality.ZEROMANY_CARDINALITY:
			if (elementChecked) {
				this._uncheckElement(element, elementValue);
				break;
			}

			this._checkElement(element, elementValue, show);
			break;
		}
	
		var detail=0;
		if (checked) {
			detail|=1;
		}

		var item=this.fa_getElementItem(element);
	
		this.fa_fireCheckChangedEvent(evt, detail, item, elementValue);
		
		return true;
	},
	/**
	 * @method protected
	 */
	fa_fireCheckChangedEvent: function(evt, detail, item, elementValue) {
	
		return this.f_fireEvent(f_event.CHECK, evt, item, elementValue, null, detail);
	},
	/**
	 * @method protected
	 */
	fa_isElementValueChecked: function(value, defaultValue) {
		var checked=defaultValue;
		
		var checkFullState=this._checkFullState;
		if (!checked && checkFullState) {
			checked=checkFullState.f_contains(value);
		}
	
		if (checked && !this._clearAllCheckedElements) {
			// On recherche s'il n'a pas été décoché !
			if (this._uncheckedElementValues.f_contains(value)) {
				// Il a été décoché !
				return false;
			}
		
			// Il n'a pas été décoché !
			return true;
		}
		
		// Tout a été décoché, ou c'etait pas coché à la création du composant!
		
		return this._checkedElementValues.f_contains(value);
	},
	/**
	 * @method public
	 * @return any[] An array of checked values.
	 */
	f_getCheckedValues: function() {
		var ret=new Array;
		if (!this._checkable) {
			return ret;
		}

		if (this._checkFullState) {
			if (!this._clearAllCheckedElements) {
				var checkFullState=this._checkFullState;
				if (checkFullState && checkFullState.length) {
					ret.push.apply(ret, checkFullState);
				}
			}	
			
			var checkedElementValues=this._checkedElementValues;
			if (checkedElementValues.length) {
				ret.f_addElements.apply(ret, checkedElementValues);
			}

			var uncheckedElementValues=this._uncheckedElementValues;
			if (uncheckedElementValues.length) {
				ret.f_removeElements.apply(ret, uncheckedElementValues);
			}
			
			return ret;
		}
		
		// Nous ne sommes pas en fullstate, on ne renvoit que ce que l'on voit !
		var currentChecks=this._currentChecks;
		for(var i=0;i<currentChecks.length;i++) {
			var element=currentChecks[i];
			
			var value=this.fa_getElementValue(element);
			if (value===undefined) {
				continue;
			}

			ret.push(value);
		}
		
		return ret;
	},

	/**
	 * @method public abstract
	 * @param any[] checkedValues An array of values
	 * @return void
	 */
	f_setCheckedValues: function(checkedValues, show) {
		f_core.Assert(typeof(checkedValues)=="object", "fa_checkManager.f_setCheckedValues: Invalid checkedValues parameter ("+selection+")");
		f_core.Assert(show===undefined || typeof(show)=="boolean", "fa_checkManager.f_setCheckedValues: Invalid show parameter ("+show+")");
		
		f_core.Debug(fa_checkManager, "f_setCheckedValues: Set checked values to '"+checkedValues+"' show='"+show+"'.");
		
		if (!checkedValues || !checkedValues.length) {
			this._uncheckAllElements();
			this.fa_fireCheckChangedEvent();
			return;
		}
		
		this._uncheckAllElements(); // ??
		this._checkElementsRange(checkedValues, show);
		
		this.fa_fireCheckChangedEvent();
	},

	/**
	 * @method protected abstract
	 * @param Object element
	 * @return Boolean
	 */
	fa_isElementChecked: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @param Object element
	 * @boolean checked state
	 * @return void
	 */
	fa_setElementChecked: f_class.ABSTRACT
	
};

new f_aspect("fa_checkManager", {
	extend: [ fa_itemsManager, fa_clientFullState ],
	members: __members 
});
