/*
 * $Id: f_progressIndicator.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Classe ProgressIndicator
 *
 * @class public f_progressIndicator extends f_component, fa_progressIndicator
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) 
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */ 
 
var __statics = {
	
	/**
	 * @field hidden static final Number
	 */
	PROGRESS_MAX: 1000
}

var __members = {
	f_progressIndicator: function() {
		this.f_super(arguments);

		var progressBar=this.f_findComponent("progressBar");
		this._progressBar=progressBar;
		
		progressBar.f_ignorePropertyChanges(); // On optimise le temps d'affichage !
		progressBar.f_setValue(-1);
		progressBar.f_setMin(0);
		progressBar.f_setMax(f_progressIndicator.PROGRESS_MAX);
		
		var label=this.f_findComponent("label");
		this._label=label;
		label.f_ignorePropertyChanges();// On optimise le temps d'affichage !
	},
	
	f_finalize: function() {
		this._progressBar=undefined;
		this._label=undefined; // f_text
	
		this.f_super(arguments);
	},

	/**
	 * @method public
	 * @return f_progressBar
	 */
	f_getProgressBar: function() {
		return this._progressBar;
	},
	
	/**
	 * @method protected
	 * @param Number value
	 * @param String nextTaskName
	 * @param String subTaskName
	 * @return void
	 */
	f_updateProgressLabel: function(value, taskName, subTaskName) {
		var label=this._label;
		if (label) {
			if (!taskName) {
				taskName=" ";
			}
		
			if (taskName!=this._oldTaskName) {
				this._oldTaskName=taskName;
				
				label.f_setText(taskName);
			}
		}
	},
	f_progressDone: function() {
		if (this.f_getStatus()!=f_shell.OPENED_STATUS) {
			return;
		}
		
		this.f_close();
	}
}
 
new f_class("f_progressIndicator", {
	extend: f_component,
	aspects: [ fa_progressIndicator ],
	statics: __statics,
	members: __members
});