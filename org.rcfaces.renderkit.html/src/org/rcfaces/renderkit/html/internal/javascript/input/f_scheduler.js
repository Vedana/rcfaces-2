/**
 * f_scheduler class
 * 
 * @class f_scheduler extends f_component, fa_items, fa_selectionManager
 * @author jb.meslin@vedana.com
 */
var __statics = {
	/**
	 * @field private static final String
	 */
	_PERIOD_STYLE: "f_scheduler_period",
	
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean 
	 * @context object:scheduler
	 */
	_OnPeriodMouseOver : function(evt) {
		var scheduler = this._scheduler;
	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
	
		if (scheduler.f_getEventLocked(evt, false)) {
			return false;
		}
		
		var divNode = this;
		var period = divNode._period;
		if(!period._selectable){
			return false;
		}
		if(divNode._hover == true){
			return false;
		}
		divNode._hover=true;
		scheduler.fa_updateElementStyle(divNode);
		return true;
	},

	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:scheduler
	 */
	_OnPeriodMouseOut : function(evt) {
		var scheduler = this._scheduler;
	
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		var divNode = this;
		//var period = divNode._period;

		if(divNode._hover == false){
			return false;
		}
		divNode._hover=false;
		scheduler.fa_updateElementStyle(divNode);
		return true;
	},
	
	/**
	 * @method private static 
	 * @param Event evt
	 * @return Boolean
	 * @context object:scheduler
	 */
	_OnPeriodMouseDown: function(evt) {
		var scheduler=this._scheduler;
		if (!evt) {
			evt=f_core.GetJsEvent(this);
		}
		if (scheduler.f_getEventLocked(evt)) {
			return false;
		}
		var divNode = this;
		var period = divNode._period;
		if(!period._selectable){
			return false;
		}
		
		var selection=fa_selectionManager.ComputeMouseSelection(evt);
		scheduler.f_moveCursor(divNode, true, evt, selection);
		
		return true;
	}

};

