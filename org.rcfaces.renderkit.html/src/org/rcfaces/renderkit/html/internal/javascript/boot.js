/*
 * $Id: boot.js,v 1.4 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $ 
 */
 
if (window.f_core) {
	var m="PANIC: Vedana Faces Library is already loaded !";
	alert(m);
	throw new Error(m);
} 

// For profiling ....
if (!window._rcfacesInitLibraryDate) {
	window._rcfacesInitLibraryDate=new Date();
}

var __SYMBOL=function(x) { return x; };

if (window._RCFACES_LEVEL3) {
	/**
	 * @dontReplaceInstanceOf
	 */
	var _rcfacesIO=function(obj, className, win) {
		var c1=window[className];
		if (obj instanceof c1) {
			return true;
		}

		c1=win[className];
		if (obj instanceof c1) {
			return true;
		}

		return false;
	};

	var _rcfacesGW=function(thiz, evt) {
		var win=undefined;
		if (evt) {
			if (evt.view) { // Firefox
				win=evt.view;

			} else if (evt.target) { // Firefox
				if (evt.target.nodeType==9) {					
					win=evt.target.defaultView;
					
				} else {
					win=evt.target.ownerDocument.defaultView;
				}

			} else if (evt.fromElement && evt.fromElement.ownerDocument) { // IE
				win=evt.fromElement.ownerDocument.parentWindow;

			} else if (evt.toElement && evt.toElement.ownerDocument) { // IE
				win=evt.toElement.ownerDocument.parentWindow;

			} else if (evt.srcElement && evt.srcElement.ownerDocument) { // IE
				win=evt.srcElement.ownerDocument.parentWindow;
			}
		}
		
		if (!win) {
			 if (thiz._window) {
				win=thiz._window;
					
			} else if (thiz.parentWindow) {
				// IE !
				win=thiz.parentWindow;
				
			} else if (thiz.defaultView) {
				win=thiz.defaultView;
				
			} else if (thiz.ownerDocument) {
				win=thiz.ownerDocument.defaultView; // Firefox
				if (!win) {
					win=thiz.ownerDocument.parentWindow; // IE
				}
	
			} else if (thiz.frames) {
				win=thiz;
			}
		}
		
		if (!win) {
			throw new Error("RCFaces: Unknown 'this' object type ! (thiz="+thiz+" evt="+evt+" this="+this+")");
		}
			
		for(var w=win;w;w=w.opener) {
			try {
				if (w._rcfacesClassLoader) {
					return w;
				}
			} catch(x) {
			}			
		}
		
		for(var w=win.parent;w;w=w.parent) {
			try {
				if (w._rcfacesClassLoader) {
					return w;
				}
			} catch(x) {
			}			
			if (w.parent==w) {
				break;
			}
		}
		
		try {
			var w=win.top;

			if (w._rcfacesClassLoader) {
				return w;
			}
			
		} catch(x) {		
		}		
		
		throw new Error("RCFaces: Can not identify the rcfaces window !");
	};
}	
