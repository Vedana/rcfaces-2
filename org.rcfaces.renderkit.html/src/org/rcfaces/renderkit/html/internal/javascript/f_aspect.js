/*
 * $Id: f_aspect.js,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
 
/**
 * Aspect primary object.
 *
 * @class public f_aspect extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */

/**
 * @context window:window
 */
function f_aspect(aspectName, staticMembers, members, extend) {
	// Constructeur vide: on ne fait rien !
	if (!arguments.length) {
		return;
	}

	var parents;
	var classLoader;
	
	if (staticMembers && (staticMembers.statics || staticMembers.members || staticMembers.extend)) {
		var atts=staticMembers;
		
		staticMembers=atts.statics;
		members=atts.members;
		parents=atts.extend;
		classLoader=atts._classLoader;
		
		if (f_core.IsDebugEnabled("f_aspect")) {
			var keywords="|members|statics|extend|_systemClass|_classLoader|_nativeClass|";				
			for(var name in atts) {
				f_core.Assert(keywords.indexOf("|"+name+"|")>=0, "f_aspect.f_aspect: Unknown keyword '"+name+"' in definition of aspect '"+aspectName+"'.");
			}
		}		
		
	} else if (arguments.length>3) {
		parents=f_core.PushArguments(null, arguments, 3);
	}

	if (!staticMembers) {
		staticMembers=new Object;
	}
	
	if (!staticMembers.f_getName) {
		staticMembers.f_getName=f_class.f_getName
	}
	
	if (!staticMembers.toString) {	
		staticMembers.toString=f_class.toString;
	}			

	this._name=aspectName;
	this._members=members;
	this._staticMembers=staticMembers;
	
	if (!classLoader) {
		classLoader=f_classLoader.Get(window);
	} 	
	
	this._classLoader=classLoader;
		
	if (f_core.IsDebugEnabled("f_aspect")) {
		if (parents) {
			for(var i=0;i<parents.length;i++) {
				var parent=parents[i];
				
				f_core.Assert(parent instanceof f_aspect, "f_aspect.f_aspect: Parent of aspect must be an aspect. (parent="+parent+").");
			}
		}
	}		
	this._parents=parents;
	
	this._classLoader.f_declareAspect(this);
}

f_aspect.prototype = {
	/**
	 * @method public
	 * @return String
	 */
	f_getName: function() {
		return this._name;
	},
	/**
	 * @method public
	 * @return f_classLoader
	 */
	f_getClassLoader: function() {
		return this._classLoader;
	},
	/**
	 * Returns all aspects extended by this aspect.
	 *
	 * @method public final
	 * @return f_aspect[]
	 */
	f_getAspects: function() {
		if (!this._parents) {
			this._parents=new Array;
		}
		return this._parents;
	},	
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[Aspect "+this._name+"]";
	}
};

/** 
 * Returns class name.
 *
 * @method public static 
 * @return String class name.
 */
f_aspect.f_getName=function() {
	return "f_aspect";
};

f_aspect._nativeClass=true;
//f_aspect._kernelClass=true;
