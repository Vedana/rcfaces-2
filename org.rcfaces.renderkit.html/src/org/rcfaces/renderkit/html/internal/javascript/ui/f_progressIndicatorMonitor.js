/*
 * $Id: f_progressIndicatorMonitor.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Classe 
 *
 * @class hidden f_progressIndicatorMonitor extends f_progressMonitor
 * @author Eclipse team & Olivier Oeuillot
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */
 
var __members={
	/**
	 * @method public
	 * @param fa_progressIndicator progressIndicator
	 */
	f_progressIndicatorMonitor: function(progressIndicator) {
		this.f_super(arguments);
		
		this._progressIndicator=progressIndicator;
	},
	f_finalize: function() {
		this._progressIndicator=undefined;  // f_progressIndicator
	
		// this._indeterminate=undefined; // boolean
		// this._totalWork =undefined; // number
		// this._sumWorked =undefined; // number
	
		this.f_super(arguments);
	},
    f_beginTask: function(taskName, totalWork) {
        this._totalWork = totalWork;
        this._sumWorked = 0;
        this._indeterminate = (totalWork==f_progressMonitor.UNKNOWN);
        
		this._progressIndicator.f_setIndeterminate(this._indeterminate); 
        
        this._progressIndicator.f_changeValues(-1, taskName, null, true);
    },
    f_done: function() {
 		this._progressIndicator.f_changeValues(f_progressIndicator.PROGRESS_MAX, null, null, true, true);
	},
	f_internalWorked: function(work) {
		//f_core.Debug(f_progressIndicator, "Internal worked="+work+" sum="+this._sumWorked+" total="+this._totalWork);
        if (!work || this._indeterminate) {
            return;
        }
        var sumWorked=this._sumWorked;
        var totalWork=this._totalWork;
        
        sumWorked += work;
        if (sumWorked > totalWork) {
            sumWorked = totalWork;
        }
        if (sumWorked < 0) {
            sumWorked = 0;
        }
        this._sumWorked=sumWorked;
        
        var value = Math.floor(sumWorked / totalWork * f_progressIndicator.PROGRESS_MAX);
        if (this._progressIndicator.f_getValue() < value) {
            this._progressIndicator.f_changeValues(value);
        }
 	},
	f_setTaskName: function(name) {
       this._progressIndicator.f_changeValues(undefined, name);
	},
	f_subTask: function(name) {
		this._progressIndicator.f_changeValues(undefined, undefined, name);
	}
}
 
new f_class("f_progressIndicatorMonitor", {
	extend: f_progressMonitor,
	members: __members
});
