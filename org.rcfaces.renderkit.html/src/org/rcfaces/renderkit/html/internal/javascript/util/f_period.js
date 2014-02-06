/*
 * $Id: f_period.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_period class
 *
 * @class public final f_period extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) & Joel Merlin
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {
	/**
	 * @method hidden static
	 * @param f_period period
	 * @return String
	 */
	Serialize: function(period) {
		var start=period.f_getStart();
		var end=period.f_getEnd();

		var s=f_core.SerializeDate(start);
		
		if (end && end.getTime()!=start.getTime()) {
			s+=":"+f_core.SerializeDate(end);
		}
		
		return s;
	},

	/**
	 * @method hidden static
	 * @param String period
	 * @return f_period
	 */
	Deserialize: function(value) {
		var idx=value.indexOf(':');
		if (idx<0) {
			var start=f_core.DeserializeDate(value);
			
			return new f_period(start, start);
		}
		
		var start=f_core.DeserializeDate(value.substring(0, idx));
		var end=f_core.DeserializeDate(value.substring(idx+1));
	
		return new f_period(start, end);
	}
}

/**
 * @method public
 */
var __members = {
 
	f_period: function(start, end) {
		if ((start instanceof Array) && start.length==2 && arguments.length==1) {
			end=start[1];
			start=start[0];
		}
		
		f_core.Assert(start instanceof Date, "f_period.f_period: Invalid start parameter ("+start+")");
		f_core.Assert((end instanceof Date) || (end===undefined) || (end===null), "f_period.f_period: Invalid end parameter ("+end+")");

		this._start=start;
		this._end=end;
	},
	
	/**
	 * @method public
	 * @return Date
	 */
	f_getStart: function() {
		return this._start;
	},
	
	/**
	 * @method public
	 * @return Date
	 */
	f_getEnd: function() {
		return this._end;
	},
	 
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		if (!this._end) {
			return "[f_period start=end="+this._start+"]";
		}
		return "[f_period start="+this._start+" end="+this._end+"]";
	}
}

new f_class("f_period", null, __statics, __members);
