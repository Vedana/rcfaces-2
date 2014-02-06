/*
 * $Id: fa_overStyleClass.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * fa_overStyleClass aspect.
 *
 * @class public fa_overStyleClass
 */
 
var __members = {

	/**
	 * @field private Boolean
	 */
	_over: undefined,
	
	/**
	 * @field private String
	 */
	_overStyleClass: undefined,

	fa_overStyleClass: function() {
		var overStyleClass=f_core.GetAttributeNS(this, "overStyleClass", null);
		if (overStyleClass!==null) {
			this._installOverStyleClass(overStyleClass);
		}
	},
/*
	f_finalize: function() {
		this._over=undefined; // String
		this._overStyleClass=undefined; // String
	},
	*/
	/**
	 * @method private
	 * @param String overStyleClass
	 * @return void
	 */
	_installOverStyleClass: function(overStyleClass) {
		var box=this;
		
		this._overStyleClass=overStyleClass;
	
		this.f_addEventListener(f_event.MOUSEOVER, function() {
			box._over=true;
			
			box.f_updateStyleClass();
			
			return true;
		});
	
		this.f_addEventListener(f_event.MOUSEOUT, function() {
			box._over=false;
			
			box.f_updateStyleClass();
			
			return true;
		});
	},
	/** 
	 * @method hidden
	 * @return Boolean
	 */
	f_isMouseOver: function() {
		return this._over;
	},
	/** 
	 * @method public
	 * @return String
	 */
	f_getOverStyleClass: function() {
		return this._overStyleClass;
	},
	
	f_computeStyleClass: f_class.ABSTRACT,
	
	f_updateStyleClass: f_class.ABSTRACT
}
 
new f_aspect("fa_overStyleClass", {
	members: __members
});

