/*
 * $Id: f_service.js,v 1.5 2013/12/12 15:39:12 jbmeslin Exp $
 */
 
/**
 * Service class.
 *
 * @class public f_service extends f_object, fa_serializable, fa_eventTarget, fa_filterProperties, fa_commands
 */
var __statics= {
	/**
	 * @field private static final String
	 */
	_CAMELIA_CONTENT_TYPE: "X-Camelia-Content-Type",
	
	/**
	 * @field public static final Number
	 */
	INIT_STATE: 0,
	
	/**
	 * @field public static final Number
	 */
	REQUESTING_STATE: 1,
	
	/**
	 * @field public static final Number
	 */
	LOADING_STATE: 10,
	
	/**
	 * @field public static final Number
	 */
	LOADED_STATE: 20,
	
	/**
	 * @field public static final Number
	 */
	ERRORED_STATE: 21,

	/**
	 * @field private static final Number
	 */
	_TOTAL_WORK_PROGRESS_MONITOR: 20,
	
	/**
	 * @field private static final Number
	 */
	_INIT_PROGRESS_MONITOR: 1,

	/**
	 * @field private static final Number
	 */
	_LOADING_PROGRESS_MONITOR: 1,

	/**
	 * @field private static final Number
	 */
	_LOADED_PROGRESS_MONITOR: 1,

	/**
	 * @field private static final Number
	 */
	_RUN_PROGRESS_MONITOR: 17,

	/**
	 * @field private static Number
	 */
	_Id: 0,

	/**
	 * @field private static final
	 */
	_EVENTS: {
		error: f_event.ERROR,
		propertyChange: f_event.PROPERTY_CHANGE,
		user: f_event.USER
	}
	
};

