/* 
 * $Id: f_classLoader.js,v 1.5 2013/12/12 15:39:12 jbmeslin Exp $
 *
 */

/**
 * f_classLoader
 *
 * @class public f_classLoader extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/12 15:39:12 $
 */
 
/**
 * @context window:window
 */
function f_classLoader(win) {
	if (!arguments.length) {
		// Constructeur vide pour l'héritage
		return;
	}
	
	f_core.Assert(win, "f_classLoader.f_classLoader: Invalid window parameter ("+win+")");

	//this._tagId=new Date().getTime() % 100000;	
	this._window=win;
	if (window._RCFACES_LEVEL3) {
		// Utilisation en LEVEL3 sans frame 0 !
		if (win.f_classLoader) { 
			win.f_classLoader._window=win; // Pour l'acces a Window des methodes statiques
		}
	}
		
	this._objectPool=new Array;
	this._componentPool=new Array;
	this._systemComponentPool=new Array;
	this._classes=new Object;
	this._aspects=new Object;
	this._bundles=new Object;
	this._serializedStates=new Object;
	this._documentCompleteObjects=new Array;
	this._serializableObjects=new Array;
	this._kclass=f_classLoader;
}

f_classLoader.prototype = {
	
	/**
	 * @field hidden final Window
	 */
	_window: undefined,
	
	/**
	 * @method public final
	 * @return Window
	 */
	f_getWindow: function() {
		return this._window;
	},
	
	/**
	 * @method public final
	 * @return Document
	 */
	f_getDocument: function() {
		return this._window.document;
	},
	
	/**
	 * @method public final
	 * @param String className Name of class.
	 * @param optional String lookId Look id.
	 * @return f_class
	 */
	f_getClass: function(className, lookId) {
		f_core.Assert(typeof(className)=="string", "f_classLoader.f_getClass: Invalid className parameter ("+className+")");
		f_core.Assert(lookId===undefined || typeof(lookId)=="string", "f_classLoader.f_getClass: Invalid lookId parameter ("+lookId+")");
		
		if (lookId) {
			var claz=this._classes[className+f_class._LOOK+lookId];
			if (claz) {
				if (!claz._initialized) {
					f_class.InitializeClass(claz);
				}
				
				return claz;
			}
		}
		
		var claz=this._classes[className];
		if (claz) {
			if (!claz._initialized) {
				f_class.InitializeClass(claz);
			}
			
			return claz;
		}
		
		return null;
	},
	
	/**
	 * @method public final
	 * @param String aspectName Name of aspect.
	 * @return f_aspect
	 */
	f_getAspect: function(aspectName) {
		f_core.Assert(typeof(aspectName)=="string", "f_classLoader.f_getAspect: Invalid aspectName parameter ("+aspectName+")");

		var aspect=this._aspects[aspectName];
		if (aspect) {
			return aspect;
		}

		return null;
	},
	/**
	 * @method hidden final
	 * @param f_class claz
	 * @return void
	 * @context object:this
	 */
	f_declareClass: function(claz) {
		var key=f_classLoader._MakeClassName(claz._name, claz._lookId);
		
		f_core.Assert(typeof(key)=="string", "f_classLoader.f_declareClass: Invalid className '"+key+"'.");
		
		f_core.Assert(!this._classes[key], "f_classLoader.f_declareClass: Class '"+key+"' is already declared.");
	
		this._classes[key] = claz;
	
		f_core.Debug(f_classLoader, "f_declareClass: Registering class "+claz._name+((claz._lookId)?" (lookId="+claz._lookId+")":"")+".");
	
		if (!claz._lookId) {
			this._window[claz._name]=claz;
		}
		
		if (window._RCFACES_LEVEL3 && !f_core._multiWindowCore && window._rcfacesMultiWindowClassLoader===true && !claz._multiWindowCore && /* !claz._kernelClass && */ !claz._nativeClass) {
			var proto=new f_class();
			
			proto._name=claz._name;
			proto._lookId=claz._lookId;
			proto._members = claz._members;
			proto._systemClass=claz._systemClass;
			proto._nativeClass=claz._nativeClass; 
			proto._initialized=true;
			proto._dontCopyStaticMembers=true;
			
			staticMembers=claz._staticMembers;
			proto._staticMembers=staticMembers;
			
			for(var name in staticMembers) {
				proto[name]=staticMembers[name];				
			}
			
			f_class.InitializeClass(claz);
			
			proto._kmethods=claz._kmethods;
			proto._constructor=claz._constructor;

			var classPrototype=function() { };
			classPrototype.prototype=proto;
			
			claz._classPrototype=classPrototype;
			
			this._initializeStaticMembers(claz);
			return;
		}
	
		this._initializeStaticMembers(claz, claz._dontCopyStaticMembers);
	},
	
	/**
	 * @method hidden final
	 * @param f_aspect aspect
	 * @return void
	 */
	f_declareAspect: function(aspect) {
	
		var name=aspect._name;
		
		f_core.Assert(aspect instanceof f_aspect, "f_classLoader.f_declareAspect: Aspect parameter must be an aspect ! ("+aspect+")");
		f_core.Assert(!this._aspects[name], "f_classLoader.f_declareAspectAspect: Aspect '"+name+"' is already known.");
	
		f_core.Debug(f_classLoader, "f_declareAspect: Registering aspect "+name+".");
		
		this._aspects[name] = aspect;
		this._window[name] = aspect;

		if (window._RCFACES_LEVEL3 && !aspect._multiWindowCore && window._rcfacesMultiWindowClassLoader===true && !f_core._multiWindowCore) {
			var proto=new f_aspect();				
			
			proto._name = aspect._name;
			proto._members = aspect._members;
			proto._multiWindowCore=true;
			
			var staticMembers=aspect._staticMembers;
			proto._staticMembers=staticMembers;
			
			for(var name in staticMembers) {
				proto[name]=staticMembers[name];				
			}

			var classPrototype=function() { };
			classPrototype.prototype=proto;
			
			aspect._classPrototype=classPrototype;
			
			this._initializeStaticMembers(aspect);
			return;
		}

		this._initializeStaticMembers(aspect, aspect._multiWindowCore);
	},
	
	/**
	 * @method hidden final 
	 * @return void
	 */
	f_onExit: function() {
		// Cleanup objects
	
		f_core.Profile(false, "f_classLoader.onExit");
		
		this._exiting=true;
		
		//
		var onFocusIds=this._onFocusIds;
		if (onFocusIds) {
			this._onFocusIds=undefined;
			
			document.body.onfocusin=null;
		}
		
		var onOverIds=this._onOverIds;
		if (onOverIds) {
			this._onOverIds=undefined;
			
			document.body.onmouseover=null;
		}

		this._onFireInits=undefined; // List<String>
		this._onCompleteIds=undefined; // List<String>
		this._onMessageIds=undefined; // List<String>
		this._onInitComponentListeners=undefined; // List<function>
		this._visibleListeners=undefined; // List<f_component>
		this._documentCompleteObjects=undefined; // List<Object>
		this._serializableObjects=undefined; // List<Object>
	
		// Vide le pool des objets AVANT !
		var pool=this._componentPool;
		f_core.Assert(pool, "f_classLoader.f_onExit: Invalid Objects componentPool !");
		this._componentPool=undefined;
	
		f_core.Debug("f_classLoader", "f_onExit: Clean "+pool.length+" components store into component pool !");
		f_class.Clean(pool);
		
		f_core.Profile(null, "f_classLoader.onExit.clean(components)");
		
		// Vide le pool des composants
		pool=this._objectPool;
		f_core.Assert(pool, "f_classLoader.f_onExit: Invalid Objects pool !");
		this._objectPool=undefined;
	
		f_core.Debug("f_classLoader","f_onExit: Clean "+pool.length+" objects store into component pool !");
		f_class.Clean(pool);
	
		// Vide le pool SYSTEM des composants
		pool=this._systemComponentPool;
		f_core.Assert(pool, "f_classLoader.f_onExit: Invalid Objects pool !");
		this._systemComponentPool=undefined;
	
		f_core.Debug("f_classLoader", "f_onExit: Clean "+pool.length+" system objects store into component pool !");
		f_class.Clean(pool);
	
		f_core.Profile(null, "f_classLoader.onExit.clean(objects)");
	
		this._mainBundleName=undefined;
		this._bundles=undefined;
		
		// Il semble que la destruction des classes ne soient pas 
		// obligatoire (fuite mémoire)
		var classes=this._classes;
		f_core.Assert(classes, "f_classLoader.f_onExit: Invalid Classes pool !");
		this._classes=undefined;
	
		var systemClasses;
	
		for (var claz in classes) {
			var cls = classes[claz];

			/*
			Surtout pas ! Car des méthodés statiques ont put être utilisées sans une seule instanciation ...  
	
			if (!cls._initialized) {
				continue;
			}
			*/
	
			var staticMembers=cls._staticMembers;
			if (!staticMembers) {
				continue;
			}
	
			var staticFinalizer=staticMembers.Finalizer;
			if (!staticFinalizer) {
				continue;
			}

			if (cls._systemClass) {
				if (!systemClasses) {
					systemClasses=new Array;
				}
				systemClasses.push(cls);
				continue;
			}
		
			f_core.Assert(typeof(staticFinalizer)=="function", "f_classLoader.f_onExit: Type of Finalizer callback of class '"+cls._name+"' is not a function  ! ("+staticFinalizer+")");
			
			try {
				staticFinalizer.call(cls);
				
			} catch (x) {
				f_core.Error("f_classLoader", "f_onExit: Call of method Finalizer of class '"+cls._name+"' throws exception.", x);
			}
		}
			
		f_core.Profile(null, "f_classLoader.onExit.clean(classes)");
			
		var aspects=this._aspects;
		f_core.Assert(aspects, "f_classLoader.f_onExit: Invalid Aspects pool !");
		this._aspects=undefined;
				
		for (var name in aspects) {
			var aspect = aspects[name];
	
			/*
			Surtout pas ! Car des méthodés statiques ont put être utilisées sans une seule instanciation ...  
			 
			if (!aspect._initialized) {
				continue;
			}
			
			*/
	
			var staticMembers=aspect._staticMembers;
			if (!staticMembers) {
				continue;
			}
	
			var staticFinalizer=staticMembers.Finalizer;
			if (!staticFinalizer) {
				continue;
			}
	
			f_core.Assert(typeof(staticFinalizer)=="function", "f_classLoader.f_onExit: Type of Finalizer callback of aspect '"+aspect._name+"' is not a function ! ("+staticFinalizer+")");
			
			try {
				staticFinalizer.call(aspect);
				
			} catch (x) {
				f_core.Error("f_classLoader", "f_onExit: Call of method Finalizer of aspect '"+aspect._name+"' throws exception.", x);
			}
		}		

		f_core.Profile(null, "f_classLoader.onExit.clean(aspects)");

		if (systemClasses) {
			for(var i=0;i<systemClasses.length;i++) {
				var cls=systemClasses[i];
				
				var staticMembers=cls._staticMembers;
				if (!staticMembers) {
					continue;
				}
		
				var staticFinalizer=staticMembers.Finalizer;
				if (!staticFinalizer) {
					continue;
				}
			
				f_core.Assert(typeof(staticFinalizer)=="function", "f_classLoader.f_onExit: Type of Finalizer callback of class '"+cls._name+"' is not a function  ! ("+staticFinalizer+")");
				
				try {
					staticFinalizer.call(cls);
					
				} catch (x) {
					f_core.Error("f_classLoader", "f_onExit: Call of method Finalizer of class '"+cls._name+"' throws exception.", x);
				}			
			}
			
			f_core.Profile(null, "f_classLoader.onExit.clean(systemClasses)");
		}		

		var win=this._window;
		this._window=undefined;

		for (var claz in classes) {
			win[claz._name]=undefined;
		}
		for (var aspect in aspects) {
			win[aspect._name]=undefined;
		}
		
		f_core.Profile(true, "f_classLoader.onExit");
		
		win._rcfacesClassLoader=undefined;
	
		//this._documentCompleted=undefined; // boolean
		//this._lazyIndex=undefined; // number
		
		// this._exiting=undefined; // boolean
	},
	
	/**
	 * @method hidden final
	 * @return void
	 */
	f_onDocumentComplete: function() {
		f_core.Assert(!this._documentCompleted, "f_classLoader.f_onDocumentComplete: Document has been already completed !");
		this._documentCompleted=true;
		
		var classes=this._classes;
		f_core.Debug(f_classLoader, "f_onDocumentComplete: Calling static DocumentComplete methods ...");
		
		var nb=0;
		for(var i in classes) {
			var claz=classes[i];
			
			var staticMembers=claz._staticMembers;
			if (!staticMembers) {
				continue;
			}
			
			var fct=staticMembers.DocumentComplete;
			if (!fct) {
				continue;
			}
			
			f_core.Assert(typeof(fct)=="function", "f_classLoader.f_onDocumentComplete: Type of DocumentComplete callback is not a function ! ("+fct+")");
			
			nb++;
			try {
				fct.call(claz);
	
			} catch (x) {			
				f_core.Error(f_classLoader, "f_onDocumentComplete: Exception during DocumentComplete for class "+claz._name, x);
			}
		}
		f_core.Debug(f_classLoader, "f_onDocumentComplete: "+nb+" static DocumentComplete method(s) called.");

		nb=0;
	
		var documentCompleteObjects = this._documentCompleteObjects;
		f_core.Assert(documentCompleteObjects, "f_classLoader.f_onDocumentComplete: Document complete objects array is null !");
		
		this._documentCompleteObjects=undefined;
		
		f_core.Debug(f_classLoader, "f_onDocumentComplete: Calling f_documentComplete methods ... ("+documentCompleteObjects.length+" objects)");
		for (var i=0; i<documentCompleteObjects.length; i++) {
			var obj = documentCompleteObjects[i];
			if (!obj) {
				continue;
			}
			
			var fct=obj.f_documentComplete;
			nb++;
			try {
				fct.call(obj);
				
			} catch (x) {
				f_core.Error(f_classLoader, "f_onDocumentComplete: Exception during documentComplete event for object "+obj.id+"/"+obj.tagName, x);
			}
		}	

		f_core.Debug(f_classLoader, "f_onDocumentComplete: "+nb+" f_documentComplete method(s) called.");
	},
	
	/**
	 * @method hidden final
	 * @param Object object
	 * @param Boolean systemClass
	 * @return void
	 */
	_newInstance: function(object, systemClass) {
		// Certains composants sont de type "object" !!!! (<OBJECT> sous fx par exemple)
		f_core.Assert(typeof(object)=="object" || typeof(object)=="function", "f_classLoader._newInstance: Object parameter must be an object ! ("+typeof(object)+")");
	
		if (this._exiting && !systemClass) {
			throw "This classloader is exiting ... [newInstance: "+((object._kclass)?("className="+object._kclass._name):"")+",tagName="+object.tagName+"]";
		}
	
		var pool;
		if (systemClass) {
			pool=this._systemComponentPool;
			f_core.Debug(f_classLoader, "_newInstance: Add SYSTEM component '"+object.id+"' of class '"+object._kclass._name+"' into component pool.", (window.RCFACES_newInstanceStack)?(new Error()):null);
	
		} else if (object.tagName) {
			pool=this._componentPool;
			f_core.Debug(f_classLoader, "_newInstance: Add component '"+object.id+"' of class '"+object._kclass._name+"' into component pool.", (window.RCFACES_newInstanceStack)?(new Error()):null);
			
		} else {
			pool=this._objectPool;
			
			if (f_core.IsDebugEnabled(f_classLoader) && window.RCFACES_newObjectInstanceStack) {
				f_core.Debug(f_classLoader, "_newInstance: Add object of class '"+object._kclass._name+"' into object pool.", (window.RCFACES_newInstanceStack)?(new Error()):null);
			}
		}	
		
		f_core.Assert(pool, "f_classLoader._newInstance: Pool must be defined !");
		pool.push(object);
		
		var documentCompleteObjects=this._documentCompleteObjects;
		if (documentCompleteObjects && typeof(object.f_documentComplete)=="function") {
			
			object._documentCompleteObjectsIndex=documentCompleteObjects.length;
			documentCompleteObjects.push(object);
		}
		
		if (typeof(object.f_serialize0)=="function") {
			var serializableObjects=this._serializableObjects;

			object._serializableObjectsIndex=serializableObjects.length;
			serializableObjects.push(object);
		}
	},
	
	/**
	 * @method hidden
	 * @param String... bundleNames
	 * @return void
	 */
	f_requiresBundle: function(bundleNames) {
		if (this._exiting) {
			throw "This classloader is exiting ... [requiresBundle]";
		}
			
		for(var i=0;i<arguments.length;i++) {
			var bundleName=arguments[i];
		
			this._loadBundle(bundleName);
		}	
	},
	/**
	 * @method private
	 * @param String bundleName
	 * @return Boolean
	 */
	_loadBundle: function(bundleName) {
	
		if (this._interactiveMode) {
			return this._asyncLoadBundle(bundleName);
		}
	
		var bundles=this._bundles;
		if (bundles[bundleName]) {
			f_core.Debug(f_classLoader, "_loadBundle: Bundle already loaded '"+bundleName+"'.");
	
			return true;
		}
		
		var url=f_env.ComputeJavaScriptURI(bundleName);
	
		f_core.Info(f_classLoader, "_loadBundle: Load '"+bundleName+"' located at url '"+url+"'.");

		f_core.Profile(null, "f_classLoader.requestBundle("+bundleName+")");
	
		document.write("<SCRIPT type=\"text/javascript\" charset=\"UTF-8\" src=\""+url+"\"></SCRIPT>");
		
		return true;
	},
	
	// Initialize les objets "lazy" qui utilisent le tag v:init pour être identifiés.
	/**
	 * @method hidden
	 * @return void
	 */
	f_initializeObjects: function() {
		if (this._interactiveMode) {
			return;
		}
	
		if (this._exiting) {
			throw "This classloader is exiting ... [initializeObjects]";
		}

		if (!window._rcfacesDisableInitSearch) {
			var root=this._window.document.body;
			
			var lazys=root.getElementsByTagName(f_core._VNS+":init");
			
			f_core.Debug(f_classLoader, "f_initializeObjects: "+lazys.length+" lazy object(s) found !"+
				((this._lazyIndex)?"(Current index="+this._lazyIndex+")":""));
			
			if (!lazys.length) {
				return;
			}
		
			var index=this._lazyIndex;
			if (index===undefined) {
				index=0;
			}
			
			var components=new Array;
			
			var evaluations=new Object;
			
			for(;index<lazys.length;) {
				var component=lazys[index++];
				
				this._lazyIndex=index;
				
				var requires=f_core.GetAttribute(component, "requiresBundle");
				if (requires) {
					var args=requires.split(";");
					
					this.f_requiresBundle.apply(args);
				}
				
				var rid=f_core.GetAttribute(component, "rid");
				if (rid) {
					components.push(rid, component);
					continue;
				}
				
				var clz=f_core.GetAttributeNS(component,"class");
				if (clz) {
					components.push(component, component);
					continue;
				}
				
				var fct=f_core.GetAttributeNS(component,"function");
				if (fct) {
					var evaluatedFunction=evaluations[fct];
					if (!evaluatedFunction) {
						try {
							evaluatedFunction=f_core.WindowScopeEval(fct);
		
						} catch (x) {
							f_core.Error(f_classLoader, "f_initializeObjects: Failed to evaluate function '"+fct+"'.", x);					
							continue;
						}
	
						if (typeof(evaluatedFunction)!="function") {
							f_core.Error(f_classLoader, "f_initializeObjects: Invalid type of function '"+fct+"': "+evaluatedFunction);
							continue;
						}
						
						evaluations[fct]=evaluatedFunction;
					}
									
					components.push(evaluatedFunction, component);
					continue;
				}
				
				// C'est donc le frere !
				var prev=component.previousSibling;
				for(;prev;prev=prev.previousSibling) {
					if (prev.nodeType!=f_core.ELEMENT_NODE || !prev.tagName) {
						continue;
					}
					
					if (prev.tagName.toLowerCase()=="script") {
						// C'est le cas du premier tag INIT !
						// ou de requires JS !
						continue;
					}
					
					var clz=f_core.GetAttributeNS(prev,"class");
					if (!clz) {
						f_core.Warn(f_classLoader, "f_initializeObjects: Lazy detection: Unknown previous sibling type '"+prev.tagName+"#"+prev.id+"'.");
						continue;
					}
		
					break;
				}
				
				if (prev) {				
					components.push(prev, prev);
					continue;
				}
				
				var path="";
				for(var p=component;p;p=p.parentNode) {
					if (path.length>0) {
						path=" > "+path;
					}
					
					if (!p.tagName) {
						continue;
					}
					path=p.tagName+((p.id)?("#"+p.id):"")+path;
				}
		
				f_core.Error(f_classLoader, "f_initializeObjects: Unknown lazy component path='"+path+"'.");
			}
			
			var onInitComponentListeners=this._onInitComponentListeners;
			
			for(var i=0;i<components.length;) {
				var obj=components[i++];
				var node=components[i++];
			
				var o;
				if (typeof(obj)=="function") {
					try {
						o = obj.call(this, node);
					
					} catch (x) {
						f_core.Error(f_classLoader, "f_initializeObjects: Failed to initialize object by function '"+obj+"'.", x);
						continue;
					}
		
				} else {
					try {
						o=this.f_init(obj);
						
					} catch (x) {
						f_core.Error(f_classLoader, "f_initializeObjects: Failed to initialize object '"+obj.id+"'.", x);
						continue;
					}
				}
										
				if (!o) {
					continue;
				}
				
				var clientDatas=f_core.ParseDataAttribute(node);
				if (clientDatas) {
					o._clientDatas=clientDatas;
				}
					
				var completeComponent=o.f_completeComponent;
				if (typeof(completeComponent)=="function") {
					try {
						completeComponent.call(o);
			
					} catch (x) {
						f_core.Error(f_classLoader, "f_initializeObjects: f_completeComponent throws exception for component '"+o.id+"'.", x);
					}
				}
				
				if (onInitComponentListeners) {
					this._callOnInitComponentListeners(onInitComponentListeners, o);
				}
			}
		}
		
		var onCompleteIds=this._onCompleteIds;
		if (onCompleteIds) {
			this.f_verifyOnComplete();
		}
		
		var onFireInits=this._onFireInits;
		if (onFireInits) {
			this.f_verifyOnFireInits();
		}
	},
	/**
	 * @method private
	 * @param function[] listeners 
	 * @param f_object component
	 * @return Boolean
	 */
	_callOnInitComponentListeners: function(listeners, component) {
		
		if (!listeners) {
			return undefined;
		}
		
		var ret=undefined;
		
		// On passe pas par un object Event pour des raisons de performances
		for(var i=0;i<listeners.length;i++) {
			var fct=listeners[i];
			
			try {
				if (fct.call(this, component)===false) {
					ret=false;
				}
				
			} catch (x) {
				f_core.Error(f_classLoader, "_callOnInitComponentListeners: Call of onInitComponentListeners ("+fct+") throws exception", x);
			}
		}
		
		return ret;
	},
	/**
	 * @method hidden final
	 * @param String... ids
	 * @return void
	 */
	f_initOnComplete: function(ids) {
		 
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var i=0;i<arguments.length;i++) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=arguments[i];
			}	
			
			f_core.Debug(f_classLoader, "f_initOnComplete: ("+arguments.length+" components) ids="+idsLog);
		}

		var onCompleteIds=this._onCompleteIds;
		if (!onCompleteIds) {
			onCompleteIds=ids;
			
			this._onCompleteIds=onCompleteIds;
			return;
		}
		
		onCompleteIds.push.apply(onCompleteIds, ids);		

		/*
		var onInitComponentListeners=this._onInitComponentListeners;

		for(var i=0;i<arguments.length;i++) {
			var obj=this.f_init(arguments[i], false, false);
			if (!obj) {
				continue;
			}			
			
			if (onInitComponentListeners) {
				this._callOnInitComponentListeners(onInitComponentListeners, obj);
			}
		}
		*/
	},
	/**
	 * @method hidden final
	 * @param String... ids
	 * @return void
	 */
	f_onInit: function(ids) {
		 
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var i=0;i<arguments.length;i++) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=arguments[i];
			}	
			
			f_core.Debug(f_classLoader, "f_onInit: ("+arguments.length+" components) ids="+idsLog);
		}

		var onInitComponentListeners=this._onInitComponentListeners;

		for(var i=0;i<arguments.length;i++) {
			var obj=this.f_init(arguments[i], false, true);
			if (!obj) {
				continue;
			}			
			
			if (onInitComponentListeners) {
				this._callOnInitComponentListeners(onInitComponentListeners, obj);
			}
		}
	},
	/**
	 * @method hidden final
	 * @param String[] ids
	 * @return void
	 */
	f_initOnMessage: function(ids) {
		
		f_core.Debug(f_classLoader, "f_initOnMessage: ***********************"+ids);

		if (f_core.IsDebugEnabled(f_classLoader)) {
			f_core.Debug(f_classLoader, "f_initOnMessage: ("+ids.length+" components) ids="+ids.join());
		}
		
		if (this._onMessageVerified) {
			// On initialise tout de suite car on a des messages sur le feu !!!
			
			this._initializeIds(ids);
			return;
		}

		var onMessageIds=this._onMessageIds;
		if (!onMessageIds) {
			onMessageIds=ids;
			
			this._onMessageIds=onMessageIds;
			return;
		}
		
		onMessageIds.push.apply(onMessageIds, ids);
	},
	/**
	 * @method hidden final
	 * @param HTMLFormElement form
	 * @return void
	 */
	f_verifyOnMessage: function(form) {
		this._onMessageVerified=true;

		var onMessageIds=this._onMessageIds;
		if (!onMessageIds) {
			return;
		}
		this._onMessageIds=undefined;
		
		f_core.Debug(f_classLoader, "f_verifyOnMessage: initialize "+onMessageIds.length+" components.");
		
		this._initializeIds(onMessageIds);
	},
	/**
	 * @method hidden final
	 * @param HTMLFormElement form
	 * @return void
	 */
	f_verifyOnComplete: function(form) {
		var onCompleteIds=this._onCompleteIds;
		if (!onCompleteIds) {
			return;
		}
		this._onCompleteIds=undefined;
		
		f_core.Info(f_classLoader, "f_verifyOnComplete: initialize "+onCompleteIds.length+" components.");
		
		this._initializeIds(onCompleteIds);
	},
	/**
	 * @method hidden final
	 * @param HTMLFormElement form
	 * @return void
	 */
	f_verifyOnFireInits: function(form) {
		var onFireInits=this._onFireInits;
		if (!onFireInits) {
			return;
		}
		this._onFireInits=undefined;
		
		f_core.Info(f_classLoader, "f_verifyOnFireInits: fire init for "+onFireInits.length+" components.");
		
		var doc=this.f_getDocument();
		for(var i=0;i<onFireInits.length;i++) {
			var componentId=onFireInits[i];
						
			var component=doc.getElementById(componentId);
			if (!component) {
				f_core.Error(f_classLoader,"f_verifyOnFireInits["+i+"/"+ids.length+"]: Can not find component '"+componentId+"'.");
				continue;
			}
			
			try {
				component.f_fireEvent(f_event.INIT);
				
			} catch (ex) {
				f_core.Error(f_classLoader, "f_verifyOnFireInits: Callback throw an exception.", ex);
			}			
		}

		f_core.Info(f_classLoader, "f_verifyOnFireInits: fire init DONE");
	},
	
	/**
	 * @method private
	 * @param String[] ids
	 * @return void
	 */
	_initializeIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			f_core.Debug(f_classLoader, "_initializeIds: ("+ids.length+" objects) ids="+ids.join());
		}

		var documentComplete = this.f_isDocumentCompleted();

		for(var i=0;i<ids.length;i++) {
			var componentId=ids[i];
	
			f_core.Debug(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: search component '"+componentId+"'.");

			var component=document.getElementById(componentId);
			if (!component) {
				// On peut avoir changer de page de componentsGrid / additionnalInformations
				f_core.Info(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: Can not find component '"+componentId+"'.");
				continue;
			}
			
			if (f_classLoader.IsObjectInitialized(component)) {
				f_core.Info(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: Already initialized '"+componentId+"'.");
				continue;
			}
			
			f_core.Info(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: Initialize component '"+componentId+"'.");
			try {
				this.f_init(component, false, true);
				
			} catch (ex) {
				f_core.Error(f_classLoader, "_initializeIds: Can not initialize component '"+componentId+"'.", ex);
			}			
						
			var onInitComponentListeners=this._onInitComponentListeners;
			if (onInitComponentListeners) {
				f_core.Debug(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: Call onInit on component '"+componentId+"'.");

				try {
					this._callOnInitComponentListeners(onInitComponentListeners, component);
					
				} catch (ex) {
					f_core.Error(f_classLoader, "_initializeIds: Can not call onInit on component '"+componentId+"'.", ex);
				}	
			}
			
			if (documentComplete) {
				var documentCompleteFct = component.f_documentComplete;
				if (documentCompleteFct) {
					f_core.Debug(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: Call documentComplete on component '"+componentId+"'.");
					
					try {			
						documentCompleteFct.call(component);
				
					} catch (ex) {
						f_core.Error(f_classLoader, "_initializeIds: Can not call documentComplete on component '"+componentId+"'.", ex);
					}			
				}
			}			

			f_core.Debug(f_classLoader,"_initializeIds["+i+"/"+ids.length+"]: End of initialization of component '"+componentId+"'.");
		}
	},
	/**
	 * @method hidden final
	 * @param Set ids
	 * @return void
	 */
	f_initOnAccessIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var id in ids) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=id;
			}	

			f_core.Debug(f_classLoader, "f_initOnAccessIds: ids="+idsLog);
		}

		f_key.AddAccessKeyByClientIds(ids);
	},
	/**
	 * @method hidden final
	 * @param String[] ids
	 * @return void
	 */
	f_initOnSubmitIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			f_core.Debug(f_classLoader, "f_initOnSubmitIds: ("+ids.length+" components) ids="+ids.join());
		}

		var onSubmitIds=this._onSubmitIds;
		if (!onSubmitIds) {
			onSubmitIds=ids;
			
			this._onSubmitIds=onSubmitIds;
			return;
		}
		
		onSubmitIds.push.apply(onSubmitIds, ids);
	},
	/**
	 * @method hidden final
	 * @param HTMLFormElement form
	 * @return void
	 */
	f_verifyOnSubmit: function(form) {
		var onSubmitIds=this._onSubmitIds;
		if (!onSubmitIds) {
			return;
		}
		this._onSubmitIds=undefined;
		
		f_core.Debug(f_classLoader, "f_verifyOnSubmit: initialize "+onSubmitIds.length+" components.");
		
		this._initializeIds(onSubmitIds);
		
		f_core.Debug(f_classLoader, "f_verifyOnSubmit: "+onSubmitIds.length+" components initialized.");
	},
	/**
	 * @method hidden final
	 * @param Set ids
	 * @return void
	 */
	f_initOnLayoutIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var id in ids) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=id;
			}	

			f_core.Debug(f_classLoader, "f_initOnLayoutIds: ids="+idsLog);
		}

		f_layoutManager.Get().f_addComponentClientIds(ids);
	},
	/**
	 * @method hidden final
	 * @param Set ids
	 * @return void
	 */
	f_initOnFocusIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var id in ids) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=id;
			}	

			f_core.Debug(f_classLoader, "f_initOnFocusIds: ids="+idsLog);
		}

		var onFocusIds=this._onFocusIds;
		if (onFocusIds) {
			for(var i in ids) {
				onFocusIds[i]=true;
			}
			
			return;
		}
		onFocusIds=ids;
		this._onFocusIds=onFocusIds;
		
		var self=this;
		
		var initFct=function(component) {
			var componentId=component.id;
			if (!componentId) { // Ca peut être l'objet document !
				return;
			}
			
			var idx=componentId.lastIndexOf('::');
			var mainId=(idx>0)?componentId.substring(0, idx):componentId;
			
			if (!(mainId in onFocusIds)) {
				return;
			}				
			delete onFocusIds[mainId];
			
			if (componentId!=mainId) {
				component=component.ownerDocument.getElementById(mainId);
			}
			
			if (f_classLoader.IsObjectInitialized(component)) {
				return true;
			}
			
			f_core.Debug(f_classLoader, "f_initOnFocusIds: Lazy onFocus initialization for "+component.id);
			
			try {
				self.f_init(component, false, true);

			} catch (ex) {
				f_core.Error(f_classLoader, "f_initOnFocusIds: Can not initialize component '"+componentId+"'.", ex);
			}	
			
			var onInitComponentListeners=self._onInitComponentListeners;
			if (onInitComponentListeners) {
				self._callOnInitComponentListeners(onInitComponentListeners, component);
			}
			
			var documentCompleteFct = component.f_documentComplete;
			if (documentCompleteFct && self.f_isDocumentCompleted()) {
				documentCompleteFct.call(component);
			}
		
		};
		
		if (f_core.IsInternetExplorer()) {
			document.body.onfocusin=function() {
				if (self._exiting) {
					return;
				}

				initFct(window.event.srcElement);
			};
		} else {
			f_core.AddEventListener(document.body, "focus", function(evt) {
				if (self._exiting) {
					return;
				}

				initFct(evt.target);
			}, document.body);
			
			// Chrome ne déclanche pas d evenement focus sur certain composant
			if (f_core.IsWebkit()) {// click sur bouton
				f_core.AddEventListener(document.body, "mousedown", function(evt) {
					if (self._exiting) {
						return;
					}

					initFct(evt.target);
				}, document.body);
			}
			if (f_core.IsWebkit()) {//accesskey
				f_core.AddEventListener(document.body, "click", function(evt) {
					if (self._exiting) {
						return;
					}

					initFct(evt.target);
				}, document.body);
			}
		}
	},
	/**
	 * @method hidden final
	 * @param Object ids
	 * @return void
	 */
	f_initOnOverIds: function(ids) {
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for(var id in ids) {
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=id;
			}	

			f_core.Debug(f_classLoader, "f_initOnOverIds: ids="+idsLog);
		}

		var onOverIds=this._onOverIds;
		if (onOverIds) {		
			for(var i in ids) {
				onOverIds[i]=true;
			}
			
			return;
		}
				
		onOverIds=ids;
		this._onOverIds=onOverIds;
		
		var self=this;
		
		var initFct=function(component, retargetIE) {
			var componentId=component.id;
			
			var idx=componentId.lastIndexOf('::');
			var mainId=(idx>0)?componentId.substring(0, idx):componentId;
			
			if (!(mainId in onOverIds)) {
				return;
			}				
			delete onOverIds[mainId];
			
			if (componentId!=mainId) {
				component=component.ownerDocument.getElementById(mainId);
			}
			
			if (f_classLoader.IsObjectInitialized(component)) {
				return true;
			}
			
			f_core.Debug(f_classLoader, "f_initOnOverIds: Lazy onMouseOver initialization for "+component.id);
			
			try {
				self.f_init(component, false, true);
				
			} catch (ex) {
				f_core.Error(f_classLoader, "f_initOnOverIds: Can not initialize component '"+componentId+"'.", ex);
			}	
			
			var onInitComponentListeners=self._onInitComponentListeners;
			if (onInitComponentListeners) {
				self._callOnInitComponentListeners(onInitComponentListeners, component);
			}
			
			var documentCompleteFct = component.f_documentComplete;
			if (documentCompleteFct && self.f_isDocumentCompleted()) {
				documentCompleteFct.call(component);
			}			
			
			if (retargetIE) {						
				var newEvt = component.ownerDocument.createEventObject(window.event);
		   		return component.fireEvent("onmouseover", newEvt);
			}
		};
		
		if (f_core.IsInternetExplorer()) {
			document.body.onmouseover=function() {
				if (self._exiting) {
					return;
				}

				initFct(window.event.srcElement, true);
			};
		} else {
			f_core.AddEventListener(document.body, "mouseover", function(evt) {
				if (self._exiting) {
					return;
				}

				initFct(evt.target);
			}, document.body);
		}		
	},
	/**
	 * @method hidden final
	 * @param Object obj Object or String
	 * @param optional Boolean ignoreNotFound
	 * @param optional Boolean callCompleteComponent
	 * @return Object
	 */
	f_init: function(obj, ignoreNotFound, callCompleteComponent) {
		f_core.Assert(obj && (obj.nodeType || typeof(obj)=="string"), "f_classLoader.f_init: Invalid obj parameter ("+obj+")");
		
		if (typeof(obj)=="string") {
			var id=obj;
			
			var doc=this._window.document;
	
			obj=doc.getElementById(id);
			if (false && !obj) { // On recherche plus par le name, trop de risque !
				var names=doc.getElementsByName(id);
				if (!names || !names.length) {
					if (ignoreNotFound) {
						return null;
					}
					throw new Error("Component not found by Id/name '"+id+"'.");
				}
				
				f_core.Assert(names.length==1, "f_classLoader._init: Too many components associated to name '"+id+"'.");
				
				obj=names[0];
				if (f_core.DebugMode) {
					if (obj.id) {
						f_core.Assert(obj, "f_classLoader._init: Component found by name ('"+id+"') has already an ID ('+obj.id+') !");
			
					} else if (f_core.GetAttributeNS(obj,"class")==null) {
						f_core.Assert(obj, "f_classLoader._init: Component found by name ('"+id+"') is not a RCFaces Component !");
					}
				}

				if (obj.id!=id) {
					// On l'a trouvé par le NAME, on essaye de changer l'ID !					
					
					// ATTENTION - Sous IE le changement de la propriété ID est Read-only !
					try {
						obj.id=id;
					} catch (x) {
					}
				}
			}
			
			if (!obj) {
				if (ignoreNotFound) {
					return null;
				}
				throw new Error("Component not found by Id/name '"+id+"'.");
			}
		}
		
		if (obj._kclass) {
			// Deja initialisé !
			return obj;
		}
		
		if (obj.nodeType!=f_core.ELEMENT_NODE) {
			f_core.Debug(f_classLoader, "f_init: Invalid type of object '"+obj.nodeType+"'.");
			return null;
		}
		
		f_core.Debug(f_classLoader, "f_init: Initialize object '"+obj+"' (id='"+obj.id+"') ignore='"+ignoreNotFound+"'  typeof(obj)='"+typeof(obj)+"'");
			
		var claz = f_core.GetAttributeNS(obj,"class");
		if (!claz) {
			// La classe n'est pas définie ... c'est peut etre une form !
	
			f_core.Debug(f_classLoader, "f_init: Class is not defined for component '"+obj.id+"'.");
			return obj;
		}
	
		var look = f_core.GetAttributeNS(obj,"lookid");
	
		var cls=this.f_getClass(claz, look);
		if (!cls) {
			f_core.Assert(cls, "f_classLoader.f_init: Class '"+claz+"' lookId '"+look+"' not found !");
	
			throw new Error("f_classLoader.f_init: Class not found: name='"+claz+"' lookId='"+look+"'.");		
		}
	
		var component =  f_class.Init(obj, cls, f_classLoader._EMPTY_ARGUMENTS);
		
		if (!component) {
			return null;
		}
		
		if (callCompleteComponent) {
			
			var clientDatas=f_core.ParseDataAttribute(component);
			if (clientDatas) {
				component._clientDatas=clientDatas;
			}

			var completeComponent=component.f_completeComponent;
			if (typeof(completeComponent)=="function") {
				try {
					completeComponent.call(component);
		
				} catch (x) {
					f_core.Error(f_classLoader, "f_init: f_completeComponent throws exception for component '"+component.id+"'.", x);
				}
			}
			
			if (this._documentCompleted) {				
				var documentCompleteFct = component.f_documentComplete;
				if (documentCompleteFct) {
					try {
						documentCompleteFct.call(component);
					
					} catch (x) {
						f_core.Error(f_classLoader, "f_init: f_documentComplete throws exception for component '"+component.id+"'.", x);
					}
				}
			}
		}
				
		return component;
	},
	
	/**
	 * @method private
	 */
	_destroy: function(objs) {
		if (this._exiting) {
			return;
		}
		
		// On evite l'empilement des destroys, on les ramene au premier appel !

		var localClean;
		/* var otherDocumentClean; */
	
		var documentCompleteObjects=this._documentCompleteObjects;
		var serializableObjects=this._serializableObjects;
		
		for(var i=0;i<objs.length;i++) {
			var obj=objs[i];
		
			var pool=(obj.tagName)?this._componentPool:this._objectPool;
			f_core.Assert(pool, "f_classLoader._destroy: Invalid Objects pool for object "+obj);
		
			for (var j=0;j<pool.length;j++) {
				if (pool[j]!=obj) {
					continue;
				}
				
				pool.splice(j, 1);
				/*
				if (obj.tagName && obj.ownerDocument!=document) {
					// Cas d'une frame !
					if (!otherDocumentClean) {
						otherDocumentClean=new Array;
					}
					
					otherDocumentClean.push(obj);
				} else {
				*/
					if (!localClean) {
						localClean=new Array;
					}
			
					localClean.push(obj);
				/*
				}
				*/

				if (documentCompleteObjects) {
					var documentCompleteObjectsIndex=obj._documentCompleteObjectsIndex;
					if (documentCompleteObjectsIndex!==undefined) {
						documentCompleteObjects[documentCompleteObjectsIndex]=undefined;
					}
				}
							
				var serializableObjectsIndex=obj._serializableObjectsIndex;
				if (serializableObjectsIndex!==undefined) {
					serializableObjects[serializableObjectsIndex]=undefined;
				}

						
				f_core.Debug(f_classLoader, "_destroy: Object '"+obj+"' "+obj.tagName+"#"+obj.id+"."+obj.className+"' has been removed from the pool !");

				obj=undefined;
				
				break;
			}
	
			if (obj) {
				f_core.Warn(f_classLoader, "_destroy: Object '"+obj+"' "+obj.tagName+"#"+obj.id+"."+obj.className+"' is not found into pool, and can not be destroyed !");
			}
		}	
		
		/*
		if (otherDocumentClean) {
			f_class.Clean(otherDocumentClean);
		}
		*/
		
		if (!localClean) {
			// Il n'y a rien a detruire !
			return;
		}
		
		
		f_class.Clean(localClean);
		
		/*
		var toClean=this._toClean;
		if (toClean) {
			// nous sommes dans un empilement d'appels
			
			// On empile nos nouveaux objets AU DEBUT de la liste
			
			toClean.unshift.apply(toClean, localClean);
			return;
		}
		
		for(;toClean.length;) {
			// On ne peut les retirer que 1 par 1 !
			// Car si c'est un element complexe, il peut en ajouter d'autres !
			
			f_class.Clean(toClean.splice(0, 1));
		}
		
		this._toClean=undefined;
		*/
	},

	/**
	 * @method private
	 * @param HTMLElement[] components
	 * @return void
	 */
	_serializeComponents: function(components) {
		var serializedStates=this._serializedStates;
		
		if (f_core.IsDebugEnabled(f_classLoader)) {
			var idsLog="";
			for (var i=0; i<components.length; i++) {
				var component = components[i];
				if (!component) {
					continue;
				}
				
				if (idsLog) {
					idsLog+=",";
				}
				
				idsLog+=component.id;
			}	

			f_core.Debug(f_classLoader, "_serializeComponents: ids="+idsLog);
		}

		try {
			this._serializing=true;
		
			for (var i=0; i<components.length; i++) {
				var component = components[i];
				if (!component) {
					continue;
				}
				
				var componentId=component.id;				
				if (!componentId) {
					continue;
				}				
				
				var f = component.f_serialize0;
				/* Ca ne doit pas arriver !
				if (!f) {
					continue;
				}
				*/
							
				f_core.Assert(typeof(f)=="function", "f_classLoader._serializeComponents: Field f_serialize0 is not a method for object '"+componentId+"'.");
				
				var ser;
				try {
					ser = f.call(component);
					
				} catch (x) {
					f_core.Error(f_classLoader, "_serializeComponents: Serialization of object '"+componentId+"' throws exception.", x);
					continue;
				}
				
				f_core.Assert(ser!==undefined, "f_classLoader._serializeComponents: Serialization of object '"+componentId+"' returns undefined !");
				
				if (!ser) {
					delete serializedStates[componentId];
					continue;
				}
				
				serializedStates[componentId]=ser;
			}
			
		} finally {
			this._serializing=undefined;
		}
	},
	/**
	 * @method hidden final
	 * @return String
	 */
	f_getSerializedState: function() {
		var serial=new Array;
		
		var serializedStates=this._serializedStates;
	
		for(var id in serializedStates) {
			serial.push(id+"={"+serializedStates[id]+"}");
		}		
		
		if (!serial.length) {
			return "";
		}
		
		return serial.join(",");		
	},
	/**
	 * @method hidden final
	 * @param HTMLFormElement form
	 * @return void
	 */
	f_serialize: function(form) {
		
		var serializableObjects=this._serializableObjects;
		
		f_core.Debug(f_classLoader, "f_serialize: Serialize "+serializableObjects.length+" objects ...");
		
		if (serializableObjects.length) {
			this._serializeComponents(serializableObjects);
		}
		
		var serial=this.f_getSerializedState();
	
		f_core.Debug(f_classLoader, "f_serialize: Serialized form '"+form.id+"' => '"+serial+"'.");
		
		f_core.SetInputHidden(form, f_core.SERIALIZED_DATA, serial);
	},
	
	/**
	 * @method hidden final
	 */
	f_addVisibleComponentListener: function(component) {
		f_core.Assert(component.f_performComponentVisible, "Callback 'f_performComponentVisible' not found !");
	
		if (this._exiting) {
			throw "This classloader is exiting ... [f_addVisibleComponentListener]";
		}
		
		if (f_core.IsComponentVisible(component)) {
			return false;
		}
		
		var visibleListeners=this._visibleListeners;
	
		if (!visibleListeners) {
			visibleListeners=new Array;
			this._visibleListeners=visibleListeners;
		}
		
		visibleListeners.f_addElement(component.id);
		
		return true;
	},
		
	/**
	 * @method hidden
	 * @param HTMLElement componentSource
	 * @return void
	 */
	fireVisibleEvent: function(componentSource) {
		f_core.Debug(f_classLoader, "fireVisibleEvent: Fire visible event for '"+componentSource.id+"'.");
		var components=this._visibleListeners;
		if (!components) {
			return;
		}

		var doc=this.f_getDocument();
	
		for(var i=0;i<components.length;) {
			var componentId=components[i];
			
			var component=doc.getElementById(componentId);
			if (!component) {
				f_core.Debug(f_classLoader, "fireVisibleEvent["+i+"/"+ids.length+"]: Can not find component '"+componentId+"'.");
				
				components.splice(i, 1);
				continue;
			}			
			
			if (!f_core.IsComponentVisible(component)) {
				i++;
				continue;
			}
			
			components.splice(i, 1);
		
			f_core.Debug(f_classLoader, "f_classLoader.fireVisibleEvent: New visible registred component: '"+component.id+"', call callback.");
			
			var fct=component.f_performComponentVisible;
			if (fct===undefined) {
				continue;
			}
			
			f_core.Assert(typeof(fct)=="function", "f_classLoader.fireVisibleEvent: f_performComponentVisible of component '"+component.id+"' is not a function !");
			
			try {
				fct.call(component, componentSource);
				
			} catch (x) {
				f_core.Error(f_classLoader, "fireVisibleEvent: Call of method f_performComponentVisible of component '"+component.id+"' throws exception.", x);
			}
		}
	},
	
	/**
	 * @method hidden
	 * @param optional Boolean serializeState
	 * @param optional HTMLElement... garbagedComponents parent elements to garbage ...
	 * @return String serialized state
	 */
	f_garbageObjects: function(serializeState, garbagedComponents) {
	
		if (this._exiting) {
			throw "This classloader is exiting ... [garbageObjects]";
		}

		var componentPool=this._componentPool;
		f_core.Assert(componentPool, "f_classLoader.f_garbageObjects: Invalid Objects pool !");
		
		var garbageMark=this._garbageMark;
		if (garbageMark===undefined) {
			garbageMark=new Date().getTime();
		}

		var keepGarbageMark=++garbageMark;
		var clearGarbageMark=++garbageMark;
		this._garbageMark=garbageMark;
		
		document._rcfacesGarbageMark=keepGarbageMark;
		for(var i=1;i<arguments.length;i++) {
			arguments[i]._rcfacesGarbageMark=clearGarbageMark;
		}
		
		var documentCompleteObjects=this._documentCompleteObjects;
		var serializableObjects=this._serializableObjects;
		
		var serializableComponentsGarbaged;
		var list=new Array;
		var toClean;
		for (var i=0;i<componentPool.length;) {
			var obj=componentPool[i];
			
			if (f_core.IsDebugEnabled(f_classLoader)) {
				var o=obj;
				for(;o && o.nodeType!=f_core.DOCUMENT_NODE;o=o.parentNode);
				f_core.Assert(o, "f_classLoader.f_garbageObjects: already DOM detached component ! ("+obj.id+")");
			}
			
			var p=obj;
			var gm;
			var parentNode;
			for(;;) {
				gm=p._rcfacesGarbageMark;

				if (gm==keepGarbageMark) {
					// On tombe sur une marque, comme quoi il faut garder !
					break;
					
				} else if (gm==clearGarbageMark) {
					// On tombe sur une marque comme quoi il faut l'effacer
					p=null;
					break;
				}

				// Aucune marque ?!

				list.push(p); // On marque

				parentNode=p.parentNode;
				if (parentNode) {
					p=parentNode;
					
					continue;
				}
				
				parentNode=p._parentNode;
				if (parentNode) {
					p=parentNode;

					continue;
				}
				
				p=null; // On arrive à la fin 
				break;
			}
			
			var setMark=(p)?keepGarbageMark:clearGarbageMark;
			for(;list.length;) {
				list.shift()._rcfacesGarbageMark=setMark;
			}
			
			if (p) {
				// On garde,  au suivant ...
				i++;
				continue;
			}
			
			f_core.Debug(f_classLoader, "f_garbageObjects: Mark object '"+obj+"' to garbage");
			
			if (!toClean) {
				toClean=new Array;
			}
			
			toClean.push(obj);
			
			componentPool.splice(i, 1);
			
			if (documentCompleteObjects) {
				// Ca devrait jamais arriver ... mais bon ...
				var documentCompleteObjectsIndex=obj._documentCompleteObjectsIndex;
				if (documentCompleteObjectsIndex!==undefined) {
					documentCompleteObjects[documentCompleteObjectsIndex]=undefined;
				}
			}
						
			var serializableObjectsIndex=obj._serializableObjectsIndex;
			if (serializableObjectsIndex!==undefined) {
				serializableObjects[serializableObjectsIndex]=undefined;
				if (!serializableComponentsGarbaged) {
					serializableComponentsGarbaged=new Array;
				}
				
				serializableComponentsGarbaged.push(obj);
			}
		}
	
		if (!toClean) {						
			f_core.Debug(f_classLoader, "f_garbageObjects: no object garbaged.");

			if (serializeState) {
				return this.f_getSerializedState();
			}
			return undefined;
		}
		
		var serializedForm=null;
		
		if (serializeState) {
			if (serializableComponentsGarbaged) {
				this._serializeComponents(serializableComponentsGarbaged);
			}

			serializedForm=this.f_getSerializedState();
		}
		
		f_class.Clean(toClean);
		
		f_core.Debug(f_classLoader, "f_garbageObjects: "+toClean.length+" object(s) garbaged ");
		
		return serializedForm;
	},
	
	/**
	 * @method hidden
	 * @return void
	 */
	f_completeGarbageObjects: function() {
		f_core.GarbageListenerReferences();
	},
	
	/**
	 * @method hidden
	 * @param f_bundle bundle
	 * @return void
	 */
	_declareBundle: function(bundle) {
		var name=bundle.f_getName();
		
		f_core.Profile(false, "f_classLoader.loadBundle("+name+")");

		f_core.Assert(!this._bundles[name], "f_classLoader._declareBundle: Bundle '"+name+"' is alreay declared !");

		this._bundles[name]=bundle;

		if (!this._mainBundleName) {
			this._mainBundleName=name;
		}

		f_core.Profile(true, "f_classLoader.loadBundle("+name+")");
	},
	/**
	 * @method private 
	 * @param f_class claz
	 * @return void
	 */
	_initializeStaticMembers: function(claz, onlyInitializer) {
		// Attention: Code pour Classes et Aspects
			
		var staticMembers=claz._staticMembers;
		if (!onlyInitializer && staticMembers) {
		/*
			if (staticMembers instanceof _remapContext) {
				staticMembers=this._classLoader._remapContext(staticMembers);
				claz._staticMembers=staticMembers;
			}
		*/
			for(var memberName in staticMembers) {				
				var member=staticMembers[memberName];
				
				/*			
				f_core.Assert(
					typeof(member)=="number" || 
					typeof(member)=="string" || 
					member===null ||
					member===false ||
					member===true ||
					memberName=="_EVENTS" || // Ok c'est pas joli, mais bon ...
					memberName=="_ACCENTS_MAPPER" ||
					memberName=="_CALLBACKS" ||
					typeof(member)=="function", "Static member '"+memberName+"' is not litteral or function for aspect/class '"+claz._name+"' !");
				*/
						
				claz[memberName]=member;
			}
		}
				
		var staticInitializer=claz.Initializer;
		if (staticInitializer) {
			f_core.Assert(typeof(staticInitializer)=="function", "f_classLoader._initializeStaticMembers: Invalid 'Initializer' field, it must be a function ! value="+staticInitializer);
			try {
				if (f_classLoader._ProfileInitializer) {
					f_core.Profile(false, "f_classLoader._initializeStaticMembers: Call static initializer");			
				}
				
				staticInitializer.call(claz);
				
			} catch (x) {
				f_core.Error(f_classLoader, "_initializeStaticMembers: Initializer of aspect/class '"+claz._name+"' throws exception.", x);

			} finally {
				if (f_classLoader._ProfileInitializer) {
					f_core.Profile(true, "f_classLoader._initializeStaticMembers: End of static initializer");			
				}				
			}
		}
	},
	/**
	 * @method hidden
	 * @return Boolean
	 */
	f_isDocumentCompleted: function() {
		return this._documentCompleted;
	},
	
	/**
	 * @method hidden
	 * @param Function listener
	 * @return void
	 */
	f_addOnInitComponentListener: function(listener) {
		f_core.Assert(typeof(listener)=="function", "f_classLoader.f_addOnInitComponentListener: Invalid listener paramter '"+listener+"'.");

		var onInitComponentListeners=this._onInitComponentListeners;
		if (!onInitComponentListeners) {
			onInitComponentListeners=new Array;
			this._onInitComponentListeners=onInitComponentListeners;
		}
		
		onInitComponentListeners.f_addElement(listener);
	},
	/**
	 * @method hidden
	 * @param Function listener
	 * @return void
	 */
	f_removeOnInitComponentListener: function(listener) {
		f_core.Assert(typeof(listener)=="function", "f_classLoader.f_addOnInitComponentListener: Invalid listener paramter '"+listener+"'.");

		var onInitComponentListeners=this._onInitComponentListeners;
		if (!onInitComponentListeners) {
			return;
		}
		onInitComponentListeners.f_removeElement(listener);
	},
	
	/**
	 * @method hidden
	 * @param f_component component
	 * @return Boolean
	 */
	f_fireInitListener: function(component) {
		
		if (window._rcfacesWindowInitialized) {
			return component.f_fireEvent(f_event.INIT);
		}		

		var onFireInits=this._onFireInits;
		if (!onFireInits) {
			onFireInits=new Array();
			
			this._onFireInits=onFireInits;
		}
		
		onFireInits.push(component.id);		
	},
	
	toString: function() {
		if (!this._window) {
			return "[ClassLoader]";
		}
		return "[ClassLoader '"+this._window.location+"']";
	}
};


