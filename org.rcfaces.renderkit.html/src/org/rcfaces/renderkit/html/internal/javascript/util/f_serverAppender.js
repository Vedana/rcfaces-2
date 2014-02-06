/* 
 * $Id: f_serverAppender.js,v 1.5 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_serverAppender
 *
 * @class hidden f_serverAppender extends f_object, fa_abstractAppender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics={
	/** 
	 * @field private static final Number
	 */
	_TIMER: 500,
	
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {	
 		// this est la classe !
		this.f_newInstance();
	}
}
var __members = {
	f_serverAppender: function() {
		this.f_super(arguments);
		
		this._running=true;
		
		f_log.AddAppenders(this);
	},
	f_finalize: function() {
		this._running=undefined;

		var timerId=this._timerId;
		if (timerId) {
			this._timerId=undefined;
			window.clearTimeout(timerId);
		}
		
		/* Trop tard !
		this._sendEvents();
		*/
		
		this._events=undefined;
	},
	/**
	 * @method public
	 */
	f_doAppend: function(event) {
	
		if (!this._running) {
			return;
		}
		
		var events=this._events;
		if (!events) {
			events=new Array;
			this._events=events;
		}
		
		events.push(event);
		
		this._prepareTimer();
	},
	_sendEvents: function() {
		this._timerId=undefined;
		
		var events=this._events;
		if (!events || events.length<1) {
			return;
		}
		
		this._events=new Array;

		var params=new Object;

		for(var i=0;i<events.length;i++) {
			var event=events[i];
			
			if (event.name) {
				params["name"+i]=event.name;
			}
			if (event.level) {
				params["level"+i]=event.level;
			}
			if (event.message) {
				params["message"+i]=event.message;
			}
			if (event.fileName) {
				params["fileName"+i]=event.fileName;
			}
			if (event.lineNumber) {
				params["lineNumber"+i]=event.lineNumber;
			}
			if (event.date) {
				params["date"+i]=event.date.getTime();
			}
			var ex=event.exception;
			if (ex) {
				if (typeof(ex)!="string") {
					ex=ex.toString();
				}
				
				params["exception"+i]=ex;
			}
		}

		try {
			this._sending=true;
			
			window._ignoreLog=true;
			
			var request=new f_httpRequest(document, null, null, null, true);
			
			var self=this;
			
			request.f_setListener({
				/**
				 * @method public
				 */
		 		onError: function() {
					self._sending=undefined;
		 			self._prepareTimer();
		 		},
				/**
				 * @method public
				 */
		 		onLoad: function() {			
					self._sending=undefined;
		 			self._prepareTimer();
		 		}			
			});
			
			request.f_setRequestHeader("X-Camelia", "log.append");
			if ( parseInt(_rcfaces_jsfVersion) >= 2) {
				// JSF 2.0
				request.f_setRequestHeader("Faces-Request", "partial/ajax");

				if (!params) {
					params={};
				}
				params["javax.faces.behavior.event"]= "log.append";
				params["javax.faces.source"]= this.id;
				params["javax.faces.partial.execute"]= this.id;
			}
			request.f_doFormRequest(params);
			
		} finally {
			window._ignoreLog=undefined;
		}
	},
	_prepareTimer: function() {
		var events=this._events;
		if (!events || 
				events.length<1 || 
				this._timerId!==undefined || 
				this._sending) {
			return;
		}
		
		var self=this;
		this._timerId=window.setTimeout(function() {
			self._sendEvents.call(self);
			
			self=null;
		}, f_serverAppender._TIMER);
	}
};

new f_class("f_serverAppender", {
	extend: f_object,
	aspects: [ fa_abstractAppender ],
	statics: __statics,
	members: __members
});