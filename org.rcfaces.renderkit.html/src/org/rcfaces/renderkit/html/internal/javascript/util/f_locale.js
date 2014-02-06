/*
 * $Id: f_locale.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * @class public final f_locale extends Object
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */


var __statics = {

	/**
	 * Short form.
	 *
	 * @field public static final Number
	 */
	SHORT: 0,

	/**
	 * Medium form.
	 *
	 * @field public static final Number
	 */
	MEDIUM: 1,

	/**
	 * Long form.
	 *
	 * @field public static final Number
	 */
	LONG: 2,

	/**
	 * @field private static f_locale
	 */
	_Instance: undefined,
	 
	/**
	 * @method public static final
	 * @return f_locale
	 */
	Get: function() {
		if (!f_locale._Instance) {
			f_locale._Instance=new f_locale();
		}
		
		return f_locale._Instance;
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_locale._Instance=undefined; // f_locale
	},
	/**
	 * @method private static final
	 * @param Number idx
	 * @return String
	 */
	_GetLocaleNamePart: function(idx) {
		var seps=f_env.GetLocaleName().split("_");
		if (seps.length<=idx) {
			return null;
		}
		
		return seps[idx];
	}
}

var __members = {
	
	/**
	 * @field private f_resourceBundle
	 */
	 _resourceBundle: undefined,
	
	/**
	 * @method hidden
	 */
	f_locale: function() {
		var resourceBundle=f_resourceBundle.Get(f_locale);
		this._resourceBundle=resourceBundle;
	
		this._monthShortNames=resourceBundle.f_get("MONTH_SHORT_NAMES");
		this._monthMedNames=resourceBundle.f_get("MONTH_MED_NAMES");
		this._monthLongNames=resourceBundle.f_get("MONTH_LONG_NAMES");
	
		this._dayShortNames=resourceBundle.f_get("DAY_SHORT_NAMES");
		this._dayMedNames=resourceBundle.f_get("DAY_MED_NAMES");
		this._dayLongNames=resourceBundle.f_get("DAY_LONG_NAMES");
		
		this._firstDayOfWeek=resourceBundle.f_get("FIRST_DAY_OF_WEEK");
		var twoDigitYearStart=resourceBundle.f_get("TWO_DIGIT_YEAR_START");
		if (twoDigitYearStart) {
			this._twoDigitYearStart=f_core.DeserializeDate(twoDigitYearStart);
		}
	
		this._dateFormats=resourceBundle.f_get("DATE_FORMATS");
	},
	
	f_finalize: function() {
		this._resourceBundle=undefined; // f_resourceBundle;
		
//		this._monthShortNames=undefined; // string[]
//		this._monthMedNames=undefined; // string[]
//		this._monthLongNames=undefined; // string[]
	
//		this._dayShortNames=undefined; // string[]
//		this._dayMedNames=undefined; // string[]
//		this._dayLongNames=undefined; // string[]
	
//		this._firstDayOfWeek=undefined; // number
	
//		this._dateFormats=undefined; // string[]
	
		// this.f_super(arguments); // C'est un OBJECT !
	},
	/**
	 * @method public 
	 * @param Number n Month number (0 to 11)
	 * @param Number form  Form of name (SHORT, MEDIUM, LONG)
	 * @return String
	 * @see #LONG
	 * @see #SHORT
	 * @see #MEDIUM
	 */
	f_getMonthName: function(n, form) {
		f_core.Assert(typeof(n)=="number" && n>=0 && n<=11, "f_locale.f_getMonthName: Invalid month number parameter ("+n+")");
		f_core.Assert(typeof(form)=="number" && (form==f_locale.SHORT || form==f_locale.MEDIUM || form==f_locale.LONG), "f_locale.f_getMonthName: Invalid form parameter ("+form+")");
	
		var a=this._monthMedNames;
	
		switch(form) {
		case f_locale.LONG:
			a=this._monthLongNames;
			break;
			
		case f_locale.SHORT:
			a=this._monthShortNames;
			break;
		}
		
		if (n<0 || n>=a.length) {
			return null;
		}
		
		return a[n];
	},
	/**
	 * @method public 
	 * @param Number n Day number (0 to 6)
	 * @param Number form  Form of name (SHORT, MEDIUM, LONG)
	 * @return String
	 * @see #LONG
	 * @see #SHORT
	 * @see #MEDIUM
	 */
	f_getDayName: function(n, form) {
		f_core.Assert(typeof(n)=="number" && n>=0 && n<=6, "f_locale.f_getDayName: Invalid day number parameter ("+n+")");
		f_core.Assert(typeof(form)=="number" && (form==f_locale.SHORT || form==f_locale.MEDIUM || form==f_locale.LONG), "f_locale.f_getDayName: Invalid form parameter ("+form+")");

		var a=this._dayMedNames;
	
		switch(form) {
		case f_locale.LONG:
			a=this._dayLongNames;
			break;
			
		case f_locale.SHORT:
			a=this._dayShortNames;
			break;
		}
		
		if (n<0 || n>=a.length) {
			return null;
		}
		
		f_core.Assert(typeof(a[n])=="string", "f_locale.f_getDayName: Invalid day name for index "+n+" form="+form);
		
		return a[n];
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getFirstDayOfWeek: function() {
		f_core.Assert(typeof(this._firstDayOfWeek)=="number", "f_locale.f_getFirstDayOfWeek: Invalid first day of week ! ("+this._firstDayOfWeek+")")
		return this._firstDayOfWeek;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getTwoDigitYearStart: function() {
		f_core.Assert(this._twoDigitYearStart instanceof Date, "f_locale.f_getTwoDigitYearStart: Invalid two digit year start ! ("+this._twoDigitYearStart+")")
		return this._twoDigitYearStart;
	},
	/**
	 * @method public
	 * @param optional Number form Form of date (SHORT, MEDIUM, LONG)
	 * @return String
	 * @see #LONG
	 * @see #SHORT
	 * @see #MEDIUM
	 */
	f_getDateFormat: function(form) {
		f_core.Assert(form===undefined || (typeof(form)=="number" && (form==f_locale.SHORT || form==f_locale.MEDIUM || form==f_locale.LONG)), "f_locale.f_getDateFormat: Invalid form parameter ("+form+")");

		var ds=this._dateFormats;
	
		switch(form) {
		case f_locale.LONG:
			return ds[2];
			
		case f_locale.MEDIUM:
			return ds[1];
		}
		
		return ds[0];
	},
	/**
     * Getter for the programmatic name of the entire locale,
     * with the language, country and variant separated by underbars.
     * Language is always lower case, and country is always upper case.
     * 
     * @method public
     * @return String
     */
	f_getName: function() {
		return f_env.GetLocaleName();
	},
  	/**
     * Returns the language code for this locale, which will either be the empty string
     * or a lowercase ISO 639 code.
     * 
     * @method public
     * @return String
     */
	f_getLanguage: function() {
		return f_locale._GetLocaleNamePart(0);
	},
 	/**
     * Returns the country/region code for this locale, which will either be the empty string
     * or an upercase ISO 3166 2-letter code.
     * 
     * @method public
     * @return String
     */
 	f_getCountry: function() {
		return f_locale._GetLocaleNamePart(1);
	},
	/**
     * Returns the variant code for this locale.
     * 
     * @method public
     * @return String
     */
	f_getVariant: function() {
		return f_locale._GetLocaleNamePart(2);
	},
	/**
	 * Returns a JSF messages associated to a key.
	 * 
	 * @method public
	 * @param String key
	 * @param optional String defaultValue
	 * @return String
	 * @see f_resourceBundle#f_get
	 */
	f_getMessage: function(key, defaultValue) {
		return f_resourceBundle.Get(f_locale).f_get(key, defaultValue);
	},
	/**
	 * Format a JSF messages associated to a key.
	 * 
	 * @method public
	 * @param String key
	 * @param optional any... params
	 * @return String
	 * @see f_resourceBundle#f_format
	 */
	f_formatMessage: function(key, params) {
		return f_resourceBundle.Get(f_locale).f_format(key, params);
	},
	/**
	 * Format a JSF messages associated to a key.
	 * 
	 * @method public
	 * @param String key
	 * @param optional any[] params
	 * @param optional String defaultValue
	 * @return String
	 * @see f_resourceBundle#f_formatParams
	 */
	f_formatMessageParams: function(key, params, defaultValue) {
		return f_resourceBundle.Get(f_locale).f_formatParams(key, params, defaultValue);
	}
};

new f_class("f_locale", {
	statics: __statics,
	members: __members
});


f_resourceBundle.Define2("f_locale", {
	MONTH_SHORT_NAMES: [ $$$MONTH_SHORT_NAMES$$$ ],
	MONTH_MED_NAMES: [ $$$MONTH_MED_NAMES$$$ ],
	MONTH_LONG_NAMES: [ $$$MONTH_LONG_NAMES$$$ ],

	DAY_SHORT_NAMES: [ $$$DAY_SHORT_NAMES$$$ ], 
	DAY_MED_NAMES: [ $$$DAY_MED_NAMES$$$ ], 
	DAY_LONG_NAMES: [ $$$DAY_LONG_NAMES$$$ ],
	
	FIRST_DAY_OF_WEEK: $$$FIRST_DAY_OF_WEEK$$$,
	
	DATE_FORMATS: [ $$$DATE_FORMATS$$$ ],
	
	TWO_DIGIT_YEAR_START: $$$TWO_DIGIT_YEAR_START$$$,
	
	javax_faces_component_UIInput_CONVERSION: "$$$javax.faces.component.UIInput.CONVERSION$$$",
	javax_faces_component_UIInput_REQUIRED: "$$$javax.faces.component.UIInput.REQUIRED$$$",
	javax_faces_component_UISelectOne_INVALID: "$$$javax.faces.component.UISelectOne.INVALID$$$",
	javax_faces_component_UISelectMany_INVALID: "$$$javax.faces.component.UISelectMany.INVALID$$$",
	javax_faces_validator_NOT_IN_RANGE: "$$$javax.faces.validator.NOT_IN_RANGE$$$",
	javax_faces_validator_DoubleRangeValidator_MAXIMUM: "$$$javax.faces.validator.DoubleRangeValidator.MAXIMUM$$$",
	javax_faces_validator_DoubleRangeValidator_MINIMUM: "$$$javax.faces.validator.DoubleRangeValidator.MINIMUM$$$",
	javax_faces_validator_DoubleRangeValidator_TYPE: "$$$javax.faces.validator.DoubleRangeValidator.TYPE$$$",
	javax_faces_validator_LengthValidator_MAXIMUM: "$$$javax.faces.validator.LengthValidator.MAXIMUM$$$",
	javax_faces_validator_LengthValidator_MINIMUM: "$$$javax.faces.validator.LengthValidator.MINIMUM$$$",
	javax_faces_validator_LongRangeValidator_MAXIMUM: "$$$javax.faces.validator.LongRangeValidator.MAXIMUM$$$",
	javax_faces_validator_LongRangeValidator_MINIMUM: "$$$javax.faces.validator.LongRangeValidator.MINIMUM$$$",
	javax_faces_validator_LongRangeValidator_TYPE: "$$$javax.faces.validator.LongRangeValidator.TYPE$$$"
	
});
