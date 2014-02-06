/*
 * $Id: f_textSelection.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_textSelection class
 *
 * @class public final f_textSelection extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) & Joel Merlin
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */

var __members = {
	/**
	 * @method public
	 * @param Number start
	 * @param Number length
	 * @param hidden String text
	 */
	f_textSelection: function(start, length, text) {
		this._start=start;
		this._length=length;
		this._text=text;	
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getStart: function() {
		return this._start;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getLength: function() {
		return this._length;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return this._text;
	}
}

new f_class("f_textSelection", {
	members: __members
});