/**
 * @field private static final String
 */
f_classLoader._LOOK="~";

/**
 * @field private static final Boolean
 */
f_classLoader._ProfileInitializer=false;

/**
 * @field private static final Array
 */
f_classLoader._EMPTY_ARGUMENTS=[];

/**
 * @method private static
 * @param String claz
 * @param optional String lookId 
 * @return String
 */
f_classLoader._MakeClassName=function(claz, lookId) {
	if (!lookId) {
		return claz;
	} 
	
	return claz+f_class._LOOK+lookId;
};


/**
 * @method public static
 * @param Object... objects
 * @return void
 */
f_classLoader.Destroy=function(objects) {

	var lastClassLoader=undefined;
	var toDestroy=undefined;
	
	for(var i=0;i<arguments.length;i++) {
		var object=arguments[i];
		
		f_core.Assert(typeof(object)=="object", "f_classLoader.Destroy: Invalid object type: "+object);
		
		var klass=object._kclass;
		if (!klass) {
			var finalizer=object.f_finalize;
			
			if (finalizer===undefined) {
				// Object sans finalizer !
				continue;
			}
			
			f_core.Assert(typeof(finalizer)=="function", "f_classLoader.Destroy: finalizer field must be a function. ("+finalizer+")");

			finalizer.call(object);
			continue;
		}
		
		var classLoader=klass._classLoader;
		f_core.Assert(classLoader, "f_classLoader.Destroy: Classloader is not defined for '"+object+"'.");
		
		// On regroupe par classLoader ...
		if (!lastClassLoader || (lastClassLoader && lastClassLoader!=classLoader)) {
			if (lastClassLoader) {
				lastClassLoader._destroy(toDestroy);
			}
			
			lastClassLoader=classLoader;
			toDestroy=new Array;
		}
		
		toDestroy.push(object);
	}
	
	if (lastClassLoader) {
		lastClassLoader._destroy(toDestroy);
	}
};

