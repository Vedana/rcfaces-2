/*
 * $Id: f_subProgressMonitor.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Classe SubProgressMonitor
 *
 * @class f_subProgressMonitor extends f_progressMonitor
 * @author Eclipse team & Olivier Oeuillot
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	/**
	 * Style constant indicating that calls to <code>subTask</code>
	 * should not have any effect.
	 *
	 * @see #SubProgressMonitor(IProgressMonitor,int,int)
	 * @field public static final Number
	 */
	SUPPRESS_SUBTASK_LABEL: 2,
	
	/**
	 * Style constant indicating that the main task label 
	 * should be prepended to the subtask label.
	 *
	 * @see #SubProgressMonitor(IProgressMonitor,int,int)
	 * @field public static final Number
	 */
	PREPEND_MAIN_LABEL_TO_SUBTASK: 4
}
 
var __members = {
	f_subProgressMonitor: function(wrappedMonitor, ticks, style) {
		this.f_super(arguments);

		this._wrappedMonitor=wrappedMonitor;

		this._parentTicks = (ticks)?ticks:0;
		this._style=(style)?style:0;
		
		this._sentToParent = 0;
		this._scale = 0;
		this._nestedBeginTasks = 0;
		this._usedUp = false;
		
	},
	f_finalize: function() {
		this._wrappedMonitor=undefined;
		
		//this._parentTicks = undefined; // number
		//this._sentToParent = undefined; // number
		//this._scale = undefined; // number
		//this._nestedBeginTasks = undefined; // number
		//this._usedUp = undefined; // boolean
		//this._mainTaskLabel = undefined // string
		
		// this._style=undefined; // number
		
		this.f_super(arguments);
	},
	
	f_beginTask: function(name, totalWork) {
		this._nestedBeginTasks++;
		// Ignore nested begin task calls.
		if (this._nestedBeginTasks > 1) {
			return;
		}
		// be safe:  if the argument would cause math errors (zero or 
		// negative), just use 0 as the scale.  This disables progress for
		// this submonitor. 
		this._scale = (totalWork <= 0) ? 0 : this._parentTicks / totalWork;
		if (this._style & f_subProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK) {
			this._mainTaskLabel = name;
		}
	},

	/**
	 * Notifies that the work is done; that is, either the main task is completed 
	 * or the user canceled it. This method may be called more than once 
	 * (implementations should be prepared to handle this case).
	 * 
	 * @method public
	 * @return void
	 */
	 f_done: function() {
		// Ignore if more done calls than beginTask calls or if we are still
		// in some nested beginTasks
		if (!this._nestedBeginTasks  || (--this._nestedBeginTasks) > 0) {
			return;
		}
		// Send any remaining ticks and clear out the subtask text
		var remaining = this._parentTicks - this._sentToParent;
		if (remaining > 0) {
			this._wrappedMonitor.f_internalWorked(remaining);
		}
		this.f_subTask("");
		this._sentToParent = 0;
	 },

	/**
	 * Internal method to handle scaling correctly. This method
	 * must not be called by a client. Clients should 
	 * always use the method </code>worked(int)</code>.
	 * 
	 * @method protected
	 * @param Number work the amount of work done
	 * @return void
	 */
	f_internalWorked: function(work) {
		if (this._usedUp || this._nestedBeginTasks != 1) {
			return;
		}

		var realWork = this._scale * work;
		// System.out.println("Sub monitor: " + realWork);
		this._wrappedMonitor.f_internalWorked(realWork);
		
		this._sentToParent += realWork;
		if (this._sentToParent >= this._parentTicks) {
			this._usedUp = true;
		}
	},

	/**
	 * Returns whether cancelation of current operation has been requested.
	 * Long-running operations should poll to see if cancelation
	 * has been requested.
	 *
	 * @method public
	 * @return Boolean <code>true</code> if cancellation has been requested,
	 *    and <code>false</code> otherwise
	 * @see #setCanceled(boolean)
	 */
	f_isCanceled: function() {
		return this._wrappedMonitor.f_isCanceled();
	},

	/**
	 * Cancel the progress monitor.
	 * 
	 * @method public
	 * @return void
	 * @see #isCanceled()
	 */
	f_cancel: function() {
		this._wrappedMonitor.f_cancel();
	},

	/**
	 * Sets the task name to the given value. This method is used to 
	 * restore the task label after a nested operation was executed. 
	 * Normally there is no need for clients to call this method.
	 *
	 * @method public
	 * @param String name the name (or description) of the main task
	 * @return void
	 * @see #beginTask(java.lang.String, int)
	 */
	f_setTaskName: function(name) {
		this._wrappedMonitor.f_setTaskName(name);
	},

	/**
	 * Notifies that a subtask of the main task is beginning.
	 * Subtasks are optional; the main task might not have subtasks.
	 *
	 * @method public
	 * @param String name the name (or description) of the subtask
	 * @return void
	 */
	f_subTask: function(name) {
		if (this._style & f_subProgressMonitor.SUPPRESS_SUBTASK_LABEL) {
			return;
		}

		var label = name;
		if ((this._style & f_subProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK) && this._mainTaskLabel && this._mainTaskLabel.length > 0) {
			label = this._mainTaskLabel + ' ' + label;
		}
		
		this._wrappedMonitor.f_subTask(label);
	}
}
 
new f_class("f_subProgressMonitor", null, __statics, __members, f_progressMonitor);
