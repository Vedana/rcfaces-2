/*
 * $Id: f_flashObject.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * 
 * @class public f_flashObject extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {

	/**
	 * @field private static String
	 */
	_Version: undefined,

/*
	Finalizer: function() {
//		f_flashObject._Version=undefined; // String
	}
*/

	/**
	 * @method hidden static
	 * @return String V.W.X  (V=major, W=minor, X=Revision)
	 */
	GetVersion: function() {
		var version=f_flashObject._Version;
		if (version!==undefined) {
			return version;
		}
		
		var plugins=navigator.plugins;
		
		if (plugins) {
			if (plugins["Shockwave Flash 2.0"] || plugins["Shockwave Flash"]) {
				var swVer2 = plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
				var flashDescription = plugins["Shockwave Flash" + swVer2].description;			
				var descArray = flashDescription.split(" ");
				var tempArrayMajor = descArray[2].split(".");
				var versionMajor = tempArrayMajor[0];
				var versionMinor = tempArrayMajor[1];
				if (descArray[3] != "" ) {
					tempArrayMinor = descArray[3].split("r");
				} else {
					tempArrayMinor = descArray[4].split("r");
				}
				var versionRevision = tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;

				version=versionMajor + "." + versionMinor + "." + versionRevision;
				
				f_flashObject._Version=version;
				
				f_core.Debug(f_flashObject, "Flash version detected: "+version+" [plugin method]");
				return version;
			}
		}
		
		// Essaye les Active X ?
		
		try {
			// version will be set for 7.X or greater players
			var axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
			version = axo.GetVariable("$version");
				
		} catch (e) {
		}
	
		if (!version) {
			try {
				var axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");				
				version = "WIN 6,0,21,0";
	
				axo.AllowScriptAccess = "always";
	
				version = axo.GetVariable("$version");
	
			} catch (e) {
			}
		}
		
		if (!version) {
			try {
				// version will be set for 4.X or 5.X player
				//var axo = 
				new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
				version = axo.GetVariable("$version");
				
			} catch (e) {
			}
		}
		
		if (!version) {
			try {
				// version will be set for 3.X player
				//var axo = 
				new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
				
				version = "WIN 3,0,18,0";
			} catch (e) {
			}
		}
			
		if (!version) {
			try {
				// version will be set for 2.X player
				//var axo = 
				new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
				
				version = "WIN 2,0,0,11";
	
			} catch (e) {
			}
		}
		
		if (!version) {
			f_flashObject._Version=null;
				
			f_core.Debug(f_flashObject, "Flash version not detected ! [activeX method]");
			return null;
		}
		
		var formattedVersion=f_core.Trim(version).replace(/\,/g, ".");
		
		if (formattedVersion.charAt(0)>'9') {
			// Le premier token est le nom de l'OS
			
			formattedVersion=formattedVersion.split(" ")[1];
		}
				
		f_core.Debug(f_flashObject, "Flash version detected: '"+formattedVersion+"' (original='"+version+"') [activeX method]");
		f_flashObject._Version=formattedVersion;				
		return formattedVersion;
	},
	
	/**
	 * @method hidden static
	 * @param optional Number major
	 * @param optional Number minor
	 * @param optional Number revision
	 * @return Boolean 
	 */
	RequiresVersion: function(major, minor, revision) {
		var version=f_flashObject.GetVersion();
		if (!version) {
			return false;
		}
		
		if (!arguments.length) {
			return true;
		}
		
		var vs=version.split(".");
		
		for(var i=0;i<arguments.length;i++) {		
			var v=0;
			if (i<vs.length) {
				v=parseInt(vs[i], 10);
			}
		
			var rv=arguments[i];
			
			if (v<rv) {
				return false;
				
			} else if (v>rv) {
				return true;
			}
		}
		
		return true;		
	}
}

new f_class("f_flashObject", {
	statics: __statics
});


