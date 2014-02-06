/* 
 * $Id: f_combo.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */


/**
 * 
 * @class public f_combo extends f_abstractList, fa_selectionProvider<String>
 * @author Joel Merlin
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __members = {
	f_update: function() {
		if (f_core.GetBooleanAttributeNS(this,"noSelection")) {
			this.selectedIndex = -1;
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method public 
	 * @param hidden Boolean byIndex Returns the index of the item if setted to <code>true</code>.
	 * @return Object Value of the selected item. (or <code>null</code>)
	 */
	f_getValue: function(byIndex) {
		var items = this.options;
		for (var i=0; i<items.length; i++) {
			var item = items[i];
			if (!item.selected) {
				continue;
			}
			
			return (byIndex? i:item.value);
		}
		return (byIndex? -1:null);
	},
	/**
	 * @method public 
	 * @param String val Value associated to an item. (if val is a number, val specifies the index of the item)
	 * @param hidden Boolean byIndex Select the item by index instead of value.
	 * @param hidden Boolean deselectOther Deselect not specified items.
	 * @return Boolean True is success. (Item has been found !)
	 */
	f_setValue: function(val, byIndex, deselectOther) {
		var items = this.options;
		
		if (byIndex) {
			f_core.Assert(typeof(val)=="number", "f_combo.f_setValue: val parameter is not a number !");
			f_core.Assert(val>=0 && val<items.length, "f_combo.f_setValue: Number is out of bounds 0<="+val+"<"+items.length+" !");

			if (deselectOther) {
				for (var i=0; i<items.length; i++) {
					var item = items[i];
					if (!item.selected || i==byIndex) {
						continue;
					}
					
					item.selected=false;
				}
			}
		
			items[val].selected = true;
			return true;
		}
		
		var ret=false;
		
		for (var i=0; i<items.length; i++) {
			var item = items[i];
			if (item.value != val) {
				if (deselectOther && item.selected) {
					item.selected=false;
				}
				continue;
			}
			
			item.selected = true;
			if (!deselectOther) {
				return true;
			}
			ret=true;
		}
		
		if (!ret) {
			// Rien n'a été selectionné !
			this.selectedIndex = -1;
		}
		return ret;
	},
	f_getSelection: function() {
		return this.f_getValue();
	},
	f_setSelection: function(selection, show) {
		this.f_setValue(selection, false, true);
	}
}


new f_class("f_combo", {
	extend: f_abstractList,
	aspects: [ fa_selectionProvider ],
	members: __members
});
