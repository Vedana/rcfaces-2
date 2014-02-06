/*
 * $Id: f_progressMonitorDialog.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * <p><strong>f_progressMonitorDialog</strong> represents popup modal.</p>
 *
 * @class public f_progressMonitorDialog extends f_dialog, fa_progressIndicator
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __statics = {
	
	
	/**
	 * @field public static final Number
	 */
	REMAINING_TIME: 0x0001,
	 
	/**
	 * @field private static final Number
	 */
	_WAITING_TIMER: 1,

	/**
	 * @field private static final Number
	 */
	_COMPUTE_TIMER: 2,

	/**
	 * @field private static final Number
	 */
	_TERMINATE_TIMER: 3,
	
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		width: 400,
		height: 164,
		dialogPriority: 0,
		styleClass: "f_progressMonitorDialog",
		backgroundMode: f_shell.LIGHT_GREYED_BACKGROUND_MODE
	},
	
	/** 
	 * @field private static final Number
	 */
	_AUTO_CLOSE_DIALOG_TIMER_MILLIS: 2000,

	/** 
	 * @field private static final Number
	 */
	_COMPUTE_TIMER_MILLIS: 2000,
	
	/** 
	 * @field private static final Number
	 */
	_COMPUTE_INTERVAL_MILLIS: 2000,
	
	/** 
	 * @field private static final Number
	 */
	_UPDATE_TIME_INTERVAL_MILLIS: 1000,
	
	/**
	 * @method public static
	 * @param String title
	 * @param optional Document doc
	 * @return f_progressMonitorDialog
	 */
	Create: function(title, status,  doc) {
		if (!doc) {
			doc=document;
		}
		
		var dialog=doc.createElement(f_core._VNS+":progressMonitorDialog");
		
		f_core.AppendChild(doc.body, dialog);
		
		var dialog=f_progressMonitorDialog.f_decorateInstance(dialog);
		
		if (title) {
			dialog.f_setTitle(title);
			
			dialog.f_setProgressTitle(title);
		}
		
		if (status & f_progressMonitorDialog.REMAINING_TIME) { 	
			dialog.f_enableRemainingTime();
		}
		
		return dialog;	
	}
}

