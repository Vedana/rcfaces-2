/*
 * $Id: f_compositeNumEntry.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * 
 * @class public f_compositeNumEntry extends f_component, fa_compositeNumEntry, fa_required, fa_message, fa_clientValidatorParameters
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __members={

	f_compositeNumEntry: function() {
		this.f_super(arguments);
		
		f_core.AddCheckListener(this, this);	
	},
	f_finalize: function() {
		f_core.RemoveCheckListener(this, this);
					
		this.f_super(arguments);
	},
	/**
	 * @method hidden
	 * @param f_event event
	 * @return Boolean 
	 */
	f_performCheckValue: function(event) {
		return true;		
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateStyleClass: function(postSuffix) {
		var suffix="";

		if (this.f_isDisabled()) {
			suffix+="_disabled";

		} else if (this.f_isReadOnly()) {
			suffix+="_readOnly";
		}
		
		if (postSuffix) {
			suffix+=postSuffix;
		}
	
		var claz=this.f_computeStyleClass(suffix);
		if (this.className!=claz) {
			this.className=claz;
		}
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_computeStyleClass: function(suffix) {	
		var styleClass=this.f_super(arguments, suffix);

		if (this._hasFocus && !this.f_isDisabled()) {
			var focusStyleClass=this.f_getFocusStyleClass();

			if (focusStyleClass) {
				styleClass+=" "+focusStyleClass;
			}
		}
		
		var msg=this._currentMessage;
		if (msg) {
			var severity=msg.f_getSeverity();
			
			var severityClass=this.f_getStyleClassFromSeverity(severity);
			
			if (!severityClass && severity>=f_messageObject.SEVERITY_ERROR) {
				severityClass=this.f_getMainStyleClass()+"_error";
			}
			
			if (severityClass) {
				styleClass+=" "+severityClass;
			}
		}

		return styleClass;
	},
	f_performMessageChanges: function() {	
		var messages=f_messageContext.ListMessages(this);
		
		var msg;
		for(var j=0;j<messages.length;j++) {
			var m=messages[j];
			
			if (!msg || msg.f_getSeverity()<m.f_getSeverity()) {
				msg=m;
			}
		}
		
		f_core.Debug(f_compositeNumEntry, "f_performMessageChanges: Change message to '"+msg+"' for component "+this.id+".");
						
		if (msg==this._currentMessage) {
			return;
		}
		
		f_core.Assert(typeof(msg)=="object" || msg===undefined, "f_compositeNumEntry.f_performMessageChanges: Invalid message object ("+msg+").");
		this._currentMessage=msg;
		
		this.f_updateStyleClass();
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_addErrorMessage: function(clazz, errorMessage) {
		var summary=null;
		var detail=null;
		var severity=f_messageObject.SEVERITY_ERROR;
		
		var summary=this.f_getClientValidatorParameter(errorMessage+".summary");
		if (summary) {
			detail=this.f_getClientValidatorParameter(errorMessage+".detail");
		} else {
			summary=this.f_getClientValidatorParameter(errorMessage);
		}			
		
		if (!summary) {
			var resourceBundle=f_resourceBundle.Get(clazz);
			
			summary=resourceBundle.f_get(errorMessage.toUpperCase().replace(/\./g, "_")+"_SUMMARY");
		}

		var messageContext=f_messageContext.Get(this);

		messageContext.f_addMessage(this, severity, summary, detail);
	},
	
	f_update: function() {
		this.f_performMessageChanges();	
				
		return this.f_super(arguments);	
	}
};
 
new f_class("f_compositeNumEntry", {
	extend: f_component,
	aspects: [ fa_compositeNumEntry, fa_required, fa_message, fa_clientValidatorParameters ],
	members: __members
});
