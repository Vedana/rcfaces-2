/*
 * $Id: fa_dataGridPopup.js,v 1.4 2014/02/05 16:07:15 jbmeslin Exp $
 */

/**
 * 
 * @aspect public abstract fa_dataGridPopup 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2014/02/05 16:07:15 $
 */

var __statics = {
	/** 
	 * @field private static final Boolean 
	 */
	_DONT_RELEASE_POPUP: false,
	
	/** 
	 * @field private static final String 
	 */
	_DATAGRID_POPUP_KEY_SCOPE_ID: "#dataGridPopup",
	
	/** 
	 * @field private static final String 
	 */
	_DEFAULT_VALUE_FORMAT: "{0}",

	/** 
	 * @field protected static Number 
	 */
	LAST_OUTSIDE: 0,
	
	/**
	 * @method private static
	 * @param Object dataGridPopup
	 * @param Object position
	 * @param Number offsetX
	 * @param Number offsetY
	 * @param function callbackWhenReady
	 * @return void
	 */
	_OpenPopup: function(dataGridPopup, position, offsetX, offsetY, callbackWhenReady) {
		f_core.Debug(fa_dataGridPopup, "_OpenPopup: Open popup for dataGridPopup '"+dataGridPopup.id+"'. (popupOpened='"+dataGridPopup._popupOpened+"' popup='"+dataGridPopup._popup+"')");

		var popupClassName="f_dataGridPopup_popup";
		var ds=f_core.GetAttributeNS(dataGridPopup,"popupStyleClass");
		if (ds) {
			popupClassName+=" "+ds;
		}			
				
		var popup=dataGridPopup._popup;
		if (!popup) {
			if (dataGridPopup._iePopup) {
				var doc=dataGridPopup.ownerDocument;
				
				f_popup.Ie_GetPopup(doc, f_popup.IE_FRAME_POPUP_TYPE, function(popup, pdoc) {
		
					//pdoc.body.innerHTML="";
					
					popup.style.width=dataGridPopup._popupWidth+"px";
					popup.style.height=dataGridPopup._popupHeight+"px";
					
					
					var body=pdoc.createElement("div");
					body.className=popupClassName;
					body.style.visibility="inherit";
					body.style.position="relative";
					body.style.width=dataGridPopup._popupWidth+"px";
				//	body.style.height=dataGridPopup._popupHeight+"px";
				//	body.setAttribute("role", "combobox");
	
					f_core.AppendChild(pdoc.body, body);

					dataGridPopup._popup=popup;
				
					if (body) {
						body._popupObject=true;
						dataGridPopup.f_constructDataGrid(body);
					}

					var newHeight=body.offsetHeight;
					popup.style.height=newHeight+"px";
					body.style.height=newHeight+"px";
			
					fa_dataGridPopup._OpenPopup2(popup, dataGridPopup, position, offsetX, offsetY, callbackWhenReady);	
				});

				return;
			}			

			popup=dataGridPopup.ownerDocument.createElement("div");
			popup.className=popupClassName;
			popup.style.width=dataGridPopup._popupWidth+"px";
			popup.style.height=dataGridPopup._popupHeight+"px";
			//popup.setAttribute("role", "combobox");

			popup.onclick=f_core.CancelJsEventHandlerTrue;
			popup.onmousedown=f_core.CancelJsEventHandlerTrue;

			var body=popup;
			
			var parent=dataGridPopup;
			
			f_core.AppendChild(parent.ownerDocument.body, popup);

			dataGridPopup._popup=popup;
		
			if (body) {
				body._popupObject=true;
				dataGridPopup.f_constructDataGrid(body);
			}
		}
		
		fa_dataGridPopup._OpenPopup2(popup, dataGridPopup, position, offsetX, offsetY, callbackWhenReady);	
	},
	_OpenPopup2: function(popup, dataGridPopup, position, offsetX, offsetY, callbackWhenReady) {
		f_core.Debug(fa_dataGridPopup, "_OpenPopup: Open popup for dataGridPopup '"+dataGridPopup.id+"'. (popupOpened='"+dataGridPopup._popupOpened+"' popup='"+dataGridPopup._popup+"')");
	
		if (f_popup.RegisterWindowClick({
				/**
				 * @method public
				 */
				exit: function(jsEvent) {
					f_core.Debug(fa_dataGridPopup, "_OpenPopup.exit: Click outside "+jsEvent);

					return dataGridPopup._clickOutside(jsEvent);
				},
				/**
				 * @method public
				 */
				keyDown: function(jsEvent) {
					f_core.Debug(fa_dataGridPopup, "_OpenPopup.keyDown: popup keyDown: "+jsEvent.keyCode);

					switch(jsEvent.keyCode) {
					case f_key.VK_RETURN:
				 	case f_key.VK_ENTER:
						dataGridPopup._rowSelection(dataGridPopup._dataGrid, jsEvent);
				 		return false;

				 	case f_key.VK_TAB:
						dataGridPopup._rowSelection(dataGridPopup._dataGrid, jsEvent);
						dataGridPopup._focusNext=true;
				 		return true; // On accepte le TAB

					case f_key.VK_ESCAPE:
						dataGridPopup.f_closeDataGridPopup(jsEvent);
				 		return false;
		 		
					case f_key.VK_DOWN:
					case f_key.VK_UP:
					case f_key.VK_PAGE_DOWN:
					case f_key.VK_PAGE_UP:
//					case f_key.VK_END:
//					case f_key.VK_HOME:
						dataGridPopup._dataGrid.f_fireEvent(f_event.KEYDOWN, jsEvent);
						return false;
					}

					return true;
				},
				/**
				 * @method public
				 */
				keyUp: function(jsEvent) {
					f_core.Debug(fa_dataGridPopup, "_OpenPopup.keyUp: popup keyUp: "+jsEvent.keyCode);
					/*return menu._filterKey("up", evt);*/
					
					switch(jsEvent.keyCode) {
					case f_key.VK_DOWN:
					case f_key.VK_UP:
					case f_key.VK_PAGE_DOWN:
					case f_key.VK_PAGE_UP:
					case f_key.VK_END:
					case f_key.VK_HOME:
					case f_key.VK_SHIFT:
					case f_key.VK_CONTROL:	
						dataGridPopup._dataGrid.f_fireEvent(f_event.KEYUP, jsEvent);
						return false;
					}
					
					return true;
				},
				/**
				 * @method public
				 */
				keyPress: function(evt) {
					f_core.Debug(fa_dataGridPopup, "_OpenPopup.keyPress: popup keyPress: "+evt.keyCode);
					/*switch(evt.keyCode) {
					case f_key.VK_RETURN:
				 	case f_key.VK_ENTER:
				 		return fa_menuCore.OnKeyDown(menu, evt);
					}
					*/
					return true;
				}
			}, dataGridPopup, popup)==false) {
			
			f_core.Debug(fa_dataGridPopup, "_OpenPopup: Register refused to open the popup of dataGridPopup='"+dataGridPopup.id+"'.");
			return;
		}
		
		f_core.Debug(fa_dataGridPopup, "_OpenPopup: Open popup "+popup+" of dataGridPopup='"+dataGridPopup.id+"'.");
		if (popup) {
			f_key.EnterScope(fa_dataGridPopup._DATAGRID_POPUP_KEY_SCOPE_ID);

			var positionParameters = {
				component: position, 
				position: f_popup.BOTTOM_LEFT_COMPONENT 
			};

			if (dataGridPopup._iePopup) {
				f_popup.Ie_openPopup(popup, positionParameters);
			
			} else {
				f_popup.Gecko_openPopup(popup, positionParameters);
			}
			
			if (dataGridPopup._searchInput) {
				dataGridPopup._searchInput.focus();
				
			} else if (dataGridPopup._input) {
				dataGridPopup._ariaInput = dataGridPopup._input;
				dataGridPopup._ariaInput._role = true;
				dataGridPopup._ariaInput.setAttribute("role", "listbox");
			}
		}
	
		dataGridPopup._popupOpened=true;
		
		if (typeof(callbackWhenReady)=="function") {
			callbackWhenReady.call(dataGridPopup);
		}
	},

	/**
	 * @method private static
	 */
	_ClosePopup: function(dataGridPopup, jsEvt) {	
		f_core.Debug(fa_dataGridPopup, "_ClosePopup: Close the popup of dataGridPopup='"+dataGridPopup.id+"' opened="+dataGridPopup._popupOpened+" popup="+dataGridPopup._popup);

		if (!dataGridPopup._popupOpened) {
			return;
		}
		
		if (dataGridPopup._ariaInput){
			dataGridPopup._ariaInput.removeAttribute("aria-activedescendant");
			if (dataGridPopup._ariaInput._role) {
				
				// Le 28/09 Réné demande de remettre le role à combobox  (JAWS ne voit plus rien)
				dataGridPopup._ariaInput.setAttribute("role", "combobox");
				dataGridPopup._ariaInput._role = false;
			}
		}
		
		dataGridPopup._popupOpened=undefined;

		f_popup.UnregisterWindowClick(dataGridPopup);		
					
		f_key.ExitScope(fa_dataGridPopup._DATAGRID_POPUP_KEY_SCOPE_ID);
		
		var popup=dataGridPopup._popup;		
		if (!popup) {
			return; 
		}
	
		dataGridPopup._popup=undefined;
		
		if (!dataGridPopup._iePopup) {
			f_popup.Gecko_closePopup(popup);

			window.setTimeout(function() {
				dataGridPopup._finalizePopup();
	
				if (fa_dataGridPopup._DONT_RELEASE_POPUP==false) {
					f_popup.Gecko_releasePopup(popup); // A REMETTRE
				} 
			}, 0);
			return;
		}	
		
		f_popup.Ie_closePopup(popup);

		// Il faut quitter la boucle d'evenement !
		window.setTimeout(function() {
			dataGridPopup._finalizePopup();
	
			f_popup.Ie_releasePopup(popup);
		}, 0);
	},
	/**
	 * @method private static
	 * @param Event jsEvent
	 * @return Boolean
	 * @context object:dataGridPopup
	 */
	_SearchSuggest_onkeyup: function(jsEvent) {
		var input=this;
		var dataGridPopup=input._dataGridPopup;
				
		if (!jsEvent) {
			jsEvent = f_core.GetJsEvent(this);
		}

		if (dataGridPopup.f_getEventLocked(jsEvent)) {
			return false;
		}
		
		return dataGridPopup._onSearchSuggest(jsEvent);
	},
	/**
	 * @method private static
	 * @param Event jsEvent
	 * @return Boolean
	 * @context object:dataGridPopup
	 */
	_SearchButton_onclick: function(jsEvent) {
		var input=this;
		var dataGridPopup=input._dataGridPopup;
				
		if (!jsEvent) {
			jsEvent = f_core.GetJsEvent(this);
		}

		if (dataGridPopup.f_getEventLocked(jsEvent)) {
			return false;
		}
		
		return dataGridPopup._onSearchClick(jsEvent);
	}
};

