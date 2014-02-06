/*
 * $Id: f_asyncClassLoader.js,v 1.2 2013/01/11 15:45:05 jbmeslin Exp $
 */
 
/**
 * f_classLoader  Additionnal interactive methods.
 *
 * @class public f_classLoader
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:05 $
 */

/**
 * @method hidden
 * @param HTMLElement component
 * @param HTMLElement htmlNode
 * @param String content
 * @param Boolean processScripts
 * @return void
 */
f_classLoader.prototype.f_loadContent = function(component, htmlNode, content, processScripts) {

	f_core.Assert(component && component._kclass, "f_asyncClassLoader.f_loadContent: component parameter is invalid : "+component);
	f_core.Assert(htmlNode && htmlNode.nodeType==f_core.ELEMENT_NODE, "f_asyncClassLoader.f_loadContent: htmlNode parameter is invalid : "+htmlNode);
	f_core.Assert(typeof(content)=="string", "f_asyncClassLoader.f_loadContent: component parameter is invalid : "+content);
	f_core.Assert(processScripts===undefined || typeof(processScripts)=="boolean", "f_asyncClassLoader.f_loadContent: processScripts parameter is invalid : "+processScripts);

	var scripts=new Array;

	{
		var buffer=content;
		content="";
		
		var LF="-¤CaMelIa¤-";
		var lfRegExp=new RegExp(LF, "g");
		buffer=buffer.replace(/[\r]?\n/g, LF);
		
		var scriptRegExp=new RegExp("^(.*)?<script([^>]*)?>(.*)?</script([^>]*)?>(.*)?", "i");
		
		for(;;) {			
			var result=scriptRegExp.exec(buffer);
			if (!result) {
				content=buffer.replace(lfRegExp, '\n')+content;
				break;
			}
			
			buffer=result[1];
			
			if (result[5]) {
				content=result[5].replace(lfRegExp, '\n')+content; // Nouveau content !
			} 
			
			if (!result[3]) {
				scripts.unshift(result[2], "");
				continue;
			}

			var text=result[3].replace(lfRegExp, '\n');
		
			scripts.unshift(result[2], text);
		}
	}
		
	f_core.Debug("f_asyncClassLoader", "f_loadContent: Set content on component id='"+component.id+"' htmlNode='"+htmlNode+"', htmlNode.tag='"+htmlNode.tagName+"' :\n"+content);
	
	this.f_processViewStates(htmlNode); // Pour nettoyer avant !
	
	try {
		htmlNode.innerHTML=content;

	} catch (x) {
		f_core.Debug("f_asyncClassLoader", "f_loadContent: Exception when setting innerHTML for component id='"+component.id+"' htmlNode='"+htmlNode.tagName+"':\n"+content, x);
	}
	
	{
		var self=this;
		
		// le innerHTML peut être asynchrone !
		
		window.setTimeout(function() {
			if (window._rcfacesExiting) {
 				return false;
 			}

			try {
				var asyncClassLoader=self;
				self=null;
				
				asyncClassLoader.f_processViewStates(htmlNode);
			
				if (processScripts!==false) {
					asyncClassLoader.f_processScripts(component, htmlNode, scripts);
				}
			} catch (x) {
				f_core.Error("f_asyncClassLoader", "f_loadContent.timeout: process script exceptions", x);
			}
		}, 10);
	}
};

/**
 * @method hidden
 * @param HTMLElement htmlNode
 * @return void
 */
