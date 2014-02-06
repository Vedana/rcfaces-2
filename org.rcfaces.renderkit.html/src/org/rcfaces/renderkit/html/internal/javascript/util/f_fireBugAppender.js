/*
 * $Id: f_fireBugAppender.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_fireBug appender
 *
 * @class hidden f_fireBugAppender extends f_object, fa_abstractAppender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics={
	
	/**
	 * @method private static
	 * @return void
	 */
	_Profile: function() {
	},
	
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {	
	
		try {
			if (!window.console || !window.console.log) {
				return;
			}
		} catch (x) {
			return;
		}
			
 		// this est la classe !
		this.f_newInstance();

		if (!window.rcfacesProfilerCB) {
			f_core.SetProfilerMode(f_fireBugAppender._Profile);
		}
		
		f_fireBugAppender._oldAssert=f_core.Assert;
		//f_core.Assert=window.console.assert;
	},
	/**
	 * @method public static 
	 * @param Boolean test
	 * @param String message
	 * @return void
	 */
	Assert: function(test, message) {
		var console=window.console;
		if (console) {
			console.assert(test, message);
		
			if (!test) {
				console.trace();
			}
			return;
		}
		
		f_fireBugAppender._oldAssert.call(f_core, test, message);
	}	
};

var __members = {
	f_fireBugAppender: function() {
		this.f_super(arguments);
		
		f_log.AddAppenders(this);
	},
	/**
	 * @method public
	 */
	f_doAppend: function(event) {

		if (!window.console) {
			return;
		}

		var param=[];
		var message="";
	
		if (event.name ) {
			message+="%s:";
			param.push(event.name);
		}
		
		if (event.message) {
			message+=" %s";
			param.push(event.message);
		}
		
		if (event.exception) {
			message+=" %o";
			param.push(event.exception);
		}
		
		
		var method=console.debug;
		
		switch(event.level) {
		case f_log.FATAL:
			message="[FATAL] "+message;
		
		case f_log.ERROR:
			method=console.error;
			break;
			
		case f_log.WARN:
			method=console.warn;
			break;

		case f_log.INFO:
			method=console.info;
			break;

		case f_log.DEBUG:
			break;

		default: 
			message="[UNKNOWN LEVEL] "+message;
		}
		
		param.unshift(message);
		method.apply(console, param);
		
		if (event.exception) {
			if (console.exception) {
				console.exception("Exception of previous message", event.exception);
			} else {
				console.error("Exception of previous message", event.exception);
			}
		}
	}
};

new f_class("f_fireBugAppender", {
	extend: f_object,
	aspects: [ fa_abstractAppender ],
	statics: __statics,
	members: __members
});