var __members={

	f_service: function() {
		this.f_super(arguments);
		
		this._serviceId=f_core.GetAttributeNS(this, "serviceId");
		
		var events=f_core.GetAttributeNS(this, "events");
		if (events) {
			this.f_initEventAtts(f_service._EVENTS, events);
		}
	},
	f_finalize: function() {
		// this._serviceId=undefined;  // string
		// this._loading=undefined; // boolean
		this._requests=undefined; // object[]
		// this._progressing=false; // boolean
		
		this.f_super(arguments);
	},
	_setRequestState: function(requestId, state) {
		var requests=this._requests;
		if (!requests) {
			requests=new Object;
			this._requests=requests;
		}

		requests[requestId]=state;		
	},
	/**
	 * @method public
	 * @param String requestId Request identifier. (returned by f_asyncCall() )
	 * @return Number State of the request, or -1 if the request is unknown !
	 * @see #f_asyncCall
	 */
	f_getRequestState: function(requestId) {
		var requests=this._requests;
		if (!requests) {
			return -1;
		}
		
		var state=requests[requestId];
		if (state===undefined) {
			return -1;
		}
		
		return state;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getServiceId: function() {
		return this._serviceId;
	},
	/**
	 * @method hidden
	 * @return String
	 */
	_allocateRequestId: function() {
		return (f_service._Id++)+"."+(new Date().getTime())*Math.random();
	},
	/**
	 * @method public
	 * @param function resultCallback Callback which will be called, when the result has been received.
	 * @param any parameter Parameters of the request.
	 * @param optional f_progressMonitor progressMonitor Progress monitor associated to the call.
	 * @param optional Number contentSizePercent
	 * @return String Request identifier.
	 */
	f_asyncCall: function(resultCallback, parameter, progressMonitor, contentSizePercent) {
		
		if (f_core.DesignerMode) {
			throw new Error("Designer mode enabled");
		}
		
		var requestId=this._allocateRequestId();
		
		this._setRequestState(requestId, f_service.INIT_STATE);
		
		if (progressMonitor && contentSizePercent===undefined) {
			contentSizePercent=100;
		}
		
		this.f_appendCommand(function(service) {			
			service._asyncCallServer(requestId, resultCallback, parameter, progressMonitor, contentSizePercent);
		});
		
		return requestId;
	},
	/**
	 * @method public
	 * @param any parameter Parameters of the request. (Supported types: String, object, and number)
	 * @param optional f_progressMonitor progressMonitor Progress monitor associated to the call.
	 * @return any Result of request.
	 */
	f_syncCall: function(parameter, progressMonitor) {
		
		if (f_core.DesignerMode) {
			throw new Error("Designer mode enabled");
		}

		var requestId=this._allocateRequestId();
		
		var subProgressMonitor=undefined;
		if (progressMonitor) {
			subProgressMonitor=f_subProgressMonitor.f_newInstance(progressMonitor, f_service._TOTAL_WORK_PROGRESS_MONITOR);
		}
		
		this._setRequestState(requestId, f_service.INIT_STATE);

		var request=new f_httpRequest(this);
		var params=this._prepareRequest(request, requestId, parameter);

		// var ret=
		this._sendRequest(request, params, subProgressMonitor);
		
		var state= f_service.ERRORED_STATE;
		try {	
			if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
				var errorMessage="Bad http response status ! ("+request.f_getStatusText()+")";
				this.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, errorMessage);
				throw new Error(errorMessage);
			}	
	
			var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
			if (!cameliaServiceVersion) {
				var errorMessage="Not a service response !";
				this.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
				throw new Error(errorMessage);
			}
	
			var responseContentType=request.f_getResponseContentType().toLowerCase();
			if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
				var code=f_error.ComputeApplicationErrorCode(request);
				
				var content=request.f_getResponse();
		 		this.f_performErrorEvent(request, code, content);				

				throw new Error("Application error");
			}
	
			if (request.f_isXmlResponse()) {
				state = f_service.LOADED_STATE;
				return request.f_getXmlResponse();
			}
			
			var content=request.f_getResponse();
	
			
			if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)>=0) {
				// Il nous faut un retour ... on a pas trop le choix concernant l'Eval		
				content=f_core.WindowScopeEval(content);
				
			} else {					
				content = this.f_decodeResponse(content, responseContentType, request);
	
				state = f_service.LOADED_STATE;
			}
			
		} finally {
			this._setRequestState(requestId, state);
			
			if (subProgressMonitor) {
				subProgressMonitor.f_done();
			}
		}
		
		return content;
	},
	/**
	 * @method private
	 */
	_prepareRequest: function(request, requestId, parameter) {
		var params=undefined;
		var type;		
				
		if (parameter==null) {
			type="null";

		} else if (typeof(parameter)=="object") {	
			if (parameter.nodeType==f_core.DOCUMENT_NODE) {
				
				if (parseInt(_rcfaces_jsfVersion) >= 2) { // JSF 2.0
					type="object";
					params = {
						type: type,
						data: f_core.EncodeObject(parameter)
					};
					
				} else { // JSF 1.x
					type="xml";
					params=parameter;
				}
				
			} else {
				type="object";
				params=new Object;
				params.type=type;
				params.data=f_core.EncodeObject(parameter);
			}
		} else {
			type="string";
				params=new Object;
			params.type=type;
			params.data=String(parameter);
		}
	
		if (!type && params) {
			var filterExpression=this.fa_getSerializedPropertiesExpression();
			if (filterExpression) {
				params.filterExpression=filterExpression;
			}
		}
		
		request.f_setRequestHeader("X-Camelia", "client.newService");
		request.f_setRequestHeader("X-Camelia-Request-Id", requestId);
		request.f_setRequestHeader("X-Camelia-Component-Id", this.id);
		if (type) {
			request.f_setRequestHeader(f_service._CAMELIA_CONTENT_TYPE, type);
		}
		
		if (parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader(f_httpRequest.FACES_REQUEST_HEADER_NAME, f_httpRequest.FACES_PARTIAL_AJAX_REQUEST);
			
			if (!params) {
				params={};
			}
			params[f_httpRequest.FACES_BEHAVIOR_EVENT_PARAMETER_NAME]= "service";
			params[f_httpRequest.FACES_BEHAVIOR_SOURCE_PARAMETER_NAME]= this.id;
			params[f_httpRequest.FACES_BEHAVIOR_EXEC_PARAMETER_NAME]=this.id;
		}
		
		return params;
	},
	/**
	 * @method private
	 * @return void
	 */
	_asyncCallServer: function(requestId, resultCallback, parameter, progressMonitor, showLoading) {
		var request=new f_httpRequest(this);
		var params=this._prepareRequest(request, requestId, parameter);
		
		var subProgressMonitor=undefined;
		if (progressMonitor) {
			var total=(showLoading)?f_service._TOTAL_WORK_PROGRESS_MONITOR:1;

			subProgressMonitor=f_subProgressMonitor.f_newInstance(progressMonitor, total);
		}
		
		this._progressing=false;
		
		var service=this;
		request.f_setListener({
			/**
			 * @method public
			 */
			onInit: function(request) {						
				service._setRequestState(requestId, f_service.REQUESTING_STATE);
				
				if (showLoading && subProgressMonitor) {
					subProgressMonitor.f_work(f_service._INIT_PROGRESS_MONITOR);
				}
			},
			/**
			 * @method public
			 */
			onProgress: function(request) {			
				if (service._progressing) {
					return;
				}
				service._progressing=true;
							
				service._setRequestState(requestId, f_service.LOADING_STATE);

				if (showLoading && subProgressMonitor) {
					subProgressMonitor.f_work(f_service._LOADING_PROGRESS_MONITOR);
				}
			},
			/**
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {
				if (showLoading && subProgressMonitor) {
					subProgressMonitor.f_work(f_service._LOADED_PROGRESS_MONITOR);
				}

	 			var loading=false;
				try {
					var state = f_service.ERRORED_STATE;					
					
					var call=false;
					
					var applicationError=false;
					
					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
		
						content=null;
						service.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
												
					} else {
						var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
						if (!cameliaServiceVersion) {
							content=null;
							service.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
							
						} else {			
							call=true;

							var responseContentType=request.f_getResponseContentType().toLowerCase();
							
							if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
								// Rien,  etat erreur, le contenu contient les infos de l'erreur
								applicationError=true;
							
							} else if (contentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)>=0) {
								f_core.WindowScopeEval(content);
								
								content=null;
								
							} else {		
								content = service.f_decodeResponse(content, contentType, request);
								
								state = f_service.LOADED_STATE;
							}
						}
					}
					
					service._setRequestState(requestId, state);
				
					if (call) {
						var pm=subProgressMonitor; 
					
						if (showLoading) {
							pm=f_subProgressMonitor.f_newInstance(pm, f_service._RUN_PROGRESS_MONITOR);
						}						
						
						try {
							resultCallback.call(service, state, parameter, content, pm);
	
						} catch (x) {
							f_core.Error(f_service, "_asyncCallServer.onLoad: Call of callback throws an exception : "+resultCallback+".", x);
						}
						
						if (showLoading) {
							pm.f_done();
						}
					}
					
					if (subProgressMonitor) {
						subProgressMonitor.f_done();
					}		
					
					if (applicationError) {
						var code=f_error.ComputeApplicationErrorCode(request);
						 		
						if (service.f_performErrorEvent(request, code, content)===false) {
							loading=undefined;
							return;
						}
					}	
					
					if (service.f_processNextCommand()) {
						loading=true;
						return;
					}					
				
				} finally {
					service._loading=loading;	
				}
	 		},
			/**
			 * @method public
			 */
	 		onError: function(request, status, text) {
				var state=f_service.ERRORED_STATE;

				service._setRequestState(requestId, state);
			
				try {
					resultCallback.call(service, state, parameter, undefined);

				} catch (x) {
					f_core.Error(f_service, "_asyncCallServer.onError: Call of callback throws an exception : "+resultCallback+".", x);
				}

	 			f_core.Info(f_service, "_asyncCallServer.onError: Bad status: "+status);

				if (subProgressMonitor) {
					subProgressMonitor.f_done();
				}			

				if (service.f_performErrorEvent(request, f_error.HTTP_ERROR, text)===false) {
					service._loading=undefined;
					return;
				}
	 			
				if (service.f_processNextCommand()) {
					return;
				}
	 		
				service._loading=undefined;		
	 		}
		});

		this._loading=true;
		this._sendRequest(request, params, progressMonitor);
	},
	/**
	 * @method private
	 * @param Object request
	 * @param any params
	 * @param f_progressMonitor progressMonitor
	 * @return Object f_doFormRequest call result.
	 */	
	_sendRequest: function(request, params, progressMonitor) {			
		var ctype=f_httpRequest.TEXT_PLAIN_MIME_TYPE;
		if (params && params.nodeType==f_core.DOCUMENT_NODE) {
			ctype=f_httpRequest.TEXT_XML_MIME_TYPE;
		}
		
		ctype+="; charset=UTF-8";
		
		request.f_setAcceptType(ctype);
		
		try {
			return request.f_doFormRequest(params, progressMonitor);
			
		} catch (x) {
			f_core.Debug(f_service, "_sendRequest: Request exception ", x);
			
			throw x;
		}
	},
	
	/**
	 * @method protected
	 */
	f_decodeResponse: function(content, contentType, request) {
		if (contentType.indexOf(f_httpRequest.XML_MIME_TYPE)>=0) {
			// Le content doit deja etre un Document !
			return content;
		}
	
		if (contentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)>=0) {
			return content;
		}
		
		var cameliaContentType=request.f_getResponseHeader(f_service._CAMELIA_CONTENT_TYPE);
//		alert("CameliaContent="+cameliaContentType);
		
		switch(cameliaContentType) {
		case "object":
			return f_core.DecodeObject(content, true);

		case "null":
			return null;
		}

		return content;
	},
	/**
	 * @method protected
	 */
	f_performErrorEvent: function(param, messageCode, message) {
		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return HTMLElement
	 */
	f_findComponent: function(id) {
		return fa_namingContainer.FindComponents(this, arguments);
	},
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return HTMLElement
	 */
	f_findSiblingComponent: function(id) {
		return fa_namingContainer.FindSiblingComponents(this, arguments);
	},
	/**
	 * @method hidden
	 */
	fa_cancelFilterRequest: function() {
	}
};
 
new f_class("f_service", {
	extend: f_object,
	aspects: [ fa_serializable, fa_eventTarget, fa_filterProperties, fa_commands ],
	statics: __statics,
	members: __members
});