/**
 * @method private static
 * @param Object parameters
 * @param HTMLElement component
 * @return Object
 */
f_classLoader._SerializeInputs=function(component) {
	f_core.Assert(component && (component.nodeType==f_core.ELEMENT_NODE || component.nodeType==f_core.DOCUMENT_NODE), "f_classLoader.SerializeInputs: Invalid component parameter '"+component+"'.");
	
	var form=f_core.GetParentForm(component);
	var serializedInputs=form._serializedInputs;
	if (!serializedInputs) {
		serializedInputs=new Object;
		form._serializedInputs=serializedInputs;
	}

	var inputs=f_core.GetElementsByTagName(component, "input");
	for(var i=0;i<inputs.length;i++) {
		var input=inputs[i];
		
		if (input._dontSerialize) {
			continue;
		}
		
		var inputName=input.name;
		if (!inputName) {
			continue;
		}

		var value="";
		
		switch(input.type.toLowerCase()) {
		case "checkbox":
		case "radio":
			if (!input.checked) {
				break;
			}
			// On continue ...
		
		case "text":
		case "password":
		case "hidden":
			 value=input.value;
			 break;
		}
		
		serializedInputs[inputName]=value;
	}
	
	var selects=f_core.GetElementsByTagName(component, "select");
	for(var i=0;i<selects.length;i++) {
		var select=selects[i];
		var selectName=select.name;
		if (!selectName) {
			continue;
		}
		
		serializedInputs[selectName]=select.value;
	}
	
	return serializedInputs;
};

