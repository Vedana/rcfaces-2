/*
 * $Id: f_calendar.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 * @class public f_calendar extends f_component, fa_readOnly, fa_disabled, fa_itemsWrapper
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __statics = {
	
	/**
	 * @field public static final Number
	 */
    SHORT_LAYOUT: 1,

 	/**
	 * @field public static final Number
	 */
    MEDIUM_LAYOUT: 2,

 	/**
	 * @field public static final Number
	 */
    LONG_LAYOUT: 3,

 	/**
	 * @field public static final Number
	 */
    FULL_LAYOUT: 4,

 	/**
	 * @field public static final Number
	 */
    DEFAULT_LAYOUT: 2
}

var __members = {
	
	f_calendar: function() {
		this.f_super(arguments);
		
		var layout;
		switch(f_core.GetNumberAttributeNS(this, "layout", f_calendar.DEFAULT_LAYOUT)) {
		default:		
			layout=f_calendarObject.YEAR_CURSOR_LAYOUT | 
				f_calendarObject.MONTH_LIST_LAYOUT |
				f_calendarObject.DAY_LIST_LAYOUT |
				f_calendarObject.UNIT_CURSOR_LAYOUT |
				f_calendarObject.SELECT_DAY_LAYOUT |
				f_calendarObject.SELECT_WEEK_LAYOUT;
			break;
		}
		
		this._calendar=f_calendarObject.CreateCalendarFromComponent(this, layout);
	},
	
	f_finalize: function() {
		var calendar=this._calendar;
		if (calendar) {
			this._calendar=undefined;
			
			f_classLoader.Destroy(calendar);
		}
	
		this.f_super(arguments);
	},
	
	f_update: function() {		
		this.f_updateComponent();
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 */
	f_updateComponent: function() {
		//var doc=this.ownerDocument;
		this._calendar.f_constructComponent(this);		
	},
	/**
	 * @method protected
	 * @return HTMLElement
	 */
	f_getFocusableElement: function() {
		return this;	
	},
	/**
	 * @method hidden
	 */
	f_appendDateItem: function(date, label, disabled, styleClass) {
		this._calendar.f_appendDateItem.apply(this._calendar, arguments);
	},
	/**
	 * @method hidden
	 */
	f_appendDateItem2: function(date) {
		this._calendar.f_appendDateItem2(date);
	},
	
	/**
	 * @method protected
	 */
	fa_getItemsWrapper: function() {
		return this._calendar;
	},
	/**
	 * @method public
	 * @return f_calendarObject
	 */
	f_getCalendarObject: function() {
		return this._calendar;
	},
	f_serialize: function() {
		var calendarObject=this.f_getCalendarObject();
		
		var value=calendarObject.f_getSelection();
		if (value) {
			this.f_setProperty(f_prop.VALUE, value);
		}
		
		var cursorDate=calendarObject.f_getCursorDate();
		if (cursorDate) {
			this.f_setProperty(f_prop.CURSOR, cursorDate);
		}

		this.f_super(arguments);
	}
}
 
new f_class("f_calendar", {
	extend: f_component,
	aspects: [ fa_readOnly, fa_disabled, fa_itemsWrapper ],
	statics: __statics,
	members: __members
});
