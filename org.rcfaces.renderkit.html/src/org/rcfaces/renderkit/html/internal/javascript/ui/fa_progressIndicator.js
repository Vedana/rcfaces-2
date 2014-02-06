/*
 * $Id: fa_progressIndicator.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Classe ProgressIndicator
 *
 * @aspect public abstract fa_progressIndicator
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) 
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */ 
 
var __statics = {
	/**
	 * @field private static final Number
	 */
	_UPDATE_DELAY: 200
}

var __members = {

	f_finalize: function() {
		this._progressBar=undefined; // f_progressBar
		
		var progressMonitor=this._progressMonitor;
		if (progressMonitor) {
			this._progressMonitor=undefined;
			
			if (progressMonitor.f_cancel) {
				progressMonitor.f_cancel();
			}
		}

		// this._oldTaskName=undefined; // string
		// this._lastUpdate=undefined; // number
	
		// this._nextValue=undefined; // number
		// this._nextTaskName=undefined; // string
		// this._nextSubTaskName=undefined; // string
		
		var timer=this._timer; // TIMER_ID
		if (timer) {
			this._timer=undefined;
			
			window.clearTimeout(timer);
		}
	},
	
	/**
	 * @method public
	 * @return f_progressMonitor
	 */
	f_createProgressMonitor: function() {		
		var old=this._progressMonitor;
		if (old) {
			this._progressMonitor=undefined;
 			if (!old.f_isCanceled()) {
	 			old.f_cancel();
	 		}
		}
		
		var progressMonitor=f_progressIndicatorMonitor.f_newInstance(this);
		
		this._progressMonitor=progressMonitor;
		return progressMonitor;
	},
	
	/**
	 * @method public
	 * @return f_progressMonitor
	 */
	f_getCurrentProgressMonitor: function() {
		return this._progressMonitor;
	},
	/**
	 * @method hidden
	 */
	f_setIndeterminate: function(indeterminate) {
		f_core.Assert(typeof(indeterminate)=="boolean", "f_progressIndicator.f_setIndeterminate: Invalid indeterminate parameter '"+indeterminate+"'.");  

		var progressBar=this.f_getProgressBar();
		if (progressBar) {
			progressBar.f_setIndeterminate(indeterminate);
		}
	},
	/**
	 * @method hidden
	 */
	f_getValue: function() {
		var progressBar=this.f_getProgressBar();
		if (!progressBar) {
			return 0;
		}
		
		return progressBar.f_getValue();
	},
	/**
	 * @method hidden
	 */
	f_changeValues: function(value, taskName, subTaskName, important, done) {
	
		var modified=false;
	
		if (value!==undefined && value!=this._nextValue) {
			this._nextValue=value;
			modified=true;
		}
		
		if (taskName!==undefined && taskName!=this._nextTaskName) {
			this._nextTaskName=taskName;
			modified=true;
		}
		
		if (subTaskName!==undefined && subTaskName!=this._nextSubTaskName) {
			this._nextSubTaskName=subTaskName;
			modified=true;
		}
		
		
		if (!modified) {
			if (done) {
				this.f_progressDone();
			}
			return;
		}

		if (important || done) {
			this._updateValues();

			if (done) {
				this.f_progressDone();
			}
			return;
		}

		var time=new Date().getTime();
		
		if (!this._lastUpdate || (time-this._lastUpdate)>fa_progressIndicator._UPDATE_DELAY) {
			this._updateValues();
			return;
		}

		var timer=this._timer;
		if (timer) {
			return;
		}
		
		var self=this;
		this._timer=window.setTimeout(function() {
			self._updateValues();
			
		}, fa_progressIndicator._UPDATE_DELAY);
	},
	/**
	 * @method private
	 * @return void
	 */
	_updateValues: function() {
		this._lastUpdate=new Date().getTime();
		
		var nextValue=this._nextValue;
		
		if (nextValue===undefined) {
			return;
		}
		this._nextValue=undefined;
	
		var progressBar=this.f_getProgressBar();		
		if (progressBar) {
			progressBar.f_setValue(nextValue);
		}
		
		this.f_updateProgressLabel(nextValue, this._nextTaskName, this._nextSubTaskName);
	},
	
	/**
	 * @method public abstract
	 * @return f_progressBar
	 */
	f_getProgressBar: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @param Number value
	 * @param String taskName
	 * @param String subTaskName
	 * @return void
	 */
	f_updateProgressLabel: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 */
	f_progressDone: f_class.ABSTRACT
}
 
new f_aspect("fa_progressIndicator", {
	statics: __statics,
	members: __members
});