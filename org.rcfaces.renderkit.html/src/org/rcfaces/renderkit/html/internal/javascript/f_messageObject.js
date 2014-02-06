/*
 * $Id: f_messageObject.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * <p><strong>f_messageObject</strong> represents a single validation (or
 * other) message, which is typically associated with a particular
 * component in the view.
 *
 * @class public final f_messageObject extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
var __statics = {
	/**
	 * <p>Message severity level indicating an informational message
     * rather than an error.</p>
     *
	 * @field public static final Number
	 */
   	SEVERITY_INFO:  0,

 	/**
 	 * <p>Message severity level indicating that an error might have
     * occurred.</p>
     *
	 * @field public static final Number
	 */
    SEVERITY_WARN:  1,

	/**
	 * <p>Message severity level indicating that an error has
     * occurred.</p>
     *
	 * @field public static final Number
	 */
    SEVERITY_ERROR: 2,

	/**
	 * <p>Message severity level indicating that a serious error has
     * occurred.</p>
     *
	 * @field public static final Number
	 */
    SEVERITY_FATAL: 3,
    
    /**
     * @method hidden static
     * @context document:tag.ownerDocument
     */     
    CreateFromTag: function(tag) {
    	f_core.Assert(tag && tag.nodeType==f_core.ELEMENT_NODE, "f_messageObject.CreateFromTag: Invalid tag parameter '"+tag+"'.");
    	
		var detail=f_core.GetAttributeNS(tag,"detail");
		var summary=f_core.GetAttributeNS(tag,"summary");		
		var severity=f_core.GetNumberAttributeNS(tag,"severity");	
		
		var clientDatas=f_core.ParseDataAttribute(tag);
		
		var component=null;
		
		var forClientId=f_core.GetAttributeNS(tag,"for");			
		if (forClientId) {
			component=f_core.GetElementById(forClientId);
		}
		
		var messageObject = new f_messageObject(severity, summary, detail, clientDatas);
		
		f_messageContext.Get(component).f_addMessageObject(component, messageObject, true);
    }
};

var __members = {
	
	/**
	 * @field private Number
	 */
	_severity: undefined,
	
	/**
	 * @field private String
	 */
	_summary: undefined,
	
	/**
	 * @field private String
	 */
	_detail: undefined,
	
	/**
	 * @field private Object
	 */
	_clientDatas: undefined,
	
	/**
	 * <p>Construct a new <code>f_messageObject</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param optional Number severity the severity
	 * @param optional String summary Localized summary message text
	 * @param optional String detail Localized detail message text
	 * @param optional Object clientDatas Client datas.
	 */
	f_messageObject: function(severity, summary, detail, clientDatas) {
	//	f_core.Assert(typeof(severity)=="number", "Bad type of severity");
	//	f_core.Assert(summary, "Bad summary"); // Summary can be null
		f_core.Assert(summary===undefined || summary===null || typeof(summary)=="string", "f_messageObject.f_messageObject: Invalid summary parameter '"+summary+"'");
		f_core.Assert(detail===undefined || detail===null || typeof(detail)=="string", "f_messageObject.f_messageObject: Invalid detail parameter '"+summary+"'");
	
		if (typeof(severity)=="object" && severity) {
			var properties=severity;
			
			summary=properties._summary;
			detail=properties._detail;
			severity=properties._severity;
			clientDatas=properties._clientDatas;	
		}
	
		if (typeof(severity)!="number") {
			severity=f_messageObject.SEVERITY_INFO;
		}
	
		this._severity=severity;
		this._summary=summary;
		this._detail=detail;
		this._clientDatas=clientDatas;
	},

/*
	f_finalize: function() {
		this._severity=undefined; // number
		this._summary=undefined; // String
		this._detail=undefined; // String
		this._clientDatas=undefined; // Map<String,String>
	},
*/
	
	/**
	 *  <p>Return the severity level.</p>
	 *
	 * @method public 
	 * @return Number Severity level
	 * @see #SEVERITY_INFO
	 * @see #SEVERITY_WARN
	 * @see #SEVERITY_ERROR
	 * @see #SEVERITY_FATAL
	 */
	f_getSeverity: function() {
		return this._severity;
	},
	
	/**
	 * <p>Return the localized summary text.</p>
	 *
	 * @method public 
	 * @return String The summary
	 */
	f_getSummary: function() {
		return this._summary;
	},
	
	/**
	 * <p>Return the localized detail text.  If no localized detail text has
     * been defined for this message, return the localized summary text
     * instead.</p>
     *
	 * @method public 
	 * @return String Detail
	 */
	f_getDetail: function() {
		var detail=this._detail;
		if (detail) {
			return detail;
		}
		
		return this._summary;
	},
	/**
	 * 
	 * @method public
	 * @param String name Name of property
	 * @return String
	 */
	f_getClientData: function(name) {
		var clientDatas=this._clientDatas;
		if (!clientDatas) {
			return null;
		}
		
		return clientDatas[name];
	},
	/**
	 * @method public
	 * @param f_messageObject message object
	 * @return Boolean
	 */
	f_equals: function(message) {
		if (this==message) {
			return true;
		}
		if (!message || 
				message._severity!=this._severity || 
				message._detail!=this._detail || 
				message._summary!=this._summary || 
				message._clientDatas!=this._clientDatas) {
			return false;
		}
		
		return true;
	},
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_messageObject severity='"+this._severity+"' summary='"+this._summary+"' detail='"+this._detail+"']";
	}
};

new f_class("f_messageObject", {
	statics: __statics,
	members: __members
});
