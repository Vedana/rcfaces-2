/* 
 * $Id: f_frameSetAppender.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_frameSetAppender
 *
 * @class hidden f_frameSetAppender extends f_object, fa_abstractAppender
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics={
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {	

		var parentWindow=window.parent;
		for(;parentWindow.parent && parentWindow.parent!=parentWindow;) {
			parentWindow=parentWindow.parent;
		}
//		alert("ParentWindow="+parentWindow);
		if (!parentWindow || !parentWindow.rcfacesLogCB) {
			// On doit se debrouiller à trouver l'URL du stylesheet,
			// car le stylesheet est surement pas encore initialisé au niveau du f_env.
			// par contre il doit y avoir un <LINK rel="stylesheet"  avant !
			var uri=null;
			var links=document.getElementsByTagName("LINK");
			for(var i=0;i<links.length;i++) {
				var link=links[i];
				if (link.rel!="stylesheet") {
					continue;
				}
				var idx=link.href.indexOf("rcfaces.css");
				if (idx<0) {
					continue;
				}
				
				uri=link.href.substring(0, idx);
				break;
			}
			
			if (!uri || window.top!=window) {
				return;
			}
			
			uri+="frameSetAppender/base.html?url="+encodeURI(window.document.location);
			
			if (!parentWindow) {
				parentWindow=window;
			}
			
			//alert("Set location: "+parentWindow+" "+window+" "+uri);
			window.document.location=uri;
			return;
		}
		
	}
}

new f_class("f_frameSetAppender", {
	extend: f_object,
	aspects: [ fa_abstractAppender ],
	statics: __statics
});