var __members = {

	/**
	 * <p>Construct a new <code>f_viewDialog</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 */
	f_progressMonitorDialog: function() {
		this.f_super(arguments, f_shell.PRIMARY_MODAL_STYLE | f_shell.COPY_STYLESHEET);
	},

	/*
	 * <p>Destruct a new <code>f_messageDialog</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {
		//this._cancelable=undefined; // boolean
		this._progressRunnable=undefined; // function
		//this._progressTitle=undefined; // String
		
		this._progressIndicatorMonitor=undefined; //f_progressIndicatorMonitor
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_preDestruction: function() {

		var win=f_core.GetWindow(this);

		var startTimerId=this._startTimerId;
		if (startTimerId) {
			this._startTimerId=undefined;
			
			win.clearTimeout(startTimerId);
		}

		var endTimerId=this._endTimerId;
		if (endTimerId) {
			this._endTimerId=undefined;
			
			win.clearTimeout(endTimerId);
		}
		
		var remainingIntervalId=this._remainingIntervalId;
		if (remainingIntervalId) {
			this._remainingIntervalId=undefined;
			
			win.clearInterval(remainingIntervalId);
		}
		
		this._titleText=undefined; // HTMLLabelElement
		this._messageText1=undefined; // HTMLLabelElement
		this._messageText2=undefined; // HTMLLabelElement

		var progressBar=this._progressBar;
		if (progressBar) {
			this._progressBar=undefined;
			
			f_classLoader.Destroy(progressBar);
		}
		
		this.f_super(arguments);		
	},
	/**
	 * @method public
	 */
	f_enableRemainingTime: function() {
		this._remainingTime=f_progressMonitorDialog._WAITING_TIMER;
		
		this.f_updateProgressLabel();
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_getDefaultFeatures: function() {
		return f_progressMonitorDialog._DEFAULT_FEATURES;
	},
	
	f_fillBody: function(base) {
		
		var ul=f_core.CreateElement(base, "ul", { 
			className: "f_progressMonitorDialog_container"
		});
		
		var title=f_core.CreateElement(ul, "li", {
			className: "f_progressMonitorDialog_title"
		});
		
		this._titleText=f_core.CreateElement(title, "label", {
			className: "f_progressMonitorDialog_titleText",
			innerHTML: "&nbsp;"
		});
		
		if (this._progressTitle) {
			f_core.SetTextNode(this._titleText, this._progressTitle);
 		}
		
		var body=f_core.CreateElement(ul, "li", {
			className: "f_progressMonitorDialog_body"
		});
			
		this._messageText1=f_core.CreateElement(body, "label", {
			className: "f_progressMonitorDialog_bodyText1",
			innerHTML: "&nbsp;"
		});
		
		this._messageText2=f_core.CreateElement(body, "label", {
			className: "f_progressMonitorDialog_bodyText2",
			innerHTML: "&nbsp;"
		});
	
		var buttons=f_core.CreateElement(ul, "li", {
			className: "f_shellDecorator_body_buttons"
		});
		
		this._cancel=f_core.CreateElement(buttons, "input", {
			type: "button",
			className: "f_progressMonitorDialog_cancel",
			value: "Annuler"
		});
		
		var progressBar=f_progressBar.Create(body, 355, 16, "f_progressMonitorDialog_bar");
		this._progressBar=progressBar;
		
		progressBar.f_setValue(-1);
		progressBar.f_setMin(0);
		progressBar.f_setMax(f_progressIndicator.PROGRESS_MAX);

		this.f_super(arguments, base);
	},
	/**
	 * @method public
	 * @return f_progressBar
	 */
	f_getProgressBar: function() {
		return this._progressBar;
	},

	/**
	 * @method public
	 * @param String text
	 * @return void
	 */
	f_setProgressTitle: function(text) {
		this._progressTitle=text;
		
		var label=this._titleText;
		if (label) {
			if (!text) {
				label.innerHTML="&nbsp;";	
			} else {
				f_core.SetTextNode(label, text);
			}
		}
	},
	/**
	 * @method protected
	 * @param Number value
	 * @param String nextTaskName
	 * @param String subTaskName
	 * @return void
	 */
	f_updateProgressLabel: function(value, taskName, subTaskName) {
		var label=this._messageText1;
		if (label && taskName!==undefined && taskName!=this._taskName) {
			this._taskName=taskName;
			
			if (!taskName) {
				label.innerHTML="&nbsp;";
				
			} else {
				f_core.SetTextNode(label, taskName);
			}
		}
		
		var label=this._messageText2;
		if (!label) {
			return;
		}
		
		if (this._remainingTime) {
			if (value===undefined || this._lastValue==value) {
				return;
			}
			
			var resourceBundle=f_resourceBundle.Get(f_progressMonitorDialog);			
		
			var now=new Date().getTime();
			
			if (now-this._lastTime<f_progressMonitorDialog._UPDATE_TIME_INTERVAL_MILLIS) {
				return;
			}
			this._lastTime=now;
			
			this._lastValue=value
			
			if (value>0 && !this._startTime) {
				this._startValue=value;
				this._startTime=now;
			}

			subTaskName=resourceBundle.f_get("TIME_COMPUTING");

			if (this._remainingTime==f_progressMonitorDialog._COMPUTE_TIMER 
					&& value>this._startValue) {
				var t=new Date().getTime();
				var dt=t-this._startTime;
				
				if (value>f_progressIndicator.PROGRESS_MAX) {
					value=f_progressIndicator.PROGRESS_MAX;
				}
				dt/=(value-this._startValue);
				
				dt*=(f_progressIndicator.PROGRESS_MAX-value);
				 			
				dt=Math.floor(dt/1000); // En secondes
			
				if (dt<1) {
					subTaskName=resourceBundle.f_get("TIME_ZERO");
					
				} else {		
					var params=new Object;
					
					var many=true;
					
					if (dt>59) {
						var min=Math.floor(dt/60);
						dt-=min*60;
						
						if (min>1) {
							params.minutes=resourceBundle.f_formatParams("TIME_MINUTES", {
								minutes: min
							});
							many=true;
								
						} else {
							params.minutes=resourceBundle.f_get("TIME_MINUTE");
							many=false;
						}
					}
					
					if (dt>1) {
						params.seconds=resourceBundle.f_formatParams("TIME_SECONDS", {
							seconds: dt
						});
						many=true;
						
					} else if (dt==1) {
						params.seconds=resourceBundle.f_get("TIME_SECOND");
						if (!many) { // Deux many !
							many=true; 
						} else {
							many=false;
						}
					}
					
					if (many) {
						params.left=resourceBundle.f_get("TIME_MANY_LEFT");
					} else {
						params.left=resourceBundle.f_get("TIME_ONE_LEFT");						
					}
				
					subTaskName=resourceBundle.f_formatParams("TIME_REMAINING", params);
				}
			}

		} else if (subTaskName===undefined) {
			return;			
		}
		
		if (this._subTaskName==subTaskName) {
			return;
		}
		
		this._subTaskName=subTaskName;
		
		if (!subTaskName) {
			label.innerHTML="&nbsp;";
			
		} else {
			f_core.SetTextNode(label, subTaskName);
		}
					
		if (this._remainingTime) {
			this.f_setTitle(subTaskName);
		}
	},

	/**
	 * @method public
	 * @param function runFunction
	 * @param Boolean cancelable
	 * @return void
	 */
	f_run: function(runFunction, cancelable) {
		f_core.Assert(typeof(runFunction)=="function", "f_progressMonitorDialog.f_run: Invalid runFunction parameter ("+runFunction+")");
		f_core.Assert(cancelable===undefined || typeof(cancelable)=="boolean", "f_progressMonitorDialog.f_run: Invalid cancelable parameter ("+cancelable+")");
		
		var progressIndicatorMonitor=this._progressIndicatorMonitor;
		if (progressIndicatorMonitor) {
			f_classLoader.Destroy(progressIndicatorMonitor);
		}
		
		progressIndicatorMonitor=f_progressIndicatorMonitor.f_newInstance(this);
		this._progressIndicatorMonitor=progressIndicatorMonitor;
		
		this._progressRunnable=runFunction;
		this._cancelable=cancelable;
		
		this.f_open();
		
		if (this._remainingTime==f_progressMonitorDialog._WAITING_TIMER) {
			var win=f_core.GetWindow(this);
			
			var self=this;
			this._startTimerId=win.setTimeout(function() {
				self._startTimerId=undefined;
				
				if (self._remainingTime!=f_progressMonitorDialog._WAITING_TIMER) {
					return;
				}
				self._remainingTime=f_progressMonitorDialog._COMPUTE_TIMER
				
				self.f_updateProgressLabel();

				self._remainingIntervalId=win.setInterval(function() {
					self.f_updateProgressLabel();
					
				}, f_progressMonitorDialog._COMPUTE_INTERVAL_MILLIS);
				
			}, f_progressMonitorDialog._COMPUTE_TIMER_MILLIS);
		}
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	f_prepareOpening: function() {
		this.f_super(arguments);
		
		this._cancel.disabled=!this._cancelable;

		var self=this;
		
		var win=f_core.GetWindow(this);
		
		win.setTimeout(function() {
			var run=self._progressRunnable;
			if (!run) {
				return;
			}
			self._progressRunnable=undefined;
			
			var progressIndicatorMonitor=self._progressIndicatorMonitor;
			self._progressIndicatorMonitor=undefined;
			
			run.call(win, progressIndicatorMonitor);
			
		}, 10);
	},

	f_progressDone: function() {		
		if (this._remainingTime) {
			var win=f_core.GetWindow(this);
			
			this._remainingTime=f_progressMonitorDialog._TERMINATE_TIMER;

			var startTimerId=this._startTimerId;
			if (startTimerId) {
				win.clearTimeout(startTimerId);
			}
			
			var remainingIntervalId=this._remainingIntervalId;
			if (remainingIntervalId) {
				this._remainingIntervalId=undefined;
				
				win.clearInterval(remainingIntervalId);
			}

			var self=this;
			this._endTimerId=win.setTimeout(function() {
				self._endTimerId=undefined;
				
				self.f_close();
			}, f_progressIndicatorMonitor._AUTO_CLOSE_DIALOG_TIMER_MILLIS);
			
			return;
		}

		this.f_close();
	},

	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_progressMonitorDialog]";
	}
}

new f_class("f_progressMonitorDialog", {
	extend: f_dialog,
	aspects: [ fa_progressIndicator ],
	members: __members,
	statics: __statics
});
