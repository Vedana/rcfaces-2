/*
 * $Id: f_menuBase.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Class MenuBase
 *
 * @class f_menuBase extends f_eventTarget, fa_menuCore
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */

new f_class("f_menuBase", {
	extend: f_eventTarget, 
	aspects: [ fa_menuCore ]
});
