/*
 * $Id: fa_cardinality.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect Cardinality
 *
 * @aspect fa_cardinality
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __statics = {
	
	/**
	 * @field public static final Number
	 */
	OPTIONAL_CARDINALITY: 1,

	/**
	 * @field public static final Number
	 */
	ZEROMANY_CARDINALITY: 2,

	/**
	 * @field public static final Number
	 */
	ONE_CARDINALITY: 3,

	/**
	 * @field public static final Number
	 */
	ONEMANY_CARDINALITY: 4
};

new f_aspect("fa_cardinality", __statics);
