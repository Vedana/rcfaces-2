/*
 * $Id: fa_audioDescription.js,v 1.1 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Aspect
 * 
 * @aspect public abstract fa_audioDescription
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:28 $
 */

var __statics = {
	/**
	 * @field private static final String
	 */
	_AUDIO_DESCRIPTION_CLASS_NAME : "f_audioDescription",
	/**
	 * @field private static final String
	 */
	_AUDIO_DESCRIPTION_ELEMENT : "span",

	/**
	 * @field private static final String
	 */
	_AUDIO_DESCRIPTION_TYPE_ATTRIBUTE : "audioDescriptionType",

	/**
	 * @field private static final String
	 */
	_AUDIO_DESCRIPTION_FOR_ATTRIBUTE : "audioDescriptionFor",

	/**
	 * @field private static final String
	 */
	_DEFAULT_TYPE : "-",

	/**
	 * @method hidden static
	 * @param Element
	 *            element
	 * @param String
	 *            message
	 * @param optional
	 *            String type
	 * @param optional
	 *            String forClientId
	 * @return Element
	 */
	SetAudioDescription : function(element, message, type, forClientId) {
		f_core.Assert(element && element.nodeType == f_core.ELEMENT_NODE,
				"fa_audioDescription.SetAudioDescription: Invalid element paremeter ('"
						+ element + "')");
		f_core.Assert(typeof (message) == "string",
				"fa_audioDescription.SetAudioDescription: Invalid message paremeter ('"
						+ message + "')");
		f_core.Assert(type === undefined || typeof (type) == "string",
				"fa_audioDescription.SearchAudioDescription: Invalid 'type' paremeter ('"
						+ type + "')");
		f_core
				.Assert(
						forClientId === undefined
								|| typeof (forClientId) == "string",
						"fa_audioDescription.SearchAudioDescription: Invalid 'forClientId' paremeter ('"
								+ forClientId + "')");

		var spans = fa_audioDescription.SearchAudioDescription(element, type,
				forClientId);
		if (spans && spans.length > 0) {
			f_core.SetTextNode(spans[0], message);
			return spans[0];
		}

		var ret = fa_audioDescription.AppendAudioDescription(element, message,
				type, forClientId);

		return ret;
	},
	/**
	 * @method hidden static
	 * @param Element
	 *            element
	 * @param optional
	 *            String type
	 * @param optional
	 *            String forClientId
	 * @return Element[]
	 */
	SearchAudioDescription : function(element, type, forClientId) {
		f_core.Assert(element && element.nodeType == f_core.ELEMENT_NODE,
				"fa_audioDescription.SearchAudioDescription: Invalid 'element' paremeter ('"
						+ element + "')");
		f_core.Assert(type === undefined || type === null ||  typeof (type) == "string",
				"fa_audioDescription.SearchAudioDescription: Invalid 'type' paremeter ('"
						+ type + "')");
		f_core
				.Assert(
						forClientId === undefined || forClientId === null
								|| typeof (forClientId) == "string",
						"fa_audioDescription.SearchAudioDescription: Invalid 'forClientId' paremeter ('"
								+ forClientId + "')");

		var ret = [];

		var spans = element
				.getElementsByTagName(fa_audioDescription._AUDIO_DESCRIPTION_ELEMENT);
		for ( var i = 0; i < spans.length; i++) {
			var span = spans[i];

			if (forClientId) {
				var cid = span
						.getAttribute(fa_audioDescription._AUDIO_DESCRIPTION_FOR_ATTRIBUTE);
				if (cid != forClientId) {
					continue;
				}
			}
			if (type) {
				var ctype = span
						.getAttribute(fa_audioDescription._AUDIO_DESCRIPTION_TYPE_ATTRIBUTE);
				if (ctype != type) {
					continue;
				}
			}

			ret.push(span);
		}

		return ret;
	},

	/**
	 * @method hidden static
	 * @param Element
	 *            element
	 * @param String
	 *            audioDescriptionText
	 * @param optional
	 *            String type
	 * @param optional
	 *            String forClientId
	 * @return Element
	 */
	AppendAudioDescription : function(element, audioDescriptionText, type,
			forClientId) {
		f_core.Assert(element && element.nodeType == f_core.ELEMENT_NODE,
				"fa_audioDescription.AppendAudioDescription: Invalid element paremeter ('"
						+ element + "')");
		f_core
				.Assert(
						typeof (audioDescriptionText) == "string",
						"fa_audioDescription.AppendAudioDescription: Invalid audioDescriptionText paremeter ('"
								+ audioDescriptionText + "')");

		var ret = f_core
				.CreateElement(
						element,
						fa_audioDescription._AUDIO_DESCRIPTION_ELEMENT,
						{
							classname : fa_audioDescription._AUDIO_DESCRIPTION_CLASS_NAME,
							textNode : audioDescriptionText
						});

		if (type) {
			ret
					.setAttribute(
							fa_audioDescription._AUDIO_DESCRIPTION_TYPE_ATTRIBUTE,
							type);
		}

		if (forClientId) {
			ret.setAttribute(
					fa_audioDescription._AUDIO_DESCRIPTION_FOR_ATTRIBUTE,
					forClientId);
		}

		return ret;
	},

	/**
	 * @method hidden static
	 * @param Element
	 *            element
	 * @param optional
	 *            String type
	 * @param optional
	 *            String forClientId
	 * @return Boolean
	 */
	RemoveAudioDescription : function(element, type, forClientId) {
		f_core.Assert(element && element.nodeType == f_core.ELEMENT_NODE,
				"fa_audioDescription.AppendAudioDescription: Invalid element paremeter ('"
						+ element + "')");

		var spans = fa_audioDescription.SetAudioDescription(element, type,
				forClientId);
		for ( var i = 0; i < spans.length; i++) {
			var span = spans[i];
			span.parentNode.removeChild(span);
		}

		return spans.length > 0;
	}
};

