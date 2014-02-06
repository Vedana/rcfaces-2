/*
 * $Id: fa_groupName.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect GroupName
 *
 * @aspect public abstract fa_groupName
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __statics = {

	/*
	 * @method private static 
	 * @param f_component component
	 * @param String groupName
	 * @return optional Boolean create
	 * @return f_component[]	 
	ListGroupFromComponent: function(component, groupName, create) {
		return null;
	},
	*/
	
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		fa_groupName._Groups=undefined;
	}
};

var __members = {
	f_finalize: function() {
		if (typeof(this.fa_getRadioScope)=="function") {
			var scope=this.fa_getRadioScope();
			if (scope) {
				scope._Groups=undefined; // Map<string,List<any>>
			}
		}
	},
	/**
	 * @method protected
	 * @param String groupName
	 * @param String obj clientId of component, or value of an item
	 * @return any
	 */
	f_deleteFromGroup: function(groupName, obj) {
		var g = this.f_listGroup(groupName);
		if (!g) {
			return null;
		}
		return g.f_removeElement(obj);
	},
	/**
	 * @method protected
	 * @param String groupName
	 * @param String obj clientId of component, or value of an item
	 * @return void
	 */
	f_addToGroup: function(groupName, obj) {
		var g = this.f_listGroup(groupName, true);
		g.f_addElement(obj);
	},
	/**
	 * @method protected
	 * @param String groupName
	 * @param String newGroupName
	 * @param String obj clientId of component, or value of an item
	 * @return void
	 */
	f_changeGroup: function(groupName, newGroupName, obj) {
		if (groupName) {
			this.f_deleteFromGroup(groupName, obj);
		}
		if (newGroupName) {
			this.f_addToGroup(newGroupName, obj);
		}
	},
	/**
	 * @method public
	 * @param String groupName
	 * @param function fct
	 * @return any
	 */
	f_mapIntoGroup: function(groupName, fct) {
		f_core.Assert(typeof(fct)=="function", "fa_groupName.f_mapIntoGroup: Invalid fct parameter ("+fct+") !");

		if (!groupName) {
			return null;
		} 
	
		var g = this.f_listGroup(groupName);
		if (!g) {
			return null;
		}
		
		for(var i=0;i<g.length;i++) {
			var ret=fct.call(this, g[i]);
			
			if (ret!==undefined) {
				return ret;
			}
		}
		return false;
	},
	/**
	 * @method public
	 * @param String groupName
	 * @param Function fct
	 * @return any
	 */
	f_mapIntoGroupOfComponents: function(groupName, fct) {
		f_core.Assert(typeof(fct)=="function", "fa_groupName.f_mapIntoGroupOfComponents: Invalid fct parameter ("+fct+") !");

		if (!groupName) {
			return null;
		} 
	
		var g = this.f_listGroupOfComponents(groupName);
		if (!g) {
			return null;
		}
		
		for(var i=0;i<g.length;i++) {
			var ret=fct.call(this, g[i]);
			
			if (ret!==undefined) {
				return ret;
			}
		}
		return false;
	},
	/**
	 * @method public
	 * @param String groupName
	 * @return f_component[]
	 */
	f_listGroupOfComponents: function(groupName) {
		f_core.Assert(typeof(groupName)=="string", "fa_groupName.f_listGroupComponents: Invalid groupName parameter ("+groupName+") !");
		
		var cachedElements=new Array;
		var group= this.f_listGroup(groupName, undefined, cachedElements);		
		if (!group || !group.length) {
			return [];
		}
		
		if (cachedElements.length) {
			return cachedElements;
		}
				
		// Il faut transformer les clientIds en composants !

		var classLoader=this.f_getClassLoader();

		for(var i=0;i<group.length;) {
			var component = this.ownerDocument.getElementById(group[i]);
			
			// He oui en ajax, le composant peut etre introuvable !
			if (!component) {
				group.splice(i,1);
				continue;
			}
			
			component=classLoader.f_init(component);
			if (!component) {
				group.splice(i,1);
				continue;
			}
			
			i++;
			cachedElements.push(component);
		}
		
		return cachedElements;
	},
	
	/**
	 * @method public
	 * @param String groupName
	 * @param hidden optional Boolean create
	 * @param hidden optional Array cachedElements
	 * @return Array If native returns a list of clientIds, else an array of declared object
	 */
	f_listGroup: function(groupName, create, cachedElements) {
		f_core.Assert(typeof(groupName)=="string", "fa_groupName.f_listGroup: Invalid groupName parameter ("+groupName+") !");
			
		var scope=fa_groupName;
		if (typeof(this.fa_getRadioScope)=="function") {
			scope=this.fa_getRadioScope();
		}

		var groupKey=this.f_getClass().f_getName();
		
		var isNativeRadioElement=this.fa_isNativeRadioElement && this.fa_isNativeRadioElement();
		if (isNativeRadioElement) {
			create=true;
		}
	
		var wg=scope._Groups;
		if (!wg) {
			if (!create) {
				return [];
			}
			wg=new Object;
			scope._Groups=wg;
		}
		
		var groups=wg[groupKey];
		if (!groups) {
			if (!create) {
				return [];
			}

			groups=new Object;
			wg[groupKey]=groups;
		}
		
		var group=groups[groupName];		
			
		if (!group) {
			group=new Array;
			groups[groupName]=group;
		}

		if (!create || !isNativeRadioElement) {
			// Les composants ont été enregistré à la construction car ils ne sont pas nativement regroupables par groupes.
			return group;
		}

		// Des radios natifs, on les recherche ...

		f_core.Debug(fa_groupName, "f_listGroup: Search elements by name '"+groupName+"' ...");

		var classLoader=f_classLoader.Get(window);

		var elements=this.ownerDocument.getElementsByName(groupName);
		for(var i=0;i<elements.length;i++) {
			var element=elements[i];
			
			var elementId=element.id;

			var inputSuffixPos=elementId.indexOf("::");
			if (inputSuffixPos>0) {
				elementId=elementId.substring(0, inputSuffixPos);
						
				element=f_core.GetElementByClientId(elementId);

			} else {
				element = classLoader.f_init(element, false, true);
			}

			f_core.Debug(fa_groupName, "f_listGroup: Found element id='"+element.id+"' mainId='"+elementId+"' element='"+element+"'.");

			if (!element) {
				continue;
			}
			
			group.push(element.id);			
			if (cachedElements) {
				cachedElements.push(element);
			}
		}

		return group;
	},
	
	/**
	 * @method protected abstract
	 * @return String
	 */
	fa_getRadioScope: f_class.OPTIONAL_ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return Boolean
	 */
	fa_isNativeRadioElement: f_class.OPTIONAL_ABSTRACT
};

new f_aspect("fa_groupName", {
	statics: __statics,
	members: __members
});
