/*
 * $Id: fa_asyncRender.js,v 1.5 2013/12/11 10:19:48 jbmeslin Exp $
 */
 
/**
 * Aspect AsyncRender
 *
 * @aspect hidden abstract fa_asyncRender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:19:48 $
 */
var __members = {
	fa_asyncRender: function() {
		if (this.nodeType==f_core.ELEMENT_NODE) {
			this._interactive=f_core.GetBooleanAttributeNS(this, "asyncRender");
		}
	},
	f_finalize: function() {
//		this._interactive=undefined; // boolean
//		this._intLoading=undefined; // boolean
		this._intWaiting=undefined; // f_waiting
//		this._asyncDecoded=undefined; // boolean
	},
	/**
	 * @method hidden
	 * @return Boolean
	 */
	f_isInteractiveRenderer: function() {
		return this._interactive;
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_setInteractiveRenderer: function(state) {
		this._interactive=state;
	},
	f_updateVisibility: {
		after: function(visible) {
			if (visible) {
				this.f_prepare(false);
			}
		}
	},
	/**
	 * @method public
	 * @param Boolean synch Wait preparation if necessary.
	 * @return Boolean <code>true</code> if component is prepared !
	 */
	f_prepare: function(synch) {
		if (!this._interactive) {
			return true;
		}
		var component = this;
		
		this._interactive=undefined;
		window.setTimeout(function(){
 			if (window._rcfacesExiting) {
 				return false;
 			}

 			var lock = f_event.GetEventLocked(null, false, f_event.SUBMIT_LOCK); 			
 			if (lock) {
 				return;
 			}
 			component._callAsyncRender();
 			component=null;
		}, 12);		
		
		return false;
	},
	
	/**
	 * @method private
	 * @param component.
	 * @return void
	 */
	_callAsyncRender: function() {
		if (window._rcfacesExiting) {
			return;
		}
		var component = this;
		var request=new f_httpRequest(component, f_httpRequest.TEXT_HTML_MIME_TYPE);
		
		if (!component.style.height || component.offsetHeight<f_waiting.HEIGHT) {
			component._removeStyleHeight=true;
			component.style.height=f_waiting.HEIGHT+"px";
		}
		
		var self=this;
		request.f_setListener({
	 		onInit: function(request) {
	 			self.f_asyncShowWaiting();
	 		},
			/* *
			 * @method public
			 */
	 		onError: function(request, status, text) {
				
				f_core.Info(fa_asyncRender, "f_prepare.onError: Bad status: "+status);
				
				self.f_asyncHideWaiting(true);
				
				self.f_performAsyncErrorEvent(request, f_error.HTTP_ERROR, text);
	 		},
			/* *
			 * @method public
			 */
	 		onProgress: function(request, content, length, contentType) {
	 			self.f_asyncShowMessageWaiting(f_waiting.GetReceivingMessage());
	 		},
			/* *
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {			
				if (component._removeStyleHeight) {
					component._removeStyleHeight=null;
					if (component.style.height==f_waiting.HEIGHT+"px") {
						component.style.height="auto";
					}
				}
	
				try {
					self.f_asyncHideWaiting(true);
					self.f_asyncDestroyWaiting();

					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
						self.f_performAsyncErrorEvent(request, f_error.INVALID_RESPONSE_ASYNC_RENDER_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
						return;
					}

					var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
					if (!cameliaServiceVersion) {
						self.f_performAsyncErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
						return;					
					}
					
					var ret=request.f_getResponse();
					//	alert("Ret="+ret);

					var responseContentType=request.f_getResponseContentType().toLowerCase();
					if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
						var code=f_error.ComputeApplicationErrorCode(request);
				
						self.f_performErrorEvent(request, code, content);
						return;
					}
				
					if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)>=0) {
			 			self.f_asyncHideWaiting();

						try {
							f_core.WindowScopeEval(ret);
							
						} catch (x) {
							self.f_performAsyncErrorEvent(x, f_error.RESPONSE_EVALUATION_ASYNC_RENDER_ERROR, "Evaluation exception");
						}
						
						component.fa_contentLoaded(ret, responseContentType, parent);
						return;
					}
					
					if (responseContentType.indexOf(f_httpRequest.TEXT_HTML_MIME_TYPE)>=0) {
						
						try {
							self._asyncSetContent(ret);
							
							self.fa_contentLoaded(ret, responseContentType, parent);
							
						} catch (x) {
							self.f_performAsyncErrorEvent(x, f_error.RESPONSE_EVALUATION_ASYNC_RENDER_ERROR, "Evaluation exception");
						}
						return;
					}
					
					self.f_performAsyncErrorEvent(request, f_error.RESPONSE_TYPE_ASYNC_RENDER_ERROR, "Unsupported content type: "+responseContentType);
					
				} finally {				
				}
			}
		});

		component._intLoading=true;
		
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!param) {
				param={};
			}
			
			param["javax.faces.behavior.event"]= "asyncRender.request";
			param["javax.faces.source"]= component.id;
			param["javax.faces.partial.execute"]= component.id;
		} else {
			request.f_setRequestHeader("X-Camelia", "asyncRender.request");
			var	param={
					id: component.id
			};
		}
		
		

		
		request.f_doFormRequest(param);
	},

	/**
	 * @method protected
	 */
	f_performAsyncErrorEvent: function(param, messageCode, message) {
		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_asyncGetParentContent: function() {
		var component=this;
		var parent;
		
		if (typeof(component.fa_getInteractiveParent)=="function") {
			parent=component.fa_getInteractiveParent();
		}
		
		if (!parent) {
			parent=component;
		}
		
		return parent;
	},
	/**
	 * @method private
	 * @param String content
	 * @return void
	 */
	_asyncSetContent: function(content) {
		if (this.f_asyncSetContent) {
			return this.f_asyncSetContent(content);
		}
		
		var component = this;
		
		this.f_getClass().f_getClassLoader().f_loadContent(component, component, component.innerHTML+content);		
	},
	/**
	 * @method hidden
	 * @param optional String text
	 * @return void
	 */
	f_asyncShowWaiting:function(text) {
		var component=this;
	
		var waiting=component._intWaiting;
		if (!waiting) {	
			var parent=this.f_asyncGetParentContent();
			
			waiting=f_waiting.Create(parent);
			component._intWaiting=waiting;
		}
		
		if (text===undefined) {
			text=f_waiting.GetLoadingMessage();
		}
		
		waiting.f_setText(text);
		waiting.f_show();
	},	
	/**
	 * @method hidden
	 * @param Boolean immediate
	 * @return void
	 */
	f_asyncHideWaiting: function (immediate) {
		var component=this;

		var waiting=component._intWaiting;
		if (waiting) {
			waiting.f_hide(immediate);
			
			if (immediate) {
				waiting.f_close();
		
			}
		}
	},
	/**
	 * @method hidden
	 * @param String text
	 * @return void
	 */
	f_asyncShowMessageWaiting: function (text) {
		var component=this;

		var waiting=component._intWaiting;
		if (waiting) {
			waiting.f_setText(text);
		}	 			
	},
	
	/**
	 * @method hidden
	 */
	f_asyncDestroyWaiting:function() {
		var component=this;

		var waiting=component._intWaiting;
		if (waiting) {
			waiting.f_hide();
			component._intWaiting=undefined;
			
			f_classLoader.Destroy(waiting);
		}
	},
	
	/**
	 * @method protected
	 * @param String content
	 * @param String mimeType
	 * @param HTMLElement
	 * @return void
	 */
	fa_contentLoaded: function() {
		if (f_core.GetBooleanAttributeNS(this, "asyncDecode") && !this._asyncDecoded) {
			// On ajoute un tag comme quoi le composant est a décoder !
			
			this._asyncDecoded=true;
			
			var form=f_core.GetParentForm(this);
			
			f_core.CreateElement(form, "INPUT", {
				type: "hidden",
				name: "org.rcfaces.async.partial."+this.id,
				value: "true"
			});
		}
	},
	
	/**
	 * @method protected abstract
	 * @return HTMLElement
	 */
	fa_getInteractiveParent: f_class.ABSTRACT
};

new f_aspect("fa_asyncRender", {
	members: __members
});
