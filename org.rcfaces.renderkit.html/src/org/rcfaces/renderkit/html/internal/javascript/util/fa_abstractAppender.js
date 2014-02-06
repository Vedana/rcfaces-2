/*
 * $Id: fa_abstractAppender.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect abstractAppender.
 *
 * @aspect public abstract fa_abstractAppender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {

	/**
	 * @method public abstract
	 * @param Object event
	 * @return void
	 */
	f_doAppend: f_class.ABSTRACT
}

new f_aspect("fa_abstractAppender", {
	members: __members
});	