var __members = {

	f_scheduler : function() {
		this.f_super(arguments);
		this._selectionCardinality=fa_cardinality.OPTIONAL_CARDINALITY;
	},

	f_finalize : function() {
		this.f_super(arguments);
	},
	
	 /**
	 * @method public 
	 * @param Object item
	 * @return void
	 */
	f_addPeriod: function(item) {
		item._begin = f_core.DeserializeDate(item._begin);
		item._end = f_core.DeserializeDate(item._end);
		this.f_addItem(this, item);
	},

	 /**
	 * @method protected 
	 * @return void
	 */
	f_update: function() {
		var items = this._items;
		
		if(!items) {
			return;
		}
	
		var columnNumber = f_core.GetNumberAttributeNS(this,"columnNumber");
		var tabIndex = f_core.GetNumberAttributeNS(this,"tabIndex");
		var dateBegin = f_core.GetAttributeNS(this,"dateBegin");
		if (dateBegin) {
			dateBegin = f_core.DeserializeDate(dateBegin);
		}
		var dayBegin = dateBegin.getDay();
		var dateEnd =  new Date(dateBegin.getTime());
		dateEnd.setTime(dateEnd.getTime()+ (columnNumber * 24 * 3600 * 1000));
		var minutesDayBegin = f_core.GetNumberAttributeNS(this,"minutesDayBegin");
		var minutesDayEnd = f_core.GetNumberAttributeNS(this,"minutesDayEnd");

		var minPerPx = parseFloat(f_core.GetAttributeNS(this,"minPerPx"));
		var columnWidth = f_core.GetNumberAttributeNS(this,"columnWidth");

		var div = this.ownerDocument.getElementById(this.id + "::periods");

		
		items.sort(function(p1	,p2){
			return p1._begin.getTime()-p2._begin.getTime();
		});
		for ( var i = 0; i < items.length; i++) {
			var period = items[i];

			if (!period._begin || !period._end) {
				continue;
			}
			var begin = period._begin;
			var end = period._end;
			var periodeDay = begin.getDay();
			if (end.getTime() >= dateBegin.getTime()
					&& begin.getTime() <= dateEnd.getTime()) {

				
				f_core.Debug(f_scheduler, "f_scheduler  periodBegin "+
						begin.getHours()+"h" +begin.getMinutes());
				var minutesPerdiodBegin = begin.getHours()*60+begin.getMinutes();
				if (minutesPerdiodBegin < minutesDayBegin){
					minutesPerdiodBegin = minutesDayBegin;
				}
				
				f_core.Debug(f_scheduler, "f_scheduler periodEnd "+
						end.getHours()+"h" +end.getMinutes());
				var minutesPerdiodEnd = end.getHours()*60+end.getMinutes();
				if (minutesPerdiodEnd > minutesDayEnd){
					minutesPerdiodEnd = minutesDayEnd;
				}
				var top = ((minutesPerdiodBegin - minutesDayBegin) * minPerPx).toFixed(0);

				var left = columnWidth * (periodeDay - dayBegin);
				var height = (minutesPerdiodEnd - minutesPerdiodBegin)
						* minPerPx;
				var width = (columnWidth - 1);
				if (periodeDay - dayBegin == columnNumber - 1) {
					width -= 1;
				}
				
				var style = f_scheduler._PERIOD_STYLE;
				if (period._periodStyle){
					style +=" "+period._periodStyle;
				}
				
				var divNode = undefined;
				
				if(period._selectable) {
				
				 divNode = f_core.CreateElement(div, "a",{
					className : style,
					cssTop : top + "px",
					cssLeft : left + "px",
					cssWidth : width + "px",
					cssHeight : height + "px",
					href : "javascript:void(0)",
					title : period._toolTip
				});
				 
				 if(tabIndex){
					 divNode.tabIndex =tabIndex;
				 }
				 
				} else {
					 divNode = f_core.CreateElement(div, "div",{
						className : style,
						cssTop : top + "px",
						cssLeft : left + "px",
						cssWidth : width + "px",
						cssHeight : height + "px",
						title : period._toolTip
					 });
				}
				
				var divNode2 = f_core.CreateElement(divNode, "div", {
					className : f_scheduler._PERIOD_STYLE+"_div_lab",
					cssWidth : width + "px",
					cssHeight : height + "px"
					
				});
				
				period._divNode = divNode;
				var type = period._periodType;
				if (!type ){
					type ="";
				}
				
				var ariaLabel = f_dateFormat.FormatDate(begin,"EEEE dd MMM yyyy")
					+ " "+ f_resourceBundle.Get(f_scheduler).f_get("FROM") + " "
					+ begin.getHours()+"h" +begin.getMinutes()
					+ " " +f_resourceBundle.Get(f_scheduler).f_get("TO") + " "
					+end.getHours()+"h" +end.getMinutes()+" "+type ;
				var labelNode = f_core.CreateElement(divNode2, "label", {
					textnode : period._label,
					cssWidth : width + "px",
					cssHeight : height + "px",
					className : period._periodStyle+"_label"

				});
				fa_aria.SetElementAriaLabel(labelNode,ariaLabel);
				period._labelNode = labelNode;
				divNode._period = period;
				divNode._scheduler = this;
				divNode.onclick = f_scheduler._OnPeriodMouseDown;
				divNode.onmouseover=f_scheduler._OnPeriodMouseOver;
				divNode.onfocus =f_scheduler._OnPeriodMouseOver;
				divNode.onmouseout=f_scheduler._OnPeriodMouseOut;
				divNode.onblur=f_scheduler._OnPeriodMouseOut;
			}
		}
		return this.f_super(arguments);
	},
	
	
	/**
	 * @method public
	 * @param Object period
	 * @return Date
	 */
	f_getItemDateBegin: function(period) {
		return   period._begin;
	},
	
	/**
	 * @method public
	 * @param Object period 
	 * @return Date
	 */
	f_getItemDateEnd: function(period) {
		return period._end;
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	fa_showElement: function(divNode){
	
	},
	
	/**
	 * @method protected
	 * @return Boolean
	 */
	
	fa_isElementDisabled: function(divNode) {
		return false;
	},
	
	
	/**
	 * @method protected
	 * @return void
	 */
	fa_updateElementStyle: function(divNode) {
		var period = divNode._period;
		var style = f_scheduler._PERIOD_STYLE;
		var periodStyle=undefined;
		if (period._periodStyle){
			periodStyle  = period._periodStyle;
		}
		if (periodStyle) {
			style += " "+periodStyle;
		}
		if(divNode._hover){
			style += " "+f_scheduler._PERIOD_STYLE+"_over"; 
			if (periodStyle){
				style += " "+periodStyle+"_over";
			}
		}
		
		if (divNode.className!=style) {
			divNode.className=style;
		}
	},
	
	/**
	 * @method protected
	 * @return Object item
	 */
	fa_getElementItem: function(divNode) {
		return divNode._period;
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	fa_updateItemStyle: function(period) {
		this.fa_updateElmentStyle(period._divNode);
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	fa_destroyItems : function(items) {
		for(var i=0;i<items.length;i++) {
			var item=items[i];
			this.f_destroyItem(item);
		}
	},	
	/**
	 * @method protected
	 * @param Object item
	 * @return void
	 */
	 f_destroyItem: function(item) {
		item._labelNode= undefined;

		var component=item._divNode;
		if (component) {
			item._divNode=undefined;
			component._period = undefined;
			component._scheduler = undefined;

			component.onmouseover=null;		
			component.onmouseout=null;
			component.onclick=null;
			component.onfocus=null;
			component.onblur=null;
			component.onkeypress=null;
		
			f_core.VerifyProperties(component);
		}
	},
	
	fa_isElementSelected:  function(divNode) {
		return divNode._selected;
	},
	
	/**
	 * @method protected
	 * @param any divNode
	 * @param Boolean selected
	 * @return void 
	 */
	fa_setElementSelected:  function(divNode, selected) {
		divNode._selected = selected;
		//fa_aria.SetElementAriaSelected(divNode, selected);
	},
	
	/**
	 * @method protected
	 * @param any divNode
	 * @return Object 
	 */
	fa_getElementValue: function(divNode) {
		return divNode._period._value;
	},
	
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			return;
		}
		
		this.f_super(arguments, type, target);
	},
	
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION: 
			return;
		}
		
		this.f_super(arguments, type, target);
	}
};

new f_class("f_scheduler", {
	extend : f_component,
	aspects : [ fa_items, fa_selectionManager ],
	statics : __statics,
	members : __members
});
