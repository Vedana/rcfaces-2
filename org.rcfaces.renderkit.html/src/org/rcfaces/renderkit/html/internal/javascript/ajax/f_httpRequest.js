/*
 * $Id: f_httpRequest.js,v 1.5 2013/12/12 15:39:12 jbmeslin Exp $
 */
 
/**
 * HTTP request support.
 *
 * @class f_httpRequest extends f_object
 * @author Joel Merlin
 * @author Olivier Oeuillot
 */
var __statics = {

	/**
	 * @field public static final String
	 */
	POST_METHOD: "POST",
	
	/**
	 * @field public static final String
	 */
	GET_METHOD: "GET",
	
	/**
	 * @field public static final String
	 */
	URLENCODED_MIME_TYPE: "application/x-www-form-urlencoded",
	
	/**
	 * @field public static final String
	 */
	TEXT_HTML_MIME_TYPE: "text/html",
	
	/**
	 * @field public static final String
	 */
	TEXT_PLAIN_MIME_TYPE: "text/plain",
	
	/**
	 * @field public static final String
	 */
	TEXT_XML_MIME_TYPE: "text/xml",
	
	/**
	 * @field public static final String
	 */
	JAVASCRIPT_MIME_TYPE: "text/javascript",
	
	/**
	 * @field public static final String
	 */
	ANY_MIME_TYPE: "*/*",
		
	/**
	 * @field public static final Number
	 */
	OK_STATUS: 200,
	
	/**
	 * @field public static final String
	 */
	HTTP_CONTENT_TYPE: "Content-Type",
	
	/**
	 * @field hidden static final String
	 */
	CAMELIA_RESPONSE_HEADER: "X-Camelia-Service",
	
	/**
	 * @field hidden static final String
	 */
	UTF_8: "UTF-8",
	
	FACES_REQUEST_HEADER_NAME: "Faces-Request",
	
	FACES_PARTIAL_AJAX_REQUEST : "partial/ajax",
	
	FACES_BEHAVIOR_EVENT_PARAMETER_NAME : "javax.faces.behavior.event",
	
	FACES_BEHAVIOR_SOURCE_PARAMETER_NAME : "javax.faces.source",
	
	FACES_BEHAVIOR_EXEC_PARAMETER_NAME : "javax.faces.partial.execute"
		
};