f_classLoader.prototype.f_processViewStates = function(htmlNode) {
	
	var toRemove = null;
	var newViewState = null;
	
	var inputs = htmlNode.ownerDocument.getElementsByName("javax.faces.ViewState");
	for(var i=0;i<inputs.length;i++) {
		var input=inputs[i];
		if (input._viewState) {
			continue; // Déjà connu
		}
		
		input._viewState=true;
	
		var form=f_core.GetParentForm(input);

		if (!form._viewState) {
			// C'est le premier INPUT !
			form._viewState = true;
			
			continue;
		}
		
		var main=null;
		
		// pas connu, il existe déjà un autre input, on le cherche :
		for(var j=0;j<inputs.length;j++) {
			if (i==j) {
				continue;
			}
			
			var input2 = inputs[j];
			if (!input2._viewState) {
				continue;
			}
			
			var form2=f_core.GetParentForm(input2);
			if (form!=form2) {
				continue;
			}
			
			// C'est le notre !
			main=input2;
			break;
		}
		
		if (!main) {
			// On a un probleme d'algo !
			continue;
		}
		
		newViewState = input.value;
		
		if (!toRemove) {
			toRemove = new Array;
		}
		toRemove.push(input);
	}
	
	if (toRemove) {
		for(var i=0;i<toRemove.length;i++) {
			var node=toRemove[i];
			
			node.parentNode.removeChild(node);
		}
		
		for(var j=0;j<inputs.length;j++) {
			inputs[j].value=newViewState;
		}
	}
};

/**
 * @method hidden
 * @param HTMLElement component
 * @param HTMLElement htmlNode
 * @return void
 */
f_classLoader.prototype.f_loadAndProcessScripts= function(component, htmlNode) {
	var scriptNodes=f_core.GetElementsByTagName(htmlNode, "script");
	
	this.f_processScripts(component, htmlNode, null, scriptNodes);
};

/**
 * @method hidden * @param HTMLElement component
 * @param HTMLElement htmlNode
 * @param optional String[] scripts
 * @param optional HTMLScriptElement[] scripts
 * @return void
 */
