/*
 * $Id: fa_autoOpen.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Aspect
 *
 * @aspect public abstract fa_autoOpen
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */

var __members = {

	/**
	 * @method public
	 * @return Object 
	 */
	fa_findAutoOpenElement: f_class.ABSTRACT,
	
	/**
	 * @method protected
	 * @param Object 
	 * @return void 
	 */
	fa_performAutoOpenElement: f_class.ABSTRACT,
	
	
	/**
	 * @method protected
	 * @param Object el1
	 * @param Object elt2
	 * @return Boolean
	 */
	fa_isSameAutoOpenElement: f_class.ABSTRACT
};

new f_aspect("fa_autoOpen", {
	members: __members
});
