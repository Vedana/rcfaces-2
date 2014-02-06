/*
 * $Id: f_multiWindowClassLoader.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Divers Add'ons afin de pouvoir gérer le multiWindow.
 * 
 * @class f_multiWindowClassLoader extends f_classLoader
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */

/**
 * @context window:window
 * @dontInheritMethod
 */
function f_multiWindowClassLoader(win) {
	f_classLoader.call(this, win); // super(win)
	
	this._kclass=f_multiWindowClassLoader;	
	this._parent=window._rcfacesClassLoader;
	this._location=win.location.toString();
	
	win._rcfacesClassLoader=this;
	
	//this._installCoreClasses();
	this.f_requiresBundle(this._parent._mainBundleName);
}

f_multiWindowClassLoader.prototype=new f_classLoader();
	
/**
 * @method public static
 * @return String
 */
f_multiWindowClassLoader.f_getName=function() {
	return "f_multiWindowClassLoader";
};

/**
 * @field private static final Boolean
 */
f_multiWindowClassLoader._PROFILE_CLONE_CLASS=false;

if (window._RCFACES_LEVEL3) {
	/**
	 *
	 * @method hidden
	 * @param Window childWindow
	 * @return f_classLoader
	 * @context window:window
	 */ 
	f_classLoader.prototype.f_newWindowClassLoader=function(childWindow) {
	
		return new f_multiWindowClassLoader(childWindow);
	};
	
	/**
	 * @method hidden
	 * @param String... bundleNames
	 * @return void
	 * @context window:window
	 */
	f_multiWindowClassLoader.prototype.f_requiresBundle=function(bundleNames) {
		f_core.Profile(false, "f_core.multiWindow.f_requiresBundles");
		
		var cnt=0;
		try {
			var parent=this._parent;
		
			for(var i=0;i<arguments.length;i++) {
				var bundleName=arguments[i];
				
				var parentBundle=parent._bundles[bundleName];
				
				if (!parentBundle) {
					// On essaye sans le pays !
					var countryIndex=bundleName.lastIndexOf('__L');
				
					if (countryIndex>0) {
						countryIndex=bundleName.indexOf('_',countryIndex+2);
						if (countryIndex>0 && bundleName.charAt(countryIndex+1)!='_') {
							bundleName=bundleName.substring(0, countryIndex)+bundleName.substring(bundleName.lastIndexOf('.'));
							
							parentBundle=parent._bundles[bundleName];
						}
					}
					
					if (!parentBundle) {
						f_core.Debug(f_multiWindowClassLoader, "f_requiresBundle: delagate to parent bundle '"+bundleName+"'.");
						f_classLoader.prototype.f_requiresBundle.call(this, bundleName);
						continue;
					}
				}
								
				var classes=parentBundle.f_listClasses();
				
				for(var j=0;j<classes.length;j++) {
					var clazz=classes[j];
					
					if (this._classes[clazz.f_getName()]) { 
						// Ne pas recharger des classes déjà connues !!!
						continue;
					}
		
					this._cloneClass(clazz);
				}
				
				cnt+=classes.length;
			}
			
		} finally {
			f_core.Profile(true, "f_core.multiWindow.f_requiresBundles("+cnt+" classes)");	
		}
	};
	
	/**
	 * @method private
	 * @param String claz
	 * @return f_class
	 * @context window:window	 
	 */
	f_multiWindowClassLoader.prototype._cloneClass=function(clazz) {
	
		var className=clazz.f_getName();
		
		if (f_multiWindowClassLoader._PROFILE_CLONE_CLASS) {
			f_core.Profile(null, "f_core.multiWindow._cloneClass: class '"+className+"'");
		}	
	
		var classProto=clazz._classPrototype;
		if (!classProto) {
			switch(className) {
			case "Array":
				var newArray=this._window.Array;
				var parentArray=this._parent._window.Array;
				
				newArray.prototype.f_removeElement=parentArray.prototype.f_removeElement;
				newArray.prototype.f_removeElements=parentArray.prototype.f_removeElements;
				newArray.prototype.f_addElement=parentArray.prototype.f_addElement;
				newArray.prototype.f_addElements=parentArray.prototype.f_addElements;
				newArray.prototype.f_contains=parentArray.prototype.f_contains;
				newArray.prototype.f_indexOf=parentArray.prototype.f_indexOf;
				return newArray;
				
			case "f_core":
				this._window._rcfacesGW=this._parent._window._rcfacesGW;
	
				var newCore=new this._parent._window.f_core._kmethodsPrototype();
				this._window.f_core=newCore;
				
				newCore._multiWindowCore=true;
				
				this._parent._window.f_core.CopyCoreFields(newCore);
				this._window.f_core._InitLibrary(this._window); // On doit mettre explicitement xxx.f_core._InitLibrary
				
				return newCore;
				
			case "f_classLoader":
			case "f_multiWindowClassLoader":
			case "f_aspect":
			case "f_class":
			
				var win=this._window;
				
				var parentClassLoader=this._parent._window[className];
				var newClassLoader=function() {
					this._window=win;
					parentClassLoader.apply(this, arguments);		
				};
				
				newClassLoader.prototype=parentClassLoader.prototype;
				for(var i in parentClassLoader) {
					newClassLoader[i]=parentClassLoader[i];
				}
				
				newClassLoader._multiWindowCore=true;
				newClassLoader._window=win;
				win[className]=newClassLoader;
				
				if (className=="f_class" || className=="f_aspect") {
					newClassLoader._classLoader=this;
				}
				
				return newClassLoader;
			}
		}
			
		if (clazz instanceof f_aspect) {
			var parentAspects=clazz._parents;
			
			var aspects=new Array;
			if (parentAspects) {				
				for(var i=0;i<parentAspects.length;i++) {
					aspects.push(this._aspects[parentAspects[i]._name]);
				}
			}

			if (classProto) {
				var newAspect=new classProto();
				newAspect._classLoader=this;
				newAspect._aspects=aspects;
				
				this.f_declareAspect(newAspect);
				
				return newAspect;
			}
					
			var atts={
				members: clazz._members,
				statics: clazz._staticMembers,
				extend: aspects,
				_classLoader: this
			};
			
			return new this._window.f_aspect(className, atts);			
		}
	
		if (!classProto) {
			f_class.InitializeClass(clazz);
		}
	
		var parentAspects=clazz._aspects;
		
		var aspects=new Array;
		if (parentAspects) {
			for(var i=0;i<parentAspects.length;i++) {
				aspects.push(this._aspects[parentAspects[i]._name]);
			}
		}
			
		var parent;
		var parentParent=clazz._parent;
		if (parentParent) {
			var key=(parentParent._lookId)?(parentParent._name+f_class._LOOK+parentParent._lookId):parentParent._name;
			
			parent=this._classes[key];
		}
	
		if (classProto) {
			var newClass=new classProto();
			newClass._classLoader=this;
			newClass._parent=parent;
			newClass._aspects=aspects;
			
			this.f_declareClass(newClass);
						
		} else {
			var atts={
				extend: parent,
				aspects: aspects,
				_classLoader: this
			};
			
			var lookId=clazz._lookId;
			if (lookId) {
				atts.lookId=lookId;
			}
		
			var members=clazz._members;
			if (members) {
				atts.members=members;
			}
	
			var staticMembers=clazz._staticMembers;
			if (staticMembers) {
				atts.statics=staticMembers;
			}
	
			if (clazz._systemClass) {
				atts._systemClass=true; 
			}
	
			if (clazz._nativeClass) {
				atts._nativeClass=true; 
			}
	
			var newClass=new this._window.f_class(className, atts);
	
			if (!newClass._nativeClass) {
				newClass._kmethods=clazz._kmethods;
				newClass._constructor=clazz._constructor;
				newClass._initialized=true;
			}
		}
			
		if (className=="f_resourceBundle") {
			// On recopie les bundles !

			this._parent._window.f_resourceBundle.CopyResourcesToChild(this._window.f_resourceBundle.PrepareParentCopy());
		}

		return newClass;
	};
	
	
	/**
	 * @method public 
	 * @return String
	 */
	f_multiWindowClassLoader.prototype.toString=function() {
		if (!this._window) {
			return "[MultiWindowClassLoader]";
		}
		return "[MultiWindowClassLoader '"+this._window.location+"']";
	};
}