f_classLoader.prototype.f_processScripts = function(component, htmlNode, scripts, scriptNodes) {
	f_core.Assert(component && component._kclass, "f_asyncClassLoader.f_processScripts: Invalid component parameter '"+component+"'.");
	f_core.Assert(htmlNode && htmlNode.nodeType==f_core.ELEMENT_NODE, "f_asyncClassLoader.f_processScripts: Invalid htmlNode parameter '"+htmlNode+"'.");
	
	
	if (!this._interactiveMode) {
		this._interactiveMode=true;

		f_core.Debug("f_asyncClassLoader", "f_processScripts: Initialize interactive kernel");							
		
		this._requestedBundles=new Object;
		this._bundlesToLoad=new Array;
		this._loadingBundle=0;
	}

	var self=this;
	var interactiveMode={
		_component: component,
		_htmlNode: htmlNode,
		_componentPoolLevel: this._componentPool.length,

		_commands: new Array,		
		
		run: function() {
		 	// var win=f_core.GetWindow(this._component); // Non utilisé
			try {
			 	var commands=this._commands;
			 	
				for(;commands.length;) {
					
					f_core.Debug("f_asyncClassLoader", "f_processScripts: commands='"+commands+"'");							
						
					switch(commands[0]) {
					case "bundle":
						var bundleName=commands[1];
					
						if (!self._bundles[bundleName]) {
							// Il n'est pas connu

							f_core.Debug("f_asyncClassLoader", "f_processScripts: Bundle '"+bundleName+"' is not define, wait for it");							
							
							// On attend d'etre appelé !
							return;
						}
						
						// Ok il est connu !  On passe à la suite ...
						
						f_core.Debug("f_asyncClassLoader", "f_processScripts: Bundle '"+bundleName+"' is loaded, next command ...");							
						
						commands.shift();
						commands.shift();
						break;

					case "scriptURL":
						commands.shift();
						var script=commands.shift();
						
	//					alert("Eval script: "+script);
						
						var scriptElement=document.createElement("script");
						scriptElement.src=script.src;
						
						if (script.type) {
							scriptElement.type=script.type;
							
						} else {
							scriptElement.type=f_httpRequest.JAVASCRIPT_MIME_TYPE;
						}
						if (script.charset) {
							scriptElement.charset=script.charset;
						}							
						scriptElement.defer=false;
		
						f_core.Debug("f_asyncClassLoader", "f_processScripts: Script URL url='"+scriptElement.src+"' type='"+scriptElement.type+"' charset="+scriptElement.charset+"' defer=false");		
						
						f_core.AppendChild(this._htmlNode, scriptElement);						
						break;

					case "scriptText":
						commands.shift();
						var script=commands.shift();
						
	//					alert("Eval script: "+script);
						
						script=f_core.Trim(script);
						
						// Pour eviter un anti-virus plutot indelicat !
						if (!script.indexOf("<!--")) {
							var v=script.indexOf("//-->");
							if (v>0) {
								var newScript=script.substring(5, v);
								newScript=f_core.Trim(newScript);

								f_core.Debug("f_asyncClassLoader", "f_processScripts: Simplify script '"+script+"' newScript='"+newScript+"'.");
							
								script=newScript;
							}
						}
											
						f_core.Debug("f_asyncClassLoader", "f_processScripts: Eval script '"+script+"'.");
						
						if (!script.length) {
							break;
						}
						
						self._currentInteractiveMode=this;

						try {
							f_core.WindowScopeEval(script);
							
						} catch (x) {
							f_core.Error("f_asyncClassLoader", "f_processScripts: Eval throws exception; script='"+script+"'.", x);

						} finally {
							self._currentInteractiveMode=undefined;				
						}

						break;

					default:
						var command=commands.shift();
						var param=commands.shift();
						
						f_core.Error("f_asyncClassLoader", "Invalid command='"+command+"' param='"+param+"'");
					}
				}
			
				f_core.Debug("f_asyncClassLoader", "f_processScripts: End of scripts evaluation.");
								
//				alert("Fin !");
				self._asyncPostLoad(this);

			} catch (x) {
				f_core.Error("f_asyncClassLoader", "f_processScripts: Async script loading exception.", x);
			}
		}
	};

	var forms=f_core.GetElementsByTagName(htmlNode, "form");	
	if (forms.length) {
		f_core.Debug("f_asyncClassLoader", "f_processScripts: "+forms.length+" form(s) detected !");
		
		for(var i=0;i<forms.length;i++) {
			var f=forms[i];
			
			f_core.InitializeForm(f);
		}
	}

	var commands=interactiveMode._commands;

	if (scripts && scripts.length) {
		var srcRegExp=new RegExp("(.*)?src=\"([^\"]*)\"(.*)?", "i");
		var typeRegExp=new RegExp("(.*)?type=\"([^\"]*)\"(.*)?", "i");
		var charsetRegExp=new RegExp("(.*)?charset=\"([^\"]*)\"(.*)?", "i");
	//	var textRegExp=new RegExp("((//<![CDATA[)?([^<]*)(//]])?", "i");
		
		for(var i=0;i<scripts.length;) {
			var scriptParams=scripts[i++];
			var scriptText=scripts[i++];
			
			f_core.Debug("f_asyncClassLoader", "f_processScripts: script params='"+scriptParams+"' text='"+scriptText+"'");
			
			var src=null;
			var type=null;
			
			if (scriptParams && scriptParams.length>5) {
				var ret=srcRegExp.exec(scriptParams);
				src=(ret)?ret[2]:null;
				
				ret=typeRegExp.exec(scriptParams);
				type=(ret)?ret[2]:null;
			}				
				
			if (typeof(type)=="string" && type.length && type.toLowerCase().indexOf("text/javascript")<0) {
				f_core.Error("f_asyncClassLoader", "f_processScripts: Unknown script type: "+type);
				continue;
			}
			
			if (src) {
				var ret=charsetRegExp.exec(scriptParams);
				var charset=(ret)?ret[2]:null;
							
				var js= { src: src, 
						type: type, 
						charset: charset };
	
				f_core.Debug("f_asyncClassLoader", "f_processScripts: Add element script: src="+src+" type="+type+" charset="+charset);
	
				commands.push("scriptURL", js);
				continue;
			}
	
			f_core.Debug("f_asyncClassLoader", "f_processScripts: Add text script: "+scriptText);
			
			commands.push("scriptText", scriptText);
		}
	}
	
	if (scriptNodes) {
		for(var i=0;i<scriptNodes.length;i++) {
			var scriptNode=scriptNodes[i];
			
			f_core.Debug("f_asyncClassLoader", "f_processScripts: scriptNode params='"+scriptParams+"' text='"+scriptText+"'");
			
			var type=scriptNode.type;
			
			if (typeof(type)=="string" && type.length && type.toLowerCase().indexOf("text/javascript")<0) {
				f_core.Error("f_asyncClassLoader", "f_processScripts: Unknown script type: "+type);
				continue;
			}

			var src=scriptNode.src;
			
			if (src) {
				var js= { src: src, 
						type: type, 
						charset: script.charset };
	
				f_core.Debug("f_asyncClassLoader", "f_processScripts: Add element script: src="+src+" type="+type+" charset="+script.charset);
	
				commands.push("scriptURL", js);
				continue;
			}
			
			var scriptText=scriptNode.text;
	
			f_core.Debug("f_asyncClassLoader", "f_processScripts: Add text script: "+scriptText);
			
			commands.push("scriptText", scriptText);
		}
		
	}
			
	f_core.Debug("f_asyncClassLoader", "f_processScripts: Run an interactive process");
	
	interactiveMode.run();
};

