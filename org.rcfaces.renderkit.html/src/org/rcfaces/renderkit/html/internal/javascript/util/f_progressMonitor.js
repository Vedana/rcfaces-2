/*
 * $Id: f_progressMonitor.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * Classe ProgressMonitor
 *
 * @class public f_progressMonitor extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) & Eclipse team
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	/** 
	 * Constant indicating an unknown amount of work.
	 * @field public static final Number
	 */
	UNKNOWN: -1
}
 
var __members = {

	/*
	f_finalize: function() {
		// this._canceled=undefined;
		this.f_super(arguments);
	},
	*/
	
	/**
	 * Notifies that the main task is beginning.  This must only be called once
	 * on a given progress monitor instance.
	 * 
	 * @method public
	 * @param String name the name (or description) of the main task
	 * @param Number totalWork the total number of work units into which
	 *  the main task is been subdivided. If the value is <code>UNKNOWN</code> 
	 *  the implementation is free to indicate progress in a way which 
	 *  doesn't require the total number of work units in advance.
	 * @return void
	 */
	f_beginTask: function(name, totalWork) {
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
		return this._canceled;
	},

	/**
	 * Cancel the progress monitor.
	 * 
	 * @method public
	 * @return void
	 * @see #isCanceled()
	 */
	f_cancel: function() {
		this._canceled=true;
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
	},

	/**
	 * Notifies that a given number of work unit of the main task
	 * has been completed. Note that this amount represents an
	 * installment, as opposed to a cumulative amount of work done
	 * to date.
	 *
	 * @method public
	 * @param Number work the number of work units just completed
	 * @return void
	 */
	f_worked: function(work) {
		this.f_internalWorked(work);
	}

}
 
new f_class("f_progressMonitor", null, __statics, __members, f_object);