/**
 * @method hidden static
 * @param Object parameters
 * @param HTMLElement component
 * @param Boolean updateInputs
 * @return void
 */
f_classLoader.SerializeInputsIntoParam=function(parameters, component, updateInputs) {
	f_core.Assert(parameters && typeof(parameters)=="object", "f_classLoader.SerializeInputsIntoParam: Invalid parameters parameter '"+parameters+"'.");
	f_core.Assert(component===undefined || (component.nodeType==f_core.ELEMENT_NODE || component.nodeType==f_core.DOCUMENT_NODE), "f_classLoader.SerializeInputsIntoParam: Invalid component parameter '"+component+"'.");
	
	var serializedInputs;
	if (updateInputs) {
		serializedInputs=f_classLoader._SerializeInputs(component);

	} else {
		var form=f_core.GetParentForm(component);
		serializedInputs=form._serializedInputs;
	}
	
	if (!serializedInputs) {
		return;
	}
	
	for(var name in serializedInputs) {
		parameters[name]=serializedInputs[name];
	}	
};

/**
 * @method hidden static
 * @param Object parameters
 * @param HTMLElement component
 * @return void
 */
f_classLoader.SerializeInputsIntoForm=function(form) {
	f_core.Assert(form && form.nodeType==f_core.ELEMENT_NODE && form.tagName.toLowerCase()=="form", "f_classLoader.SerializeInputsIntoParam: Invalid form parameter '"+form+"'.");
	
	var serializedInputs=form._serializedInputs;
	if (!serializedInputs) {
		// Aucune sérialisation donc on laisse tomber la recherche de doublons d'INPUT		
		return;
	}
	
	for(var name in serializedInputs) {
		var value=serializedInputs[name];
		var elements=document.getElementsByName(name);
		
		f_core.Debug(f_classLoader, "SerializeInputsIntoForm: elements.length="+elements.length+" for name '"+name+"'.");
		
		switch(elements.length) {
		case 0:
			// Aucun élément
			var input = form.ownerDocument.createElement("input");
			input.type = "hidden";
			input.value = value;
			input.name = name;
			input._serializedInput=true;
		
			form.appendChild(input);

			f_core.Debug(f_classLoader, "SerializeInputsIntoForm: add input name='"+name+"' value='"+value+"'");
			break;
		
		case 1:
			// Un seul élément, c'est un faux ?
			var input=elements[0];
			if (input._serializedInput) {
				// C'est un faux !
				// On change juste la valeur
				
				input.value=value;
				f_core.Debug(f_classLoader, "SerializeInputsIntoForm: change input value for name='"+name+"', new value='"+value+"'");
				break;
			}
			// C'est pas un faux !
			// Bizarre comme config, on laisse tomber ...
			break;
		
		default:
			// Plusieurs champs avec le même nom
			
			// Y a t-il un vrai input ?
			var realValueComponent=null;
			for(var i=0;i<elements.length;i++) {
				var input=elements[i];
				if (input._serializedInput) {
					continue;
				}
				
				realValueComponent=input;
				break;
			}
			
			if (!realValueComponent) {
				// Que des faux, on garde le premier faux
				// et on efface le reste ...
				
				realValueComponent=elements[0];
				realValueComponent.value=value;
			}
				
			// On effaces tous les faux sauf le realValueComponent
			for(var i=0;i<elements.length;i++) {
				var input=elements[i];
				if (!input._serializedInput || input==realValueComponent) {
					continue;
				}

				input.parentNode.removeChild(input);
			}
			
			break;
		}
	}		
};

