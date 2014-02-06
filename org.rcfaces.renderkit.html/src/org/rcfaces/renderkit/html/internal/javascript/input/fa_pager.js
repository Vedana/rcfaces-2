/*
 * $Id: fa_pager.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect Pager
 *
 * @aspect public abstract fa_pager
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_pagedComponentInitialized: f_class.ABSTRACT
	
};

new f_aspect("fa_pager", null, __members);
	