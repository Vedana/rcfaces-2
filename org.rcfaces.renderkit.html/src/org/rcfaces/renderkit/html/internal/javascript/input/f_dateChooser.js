/*
 * $Id: f_dateChooser.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * 
 * @class public f_dateChooser extends f_imageButton, fa_calendarPopup
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members={

	f_dateChooser: function() {
		this.f_super(arguments);
		
		this._forComponent=f_core.GetAttributeNS(this,"for");
		if (this._forComponent) {
			this._forValueFormat=f_core.GetAttributeNS(this,"forValueFormat");
		}
		
		
		var defaultSelectedDate=f_core.GetAttributeNS(this,"defaultSelectedDate");
		if (defaultSelectedDate) {
			this.f_setDefaultSelectedDate(f_core.DeserializeDate(defaultSelectedDate));
		}
	},
	/*
	f_finalize: function() {
		// this._forComponent=undefined; // String
		// this._forValueFormat=undefined; // String

		this.f_super(arguments);
	},
	*/
	f_imageButtonSelect: function(event) {
		f_core.Debug(f_dateChooser, "f_imageButtonSelect: "+event+" detail="+event.f_getDetail());

		if (this.f_isDisabled()) {
			return false;
		}
		
		if (!this._focus)  {
			this.f_setFocus();
		}

		if (this.f_isReadOnly()) {
			return false;
		}

		return this.f_openCalendarPopup(event);
	},
	f_setDomEvent: function(type, target) {
		if (type==f_event.CHANGE) {
			return;
		}
		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		if (type==f_event.CHANGE) {
			return;
		}
		return this.f_super(arguments, type, target);
	},
	_onDateSelected: function(date, jsEvent) {
		f_core.Debug(f_dateChooser, "_onDateSelected: Selected date: "+date);
		
		return this.f_fireEvent(f_event.CHANGE, jsEvent, null, date);
	},
	f_calendarPopupClosed: function() {
		this.f_clearMouseDownState();
	},
	f_serialize: function() {
		var calendarObject=this.f_getCalendarObject();
		
		var value=calendarObject.f_getSelection();
		if (value) {				
			this.f_setProperty(f_prop.VALUE, value);
		}
		
		/*
		 * On risque d'avoir des problemes lors de la réouverture du Popup qui ne sera pas positionné correctement.
		 * 		
		var cursorDate=calendarObject.f_getCursorDate();
		if (cursorDate) {
			this.f_setProperty(f_prop.CURSOR, cursorDate);
		}
		*/

		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getFor: function() {
		return this._forComponent;
	},
	/**
	 * @method public
	 * @return f_component
	 */
	f_getForComponent: function() {
		var forComponent=this._forComponent;
		if (!forComponent) {
			return null;
		}
		
		return f_core.FindComponent(forComponent);
	}
}
 
new f_class("f_dateChooser", {
	extend: f_imageButton, 
	aspects: [ fa_calendarPopup ],
	members: __members
});
