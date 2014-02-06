/* 
 * $Id: f_abstractList.js,v 1.5 2013/11/13 12:53:28 jbmeslin Exp $
 */


/**
 * 
 * @class hidden abstract f_abstractList extends f_input, fa_required, fa_filterProperties, fa_commands, fa_immediate
 * @author Olivier Oeuillot
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __statics = {

	/**
	 * @field private static final Number
	 */
	_MIN_WIDTH: 128,
	
	/**
	 * @method private static final
	 * @param Event evt
	 * @return Boolean
	 * @context object:combo
	 */
	_OnChange: function(evt) {
		var combo=this;
		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		return combo.f_fireEvent(f_event.SELECTION, evt, null, combo.f_getValue(), combo);
	}
};

var __members = {

	/*
	f_abstractList: function() {
		this.f_super(arguments);
	},
	*/

	f_finalize: function() {
//		this._loading=undefined; // boolean
		this._waiting=undefined; // f_waiting
//		this._oldWidth=undefined; // String

		// Pour l'instant il n'y a pas de variables attachées aux OPTIONs
	
		this.f_super(arguments);
	},
	/**
	 * @method public 
	 * @param String value
	 * @return Object The item object or <code>null</code> if the item is not found.
	 */
	f_getItemByValue: function(value) {
		if (value.nodeType==f_core.ELEMENT_NODE && value.tagName.toLowerCase()=="option") {
			return value;
		}
		
		var items = this.options;
		for (var i=0; i<items.length; i++) {
			var item = items[i];
			if (item.value!=value) {
				continue;
			}
			
			return item;
		}
		return null;
	},
	/**
	 * Returns the index of the first selected value.
	 *
	 * @method public
	 * @return Number Index of the first selected value.
	 */
	f_getSelectedIndex: function() {
		return this.f_getValue(true);
	},
	/**
	 * Specify selection by the index of the selected value.
	 *
	 * @method public
	 * @param Number idx Index of the first selected value.
	 * @return void
	 */
	f_setSelectedIndex: function(idx) {
		this.f_setValue(idx, true);
	},
	/**
	 * Returns the value of item specified by an index.
	 *
	 * @method public
	 * @param Number idx Index of the item.
	 * @return String Value of the item.
	 */
	f_getValueFromIndex: function(idx) {
		f_core.Assert(typeof(idx)=="number", "f_abstractList.f_getValueFromIndex: Invalid idx parameter.");
		f_core.Assert(idx<0, "f_abstractList.f_getValueFromIndex: Index parameter is out of range (0<="+idx+"<"+items.length+").");

		var items = this.options;
		
		if (idx<0 || idx>=items.length) {
			return null;
		}
		
		return items[idx].value;
	},
	/**
	 * Remove specified items
	 * 
	 * @method public
	 * @param String... value
	 * @return Number Number of removed items.
	 */
	f_clear: function(value) {		
		var cnt=0;
		var input=this.f_getInput();
		
		for(var j=0;j<arguments.length;j++) {
			value=arguments[j];
			
			var items = input.options; // On recharge à chaque fois !
			
			for (var i=0; i<items.length; i++) {
				var item=items[i];
				
				if (items.value != value) {
					continue;
				}
				
				input.removeChild(item);

				cnt++;
				break;
			}
		}
		
		return cnt;
	},
	/**
	 * Remove specified items.
	 * 
	 * @method public
	 * @param any[] values List of values whose specified items.
	 * @return Number Number of removed items.
	 */
	f_clearArray: function(values) {
		f_core.Assert(values instanceof Array, "f_abstractList.f_clearArray: Invalid values parameter '"+values+"'.");

		return this.f_clear.apply(this, values);
	},
	/**
	 * Remove all items.
	 * 
	 * @method public
	 * @return Number Number of removed rows.
	 */
	f_clearAll: function() {
		var input=this.f_getInput();
		var items=input.items;
		var cnt=items.length;
		if (!cnt) {
			return 0;
		}
		
		// items risque d'etre modifié au fur & à mesure des removeChilds
		for(var i=cnt-1;i>=0;i--) {
			input.removeChild(items[i]);
		}
		
		this._waiting=null;
		
		return cnt;
	},
	/**
	 * Returns the index of item specified by its value.
	 *
	 * @method public
	 * @param String val Value of the item
	 * @return Number Index of the item.
	 */
	f_getIndexFromValue: function(val) {
		var items = this.options;
		for (var i=0; i<items.length; i++) {
			if (items[i].value == val) {
				return i;
			}
		}
		return -1;
	},
	f_serialize: function() {
		// On sait jamais ... dés fois que !
		var waiting=this._waiting;
		if (waiting) {
			// Il faut interdire la modification !
			this._waiting=undefined;
			this.removeChild(waiting);
		}

		if (this.f_isDisabled()) {
			var sel = this.f_getValue();
			this.f_setProperty(f_prop.SELECTED_ITEMS, sel, sel instanceof Array);
		}

		this.f_super(arguments);
	},
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			this.onchange = f_abstractList._OnChange;
			return;
		}
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			this.onchange = null;
			return;
		}
		this.f_super(arguments, type, target);
	},
	
	fa_updateFilterProperties: function() {
		this.f_appendCommand(function(combo) {			
			combo._callServer();
		});
	},
	/**
	 * @method private
	 */
	_callServer: function() {
		f_class.IsClassDefined("f_httpRequest", true);
		
		var w=f_core.GetCurrentStyleProperty(this, "width");
		if (f_core.IsGecko()) {
			if (!this.style.width) {
				w=(this.offsetWidth+2)+"px";
			}
		}
		
		// f_core.Info(f_abstractList, "Width="+w);
		
		if (!w || w=="auto") {
			if (!this._oldWidth) {
				this._oldWidth="auto";
				
				this._oldWidth=w;
//				alert("old="+oldWidth);
			}
						
			w=this.offsetWidth;
			if (w<f_abstractList._MIN_WIDTH) {
				w=f_abstractList._MIN_WIDTH;
			}
			this.style.width=w+"px";
		}
		this.className=this.f_computeStyleClass("_loading");
		
		// Effaces les items !
		while (this.hasChildNodes()) {
			this.removeChild(this.lastChild);
		}
 	
		var params=new Object;
		params.componentId=this.id;
		
		var filterExpression=this.fa_getSerializedPropertiesExpression();
		if (filterExpression) {
			params.filterExpression=filterExpression;
		}
	
		var request=new f_httpRequest(this, f_httpRequest.JAVASCRIPT_MIME_TYPE);
		var combo=this;
		request.f_setListener({
			/**
			 * @method public
			 */
	 		onInit: function(request) {
	 		 	var waiting=combo._waiting;
	 			if (!waiting && !combo.childNodes.length) {
		 			waiting=combo.ownerDocument.createElement("option");
		 			waiting.disabled=true;
		 			
		 			f_core.AppendChild(combo, waiting);
		 			if (combo.size>1) {
		 				// Pas de selection si il y a plusieurs elements affichés dans la liste
		 				combo.selectedIndex=-1;
		 				
		 			} else {
			 			combo.selectedIndex=0;
			 		}
		 					 			
		 			combo._waiting=waiting;
		 		}
		 		
		 		if (waiting) {
					// pas de f_core.SetTextNode  : ca marche pas !
		 			waiting.innerHTML=f_core.EncodeHtml(f_waiting.GetLoadingMessage());
		 			waiting.disabled=true;
		 		}
	 		},	 		
			/**
			 * @method public
			 */
	 		onError: function(request, status, text) {
	 			f_core.Info(f_abstractList, "f_callServer.onError: Bad status: "+status);
	 			
				if (combo.f_processNextCommand()) {
					return;
				}
	 		
				combo._loading=false;		
				
				var waiting=combo._waiting;
				if (waiting) {
					combo._waiting=undefined;
					combo.removeChild(waiting);
				}
				if (combo._oldWidth) {
					combo.style.width=combo._oldWidth;
					combo._oldWidth=undefined;
				}
				
				combo.className=this.f_computeStyleClass();
	 		},
			/**
			 * @method public
			 */
	 		onProgress: function(request, content, length, contentType) {
	 			var waiting=combo._waiting;
				if (waiting) {
					// pas de f_core.SetTextNode  : ca marche pas !
					waiting.innerHTML=f_core.EncodeHtml(f_waiting.GetReceivingMessage());
					waiting.disabled=true;
				}	 			
	 		},
			/**
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {
				if (combo.f_processNextCommand()) {
					return;
				}
	 			
	 			var waiting=combo._waiting;
				combo._waiting=undefined;
				
				try {
					if (waiting) {
						combo.removeChild(waiting);
						waiting=null;
					}
					if (combo._oldWidth) {
						combo.style.width=combo._oldWidth;
						combo._oldWidth=undefined;
					}

					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
						combo.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
						return;
					}
				
					var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
					if (!cameliaServiceVersion) {
						combo.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
						return;					
					}
		
					var responseContentType=request.f_getResponseContentType().toLowerCase();
					
					if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
						var code=f_error.ComputeApplicationErrorCode(request);
				
				 		combo.f_performErrorEvent(request, code, content);
						return;
					}
					
					if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)<0) {
			 			combo.f_performErrorEvent(request, f_error.RESPONSE_TYPE_SERVICE_ERROR, "Unsupported content type: "+responseContentType);
						return;
					}
		
					var ret=request.f_getResponse();
					try {
						f_core.WindowScopeEval(ret);
						
					} catch (x) {
						f_core.Error(f_abstractList, "_callServer.onLoad: Can not eval response '"+ret+"'.", x);
					}

				} finally {
					combo._loading=undefined;	
					combo.className=combo.f_computeStyleClass();
				}
				
				var event=new f_event(combo, f_event.LOAD);
				try {
					combo.f_fireEvent(event);
					
				} finally {
					f_classLoader.Destroy(event);
				}
	 		}
		});

		this._loading=true;
		request.f_setRequestHeader("X-Camelia", "items.request");
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!params) {
				params={};
			}
			params["javax.faces.behavior.event"]= "items.request";
			params["javax.faces.source"]= this.id;
			params["javax.faces.partial.execute"]= this.id;
		}
		request.f_doFormRequest(params);
	},
	/**
	 * @method protected
	 * @override
	 */
	f_performErrorEvent: function(param, messageCode, message) {
		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * Append an item.
	 *
	 * @method public
	 * @param Boolean parent
	 * @param String label
	 * @param String value
	 * @param optional Boolean selected
	 * @param optional Boolean disabled
	 * @param optional String description
	 * @return Object New item.
	 */
	f_appendItem: function(parent, label, value, selected, disabled, description) {
		if (parent) {
			var optgroup=this.ownerDocument.createElement("optgroup");
			if (disabled) {
				optgroup.disabled=true;
			}
			
			// Pas de SetTextNode: ca marche pas !
			optgroup.innerHTML=f_core.EncodeHtml(label);
			
			f_core.AppendChild(this, optgroup);
			return optgroup;
		}
		
		var option=this.ownerDocument.createElement("option");
		option.value=value;
		if (disabled) {
			option.disabled=true;
		}
		if (selected) {
			option.selected=true;
		}
		
		if (description) {
			option.title=description;
		}
		if (arguments.length>5) {
			var values=new Object;
			
			option._clientDatas=values;
			
			for(var i=6;i<arguments.length;i+=2) {
				values[arguments[i]]=arguments[i+1];
			}
		}
		
		// Pas de SetTextNode: ca marche pas !
		option.innerHTML=f_core.EncodeHtml(label);
		
		f_core.AppendChild(this, option);
		
		return option;
	},
	/**
	 * Returns the disabled state of an item 
	 * @method public
	 * @param String itemValue Value of the item or the item object.
	 * @return Boolean Disable state.
	 */
	f_isItemDisabled: function(itemValue) {
		var item=this.f_getItemByValue(itemValue);
		if (!item) {
			return null;
		}
		
		return item.disabled;
	},
	/**
	 * Returns the description of an item 
	 * @method public
	 * @param String itemValue Value of the item or the item object.
	 * @return String Description of the item.
	 */
	f_getItemToolTip: function(itemValue) {
		var item=this.f_getItemByValue(itemValue);
		if (!item) {
			return null;
		}
		
		return item.title;
	},
	/**
	 * Returns the label of an item 
	 * @method public
	 * @param String itemValue Value of the item or the item object.
	 * @return String Label of the item.
	 */
	f_getItemLabel: function(itemValue) {
		var item=this.f_getItemByValue(itemValue);
		if (!item) {
			return null;
		}
		
		return item.innerHTML;
	},
	/**
	 * Returns a value of a property.
	 * 
	 * @method public
	 * @param String itemValue Value of the item or the item object.
	 * @param String name Property name.
	 * @return String Value associated to the specified property.
	 */
	f_getItemClientData: function(itemValue, name) {
		var set=this.f_getItemClientSet(itemValue);
		if (!set) {
			return null;
		}
		
		return set[name];
	},
	/**
	 * Returns all the values associated to an item specified by its value.
	 * 
	 * @method public
	 * @param String itemValue Value of the item or the item object.
	 * @return String Value associated to the specified property.
	 */
	f_getItemClientSet: function(itemValue) {
		var item=this.f_getItemByValue(itemValue);
		if (!item) {
			return null;
		}
		
		var clientDatas=item._clientDatas;
		if (clientDatas) {
			return clientDatas;
		}

		clientDatas=f_core.ParseDataAttribute(item);
		item._clientDatas=clientDatas;
		
		return clientDatas;
	},
	/**
	 * @method hidden
	 */
	fa_cancelFilterRequest: function() {
	}
};

new f_class("f_abstractList", {
	extend: f_input,
	aspects: [ fa_required, fa_filterProperties, fa_commands, fa_immediate ],
	members: __members,
	statics: __statics
});