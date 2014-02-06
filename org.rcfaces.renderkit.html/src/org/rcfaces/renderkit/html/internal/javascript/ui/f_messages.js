/*
 * $Id: f_messages.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * @class public f_messages extends f_component, fa_messageText
 */
 
var __statics = {
	/**
	 * @method private static
	 */
	_IsEquals: function(array1, array2) {
		
		if (array1===undefined) {
			return (array2===array1);
		}
		
		if (!(array1 instanceof Array) 
				|| !(array2 instanceof Array) 
				|| (array1.length!=array2.length)) {
			return false;
		}
		
		for(var i=0;i<array1.length;i++) {
			if (array1[i]!=array2[i]) {
				return false;
			}
		}
		
		return true;
	}
};

var __members = {
/*
	f_messages: function() {
		this.f_super(arguments);
	},
	*/
	f_finalize: function() {
//		this._globalOnly=undefined;  // boolean
		this._currentMessages=undefined; // object[]
		this._tbody=undefined; // HTMLElement

		this.f_super(arguments);
	},
	
	f_update: function() {
		var messageContext=f_messageContext.Get(this);

		this._currentMessages=messageContext.f_listMessages(null, this.f_isGlobalOnly());

		this.fa_updateMessages();		
				
		return this.f_super(arguments);	
	},
	/**
	 * @method public 
	 * @return f_component
	 */
	f_isGlobalOnly: function() {
		if (this._globalOnly!==undefined) {
			return this._globalOnly;
		}
		
		var b=f_core.GetBooleanAttributeNS(this,"globalOnly", false);
		this._globalOnly=b;
		return b;
	},
	f_performMessageChanges: function(messageContext) {	
		var messages=messageContext.f_listMessages(null, this.f_isGlobalOnly());
		
		if (f_messages._IsEquals(messages, this._currentMessages)) {
			f_core.Debug(f_messages, "f_performMessageChanges: Messages change events: no changes !");
			return;
		}
		
		f_core.Debug(f_messages, "f_performMessageChanges: Messages change events: update "+(messages?messages.length:0)+" messages !");
		
		if (!messages) {
			this._currentMessages=null;
			
		} else {
			var currentMessages=new Array;
			currentMessages.push.apply(currentMessages, messages);
			
			this._currentMessages=currentMessages;
		}

		this.fa_updateMessages();
	},
	fa_updateMessages: function() {
		var messages=this._currentMessages;

		var tbody=this._tbody;
		if (!tbody) {
			if (!messages) {
				f_core.Debug(f_messages, "fa_updateMessages: No body and no messages to update !");
				return;
			}

			tbody=document.createElement("tbody");

		} else  {
			while (tbody.hasChildNodes()) {
				tbody.removeChild(tbody.lastChild);
			}	

			if (!messages) {
				f_core.Debug(f_messages, "fa_updateMessages: Body cleared and no messages to update !");
				return;
			}
		}
	
		f_core.Debug(f_messages, "fa_updateMessages: Update "+messages.length+" messages.");
		
		var messageLength = messages.length;
		
		var maxCount=f_core.GetNumberAttributeNS(this,"maxCount", 0);
		if (maxCount>0 && messageLength>maxCount) {
			messageLength=maxCount;
		}
		
		for(var i=0;i<messageLength;i++) {
			var message=messages[i];
			
			f_core.Assert(message, "f_messages.fa_updateMessages: Null message into list of message !");
			
			var styleMessage=this.f_getStyleClassFromSeverity(message.f_getSeverity());
			
			var tr=document.createElement("tr");
			f_core.AppendChild(tbody, tr);
			
			var summaryLabel=null;
			if (this.f_isShowSummary()) {
				summaryLabel=document.createElement("td");
				f_core.AppendChild(tr, summaryLabel);
			}
			
			var detailLabel=null;
			if (this.f_isShowDetail()) {		
				detailLabel=document.createElement("td");
				f_core.AppendChild(tr, detailLabel);
			}
			
			f_message.FillComponent(this.f_computeStyleClass(), tr, null, summaryLabel, detailLabel, message, styleMessage);			
		}
		
		if (!this._tbody) {
			this._tbody=tbody;
			f_core.AppendChild(this, tbody);
		}
	}
};

new f_class("f_messages", null, __statics, __members, f_component, fa_messageText);
