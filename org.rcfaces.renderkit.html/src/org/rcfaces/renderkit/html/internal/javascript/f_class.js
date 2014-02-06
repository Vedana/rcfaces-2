/* 
 * $Id: f_class.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */
 
 /* 
  * !!!!!!
  * 
  * Attention, a cause de la déclaration de methodes STATIQUES dans un objet JavaScript,
  * et de l'utilisation d'un optimiseur de code, 
  * Il ne faut SURTOUT PAS UTILISER de with dans le code des méthode de f_class !
  */

/**
 * f_class package
 *
 * @class public final f_class extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @author Joel Merlin
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */

var __statics = {
	
	/**
	 * @field hidden static final Boolean
	 */
	PROFILE_COMPONENT: false,
	
	/**
	 * @field hidden static final String
	 */
	ABSTRACT: "f_abstract",
	
	/**
	 * @field hidden static final String
	 */
	OPTIONAL_ABSTRACT: "f_optionalAbstract",
	
	/**
	 * @field hidden static final String
	 */
	BEFORE_ASPECT: "before",

	/**
	 * @field hidden static final String
	 */
	AFTER_ASPECT: "after",

	/**
	 * @field hidden static final String
	 */
	THROWING_ASPECT: "throwing",


	/**
	 * @field private static final Boolean
	 */
	_CLEAN_METHODS: true,
	
	/**
	 * @method private static 
	 * @param Object caller
	 * @return any
	 * @context object:this
	 */
	_Super: function(caller) {
		f_core.Assert(caller && caller.callee, "f_class._Super: First parameter must be an argument object ! (caller="+caller+")");
		
		var callee=caller.callee;

		var ksuper=callee._ksuper;
		if (!ksuper) {
			var name=callee._kname;
			var cls=callee._kclass;
			f_core.Assert(cls instanceof f_class, "f_class._Super: Can not find class of object '"+caller+"'\n["+caller.callee+"'\nclass='"+cls+"'\nname='"+name+"' !");
	
			if (window._RCFACES_LEVEL3) {
				// On recherche le parent du bon contexte !
				cls = this._kclass._classLoader.f_getClass(cls._name, cls._lookId); 
			}
	
			var p = cls._parent;

			f_core.Assert(p instanceof f_class, "f_class._Super: No parent class ! (className='"+cls._name+", method='"+name+"')");
		
			if (callee._constructor) {
				for (;p && !ksuper;p = p._parent) {
					ksuper=p._constructor;
				}
			
			} else {
				for (;p && !ksuper;p = p._parent) {
					ksuper=p._kmethods[name];
				}
			}

			if (!ksuper) {
				throw new Error("Core._Super: No super method ! (className='"+cls._name+", method='"+name+"')");
			}

			callee._ksuper=ksuper;
		}
	
		var nargs=arguments.length;
		
		if (ksuper._kcreateAspectMethod) {
// On évite d'empiler ... (a cause de IE)
//			return f_class._ManageAspects(arguments, this);

			var callerArguments = new Array;
			if (nargs>1) {
				f_core.PushArguments(callerArguments, arguments, 1);
			}
	
			var before=ksuper._kbefore;
			if (before) {
				for(var i=0;i<before.length;i++) {
					before[i].apply(this, callerArguments);
				}
			}
			
			var ret=undefined;
			var kmethod=ksuper._kmethod;
			var after=ksuper._kafter;
			if (kmethod) {
				try {
					ret=kmethod.apply(this, callerArguments);
					
				} catch (x) {
					var throwing=ksuper._kthrowing;
					if (throwing) {
						for(var i=0;i<throwing.length;i++) {
							throwing[i].call(this, x, kmethod, callerArguments);
						}
					}

					throw x;
				}
			}

			if (after) {
				for(var i=0;i<after.length;i++) {
					after[i].apply(this, callerArguments);
				}
			}

			return ret;	
		}
		
		switch(nargs) {
		case 1:
			return ksuper.call(this);

		case 2:
			return ksuper.call(this, arguments[1]);
		}
		
		var a = f_core.PushArguments(null, arguments, 1);
	
		return ksuper.apply(this, a);
	},
	/**
	 * @method hidden static final
	 */
	InitializeClass: function(claz) {
		if (claz._initialized) {
			return;
		}

		// On le met apres le test à cause du multiWindow !
		f_core.Assert((typeof(claz)=="function" && claz._nativeClass) || typeof(claz)=="object", "f_class.InitializeClass: Invalid class parameter '"+claz+"'.");
				
		claz._initialized=true;
		
		if (claz._nativeClass) {
			return;
		}
		
		var methods=new Object;
				
		var parent=claz._parent;
		if (parent) {
			f_class.InitializeClass(parent);
			
			var pms=parent._kmethods;
			for(var member in pms) {
				methods[member]=pms[member];
			}
		}
		
		// Les methodes
		var members=claz._members;
		if (members) {
			f_class._InitializeClassMembers(claz, methods);
		}

		methods.f_super = f_class._Super;
		
		var aspects=claz._aspects;
		if (aspects) {
			for(var i=0;i<aspects.length;i++) {
				f_class._InstallAspects(claz, aspects[i], methods);
			}
		}
		
		claz._kmethods = methods;
	},
	/**
	 * @method private static final
	 */
	_InitializeClassMembers: function(claz, methods) {
		f_core.Assert(typeof(claz)=="object", "f_class._InitializeClassMembers: Invalid claz parameter '"+claz+"'.");
		f_core.Assert(typeof(methods)=="object" && methods, "f_class._InitializeClassMembers: Invalid methods parameter '"+methods+"'.");		
		
		var members=claz._members;
/*
		if (members instanceof _remapContext) {
			members=this._classLoader._remapContext(members);
			claz._members=members;
		}
	*/
		var className=claz._name;
		for (var memberName in members) {
			var member=members[memberName];

			var type=typeof(member);

			// On verifie plus le "undefined" car on accepte la déclaration de champs non static !
			//f_core.Assert(type!="undefined", "f_class._InitializeClassMembers: Type undefined for "+claz._name+"."+memberName);
			if (type=="undefined") {
				// declaration d'un champ !
				continue;
			}		

			f_core.Assert(type!="object", "f_class._InitializeClassMembers: Type Object for "+claz._name+"."+memberName);
		
			if (type=="function") {
				member._kname = memberName;
				member._kclass = claz;

				if (memberName==className) {
					claz._constructor=member;
					member._constructor=true;
					
					// Pas dans les methodes !
					continue;
				}
			}
			
			methods[memberName] = member;
		}
	},
	/**
	 * @method private static final
	 */
	_ManageAspects: function(callerArguments, callerThis) {
		var obj=callerArguments.callee;
		
		var before=obj._kbefore;
		if (before) {
			for(var i=0;i<before.length;i++) {
				before[i].apply(callerThis, callerArguments);
			}
		}
		
		var ret=undefined;
		var kmethod=obj._kmethod;
		if (kmethod) {
			try {
				ret=kmethod.apply(callerThis, callerArguments);
				
			} catch (x) {
				var throwing=obj._kthrowing;
				if (throwing) {
					for(var i=0;i<throwing.length;i++) {
						throwing[i].call(callerThis, x, kmethod, callerArguments);
					}					
				}
				
				throw x;
			}
		}
		
		var after=obj._kafter;
		if (after) {
			for(var i=0;i<after.length;i++) {
				after[i].apply(callerThis, callerArguments);
			}
		}
		
		return ret;
	},
	/**
	 * @method private static final
	 */
	_InstallAspects: function(claz, aspect, methods, abstracts) {
	
		// Gestion des aspects parents !
		var parents=aspect._parents;
		if (parents) {
			for(var i=0;i<parents.length;i++) {
				var p=parents[i];
				
				f_class._InstallAspects(claz, p, methods, abstracts);
			}
		}
	
		var members=aspect._members;
		aspect._initialized=true;
/*
		if (members instanceof _remapContext) {
			members=this._classLoader._remapContext(members);
			aspect._members=members;
		}
	*/	
		var constructor;
		var aspectName=aspect._name;
		for(var memberName in members) {		
			var member=members[memberName];			
					
			if (memberName==aspectName) {
				memberName=claz._name;
				constructor=true;
				
			} else {
				constructor=false;
			}

			if (typeof(member)=="object") {
				if (memberName=="__all__") {
					for(var mname in member) {
						f_core.Assert(mname==f_class.BEFORE_ASPECT || mname==f_class.AFTER_ASPECT || mname==f_class.THROWING_ASPECT, "f_class._InstallAspects: Bad keyword '"+mname+"' defined in aspect '"+aspect._name+"'.");
						
						var m2=member[mname];
						
						f_core.Assert(typeof(m2)=="function", "f_class._InstallAspects: Bad function type '"+typeof(m2)+"' for member '"+mname+"' of aspect '"+aspect._name+"'.");
						
						for(var i=0;i<methods.length;i++) {
							f_class._InstallAspectMethod(claz, methods, methods[i], mname, m2, constructor);
						}
					}
					
					continue;
				}
				
				for(var mname in member) {
					f_core.Assert(mname==f_class.BEFORE_ASPECT || mname==f_class.AFTER_ASPECT || mname==f_class.THROWING_ASPECT, "f_class._InstallAspects: Bad keyword '"+mname+"' defined in aspect '"+aspect._name+"'.");
					
					var m2=member[mname];
					
					f_core.Assert(typeof(m2)=="function", "f_class._InstallAspects: Bad function type '"+typeof(m2)+"' for member '"+mname+"' of aspect '"+aspect._name+"'.");
						
					f_class._InstallAspectMethod(claz, methods, memberName, mname, m2, constructor);
				}
				
				continue;
			}

			if (typeof(member)=="function") {
				var type=null;

				if (memberName=="f_finalize") {
					type=f_class.BEFORE_ASPECT;

				} else if (constructor) {
					type=f_class.AFTER_ASPECT;
				}
				
				if (type) {
					f_class._InstallAspectMethod(claz, methods, memberName, type, member, constructor);
					continue;
				}

			} else if (member==f_class.ABSTRACT || member==f_class.OPTIONAL_ABSTRACT) {
				// méthode abstraite !
				if (abstracts) {
					abstracts.push(memberName);
				}
				continue;
			}
			
			if (f_core.IsDebugEnabled("f_class")) {
				var oldMember=methods[memberName];
				f_core.Assert(!oldMember || oldMember._kclass!=claz, "Aspect: Already defined member '"+memberName+"' of aspect '"+aspect._name+"'.");
			}
						
			methods[memberName]=member;
		}
	},
	/**
	 * @method private static final
	 */
	_CreateAspectMethod: function() {
		return function() {
			return f_class._ManageAspects(arguments, this);
		};
	},
	/**
	 * @method private static final
	 */
	_InstallAspectMethod: function(claz, methods, memberName, type, member, constructor) {
		var old;
		if (constructor) {
			for(var pclaz=claz;!old && pclaz;pclaz=pclaz._parent) {
				old=pclaz._constructor;
			}

		} else {
			old=methods[memberName];
		}
		
		// Il faut refaire une function a chaque niveau de classe !
		if (!old || !old._kmethod || old._kclass!=claz) {
			var f=f_class._CreateAspectMethod();
			f._kmethod=old;
			f._kname=memberName;
			f._kclass=claz;
			f._kcreateAspectMethod=true;
			
			if (constructor) {
				claz._constructor=f;
			}		
			
			old=f;
			methods[memberName]=f;
		}
		
		var l;
		switch(type) {
		case f_class.BEFORE_ASPECT:
			l=old._kbefore;
			if (!l) {
				l=old._kbefore=new Array;
			}
			l.push(member);
			break;
			
		case f_class.AFTER_ASPECT:
			l=old._kafter;
			if (!l) {
				l=old._kafter=new Array;
			}
			l.unshift(member);
			break;
			
		case f_class.THROWING_ASPECT:
			l=old._kthrowing;
			if (!l) {
				l=old._kthrowing=new Array;
			}
			l.push(member);
		}
	},
	/**
	 * @method private static final
	 */
	_Inherit: function(obj) {
		var cls=obj._kclass;
		
		// f_core.Assert(cls, "f_class._Inherit: Class of object '"+obj+"' is null !");
		// le toString de 'obj' peut planter de temps en temps ???
		
		if (!cls._initialized) {
			f_class.InitializeClass(cls);
		}
		
		var methods=cls._kmethods;
		for (var fname in methods) {
			obj[fname] = methods[fname];
		}
		
//		alert("Super '"+cls._name+"' "+cls._ksupmethods);
//		obj.f_super=cls._ksupmethods;
	},
	/**
	 * @method hidden static final
	 */
	Init: function(obj, cls, args, systemClass) {
		if (obj._kclass) {
			return obj;
		}
					
		if (f_class.PROFILE_COMPONENT) {
			f_core.Profile(false, "f_class.init("+obj.id+" / "+cls._name+")");
		}
				
		obj._kclass = cls;
		
		f_class._Inherit(obj);
		
		var constructor=undefined;
		for (var kls=cls;kls && !constructor;kls = kls._parent) {
			constructor=kls._constructor;
		}
		
		f_core.Assert(typeof(constructor)=="function", "f_class.Init: No constructor for class '"+cls._name+"'.");

		try {
			constructor.apply(obj, args);
		
		} catch (ex) {
			f_core.Error(f_class, "Init: Call of constructor of '"+cls._name+"' throws exception ! (id='"+obj.id+"')", ex);
		
			throw ex;
		}
							
		cls._classLoader._newInstance(obj, systemClass);
		
		if (f_class.PROFILE_COMPONENT) {
			f_core.Profile(true, "f_class.init("+obj.id+" / "+cls._name+")");
		}
		
		return obj;
	},
	/**
	 * @method hidden static final
	 */
	Clean: function(objs) {
		f_core.Assert(objs instanceof Array, "f_class.Clean: Invalid array of objects ("+objs+")");

		for(var i=0;i<objs.length;i++) {
			var obj=objs[i];
			if (!obj) {
				continue;
			}
			
			var cls=obj._kclass;
			f_core.Assert((cls instanceof f_class) || (typeof(cls)=="function"), "f_class.Clean: Not a class object ? ("+cls+")");
			
			var finalizer=obj.f_finalize;
			f_core.Assert((typeof(finalizer)=="function") || (typeof(finalizer)=="undefined"),"f_class.Clean: f_finalize not a function ? ("+finalizer+")");
			if (typeof(finalizer)=="function") {
				try {
					finalizer.call(obj);
					
				} catch (x) {
					f_core.Error(f_class, "Clean: Call of method f_finalize of class '"+cls._name+"' throws exception.", x);
				}
			}
				
			if (f_class._CLEAN_METHODS) {
				// Desinherit		
				var methods=cls._kmethods;
				for (var fname in methods) {
					obj[fname] = undefined;
				}
				
				obj.f_super = undefined;
			}
	
			obj._kclass = undefined;
			
			if (obj.nodeType) {
				// Un composant du DOM !
				f_core.VerifyProperties(obj);
			}
		}
	},
	/**
	 * @method private static final 
	 * @param f_classLoader classLoader
	 * @param String name
	 * @param optional Object staticMembers
	 * @param optional Object methods
	 * @param private optional function constructorFactory
	 * @context window:window
	 * @dontInline f_class
	 */
	_DeclarePrototypeClass: function(classLoader, name, staticMembers, methods, constructorFactory) {
	
		var constructorFct;
		if (methods) {
			constructorFct=methods[name];
		} else {
			methods=new Object;
		}
		
		if (!constructorFactory) {
			constructorFactory=f_class._CreateConstructor;
		}
		
		var cls=constructorFactory(constructorFct);
		f_core.Assert(!cls._name, "f_class._DeclarePrototypeClass: Invalid constructor ! ("+cls._name+")");
	
		if (!methods.f_getClass) {
			if (name!="f_class") {
				methods.f_getClass=f_class._ObjectGetClass;
			}
		}				
		if (!methods.toString) {	
			if (name!="f_class") {
				methods.toString=f_class._ObjectToString;
			}
		}
		
		cls.prototype=methods;
		//cls.prototype._kclass=cls; // SURTOUT Pas pour le LEVEL3
		cls._members=methods; // On en a besoin pour le multiWindow
		cls._name=name;
		cls._classLoader=classLoader;
		cls._nativeClass=true;

		if (!staticMembers) {
			staticMembers=new Object;
		}
		cls._staticMembers=staticMembers;
		
		if (!staticMembers.f_getName) {
			staticMembers.f_getName=f_class.f_getName;
		}
		
		if (!staticMembers.f_getClassLoader) {
			if (name!="f_class") {
				staticMembers.f_getClassLoader=f_class._ClassGetClassLoader;
			}
		}
		
		if (!staticMembers.toString) {	
			staticMembers.toString=f_class.toString;
		}			

		//cls._kernelClass=(name=="f_class");		

		if (name!="f_classLoader") {
			cls._classLoader.f_declareClass(cls);
		}
	},
	/**
	 * @method private static
	 * @return function
	 * @dontInline arguments
	 */
	_CreateConstructor: function(constructorFct) {
		return function() {
			var cls=arguments.callee;
			this._kclass=cls;

			if (typeof(this.f_finalize)=="function") {
				cls._classLoader._newInstance(this);
			}
			
			if (constructorFct) {
				constructorFct.apply(this, arguments);
			}			
		};
	},
	/**
	 * @method hidden static
	 * @param String className
	 * @param optional String requiredClassErrorMessage
	 * @return Boolean
	 */
	IsClassDefined: function(className, requiredClassErrorMessage) {
		f_core.Assert(typeof(className)=="string" && className.length, "f_class.IsClassDefined: Invalid className parameter ("+className+")");
		
		var clazz=window[className];
		if (clazz) {
			return true;
		}
		
		if (requiredClassErrorMessage) {
			if (requiredClassErrorMessage===true) {
				throw new Error("Panic: Required class '"+className+"' is not loaded !");
			}
			throw new Error(requiredClassErrorMessage);
		}
		
		return false;
	},
	/**
	 * @method private static
	 * @return f_class
	 */
	_ObjectGetClass: function() {
		return this._kclass;
	},
	/**
	 * @method private static
	 * @return f_class
	 */
	_ObjectToString: function() {
		var s="[object";
		
		var kclazz=this._kclass;
		if (kclazz) {
			s+=" class=\""+kclazz.f_getName()+"\"";
		} else {
			s+=" class=*undefined*";
		}
		
		return s+"]";
	},
	/**
	 * @method private static
	 * @return f_class
	 */
	_ClassGetClassLoader: function() {
		return this._classLoader;
	},
	/**
	 * @method public static 
	 * @return String
	 */
	f_getName: function() {
		return this._name;
	},
	
	/**
	 * @method public static 
	 * @return String
	 */
	toString: function() {
		return "[class "+this._name+"]";
	}
};