/**
 * @field hidden static final Number
 */
f_classLoader.UNKNOWN_STATE = 0;

/**
 * @field hidden static final Number
 */
f_classLoader.LAZY_STATE = 1;

/**
 * @field hidden static final Number
 */
f_classLoader.INITIALIZED_STATE = 2;

/**
 * @method hidden static final
 * @param Object object
 * @return Number  (0=Unknown 1=Not initialized  2=Initialized)
 */
f_classLoader.GetObjectState=function(object) {
	if (object._kclass) {
		return f_classLoader.INITIALIZED_STATE;
	}
	
	if (object.nodeType==f_core.ELEMENT_NODE) {
		var claz = f_core.GetAttribute(object, "v:class");
		if (claz) {
			return f_classLoader.LAZY_STATE;
		}
	}

	return f_classLoader.UNKNOWN_STATE;
};

/**
 * @method hidden static final 
 * @param Object object
 * @return Boolean
 */
f_classLoader.IsObjectInitialized=function(object) {
	f_core.Assert(object && typeof(object)=="object", "f_class.IsObjectInitialized: Object is invalid ("+object+")");

	return !!object._kclass;
};
/**
 * @method public static
 * @param Window win
 * @return f_classLoader
 * @context window:win
 */
f_classLoader.Get=function(win) {
	return win._rcfacesClassLoader;
};

/**
 * @method public static
 * @return String
 */
f_classLoader.f_getName=function() {
	return "f_classLoader";
};

f_classLoader._kernelClass=true;

window._rcfacesClassLoader=new f_classLoader(window);
