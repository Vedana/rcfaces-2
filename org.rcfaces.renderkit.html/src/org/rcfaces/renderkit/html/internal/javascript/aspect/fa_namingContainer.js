/*
 * $Id: fa_namingContainer.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect NamingContainer
 *
 * @aspect fa_namingContainer
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __statics = {
	
	/** 
	 * @field private static String
	 */
	_PageSeparator:		":",

	/** 
	 * @field private static final String
	 */
	_NAMING_CONTAINER_ATTRIBUTE:		f_core._VNS+":nc",
	
	/** 
	 * @field private static final String
	 */
	_NAMING_CONTAINER_COMPONENT:		f_core._VNS+":namingContainer",
	
	/** 
	 * @field private static final String
	 */
	_SEPARATOR_CHAR:		":",

	/** 
	 * @field private static final String
	 */
	_SEPARATOR_CHAR_REGEXP:	new RegExp(":", "g"),
	
	/**
	 * Find a component. <b>(The naming separator is ':')</b>
	 *
	 * @method public static final 
	 * @param HTMLElement component
	 * @param String id
	 * @param hidden optional Boolean sibling
	 * @param hidden optional Boolean doNotInitialize
	 * @return HTMLElement
	 */
	FindComponent: function(component, id, sibling, doNotInitialize) {
		f_core.Assert(component && component.tagName, "fa_namingContainer.FindComponent: Bad component parameter ! ("+component+")");
		f_core.Assert(typeof(id)=="string", "Bad id parameter !");

		var pageId=id;
		var separator=fa_namingContainer._SEPARATOR_CHAR;
		if (id.charAt(0)!=separator) {
			var pageSeparator=fa_namingContainer._PageSeparator;
			if (separator!=pageSeparator) {
				pageId=id.replace(fa_namingContainer._SEPARATOR_CHAR_REGEXP, pageSeparator);
			}
	
			if (component.id==pageId) {
				return component;
			}
		}
				
		var cid=fa_namingContainer.ComputeComponentId(component, id, sibling);
		f_core.Debug(fa_namingContainer, "Compute component from "+component.id+" id='"+id+"' (pageId='"+pageId+"') returns '"+cid+"'.");

		if (doNotInitialize) {
			return component.ownerDocument.getElementById(cid); 
		}

		return f_core.GetElementByClientId(cid, component.ownerDocument);
	},
	/**
	 * @method hidden static final 
	 * @param HTMLElement component
	 * @param String[] args Component ids <b>(The naming separator is ':')</b>
	 * @return HTMLElement
	 */
	FindComponents: function(component, args) {
		f_core.Assert(component && component.tagName, "fa_namingContainer.FindComponents: Bad component parameter ! ("+component+")");

		for(var i=0;component && i<args.length;i++) {
			var id=args[i];
			f_core.Assert(typeof(id)=="string", "fa_namingContainer.FindComponents: Bad id parameter (parameter #"+(i+1)+") !");
			
			component=fa_namingContainer.FindComponent(component, id);
		}
		
		return component;
	},
	/**
	 * @method hidden static final 
	 * @param HTMLElement component
	 * @param String[] args component ids <b>(The naming separator is ':')</b>
	 * @return HTMLElement
	 */
	FindSiblingComponents: function(component, args) {
		f_core.Assert(component && component.tagName, "fa_namingContainer.FindSiblingComponents: Bad 'component' parameter ! ("+component+")");
		f_core.Assert(typeof(args)=="string" || (args.length>0), "fa_namingContainer.FindSiblingComponents: Bad 'args' parameter ! ("+args+")")
		
		if (typeof(args)=="string") {
			args=[args];
		}
		
		for(var i=0;component && i<args.length;i++) {
			var id=args[i];
			f_core.Assert(typeof(id)=="string", "fa_namingContainer.FindSiblingComponents: Bad id parameter (parameter #"+(i+1)+") !");
			
			component=fa_namingContainer.FindComponent(component, id, !i);
		}
		
		return component;
	},
	/**
	 * @method hidden static 
	 * @param HTMLElement component
	 * @param String id <b>(The naming separator is ':')</b>
	 * @param Boolean sibling
	 * @return String Identifier (The naming separator might not be ':')
	 */
	ComputeComponentId: function(component, id, sibling) {
		f_core.Assert(component && component.tagName, "fa_namingContainer.ComputeComponentId: Invalid component parameter ('"+component+"')");
		f_core.Assert(typeof(id)=="string" && id.length, "fa_namingContainer.ComputeComponentId: Invalid id parameter ('"+id+"')");

		if (fa_namingContainer._flatIdentifierMode) {
			return id;
		}
		
		var separator=fa_namingContainer._SEPARATOR_CHAR;
		var pageSeparator=fa_namingContainer._PageSeparator;
		
        if (id.charAt(0)==separator) {
        	// Ca commence par un ':'  l'ID est donc en absolue
        	// On y va directe !
        	
  	        var pageId=id.substring(1);
	      	if (separator!=pageSeparator) {
        		pageId=pageId.replace(fa_namingContainer._SEPARATOR_CHAR_REGEXP, pageSeparator);
        	}

			return pageId;	        
		}

       	var pageId=id;
      	if (separator!=pageSeparator) {
    		pageId=id.replace(fa_namingContainer._SEPARATOR_CHAR_REGEXP, pageSeparator);
    	}
		
		f_core.Debug(fa_namingContainer, "ComputeComponentId id='"+id+"' pageId='"+pageId+"' componentId='"+component.id+"' pageSeparator='"+pageSeparator+"'.");

    	// Le chemin est en relatif 
    	// On remplace le dernier segment du composant, par l'ID recherché !
    	
       	var cid=component.id; // L'ID contient le séparateur !
 
       	if (sibling || !fa_namingContainer._IsNamingContainer(component)) {
	       	var idx=cid.lastIndexOf(pageSeparator);
	       	
	       	if (idx<0) {
	       		// Pas de container ... !
	       		// On recherche donc à la racine !
	       		
			 	f_core.Debug(fa_namingContainer, "ComputeComponentId: No container returns '"+cid+"'.");
          		return pageId;
	       	}
	       	if (pageSeparator.length>1) {
	       		for(;idx;idx--) {
	       			if (cid.substring(idx-1, idx+1)==pageSeparator) {
	       				continue;
	       			}
	       			
	       			break;
	       		}
		   	}
	       	
	       	// On prend le container précédent !
       		cid=cid.substring(0, idx);
 
 		 	f_core.Debug(fa_namingContainer, "ComputeComponentId: Not a naming container cut cid="+cid);
    
    	} else {
 		 	f_core.Debug(fa_namingContainer, "ComputeComponentId: Component '"+cid+"' is a naming container");
    	}
    	
    	cid+=pageSeparator+pageId;
    	
    	f_core.Debug(fa_namingContainer, "ComputeComponentId: returns '"+cid+"'.");
    	
    	return cid;
	},
	/**
	 * @method private static final
	 */
	_IsNamingContainer: function(component) {
		f_core.Assert(component.tagName, "Component is invalid ! ("+component+").");

		var tagName=component.tagName;
		if (!tagName) {
			return false;
		}
		if (tagName.toLowerCase()=="form" || tagName==fa_namingContainer._NAMING_CONTAINER_COMPONENT) {
			return true;
		}
		
		if (component._namingContainer) {
			return true;
		}
		
		return f_core.GetAttribute(component, fa_namingContainer._NAMING_CONTAINER_ATTRIBUTE)!=null;
	},
	
	/**
	 * @method hidden static final
	 */
	AddNamingContainerAttribute: function(component) {
		component.setAttribute(fa_namingContainer._NAMING_CONTAINER_ATTRIBUTE, "true");
	},
	
	/**
	 * @method hidden static final
	 * @param String separator
	 */
	SetSeparator: function(separator) {
		if (separator===false) {
			fa_namingContainer._flatIdentifierMode=true;
			return;
		}
		
		fa_namingContainer._PageSeparator=separator;
	},
	/**
	 * @method hidden static final
	 * @return String
	 */
	GetSeparator: function() {
		return fa_namingContainer._PageSeparator;
	},
	
	/**
	 * Search into each forms of the document, a component by its identifier. <br>
	 * It does not initialize the found component. (naming container separator must be ':' )
	 *
	 * @method hidden static final
	 * @param Document doc The document.
	 * @param String id Identifier of the searched component.
	 * @return HTMLElement
	 */
	SearchElementById: function(doc, id) {
		if (fa_namingContainer._flatIdentifierMode) {
			return null;
		}

		var separator=fa_namingContainer._SEPARATOR_CHAR;
		
		// On ne traite pas les id avec séparateurs
		if (id.indexOf(separator)>=0) {
			return null;
		}
	
		var pageSeparator=fa_namingContainer._PageSeparator;

		var pageId=id;
/*	
     	if (separator!=pageSeparator) {
    		pageId=id.replace(fa_namingContainer._SEPARATOR_CHAR_REGEXP, pageSeparator);
    	}
*/	
		
		// Nous sommes dans la recherche d'un ID sans séparateur !
		// C'est peut etre un composant dans une form !
		// On passe les forms en revu !
		
		var forms = doc.forms;
		for (var i=0;i<forms.length; i++) {
			var fid=forms[i].id+pageSeparator+pageId;
			
			var obj=doc.getElementById(fid);
			if (!obj) {
				continue;
			}
			
			f_core.Debug(fa_namingContainer, "SearchElementById of direct id='"+id+"' (pageId='"+pageId+"') (without scope).");
			return obj;
		}
		
		return null;
	}
};

new f_aspect("fa_namingContainer", {
	statics: __statics
});	
