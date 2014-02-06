/*
 * $Id: f_dataGrid.js,v 1.8 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * 
 * @class public f_dataGrid extends f_grid, fa_readOnly, fa_checkManager, fa_droppable, fa_draggable, fa_autoOpen
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.8 $ $Date: 2013/12/11 10:19:48 $
 */

var __statics = {
	
	/**
	 * @field private static final Number
	 */
	_SEARCH_KEY_DELAY: 400,
	
	/**
	 * @method private static
	 */
	_CheckMouseButtons: f_core.CancelJsEventHandler,
	
	/**
	 * @field private static Number
	 */
	_CellIdx: 0,

	/**
	 * @method private static
	 * @context object:dataGrid
	 */
	_Ie_CheckMouseDown: function(evt) {
		var row=this._row;
		var dataGrid=row._dataGrid;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}

		if (dataGrid.f_isReadOnly()) {
			return false;
		}
		
		if (row!=dataGrid._cursor) {
			dataGrid.f_moveCursor(row, true, evt);
		}

		// Il faut bloquer le bubble !
		evt.cancelBubble = true;
		return false;
	},
	
	/**
	 * @method private static
	 * @context event:evt
	 */
	_ReturnFalse: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (!f_grid.VerifyTarget(evt)) {
			return true;
		}
	
		if (f_core.IsPopupButton(evt)) {
			return f_core.CancelJsEvent(evt);
		}
		
		return true;
	},
	
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_CheckSelect: function(evt) {
		var row=this._row;
		var dataGrid=row._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		f_core.Debug(f_dataGrid, "_CheckSelect: perform event "+evt);
		
		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}

		// Il faut bloquer le bubble !
		evt.cancelBubble = true;

		if (dataGrid.f_isReadOnly()) {
			return false;
		}
		
		if (row!=dataGrid._cursor) {
			dataGrid.f_moveCursor(row, true, evt);
		}
		
		var checked;
		if (this.type=="radio") {
			checked=true;
			
		} else {
			checked=!dataGrid.fa_isElementChecked(row);
		}
	
		if (!dataGrid.fa_performElementCheck(row, true, evt, checked)) {
			return f_core.CancelJsEvent(evt);
		}
		
		if (f_core.IsGecko()) {
			if (dataGrid.fa_isElementChecked(row)!=checked) {
				return false;
			}
		}
		 		 
		return true;
	},
		
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:dataGrid
	 */
	_AdditionalInformationSelect: function(evt) {
		var row=this._row;
		var dataGrid=row._dataGrid;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		if (dataGrid.f_getEventLocked(evt)) {
			return false;
		}
		
		if (row!=dataGrid._cursor) {
			dataGrid.f_moveCursor(row, true, evt);
		}
	
		if (dataGrid.f_hasAdditionalElement(row)) {
			var show=!dataGrid.fa_isAdditionalElementVisible(row);
				
			dataGrid.fa_performElementAdditionalInformation(row, true, evt, show);
		}
				
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_Alpha: function(text1, text2) {
		text1=(text1)?text1:"";
		text2=(text2)?text2:"";
		
		if (text1 == text2) {
			return 0;
		}
		return (text1 > text2)? 1:-1;
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_AlphaIgnoreCase: function(text1, text2) {
		text1=(text1)?text1.toLowerCase():"";
		text2=(text2)?text2.toLowerCase():"";
		
		if (text1 == text2) {
			return 0;
		}
		return (text1 > text2)? 1:-1;
	},
	/**
	 * @method private static
	 * @param String text
	 * @return String
	 */
	_NormalizeInteger: function(text) {
		return text.replace(/[^\d]/g, '');
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_Integer: function(text1, text2) {
		var t1=f_dataGrid._NormalizeInteger(text1);
		var t2=f_dataGrid._NormalizeInteger(text2);

		var val1 = parseInt(t1, 10);
		var val2 = parseInt(t2, 10);
		if (val1 == val2) {
			return 0;
		}
		return (val1 > val2)? 1:-1;
	},
	/**
	 * @method private static
	 * @param String text
	 * @return String
	 */
	_NormalizeNumber: function(text) {
		if (text.indexOf('.')>=0) {
			return text.replace(/[^\d\.]/g, '');
		}

		text=text.replace(/[^\d\,]/g,'').replace(',', '.');
		
		return text;
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_Number: function(text1, text2) {
		var t1=f_dataGrid._NormalizeNumber(text1);
		var t2=f_dataGrid._NormalizeNumber(text2);
		
		var val1 = parseFloat(t1);
		var val2 = parseFloat(t2);
		if (val1 == val2) {
			return 0;
		}
		return (val1 > val2)? 1:-1;
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_Date: function(text1, text2) {
		if (text1=="") {
			return -1;
			
		} else if (text2=="") {
			return 1;
		}
		var val1 = text1.split("/");
		var val2 = text2.split("/");
		if (val1.length!=val2.length) {
			return val1.length-val2.length;
		}
		
		for(var i=val1.length-1;i>=0;i--) {
			if (val1[i]==val2[i]) {
				continue;
			}
			
			return (val1[i] > val2[i])? 1:-1;
		}
				
		return 0;
	},
	/**
	 * @method hidden static
	 * @param String text1
	 * @param String text2
	 * @return Number
	 */
	Sort_Time: function(text1, text2) {
		if (text1=="") {
			return -1;
			
		} else if (text2=="") {
			return 1;
		}
		var val1 = text1.split(":");
		var val2 = text2.split(":");
		if (val1.length!=val2.length) {
			return val1.length-val2.length;
		}
		
		for(var i=0;i<val1.length;i++) {
			if (val1[i]==val2[i]) {
				continue;
			}
			
			return (val1[i] > val2[i])? 1:-1;
		}
				
		return 0;
	}
};
 
var __members = {
	
	/**
	 * @method 
	 * @param hidden HTMLElement parent
	 */
	f_dataGrid: function() {
		this.f_super(arguments);

		this._showCursor=true;
		this._cellStyleClass="f_dataGrid_cell";
		this._rowStyleClass="f_dataGrid_row";
		this._gridUpdadeServiceId="dataGrid.update";
		this._gridUpdadeCriteriaServiceId="criteria.request";
		this._serviceGridId=this.id;
		this._keyRowSearch=true;
		this._countToken =0;
		this._cellWrap=f_core.GetAttributeNS(this,"cellTextWrap", false);
		this._cellFocusable=true;
		//this._noCellWrap=false;

		if (!!this._cellWrap) {
			// this.className+=" f_dataGrid_noWrap";
		}
		
		if (window.f_indexedDbEngine) {
			this._indexDb=f_indexedDbEngine.FromComponent(this);
		}
	},
	f_finalize: function() {
	 
		this._addRowFragment=undefined; // HtmlDocumentFragment
		
		this._criteriaEvaluateCallBacks= undefined; // Object
		
		var indexDb=this._indexDb;
		if (indexDb) {
			this._indexDb=undefined; // f_indexedData
			
			f_classLoader.Destroy(indexDb);
		}
	
		// this._labelColumnId=undefined; // String
		// this._gridUpdadeServiceId=undefined; // String
		// this._serviceGridId=undefined; // String
		
		//this._countToken=undefided; // Integer
		//		this._lastKeyDate=undefined; // number
		//		this._lastKey=undefined; // char

		this.f_super(arguments);
	},
	/*
	f_serialize : function() { 
		return this.f_super(arguments);
	},
	*/
	
	/**
	 * @method protected
	 */
	fa_updateElementStyle: function(row, updateCells) {
		this.f_super(arguments, row, updateCells);	
		
		if (updateCells!==false) {
			var input=row._checkbox;
			var checked=row._checked;
			if (input && checked!==input._checked) {
				input.checked=checked;
				input._checked=checked;
				
				row._input.setAttribute("aria-checked", checked);
				
				var rb=f_resourceBundle.Get(f_grid);
				input.title = rb.f_formatParams((checked)?"UNCHECK_TITLE":"CHECK_TITLE", {
					value: row._lineHeader
				});
				
				var checkRowMessage=rb.f_get((checked)?"CHECKED_ROW":"CHECKABLE_ROW");			
				fa_audioDescription.SetAudioDescription(row._input._label, checkRowMessage, "check", row._input._label.id);
				
				if (f_core.IsInternetExplorer()) {
					// Il se peut que le composant ne soit jamais affiché 
					// auquel cas il faut utiliser le defaultChecked !
					input.defaultChecked=checked;
				}
			}
		} 
	},
	/**
	 * 
	 * @method public
	 * @param any value The value of the new row
	 * @param String... columnValue1 A parameter for each column 
	 * @return Object
	 */
	f_addRow: function(value, columnValue1) {
		//f_core.Assert(this._rows==0, "All rows of the DataGrid must be loaded (attribute rows=0)");
		
		var properties=new Object;
		
		var l=[value, properties];
		
		f_core.PushArguments(l, arguments, 1);
		
		var ret=this.f_addRow2.apply(this, l);
		if (!ret) {
			return ret;
		}
		
		if (this._rowCount>=0) {
			this._rowCount++;
		}

		if (ret>0) {
			this.f_performPagedComponentInitialized();
		}
			
		return ret;
	},
	/**
	 *  @method hidden
	 *  @return void
	 */
	f_preAddRow2: function() {
		var doc=this.ownerDocument;
		
		this._addRowFragment= doc.createDocumentFragment();
	},
	/**
	 *  @method hidden
	 *  @return void
	 */
	f_postAddRow2: function() {
		var fragment=this._addRowFragment;
		if (!fragment) {
			return;
		}
		
		this._addRowFragment=undefined;
		
		f_core.AppendChild(this._tbody, fragment);
	
		var rows=this._rowsPool;
		var columns=this._columns;
		if (this._indexDb && rows && rows.length) {
			this._indexDb.f_asyncFillRows(rows, function(row) {
				var obj = {
					value: row._index,
					rowIndex: row._rowIndex
				};
				
				if (row._className) {
					obj.className=row._className;
				}
				if (row._toolTipId) {
					obj.toolTipId = row._toolTipId;
				}
				if (row._toolTipContent) {
					obj.toolTipContent = row._toolTipContent;
				} 
				
				var columnsValue={};
				obj.columns=columnsValue;
				
				var index=0;
				var cells=row._cells;
				for(var i=0;i<columns.length;i++) {
					var col=columns[i];

					if (col._visibility===null) { /* Hidden coté serveur ! */
						continue;
					}

					var objc={};
					var colId=col._id;
					if (colId===undefined) {
						colId=index;
					}
					columnsValue[colId]=objc;

					var cell=cells[index];

					var text=cell._text;
					if (text!==null && text!==undefined) {
						objc.text=text;
					}				
					if (cell._cellStyleClass) {
						objc.cellStyleClass=cell._cellStyleClass;
					}
					if (cell.title) {
						objc.tooltipText=cell.title;
					}
					if (cell._toolTipId) {
						objc.toolTipId=cell._toolTipId;
					}
					if (cell._toolTipContent) {
						objc.toolTipContent=cell._toolTipContent;
					}
					if (cell._imageURL) {
						objc.imageURL=cell._imageURL;
					}		
					
					index++;
				}
				
				return obj;
			});
		}

	},
	/**
	 * @method hidden
	 */
	f_addRow2: function() {
		f_core.Assert(this._tbody, "f_dataGrid.f_addRow2: No table body !");
		
		var doc=this.ownerDocument;
		var fragment=this._addRowFragment;
		
		var row;
		var firstCell=true;
		var shadowRows=this._shadowRows;
		if (shadowRows && shadowRows.length) {
			row=shadowRows.shift();
			firstCell=row.firstChild;
			
			while (firstCell.hasChildNodes()) {
				firstCell.removeChild(firstCell.lastChild);
			}
			
			f_core.Assert(row.tagName.toLowerCase()=="tr", "f_dataGrid.f_addRow2: Invalid row ! "+row);
			
			if (fragment) {
				row.parentNode.removeChild(row);
				
				f_core.AppendChild(fragment, row);
			}	
			
			
		} else {
			row=doc.createElement("tr");
			if (fragment) {
				f_core.AppendChild(fragment, row);
			} else {
				f_core.AppendChild(this._tbody, row);
			}
		}
		this._rowsPool.push(row);
		row._dataGrid=this;
		
		var rowIdx=this._rowsPool.length;
		
		var idx=0;
		row._index=arguments[idx++];
		row.id=this.id+"::row"+rowIdx;
		var rowHeaderClientId=this.id+"::rh"+rowIdx;
		
		if(this._useInPopup){ // pour aria a revoir
			row.setAttribute("role", "option");
		} else {
			row.setAttribute("role", "row");
		}
		
		if (f_core.IsInternetExplorer()) {
			row.tabIndex=-1; // Pas sous FF car le TR devient focusable
		}
		
		if (this.f_isSelectable() || this.f_isCheckable() || this._additionalInformations) {
			row.onmousedown=f_grid.RowMouseDown;
			row.onmouseup=f_grid.RowMouseUp;
			row.onclick=f_core.CancelJsEventHandler;
			row.ondblclick=f_grid.RowMouseDblClick;
			row.onfocus=f_grid.GotFocus;
		}
		
		var properties=arguments[idx++];
		
		var className=null;
		
		if (properties) {
			className=properties._styleClass;
			row._rowIndex=properties._rowIndex;
		}
		if (!className) {
			className=this._rowStyleClasses[rowIdx % this._rowStyleClasses.length];
		}
		row.className=className;
		row._className=className;
		
		var cellClassName=this._cellStyleClass;
		var selected=false;
		if (this.f_isSelectable()) {
			row._selected=false;			
			
			if (!this._selectionFullState && properties) {
				selected=properties._selected;
			}
			
			selected=this.f_updateElementSelection(row, selected);
			
			if (selected) {
				cellClassName+=" f_grid_cell_selected";
			}
		}
		
		var checked=undefined;
		if (this.f_isCheckable()) {	
			if (!this._checkFullState && properties) {
				checked=properties._checked;
			}
		}
		
		var additional=undefined;
		if (this._additionalInformations && properties) {
			row._additionalContent=properties._additionalContent;
			row._additionalHeight=properties._additionalHeight;
			
			if (!this._additionalFullState) {
				additional=properties._additional;
			}
		}
		
		if (row._additionalContent) {
			this.f_addSerializedIndexes(row._rowIndex, 1);
		}
		
		var initCursorValue=this._initCursorValue;
		if (!this._cursor && row._index==initCursorValue) {
			this._cursor=row;
			this._initCursorValue=undefined;
		}
		
		var cells=new Array;
		row._cells=cells;
		var countTd=0;
		var rowValueColumnIndex=this._rowValueColumnIndex;
		var columns=this._columns;
		var cellWrap=this._cellWrap;
		var cellFocusable=this._cellFocusable;

		var rb=f_resourceBundle.Get(f_dataGrid);
		
		var clickableCellMessage= rb.f_get("CLICKABLE_CELL");
		var tooltipCellMessage=rb.f_get("TOOLTIP_CELL");

		// Accessibility : Get line header cell value
		var idxBak = idx;
		for (var i=0; i < columns.length;i++) {
			var col=columns[i];
					
			if (col._visibility != null && i !=rowValueColumnIndex) {
				var cellText=arguments[idx++];
				if (col._scopeCol) {
					row._lineHeader = cellText;
				}
				
			} else if (col._scopeCol) {
				row._lineHeader = row._index;
			}

		}
		idx = idxBak;
		
		for(var i=0;i<columns.length;i++) {
			var col=columns[i];

			var td;
			if (col._visibility===null) {
				td=null;
				
			} else {
				var cellText;
				
				if (i===rowValueColumnIndex) {
					cellText=row._index;
					
				} else {
					cellText=arguments[idx++];
				}
		
				if (col._visibility) {
					if (f_grid._GENERATE_HEADERS_ATTRIBUTE && !col._headersId) {
						col._headersId=this.id+"::ch"+i;
					}
					var cClassName=[cellClassName];					
					
					var colStyleClasses=col._cellStyleClasses;		
					if (colStyleClasses) {
						var csc=colStyleClasses[rowIdx % colStyleClasses.length];
						if (csc) {
							var cs=csc.split(" ");
							for(var j=0;j<cs.length;j++) {
								cClassName.push(" ", cs[j]);
								if (selected) {
									cClassName.push("_selected");
								}
							}
						}
					}
					
					var cellType=(!f_grid._GENERATE_HEADERS_ATTRIBUTE && col._scopeCol)?"th":"td";
					if (firstCell) {
						if (firstCell===true) {
							td=doc.createElement(cellType);
							f_core.AppendChild(row, td);
						} else {
							td=firstCell;
							td.colSpan=1; // pour le shadow
							td.className="";
						}
						firstCell=undefined;
						
						cClassName.push(" f_grid_cell_left");
						if (row._hasCursor && this._focus && this._showCursor) {
							cClassName.push(" f_grid_cell_cursor");
						}
						
					} else {
						td=doc.createElement(cellType);
						f_core.AppendChild(row, td);								
					}
					
					this._cellsPool.push(td);
					
					td.setAttribute("role", "gridcell");
					td.setAttribute("aria-labelledby", this.id+"::ch"+col._index+" cellIdx"+(f_dataGrid._CellIdx));
					td.valign="top";
					if (!cellWrap) {
						td.noWrap=true;
					}
					
					cClassName.push(" f_grid_cell_align_"+col._align);
					td.className=cClassName.join("");

					td._text=cellText;
					td.onbeforeactivate=f_core.CancelJsEventHandler;
					
					if (f_grid._GENERATE_HEADERS_ATTRIBUTE) {
						td.headers=rowHeaderClientId+" "+col._headersId;
						if (col._scopeCol) {
							td.id=rowHeaderClientId;
						}
					} else {
						if (col._scopeCol) {
							td.scope="row";
						}						
					}

					if (this.f_isSelectable()) {
//						td.onmouseup=f_dataGrid._ReturnFalse;
//						td.onmousedown=f_dataGrid._ReturnFalse;
//						td.ondblclick=f_core.CancelJsEventHandler;
						td.onclick=f_dataGrid._ReturnFalse;
						
						td._dataGrid=this;
					//	td.onfocus=f_grid.GotFocus; // OO: A VOIR 
					}
					
					var ctrlContainer=td;
					if (!countTd) {
						if ((this._additionalInformations || this.f_isCheckable()) && f_core.IsInternetExplorer()) {
							ctrlContainer=doc.createElement("div");
							if (!cellWrap) {
								ctrlContainer.noWrap=true;
							}
							f_core.AppendChild(td, ctrlContainer);
						}
						
						if (this._additionalInformations) {
							var button=doc.createElement("img");
							button.width=f_grid.IMAGE_WIDTH;
							button.height=f_grid.IMAGE_HEIGHT;
							button.src=this._blankImageURL;
							button._row=row;
							button.onclick=f_dataGrid._AdditionalInformationSelect;
							button.onfocus=f_grid.GotFocus;
							button.tabIndex=-1;
							
							var cn="f_grid_additional_button";
							
							if (this._additionnalOpenImageURL===undefined) {								
								this._additionnalOpenImageURL=f_core.GetAttributeNS(this, "addOpenImageURL", undefined);
								if (this._additionnalOpenImageURL) {
									this._additionnalCloseImageURL=f_core.GetAttributeNS(this, "addCloseImageURL");
									
									this._tbody.className+=" f_grid_additional_hasImage";
								}
							}
							button.className=cn;
							
							row._additionalButton=button;

							f_core.AppendChild(ctrlContainer, button);
						}
						
						if (this.f_isCheckable()) {
							
							var input=doc.createElement("input");
							row._checkbox=input;
							
							input.id=this.id+"::"+rowIdx;
							
							if (this._checkCardinality==fa_cardinality.ONE_CARDINALITY) {
								input.type="radio";
								input.value="CHECKED_"+rowIdx;
								input.name=this.id+"::radio";
								
							} else {							
								input.type="checkbox";
								input.value="CHECKED";
								input.name=input.id;
							}
							
							input.onclick=f_dataGrid._CheckSelect;							

							input._row=row;
							input._dontSerialize=true;
							
							if (this._focusOnInput) {
								// On prend le premier INPUT
								if (!this._inputTabIndex) {
									this._inputTabIndex=input;

									// On lui donne le tabIndex du composant
									input.tabIndex=this.fa_getTabIndex();

								} else if (this._cursor==row) {
									// déjà donné ?
									if (this._inputTabIndex) {
										this._inputTabIndex.tabIndex=-1;
									}

									// On lui donne le tabIndex du composant
									input.tabIndex=this.fa_getTabIndex();
									this._inputTabIndex=input;

								} else {
									input.tabIndex=-1;
								}
								
								input.onfocus=f_grid._Link_onfocus;
								input.onblur=f_grid._Link_onblur;							
								
							} else {
								input.tabIndex=-1; // -1 car sinon pas de sortie du focus du grid
								input.onfocus=f_grid.GotFocus;								
								
							if (f_core.IsInternetExplorer()) {
								input.onmousedown=f_dataGrid._Ie_CheckMouseDown;
							} else {
								input.onmousedown=f_dataGrid._CheckMouseButtons;
							}
							input.onmouseup=f_dataGrid._CheckMouseButtons;
							}

					
							input.className="f_grid_input";
							
							if (this.f_isDisabled()) {
								input.disabled=true;
							}
							
							f_core.AppendChild(ctrlContainer, input);
							
							checked = this.fa_updateElementCheck(row, checked);
							if (checked) {
								input.checked=true;
								input.defaultChecked=true;
							}
							
							var bundleKey=(checked)?"UNCHECK_TITLE":"CHECK_TITLE";
							input.title = f_resourceBundle.Get(f_grid).f_formatParams(bundleKey, {
								value: row._lineHeader
							});
						}
					}

					if (col._cellImage || col._defaultCellImageURL) {
						var cellImage=doc.createElement("img");
						cellImage.className="f_grid_imageCell";
						cellImage.width=f_grid.IMAGE_WIDTH;
						cellImage.height=f_grid.IMAGE_HEIGHT;

						var imageURL=col._defaultCellImageURL;
						// L'image par cellule est spécifié par une méthode evaluée plus tard ...

						if (f_grid.USE_BACKGROUND_IMAGE) {	
							cellImage.src=this._blankImageURL;
							if (imageURL) {
								cellImage.style.backgroundImage="url("+imageURL+")";
							}
							
						} else {
							if (!imageURL) {
								imageURL=this._blankImageURL;
							}
							
							cellImage.src=imageURL;
						}

						cellImage.border=0;

						var cellImages=row._cellImages;
						if (!cellImages) {
							cellImages=new Array;
							row._cellImages=cellImages;
						}
						cellImages[countTd]=cellImage;

						f_core.AppendChild(ctrlContainer, cellImage);
					}
					if (col._toolTipId) {
						td._toolTipId=col._toolTipId;
						td._toolTipContent=col._toolTipContent;
					}
					
					var aType=cellFocusable || (!row._input && !countTd);
					
					var labelType="label"; //"(aType?"a":"label";					
					var labelComponent=doc.createElement(labelType);
					td._label=labelComponent;
					if (!cellText) {
						cellText=" ";
					}
					var labClassName="f_grid_label";
					if (aType) {
						//labelComponent.tabIndex="-1";
						//labelComponent.href=f_core.CreateJavaScriptVoid0();
						//labelComponent.setAttribute("aria-haspopup", true);
						//labelComponent._row=row;
						labelComponent.id= "cellIdx"+(f_dataGrid._CellIdx++);
						labClassName+=" f_grid_cellLink";
						
						
						td.onfocus=f_grid._Link_onfocus;
						td.onblur=f_grid._Link_onblur;					

						ctrlContainer._input=td;
							
						if (!row._input) {
							row._input=td;
							
							if (!this._inputTabIndex) {
								this._inputTabIndex=td;
								if (cellFocusable) {
									//this._cursorCellIdx=0;
								}
							
								// On lui donne le tabIndex du composant
								td.tabIndex=this.fa_getTabIndex();
							
							} else if (this._cursor==row) {
								// déjà donné ?
								if (this._inputTabIndex) {
									this._inputTabIndex.tabIndex=-1;
								}
							
								// On lui donne le tabIndex du composant
								td.tabIndex=this.fa_getTabIndex();
								this._inputTabIndex=td;
								
							} else {
								td.tabIndex=-1;
							}
						} else {
							td.tabIndex=-1;
						}
					}
					
					f_core.AppendChild(labelComponent, doc.createTextNode(cellText));
					
					if (col._cellClickable) {
						fa_audioDescription.AppendAudioDescription(labelComponent, clickableCellMessage);
					}
					if (td._toolTipId) {
						fa_audioDescription.AppendAudioDescription(labelComponent, tooltipCellMessage);
						
						td._input.setAttribute("aria-haspopup", true);
					}
				
					labelComponent.className=labClassName;
					f_core.AppendChild(ctrlContainer, labelComponent);
					
					countTd++;
					
				} else {
					td = { _text: cellText };
				}
			}
			
			cells.push(td);
		}
		
		if (row._additionalButton) {			
			var additionalContent=row._additionalContent;
			if (typeof(additionalContent)=="string") {
				additional=true;
				
			} else if (additionalContent===false) {
				additional=false;
			}
			
			this.fa_updateElementAdditionalInformations(row, additional);
			
			if (this.fa_isAdditionalElementVisible(row)) {				
				this.f_showAdditionalContent(row);
			}
		}
		
		if (properties._toolTipId) {
			row._toolTipId = properties._toolTipId;
			row._toolTipContent = properties._toolTipContent;
		}
		
		this.fa_updateElementStyle(row, false);
		
		return row;
	},
	/**
	 * 
	 * @method public
	 * @param any... rowValue1 The value of the row to remove
	 * @return Number Number of removed rows.
	 */
	f_clear: function(rowValue1) {
		f_core.Assert(this._rows==0, "f_dataGrid.f_clear: All rows of the DataGrid must be loaded (attribute rows=0)");
		
		var ret=0;
		var tbody=this._tbody;
		var rowsPool=this._rowsPool;
		
		var selectionChanged=false;
		var checkChanged=false;
		for(var i=0;i<arguments.length;i++) {
			var rowValue=arguments[i];
			
			var row=this.f_getRowByValue(rowValue, false);
			if (!row) {
				continue;
			}
			
			if (this._deselectElement(row)) {
				selectionChanged=true;
			}
			if (this._uncheckElement(row)) {
				checkChanged=true;
			}
			
			if (row==this._cursor) {
				this._cursor=undefined;
			}
			
			this.f_getClass().f_getClassLoader().f_garbageObjects(false, row);
			
			tbody.removeChild(row);
			rowsPool.f_removeElement(row);
		
			this.f_releaseRow(row);			

			f_core.VerifyProperties(row);
			
			ret++;
			if (this._rowCount>=0) {
				this._rowCount--;
			}
		}

		if (ret<1) {
			return 0;
		}
		
		this.f_getClass().f_getClassLoader().f_completeGarbageObjects();

		this.f_performPagedComponentInitialized();

		if (selectionChanged) {
			this.fa_fireSelectionChangedEvent();
		}
		if (checkChanged) {
			this.fa_fireCheckChangedEvent();
		}
					
		return ret;
	},
	/**
	 * Returns an array of content of each cell of the specified row.
	 *
	 * @method public
	 * @param any rowValue Row value, a row object, or the index of row into the table.
	 * @return optional Boolean onlyVisible Key only visible columns.
	 * @return String[] 
	 */
	f_getRowValues: function(rowValue, onlyVisible) {
		f_core.Assert(rowValue!==undefined && rowValue!==null, "f_dataGrid.f_getRowValues: Invalid rowValue parameter ! ("+rowValue+")");
		f_core.Assert(onlyVisible===undefined || typeof(onlyVisible)=="boolean", "f_dataGrid.f_getRowValues: Invalid onlyVisible parameter ! ("+onlyVisible+")");
		var row;
		
		if (rowValue._dataGrid) {
			row=rowValue;
			
		} else if (typeof(rowValue)=="number") {
			row=this.f_getRow(rowValue, true);
			
		} else {
			row=this.f_getRowByValue(rowValue, true);
		}
		
		var cells=row._cells;
		var array=new Array;
		
		array.index=row._index;
		
		var index=0;
		for(var i=0;i<this._columns.length;i++) {
			var col=this._columns[i];

			if (col._visibility===null) { // HiddenMode coté serveur !
				if (onlyVisible) {
					continue;
				}
				
				array.push(null);
				continue;
			}
	
			var cell=cells[index++];

			if (!col._visibility && onlyVisible) { // HiddenMode coté client !
				continue;
			}

			array.push(cell._text);
		}
		
		return array;
	},
	/**
	 * Returns into an object, contents of each cell of the specified row.
	 *
	 * @method public
	 * @param any rowValue Row value, a row object, or the index of the row into the table.
	 * @return optional Boolean onlyVisible Keey only visible columns.
	 * @return Object
	 */
	f_getRowValuesSet: function(rowValue, onlyVisible) {
		f_core.Assert(rowValue!==undefined && rowValue!==null, "f_dataGrid.f_getRowValuesSet: Invalid value '"+rowValue+"'.");
		f_core.Assert(onlyVisible===undefined || typeof(onlyVisible)=="boolean", "f_dataGrid.f_getRowValuesSet: Invalid onlyVisible parameter ! ("+onlyVisible+")");

		var row;
		
		if (rowValue._dataGrid) {
			row=rowValue;
			
		} else if (typeof(rowValue)=="number") {
			row=this.f_getRow(rowValue, true);
			
		} else {
			row=this.f_getRowByValue(rowValue, true);
		}
		
		var cells=row._cells;
		var set=new Object;
		
		if (row._index) {
			set.id=row._index;
		}
		
		var index=0;
		for(var i=0;i<this._columns.length;i++) {
			var col=this._columns[i];

			if (col._visibility===null) { // HiddenMode coté serveur !
				continue;
			}
	
			var cell=cells[index++];

			if (!col._id) {
				continue;
			}

			if (!col._visibility && onlyVisible) { // HiddenMode cot? client !
				continue;
			}

			set[col._id]=cell._text;
		}
		
		return set;
	},
	/**
	 * Returns the content of the cell specified by row and column.
	 *
	 * @method protected
	 * @param any rowValue Row value, row object or the index of row the into table.
	 * @param Number columnIndex Index of the column.
	 * @return Object column and cell properties
	 */
	_getCellAndColumn: function(rowValue, columnIndex) {	
		var row=this.f_getRowByValue(rowValue, true);
		
		var cells=(row && row._cells) || []; // On sécurise
		var index=0;
		
		var columns=this._columns;
		if (typeof(columnIndex)=="number") {
			for(var i=0;i<columns.length;i++) {
				var col=columns[i];
	
				if (col._visibility===null) { /* Hidden coté serveur ! */
					if (columnIndex==i) {
						return {
							column: col
						};
					}
					continue;
				}

				if (columnIndex==i) {
					return {
						cell: cells[index],
						column: col
					};
				}
		
				index++;
			}
			
			return null;
		}
		
		for(var i=0;i<columns.length;i++) {
			var col=columns[i];

			if (col._visibility===null) { /* Hidden coté serveur ! */
				if (col._id==columnIndex) {
					return {
						column: col
					};
				}
				continue;
			}

			if (col._id==columnIndex) {
				return {
					cell: cells[index],
					column: col
				};
			}
	
			index++;
		}
		
		return null;
	},	/**
	 * Returns the content of the cell specified by row and column.
	 *
	 * @method public
	 * @param any rowValue Row value, row object or the index of row the into table.
	 * @param Number columnIndex Index of the column.
	 * @return String
	 */
	f_getCellValue: function(rowValue, columnIndex) {	
		var cv=this._getCellAndColumn(rowValue, columnIndex);
		
		if (!cv || !cv.cell) {
			return null;
		}

		return cv.cell._text;
	},
	/**
	 * @method protected
	 */
	f_callServer: function(firstIndex, length, cursorIndex, selection, partialWaiting, fullUpdate) {
		var indexDb=this._indexDb;
		if (!indexDb || this._sortIndexes || this._additionalInformations || this._selectedCriteria || fullUpdate) {
			return this._ajaxCallServer(firstIndex, length, cursorIndex, selection, partialWaiting, fullUpdate);
		}
		
		var rows=-1;
		if (length>0) {
			rows=length;
		}

		var self=this;
		indexDb.f_asyncSearch(null, firstIndex, rows, function(state) {
			if (!state) {
				return self._ajaxCallServer(firstIndex, length, cursorIndex, selection, partialWaiting, fullUpdate);
		}

			if (self.f_processNextCommand()) {
				return;
		}
		
			if (self._waitingLoading) {
				if (self._waitingMode==f_grid.END_WAITING) {
					self.f_removePagedWait();
				}
		}
		
			self._indexDbResponse=true;
			try {
		
				
			} finally {
				self._indexDbResponse=undefined;
		}
		});

	},
	/**
	 * @method private
	 * @param optional Object params
	 * @return void
	 */
	_prepareNewRows: function(params, cursorIndex, selection, partialWaiting) {
		this._waitingIndex=cursorIndex;
		this._waitingSelection=selection;
		this._partialWaiting=partialWaiting;
		
		
		this.f_hideEmptyDataMessage();
		
		if (!partialWaiting) {
			var tbody=this._tbody;
			
			var scrollBody=this._scrollBody;
			if (!this._oldHeight) {
				this._oldHeight=true;
				this._oldHeightStyle=scrollBody.style.height;
				scrollBody.style.height=scrollBody.offsetHeight+"px";
			}
						
			if (tbody) {
								
				if (this._additionalInformations) { // Des AdditionalInformations à effacer ?
					f_classLoader.SerializeInputsIntoParam(params, tbody, true);
						
					params.serializedFirst=this._first;
					params.serializedRows=this._rows;
					
				
					this._additionalInformationCount=0;
			
					var serializedState=this.f_getClass().f_getClassLoader().f_garbageObjects(true, tbody);
					f_core.Debug(f_dataGrid, "f_callServer: serializedState="+serializedState);
					if (serializedState) {
						params[f_core.SERIALIZED_DATA]=serializedState;
					}
	
					f_core.Debug(f_dataGrid, "f_callServer: garbage "+(this._additionalInformationCount)+" additional information ");
				}

				this.f_releaseRows();
				this.f_releaseCells();

				if (this._waitingMode==f_grid.END_WAITING) {
					this.f_removePagedWait();
				}

				// Detache temporairement !
				if (tbody.parentNode) {
					// is the tbody already detached ?
//					if (tbody.parentNode.nodeType != f_core.DOCUMENT_FRAGMENT) {
						f_core.Assert(tbody.parentNode==this._table, "f_dataGrid.f_callServer: Not same parent ? ("+tbody.parentNode+")");
						
						this._table.removeChild(tbody);
//					}
				}

				this._shadowRows=undefined;
				this._endRowIndex=undefined;
	
				// Il faut dettacher les composants afin de retrouver ceux qui doivent être garbagés				
				while (tbody.hasChildNodes()) {
					tbody.removeChild(tbody.lastChild);
				}
			}
		}
	},
	/**
	 * @method private
	 * @param Number firstIndex
	 * @param Number length
	 * @param Number cursorIndex
	 */
	_ajaxCallServer: function(firstIndex, length, cursorIndex, selection, partialWaiting, fullUpdate) {
//		f_core.Assert(!this._loading, "Already loading ....");
		if (!selection) {
			selection=0;
		}
		
		var params=undefined;
		if (params === undefined){
			params=new Object;
		}
		
		params.gridId=this._serviceGridId;		
		params.index=firstIndex;
		if (length>0) {
			params.rows=length;
		}
		if (fullUpdate || this.f_isRefreshFullUpdateState() || this._rowCount<0) { /* && this._rows */			
	        params.unknownRowCount=true;			
		}

		var orderColumnIndex=this._sortIndexes;
		if (orderColumnIndex) {
			params.sortIndex=orderColumnIndex;
		}
		
		if (this._indexDb && this._indexDb.f_isCompleted()) {
			params.indexDbCompleted=true;
		}
		
		var filterExpression=this.fa_getSerializedPropertiesExpression();
		if (filterExpression) {
			params.filterExpression=filterExpression;
		}
		
		params.criteria = this._computeSelectedCriteria(this._selectedCriteria);//compute
		
		if (this._additionalInformations) {
			this.fa_serializeAdditionalInformations(params);
		}
		
		
		
		f_core.Debug(f_dataGrid, "f_callServer: Call server  firstIndex="+firstIndex+" cursorIndex="+cursorIndex+" selection="+selection);
		
		this._prepareNewRows(params, cursorIndex, selection, partialWaiting);
		
		this._normalizeIndexes();
		params.serializedIndexes = this._additionalIndexes;
		this._additionalIndexes = [];
		
		var waitingObject=(partialWaiting && !length)?this._pagedWaiting:this._waiting;

		var request=new f_httpRequest(this, f_httpRequest.JAVASCRIPT_MIME_TYPE);
		var dataGrid=this;
		request.f_setListener({
			/**
			 * @method public
			 */
	 		onInit: function(request) {
	 			if (!waitingObject) {
	 				waitingObject=f_waiting.Create(dataGrid, null, false);
	 				dataGrid._waiting=waitingObject;
	 			}
	 			
	 			if (waitingObject) {
		 			waitingObject.f_setText(f_waiting.GetLoadingMessage());
		 			waitingObject.f_show();
			 	}
		 	},
			/**
			 * @method public
			 */
	 		onError: function(request, status, text) {
	 			f_core.Info(f_dataGrid, "f_callServer.onError: Bad status: "+status);
	 			
	 			var continueProcess;
	 			
	 			try {
	 				continueProcess=dataGrid.f_performErrorEvent(request, f_error.HTTP_ERROR, text);
	 				
	 			} catch (x) {
	 				// On continue coute que coute !
	 				continueProcess=false;
	 			}	 				
	 				 				
	 			 			
		 		if (continueProcess===false) {
					dataGrid._loading=undefined;
	
					dataGrid.f_clearCommands();
					
					if (waitingObject) {
						waitingObject.f_hide();
					}
			 		return;
		 		}
	 			
				if (dataGrid.f_processNextCommand()) {
					return;
				}
	 		
				dataGrid._loading=undefined;		

				if (waitingObject) {
					waitingObject.f_hide();
				}
	 		},
			/**
			 * @method public
			 */
	 		onProgress: function(request, content, length, contentType) {
				if (waitingObject && f_classLoader.IsObjectInitialized(waitingObject)) {
	 				waitingObject.f_setText(f_waiting.GetReceivingMessage());
				}	 			
	 		},
			/**
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {
	 			
				if (!f_classLoader.IsObjectInitialized(dataGrid)) {
					return;
				}
			
				if (dataGrid.f_processNextCommand()) {
					return;
				}
	 				
				try {
					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
						dataGrid.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
						return;
					}

					var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
					if (!cameliaServiceVersion) {
						dataGrid.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
						return;					
					}
	
					var responseContentType=request.f_getResponseContentType().toLowerCase();
					if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
						var code=f_error.ComputeApplicationErrorCode(request);
				
				 		dataGrid.f_performErrorEvent(request, code, content);
						return;
					}
		
					if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)<0) {
				 		dataGrid.f_performErrorEvent(request, f_error.RESPONSE_TYPE_SERVICE_ERROR, "Unsupported content type: "+responseContentType);
						return;
					}
					
					var ret=request.f_getResponse();
					
					if (dataGrid._waitingLoading) {
						if (dataGrid._waitingMode==f_grid.END_WAITING) {
							dataGrid.f_removePagedWait();
						}
					}
					
					//alert("ret="+ret);
					try {
						f_core.WindowScopeEval(ret);
						
					} catch (x) {
			 			dataGrid.f_performErrorEvent(x, f_error.RESPONSE_EVALUATION_SERVICE_ERROR, "Evaluation exception");
					}

				} finally {
					dataGrid._loading=undefined;
					
					if (waitingObject) {
						waitingObject.f_hide(true);					
					}					
					
					dataGrid._waitingLoading=undefined;

					if (dataGrid._waitingMode==f_grid.END_WAITING) {						
						dataGrid.f_addPagedWait();
						
					} else if (dataGrid._waitingMode==f_grid.ROWS_WAITING) {
						dataGrid.f_addWaitingRows();
					}
				}
	
				var event=new f_event(dataGrid, f_event.LOAD);
				try {
					dataGrid.f_fireEvent(event);
					
				} finally {
					f_classLoader.Destroy(event);
				}
	 		}
		});

		this._loading=true;
		request.f_setRequestHeader("X-Camelia", this._gridUpdadeServiceId);
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!params) {
				params={};
			}
			params["javax.faces.behavior.event"]= this._gridUpdadeServiceId;
			
			
			var elementId=this.id;

			var inputSuffixPos=elementId.indexOf("::");
			if (inputSuffixPos>0) {
				elementId=elementId.substring(0, inputSuffixPos);
			}
			params["javax.faces.source"]= elementId;
			params["javax.faces.partial.execute"]= elementId;
		}
		request.f_doFormRequest(params);
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_startNewPage: function(rowIndex) {
		// Appeler par la génération du serveur !

		this.f_hideEmptyDataMessage();
		
		this._oldInputTabIndex=false;
		if (this._inputTabIndex) {
			this._inputTabIndex=undefined;
//			this._oldInputTabIndex=true;
		}
		
		var tbody=this._tbody;
		
		var scrollBody=this._scrollBody;
		if (this._oldHeight) {
			scrollBody.style.height=this._oldHeightStyle;
			this._oldHeight=undefined;
			this._oldHeightStyle=undefined;
		}
		
		if (false) {
			// Pas 2 fois !
			// Ca peut poser des problemes lors d'enchainement de filtres !
			
			if (tbody && tbody.parentNode) {		
				this.f_releaseRows();
				this.f_releaseCells();
				
				f_core.Assert(tbody.parentNode==this._table, "StartNewPage: Not same parent ? ("+tbody.parentNode+"/"+this._table+")");
				this._table.removeChild(tbody);
				this._tbody=undefined;	
	
				while (tbody.hasChildNodes()) {
					tbody.removeChild(tbody.lastChild);
				}	
			}
		}

		if (!this._waitingLoading) {
			this._first=rowIndex;

			if (this.f_isSelectable()) {
				var oldCurrentSelection=(this._currentSelection.length);
				this._currentSelection=new Array;
				this._lastSelectedElement=undefined;
				
				// Reset des lignes selectionnées ...
				if (oldCurrentSelection) {
					// On avait des selections !
					
					if (!this._selectionFullState) {
						// Pas de fullstate: elles sont perdues !
						this.fa_fireSelectionChangedEvent(null,  { value: f_event.REFRESH_DETAIL, refresh: true});
					}
				}
			}
			if (this.f_isCheckable()) {
				var oldCurrentChecks=(this._currentChecks.length);
				this._currentChecks=new Array;
				
				// Reset des lignes selectionnées ...
				if (oldCurrentChecks) {
					// On avait des selections !
					
					if (!this._checkFullState) {
						// Pas de fullstate: elles sont perdues !
						this.fa_fireCheckChangedEvent(); 
					}
				}			
			}
		}		
		this.fa_componentUpdated=false;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_updateNewPage: function() {
		// Appeler par la génération du serveur !

		f_core.Debug(f_dataGrid, "f_updateNewPage: Update new page _rowCount='"+this._rowCount+"' _maxRows="+this._maxRows+"' _rows='"+this._rows+"'.");

		if (this._rowCount<0) {
			var poolSize=this._rowsPool.length+this._first;
			if (this._maxRows<poolSize) {
				this._maxRows=poolSize;
			}
		}

		var cursorRow=undefined;
		
		var tbody=this._tbody;
		if (tbody && !this._partialWaiting) {
			f_core.Assert(tbody.parentNode!=this._table, "f_dataGrid.f_updateNewPage: Tbody has not been detached !");
			
			f_core.AppendChild(this._table, tbody);
			
			if (this._scrollTitle && this._scrollBody) {
				this._scrollBody.scrollLeft=this._scrollTitle.scrollLeft;
			}
			
			var rows=f_grid.ListRows(this._table);
			var j = 0;
			for(var i=0;i<rows.length;i++) {
				var row=rows[i];
				var index=row._index;
				if (index===undefined) {
					continue;
				}
				
				if (this._first+j==this._waitingIndex) {
					cursorRow=row;
					this._waitingIndex=undefined;
					
					// On trouve le bon,  on desactive un eventuel prétendant !
					var oldInputTabIndex=this._inputTabIndex;
					if (oldInputTabIndex) {
						oldInputTabIndex.tabIndex=-1;
					}
					
					this._inputTabIndex=row._input;
					if (this._inputTabIndex) {
						this._inputTabIndex.tabIndex=this.fa_getTabIndex();
					}
					break;
				}
				j++;
			}
			
			if (!cursorRow) {
				switch(this._selectionCardinality) {
				case fa_cardinality.OPTIONAL_CARDINALITY:
				case fa_cardinality.ONE_CARDINALITY:
					for(var i=0;i<rows.length;i++) {
						var row=rows[i];
					
						if (!row._selected) {
							continue;
						}
						
						cursorRow=row;
					}
					break;	
				}
			}
			
			if (f_core.IsGecko()) {
				// On a un probleme de layout avec le DIV ! arg !
				
				if (this._rows>0 && !this.style.height) {
					var h=this._table.offsetHeight;
					
					var body=this._scrollBody;
					if (body) {
						var dh=body.offsetHeight-body.clientHeight;
						
						h+=dh;
					}
					
					//this._table.parentNode.style.height=h+"px";
				}
			}			

		
			if (!this._paged) {
				// On recalcule le mode, car il peut avoir changé !
				if (this._rowCount>=0) {
					if (this._rows>0) {
						this._waitingMode=f_grid.ROWS_WAITING;
					}
					
				} else {
					this._waitingMode=f_grid.END_WAITING;
				}
			}

			switch(this._waitingMode) {
			case f_grid.ROWS_WAITING:
				this.f_addWaitingRows();
				break;
				
			case f_grid.END_WAITING:
				this.f_addPagedWait();
				break;
			}
		}
	
		this.fa_componentUpdated=true;

		this.f_updateTitle();

		if (this._interactiveShow || !this._titleLayout ) {
			this._interactiveShow=undefined;
		}

		f_core.Debug(f_dataGrid, "f_updateNewPage: cursorRow="+cursorRow);
		if (cursorRow) {
			this._lastSelectedElement=cursorRow;
			var selection=this._waitingSelection;
			this._waitingSelection=undefined;
			
			if (selection & fa_selectionManager.RANGE_SELECTION) {
				selection|=fa_selectionManager.APPEND_SELECTION;
			}

			this.f_moveCursor(cursorRow, true, null, selection);
		
		}
		
		if (this._inputTabIndex) {			
			f_core.Debug(f_dataGrid, "f_updateNewPage: give focus to '"+this._inputTabIndex+"'.");

			if (!this._ignoreFocus) {
			f_core.SetFocus(this._inputTabIndex, true);
		}
		}

		this.f_performPagedComponentInitialized();
		
		if (!this._rowsPool.length) {
			this.f_showEmptyDataMessage();
		}
	},
	/**
	 * Specify the image of a cell.
	 * 
	 * @method public
	 * @param Number row
	 * @param Number columnIndex
	 * @param String imageURL 
	 * @return void
	 */
	f_setCellImageURL: function(row, columnIndex, imageURL) {
		var cols=this._columns;
		var col=null;
		var cindex=0;
		for(var i=0;i<cols.length;i++) {
			col=cols[i];
			if (col._visibility===null) {
				continue;
			}
			if (columnIndex==cindex) {
				break;
			}
			cindex++;
		}
		if (cindex==cols.length) {
			return;
		}
		
		if (!col._cellImage && !col._defaultCellImageURL) {
			return;
		}
		
		var images=row._cellImages;
		if (!images || images.length<=cindex) {
			return;
		}

		var imageTag=images[cindex];
		
		imageTag._imageURL=imageURL;
		
		if (f_grid.USE_BACKGROUND_IMAGE) {
			if (imageURL) {
				imageURL="url("+imageURL+")";
				
			} else {
				imageURL="none";
			}
						
			imageTag.style.backgroundImage=imageURL;
			
		} else {
			if (!imageURL) {
				imageURL=this._blankImageURL;
			}
		
			imageTag.src=imageURL;			
		}
	},
	/**
	 * Returns the imageURL of the cell.
	 *
	 * @method public 
	 * @return String
	 */
	f_getCellImageURL: function(row, columnIndex) {
		var cols=this._columns;
		var col=null;
		var cindex=0;
		
		for(var i=0;i<cols.length;i++) {
			col=cols[i];
			if (col._visibility===null) {
				continue;
			}
			if (columnIndex==cindex) {
				break;
			}
			cindex++;
		}
		if (cindex==cols.length) {
			return null;
		}
		
		var images=row._cellImages;
		if (!images || images.length<=cindex) {
			return null;
		}
		var imageTag=images[cindex];
		if (!imageTag) {
			return null;
		}
		
		return imageTag._imageURL;
	},
	/**
	 * @method hidden
	 * @param HTMLTableRowElement row
	 * @param Object... Properties of each row
	 * @return void
	 */
	f_setCells2: function(row, configs) {
		var tds=row._cells; //getElementsByTagName("td"); // Parfois ca peut être du TH ! 
		//on utilise _cells car il ya  un bug IE7 avec .cells
		
		var cols=this._columns;

		var images=row._cellImages;

		var callUpdate=false;
		var tooltipMessage=undefined;
		
		var argIdx=0;
		for(var i=0;i<cols.length;i++) {
			var col=cols[i];
			if (!col._visibility) {
				continue;
			}
			
			var td=tds[i];
			argIdx++;
			
			var properties=configs[i];
			if (!properties) {	
				continue;
			}
			
			var cls=properties._styleClass;
			if (cls) {
				td._cellStyleClass=cls;
				//td.className=this._cellStyleClass+" "+cls;

				// row._cellsStyleClass=true;
				callUpdate=true;
			}
			
			var toolTipText=properties._toolTipText;
			if (toolTipText) {
				if (td._input) {
					td._input.title=toolTipText;		
				} else {
				td.title=toolTipText;
				}
				
			} else {
				var toolTipId = properties._toolTipId;  
				var toolTipContent = properties._toolTipContent;
				
				if (!toolTipId) {
					toolTipId = col._toolTipId;
					toolTipContent = col._toolTipContent;
				}
				
				if (toolTipId) {
					
					if (td._label && !td._toolTipId) {
						if (!tooltipMessage) {
							tooltipMessage=f_resourceBundle.Get(f_dataGrid).f_get("TOOLTIP_CELL");
						}
						
						fa_audioDescription.AppendAudioDescription(td._label, tooltipMessage);
					}

					td._toolTipId = toolTipId;
					td._toolTipContent = toolTipContent;
					
					if (td._input) {
						td._input.setAttribute("aria-haspopup", true);
					}
				}
			}
			
			var imageURL=properties._imageURL;
			if (imageURL!==undefined && images) {
				if (imageURL) {
					f_imageRepository.PrepareImage(imageURL);
				}				
				
				var imageTag=images[argIdx-1];
				
				if (f_grid.USE_BACKGROUND_IMAGE) {
					if (imageURL) {
						imageURL="url("+imageURL+")";
						
					} else {
						imageURL="none";
					}
								
					imageTag.style.backgroundImage=imageURL;
					
				} else {
					if (!imageURL) {
						imageURL=this._blankImageURL;
					}
				
					imageTag.src=imageURL;			
				}
				
				callUpdate=true;
			}
			
			if (properties._clickable) {
				td._clickable=true;
				// callUpdate=true;
				
				if (!col._cellClickable) {
					fa_audioDescription.AppendAudioDescription(td._label, "Cellule clickable");
				}
			}

		}
		
		if (callUpdate) {
			this.f_updateCellsStyle(row);
		}
	},
	
	/**
	 * Check a row.
	 *
	 * @method public
	 * @param any rowValue Value associated to the row
	 * @param Boolean show Show the checked row.
	 * @param hidden optional Event jsEvent
	 * @return Boolean Returns <code>true</code> if check has successed.
	 */
	f_checkRow: function(rowValue, show, jsEvent) {
		var row=this.f_getRowByValue(rowValue, true);
			
		if (this.fa_isElementChecked(row)) {
			return false;
		}
		
		return this.fa_performElementCheck(row, show, jsEvent, true);
	},
	/**
	 * Uncheck a row.
	 *
	 * @method public
	 * @param any rowValue Value associated to the row
	 * @param hidden optional Event jsEvent
	 * @return Boolean Returns <code>true</code> if uncheck has successed.
	 */
	f_uncheckRow: function(rowValue, jsEvent) {
		var row=this.f_getRowByValue(rowValue, true);
		
		if (!this.fa_isElementChecked(row)) {
			return false;
		}
		
		return this.fa_performElementCheck(row, false, jsEvent, false);
	},
	
	/**
	 * Returns <code>true</code> if the receiver is checked, and <code>false</code> otherwise
	 *
	 * @method public
	 * @param any rowValue Value associated to the row, or a row object.
	 * @return Boolean The checked state of the row
	 */
	f_getChecked: function(rowValue) {
		var row=this.f_getRowByValue(rowValue, true);
	
		return this.fa_isElementValueChecked(row);
	},	
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_isElementChecked: function(row) {
		f_core.Assert(row && row.tagName.toLowerCase()=="tr", "f_dataGrid.fa_isElementChecked: Invalid element parameter ! ("+row+")");

		return !!row._checked;
	},
	/**
	 * @method protected
	 * @return void
	 */
	fa_setElementChecked: function(row, checked) {
		f_core.Assert(row && row.tagName.toLowerCase()=="tr", "f_dataGrid.fa_setElementChecked: Invalid element parameter ! ("+row+")");

		row._checked=checked;
	},
	fa_updateReadOnly: function() {
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_sortClientSide: function(methods, ascendings, tdIndexes) {
		
		var self=this;
		function internalSort(obj1, obj2) {				
			for(var i=0;i<methods.length;i++) {
				var tdIndex=tdIndexes[i];
				
				var tc1 = obj1.childNodes[tdIndex];
				var tc2 = obj2.childNodes[tdIndex];

				 var ret=methods[i].call(self, tc1._text, tc2._text, tc1, tc2, tc1._index, tc2._index);
				 if (!ret) {
					continue;
				 }
				 
				return (ascendings[i])? ret:-ret;
			}
			
			return 0;
		}
		
		var body=this._table.tBodies[0];
		f_core.Assert(body, "f_grid._sortTable: No body for data table of dataGrid !");
		
		var trs = this.fa_listVisibleElements(true);
		
		trs.sort(internalSort);

		this._table.removeChild(body);
		
		while(body.firstChild) {
			body.removeChild(body.firstChild);
		}

		for(var i=0;i<trs.length;i++) {
			var row=trs[i];
			row._curIndex=null;
			
			f_core.AppendChild(body, row);
		}

		var rowClasses=this._rowStyleClasses;

		for(var i=0;i<trs.length;i++) {
			var row=trs[i];
			
			row._className=rowClasses[i % rowClasses.length];
			
			this.fa_updateElementStyle(row);
						
			if (this._additionalInformationCount && 
					this.fa_isAdditionalElementVisible(row) && 
					this.f_hasAdditionalElement(row)) {

				this.f_showAdditionalContent(row);
			}
		}		
	
		f_core.AppendChild(this._table, body);	
	},
	/**
	 * @method protected
	 * @param Number code Keycode
	 * @param Event evt
	 * @param Boolean selection
	 * @return Boolean Success
	 */
	f_searchRowNode: function(code, evt, selection) {
			
		var key=String.fromCharCode(code).toUpperCase();
	
		var now=new Date().getTime();
		if (this._lastKeyDate!==undefined) {
			var dt=now-this._lastKeyDate;
			f_core.Debug(f_dataGrid, "_searchRowNode: Delay key down "+dt+"ms");
			if (dt<f_dataGrid._SEARCH_KEY_DELAY) {
				var nkey=this._lastKey+key;
				
				if (this._searchRowNodeByText(nkey,false, evt, selection)) {			
					this._lastKeyDate=now;
					this._lastKey=nkey;
					return true;
				}
			}
		}
		
		this._lastKeyDate=now;
		this._lastKey=key;
		
		return this._searchRowNodeByText(key, true, evt, selection);
	},
	/**
	 * @method private
	 * @param String key Complete text to search
	 * @param Boolean next  Skip the current row
	 * @param Event evt
	 * @param Boolean selection
	 * @return Boolean Success
	 */
	_searchRowNodeByText: function(key, next, evt, selection) {
		var tr=this._cursor;
		if (!tr) {
			tr=this._tbody.firstChild;
			
		} else if (next) {
			tr=tr.nextSibling; // A partir du suivant
		}

		var columns=this._columns;

		var colIndex=this._keySearchColumnIndex;
		
		if (colIndex===undefined) {
			var currentSorts=this._currentSorts;
			if (currentSorts && currentSorts.length) {
				var currentSort=currentSorts[0];
			
				for(var i=0;i<columns.length;i++) {
					var col=columns[i];
	
					if (col==currentSort) {
						colIndex=i;
						break;		
					}
				}
			}
		}
				
		if (colIndex===undefined) {
			for(var i=0;i<columns.length;i++) {
				var col=columns[i];
	
				if (col._visibility) {
					colIndex=i;
					break;
				}
			}
		}
		
		if (colIndex===undefined) {
			return;
		}
		
		var kl=key.length;
		
		var size=this._tbody.childNodes.length;
		for(var i=0;i<size;i++,tr=tr.nextSibling) {
			if (!tr) {
				tr=this._tbody.firstChild;
			}
			if (!tr._dataGrid) {
				continue;
			}
			
			var cells=tr._cells;
				
			var text=cells[colIndex]._text;
			if (!text || text.length<kl) {
				continue;
			}	
			
			if (text.substring(0, kl).toUpperCase()!=key) {
				continue;
			}
			
			this.f_moveCursor(tr, true, evt, selection);
			return true;
		}
		
		return false;
	},
	
	/**
	 * @method public
	 */
	f_checkAllPage: function() {
		if (!this.f_isCheckable()) {
			return;
		}
		
		var elts = this.fa_listVisibleElements();
		for(var i=0;i<elts.length;i++) {
			var element=elts[i];
			if(!this.fa_isElementChecked(element)) {
				this._checkElement(element, this.fa_getElementValue(element), true);
				this.fa_updateElementCheck(element, true);
			}
		}
	},
	
	/**
	 * @method public
	 */
	f_uncheckAllPage: function() {
		if (!this.f_isCheckable()) {
			return;
		}
		var elts = this.fa_listVisibleElements();
		for(var i=0;i<elts.length;i++) {
			var element=elts[i];
			if(this.fa_isElementChecked(element)) {
				this._checkElement(element, this.fa_getElementValue(element), false);
				this.fa_updateElementCheck(element, false);
			}
		}
	},
	/**
	 * @method public
	 */
	f_uncheckAll: function() {
		if (this.f_isCheckable()) {
			this._uncheckAllElements();
		}
	},
	/**
	 * Returns label of a row
	 * 
	 * @method public
	 * @param any rowValue Value of row
	 * @return String
	 */
	f_getElementLabel: function(rowValue) {
		var labelColumnId=this._labelColumnId;
		if (labelColumnId===undefined) {
			labelColumnId=f_core.GetAttributeNS(this,"rowLabelColumnId", null);
			
			this._labelColumnId=labelColumnId;
		}
		
		if (!labelColumnId) {
			return null;
		}
		
		return this.f_getCellValue(rowValue, labelColumnId);
	},
	f_overDropInfos: function(dragAndDropEngine, infos) {
		var row=infos.item;
		
		var cells=row._cells;
		if (cells) {
			for(var i=0;i<cells.length;i++) {
				var cell=cells[i];
				var title=cell.title;			
				if (!title) {
					continue;
				}
				
				cell._toolTipText=title;
				cell.removeAttribute("title");
			}
		}
		
		this.f_super(arguments, dragAndDropEngine, infos);
	},
	f_outDropInfos: function(dragAndDropEngine, infos) {
		var row=infos.item;
		
		var cells=row._cells;
		if (cells) {
			for(var i=0;i<cells.length;i++) {
				var cell=cells[i];
				var title=cell._toolTipText;
				if (!title) {
					continue;
				}
				
				cell.title=title;
			}
		}
		
		this.f_super(arguments, dragAndDropEngine, infos);
	},
	
	fa_evaluateCriteria: function (selectedCriteria, callBack, waitingElement){
		
		if (!this._interactive) {
			return false;
		}
		
		if (this._criteriaEvaluateCallBacks === undefined){
			this._criteriaEvaluateCallBacks = new Object;
		}
		this._criteriaEvaluateCallBacks[++this._countToken] = callBack;
		
		this.f_appendCommand(function(dataGrid) {
			
			var params = new Object();
			params.gridId=this._serviceGridId;		
			params.tokenId = this._countToken;	

			params.selectedCriteria = this._computeSelectedCriteria(selectedCriteria);

			this.f_hideEmptyDataMessage();
			
			var waitingObject=undefined;

			var request=new f_httpRequest(this, f_httpRequest.JAVASCRIPT_MIME_TYPE);
			var dataGrid=this;
			var elementWait = waitingElement;
			request.f_setListener({
				/**
				 * @method public
				 */
		 		onInit: function(request) {
					if (waitingElement) {
						f_core.SetTextNode(waitingElement, f_waiting.GetLoadingMessage());
					}
			 	},
				/**
				 * @method public
				 */
		 		onError: function(request, status, text) {
		 			f_core.Info(f_dataGrid, "f_callServer.onError: Bad status: "+status);
		 			
		 			try {
		 				continueProcess=dataGrid.f_performErrorEvent(request, f_error.HTTP_ERROR, text);
		 				
		 			} catch (x) {
		 				
		 			}	 				
		 			
					if (dataGrid.f_processNextCommand()) {
						return;
					}
			 	},
		 		
				/**
				 * @method public
				 */
		 		onProgress: function(request, content, length, contentType) {
		 			if (waitingElement) {
						f_core.SetTextNode(waitingElement, f_waiting.GetReceivingMessage());
					}	
		 		},
				/**
				 * @method public
				 */
		 		onLoad: function(request, content, contentType) {
					if (!f_classLoader.IsObjectInitialized(dataGrid)) {
						return;
					}
				
					if (dataGrid.f_processNextCommand()) {
						return;
					}
		 				
					try {
						if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
							dataGrid.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
							return;
						}

						var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
						if (!cameliaServiceVersion) {
							dataGrid.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
							return;					
						}
		
						var responseContentType=request.f_getResponseContentType().toLowerCase();
						if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
							var code=f_error.ComputeApplicationErrorCode(request);
					
					 		dataGrid.f_performErrorEvent(request, code, content);
							return;
						}
			
						if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)<0) {
					 		dataGrid.f_performErrorEvent(request, f_error.RESPONSE_TYPE_SERVICE_ERROR, "Unsupported content type: "+responseContentType);
							return;
						}
						
						var ret=request.f_getResponse();
						
						if (dataGrid._waitingLoading) {
							if (dataGrid._waitingMode==f_grid.END_WAITING) {
								dataGrid.f_removePagedWait();
							}
						}
												
						try {
							f_core.WindowScopeEval(ret);
							
						} catch (x) {
				 			dataGrid.f_performErrorEvent(x, f_error.RESPONSE_EVALUATION_SERVICE_ERROR, "Evaluation exception");
						}

					} finally {
						dataGrid._loading=undefined;
						dataGrid._waitingLoading=undefined;
					}
		
					var event=new f_event(dataGrid, f_event.LOAD);
					try {
						dataGrid.f_fireEvent(event);
						
					} finally {
						f_classLoader.Destroy(event);
					}
		 		}
			});

			this._loading=true;
			request.f_setRequestHeader("X-Camelia", this._gridUpdadeCriteriaServiceId);
			if ( parseInt(_rcfaces_jsfVersion) >= 2) {
				// JSF 2.0
				request.f_setRequestHeader("Faces-Request", "partial/ajax");

				if (!params) {
					params={};
				}
				params["javax.faces.behavior.event"]=  this._gridUpdadeCriteriaServiceId;
				params["javax.faces.source"]= this.id;
				params["javax.faces.partial.execute"]= this.id;
			}
			request.f_doFormRequest(params);
		});
		
	},
	
	/**
	 * @method protected 
	 * @param Array criteriaSelected  Sous la forme  [{ id: "idColonne", values: [ val1, val2 ] }, {...} ] 
	 * @param Boolean refresh Refresh the grid
	 * @return void
	 */
	fa_setSelectedCriteria: function (selectedCriteria, refresh){
		f_core.Assert(selectedCriteria instanceof Array, "f_dataGrid.fa_setSelectedCriteria: Invalid selectedCriteria parameter ! ("+selectedCriteria+")");
	
		this._selectedCriteria = selectedCriteria;
		this._countToken = -1;
		if(refresh === false) {
			return;
		}
		this._changeFirst(0);
		
		this.f_refreshContent(true);
		
	},
	
	fa_getColumnCriteriaCardinality: function (columnId) {
		f_core.Assert(typeof(columnId)=="string" || typeof(columnId)=="object", "f_dataGrid.fa_getColumnCriteriaCardinality: Invalid columnId parameter ! ("+columnId+")");

		var column = this._getColumn(columnId);		
		if (!column) {
			return undefined;
		}
				
		return column._criteriaCardinality;
	},
	
	/**
	 * @method protected
	 * @param Integer tokenId
	 * @param Integer resultCount
	 * @parameter Object criteriaSelected 
	 * @return void
	 */
	_processSelectedCriteriaResult: function (tokenId, resultCount, availableCriteria) {
		
		var cb=this._criteriaEvaluateCallBacks[tokenId];
		delete this._criteriaEvaluateCallBacks[tokenId];
				
		cb.call(this, resultCount, availableCriteria);
	},
	/**
	 * @method private
	 * @param columnId Identifier of Column or column object  
	 * @return Object Column object
	 */
	_getColumn: function(columnId) {
		if (typeof(columnId)=="object") {
			return columnId;
		}
		
		var column = null;
		var columns = this._columns;
		for (var i = 0; i < columns.length; i++) {
			var cl = columns[i];
			
			if (cl._id==columnId) {
				column=cl;
				break;
			}
		}
		
		if (!column) {
			return undefined;
		}
				
		return column;
	},
	/**
	 * @method public
	 * @param String columnId Identifier of Column or column object  
	 */
	fa_getCriteriaLabelByColumn: function(columnId) {
		f_core.Assert(typeof(columnId)=="string" || typeof(columnId)=="object", "f_dataGrid.fa_getCriteriaLabelByColumn: Invalid columnId parameter ! ("+columnId+")");

		var column = this._getColumn(columnId);		
		if (!column) {
			return undefined;
		}
				
		if (column._criteriaTitle) {
			return column._criteriaTitle;
		}
		
		return this.f_getColumnName(column);
	},
	
	/**
	 * @method protected
	 */
	fa_showElement : function(row, giveFocus) {
		this.f_super(arguments, row);
		
		if (!giveFocus || this._ignoreFocus) {
			return;
		}
		
		f_core.Debug(f_dataGrid, "fa_showElement: show row '"+row._value+"'  inputTabIndex='"+this._inputTabIndex+"'.");
		
		var old=this._inputTabIndex;
		var oldCell = this._inputCellIndex;
		if (old) { 
			if (row && row._input==old) {
				if (this._cursorCellIdx===undefined || this._cursorCellIdx===oldCell) {
				return;
			}
			}
			
			old.tabIndex=-1;
			this._inputTabIndex=undefined;
			this._inputCellIndex=undefined;
		}
		
		if (row && row._input) {
			var input=row._input;
			if (this._cursorCellIdx!==undefined) {
				var cc=row._cells[this._cursorCellIdx];
				if (cc && cc._input) {
					input=cc._input;
				}
			}
			
			input.tabIndex=this.fa_getTabIndex();
			this._inputTabIndex=input;
			this._inputCellIndex=this._cursorCellIdx;
	
			f_core.Debug(f_dataGrid, "fa_showElement: give focus to "+input);

			var deltaFocus=undefined;
			// On s'assure que le focus soit visible
			if (this._scrollBody && this._checkable && this._scrollBody.scrollLeft) {
				var sl=this._scrollBody.scrollLeft;
				sl-=input.offsetLeft;
				if (sl>0) {
					deltaFocus={
						x: sl
					};
				}
			}
			
			f_core.SetFocus(input, true, deltaFocus);
		}
	},
	f_getFocusableElement : function() {
		if (this._inputTabIndex) {
			return this._inputTabIndex;
		}
		
		return this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @param evt
	 * @returns
	 */
	_processRowRightKey: function(evt) {		
		if (this._cellFocusable && !evt.ctrlKey) {
			var cursorCellIdx=this._cursorCellIdx;
			if (!cursorCellIdx) {
				cursorCellIdx=0;
			}
					
			var columns=this._columns;
			var newIdx=cursorCellIdx+1;
			for(;newIdx<columns.length;newIdx++) {
				if (columns[newIdx]._visibility===false) {
					continue;
				}
				break;
			}
			if (newIdx<columns.length) {
				cursorCellIdx=newIdx;
						
				this._cursorCellIdx=cursorCellIdx;
				
				this.fa_showElement(this._cursor, true);
			}			
			return true;			
		}
		
		var ret=this.f_super(arguments, evt);
		return ret;
	},
	/**
	 * @method protected
	 * @param evt
	 * @returns
	 */
	_processRowLeftKey: function(evt) {		
		if (this._cellFocusable && !evt.ctrlKey) {
			var cursorCellIdx=this._cursorCellIdx;
			if (cursorCellIdx) {
				var columns=this._columns;
				var newIdx=cursorCellIdx;
				for(newIdx--;newIdx>=0;newIdx--) {
					if (columns[newIdx]._visibility===false) {
						continue;
					}
					break;
				}
				if (newIdx>=0) {						
					this._cursorCellIdx=newIdx;
					
					this.fa_showElement(this._cursor, true);					
				}
			}
			return true;
		}
		
		var ret=this.f_super(arguments, evt);
		return ret;
	},
	/**
	 * @method protected
	 * @param Object details
	 * @return Object 
	 */
	_fillColumnDetails: function(details, column, cell) {
		details=this.f_super(arguments, details, column);
		
		if (!column) {
			var cursorCellIdx=this._cursorCellIdx;
			if (cursorCellIdx!==undefined) {
				var cs=this._getCellAndColumn(this._cursor, cursorCellIdx);
				if (cs) {
					column=cs.column;
					cell=cs.cell;
				}
				
			}
		}
			
		if (column && (column._cellClickable || (cell && cell._clickable))) {
			if (!details) {
				details=f_event.NewDetail();
			}
			
			details.column=column;
			details.columnId=column.f_getId();
			details.columnIndex=column._index;
			
			details.cell=cell;
		}
		
		return details;
	}
		
};

new f_class("f_dataGrid", {
	extend: f_grid,
	aspects: [fa_readOnly, fa_checkManager, fa_droppable, fa_draggable, fa_criteriaManager],
	statics: __statics,
	members: __members
});