var __members = {
	
	/**
	 * @field hidden boolean
	 */
	_systemClass: undefined,
			
	/**
	 * @field hidden String
	 */
	_name: undefined,
	
	/**
	 * @field hidden Object
	 */
	_staticMembers: undefined, // Map<String, function>
	
	/**
	 * @field hidden String
	 */
	_lookId: undefined,
	
	/**
	 * @field hidden Object 
	 */
	_members: undefined,  // Map<String, function>
	
	/**
	 * @field hidden f_class
	 */
	_parent: undefined,
		
	/**
	 * @field hidden f_classLoader
	 */
	_classLoader: undefined,
	
	/**
	 * @context window:window
	 * @param String className
	 * @param optional String lookId
	 * @param optional Object staticMembers
	 * @param optional Object members
	 * @param optional f_class parentClass
	 * @dontInline f_classLoader
	 */
	f_class: function(className, lookId, staticMembers, members, parentClass) {
		// Constructeur vide: on ne fait rien !
		if (!arguments.length) {
			return;
		}
	
		var aspects;
		var classLoader;
		
		if (lookId && typeof(lookId)=="object") {
			var atts=lookId;
			
			lookId=atts.lookId;
			staticMembers=atts.statics;
			members=atts.members;
			parentClass=atts.extend;
			classLoader=atts._classLoader;
			this._systemClass=!!atts._systemClass;
			
			aspects=atts.aspects;
			
			f_core.Assert(lookId===undefined || typeof(lookId)=="string", "f_class: Invalid lookId attribute ("+lookId+") for class '"+className+"'.");
			f_core.Assert(staticMembers===undefined || (typeof(staticMembers)=="object" && staticMembers), "f_class: Invalid staticMembers attribute ("+staticMembers+") for class '"+className+"'.");
			f_core.Assert(members===undefined || (typeof(members)=="object" && members), "f_class: Invalid members attribute ("+members+") for class '"+className+"'.");
			f_core.Assert(parentClass===undefined || (typeof(parentClass)=="object" && parentClass) || (typeof(parentClass)=="function"), "f_class: Invalid parentClass attribute ("+parentClass+") for class '"+className+"'.");
			f_core.Assert(aspects===undefined || (aspects instanceof Array), "f_class: Invalid aspects attribute ("+aspects+") for class '"+className+"'.");
			
			if (f_core.IsDebugEnabled(f_class)) {
				var keywords="|lookId|members|statics|extend|_systemClass|aspects|_classLoader|_nativeClass|";
				for(var name in atts) {
					f_core.Assert(keywords.indexOf("|"+name+"|")>=0, "f_class: Unknown keyword '"+name+"' in definition of class '"+className+"'.");
				}
				
				if (aspects) {
					for(var i=0;i<aspects.length;i++) {
						var aspect=aspects[i];
						
						f_core.Assert(aspect instanceof f_aspect, "f_class: Aspect #"+i+" ("+aspect+") for className '"+className+"' lookId='"+lookId+"' is not defined !");
					}
				}
			}
			
		} else if (arguments.length>5) {
			// Aspects
			aspects=f_core.PushArguments(null, arguments, 5);
			
			if (f_core.IsDebugEnabled("f_class")) {
				for(var i=0;i<aspects.length;i++) {
					var aspect=aspects[i];
					
					f_core.Assert(aspect instanceof f_aspect, "f_class: Not an aspect ("+aspect+") for className '"+className+"' lookId='"+lookId+"' ?");
				}
			}
		}
		
		if (!classLoader) {
			var win=this._window;
			if (!win) {
				win=window;
			}
			
			classLoader=(parentClass)?parentClass._classLoader:f_classLoader.Get(win);
		}
		
		if (!parentClass && className!="f_object") {
			f_class._DeclarePrototypeClass(classLoader, className, staticMembers, members);
			
			return;
		}
	
		this._classLoader=classLoader;

		if (!staticMembers) {
			staticMembers=new Object;
		}
		
		if (!staticMembers.f_getName) {
			staticMembers.f_getName=f_class.f_getName;
		}
		
		if (!staticMembers.toString) {
			staticMembers.toString=f_class.toString;
		}			
		
		this._name = className;
		this._staticMembers = staticMembers;
		this._lookId = lookId;
		this._members = members;
		this._parent = parentClass;

		if (!aspects) {
			aspects=new Array;
		}
		this._aspects=aspects;
	
		this._classLoader.f_declareClass(this);
	},

	/**
	 * @method public final
	 * @return String
	 */
	f_getName: function() {
		return this._name;
	},
	
	/**
	 * @method public final
	 * @return String
	 */
	f_getLookId: function() {
		return this._lookId;
	},
	
	/**
	 * Returns super class of this class.
	 *
	 * @method public final
	 * @return f_class
	 */
	f_getSuperClass: function() {
		return this._parent;
	},
	
	/**
	 * Returns all aspects extended by this class.
	 *
	 * @method public final
	 * @return f_aspect[]
	 */
	f_getAspects: function() {
		return this._aspects;
	},
	
	/**
	 * @method public final
	 * @param any... args Arguments of constructor.
	 * @return Object
	 */
	f_newInstance: function(args) {
		var obj = new Object;
		
		return f_class.Init(obj, this, arguments, this._systemClass);
	},
	
	/**
	 * @method hidden final
	 * @param Object obj
	 * @param any... args Arguments of constructor.
	 * @return Object
	 */
	f_decorateInstance: function(obj, args) {
		args=f_core.PushArguments(null, arguments, 1);
	
		return f_class.Init(obj, this, args, this._systemClass);
	},
	
	/**
	 * Returns the classes loader of this class.
	 * 
	 * @method public final
	 * @return f_classLoader
	 */
	f_getClassLoader: function() {
		return this._classLoader;
	},
	
	/**
	 * @method hidden final
	 * @return void
	 */
	f_localize: function(staticMembers, instanceMembers) {
		if (staticMembers) {
			var sms=this._staticMembers;
			for(var memberName in staticMembers) {
				sms[memberName] = staticMembers[memberName];
			}
		}
		
		if (instanceMembers) {
			var ims=this._instanceMembers;
			for(var memberName in instanceMembers) {
				ims[memberName] = instanceMembers[memberName];
			}
		}
	}
};

__statics._DeclarePrototypeClass(window._rcfacesClassLoader, "f_class", __statics, __members, __statics._CreateConstructor);
