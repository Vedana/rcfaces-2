/*
 * $Id: f_xml.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * XML utils
 * 
 * @class public f_xml extends Object
 * @author Joel Merlin and Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */

var __statics = {

	/**
	 * @method public static
	 * @param Node element XML node.
	 * @param optional String tagName Name of the tag or null.
	 * @param optional String attrName Name of an attribute.
	 * @param optional String attrValue A value for the specified attribute.
	 * @return Node[] An array of xml nodes.
	 */
	GetChildElements: function(element, tagName, attrName, attrValue) {
		var list = new Array;
		var acn = element.childNodes;
		if (!acn || !acn.length) {
			return list;
		}
		
		for (var i=0; i<acn.length; i++) {
			var cn = acn[i];
			if (cn.nodeType!=f_core.ELEMENT_NODE) {
				continue;
			}
			if (tagName && cn.nodeName!=tagName) {
				continue;
			}
			if (attrName) {
				var attr  = cn.getAttributeNode(attrName);
				if (!attr) {
					continue;
				}
			
				if (attrValue && attr.nodeValue!=attrValue) {
					continue;
				}
			}
			list.push(cn);
		}
		return list;
	},
	/**
	 * @method public static
	 * @param Node element XML node.
	 * @param optional String tagName Name of the tag.
	 * @return Node XML node or null.
	 */
	GetFirstChildElement: function(element, tagName) {
		var acn = element.childNodes;
		if (!acn || !acn.length) {
			return null;
		}
		
		for (var i=0; i<acn.length; i++) {
			var cn = acn[i];
			if (cn.nodeType!=f_core.ELEMENT_NODE) {
				continue;
			}
			if (tagName && cn.nodeName!=tagName) {
				continue;
			}
			return cn;
		}
		return null;
	},

	/**
	 * @method public static
	 * @param Node element XML node.
	 * @param optional String defaultValue Default value.
	 * @return String Value associated to the XML node.
	 */
	GetValue: function(element, defaultValue) {
		var childNodes = element.childNodes;
		if (!childNodes || !childNodes.length) {
			return value;
		}
		
		var ret = null;
		for (var i=0; i<childNodes.length; i++) {
			var childNode = childNodes[i];
			if (childNode.nodeType!=f_core.TEXT_NODE && childNode.nodeType!=f_core.CDATA_SECTION_NODE) {
				continue;
			}
			
			var v = childNode.nodeValue;
			if (!v) {
				continue;
			}
			
			if (!ret) {
				ret = v;
				continue;
			}

			ret += v;
		}
		
		if (ret==null) {
			return defaultValue;
		}
		
		return ret;
	},

	/**
	 * Create an empty XML Document.
	 *
	 * @method public static
	 * @return Document Empty XML DOM document.
	 */
	Object: function() {
		var dom=null;
		
		if (f_core.IsInternetExplorer()) {
			dom = new ActiveXObject("microsoft.XMLDOM");

		} else if (f_core.IsGecko()) {
			dom = document.implementation.createDocument("","",null);
		}
		
		if (!dom) {
			throw "f_xml: failed to create DOM object";
		}

		return dom;
	},

	/**
	 * @method public static
	 * @param String data XML content.
	 * @return Document XML Document.
	 */
	FromString: function(data) {
		var dom;
		
		if (f_core.IsInternetExplorer()) {
			dom = new ActiveXObject("microsoft.XMLDOM");
			dom.loadXML(data);

		} else if (f_core.IsGecko()) {
			dom = new DOMParser();
			dom = dom.parseFromString(data, "text/xml");
		}
		
		if (!dom) {
			throw "f_xml: failed to create DOM object from string";
		}

		return dom;
	},

	/**
	 * @method public static
	 * @param Node node XML node.
	 * @return String XML output.
	 */
	Serialize: function(node) {
		var ret = "";
		var acn = node.childNodes;
		if (!acn || !acn.length) {
			return ret;
		}
		
		for (var i=0; i<acn.length; i++) {
			var cn = acn[i];
			var value=cn.nodeValue;
			
			switch(cn.nodeType) {
			case 1: // ELEMENT
				ret += "<"+cn.tagName;
				var attrs = cn.attributes;
				for (var n=0; n<attrs.length; n++) {
					var attr = attrs[n];
					ret += " "+attr.nodeName+"=\"";
					var v=attr.nodeValue;
					if (v) {
						ret+=v;
					}
					ret+="\"";
				}
				
				var content=f_xml.serialize(cn);
				if (!content.length) {
					ret+="/>";
					break;
				}
				ret += ">"+content+"</"+cn.tagName+">";
				break;
			
			case 3: // Text node
				ret += value; 
				break;
				
			case 4: // CDATA
				ret += "<![CDATA["+value+"]]>";
				break;
			
			case 7: // XML prolog
				ret += "<?xml "+value+"?>"; 
				break;
				
			case 8: // Comment
				ret += "<!-- "+value+" -->"; 
				break;
				
			//case 2: // ATTRIBUTE
			//case 5: // ENTITY REF
			//case 6: // ENTITY
			//case 9: // DOCUMENT
			//case 10: ret += "<!DOCTYPE "+value+">"; break;
			//case 11: // FRAGMENT
			//case 12: // NOTATION
			default: 
				break;
			}
		}
		
		return ret;
	}
}

new f_class("f_xml", {
	statics: __statics
});

