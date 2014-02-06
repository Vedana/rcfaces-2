/*
 * $Id: f_gridColumn.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 * @class public f_gridColumn extends f_object, fa_eventTarget
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __statics={

	/**
	 * @field private static final
	 */
	_EVENTS: {
			init: f_event.INIT,
			selection: f_event.SELECTION,
			dblClick: f_event.DBLCLICK,
			propertyChange: f_event.PROPERTY_CHANGE,
			user: f_event.USER
	}
};
	
var __members = {
	f_gridColumn: function() {
		var events=this._events;
		if (events) {
			this.f_initEventAtts(f_gridColumn._EVENTS ,events);
		}

		if (this._hasInitListeners) {
			this._hasInitListeners=undefined;
		
			this.f_fireEvent(f_event.INIT);
		}
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getId: function() {
		return this._id;
	},
	/**
	 * @method public
	 * @return f_grid
	 */
	f_getGrid: function() {
		return this._dataGrid;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isVisible: function() {
		return this._visibility;
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isSortable: function() {
		if (this._method) {
			return true;
		}
		return false;
	},
	/**
	 * @method public
	 * @return Number Ascending:1 Descending:-1 not-sorted:0
	 */
	f_getColumnOrderState: function() {
		if (this._ascendingOrder===true) {
			return 1;
			
		} else if (this._ascendingOrder===false) {
			return -1;
		}
		
		return 0;
	}
};


new f_class("f_gridColumn", {
	extend: f_object,
	aspects: [fa_eventTarget],
	statics: __statics,
	members: __members
});
