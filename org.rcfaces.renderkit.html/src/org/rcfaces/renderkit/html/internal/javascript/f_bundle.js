/*
 * $Id: f_bundle.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */


/**
 * class f_bundle
 *
 * @class hidden f_bundle extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */

var __members = {
	
	/**
	 * @field private final String
	 */
	_name: undefined,
	
	/**
	 * @field private final f_classLoader
	 */
	_classLoader: undefined,
	
	/**
	 * @field private final f_class[]
	 */
	_classes: undefined,
	
	/**
	 * @method hidden
	 * @param Window win
	 * @param String name
	 * @param f_class[] classes
	 */
	f_bundle: function(win, name, classes)  {
		this._name=name;
	
		this._classes=classes;

		var timeBundle=win._rcfacesBundleTime;
		if (timeBundle) {
			var timeBundleEnd=new Date();
			
			win._rcfacesBundleTime=undefined;
			
			f_core.Profile(false, "f_bundle.parse("+name+")", timeBundle);			

			f_core.Profile(true, "f_bundle.parse("+name+")", timeBundleEnd);			
		}
		
		if (f_core.IsInfoEnabled(f_bundle)) {
			var names=new Array;
					
			for(var i=0;i<classes.length;i++) {			
				names.push(classes[i].f_getName());
			}
			
			f_core.Info(f_bundle, "f_bundle: Bundle '"+name+"' declares classes: "+names.join(","));		
		}
			
		var classLoader=f_classLoader.Get(win);
		f_core.Assert(classLoader, "f_bundle.f_bundle: Bundle '"+name+"' can not get window classloader !");
		
		this._classLoader=classLoader;
		
		classLoader._declareBundle(this);
	},
	
	f_finalize: function() {
		this._classLoader=undefined; // f_classLoader
		this._classes=undefined; // f_class[]
	},
	
	/**
	 * @method public
	 * @return f_class[]
	 */
	f_listClasses: function() {
		return this._classes;
	},
	
	/**
	 * @method public
	 * @return String
	 */
	f_getName: function() {
		return this._name;
	}
};

new f_class("f_bundle", {
	members: __members
});


