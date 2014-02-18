/*
 * $Id: fsvg_svg.js,v 1.1 2013/11/13 15:52:40 jbmeslin Exp $
 */

/**
 * Class fsvg_image.
 * 
 * @class fsvg_image extends f_image, fa_items
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:40 $
 */

var __statics = {
	/**
	 * @field public static final String
	 */
	XMLNS : "http://www.w3.org/2000/svg",
	
	/**
	 * @method private static
	 * @param Event event
	 * @return Boolean
	 * @context object:svg
	 */
	_ItemOnClick: function(evt) {
		var svg=this._svg;
		var item=this;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		f_core.Debug(fsvg_svg, "_ItemOnClick: perform event "+evt);
		
		if (svg.f_getEventLocked(evt)) {
			return false;
		}
	
		return svg._onClick(evt, item);
	}

}

var __members = {
	/**
	 * @method public
	 * @return Document
	 */
	f_getSVGDocument : function() {
		return this.contentDocument;
	},
	/**
	 * @method public
	 * @return Element
	 */
	f_getRootElement : function(id) {
		return this.f_getSVGDocument().rootElement;
	},
	/**
	 * @method public
	 * @return Element
	 */
	f_getElementById : function(id) {
		return this.f_getSVGDocument().getElementById(id);
	},
	/**
	 * @method public
	 * @return Element
	 */
	$ : function(expr) {
		return this.f_querySelector(expr);
	},
	/**
	 * @method public
	 * @return Element
	 */
	f_querySelector : function(expr) {
		return this.f_getSVGDocument().querySelector(expr);
	},
	/**
	 * @method public
	 * @return Element[]
	 */
	f_querySelectorAll : function(expr) {
		return this.f_getSVGDocument().querySelectorAll(expr);
	},
	f_setItemColor : function(item, color) {

	},
	fa_updateItemStyle : function(item) {
		var elt = this._getItemElement(item);
		if (!elt) {
			return;
		}

		var itemStyle = item.style;

		var itemColor = item._color;
		if (itemColor !== undefined && itemStyle.color != itemColor) {
			if (itemColor) {
				itemStyle.color = itemColor;
			} else {
				// remove style
			}
		}

	},
	_getItemElement : function(item) {
		var valueToElement = this._valueToElement;
		if (!valueToElement) {
			valueToElement = new Object();
			this._valueToElement = valueToElement;
		}

		var value = this.f_getItemValue(item);
		if (value === undefined) {
			return undefined;
		}

		var element = valueToElement[value];
		if (typeof (element) != "undefined") {
			return element;
		}

		element = this.f_getElementById(value);
		valueToElement[value] = element;

		return element;
	},
	_update : function(id, modifs) {
		var c = this.f_getElementById(id);
		if (!c) {
			return;
		}

		if (modifs._color !== undefined) {
			c.style.color = modifs._color;
		}
		if (modifs._fill !== undefined) {
			c.style.fill = modifs._fill;
		}
		if (modifs._styleClass !== undefined) {
			c.className = modifs._styleClass;
		}
		if (modifs._tooltipText !== undefined) {
			c.title = modifs._tooltipText;
		}
		if (modifs._text !== undefined) {
			f_core.SetTextNode(c, modifs._text);
		}
		if (modifs._value !== undefined) {
			c._value=modifs._value;
		}
		if (modifs._visibility !== undefined) {
			c.style.visibility = (modifs._visibility) ? "visible" : "hidden";
		}
		if (modifs._selectable) {
			c._svg = this;
			c.onclick = fsvg_svg._ItemOnClick;
			c.tabIndex = 0;
			c.style.cursor="pointer";

		} else if (modifs._selectable === false) {
			c.tabIndex = -1;
		}
		if (modifs._audioDescription !== undefined) {
			c.setAttribute("aria-label", modifs._audioDescription);
		}

	},
	/**
	 * @method public
	 * @param String
	 *            url URL of stylesheet
	 * @return void
	 */
	f_appendStyleSheet : function(url) {
		var doc = this.f_getSVGDocument();
		
		url=f_env.ResolveContentUrl(url, window);

		var pi = doc.createProcessingInstruction('xml-stylesheet', 'href="'
				+ url + '" type="text/css"');

		doc.insertBefore(pi, doc.firstChild);
	},
	/**
	 * @method public
	 * @param String
	 *            styles
	 * @return void
	 */
	f_appendSVGStyle : function(styles) {
		var doc = this.f_getSVGDocument();

		var pi = doc.createElementNS(fsvg_svg.XMLNS, "style");
		pi.setAttributeNS(null, "type", "text/css");

		var pt = doc.createTextNode(styles);
		pi.appendChild(pt);

		doc.rootElement.insertBefore(pi, doc.rootElement.firstChild);
	},
	/**
	 * @method protected
	 * @param Event jsEvent
	 * @param Object item
	 * @return Boolean
	 */
	_onClick: function(jsEvent, item) {
		
		var event=new f_event(this, f_event.SELECTION, jsEvent, item, item._value);
		try {
			this.f_fireEvent(event);
			
		} finally {
			f_classLoader.Destroy(event);
		}
	}
}

new f_class("fsvg_svg", {
	extend : f_box,
	aspects : [ fa_items ],
	members : __members,
	statics : __statics
});
