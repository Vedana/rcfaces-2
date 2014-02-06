/*
 * $Id: f_imageRepository.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */


/**
 * This class provides a method to load any images if not loaded yet.
 *
 * @class public final f_imageRepository extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics = {

	/**
	 * @field private static final Number
	 */
	 _IMAGE_OBJECT_MAX_NUMBER: 2,

	/**
	 * @field private static final Number
	 */
	 _ASYNC_IMAGE_OBJECT_TIMER: 100,	

	/**
	 * @field private static Map<String, Object>
	 */
	 _Images: undefined,

	/**
	 * @field private static Image[]
	 */
	 _ImagesObjectPool: undefined,

	/**
	 * @field private static Number
	 */
	 _ImagesObjectCount: 0,

	/**
	 * @field private static String[]
	 */
	 _ImagesWaiting: undefined,
	 

	/**
	 * Prepare an image. (Preload it if necessary)
	 *
	 * @method public static final
	 * @param String url
	 * @return void
	 */
	PrepareImage: function(url) {
		f_core.Assert(url, "f_imageRepository.PrepareImage: URL must be not NULL !");
		
		if (window._rcfacesPrepareImages!==true) {
			return;
		}
		
		var images=f_imageRepository._Images;
		if (!images) {
			images=new Object;
			f_imageRepository._Images=images;
			f_imageRepository._ImagesObjectPool=new Array;
		}

		if (images[url]!==undefined) {
			return;
		}

		var imageObject;
		
		var pool=f_imageRepository._ImagesObjectPool;
		if (pool.length) {
			imageObject=pool.pop();

			f_core.Debug(f_imageRepository, "PrepareImage: Load image '"+url+"'. (Use popped image object '"+imageObject.id+"')");
			
		} else if (f_imageRepository._IMAGE_OBJECT_MAX_NUMBER<0 || 
				f_imageRepository._ImagesObjectCount<f_imageRepository._IMAGE_OBJECT_MAX_NUMBER) {
	
			imageObject=new Image();
			imageObject.id="ImageObject #"+f_imageRepository._ImagesObjectCount;
			imageObject._window=window;
			imageObject.onerror=f_imageRepository._OnErrorHandler;
			imageObject.onload=f_imageRepository._OnLoadHandler;

			f_imageRepository._ImagesObjectCount++;

			f_core.Debug(f_imageRepository, "PrepareImage: Load image '"+url+"'. (Create an image object '"+imageObject.id+"')");

		} else {
			// On la met en attente ...
			
			var waiting=f_imageRepository._ImagesWaiting;
			if (!waiting) {
				waiting=new Array;
				f_imageRepository._ImagesWaiting=waiting;
			}
			
			waiting.push(url);
			
			f_core.Debug(f_imageRepository, "PrepareImage: Load image '"+url+"'. (Queue URL)");			
			return;
		}
			
		images[url]=imageObject;
		
		if (f_imageRepository._ASYNC_IMAGE_OBJECT_TIMER>0) {
		
			var _imageObject=imageObject;
			var _url=url;
			window.setTimeout(function() {
				f_core.Debug(f_imageRepository, "PrepareImage: Async setting of url '"+_url+"' to image object '"+_imageObject.id+"'.");
	
				_imageObject.src=_url;		
				_imageObject=null;
				_url=null;
			}, f_imageRepository._ASYNC_IMAGE_OBJECT_TIMER);
			
		} else {
			f_core.Debug(f_imageRepository, "PrepareImage: Sync setting of url '"+url+"' to image object '"+imageObject.id+"'.");

			imageObject.src=url;		
		}
	},
	/**
	 * @method private static
	 * @return void
	 * @context window:win
	 */
	_OnErrorHandler: function() {
		var win=this._window;

		if (win._rcfacesExiting) {
			f_imageRepository._FinalizeImage(this);
			return;
		}

		f_core.Error(f_imageRepository, "_OnErrorHandler: Error while loading image '"+this.src+"'.");

//		this.onload=null;
//		this.onerror=null;

		f_imageRepository._NextImage(this, false);
	},
	/**
	 * @method private static
	 * @return void
	 * @context window:win
	 */
	_OnLoadHandler: function() {
		var win=this._window;
		if (win._rcfacesExiting) {
			f_imageRepository._FinalizeImage(this);
			return;
		}

		f_core.Debug(f_imageRepository, "_OnLoadHandler: Image '"+this.src+"' loaded.");

//		this.onload=null;
//		this.onerror=null;

		f_imageRepository._NextImage(this, true);
	},	
	/**
	 * @method private static
	 * @param Image imageObject
	 * @param Boolean status of image loading
	 * @return void
	 */
	_NextImage: function(imageObject, status) {
		var src=imageObject.src;

		var images=f_imageRepository._Images;
		if (!images) {
			return; // Nous sommes en cours de desinstallation !
		}
	
		images[src]=status;
		
		var waiting=f_imageRepository._ImagesWaiting;
		if (waiting && waiting.length) {
			var url=waiting.unshift();
			f_core.Debug(f_imageRepository, "_NextImage: Set url '"+url+"' to image object '"+imageObject.id+"'.");

			this.src=url
			return;
		}

		f_core.Debug(f_imageRepository, "_NextImage: Push image object '"+imageObject.id+"' into pool.");
		f_imageRepository._ImagesObjectPool.push(imageObject);
	},
	/**
	 * @method private static
	 * @param Image image
	 * @return void
	 */
	_FinalizeImage: function(image) {
//		image.id=undefined;
		image.onload=null;
		image.onerror=null;
		image._window=undefined; // Window
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		var images=f_imageRepository._Images;
		if (images) {
			f_imageRepository._Images=undefined;
			
			for(var url in images) {
				var image=images[url];
				if (!image._window) {
					continue;
				}
			
				f_imageRepository._FinalizeImage(image);	
			}
		}
				
		var imagesPool=f_imageRepository._ImagesPool;
		if (imagesPool) {
			f_imageRepository._ImagesPool=undefined;
		
			for(var i=0;i<imagesPool.length;i++) {
				var image=imagesPool[i];
			
				f_imageRepository._FinalizeImage(image);	
			}
		}

		f_imageRepository._ImagesWaiting=undefined;
	}
}

new f_class("f_imageRepository", {
	statics: __statics
});
