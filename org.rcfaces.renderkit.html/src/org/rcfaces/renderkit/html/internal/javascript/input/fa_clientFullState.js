/*
 * $Id: fa_clientFullState.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * ClientFullState constants
 *
 * @aspect fa_clientFullState
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __statics = {

	/**
	 * @field public static final Number
	 */
    NONE_CLIENT_FULL_STATE: 0,

	/**
	 * @field public static final Number
	 */
    ONEWAY_CLIENT_FULL_STATE: 1,

 	/**
	 * @field public static final Number
	 */
    TWOWAYS_CLIENT_FULL_STATE: 2
};

new f_aspect("fa_clientFullState", __statics);
