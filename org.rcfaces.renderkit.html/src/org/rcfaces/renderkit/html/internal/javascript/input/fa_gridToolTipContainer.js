/**
 * Aspect Tooltip Container
 *
 * @aspect public abstract fa_gridToolTipContainer extends fa_toolTipContainer
 * @author jbmeslin@vedana.com (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/12/19 15:46:46 $
 */
 
var __members = {
	/**
	 * @method protected
	 * @param f_toolTip
	 *            tooltip
	 * @param optional
	 *            Event jsEvent
	 * @param optional
	 *            Number toolTipPosition
	 * @return void
	 */
	f_showToolTip : function(tooltip, jsEvent, toolTipPosition) {
		f_core
				.Debug(f_grid, "f_showToolTip: show tooltip '" + tooltip.id
						+ "'");
		var toolTipManager = f_toolTipManager.Get();

		var self = this;
		toolTipManager
				.f_appendCommand(function() {
					f_core.Debug(f_grid, "f_showToolTip: show tooltip '"
							+ tooltip.id + "' COMMAND START");

					if (self._loadToolTip(tooltip, jsEvent, toolTipPosition) !== false) {
						return;
					}

					toolTipManager.f_processNextCommand();
				});
	},
	
	/**
	 * @method protected
	 * @param Element
	 *            elementItem
	 * @param String tooltipId
	 * @return Object
	 */
	_computeTooltipRowContext: f_class.ABSTRACT,
	
	/**
	 * @method private
	 * @param f_toolTip
	 *            tooltip
	 * @param optional
	 *            Event jsEvent
	 * @param optional
	 *            Number toolTipPosition
	 * @return Boolean
	 */
	_loadToolTip : function(tooltip, jsEvent, toolTipPosition) {
		var tooltipId = tooltip.f_getId();
		if (!tooltipId) {
			return false;
		}

		var elementItem = tooltip.f_getElementItem();
		if (!elementItem) {
			// Pas d'item ?
			// Le temps que le nextCommand soit appelé, la popup est fermée
			return false;
		}

		var ctx=this._computeTooltipRowContext(elementItem, tooltipId);
		if (!ctx) {
			return false;
		}
		
		var row = ctx._row;
		if (ctx._tooltipId) {
			tooltipId=ctx._tooltipId;
		}

		if (!row || row._rowIndex === undefined || row._rowIndex < 0) {
			return false;// a revoir
		}

		var component = tooltip;

		var request = new f_httpRequest(component,
				f_httpRequest.TEXT_HTML_MIME_TYPE);

		var toolTipManager = f_toolTipManager.Get();

		var toolTipStateId = tooltip.f_getStateId();

		tooltip.f_setInteractiveRenderer(false); // Le contenu est géré par
		// NOTRE appel ajax !
		tooltip.f_cleanContent();

		var self = this;
		request
				.f_setListener({
					onInit : function(request) {
						tooltip.f_asyncShowWaiting();

						component.f_show(toolTipStateId, jsEvent,
								toolTipPosition);
					},
					/**
					 * @method public
					 */
					onError : function(request, status, text) {

						if (toolTipManager.f_processNextCommand()) {
							return;
						}

						if (tooltip.f_getStateId() != toolTipStateId) {
							// Reset du tooltip !
							return;
						}
						tooltip.f_asyncHideWaiting(true);

						component.f_show(toolTipStateId);

						f_core.Info(f_grid,
								"f_showToolTip.onError: Bad status: " + status);

						self.f_performErrorEvent(request, f_error.HTTP_ERROR,
								text);
					},
					/**
					 * @method public
					 */
					onProgress : function(request, content, length, contentType) {
						if (tooltip.f_getStateId() != toolTipStateId) {
							// Reset du tooltip !
							return;
						}

						tooltip.f_asyncShowMessageWaiting(f_waiting
								.GetReceivingMessage());
					},
					/**
					 * @method public
					 */
					onLoad : function(request, content, contentType) {
						if (toolTipManager.f_processNextCommand()) {
							return;
						}

						if (tooltip.f_getStateId() != toolTipStateId) {
							// Reset du tooltip !
							return;
						}

						try {
							tooltip.f_asyncHideWaiting(true);
							tooltip.f_asyncDestroyWaiting();

							if (component._removeStyleHeight) {
								component._removeStyleHeight = null;
								if (component.style.height == f_waiting.HEIGHT
										+ "px") {
									component.style.height = "auto";
								}
							}

							if (request.f_getStatus() != f_httpRequest.OK_STATUS) {
								component
										.f_performErrorEvent(
												request,
												f_error.INVALID_RESPONSE_ASYNC_RENDER_ERROR,
												"Bad http response status ! ("
														+ request
																.f_getStatusText()
														+ ")");
								return;
							}

							var cameliaServiceVersion = request
									.f_getResponseHeader(f_httpRequest.CAMELIA_RESPONSE_HEADER);
							if (!cameliaServiceVersion) {
								component.f_performErrorEvent(request,
										f_error.INVALID_SERVICE_RESPONSE_ERROR,
										"Not a service response !");
								return;
							}

							var ret = request.f_getResponse();
							// alert("Ret="+ret);

							var responseContentType = request
									.f_getResponseContentType().toLowerCase();
							if (responseContentType
									.indexOf(f_error.APPLICATION_ERROR_MIME_TYPE) >= 0) {
								var code = f_error
										.ComputeApplicationErrorCode(request);

								component.f_performErrorEvent(request, code,
										content);
								return;
							}

							if (responseContentType
									.indexOf(f_httpRequest.TEXT_HTML_MIME_TYPE) >= 0) {

								try {
									tooltip.f_setContent(ret);

								} catch (x) {
									component
											.f_performAsyncErrorEvent(
													x,
													f_error.RESPONSE_EVALUATION_ASYNC_RENDER_ERROR,
													"Evaluation exception");
								}
								return;
							}

							component.f_performAsyncErrorEvent(request,
									f_error.RESPONSE_TYPE_ASYNC_RENDER_ERROR,
									"Unsupported content type: "
											+ responseContentType);

						} finally {

						}
					}
				});

		
		
		var params = {
				gridId : this.id,
				rowValue : ctx._rowValue,
				rowIndex : ctx._rowIndex,
				toolTipId : tooltipId
			};
		
		if ( parseInt(_rcfaces_jsfVersion) >= 2) {
			// JSF 2.0
			request.f_setRequestHeader("Faces-Request", "partial/ajax");
			params["javax.faces.behavior.event"]= "grid.toolTip";
			params["javax.faces.source"]= this.id;
			params["javax.faces.partial.execute"]= this.id;
			
		} else {
			request.f_setRequestHeader("X-Camelia", "grid.toolTip");
		}

		

		if (this._paged) {
			// On synchronise 1 SEULE ligne, on envoi pas les indexes !

			var serializedState = this.f_getClass().f_getClassLoader()
					.f_getSerializedState();
			f_core.Debug(f_dataGrid, "f_callServer: serializedState="
					+ serializedState);
			if (!serializedState) {
				serializedState = ""; // Il faut informer le service que nous
				// sommes en mode paginé !
			}

			params[f_core.SERIALIZED_DATA] = serializedState;

			f_classLoader.SerializeInputsIntoParam(params, this, false);
		}

		request.f_doFormRequest(params);

		return true;
	}

};

new f_aspect("fa_gridToolTipContainer", {
	members: __members,
	extend: [fa_toolTipContainer]
});
