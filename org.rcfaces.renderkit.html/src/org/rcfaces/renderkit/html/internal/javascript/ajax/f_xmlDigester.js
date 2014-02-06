/*
 * $Id: f_xmlDigester.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * XML Digester
 * 
 * @class public f_xmlDigester extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
var __statics = {
	/**
	 * @field private static final String
	 */
	_MAIN_STACK_NAME: "_--main--_",
	
	/**
	 * @field private static final String
	 */
	_PARAMS_STACK_NAME: "_--params--_",
	
	/**
	 * @field hidden static final Number
	 */
	BEGIN_MODE: 0,

	/**
	 * @field hidden static final Number
	 */
	BODY_MODE: 1,
	
	/**
	 * @field hidden static final Number
	 */
	END_MODE: 2,
	
	/**
	 * @method private static
	 */
	_ObjectCreateRule: function(xmlNode, mode, parameters, body) {
		if (mode!=f_xmlDigester.BEGIN_MODE) {
			return;
		}

       	var realClass = parameters[0];
       	var attClass = parameters[1]; // Potentiellement, un attribut peut specifier la classe !
        if (attClass) {
  			var attributes=xmlNode.attributes;
  			
  			var att=attributes.getNamedItem(attClass);
  			if (att) {
	           	var value = att.nodeValue;
	            if (value) {
    	            value = window[value];
    	            if (value) {
	    	            realClass = value;	    	            

		   	            f_core.Debug(f_xmlDigester, "Attribute '"+attClass+"' specifies class name: "+value);
    	            }
        	    }        	    
			}
        }

		if (!realClass) {
			f_core.Error(f_xmlDigester, "Can not get class to instanciate specified by rule.");
			return;
		}

		var instance;
		if (realClass.f_newInstance) {
			instance=realClass.f_newInstance();
			
		} else {
			instance=new Object;
			instance.prototype=realClass.prototype;
		}
		
		f_core.Debug(f_xmlDigester, "Create object from class '"+realClass+"'.");

		var digester=this; // et oui ...        
        digester.f_push(instance);
	},
	
	/**
	 * @method private static
	 */
	_AddSetProperty: function(xmlNode, mode, parameters) {
		if (mode!=f_xmlDigester.BEGIN_MODE) {
			return;
		}

		var digester=this; // et oui ...        
		var top=digester.f_peek();

		for(var j=0;j<parameters.length;) {
			var name=parameters[j++];
			var value=parameters[j++];
	
	       	var actualName=null;
	        var actualValue=null;
	
			var attributes=xmlNode.attributes;
	        for(var i = 0; i < attributes.length; i++) {
	        	var attribute=attributes[i];
	        	
	            var attName = attribute.nodeName;
	            var attValue = attribute.nodeValue;
	            if (attName==name) {
	                actualName = attValue;
	                continue;
	            }
	                
	            if (attName==value) {
	                actualValue = attValue;
	                continue;
	            }
	        }
	        
	        if (!actualName) {
	        	continue;
	        }
			
			digester._setProperty(top, actualName, actualValue);
		}
	},
	
	/**
	 * @method private static
	 */
	_AddSetProperties: function(xmlNode, mode, parameters) {
		if (mode!=f_xmlDigester.BEGIN_MODE) {
			return;
		}
	},
	
	/**
	 * @method private static
	 */
	_AddSetNextRule: function(xmlNode, mode, parameters) {
		var digester=this; // et oui ...        
	
		var child=digester.peek(0);
		var parent=digester.peek(1);
		
		var methodName=parameters[0];
		
		if (typeof(parent)!="object") {
			f_core.Error(f_xmlDigester, "Invalid parent in the stack ! ("+parent+")");
			return;
		}
		
		var method=parent[methodName];
		if (typeof(method)!="function") {
			f_core.Error(f_xmlDigester, "Invalid methodName '"+methodName+"' for parent '"+parent+"'");
			return;
		}
		
		try {
			method.call(parent, child);
			
		} catch (x) {
			f_core.Error(f_xmlDigester, "Call of method '"+method+"' of object '"+parent+"' with parameter '"+child+"' throws an exception !", x);
			
			throw x;
		}
	},
	
	/**
	 * @method private static
	 */
	_AddSetTopRule: function(xmlNode, mode, parameters) {
		var digester=this; // et oui ...        
	
		var child=digester.peek(0);
		var parent=digester.peek(1);
		
		var methodName=parameters[0];
		if (methodName) {
			var method=child[methodName];
			
			if (typeof(method)!="function") {
				f_core.Error(f_xmlDigester, "Invalid methodName '"+methodName+"' for object '"+child+"'.");
				return;
			}
			
			f_core.Debug(f_xmlDigester, "Call method '"+method+"' to object '"+child+"' with parameter '"+parent+"'.");
			try {
				method.call(child, parent);

			} catch (x) {
				f_core.Error(f_xmlDigester, "Call of method '"+method+"' of object '"+parent+"' with parameter '"+child+"' throws an exception !", x);
				
				throw x;
			}
				
			return;			
		}
		
		digester._setProperty(child, "parent", parent);		
	}
}

