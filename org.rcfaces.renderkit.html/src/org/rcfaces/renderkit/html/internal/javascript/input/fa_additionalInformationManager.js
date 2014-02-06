/*
 * $Id: fa_additionalInformationManager.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect AdditionalInformationManager
 *
 * @aspect public abstract fa_additionalInformationManager extends fa_itemsManager, fa_clientFullState
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __members = {
	
	/**
	 * @field protected Boolean
	 */
	_additionalInformations: undefined,
	
	/**
	 * @field protected Number
	 */
	_additionalInformationCardinality: undefined,
	
	fa_additionalInformationManager: function() {
		if (this._additionalInformationCardinality===undefined) {
			var v_additionalInformationCardinality=f_core.GetNumberAttributeNS(this,"additionalInformationCardinality", undefined);
	
			if (v_additionalInformationCardinality===undefined) {
				return;
			}
			this._additionalInformationCardinality=v_additionalInformationCardinality;
							
			var clientAdditionalFullState=f_core.GetNumberAttributeNS(this,"clientAdditionalInformationFullState", fa_clientFullState.NONE_CLIENT_FULL_STATE);
			if (clientAdditionalFullState) {
				this._clientAdditionalFullState=clientAdditionalFullState;

				this._additionalFullState=new Array;
			}
		}
		
		this._additionalInformations=true;
		
		this._shownAdditionalElementValues=new Array;
		this._hiddenAdditionalElementValues=new Array;
		this._currentAdditionalInformations=new Array;
	},
	f_finalize: function() {
		this._currentAdditionalInformations=undefined; // HtmlElement[]

		// this._shownAdditionalElementValues=undefined; // string[] or number[]
		// this._hiddenAdditionalElementValues=undefined; // string[] or number[]
		// this._hideAllAdditionalInformations=undefined; // boolean

		// this._additionalFullState=undefined; // string[] or number[]
		// this._clientAdditionalFullState=undefined; // number

		//	this._additionalInformations=undefined;  // boolean
		//	this._additionalInformationCardinality=undefined; // boolean
		//  this._clientSelectionFullState=undefined // boolean
	},
	f_serialize: {
		before: function() {
			if (!this._additionalInformations) {
				return;
			}
			
			if (this._clientAdditionalFullState==fa_clientFullState.TWOWAYS_CLIENT_FULL_STATE) {
				this.f_setProperty(f_prop.HIDE_ADDITIONAL, f_prop.ALL_VALUE);
				
				this.f_setProperty(f_prop.SHOW_ADDITIONAL, this.f_getAdditionalInformationValues(), true);
				return;
			}
			
			var selectedElementValues=this._shownAdditionalElementValues;
			if (selectedElementValues.length) {
				this.f_setProperty(f_prop.SHOW_ADDITIONAL, selectedElementValues, true);
			}
			
			if (this._hideAllAdditionalInformations) {
				this.f_setProperty(f_prop.HIDE_ADDITIONAL, f_prop.ALL_VALUE);

			} else {
				var deselectedElementValues=this._hiddenAdditionalElementValues;
				if (deselectedElementValues.length) {
					this.f_setProperty(f_prop.HIDE_ADDITIONAL, deselectedElementValues, true);
				}
			}
		}
	},
	/**
	 * @method protected
	 * @param Object data
	 * @return void
	 */
	fa_serializeAdditionalInformations: function(data) {
		var listSep='\x01';
			
		var selectedElementValues=this._shownAdditionalElementValues;
		if (selectedElementValues.length) {
			data[f_prop.SHOW_ADDITIONAL]=selectedElementValues.join(listSep);
		}
	
		if (this._hideAllAdditionalInformations) {
			data[f_prop.HIDE_ADDITIONAL]=f_prop.ALL_VALUE;

		} else {
			var deselectedElementValues=this._hiddenAdditionalElementValues;
			if (deselectedElementValues.length) {
				data[f_prop.HIDE_ADDITIONAL]=deselectedElementValues.join(listSep);
			}
		}
	},
	/**
	 * @method protected
	 * @param HTMLElement element
	 * @param Boolean shown
	 * @return Boolean
	 */
	fa_updateElementAdditionalInformations: function(element, shown) {
		// Suivant l'état enregistré, on recalcule l'état !

		var value=this.fa_getElementValue(element);
	
		shown=this.fa_isAdditionalElementValueVisible(value, shown);
		this.fa_setAdditionalElementVisible(element, shown);
		
		if (!shown) {
			return false;
		}
		
		this._currentAdditionalInformations.push(element);
		
		return true;
	},
	/**
	 * @method private
	 * @return void
	 */
	_showAdditionalElement: function(element, value, show) {
		if (this.fa_isAdditionalElementVisible(element)) {
			return;
		}
		
		this.fa_setAdditionalElementVisible(element, true);
		this.fa_updateElementStyle(element);
		
		this._currentAdditionalInformations.push(element);
		
		if (value===undefined) {
			value=this.fa_getElementValue(element);
		}
		
		if (!this._hiddenAdditionalElementValues.f_removeElement(value)) {
			this._shownAdditionalElementValues.f_addElement(value);
		}
		
		if (show) {
			this.fa_showElement(element);
		}
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_hideAdditionalElement: function(element, value) {
		if (!this.fa_isAdditionalElementVisible(element)) {
			return false;
		}

		this.fa_setAdditionalElementVisible(element, false);
		this.fa_updateElementStyle(element);
		
		this._currentAdditionalInformations.f_removeElement(element);
		
		if (value===undefined) {
			value=this.fa_getElementValue(element);
		}
		
		if (this._shownAdditionalElementValues.f_removeElement(value)) {
			return true;
		}
		
		if (this._hideAllAdditionalInformations) {
			return false;
		}
		
		return this._hiddenAdditionalElementValues.f_addElement(value);
	},
	/**
	 * @method private
	 * @return void
	 */
	_hideAllAdditionalElements: function() {		
		var currentSelection=this._currentAdditionalInformations;
		if (currentSelection.length) {
			this._currentAdditionalInformations=new Array;

			for(var i=0;i<currentSelection.length;i++) {
				var element=currentSelection[i];
				
				this.fa_setAdditionalElementVisible(element, false);
				this.fa_updateElementStyle(element);
			}
		}
		
		this._hideAllAdditionalInformations=true;
		this._hiddenAdditionalElementValues=new Array;
		this._shownAdditionalElementValues=new Array;
	},
	/**
	 * @method private
	 * @return void
	 */
	_showAdditionalElementsRange: function(l, appendSelection, show, elements) {
		if (f_core.IsDebugEnabled(fa_additionalInformationManager)) {
			var s="Range select: "+l.length+" elements: ";

			if (!l.length) {
				s+=" EMPTY ???";
				
			} else {
				s+=l.join(",");
			}
						
			f_core.Debug("fa_additionalInformationManager", s);
		}

		var elementByValue=new Object;
		if (elements===undefined) {
			elements=this.fa_listVisibleElements();
		}
		
		for(var i=0;i<elements.length;i++) {
			var element=elements[i];
			
			elementByValue[this.fa_getElementValue(element)]=element;
		}
			
		var selectedElementValues=this._shownAdditionalElementValues;
		for(var i=0;i<selectedElementValues.length;) {
			var selectedElementValue=selectedElementValues[i];
				
			var found=false;
			for(var j=0;j<l.length;j++) {
				if (selectedElementValue!=l[j]) {
					continue;
				}
				
				// On le laisse additionalné, on le retire de notre liste "à additionalner" !
				l.splice(j, 1);
				found=true;
				break;
			}
			
			if (found || appendSelection) {
				i++;
				continue;
			}

			var element=elementByValue[selectedElementValue];
			
			if (element) {
				this._hideAdditionalElement(element);
				continue;
			}

			// Pas dans les visibles, on supprime directement du tableau.
			selectedElementValues.splice(i, 1);
		}
		
		if (!this._hideAllAdditionalInformations) {
			var deselectedElementValues=this._hiddenAdditionalElementValues;

			for(var i=0;i<deselectedElementValues.length;) {
				var deselectedElementValue=deselectedElementValues[i];
				
				var found=false;
				for(var j=0;j<l.length;j++) {
					if (deselectedElementValue!=l[j]) {
						continue;
					}
					
					// On le retire de la deadditional !
					found=true;
					l.splice(j, 1);
					break;
				}
					
				if (!found) {
					i++;
					continue;
				}
				
				// On le retire de la liste des "déadditionalnés" et on le readditionalne !
				var element=elementByValue[deselectedElementValue];
				if (element) {
					this._showAdditionalElement(element, deselectedElementValue, show);
					show=false;
					continue;
				}

				deselectedElementValues.splice(i, 1);
			}			
		}
		
		for(var i=0;i<l.length;i++) {
			var value=l[i];
			var element=elementByValue[value];
			if (!element) {
				// La valeur n'est pas affichée !
				
				selectedElementValues.push(value);
				continue;
			}
			
			this._showAdditionalElement(element, value, show);
			show=false;
		}		
	},
	
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_performElementAdditionalInformation: function(element, show, evt, additional) {
		var cardinality=this._additionalInformationCardinality;
		if (!cardinality) {
			return false;
		}
		
		f_core.Debug(fa_additionalInformationManager, "fa_performElementAdditionalInformation: "+
			" additional='"+additional+"'"+
			" disabled="+this.fa_isElementDisabled(element));
	
		if (this.fa_isElementDisabled(element)) {
			return false;
		}
				
		var elementSelected=this.fa_isAdditionalElementVisible(element);
		var elementValue=this.fa_getElementValue(element);
		
		switch(cardinality) {
		case fa_cardinality.ONE_CARDINALITY:
			if (elementSelected) {
				return false;
			}
			
			// On continue ....
			
		case fa_cardinality.OPTIONAL_CARDINALITY:			
			// On décoche tout: 1 seul doit rester selectionner 
			this._hideAllAdditionalElements();
				
			if (additional) {
				this._showAdditionalElement(element, elementValue, show);
			}
			break;
			
		case fa_cardinality.ONEMANY_CARDINALITY:
			if (elementSelected) {
				if (this._currentAdditionalInformations.length<2) {
					// Un seul décoché: on arrete tout !
					return false;
				}
			}

			// On continue ...

		case fa_cardinality.ZEROMANY_CARDINALITY:
			if (elementSelected) {
				this._hideAdditionalElement(element, elementValue);
				break;
			}

			this._showAdditionalElement(element, elementValue, show);
			break;
		}
	
		var detail=0;
		if (additional) {
			detail|=1;
		}

		var item=this.fa_getElementItem(element);
		
		this.fa_fireAdditionalInformationChangedEvent(evt, detail, item, elementValue);
		
		return true;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_setAdditionalInformationStates: function(additionalFullState) {
		this._additionalFullState=additionalFullState;
	},
	/**
	 * @method public
	 * @return Object[] An array of selected values.
	 */
	f_getAdditionalInformationValues: function() {
		var ret=new Array;
		if (!this._additionalInformations) {
			return ret;
		}
		
		if (this._clientAdditionalInformationFullState) {
			if (!this._hideAllAdditionalInformations) {
				var additionalFullState=this._additionalFullState;
				if (additionalFullState && additionalFullState.length) {
					ret.push.apply(ret, additionalFullState);
				}
			}	
			
			var selectedElementValues=this._shownAdditionalElementValues;
			if (selectedElementValues.length) {
				ret.f_addElements.apply(ret, selectedElementValues);
			}

			var deselectedElementValues=this._hiddenAdditionalElementValues;
			if (deselectedElementValues.length) {
				ret.f_removeElements.apply(ret, deselectedElementValues);
			}
			
			return ret;
		}
		
		// Nous ne sommes pas en fullstate, on ne renvoit que ce que l'on voit !
		var currentSelection=this._currentAdditionalInformations;
		for(var i=0;i<currentSelection.length;i++) {
			var element=currentSelection[i];
			
			var value=this.fa_getElementValue(element);
			if (value===undefined) {
				continue;
			}

			ret.push(value);
		}
		
		return ret;
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_isAdditionalElementValueVisible: function(value, defaultValue) {
		var selected=defaultValue;
		
		var additionalFullState=this._additionalFullState;
		if (!selected && additionalFullState) {
			selected=additionalFullState.f_contains(value);
		}
	
		if (selected && !this._hideAllAdditionalInformations) {
			// On recherche s'il n'a pas été deadditionalné !
			if (this._hiddenAdditionalElementValues.f_contains(value)) {
				// Il a été deadditionalné !
				return false;
			}
		
			// Il n'a pas été deadditionalné !
			return true;
		}
		
		// Tout a été deadditionalné, ou c'etait pas sélectionné à la création du composant!
		
		return this._shownAdditionalElementValues.f_contains(value);
	},
	/**
	 * @method public
	 * @param Object[] additional The new additional.
	 * @param optional Boolean show Show the first new shown element.
	 * @return void
	 */
	f_expandAdditionalInformations: function(additional, show) {
		if (additional && typeof(additional)=="object" && (additional instanceof Array)==false) {
			additional=[additional];
		}
		
		f_core.Assert(additional===undefined || (additional instanceof Array), "fa_additionalInformationManager.f_showAdditional: Invalid additional parameter ("+additional+")");
		f_core.Assert(show===undefined || typeof(show)=="boolean", "fa_additionalInformationManager.f_showAdditional: Invalid show parameter ("+show+")");
		
		f_core.Debug(fa_additionalInformationManager, "f_showAdditional: Set additional to '"+additional+"' show='"+show+"'.");
		
		if (!additional || !additional.length) {
			this._hideAllAdditionalElements();
			this.fa_fireAdditionalInformationChangedEvent();
			return;
		}
		
		this._showAdditionalElementsRange(additional, show);
		
		this.fa_fireAdditionalInformationChangedEvent();
	},
	/**
	 * @method protected
	 */
	fa_fireAdditionalInformationChangedEvent: function(evt, detail, item, elementValue) {
		
		this.f_fireEvent(f_event.ADDITIONAL_INFORMATION, evt, item, elementValue, this, detail);
	},

	/**
	 * @method protected abstract
	 */
	fa_isAdditionalElementVisible: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 */
	fa_setAdditionalElementVisible: f_class.ABSTRACT
};

new f_aspect("fa_additionalInformationManager", {
	extend: [ fa_itemsManager, fa_clientFullState ],
	members: __members 
});
