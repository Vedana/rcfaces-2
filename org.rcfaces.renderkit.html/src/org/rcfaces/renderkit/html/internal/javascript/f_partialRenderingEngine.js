/*
 * $Id: f_partialRenderingEngine.js,v 1.2 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_partialRenderingEngine class
 *
 * @class public f_partialRenderingEngine extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __statics = {
	/**
	 * 
	 * @method hidden static
	 * @param f_event event 
	 * @return Boolean
	 * @context object:this
	 */
	 DefaultSubmit: function(event) {

		return false;
	 }
}

var __members = {
		
}

new f_aspect("f_partialRenderingEngine", {
	statics: __statics,
	members: __members
});