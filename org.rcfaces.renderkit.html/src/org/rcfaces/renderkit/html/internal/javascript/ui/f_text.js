/*
 * $Id: f_text.js,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */

/**
 * @class f_text extends f_component, fa_audioDescription, fa_message
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
var __members = {
		
	/**
	 * @field private String
	 */
	_forComponentId: undefined,

	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		this.fa_searchAudioDescription();
		
		return f_core.GetTextNode(this, true, true);
	},
	/**
	 * @method public
	 * @param String text The text.
	 * @return void
	 */
	f_setText: function(text) {
		f_core.Assert(typeof(text)=="string", "f_text.f_setText: Invalid text parameter ! ('"+text+"')");
		if (this.f_getText() == text) {
			return;
		}
		
		f_core.SetTextNode(this, text, this._accessKey);

		this.fa_updateAudioDescription();

		this.f_setProperty(f_prop.TEXT,text);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getValue: function() {
		return this.f_getText();
	},
	/**
	 * @method public
	 * @param String text The text.
	 * @return Boolean
	 */
	f_setValue: function(text) {
		if (typeof(text)=="string") {
			this.f_setText(text);
			
			return true;
		}
		
		return false;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getForClientId: function() {
		var forComponentId=this._forComponentId;
		if (forComponentId!==undefined) {
			return forComponentId;
		}
		
		forComponentId=f_core.GetAttributeNS(this, "for", null);
		if (!forComponentId) {
			forComponentId=f_core.GetAttribute(this, "for", null);
		}
		
		this._forComponentId=forComponentId;
		
		return forComponentId;
	},
	f_performMessageChanges: function(messageContext, messageEvent) {
		
		if (messageEvent) {
			switch(messageEvent.type) {
			case f_messageContext.POST_CHECK_EVENT_TYPE:
			case f_messageContext.POST_PENDINGS_EVENT_TYPE:
				break;
				
			default:
				return;
			}
		}
		
		var cid=this.f_getTargetMessageClientId();
		if (!cid) {
			return;
		}

		var messages=messageContext.f_listMessages(cid, false);
	
		var selectedMessage=null;
		if (messages) {			
			for(var j=0;j<messages.length;j++) {
				var message=messages[j];
				var severity=message.f_getSeverity();
	
				if (!selectedMessage || selectedMessage.severity<severity) {
					selectedMessage=message;
				}
			}
		}

    	var message = this._getAudioDescriptionMessage();

		if (!selectedMessage || !message) {
			this.fa_removeAudioDescription("error");
			return;
		}
		
    	message=message.replace("%s", selectedMessage.f_getSummary());
    	message=message.replace("%d", selectedMessage.f_getDetail());

		this.fa_setAudioDescription(message, "error");
	},
	/**
	 * @method protected
	 * @param message
	 * @return String
	 */
	_getAudioDescriptionMessage: function() {
		return f_resourceBundle.Get(f_text).f_get("ARIA_ERROR");
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_getTargetMessageClientId: function() {

		var cid=this.f_getForClientId();
		if (!cid) {
			return null;
		}
		var pid=cid.lastIndexOf("::");
		if (pid>0) {
			cid=cid.substring(0, pid);
		}

		return cid;
	}
};

new f_class("f_text", {
	extend: f_component,
	members: __members,
	aspects: [fa_audioDescription, fa_message]
});
