/*
 * $Id: fa_converter.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 * Aspect converter.
 *
 * @aspect public abstract fa_converter
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
var __members = {

	/**
	 * @method public abstract
	 * @param f_clientValidator validator
	 * @param String text
	 * @return Object
	 */
	f_getAsObject: f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param f_clientValidator validator
	 * @param Object object
	 * @return String
	 */
	f_getAsString: f_class.ABSTRACT
};

new f_aspect("fa_converter", {
	members: __members
});	