var __members = {
	f_xmlDigester: function() {
		this.f_super(arguments);

		this._rules=new Array;
		this._stacks=new Object;
	},

	f_finalize: function() {
		this._tree=undefined; // object[]
		this._rules=undefined; // any[]
		this._root=undefined; // any
		this._stacks=undefined; // any[][]
		
		this.f_super(arguments);
	},

	/**
     * This method allows you to access the root object that has been
     * created after parsing.
     * 
	 * @method public
     * @return any the root object that has been created after parsing or null if the digester has not parsed any XML yet.
	 */
	f_getRoot: function() {
		return this._root;
	},
	
	/**
	 * @method public
	 * @param String pattern
	 * @param f_class clazz f_class or a javascript class.
	 * @return void
	 */
	f_addObjectCreate: function(pattern, clazz, attributeName) {
		this._addRule(pattern, f_xmlDigester._ObjectCreateRule, clazz, attributeName);
	},
	
	/**
	 * @method public
	 * @param String pattern 
	 * @param String name
	 * @param String value  (if NULL take name as value !)
	 * @param optional String name2
	 * @param optional String value2
	 * @return void
	 */
	f_addSetProperty: function(pattern, name, value, name2, value2) {
		this._addRule(pattern, f_xmlDigester._AddSetProperty, arguments);	
	},
	
	/**
	 * @method public
	 * @param String pattern 
	 * @return void
	 */
	f_addSetProperties: function(pattern, attributes) {
	
		// Parameters:
		//   "toto", ["titi", "attTiti"], "equ"
	
		this._addRule(pattern, f_xmlDigester._AddSetProperties, clazz);	
	},
	
	/**
	 * @method public
	 * @param String pattern 
	 * @param String name  Name of method to call to append top object of the stack, to the previous top object.
	 * @return void 
	 */
	f_addSetNextRule: function(pattern, name) {
		this._addRule(pattern, f_xmlDigester._AddSetNextRule, name);	
	},
	
	/**
	 * @method public
	 * @param String pattern 
	 * @param String name  Name of method to call to append the second top object of the stack, to the top object.
	 * @return void 
	 */
	f_addSetTopRule: function(pattern, name) {
		this._addRule(pattern, f_xmlDigester._AddSetTopRule, name);	
	},
	
	/**
	 * @method public
	 * @param String pattern 
	 * @param Function method
	 * @param any parameters
	 * @return void 
	 */
	f_addCallMethod: function(pattern, method, parameters) {
		this._addRule(pattern, f_xmlDigester._AddCallMethod, method, parameters);	
	},
	
	/**
	 * @method private	 
	 */
	_addRule: function(pattern, method, parameters) {
		f_core.Assert(typeof(pattern)=="string", "Pattern parameter is invalid ("+pattern+")");
		f_core.Assert(typeof(method)=="function", "Pattern parameter is invalid ("+pattern+")");
		
		if (arguments.length>2) {
			parameters=f_core.PushArguments(null, arguments, 2);
		}
		
		var rule={
			_pattern: pattern,
			_method: method,
			_parameters: parameters
		};
		
		this._rules.push(rule);
		
		this._tree=undefined;
		
		return rule;
	},
	
	/**
	 * @method private
	 */
	_computeTree: function() {
		var tree=this._tree;
		if (tree) {
			return true;
		}
		tree=new Object;
		this._tree=tree;
		
		var rules=this._rules;
		if (!rules) {
			return tree;
		}
		
		var cacheRules=new Object;
		
		for(var i=0;i<rules.length;i++) {
			var rule=rules[i];
			var pattern=rule._pattern;
			
			var node=cacheRules[pattern];
			
			if (!node) {
				node=tree;
				
				var ss=pattern.split("/");
				for(var j=0;j<ss.length;j++) {
					var s=ss[j];
					
					var n=node[s];
					if (n) {
						node=n;
						continue;
					}
					n=new Object;
					node[s]=n;
					node=n;
				}
				
				cacheRules[pattern]=node;
			}
			
			var rs=node._rules;
			if (!rs) {
				rs=new Array;
				node._rules=rs;
			}
			
			rs.push(rule);
		}		
		
		return tree;
	},

	/**
	 * @method private
	 */
	_parseNode: function(xmlNode, ruleNode) {
		var tagName=xmlNode.tagName;
		
		var rule=ruleNode[tagName];
		if (rule) {
			this._parseRule(xmlNode, rule);
		}
		
		rule=ruleNode["*"];
		if (rule) {
			this._parseRule(xmlNode, rule);
		}
	},
	
	/**
	 * @method private
	 */
	_parseRule: function(xmlNode, ruleNode) {
		var rules=ruleNode._rules;
		for(var i=0;i<rules.length;i++) {
			var rule=rules[i];
			
			var method=rule._method;
			var parameters=rule._parameters;

			method.call(this, xmlNode, f_xmlDigester.BEGIN_MODE, parameters);
		}
		
		var body="";
		
		var children=xmlNode.childNodes;
		if (children && children.length) {
			for(var i=0;i<children.length;i++) {
				var xmlChild=chilren[i];
				
				switch(xmlChild.nodeType) {
				case 1: // Element Node
					this._parseNode(xmlChild, ruleNode);
					break;
					
				case 3: // Text Node
				case 4: // CDATA
					body += xmlChild.data;
					break;
				}
			}
		}

		for(var i=0;i<rules.length;i++) {
			var rule=rules[i];
			
			var method=rule._method;
			var parameters=rule._parameters;

			method.call(this, xmlNode, f_xmlDigester.BODY_MODE, parameters, body);
		}

		for(var i=rules.length;i>0;) {
			var rule=rules[--i];
			
			var method=rule._method;
			var parameters=rule._parameters;

			method.call(this, xmlNode, f_xmlDigester.END_MODE, parameters);
		}
	},
	
	/**
	 * Pop the top object off of the parameters stack, and return it. If there are no objects on the stack, return null
	 * 
	 * @method public
	 * @return any the top Object on the stack or or null if the stack is either empty or has not been created yet
	 */
	f_popParams: function() {
		return this.f_pop(f_xmlDigester._PARAMS_STACK_NAME);
	},
	
	/**
	 * Pops (gets and removes) the top object from the stack with the given name. <br>
	 * Note: a stack is considered empty if no objects have been pushed onto it yet.
	 * 
	 * @method public
	 * @param optional String name Name of stack
	 * @return any the top Object on the stack or or null if the stack is either empty or has not been created yet
	 */
	f_pop: function(name) {
		var stack=this._getStack(name);
		if (!stack) {
			return null;
		}
		
		return stack.pop();
	},
	/**
	 * Push a new object onto the top of the parameters stack.
	 *
	 * @method public
	 * @param any object The new object
	 * @return void
	 */
	f_pushParams: function(object) {
		this.f_push(f_xmlDigester._PARAMS_STACK_NAME, object);
	},
	
	/**
	 * Push a new object onto the top of the object stack.
	 *
	 * @method public
	 * @param any object The new object
	 * @param optional String name Name of stack
	 * @return void
	 */
	f_push: function(object, name) {
		var stacks=this._stacks;
		
		if (!name) {
			name=f_xmlDigester._MAIN_STACK_NAME;
		}
		
		var stack=stacks[name];
		if (!stack) {
			stack=new Array;
			stacks[name]=stack;
		}
		
		stack.push(object);
		
		if (stack.length==1 && name==f_xmlDigester._MAIN_STACK_NAME) {
			this._root=object;
		}
	},
	
	/**
	 * Return the n'th object down the parameters stack, where 0 is the top element and [getCount()-1] is the bottom element. If the specified index is out of range, return null.
	 * 
	 * @method public
	 * @param optional Number index Index of the desired element, where 0 is the top of the stack, 1 is the next element down, and so on.
	 * @return any 
	 */
	f_peekParams: function(index) {
		return this.f_peek(f_xmlDigester._PARAMS_STACK_NAME, index);
	},
	
	/**
	 * Return the n'th object down the stack, where 0 is the top element and [getCount()-1] is the bottom element. If the specified index is out of range, return null.
	 * 
	 * @method public
	 * @param optional Number index Index of the desired element, where 0 is the top of the stack, 1 is the next element down, and so on.
	 * @param optional String name Name of stack
	 * @return any
	 */
	f_peek: function(index, name) {
		var stack=this._getStack(name);
		if (!stack) {
			return null;
		}
		
		if (index===undefined) {
			index=0;
		}
		
		if (index>=stack.length) {
			return null;
		}
		
		return stack[stack.length-1-index];
	},
	
	/**
	 * @method private
	 */
	_getStack: function(name) {
		if (!name) {
			name=f_xmlDigester._MAIN_STACK_NAME;
		}
		
		return this._stacks[name];
	},
	
	/**
	 * Is the stack with the given name empty?<br>
	 * Note: a stack is considered empty if no objects have been pushed onto it yet.
	 *
	 * @method public
	 * @param optional String name The name of the stack whose emptiness should be evaluated.
	 * @return Boolean <code>true</code> if the given stack if empty 
	 */
	f_isEmpty: function(name) {
		return this.f_getCount(name)==0;
	},
	
    /**
     * Return the current depth of the element stack.
     * 
     * @method public
     * @param optional String name The name of the stack whose depth should be evaluated.
     * @return Number The depth of the stack.
     */
	f_getCount: function(name) {		
		var stack=this._getStack(name);
		if (!stack) {
			return 0;
		}
		
		return stack.length;
	},
	
	/**
	 * Parse the content of the specified URI using this Digester.
     * Returns the root element from the object stack (if any).
     *
	 * @method public
	 * @param Object source XML data to be parsed.
	 * @return any Root object
	 */
	f_parse: function(source) {
		var xmlDocument=source;
		if (typeof(source)=="string") {
			// convertir en document
			// Si URL, telecharger le document !
			
			xmlDocument=f_xml.FromString(source);
		}
		
		var tree=this._computeTree();
		
		var root=xmlDocument.rootElement;
		
		this._parseNode(root, tree);
		
		return this._root;
	},
	
	/**
     * Clear the current contents of the object stack.
     * <p>
     * Calling this method <i>might</i> allow another document of the same type
     * to be correctly parsed. However this method was not intended for this 
     * purpose. In general, a separate Digester object should be created for
     * each document to be parsed.
     *
	 * @method public
	 * @return void
	 */
	f_clear: function() {
		this._stacks=new Array;
	},
	
	/** 
	 * @method private
	 * @return void
	 */
	_setProperty: function(object, attributeName, attributeValue) {
		f_core.Assert(typeof(attributeName)=="string", "_setProperty: Attribute name is not a string ! ("+attributeName+")");
		f_core.Assert(typeof(object)=="object", "_setProperty: Object parameter is not an object ! ("+object+")");

		var setterName="set"+attributeName.charAt(0).toUpperCase()+attributeName.substring(1);
		
		var f=object[setterName];
		if (typeof(f)=="function") {
			try {
				f.call(object, attributeValue);
				
			} catch (x) {
				f_core.Error(f_xmlDigester, "_setProperty: Setter '"+setterName+"' of object '"+object+"' throws exception (value='"+attributeValue+"')");
				
				throw x;
			}
			
			return;
		}
		
		try {
			object[attributeName]=attributeValue;
			
		} catch (x) {
			f_core.Error(f_xmlDigester, "_setProperty: Set field '"+setterName+"' of object '"+object+"' with value '"+attributeValue+"' throws exception (value='"+attributeValue+"')");
			
			throw x;
		}
	}
}
new f_class("f_xmlDigester", {
	extend: f_object,
	statics: __statics,
	members: __members
});