/**
 * @method private
 * @param Boolean interactiveMode
 * @return void
 */
f_classLoader.prototype._asyncPostLoad=function(interactiveMode) {
	
	if (this._documentCompleted) {
		var pool=this._componentPool;

		f_core.Info("f_asyncClassLoader", "_asyncPostLoad: Call of method f_documentComplete for "+(pool.length-interactiveMode._componentPoolLevel)+" objects.");

		for (var nb=interactiveMode._componentPoolLevel; nb<pool.length; nb++) {
			var obj = pool[nb];
			
			var documentComplete=obj.f_documentComplete;
			if (!documentComplete) {
				continue;
			}
	
			f_core.Assert(typeof(documentComplete)=="function", "f_asyncClassLoader._asyncPostLoad: Type of f_documentComplete method of class '"+obj._kclass._name+"' is not a function  ! ("+documentComplete+")");
			
			try {
				documentComplete.call(obj);
				
			} catch (x) {
				f_core.Error("f_asyncClassLoader", "_asyncPostLoad: f_documentComplete throws exception for component '"+obj.id+"'.", x);
			}
		}
	}

	f_core.Debug("f_asyncClassLoader", "_asyncPostLoad: Call load event for interactiveMode component: "+interactiveMode._component);
	
	// Appel du onload des composants parents !
	interactiveMode._component.f_fireEvent("load");
};

/**
 * @method private
 * @param String bundleName
 * @return Boolean
 */
f_classLoader.prototype._asyncLoadBundle=function(bundleName) {		
	
	if (window._rcfacesExiting) {
		return false;
	}

	f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: Add require bundle '"+bundleName+"'.");

	var requestedBundles=this._requestedBundles;
	
	var interactives=requestedBundles[bundleName];
	if (this._bundles[bundleName] || interactives===false) {
		// Already loaded
		f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: Bundle '"+bundleName+"' already loaded.");
		return false;
	}
	
	var currentInteractiveMode=this._currentInteractiveMode;
	
	if (currentInteractiveMode) {
		f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: Add a new command to the current interactiveMode.");

		currentInteractiveMode._commands.unshift("bundle", bundleName);
	
		if (interactives) {
			f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: Bundle '"+bundleName+"' already required, add to the interactive pool");
	
			interactives.push(currentInteractiveMode);
			return true;
		}
		
		interactives=[currentInteractiveMode];
		requestedBundles[bundleName]=interactives;
	}
		
	if (!this._loadingBundle) {
		// C'est le premier bundle à charger ...  il faut amorcer la pompe ! 

		f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: No bundle waiting, call the systemLoad.");

		this._asyncSystemLoadBundle(bundleName);
		
		return true;		
	}

	// On la met dans la liste d'attente alors ...	
	this._bundlesToLoad.push(bundleName);

	f_core.Debug("f_asyncClassLoader", "_asyncLoadBundle: Bundles are already waiting, pool this bundle '"+bundleName+"' => "+this._bundlesToLoad);	
	
	return true;
};

