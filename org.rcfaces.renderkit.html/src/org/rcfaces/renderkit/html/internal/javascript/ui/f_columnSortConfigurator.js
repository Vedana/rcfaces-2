/*
 * $Id: f_columnSortConfigurator.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p>
 *
 * @class public final f_columnSortConfigurator extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */
var __statics = {
	
	/**
	 * @field private static final Number
	 */
	_DEFAULT_MAX_COLUMN_NUMBER: 3,
	
     /**
     * @method private static
     * @param Event evt the event
     * @return Boolean
     * @context object:base
     */
    _OnClick: function(evt) {
    	var button=this;
		var base=button._base;
		
		f_core.Debug(f_columnSortConfigurator, "_OnClick: entering");

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (base.f_getEventLocked(evt, true)) {
			f_core.Debug(f_columnSortConfigurator, "_OnClick : popup.f_getEventLocked(true)");
			return false;
		}
		
		f_core.Debug(f_columnSortConfigurator, "_OnClick: before popup.f_buttonOnClick(button);");
		
		base.f_buttonOnClick(button, evt, button._close, button._apply);		

		return f_core.CancelJsEvent(evt);
    },
    
     /**
     * @method private static
     * @param Event evt the event
     * @return Boolean
     * @context object:base
     */
    _SelectOnChange: function(evt) {
    	var selectedSelect=this;
		var base=selectedSelect._base;
		
//		var docBase = selectedSelect.ownerDocument;
//		var number = selectedSelect._number;
		
		f_core.Debug(f_columnSortConfigurator, "_SelectOnChange: entering");

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		var grid=base._grid;
		var cols = f_columnSortConfigurator.GetColumns(grid);		
		
		var selects=base._selects;
//		var radios=base._radios;
		for(var i=0;i<selects.length;i++) {
			var select=selects[i];
			
			var selection=select.selectedIndex;
			
			if (selection==0) {
				select._sort=undefined;
				// Plus de selection !
				for(i++;i<selects.length;i++) {
					// Deselectionne le reste ...
					selects[i].selectedIndex=0;
					selects[i]._sort=undefined;
				}
				break;
			}

			var colIndex=select.options[selection]._columnIndex;

			// On verifie qu'il n'y a pas de doublons			
			for(var j=0;j<i;j++) {
				var s=selects[j];
				var sc=s.options[s.selectedIndex]._columnIndex;
				
				if (sc!=colIndex) {
					continue;
				}
				
				// Conflit, on reinitialise tout !
								
				for(i=j+1;i<selects.length;i++) {
					selects[i].selectedIndex=0;
					selects[i]._sort=undefined;
				}
				break;
			}
		}
		
		if (selectedSelect.selectedIndex>0 && !selectedSelect._sort) {
			selectedSelect._sort=1;
		}

		base.f_updateRadioButtons(0);

		for(var i=1;i<selects.length;i++) { // On debute a la deuxieme combo !
			// On remplis les selects en essayant de conserver la selection
				
			var select=selects[i];
			
			select.disabled=(selects[i-1].selectedIndex==0);
				
			var colIndex=-1;
			var selection=select.selectedIndex;
			if (selection) {
				colIndex=select.options[selection]._columnIndex;
			}
			
			for(;select.firstChild;) {
				select.removeChild(select.firstChild);
			}
			
			// Remplissage
			base.f_addOptions(select, cols, colIndex);
		}
		
		return true;
    },
	/**
	 *  <p>get the columns.</p>
     * @method public static
     * @param f_grid grid
     * @return Object[] array of visible columns
	 */
	GetColumns: function(grid) {
		if (!grid) {
			f_core.Error(f_columnSortConfigurator, "GetColumns: grid is undefined !");
			return [];
		}
		
		var allCols = grid.f_getColumns();
		
		var visibleCols = new Array;
		for (var i=0; i<allCols.length; i++) {
			var col = allCols[i];
			f_core.Debug(f_columnSortConfigurator, "GetColumns: col "+col.f_getId()+" visibility :" +col.f_isVisible()+" sortability :" +col.f_isSortable());
			if (col.f_isVisible() && col.f_isSortable()) {
				visibleCols.push(col);
			}
		}
		return visibleCols;
	}
};