var __members = {

	/**
	 * @method public
	 * @param HTMLElement component
	 * @param optional String acceptType
	 * @param optional String charSet
	 * @param optional String url
	 * @param optional hidden Boolean noLog
	 */
	f_httpRequest: function(component, acceptType, charSet, url, noLog) {
		this._component=component;		
	
		this.f_setAcceptType(acceptType, charSet);

		if (!url) {
			url=f_env.GetViewURI();
		}
		this._url=url;

		this._noLog=noLog;

		this.f_cancelRequest();		
	},

	f_finalize: function() {
		this.f_cancelRequest();
		
		// this._requestHeaders=undefined; // Map<String,String>
		// this._ready = undefined; // Boolean
		//this._noLog=undefined; // Boolean
		// this._url=undefined; // String
		this._listener= undefined; // Function
		this._component = undefined; // any ?
	},

	/**
	 * Install a listener for asynchronus response processing.
	 *
	 * Listener sample:
	 * <pre>
	 * 	var listener = {
	 *		onInit: function(httpObject) {
	 *			alert("onInit");
	 *		},
	 *		onError: function(httpObject, status, text) {
	 *			alert("onError");
	 *		},
	 *		onProgress: function(httpObject, content, length, contentType) {
	 *			alert("onProgress");
	 *		},
	 *		onLoad: function(httpObject, content, contentType) {
	 *			alert("onLoad");
	 *		},
	 *		onAbort: function(httpObject, content, contentType) {
	 *			alert("onAbort");
	 *		}
	 * 	}
	 * </pre>
	 *
	 * @method public
	 * @param Object listener An object which defines callbacks for asynchronus processing.
	 * @return void
	 */
	f_setListener: function(listener) {
		this._listener = listener;
	},

	/**
	 * Returns ready state.
	 *
	 * @method public
	 * @return Boolean Ready state.
	 */
	f_getReady: function() {
		return this._ready;
	},
	
	/**
	 * Returns the code associated to the status of the response.
	 *
	 * @method public
	 * @return Number
	 */
	f_getStatus: function() {
		var request=this._request;
		if (!request) {
			return null;
		}
		
		return request.status;
	},

	/**
	 * Returns the message associated to the status of the response.
	 *
	 * @method public
	 * @return String
	 */
	f_getStatusText: function() {
		var request=this._request;
		if (!request) {
			return null;
		}
		return request.statusText;
	},
	
	/**
	 * Returns the raw data of the response.
	 *
	 * @method public
	 * @return String
	 */
	f_getResponse: function() {
		var response=this._response;
		if (response===false) {
			throw new Error("HTTP Request is not an plain text request !");
		}
		
		return response;
	},

	/**
	 * Returns the reponse xml document.
	 *
	 * @method public
	 * @return Document
	 * @throws Error If the response is not a xml document.
	 */
	f_getXmlResponse: function() {
		var responseXML=this._responseXML;
		if (responseXML===false) {
			throw new Error("HTTP Request is not an XML request !");
		}
		
		return responseXML;
	},
	/**
	 * Returns <code>true</code> if the response is a Xml document.
	 *
	 * @method public
	 * @return Boolean
	 */
	f_isXmlResponse: function() {
		return typeof(this._responseXML)=="object";
	},
	
	/**
	 * Returns the response content type;
	 *
	 * @method public
	 * @return String
	 */
	f_getResponseContentType: function() {
		return this._responseContentType;
	},

	/**
	 * 
	 *
	 * @method public
	 * @param String name
	 * @return String
	 */
	f_getResponseHeader: function(name) {
		f_core.Assert(this._ready, "f_httpRequest.f_getResponseHeader: This request is not ready yet !");
		f_core.Assert(typeof(name)=="string", "f_httpRequest.f_getResponseHeader: Name of property must be a string ! ("+name+")");

		var request=this._request;
		f_core.Assert(request, "f_httpRequest.f_getResponseHeader: No request to get response header '"+name+"' !");
		
		try {
			return request.getResponseHeader(name);
			
		} catch (x) {
			f_core.Error(f_httpRequest, "f_getResponseHeader: Can not get reponse header '"+name+"'.", x);
			// Il peut y avoir des cas ou la reponse n'est pas prete car c'est un RELOAD forcé par l'utilisateur !
			return null;
		}
	},

	/**
	 * Cancel the request if possible.
	 *
	 * @method public
	 * @return void
	 */
	f_cancelRequest: function() {
		f_core.Debug(f_httpRequest, "f_cancelRequest: Clean or cancel request");

		var request=this._request;
		if (request) {
			this._request = undefined;
	
			if (!this._ready) {
				try {
					request.abort();
					
				} catch (x) {
					f_core.Error(f_httpRequest, "f_cancelRequest: Cancel request has failed !", x);
				}
			}
			
			/* Ca marche pas ! ????*/
			try {
				request.onreadystatechange=null;
			} catch (x) {
				//alert(x);
			}
			/*
			if (f_core.IsGecko()) {
				request.onerror=null;
			}
			*/
		}
		
		if (this._initialized && !this._ready) {		
			var onAbort=this._listener.onAbort;
			if (onAbort) {
				f_core.Debug(f_httpRequest, "f_cancelRequest: Call onAbort callback");

				try {
					onAbort.call(this, this);

				} catch (ex) {
					f_core.Error(f_httpRequest, "f_cancelRequest: Exception when calling onAbort for url="+this._url+".\n"+onAbort,ex);
				}
			}
		}
		
		this._responseContentType = undefined;
		this._response = undefined;
		this._responseXML = undefined;
		this._ready = false;
		this._initialized=undefined;
		this._date=undefined;
	},
	
	/**
	 * Specifies a request header.
	 *
	 * @method public
	 * @param String name Name of property.
	 * @param String value Value to associate.
	 * @return void
	 */
	f_setRequestHeader: function(name, value) {
		f_core.Assert(value===null || typeof(value)=="string", "f_httpRequest.f_setRequestHeader: Header parameter '"+name+"' is not a string ! ("+value+").");
	
		var requestHeaders=this._requestHeaders;
		if (!requestHeaders) {
			if (value===null) {
				return;
			}
		
			requestHeaders=new Object;
			this._requestHeaders=requestHeaders;
		}
		
		if (value===null) {
			requestHeaders[name]=undefined;
			return;
		}		
		
		requestHeaders[name]=value;
	},

	/**
	 * Processeur de requête, les données par défaut seront les suivantes.
	 * la méthode est de type "POST", les données sont à NULL, le type mime est
	 * "text/plain" et le mode de transmission synchrone.
	 *
	 * @method public
	 * @param String method
	 * @param any data
	 * @param optional String contentType
	 * @param optional f_progressMonitor progressMonitor
	 * @return void
	 */
	f_doRequest: function(method, data, contentType, progressMonitor) {
		
	//	alert("url="+this._url+"\nmethod="+method+"\ntype="+type+"\nasync="+asynch+"\ndata="+data);
		f_core.Debug(f_httpRequest, "f_doRequest: Prepare request: url="+this._url+"\nmethod="+method+"\ncontentType="+contentType+"\nacceptType="+this._acceptType+"\nasync="+(typeof(this._listener)=="function")+"\ndata="+data);

		var oldCursor=null;
		if (!this._listener) {
			oldCursor=document.body.style.cursor;
			if (!oldCursor) {
				oldCursor="default";
			}
			document.body.style.cursor = "wait";
		}
		
		try {
			this._doRequest.apply(this, arguments);

		} catch (x) {
			f_core.Error(f_httpRequest, "f_doRequest: Can not send request to "+this._url+" data="+data, x);
			throw x;
			
		} finally {
			if (oldCursor) {
				document.body.style.cursor = oldCursor;
			}
		}
	},
	/** 
	 * @method private
	 */
	_doRequest: function(method, data, contentType, progressMonitor) {
		f_core.Profile(false, "f_httpRequest.doRequest("+this._url+")");
		try {
			// Check if pending request		
			if (this._ready) {
				return false;
			}
		
			// Cleanup
			this.f_cancelRequest();

			// this._ready = false; // Deja fait !
			this._error = false;	

			this._date=new Date().getTime();
		
			f_core.Assert(typeof(this._url)=="string", "f_httpRequest._doRequest: URL is invalid ! ("+this._url+")");
		
			// Create new object
			var req = null;
			if (window.XMLHttpRequest){
				try { 
					req = new XMLHttpRequest(); 
	
				} catch(ex) {
					f_core.Error(f_httpRequest, "_doRequest: Can not create XMLHttpRequest !", ex);
					
					throw ex;
				}
			
			} else if (f_core.IsInternetExplorer()) {
				// Get most recent version
				try { 
					req = new ActiveXObject("Msxml2.XMLHTTP"); 
	
				} catch(ex) {
					// Try with older one
					try { 
						req = new ActiveXObject("Microsoft.XMLHTTP"); 
						
					} catch(ex) {
						f_core.Error(f_httpRequest, "_doRequest: Can not find ActiveX XmlHttp !", ex);
						
						throw ex;
					}
				}
			} 
		
			// Check valid object
			if (!req) {
				throw "f_httpRequest: Cannot create XML Http object...";
			}
	
			this._request = req;
		
			// Initialize event handler
			var async = (typeof(this._listener)=="object");
			if (async) {
				var self=this;
				
				// Ben oui, c'est la joie du context de création des fonctions.
				req.onreadystatechange =  function() {
					
					if (window._rcfacesExiting) {
						// Nous ne sommes pas dans un contexte sain ....
						// Par exemple, échanges HTTP aprés un onExit de f_core !
						self=null;
						return false;
					}
				
					self._onReadyStateChange();
				};
				
				if (f_core.IsGecko() || f_core.IsWebkit()) {
					req.onerror = function() {
						if (window._rcfacesExiting) {
							// Nous ne sommes pas dans un contexte sain ....
							// Par exemple, échanges HTTP aprés un onExit de f_core !
							return false;
						}
						if (!self._error) {
							return;
						}
		
						var req=self._request;				
		
						var status;
						var statusText;
						try {
							status = req.status;
							statusText=req.statusText;
									
						} catch(ex) {
							// C'est pas grave s'il y a des erreurs !
						}
						
						self._callError(status, statusText);
					};
				}
			}
				
			// Open request
			if (!method) {
				 method = f_httpRequest.GET_METHOD;
			}
			
			req.open(method, this._url, async);
		
			if (!contentType && data && method==f_httpRequest.POST_METHOD) {
				if (typeof(data)=="string") {		
					if (data.indexOf("<?xml")>=0) {
						contentType = f_httpRequest.TEXT_XML_MIME_TYPE;
						
					} else {
						contentType = f_httpRequest.TEXT_PLAIN_MIME_TYPE;
					}
				}
			}
		
			if (contentType) {		
				req.setRequestHeader(f_httpRequest.HTTP_CONTENT_TYPE, contentType);
			}
						
			if (this._acceptType) {
				req.setRequestHeader("Accept", this._acceptType);
			}
	
			var requestHeaders=this._requestHeaders;
			if (requestHeaders) {
				for(var p in requestHeaders) {
					var pv=requestHeaders[p];
					
					f_core.Assert(typeof(pv)=="string", "f_httpRequest._doRequest: Header parameter '"+p+"' is not a string ! ("+pv+").");
					
					req.setRequestHeader(p, pv);
				}
			}
			
			try {
				if (data) {
					req.send(data);
					
				} else {
					req.send(null);
				}
				
			} catch (x) {
				f_core.Error(f_httpRequest, "_doRequest: Can not send data '"+data+"'.", x);
				throw x;
			}
				
			// Update response
			if (!async) {
				this._responseContentType=req.getResponseHeader(f_httpRequest.HTTP_CONTENT_TYPE);
	
				if (this._responseContentType && this._responseContentType.indexOf(f_httpRequest.TEXT_XML_MIME_TYPE)>=0) {
					this._responseXML = req.responseXML;
					this._response = false;
					
				} else {
					this._response = req.responseText;
					this._responseXML = false;
				}
				
				if (!this._noLog) {
					f_core.Info(f_httpRequest, "_doRequest: Response of url="+this._url+" received.");
	
					f_core.Debug(f_httpRequest, "_doRequest: Response of url="+this._url+"\n"+(this._responseXML)?this._responseXML:this._response);
				}
	
				this._ready = true;
			}
			
			return true;

		} finally {
			f_core.Profile(true, "f_httpRequest.doRequest("+this._url+")");			
		}
	},

	/**
	 * Processeur de requête de type http form, les données sont passées
	 * sous forme d'un objet avec des propriétés
	 * 
	 * @method public
	 * @param any data
	 * @param optional String contentType
	 * @param optional f_progressMonitor progressMonitor
	 * @return void
	 */
	f_doFormRequest: function(data, contentType, progressMonitor) {
		var formData = null;

		var method = f_httpRequest.POST_METHOD;
	
		data=f_core.UpdateAjaxParameters(this._component, this._url, data);
		if (data) {
			if (typeof(data)=="object") {
				formData="";
	
				for (var prop in data) {
					if (formData) {
						formData += "&";
					}
					formData+= encodeURIComponent(prop)+"=";
					
					var pdata=data[prop];
					if (pdata===undefined) {
						continue;
					}
					formData += encodeURIComponent(pdata);
				}
				
				if (!contentType) {
					contentType = f_httpRequest.URLENCODED_MIME_TYPE+"; charset=UTF-8";
				}
				
			} else if (typeof(data)=="string") {
				formData=data;

				if (!contentType) {
					contentType = f_httpRequest.URLENCODED_MIME_TYPE+"; charset=UTF-8";
				}
			} 
		}
		
		this.f_doRequest(method, formData, contentType, progressMonitor);
	},

	/**
	 * Gestionnaire d'évènements de la requête xmlHttp, appelle le
	 * listener enregistré pour la gestion des évènements de la requête
	 *
	 * @method private
	 * @return void
	 */
	_onReadyStateChange: function() {
		if (window._rcfacesExiting || window._rcfacesSubmitLocked) {
			return;
		}

		var req = this._request;
		if (!req) {
			f_core.Info(f_httpRequest, "_onReadyStateChange: Request has been canceled !");
			return;
		}
		
		var url = this._url;
		
		switch (req.readyState) {
		// LOADING, Object created, send not called
		case 1: {
			f_core.Profile(null, "f_httpRequest.stateChange.loading("+url+")");

			if (this._initialized) {
				return;
			}
			this._initialized=true;
			
			var onInit=this._listener.onInit;
			if (typeof(onInit)=="function") {
				if (!this._noLog) {
					f_core.Info(f_httpRequest, "_onReadyStateChange.loading: Call onInit for url="+url+" . (+"+(new Date().getTime()-this._date)+"ms)");
				}

				try {
					onInit.call(this, this);

				} catch (ex) {
					f_core.Error(f_httpRequest, "_onReadyStateChange.loading: Exception when calling onInit for url="+url+".\n"+onInit,ex);
				}
			}
			return;
		}

		// LOADED, Send called, status and headers available but no response
		case 2: {
			f_core.Profile(null, "f_httpRequest.stateChange.loaded("+url+")");

			if (f_core.IsInternetExplorer()) {
				// Inutile de tester qq chose !
				return;
			}
			if (this._error) {
				// On peut continuer a recevoir des infos, meme si il y a eu des problemes !
				return;
			}
			
			var status;
			var statusText;
			try {
				status = req.status;
				if (status==f_httpRequest.OK_STATUS) {
					return;
				}
				
			} catch(ex) {				
				try {
					f_core.Error(f_httpRequest, "_onReadyStateChange.loaded: Can not get status of request !", ex);
				} catch (x2) {
					// On recupere une exception de l'erreur car on appele la callback
				}
			}
			
			try {				
				statusText=req.statusText;
				
			} catch (x) {
				// Pas de traitement			
			}
			
			if (!statusText || statusText=="Unknown") {
				statusText=f_resourceBundle.Get(f_httpRequest).f_get("STATUS_ERROR");
			}
		
			this._callError(status, statusText);
			return;
		}

		// INTERACTIVE Some data received, partial results in responseBody/Text
		case 3: {
			if (this._error) {
				// On peut continuer a recevoir des infos, meme si il y a eu des problemes !
				return;
			}
			
			var len = 0;
			var response = null;
			var contentType = null;
			try {
				len = req.getResponseHeader("Content-Length");
				
			} catch(ex) {
				f_core.Debug(f_httpRequest, "_onReadyStateChange.interactive: Can not get length of response of url="+url+".");
				len = NaN;
			}
			try {
				contentType=req.getResponseHeader(f_httpRequest.HTTP_CONTENT_TYPE);
				if (contentType) {
					this._responseContentType=contentType;
				}
			} catch (ex) {
				f_core.Debug(f_httpRequest, "_onReadyStateChange.interactive: Can not get content type of response of url="+url+".");
			}
			
			if (!contentType || contentType.indexOf(f_httpRequest.TEXT_XML_MIME_TYPE)<0) {
				try {
					response = req.responseText;
				} catch(ex) {
					f_core.Debug(f_httpRequest, "_onReadyStateChange.interactive: Can not get response text of url="+url+".");
				}
			}
			
			var onProgress=this._listener.onProgress;
			if (typeof(onProgress)=="function") {				
				if (!this._noLog) {
					f_core.Info(f_httpRequest, "_onReadyStateChange.interactive: Call onProgress for url="+url+" . (+"+(new Date().getTime()-this._date)+"ms)");
				}

				try {
					onProgress.call(this, this, response, len, contentType);

				} catch (ex) {
					f_core.Error(f_httpRequest, "_onReadyStateChange.interactive: Exception when calling onProgress method for url='"+url+"'.\n"+onProgress, ex);
				}
			}
			
			return;
		}

		// COMPLETE, All data received headers and status updated
		case 4: {
			f_core.Profile(null, "f_httpRequest.stateChange.complete("+url+")");
			
			if (this._error) {
				// On peut continuer a recevoir des infos, meme si il y a eu des problemes !
				return;
			}

			var responseContentType;
			try {
				responseContentType=req.getResponseHeader(f_httpRequest.HTTP_CONTENT_TYPE);
				
			} catch (x) {
				f_core.Debug(f_httpRequest, "_onReadyStateChange.complete: getResponseHeader exception !", ex);
				// Il peut y avoir des cas ou la reponse n'est pas prete car c'est un RELOAD forcé par l'utilisateur !
				return;
			}
			this._responseContentType=responseContentType;
		
			var response=null;
			if (responseContentType && responseContentType.indexOf(f_httpRequest.TEXT_XML_MIME_TYPE)>=0) {
				this._response = false;
				response=this._responseXML = req.responseXML;
				
			} else {
				response=this._response = req.responseText;
				this._responseXML = false;
			}
			
			this._ready = true;
			var status;
			var statusText;
			try {
				status=req.status;
				statusText=req.statusText;
				
			} catch (ex) {
				try {
					f_core.Error(f_httpRequest, "_onReadyStateChange.complete: Can not get status of request !", ex);
				} catch (x2) {					
					// On recupere une exception de l'erreur car on appele la callback
				}
			}
			
			f_core.Debug(f_httpRequest, "_onReadyStateChange.complete: Response='"+response+"' status='"+status+"' statusText='"+statusText+"'");

			if (status!=f_httpRequest.OK_STATUS) {
				this._callError(status, statusText);
				return;		
			}
			
			var onLoad=this._listener.onLoad;
			if (typeof(onLoad)!="function") {
				return;
			}
			
			try {
				if (!this._noLog) {
					f_core.Info(f_httpRequest, "_onReadyStateChange.complete: Call onLoad for url="+url+" . (+"+(new Date().getTime()-this._date)+"ms)\nresponse size="+((response)?(response.length+" bytes"):"null"));
				}

				onLoad.call(this, this, response, responseContentType);
				
			} catch (ex) {
				f_core.Error(f_httpRequest, "_onReadyStateChange.complete: Exception when calling onLoad method for url '"+url+"'.\n"+onLoad, ex);
			}

			return;
		}
		
		// UNINITIALIZED, Object created but not initialized, open not called
		default: 
			return;
		}
	},
	/**
	 * @method private
	 * @return void
	 */
	_callError: function(status, statusText) {
		if (window._rcfacesExiting) {
			return;
		}

		if (this._error) {
			// Error handler already called
			return;
		}
		this._error = true;
	
		// Call error handler
		var onError=this._listener.onError;
		if (typeof(onError)!="function") {
			return;
		}
		
		try {
			if (!this._noLog) {
				f_core.Info(f_httpRequest, "_callError: Call onError for url="+this._url+" . (+"+(new Date().getTime()-this._date)+"ms)");
			}

			onError.call(this, this, status, statusText);
			
		} catch (ex) {
			f_core.Error(f_httpRequest, "_callError: Exception when calling onError.\n"+onError, ex);
		}
	},
	/**
	 * @method public
	 * @param optional String acceptType Mime type of accept header parameter.
	 * @param optional String charSet
	 * @return void
	 */
	f_setAcceptType: function(acceptType, charSet) {
		if (!acceptType) {
			acceptType=f_httpRequest.ANY_MIME_TYPE;		
		}
		if (charSet) {
			acceptType+="; charset="+charSet;
		}
		this._acceptType=acceptType;
	}
};

new f_class("f_httpRequest", {
	statics: __statics,
	members: __members
});

