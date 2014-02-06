/*
 * $Id: fa_message1.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Aspect Message1
 *
 * @aspect fa_message1 extends fa_messageText
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */

var __members = {

	/*
	fa_message1: function() {
	},
	*/
	
	f_finalize: function() {
		// this._fors=undefined; // string[]
		// this._forsTranslated=undefined; // String[]
		
		this._currentMessage=undefined; // f_messageObject
 	},
	f_update: {
		after: function() {
			this.f_performMessageChanges();
		}
	},
	/**
	 * @method public 
	 * @return String Return an identifier (naming separator might not be ':')
	 */
	f_getFor: function() {
		var fors=this.f_getForComponentIds();
		
		return fors[0];
	},
	/**
	 * @method public 
	 * @return String Returns an array of identifiers (naming separator might not be ':')
	 */
	f_getForComponentIds: function() {
		var fors=this._fors;
		if (fors!==undefined) {
			return fors;
		}
		
		var s=f_core.GetAttributeNS(this,"for");
		
		fors=new Array();
		this._fors=fors;
		
		var keys=new Array();
		this._forsTranslated=keys;
		
		if (!s) {
			return fors;
		}
		
		var sd=s.split(",");
		for(var i=0;i<sd.length;i++) {
			var f=f_core.Trim(sd[i]);
			if (!f.length) {
				continue;
			}
			fors.push(f);

			var forTranslated=fa_namingContainer.ComputeComponentId(this, f);					

			f_core.Assert(forTranslated, "fa_message1.f_getForComponents: Component '"+f+"' associated to this message is not defined !");
			keys.push(forTranslated);

			f_core.Debug(fa_message1, "f_getForComponentIds: translate '"+f+"' to '"+forTranslated+"'.");
		}
		
		return fors;
	},
	/**
	 * @method private 
	 * @return String
	 */
	_getForTranslatedComponentIds: function() {
		this.f_getForComponentIds();
		
		return this._forsTranslated;
	},
	f_performMessageChanges: function() {

		var keys=this._getForTranslatedComponentIds();
		
		if (!keys.length) {
			keys=[null]; // On prend les globaux
		}

		f_core.Debug(fa_message1, "f_performMessageChanges: Search keys: nb="+keys.length+" keys");

		var msg;
		for(var i=0;i<keys.length;i++) {
			var key=keys[i];

			f_core.Debug(fa_message1, "f_performMessageChanges: key#"+i+"="+key);
			
			var messages;
			// Fred : (key===null) pas suffisant : key peut etre "" sous IE en tout cas
			if (!key) {
				messages=f_messageContext.Get(this).f_listMessages(null);
			
				f_core.Debug(fa_message1, "f_performMessageChanges: Get messages for key=null : "+ messages);
				
			} else {
				var component=f_core.GetElementByClientId(key);
				if (!component) {
					f_core.Debug(fa_message1, "f_performMessageChanges: Can not get component associated to key '"+key+"'.");
				
					continue;
				}
			
				messages=f_messageContext.ListMessages(component);

				f_core.Debug(fa_message1, "f_performMessageChanges: Messages associated to component: '"+key+"': "+messages);
			}
			
			for(var j=0;j<messages.length;j++) {
				var m=messages[j];
				
				if (!msg || msg.f_getSeverity()<m.f_getSeverity()) {
					msg=m;
				}
			}
		}
						
		if (msg==this._currentMessage) {
			f_core.Debug(fa_message1, "f_performMessageChanges: no modifications ("+msg+")");
			return;
		}

		f_core.Assert(typeof(msg)=="object" || msg===undefined, "fa_message1.f_performMessageChanges: Invalid message object ("+msg+").");

		f_core.Debug(fa_message1, "f_performMessageChanges: change message to "+msg);
		
		this._currentMessage=msg;
		
		this.fa_updateMessages();
	}
};

new f_aspect("fa_message1", {
	extend: [ fa_messageText ],
	members: __members
});