var __members = {
	
	/**
	 * @field private fa_eventTarget
	 */
	_eventTarget: undefined,
	
	/**
	 * @field private f_grid
	 */
	_grid: undefined,
	
	/**
	 * @field private Number
	 */
	_maxColumnNumber: undefined,

	/**
	 * @method
	 * @param fa_eventTarget eventTarget
	 * @param f_grid grid
	 * @param optional Number maxColumnNumber
	 */
	f_columnSortConfigurator: function(eventTarget, grid, maxColumnNumber) {
	
		f_core.Assert(grid, "f_columnSortConfigurator.f_columnSortConfigurator: Invalid eventTarget parameter ("+eventTarget+")");
		this._eventTarget=eventTarget;
	
		f_core.Assert(grid, "f_columnSortConfigurator.f_columnSortConfigurator: Invalid grid parameter ("+grid+")");
		this._grid=grid;

		this._radios = new Array;
		this._selects = new Array;
		this._buttons = new Array;

		this._maxColumnNumber=(maxColumnNumber>0)?maxColumnNumber:f_columnSortConfigurator._DEFAULT_MAX_COLUMN_NUMBER;
	},
	
	/**
	 * <p>Destruct a  <code>f_columnSortConfigurator</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {		
		// this._title=undefined; // String
		// this._maxColumnNumber=undefined; // number
		
		this._eventTarget=undefined; // fa_eventTarget
		
		this._grid=undefined; // f_grid

		this.f_cleanInputs();
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @param Document doc
	 * @param String[] selects
	 * @param String[] radios
	 * @param String okButton
	 * @param optional String cancelButton
	 * @param optional String applyButton
	 * @return void 
	 */
	f_fillBodyByClientIds: function(doc, selects, radios, okButton, cancelButton, applyButton) {
		var selects2=new Array;
		var radios2=new Array;
		
		for(var i=0;i<selects.length;i++) {
			if (typeof(selects[i])!="string") {
				selects2.push(selects[i]);
				continue;
			}
			
			var s=f_core.GetElementByClientId(selects[i], doc);
			
			selects2.push(s);
		}
		
		for(var i=0;i<radios.length;i++) {
			if (typeof(radios[i])!="string") {
				radios2.push(radios[i]);
				continue;
			}
			
			var r=f_core.GetElementByClientId(radios[i], doc);
			
			radios2.push(r);
		}
		
		if (typeof(okButton)=="string") {
			okButton=f_core.GetElementByClientId(okButton, doc);
		}
		
		if (typeof(cancelButton)=="string") {
			cancelButton=f_core.GetElementByClientId(cancelButton, doc);
		}
		
		if (typeof(applyButton)=="string") {
			applyButtonf_core.GetElementByClientId(applyButton, doc);
		}
		
		this.f_fillBody(selects2, radios2, okButton, cancelButton, applyButton);
	},
	
	/**
	 *  <p>draw a message box.
	 *  </p>
	 *
	 * @method public 
	 * @param HtmlSelectElement[] 
	 * @param HtmlInputElement[] 
	 * @param HtmlInputElement 
	 * @param optional HtmlInputElement 
	 * @param optional HtmlInputElement 
	 * @return void
	 */
	f_fillBody: function(selects, radios, okButton, cancelButton, applyButton) {
     	f_core.Debug(f_columnSortConfigurator, "f_fillBody: entering");

     	var grid=this._grid;
     	
		var sortedCols = grid.f_getSortedColumns();
		
		var cols = f_columnSortConfigurator.GetColumns(grid);
		var nbCols = cols.length;
		if (nbCols > this._maxColumnNumber) {
			nbCols = this._maxColumnNumber;
		}
		
		/*
		var form=f_core.GetParentForm(selects[0]);
		if (form) {
			this._form=form;
			
			form.onsubmit=f_columnSortConfigurator._OnClick;
			form._base=this;
		}
		*/

		for(var j=0;j<nbCols;j++) {
			var selectComp=selects[j];
			if (!selectComp) {
				continue;
			}
			this._selects.push(selectComp);
			
			var selectedColIndex=-1;
			if (j < sortedCols.length) {
				var selectedCol = sortedCols[j];
				
				for (var i = 0; i < cols.length; i++) {
					if (cols[i]==selectedCol) {
						selectedColIndex=i;
						break;
					}
				}			
			}
				
			if (j>0) {
				selectComp.disabled=(this._selects[j-1].selectedIndex==0);
			}
					
			selectComp._base = this;
			selectComp.onchange = f_columnSortConfigurator._SelectOnChange;
	
			var ascRadio=radios[j*2];
			if (ascRadio) {
				ascRadio._select=selectComp;
				ascRadio.onclick = function() {
					this._select._sort = 1;
		        };
		        
		        this._radios.push(ascRadio);
			}
			
			var descRadio=radios[j*2+1];
			if (descRadio) {
				descRadio._select=selectComp;
				descRadio.onclick = function() {
					this._select._sort = -1;
		        };
		        
		        this._radios.push(descRadio);
			}
			
			// Remplissage
			this.f_addOptions(selectComp, cols, selectedColIndex);
		}
		
		for(var j=nbCols; j<this._maxColumnNumber;j++){
			var selectComp=selects[j];
			if (selectComp) {
				selectComp.disabled = true;
			}
			if(radios[j*2]) {
				this._changeButtonState(radios[j*2], false, true);
			}
			if(radios[j*2+1]) {
				this._changeButtonState(radios[j*2+1], false, true);
			}
		}
		
		var self=this;
		if (okButton) {
			if (okButton.f_addEventListener) {
				okButton.f_addEventListener(f_event.SELECTION, function(jsEvent) {
					self.f_buttonOnClick(this, jsEvent, true, true);
					
					return f_core.CancelJsEvent(jsEvent);
				});
				
			} else {
				okButton.onclick=f_columnSortConfigurator._OnClick;
				okButton._base = this;
				okButton._close = true;
				okButton._apply = true;

				//button.onfocusin=noFocus;
				this._buttons.push(okButton);
			}		
		}
		
		if (applyButton) {
			if (applyButton.f_addEventListener) {
				applyButton.f_addEventListener(f_event.SELECTION, function(jsEvent) {
					self.f_buttonOnClick(this, jsEvent, false, true);
					
					return f_core.CancelJsEvent(jsEvent);
				});
				
			} else {
				applyButton.onclick=f_columnSortConfigurator._OnClick;
				applyButton._base = this;
				applyButton._apply = true;
				this._buttons.push(applyButton);
			}
		}
		
		if (cancelButton) {
			if (cancelButton.f_addEventListener) {
				cancelButton.f_addEventListener(f_event.SELECTION, function(jsEvent) {
					self.f_buttonOnClick(this, jsEvent, true, false);
					
					return f_core.CancelJsEvent(jsEvent);
				});
				
			} else {
				cancelButton.onclick=f_columnSortConfigurator._OnClick;
				cancelButton._base = this;
				cancelButton._close = true;
				this._buttons.push(cancelButton);
			}
		}
	},
		
	/**
	 *  <p>callBack that will call the user provided callBack</p>
	 *
	 * @method protected 
	 * @param HTMLInputElement selectedButton The button that was pushed
	 * @param Event jsEvent
	 * @return void
	 */
	f_buttonOnClick: function(selectedButton, jsEvent, close, apply) {
     	f_core.Debug(f_columnSortConfigurator, "f_buttonOnClick: entering ("+selectedButton+")");

     	f_core.Debug(f_columnSortConfigurator, "f_buttonOnClick: button close="+close+", apply="+apply);
		
		var colsSorted = new Array;

		var grid = this._grid;
		if (apply) {
			var cols = f_columnSortConfigurator.GetColumns(grid);		

			var selects=this._selects;
			for (var i=0; i<selects.length; i++) {
				var select = selects[i];
				
				if (!select.selectedIndex) {
					break;
				}
				
				var sc=select.options[select.selectedIndex]._columnIndex;
				
				colsSorted.push(cols[sc], select._sort>=0);
				if (i==0) {
					colsSorted.push(false); // Append mode
				}
			}

			// Impact the grid
	     	f_core.Debug(f_columnSortConfigurator, "f_buttonOnClick: sorting "+(colsSorted.length/3)+" cols");
			
     		var delayedSort = function () {
     			if (window._rcfacesExiting) {
     				return;
     			}
     		
     			if (!colsSorted.length) {
     				grid.f_clearSort();
     				return;
     			}
     			
     			grid.f_setColumnSort.apply(grid, colsSorted);
			};	     		

	     	f_core.Debug(f_columnSortConfigurator, "f_buttonOnClick: setting timeout on "+window);
			// main window
			window.setTimeout(delayedSort, 10);
		}

		if (close) {
			var eventTarget=this._eventTarget;
			
			if (eventTarget && eventTarget.f_close)  {
				eventTarget.f_close();
			}
		}
	},
	
	/**
	 * @method public
	 * @return void
	 */
	f_cleanInputs: function() {
		/*
		var form=this._form;
		if (form) {
			this._form=undefined;
			form.onsubmit=null;
			form._base=undefined; // f_columnSortConfigurator
		}
		*/
		
		var buttons = this._buttons;
		if (buttons) {
			this._buttons=undefined;
			
			// Buttons cleaning
			for (var i=0; i<buttons.length; i++) {
				var button = buttons[i];
				button._base=undefined; // f_columnSortConfigurator
				// button._close=undefined; // boolean
				// button._apply=undefined; // boolean
				button.onclick=null;
				button.onfocusin=null;
				
				f_core.VerifyProperties(button);
			}
		}

		var radios = this._radios;
		if (radios) {
			this._radios=undefined;
			
			// Radios cleaning
			for (var i=0; i<radios.length; i++) {
				var radio = radios[i];
				radio.onclick=null;
				radio._select=undefined; // HtmlSelectElement
				
				//f_core.VerifyProperties(radio);
			}
		}	
		
		var selects = this._selects;
		if (selects) {
			this._selects=undefined;
			
			// Selects cleaning
			// & Get the informations !!!
			for (var i=0; i<selects.length; i++) {
				var select = selects[i];
				select._base=undefined; // f_columnSortConfigurator
				select.onchange=null;
				select.onfocusin=null;
				
				//f_core.VerifyProperties(select);
			}
		}
	},
    /**
     * @method private 
     * @return void
     */
	f_addOptions: function(select, cols, colIndex) {
		var grid=this._grid;
		var selects=this._selects;
		
    	this.f_addOption(select); // Ajoute vide
		select.selectedIndex = 0;
		
		var i=0;
		for(;i<selects.length;i++) {
			if (selects[i]==select) {
				break;
			}
		}

		this.f_updateRadioButtons(0);
		
		var cnt=0;
		for (var j = 0; j < cols.length; j++) {				
			var found=false; // On retire les colonnes deja referencees !
			for(var k=0;k<i;k++) {
				var selection=selects[k].selectedIndex;
				if (!selection) {
					break;
				}
				
				var s=selects[k].options[selection]._columnIndex;
				if (s==j) {
					found=true;
					break;
				}
			}
			
			if (found) {
				continue;
			}
			
			this.f_addOption(select, cols[j], j);
			
			if (colIndex==j) {
				if (!select._sort) {
					select._sort = grid.f_getColumnOrderState(cols[j]);
				}
				select.selectedIndex = cnt+1;
			}

			cnt++;	
		}
		if (!select._sort) {
			//select._sort = 1;
		}
			
		this.f_updateRadioButtons(i);
	},
	/**
	 * @method private
	 * @param Number i
	 * @return void
	 */
	f_updateRadioButtons: function(i) {		
		var selects=this._selects;
		var radios=this._radios;
		
		if (i && selects[i-1].selectedIndex==0) {
			// Le precedant n'est pas trié !
			i*=2;
			
			this._changeButtonState(radios[i], false, true);
			this._changeButtonState(radios[i+1], false, true);
			return;
		}


		var sort=selects[i]._sort;

		i*=2;
		
		this._changeButtonState(radios[i], (sort>0), false);
		this._changeButtonState(radios[i+1], (sort<0), false);
	},
	_changeButtonState: function(but, checked, disabled) {
		if (but.f_setSelected) {
			but.f_setSelected(checked);
		} else {
			but.checked=checked;
		}
		
		if (but.f_setDisabled) {
			but.f_setDisabled(disabled);
		} else {
			but.disabled=disabled;
		}
	},
    /**
     * @method hidden
     * @param HTMLElement selectComp Select
     * @param Object column
     * @return HTMLOptionElement
     */
    f_addOption: function(selectComp, column, columnIndex) {
        var newOpt = selectComp.ownerDocument.createElement("option");
        var text;

        if (column) {
	        text = column.f_getGrid().f_getColumnName(column);
	        if (!text) {
	        	text = column.f_getId();
	        }

	    } else {
	    	text = f_resourceBundle.Get(f_columnSortConfigurator).f_get("NO_COLUMN");
	    	
	    	newOpt.className="f_columnSortConfigurator_no_column";
	    }
	    
        newOpt.value = text;
        f_core.AppendChild(newOpt, selectComp.ownerDocument.createTextNode(text));
        newOpt._columnIndex = columnIndex;
        
        selectComp.appendChild(newOpt);
        
        return newOpt;
  	},
	f_getEventLocked: function(evt, showAlert, mask) {
  		return this._eventTarget.f_getEventLocked(evt, showAlert, mask);
  	}
};

new f_class("f_columnSortConfigurator", {
	extend: f_object,
	members: __members,
	statics: __statics
});
