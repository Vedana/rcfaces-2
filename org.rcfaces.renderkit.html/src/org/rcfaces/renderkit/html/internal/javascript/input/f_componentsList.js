/*
 * $Id: f_componentsList.js,v 1.5 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 * @class public f_componentsList extends f_component, fa_pagedComponent
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:28 $
 */

var __statics = {
	/**
	 * @method private static
	 */
	_InitializeScrollbars: function(componentsList) {
		if (!componentsList._scrollBody) {
			return;
		}
		
		var pos=componentsList._initialHorizontalScrollPosition;
		if (pos) {
			componentsList._scrollBody.scrollLeft=pos;
			if (componentsList._scrollTitle) {
				componentsList._scrollTitle.scrollLeft=pos;
			}
		}
		
		pos=componentsList._initialVerticalScrollPosition;
		if (pos) {
			componentsList._scrollBody.scrollTop=pos;
			if (componentsList._scrollTitle) {
				componentsList._scrollTitle.scrollTop=pos;
			}
		}
	}
};
 
var __members = {
	
	f_componentsList: function() {
		this.f_super(arguments);
	
		this._updateScrollComponents();
	},
	f_finalize: function() {
		this._scrollBody=undefined;
		this._tbody=undefined;

		this._nextCommand=undefined; // function

		// this._loading=undefined; // boolean
		this._waiting=undefined;

		// this._oldHeight=undefined; // boolean 
		// this._oldHeightStyle=undefined; // string
		
		this.f_super(arguments);
	},
	_updateScrollComponents: function() {
		var scrollBody=this.ownerDocument.getElementById(this.id+"::table");
		this._scrollBody=scrollBody;

		if (scrollBody.tagName.toUpperCase()=="TABLE") {	
			this._tbody=scrollBody.tBodies[0];
			
		} else {
			this._tbody=scrollBody;
		}
	},
	f_update: function() {
		this.f_super(arguments);
		
		this.f_performPagedComponentInitialized();
		
		/*
		if (!this.f_isVisible()) {
			this.f_getClass().f_getClassLoader().f_addVisibleComponentListener(this);			
		}
		*/
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		return this;	
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_documentComplete: function() {
		this.f_super(arguments);

	//	this._documentComplete=true;

		if (!this.f_isVisible()) {
			return;
		}
		
		this.f_performComponentVisible();
	},
	/**
	 * @method hidden
	 */
	f_performComponentVisible: function() {
		
		f_componentsList._InitializeScrollbars(this);		

		if (this._interactiveShow) {
			this.f_setFirst(this._first, this._currentCursor);			
		}
	},
	/**
	 * Specify the index of the first row which starts the grid.
	 *
	 * @method public
	 * @param Number index
	 * @param optional Number cursorIndex The cursor index. (can be undefined)
	 * @param optional hidden Event jsEvent
	 * @return Boolean Returns <code>false</code>.
	 */
	f_setFirst: function(index, cursorIndex, jsEvent) {
		//var oldFirst=this._first;
		
		this.f_setProperty(f_prop.FIRST, index);
	
		if (this._interactive) {
			this._appendCommand(function(dataGrid) {
				dataGrid.f_callServer(index, cursorIndex);
			});
			
			return false;
		}

		f_core._Submit(null, this, f_event.CHANGE);
			
		return false;
	},
	/**
	 * @method private
	 * @return void
	 */
	_appendCommand: function(callBack) {
		if (!this._loading) {
			callBack.call(this, this);
			return;
		}
		
		this._nextCommand=callBack;
	},
	/**
	 * @method private
	 * @return void
	 */
	_processNextCommand: function() {
		var nextCommand=this._nextCommand;
		if (!nextCommand) {
			return;
		}
		
		this._nextCommand=undefined;
		
		nextCommand.call(this, this);
	},
	/**
	 * @method private
	 * @return void
	 */
	f_callServer: function(firstIndex, cursorIndex) {
//		f_core.Assert(!this._loading, "Already loading ....");
		
		var params=new Object;
		params.componentsListId=this.id;
		params.index=firstIndex;
		
		var filterExpression=this.fa_getSerializedPropertiesExpression();
		if (filterExpression) {
			params.filterExpression=filterExpression;
		}

		var tbody=this._tbody;

		//var scrollBody=this._scrollBody;
		if (!this._oldHeight) {
			this._oldHeight=true;
			this._oldHeightStyle=this.style.height;
			this.style.height=this.offsetHeight+"px";
		}
		
		if (tbody) {	
			this.f_getClass().f_getClassLoader().f_garbageObjects(false, tbody);

			while (tbody.hasChildNodes()) {
				tbody.removeChild(tbody.lastChild);
			}			
			
			this.f_getClass().f_getClassLoader().f_completeGarbageObjects();
		}

		var request=new f_httpRequest(this, f_httpRequest.JAVASCRIPT_MIME_TYPE);
		var componentsList=this;
		request.f_setListener({
			/**
			 * @method public
			 */
	 		onInit: function(request) {
	 			var waiting=componentsList._waiting;
	 			if (!waiting) {	
	 				waiting=f_waiting.Create(componentsList);
	 				componentsList._waiting=waiting;
	 			}
	 			
	 			waiting.f_setText(f_waiting.GetLoadingMessage());
	 			waiting.f_show();
	 		},
			/**
			 * @method public
			 */
	 		onError: function(request, status, text) {
	 			f_core.Info(f_componentsList, "Bad status: "+status);
 			
	 			var continueProcess;
	 			
	 			try {
	 				continueProcess=componentsList.f_performErrorEvent(request, f_error.HTTP_ERROR, text);
	 				
	 			} catch (x) {
	 				// On continue coute que coute !
	 				continueProcess=false;
	 			}	 				
	 				 				 			 			
		 		if (continueProcess===false) {
					componentsList._loading=false;		
					
					var waiting=componentsList._waiting;
					if (waiting) {
						waiting.f_hide();
					}
					return;
				}
					 			
				if (componentsList._nextCommand) {
					componentsList._processNextCommand();
					return;
				}

				componentsList._loading=false;		
				
				var waiting=componentsList._waiting;
				if (waiting) {
					waiting.f_hide();
				}
	 		},
			/**
			 * @method public
			 */
	 		onProgress: function(request, content, length, contentType) {
	 			var waiting=componentsList._waiting;
				if (waiting) {
					waiting.f_setText(f_waiting.GetReceivingMessage());
				}	 			
	 		},
			/**
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {			
				if (componentsList._nextCommand) {
					componentsList._processNextCommand();
					return;
				}

	 			var waiting=componentsList._waiting;
				try {
					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
						componentsList.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
						return;
					}

					var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
					if (!cameliaServiceVersion) {
						componentsList.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
						return;					
					}
	
					var responseContentType=request.f_getResponseContentType().toLowerCase();
					
					if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
						var code=f_error.ComputeApplicationErrorCode(request);
				
				 		componentsList.f_performErrorEvent(request, code, content);
						return;
					}
	
					if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)<0) {
				 		componentsList.f_performErrorEvent(request, f_error.RESPONSE_TYPE_SERVICE_ERROR, "Unsupported content type: "+responseContentType);
						return;
					}
				
					var ret=request.f_getResponse();
					
					//alert("ret="+ret);
					try {
						f_core.WindowScopeEval(ret);
						
					} catch (x) {
			 			componentsList.f_performErrorEvent(x, f_error.RESPONSE_EVALUATION_SERVICE_ERROR, "Evaluation exception");
					}
					
				} finally {				
					componentsList._loading=false;

					if (waiting) {
						waiting.f_hide();
					}
				}
	
				var event=new f_event(componentsList, f_event.CHANGE);
				try {
					componentsList.f_fireEvent(event);
					
				} finally {
					f_classLoader.Destroy(event);
				}
	 		}			
		});

		this._loading=true;
		request.f_setRequestHeader("X-Camelia", "componentsList.update");
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!params) {
				params={};
			}
			params["javax.faces.behavior.event"]= "componentsList.update";
			params["javax.faces.source"]= this.id;
			params["javax.faces.partial.execute"]= this.id;
		}
		request.f_doFormRequest(params);
	},
	/**
	 * @method protected
	 */
	f_performErrorEvent: function(param, messageCode, message) {

		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_startNewPage: function(rowIndex) {
		// Appeler par la génération du serveur !

		//var scrollBody=this._scrollBody;
		if (this._oldHeight) {
			this.style.height=this._oldHeightStyle;
			this._oldHeight=undefined;
			this._oldHeightStyle=undefined;
		}

		var tbody=this._tbody;
		if (tbody) {
			while (tbody.hasChildNodes()) {
				tbody.removeChild(tbody.lastChild);
			}	
		}
		
		this._first=rowIndex;
		
		this.fa_componentUpdated=false;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_updateNewPage: function(rowCount, buffer) {
		// Appeler par la génération du serveur !

		var component=this._tbody;

		if (f_core.IsInternetExplorer()) {
			var b=this._scrollBody.outerHTML;
			
			var pos=b.lastIndexOf("</TBODY>");
			
			buffer=b.substring(0, pos)+buffer+b.substring(pos);
			
			this.removeChild(this._scrollBody);
			
			component=this;
		}
		
		this.f_getClass().f_getClassLoader().f_loadContent(this, component, buffer);

		if (component==this) {
			this._updateScrollComponents();
		}
			
		if (rowCount>0) {
			this._rowCount=rowCount;
		}

		if (this._rowCount<0) {
			if (this._maxRows<this._first+this._rows) {
				this._maxRows=this._first+this._rows;
			}
		}

		this.fa_componentUpdated=true;

		if (this._interactiveShow) {
			this._interactiveShow=undefined;
		}

		this.f_performPagedComponentInitialized();
	},
	/**
	 * @method hidden
	 */
	fa_cancelFilterRequest: function() {
		// Appeler par la génération du serveur !
	},
	fa_updateFilterProperties: function(filterProperties) {
		if (!this._interactive) {
			return false;
		}
		
		this._appendCommand(function(dataGrid) {
			if (dataGrid._rows>0) {
				// Page par page !
				// On ne sait plus le nombre de lignes ...
				dataGrid._rowCount=-1;
				dataGrid._maxRows=dataGrid._rows;
			}
			
			dataGrid.f_callServer(0);
		});
		
		return false;
	}
};

new f_class("f_componentsList", {
	extend: f_component,
	aspects: [ fa_pagedComponent ],
	statics: __statics,
	members: __members
});