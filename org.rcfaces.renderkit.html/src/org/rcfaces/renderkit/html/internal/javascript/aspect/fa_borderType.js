/*
 * $Id: fa_borderType.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect BorderType
 *
 * @aspect abstract fa_borderType
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __statics = {
	
	/** 
	 * @field private static final String 
	 */
	_BORDER_ID_SUFFIX: "::border",
	
	/** 
	 * @field hidden static final String 
	 */
	NONE_BORDER_TYPE: "none",

	/** 
	 * @field private static fa_borderType
	 */
	_LastFlatBorder: undefined,

	/**
	 * @method hidden static
	 */
	GetCurrentBorder: function() {
		return fa_borderType._LastFlatBorder;
	},

	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		fa_borderType._LastFlatBorder=undefined; // fa_borderType
	}
}

var __members = {

	f_finalize: function() {
		// this._borderType=undefined; // string
		// this._flatType=undefined; // string
		
		var border=this._border;
		if (border) {
			this._border=undefined;

			// border._className=undefined; // string
		
			f_core.VerifyProperties(border);
		}
	},
	/**
	 * Returns the border type name.
	 *
	 * @method public
	 * @return String
	 */
	f_getBorderType: function() {
		var borderType=this._borderType;

		if (borderType!==undefined) {
			return borderType;
		}
		
		// Appel depuis le constructor de l'objet !
		
		var v_borderType=f_core.GetAttributeNS(this, "borderType");
		if (v_borderType && v_borderType!=fa_borderType.NONE_BORDER_TYPE) {
			var border=this.ownerDocument.getElementById(this.id+fa_borderType._BORDER_ID_SUFFIX);
			borderType=v_borderType;
			
			if (border) {
	//			f_core.Assert(border, "Can not find border of component '"+this.id+"' (borderType='"+v_borderType+"').");
				this._border=border;
				
				var cl=f_core.GetAttributeNS(border, "className");
				if (cl) {
					border._className=cl;
	
				} else {
					border._className=border.className;
				}
				
				this._flatType=f_core.GetBooleanAttributeNS(this, "flatMode", false);
			}
		} else {
			borderType=null;
		}
	
		this._borderType=borderType;
		
		return borderType;
	},
	/**
	 *
	 * @method protected
	 * @return HTMLElement
	 */
	f_getBorderComponent: function() {
		if (!this.f_getBorderType()) {
			return null;
		}
		
		return this._border;
	},
	/**
	 * @method protected
	 */	
	f_isFlatTypeBorder: function() {
		if (!this.f_getBorderType()) {
			return;
		}
		
		return this._flatType;
	},
	/**
	 * @method protected
	 */
	f_updateLastFlatBorder: function() {
		var lastFlat=fa_borderType._LastFlatBorder;
		if (lastFlat && f_classLoader.IsObjectInitialized(lastFlat)) {
			if (lastFlat==this) {
				return;
			}
			
			fa_borderType._LastFlatBorder=undefined;

			//alert("Update last !");
			// Il s'est peut être passé un garbage ou qq chose du genre :
			// Soyons prudents !!!
			if (typeof(lastFlat._updateImage)=="function") {
				lastFlat._updateImage();
			}
		}
		
		if (!this.f_isFlatTypeBorder()) {
			return;
		}
		
		fa_borderType._LastFlatBorder=this;
	}
}

new f_aspect("fa_borderType", {
	statics: __statics,
	members: __members
});
	
