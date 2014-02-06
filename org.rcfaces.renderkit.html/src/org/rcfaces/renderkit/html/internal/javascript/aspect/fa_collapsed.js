/*
 * $Id: fa_collapsed.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Collapsed
 *
 * @aspect abstract fa_collapsed
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize: function() {
		// this._collapsed=undefined; // Boolean
	},
	*/
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isCollapsed: function() {
		if (this._collapsed===undefined) {
			// Appel depuis le constructor de l'objet !
			this._collapsed =f_core.GetBooleanAttributeNS(this, "collapsed", false);
		}

		return this._collapsed;
	},
	/**
	 * @method public
	 * @param optional Boolean set
	 * @return void
	 */
	f_setCollapsed: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (this.f_isCollapsed()==set) {
			return;
		}
		
		this._collapsed = set;
	
		this.fa_updateCollapsed(set);
		
		this.f_setProperty(f_prop.COLLAPSED, set);
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateCollapsed: f_class.ABSTRACT
}

new f_aspect("fa_collapsed", {
	members: __members
});
