/*
 * $Id: fa_images.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Images
 *
 * @aspect public abstract fa_images
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {
/*
	f_finalize:  function() {
		this._imageURL=undefined; // string
		this._selectedImageURL=undefined; // string
		this._hoverImageURL=undefined; // string 
		this._disabledImageURL=undefined; // string
	},
	*/
	/**
	 * @method protected
	 * @return void
	 */
	fa_parseImageURLs: function() {
		if (this._imageURL!==undefined) {
			return;
		}
		
		var imageURL=this.fa_getInitialImageURL();
		
		var v_imageURL=f_core.GetAttributeNS(this, "imageURL");
		if (v_imageURL) {
			if (this.fa_getInitialDisabled && this.fa_getInitialDisabled()) {
				this._disabledImageURL=imageURL;
				
			} else if (this.fa_getInitialSelection && this.fa_getInitialSelection()) {
				this._selectedImageURL=imageURL;
			}

			imageURL=v_imageURL;
			f_imageRepository.PrepareImage(imageURL);
			
		} else if (this.fa_getInitialDisabled && this.fa_getInitialDisabled()) {
			// Il n'y a que le disabled qui est spécifié !
			this._disabledImageURL=imageURL;
//			imageURL=null;
			
		} else if (this.fa_getInitialSelection && this.fa_getInitialSelection()) {
			// Il n'y a que le selected qui est spécifié !
			this._selectedImageURL=imageURL;
//			imageURL=null;
		}
		
		if (imageURL===undefined) {
			imageURL=null;		
		}
		this._imageURL=imageURL;
		
		if (!this._disabledImageURL) {
			var v_disabledImageURL=f_core.GetAttributeNS(this, "disabledImageURL");
			if (v_disabledImageURL) {
				this._disabledImageURL=v_disabledImageURL;
				f_imageRepository.PrepareImage(v_disabledImageURL);
			}
		}
		
		if (!this._selectedImageURL) {
			var v_selectedImageURL=f_core.GetAttributeNS(this, "selectedImageURL");
			if (v_selectedImageURL) {
				this._selectedImageURL=v_selectedImageURL;
				f_imageRepository.PrepareImage(v_selectedImageURL);
			}
		}	

		var v_hoverImageURL=f_core.GetAttributeNS(this, "hoverImageURL");
		if (v_hoverImageURL) {
			this._hoverImageURL=v_hoverImageURL;
			f_imageRepository.PrepareImage(v_hoverImageURL);
		}
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getImageURL: function() {
		this.fa_parseImageURLs();

		return this._imageURL;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getHoverImageURL: function() { 
		this.fa_parseImageURLs();
		
		return this._hoverImageURL; 
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getSelectedImageURL: function() { 
		this.fa_parseImageURLs();
		
		return this._selectedImageURL; 
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getDisabledImageURL: function() { 
		this.fa_parseImageURLs();
		
		return this._disabledImageURL; 
	},
	/**
	 * @method public
	 * @param String url
	 * @return Boolean
	 */
	f_setImageURL: function(url) {
		if (this.f_getImageURL()==url) {
			return false;
		}

		this._imageURL=url;
		f_imageRepository.PrepareImage(url);
		
		if (!this.fa_componentUpdated) {
			return true;
		}

		var prop=f_prop.IMAGE_URL;
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(prop, url);

		this.fa_updateImages(prop, url);
		
		return true;
	},
	/**
	 * @method public
	 * @param String url
	 * @return Boolean
	 */
	f_setSelectedImageURL: function(url) {
		if (this.f_getSelectedImageURL()==url) {
			return false;
		}

		this._selectedImageURL=url;
		f_imageRepository.PrepareImage(url);
		
		if (!this.fa_componentUpdated) {
			return true;
		}

		var prop=f_prop.SELECTED_IMAGE_URL;
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(prop, url);
		
		this.fa_updateImages(prop, url);
		
		return true;
	},
	/**
	 * @method public
	 * @param String url
	 * @return Boolean
	 */
	f_setDisabledImageURL: function(url) {
		if (this.f_getDisabledImageURL()==url) {
			return false;
		}

		this._disabledImageURL=url;
		f_imageRepository.PrepareImage(url);
		
		if (!this.fa_componentUpdated) {
			return true;
		}

		var prop=f_prop.DISABLED_IMAGE_URL;
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(prop, url);

		this.fa_updateImages(prop, url);
		
		return true;
	},
	/**
	 * @method public
	 * @param String url
	 * @return Boolean
	 */
	f_setHoverImageURL: function(url) {
		if (this.f_getHoverImageURL()==url) {
			return false;
		}

		this._hoverImageURL=url;
		f_imageRepository.PrepareImage(url);
		
		if (!this.fa_componentUpdated) {
			return true;
		}

		var prop=f_prop.HOVER_IMAGE_URL;
		// On le met avant l'update, car des fois que la valeur rechange ...
		this.f_setProperty(prop, url);
		
		this.fa_updateImages(prop, url);
		
		return true;
	},

	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_hasOverImageURL: function() {
		if (this._imageURL!==undefined) {
			return this._hoverImageURL!==undefined;
		}
				
		return f_core.GetAttributeNS(this, "hoverImageURL")!=null;
	},
	
	/**
	 * @method protected abstract
	 * @return void
	 */

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateImages: f_class.ABSTRACT,
		
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_getInitialImageURL: f_class.ABSTRACT
}

new f_aspect("fa_images", {
	members: __members
});	