var __members = {
		
	/**
	 * @field private String
	 */
	_dataGridInnerHtml: undefined,
	
	/**
	 * @field private String
	 */
	_valueColumnId: undefined,
	
	/**
	 * @field private String
	 */
	_labelColumnId: undefined,
	
	/**
	 * @field private String
	 */
	_valueFormat: undefined,
	
	/**
	 * @field private Object[]
	 */
	_columns: undefined,
	
	fa_dataGridPopup: function() {		
		this._valueColumnId=f_core.GetAttributeNS(this,"valueColumnId");
		
		var labelColumnId=f_core.GetAttributeNS(this,"labelColumnId");
		if (labelColumnId) {
			this._labelColumnId=labelColumnId;
		}
		
		var valueFormat=f_core.GetAttributeNS(this,"valueFormat");
		if (!valueFormat) {
			if (labelColumnId) {
				valueFormat="{"+labelColumnId+"}";

			} else {
				valueFormat=fa_dataGridPopup._DEFAULT_VALUE_FORMAT;
			}
		}
		
		this._popupWidth=f_core.GetNumberAttributeNS(this,"popupWidth", 320);
		this._popupHeight=f_core.GetNumberAttributeNS(this,"popupHeight", 200);
		
		this._valueFormat=valueFormat;
		
		this._iePopup=f_popup.Ie_enablePopup();
	},
	f_finalize: function() {
		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			window.clearTimeout(timerId);
		}
		
		// this._dataGridHtml=undefined; // String
		// this._autoSelect=undefined; // number
		// this._valueColumnId=undefined; // String
		// this._labelColumnId=undefined; // String
		// this._valueFormat=undefined; // String
		// this._iePopup=undefined; // boolean
		// this._popupWidth=undefined; // number
		// this._popupHeight=undefined; // number
				
		this._popup=undefined; // ? HtmlDivElement
		this._columns=undefined;  // Object[]
		
		this._finalizePopup();
		
	},
	/**
	 * @method private
	 * @return void
	 */
	_finalizePopup: function() {
		f_core.Debug(fa_dataGridPopup, "_finalizePopup: clear popup !");
		
		try {
			var searchInput=this._searchInput;
			if (searchInput) {
				this._searchInput=undefined; // HtmlInputElement
	
				searchInput._dataGridPopup=undefined;	
				searchInput.onkeyup=null;
				
				f_core.VerifyProperties(searchInput);
			}
			
			if (this._ariaInput){
				this._ariaInput = undefined; // HtmlInputElement
			}
			
			var searchIcon=this._searchIcon;
			if (searchIcon) {
				this._searchIcon=undefined; // HtmlImageElement
	
				searchIcon._dataGridPopup=undefined;	
				searchIcon.onclick=null;
				
				f_core.VerifyProperties(searchIcon);
			}
	
			var dataGrid=this._dataGrid;
			if (dataGrid) {
				this._dataGrid=undefined;
		
				var parent=dataGrid.parentNode;
				if (parent) {
					parent.removeChild(dataGrid);
				}
	
				f_classLoader.Destroy(dataGrid);
			}
					
			var pager=this._pager;
			if (pager) {
				this._pager=undefined;
				
				var parent=pager.parentNode;
				if (parent) {
					parent.removeChild(pager);
				}
				
				f_classLoader.Destroy(pager);
			}
		} catch (x) {
			f_core.Debug(fa_dataGridPopup, "_finalizePopup: error while cleaning popup object", x);
		}
	},
	/**
	 * @method protected
	 * @param HTMLElement parent
	 * @return f_dataGrid
	 */
	f_constructDataGrid: function(parent) {
		f_core.Debug(fa_dataGridPopup, "f_constructDataGrid: construct components parent="+parent);

		var rows=f_core.GetNumberAttributeNS(this, "rows");
		var paged=f_core.GetBooleanAttributeNS(this, "paged", true);
		
		var hasPager=(rows>0 && paged);
		
		var width=this._popupWidth-2; /*border */
		var height=this._popupHeight-2; /*border */
		var pagerHeight=(hasPager)?(18):0;
		var inputHeight=0;
		
		var tableClassName="fa_dataGridPopup_table";
		
		var dataGridContainer=f_core.CreateElement(parent, "table", {
			cellSpacing: 0, 
			cellPadding: 0,
			className: tableClassName, 
			"style": "width:"+width+"px;height:"+height+"px" 
		});
		dataGridContainer.setAttribute("role", "presentation");
		
		var tBodyContainer=f_core.CreateElement(dataGridContainer, "tbody");		
		
		var showTitle=f_core.GetBooleanAttributeNS(this,"searchFieldVisible", true);
		
		var cwidth=width;
		var cheight=height;
		var resourceBundle=f_resourceBundle.Get(fa_dataGridPopup);
		
		if (showTitle) {	
			inputHeight=20;
			cwidth-=2+2;
			cheight-=2+2+inputHeight+2+2;
				
			var tr=f_core.CreateElement(tBodyContainer, "tr", {
			 	className: "fa_dataGridPopup_search"
			});								
			
			var search=f_core.CreateElement(tr, "td", {
				align: "left", 
				valign: "middle" 
			});
			
			var div=f_core.CreateElement(search, "div", {
				className: "fa_dataGridPopup_title"
			});
			
			var plabel = f_core.CreateElement(div, "label", {
				className: "fa_dataGridPopup_label",
				textNode: resourceBundle.f_get("SEARCH_LABEL")
			});
			plabel.setAttribute("for", this.id+"::dataGridPopup_input");
			
			var form=f_core.CreateElement(div, "form", {
				className: "fa_dataGridPopup_form"
			});
			form.onsubmit=document._rcfacesDisableSubmit;
			form.submit=document._rcfacesDisableSubmitReturnFalse;
			form.action="#";
		
			var button=f_core.CreateElement(form, "img", {
				className: "fa_dataGridPopup_icon",
				src: f_env.GetBlankImageURL(),
				name: "searchButton"
			});
			this._searchIcon=button;
			button._dataGridPopup=this;
			button.onclick=fa_dataGridPopup._SearchButton_onclick;
			
			var input=f_core.CreateElement(form, "input", {
				id: this.id+"::dataGridPopup_input",
				className: "fa_dataGridPopup_input",
				name: "searchValue",
				type: "text",
				// He oui ! cela semble marcher sur tous les browsers ! (meme Gecko !?)		
				autocomplete: "off"
			});
			this._searchInput=input;
			input._dataGridPopup=this;
			input.onkeyup=fa_dataGridPopup._SearchSuggest_onkeyup;
			this._ariaInput = input;
			this._ariaInput.setAttribute("role","listbox");
		}
		
		this._lastValue="";
		
		if (pagerHeight) {
			cheight-=pagerHeight+2+2;
		}
		
		var td=f_core.CreateElement(tBodyContainer, "tr", {
			height: cheight
		}, "td", {
			align: "left", 
			valign: "middle" 
		});									
		
		
		var dataGridStyleClass="fa_dataGridPopup_grid";
		var ds=f_core.GetAttributeNS(this,"gridStyleClass");
		if (ds) {
			dataGridStyleClass+=" "+ds;
		}
		
		dataGrid=f_dataGridPopup.Create(td, 
			this, 
			cwidth, 
			cheight,
			dataGridStyleClass);
		
		dataGrid._useInPopup = true;
		dataGrid.removeAttribute("role");
		this._dataGrid=dataGrid;
	
		if (hasPager) {

			if (!f_core.GetAttributeNS(this,"message")) {				

				f_core.SetAttributeNS(this, "message", resourceBundle.f_get("MESSAGE"));
				f_core.SetAttributeNS(this, "zeroResultMessage", resourceBundle.f_get("ZERO_RESULT_MESSAGE"));			
				f_core.SetAttributeNS(this, "oneResultMessage", resourceBundle.f_get("ONE_RESULT_MESSAGE"));			
				f_core.SetAttributeNS(this, "manyResultMessage", resourceBundle.f_get("MANY_RESULTS_MESSAGE"));			
				f_core.SetAttributeNS(this, "manyResultMessage2", resourceBundle.f_get("MANY_RESULTS_MESSAGE2"));			
			}			
			
			td=f_core.CreateElement(tBodyContainer, "tr", { 
				height: pagerHeight+4
			}, "td", {
				align: "center", 
				valign: "middle" });

			var psc="fa_dataGridPopup_pager";
			var ppsc=f_core.GetAttributeNS(this,"pagerStyleClass");
			if (ppsc) {
				psc+=" "+ppsc;
			}
				
			pager=f_pager.Create(td, 
				this, 
				":"+dataGrid.id,
				psc);
			this._pager=pager;			
			
			pager.setAttribute("role", "description");        
			pager.setAttribute("aria-relevant", "additions all");
			pager.setAttribute("aria-atomic", "true");
			pager.setAttribute("aria-live", "polite");
		}
				
		var self=this;
		dataGrid.f_addEventListener(f_event.SELECTION, function(event) {
			f_core.Debug(fa_dataGridPopup, "f_constructDataGrid.SELECTION: selection detail="+event.f_getDetail());
			
			//document.title="Detail: "+event.f_getDetail();

			if (!(event.f_getDetail() & f_event.ACTIVATE_DETAIL)) {
				return;
			}

			return self._rowSelection(event.f_getSelectionProvider(), event.f_getJsEvent());			
		});
		
		if (f_core.IsGecko()) {
			parent.style.height=(parent.scrollHeight+2)+"px";
		}
		
		return dataGrid;
	},
	/**
	 * @method hidden
	 * @param String html
	 * @return void
	 */
	f_setGridInnerHTML: function(html) {
		this._dataGridInnerHtml=html;
	},
	/**
	 * @method hidden
	 * @param Object... columns
	 * @return void
	 */
	f_setColumns2: function(columns) {
		this._columns=f_core.PushArguments(null, arguments);
	},
	/**
	 * @method hidden
	 * @param optional Event jsEvent
	 * @return void
	 */
	f_closeDataGridPopup: function(jsEvent) {
		f_core.Debug(fa_dataGridPopup, "f_closeDataGridPopup: event="+jsEvent);

		this._closePopupDate=new Date().getTime(); // Pour Webkit !
		
		fa_dataGridPopup._ClosePopup(this, jsEvent);
		
		var self=this;
		window.setTimeout(function() {
			var popup=self;
			self=null;
		
			popup.f_setFocus();
		}, 10);
	},
	/**
	 * @method hidden
	 * @param optional Event jsEvent
	 * @param optional String text
	 * @param optional Number autoSelect
	 * @return Boolean Returns <code>true</code> if success.
	 */
	f_openDataGridPopup: function(jsEvent, text, autoSelect) {
		f_core.Debug(fa_dataGridPopup, "f_openDataGridPopup: jsEvent="+jsEvent+" text='"+text+"' autoSelect="+autoSelect);

		var popupOpened=this._popupOpened;
		if (!popupOpened) {
			if (this.f_fireEvent(f_event.MENU, jsEvent)===false) {
				return false;
			}
			
			// var offsetX=0;
			// var offsetY=this.offsetHeight;
			
			var self=this;
			fa_dataGridPopup._OpenPopup(this, this, 0, this.offsetHeight, function() {
				self._updateDataGridPopup(text);
			}); 

		} else {
			this._updateDataGridPopup(text);
		}
		
		return false;
	},
	/**
	 * @method private
	 * @param optional String text
	 * @return void
	 */
	_updateDataGridPopup: function(text) {
		var dataGrid=this._dataGrid;
		if (!dataGrid) {
			return;
		}
		
		dataGrid.f_setAutoSelection(1);
		
		var pager=this._pager;
		if (pager) {
			var parent=pager.parentNode;
			parent.removeChild(pager);
			parent.appendChild(pager);
		}
		
		var filterProperties=this.f_getFilterProperties();
		if (text) {
			filterProperties.text=text;
		}
		
		dataGrid.f_setFilterProperties(filterProperties); 
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_clickOutside: function(jsEvent) {
		
		if(!jsEvent) {
			jsEvent = f_core.GetJsEvent(this);
		}
		
		if (jsEvent.type == f_event.BLUR) {
			// on ne gère pas les events de type blur afin de ne pas fermer la popup au changement de focus
			return true;
		}
		
		f_core.Debug(fa_dataGridPopup, "_clickOutside: popup click outside");
		fa_dataGridPopup.LAST_OUTSIDE = jsEvent.timeStamp;
		this.f_closeDataGridPopup(jsEvent);
		return false;	
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_isDataGridPopupOpened: function() {
		return this._popupOpened;
	},
	/**
	 * @method hidden
	 * @return f_dataGrid
	 */
	f_getDataGrid: function() {
		return this._dataGrid;
	},
	
	/**
	 * @method hidden
	 * @param Number autoSelection
	 * @return void
	 */
	f_changeSelection: function(autoSelection) {
		f_core.Assert(typeof(autoSelection)=="number", "fa_dataGridPopup.f_changeSelection: Invalid autoSelection parameter ("+autoSelection+")");		

		var dataGrid=this.f_getDataGrid();
		if (!dataGrid) {
			return;
		}
		dataGrid.f_performAutoSelection(autoSelection);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_clearAllGridRows: function() {

		var dataGrid=this.f_getDataGrid();
		if (!dataGrid) {
			return;
		}
		dataGrid.f_clearAll();
	},
	/**
	 * @method private
	 * @param f_dataGrid dataGrid
	 * @param Event jsEvent
	 * @return void
	 */
	_rowSelection: function(dataGrid, jsEvent) {
		f_core.Debug(fa_dataGridPopup, "_rowSelection: dataGrid.selection="+dataGrid.f_getSelection());

		this._focusNext=false;

		var selection=dataGrid.f_getSelection();
		
		if (!selection.length) {
			return;
		}
		
		var first=selection[0];
		
		var array=dataGrid.f_getRowValues(first);
		var values=dataGrid.f_getRowValuesSet(first);

		this.f_closeDataGridPopup(jsEvent);

		if (values) {
			for(var name in values) {
				array[name]=values[name];
			}
		}
		
		var message=f_core.FormatMessage(this._valueFormat, array);
		
		var valueColumnId=this._valueColumnId;
		if (!valueColumnId) {
			valueColumnId=0;
		}
		
		var value=array[valueColumnId];
		
		// A cause de IE !!!! ARG !
		var self=this;
		window.setTimeout(function() {
			popup=self;
			self=null;
			
			popup.fa_valueSelected(value, message, values, popup._focusNext);
		}, 0);
	},
	/**
	 * @method private
	 * @param Event jsEvt
	 * @return void 
	 */
	_onSearchClick: function(jsEvt) {
		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			window.clearTimeout(timerId);
		}
		
		var text=this._searchInput.value;

		// Filtrer la valeur
		this.f_openDataGridPopup(null, text);
	},
	/**
	 * @method private
	 * @param Event jsEvt
	 * @return Boolean 
	 */
	_onSearchSuggest: function(jsEvt) {
		f_core.Debug(fa_dataGridPopup, "_onSearchSuggest: Charcode ("+jsEvt.keyCode+")");

		var cancel=false;
	//	var value=this.f_getValue();
		
		switch(jsEvt.keyCode) {
		case f_key.VK_DOWN:
		case f_key.VK_UP:
		case f_key.VK_PAGE_DOWN:
		case f_key.VK_PAGE_UP:
			//var direction=(jsEvt.keyCode==f_key.VK_DOWN)?1:-1;
			
			//this._dataGrid.f_performKeyDown(jsEvt);
			// On chosi SANS SELECTIONNER en haut ou en bas du datagrid
			break;

		case f_key.VK_ENTER:
		case f_key.VK_RETURN:
		case f_key.VK_TAB:
			// On selectionne puis on ferme !
	
			this.f_closePopup(jsEvt);
			return true;
		}
		
		var newInputValue=this._searchInput.value;
		if (newInputValue==this._lastValue) {
			f_core.Debug(fa_dataGridPopup, "_onSearchSuggest: Same value ! (value='"+newInputValue+"' / last='"+this._lastValue+"')");
			return true;
		}
		this._lastValue=newInputValue;
		
		var keyCode=jsEvt.keyCode;
		
		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			window.clearTimeout(timerId);
		}
		
		var suggestionDelayMs=this.f_getSuggestionDelayMs();		
		if (suggestionDelayMs<1) {
			return true;
		}
		
		var pager=this._pager;
		if (pager) {
			// Pour la vocalisation ... il faut y aller franco !
			for (; pager.firstChild;) {
				pager.removeChild(pager.firstChild);
			}
		}
		
		f_core.Debug(fa_dataGridPopup, "_onSearchSuggest: Set timeout to "+suggestionDelayMs);
		
		var delay=suggestionDelayMs;

		var self=this;
		this._timerId=window.setTimeout(function() {
			var popup=self;
			self=null;
		
			if (window._rcfacesExiting) {
				return;
			}
						
			try {
				popup._onSuggestTimeOut();
				
			} catch (x) {
				f_core.Error(fa_dataGridPopup, "_onSearchSuggest.timer: Timeout processing error !", x);
			}
		}, delay);
		
		if (cancel) {
			return f_core.CancelJsEvent(jsEvt);
		}
		
		return true;
	},
	/**
	 * @method private
	 */
	_onSuggestTimeOut: function(text) {
		if (!text && this._searchInput) {
			text=this._searchInput.value;
		}
		
		var minChars=this.f_getSuggestionMinChars();
		f_core.Debug(fa_dataGridPopup, "_onSuggestTimeOut: text='"+text+"'. (minChars="+minChars+")");

		if (minChars>0 && text.length<minChars) {
			return;
		}

		// Filtrer la valeur
		this.f_openDataGridPopup(null, text);
	},

	/**
	 * @method protected abstract
	 * @param String value
	 * @param String label
	 * @param Object rowValues values of row
	 * @return optional Boolean focusNext
	 * @return void
	 */	
	fa_valueSelected: f_class.ABSTRACT
};

new f_aspect("fa_dataGridPopup", __statics, __members, fa_filterProperties);