var __members = {

	/**
	 * @field private Object
	 */
	_audioDescriptionTexts : undefined,

	/**
	 * @field private Number
	 */
	_audioDescriptionCount : 0,

	/*
	 * f_finalize: function() { // this._audioDescriptionText=undefined; },
	 */

	/**
	 * @method protected
	 * @param optional
	 *            HTMLElement element
	 * @return Map<String,String>
	 */
	fa_searchAudioDescription : function(element) {
		var auds = this._audioDescriptionTexts;
		if (auds) {
			return auds;
		}
		auds = new Object();
		this._audioDescriptionTexts = auds;

		if (!element) {
			element = this;
		}

		var count = 0;

		var spans = fa_audioDescription.SearchAudioDescription(element, null,
				this.id);
		for ( var i = 0; i < spans.length; i++) {
			var span = spans[i];

			var type = span
					.getAttribute(fa_audioDescription._AUDIO_DESCRIPTION_TYPE_ATTRIBUTE);
			if (!type) {
				type = fa_audioDescription._DEFAULT_TYPE;
			}
			auds[type] = f_core.GetTextNode(span);
			count++;
		}

		this._audioDescriptionCount = count;

		return auds;
	},

	/**
	 * @method protected
	 * @param String
	 *            message
	 * @param optional
	 *            String type
	 * @param optional
	 *            HTMLElement element
	 * @return void
	 */
	fa_setAudioDescription : function(message, type, element) {

		if (!element) {
			element = this;
		}

		var auds = this.fa_searchAudioDescription(element);

		if (!type) {
			type = fa_audioDescription._DEFAULT_TYPE;
		}

		var audioDescriptionText = auds[type];
		if (audioDescriptionText === message) {
			return;
		}

		auds[type] = message;

		if (audioDescriptionText === undefined) {
			this._audioDescriptionCount++;
		}

		if (!element) {
			element = this;
		}

		fa_audioDescription
				.SetAudioDescription(element, message, type, this.id);
	},
	/**
	 * @method protected
	 * @param optional
	 *            String type
	 * @param optional
	 *            HTMLElement element
	 * @return void
	 */
	fa_removeAudioDescription : function(type, element) {
		var auds = this.fa_searchAudioDescription(element);

		if (!type) {
			type = fa_audioDescription._DEFAULT_TYPE;
		}

		var audioDescriptionText = auds[type];
		if (audioDescriptionText === undefined) {
			return;
		}

		delete auds[type];
		this._audioDescriptionCount--;

		if (!element) {
			element = this;
		}

		fa_audioDescription.RemoveAudioDescription(element, type, this.id);
	},
	/**
	 * @method protected
	 * @param optional
	 *            HTMLElement element
	 * @return void
	 */
	fa_updateAudioDescription : function(element) {
		if (!this._audioDescriptionCount) {
			return;
		}

		if (!element) {
			element = this;
		}

		var auds = this.fa_searchAudioDescription(element);

		for ( var type in auds) {
			var message = auds[type];

			fa_audioDescription.AppendAudioDescription(element, message, type,
					this.id);
		}
	}
};

new f_aspect("fa_audioDescription", {
	members : __members,
	statics : __statics
});