/**
 * @method private
 * @param String bundleName
 * @return void
 */
f_classLoader.prototype._asyncBundleLoaded=function(bundleName) {
	
	if (window._rcfacesExiting) {
		return;
	}
	
	this._loadingBundle--;

	f_core.Debug("f_asyncClassLoader", "_asyncBundleLoaded: Bundle '"+bundleName+"' loaded  (loadingCount="+this._loadingBundle+")");	

	var requestedBundles=this._requestedBundles; 
	
	var interactives=requestedBundles[bundleName];
	requestedBundles[bundleName]=false;
	
	// D'autres bundles dans la file ?
	var bundlesToLoad=this._bundlesToLoad;	
	for (bundleName=null;bundlesToLoad.length;) {	
		bundleName=bundlesToLoad.shift();
		
		if (requestedBundles[bundleName]!==false) {
			break;
		}
		
		bundleName=null;
	}	
	
	if (bundleName) {
		// Un autre Bundle a charger ... 
		f_core.Debug("f_asyncClassLoader", "_asyncBundleLoaded: Load next bundle '"+bundleName+"'");	
		
		this._asyncSystemLoadBundle(bundleName);
	}

	if (interactives) {
		// On execute quand même les scripts interactifs.

		f_core.Debug("f_asyncClassLoader", "_asyncBundleLoaded: Execute '"+interactives.length+"' scripts");	
		for(;interactives.length;) {
			var interactive=interactives.shift();
			try {
				interactive.run();
				
			} catch (x) {
				f_core.Error("f_asyncClassLoader", "_asyncBundleLoaded: Interactive script throws exception.", x);
			}
		}
	}
};

/**
 * @method private
 * @param String bundleName
 * @return void
 */
f_classLoader.prototype._asyncSystemLoadBundle=function(bundleName) {
	// On lance le chargement du bundle ...	
	
	if (window._rcfacesExiting) {
		return;
	}

	var url=f_env.ComputeJavaScriptURI(bundleName);
	
	this._loadingBundle++;

	f_core.Debug("f_asyncClassLoader", "_asyncBundleLoaded: Declare the loading of script '"+bundleName+"' at url='"+url+"'. (loadingCount="+this._loadingBundle+")");

	var script=document.createElement("script");
	script.type=f_httpRequest.JAVASCRIPT_MIME_TYPE;
	script.src=url;
	script.defer=false;
	script.charset="UTF-8";
	script._bundleName=bundleName;
	
	if (f_core.IsInternetExplorer()) {

		var self=this;
		script.onreadystatechange=function() {
		
			var asyncClassLoader=self;
		
			switch(script.readyState) {
			case "complete":
				script.onreadystatechange=null;
				self=null;

			case "loaded":
			case "interactive":
				break;

			default:
//								alert("State "+script.readyState); 
				f_core.Debug("f_asyncClassLoader", "_asyncSystemLoadBundle: Script '"+url+"' state="+script.readyState);
				return;
			}
			
			asyncClassLoader._asyncBundleLoaded(script._bundleName);
		};

	} else { // Firefox et autres ...
		var self=this;
		script.onload=function() {
			script.onload=null; 
			self._asyncBundleLoaded(script._bundleName);
		};
	}
	
	f_core.AppendChild(document.body, script);
};


/**
 * @method hidden static
 * @param HTMLElement htmlNode
 * @param String newViewState
 * @return void
 */
f_classLoader.ChangeJsfViewId = function(htmlNode, newViewState) {
	var inputs = htmlNode.ownerDocument.getElementsByName("javax.faces.ViewState");

	for(var i=0;i<inputs.length;i++) {
		var input=inputs[i];
	
		input.value=newViewState;
	}
};
