/*
 * $Id: fsvg_imageButton.js,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */

/**
 * Class fsvg_image.
 *
 * @class fsvg_imageButton extends f_component, fa_readOnly, fa_disabled, fa_immediate, fa_value, fa_items
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */

var __members = {
	fsvg_imageButton: function() {
		this.f_super(arguments);
	
		var useMap=this.useMap;
		
		if (useMap) {
			var maps=this.ownerDocument.getElementsByName(useMap.substring(1));
			if (maps && maps.length) {
				var areas=f_core.GetElementsByTagName(maps[0], "area")
				for(var i=0;i<areas.length;i++) {
					var area=areas[i];
					
					var value=f_core.GetAttribute(area, "v:value");
					if (!value) {
						continue;
					}
					
					area._value=value;					
					area._imageButton=this;
					
					var button=this;
					area.onclick=function(event) {
						return button._fireItemSelectionEvent(this, event);
					}
					area.onmouseover=function(event) {
						return button._fireItemMouseOverEvent(this, event);
					}
					area.onmouseout=function(event) {
						return button._fireItemMouseOutEvent(this, event);
					}
					
					var label=f_core.GetAttribute(area, "v:label");
					if (label) {
						area._label=label;
					}

					this.f_addItem(this, area);
					
					var clientDatas=f_core.GetAttribute(area,"v:data");
					if (clientDatas) {						
						var datas=f_core.ParseDataAttribute(area);
						this.f_setItemClientDatas(area, datas);
					}	
				}
			}
		}
	},
	
	_fireItemSelectionEvent: function(item, evt) {	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		return this.f_fireEvent(f_event.SELECTION, evt, item, item._value);
	},
	_fireItemMouseOverEvent: function(item, evt) {		
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		
		return this.f_fireEvent(f_event.MOUSEOVER, evt, item, item._value);
	},
	_fireItemMouseOutEvent: function(item, evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		return this.f_fireEvent(f_event.MOUSEOUT, evt, item, item._value);
	},
	fa_updateItemStyle: function() {
		// Rien !
	},
	/**
	 * @method public
	 * @param Object item
	 * @return String Text associated to the item.
	 */
	f_getItemLabel: function(item) {
		return item._label;
	},
	fa_destroyItems: function(items) {
		for(var i=0;i<items.length;i++) {
			var area=items[i];
			items[i]=null;
			
			area.onclick=null;
			area.onfocus=null;
			area.onblur=null;
			area.onmouveover=null;
			area.onmouseout=null;
		}
	}
}

var __statics = {
}

new f_class("fsvg_imageButton", {
	extend: f_component, 
	aspects: [ fa_readOnly, fa_disabled, fa_immediate, fa_value, fa_items],
	statics: __statics,
	members: __members
});
