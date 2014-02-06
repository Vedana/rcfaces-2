/*
 * $Id: fa_clientValidatorParameters.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect ClientValidatorParameters
 *
 * @aspect fa_clientValidatorParameters
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {


/*	
	f_finalize: function() {
		// this._clientValidatorParameters=undefined; // Map<String,String>
	},
	*/
		
	/**
	 * @method protected
	 * @param String key
	 * @return String
	 */
	 f_getClientValidatorParameter: function(key) {
	 	var clientValidatorParameters=this._clientValidatorParameters;

	 	if (clientValidatorParameters===undefined) {
	 		clientValidatorParameters=null;
			var params=f_core.GetAttributeNS(this, "clientValidator", null);
			if (params) { // Il peut être "" !
				clientValidatorParameters=f_core.ParseParameters(params);
			}
			
			this._clientValidatorParameters=clientValidatorParameters;
	 	}
	 	
	 	if (!clientValidatorParameters) {
	 		return null;
	 	}
	 	
	 	return clientValidatorParameters[key];
	 }
};

new f_aspect("fa_clientValidatorParameters", {
	members: __members
});
