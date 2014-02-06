/*
 * $Id: f_numberEntry.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 * @class public f_numberEntry extends f_compositeNumEntry
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */

var __members={

	f_numberEntry: function() {
		this.f_super(arguments);
		
		var fractionInput=this.fa_getInputByType("D");
		if (fractionInput) {
			fractionInput._fillRight=true;
		}
	},
	/*
	f_finalize: function() {
		// this._timeFormat=undefined; // String
		// this._minTime=undefined; // number
		// this._maxTime=undefined; // number
				
		this.f_super(arguments);
	},
	*/
	fa_initializeInput: function(input) {
		input._forced=f_core.GetNumberAttributeNS(input,"auto");
		if (input._forced<1) {
			input._forced=1;
		}
	},
	/**
	 * @method public
	 * @param optional hidden number numberType Type of number. (min, max, default)
	 * @return Number
	 */
	f_getNumber: function(numberType) {
		var raddix=-1;
		var decimal=0;
		
		var inputs=this._inputs;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			var type=input._type;
			
			var min=input._min;
			var max=input._max;
			
			var v=undefined;
			switch(numberType) {
				
			case fa_compositeEntry.DEFAULT_TYPE:
				v=input._default;
				break;

			default:
				v=parseInt(input.value, 10);
				if (isNaN(v) || (min!==undefined && v<min) || (max!==undefined && v>max)) {
					v=undefined;
				}
			}
						
			switch(type) {
			case "I":
				raddix=v
				break;
				
			case "D":
				decimal=input.value; // Il faut conserver les 0 au début !
				break;
			}
		}
		
		if (raddix===undefined) {
			return null;
		}
		
		var t=raddix;
		if (decimal) {
			t=parseFloat(t+"."+decimal);
		} else {
			t=parseInt(t, 10);
		}
		
		return t;
	},
	/**
	 * @method public
	 * @param Number num
	 * @return void
	 */
	f_setNumber: function(num) {
		f_core.Assert(typeof(num)!="number", "f_numberEntry.f_setNumber: Invalid number parameter ("+num+").");
		
		var inputs=this._inputs;
		for(var i=0;i<inputs.length;i++) {
			var input=inputs[i];
			var type=input._type;
			var maxLength=parseInt(input.maxLength, 10);
			
			var v=-1;
			switch(type) {
			case "I":
				v=Math.floor(num);
				break;
				
			case "F":
				v=num-Math.floor(num);
				if (v!=0) {
					
				}

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
	 * @return Number
	 */
	f_getMinNumber: function() {
		var minNumber=this._minNumber;
		if (minNumber!==undefined) {
			return minNumber;
		}
		
		minNumber=f_core.GetAttributeNS(this,"minNumber");
		if (minNumber) {
			if (minNumber.indexOf('.')>=0) {
				minNumber=parseFloat(minNumber);
			} else {
				minNumber=parseInt(minNumber, 10);
			}
		} else {
			minNumber=null;
		}
		
		this._minNumber=minNumber;

		return minNumber;
	},

	/**
	 * @method public
	 * @return Number
	 */
	f_getMaxNumber: function() {
		var maxNumber=this._maxNumber;
		if (maxNumber!==undefined) {
			return maxNumber;
		}
		
		maxNumber=f_core.GetAttributeNS(this,"maxNumber");
		if (maxNumber) {
			if (maxNumber.indexOf('.')>=0) {
				maxNumber=parseFloat(maxNumber);
			} else {
				maxNumber=parseInt(maxNumber, 10);
			}
		} else {
			maxNumber=null;
		}
		
		this._maxNumber=maxNumber;

		return maxNumber;
	},
	f_serialize: function() {
		var number=this.f_getNumber();
		
		this.f_setProperty(f_prop.VALUE, number);

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
			
		var number=this.f_getNumber();
		/*
		f_core.Debug(f_numberEntry, "Time: "+date);
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
					return;
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
			
			if (d2.f_getHours()!=date.f_getHours() || 
					d2.f_getMinutes()!=date.f_getMinutes() || 
					d2.f_getSeconds()!=date.f_getSeconds() ||
					d2.f_getMilliseconds()!=date.f_getMilliseconds()) {
					
//					alert("Different ! "+d2.f_getHours()+"/"+date.f_getHours());
				errorMessage="invalidTime.error";
				
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
		*/
		
		f_core.Debug(f_numberEntry, "Error Message: "+errorMessage+" number="+number);
		
		if (!errorMessage) {
			return true;
		}
		
		this.f_addErrorMessage(f_numberEntry, errorMessage);
		
		return false;
	},
	/**
	 * @method protected
	 */
	fa_formatNumber: function(input, number, size) {
		var s=String(number);
		
		var sizeLeft=input._forced-s.length;
		if (sizeLeft<1) {
			return s;
		}
		
		if (input._fillRight) {
			for(;sizeLeft;sizeLeft--) {
				s+="0";
			}
			return s;
		}
		
		for(;sizeLeft;sizeLeft--) {
			s="0"+s;
		}
		return s;
	}
}
 
new f_class("f_numberEntry", {
	extend: f_compositeNumEntry,
	members: __members
});
