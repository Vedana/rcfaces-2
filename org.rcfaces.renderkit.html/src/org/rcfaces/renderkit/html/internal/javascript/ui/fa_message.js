/*
 * $Id: fa_message.js,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * Aspect Message
 *
 * @aspect public abstract fa_message
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */

var __members = {
	
	/**
	 * @field private f_messageContext
	 */
	_messageContext: undefined,
	
	fa_message: function() {
		var messageContext=f_messageContext.Get(this);
		this._messageContext=messageContext;
		
		messageContext.f_addMessageListener(this);
		
		var targetId=this.id;
		if (this.f_getTargetMessageClientId) {
			targetId=this.f_getTargetMessageClientId();
		}
		
		if (targetId && messageContext.f_containsMessagesFor(targetId)) {
			f_core.Debug(fa_message, "fa_message: message detected for component id='"+this.id+"'");

			this.f_performMessageChanges(messageContext);
		}
	},
	f_finalize: function() {
		var messageContext=this._messageContext;
		if (messageContext) {
			this._messageContext=undefined;
			
			messageContext.f_removeMessageListener(this);
		}		
	},
	
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_hasSeverityClassName: function() {

		var classSpecified=this._classSpecified;
		
		if (classSpecified!==undefined) {
			return classSpecified;
		}
		
		this._fatalStyleClass=f_core.GetAttributeNS(this,"fatalStyleClass");
		this._errorStyleClass=f_core.GetAttributeNS(this,"errorStyleClass");
		this._warnStyleClass=f_core.GetAttributeNS(this,"warnStyleClass");
		this._infoStyleClass=f_core.GetAttributeNS(this,"infoStyleClass");
		
		classSpecified=this._fatalStyleClass || this._errorStyleClass || this._warnStyleClass || this._infoStyleClass;
		this._classSpecified=classSpecified;

		if (classSpecified) {
			return true;
		}
		
		var styleClass=this.f_getMainStyleClass();
		this._fatalStyleClass=styleClass+"_fatal";
		this._errorStyleClass=styleClass+"_error";
		this._warnStyleClass=styleClass+"_warn";
		this._infoStyleClass=styleClass+"_info";
		
		styleClass=this.f_getStyleClass();
		if (styleClass) {
			this._fatalStyleClass+=" "+styleClass+"_fatal";
			this._errorStyleClass+=" "+styleClass+"_error";
			this._warnStyleClass+=" "+styleClass+"_warn";
			this._infoStyleClass+=" "+styleClass+"_info";
		}

		return false;
	},
	
	/*
	f_finalize: function() {
		// this._classSpecified=undefined; // boolean
		
		// this._fatalStyleClass=undefined;  // string
		// this._errorStyleClass=undefined; // string
		// this._warnStyleClass=undefined; // string
		// this._infoStyleClass=undefined; // string
	},
	*/

	/**
	 * @method public 
	 * @return String
	 */
	f_getFatalStyleClass: function() {
		this.f_hasSeverityClassName();

		return this._fatalStyleClass;
	},
	/**
	 * @method public 
	 * @return String
	 */
	f_getErrorStyleClass: function() {
		this.f_hasSeverityClassName();

		return this._errorStyleClass;
	},
	/**
	 * @method public 
	 * @return String
	 */
	f_getWarnStyleClass: function() {
		this.f_hasSeverityClassName();
		
		return this._warnStyleClass;
	},
	/**
	 * @method public 
	 * @return String
	 */
	f_getInfoStyleClass: function() {
		this.f_hasSeverityClassName();
		
		return this._infoStyleClass;
	},
	/**
	 * @method protected final
	 * @param Number severity
	 * @return String style class name.
	 */
	f_getStyleClassFromSeverity: function(severity) {
		f_core.Assert(typeof(severity)=="number", "Invalid severity parameter ('"+severity+"')");
		
		var className=null;
		
		switch(severity) {
		case f_messageObject.SEVERITY_FATAL:
			className=this.f_getFatalStyleClass();
			if (className) {
				return className;
			}

		case f_messageObject.SEVERITY_ERROR:
			className=this.f_getErrorStyleClass();
			if (className) {
				return className;
			}

		case f_messageObject.SEVERITY_WARN:
			className=this.f_getWarnStyleClass();
			if (className) {
				return className;
			}

		case f_messageObject.SEVERITY_INFO:
			className=this.f_getInfoStyleClass();
			if (className) {
				return className;
			}
		}
		
		return null;
	},
	
	/**
	 * @method protected abstract
	 * @param f_messageContext messageContext
	 * @return void
	 */
	f_performMessageChanges: f_class.ABSTRACT,
	
	
	/**
	 * @method protected optional abstract
	 * @return String
	 */
	f_getTargetMessageClientId: f_class.OPTIONAL_ABSTRACT
};

new f_aspect("fa_message", {
	members: __members
});
