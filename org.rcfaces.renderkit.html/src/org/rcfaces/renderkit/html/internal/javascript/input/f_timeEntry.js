/*
 * $Id: f_timeEntry.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 * @class public f_timeEntry extends f_compositeNumEntry
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __members={

	f_timeEntry: function() {
		this.f_super(arguments);

//		this._timeFormat=f_core.GetAttributeNS(this,"timeFormat");
	},
	/*
	f_finalize: function() {
		// this._timeFormat=undefined; // String
		// this._minTime=undefined; // f_time
		// this._maxTime=undefined; // f_time
				
		this.f_super(arguments);
	},
	*/

	/**
	 * @method public
	 * @param optional hidden Number timeType Type of time. (min, max, default)
	 * @return f_time
	 */
	f_getTime: function(timeType) {
		var hour=-1;
		var minute=0;
		var second=0;
		var millis=0;
		
		var inputs=this._inputs;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			var type=input._type;
			
			var min=input._min;
			var max=input._max;
			
			var v;
			switch(timeType) {
				
			case fa_compositeEntry.DEFAULT_TYPE:
				v=input._default;
				break;

			default:
				v=parseInt(input.value, 10);
				if (isNaN(v) || (min!==undefined && v<min) || (max!==undefined && v>max)) {
					v=-1;
				}
			}
			if (v===undefined) {
				v=-1;
			}
						
			switch(type) {
			case "H":
				hour=v
				break;
				
			case "m":
				minute=v;
				break;
				
			case "s":
				second=v;
				break;

			case "S":
				millis=v;
				break;
			}
		}
		
		if (hour<0) {
			return null;
		}
		
		var t=new f_time(hour, minute, second, millis);
		
		if (t.f_getHours()!=hour || 
				t.f_getMinutes()!=minute || 
				t.f_getSeconds()!=second || 
				t.f_getMilliseconds()!=millis) {
			return null;
		}
		
		return t;
	},
	/**
	 * @method public
	 * @param f_time time
	 * @return void
	 */
	f_setTime: function(time) {
		f_core.Assert(time instanceof f_time, "f_timeEntry.f_setTime: Invalid time parameter ("+time+").");
		
		var inputs=this._inputs;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			var type=input._type;
			var maxLength=parseInt(input.maxLength, 10);
			
			var v=-1;
			switch(type) {
			case "H":
				v=time.f_getHours();
				break;
				
			case "m":
				v=time.f_getMinutes();
				break;
				
			case "s":
				v=time.f_getSeconds();
				break;

			case "S":
				v=m.f_getMilliseconds();	
				break;
			}
			if (v<0) {
				continue;
			}
			
			v=this.fa_formatNumber(input, v, maxLength);
			if (v!=input.value) {
				input.value=v;
			}
		}
	},
	/**
	 * @method public
	 * @return f_time
	 */
	f_getMinTime: function() {
		var minTime=this._minTime;
		if (minTime!==undefined) {
			return minTime;
		}
		
		minTime=f_core.GetAttributeNS(this,"minTime");
		if (minTime) {
			minTime=new f_time(parseInt(minTime, 10));
		} else {
			minTime=null;
		}
		
		this._minTime=minTime;

		return minTime;
	},

	/**
	 * @method public
	 * @return f_time
	 */
	f_getMaxTime: function() {
		var maxTime=this._maxTime;
		if (maxTime!==undefined) {
			return maxTime;
		}
		
		maxTime=f_core.GetAttributeNS(this,"maxTime");
		if (maxTime) {
			maxTime=new f_time(parseInt(maxTime, 10));
		} else {
			maxTime=null;
		}
		
		this._maxTime=maxTime;

		return maxTime;
	},
	f_serialize: function() {
		var time=this.f_getTime();
		
		this.f_setProperty(f_prop.VALUE, time);

		this.f_super(arguments);
	},
	/**
	 * @method hidden
	 * @param f_event event
	 * @return Boolean 
	 */
	f_performCheckValue: function(event) {		
		var messageContext=f_messageContext.Get(this);
		if (!messageContext) {
			return true;
		}

		var errorMessage=null;
			
		var date=this.f_getTime();
		
		f_core.Debug(f_timeEntry, "Time: "+date);
		if (!date) {
			if (!this.f_isRequired()) {
				// Si c'est pas requis, on ne rale que si un des champs est rempli
				var empty=true;
				var inputs=this._inputs;
				for(var i=0;i<inputs.length;i++) {
					if (inputs[i].value.length<1) {
						continue;
					}
					
					empty=false;
					break;
				}
				
				if (empty) {
					// Tous les champs sont vides
					return true;
				}

				errorMessage="invalidTime.error";
				
			} else {
				errorMessage="required.error";
			}
			
			// Le champ n'est pas requis, mais un des champs n'est pas vide !
			// ou le champ est requis et la date est invalide 

		} else {
			// La date est valide ! ?
			var t=date.f_getTime();
			
			var d2=new f_time(t);
			
			if (d2.f_getHours()!=date.f_getHours()) {
				errorMessage="invalidTime.error";
			
			} else if (d2.f_getMinutes()!=date.f_getMinutes()) {
				errorMessage="invalidMinute.error";
		
			} else if (d2.f_getSeconds()!=date.f_getSeconds()) {
				errorMessage="invalidSecond.error";
			
			} else if (d2.f_getMilliseconds()!=date.f_getMilliseconds()) {
					
//					alert("Different ! "+d2.f_getHours()+"/"+date.f_getHours());
				errorMessage="invalideMillisecond.error";
				
			} else {			
				var minTime=this.f_getMinTime();
				var maxTime=this.f_getMaxTime();
				
				if (minTime && t<minTime.f_getTime()) {
					errorMessage="minTime.error";
	
				} else if (maxTime && t>maxTime.f_getTime()) {
					errorMessage="maxTime.error";
				}

				f_core.Debug(f_timeEntry, "Test Min/max : Error Message: "+errorMessage+" time="+date+" timeMin="+minTime+" timeMax="+maxTime);
			}
		}
		
		f_core.Debug(f_timeEntry, "Error Message: "+errorMessage+" date="+date);
		
		if (!errorMessage) {
			return true;
		}
		
		this.f_addErrorMessage(f_timeEntry, errorMessage);
	
		return true;
	}
}
 
new f_class("f_timeEntry", {
	extend: f_compositeNumEntry,
	members: __members
});
