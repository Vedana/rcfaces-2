/*
 * $Id: f_filtredComponent.js,v 1.5 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Class f_filtredComponent.
 *
 * @class abstract f_filtredComponent extends f_component, fa_filterProperties, fa_commands
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:28 $
 */

var __members = {

	f_finalize: function() {
//		this._loading=undefined; // Boolean
		this._waiting=undefined; // f_waiting

		this.f_super(arguments);
	},
	fa_updateFilterProperties: function() {
		this.f_appendCommand(function(filtredComponent) {			
			filtredComponent._callServer();
		});
	},
	/**
	 * @method private
	 */
	_callServer: function() {
		f_class.IsClassDefined("f_httpRequest", true);

		this.className=this.f_computeStyleClass("_loading");
 	
		var params=new Object;
		params.componentId=this.id;
		
		var filterExpression=this.fa_getSerializedPropertiesExpression();
		if (filterExpression) {
			params.filterExpression=filterExpression;
		}
	
		var request=new f_httpRequest(this, f_httpRequest.JAVASCRIPT_MIME_TYPE);
		var filtredComponent=this;
		request.f_setListener({
			/**
			 * @method public
			 */
	 		onInit: function(request) {
	 		},	 		
			/**
			 * @method public
			 */
	 		onError: function(request, status, text) {
	 			f_core.Info(f_filtredComponent, "_callServer.onError: Bad status: "+status);

	 			var continueProcess;
	 			
	 			try {
	 				continueProcess=filtredComponent.f_performErrorEvent(request, f_error.HTTP_ERROR, text);
	 				
	 			} catch (x) {
	 				// On continue coute que coute !
	 				continueProcess=false;
	 			}	 				
	 				 				 			 			
		 		if (continueProcess===false) {
					filtredComponent._loading=false;		
					
					filtredComponent.className=filtredComponent.f_computeStyleClass();
					return;
		 		}
	 			
				if (filtredComponent.f_processNextCommand()) {
					return;
				}
	 		
				filtredComponent._loading=false;		
				
				filtredComponent.className=filtredComponent.f_computeStyleClass();
	 		},
			/**
			 * @method public
			 */
	 		onProgress: function(request, content, length, contentType) {
	 		},
			/**
			 * @method public
			 */
	 		onLoad: function(request, content, contentType) {
				if (filtredComponent.f_processNextCommand()) {
					return;
				}
				
				try {
					filtredComponent.className=filtredComponent.f_computeStyleClass();

					if (request.f_getStatus()!=f_httpRequest.OK_STATUS) {
						filtredComponent.f_performErrorEvent(request, f_error.INVALID_RESPONSE_SERVICE_ERROR, "Bad http response status ! ("+request.f_getStatusText()+")");
						return;
					}

					var cameliaServiceVersion=request.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
					if (!cameliaServiceVersion) {
						filtredComponent.f_performErrorEvent(request, f_error.INVALID_SERVICE_RESPONSE_ERROR, "Not a service response !");
						return;					
					}
	
					var responseContentType=request.f_getResponseContentType().toLowerCase();
					
					if (responseContentType.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE)>=0) {
						var code=f_error.ComputeApplicationErrorCode(request);
				
				 		filtredComponent.f_performErrorEvent(request, code, content);
						return;
					}

					if (responseContentType.indexOf(f_httpRequest.JAVASCRIPT_MIME_TYPE)<0) {
				 		filtredComponent.f_performErrorEvent(request, f_error.RESPONSE_TYPE_SERVICE_ERROR, "Unsupported content type: "+responseContentType);
						return;
					}

					var ret=request.f_getResponse();
					try {
						f_core.WindowScopeEval(ret);
						
					} catch (x) {
						f_core.Error(f_filtredComponent, "_callServer.onLoad: Can not eval response '"+ret+"'.", x);
					}

				} finally {
					filtredComponent._loading=undefined;	
				}
	 		}
		});

		request.f_setRequestHeader("X-Camelia", this.f_getServiceId());
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");

			if (!params) {
				params={};
			}
			params["javax.faces.behavior.event"]= this.f_getServiceId();
			params["javax.faces.source"]= this.id;
			params["javax.faces.partial.execute"]= this.id;
		}
		request.f_doFormRequest(params);
		this._loading=true;
	},
	/**
	 * @method protected abstract
	 */
	f_getServiceId: f_class.ABSTRACT,
	/**
	 * @method protected
	 */
	f_performErrorEvent: function(param, messageCode, message) {

		return f_error.PerformErrorEvent(this, messageCode, message, param);
	},
	/**
	 * @method hidden
	 */
	fa_cancelFilterRequest: function() {
	},
	fa_updateValue: function(newValue) {
		this.f_setFilterProperty(f_prop.VALUE, newValue)
	}
};
 
new f_class("f_filtredComponent", {
	extend: f_component, 
	aspects: [ fa_filterProperties, fa_commands ],
	members: __members
});