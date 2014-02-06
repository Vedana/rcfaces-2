/*
 * $Id: f_slideUpTransEffect.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 *
 * @class hidden f_slideUpTransEffect extends f_slideUpEffect
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {

	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {
		f_effect.Declare("slideUpTrans", this);
	}
};

var __members = {
	f_performTick: function(component, cur, next) {

		this.f_super(arguments, component, cur, next);
		
		if (cur==0) {
			return;
		}
		
		f_core.SetOpacity(component, cur);
	}
};

new f_class("f_slideUpTransEffect", null, __statics, __members, f_slideUpEffect);

